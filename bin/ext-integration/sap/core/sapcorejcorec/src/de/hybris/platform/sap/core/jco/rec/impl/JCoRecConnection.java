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

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.connection.impl.JCoConnectionImpl;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.JCoExceptionSpliter;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;


/**
 * The JCoRecorder equivalent to the {@link JCoConnectionImpl}.
 */
class JCoRecConnection implements JCoConnection, JCoStateful
{

	private final JCoConnection wrappedConnection;
	private final JCoRecMode mode;

	/** The current repository. Either parsed from the file or the current recorded backend-calls */
	private final JCoRecRepository repo;

	/**
	 * Constructor.
	 * 
	 * @param connection
	 *           the JCoConnection to wrap.
	 * @param repo
	 *           a reference to the repository of the JCoRecorder.
	 * @param mode
	 *           the current mode of the JCoRecorder.
	 */
	public JCoRecConnection(final JCoConnection connection, final JCoRecRepository repo, final JCoRecMode mode)
	{
		this.repo = repo;
		this.mode = mode;

		wrappedConnection = connection;
	}

	@Override
	public void execute(final JCoFunction function) throws BackendException
	{
		if (mode == JCoRecMode.PLAYBACK)
		{
			try
			{
				((JCoRecFunctionDecorator) function).execute(null);
			}
			catch (final JCoException e)
			{
				JCoExceptionSpliter.splitAndThrowException(e);
			}
			return;
		}

		wrappedConnection.execute(function);
	}

	@Override
	public boolean isFunctionAvailable(final String functionName)
	{
		if (mode == JCoRecMode.PLAYBACK)
		{
			return true;
		}
		return wrappedConnection.isFunctionAvailable(functionName);
	}

	@Override
	public JCoFunction getFunction(final String functionName) throws BackendException
	{
		JCoFunction function;

		repo.increaseCounter(functionName);

		if (mode == JCoRecMode.PLAYBACK)
		{
			function = repo.getEmptyFunction(functionName);
			if (function == null)
			{
				throw new JCoRecRuntimeException(
						"Can't find function in repository while in PLAYBACK mode. You can try to record again.");
			}
		}
		else
		{
			function = wrappedConnection.getFunction(functionName);
		}

		return new JCoRecFunctionDecorator(function, repo, System.currentTimeMillis(), repo.getCounter(functionName), mode);
	}

	@Override
	public void destroy() throws BackendException
	{
		if (wrappedConnection instanceof JCoStateful)
		{
			((JCoStateful) wrappedConnection).destroy();
		}

	}

	@Override
	public void setCallerId(final String callerID)
	{

	}
}
