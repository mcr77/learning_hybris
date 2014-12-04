/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.sap.core.jco.service.impl;

import de.hybris.platform.sap.core.common.util.GenericFactoryProvider;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationConstants;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationService;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationJCoTraceEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationPingEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationRemoveEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationUpdateEvent;
import de.hybris.platform.sap.core.constants.SapcoreConstants;
import de.hybris.platform.sap.core.jco.service.SAPRFCDestinationService;
import de.hybris.platform.sap.core.runtime.SAPHybrisSession;
import de.hybris.platform.sap.core.runtime.SAPHybrisSessionProvider;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.util.localization.Localization;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;


/**
 * Default implementation for the {@link SAPRFCDestinationService}.
 */
public class DefaultSAPRFCDestinationService extends AbstractEventListener<SAPRFCDestinationEvent> implements
		SAPRFCDestinationService
{

	private static final Logger log = Logger.getLogger(DefaultSAPRFCDestinationService.class);
	private static final String SNC_LIB = "SNC_LIB";

	private RFCDestinationService rfcDestinationService;
	private TenantService tenantService;

	private static DestinationDataProvider destinationDataProvider = null;
	private static AtomicInteger providerUsageCount = new AtomicInteger(0);
	private static DestinationDataEventListener destinationDataEventListener;

	private static String sncLibraryPath = null;
	private static String rfcDefaultLanguage = "en";

	/**
	 * Returns the RFC default language.
	 * 
	 * @return RFC default language
	 */
	public String getRfcDefaultLanguage()
	{
		return rfcDefaultLanguage;
	}

	/**
	 * Sets the RFC default language.
	 * 
	 * @param rfcDefaultLanguage
	 *           RFC default language
	 */
	@Required
	public void setRfcDefaultLanguage(final String rfcDefaultLanguage)
	{
		if (rfcDefaultLanguage != null && !rfcDefaultLanguage.isEmpty() && !rfcDefaultLanguage.startsWith("$"))
		{
			DefaultSAPRFCDestinationService.rfcDefaultLanguage = rfcDefaultLanguage;
		}
	}

	/**
	 * Returns the SNC library path.
	 * 
	 * @return SNC library path
	 */
	public String getSncLibraryPath()
	{
		if (this.sncLibraryPath == null || this.sncLibraryPath.isEmpty())
		{
			// try environment variable
			this.sncLibraryPath = System.getenv(SNC_LIB);
		}

		return sncLibraryPath;
	}

	/**
	 * Sets the SNC library path.
	 * 
	 * @param sncLibraryPath
	 *           SNC library path
	 */
	@SuppressWarnings("static-access")
	public void setSncLibraryPath(final String sncLibraryPath)
	{
		if (sncLibraryPath != null && !sncLibraryPath.isEmpty() && !sncLibraryPath.startsWith("$"))
		{
			this.sncLibraryPath = sncLibraryPath;
		}

	}

	/**
	 * Injection setter for {@link TenantService}.
	 * 
	 * @param tenantService
	 *           {@link TenantService} to set
	 */
	@Required
	@Override
	public void setTenantService(final TenantService tenantService)
	{
		super.setTenantService(tenantService);
		this.tenantService = tenantService;
	}

	/**
	 * Injection setter for {@link RFCDestinationService}.
	 * 
	 * @param rfcDestinationService
	 *           {@link RFCDestinationService}
	 * 
	 */
	@Required
	public void setRfcDestinationService(final RFCDestinationService rfcDestinationService)
	{
		this.rfcDestinationService = rfcDestinationService;
	}

	/**
	 * Initialization method called by the Spring framework.
	 */
	public void init()
	{
		if (!tenantService.getCurrentTenantId().equals("junit"))
		{
			registerDestinationDataProvider(this);
		}
	}

	/**
	 * Destroy method called by the Spring framework.
	 */
	public void destroy()
	{
		if (!tenantService.getCurrentTenantId().equals("junit"))
		{
			unregisterDestinationDataProvider();
		}
	}

	@Override
	public RFCDestination getRFCDestination(final String jcoDestinationName)
	{
		return rfcDestinationService.getRFCDestination(jcoDestinationName);
	}

	/**
	 * Unregisters the destination data provider.
	 */
	private static synchronized void unregisterDestinationDataProvider()
	{
		if (destinationDataProvider != null && providerUsageCount.decrementAndGet() <= 0)
		{
			try
			{
				Environment.unregisterDestinationDataProvider(destinationDataProvider);
				destinationDataProvider = null;
			}
			catch (final IllegalStateException e)
			{
				log.error("SAP JCoDestinationData exists in service, but is not registered", e);
			}
		}
	}

	/**
	 * Registers the destination data provider.
	 * 
	 * @param sapRFCDestinationService
	 *           {@link SAPRFCDestinationService}
	 */
	private static synchronized void registerDestinationDataProvider(final SAPRFCDestinationService sapRFCDestinationService) // NOPMD
	{
		if (destinationDataProvider != null)
		{
			log.debug("Provider exists in DefaultSAPRFCDestinationService");
			providerUsageCount.incrementAndGet();
		}
		else
		{
			if (Environment.isDestinationDataProviderRegistered())
			{
				log.warn("SAP JCoDestinationData provider already registered");
			}
			else
			{
				destinationDataProvider = new DestinationDataProvider()
				{
					@Override
					public boolean supportsEvents()
					{
						return true;
					}

					@Override
					public void setDestinationDataEventListener(final DestinationDataEventListener eventListener)
					{
						destinationDataEventListener = eventListener;
					}

					@Override
					public Properties getDestinationProperties(final String destinationName) // NOPMD
					{
						final RFCDestination rfcDestination = sapRFCDestinationService.getRFCDestination(destinationName);
						final Properties destinationProperties = new Properties();
						if (rfcDestination != null)
						{

							if (rfcDestination.getConnectionType() != null && rfcDestination.getConnectionType().booleanValue())
							{
								destinationProperties.setProperty(DestinationDataProvider.JCO_ASHOST, rfcDestination.getTargetHost());
								destinationProperties.setProperty(DestinationDataProvider.JCO_SYSNR, rfcDestination.getInstance());
							}
							else
							{
								destinationProperties.setProperty(DestinationDataProvider.JCO_MSHOST, rfcDestination.getMessageServer());
								destinationProperties.setProperty(DestinationDataProvider.JCO_GROUP, rfcDestination.getGroup());
								destinationProperties.setProperty(DestinationDataProvider.JCO_R3NAME, rfcDestination.getSid());
							}
							destinationProperties.setProperty(DestinationDataProvider.JCO_CLIENT, rfcDestination.getClient());
							destinationProperties.setProperty(DestinationDataProvider.JCO_USER, rfcDestination.getUserid());
							destinationProperties.setProperty(DestinationDataProvider.JCO_PASSWD, rfcDestination.getPassword());
							destinationProperties.setProperty(DestinationDataProvider.JCO_LANG, rfcDefaultLanguage);
							if (rfcDestination.getJcoMsServ() != null)
							{
								destinationProperties.setProperty(DestinationDataProvider.JCO_MSSERV, rfcDestination.getJcoMsServ());
							}
							if (rfcDestination.getJcoSAPRouter() != null)
							{
								destinationProperties
										.setProperty(DestinationDataProvider.JCO_SAPROUTER, rfcDestination.getJcoSAPRouter());
							}
							if (rfcDestination.getJcoClientDelta() != null)
							{
								if (rfcDestination.getJcoClientDelta().booleanValue())
								{
									destinationProperties.setProperty(DestinationDataProvider.JCO_DELTA, "1");
								}
								else
								{
									destinationProperties.setProperty(DestinationDataProvider.JCO_DELTA, "0");
								}
							}

							// !!! For RFC Destinations only the configured service user is used. !!!
							// !!! Thus the authentication type is by default 'Configured_user'   !!!
							destinationProperties.setProperty(DestinationDataProvider.JCO_AUTH_TYPE,
									DestinationDataProvider.JCO_AUTH_TYPE_CONFIGURED_USER);

							if (rfcDestination.getPooledConnectionMode() != null
									&& rfcDestination.getPooledConnectionMode().booleanValue())
							{
								destinationProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY,
										String.valueOf(rfcDestination.getPoolSize()));
								destinationProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,
										String.valueOf(rfcDestination.getMaxConnections()));
								destinationProperties.setProperty(DestinationDataProvider.JCO_MAX_GET_TIME,
										String.valueOf(rfcDestination.getMaxWaitTime()));
							}
							else
							{
								// A value of 0 has the effect that there is no connection pooling. !!
								destinationProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, String.valueOf(0));
							}
							if (rfcDestination.isJcoRFCTraceEnabled())
							{
								destinationProperties.setProperty(DestinationDataProvider.JCO_TRACE, "1");
							}
							else
							{
								destinationProperties.setProperty(DestinationDataProvider.JCO_TRACE, "0");
							}
							if (rfcDestination.getJcoCPICTrace() != null)
							{
								destinationProperties.setProperty(DestinationDataProvider.JCO_CPIC_TRACE,
										rfcDestination.getJcoCPICTrace());
							}
							if (rfcDestination.isSncEnabled())
							{
								destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_MODE, "1");
								destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_PARTNERNAME,
										rfcDestination.getSncPartnerName());
								destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_QOP, rfcDestination.getSncQOP());

								destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_LIBRARY, sncLibraryPath);
								destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_SSO, "0");

							}
						}
						return destinationProperties;
					}
				};
				Environment.registerDestinationDataProvider(destinationDataProvider);
				providerUsageCount.incrementAndGet();
			}
		}
	}

	@Override
	protected void onEvent(final SAPRFCDestinationEvent event)
	{
		if (event instanceof SAPRFCDestinationRemoveEvent)
		{
			destinationDataEventListener.deleted(event.getSource().toString());
		}
		else if (event instanceof SAPRFCDestinationUpdateEvent)
		{
			destinationDataEventListener.updated(event.getSource().toString());
		}
		else if (event instanceof SAPRFCDestinationPingEvent)
		{
			final Map<String, Object> actionResultMap = pingCurrentDestination(event.getSource().toString());
			((SAPRFCDestinationPingEvent) event).setMessage((String) actionResultMap.get("message"));
			((SAPRFCDestinationPingEvent) event).setResultIndicator((int) actionResultMap.get("result"));
			((SAPRFCDestinationPingEvent) event).setNeedRefresh((boolean) actionResultMap.get("needRefresh"));
			return;
		}
		else if (event instanceof SAPRFCDestinationJCoTraceEvent)
		{
			// Turn on / off global JCo Trace
			final int jcoTraceLevel = convertJCoTraceLevel(event.getSource().toString());
			if (jcoTraceLevel >= 0 && jcoTraceLevel < 11)
			{
				String path = null;
				if (((SAPRFCDestinationJCoTraceEvent) event).getJCoTracePath() != null
						&& !((SAPRFCDestinationJCoTraceEvent) event).getJCoTracePath().isEmpty())
				{
					path = ((SAPRFCDestinationJCoTraceEvent) event).getJCoTracePath();
				}
				JCo.setTrace(jcoTraceLevel, path);
			}
		}
	}

	/**
	 * Convert the DDLB value to a "integer".
	 * 
	 * 
	 * @param traceLevel
	 *           the tracelevel as a Constant
	 * @return the Tracelevel as a Integer
	 */
	private int convertJCoTraceLevel(final String traceLevel)
	{
		switch (traceLevel)
		{
			case RFCDestinationConstants.JCO_TRACE_LEVEL_NO_TRACE:
				return 0;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_ERRORS:
				return 1;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_ERRORS_WARNINGS:
				return 2;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_INFOS_ERRORS_WARNINGS:
				return 3;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_EXPATH_INFOS_ERRORS_WARNINGS:
				return 4;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_VERBEXPATH_INFOS_ERRORS_WARNINGS:
				return 5;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_VERBEXPATH_LIMDATADUMPS_INFOS_ERRORS_WARNINGS:
				return 6;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_FULLEXPATH_DATADUMPS_VERBINFOS_ERRORS_WARNINGS:
				return 7;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_FULLEXPATH_FULLDATADUMPS_VERBINFOS_ERRORS_WARNINGS:
				return 8;
			default:
				return 0;
		}
	}

	/**
	 * Pings the requested RFC destination name.
	 * 
	 * @param rfcDestinationName
	 *           RFC destination name
	 * @return action result map
	 */
	private Map<String, Object> pingCurrentDestination(final String rfcDestinationName)
	{
		JCoDestination jcoDestination;
		final Map<String, Object> actionResultMap = new HashMap<String, Object>();

		final StringBuffer buffer = new StringBuffer();
		Properties properties = null;
		try
		{
			final SAPHybrisSessionProvider sapHybrisSessionProvider = (SAPHybrisSessionProvider) GenericFactoryProvider
					.getInstance().getBean("sapCoreHybrisSessionProvider");

			// Create and set a SAPHybrisSession --> the SAPJCoSessionReferenceProvider requires a SAPHybrisSession
			@SuppressWarnings("unused")
			final SAPHybrisSession sapHybrisSession = sapHybrisSessionProvider.getSAPHybrisSession();

			buffer.append(Localization.getLocalizedString("ping.RFCDestination.GetDestination", new Object[]
			{ rfcDestinationName })).append(SapcoreConstants.CRLF);

			jcoDestination = JCoDestinationManager.getDestination(rfcDestinationName);
			// save properties for the catch clause
			properties = jcoDestination.getProperties();

			addConnectionData(buffer, jcoDestination);
			buffer.append(Localization.getLocalizedString("ping.RFCDestination.Execute")).append(SapcoreConstants.CRLF);

			jcoDestination.ping();
			buffer.append(Localization.getLocalizedString("ping.RFCDestination.SuccessMessage", new Object[]
			{ rfcDestinationName }));
			actionResultMap.put("result", 0);
			actionResultMap.put("needRefresh", true);
		}
		catch (final JCoException e)
		{
			actionResultMap.put("result", 1);
			actionResultMap.put("needRefresh", false);

			buffer.append(Localization.getLocalizedString("ping.RFCDestination.FailureMessage", new Object[]
			{ rfcDestinationName }));
			buffer.append(SapcoreConstants.CRLF).append(SapcoreConstants.CRLF);
			appendConnectionProperties(buffer, properties);
			buffer.append(Localization.getLocalizedString("ping.RFCDestination.Exception")).append(SapcoreConstants.CRLF);
			buffer.append(e.getLocalizedMessage());
		}
		actionResultMap.put("message", buffer.toString());
		return actionResultMap;
	}

	/**
	 * Adds the core data of the destination to the given StringBuffer.
	 * 
	 * @param buffer
	 *           the StringBuffer that contains the core data afterwards.
	 * @param jcoDestination
	 *           the core data is taken from this destination.
	 */
	private void addConnectionData(final StringBuffer buffer, final JCoDestination jcoDestination)
	{
		buffer.append(Localization.getLocalizedString("ping.RFCDestination.UseData")).append(SapcoreConstants.CRLF);
		final String host = jcoDestination.getApplicationServerHost();
		if (host != null)
		{
			buffer.append("\t").append("Host\t\t\t\t\t\t\t\t").append(host).append(SapcoreConstants.CRLF);
			buffer.append("\t").append("Instance Nr\t\t\t\t").append(jcoDestination.getSystemNumber()).append(SapcoreConstants.CRLF);
		}
		else
		{
			buffer.append("\t").append("SystemID\t\t\t\t\t").append(jcoDestination.getR3Name()).append(SapcoreConstants.CRLF);
			buffer.append("\t").append("Message Server\t").append(jcoDestination.getMessageServerHost())
					.append(SapcoreConstants.CRLF);
			buffer.append("\t").append("Logon Group\t\t\t").append(jcoDestination.getLogonGroup()).append(SapcoreConstants.CRLF);
		}
		buffer.append("\t").append("Client \t\t\t\t\t\t\t").append(jcoDestination.getClient()).append(SapcoreConstants.CRLF);
		buffer.append("\t").append("User\t\t\t\t\t\t\t\t").append(jcoDestination.getUser());
		buffer.append(SapcoreConstants.CRLF).append(SapcoreConstants.CRLF);
	}

	/**
	 * Adds the properties to the given StringBuffer. The jco-client-password is excluded if the properties contain a
	 * such a key.
	 * 
	 * @param buffer
	 *           the StringBuffer that contains the properties afterwards.
	 * @param properties
	 *           the properties that should be appended to the given StringBuffer.
	 */
	private void appendConnectionProperties(final StringBuffer buffer, final Properties properties)
	{
		if (properties != null)
		{
			buffer.append(Localization.getLocalizedString("ping.RFCDestination.Properties")).append(SapcoreConstants.CRLF);
			for (final Entry<Object, Object> entry : properties.entrySet())
			{
				if (!entry.getKey().equals("jco.client.passwd"))
				{
					final String[] key = ((String) entry.getKey()).split("\\.");
					if (key.length > 0)
					{
						buffer.append(key[key.length - 1]).append("\t\t\t\t").append(entry.getValue()).append(SapcoreConstants.CRLF);
					}
					else
					{
						buffer.append(entry.getKey()).append("\t\t").append(entry.getValue()).append(SapcoreConstants.CRLF);
					}
				}
			}
			buffer.append(SapcoreConstants.CRLF);
		}
	}
}
