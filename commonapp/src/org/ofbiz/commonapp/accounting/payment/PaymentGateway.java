/*
 * PaymentGateway.java
 *
 * Created on August 19, 2002, 1:34 PM
 */

package org.ofbiz.commonapp.accounting.payment;

/**
 *
 * @author  cnelson
 */
public interface PaymentGateway
{
	public static final String PAYMENT_NOT_AUTHORIZED = "PAYMENT_NOT_AUTHORIZED";
	public static final String PAYMENT_AUTHORIZED = "PAYMENT_AUTHORIZED";
	public static final String PAYMENT_DECLINED = "PAYMENT_DECLINED";
	public static final String PAYMENT_ERROR = "PAYMENT_ERROR";
	public static final String PAYMENT_CAPTURED = "PAYMENT_CAPTURED";
	
	public boolean authorize(PaymentInfo payment) throws PaymentGatewayException;
	
	public boolean capture(PaymentInfo payment) throws PaymentGatewayException;
	
}
