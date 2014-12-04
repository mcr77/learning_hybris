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

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.TechKey;

import java.util.Arrays;

import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoRecord;
import com.sap.tc.logging.Severity;


public class BackendMessage
{

	protected static final Log4JWrapper LOC = Log4JWrapper.getInstance(BackendMessage.class.getName());

	final public static class FIELDS
	{
		static public final String //
				TYPE = "MSGTY", //
				CLASS = "MSGID", //
				NUMBER = "MSGNO", //
				V1 = "MSGV1", //
				V2 = "MSGV2", //
				V3 = "MSGV3", //
				V4 = "MSGV4", //
				TEXT[] =
				{ "T_MSG", "TEXT" }, //
				REF_TECH_KEY[] =
				{ "HANDLE_ITEM", "HANDLE", // structure
						"OBJECT", "EXTNUMBER" // table
				};
	}

	protected String beSeverity;
	protected String beClass;
	protected String beNumber;
	protected final String[] vars = new String[4];
	protected String messageText;
	protected TechKey refTechKey;

	public BackendMessage(final JCoRecord struct)
	{

		final JCoMetaData meta = struct.getMetaData();

		beSeverity = struct.getString(FIELDS.TYPE);
		beClass = struct.getString(FIELDS.CLASS);
		beNumber = struct.getString(FIELDS.NUMBER);

		if ("A".equals(beSeverity) || "E".equals(beSeverity))
		{
			final String beMessage = "Back end error message: ";
			if (meta.hasField("TEXT"))
			{
				LOC.log(Severity.ERROR, LogCategories.APPLICATIONS, beMessage + struct.getString("TEXT"));
			}
			else
			{
				LOC.log(Severity.ERROR, LogCategories.APPLICATIONS, beMessage + beClass + "," + beNumber);
			}
		}

		vars[0] = struct.getString(FIELDS.V1);
		vars[1] = struct.getString(FIELDS.V2);
		vars[2] = struct.getString(FIELDS.V3);
		vars[3] = struct.getString(FIELDS.V4);


		final String fieldNameMessageText = determineFirstExistingFieldNameInJcoRecord(meta, FIELDS.TEXT);
		if (fieldNameMessageText != null)
		{
			messageText = struct.getString(fieldNameMessageText);
		}
		else
		{
			messageText = null;
		}

		final String notEmptyFieldForRefTechKey = determineFirstExistingFieldNameAndEmptyFieldinJcoRecord(meta, struct,
				FIELDS.REF_TECH_KEY);
		if (notEmptyFieldForRefTechKey != null)
		{
			refTechKey = JCoHelper.getTechKey(struct, notEmptyFieldForRefTechKey);
		}
		else
		{
			refTechKey = null;
		}
	}


	public BackendMessage(final String msgType, final String msgId, final String msgNo, final String msgV1, final String msgV2,
			final String msgV3, final String msgV4)
	{

		this.beSeverity = msgType;
		this.beClass = msgId;
		this.beNumber = msgNo;

		vars[0] = msgV1;
		vars[1] = msgV2;
		vars[2] = msgV3;
		vars[3] = msgV4;

	}

	static final String determineFirstExistingFieldNameInJcoRecord(final JCoMetaData meta, final String[] fieldsNames)
	{
		for (final String fName : fieldsNames)
		{
			if (meta.hasField(fName))
			{
				return fName;
			}
		}
		return null;
	}

	static final String determineFirstExistingFieldNameAndEmptyFieldinJcoRecord(final JCoMetaData meta, final JCoRecord struct,
			final String[] fieldsNames)
	{
		for (final String fName : fieldsNames)
		{
			if (meta.hasField(fName) && !struct.getString(fName).isEmpty())
			{
				return fName;
			}
		}
		return null;
	}

	public String getBeSeverity()
	{
		return beSeverity;
	}

	public String getBeClass()
	{
		return beClass;
	}

	public String getBeNumber()
	{
		return beNumber;
	}

	public TechKey getRefTechKey()
	{
		return refTechKey;
	}

	public String[] getVars()
	{
		return vars;
	}

	public String getMessageText()
	{
		return messageText;
	}

	public boolean isEmpty()
	{
		return (beSeverity == null || beSeverity.isEmpty()) && (beClass == null || beClass.isEmpty())
				&& (beNumber == null || beNumber.isEmpty() || beNumber.equals("000"));
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(vars);
		result = prime * result + ((beClass == null) ? 0 : beClass.hashCode());
		result = prime * result + ((beNumber == null) ? 0 : beNumber.hashCode());
		result = prime * result + ((beSeverity == null) ? 0 : beSeverity.hashCode());
		result = prime * result + ((messageText == null) ? 0 : messageText.hashCode());
		result = prime * result + ((refTechKey == null) ? 0 : refTechKey.hashCode());
		return result;
	}


	@Override
	public boolean equals(final Object object)
	{
		if (object == null)
		{
			return false;
		}
		if (object == this)
		{
			return true;
		}
		if (!(object instanceof BackendMessage))
		{
			return false;
		}

		final BackendMessage message = (BackendMessage) object;

		if (this.beSeverity.equals(message.beSeverity) && this.beNumber.equals(message.beNumber)
				&& this.beClass.equals(message.beClass) && this.vars[0].equals(message.vars[0])
				&& this.vars[1].equals(message.vars[1]) && this.vars[2].equals(message.vars[2])
				&& this.vars[3].equals(message.vars[3]))
		{
			return true;
		}
		return false;
	}






}
