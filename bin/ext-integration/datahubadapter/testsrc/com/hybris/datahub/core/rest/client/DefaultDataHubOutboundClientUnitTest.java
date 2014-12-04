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
package com.hybris.datahub.core.rest.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.datahub.core.util.OutboundServiceDataGenerationTestUtils;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.sun.jersey.api.client.ClientResponse;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDataHubOutboundClientUnitTest
{
	private static final String POOL_NAME = "some_pool";
	private static final String CANONICAL_TYPE = "TestCanonicalProduct";
	private static final String DATA_HUB_URL = "http://localhost:9797/datahub-webapp";
	private static final Map<String, String> KEY_FIELDS = OutboundServiceDataGenerationTestUtils.createUniquePrimaryKeyMap();

	@Spy
	DefaultDataHubOutboundClient client = new DefaultDataHubOutboundClient();

	@Before
	public void setup()
	{
		client.setDataHubUrl(DATA_HUB_URL);
	}

	@Test
	public void testSuccessfulDelete()
	{
		final ClientResponse response = new ClientResponse(Response.Status.NO_CONTENT.getStatusCode(), null, null, null);
		when(client.sendDeleteRequest(any(String.class), any(String.class), any(Map.class))).thenReturn(response);
		assertTrue(client.deleteItem(POOL_NAME, CANONICAL_TYPE, KEY_FIELDS));
	}

	@Test
	public void testFailedDelete()
	{
		final ClientResponse response = new ClientResponse(Response.Status.BAD_REQUEST.getStatusCode(), null, null, null);
		when(client.sendDeleteRequest(any(String.class), any(String.class), any(Map.class))).thenReturn(response);
		assertFalse(client.deleteItem(POOL_NAME, CANONICAL_TYPE, KEY_FIELDS));
	}

	@Test(expected = IllegalStateException.class)
	public void testNonExpectedReponseDuringDelete()
	{
		final ClientResponse response = new ClientResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), null, null, null);
		when(client.sendDeleteRequest(any(String.class), any(String.class), any(Map.class))).thenReturn(response);
		assertFalse(client.deleteItem(POOL_NAME, CANONICAL_TYPE, KEY_FIELDS));
	}
}
