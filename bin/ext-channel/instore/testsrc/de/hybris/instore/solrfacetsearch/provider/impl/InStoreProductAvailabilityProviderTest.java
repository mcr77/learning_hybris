/**
 * 
 */
package de.hybris.instore.solrfacetsearch.provider.impl;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.PickupAvailabilityStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.hybris.instore.strategies.impl.InStoreAvailabilityCalculationStrategy;


/**
 * @author johannesdoberer
 * 
 */
public class InStoreProductAvailabilityProviderTest extends PropertyFieldValueProviderTestBase
{
	@Mock
	private FieldNameProvider fieldNameProvider;
	@Mock
	private CommerceStockService commerceStockService;
	@Mock
	private PickupAvailabilityStrategy pickupAvailabilityStrategy;
	@Mock
	private SessionService sessionService;
	@Mock
	private VariantProductModel model;
	@Mock
	private ProductModel baseProduct;
	@Mock
	private KeywordModel keyword;
	@Mock
	private IndexedProperty indexedProperty;
	@Mock
	private PointOfServiceModel pos;

	private InStoreProductAvailabilityProvider inStoreProductAvailabilityProvider;
	private InStoreAvailabilityCalculationStrategy inStoreAvailabilityCaluculationStrategy;


	@Override
	protected String getPropertyName()
	{
		return null;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new InStoreProductAvailabilityProvider());
		configureBase();
		((InStoreProductAvailabilityProvider) getPropertyFieldValueProvider()).setCommerceStockService(commerceStockService);
		((InStoreProductAvailabilityProvider) getPropertyFieldValueProvider())
				.setPickupAvailabilityStrategy(pickupAvailabilityStrategy);
		((InStoreProductAvailabilityProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);

		given(Boolean.valueOf(indexedProperty.isLocalized())).willReturn(Boolean.FALSE);
		given(model.getBaseProduct()).willReturn(baseProduct);
		given(keyword.getKeyword()).willReturn(getPropertyName());
		given(model.getKeywords()).willReturn(singletonList(keyword));
		given(baseProduct.getKeywords()).willReturn(singletonList(keyword));
	}


	@Before
	public void setUp()
	{
		configure();

		inStoreProductAvailabilityProvider = new InStoreProductAvailabilityProvider();
		inStoreProductAvailabilityProvider.setCommerceStockService(commerceStockService);
		inStoreProductAvailabilityProvider.setPickupAvailabilityStrategy(pickupAvailabilityStrategy);
		inStoreProductAvailabilityProvider.setFieldNameProvider(fieldNameProvider);

		inStoreAvailabilityCaluculationStrategy = new InStoreAvailabilityCalculationStrategy();

		baseProduct = new ProductModel();
		baseProduct.setCode("testProduct");

		final StockLevelModel stock = new StockLevelModel();
		stock.setAvailable(9);
		final Set<StockLevelModel> stocklevels = new HashSet<StockLevelModel>();
		stocklevels.add(stock);
		baseProduct.setStockLevels(stocklevels);

		pos = new PointOfServiceModel();
		final WarehouseModel wareHouseModel = new WarehouseModel();
		wareHouseModel.setStockLevels(stocklevels);
		final List<WarehouseModel> wareHouses = new ArrayList<WarehouseModel>();
		wareHouses.add(wareHouseModel);
		pos.setWarehouses(wareHouses);
	}

	@Test
	public void getProductStockLevelStatusTest()
	{
		Mockito.when(commerceStockService.getStockLevelForProductAndPointOfService(baseProduct, pos)).thenReturn(Long.valueOf(9));
		final StockLevelStatus stockLevelStatus = inStoreProductAvailabilityProvider.getProductStockLevelStatus(baseProduct, pos);
		Assert.assertEquals(StockLevelStatus.INSTOCK, stockLevelStatus);
		Mockito.when(commerceStockService.getStockLevelForProductAndPointOfService(baseProduct, pos)).thenReturn(Long.valueOf(0));
		final StockLevelStatus stockLevelStatusII = inStoreProductAvailabilityProvider.getProductStockLevelStatus(baseProduct, pos);
		Assert.assertEquals(StockLevelStatus.OUTOFSTOCK, stockLevelStatusII);
	}

	@Test
	public void calculateAvailabilityTest()
	{
		final Long availability = inStoreAvailabilityCaluculationStrategy.calculateAvailability(baseProduct.getStockLevels());
		Assert.assertEquals(9, availability.longValue());
	}
}
