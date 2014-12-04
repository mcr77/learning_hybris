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
package de.hybris.platform.sap.core.configuration.datahub;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class handles the save events in order to create data hub configurations and to transfer them to the data hub.
 */
public class ConfigurationSaveListener implements AfterSaveListener
{
	private static final Logger LOG = Logger.getLogger(ConfigurationSaveListener.class);

	private ModelService modelService;
	private DataHubTransferConfigurationManager dataHubManager;
	private DataHubTransferHandler dataHubTransferHandler;
	private Boolean dataHubOutboundEnabled;

	@Override
	public void afterSave(final Collection<AfterSaveEvent> events)
	{
		if (!isDataHubOutboundEnabled())
		{
			LOG.debug("Data hub outbound not enabled (sapcoreconfiguration.datahuboutbound.enabled=false).");
			return;
		}

		for (final AfterSaveEvent event : events)
		{
			final PK pk = event.getPk();
			final int typeCode = pk.getTypeCode();
			// Prevent invoking data hub transfer for SAPAdministration
			if (typeCode != SapcoreconfigurationConstants.ITEM_TYPE_CODE_SAP_ADMINISTRATION)
			{
				final int type = event.getType();
				final String code = dataHubManager.getItemCode(typeCode);
				if (code != null)
				{

					final List<ItemModel> models = new ArrayList<ItemModel>();
					models.add(getItemModel(event.getPk()));
					final List<DataHubTransferConfiguration> dataHubTransferConfigurations = dataHubManager
							.getDataHubTransferConfigurations(code);
					dataHubTransferHandler
							.invokeTransfer(code, models, dataHubTransferConfigurations, (AfterSaveEvent.REMOVE == type));
				}
			}
		}
	}

	/**
	 * Get item model with the latest properties.
	 * 
	 * @param pk
	 *           primary key
	 * @return item model
	 */
	protected ItemModel getItemModel(final PK pk)
	{
		final ItemModel model = modelService.get(pk);
		modelService.refresh(model);
		return model;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setDataHubManager(final DataHubTransferConfigurationManager dataHubManager)
	{
		this.dataHubManager = dataHubManager;
	}

	/**
	 * Injection setter for {@link DataHubTransferHandler}.
	 * 
	 * @param dataHubTransferHandler
	 *           the data hub transfer handler to set
	 */
	@Required
	public void setDataHubTransferHandler(final DataHubTransferHandler dataHubTransferHandler)
	{
		this.dataHubTransferHandler = dataHubTransferHandler;
	}

	/**
	 * Checks if the data hub outbound for configuration is enabled. <br>
	 * The enablement is done by setting property <code>sapcoreconfiguration.datahuboutbound.enabled</code> to true
	 * (default: false).
	 * 
	 * @return the dataHubOutboundEnabled
	 */
	private boolean isDataHubOutboundEnabled()
	{
		if (dataHubOutboundEnabled == null)
		{
			dataHubOutboundEnabled = Config.getBoolean("sapcoreconfiguration.datahuboutbound.enabled", false);
		}
		return dataHubOutboundEnabled;
	}

}
