/*
 * PaymentGatewayException.java
 *
 * Created on August 20, 2002, 11:18 AM
 */

package org.ofbiz.commonapp.accounting.payment;

import org.ofbiz.core.util.GeneralException;

/**
 *
 * @author  cnelson
 */
public class PaymentGatewayException extends GeneralException
{
	
	/** Creates a new instance of PaymentGatewayException */
	public PaymentGatewayException()
	{
		super();
	}
	
    /**
     * Constructs an <code>PaymentGatewayException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PaymentGatewayException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>PaymentGatewayException</code> with the specified detail message and nested Exception.
     * @param msg the detail message.
     */
    public PaymentGatewayException(String msg, Throwable nested) {
        super(msg, nested);
    }
	
	/**
     * Constructs an <code>GeneralException</code> with the specified detail message and nested Exception.
     * @param msg the detail message.
     */
    public PaymentGatewayException(Throwable nested) {
        super(nested);
        
    }

	
}
