/*
 * $Id$
 * $Log$ 
 */

package org.ofbiz.ecommerce.shoppingcart;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * <p><b>Title:</b> ShoppingCartItem.java
 * <p><b>Description:</b> Shopping cart item object.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on August 4, 2001, 8:21 PM
 */
public class ShoppingCartItem implements Serializable {

    private String productId;
    private String description;
    private String itemComment;
    private double basePrice;
    private double discountAmount;
    private double quantity;
    private int type;
    private HashMap attributes;
    
    /** Creates new ShoppingCartItem object. */
    public ShoppingCartItem(String productId, String description, double basePrice, double quantity) {
        this.productId = productId;
        this.description = description;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.attributes = null;
        this.discountAmount = 0.00;
        this.itemComment = null;
        this.type = 0;
    }
    
    /** Creates new ShoppingCartItem object. */
    public ShoppingCartItem(String productId, String description, double basePrice, double quantity, HashMap attributes) {
        this.productId = productId;
        this.description = description;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.attributes = attributes;
        this.discountAmount = 0.00;
        this.itemComment = null;
        this.type = 0;
    }        
    
    /** Sets the quantity for the item. */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    
    /** Sets the discount dollar amount for the item. */
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    /** Sets the item comment. */
    public void setComment(String itemComment) {
        this.itemComment = itemComment;
    }
    
    /** Returns the item's productId. */
    public String getProductId() {
        return productId;
    }
    
    /** Returns the item's description. */
    public String getDescription() {
        return description;
    }
    
    /** Returns the item's comment. */
    public String getItemComment() {
        return itemComment;
    }
    
    /** Returns the quantity. */
    public double getQuantity() {
        return quantity;
    }
    
    /** Returns the base price. */
    public double getBasePrice() {
        return basePrice;
    }
    
    /** Returns the discount dollar amount. */
    public double getDiscountAmount() {
        return discountAmount;
    }
    
    /** Returns the adjusted price amount. */
    public double getPrice() {
        return (basePrice - discountAmount);
    }
    
    /** Returns the total line price. */
    public double getTotalPrice() {
        return (basePrice - discountAmount) * quantity;
    }
    
    /** Returns the attributes for the item. */
    public HashMap getAttributes() {
        return attributes;
    }
    
    /** Returns a collection of attribute names. */
    public Collection getAttributeNames() {
        if ( attributes == null || attributes.size() < 1 )
            return null;       
        return (Collection) attributes.keySet();
    }
    
    /** Returns a collection of attribute values. */
    public Collection getAttributeValues() {
        if ( attributes == null || attributes.size() < 1 )
            return null;
        return attributes.values();
    }     
    
    /** Compares the specified object with this cart item. */
    public boolean equals(ShoppingCartItem item) {
        if ( !item.getProductId().equals(productId) )
            return false;
        if ( item.getAttributes() != null && getAttributes() != null ) {
            if ( !item.getAttributes().equals(getAttributes()) )
            return false;
        }
        return true;
    }
}
