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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.BackendMessageMapper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingRulesContainer;

import java.util.Iterator;

import javax.annotation.Resource;

import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoTable;


public class BackendMessageMapperImpl implements BackendMessageMapper
{

	protected static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(BackendMessageMapperImpl.class.getName());

	protected GenericFactory genericFactory;

	protected static final char //
			BAPI_RETURN_ERROR = 'E', //
			BAPI_RETURN_WARNING = 'W', //
			BAPI_RETURN_INFO = 'I', //
			BAPI_RETURN_ABORT = 'A', //
			BAPI_RETURN_SUCCESS = 'S'; //

	/**
	 * Convert external message type to internal. Returns '0' if the message could not being converted.
	 * 
	 * @param msgType
	 *           message type external
	 * @return message type internal
	 */
	protected static int msgTypeBe2Java(final String msgType)
	{
		final char beType = msgType.charAt(0);
		switch (beType)
		{
			case BAPI_RETURN_SUCCESS:
				return Message.SUCCESS;
			case BAPI_RETURN_WARNING:
				return Message.WARNING;
			case BAPI_RETURN_INFO:
				return Message.INFO;
			case BAPI_RETURN_ERROR:
			case BAPI_RETURN_ABORT:
				return Message.ERROR;
			default:
				return Message.INITIAL;
		}
	}

	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER)
	protected MessageMappingRulesContainer messageMappingRulesContainer;

	public BackendMessageMapperImpl()
	{
		sapLogger.entering("ErpMessageMapper.ErpMessageMapper(TransactionConfiguration transConf )");
		sapLogger.exiting();
	}



	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessageMapper
	 * #map (com.sap.wec.tc.core.businessobject.boi.BusinessObject, com.sap.conn.jco.JCoRecord,
	 * com.sap.conn.jco.JCoTable)
	 */
	@Override
	public void map(final BusinessObject rootBo, //
			final JCoRecord singleMsg, final JCoTable msgTable) throws BackendException
	{

		if (singleMsg != null)
		{
			map(rootBo, singleMsg);
		}

		if (msgTable != null)
		{
			int cnt = msgTable.getNumRows();
			msgTable.firstRow();
			while (cnt > 0)
			{
				map(rootBo, msgTable);
				--cnt;
				msgTable.nextRow();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessageMapper
	 * #map (com.sap.wec.tc.core.businessobject.boi.BusinessObject, com.sap.conn.jco.JCoRecord)
	 */
	@Override
	public Message map(final BusinessObject rootBo, final JCoRecord struct) throws BackendException
	{
		sapLogger.entering("map(BusinessObject rootBo, JCoRecord struct)");

		final BackendMessage beMes = createBackendMessage(struct);

		if (beMes.isEmpty())
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("Message " + beMes + " is empty");
			}
			sapLogger.exiting();
			return null;

		}
		else if (beMes.getBeSeverity().charAt(0) == 'A')
		{
			final BackendException ex = new BackendException("Abort BE message: " + beMes.toString());
			sapLogger.throwing(ex);
			throw ex;

		}
		else
		{
			final String severity = beMes.getBeSeverity();
			final MessageMappingRule.Pattern patrn = new MessageMappingRule.Pattern(beMes.getBeClass(), beMes.getBeNumber(),
					severity, beMes.getVars()[0], beMes.getVars()[1], beMes.getVars()[2], beMes.getVars()[3]);

			// find the matching rule
			final MessageMappingRule rule = messageMappingRulesContainer.mostNarrow(patrn);

			// decide action base on rule
			Message msg;
			if (rule != null)
			{
				msg = transformMessageByRule(beMes, rule);
			}
			else
			{
				if (messageMappingRulesContainer.isHideNonErrorMsg()
						&& severityScale(severity.charAt(0)) < severityScale(BAPI_RETURN_ERROR))
				{
					// remove;
					msg = null;
				}
				else
				{
					msg = passMessage(beMes);
				}
			}

			if (msg != null)
			{
				final BusinessObject bob = findReferredBO(rootBo, beMes);
				bob.addMessage(msg);
				if (sapLogger.isDebugEnabled())
				{
					sapLogger.debug("Message " + beMes + " mapped by rule " + rule + " to " + msg + " and attched to BO " + bob);
				}
			}
			else
			{
				if (sapLogger.isDebugEnabled())
				{
					sapLogger.debug("Message " + beMes + " mapped by rule " + rule + " to nothing");
				}
			}

			sapLogger.exiting();
			return msg;
		}
	}

	protected BackendMessage createBackendMessage(final JCoRecord struct)
	{
		final BackendMessage beMes = new BackendMessage(struct);
		return beMes;
	}

	protected OrderMgmtMessage transformMessageByRule(final BackendMessage beMsg, final MessageMappingRule rule)
	{

		if (rule.getResult().isHide())
		{
			// remove;
			return null;
		}
		else
		{
			if (!processCallbacks(rule, beMsg))
			{
				return null;
			}


			final MessageMappingRule.Result result = rule.getResult();

			String beSeverity;
			if (result.getSeverity() != null)
			{
				beSeverity = String.valueOf(result.getSeverity());
			}
			else
			{
				beSeverity = beMsg.getBeSeverity();
			}
			final OrderMgmtMessage msg = new OrderMgmtMessage(msgTypeBe2Java(beSeverity));

			takeOverAllwaysPassedAttributes(beMsg, msg);

			if (result.getResourceKey() != null)
			{
				msg.setResourceKey(result.getResourceKey());
			}
			else
			{
				msg.setDescription(beMsg.getMessageText());
			}

			//set process step
			msg.setProcessStep(rule.result.processStep);

			return msg;
		}
	}

	/**
	 * @param rule
	 * @param beMsg
	 */
	private boolean processCallbacks(final MessageMappingRule rule, final BackendMessage beMsg)
	{
		if (messageMappingRulesContainer.getCallbacks() != null)
		{
			final MessageMappingCallbackProcessor callback = messageMappingRulesContainer.getCallbacks().get(
					rule.result.getCallbackId());
			if (callback != null)
			{
				return callback.process(beMsg);
			}
		}
		return true;

	}



	protected OrderMgmtMessage passMessage(final BackendMessage beMsg)
	{
		final OrderMgmtMessage msg = new OrderMgmtMessage(msgTypeBe2Java(beMsg.getBeSeverity()));
		takeOverAllwaysPassedAttributes(beMsg, msg);
		msg.setDescription(beMsg.getMessageText());
		return msg;
	}

	protected void takeOverAllwaysPassedAttributes(final BackendMessage beMsg, final OrderMgmtMessage msg)
	{
		msg.setRefTechKey(beMsg.getRefTechKey());
		msg.setResourceArgs(beMsg.getVars());
		msg.setTechKey(new TechKey(beMsg.getBeClass() + ' ' + beMsg.getBeNumber()));
		// msg.setProperty(property);
	}

	protected boolean isMessageReferRootBO(final BusinessObject bo, final BackendMessage msg)
	{
		final TechKey key = msg.getRefTechKey();

		if (key == null || key.isInitial())
		{
			// nothing can be found.
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("message " + msg + "have no tech key; force to refer to root BO " + bo);
			}
			return true;

		}
		else
		{
			return key.equals(bo.getTechKey()) //
					|| key.toString().equals("HEAD") //
					|| key.toString().equals(bo.getHandle());
		}
	}

	protected boolean isMessageReferChildBO(final BusinessObject bo, final BackendMessage msg)
	{
		return bo.getTechKey() != null && bo.getTechKey().equals(msg.getRefTechKey());
	}

	protected BusinessObject findReferredBO(final BusinessObject rootBo, final BackendMessage msg)
	{
		sapLogger.entering("findReferencedBO(BusinessObject rootBo, ErpMessage mes)");

		if (isMessageReferRootBO(rootBo, msg))
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("Message " + msg + " belongs to header " + rootBo);
			}
			sapLogger.exiting();
			return rootBo;
		}
		else
		{
			// check shields
			@SuppressWarnings("unchecked")
			final Iterator<Object> it = rootBo.getSubObjectIterator();
			while (it.hasNext())
			{
				// must be BusinessObject
				final BusinessObject childBo = (BusinessObject) it.next();
				if (isMessageReferChildBO(childBo, msg))
				{
					if (sapLogger.isDebugEnabled())
					{
						sapLogger.debug("Message " + msg + " belongs to chiled BO " + childBo);
					}
					sapLogger.exiting();
					return childBo;
				}
			}
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("Message " + msg + " do not belongs to " + rootBo + " or its chields, but forced to refer to it");
			}
			sapLogger.exiting();
			return rootBo;
		}
	}

	/**
	 * Returns a relative scale of severity.
	 * <p>
	 * Concrete value is unimportant. Defined for severity comparison.
	 */
	protected static int severityScale(final char beSeverity)
	{
		switch (beSeverity)
		{
			case BAPI_RETURN_INFO:
				return 10;
			case BAPI_RETURN_SUCCESS:
				return 20;
			case BAPI_RETURN_WARNING:
				return 30;
			case BAPI_RETURN_ERROR:
				return 40;
			case BAPI_RETURN_ABORT:
				return 50;
			default:
				return 00;
		}
	}

}
