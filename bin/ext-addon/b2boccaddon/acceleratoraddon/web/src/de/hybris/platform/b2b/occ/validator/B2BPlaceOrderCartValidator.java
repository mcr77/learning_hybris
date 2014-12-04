package de.hybris.platform.b2b.occ.validator;

import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.CartData;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * B2B cart validator. Checks if cart is calculated and if needed values are filled.
 */
public class B2BPlaceOrderCartValidator implements Validator
{
	@Override
	public boolean supports(final Class<?> clazz)
	{
		return CartData.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final CartData cart = (CartData) target;

		if (!cart.isCalculated())
		{
			errors.reject("cart.notCalculated");
		}

		if (cart.getDeliveryMode() == null)
		{
			errors.reject("cart.deliveryModeNotSet");
		}

		if (cart.getDeliveryAddress() == null)
		{
			errors.reject("cart.deliveryAddressNotSet");
		}

		final boolean isAccountPaymentType = CheckoutPaymentType.ACCOUNT.getCode().equals(cart.getPaymentType().getCode());

		// for CARD type, paymentInfo must be set
		if (!isAccountPaymentType && cart.getPaymentInfo() == null)
		{
			errors.reject("cart.paymentInfoNotSet");
		}
	}
}
