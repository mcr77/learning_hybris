/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at Dec 5, 2014 7:51:07 PM
 * ----------------------------------------------------------------
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.cuppytrail.data;

import java.util.List;

/**
 * DataObject representing a Stadium
 */
public class StadiumData  implements java.io.Serializable 
{

	/** <i>Generated property</i> for <code>StadiumData.matches</code> property defined at extension <code>cuppytrail</code>. */
	private List<MatchSummaryData> matches;
	/** <i>Generated property</i> for <code>StadiumData.name</code> property defined at extension <code>cuppytrail</code>. */
	private String name;
	/** <i>Generated property</i> for <code>StadiumData.capacity</code> property defined at extension <code>cuppytrail</code>. */
	private String capacity;
		
	public StadiumData()
	{
		// default constructor
	}
	
		
	public void setMatches(final List<MatchSummaryData> matches)
	{
		this.matches = matches;
	}
	
		
	public List<MatchSummaryData> getMatches() 
	{
		return matches;
	}
		
		
	public void setName(final String name)
	{
		this.name = name;
	}
	
		
	public String getName() 
	{
		return name;
	}
		
		
	public void setCapacity(final String capacity)
	{
		this.capacity = capacity;
	}
	
		
	public String getCapacity() 
	{
		return capacity;
	}
		
	
}