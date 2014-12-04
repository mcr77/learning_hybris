/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.sap.sapcarintegration.constants;

/**
 * Global class for all Sapcarintegration constants. You can add global constants for your extension into this class.
 */
public final class SapcarintegrationConstants extends GeneratedSapcarintegrationConstants
{
	public static final String EXTENSIONNAME = "sapcarintegration";
	
	public static final String PROXY_HOST = "proxyhost";
	public static final String PROXY_PORT = "proxyport";
	public static final String SEPARATOR = "/";

	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	
	
	// sap client
	public static final String SAPCLIENT_PARAM = "P_SAPClient";
	
	

	public static final String SELECT_POSSALES_ITEM = "OrderChannel,OrderChannelName,Location,LocationName,Article,"
			+ "ArticleName,RetailStoreID,BusinessDayDate,TransactionIndex,TransactionCurrency,CustomerNumber,"
			+ "SalesUnit,SalesAmount,TaxExcludedAmount,DistributedTaxIncludedAmount,DistributedTaxExcludedAmount,"
			+ "SalesQuantityInSalesUnit,SalesQuantityInBaseUnit,ItemDiscountsAmount,DistributedDiscountsAmount,"
			+ "DistributedHeaderTaxAmount";
	
	
	public static final String SELECT_POSSALES_HEADER = "OrderChannel,OrderChannelName,Location,LocationName,"
			+ "RetailStoreID,BusinessDayDate,TransactionIndex,TransactionCurrency,CustomerNumber,"
			+ "SalesUnit,SalesAmount,TaxExcludedAmount,DistributedTaxIncludedAmount,DistributedTaxExcludedAmount,"
			+ "SalesQuantityInSalesUnit,SalesQuantityInBaseUnit,ItemDiscountsAmount,DistributedDiscountsAmount,"
			+ "DistributedHeaderTaxAmount";
	
	
	
	
	private SapcarintegrationConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
