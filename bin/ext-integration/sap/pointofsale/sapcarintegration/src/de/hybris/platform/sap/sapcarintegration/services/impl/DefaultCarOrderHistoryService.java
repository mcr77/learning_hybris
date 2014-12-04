package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;
import de.hybris.platform.sap.sapcarintegration.services.CarOrderHistoryService;


public class DefaultCarOrderHistoryService implements CarOrderHistoryService{

	private CarDataProviderService carDataProviderService;
	
	
	private DefaultCarOrderHistoryExtractorService carOrderHistoryExtractorService;
	
	
	public DefaultCarOrderHistoryExtractorService getCarOrderHistoryExtractorService() {
		return carOrderHistoryExtractorService;
	}

	@Required
	public void setCarOrderHistoryExtractorService(
			DefaultCarOrderHistoryExtractorService carOrderHistoryExtractorService) {
		this.carOrderHistoryExtractorService = carOrderHistoryExtractorService;
	}

	public CarDataProviderService getCarDataProviderService() {
		return carDataProviderService;
	}
	
	@Required
	public void setCarDataProviderService(
			CarDataProviderService carDataProviderService) {
		this.carDataProviderService = carDataProviderService;
	}

	@Override
	public List<CarOrderHistoryData> readOrdersForCustomer(String customerNumber, String sortBy) {
		
		return getCarOrderHistoryExtractorService().extractOrders(getCarDataProviderService().readHeaderFeed(customerNumber, sortBy));
		
	}
	
	
	
	@Override
	@Deprecated
	public CarOrderHistoryData readOrderDetails(Date businessDayDate, String storeId, Integer transactionIndex) {
	
		return readOrderDetails(businessDayDate, storeId, transactionIndex, null);
	
	}

	@Override
	public CarOrderHistoryData readOrderDetails(Date businessDayDate, String storeId, Integer transactionIndex, String customerNumber)
	{
			// read and extract header data
			CarOrderHistoryData order = getCarOrderHistoryExtractorService().extractOrder(getCarDataProviderService().readHeaderFeed(businessDayDate, storeId, transactionIndex,customerNumber));
			
			// read and extract item data
			getCarOrderHistoryExtractorService().extractOrderEntries(order, getCarDataProviderService().readItemFeed(businessDayDate, storeId, transactionIndex, customerNumber));
			
			// read store location
			order.getStore().setAddress(getCarOrderHistoryExtractorService().extractStoreLocation(getCarDataProviderService().readLocaltionFeed(storeId)));
			
			return order;
	}


	
	
		
		
}
