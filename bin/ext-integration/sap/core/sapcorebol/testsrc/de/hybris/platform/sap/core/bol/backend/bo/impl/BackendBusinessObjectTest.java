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
package de.hybris.platform.sap.core.bol.backend.bo.impl;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.bol.backend.be.CarrierBETest;
import de.hybris.platform.sap.core.bol.backend.bo.CarrierBOTest;
import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Manages the search for Airline Carrier.
 */
@BackendInterface(CarrierBETest.class)
public class BackendBusinessObjectTest extends BusinessObjectBase implements CarrierBOTest
{

	@Override
	public BackendBusinessObject getBackendBusinessObjectForTest(final boolean initialize) throws BackendException
	{
		return getBackendBusinessObject(initialize);
	}

}
