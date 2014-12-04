package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.TestUtil

class TestUtilV2 extends TestUtil
{
	private static final TestUtilV2 INSTANCE = new TestUtilV2();

	private TestUtilV2()
	{
		super("/groovytests-property-file-v2.groovy")
	}

	static synchronized TestUtilV2 getInstance()
	{
		return INSTANCE;
	}
}
