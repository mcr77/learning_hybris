/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.servicelayer.i18n.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.daos.LanguageDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultLanguageDaoTest extends ServicelayerTest
{

	private int initLanguageCount = 0;
	private static final int CREATED_COUNT = 8;

	@Resource(name = "languageDao")
	private LanguageDao languageDao;

	@Resource
	private ModelService modelService;

	private static final String IW = "iw"; //NOPMD
	private static final String HE = "he"; //NOPMD
	private static final String YI = "yi"; //NOPMD
	private static final String JI = "ji"; //NOPMD
	private static final String ID = "id"; //NOPMD
	private static final String IN = "in"; //NOPMD

	@Before
	public void prepare()
	{
		initLanguageCount = languageDao.findLanguages().size();

		final LanguageModel lModel0 = modelService.create(LanguageModel.class);
		lModel0.setIsocode("tinyRed");

		final LanguageModel lModel1 = modelService.create(LanguageModel.class);
		lModel1.setIsocode("bigGrey");

		final LanguageModel lModel2 = modelService.create(LanguageModel.class);
		lModel2.setIsocode("smallWhite");

		final LanguageModel lModel3 = modelService.create(LanguageModel.class);
		lModel3.setIsocode("hugePink");

		final LanguageModel iwLanguage = modelService.create(LanguageModel.class);
		iwLanguage.setIsocode(IW);
		final LanguageModel heLanguage = modelService.create(LanguageModel.class);
		heLanguage.setIsocode(HE);
		final LanguageModel jiLanguage = modelService.create(LanguageModel.class);
		jiLanguage.setIsocode(JI);
		final LanguageModel idLanguage = modelService.create(LanguageModel.class);
		idLanguage.setIsocode(ID);

		modelService.saveAll();
	}

	@Test
	public void testGetAllLangauge()
	{
		final List<LanguageModel> result = languageDao.findLanguages();

		Assert.assertNotNull(result);
		Assert.assertEquals(CREATED_COUNT + initLanguageCount, result.size());
	}


	@Test
	public void testLangaugeByNotExistingCode()
	{
		final List<LanguageModel> result = languageDao.findLanguagesByCode("notExisting");

		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}


	@Test
	public void testLangaugeByExistingCode()
	{
		final List<LanguageModel> result = languageDao.findLanguagesByCode("bigGrey");

		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLangaugeByExistingCodeWithNull()
	{
		languageDao.findLanguagesByCode(null);
	}

	@Test
	public void testLangaugeByEmptyCode()
	{
		final List<LanguageModel> result = languageDao.findLanguagesByCode("");

		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}

	//PLA-12635
	@Test
	public void testLanguageWithOldLocal()
	{
		//both "iw" and "he" must be found
		List<LanguageModel> result = languageDao.findLanguagesByCode(IW);
		Assert.assertEquals(1, result.size());

		result = languageDao.findLanguagesByCode(HE);
		Assert.assertEquals(1, result.size());

		//old "yi" CANNOT be found
		result = languageDao.findLanguagesByCode(YI);
		Assert.assertEquals(0, result.size());

		//new "ji" must be found
		result = languageDao.findLanguagesByCode(JI);
		Assert.assertEquals(1, result.size());

		//old "id" must be found
		result = languageDao.findLanguagesByCode(ID);
		Assert.assertEquals(1, result.size());

		//new "in" must be found, since "id" exists
		result = languageDao.findLanguagesByCode(IN);
		Assert.assertEquals(1, result.size());
	}

}
