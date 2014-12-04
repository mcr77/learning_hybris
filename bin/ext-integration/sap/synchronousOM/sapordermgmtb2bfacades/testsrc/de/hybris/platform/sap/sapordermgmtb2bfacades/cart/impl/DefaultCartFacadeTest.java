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
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultCartFacadeTest
{
	DefaultCartFacade classUnderTest = new DefaultCartFacade();
	private final String code = "A";
	private final long quantity = 1;
	private final CartModificationData cartModificationData = new CartModificationData();
	OrderEntryData newEntry = new OrderEntryData();
	private final String storeId = "Store";
	private CartService cartService;
	private UserService userService;
	private UserService userServiceLoggedInUser;
	private ProductImageHelper productImageHelper;
	private final UserModel userModel = new UserModel();
	private final CartData sessionCart = new CartData();
	private final List<CartModificationData> validateCartData = new ArrayList<>();
	private final long entryNumber = 10;


	@Before
	public void init()
	{
		cartModificationData.setEntry(newEntry);

		cartService = EasyMock.createMock(CartService.class);
		classUnderTest.setCartService(cartService);
		EasyMock.expect(cartService.addToCart(code, quantity)).andReturn(cartModificationData);
		EasyMock.expect(cartService.getSessionCart()).andReturn(sessionCart);
		EasyMock.expect(cartService.getSessionCart(false)).andReturn(sessionCart);
		EasyMock.expect(cartService.hasSessionCart()).andReturn(true);
		EasyMock.expect(cartService.validateCartData()).andReturn(validateCartData);
		EasyMock.expect(cartService.updateCartEntry(entryNumber, quantity)).andReturn(cartModificationData);
		cartService.removeSessionCart();

		productImageHelper = EasyMock.createMock(ProductImageHelper.class);
		classUnderTest.setProductImageHelper(productImageHelper);
		productImageHelper.enrichWithProductImages(newEntry);
		productImageHelper.enrichWithProductImages(sessionCart);

		userService = EasyMock.createMock(UserService.class);
		EasyMock.expect(userService.getCurrentUser()).andReturn(userModel);
		EasyMock.expect(userService.isAnonymousUser(userModel)).andReturn(Boolean.TRUE);
		classUnderTest.setUserService(userService);

		userServiceLoggedInUser = EasyMock.createMock(UserService.class);
		EasyMock.expect(userServiceLoggedInUser.getCurrentUser()).andReturn(userModel);
		EasyMock.expect(userServiceLoggedInUser.isAnonymousUser(userModel)).andReturn(Boolean.FALSE);

		EasyMock.replay(cartService, productImageHelper, userService, userServiceLoggedInUser);
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{
		assertEquals(cartModificationData, classUnderTest.addToCart(code, quantity));
	}

	@Test
	public void testAddToCartWithStore() throws CommerceCartModificationException
	{
		assertEquals(cartModificationData, classUnderTest.addToCart(code, quantity, storeId));
	}

	@Test
	public void testAttribs()
	{
		//assertEquals(cartService, classUnderTest.getCartService());
		assertEquals(productImageHelper, classUnderTest.getProductImageHelper());

	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testEstimateExternalTaxes()
	{
		final String deliveryZipCode = null;
		final String countryIsoCode = null;
		classUnderTest.estimateExternalTaxes(deliveryZipCode, countryIsoCode);
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testUpdateCartIdWithStore() throws CommerceCartModificationException
	{
		final long entryNumber = 1;
		classUnderTest.updateCartEntry(entryNumber, storeId);
	}


	//TODO Check test
	//	@Test
	//	public void testGetSessionCartNoLogin()
	//	{
	//		assertNull(classUnderTest.getSessionCart());
	//	}

	@Test
	public void testGetSessionCart()
	{
		classUnderTest.setUserService(userServiceLoggedInUser);
		assertEquals(sessionCart, classUnderTest.getSessionCart());
	}

	@Test
	public void testGetSessionCartWOrdering()
	{
		classUnderTest.setUserService(userServiceLoggedInUser);
		assertEquals(sessionCart, classUnderTest.getSessionCartWithEntryOrdering(false));
	}

	@Test
	public void testGetMiniCart()
	{
		classUnderTest.setUserService(userServiceLoggedInUser);
		assertEquals(sessionCart, classUnderTest.getMiniCart());
	}

	@Test
	public void testGetCartsForCurrentUserNoLogin()
	{
		assertTrue(classUnderTest.getCartsForCurrentUser().size() == 1);
	}

	@Test
	public void testGetDeliveryCountries()
	{
		assertNull(classUnderTest.getDeliveryCountries());
	}

	@Test
	public void testHasSessionCart()
	{
		assertTrue(classUnderTest.hasSessionCart());
	}

	@Test
	public void testRemoveSessionCart()
	{
		classUnderTest.removeSessionCart();
	}

	@Test
	public void testRestoreAnonymousCartAndTakeOwnership() throws CommerceCartRestorationException
	{
		assertNull(classUnderTest.restoreAnonymousCartAndTakeOwnership(null));
	}

	@Test
	public void testRestoreSavedCart() throws CommerceCartRestorationException
	{
		assertNull(classUnderTest.restoreSavedCart(code));
	}

	@Test
	public void testValidate() throws CommerceCartModificationException
	{
		assertEquals(validateCartData, classUnderTest.validateCartData());
	}

	@Test
	public void testUpdate() throws CommerceCartModificationException
	{
		assertEquals(cartModificationData, classUnderTest.updateCartEntry(entryNumber, quantity));
	}
}
