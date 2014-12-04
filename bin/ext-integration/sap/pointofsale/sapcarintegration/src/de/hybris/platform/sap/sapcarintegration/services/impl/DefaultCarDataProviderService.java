package de.hybris.platform.sap.sapcarintegration.services.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;
import de.hybris.platform.sap.sapcarintegration.services.CarConnectionService;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;
import de.hybris.platform.sap.sapcarintegration.utils.DateUtil;


/**
 *
 */
public class DefaultCarDataProviderService implements CarDataProviderService
{

	private static final String SEPARATOR = "/";

	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	
	private String POSSALES_ENTITYSETNAME_QUERY = "POSSalesQuery";
	
	private String POSSALES_ENTITYSETNAME_RESULT = "POSSalesQueryResults";
		
	private String LOCATION_ENTITYSETNAME_QUERY = "RetailLocationQuery";
	
	private String LOCATION_ENTITYSETNAME_RESULT = "RetailLocationQueryResults";
	
	private CarConfigurationService carConfigurationService;
	
	

	public CarConfigurationService getCarConfigurationService() {
		return carConfigurationService;
	}

	@Required
	public void setCarConfigurationService(
			CarConfigurationService carConfigurationService) {
		this.carConfigurationService = carConfigurationService;
	}

	private static final String SELECT_POSSALES_ITEM = "Article,ArticleName,SalesUnit,SalesQuantityInSalesUnit,TransactionNumber,RetailStoreID,OperatorID,OrderChannel,OrderChannelName,Location,LocationName,"
			+ "RetailStoreID,BusinessDayDate,TransactionIndex,TransactionCurrency,CustomerNumber,"
			+ "SalesUnit,SalesAmount,TaxExcludedAmount,DistributedHeaderTaxAmount";
	
	
	private static final String SELECT_LOCATION = "HouseNumber,Location,StreetName,POBox,PostalCode,CityName,Country";
	
	private static final String SELECT_POSSALES_HEADER = "TransactionNumber,RetailStoreID,OperatorID,OrderChannel,OrderChannelName,Location,LocationName,"
			+ "RetailStoreID,BusinessDayDate,TransactionIndex,TransactionCurrency,CustomerNumber,"
			+ "SalesUnit,SalesAmount,TaxExcludedAmount,DistributedHeaderTaxAmount";
	
	
	private CarConnectionService connectionService;
	
	
	

	public CarConnectionService getConnectionService() {
		return connectionService;
	}

	@Required
	public void setConnectionService(CarConnectionService connectionService) {
		this.connectionService = connectionService;
	}

	private String getSapClient() {
		return getCarConfigurationService().getSapClient();
	}
	
	private String getRootUrl() {
		return getCarConfigurationService().getRootUrl();
	}
	
	private String getServiceName(){
		
		return getCarConfigurationService().getServiceName();
	}
	
	private InputStream execute(final String relativeUri, final String contentType, final String httpMethod) throws IOException,
			MalformedURLException
	{
		final HttpURLConnection connection = connectionService.createConnection(relativeUri, contentType, httpMethod);
		
		final InputStream content = connection.getInputStream();
		
		return content;
	}

	

	// reads Header data
	@Override
	public ODataFeed readHeaderFeed(String customerNumber, String sortBy){

		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");

		return readFeed(getCarConfigurationService().getRootUrl() + getServiceName(), 
						APPLICATION_JSON,
						POSSALES_ENTITYSETNAME_QUERY, 
						POSSALES_ENTITYSETNAME_RESULT,
						SELECT_POSSALES_HEADER, 
						queryFilter.toString(), 
						null,
						sortBy);
	}
	
	
	@Deprecated
	public ODataFeed readHeaderFeed(Date businessDayDate, String storeId,
			Integer transactionIndex){
		return readHeaderFeed(businessDayDate, storeId, transactionIndex, null);
	}
	
	
	
	// reads Header data
	@Deprecated
	public ODataFeed readItemFeed(Date businessDayDate, String storeId, Integer transactionIndex){
		return readItemFeed(businessDayDate, storeId, transactionIndex, null);
	}

	@Override
	public ODataFeed readLocaltionFeed(String location) {

		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("Location").append(" eq ").append("'").append(location).append("'");
		
		return readFeed(getRootUrl() + getServiceName(), 
						APPLICATION_JSON,
						LOCATION_ENTITYSETNAME_QUERY, 
						LOCATION_ENTITYSETNAME_RESULT,
						SELECT_LOCATION, 
						queryFilter.toString(), 
						null, 
						null);
	}
	
	private String convertToInternalKey(final String id)
	{
		final Integer in = Integer.valueOf(id);
		final String intKey = String.format("%010d", in);
		return intKey;
	}

		
	private ODataFeed readFeed(final String serviceUri, final String contentType, final String entitySetQueryName,final String entityResultSetName, final String select,
			final String filter, final String expand, final String sortBy)
	{

		final Edm edm = this.readEdm(serviceUri);

		
		try
		{
			final EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
			final String absoluteUri = createUri(serviceUri, entitySetQueryName, null, expand, select, filter, sortBy);
			InputStream content = null;
			content = execute(absoluteUri, contentType, HTTP_METHOD_GET);
			
			return EntityProvider.readFeed(contentType, entityContainer.getEntitySet(entityResultSetName), content,
					EntityProviderReadProperties.init().build());
		}
		catch (final Exception e)
		{
			throw new RuntimeException("error while reading feed url : " + serviceUri  + e);
		}
		
		
	}
	

	private Edm readEdm(final String serviceUrl)
	{
		try {
			
			final InputStream content = execute(serviceUrl + SEPARATOR + "$metadata", APPLICATION_XML, HTTP_METHOD_GET);
			return EntityProvider.readMetadata(content, false);
			
		} catch (Exception e) {
			throw new RuntimeException("Error while reading service url : " + serviceUrl, e);
		}
		
	}

	private String createUri(final String serviceUri, final String entitySetName, final String id, final String expandRelationName,
			final String select, final String filter, String sortBy)
	{
		UriBuilder uriBuilder = null;
		if (id == null)
		{
			uriBuilder = UriBuilder.serviceUri(serviceUri, entitySetName, getSapClient());
		}
		else
		{
			uriBuilder = UriBuilder.serviceUri(serviceUri, entitySetName, id, getSapClient());
		}

		if (!StringUtils.isEmpty(expandRelationName)) {
			uriBuilder = uriBuilder.addQuery("$expand", expandRelationName);
		}
		
		uriBuilder = uriBuilder.addQuery("$select", select);
		uriBuilder = uriBuilder.addQuery("$filter", filter);
		
		if (!StringUtils.isEmpty(sortBy)) {
			uriBuilder = uriBuilder.addQuery("$orderby", sortBy);
		}

		String absoluteURI;
		try {
			absoluteURI = new URI(null, uriBuilder.build(), null).toASCIIString();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error while building url : " + serviceUri, e);
		}

		return absoluteURI;
	}

	private static class UriBuilder
	{
		private StringBuilder uri;
		private StringBuilder query;

		private UriBuilder(final String serviceUri, final String entitySetName, final String sapClient)
		{
			uri = new StringBuilder(serviceUri).append(SEPARATOR).append(entitySetName);
			if (!StringUtils.isEmpty(sapClient)) {
				uri = new StringBuilder(uri.append("(P_SAPClient='").append(sapClient).append("')"));
			}
			query = new StringBuilder();
		}

		public static UriBuilder serviceUri(final String serviceUri, final String entitySetName, final String id, String sapClient)
		{
			final UriBuilder b = new UriBuilder(serviceUri, entitySetName, sapClient);
			return b.id(id);
		}

		public static UriBuilder serviceUri(final String serviceUri, final String entitySetName, String sapClient)
		{
			return new UriBuilder(serviceUri, entitySetName, sapClient);
		}

		private UriBuilder id(final String id)
		{
			if (id == null)
			{
				throw new IllegalArgumentException("Null is not an allowed id");
			}
			uri.append("(").append(id).append(")");
			return this;
		}

		public UriBuilder addQuery(final String queryParameter, final String value)
		{
			if (value != null)
			{
				if (query.length() == 0)
				{
					query.append("/Results?");
				}
				else
				{
					query.append("&");
				}
				query.append(queryParameter).append("=").append(value);
			}
			return this;
		}

		public String build()
		{
			return uri.toString() + query.toString();
		}
	}

	@Override
	public ODataFeed readHeaderFeed(Date businessDayDate, String storeId, Integer transactionIndex, String customerNumber)
	{
		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("BusinessDayDate").append(" eq ").append("'").append(DateUtil.formatDate(businessDayDate)).append("'");
		queryFilter.append(" and ").append("RetailStoreID").append(" eq ").append("'").append(storeId).append("'");
		queryFilter.append(" and ").append("TransactionIndex").append(" eq ").append(transactionIndex);
		if (customerNumber != null)
		{
			queryFilter.append(" and ").append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");
		}

		return readFeed(getRootUrl() + getServiceName(), APPLICATION_JSON, POSSALES_ENTITYSETNAME_QUERY,
				POSSALES_ENTITYSETNAME_RESULT, SELECT_POSSALES_HEADER, queryFilter.toString(), null, null);
	}

	@Override
	public ODataFeed readItemFeed(Date businessDayDate, String storeId, Integer transactionIndex, String customerNumber)
	{

		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("BusinessDayDate").append(" eq ").append("'").append(DateUtil.formatDate(businessDayDate)).append("'");
		queryFilter.append(" and ").append("RetailStoreID").append(" eq ").append("'").append(storeId).append("'");
		queryFilter.append(" and ").append("TransactionIndex").append(" eq ").append(transactionIndex);
		if (customerNumber != null)
		{
			queryFilter.append(" and ").append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");
		}

		return readFeed(getRootUrl() + getServiceName(), 
						APPLICATION_JSON,
						POSSALES_ENTITYSETNAME_QUERY, 
						POSSALES_ENTITYSETNAME_RESULT,
						SELECT_POSSALES_ITEM, 
						queryFilter.toString(), 
						null,
						null);
	}

}

