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
package de.hybris.platform.sap.sapordermgmtservices.bolfacade;

import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.Map;


/**
 * 
 */
public interface BolCartFacade
{
	/**
	 * Return current cart from BOL.
	 * 
	 * @return Current cart, might be null if not created yet.
	 */
	public Basket getCart();

	/**
	 * Does a session cart exist? It corresponds to a hybris cart model which is not persisted.
	 * 
	 * @return Does a session cart exist?
	 */
	public Boolean hasCart();

	/**
	 * Create a new session cart
	 * 
	 * @return Newly created cart BO instance
	 */
	public Basket createCart();

	/**
	 * Updates the session cart and re-reads it from backend
	 */
	public void updateCart();

	/**
	 * Adds a product to the cart
	 * 
	 * @param code
	 *           Product ID to be added to the cart
	 * @param quantity
	 *           Quantity to be added to the cart
	 * @return BOl representation of new item
	 */
	public Item addToCart(String code, long quantity);

	/**
	 * Fetches cart item per item number
	 * 
	 * @param itemNumber
	 *           Internal item number
	 * @return BOL representation of cart item
	 */
	public Item getCartItem(int itemNumber);


	/**
	 * Fetch available delivery types.
	 * 
	 * @return Map of delivery types. Key is the shipping condition code, value the language dependent description
	 */
	public Map<String, String> getAllowedDeliveryTypes();

	/**
	 * Creates a new address instance.
	 * 
	 * @return SAP Address representation
	 */
	public Address createAddress();

	/**
	 * Places an order based on a cart, and returns this order
	 * 
	 * @param sapCart
	 *           SAP representation of a cart
	 * @return SAP representation of an order which has been submitted
	 */
	public Order placeOrderFromCart(Basket sapCart);

	/**
	 * Releases the current session cart. A new, initial session cart will be created.
	 * 
	 */
	public void releaseCart();



	/**
	 * Validates the cart and compiles the list of cart error messages. Performs a back end call and touches all items to
	 * be sure all error situations are handled. Depending on the customizing in the back end, this can be avoided by
	 * either redefining this method or the corresponding one in {@link SalesDocument}
	 * 
	 * @return List of cart error messages arising from validation.
	 */
	MessageList validateCart();

	/**
	 * Adds a configuration to the cart, adding a new item with the config model attached.
	 * 
	 * @param configModel
	 * @return Key of new item
	 */
	String addConfigurationToCart(ConfigModel configModel);

	/**
	 * Updates the configuration attached to an item
	 * 
	 * @param key
	 *           Item key
	 * @param configModel
	 *           Configuration
	 */
	void updateConfigurationInCart(String key, ConfigModel configModel);







}
