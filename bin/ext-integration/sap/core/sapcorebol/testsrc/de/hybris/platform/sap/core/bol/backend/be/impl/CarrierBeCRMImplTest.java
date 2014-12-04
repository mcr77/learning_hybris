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
package de.hybris.platform.sap.core.bol.backend.be.impl;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.be.CarrierBETest;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;


/**
 * Test Carrier BE.
 */
@BackendType("CRM")
public class CarrierBeCRMImplTest extends BackendBusinessObjectBaseJCo implements CarrierBETest
{

	//	/*
	//	 * (non-Javadoc)
	//	 * 
	//	 * @see de.hybris.platform.sflight.be.SFlightBE#readAllAirlines(de.hybris.platform .sflight.bo.SFlightBO)
	//	 */
	//	@Override
	//	public void readAllAirlines(final CarrierBOTest carrierBO) throws BackendException
	//	{
	//
	//		// final JCoDestination jcoDestination =
	//		// getDestinationProvider().getJCoDestination();
	//		final JCoConnection jcoConnection = getDefaultJCoConnection();
	//		JCoFunction function = null;
	//
	//		function = jcoConnection.getFunction("CMR_ISA_SCARR_GETLIST");
	//
	//		// call the function
	//		jcoConnection.execute(function);
	//
	//		// get the output parameter
	//		final JCoTable airlines = function.getTableParameterList().getTable("ET_AIRLINES");
	//		CarrierDetail carrierDetail;
	//
	//		final int numAirlines = airlines.getNumRows();
	//
	//		// Clear the list
	//		carrierBO.clearCarrierList();
	//
	//		for (int i = 0; i < numAirlines; i++)
	//		{
	//			carrierDetail = (CarrierDetail) genericFactory.getBean("sapCarrierDetail");
	//			// carrierDetail = carrierBO.createCarrierDetail();
	//
	//			carrierDetail.setId(airlines.getString("CARRID"));
	//			carrierDetail.setAirlineCode(airlines.getString("CARRID"));
	//			carrierDetail.setCCName(airlines.getString("CARRNAME"));
	//			carrierDetail.setCurrencyCode(airlines.getString("CURRCODE"));
	//
	//			// final UUID guid = new UUID(0, 0);
	//			// final UUID newGuid = guid.randomUUID();
	//			// carrierDetail.setTechKey(new TechKey(newGuid.toString()));
	//
	//			// // Set the Dynamic Field Control in the backend
	//			// String uiField =
	//			// accessController.getUIElementNameForBackendElement(new GenericCacheKey(new
	//			// String[] {"ET_AIRLINES", "CARRID" }));
	//			// UIElement uiElement =
	//			// accessController.getUIController().getUIElement(uiField,
	//			// airlineCarrierDetail.getTechKey().toString());
	//			// //uiElement.setHidden(true);
	//
	//			// add to carrier search object
	//			carrierBO.addCarrierDetail(carrierDetail);
	//
	//			airlines.nextRow();
	//		}
	//		final CarrierDetail testCarrierDetail = (CarrierDetail) genericFactory.getBean("sapTestCarrierDetail");
	//		carrierBO.addCarrierDetail(testCarrierDetail);
	//	}
}
