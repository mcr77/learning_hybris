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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.BackendExceptionECOERP;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import org.junit.Before;
import org.junit.Test;

import com.sap.conn.jco.JCoStructure;


/**
 * 
 */
@UnitTest
public class LrdActionsStrategyERPTest extends JCORecTestBase
{


	private LrdActionsStrategyERP classUnderTest;
	private JCoStructure is_logic_switch = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest#setUp()
	 */
	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		classUnderTest = (LrdActionsStrategyERP) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ACTIONS_STRATEGY);
		final JCoMockRepository testRepository = getJCORepository("jcoReposActionsStrategy");
		try
		{
			is_logic_switch = testRepository.getStructure("IS_LOGIC_SWITCH");
		}
		catch (final JCoRecException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testBeanAvailable()
	{
		assertNotNull(classUnderTest);
	}



	@Test
	public void testFillControlAttributes()
	{
		classUnderTest.fillControlAttributes(is_logic_switch);
		assertEquals("X", is_logic_switch.getString(LrdActionsStrategyERP.FIELD_NO_MESSAGES_DOC));
		assertEquals("X", is_logic_switch.getString(LrdActionsStrategyERP.FIELD_NO_CONVERSION));
	}

	@Test
	public void checkAttributesLrdLoadOk() throws BackendExceptionECOERP
	{
		classUnderTest = (LrdActionsStrategyERP) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ACTIONS_STRATEGY);
		final SalesDocument salesDoc = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		final PartnerList partnerList = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST);
		final PartnerListEntry partnerListEntry = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY);
		partnerListEntry.setPartnerId("4711");
		partnerListEntry.setPartnerTechKey(new TechKey("4711"));
		partnerList.setSoldToData(partnerListEntry);

		salesDoc.getHeader().setPartnerList(partnerList);
		classUnderTest.checkAttributesLrdLoad(salesDoc);
	}

}
