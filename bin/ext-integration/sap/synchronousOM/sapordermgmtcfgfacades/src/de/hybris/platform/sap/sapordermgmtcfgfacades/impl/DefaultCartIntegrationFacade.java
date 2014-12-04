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
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;


public class DefaultCartIntegrationFacade implements ConfigurationCartIntegrationFacade
{
	ProductConfigurationService productConfigurationService;
	CartService cartService;
	ProductService productService;

	@Override
	public String addConfigurationToCart(final ConfigurationData configuration) throws CommerceCartModificationException
	{
		final ConfigModel configModel = productConfigurationService.retrieveConfigurationModel(configuration.getConfigId());
		final String itemKey = configuration.getCartItemPK();
		final boolean isItemAvailable = isItemInCartByKey(itemKey);
		if (isItemAvailable)
		{
			cartService.updateConfigurationInCart(itemKey, configModel);
			return itemKey;
		}
		else
		{
			return cartService.addConfigurationToCart(configModel);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade#isItemInCartByKey(java.lang.String
	 * )
	 */
	@Override
	public boolean isItemInCartByKey(final String key)
	{
		return cartService.isItemAvailable(key);
	}

	/**
	 * @return the productConfigurationService
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}


	@Override
	public String copyConfiguration(final String configId)
	{
		final String externalConfiguration = productConfigurationService.retrieveExternalConfiguration(configId);
		final ConfigModel configModel = productConfigurationService.retrieveConfigurationModel(configId);
		final KBKey kbKey = getKBKey(configModel.getRootInstance().getName());
		final ConfigModel newConfiguration = productConfigurationService.createConfigurationFromExternal(kbKey,
				externalConfiguration);
		return newConfiguration.getId();
	}

	/**
	 * Creates a KB key for a given product ID, accessing the product model, and returns it.
	 * 
	 * @param productId
	 * @return KBKey, containing KB data for the given product
	 */
	protected KBKey getKBKey(final String productId)
	{

		final KBKey kbKey = new KBKeyImpl(productId);

		return kbKey;

	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	public void resetConfiguration(final String configId)
	{
		//nothing needed for us as configuration must stay in session
	}


}
