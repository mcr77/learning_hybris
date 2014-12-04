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
package de.hybris.platform.sap.core.jco.rec;

import de.hybris.platform.sap.core.jco.rec.impl.JCoRecRepository;

import java.io.File;


/**
 * The interface for the JCoRecorder XML writer.
 */
public interface JCoRecXMLWriter
{

	/**
	 * Writes the repository to a XML file.
	 * 
	 * @param repository
	 *           the repository that should be written to the file.
	 * @param f
	 *           the file that ahould contain the data afterwards.
	 * @throws JCoRecXMLWriterException
	 *            if something goes wrong.
	 */
	public void write(JCoRecRepository repository, File f) throws JCoRecXMLWriterException;
}
