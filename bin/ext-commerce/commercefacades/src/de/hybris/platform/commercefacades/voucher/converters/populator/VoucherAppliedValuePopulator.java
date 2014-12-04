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
package de.hybris.platform.commercefacades.voucher.converters.populator;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.jalo.util.VoucherValue;
import de.hybris.platform.voucher.model.VoucherModel;


/**
 * Populate the {@link VoucherData} with the value applied to the voucher in {@link AbstractOrderModel} <br/>
 * Be aware that target's voucherCode attribute cannot be null.
 */
public class VoucherAppliedValuePopulator implements Populator<AbstractOrderModel, VoucherData>
{
	private PriceDataFactory priceDataFactory;
	private VoucherModelService voucherModelService;
	private VoucherService voucherService;

	@Override
	public void populate(final AbstractOrderModel source, final VoucherData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		Assert.notNull(target.getVoucherCode(), "Target's voucherCode attribute cannot be null.");

		final VoucherModel voucher = getVoucherService().getVoucher(target.getVoucherCode());
		if (voucher != null)
		{
			final VoucherValue voucherVal = getVoucherModelService().getAppliedValue(voucher, source);
			target.setAppliedValue(getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(voucherVal.getValue()),
					voucherVal.getCurrencyIsoCode()));
		}
	}

	public VoucherModelService getVoucherModelService()
	{
		return voucherModelService;
	}

	@Required
	public void setVoucherModelService(final VoucherModelService voucherModelService)
	{
		this.voucherModelService = voucherModelService;
	}

	public VoucherService getVoucherService()
	{
		return voucherService;
	}

	@Required
	public void setVoucherService(final VoucherService voucherService)
	{
		this.voucherService = voucherService;
	}

	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}
}
