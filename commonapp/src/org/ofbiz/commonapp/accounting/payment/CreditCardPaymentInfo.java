/*
 * CreditCardPaymentInfo.java
 *
 * Created on August 19, 2002, 3:07 PM
 */

package org.ofbiz.commonapp.accounting.payment;

import org.ofbiz.core.entity.GenericValue;

/**
 *
 * @author  cnelson
 */
public class CreditCardPaymentInfo extends PaymentInfo
{
	
	/** Holds value of property creditCardInfo. */
	private GenericValue creditCard;
	
	public CreditCardPaymentInfo() {}
	
	/** Creates a new instance of CreditCardPaymentInfo */
	public CreditCardPaymentInfo(GenericValue creditCard)
	{
		this.creditCard = creditCard;
	}
	
	/** Getter for property creditCardInfo.
	 * @return Value of property creditCardInfo.
	 */
	public GenericValue getCreditCard()
	{
		return this.creditCard;
	}
	
	/** Setter for property creditCardInfo.
	 * @param creditCardInfo New value of property creditCardInfo.
	 */
	public void setCreditCard(GenericValue creditCardInfo)
	{
		this.creditCard = creditCardInfo;
	}
	
}
