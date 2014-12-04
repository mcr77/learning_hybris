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

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.exception.DomainException;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;




/**
 * Default cart facade, serving the cart in the context of SAP synchronous order management. The cart does not touch the
 * hybris persistence, but is stored in the back end session after the first addToCart interaction.<br>
 * Pricing, availability etc. are all fetched from the SAP back end representation of the order.
 */
public class DefaultCartFacade implements CartFacade, de.hybris.platform.commercefacades.order.CartFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCartFacade.class);
	private CartService cartService;
	private ProductImageHelper productImageHelper;
	private UserService userService;


	private static final String CART_MODIFICATION_ERROR = "basket.error.occurred";



	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getSessionCart()
	 */
	@Override
	public CartData getSessionCart()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSessionCart");
		}
		if (!isUserLoggedOn())
		{
			return new CartData();
		}
		final CartData sessionCart = cartService.getSessionCart();
		productImageHelper.enrichWithProductImages(sessionCart);
		return sessionCart;
	}

	/**
	 * 
	 */
	private boolean isUserLoggedOn()
	{
		final UserModel userModel = userService.getCurrentUser();
		return !userService.isAnonymousUser(userModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getSessionCartWithEntryOrdering(boolean)
	 */
	@Override
	public CartData getSessionCartWithEntryOrdering(final boolean recentlyAddedFirst)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSessionCartWithEntryOrdering called with: " + recentlyAddedFirst);
		}
		return cartService.getSessionCart(recentlyAddedFirst);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#hasSessionCart()
	 */
	@Override
	public boolean hasSessionCart()
	{
		return cartService.hasSessionCart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getMiniCart()
	 */
	@Override
	public CartData getMiniCart()
	{
		return getSessionCart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#addToCart(java.lang.String, long)
	 */
	@Override
	public CartModificationData addToCart(final String code, final long quantity) throws CommerceCartModificationException
	{
		final CartModificationData cartModification = cartService.addToCart(code, quantity);
		productImageHelper.enrichWithProductImages(cartModification.getEntry());
		return cartModification;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#addToCart(java.lang.String, long, java.lang.String)
	 */
	@Override
	public CartModificationData addToCart(final String code, final long quantity, final String storeId)
			throws CommerceCartModificationException
	{
		LOG.info("addToCart called with store ID, ignoring: " + storeId);
		return cartService.addToCart(code, quantity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#validateCartData()
	 */
	@Override
	public List<CartModificationData> validateCartData() throws CommerceCartModificationException
	{
		return cartService.validateCartData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#updateCartEntry(long, long)
	 */
	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final long quantity)
			throws CommerceCartModificationException
	{
		return cartService.updateCartEntry(entryNumber, quantity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#updateCartEntry(long, java.lang.String)
	 */
	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final String storeId)
			throws CommerceCartModificationException
	{
		throw new ApplicationBaseRuntimeException("Not supported: updateCartEntry(final long entryNumber, final String storeId)");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#restoreSavedCart(java.lang.String)
	 */
	@Override
	public CartRestorationData restoreSavedCart(final String code) throws CommerceCartRestorationException
	{
		// Restoration leads to an empty cart in our case
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getDeliveryCountries()
	 */
	@Override
	public List<CountryData> getDeliveryCountries()
	{
		//No delivery countries available, only choosing from existing addresses supported
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#estimateExternalTaxes(java.lang.String, java.lang.String)
	 */
	@Override
	public CartData estimateExternalTaxes(final String deliveryZipCode, final String countryIsoCode)
	{
		//We cannot support this, as the delivery costs are based on the ship-to party address in the ERP case
		throw new ApplicationBaseRuntimeException("Not supported: estimateExternalTaxes");

	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}



	/**
	 * @return the productImageHelper
	 */
	public ProductImageHelper getProductImageHelper()
	{
		return productImageHelper;
	}

	/**
	 * @param productImageHelper
	 *           the productImageHelper to set
	 */
	public void setProductImageHelper(final ProductImageHelper productImageHelper)
	{
		this.productImageHelper = productImageHelper;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#removeStaleCarts()
	 */
	@Override
	public void removeStaleCarts()
	{
		//No stale carts in this scenario

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#restoreAnonymousCartAndTakeOwnership(java.lang.String)
	 */
	@Override
	public CartRestorationData restoreAnonymousCartAndTakeOwnership(final String guid) throws CommerceCartRestorationException
	{
		//No anonymous carts in our scenario
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#removeSessionCart()
	 */
	@Override
	public void removeSessionCart()
	{
		//reset cart
		cartService.removeSessionCart();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#getCartsForCurrentUser()
	 */
	@Override
	public List<CartData> getCartsForCurrentUser()
	{
		return Arrays.asList(getSessionCart());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#mergeCarts(de.hybris.platform.core.model.order.CartModel,
	 * de.hybris.platform.core.model.order.CartModel, java.util.List)
	 */
	@Override
	public void mergeCarts(final CartModel fromCart, final CartModel toCart, final List<CommerceCartModification> modifications)
			throws CommerceCartMergingException
	{
		throw new ApplicationBaseRuntimeException("mergeCarts not supported");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#restoreAnonymousCartAndMerge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public CartRestorationData restoreAnonymousCartAndMerge(final String fromAnonumousCartGuid, final String toUserCartGuid)
			throws CommerceCartMergingException, CommerceCartRestorationException
	{
		throw new ApplicationBaseRuntimeException("restoreAnonymousCartAndMerge not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.CartFacade#restoreCartAndMerge(java.lang.String, java.lang.String)
	 */
	@Override
	public CartRestorationData restoreCartAndMerge(final String fromUserCartGuid, final String toUserCartGuid)
			throws CommerceCartRestorationException, CommerceCartMergingException
	{
		throw new ApplicationBaseRuntimeException("restoreCartAndMerge not supported");
	}

	@Override
	public CartModificationData addOrderEntry(final OrderEntryData cartEntry)
	{


		CartModificationData cartModification = null;
		try
		{
			cartModification = addToCart(cartEntry.getProduct().getCode(), cartEntry.getQuantity().longValue());
		}
		catch (final CommerceCartModificationException e)
		{
			throw new DomainException(getLocalizedString(CART_MODIFICATION_ERROR), e);
		}

		return cartModification;
	}





	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade#updateOrderEntry(de.hybris.platform.commercefacades
	 * .order.data.OrderEntryData)
	 */
	@Override
	public CartModificationData updateOrderEntry(final OrderEntryData cartEntry) throws EntityValidationException
	{
		return cartService.updateCartEntry(cartEntry.getEntryNumber().longValue(), cartEntry.getQuantity().longValue());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade#addOrderEntryList(java.util.List)
	 */
	@Override
	public List<CartModificationData> addOrderEntryList(final List<OrderEntryData> cartEntries)
	{
		throw new ApplicationBaseRuntimeException("addOrderEntryList not supported");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade#updateOrderEntryList(java.util.List)
	 */
	@Override
	public List<CartModificationData> updateOrderEntryList(final List<OrderEntryData> cartEntries)
	{
		throw new ApplicationBaseRuntimeException("updateOrderEntryList not supported");
	}


	public boolean hasEntries()
	{
		boolean hasEntries = false;
		final CartData sessionCart = cartService.getSessionCart();
		if (sessionCart != null && sessionCart.getEntries() != null)
		{
			hasEntries = sessionCart.getEntries().size() > 0;
		}
		return hasEntries;
	}

}
