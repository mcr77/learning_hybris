/**
 * 
 */
package de.hybris.platform.b2bacceleratorfacades.futurestock;

import de.hybris.platform.commercefacades.product.data.FutureStockData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;
import java.util.Map;


/**
 * Facade for 'Future Stock Management'.
 */
public interface B2BFutureStockFacade
{

	/**
	 * Gets the future product availability for the specified product, for each future date.
	 * 
	 * @param product
	 *           the product
	 * @return A list of quantity ordered by date. If there is no availability for this product in the future, an empty
	 *         list is returned. If the external future stock system is completely not available a null value will be
	 *         returned.
	 */
	List<FutureStockData> getFutureAvailability(ProductModel product);


	/**
	 * Gets the future product availability for the list of specified products, for each future date.
	 * 
	 * @param products
	 *           the products
	 * @return A map of product codes with a list of quantity ordered by date. If the external future stock system is
	 *         completely not available a null value will be returned.
	 */
	Map<String, List<FutureStockData>> getFutureAvailability(List<ProductModel> products);


}
