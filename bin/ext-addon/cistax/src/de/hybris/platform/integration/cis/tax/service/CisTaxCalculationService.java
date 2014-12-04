package de.hybris.platform.integration.cis.tax.service;


import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;


/**
 * Service for calculating taxes using CIS tax service.
 */
public interface CisTaxCalculationService
{
	/**
	 * Calculate taxes for an order.
	 * 
	 * @param abstractOrder
	 *           order to calculate taxes for
	 * @return an ExternalTaxDocument that represents the taxes
	 */
	ExternalTaxDocument calculateExternalTaxes(final AbstractOrderModel abstractOrder);

}
