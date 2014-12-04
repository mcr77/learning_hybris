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
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.user.converters.populator.PrincipalPopulator;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;



@UnitTest
public class CustomerReviewPopulatorTest
{
	private static final String USER_UID = "author";
	private static final String COMMENT = "comment";
	private static final Double RATING = Double.valueOf(5.0);

	private CustomerReviewPopulator customerReviewPopulator = new CustomerReviewPopulator();
	private AbstractPopulatingConverter<CustomerReviewModel, ReviewData> customerReviewConverter;

	private AbstractPopulatingConverter<PrincipalModel, PrincipalData> principalConverter =
				new ConverterFactory<PrincipalModel, PrincipalData, PrincipalPopulator>().create(PrincipalData.class, new PrincipalPopulator());

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		customerReviewPopulator.setPrincipalConverter(principalConverter);

		customerReviewConverter = new ConverterFactory<CustomerReviewModel, ReviewData, CustomerReviewPopulator>().create(ReviewData.class, customerReviewPopulator);
	}

	@Test
	public void testConvert()
	{
		final CustomerReviewModel source = mock(CustomerReviewModel.class);
		final UserModel userModel = mock(UserModel.class);

		given(source.getUser()).willReturn(userModel);
		given(userModel.getUid()).willReturn(USER_UID);
		given(source.getComment()).willReturn(COMMENT);
		given(source.getRating()).willReturn(RATING);

		final ReviewData result = customerReviewConverter.convert(source);

		Assert.assertEquals(COMMENT, result.getComment());
		Assert.assertEquals(RATING, result.getRating());
		Assert.assertEquals(USER_UID, result.getPrincipal().getUid());
	}
}
