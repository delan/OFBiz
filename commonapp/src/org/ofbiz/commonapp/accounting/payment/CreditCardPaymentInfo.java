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


import org.ofbiz.core.entity.GenericValue;


/**
 * EFTPaymentInfo.java
 *
 * @author     cnelson
 * @version    1.0
 * @created    August 23, 2002
 */
public class CreditCardPaymentInfo extends PaymentInfo {

    /** Holds value of property creditCardInfo. */
    private GenericValue creditCard;

    public CreditCardPaymentInfo() {}

    /** Creates a new instance of CreditCardPaymentInfo */
    public CreditCardPaymentInfo(GenericValue creditCard) {
        this.creditCard = creditCard;
    }

    /** Getter for property creditCardInfo.
     * @return Value of property creditCardInfo.
     */
    public GenericValue getCreditCard() {
        return this.creditCard;
    }

    /** Setter for property creditCardInfo.
     * @param creditCardInfo New value of property creditCardInfo.
     */
    public void setCreditCard(GenericValue creditCardInfo) {
        this.creditCard = creditCardInfo;
    }

}
