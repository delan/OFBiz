/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.commonapp.order.order;

import org.ofbiz.core.util.UtilFormatOut;

/**
 * Order Adjustment Helper Class
 *
 *@author     <a href="mailto:epabst@bigfoot.com">Eric Pabst</a>
 *@author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 *@created    December 11, 2001
 *@version    1.0
 */
public class Adjustment {
    public static final String SHIPPING_TYPE_ID = "SHIPPING_CHARGES";
    public static final String SALES_TAX_TYPE_ID = "SALES_TAX";
    private String description;
    private double amount;
    
    /** Holds value of property typeId. */
    private String typeId;
    
    public Adjustment(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }
    
    public Adjustment(String description, double amount, String typeId) {
        this(description, amount);
        this.typeId = typeId;
    }
    
    public Adjustment(String description, double percentage, double basePrice) {
        this(description, percentage * basePrice);
        prependDescription(UtilFormatOut.formatPercentage(percentage) + " ");
    }
    
    /** only and at least one of the amount or percentage must be specified */
    public Adjustment(String description, Double amount, Double percentage, double basePrice) {
        this.description = description;
        if ((amount != null) != (percentage != null)) {
            if (amount != null) {
                this.amount = amount.doubleValue();
            } else {
                this.amount = percentage.doubleValue() * basePrice;
                prependDescription(UtilFormatOut.formatPercentage(percentage) + " ");
            }
        } else {
            throw new IllegalArgumentException(
            "Either amount or percentage must be specified for adjustment");
        }
    }
    
    /** include the percentage amount, if applicable */
    public String getDescription() {
        return description;
    }
    
    public void prependDescription(String prefix) {
        description = prefix + description;
    }
    
    /** uses either the amount or percentage */
    public double getAmount() {
        return amount;
    }
    
    /** Getter for property typeId.
     * @return Value of property typeId.
     */
    public String getTypeId() {
        return this.typeId;
    }
    
    /** Setter for property typeId.
     * @param typeId New value of property typeId.
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
