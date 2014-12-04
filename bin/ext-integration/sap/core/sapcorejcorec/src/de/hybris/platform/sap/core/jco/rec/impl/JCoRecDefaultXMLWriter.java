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

import de.hybris.platform.sap.core.jco.rec.JCoRecXMLConstants;
import de.hybris.platform.sap.core.jco.rec.JCoRecXMLWriter;
import de.hybris.platform.sap.core.jco.rec.JCoRecXMLWriterException;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoRecordMetaData;


/**
 * This implementation of {@link JCoRecXMLWriter} is the default xml writer used in JCoRec.
 */

class JCoRecDefaultXMLWriter implements JCoRecXMLWriter
{

	private DocumentBuilder docBuilder;

	/**
	 * Standard Constructor that initializes {@link #docBuilder}.
	 */
	public JCoRecDefaultXMLWriter()
	{
		try
		{
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (final ParserConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Writes the Objects (i.e. {@link JCoRecordMetaData}, {@link JCoFunctionTemplate}, {@link JCoFunction},
	 * {@link JCoRecord}) available in the given {@link JCoRecRepository} to a xml {@link File}
	 * 
	 * @param repository
	 *           the {@link JCoRecRepository} to persist
	 * @param file
	 *           the output xml {@link File}
	 * @throws JCoRecXMLWriterException
	 *            if the TransformerFactory throws an exception.
	 * @see de.hybris.platform.sap.core.jco.rec.JCoRecXMLWriter#write(JCoRecRepository, File)
	 */
	@Override
	public void write(final JCoRecRepository repository, final File file) throws JCoRecXMLWriterException
	{
		final Document doc = getNewDocument();

		if (file == null)
		{
			throw new IllegalArgumentException("File is not supposed to be null");
		}

		final Element root = doc.createElement(JCoRecXMLConstants.TAGNAME_REPOSITORY_ROOT);
		//		JCoRecMetaDataWriter.writeMetaDataToDocument(repository.getMetaDataRepository(), root);
		new JCoRecMetaDataWriter().writeMetaDataToDocument(repository.getMetaDataRepository(), root);
		//		JCoRecFunctionWriter.functionsToXMLDocument(repository, root);
		new JCoRecFunctionWriter().functionsToXMLDocument(repository, root);
		doc.appendChild(root);

		file.getParentFile().mkdirs(); // make sure that the directory of the
		// file exists
		final Source source = new DOMSource(doc);
		final Result result = new StreamResult(file);
		Transformer xformer;
		try
		{
			final TransformerFactory factory = TransformerFactory.newInstance();
			// factory.setAttribute("indent-number", new Integer(1));

			xformer = factory.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			xformer.transform(source, result);
		}
		catch (final TransformerConfigurationException e)
		{
			throw new JCoRecXMLWriterException(e);
		}
		catch (final TransformerFactoryConfigurationError e)
		{
			throw new JCoRecXMLWriterException(e);
		}
		catch (final TransformerException e)
		{
			throw new JCoRecXMLWriterException(e);
		}
	}

	/**
	 * Getter for a new Document from the local DocumentBuilder {@link #docBuilder}.
	 * 
	 * @return the new Document instance.
	 */
	private Document getNewDocument()
	{
		return docBuilder.newDocument();
	}
}
