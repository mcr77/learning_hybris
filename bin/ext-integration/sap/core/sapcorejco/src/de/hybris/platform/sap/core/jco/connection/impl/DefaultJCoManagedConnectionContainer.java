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

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionContainer;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import java.util.HashMap;
import java.util.Map;


/**
 * Container for managed connections. <br>
 * Container is session scoped. In case of the session is destroyed the destroy method is called, which cleanups
 * stateful connections.
 */
public class DefaultJCoManagedConnectionContainer implements JCoManagedConnectionContainer
{
	/**
	 * Managed connection factory.
	 */
	protected JCoManagedConnectionFactory managedConnectionFactory; //NOPMD
	/**
	 * Map of managed connections.
	 */
	protected Map<String, JCoConnection> managedConnections = new HashMap<String, JCoConnection>(4); //NOPMD

	/**
	 * Setter for connection factory.
	 * 
	 * @param manangedConnectionFactory
	 *           managed connection factory.
	 */
	public void setManagedConnectionFactory(final JCoManagedConnectionFactory manangedConnectionFactory)
	{
		this.managedConnectionFactory = manangedConnectionFactory;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.core.jco.connection.impl.JCoManagedConnectionContainer#getManagedConnection(java.lang.String
	 * )
	 */
	@Override
	public JCoConnection getManagedConnection(final String connectionName)
	{

		if (managedConnections.containsKey(connectionName))
		{
			return managedConnections.get(connectionName);
		}

		final JCoConnection con = managedConnectionFactory.getManagedConnection(connectionName, this.getClass().getName());

		// store only Stateful connections
		if (con instanceof JCoStateful)
		{
			managedConnections.put(connectionName, con);
		}

		return con;

	}

	/**
	 * Destroy hosted managed connections.
	 * 
	 * @throws BackendException
	 *            Exception in case of failure.
	 */
	protected void destroy() throws BackendException
	{
		for (final JCoConnection con : managedConnections.values())
		{
			if (con instanceof JCoStateful)
			{
				((JCoStateful) con).destroy();
			}
		}
		managedConnections.clear();
		managedConnections = null;
	}

}
