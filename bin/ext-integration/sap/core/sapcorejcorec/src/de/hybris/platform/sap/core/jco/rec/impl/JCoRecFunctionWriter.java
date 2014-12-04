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

import de.hybris.platform.sap.core.common.DocumentBuilderFactoryUtil;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.rec.JCoRecXMLConstants;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRecord;


/**
 * This class provides functionality to write a Repositoy to a DOM element.
 */
class JCoRecFunctionWriter
{

	/**
	 * Writes a JCoRecRepository to a DOM element.
	 * 
	 * @param repository
	 *           the JCoRecRepository to write
	 * @param root
	 *           the DOM Element where to append the given JCoRecRepository
	 */
	public void functionsToXMLDocument(final JCoRecRepository repository, final Element root)
	{
		final Document doc = root.getOwnerDocument();
		final Element functionRoot = doc.createElement(JCoRecXMLConstants.TAGNAME_FUNCTION_ROOT);
		root.appendChild(functionRoot);

		final Map<String, JCoRecFunctionDecorator> calls = repository.getFunctions();
		for (final Iterator<JCoRecFunctionDecorator> iterator = calls.values().iterator(); iterator.hasNext();)
		{
			final JCoRecFunctionDecorator function = iterator.next();
			RFCtoXMLDocument(function, functionRoot);
		}

		final Element recordsRoot = doc.createElement(JCoRecXMLConstants.TAGNAME_RECORDS_ROOT);
		root.appendChild(recordsRoot);

		final Map<String, JCoRecord> records = repository.getRecords();
		for (final Iterator<Entry<String, JCoRecord>> iterator = records.entrySet().iterator(); iterator.hasNext();)
		{
			final Entry<String, JCoRecord> record = iterator.next();
			RecordtoXMLDocument(record.getValue(), record.getKey(), recordsRoot);
		}

	}

	/**
	 * Writes a single Record (ie. Tables or Structures) to a DOM Element
	 * 
	 * @param record
	 *           the Record to write
	 * @param key
	 *           value of the key-attribute
	 * @param recordsRoot
	 *           the DOM Element where to append the record
	 */
	private void RecordtoXMLDocument(final JCoRecord record, final String key, final Element recordsRoot)
	{
		final String stringXML = record.toXML();
		final Element recordNode = (Element) xmlStringToNode(recordsRoot, stringXML);
		recordNode.setAttribute("key", key);
		recordsRoot.appendChild(recordNode);
	}

	/**
	 * Writes a single JCoRecFunctionDecorator Object to a DOM Element.<br>
	 * This Method therefor creates a new DOM Element, setting it's attributes and then parses the functions
	 * parameterlists by calling writeRFCtoElementNode(JCoFunction func, Element root)
	 * 
	 * @param function
	 *           the JCoRecFunctionDecorator to write
	 * @param parent
	 *           the DOM Element where to append the JCoRecFunctionDecorator
	 */
	private void RFCtoXMLDocument(final JCoRecFunctionDecorator function, final Element parent)
	{
		final Document doc = parent.getOwnerDocument();
		// create the root element and add it to the document
		final Element functionRoot = doc.createElement(JCoRecXMLConstants.TAGNAME_SINGLE_RFC);
		functionRoot.setAttribute(JCoRecXMLConstants.ATTNAME_FUNCTION_NAME, function.getName());
		functionRoot.setAttribute(JCoRecXMLConstants.ATTNAME_REPO_KEY, function.getRepository().getRepositoryKey());
		functionRoot.setAttribute(JCoRecXMLConstants.ATTNAME_RECORD_TIME, String.valueOf(function.getTimestamp()));
		functionRoot.setAttribute(JCoRecXMLConstants.ATTNAME_FUNCTION_KEY, function.getFunctionKey());
		parent.appendChild(functionRoot);
		writeRFCtoElementNode(function, functionRoot);
	}

	/**
	 * Calls the toXML()-Method of all parameterlists from the given JCoFunction and append the results to a DOM Element.
	 * 
	 * @param func
	 *           the content of this function should be added to the DOM Element.
	 * @param root
	 *           the node that should contain the data from the function.
	 */
	private void writeRFCtoElementNode(final JCoFunction func, final Element root)
	{
		final Element functionNode = root;

		// Add Input parameters
		if (func.getImportParameterList() != null)
		{
			final String in = func.getImportParameterList().toXML();
			appendXmlFragment(functionNode, in);
		}
		// Add Output parameters
		if (func.getExportParameterList() != null)
		{
			final String in = func.getExportParameterList().toXML();
			appendXmlFragment(functionNode, in);
		}
		// Add tables parameters
		if (func.getTableParameterList() != null)
		{
			final String in = func.getTableParameterList().toXML();
			appendXmlFragment(functionNode, in);
		}
		// Add changing parameters
		if (func.getChangingParameterList() != null)
		{
			final String in = func.getChangingParameterList().toXML();
			appendXmlFragment(functionNode, in);
		}
		// Add exception parameters
		if (func.getExceptionList() != null)
		{
			// Write ExceptionList to XML
		}
	}

	/**
	 * Creates a new node from the XML String {@code fragment} and adds it to the children of the given node @{code
	 * parent}.
	 * 
	 * @param parent
	 *           the node that should contain the new parsed node as child element.
	 * @param fragment
	 *           the XML String that should be parsed
	 */
	private void appendXmlFragment(final Node parent, final String fragment)
	{
		final Node fragmentNode = xmlStringToNode(parent, fragment);
		//removeEmptyTags(fragmentNode);
		parent.appendChild(fragmentNode);
	}

	/**
	 * Creates a Node for the given XML String.
	 * 
	 * @param parent
	 *           the node that should has the new node as child.
	 * @param fragment
	 *           the XML String that should be parsed.
	 * @return the generated node.
	 */
	private Node xmlStringToNode(final Node parent, final String fragment)
	{
		final Document doc = parent.getOwnerDocument();
		Node fragmentNode;
		try
		{

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilderFactoryUtil.setSecurityFeatures(factory);

			fragmentNode = factory.newDocumentBuilder().parse(new InputSource(new StringReader(fragment))).getDocumentElement();

		}
		catch (final SAXException e)
		{
			throw new ApplicationBaseRuntimeException("Not handled '" + e.getClass().getName() + "' exception.", e);
		}
		catch (final IOException e)
		{
			throw new ApplicationBaseRuntimeException("Not handled '" + e.getClass().getName() + "' exception.", e);
		}
		catch (final ParserConfigurationException e)
		{
			throw new ApplicationBaseRuntimeException("Not handled '" + e.getClass().getName() + "' exception.", e);
		}
		fragmentNode = doc.importNode(fragmentNode, true);
		return fragmentNode;
	}
}
