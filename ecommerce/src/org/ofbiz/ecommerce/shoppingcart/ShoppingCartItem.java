/*
 * $Id$
 * $Log$
 * Revision 1.6  2001/09/28 21:57:53  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.5  2001/09/05 00:56:24  jonesde
 * Added getName to cart item, and uses it in the minicart because description is too long; should use in real cart too.
 *
 * Revision 1.4  2001/08/29 13:08:49  azeneski
 * Update the cart line item object.
 *
 * Revision 1.3  2001/08/28 02:24:34  azeneski
 * Updated shopping cart to use store a reference to the product entity, rather then individual attributes.
 * Worked on the equals() method in ShoppingCartItem.java. Might be fixed now.
 *
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
import java.util.Map;

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
    
    private transient GenericValue product;
    private String productId;    
    private String itemComment;
    private double discountAmount;
    private double quantity;            
    private Map features;
    private Map attributes;
    private int type;
    
    /** Creates new ShoppingCartItem object. */
    public ShoppingCartItem(GenericValue product, double quantity) {
        this(product,quantity,null);
    }
       
    /** Creates new ShoppingCartItem object. */
    public ShoppingCartItem(GenericValue product, double quantity, HashMap features) {
        this.product = product;
        this.productId = product.getString("productId");
        this.quantity = quantity;
        this.attributes = features;
        this.discountAmount = 0.00;
        this.itemComment = null;        
        this.type = 0;
        this.attributes = new HashMap();
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
        
    /** Sets an item attribute. */
    public void setAttribute(String name, String value) {
        attributes.put(name,value);
    }
    
    /** Returns true if shipping charges apply to this item. */
    public boolean shippingApplies() {
        Boolean shipCharge = product.getBoolean("chargeShipping");
        if ( shipCharge == null )
            return true;
        else
            return shipCharge.booleanValue();      
    }
    
    /** Return a specific attribute. */
    public String getAttribute(String name) {
        return (String) attributes.get(name);
    }        
    
    /** Returns the item's productId. */
    public String getProductId() {
        return productId;
    }
    
    /** Returns the item's description. */
    public String getName() {
        return product.getString("name");
    }
    
    /** Returns the item's description. */
    public String getDescription() {
        return product.getString("description");
    }
    
    /** Returns the item's comment. */
    public String getItemComment() {
        return itemComment;
    }
    
    /** Returns the item's unit weight */
    public double getWeight() {
        Double weight = product.getDouble("weight");
        if ( weight == null )
            return 0;
        else 
            return weight.doubleValue();
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
    
    /** Returns the features for the item. */
    public Map getFeatures() {
        return features;
    }
    
    /** Returns a collection of attribute names. */
    public Collection getFeatureNames() {
        if ( features == null || features.size() < 1 )
            return null;       
        return (Collection) features.keySet();
    }
    
    /** Returns a collection of attribute values. */
    public Collection getFeatureValues() {
        if ( features == null || features.size() < 1 )
            return null;
        return features.values();
    }     
    
    /** Compares the specified object with this cart item. */
    public boolean equals(ShoppingCartItem item) {
        if ( item == null ) 
            return false;
        if ( item.getProductId().equals(getProductId()) ) {
            if ( getFeatures() != null ) {
                if ( item.getFeatures() != null ) {
                    if ( item.getFeatures().equals(getFeatures()) )
                        return true;
                }
            } 
            else if (item.getFeatures() == null ) 
                return true;
        }
        return false;
    }                                    
}
