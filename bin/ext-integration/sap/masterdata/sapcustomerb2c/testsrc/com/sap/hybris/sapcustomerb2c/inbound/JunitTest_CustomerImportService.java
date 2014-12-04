package com.sap.hybris.sapcustomerb2c.inbound;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationListener;

import com.sap.hybris.sapcustomerb2c.JunitCustomerConstants;


/**
 * Test of CustomerImportService
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class JunitTest_CustomerImportService
{

	class MyCustomerImportService extends CustomerImportService
	{
		@Override
		protected CustomerModel readCustomer(final String customerId)
		{
			final CustomerModel customerModel = new CustomerModel();

			return customerModel;
		}

		@Override
		public EventService getEventService()
		{
			return new MyEventService();
		}
	}

	class MyEventService implements EventService
	{

		@SuppressWarnings("rawtypes")
		@Override
		public Set<ApplicationListener> getEventListeners()
		{
			return null;
		}

		@Override
		public void publishEvent(final AbstractEvent arg0)
		{
			// Main check: is customerId the correct one given at processConsumerReplicationNotificationFromHub
			assertEquals(((CustomerReplicationEvent) arg0).getCustomerID(), JunitCustomerConstants.CUSTOMER_ID);
		}

		@Override
		public boolean registerEventListener(@SuppressWarnings("rawtypes") final ApplicationListener arg0)
		{
			return false;
		}

		@Override
		public boolean unregisterEventListener(@SuppressWarnings("rawtypes") final ApplicationListener arg0)
		{
			return false;
		}

	}

	@InjectMocks
	private final CustomerImportService customerImportService = new MyCustomerImportService();


	/**
	 * test processed in MyEventSerivce.publishEvent
	 */
	@Test
	public void testEventPublishing()
	{
		final ModelService modelService = mock(ModelService.class);
		customerImportService.setModelService(modelService);
		customerImportService.processConsumerReplicationNotificationFromHub(JunitCustomerConstants.CUSTOMER_ID);
	}

}
