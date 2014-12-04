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

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hybris.datahub.core.services.DataHubOutboundService;


/**
 * This class handles the transfer of configuration models to the data hub via property map.
 * 
 */
public class DataHubTransferHandler
{
	private static final Logger LOG = Logger.getLogger(DataHubTransferHandler.class.getName());
	private DataHubOutboundService outboundService;

	/**
	 * Invokes the data transfer of the given configuration models to data hub.
	 * 
	 * @param code
	 *           item code as string
	 * @param models
	 *           list of configuration models
	 * @param dataHubTransferConfigurations
	 *           list of data hub transfer configurations related to this model
	 * @param delete
	 *           indicator that the configuration should be deleted
	 * @return {@link DataHubTransferLog}
	 */
	@SuppressWarnings("unchecked")
	public DataHubTransferLog invokeTransfer(final String code, final List<ItemModel> models,
			final List<DataHubTransferConfiguration> dataHubTransferConfigurations, final boolean delete)
	{
		final DataHubTransferLog dataHubTransferLog = new DataHubTransferLog();
		dataHubTransferLog.setItemCode(code);
		// As deletion of single configuration is not fully supported yet, it has to be provided in one of the next releases.
		if (models.size() > 0)
		{
			for (final DataHubTransferConfiguration dataHubTransferConfiguration : dataHubTransferConfigurations)
			{
				final List<Map<String, Object>> propertyMapList = new ArrayList<Map<String, Object>>();
				for (final ItemModel model : models)
				{
					final AbstractPopulatingConverter<ItemModel, Map<String, Object>> converter = dataHubTransferConfiguration
							.getConverter();
					final Map<String, Object> propertyMap = converter.convert(model);
					propertyMapList.add(propertyMap);
				}
				invokeDataHubTransfer(code, propertyMapList, dataHubTransferConfiguration.getRawType(), dataHubTransferLog);
			}
		}
		else
		{
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.noConfigurationItem");
			dataHubTransferLog.addLog(logMessage);
			LOG.info(code + ": " + logMessage);
		}
		return dataHubTransferLog;
	}

	/**
	 * Creates the property map for the given model.
	 * 
	 * @param model
	 *           the given model
	 * @param converter
	 *           converter for populating the property map
	 * @return the related property map of the event
	 */
	protected Map<String, Object> createConfigurationMap(final ItemModel model,
			final AbstractPopulatingConverter<ItemModel, Map<String, Object>> converter)
	{
		return converter.convert(model);
	}

	/**
	 * Creates the DTO for the item type of the save event.
	 * 
	 * @param model
	 *           source item model
	 * @param converter
	 *           converter for populating the dto
	 * @return the related DTO of the event
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	protected Object createConfigurationDTO(final ItemModel model, final AbstractPopulatingConverter converter)
	{
		return converter.convert(model);
	}

	/**
	 * Transfers a list of property maps to Data Hub.
	 * 
	 * @param itemCode
	 *           item code as string
	 * 
	 * @param propertyMapList
	 *           the list of property maps
	 * @param rawType
	 *           the raw type
	 * @param transferLog
	 *           transfer log container
	 */
	private void invokeDataHubTransfer(final String itemCode, final List<Map<String, Object>> propertyMapList,
			final String rawType, final DataHubTransferLog transferLog)
	{
		try
		{
			outboundService.sendToDataHub(getOutboundFeedName(), rawType, propertyMapList);
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.sendToRawItem.success", new Object[]
			{ propertyMapList.size(), rawType });
			transferLog.addLog(logMessage);
			LOG.info(itemCode + ": " + logMessage);
		}
		catch (final Exception ex)
		{
			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.sendToRawItem.exception", new Object[]
			{ rawType }) + ex.getLocalizedMessage();
			transferLog.addLog(logMessage);
			transferLog.setTransferException(new DataHubTransferException(logMessage, ex));
			LOG.fatal(itemCode + ": " + logMessage);
		}
	}

	/**
	 * Returns the data hub outbound feed name to be used to transfer configurations.
	 * 
	 * @return outbound feed name
	 */
	private String getOutboundFeedName()
	{
		return Config.getString(SapcoreconfigurationConstants.CONFIGURATION_DATAHUB_OUTBOUND_FEED_PROPERTY,
				SapcoreconfigurationConstants.CONFIGURATION_DATAHUB_OUTBOUND_FEED);
	}

	@SuppressWarnings("javadoc")
	public void setDataHubOutboundService(final DataHubOutboundService outboundService)
	{
		this.outboundService = outboundService;
	}

}
