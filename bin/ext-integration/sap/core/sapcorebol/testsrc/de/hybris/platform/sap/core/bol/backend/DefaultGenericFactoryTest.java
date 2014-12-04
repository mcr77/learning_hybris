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
package de.hybris.platform.sap.core.bol.backend;

import static org.junit.Assert.assertNotSame;

import de.hybris.platform.sap.core.bol.backend.bo.CarrierBOTest;
import de.hybris.platform.sap.core.bol.test.SapcorebolSpringJUnitTest;
import de.hybris.platform.sap.core.common.util.DefaultGenericFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 * Test for Default Generic Factory.
 */
@ContextConfiguration(locations =
{ "BackendObjectTest-spring.xml" })
public class DefaultGenericFactoryTest extends SapcorebolSpringJUnitTest
{

	private final DefaultGenericFactory classUnderTest = new DefaultGenericFactory();

	/**
	 * Tests the removal of session scoped beans.
	 */
	@Test
	public void testRemoveSessionScopeBEBean()
	{
		final CarrierBOTest carrierBOTest = getCarrierBOTest();
		try
		{
			final BackendBusinessObject backendBusinessObjectForTest_false = carrierBOTest.getBackendBusinessObjectForTest(false);
			final BackendBusinessObject backendBusinessObjectForTest_true = carrierBOTest.getBackendBusinessObjectForTest(true);

			assertNotSame(backendBusinessObjectForTest_false, backendBusinessObjectForTest_true);
		}
		catch (final BackendException e)
		{
			e.printStackTrace(); //NOPMD
		}
	}

	/**
	 * Get Carrier test BO.
	 * 
	 * @return Carrier test BO
	 */
	private CarrierBOTest getCarrierBOTest()
	{
		return (CarrierBOTest) classUnderTest.getBean("sapCarrierBOTest");
	}


}
