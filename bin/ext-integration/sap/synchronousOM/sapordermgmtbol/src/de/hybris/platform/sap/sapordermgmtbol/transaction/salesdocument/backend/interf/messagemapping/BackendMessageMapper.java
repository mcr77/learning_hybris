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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoTable;


public interface BackendMessageMapper
{

	public void map(BusinessObject rootBo, //
			JCoRecord singleMsg, JCoTable msgTable) throws BackendException;

	public Message map(BusinessObject rootBo, JCoRecord struct) throws BackendException;

}