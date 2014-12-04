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
package de.hybris.platform.sap.core.bol.backend.jco;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * This interface should be implemented by Backend Business Object which communicate with an SAP System using Java
 * Connector of SAP.
 */
public interface BackendBusinessObjectJCo extends BackendBusinessObject
{

	/**
	 * Returns a JCo connection to an SAP system. This is a default connection configured externally. <br />
	 * The methods checks if the connection is valid. If it is invalid, it will be tried to rebuild the connection if
	 * <ul>
	 * <li>connection is of type JCoConnectionStateless</li>
	 * <li>user ID from the old connection is the same as the user from EAI-Config.</li>
	 * </ul>
	 * The rebuild reads language from context and it will be used to complete the connection (the parameter user and
	 * password are taken from an connection called JCoComplete which must be defined is sapcore-connection-spring.xml.
	 * 
	 * @return JCo client connection to an SAP system
	 * @throws BackendException
	 *            Backend Exception
	 */
	public JCoConnection getDefaultJCoConnection() throws BackendException;

	/**
	 * Set the defaultConnectionName.
	 * 
	 * @param defaultConnectionName
	 *           default connection name to be set
	 */
	public void setDefaultConnectionName(String defaultConnectionName);

	/**
	 * Return the defaultConnectionName.
	 * 
	 * @return defaultConnectionName
	 */
	public String getDefaultConnectionName();

}
