/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.commonapp.accounting.payment;


/**
 * AbstractPaymentGateway.java
 *
 * @author     cnelson
 * @version    1.0
 * @created    August 23, 2002
 */
public abstract class AbstractPaymentGateway implements PaymentGateway {

    public boolean authorize(PaymentInfo payment) throws PaymentGatewayException {
        if (payment instanceof CreditCardPaymentInfo) {
            return authorize((CreditCardPaymentInfo) payment);
        } else if (payment instanceof EFTPaymentInfo) {
            return authorize((EFTPaymentInfo) payment);
        }
        throw new PaymentGatewayException("not supported");
    }

    public boolean capture(PaymentInfo payment) throws PaymentGatewayException {
        if (payment instanceof CreditCardPaymentInfo) {
            return capture((CreditCardPaymentInfo) payment);
        } else if (payment instanceof EFTPaymentInfo) {
            return capture((EFTPaymentInfo) payment);
        }
        throw new PaymentGatewayException("not supported");
    }

    public boolean authorize(CreditCardPaymentInfo payment) throws PaymentGatewayException {
        throw new PaymentGatewayException("not supported");
    }

    public boolean authorize(EFTPaymentInfo payment) throws PaymentGatewayException {
        throw new PaymentGatewayException("not supported");
    }

    public boolean capture(CreditCardPaymentInfo payment) throws PaymentGatewayException {
        throw new PaymentGatewayException("not supported");
    }

    public boolean capture(EFTPaymentInfo payment) throws PaymentGatewayException {
        throw new PaymentGatewayException("not supported");
    }

}
