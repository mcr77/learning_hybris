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
package de.hybris.platform.sap.sapordermgmtservices.order.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.sap.sapordermgmtservices.order.impl.DefaultOrderService;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class DefaultOrderServiceTest
{
	DefaultOrderService classUnderTest = new DefaultOrderService();

	@Test
	public void testCalculateNumberOfPages()
	{
		int totalNumberOfResults = 1;
		int pageSize = 3;
		int numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(1, numberOfPages);

		totalNumberOfResults = 2;
		pageSize = 3;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(1, numberOfPages);

		totalNumberOfResults = 3;
		pageSize = 3;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(1, numberOfPages);

		totalNumberOfResults = 4;
		pageSize = 3;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(2, numberOfPages);

		totalNumberOfResults = 5;
		pageSize = 3;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(2, numberOfPages);

		totalNumberOfResults = 6;
		pageSize = 3;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(2, numberOfPages);

		totalNumberOfResults = 7;
		pageSize = 3;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(3, numberOfPages);

		totalNumberOfResults = 8;
		pageSize = 3;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(3, numberOfPages);

		totalNumberOfResults = 18;
		pageSize = 5;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(4, numberOfPages);

		totalNumberOfResults = 24;
		pageSize = 5;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(5, numberOfPages);

		totalNumberOfResults = 25;
		pageSize = 5;
		numberOfPages = classUnderTest.calculateNumberOfPages(totalNumberOfResults, pageSize);
		assertEquals(5, numberOfPages);
	}

}
