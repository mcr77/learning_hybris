package de.hybris.platform.sap.sapcarintegration.services.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;


@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultCarOrderHistoryServiceTest extends SapcoreSpringJUnitTest{

	
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	
	@Resource
	private DefaultCarOrderHistoryService carOrderHistoryService;
	
	
	
	@Test
	public void testReadOrdersMockedData() throws Exception{
		
		String customerNumber = "0000001000";
		
		long start = System.currentTimeMillis();
		
		Collection<CarOrderHistoryData> orders = carOrderHistoryService.readOrdersForCustomer(customerNumber, null);
		
		long end = System.currentTimeMillis();
		
		// total time spent to query data 
		System.out.println("Total time to query " + orders.size() + " orders is : " + (end - start));
		
		assertNotNull(orders);
		
		
	}
	
	
}
