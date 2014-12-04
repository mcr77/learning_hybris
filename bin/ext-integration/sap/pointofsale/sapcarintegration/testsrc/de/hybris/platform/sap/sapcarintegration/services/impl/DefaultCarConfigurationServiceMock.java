package de.hybris.platform.sap.sapcarintegration.services.impl;

import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;

public class DefaultCarConfigurationServiceMock implements CarConfigurationService{

	@Override
	public String getSapClient() {
		return "800";
	}
	
	@Override
	public String getRootUrl() {
		
		return "http://ldcird1.wdf.sap.corp:8002";
	};
	
	@Override
	public String getUsername() {
	return "";
	}
	
	@Override
	public String getPassword() {
		return "";
	}
	
	@Override
	public String getServiceName() {
		
		return "/sap/is/retail/car/int/odata/CARServices.xsodata";
	}
	
}
