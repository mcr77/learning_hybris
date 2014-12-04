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
package de.hybris.platform.sap.core.bol.backend.bo;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * @author Administrator
 * 
 */
public interface CarrierBOTest extends BusinessObject
{
	//	/**
	//	 * Returns all airline carrier details.
	//	 * 
	//	 * @return ArrayList of all airline carriers
	//	 */
	//	public List<CarrierDetail> getCarriers() throws BusinessObjectException;

	/**
	 * Get backend buiness object test method.
	 * 
	 * @param initialize
	 *           initialize indicator
	 * @return {@link BackendBusinessObject}
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public BackendBusinessObject getBackendBusinessObjectForTest(final boolean initialize) throws BackendException;
}
