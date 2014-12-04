package de.hybris.platform.sap.sapcarintegration.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreAddress;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;

import java.util.List;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultCarOrderHistoryExtractorServiceTest extends SapcoreSpringJUnitTest{


	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";

	@Resource(name="carOrderHistoryExtractorService")
	private DefaultCarOrderHistoryExtractorService extractorService;

	@Resource
	private CarDataProviderService carDataProviderService;


	@Test
	public void testExtractOrders() throws Exception{

		final String customerNumber  = "0000001000";

		final ODataFeed feed = carDataProviderService.readHeaderFeed(customerNumber, null);


		final List<CarOrderHistoryData> orders = extractorService.extractOrders(feed);

		assertNotNull(orders);

	}

	@Test
	public void testExtractOrder() throws Exception{

		final String customerNumber  = "0000001000";

		final ODataFeed ordersFeed = carDataProviderService.readHeaderFeed(customerNumber, null);


		final List<CarOrderHistoryData> orders = extractorService.extractOrders(ordersFeed);


		final CarOrderHistoryData order = orders.iterator().next();

		final ODataFeed orderFeed = carDataProviderService.readHeaderFeed(order.getBusinessDayDate(), order.getStore().getStoreId(), order.getTransactionIndex());


		final CarOrderHistoryData orderResult = extractorService.extractOrder(orderFeed);

		assertNotNull(orderResult);

	}


	@Test
	public void testExtractLocation() throws Exception{

		final String locationName  = "R101";

		final ODataFeed feed = carDataProviderService.readLocaltionFeed(locationName);

		final CarStoreAddress address = extractorService.extractStoreLocation(feed);

		assertNotNull(address);

	}

	@Test
	public void testExtractOrderEntries() throws Exception{

		final String customerNumber  = "0000001000";

		final long start = System.currentTimeMillis();

		final ODataFeed headerFeed = carDataProviderService.readHeaderFeed(customerNumber, null);


		final List<CarOrderHistoryData> orders = extractorService.extractOrders(headerFeed);


		System.out.println("total orders : " + orders.size());

		assertNotNull(orders);

		final CarOrderHistoryData order = orders.iterator().next();

		// fetch store information

		final ODataFeed feed = carDataProviderService.readLocaltionFeed(order.getStore().getStoreId());

		final CarStoreAddress address = extractorService.extractStoreLocation(feed);

		assertNotNull(address);

		order.getStore().setAddress(address);

		final ODataFeed itemFeed = carDataProviderService.readItemFeed(order.getBusinessDayDate(), order.getStore().getStoreId(), order.getTransactionIndex());
		// read order entries
		extractorService.extractOrderEntries(order, itemFeed);

		assertTrue(order.getOrderEntries().size() > 0);

		final long end = System.currentTimeMillis();

		System.out.println("total time : " + (end - start) + " ms");
	}



}
