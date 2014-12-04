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
package de.hybris.platform.sap.core.jco.connection.impl;

import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionEvent;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameter;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameters;
import de.hybris.platform.sap.core.jco.connection.JCoLogUtil;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.core.jco.exceptions.JCoExceptionSpliter;

import java.security.InvalidParameterException;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoCustomDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;



/**
 * Base class for a managed connection.
 */

public class JCoConnectionImpl implements JCoConnection
{

	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(JCoConnectionImpl.class.getName());

	/**
	 * JCO Custom destination.
	 */
	protected JCoCustomDestination destination; //NOPMD
	/**
	 * Properties filled by spring definition in constructor. <br>
	 * Possible values e.g. {@link RFCManagedConnection.JCO_DESTINATION} and {@link RFCManagedConnection.JCO_SCOPE_TYPE}
	 */
	protected Properties properties; //NOPMD
	/**
	 * Connection parameters.
	 */
	protected JCoConnectionParameters connectionParameters = null;//NOPMD


	/**
	 * Caller id.
	 */
	private String callerId;

	/**
	 * Scope type.
	 */
	private String scopeType = null;

	/**
	 * Constructor.
	 * 
	 * @param properties
	 *           properties for this instance.
	 */
	public JCoConnectionImpl(final Properties properties)
	{
		super();
		this.properties = properties;
	}

	/**
	 * Setter for connection parameters.
	 * 
	 * @param connectionParameters
	 *           connection parameters.
	 */
	public void setConnectionParameters(final JCoConnectionParameters connectionParameters)
	{
		this.connectionParameters = connectionParameters;
	}


	/**
	 * Init method called by spring init method definition.
	 * 
	 * @throws BackendException
	 *            Exception in case of failure.
	 */
	public void init() throws BackendException
	{
		if (!properties.containsKey(JCoConnection.JCO_DESTINATION))
		{
			throw new BackendException("" + JCoConnection.JCO_DESTINATION + " not provided");
		}

		final String destinationName = properties.getProperty(JCoConnection.JCO_DESTINATION);

		final String scopeType = properties.getProperty(JCoConnection.JCO_SCOPE_TYPE, getScopeType());

		try
		{
			destination = JCoDestinationManager.getDestination(destinationName, scopeType).createCustomDestination();
			final JCoCustomDestination.UserData userProps = getDestination().getUserLogonData();
			setLanguageInCustomDestination(userProps);
		}
		catch (final JCoException e)
		{
			this.destination = null;
			JCoExceptionSpliter.splitAndThrowException(e);
		}


	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.bol.jco2.RFCManagedConnection#getFunction(java.lang.String)
	 */
	@Override
	public JCoFunction getFunction(String functionName) throws BackendException
	{
		checkInternalStatus();
		JCoFunction function = null;
		JCoConnectionParameter connectionParameter = null;

		if (functionName == null)
		{
			throw new InvalidParameterException("parameter functionName must not be null.");
		}


		if (connectionParameters != null && connectionParameters.isFunctionModuleConfigured(functionName))
		{
			connectionParameter = connectionParameters.getConnectionParameter(functionName);
			functionName = getReplacedFunctionModuleName(functionName);
		}

		try
		{
			function = getDestination().getRepository().getFunction(functionName);
		}
		catch (final JCoException e)
		{
			JCoExceptionSpliter.splitAndThrowException(e);
		}

		if (function == null)
		{
			throw new BackendException("Cannot get function " + functionName + " from repository. Destination is "
					+ getDestination().toString());
		}

		return new JCoManagedFunction(function, connectionParameter);
	}

	/**
	 * Replaces origin function name with function name defined in connection parameters.
	 * 
	 * @param functionName
	 *           origin function name
	 * @return replaced function name
	 */
	protected String getReplacedFunctionModuleName(String functionName)
	{
		final JCoConnectionParameter connectionParameter = connectionParameters.getConnectionParameter(functionName);
		if (connectionParameter.getFunctionModuleToBeReplaced() != null)
		{
			functionName = connectionParameter.getFunctionModuleToBeReplaced();
		}
		return functionName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.bol.jco2.RFCManagedConnection#execute(com.sap.conn.jco.JCoFunction)
	 */
	@Override
	public void execute(final JCoFunction function) throws BackendException
	{
		checkInternalStatus();
		try
		{
			if (!(function instanceof JCoManagedFunction))
			{
				function.execute(getDestination());
				return;
			}

			final JCoManagedFunction managedFunction = (JCoManagedFunction) function;

			if (managedFunction.hasConnectionParameters())
			{
				preProcessingExecute((JCoManagedFunction) function);
			}

			function.execute(getDestination());

			if (managedFunction.hasConnectionParameters())
			{
				postProcessingExecute((JCoManagedFunction) function);
			}
		}
		catch (final JCoException e)
		{
			JCoExceptionSpliter.splitAndThrowException(e);
		}
	}

	/**
	 * Called before JCO execution. Calls event listeners and tracing. <br>
	 * This method is only called if parameters are defined for that function.
	 * 
	 * @param function
	 *           function which is called.
	 */
	protected void preProcessingExecute(final JCoManagedFunction function)
	{

		final JCoConnectionParameter connectionParameter = function.getConnectionParameter();

		if (connectionParameter.getEventListener() != null)
		{
			connectionParameter.getEventListener().connectionEvent(
					new JCoConnectionEvent(this, function, JCoConnectionEvent.BEFORE_JCO_FUNCTION_CALL));
		}


		if (connectionParameter.getTraceBefore())
		{
			final JCoLogUtil logUtil = new JCoLogUtil();
			logUtil.logBeforeCall(function);
		}

	}

	/**
	 * Called after JCO execution. Calls event listeners and tracing. <br>
	 * This method is only called if parameters are defined for that function.
	 * 
	 * @param function
	 *           function which is called.
	 */
	protected void postProcessingExecute(final JCoManagedFunction function)
	{
		final JCoConnectionParameter connectionParameter = function.getConnectionParameter();

		if (connectionParameter.getEventListener() != null)
		{
			connectionParameter.getEventListener().connectionEvent(
					new JCoConnectionEvent(this, function, JCoConnectionEvent.AFTER_JCO_FUNCTION_CALL));
		}

		if (connectionParameter.getTraceAfter())
		{
			final JCoLogUtil logUtil = new JCoLogUtil();
			logUtil.logAfterCall(function);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.bol.jco2.RFCManagedConnection#isFunctionAvailable(java.lang.String)
	 */
	@Override
	public boolean isFunctionAvailable(final String functionName)
	{

		checkInternalStatus();
		boolean returnValue = false;
		try
		{
			returnValue = getDestination().getRepository().getFunction(functionName) != null;
		}
		catch (final JCoException e)
		{
			throw new BackendRuntimeException("Call respository.getFunction() failed", e);
		}

		return returnValue;
	}


	/**
	 * Sets language in custom destination. <br>
	 * If properties contains a value with key {@link RFCManagedConnection.JCO_LANG} this language is set.
	 * 
	 * Otherwise language is taken from {@link LocaleUtil}
	 * 
	 * @param userData
	 *           user data of custom destination
	 */
	private void setLanguageInCustomDestination(final JCoCustomDestination.UserData userData)
	{

		if (properties != null && properties.containsKey(JCoConnection.JCO_LANG))
		{
			final String lang = properties.getProperty(JCoConnection.JCO_LANG);
			log.debug("Language for JCo connection in connectionProperties is " + lang);
			userData.setLanguage(lang);
			return;
		}

		final Locale locale = LocaleUtil.getLocale();
		if (locale != null)
		{
			log.debug("Language for JCo connection is taken from LocaleUtil " + locale.getLanguage());
			userData.setLanguage(locale.getLanguage());
			return;
		}
		log.debug("No language for JCo connection is set");
	}


	/**
	 * Check if the destination is stateful.
	 * 
	 * @return true if stateful, false otherwise.
	 * @throws BackendException
	 *            BackendException
	 */
	protected boolean isStateful() throws BackendException
	{
		checkInternalStatus();
		return JCoContext.isStateful(destination);
	}

	/**
	 * Getter for destination.
	 * 
	 * @return the destination
	 */
	protected JCoCustomDestination getDestination()
	{
		return destination;
	}

	/**
	 * Checks internal status of the connection. <br>
	 * After the object is destroyed the destination is set to null. No more function execution is possible.
	 * 
	 * Throws BackendRuntimeException in case of object was already destroyed.
	 */
	protected void checkInternalStatus()
	{
		if (this.destination == null)
		{
			throw new BackendRuntimeException("Connection already released");
		}

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.jco.connection.JCoConnection#setCallerId(java.lang.String)
	 */
	@Override
	public void setCallerId(final String callerID)
	{
		this.callerId = callerID;

	}

	/**
	 * Getter for caller id.
	 * 
	 * @return caller id
	 */
	public String getCallerId()
	{
		return callerId;
	}

	/**
	 * Getter for scope type.
	 * 
	 * @return the scopeType
	 */
	public String getScopeType()
	{
		return scopeType;
	}

	/**
	 * Setter for scope type.
	 * 
	 * @param scopeType
	 *           the scopeType to set
	 */
	public void setScopeType(final String scopeType)
	{
		this.scopeType = scopeType;
	}



}
