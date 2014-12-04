package de.hybris.platform.sap.sapcarintegration.services;

import java.util.List;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreAddress;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreData;

public interface CarOrderHistoryExtractorService {

	
	public static final String ORDERCHANNELNAME = "orderChannelName";
	
	
	/**
	 * 
	 * @param feed
	 * @return
	 */
	abstract List<CarOrderHistoryData> extractOrders(final ODataFeed feed);
	
	/**
	 * extract the address information from store location
	 * @param feed
	 * @return
	 */
	abstract CarStoreAddress extractStoreLocation(final ODataFeed feed);

	/**
	 * 
	 * @param feed
	 * @return
	 */
	abstract CarOrderHistoryData extractOrder(ODataFeed feed);
}
