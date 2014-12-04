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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CsticTypeMapperImplTest
{
	private CsticTypeMapper typeMapper;

	private static final String GROUP_NAME = "GENERAL";

	@Before
	public void setup()
	{
		typeMapper = new CsticTypeMapperImpl();
		final UiTypeFinder uiTypeFinder = new UiTypeFinderImpl();
		typeMapper.setUiTypeFinder(uiTypeFinder);
		final ValueFormatTranslator valueFormatTranslater = new ValueFormatTranslatorImpl();
		typeMapper.setValueFormatTranslater(valueFormatTranslater);
	}

	@Test
	public void testCstiModelToDatacMapping() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getTypeLength(), data.getMaxlength());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.STRING, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));
		assertEquals(Integer.valueOf(model.getConflicts().size()), Integer.valueOf(data.getConflicts().size()));
	}

	@Test
	public void testLangaugeFallbackForEmptyString() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setLanguageDependentName("");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getTypeLength(), data.getMaxlength());
		assertEquals("[" + model.getName() + "]", data.getLangdepname());
	}

	@Test
	public void testLangaugeFallbackForNullValue() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setLanguageDependentName(null);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getTypeLength(), data.getMaxlength());
		assertEquals("[" + model.getName() + "]", data.getLangdepname());
	}

	@Test
	public void testCstiModelToDatacMappingForLastValidValue() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setSingleValue("A value");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals("A value", data.getValue());
		assertEquals("A value", data.getLastValidValue());
	}

	@Test
	public void testCstiModelToDatacMappingForLastValidValueNotNullForNumeric() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createIntegerCstic();
		model.setSingleValue(null);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertNotNull("value may not be null", data.getValue());
		assertNotNull("last valid value may not be null", data.getLastValidValue());
	}

	@Test
	public void testReadOnlyCstiModelToDatacMapping() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createReadOnlyCstic();
		model.setSingleValue("aaaa");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getTypeLength(), data.getMaxlength());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.READ_ONLY, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));
		assertEquals(0, data.getDomainvalues().size());

	}

	@Test
	public void testReadOnlyDomainValue() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setAuthor(CsticTypeMapper.READ_ONLY_AUTHOR);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());

		assertTrue(data.getDomainvalues().get(0).isReadonly());
	}


	@Test
	public void testDomainValueLangName() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setAuthor(CsticTypeMapper.READ_ONLY_AUTHOR);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getAssignableValues().get(0).getLanguageDependentName(), data.getDomainvalues().get(0).getLangdepname());
	}

	@Test
	public void testDomainValueLangNameFallbackEmpty() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setLanguageDependentName("");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals("[" + model.getAssignableValues().get(0).getName() + "]", data.getDomainvalues().get(0).getLangdepname());
	}

	@Test
	public void testDomainValueLangNameFallbackNull() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setLanguageDependentName(null);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals("[" + model.getAssignableValues().get(0).getName() + "]", data.getDomainvalues().get(0).getLangdepname());
	}


	@Test
	public void testCstiModelToDatacMappingForSimpleCheckBoxTrue() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		model.setSingleValue("X");
		CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.CHECK_BOX, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals(1, data.getDomainvalues().size());
		assertEquals(Boolean.TRUE, Boolean.valueOf(data.getDomainvalues().get(0).isSelected()));

	}

	@Test
	public void testCstiModelToDatacMappingForSimpleCheckBoxFalse() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		model.setSingleValue("");
		CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.CHECK_BOX, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals(1, data.getDomainvalues().size());
		assertEquals(Boolean.FALSE, Boolean.valueOf(data.getDomainvalues().get(0).isSelected()));

	}

	@Test
	public void testCstiModelToDatacMappingForSimpleRadioButton() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.RADIO_BUTTON, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		checkModelContainsDomainValue(model, data);


	}

	@Test
	public void testCstiModelToDatacMappingForSimpleDropDown() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.DROPDOWN, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		checkModelContainsDomainValue(model, data);

	}

	private void checkModelContainsDomainValue(final CsticModel model, final CsticData data)
	{
		for (final CsticValueModel modelValue : model.getAssignableValues())
		{
			boolean found = false;
			for (final CsticValueData dataValue : data.getDomainvalues())
			{
				if (modelValue.getName().equals(dataValue.getName()))
				{
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	public void testCstiModelToDataMappingForUndefinedNotImplemented() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createUndefinedCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.NOT_IMPLEMENTED, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));



	}

	@Test
	public void testCsticMappingFloatToSimpleNumeric() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		model.setNumberScale(23);
		model.setTypeLength(42);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("Map float type to UIType NUMERIC", data.getType(), UiType.NUMERIC);
		assertEquals("Wrong number scale", 23, data.getNumberScale());
		assertEquals("Wrong type length", 42, data.getTypeLength());
	}

	@Test
	public void testCsticMappingIntegerToSimpleNumeric() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createIntegerCstic();
		model.setTypeLength(42);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("Map integer type to UIType NUMERIC", data.getType(), UiType.NUMERIC);
		assertEquals("Number scale is 0 for Integers", 0, data.getNumberScale());
		assertEquals("Wrong type length", 42, data.getTypeLength());
	}


	@Test
	public void testUpdateCsticModelValuesFromDataSingleValueInitial()
	{

		final CsticData data = new CsticData();
		final CsticModel model = new CsticModelImpl();
		data.setValue("aValue");
		assertEquals(0, model.getAssignedValues().size());
		typeMapper.updateCsticModelValuesFromData(data, model);
		assertEquals(1, model.getAssignedValues().size());
		assertEquals("aValue", model.getAssignedValues().get(0).getName());
	}




	@Test
	public void testUpdateCsticModelValuesFromDataSingleValueUpdate()
	{

		final CsticData data = new CsticData();
		final CsticModel model = new CsticModelImpl();
		data.setValue("aValue");
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("anotherValue");
		model.setAssignedValuesWithoutCheckForChange(Collections.singletonList(value));

		assertEquals(1, model.getAssignedValues().size());
		typeMapper.updateCsticModelValuesFromData(data, model);
		assertEquals(1, model.getAssignedValues().size());
		assertEquals("aValue", model.getAssignedValues().get(0).getName());

	}

	@Test
	public void testUpdateCstiModelValueFromDataSimpleCheckBoxSelect() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		model.setSingleValue("");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		data.getDomainvalues().get(0).setSelected(true);

		typeMapper.updateCsticModelValuesFromData(data, model);

		assertEquals(1, model.getAssignedValues().size());
		assertEquals(model.getAssignableValues().get(0).getName(), model.getSingleValue());


	}

	@Test
	public void testUpdateCstiModelValueFromDataSimpleCheckBoxUnSelect() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		model.setSingleValue("X");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		data.getDomainvalues().get(0).setSelected(false);

		typeMapper.updateCsticModelValuesFromData(data, model);

		assertEquals(0, model.getAssignedValues().size());
		assertNull(model.getSingleValue());


	}

	@Test
	public void testUpdateCstiModelValueCheckBoxListNotChanged() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		model.setChangedByFrontend(false);

		typeMapper.updateCsticModelValuesFromData(data, model);

		assertFalse("DTO was not changed", model.isChangedByFrontend());
	}

	@Test
	public void testCstiModelToDatacMappingForCheckBoxList() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.CHECK_BOX_LIST, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		checkModelContainsDomainValue(model, data);
		checkAssignedValuesAreSelected(model, data);

	}


	private void checkAssignedValuesAreSelected(final CsticModel model, final CsticData data)
	{

		for (final CsticValueModel modelValue : model.getAssignableValues())
		{
			final boolean isAssigned = model.getAssignedValues().contains(modelValue);
			boolean found = false;
			for (final CsticValueData dataValue : data.getDomainvalues())
			{
				if (modelValue.getName().equals(dataValue.getName()))
				{
					if (isAssigned)
					{
						assertTrue(dataValue.getName() + " should be selected", dataValue.isSelected());
					}
					else
					{
						assertFalse(dataValue.getName() + " should NOT be selected", dataValue.isSelected());
					}
					found = true;
					break;
				}
			}
			assertTrue(found);
		}

	}

	@Test
	public void testUpdateCsticModelValuesFromDataCheckBoxList()
	{

		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		data.setValue("NULL_VALUE");

		typeMapper.updateCsticModelValuesFromData(data, model);

		assertNull("null value was not set", model.getSingleValue());
	}

	@Test
	public void testUpdateCsticModelValuesFromDataDropDownListNullValue()
	{

		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		// value 1 is pre-selected
		data.getDomainvalues().get(0).setSelected(false);
		data.getDomainvalues().get(1).setSelected(true);
		data.getDomainvalues().get(2).setSelected(true);
		typeMapper.updateCsticModelValuesFromData(data, model);

		assertEquals("number assigned values wrong", 2, model.getAssignedValues().size());
		assertEquals("wrong value assigned", model.getAssignableValues().get(1), model.getAssignedValues().get(0));
		assertEquals("wrong value assigned", model.getAssignableValues().get(2), model.getAssignedValues().get(1));

	}

	@Test
	public void testMapFieldMask() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		final String mask = "-____";
		model.setEntryFieldMask(mask);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("Field mask missing", mask, data.getEntryFieldMask());
	}

	@Test
	public void testMapFieldMaskNull() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		final String mask = null;
		model.setEntryFieldMask(mask);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("Field mask should not be null", "", data.getEntryFieldMask());
	}

	@Test
	public void testReadOnlyForSelectable() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setSelectable(false);
		model.getAssignableValues().get(1).setSelectable(true);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertTrue("Should be mapped to readonly", data.getDomainvalues().get(0).isReadonly());
		assertFalse("Should NOT be mapped to readonly", data.getDomainvalues().get(1).isReadonly());
	}

	@Test
	public void testGenerateUniqueKeyCtsic()
	{
		final CsticModel model = new CsticModelImpl();
		model.setName("csticName");
		final String key = typeMapper.generateUniqueKey(model, GROUP_NAME);

		assertEquals("root." + GROUP_NAME + ".csticName", key);
	}

	@Test
	public void testGenerateUniqueKeyCtsicValue()
	{
		final CsticModel model = new CsticModelImpl();
		model.setName("csticName");
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("valueName");
		final String key = typeMapper.generateUniqueKey(model, value, GROUP_NAME);

		assertEquals("root." + GROUP_NAME + ".csticName.valueName", key);
	}

	@Test
	public void testCstiModelToDataKeyNotNull() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertNotNull(data.getKey());

		assertNotNull(data.getDomainvalues().get(0));
		assertNotNull(data.getDomainvalues().get(0).getKey());

	}

	@Test
	public void testCstiModelToDatacConflictMapping() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCsticWithConflicts();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(Integer.valueOf(model.getConflicts().size()), Integer.valueOf(data.getConflicts().size()));
		assertEquals("CStic with conflict should have 'Warning Status'", CsticStatusType.WARNING, data.getCsticStatus());
	}


	@Test
	public void testAssignedValueNotAssignable()
	{

		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		model.setSingleValue("NOT ASSIGNABLE");

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("NOT ASSIGNABLE", data.getValue());
		boolean found = false;
		for (final CsticValueData value : data.getDomainvalues())
		{
			if ("NOT ASSIGNABLE".equals(value.getName()))
			{
				found = true;
				assertTrue("assigned value not selected", value.isSelected());
				break;
			}
		}
		assertTrue("assigned value is not in domain values", found);
	}

	@Test
	public void testModifiedFlagTrue()
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setAuthor(CsticModel.AUTHOR_USER);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals("user assigned cstic should be marked as finished", CsticStatusType.FINISHED, data.getCsticStatus());
	}

	@Test
	public void testModifiedFlagFalse()
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setAuthor(CsticModel.AUTHOR_SYSTEM);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals("system assigned cstic should be marked as not-finished", CsticStatusType.DEFAULT, data.getCsticStatus());
	}
}
