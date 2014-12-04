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
 */
package com.hybris.datahub.core.services.impl;

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.datahub.core.data.TestProductData;
import com.hybris.datahub.core.rest.client.DefaultDataHubOutboundClient;
import com.hybris.datahub.core.util.OutboundServiceCsvUtils;
import com.hybris.datahub.core.util.OutboundServiceDataGenerationTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDataHubOutboundServiceUnitTest
{
	private final DefaultDataHubOutboundService outboundService = new DefaultDataHubOutboundService();

	private final static String DEFAULT_FEED = "DEFAULT_FEED";
	private final static String DEFAULT_POOL = "GLOBAL";
	private static final String FEED_NAME = "SOME_FEED";
	private final static String POOL_NAME = "SOME_POOL";
	private final static String CANONICAL_TYPE = "TestCanonicalProduct";

	private TestProductData testProduct;
	private Map<String, Object> testProductMap;
	private Map<String, String> primaryKeyMap;

	@Mock
	private DefaultDataHubOutboundClient dataHubOutboundClient;

	@Mock
	private OutboundServiceCsvUtils csvUtils;

	@Before
	public void setup()
	{
		testProduct = OutboundServiceDataGenerationTestUtils.createTestProductData();
		testProductMap = OutboundServiceDataGenerationTestUtils.createUniqueTestProductMap();
		primaryKeyMap = OutboundServiceDataGenerationTestUtils.createUniquePrimaryKeyMap();

		outboundService.setDataHubOutboundClient(dataHubOutboundClient);
		outboundService.setCsvUtils(csvUtils);
	}

	@Test
	public void testSendToDataHub_WithFeedNameAndObject()
	{
		outboundService.sendToDataHub(FEED_NAME, CANONICAL_TYPE, testProduct);

		Mockito.verify(csvUtils).convertObjectToCsv(testProduct);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(FEED_NAME), Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithObject()
	{
		outboundService.sendToDataHub(CANONICAL_TYPE, testProduct);

		Mockito.verify(csvUtils).convertObjectToCsv(testProduct);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(DEFAULT_FEED), Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithFeedNameAndMap()
	{
		outboundService.sendToDataHub(FEED_NAME, CANONICAL_TYPE, testProductMap);

		Mockito.verify(csvUtils).convertMapToCsv(testProductMap);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(FEED_NAME), Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithMap()
	{
		outboundService.sendToDataHub(CANONICAL_TYPE, testProductMap);

		Mockito.verify(csvUtils).convertMapToCsv(testProductMap);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(DEFAULT_FEED), Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithFeedNameAndListOfMaps()
	{
		final List<Map<String, Object>> testList = Arrays.asList(testProductMap);
		outboundService.sendToDataHub(FEED_NAME, CANONICAL_TYPE, testList);

		Mockito.verify(csvUtils).convertListToCsv(testList);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(FEED_NAME), Mockito.eq(CANONICAL_TYPE));
	}

	@Test
	public void testSendToDataHub_WithListOfMaps()
	{
		final List<Map<String, Object>> testList = Arrays.asList(testProductMap);
		outboundService.sendToDataHub(CANONICAL_TYPE, testList);

		Mockito.verify(csvUtils).convertListToCsv(testList);
		Mockito.verify(dataHubOutboundClient).exportData(Mockito.any(String[].class), Mockito.eq(DEFAULT_FEED), Mockito.eq(CANONICAL_TYPE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendToDataHub_WithNullFeedNameAndObject()
	{
		outboundService.sendToDataHub(null, CANONICAL_TYPE, testProduct);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendToDataHub_WithNullFeedNameAndMap()
	{
		outboundService.sendToDataHub(null, CANONICAL_TYPE, testProductMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendToDataHub_WithNullRawTypeAndObject()
	{
		outboundService.sendToDataHub(POOL_NAME, null, testProduct);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendToDataHub_WithNullRawTypeAndMap()
	{
		outboundService.sendToDataHub(POOL_NAME, null, testProductMap);
	}

	@Test
	public void testDeleteItem()
	{
		outboundService.deleteItem(POOL_NAME, CANONICAL_TYPE, primaryKeyMap);
		verify(dataHubOutboundClient).deleteItem(POOL_NAME, CANONICAL_TYPE, primaryKeyMap);
	}

	@Test
	public void testDeleteItemWithDefaultPool()
	{
		outboundService.deleteItem(CANONICAL_TYPE, primaryKeyMap);
		verify(dataHubOutboundClient).deleteItem(DEFAULT_POOL, CANONICAL_TYPE, primaryKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithNullTypeCode()
	{
		outboundService.deleteItem(POOL_NAME, null, primaryKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithBlankTypeCode()
	{
		outboundService.deleteItem(POOL_NAME, " ", primaryKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithNullFeedName()
	{
		outboundService.deleteItem(null, CANONICAL_TYPE, primaryKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithBlankFeedName()
	{
		outboundService.deleteItem(" ", CANONICAL_TYPE, primaryKeyMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithNullPrimaryKeysMap()
	{
		outboundService.deleteItem(POOL_NAME, CANONICAL_TYPE, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteItemWithEmptyPrimaryKeysMap()
	{
		outboundService.deleteItem(POOL_NAME, CANONICAL_TYPE, Maps.<String, String>newHashMap());
	}
}
