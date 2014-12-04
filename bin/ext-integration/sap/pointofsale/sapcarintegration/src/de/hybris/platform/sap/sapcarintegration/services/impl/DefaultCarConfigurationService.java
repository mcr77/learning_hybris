package de.hybris.platform.sap.sapcarintegration.services.impl;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultCarConfigurationService implements CarConfigurationService{
	
	private BaseStoreService baseStoreService;
	
	
	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}
	
	@Override
	public String getSapClient() {
		return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcarintegration_client();
	}

	@Override
	public String getRootUrl(){
		
		return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcarintegration_HTTPDestination().getTargetURL();
	}
	
	@Override
	public String getServiceName(){
		
		return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcarintegration_serviceName();
	}

	@Override
	public String getUsername() {

		return getBaseStoreService().getCurrentBaseStore()
				.getSAPConfiguration().getSapcarintegration_HTTPDestination()
				.getUserid();
	}

	@Override
	public String getPassword() {

		return getBaseStoreService().getCurrentBaseStore()
				.getSAPConfiguration().getSapcarintegration_HTTPDestination()
				.getPassword();
	}
}
