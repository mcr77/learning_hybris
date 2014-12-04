package de.hybris.platform.sap.sapcarintegration.services.impl;


import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;

import com.meterware.httpunit.HttpUnitOptions;

import de.hybris.platform.sap.core.configuration.jalo.SAPConfiguration;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;
import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;
import de.hybris.platform.sap.sapcarintegration.services.CarConnectionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.store.services.impl.DefaultBaseStoreService;



/**
 * Test for configuration provider.
 */
@ContextConfiguration(locations =
{"classpath:test/sapcarintegration-test-spring.xml"})
public class DefaultConnectionServiceTest extends SapcoreSpringJUnitTest
{
	
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String APPLICATION_JSON = "application/json";
	private String serviceUrl = "/sap/is/retail/car/int/odata/CARServices.xsodata";
	private String rootUrl = "http://ldcird1.wdf.sap.corp:8002";
	
	
	
	@Resource
	DefaultCarConnectionService carConnectionService;
	
	@Resource
	CarConfigurationService carConfigurationService;
	

	
	

	/**
	 * The name of the destination.
	 */
	private static final String destinationName = "carHttpDestinationName";

	@Test
	public void testConnection() throws URISyntaxException, MalformedURLException, IOException {

				
		HttpURLConnection connection = carConnectionService.createConnection(carConfigurationService.getRootUrl() + carConfigurationService.getServiceName(), APPLICATION_JSON, HTTP_METHOD_GET);
		assertNotNull(connection);
		
	}

}

