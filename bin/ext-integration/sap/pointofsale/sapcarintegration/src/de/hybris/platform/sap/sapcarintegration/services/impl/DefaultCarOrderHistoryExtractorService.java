package de.hybris.platform.sap.sapcarintegration.services.impl;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderEntryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreAddress;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreData;
import de.hybris.platform.sap.sapcarintegration.services.CarOrderHistoryExtractorService;
import de.hybris.platform.sap.sapcarintegration.utils.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;


public class DefaultCarOrderHistoryExtractorService implements CarOrderHistoryExtractorService
{


	@Override
	public List<CarOrderHistoryData> extractOrders(final ODataFeed feed)
	{


		final List<CarOrderHistoryData> orders = new ArrayList<CarOrderHistoryData>();

		final List<ODataEntry> entries = feed.getEntries();

		if (entries != null)
		{

			for (final Iterator<ODataEntry> iterator = entries.iterator(); iterator.hasNext();)
			{

				final ODataEntry oDataEntry = iterator.next();

				orders.add(extractOrder(oDataEntry));

			}

		}

		return orders;

	}

	@Override
	public CarOrderHistoryData extractOrder(final ODataFeed feed)
	{

		final List<ODataEntry> entries = feed.getEntries();

		CarOrderHistoryData order = null;

		if (entries != null)
		{

			order = extractOrder(feed.getEntries().iterator().next());

		}

		return order;

	}


	protected CarOrderHistoryData extractOrder(final ODataEntry entry)
	{


		final CarOrderHistoryData order = new CarOrderHistoryData();

		final Map<String, Object> props = entry.getProperties();

		if (props != null)
		{
			// store name
			order.setOrderChannelName(props.get("orderChannelName") != null ? props.get("orderChannelName").toString() : "");
			order.setPurchaseOrderNumber(props.get("TransactionNumber") != null ? props.get("TransactionNumber").toString() : "");
			order.setBusinessDayDate(DateUtil.formatDate(props.get("BusinessDayDate") != null ? props.get("BusinessDayDate")
					.toString() : ""));
			order.setFormattedBusinessDayDate(props.get("BusinessDayDate") != null ? props.get("BusinessDayDate").toString() : "");
			order.setTransactionIndex(props.get("TransactionIndex") != null ? new Integer(props.get("TransactionIndex").toString())
					: new Integer(0));
			order.setOperatorId(props.get("OperatorID") != null ? props.get("OperatorID").toString() : "");

			order.setPurchaseOrderNumber(props.get("TransactionNumber") != null ? props.get("TransactionNumber").toString() : "");
			order.setOrderChannelName(props.get("OrderChannelName") != null ? props.get("OrderChannelName").toString() : "");
			// store data
			final CarStoreData storeData = new CarStoreData();
			storeData.setStoreId(props.get("RetailStoreID") != null ? props.get("RetailStoreID").toString() : "");
			storeData.setStoreName(props.get("LocationName") != null ? props.get("LocationName").toString() : "");
			order.setStore(storeData);
			// add price data
			order.setSubTotal(extractSubTotal(props));

			// add tax data
			order.setTotalTax(extractTotalTax(props));

			// add tax data
			order.setTotalPriceWithTax(extractTotalPriceWithTax(props));

		}


		return order;

	}

	@Override
	public CarStoreAddress extractStoreLocation(final ODataFeed feed)
	{


		final List<ODataEntry> foundEntries = feed.getEntries();

		final ODataEntry entry = foundEntries.iterator().next();

		final Map<String, Object> props = entry.getProperties();

		final CarStoreAddress storeAddress = new CarStoreAddress();

		storeAddress.setHouseNumber(props.get("HouseNumber") != null ? props.get("HouseNumber").toString() : "");
		storeAddress.setStreet(props.get("StreetName") != null ? props.get("StreetName").toString() : "");
		storeAddress.setPoBox(props.get("POBox") != null ? props.get("POBox").toString() : "");
		storeAddress.setZip(props.get("PostalCode") != null ? props.get("PostalCode").toString() : "");
		storeAddress.setCity(props.get("CityName") != null ? props.get("CityName").toString() : "");
		storeAddress.setCountryCode(props.get("Country") != null ? props.get("Country").toString() : "");

		return storeAddress;
	}

	/**
	 * extract entries and append them to the order
	 * 
	 * @param entry
	 * @return
	 */
	public void extractOrderEntries(final CarOrderHistoryData order, final ODataFeed feed)
	{

		final List<CarOrderEntryData> orderEntries = new ArrayList<CarOrderEntryData>();

		final List<ODataEntry> entries = feed.getEntries();

		int entryNumber = 0;

		if (entries != null)
		{

			for (final Iterator<ODataEntry> iterator = entries.iterator(); iterator.hasNext();)
			{

				entryNumber++;
				final ODataEntry oDataEntry = iterator.next();

				final CarOrderEntryData orderEntry = extractOrderEntry(oDataEntry, entryNumber);

				if (orderEntry != null)
				{
					orderEntries.add(orderEntry);
				}


			}

		}

		order.setOrderEntries(orderEntries);

	}


	protected CarOrderEntryData extractOrderEntry(final ODataEntry odataEntry, final int entryNumber)
	{


		final Map<String, Object> props = odataEntry.getProperties();

		if (props != null)
		{

			final CarOrderEntryData orderEntry = new CarOrderEntryData();
			final BigDecimal quantity = props.get("SalesQuantityInSalesUnit") != null ? new BigDecimal(props.get(
					"SalesQuantityInSalesUnit").toString()) : BigDecimal.ONE;
			orderEntry.setQuantity(quantity);
			orderEntry.setEntryNumber(entryNumber);

			// price data
			final PriceData totalPriceData = extractSubTotal(props);
			orderEntry.setTotalPrice(totalPriceData);
			// use total price and quantity to calculate base price
			final PriceData basePriceData = new PriceData();
			basePriceData.setValue(totalPriceData.getValue().divide(quantity, 2, RoundingMode.HALF_UP));
			basePriceData.setCurrencyIso(totalPriceData.getCurrencyIso());
			orderEntry.setBasePrice(basePriceData);

			// extract product data
			final ProductData productData = new ProductData();
			productData.setCode(props.get("Article") != null ? props.get("Article").toString() : "");
			productData.setName(props.get("ArticleName") != null ? props.get("ArticleName").toString() : "");
			orderEntry.setProduct(productData);

			return orderEntry;

		}

		return null;


	}


	protected PriceData extractSubTotal(final Map<String, Object> props)
	{
		final PriceData priceData = new PriceData();
		final BigDecimal salesAmount = new BigDecimal(props.get("SalesAmount") != null ? props.get("SalesAmount").toString() : "0");

		priceData.setValue(salesAmount);

		// set currency
		priceData.setCurrencyIso(props.get("TransactionCurrency") != null ? props.get("TransactionCurrency").toString() : "");

		return priceData;

	}

	protected PriceData extractTotalTax(final Map<String, Object> props)
	{
		final PriceData priceData = new PriceData();
		final BigDecimal taxExcludedAmount = new BigDecimal(props.get("TaxExcludedAmount") != null ? props.get("TaxExcludedAmount")
				.toString() : "0");
		final BigDecimal distributedHeaderTaxAmount = new BigDecimal(props.get("DistributedHeaderTaxAmount") != null ? props.get(
				"DistributedHeaderTaxAmount").toString() : "0");

		final BigDecimal total = taxExcludedAmount.add(distributedHeaderTaxAmount);

		priceData.setValue(total);

		// set currency
		priceData.setCurrencyIso(props.get("TransactionCurrency") != null ? props.get("TransactionCurrency").toString() : "");

		return priceData;

	}

	protected PriceData extractTotalPriceWithTax(final Map<String, Object> props)
	{
		final PriceData priceData = new PriceData();
		final BigDecimal salesAmount = new BigDecimal(props.get("SalesAmount") != null ? props.get("SalesAmount").toString() : "0");
		final BigDecimal taxExcludedAmount = new BigDecimal(props.get("TaxExcludedAmount") != null ? props.get("TaxExcludedAmount")
				.toString() : "0");
		final BigDecimal distributedHeaderTaxAmount = new BigDecimal(props.get("DistributedHeaderTaxAmount") != null ? props.get(
				"DistributedHeaderTaxAmount").toString() : "0");

		final BigDecimal total = salesAmount.add(taxExcludedAmount).add(distributedHeaderTaxAmount);

		priceData.setValue(total);

		// set currency
		priceData.setCurrencyIso(props.get("TransactionCurrency") != null ? props.get("TransactionCurrency").toString() : "");

		return priceData;

	}

}
