/*
 * AbstractPaymentGateway.java
 *
 * Created on August 23, 2002, 11:09 AM
 */

package org.ofbiz.commonapp.accounting.payment;

/**
 *
 * @author  cnelson
 */
public abstract class AbstractPaymentGateway implements PaymentGateway
{
	
	public boolean authorize(PaymentInfo payment) throws PaymentGatewayException
	{
		if (payment instanceof CreditCardPaymentInfo)
		{
			return authorize((CreditCardPaymentInfo)payment);
		}
		else if (payment instanceof EFTPaymentInfo)
		{
			return authorize((EFTPaymentInfo)payment);
		}
		throw new PaymentGatewayException("not supported");
	}	
	
	public boolean capture(PaymentInfo payment) throws PaymentGatewayException
	{
		if (payment instanceof CreditCardPaymentInfo)
		{
			return capture((CreditCardPaymentInfo)payment);
		}
		else if (payment instanceof EFTPaymentInfo)
		{
			return capture((EFTPaymentInfo)payment);
		}
		throw new PaymentGatewayException("not supported");
	}
	
	public boolean authorize(CreditCardPaymentInfo payment) throws PaymentGatewayException
	{
		throw new PaymentGatewayException("not supported");
	}
	
	public boolean authorize(EFTPaymentInfo payment) throws PaymentGatewayException
	{
		throw new PaymentGatewayException("not supported");
	}
	
	public boolean capture(CreditCardPaymentInfo payment) throws PaymentGatewayException
	{
		throw new PaymentGatewayException("not supported");
	}
	
	public boolean capture(EFTPaymentInfo payment) throws PaymentGatewayException
	{
		throw new PaymentGatewayException("not supported");
	}	
	
}
