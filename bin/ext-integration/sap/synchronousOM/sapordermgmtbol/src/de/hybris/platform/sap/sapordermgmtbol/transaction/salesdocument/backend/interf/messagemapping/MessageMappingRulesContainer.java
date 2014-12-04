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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRule;

import java.util.Map;


/**
 * Container for the message mapping rules maintained in messages.xml
 */
public interface MessageMappingRulesContainer
{

	public boolean isHideNonErrorMsg();

	public MessageMappingRule mostNarrow(MessageMappingRule.Pattern beMes);

	/**
	 * @return Map of callback implementations
	 */
	Map<String, MessageMappingCallbackProcessor> getCallbacks();

}