package com.sap.hybris.sapcustomerb2c.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.task.RetryLaterException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcustomerb2c.JunitCustomerConstants;


/**
 * Test class for SapPublishCustomerAction class check of all value will be passed and set correctly
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class JunitTest_CustomerPublishAction implements JunitCustomerConstants
{

	class MyCustomerPublishAction extends CustomerPublishAction
	{
		@Override
		protected void setSapContactId(final BusinessProcessModel businessProcessModel)
		{

		}

	}

	class MyCustomerExportService extends CustomerExportService
	{
		@Override
		public void createAndSend(final CustomerModel customerModel, final String baseStoreName, final String sessionLanguage)
		{
			assertNotNull("customerModel is not transferred to the SendToDataHub class", customerModel);
			assertEquals(UID, customerModel.getUid());
			assertEquals(BASE_STORE, baseStoreName);
			assertEquals(SESSION_LANGUAGE, sessionLanguage);
		}

	}

	@InjectMocks
	private final CustomerPublishAction sapPublishCustomerAction = new MyCustomerPublishAction();

	@Mock
	private ModelService modelService;

	@Mock
	private DefaultStoreSessionFacade storeSessionFacade;

	@Mock
	private LanguageData languageData;


	/**
	 * <b>what to test:</b><br/>
	 * execute Action<br/>
	 * <b>expected result:</b> <li>update SapReplicationInfo property on the customer model</li> <li>check if
	 * customerModel will be transferred to the SendToDataHub class</li> <li>check if baseStoreName will be set correctly
	 * </li>
	 * 
	 * @throws RetryLaterException
	 *            will be raised if action should be again triggered
	 * 
	 */
	@SuppressWarnings("javadoc")
	@Test
	public void checkIfSapReplicationInfoWasUpdated()
	{

		// given
		final StoreFrontCustomerProcessModel businessProcessModel = mock(StoreFrontCustomerProcessModel.class);
		final CustomerModel customerModel = new CustomerModel();
		customerModel.setUid(UID);
		given(businessProcessModel.getCustomer()).willReturn(customerModel);

		final CustomerExportService sendCustomerToDataHub = new MyCustomerExportService();
		sapPublishCustomerAction.setSendCustomerToDataHub(sendCustomerToDataHub);

		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		businessProcessModel.setCustomer(customerModel);
		given(businessProcessModel.getStore()).willReturn(baseStoreModel);
		given(baseStoreModel.getUid()).willReturn(BASE_STORE);

		sapPublishCustomerAction.setStoreSessionFacade(storeSessionFacade);
		given(sapPublishCustomerAction.getStoreSessionFacade().getCurrentLanguage()).willReturn(languageData);
		given(sapPublishCustomerAction.getStoreSessionFacade().getCurrentLanguage().getIsocode()).willReturn(SESSION_LANGUAGE);
		sapPublishCustomerAction.setModelService(modelService);

		// when
		try
		{
			sapPublishCustomerAction.executeAction(businessProcessModel);
		}
		catch (final RetryLaterException retryLaterException)
		{
			assertEquals("RetryLaterException was raised", retryLaterException);
		}

		// then 
		assertNotNull("SapReplicationInfo was NOT updated", customerModel.getSapReplicationInfo());

	}
}
