package de.hybris.platform.sap.sapcarintegration.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

public interface CarDataProviderService {

	/**
	 * read all orders for a given customer
	 * @param customerNumber
	 * @return
	 */
	ODataFeed readHeaderFeed(String customerNumber, String sortBy);
	
	/**
	 * read order header date which corresponds to composed order key (businessDayDate,storeId,transactionIndex)
	 * @param businessDayDate
	 * @param storeId
	 * @param transactionIndex
	 * @return
	 * @throws IOException
	 * @throws ODataException
	 * @throws URISyntaxException
	 */
	@Deprecated
	ODataFeed readHeaderFeed(Date businessDayDate, String storeId, Integer transactionIndex);
	
	/**
	 * read point of sales order header for a given transaction, use pos order key (businessDayDate,storeId,transactionIndex)
	 * @param businessDayDate
	 * @param storeId
	 * @param transactionIndex
	 * @param customerNumber
	 * @return ODataFeed
	 */
	ODataFeed readHeaderFeed(Date businessDayDate, String storeId, Integer transactionIndex, String customerNumber);
	
	/**
	 * read all items within a given order
	 * @param businessDayDate
	 * @param storeId
	 * @param transactionIndex
	 * @return
	 */
	@Deprecated
	ODataFeed readItemFeed(Date businessDayDate, String storeId, Integer transactionIndex);
	
	/**
	 * read point of sales transaction items for a given transaction
	 * @param businessDayDate
	 * @param storeId
	 * @param transactionIndex
	 * @return ODataFeed
	 */
	ODataFeed readItemFeed(Date businessDayDate, String storeId, Integer transactionIndex, String customerNumber);

	/**
	 * read store location information
	 * @param location
	 * @return
	 * @throws IOException
	 * @throws ODataException
	 * @throws URISyntaxException
	 */
	ODataFeed readLocaltionFeed(String location);

	
}
