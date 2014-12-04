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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class SalesTransactionsUtilImplTest extends SapordermanagmentBolSpringJunitTest
{

	private static final String PRODUCT_ID = "HT-1010";
	private static final BigDecimal QUANTITY = BigDecimal.TEN;
	private static final String PRODUCT_KEY = "01234556888";
	private static final String UNIT = "ST";

	private SalesTransactionsUtilImpl classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		classUnderTest = (SalesTransactionsUtilImpl) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SALES_TRANSACTIONS_UTIL);
	}

	private Item prepareItem()
	{
		final Item item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		item.setProductId(PRODUCT_ID);
		item.setQuantity(QUANTITY);
		item.setProductGuid(new TechKey(PRODUCT_KEY));
		item.setUnit(UNIT);
		return item;
	}

	@Test
	public void testRemoveLeadingZeros()
	{

		Assert.assertEquals("1", classUnderTest.removeLeadingZeros("0001"));

	}



}
