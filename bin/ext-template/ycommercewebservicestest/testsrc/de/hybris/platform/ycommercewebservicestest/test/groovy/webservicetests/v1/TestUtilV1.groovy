package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v1

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.TestUtil

class TestUtilV1 extends TestUtil
{
	private static final TestUtilV1 INSTANCE = new TestUtilV1();

	private TestUtilV1()
	{
		super("/groovytests-property-file-v1.groovy")
	}

	static synchronized TestUtilV1 getInstance()
	{
		return INSTANCE;
	}
}
