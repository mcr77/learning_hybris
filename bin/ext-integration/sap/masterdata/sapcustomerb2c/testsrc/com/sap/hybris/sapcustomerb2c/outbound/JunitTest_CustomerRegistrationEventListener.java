package com.sap.hybris.sapcustomerb2c.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Test class for SapPublishCustomerAction class check of all value will be passed and set correctly
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class JunitTest_CustomerRegistrationEventListener
{

	class MyCustomerRegistrationEventListener extends CustomerRegistrationEventListener
	{
		private boolean replicateRegisteredUserActive;

		@Override
		public BusinessProcessService getBusinessProcessService()
		{
			// no idea how I can instantiate BusinessProcessService 
			if (!replicateRegisteredUserActive)
			{
				assertEquals("BusinessProcessService should not be started", null);
			}
			return null;
		}
	}


	/**
	 * <b>what to test:</b><br/>
	 * Replicate Registered User not active<br/>
	 * <b>expected result:</b> <li>Business Process should NOT be started</li>
	 * 
	 */
	@Test
	public void checkIfReplicateRegisteredUserIsNotActive()
	{

		// given
		final MyCustomerRegistrationEventListener CustomerRegistrationEventListener = new MyCustomerRegistrationEventListener();
		final RegisterEvent registerEvent = mock(RegisterEvent.class);

		final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService = mock(SAPGlobalConfigurationServiceImpl.class);
		CustomerRegistrationEventListener.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(sapCoreSAPGlobalConfigurationService.getProperty("replicateregistereduser")).willReturn(false);

		CustomerRegistrationEventListener.replicateRegisteredUserActive = false;

		//when
		try
		{
			CustomerRegistrationEventListener.onEvent(registerEvent);
		}
		catch (final NullPointerException nullPointerException)
		{
			// in case of null pointer exception the "replicateregistereduser" property was in correctly evaluated
			assertNull("BusinessProcess was triggered");
		}


	}

	/**
	 * <b>what to test:</b><br/>
	 * Replicate Registered User active<br/>
	 * <b>expected result:</b> <li>Business Process should be started</li>
	 * 
	 */
	@Test
	public void checkIfReplicateRegisteredUserIsActive()
	{

		// given
		final MyCustomerRegistrationEventListener CustomerRegistrationEventListener = new MyCustomerRegistrationEventListener();
		final RegisterEvent registerEvent = mock(RegisterEvent.class);

		final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService = mock(SAPGlobalConfigurationServiceImpl.class);
		CustomerRegistrationEventListener.setSapCoreSAPGlobalConfigurationService(sapCoreSAPGlobalConfigurationService);
		given(sapCoreSAPGlobalConfigurationService.getProperty("replicateregistereduser")).willReturn(true);


		CustomerRegistrationEventListener.replicateRegisteredUserActive = true;
		boolean testSuccessfull = false;

		//when
		try
		{
			CustomerRegistrationEventListener.onEvent(registerEvent);
		}
		catch (final NullPointerException nullPointerException)
		{
			testSuccessfull = true;
		}
		// then 
		assertEquals("BusinessProcess was triggered", testSuccessfull, true);

	}

}
