/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Dec 5, 2014 7:51:06 PM                      ---
 * ----------------------------------------------------------------
 *  
 * [y] hybris Platform
 *  
 * Copyright (c) 2000-2011 hybris AG
 * All rights reserved.
 *  
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *  
 */
package de.hybris.platform.catalog.model;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.enums.ProductDifferenceMode;
import de.hybris.platform.catalog.model.CatalogVersionDifferenceModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.CompareCatalogVersionsCronJobModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;

/**
 * Generated model class for type ProductCatalogVersionDifference first defined at extension catalog.
 */
@SuppressWarnings("all")
public class ProductCatalogVersionDifferenceModel extends CatalogVersionDifferenceModel
{
	/**<i>Generated model type code constant.</i>*/
	public final static String _TYPECODE = "ProductCatalogVersionDifference";
	
	/** <i>Generated constant</i> - Attribute key of <code>ProductCatalogVersionDifference.targetProductApprovalStatus</code> attribute defined at extension <code>catalog</code>. */
	public static final String TARGETPRODUCTAPPROVALSTATUS = "targetProductApprovalStatus";
	
	/** <i>Generated constant</i> - Attribute key of <code>ProductCatalogVersionDifference.targetProduct</code> attribute defined at extension <code>catalog</code>. */
	public static final String TARGETPRODUCT = "targetProduct";
	
	/** <i>Generated constant</i> - Attribute key of <code>ProductCatalogVersionDifference.mode</code> attribute defined at extension <code>catalog</code>. */
	public static final String MODE = "mode";
	
	/** <i>Generated constant</i> - Attribute key of <code>ProductCatalogVersionDifference.sourceProduct</code> attribute defined at extension <code>catalog</code>. */
	public static final String SOURCEPRODUCT = "sourceProduct";
	
	/** <i>Generated constant</i> - Attribute key of <code>ProductCatalogVersionDifference.sourceProductApprovalStatus</code> attribute defined at extension <code>catalog</code>. */
	public static final String SOURCEPRODUCTAPPROVALSTATUS = "sourceProductApprovalStatus";
	
	
	/** <i>Generated variable</i> - Variable of <code>ProductCatalogVersionDifference.targetProductApprovalStatus</code> attribute defined at extension <code>catalog</code>. */
	private ArticleApprovalStatus _targetProductApprovalStatus;
	
	/** <i>Generated variable</i> - Variable of <code>ProductCatalogVersionDifference.targetProduct</code> attribute defined at extension <code>catalog</code>. */
	private ProductModel _targetProduct;
	
	/** <i>Generated variable</i> - Variable of <code>ProductCatalogVersionDifference.mode</code> attribute defined at extension <code>catalog</code>. */
	private ProductDifferenceMode _mode;
	
	/** <i>Generated variable</i> - Variable of <code>ProductCatalogVersionDifference.sourceProduct</code> attribute defined at extension <code>catalog</code>. */
	private ProductModel _sourceProduct;
	
	/** <i>Generated variable</i> - Variable of <code>ProductCatalogVersionDifference.sourceProductApprovalStatus</code> attribute defined at extension <code>catalog</code>. */
	private ArticleApprovalStatus _sourceProductApprovalStatus;
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public ProductCatalogVersionDifferenceModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public ProductCatalogVersionDifferenceModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated Since 4.1.1 Please use the default constructor without parameters
	 * @param _cronJob initial attribute declared by type <code>CatalogVersionDifference</code> at extension <code>catalog</code>
	 * @param _mode initial attribute declared by type <code>ProductCatalogVersionDifference</code> at extension <code>catalog</code>
	 * @param _sourceVersion initial attribute declared by type <code>CatalogVersionDifference</code> at extension <code>catalog</code>
	 * @param _targetVersion initial attribute declared by type <code>CatalogVersionDifference</code> at extension <code>catalog</code>
	 */
	@Deprecated
	public ProductCatalogVersionDifferenceModel(final CompareCatalogVersionsCronJobModel _cronJob, final ProductDifferenceMode _mode, final CatalogVersionModel _sourceVersion, final CatalogVersionModel _targetVersion)
	{
		super();
		setCronJob(_cronJob);
		setMode(_mode);
		setSourceVersion(_sourceVersion);
		setTargetVersion(_targetVersion);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated Since 4.1.1 Please use the default constructor without parameters
	 * @param _cronJob initial attribute declared by type <code>CatalogVersionDifference</code> at extension <code>catalog</code>
	 * @param _mode initial attribute declared by type <code>ProductCatalogVersionDifference</code> at extension <code>catalog</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _sourceProduct initial attribute declared by type <code>ProductCatalogVersionDifference</code> at extension <code>catalog</code>
	 * @param _sourceVersion initial attribute declared by type <code>CatalogVersionDifference</code> at extension <code>catalog</code>
	 * @param _targetProduct initial attribute declared by type <code>ProductCatalogVersionDifference</code> at extension <code>catalog</code>
	 * @param _targetVersion initial attribute declared by type <code>CatalogVersionDifference</code> at extension <code>catalog</code>
	 */
	@Deprecated
	public ProductCatalogVersionDifferenceModel(final CompareCatalogVersionsCronJobModel _cronJob, final ProductDifferenceMode _mode, final ItemModel _owner, final ProductModel _sourceProduct, final CatalogVersionModel _sourceVersion, final ProductModel _targetProduct, final CatalogVersionModel _targetVersion)
	{
		super();
		setCronJob(_cronJob);
		setMode(_mode);
		setOwner(_owner);
		setSourceProduct(_sourceProduct);
		setSourceVersion(_sourceVersion);
		setTargetProduct(_targetProduct);
		setTargetVersion(_targetVersion);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductCatalogVersionDifference.mode</code> attribute defined at extension <code>catalog</code>. 
	 * @return the mode
	 */
	public ProductDifferenceMode getMode()
	{
		if (this._mode!=null)
		{
			return _mode;
		}
		return _mode = getPersistenceContext().getValue(MODE, _mode);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductCatalogVersionDifference.sourceProduct</code> attribute defined at extension <code>catalog</code>. 
	 * @return the sourceProduct
	 */
	public ProductModel getSourceProduct()
	{
		if (this._sourceProduct!=null)
		{
			return _sourceProduct;
		}
		return _sourceProduct = getPersistenceContext().getValue(SOURCEPRODUCT, _sourceProduct);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductCatalogVersionDifference.sourceProductApprovalStatus</code> attribute defined at extension <code>catalog</code>. 
	 * @return the sourceProductApprovalStatus
	 */
	public ArticleApprovalStatus getSourceProductApprovalStatus()
	{
		if (this._sourceProductApprovalStatus!=null)
		{
			return _sourceProductApprovalStatus;
		}
		return _sourceProductApprovalStatus = getPersistenceContext().getValue(SOURCEPRODUCTAPPROVALSTATUS, _sourceProductApprovalStatus);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductCatalogVersionDifference.targetProduct</code> attribute defined at extension <code>catalog</code>. 
	 * @return the targetProduct
	 */
	public ProductModel getTargetProduct()
	{
		if (this._targetProduct!=null)
		{
			return _targetProduct;
		}
		return _targetProduct = getPersistenceContext().getValue(TARGETPRODUCT, _targetProduct);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ProductCatalogVersionDifference.targetProductApprovalStatus</code> attribute defined at extension <code>catalog</code>. 
	 * @return the targetProductApprovalStatus
	 */
	public ArticleApprovalStatus getTargetProductApprovalStatus()
	{
		if (this._targetProductApprovalStatus!=null)
		{
			return _targetProductApprovalStatus;
		}
		return _targetProductApprovalStatus = getPersistenceContext().getValue(TARGETPRODUCTAPPROVALSTATUS, _targetProductApprovalStatus);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>ProductCatalogVersionDifference.mode</code> attribute defined at extension <code>catalog</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the mode
	 */
	public void setMode(final ProductDifferenceMode value)
	{
		_mode = getPersistenceContext().setValue(MODE, value);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>ProductCatalogVersionDifference.sourceProduct</code> attribute defined at extension <code>catalog</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the sourceProduct
	 */
	public void setSourceProduct(final ProductModel value)
	{
		_sourceProduct = getPersistenceContext().setValue(SOURCEPRODUCT, value);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>ProductCatalogVersionDifference.targetProduct</code> attribute defined at extension <code>catalog</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the targetProduct
	 */
	public void setTargetProduct(final ProductModel value)
	{
		_targetProduct = getPersistenceContext().setValue(TARGETPRODUCT, value);
	}
	
}
