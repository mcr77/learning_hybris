package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.io.InputStream;
import java.util.Date;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

public class DefaultCarDataProviderServiceMockData extends
		DefaultCarDataProviderService {

		// reads Header data
		@Override
		public ODataFeed readHeaderFeed(String customerNumber, String sortBy) {
	
			ODataFeed feed;
	
			try {
	
				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/posServicesMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);
	
				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();
	
				InputStream content = getClass().getResourceAsStream(
						"/test/POSSalesHeader.json");
	
				feed = EntityProvider.readFeed(APPLICATION_JSON,
						entityContainer.getEntitySet("POSSalesQueryResults"),
						content, EntityProviderReadProperties.init().build());
	
			} catch (Exception e) {
				throw new RuntimeException("Error while reading Header Feed");
			}
	
			return feed;
		}
		
		
		@Override
		public ODataFeed readHeaderFeed(Date businessDayDate, String storeId,
				Integer transactionIndex){

			ODataFeed feed;
			
			try {
	
				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/posServicesMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);
	
				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();
	
				InputStream content = getClass().getResourceAsStream(
						"/test/POSSalesHeaderWithKey.json");
	
				feed = EntityProvider.readFeed(APPLICATION_JSON,
						entityContainer.getEntitySet("POSSalesQueryResults"),
						content, EntityProviderReadProperties.init().build());
	
			} catch (Exception e) {
				throw new RuntimeException("Error while reading Header Feed");
			}
	
			return feed;
		}
		
		// reads Header data
		@Override
		public ODataFeed readItemFeed(Date businessDayDate, String storeId, Integer transactionIndex){

			ODataFeed feed;

			try {

				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/posServicesMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);

				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();

				InputStream content = getClass().getResourceAsStream(
						"/test/SalesQueryItem.json");
				feed = EntityProvider.readFeed(APPLICATION_JSON,
						entityContainer.getEntitySet("POSSalesQueryResults"),
						content, EntityProviderReadProperties.init().build());
			} catch (Exception e) {
				throw new RuntimeException("Error while reading Header Feed");
			}

			return feed;
		}

		@Override
		public ODataFeed readLocaltionFeed(String location) {

			ODataFeed feed;

			try {

				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/posServicesMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);

				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();

				InputStream content = getClass().getResourceAsStream(
						"/test/storeLocation.json");

				feed = EntityProvider.readFeed(APPLICATION_JSON,
						entityContainer.getEntitySet("RetailLocationQueryResults"),
						content, EntityProviderReadProperties.init().build());

			} catch (Exception e) {
				throw new RuntimeException("Error while reading Header Feed");
			}

			return feed;
		}

		
		
	
}
