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
package de.hybris.platform.sap.sapordermgmtbol.constants;



/**
 * Global class for all Sapordermgmtbol constants. You can add global constants for your extension into this class.
 */
public final class SapordermgmtbolConstants extends GeneratedSapordermgmtbolConstants
{
	public static final String EXTENSIONNAME = "sapordermgmtbol";
	public static final String PREFIX = "sapOrdermgmt";

	private SapordermgmtbolConstants()
	{
		//empty to avoid instantiating this constant class
	}

	//Aliases - Business Objects
	public static final String ALIAS_BO_CART = PREFIX + "CartBO";
	public static final String ALIAS_BO_ORDER = PREFIX + "OrderBO";
	public static final String ALIAS_BO_ORDER_HISTORY = PREFIX + "OrderHistoryBO";
	public static final String ALIAS_BO_TRANSACTION_CONFIGURATION = PREFIX + "TransactionConfigurationBO";
	public static final String ALIAS_BO_SEARCH = PREFIX + "SearchBO";

	//Aliases - Backend Objects
	public static final String ALIAS_BE_CART_ERP = PREFIX + "CartBeERP";
	public static final String ALIAS_BE_ORDER_ERP = PREFIX + "OrderBeERP";
	public static final String ALIAS_BE_ORDER_HISTORY_ERP = PREFIX + "OrderHistoryBeERP";

	//Aliases - Interaction Objects
	public static final String ALIAS_INT_CREATE_ORDER = PREFIX + "CreateOrderInteraction";
	public static final String ALIAS_INT_INITBASKET = PREFIX + "InitCartInteraction";
	public static final String ALIAS_INT_ADD_TO_BASKET_FUNCTIONS = PREFIX + "AddToBasketFunctions";
	public static final String ALIAS_INT_CHECK_DOCUMENT_VALID = PREFIX + "CheckDocumentValid";


	//Other aliases
	public static final String ALIAS_BEAN_CONNECTED_DOCUMENT = PREFIX + "ConnectedDocument";
	public static final String ALIAS_BEAN_BE_CUST_EXIT = PREFIX + "ERPCustExit";
	public static final String ALIAS_BEAN_BACKEND_UTIL = PREFIX + "BackendUtil";
	public static final String ALIAS_BEAN_TRANSACTIONS_FACTORY = PREFIX + "TransactionsFactory";
	public static final String ALIAS_BEAN_ITEM_LIST = PREFIX + "ItemList";
	public static final String ALIAS_BEAN_BILLING_HEADER_STATUS = PREFIX + "BillingHeaderStatus";
	public static final String ALIAS_BEAN_BILLING_ITEM_STATUS = PREFIX + "BillingItemStatus";
	public static final String ALIAS_BEAN_SHIPPING_STATUS = PREFIX + "ShippingStatus";
	public static final String ALIAS_BEAN_PROCESSING_STATUS = PREFIX + "ProcessingStatus";
	public static final String ALIAS_BEAN_CONNECTED_DOCUMENT_ITEM = PREFIX + "ConnectedDocumentItem";
	public static final String ALIAS_BEAN_PARTNER_LIST = PREFIX + "PartnerList";
	public static final String ALIAS_BEAN_TEXT = PREFIX + "Text";
	public static final String ALIAS_BEAN_ALTERNATIVE_PRODUCT = PREFIX + "AlternativeProduct";
	public static final String ALIAS_BEAN_ALTERNATIVE_PRODUCT_LIST = PREFIX + "AlternativeProductList";
	public static final String ALIAS_BEAN_ADDITIONAL_PRICING = PREFIX + "AdditionalPricing";
	public static final String ALIAS_BEAN_SCHEDLINE = PREFIX + "Schedline";
	public static final String ALIAS_BEAN_PARTNER_LIST_ENTRY = PREFIX + "PartnerListEntry";
	public static final String ALIAS_BEAN_OVERALL_STATUS_ORDER = PREFIX + "OverallStatusOrder";
	public static final String ALIAS_BEAN_SALES_TRANSACTIONS_UTIL = PREFIX + "SalesTransactionsUtil";
	public static final String ALIAS_BEAN_ITEM = PREFIX + "Item";
	public static final String ALIAS_BEAN_SHIP_TO = PREFIX + "ShipTo";
	public static final String ALIAS_BEAN_BILL_TO = PREFIX + "BillTo";
	public static final String ALIAS_BEAN_TRANSFERITEM_UTILITY = PREFIX + "TransferItemUtility";
	public static final String ALIAS_BEAN_HEADER = PREFIX + "SalesdocHeader";
	public static final String ALIAS_BEAN_BACKEND_MESSAGE_MAPPER = PREFIX + "BackendMessageMapper";
	public static final String ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER = PREFIX + "MessageMappingRulesContainer";
	public static final String ALIAS_BEAN_MESSAGE_MAPPING_RULES_LOADER = PREFIX + "MessageMappingRulesLoader";
	public static final String ALIAS_BEAN_SEARCH_RESULT_LIST = PREFIX + "SearchResultList";
	public static final String ALIAS_BEAN_SEARCH_RESULT_ENTRY = PREFIX + "SearchResult";

	//Strategies
	public static final String ALIAS_BEAN_READ_STRATEGY = PREFIX + "ReadStrategy";
	public static final String ALIAS_BEAN_WRITE_STRATEGY = PREFIX + "WriteStrategy";
	public static final String ALIAS_BEAN_ACTIONS_STRATEGY = PREFIX + "ActionsStrategy";
	public static final String ALIAS_BEAN_PCFG_STRATEGY = PREFIX + "ProductConfigurationStrategy";


	// Mapper
	public static final String ALIAS_BEAN_PARTNER_MAPPER = PREFIX + "PartnerMapper";
	public static final String ALIAS_BEAN_HEADER_MAPPER = PREFIX + "HeaderMapper";
	public static final String ALIAS_BEAN_ITEM_MAPPER = PREFIX + "ItemMapper";
	public static final String ALIAS_BEAN_HEADER_TEXT_MAPPER = PREFIX + "HeaderTextMapper";
	public static final String ALIAS_BEAN_ITEM_TEXT_MAPPER = PREFIX + "ItemTextMapper";
	public static final String ALIAS_BEAN_INCOMPLETION_MAPPER = PREFIX + "IncompletionMapper";



	//Bean Ids - Caches
	public static final String BEAN_ID_CACHE_DELIVERY_TYPES = PREFIX + "DeliveryTypeCacheRegion";
	public static final String BEAN_ID_CACHE_MESSAGE_MAPPING = PREFIX + "MessageMappingCacheRegion";
	public static final String BEAN_ID_CACHE_USER_STATUS = PREFIX + "UserStatusCacheRegion";


	//Configuration properties
	public static final String CONFIGURATION_PROPERTY_HEADER_CONDITION_TYPE_FREIGHT = "sapordermgmt_headerCondTypeFreight";
	public static final String CONFIGURATION_PROPERTY_SOURCE_FREIGHT = "sapordermgmt_sourceFreight";
	public static final String CONFIGURATION_PROPERTY_SOURCE_NET_WO_FREIGHT = "sapordermgmt_sourceNetWOFreight";
	public static final String CONFIGURATION_PROPERTY_SEARCH_MAX_HITS = "sapordermgmt_maxHits";
	public static final String CONFIGURATION_PROPERTY_SEARCH_DATE_RANGE = "sapordermgmt_dateRange";








}
