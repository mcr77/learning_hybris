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
import de.hybris.platform.sap.core.jco.rec.JCoRecXMLParserException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.util.Codecs;


/**
 * This class provides functionality to parse a DOM document.
 */
class JCoRecFunctionParser
{
	private final String TYPE_EMPTY_STRUCTURE = "emptyStructure";
	private final String TYPE_EMPTY_TABLE = "emptyTable";
	private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private Transformer transformer;


	/**
	 * Parses functions from a JCoRecorder repository file.
	 * 
	 * @param doc
	 *           the parsed content will be added to this document.
	 * @param repo
	 *           the JCoRecorder repository that should be parsed.
	 * @throws JCoRecXMLParserException
	 * <br/>
	 *            if<br/>
	 *            <ul>
	 *            <li>{@code repo == null}</li>
	 *            <li>the xml-transformer can't be initialized</li>
	 *            <li>the parsing of the content raises an exception</li>
	 *            <li>the repository contains unexpected content</li>
	 *            </ul>
	 */
	public void parseRepository(final Document doc, final JCoRecRepository repo) throws JCoRecXMLParserException
	{
		if (repo.getMetaDataRepository() == null)
		{
			throw new JCoRecXMLParserException("No MetaData in JCoRecRepository " + repo.getRepositoryKey() + " available");
		}

		if (transformer == null)
		{
			try
			{
				transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			}
			catch (final TransformerConfigurationException e)
			{
				throw new JCoRecXMLParserException(e);
			}
		}
		// NodeList list =
		// doc.getElementsByTagName(JCoRecXMLConstants.TAGNAME_SINGLE_RFC);
		final Element root = doc.getDocumentElement();

		final NodeList list = root.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node currNode = list.item(i);

			final String tagName = currNode.getNodeName();
			if (tagName.equalsIgnoreCase(JCoRecXMLConstants.TAGNAME_FUNCTION_ROOT))
			{
				parseFunctions(currNode.getChildNodes(), repo);
			}
			else if (tagName.equalsIgnoreCase(JCoRecXMLConstants.TAGNAME_RECORDS_ROOT))
			{
				parseRecords(currNode.getChildNodes(), repo);
			}
			else if (tagName.equalsIgnoreCase(JCoRecXMLConstants.TAGNAME_METADATA_ROOT))
			{
				continue;
			}
			else
			{
				throw new JCoRecXMLParserException("Expected " + JCoRecXMLConstants.TAGNAME_FUNCTION_ROOT + " or "
						+ JCoRecXMLConstants.TAGNAME_RECORDS_ROOT + " but found " + tagName);
			}
		}

	}

	/**
	 * Parses all records from the {@code list} and adds them to the {@code repo}.
	 * 
	 * @param list
	 *           the list that should be parsed.
	 * @param repo
	 *           the repo containing the records afterwards.
	 * @throws JCoRecXMLParserException
	 *            if a JCoRecXMLParserException is thrown while parsing a single record, or if there is data for a record
	 *            missing.
	 */
	private void parseRecords(final NodeList list, final JCoRecRepository repo) throws JCoRecXMLParserException
	{
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node record = list.item(i);
			if (record == null)
			{
				continue;
			}
			final String recordType = record.getNodeName();
			final String recordKey = record.getAttributes().getNamedItem("key").getNodeValue();
			String emptyStructOrTable = "default";
			if (record.getAttributes().getNamedItem("type") != null)
			{
				emptyStructOrTable = record.getAttributes().getNamedItem("type").getNodeValue();
			}
			JCoRecord newRecord = null;
			JCoRecordMetaData metaData = null;

			try
			{
				metaData = repo.getMetaDataRepository().getRecordMetaData(recordType);
				if (metaData == null)
				{
					throw new JCoRecRuntimeException("Meta data not found: " + recordType);
				}

				if (emptyStructOrTable.equalsIgnoreCase(TYPE_EMPTY_STRUCTURE))
				{
					newRecord = JCo.createStructure(metaData);
					repo.put(recordKey, newRecord);
					continue;
				}
				else if (emptyStructOrTable.equalsIgnoreCase(TYPE_EMPTY_TABLE))
				{
					newRecord = JCo.createTable(metaData);
					repo.put(recordKey, newRecord);
					continue;
				}
				else if (record.getFirstChild() != null && record.getFirstChild().getNodeName().equals("item"))
				{
					// Table
					newRecord = JCo.createTable(metaData);
				}
				else
				{
					// Structure
					newRecord = JCo.createStructure(metaData);
				}
				parseJCoRecord(record, newRecord);
				if (newRecord instanceof JCoTable)
				{
					((JCoTable) newRecord).firstRow();
				}
				repo.put(recordKey, newRecord);
			}
			catch (final JCoException e)
			{
				throw new JCoRecXMLParserException("Meta data not found: " + recordType, e);
			}

		}
	}

	/**
	 * Parses all functions from the {@code list} and adds them to the {@code repo}.
	 * 
	 * @param list
	 *           the list that should be parsed.
	 * @param repo
	 *           the repo containing the functions afterwards.
	 * @throws JCoRecXMLParserException
	 *            if there is data for a function missing.
	 */
	private void parseFunctions(final NodeList list, final JCoRecRepository repo) throws JCoRecXMLParserException
	{
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node rfc = list.item(i);
			String functionKey = "";
			String functionName;
			long timestamp;

			final Node functionNameNode = rfc.getAttributes().getNamedItem(JCoRecXMLConstants.ATTNAME_FUNCTION_NAME);
			final Node timestampNode = rfc.getAttributes().getNamedItem(JCoRecXMLConstants.ATTNAME_RECORD_TIME);
			final Node functionKeyNode = rfc.getAttributes().getNamedItem(JCoRecXMLConstants.ATTNAME_FUNCTION_KEY);

			if (functionKeyNode == null)
			{
				throw new JCoRecXMLParserException("Missing Attribute (" + JCoRecXMLConstants.ATTNAME_FUNCTION_KEY + ") in "
						+ rfc.getNodeName());
			}
			if (timestampNode == null)
			{
				throw new JCoRecXMLParserException("Missing Attribute (" + JCoRecXMLConstants.ATTNAME_RECORD_TIME + ") in "
						+ rfc.getNodeName());
			}
			if (functionNameNode == null)
			{
				throw new JCoRecXMLParserException("Missing Attribute (" + JCoRecXMLConstants.ATTNAME_FUNCTION_NAME + ") in "
						+ rfc.getNodeName());
			}

			functionName = functionNameNode.getNodeValue();
			timestamp = Long.valueOf(timestampNode.getNodeValue()).longValue();
			functionKey = functionKeyNode.getNodeValue();

			JCoFunction newFunc;
			try
			{
				final JCoFunctionTemplate functionTemplate = repo.getMetaDataRepository().getFunctionTemplate(functionName);
				if (functionTemplate == null)
				{
					throw new JCoRecXMLParserException("No Function Template available for " + functionName);
				}
				newFunc = functionTemplate.getFunction();
			}
			catch (final JCoException e)
			{
				throw new JCoRecXMLParserException("No Function Template available for " + functionName, e);
			}

			final NodeList parameters = rfc.getChildNodes();
			for (int j = 0; j < parameters.getLength(); j++)
			{
				final Node param = parameters.item(j);

				if (param.getNodeName().equalsIgnoreCase(JCoRecXMLConstants.TAGNAME_IMPORT_PARAM))
				{
					parseJCoRecord(param, newFunc.getImportParameterList());
				}
				else if (param.getNodeName().equalsIgnoreCase(JCoRecXMLConstants.TAGNAME_EXPORT_PARAM))
				{
					parseJCoRecord(param, newFunc.getExportParameterList());
				}
				else if (param.getNodeName().equalsIgnoreCase(JCoRecXMLConstants.TAGNAME_TABLE_PARAM))
				{

					parseJCoRecord(param, newFunc.getTableParameterList());
				}
				else if (param.getNodeName().equalsIgnoreCase(JCoRecXMLConstants.TAGNAME_CHANGING_PARAM))
				{

					parseJCoRecord(param, newFunc.getChangingParameterList());
				}
				else if (param.getNodeName().equalsIgnoreCase(JCoRecXMLConstants.TAGNAME_EXCEPTION_PARAM))
				{
					throw new JCoRecXMLParserException("Parameterlist \"exceptions\" is not supported");
				}
				else
				{
					throw new JCoRecXMLParserException("Unexpected Tag: " + param.getNodeName());
				}
			}
			repo.put(newFunc, repo, timestamp, functionKey);//function);
		}

	}

	/**
	 * Parses a Node to the given JCoRecord.
	 * 
	 * @param param
	 *           the Node to be parsed
	 * @param record
	 *           the JCoRecord to append the parsed values to
	 * @throws JCoRecXMLParserException
	 *            if a JCoRecXMLParserException is thrown while parsing the contents.
	 */
	private void parseJCoRecord(final Node param, final JCoRecord record) throws JCoRecXMLParserException
	{

		final NodeList nodes = param.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			final Node currNode = nodes.item(i);
			final String recordName = currNode.getNodeName();

			if (currNode.getNodeType() == Node.ELEMENT_NODE)
			{
				if (currNode.hasChildNodes())
				{
					final boolean item = currNode.getNodeName().equals("item");
					if (currNode.getFirstChild().getNodeType() == Node.TEXT_NODE)
					{
						// Field
						if (item)
						{
							final String set = currNode.getFirstChild().getNodeValue();
							((JCoTable) record).appendRow();
							record.setValue("", set);
						}
						else
						{
							String set = currNode.getTextContent();
							final JCoMetaData metaData = record.getMetaData();
							final int type = metaData.getType(recordName);
							switch (type)
							{
								case JCoMetaData.TYPE_BYTE:
									record.setValue(recordName, Codecs.Base64.decode(set));
									break;

								case JCoMetaData.TYPE_INT1:
								case JCoMetaData.TYPE_INT2:
								case JCoMetaData.TYPE_INT:
									record.setValue(recordName, Integer.parseInt(set));
									break;

								case JCoMetaData.TYPE_BCD:
									set = handleBCD(set);
									record.setValue(recordName, set);
									break;

								case JCoMetaData.TYPE_NUM:
									record.setValue(recordName, set);
									break;

								//case JCoMetaData.TYPE_ABAPOBJECT: //Does not exist in sapjco3.jar (Issue with different Versions)
								//case JCoMetaData.TYPE_BOX: //Does not exist in sapjco3.jar (Issue with different Versions)
								case JCoMetaData.TYPE_CHAR:
								case JCoMetaData.TYPE_DATE:
								case JCoMetaData.TYPE_DECF16:
								case JCoMetaData.TYPE_DECF34:
								case JCoMetaData.TYPE_EXCEPTION:
								case JCoMetaData.TYPE_FLOAT:
									//case JCoMetaData.TYPE_GENERIC_BOX: //Does not exist in sapjco3.jar (Issue with different Versions)
								case JCoMetaData.TYPE_TIME:
								case JCoMetaData.TYPE_STRING:
								case JCoMetaData.TYPE_XSTRING:
								default:
									record.setValue(recordName, set);
									break;

								case JCoMetaData.TYPE_TABLE:
								case JCoMetaData.TYPE_STRUCTURE:
								case JCoMetaData.UNINITIALIZED:
									throw new JCoRecXMLParserException("Unexpected Failure: Type " + metaData.getTypeAsString(recordName)
											+ "(" + recordName + " ; " + metaData.getName() + ") not supported in this context ... BUG!?");
							}
						}
					}
					else if (item)
					{
						// TableRow
						((JCoTable) record).appendRow();
						parseJCoRecord(currNode, record);
					}
					else
					{
						if (currNode.getFirstChild().getNodeName().equals("item")
								&& currNode.getLastChild().getNodeName().equals("item"))
						{
							// Table
							parseJCoRecord(currNode, record.getTable(recordName));
						}
						else
						{
							// Structure
							parseJCoRecord(currNode, record.getStructure(recordName));
						}
					}
				}
				else
				{
					continue;
				}
			}
			else
			{
				throw new JCoRecXMLParserException("Expected an Element Node but found " + recordName);
			}
		}
	}

	/**
	 * Type BCD needs to have decimal places even if the value is integer.
	 * 
	 * @param set
	 *           The string to handle as BCD
	 * @return a String representing a BCD with decimal places
	 */
	private String handleBCD(final String set)
	{
		return set.contains(".") == false ? set + ".0" : set;
	}
}
