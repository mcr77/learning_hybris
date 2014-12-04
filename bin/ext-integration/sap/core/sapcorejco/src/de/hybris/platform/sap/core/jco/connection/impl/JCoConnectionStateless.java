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
package de.hybris.platform.sap.core.jco.connection.impl;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.JCoExceptionSpliter;

import java.util.Properties;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;



/**
 * Stateless RFC connection. <br>
 * Establishes a stateless RFC connection to the backend. Connection is taken from the connection pool and released
 * automatically to the pool after execution was done.
 */
public class JCoConnectionStateless extends JCoConnectionImpl
{


	/**
	 * Cache for managed functions.
	 */
	protected JCoManagedFunctionCache managedFunctionCache = new JCoManagedFunctionCache(); //NOPMD

	/**
	 * Constructor.
	 * 
	 * @param properties
	 *           properties
	 */
	public JCoConnectionStateless(final Properties properties)
	{
		super(properties);
	}

	@Override
	public void execute(final JCoFunction function) throws BackendException
	{
		try
		{
			if (!(function instanceof JCoManagedFunction))
			{
				function.execute(getDestination());
				return;
			}

			final JCoManagedFunction managedFunction = (JCoManagedFunction) function;

			if (managedFunction.hasConnectionParameters())
			{
				preProcessingExecute((JCoManagedFunction) function);
			}

			if (managedFunctionCache != null && managedFunctionCache.isFunctionCached(managedFunction))
			{
				managedFunctionCache.executeCachedFunction(getDestination(), managedFunction);
			}
			else
			{
				function.execute(getDestination());
			}

			if (managedFunction.hasConnectionParameters())
			{
				postProcessingExecute((JCoManagedFunction) function);
			}
		}
		catch (final JCoException e)
		{
			JCoExceptionSpliter.splitAndThrowException(e);
		}

	}
}
