/*
 * EFTPaymentInfo.java
 *
 * Created on August 19, 2002, 3:09 PM
 */

package org.ofbiz.commonapp.accounting.payment;

import org.ofbiz.core.entity.GenericValue;

/**
 *
 * @author  cnelson
 */
public class EFTPaymentInfo extends PaymentInfo
{
	
	/** Holds value of property eftInfo. */
	private GenericValue eftInfo;
	
	/** Creates a new instance of EFTPaymentInfo */
	public EFTPaymentInfo(GenericValue eftInfo)
	{
		this.eftInfo = eftInfo;
	}
	
	/** Getter for property eftInfo.
	 * @return Value of property eftInfo.
	 */
	public GenericValue getEftInfo()
	{
		return this.eftInfo;
	}
	
	/** Setter for property eftInfo.
	 * @param eftInfo New value of property eftInfo.
	 */
	public void setEftInfo(GenericValue eftInfo)
	{
		this.eftInfo = eftInfo;
	}
	
}
