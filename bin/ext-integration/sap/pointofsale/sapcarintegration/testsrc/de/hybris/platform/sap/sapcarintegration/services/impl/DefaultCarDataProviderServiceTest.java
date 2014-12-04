package de.hybris.platform.sap.sapcarintegration.services.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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


@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultCarDataProviderServiceTest extends SapcoreSpringJUnitTest{

	
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	
	@Resource
	private DefaultCarDataProviderService carDataProviderService;
	
	@Test
	public void testReadOrders() throws IOException, ODataException, URISyntaxException {
		
		String customerNumber  = "0000001000";
		
		ODataFeed feed = carDataProviderService.readHeaderFeed(customerNumber, null);
		
		final List<ODataEntry> foundEntries = feed.getEntries();
		
		if (foundEntries != null)
		{
			final Iterator<ODataEntry> iter = foundEntries.iterator();

			while (iter.hasNext())
			{
				final ODataEntry entity = iter.next();
				
				// extract entry here
				
				final Map<String, Object> props = entity.getProperties();
				if (props != null)
				{
					
					System.out.println(props.get("RetailStoreID").toString());
					
				}
			}
		}
		
		
		assertNotNull(carDataProviderService.readHeaderFeed(customerNumber, null));
		
		
	}
	
	
	@Test
	public void testReadLocation() throws IOException, ODataException, URISyntaxException {
		
		String location  = "R100";
		
		ODataFeed feed = carDataProviderService.readLocaltionFeed(location);
		
		assertNotNull(feed);
		
		final List<ODataEntry> foundEntries = feed.getEntries();
		
		assertNotNull(foundEntries.iterator().next());
		
	}
	
	@Test
	public void testReadItems() throws IOException, ODataException, URISyntaxException, ParseException {
		
		
		Date businessDaydate = new SimpleDateFormat("yyyymmdd").parse("20120101");
		String storeId = "R100";
		int transactionIndex = 2;
		
		ODataFeed feed = carDataProviderService.readItemFeed(businessDaydate, storeId, transactionIndex);
		
		assertNotNull(feed);
		
		final List<ODataEntry> foundEntries = feed.getEntries();
		
		assertNotNull(foundEntries.iterator().next());
		
	}
	
	
	
	
	
	
}
