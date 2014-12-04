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

import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.JCoRecXMLParser;
import de.hybris.platform.sap.core.jco.rec.JCoRecXMLParserException;
import de.hybris.platform.sap.core.jco.rec.JCoRecXMLWriter;
import de.hybris.platform.sap.core.jco.rec.JCoRecXMLWriterException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


/**
 * This represents the repository while it is held in memory.
 */
public class JCoRecRepository
{
	private JCoRecMode mode;

	private JCoRecXMLParser parser = new JCoRecDefaultXMLParser();
	private JCoRecXMLWriter writer = new JCoRecDefaultXMLWriter();
	//function template repository
	private final JCoCustomRepository metaDataRepository;
	private String repoKey = "default";
	/** counts the number of uses of the respective function. From functionName to number of uses. */
	private final Map<String, Integer> counter = new HashMap<String, Integer>();

	/** All executed functions. Identified by DestinationName+::+FunctionName+::+numberOfExecutions. */
	private final Map<String, JCoRecFunctionDecorator> functions = new HashMap<String, JCoRecFunctionDecorator>();
	private final Map<String, JCoRecord> records = new HashMap<String, JCoRecord>();


	/**
	 * Constructor.
	 * 
	 * @param metaData
	 *           the {@link #metaDataRepository} for the new repository.
	 * @param key
	 *           the {@link #repoKey} for the new repository.
	 */
	protected JCoRecRepository(final JCoCustomRepository metaData, final String key)
	{
		this.repoKey = key;
		this.metaDataRepository = metaData;
	}

	/**
	 * Calls the {@link #JCoRecRepository(JCoCustomRepository, String)} constructor with the parameter
	 * {@link JCo#createCustomRepository(String)} and the given {@code key}.<br/>
	 * The parameter {@code mode} is saved locally.
	 * <p>
	 * Furthermore {@link JCo#createCustomRepository(String)} is called with the paramter {@code key}.
	 * </p>
	 * 
	 * @param key
	 *           is used for the new repository.
	 * @param mode
	 *           the value for the local attribute {@link #mode}.
	 */
	public JCoRecRepository(final String key, final JCoRecMode mode)
	{
		this(JCo.createCustomRepository(key), key);
		this.mode = mode;
	}

	/**
	 * Getter for the property {@link #records}.
	 * 
	 * @return Returns the {@link #records}.
	 */
	protected Map<String, JCoRecord> getRecords()
	{
		return records;
	}

	/**
	 * Getter for the field {@link #functions}.
	 * 
	 * @return Returns the current map of functions.
	 */
	protected Map<String, JCoRecFunctionDecorator> getFunctions()
	{
		return functions;
	}

	/**
	 * Getter for the property {@link #repoKey}.
	 * 
	 * @return Returns the {@link #repoKey}.
	 */
	public String getRepositoryKey()
	{
		return repoKey;
	}

	/**
	 * Getter for the current {@link #metaDataRepository}.
	 * 
	 * @return Returns the current {@link #metaDataRepository}
	 */
	public JCoCustomRepository getMetaDataRepository()
	{
		return this.metaDataRepository;
	}

	/**
	 * Adds the {@code function} to the local {@link #metaDataRepository}.
	 * 
	 * @param function
	 *           the new function to add.
	 * @return Returns the previous mapping for the {@link JCoRecFunctionDecorator#getFunctionKey()
	 *         function.getFunctionKey()} or {@code null} if there was no mapping.
	 */
	public JCoRecFunctionDecorator put(final JCoRecFunctionDecorator function)
	{
		this.metaDataRepository.addFunctionTemplateToCache(function.getFunctionTemplate());
		return functions.put(function.getFunctionKey(), function);
	}

	/**
	 * This put function gives the same functionality as the {@link #put(JCoRecFunctionDecorator)}, but without the
	 * parameter mode.
	 * 
	 * @param org
	 *           parameter for the new {@link JCoRecFunctionDecorator}.
	 * @param repo
	 *           parameter for the new {@link JCoRecFunctionDecorator}.
	 * @param timestamp
	 *           parameter for the new {@link JCoRecFunctionDecorator}.
	 * @param functionKey
	 *           parameter for the new {@link JCoRecFunctionDecorator}.
	 * @return Returns the previous mapping for the {@link JCoRecFunctionDecorator#getFunctionKey()
	 *         function.getFunctionKey()} or {@code null} if there was no mapping.
	 */
	public JCoRecFunctionDecorator put(final JCoFunction org, final JCoRecRepository repo, final long timestamp,
			final String functionKey)
	{
		return this.put(new JCoRecFunctionDecorator(org, repo, timestamp, functionKey, mode));
	}

	/**
	 * Setter for the field {@link #parser}.
	 * 
	 * @param parser
	 *           the new value for the local parser.
	 */
	public void setParser(final JCoRecXMLParser parser)
	{
		this.parser = parser;
	}

	/**
	 * Setter for the field {@link #writer}.
	 * 
	 * @param writer
	 *           the new value for the local writer.
	 */
	public void setWriter(final JCoRecXMLWriter writer)
	{
		this.writer = writer;
	}

	/**
	 * Saves the current repository and all its contents to the repo file specified in
	 * {@code JCoRecManagedConnectionFactory.getKey()}.
	 * 
	 * @param output
	 *           the location where to save the repo.
	 */
	public void saveRepositoryFile(final File output)
	{
		try
		{
			writer.write(this, output);
		}
		catch (final JCoRecXMLWriterException e)
		{
			throw new JCoRecRuntimeException("Error writing " + output.getAbsolutePath(), e);
		}
	}

	/**
	 * Loads the saves repository and all its contents from the repo file specified in
	 * {@code JCoRecManagedConnectionFactory.getKey()}.
	 * 
	 * @param input
	 *           the location of the source repository file.
	 */
	public void parseRepositoryFile(final File input)
	{
		String filePath = "";
		try
		{
			filePath = input.getCanonicalPath();
			if (input.exists() == false)
			{
				throw new JCoRecRuntimeException("Cannot find recording file " + filePath);
			}

			parser.parse(this, input);
		}
		catch (final JCoRecXMLParserException e)
		{
			throw new JCoRecRuntimeException("Error parsing " + filePath, e);
		}
		catch (final IOException e)
		{
			throw new JCoRecRuntimeException("IOException during reading canonical path.", e);
		}
	}

	/**
	 * Retrieves a JCoFunction from this repository.
	 * 
	 * @param functionName
	 *           is the name of the file that should be retrieved.
	 * 
	 * @return Returns the JCoFunction wrapped in a {@link JCoRecFunctionDecorator} or {@code null} if no function is
	 *         present with this {@code functionName}.
	 */
	public JCoFunction getEmptyFunction(final String functionName)
	{
		JCoFunctionTemplate templ;
		try
		{
			templ = this.getMetaDataRepository().getFunctionTemplate(functionName);
		}
		catch (final JCoException e)
		{
			throw new JCoRecRuntimeException("Error retriving a JCoFunctionTemplate for " + functionName, e);
		}

		if (templ == null)
		{
			return null;
		}
		return templ.getFunction();
	}

	/**
	 * Retrieves a JCoFunction that got executed in the backend before.<br/>
	 * The function is identified by it's function key.
	 * 
	 * @param functionKey
	 *           to identify an recorded JCoFunction
	 * @return Returns the function with the {@code funcktionKey} from the field {@link #functions}.
	 */
	public JCoRecFunctionDecorator getExecutedFunctionByKey(final String functionKey)
	{
		return functions.get(functionKey);
	}

	/**
	 * Increases the {@link #counter} for the funtion with the {@code functionName}.
	 * 
	 * @param functionName
	 *           the name of the function for which the counter should be increased.
	 */
	public void increaseCounter(final String functionName)
	{
		if (counter.containsKey(functionName))
		{
			final int countForFunction = counter.get(functionName).intValue() + 1;
			counter.put(functionName, Integer.valueOf(countForFunction));
		}
		else
		{
			counter.put(functionName, Integer.valueOf(1));
		}
	}

	/**
	 * Getter for the {@link #counter} of a specific function.
	 * 
	 * @param functionName
	 *           the name of the function for which the counter should be returned.
	 * 
	 * @return Returns the {@link #counter} for the funtion with the {@code functionName}.
	 */
	public int getCounter(final String functionName)
	{
		// in case this module needs stateful execution, we just count its calls and take this counter as module identifier
		final Integer count = counter.get(functionName);
		return count == null ? Integer.valueOf(0).intValue() : count.intValue();
	}

	/**
	 * Returns a JCoRecord (ie. JCoTable or JCoStructure) by it's key.
	 * 
	 * @param key
	 *           for this JCoRecord.
	 * @return Returns the Record with the given key.
	 * @throws JCoRecException
	 *            if there is no Record available with the given key.
	 */
	public JCoRecord getRecord(final String key) throws JCoRecException
	{
		final JCoRecord theRecord = records.get(key);
		if (theRecord == null)
		{
			throw new JCoRecException("JCoRecord with key " + key + " is not available");
		}
		return theRecord;
	}

	/**
	 * Returns a JCoStructure (JCoRecord) by it's key.
	 * 
	 * @param key
	 *           the key for this JCoStructure.
	 * @return Returns the JCoStructure with the given key.
	 * @throws JCoRecException
	 *            if the Record with this key is not a JCoStructure or there is no JCoStructure with this key.
	 */
	public JCoStructure getStructure(final String key) throws JCoRecException
	{
		try
		{
			return (JCoStructure) this.getRecord(key);
		}
		catch (final ClassCastException e)
		{
			throw new JCoRecException("Record with key " + key + " is not a JCoStructure", new ClassCastException());
		}
	}

	/**
	 * Returns a JCoTable by it's key.
	 * 
	 * @param key
	 *           for this JCoTable.
	 * @return Returns the JCoTable with the given key.
	 * @throws JCoRecException
	 *            if the Record with this key is not a JCoTable or there is no JCoTable with this key.
	 */
	public JCoTable getTable(final String key) throws JCoRecException
	{
		try
		{
			final JCoTable table = (JCoTable) this.getRecord(key);
			table.firstRow();
			return table;
		}
		catch (final ClassCastException e)
		{
			throw new JCoRecException("Record with key " + key + " is not a JCoTable", new ClassCastException());
		}
	}

	/**
	 * Puts {@code newRecord} with the {@code recordKey} into the map {@link #records}.
	 * 
	 * @param recordKey
	 *           the identifier of the {@code newRecord}.
	 * @param newRecord
	 *           the new value for the map {@link #records}.
	 */
	public void put(final String recordKey, final JCoRecord newRecord)
	{
		records.put(recordKey, newRecord);
	}
}
