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
package de.hybris.platform.sap.sapordermgmtservices.messagemappingcallback;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessage;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;


public class DefaultSapProductIdReplacementMsgMappingCallback implements MessageMappingCallbackProcessor
{

	/**
	 * 
	 */
	public static final String SAP_PRODUCTID_REPLACEMENT_CALLBACK_ID = "sapProductIdReplacement";
	private ProductService productService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.
	 * MessageMappingCallbackProcessor
	 * #process(de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl
	 * .messagemapping.BackendMessage)
	 */
	@Override
	public boolean process(final BackendMessage message)
	{
		final String[] vars = message.getVars();

		final String productIdFromBackend = vars[0];

		final ProductModel productModel = productService.getProductForCode(productIdFromBackend);

		if (productModel != null)
		{
			final String productDescription = productModel.getName(LocaleUtil.getLocale());
			if (productDescription != null && !productDescription.equals(""))
			{
				vars[0] = productDescription;
			}
		}
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.
	 * AbstractMessageMappingCallbackProcessor#getId()
	 */
	@Override
	public String getId()
	{
		return SAP_PRODUCTID_REPLACEMENT_CALLBACK_ID;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}


}
