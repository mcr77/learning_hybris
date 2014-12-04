/**
 * 
 */
package de.hybris.platform.b2b.punchout;

/**
 *
 */
public class PunchOutSessionNotFoundException extends PunchOutException
{

	/**
	 * Constructor.
	 * 
	 * @param message
	 *           the error message
	 */
	public PunchOutSessionNotFoundException(final String message)
	{
		super(PunchOutResponseCode.CONFLICT, message);
	}

}
