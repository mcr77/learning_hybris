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
package de.hybris.platform.b2b.punchout.populators.impl;

import de.hybris.platform.b2b.punchout.Address;
import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PostalAddress;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.Header;
import org.cxml.PunchOutSetupRequest;
import org.cxml.ShipTo;


/**
 * Populator from {@link CXML} to {@link PunchOutSession}.
 */
public class DefaultPunchOutSessionPopulator implements Populator<CXML, PunchOutSession>
{

	@Override
	public void populate(final CXML source, final PunchOutSession target) throws ConversionException
	{
		final CXMLElementBrowser cXmlBrowser = new CXMLElementBrowser(source);

		final PunchOutSetupRequest request = cXmlBrowser.findRequestByType(PunchOutSetupRequest.class);
		target.setOperation(request.getOperation());
		populateBuyerCookie(target, request);
		populateShippingInfo(target, request);
		target.setBrowserFormPostUrl(request.getBrowserFormPost().getURL().getvalue());

		populateOrganizationInfo(cXmlBrowser.findHeader(), target);

	}

	private void populateOrganizationInfo(final Header header, final PunchOutSession punchoutSession)
	{
		final List<Organization> initiatedByList = new ArrayList<Organization>();
		final List<Organization> targetedToList = new ArrayList<Organization>();
		final List<Organization> sentByList = new ArrayList<Organization>();


		for (final Iterator<Credential> credentials = header.getFrom().getCredential().iterator(); credentials.hasNext();)
		{
			final Credential credential = credentials.next();

			final Organization organization = new Organization();
			organization.setDomain(credential.getDomain());
			organization.setIdentity(credential.getIdentity().getContent().get(0).toString());

			initiatedByList.add(organization);

		}

		for (final Iterator<Credential> credentials = header.getTo().getCredential().iterator(); credentials.hasNext();)
		{
			final Credential credential = credentials.next();

			final Organization organization = new Organization();
			organization.setDomain(credential.getDomain());
			organization.setIdentity(credential.getIdentity().getContent().get(0).toString());

			targetedToList.add(organization);

		}

		for (final Iterator<Credential> credentials = header.getSender().getCredential().iterator(); credentials.hasNext();)
		{
			final Credential credential = credentials.next();

			final Organization organization = new Organization();
			organization.setDomain(credential.getDomain());
			organization.setIdentity(credential.getIdentity().getContent().get(0).toString());

			sentByList.add(organization);

		}

		punchoutSession.setInitiatedBy(initiatedByList);
		punchoutSession.setTargetedTo(targetedToList);
		punchoutSession.setSentBy(sentByList);

	}

	private void populateShippingInfo(final PunchOutSession output, final PunchOutSetupRequest request)
	{
		final ShipTo shipTo = request.getShipTo();
		if (shipTo != null)
		{
			final org.cxml.Address requestAddress = shipTo.getAddress();
			output.setShippingAddress(new Address());
			output.getShippingAddress().setId(requestAddress.getAddressID());
			if (requestAddress.getEmail() != null)
			{
				output.getShippingAddress().setEmail(requestAddress.getEmail().getvalue());
			}
			if (requestAddress.getName() != null)
			{
				output.getShippingAddress().setName(requestAddress.getName().getvalue());
			}
			if (requestAddress.getPhone() != null)
			{
				output.getShippingAddress().setPhone(requestAddress.getPhone().getTelephoneNumber().getNumber());
			}
			output.getShippingAddress().setPostalAddress(toPostalAddress(requestAddress.getPostalAddress()));

		}
	}

	private PostalAddress toPostalAddress(final org.cxml.PostalAddress postalAddress)
	{
		final PostalAddress result = new PostalAddress();

		result.setCity(postalAddress.getCity());
		result.setCountry(postalAddress.getCountry().getIsoCountryCode());
		if (CollectionUtils.isNotEmpty(postalAddress.getDeliverTo()))
		{
			result.setDeliverTo(postalAddress.getDeliverTo().iterator().next().getvalue());
		}
		result.setName(postalAddress.getName());
		result.setPostalCode(postalAddress.getPostalCode());
		result.setState(postalAddress.getState());
		if (CollectionUtils.isNotEmpty(postalAddress.getStreet()))
		{
			result.setStreet(postalAddress.getStreet().iterator().next().getvalue());
		}

		return result;
	}

	private void populateBuyerCookie(final PunchOutSession output, final PunchOutSetupRequest request)
	{
		if (request.getBuyerCookie() != null)
		{
			final String buyerCookieId = (String) request.getBuyerCookie().getContent().iterator().next();

			output.setBuyerCookie(buyerCookieId);
		}
	}

}
