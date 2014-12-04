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
package de.hybris.platform.b2b.punchout.actions;

import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.From;
import org.cxml.Header;
import org.cxml.Identity;
import org.cxml.Sender;
import org.cxml.SharedSecret;
import org.cxml.To;
import org.springframework.beans.factory.annotation.Required;


/**
 * Creates the header for a PunchOut message. The Header element contains addressing and authentication information. The
 * Header element is the same regardless of the specific Request or Response within the body of the cXML message.
 */
public class PunchOutOrderMessageHeaderProcessingAction implements PunchOutProcessingAction<CartModel, CXML>
{
	private PunchOutSessionService punchOutSessionService;
	private PunchOutConfigurationService punchOutConfigurationService;

	@Override
	public void process(final CartModel input, final CXML transaction)
	{
		final Header header = new Header();
		final PunchOutSession currentPunchOutSession = getPunchOutSessionService().getCurrentPunchOutSession();

		header.setFrom(createFrom(currentPunchOutSession));
		header.setTo(createTo(currentPunchOutSession));
		header.setSender(createSender(currentPunchOutSession));

		transaction.getHeaderOrMessageOrRequestOrResponse().add(header);
	}

	protected From createFrom(final PunchOutSession currentPunchOutSession)
	{
		final From headerFrom = new From();
		final Credential credentialFrom = new Credential();
		// Since it is a request the from well be initiated by this system, the original <To> in the request will become 
		// the <From> in the response.

		final List<Organization> originalToList = currentPunchOutSession.getTargetedTo();

		for (int i = 0; i < originalToList.size(); i++)
		{
			//System.out.println(list.get(i));
			credentialFrom.setDomain(originalToList.get(i).getDomain());
			credentialFrom.setIdentity(new Identity());
			credentialFrom.getIdentity().getContent().add(originalToList.get(i).getIdentity());
			headerFrom.getCredential().add(credentialFrom);
		}

		return headerFrom;
	}

	protected To createTo(final PunchOutSession currentPunchOutSession)
	{
		final To headerTo = new To();
		final Credential credentialTo = new Credential();
		// Since it is a request the from well be initiated by this system, the original <From> in the request will become 
		// the <To> in the response.
		final List<Organization> originalFromList = currentPunchOutSession.getInitiatedBy();

		for (int i = 0; i < originalFromList.size(); i++)
		{
			//System.out.println(list.get(i));
			credentialTo.setDomain(originalFromList.get(i).getDomain());
			credentialTo.setIdentity(new Identity());
			credentialTo.getIdentity().getContent().add(originalFromList.get(i).getIdentity());
			headerTo.getCredential().add(credentialTo);
		}

		return headerTo;
	}

	protected Sender createSender(final PunchOutSession currentPunchOutSession)
	{
		final Sender headerSender = new Sender();
		// Sender stays the same as original
		final Credential credentialSender = new Credential();
		final List<Organization> originalSenderList = currentPunchOutSession.getSentBy();

		for (int i = 0; i < originalSenderList.size(); i++)
		{
			//System.out.println(list.get(i));
			credentialSender.setDomain(originalSenderList.get(i).getDomain());
			credentialSender.setIdentity(new Identity());
			credentialSender.getIdentity().getContent().add(originalSenderList.get(i).getIdentity());

			final SharedSecret sharedSecret = new SharedSecret();
			sharedSecret.getContent().add(originalSenderList.get(i).getSharedsecret());

			credentialSender.getSharedSecretOrDigitalSignatureOrCredentialMac().add(sharedSecret);
			headerSender.getCredential().add(credentialSender);
		}

		return headerSender;
	}

	protected PunchOutConfigurationService getPunchOutConfigurationService()
	{
		return punchOutConfigurationService;
	}

	@Required
	public void setPunchOutConfigurationService(final PunchOutConfigurationService punchOutConfigurationService)
	{
		this.punchOutConfigurationService = punchOutConfigurationService;
	}

	protected PunchOutSessionService getPunchOutSessionService()
	{
		return punchOutSessionService;
	}

	@Required
	public void setPunchOutSessionService(final PunchOutSessionService punchOutSessionService)
	{
		this.punchOutSessionService = punchOutSessionService;
	}
}
