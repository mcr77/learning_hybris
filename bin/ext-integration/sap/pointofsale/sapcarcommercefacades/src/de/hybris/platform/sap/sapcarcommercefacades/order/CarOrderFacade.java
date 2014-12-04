/**
 * 
 */
package de.hybris.platform.sap.sapcarcommercefacades.order;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;

import java.util.Date;


/**
 * @author I827395
 * 
 */
public interface CarOrderFacade
{

	/**
	 * Returns the detail of an Order corresponding to unique key (date, store, transaction index).
	 * 
	 * @param transactionDate
	 * @param storeId
	 * @param transactionIndex
	 * @return the order details made in store
	 */
	CarOrderHistoryData getOrderDetails(Date transactionDate, String storeId, Integer transactionIndex);


	/**
	 * Returns the order history of the current user for given customer Id.
	 * 
	 * @param pageableData
	 * @return The order history made in store for current user.
	 */
	SearchPageData<CarOrderHistoryData> getPagedOrderHistoryForCustomer(PageableData pageableData);


}
