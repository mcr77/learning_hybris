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

import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.sap.conn.jco.AbapClassException.Mode;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.rt.AbstractField;


/**
 * Design of this class follows the decorator pattern although it is not exactly the class hierarchy mentioned in
 * http://en.wikipedia.org/wiki/Decorator_pattern. <br>
 * Execution of the actual backend calls is delegated to {@link #decoratedFunction}, the execution against the XML
 * repository is implemented here.
 */
class JCoRecFunctionDecorator implements JCoFunction
{

	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(JCoRecFunctionDecorator.class.getName());


	private static final long serialVersionUID = -4626071511384483622L;

	private final JCoRecMode mode;

	/**
	 * The {@link JCoFunction} that is executed against the backend or taken from the repository file.
	 */
	private final JCoFunction decoratedFunction;

	private JCoParameterList input;
	private JCoParameterList export;
	private JCoParameterList changing;
	private JCoParameterList table;
	private AbapException[] exc;
	private JCoFunctionTemplate template;


	/**
	 * The {@link JCoRecRepository} that this {@link JCoRecFunctionDecorator} relates to.
	 */
	private JCoRecRepository repository;

	/**
	 * A {@link String} that identifies this {@link JCoRecFunctionDecorator}.
	 */
	private String explicitFunctionKey;

	/**
	 * The timestamp of the last execution against the SAP Backend.
	 */
	private long timestamp;

	/**
	 * Wraps the function {@code org} into this RecorderFuntionDecorator.
	 * 
	 * @param function
	 *           the JCoFunction to wrap.
	 * @param repo
	 *           the JCoRepository where one can find the function {@code org}.
	 * @param timestamp
	 *           is the point in time the function was executed.
	 * @param mode
	 *           the current mode of the JCoRecorder.
	 */
	private JCoRecFunctionDecorator(final JCoFunction function, final JCoRecRepository repo, final long timestamp,
			final JCoRecMode mode)
	{
		this.decoratedFunction = function;
		mapFunctionValues(function);

		this.repository = repo;
		this.timestamp = timestamp;
		this.mode = mode;
	}

	/**
	 * Calls the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)} constructor.<br/>
	 * Additionally the {@link #explicitFunctionKey} is generated with the help of the {@code counter} parameter.
	 * 
	 * @param org
	 *           parameter for the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)
	 *           constructor} call.
	 * @param repo
	 *           parameter for the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)
	 *           constructor} call.
	 * @param timestamp
	 *           parameter for the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)
	 *           constructor} call.
	 * @param counter
	 *           parameter for the generation of the {@link #explicitFunctionKey}.
	 * @param mode
	 *           parameter for the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)
	 *           constructor} call.
	 */
	public JCoRecFunctionDecorator(final JCoFunction org, final JCoRecRepository repo, final long timestamp, final int counter,
			final JCoRecMode mode)
	{
		this(org, repo, timestamp, mode);
		this.explicitFunctionKey = generateFunctionKey(org.getName(), counter);
	}

	/**
	 * Calls the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)} constructor.<br/>
	 * Additionally the {@link #explicitFunctionKey} is generated with the help of the {@code counter} parameter.
	 * 
	 * @param org
	 *           parameter for the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)
	 *           constructor} call.
	 * @param repo
	 *           parameter for the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)
	 *           constructor} call.
	 * @param timestamp
	 *           parameter for the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)
	 *           constructor} call.
	 * @param functionKey
	 *           is saved in the local attribute {@link #explicitFunctionKey}.
	 * @param mode
	 *           parameter for the {@link #JCoRecFunctionDecorator(JCoFunction, JCoRecRepository, long, JCoRecMode)
	 *           constructor} call.
	 */
	public JCoRecFunctionDecorator(final JCoFunction org, final JCoRecRepository repo, final long timestamp,
			final String functionKey, final JCoRecMode mode)
	{
		this(org, repo, timestamp, mode);
		this.explicitFunctionKey = functionKey;
	}

	/**
	 * Getter-Method for property {@link #timestamp}.
	 * 
	 * @return Returns the {@link #timestamp}.
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * Setter-Method for property {@link #timestamp}.
	 * 
	 * @param timestamp
	 *           The {@link #timestamp} to set.
	 */
	public void setTimestamp(final long timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * Compiles a hash code for the import parameter list. Does not take tables as importing parameters into account,
	 * only structures.
	 * 
	 * @return Returns the hash for the local import parameterlist.
	 */
	@SuppressWarnings("unused")
	private int getHashForImportParams()
	{
		final JCoParameterList importParameterList = getImportParameterList();
		final Iterator<JCoField> iterator = importParameterList.iterator();
		int hash = 1;
		while (iterator.hasNext())
		{
			final AbstractField nextField = (AbstractField) iterator.next();
			if (!nextField.isStructure())
			{
				hash = hash ^ nextField.getString().hashCode();
			}
			else
			{
				final JCoStructure structure = nextField.getStructure();
				hash = hash ^ getHashForFieldIterator(structure.getFieldIterator());
			}
		}
		return hash;
	}

	/**
	 * Compiles the hash value for a JCoStructure.
	 * 
	 * @param fieldIterator
	 *           the JCoStructure for that the hash should be generated.
	 * @return Returns the hash for the {@code fieldIterator}.
	 */
	private int getHashForFieldIterator(final JCoFieldIterator fieldIterator)
	{

		int hash = 1;
		while (fieldIterator.hasNextField())
		{
			final AbstractField nextField = (AbstractField) fieldIterator.nextField();
			if (!nextField.isStructure())
			{
				hash = hash ^ nextField.getString().hashCode();
			}
			else
			{
				final JCoStructure structure = nextField.getStructure();
				hash = hash ^ getHashForFieldIterator(structure.getFieldIterator());
			}
		}
		return hash;
	}

	/**
	 * <p>
	 * Tries to get the execution results from {@link #repository}. <br>
	 * If this fails, the {@link #decoratedFunction} is executed on the given destination.
	 * </p>
	 * The public visible fields then get {@link #mapFunctionValues(JCoFunction) mapped} to this Objects fields.<br>
	 * This Object then is added to {@link #repository}.
	 * 
	 * @param destination
	 *           the {@link JCoDestination} on which the function module should be executed
	 * @throws JCoException
	 *            if an exception occurred during the call of the method {@code execute(..)}.
	 * @see JCoFunction#execute(JCoDestination)
	 * @see JCoFunction#execute(JCoDestination, String)
	 * @see JCoFunction#execute(JCoDestination, String, String)
	 */
	protected void executeWrapper(final JCoDestination destination) throws JCoException
	{
		// get previously executed versions of the current function
		final JCoRecFunctionDecorator availableJcoFunction = this.repository.getExecutedFunctionByKey(getFunctionKey());

		if (mode == JCoRecMode.PLAYBACK)
		{
			if (availableJcoFunction == null)
			{
				throw new JCoRecRuntimeException("PLAYBACK-Mode but function " + getFunctionKey()
						+ " is not contained in parsed repository-file!");
			}

			this.timestamp = availableJcoFunction.getTimestamp();
			this.repository = availableJcoFunction.repository;
			putFunctionParams(availableJcoFunction);
			return;
		}

		this.timestamp = System.currentTimeMillis();
		this.decoratedFunction.execute(destination);

		// Function could not be received from JCoRecRepository, so put it to the Repository
		if (availableJcoFunction == null)
		{
			// we haven't it pushed to the repository yet => do this now
			repository.put(this);
		}
		else
		{
			log.debug("Don't push to repository because the same call has been done before.");
		}
	}

	/**
	 * Maps all public visible fields of a JCoFunction to the corresponding fields of this Object.
	 * 
	 * @param source
	 *           the JCoFunction to map the fields from
	 */
	private void mapFunctionValues(final JCoFunction source)
	{
		this.input = source.getImportParameterList();
		this.export = source.getExportParameterList();
		this.changing = source.getChangingParameterList();
		this.table = source.getTableParameterList();
		this.exc = source.getExceptionList();
		this.template = source.getFunctionTemplate();
	}

	/**
	 * Copies the Data from a given JCoFunction to this objects datafields.
	 * 
	 * @param source
	 *           the JCoFunction where to copy the parameterlists from
	 */
	private void putFunctionParams(final JCoFunction source)
	{
		final JCoParameterList input = source.getImportParameterList();
		final JCoParameterList export = source.getExportParameterList();
		final JCoParameterList changing = source.getChangingParameterList();
		final JCoParameterList table = source.getTableParameterList();
		final AbapException[] exc = source.getExceptionList();

		if (input != null && this.input != null)
		{
			this.input.copyFrom(input);
		}
		if (export != null && this.export != null)
		{
			this.export.copyFrom(export);
		}
		if (changing != null && this.changing != null)
		{
			this.changing.copyFrom(changing);
		}
		if (table != null && this.table != null)
		{
			this.table.copyFrom(table);
		}
		if (exc != null)
		{
			this.exc = exc;
		}
	}

	/**
	 * Tries to retrieve the function result from {@link #repository}. In case this function is available the results get
	 * mapped to this Object else the execution of {@link #decoratedFunction} on the given destination is started.
	 * 
	 * @param destination
	 *           the destination on which the function module should be executed.
	 * @throws JCoException
	 *            if an exception occurred during the call execution.
	 * @see JCoRecFunctionDecorator#executeWrapper(JCoDestination)
	 * @see com.sap.conn.jco.JCoFunction#execute(com.sap.conn.jco.JCoDestination)
	 */
	@Override
	public void execute(final JCoDestination destination) throws JCoException
	{
		executeWrapper(destination);
	}

	/**
	 * Unsupported method.
	 * 
	 * @param destination
	 *           Unsupported method.
	 * @param tid
	 *           Unsupported method.
	 * @throws JCoException
	 *            Unsupported method.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#execute(com.sap.conn.jco.JCoDestination, java.lang.String)
	 */
	@Override
	public void execute(final JCoDestination destination, final String tid) throws JCoException
	{
		throw new UnsupportedOperationException("This method is NOT supported!");
	}

	/**
	 * Unsupported method.
	 * 
	 * @param destination
	 *           Unsupported method.
	 * @param tid
	 *           Unsupported method.
	 * @param queueName
	 *           Unsupported method.
	 * @throws JCoException
	 *            Unsupported method.
	 * 
	 * @see com.sap.conn.jco.JCoFunction#execute(com.sap.conn.jco.JCoDestination, java.lang.String, java.lang.String)
	 */
	@Override
	public void execute(final JCoDestination destination, final String tid, final String queueName) throws JCoException
	{
		throw new UnsupportedOperationException("This method is NOT supported!");
	}

	/**
	 * Returns the local copy of the changing parameterlist.
	 * 
	 * @return Returns the local changing parameterlist.
	 */
	@Override
	public JCoParameterList getChangingParameterList()
	{
		return this.changing;
	}

	/**
	 * Executes {@link JCoFunction#getException(String)} on the wrapped JCoFunction.
	 * 
	 * @param arg0
	 *           redirects the parameter to the delegated function.
	 * 
	 * @return Returns the result from the delegated function call.
	 */
	@Override
	public AbapException getException(final String arg0)
	{
		return decoratedFunction.getException(arg0);
	}

	/**
	 * Returns the local copy of the exceptionlist.
	 * 
	 * @return Returns the local exceptionlist.
	 */
	@Override
	public AbapException[] getExceptionList()
	{
		return this.exc;
	}

	/**
	 * Returns the local copy of the export parameterlist.
	 * 
	 * @return Returns the local export parameterlist.
	 */
	@Override
	public JCoParameterList getExportParameterList()
	{
		return this.export;
	}

	/**
	 * Returns the local copy of the function template.
	 * 
	 * @return Returns the local function template.
	 */
	@Override
	public JCoFunctionTemplate getFunctionTemplate()
	{
		return this.template;
	}

	/**
	 * Returns the local copy of the import parameterlist.
	 * 
	 * @return Returns the local import parameterlist.
	 */
	@Override
	public JCoParameterList getImportParameterList()
	{
		return this.input;
	}

	/**
	 * Returns the local copy of the table parameterlist.
	 * 
	 * @return Returns the local table parameterlist.
	 */
	@Override
	public JCoParameterList getTableParameterList()
	{
		return this.table;
	}

	/**
	 * Executes {@link JCoFunction#toXML()} on the wrapped JCoFunction.
	 * 
	 * @return Returns the delegated function serialized in XML format
	 */
	@Override
	public String toXML()
	{
		return this.decoratedFunction.toXML();
	}

	/**
	 * Getter for the {@link #repository}.
	 * 
	 * @return the local JCoRecRepository.
	 */
	public JCoRecRepository getRepository()
	{
		return this.repository;
	}

	/**
	 * Generates the input key for the JCoFunction {@code function}.
	 * 
	 * @param function
	 *           is the JCoFunction for that the input key is returned.
	 * @return Returns the result from the call of {@link #getInputKey(String, JCoParameterList)}.
	 */
	private String getInputKey(final JCoFunction function)
	{
		return getInputKey(function.getName(), function.getImportParameterList());
	}

	/**
	 * Computes the input key for the {@code functionName}.
	 * 
	 * @param functionName
	 *           is used for the input key.
	 * @param importParam
	 *           is used for the input key.
	 * @return Returns {@code functionName + ":" + importParam.toXML().hashCode()}.
	 */
	private String getInputKey(final String functionName, final JCoParameterList importParam)
	{
		return functionName + ":" + importParam.toXML().hashCode();

	}

	/**
	 * Generates the input key for this instance.
	 * 
	 * @return Returns the result from the call of {@link #getInputKey(JCoFunction)}.
	 */
	public String getInputKey()
	{
		return getInputKey(this);
	}

	/**
	 * Computes the output key for the parameters.
	 * 
	 * @param exportParam
	 *           is used for the output key.
	 * @param changingParam
	 *           is used for the output key.
	 * @param tableParam
	 *           is used for the output key.
	 * @return Returns {@code (exportParam.toXML() + changingParam.toXML() + tableParam.toXML()).hashCode()}.
	 */
	private String getOutputKey(final JCoParameterList exportParam, final JCoParameterList changingParam,
			final JCoParameterList tableParam)
	{
		final String exXML = exportParam == null ? "" : exportParam.toXML();
		final String chXML = changingParam == null ? "" : changingParam.toXML();
		final String tabXML = tableParam == null ? "" : tableParam.toXML();

		return String.valueOf((exXML + chXML + tabXML).hashCode());
	}

	/**
	 * Generates the output key for the JCoFunction {@code function}.
	 * 
	 * @param function
	 *           is the JCoFunction for that the output key is returned.
	 * @return Returns the result from the call of
	 *         {@link #getOutputKey(JCoParameterList, JCoParameterList, JCoParameterList)}.
	 */
	private String getOutputKey(final JCoFunction function)
	{
		return getOutputKey(function.getExportParameterList(), function.getChangingParameterList(),
				function.getTableParameterList());
	}

	/**
	 * Generates the output key for this instance.
	 * 
	 * @return Returns the result from the call of {@link #getOutputKey(JCoFunction)}.
	 */
	public String getOutputKey()
	{
		return getOutputKey(this);
	}

	/**
	 * Generates the function key for this instance.
	 * 
	 * @return Returns the local {@link #explicitFunctionKey}.
	 */
	public String getFunctionKey()
	{
		return explicitFunctionKey;
	}

	/**
	 * Generates the function key for the {@code functionName}.
	 * 
	 * @param functionName
	 *           is used for the function key.
	 * @param count
	 *           is used for the function key.
	 * @return Returns {@code functionName+"::"+count}
	 */
	private String generateFunctionKey(final String functionName, final int count)
	{
		return functionName + "::" + count;
	}

	/**
	 * Check for status of AbapClassException. Not delegated due to a bug.
	 * 
	 * @return Returns nothing. Method throws JCoRecRuntimeException.
	 */
	public boolean isAbapClassExceptionEnabled()
	{
		throw new JCoRecRuntimeException(
				"Method is not delegated due to a bug (see https://GTP.wdf.sap.corp/sap/bc/webdynpro/qce/msg_gui_edit?sap-language=E&csinsta=0120031469&mnumm=0000221893&myear=2012)");
	}

	/**
	 * Setter for the AbapClassExceptionMode. Not delegeated due to a bug.
	 * 
	 * @param paramMode
	 *           Unused parameter. Method throws JCoRecRuntimeException.
	 */
	public void setAbapClassExceptionMode(final Mode paramMode)
	{
		throw new JCoRecRuntimeException(
				"Method is not delegated due to a bug (see https://GTP.wdf.sap.corp/sap/bc/webdynpro/qce/msg_gui_edit?sap-language=E&csinsta=0120031469&mnumm=0000221893&myear=2012)");
	}

	@Override
	public String getName()
	{
		return decoratedFunction.getName();
	}
}
