/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.commerceservices.stock.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class DefaultCommerceWarehouseDaoTest  extends ServicelayerTransactionalTest
{
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private CommerceStockService commerceStockService;
	@Resource
	private ProductService productService;
	@Resource
	private BaseStoreService baseStoreService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/commerceservices/test/defaultCommerceWarehouseDaoTest.impex", "utf-8");
		final BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID("testSite");
		baseSiteService.setCurrentBaseSite(baseSiteForUID, false);
	}

	@Test
	public void testGetStockLevelStatusForProductAndPointOfService()
	{
		final Map<String,StockLevelStatus> posStockLevelStatusMap = commerceStockService.getPosAndStockLevelStatusForProduct
				(productService.getProductForCode("product1"), baseStoreService.getCurrentBaseStore());
		Assert.assertNotNull(posStockLevelStatusMap);
		Assert.assertEquals(4,posStockLevelStatusMap.size());
		Assert.assertEquals(StockLevelStatus.OUTOFSTOCK,posStockLevelStatusMap.get("Nakano"));
		Assert.assertEquals(StockLevelStatus.INSTOCK,posStockLevelStatusMap.get("Shinbashi"));
	}

}
