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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.Map;

import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


public interface IPCPriceAttributeHandler
{

	public abstract void setCustExits(ERPLO_APICustomerExits custExits);

	/**
	 * Sets item ipc price relevant attributes from the ComR table, if requested and if the item is configurable
	 * 
	 * @param ttItemComR
	 *           the ComR table
	 * @param setIpcPriceAttributes
	 *           flag, to indicate if price Attributes should be set at all
	 * @param itm
	 *           the item, to set the attributes
	 */
	public abstract void buildComRItemMapPriceAttributes(JCoTable ttItemComR, boolean setIpcPriceAttributes, Item itm,
			Map<String, Map<String, String>> itemsPriceAttribMap);

	/**
	 * Sets item ipc price relevant atttributes from the ComV table, if requested and if the item is configurable
	 * 
	 * @param ttItemComV
	 *           the ComV table
	 * @param setIpcPriceAttributes
	 *           flag, to indicate if price Attributes should be set at all
	 */
	public abstract void buildComVItemMapPriceAttributes(JCoTable ttItemComV, boolean setIpcPriceAttributes, Item itm,
			Map<String, Map<String, String>> itemsPriceAttribMap);

	/**
	 * Sets header ipc price relevant atttributes if requested
	 * 
	 * @param esHeadComV
	 * @param setIpcPriceAttributes
	 */
	public void handleEsHeadComVPriceAttributes(final JCoStructure esHeadComV, final Header header,
			final boolean setIpcPriceAttributes, final Map<String, String> ipcHeadPriceAttributes,
			final Map<String, String> ipcHeadPropMap);

}