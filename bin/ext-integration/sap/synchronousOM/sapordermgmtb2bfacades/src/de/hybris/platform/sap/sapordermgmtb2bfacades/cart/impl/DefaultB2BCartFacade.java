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

import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.B2BCartFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.order.CartModel;

import java.util.Comparator;
import java.util.List;


/**

 * 
 */
public class DefaultB2BCartFacade extends DefaultCartFacade implements B2BCartFacade
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade#addOrderEntry(de.hybris.platform.commercefacades.
	 * order.data.OrderEntryData)
	 */
	@Override
	public CartModificationData addOrderEntry(final OrderEntryData cartEntry) throws EntityValidationException
	{
		return null;
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
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade#addOrderEntryList(java.util.List)
	 */
	@Override
	public List<CartModificationData> addOrderEntryList(final List<OrderEntryData> cartEntries)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade#updateOrderEntryList(java.util.List)
	 */
	@Override
	public List<CartModificationData> updateOrderEntryList(final List<OrderEntryData> cartEntries)
	{
		return null;
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
		// not implemented

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
		return null;
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
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2bacceleratorfacades.order.B2BCartFacade#groupMultiDimensionalProducts(de.hybris.platform.
	 * commercefacades.order.data.AbstractOrderData, java.util.Comparator)
	 */
	@Override
	public <T extends AbstractOrderData> void groupMultiDimensionalProducts(final T orderData,
			final Comparator<VariantOptionData> variantSortStrategy)
	{
		// not implemented

	}
}
