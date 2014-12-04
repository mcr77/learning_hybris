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
/**
 * 
 */
package de.hybris.platform.sap.core.jco.rec.impl;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;

import org.junit.Assert;
import org.junit.Test;

import com.sap.conn.jco.JCoFunction;


/**
 * Tests for class {@link de.hybris.platform.sap.core.jco.rec.impl}.
 */
@SuppressWarnings("javadoc")
public class JCoRecManagedConnectionFactoryTest
{


	@Test
	public void testRecordingModeOff()
	{
		final JCoRecManagedConnectionFactory classUnderTest = new JCoRecManagedConnectionFactory(
				new JCoManagedConnectionFactoryMock());
		classUnderTest.startRecorder(JCoRecMode.OFF, "", "");

		final JCoConnection managedConnection = classUnderTest.getManagedConnection(null, this.getClass().getName());

		Assert.assertFalse(managedConnection instanceof JCoRecConnection);

	}

	@Test
	public void testRecordingModePlayback()
	{
		final JCoRecManagedConnectionFactory classUnderTest = new JCoRecManagedConnectionFactory(
				new JCoManagedConnectionFactoryMock());

		try
		{
			classUnderTest.startRecorder(JCoRecMode.PLAYBACK, "", "");

		}
		catch (final JCoRecRuntimeException e)
		{
			// this is OK, since we did not provide a valid file path
		}
		final JCoConnection managedConnection = classUnderTest.getManagedConnection(null, this.getClass().getName());

		Assert.assertTrue(managedConnection instanceof JCoRecConnection);

	}


	@Test
	public void testRecordingModeRecording()
	{
		final JCoRecManagedConnectionFactory classUnderTest = new JCoRecManagedConnectionFactory(
				new JCoManagedConnectionFactoryMock());
		classUnderTest.startRecorder(JCoRecMode.RECORDING, "", "");

		final JCoConnection managedConnection = classUnderTest.getManagedConnection(null, this.getClass().getName());

		Assert.assertTrue(managedConnection instanceof JCoRecConnection);

	}


	/**
	 * Test for force recording.
	 */
	@Test
	public void testForceRecording()
	{
		final JCoRecManagedConnectionFactory classUnderTest = new JCoRecManagedConnectionFactory(
				new JCoManagedConnectionFactoryMock());

		Assert.assertFalse(classUnderTest.isForceRecording());

		classUnderTest.setForceRecording(true);

		Assert.assertTrue(classUnderTest.isForceRecording());

		classUnderTest.startRecorder(JCoRecMode.PLAYBACK, "", "");

		Assert.assertEquals(JCoRecMode.RECORDING, classUnderTest.getRecordingMode());

		classUnderTest.startRecorder(JCoRecMode.OFF, "", "");

		Assert.assertEquals(JCoRecMode.OFF, classUnderTest.getRecordingMode());

	}


	/**
	 * Test for disable recorder.
	 */
	@Test
	public void testDisableRecorder()
	{
		final JCoRecManagedConnectionFactory classUnderTest = new JCoRecManagedConnectionFactory(
				new JCoManagedConnectionFactoryMock());

		Assert.assertFalse(classUnderTest.isDisableRecorder());

		classUnderTest.setDisableRecorder(true);

		Assert.assertTrue(classUnderTest.isDisableRecorder());

		classUnderTest.startRecorder(JCoRecMode.PLAYBACK, "", "");

		Assert.assertEquals(JCoRecMode.OFF, classUnderTest.getRecordingMode());

		classUnderTest.startRecorder(JCoRecMode.RECORDING, "", "");

		Assert.assertEquals(JCoRecMode.OFF, classUnderTest.getRecordingMode());

		final JCoConnection managedConnection = classUnderTest.getManagedConnection(null, this.getClass().getName());

		Assert.assertFalse(managedConnection instanceof JCoRecConnection);

	}


	/**
	 * Mock class for JCoManagedConnectionFactory.
	 */
	public class JCoManagedConnectionFactoryMock implements JCoManagedConnectionFactory
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory#getManagedConnection(java.lang.String)
		 */
		@Override
		public JCoConnection getManagedConnection(final String connectionName, final String callerId)
		{
			return new JCoConnection()
			{

				@Override
				public boolean isFunctionAvailable(final String functionName)
				{
					return false;
				}

				@Override
				public JCoFunction getFunction(final String functionName) throws BackendException
				{
					return null;
				}

				@Override
				public void execute(final JCoFunction function) throws BackendException
				{
					//
				}

				@Override
				public void setCallerId(final String callerID)
				{
				}
			};
		}

	}


}
