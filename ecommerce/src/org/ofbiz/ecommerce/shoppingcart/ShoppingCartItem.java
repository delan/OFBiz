/*
 * $Id$
 * $Log$
 * Revision 1.2  2001/08/27 17:29:31  epabst
 * simplified
 *
 * Revision 1.1.1.1  2001/08/24 01:01:42  azeneski
 * Initial Import
 * 
 */

package org.ofbiz.ecommerce.shoppingcart;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import org.ofbiz.core.entity.GenericValue;

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

    private GenericValue product;
    private HashMap attributes;
    private String itemComment;
    private double discountAmount;
    private double quantity;            
    private boolean shippingApplies;
    private int type;
    
    /** Creates new ShoppingCartItem object. */
    public ShoppingCartItem(GenericValue product, double quantity) {
        this(product,quantity,null);
    }
       
    /** Creates new ShoppingCartItem object. */
    public ShoppingCartItem(GenericValue product, double quantity, HashMap attributes) {
        this.product = product;
        this.quantity = quantity;
        this.attributes = attributes;
        this.discountAmount = 0.00;
        this.itemComment = null;
        this.shippingApplies = true;
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
    
    /** Specifies if shipping applies to this item. */
    public void setShippingApplies(boolean shippingApplies) {
        this.shippingApplies = shippingApplies;
    }
    
    /** Returns true if shipping charges apply to this item. */
    public boolean shippingApplies() {
        return shippingApplies;
    }
    
    /** Returns the item's productId. */
    public String getProductId() {
        return product.getString("productId");
    }
    
    /** Returns the item's description. */
    public String getDescription() {
        return product.getString("description");
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
        // todo calculate the price using price component.
        return product.getDouble("defaultPrice").doubleValue();
    }
    
    /** Returns the discount dollar amount. */
    public double getDiscountAmount() {
        return discountAmount;
    }
    
    /** Returns the adjusted price amount. */
    public double getPrice() {
        return (getBasePrice() - discountAmount);
    }
    
    /** Returns the total line price. */
    public double getTotalPrice() {
        return (getBasePrice() - discountAmount) * quantity;
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
        if ( item == null ) 
            return false;
        if ( item.getProductId().equals(getProductId()) ) {
            if ( getAttributes() != null ) {
                if ( item.getAttributes() != null ) {
                    if ( item.getAttributes().equals(getAttributes()) )
                        return true;
                }
            } 
            else if (item.getAttributes() == null ) 
                return true;
        }
        return false;
    }                                    
}
