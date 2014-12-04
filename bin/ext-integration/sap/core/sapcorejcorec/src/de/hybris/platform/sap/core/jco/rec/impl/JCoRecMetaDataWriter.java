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
package de.hybris.platform.sap.core.jco.rec.impl;

import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.JCoRecXMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoRecordMetaData;


/**
 * This class provides functionality to write JCoMetaData to a DOM Document.
 */
class JCoRecMetaDataWriter
{
	/**
	 * Writes the metadata from the {@code metaDataRepository} to the DOM document by appending it to the {@code root}
	 * element.
	 * 
	 * @param metaDataRepository
	 *           the repository that should be written.
	 * @param root
	 *           the root element of the DOM document.
	 */
	public void writeMetaDataToDocument(final JCoCustomRepository metaDataRepository, final Element root)
	{
		final Document doc = root.getOwnerDocument();
		final Element metaDataRoot = doc.createElement(JCoRecXMLConstants.TAGNAME_METADATA_ROOT);

		final Element functionTemplateRoot = doc.createElement(JCoRecXMLConstants.TAGNAME_METADATA_TEMPLATE_ROOT);
		metaDataRoot.appendChild(functionTemplateRoot);

		final Element recordRoot = doc.createElement(JCoRecXMLConstants.TAGNAME_METADATA_RECORD_ROOT);
		metaDataRoot.appendChild(recordRoot);

		root.appendChild(metaDataRoot);

		final String[] templatesA = metaDataRepository.getCachedFunctionTemplateNames();
		final String[] recordsA = metaDataRepository.getCachedRecordMetaDataNames();

		for (int i = 0; i < templatesA.length; i++)
		{
			JCoFunctionTemplate templ;
			try
			{
				templ = metaDataRepository.getFunctionTemplate(templatesA[i]);
			}
			catch (final JCoException e)
			{
				throw new JCoRecRuntimeException(e);
			}
			writeFunction(templ, functionTemplateRoot);
		}
		for (int i = 0; i < recordsA.length; i++)
		{
			JCoRecordMetaData record;
			try
			{
				record = metaDataRepository.getRecordMetaData(recordsA[i]);
			}
			catch (final JCoException e)
			{
				throw new JCoRecRuntimeException(e);
			}
			writeRecord(record, recordRoot);
		}
	}

	/**
	 * Writes the given record to the nodes child list.
	 * 
	 * @param recordMetaData
	 *           the metadata that should be written.
	 * @param root
	 *           the parent node of the new child node.
	 */
	private void writeRecord(final JCoRecordMetaData recordMetaData, final Element root)
	{
		final Document doc = root.getOwnerDocument();
		final Element metaDataElement = doc.createElement(JCoRecXMLConstants.TAGNAME_METADATA_RECORD);
		root.appendChild(metaDataElement);
		metaDataElement.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_FIELDNAME, recordMetaData.getName());
		writeRecordMetaData(recordMetaData, metaDataElement, true);
	}

	/**
	 * Writes the given function to the nodes child list.
	 * 
	 * @param functionTemplate
	 *           the function that should be written.
	 * @param root
	 *           the parent node of the new child node.
	 */
	private void writeFunction(final JCoFunctionTemplate functionTemplate, final Element root)
	{
		if (functionTemplate == null)
		{
			return;
		}
		final Document doc = root.getOwnerDocument();

		final Element metaDataFunction = doc.createElement(JCoRecXMLConstants.TAGNAME_METADATA_FUNCTION);
		metaDataFunction.setAttribute(JCoRecXMLConstants.ATTNAME_FUNCTION_NAME, functionTemplate.getName());

		final Element metaDataInput = doc.createElement(JCoRecXMLConstants.TAGNAME_METADATA_INPUT);
		final Element metaDataOutput = doc.createElement(JCoRecXMLConstants.TAGNAME_METADATA_OUTPUT);
		final Element metaDataChanging = doc.createElement(JCoRecXMLConstants.TAGNAME_METADATA_CHANGING);
		final Element metaDataTable = doc.createElement(JCoRecXMLConstants.TAGNAME_METADATA_TABLE);

		final JCoListMetaData input = functionTemplate.getImportParameterList();
		final JCoListMetaData output = functionTemplate.getExportParameterList();
		final JCoListMetaData changing = functionTemplate.getChangingParameterList();
		final JCoListMetaData table = functionTemplate.getTableParameterList();

		writeParameterList(input, metaDataInput);
		writeParameterList(output, metaDataOutput);
		writeParameterList(changing, metaDataChanging);
		writeParameterList(table, metaDataTable);

		metaDataFunction.appendChild(metaDataInput);
		metaDataFunction.appendChild(metaDataOutput);
		metaDataFunction.appendChild(metaDataChanging);
		metaDataFunction.appendChild(metaDataTable);

		root.appendChild(metaDataFunction);

	}

	/**
	 * Writes the parameterlist to the root elements child list.
	 * 
	 * @param input
	 *           the written parameterlist.
	 * @param root
	 *           the parent node of the new child node.
	 */
	private void writeParameterList(final JCoListMetaData input, final Element root)
	{
		writeRecordMetaData(input, root, true);
	}

	/**
	 * Writes the metadata to the root elements child list.
	 * 
	 * @param metaData
	 *           the data that should be written.
	 * @param root
	 *           the node that should contain the metadata afterwards.
	 * @param explicit
	 *           if this is true, additionally the length of the data is put into the child node.
	 */
	private void writeRecordMetaData(final JCoMetaData metaData, final Element root, final boolean explicit)
	{
		if (metaData == null)
		{
			return;
		}

		final Document document = root.getOwnerDocument();
		for (int i = 0; i < metaData.getFieldCount(); i++)
		{
			final Element field = document.createElement(JCoRecXMLConstants.TAGNAME_METADATA_FIELD);
			root.appendChild(field);

			final String recordTypeName = metaData.getRecordTypeName(i);
			field.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_RECORDTYPE, recordTypeName);
			field.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_FIELDNAME, metaData.getName(i));

			if (explicit == true)
			{
				field.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_NUC_BYTELENGTH, String.valueOf(metaData.getByteLength(i)));
				field.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_UC_BYTELENGTH,
						String.valueOf(metaData.getUnicodeByteLength(i)));

				if (metaData instanceof JCoRecordMetaData)
				{
					field.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_NUC_BYTEOFFSET,
							String.valueOf(((JCoRecordMetaData) metaData).getByteOffset(i)));
					field.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_UC_BYTELOFFSET,
							String.valueOf(((JCoRecordMetaData) metaData).getUnicodeByteOffset(i)));
				}

				final int decimals = metaData.getDecimals(i);
				if (decimals != 0)
				{
					field.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_DECIMALS, String.valueOf(decimals));
				}
				field.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_DESCRIPTION, metaData.getDescription(i));
				field.setAttribute(JCoRecXMLConstants.ATTNAME_METADATA_FIELDTYPE, String.valueOf(metaData.getType(i)));
			}
		}
	}
}
