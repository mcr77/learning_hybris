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
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.HashMap;
import java.util.Map;

import com.hybris.datahub.core.services.DataHubOutboundService;


/**
 * Class to prepare the customer data and send the data to the Data Hub
 */
public class CustomerExportService
{
	protected static final String RAW_HYBRIS_CUSTOMER = "RawHybrisCustomer";
	protected static final String DEFAULT_FEED = "DEFAULT_FEED";
	protected static final String COUNTRY_DE = "DE";
	protected static final String ADDRESSUSAGE_DE = "DE";
	protected static final String OBJTYPE_KNA1 = "KNA1";

	private CustomerNameStrategy customerNameStrategy;
	private DataHubOutboundService dataHubOutboundService;

	private String feed = DEFAULT_FEED;
	private final String country = COUNTRY_DE;

	/**
	 * return Data Hub Outbound Service
	 * 
	 * @return dataHubOutboundService
	 */
	public DataHubOutboundService getDataHubOutboundService()
	{
		return dataHubOutboundService;
	}

	/**
	 * set Data Hub Outbound Service
	 * 
	 * @param dataHubOutboundService
	 */
	public void setDataHubOutboundService(final DataHubOutboundService dataHubOutboundService)
	{
		this.dataHubOutboundService = dataHubOutboundService;
	}

	/**
	 * map customer Model to the target map, set session language and base store name, and send data to the Data Hub
	 * 
	 * @param customerModel
	 * @param baseStoreUid
	 * @param sessionLanguage
	 */
	public void createAndSend(final CustomerModel customerModel, final String baseStoreUid, final String sessionLanguage)
	{
		final Map<String, Object> target;

		final String[] names = customerNameStrategy.splitName(customerModel.getName());

		target = new HashMap<String, Object>();
		target.put("UID", customerModel.getUid());

		target.put("customerID", customerModel.getCustomerID());
		target.put("contactID", customerModel.getSapContactID());
		target.put("firstname", names[0]);
		target.put("lastname", names[1]);
		target.put("sessionLanguage", sessionLanguage);
		target.put("title", customerModel.getTitle().getCode());
		target.put("baseStore", baseStoreUid);
		target.put("objType", OBJTYPE_KNA1);
		target.put("addressUsage", ADDRESSUSAGE_DE);
		target.put("country", country);

		dataHubOutboundService.sendToDataHub(getFeed(), RAW_HYBRIS_CUSTOMER, target);
	}

	/**
	 * return data hub feed
	 * 
	 * @return feed
	 */
	public String getFeed()
	{
		return feed;
	}

	/**
	 * set data hub feed (usually set via the local property file)
	 * 
	 * @param feed
	 */
	public void setFeed(final String feed)
	{
		this.feed = feed;
	}

	/**
	 * @return customerNameStrategy
	 */
	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	/**
	 * @param customerNameStrategy
	 */
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

}
