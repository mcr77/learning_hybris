/**
 * 
 */
package com.sap.hybris.sapcustomerb2b.inbound;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.DefaultFlexibleSearchService;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
@UnitTest
public class DefaultSAPCustomerAddressConsistencyInterceptorTest
{
	private static final String kunnr = "0815";
	private static final String QUERYRESULTSTRING0815 = "SELECT {pk} from {Address} where {sapCustomerID} =?kunnr AND {duplicate} =?duplicate";
	private String queryString;
	private Map<String, Object> queryParameters;

	@Before
	public void setUp()
	{
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	class MyFlexibleSearchService extends DefaultFlexibleSearchService
	{
		@Override
		public <T> SearchResult<T> search(final FlexibleSearchQuery query)
		{
			queryString = query.getQuery();
			queryParameters = query.getQueryParameters();
			return null;
		}
	}

	@Test
	public void findAddressesBySAPCustomerID()
	{

		final MyFlexibleSearchService flexibleSearchService = new MyFlexibleSearchService();
		final DefaultSAPCustomerAddressConsistencyInterceptor sapCustomerAddressConsistencyInterceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();
		sapCustomerAddressConsistencyInterceptor.setFlexibleSearchService(flexibleSearchService);

		sapCustomerAddressConsistencyInterceptor.findAddressesBySAPCustomerID(kunnr, Boolean.FALSE);

		Assert.assertEquals(QUERYRESULTSTRING0815, queryString);
		Assert.assertEquals(kunnr, queryParameters.get("kunnr"));
		Assert.assertEquals(Boolean.FALSE, queryParameters.get("duplicate"));

		sapCustomerAddressConsistencyInterceptor.findAddressesBySAPCustomerID(kunnr, Boolean.TRUE);
		Assert.assertEquals(Boolean.TRUE, queryParameters.get("duplicate"));

	}

}
