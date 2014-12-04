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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.ConflictData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


public class CsticTypeMapperImpl implements CsticTypeMapper
{
	private UiTypeFinder uiTypeFinder;

	private final static Logger LOG = Logger.getLogger(CsticTypeMapperImpl.class);

	@Autowired
	private ValueFormatTranslator valueFormatTranslator;

	@Override
	public CsticData mapCsticModelToData(final CsticModel model, final String prefix)
	{
		final CsticData data = new CsticData();

		data.setKey(generateUniqueKey(model, prefix));
		String langDepName = model.getLanguageDependentName();
		final String name = model.getName();
		langDepName = getDisplayName(langDepName, name);
		data.setLangdepname(langDepName);
		data.setName(name);
		data.setVisible(model.isVisible());
		data.setRequired(model.isRequired());

		data.setMaxlength(model.getTypeLength());
		final String entryFieldMask = model.getEntryFieldMask();
		data.setEntryFieldMask(emptyIfNull(entryFieldMask));

		final List<CsticValueData> domainValues = createDomainValues(model, prefix);
		data.setDomainvalues(domainValues);

		final List<ConflictData> conflicts = extractConflicts(model);
		data.setConflicts(conflicts);
		final boolean hasConflicts = conflicts != null && !conflicts.isEmpty();
		if (hasConflicts)
		{
			data.setCsticStatus(CsticStatusType.WARNING);
		}
		else if (CsticModel.AUTHOR_USER.equals(model.getAuthor()))
		{
			data.setCsticStatus(CsticStatusType.FINISHED);
		}
		else
		{
			data.setCsticStatus(CsticStatusType.DEFAULT);
		}

		final UiType uiType = uiTypeFinder.findUiTypeForCstic(model);
		data.setType(uiType);

		final String singleValue = model.getSingleValue();
		final String formattedValue = valueFormatTranslator.format(uiType, singleValue);
		data.setValue(formattedValue);
		data.setLastValidValue(formattedValue);

		if (uiType == UiType.NUMERIC)
		{
			mapNumericSpecifics(model, data);
		}

		if (LOG.isTraceEnabled())
		{
			LOG.trace("map CsticModel to CsticData [CSTIC_NAME='" + model.getName() + "';CSTIC_UI_KEY='" + data.getKey()
					+ "';CSTIC_UI_TYPE='" + data.getType() + "';CSTIC_VALUE='" + data.getValue() + "']");
		}

		return data;
	}

	private String getDisplayName(String langDepName, final String name)
	{
		if (langDepName == null || langDepName.isEmpty())
		{
			langDepName = "[" + name + "]";
		}
		return langDepName;
	}

	private List<ConflictData> extractConflicts(final CsticModel model)
	{
		final List<ConflictData> conflicts = new ArrayList<>();

		for (final ConflictModel conflict : model.getConflicts())
		{
			final ConflictData conflictData = new ConflictData();
			conflictData.setText(conflict.getText());
			conflicts.add(conflictData);
		}
		return conflicts;
	}

	private String emptyIfNull(final String value)
	{
		return (value == null) ? "" : value;
	}



	protected void mapNumericSpecifics(final CsticModel model, final CsticData data)
	{
		data.setNumberScale(model.getNumberScale());
		data.setTypeLength(model.getTypeLength());
	}





	private List<CsticValueData> createDomainValues(final CsticModel model, final String prefix)
	{
		final List<CsticValueData> domainValues = new ArrayList<>();

		for (final CsticValueModel csticValue : model.getAssignableValues())
		{
			final CsticValueData domainValue = createDomainValue(model, csticValue, prefix);
			domainValues.add(domainValue);
		}
		if (model.isConstrained())
		{
			for (final CsticValueModel assignedValue : model.getAssignedValues())
			{
				if (!model.getAssignableValues().contains(assignedValue))
				{
					final CsticValueData domainValue = createDomainValue(model, assignedValue, prefix);
					domainValues.add(domainValue);
				}
			}
		}

		return domainValues;

	}


	protected CsticValueData createDomainValue(final CsticModel model, final CsticValueModel csticValue, final String prefix)
	{
		final CsticValueData domainValue = new CsticValueData();
		domainValue.setKey(generateUniqueKey(model, csticValue, prefix));
		String langDepName = csticValue.getLanguageDependentName();
		final String name = csticValue.getName();
		langDepName = getDisplayName(langDepName, name);
		domainValue.setLangdepname(langDepName);
		domainValue.setName(name);
		final boolean isAssigned = model.getAssignedValues().contains(csticValue);
		domainValue.setSelected(isAssigned);

		final boolean isReadOnly = checkReadonly(csticValue);
		domainValue.setReadonly(isReadOnly);
		return domainValue;
	}


	protected boolean checkReadonly(final CsticValueModel csticValue)
	{
		final boolean isSystemValue = (csticValue.getAuthor() != null && csticValue.getAuthor().equalsIgnoreCase(READ_ONLY_AUTHOR));

		final boolean isSelectable = csticValue.isSelectable();
		return isSystemValue || !isSelectable;
	}

	@Override
	public void setUiTypeFinder(final UiTypeFinder uiTypeFinder)
	{
		this.uiTypeFinder = uiTypeFinder;
	}


	@Override
	public void updateCsticModelValuesFromData(final CsticData data, final CsticModel model)
	{
		final UiType uiType = data.getType();
		if (UiType.CHECK_BOX_LIST == uiType || UiType.CHECK_BOX == uiType)
		{
			//			model.clearValues();
			for (final CsticValueData valueData : data.getDomainvalues())
			{
				final String value = valueData.getName();
				final String parsedValue = valueFormatTranslator.parse(uiType, value);
				if (valueData.isSelected())
				{
					model.addValue(parsedValue);
				}
				else
				{
					model.removeValue(parsedValue);
				}
			}
		}
		else
		{
			final String value = data.getValue();
			final String parsedValue = valueFormatTranslator.parse(uiType, value);
			if (UiType.DROPDOWN == uiType && "NULL_VALUE".equals(value))
			{
				model.setSingleValue(null);
			}
			else
			{
				model.setSingleValue(parsedValue);
			}
		}

		if (LOG.isTraceEnabled())
		{
			LOG.trace("update CsticData to CsticModel [CSTIC_NAME='" + model.getName() + "';CSTIC_UI_KEY='" + data.getKey()
					+ "';CSTIC_UI_TYPE='" + data.getType() + "';CSTIC_VALUE='" + data.getValue() + "']");
		}

	}



	@Override
	public void setValueFormatTranslater(final ValueFormatTranslator valueFormatTranslater)
	{
		this.valueFormatTranslator = valueFormatTranslater;
	}



	@Override
	public String generateUniqueKey(final CsticModel model, final CsticValueModel value, final String prefix)
	{

		String key = generateUniqueKey(model, prefix);
		key += "." + value.getName();
		return key;
	}


	@Override
	public String generateUniqueKey(final CsticModel model, final String prefix)
	{
		final String key = "root" + "." + prefix + "." + model.getName();
		return key;
	}


}
