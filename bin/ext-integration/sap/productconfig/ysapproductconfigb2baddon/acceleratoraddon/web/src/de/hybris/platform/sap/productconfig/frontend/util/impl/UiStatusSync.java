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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;

import java.util.ArrayList;
import java.util.List;


public class UiStatusSync
{

	public void updateUiStatus(final ConfigurationData configData, final UiStatus uiStatus, final String selectedGroup)
	{
		final List<UiGroupStatus> uiGroupsStatus = uiStatus.getGroups();
		final List<UiGroupData> uiGroups = configData.getGroups();

		configData.setPriceSummaryCollapsed(uiStatus.isPriceSummaryCollapsed());
		configData.setSpecificationTreeCollapsed(uiStatus.isSpecificationTreeCollapsed());

		for (final UiGroupData uiGroup : uiGroups)
		{
			if (selectedGroup.equals(uiGroup.getId()))
			{
				uiGroup.setCollapsed(false);
			}
			else
			{
				uiGroup.setCollapsed(true);
			}
			for (final UiGroupStatus uiGroupStatus : uiGroupsStatus)
			{
				if (uiGroup.getId().equals(uiGroupStatus.getId()))
				{
					if (isCsticSubGroupFilled(uiGroup))
					{
						updateUiGroupStatus(uiGroupStatus.getSubGroups(), uiGroup.getSubGroups());
					}
					break;
				}
			}
		}
	}

	public void updateUiStatus(final ConfigurationData configData, final UiStatus uiStatus)
	{
		final List<UiGroupStatus> uiGroupsStatus = uiStatus.getGroups();
		final List<UiGroupData> uiGroups = configData.getGroups();

		configData.setPriceSummaryCollapsed(uiStatus.isPriceSummaryCollapsed());
		configData.setSpecificationTreeCollapsed(uiStatus.isSpecificationTreeCollapsed());

		updateUiGroupStatus(uiGroupsStatus, uiGroups);
	}

	private void updateUiGroupStatus(final List<UiGroupStatus> uiGroupsStatus, final List<UiGroupData> uiGroups)
	{
		for (final UiGroupData uiGroup : uiGroups)
		{
			boolean found = false;
			// TODO: verify why sometimes uiGroupsStatus == null
			if (uiGroupsStatus != null)
			{
				for (final UiGroupStatus uiGroupStatus : uiGroupsStatus)
				{
					if (uiGroup.getId().equals(uiGroupStatus.getId()))
					{
						uiGroup.setCollapsed(uiGroupStatus.isCollapsed());
						uiGroup.setCollapsedInSpecificationTree(uiGroupStatus.isCollapsedInSpecificationTree());
						if (isCsticSubGroupFilled(uiGroup))
						{
							updateUiGroupStatus(uiGroupStatus.getSubGroups(), uiGroup.getSubGroups());
						}
						found = true;
						break;
					}
				}
			}

			if (!found)
			{
				uiGroup.setCollapsed(true);
			}
		}
	}

	private boolean isCsticSubGroupFilled(final UiGroupData uiGroup)
	{
		return uiGroup.getSubGroups() != null && !uiGroup.getSubGroups().isEmpty();
	}

	public void setInitialStatus(final ConfigurationData configData)
	{
		final List<UiGroupData> csticGroups = configData.getGroups();
		setInitialGroupStatus(csticGroups);

		configData.setSpecificationTreeCollapsed(false);
		configData.setPriceSummaryCollapsed(false);
	}

	private void setInitialGroupStatus(final List<UiGroupData> uiGroups)
	{
		boolean firstGroup = true;
		for (final UiGroupData uiGroup : uiGroups)
		{
			if (uiGroup.isConfigurable())
			{
				uiGroup.setCollapsed(!firstGroup);
				firstGroup = false;
			}
			else
			{
				uiGroup.setCollapsed(true);
			}
			uiGroup.setCollapsedInSpecificationTree(false);

			if (isCsticSubGroupFilled(uiGroup))
			{
				setInitialGroupStatus(uiGroup.getSubGroups());
			}
		}
	}

	public UiStatus storeUiStatusInSession(final ConfigurationData configData)
	{
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setConfigId(configData.getConfigId());
		uiStatus.setPriceSummaryCollapsed(configData.isPriceSummaryCollapsed());
		uiStatus.setSpecificationTreeCollapsed(configData.isSpecificationTreeCollapsed());
		final String selectedGroup = configData.getSelectedGroup();
		if (selectedGroup != null && !selectedGroup.isEmpty())
		{
			setSelectedGroupExpanded(selectedGroup, configData.getGroups());
		}
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		setUiGroupStatus(configData.getGroups(), uiGroups);
		uiStatus.setGroups(uiGroups);

		return uiStatus;
	}

	private void setSelectedGroupExpanded(final String selectedGroup, final List<UiGroupData> groups)
	{
		for (final UiGroupData uiGroup : groups)
		{
			if (selectedGroup.equals(uiGroup.getId()))
			{
				uiGroup.setCollapsed(false);
			}
			else
			{
				uiGroup.setCollapsed(true);
			}
		}
	}

	private void setUiGroupStatus(final List<UiGroupData> uiGroups, final List<UiGroupStatus> uiGroupsStatus)
	{
		for (final UiGroupData uiGroup : uiGroups)
		{
			final UiGroupStatus uiGroupStatus = new UiGroupStatus();
			uiGroupStatus.setId(uiGroup.getId());
			uiGroupStatus.setCollapsed(uiGroup.isCollapsed());
			uiGroupStatus.setCollapsedInSpecificationTree(uiGroup.isCollapsedInSpecificationTree());
			if (isCsticSubGroupFilled(uiGroup))
			{
				final List<UiGroupStatus> uiSubGroups = new ArrayList<>();
				setUiGroupStatus(uiGroup.getSubGroups(), uiSubGroups);
				uiGroupStatus.setSubGroups(uiSubGroups);
			}
			uiGroupsStatus.add(uiGroupStatus);
		}
	}
}
