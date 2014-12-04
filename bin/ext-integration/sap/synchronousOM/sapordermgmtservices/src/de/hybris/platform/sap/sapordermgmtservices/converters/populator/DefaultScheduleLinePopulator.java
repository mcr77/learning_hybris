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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtservices.schedline.data.ScheduleLineData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.util.localization.Localization;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;



/**
 * 
 */
public class DefaultScheduleLinePopulator implements Populator<Schedline, ScheduleLineData>
{
	/**
	 * 
	 */
	public static final String SAPORDERMGMT_SCHEDULE_LINE = "sapordermgmt.scheduleLine";
	private static final Logger LOG = Logger.getLogger(DefaultAbstractOrderPopulator.class);
	private I18NService i18NService;
	private GenericFactory genericFactory;
	private Message message;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Schedline source, final ScheduleLineData target) throws ConversionException
	{
		final Date committedDate = source.getCommittedDate();
		target.setConfirmedDate(committedDate);
		target.setConfirmedQuantity(new Double(source.getCommittedQuantity().doubleValue()));

		try
		{
			final Converter converter = genericFactory.getBean(SapcommonbolConstants.ALIAS_BO_CONVERTER);
			target.setQuantityUnit(converter.convertUnitKey2UnitID(source.getUnit()));
		}
		catch (final BusinessObjectException e)
		{
			throw new ApplicationBaseRuntimeException("Could not convert unit", e);
		}

		this.setFormatedScheduleLIne(target);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(("created new schedule line: " + target.getConfirmedQuantity() + ", " + target.getConfirmedDate()));
		}
	}


	private void setFormatedScheduleLIne(final ScheduleLineData target)
	{
		final String[] arg = new String[]
		{ formatDouble(target.getConfirmedQuantity().doubleValue()), target.getQuantityUnit(),
				this.convertDateToLongDateString(target.getConfirmedDate()) };

		target.setFormattedValue(Localization.getLocalizedString(SAPORDERMGMT_SCHEDULE_LINE, arg));
	}

	/**
	 * @return the i18NService
	 */
	public I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	/**
	 * Formats schedule line date according to the current session locale
	 * 
	 * @param date
	 * @param locale
	 *           Session locale
	 * @return Schedule line date in localized format, using {@link DateFormat#LONG}
	 */
	protected String getFormattedDate(final Date date, final Locale locale)
	{
		final DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.LONG, locale);
		return dateFormat.format(date);
	}

	/**
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * @param genericFactory
	 *           the genericFactory to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * Converts a date to a localized string
	 * 
	 * @param date
	 * @return Date, localized according the session locale
	 */
	protected String convertDateToLongDateString(final Date date)
	{
		return ConversionHelper.convertDateToLocalizedString(date);
	}

	protected String convertDoubleToString(final Double d)
	{
		return ConversionHelper.convertBigDecimalToString(new BigDecimal(d.doubleValue()));
	}

	/**
	 * @return the message
	 */
	public Message getMessage()
	{
		return message;
	}


	/**
	 * @param message
	 *           the message to set
	 */
	public void setMessage(final Message message)
	{
		this.message = message;
	}

	public String formatDouble(final double d)
	{
		DecimalFormat df = new DecimalFormat("#.##");

		if ((d == Math.floor(d)) && !Double.isInfinite(d))
		{
			df = new DecimalFormat("#");
		}

		return df.format(d);
	}

}
