/**
 * 
 */
package de.hybris.platform.sap.sapcarcommercefacades.order.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcarcommercefacades.order.CarOrderConverter;
import de.hybris.platform.sap.sapcarcommercefacades.order.CarOrderFacade;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.services.CarOrderHistoryService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * @author I827395
 *
 */
public class DefaultCarOrderFacade implements CarOrderFacade
{

	private CarOrderHistoryService carOrderHistoryService;
	private UserService userService;

	private CarOrderConverter carOrderConverter;

	public CarOrderConverter getCarOrderConverter()
	{
		return carOrderConverter;
	}

	public void setCarOrderConverter(final CarOrderConverter carOrderConverter)
	{
		this.carOrderConverter = carOrderConverter;
	}

	public CarOrderHistoryService getCarOrderHistoryService()
	{
		return carOrderHistoryService;
	}

	@Required
	public void setCarOrderHistoryService(final CarOrderHistoryService carOrderHistoryService)
	{
		this.carOrderHistoryService = carOrderHistoryService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	@Override
	public SearchPageData<CarOrderHistoryData> getPagedOrderHistoryForCustomer(final PageableData pageableData)
	{

		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();

		Assert.notNull(currentCustomer.getCustomerID(), "Parameter source cannot be null.");

		String sortBy = null;

		if (!StringUtils.isEmpty(pageableData.getSort()))
		{

			if (pageableData.getSort().equals("byDate"))
			{
				sortBy = "BusinessDayDate desc";
			}

			else if (pageableData.getSort().equals("byOrderNumber"))
			{
				sortBy = "TransactionNumber desc";
			}
		}

		final List<CarOrderHistoryData> orderList = getCarOrderHistoryService().readOrdersForCustomer(
				currentCustomer.getCustomerID(), sortBy);

		// convert orders, calculate totals, format ...
		getCarOrderConverter().convertOrders(orderList);

		final SearchPageData<CarOrderHistoryData> searchPageData = createSearchPageData();

		searchPageData.setPagination(createPagination(pageableData, orderList));

		searchPageData.setSorts(createSorts(pageableData.getSort()));

		final int page = pageableData.getCurrentPage();
		final int pageSize = pageableData.getPageSize();

		searchPageData.setResults(orderList.subList(page * pageSize,
				page * pageSize + Math.min(pageSize, orderList.size() - page * pageSize)));

		return searchPageData;

	}

	protected List<SortData> createSorts(final String selectedSortCode)
	{
		final List result = new ArrayList(2);

		result.add(createSort("byDate", null, selectedSortCode));
		result.add(createSort("byOrderNumber", null, selectedSortCode));
		return result;
	}

	protected SortData createSort(final String code, final String name, final String selectedSortCode)
	{
		final SortData sortData = createSortData();
		sortData.setCode(code);
		sortData.setName(name);
		sortData.setSelected((selectedSortCode != null) && (selectedSortCode.equals(code)));
		return sortData;
	}

	@Override
	public CarOrderHistoryData getOrderDetails(final Date businessDayDate, final String storeId, final Integer transactionIndex)
	{

		Assert.notNull(businessDayDate, "Parameter source cannot be null.");
		Assert.notNull(storeId, "Parameter source cannot be null.");
		Assert.notNull(transactionIndex, "Parameter source cannot be null.");

		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		Assert.notNull(currentCustomer.getCustomerID(), "Customer id cannot be null.");


		final CarOrderHistoryData order = getCarOrderHistoryService().readOrderDetails(businessDayDate, storeId, transactionIndex,
				currentCustomer.getCustomerID());

		if (order == null)
		{
			throw new UnknownIdentifierException("Order with orderGUID " + businessDayDate + "Order with orderGUID "
					+ businessDayDate + "Order with orderGUID " + businessDayDate + " not found for current user in current BaseStore");
		}

		// convert order values
		carOrderConverter.convertOrder(order);

		// convert order entries
		carOrderConverter.convertOrderEntries(order.getOrderEntries());

		return order;
	}



	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	protected <T> SearchPageData<T> createSearchPageData()
	{
		return new SearchPageData();
	}


	protected PaginationData createPaginationData()
	{
		return new PaginationData();
	}

	protected SortData createSortData()
	{
		return new SortData();
	}


	// create pagination data
	protected <T> PaginationData createPagination(final PageableData pageableData, final List<CarOrderHistoryData> orderList)
	{
		final PaginationData paginationData = new PaginationData();
		paginationData.setPageSize(pageableData.getPageSize());
		paginationData.setSort(pageableData.getSort());
		paginationData.setTotalNumberOfResults(orderList.size());

		paginationData.setNumberOfPages((orderList.size() - 1) / pageableData.getPageSize() + 1);
		paginationData.setCurrentPage(Math.max(0, Math.min(paginationData.getNumberOfPages(), pageableData.getCurrentPage())));

		return paginationData;
	}



}
