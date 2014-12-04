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
package de.hybris.platform.sap.sapordermgmtservices.cart.impl;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.impl.DefaultCartService;


/**
 * This implementation is needed for one bean in the storefront which does not access cart data via the facade, but via
 * the standard service.<br>
 * For the call to the session cart, we just return an empty cart. This is fine as in our case, the synchronous session
 * cart, managed via {@link de.hybris.platform.sap.sapordermgmtservices.cart.impl.DefaultCartService}, is coupled to the
 * back end session.
 */
public class DefaultStandardCartService extends DefaultCartService
{

	@Override
	public CartModel getSessionCart()
	{
		final CartModel cartModel = new CartModel();
		final UserModel userModel = new UserModel();
		userModel.setUid("");
		cartModel.setUser(userModel);
		return cartModel;
	}

}
