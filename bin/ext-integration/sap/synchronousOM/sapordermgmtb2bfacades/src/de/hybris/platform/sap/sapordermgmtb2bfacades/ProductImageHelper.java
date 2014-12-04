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
package de.hybris.platform.sap.sapordermgmtb2bfacades;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;


/**
 * 
 */
public interface ProductImageHelper
{
	public void enrichWithProductImages(AbstractOrderData order);

	/**
	 * @return
	 */
	ProductService getProductService();

	/**
	 * @param productService
	 */
	void setProductService(ProductService productService);

	/**
	 * @param entry
	 */
	void enrichWithProductImages(OrderEntryData entry);

	/**
	 * @return
	 */
	ProductPrimaryImagePopulator<ProductModel, ProductData> getProductPrimaryImagePopulator();

	/**
	 * @param productPrimaryImagePopulator
	 */
	void setProductPrimaryImagePopulator(ProductPrimaryImagePopulator<ProductModel, ProductData> productPrimaryImagePopulator);
}
