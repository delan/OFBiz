/*
 * $Id$
 */

package org.ofbiz.ecommerce.shoppingcart;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> ShoppingCartItem.java
 * <p><b>Description:</b> Shopping cart item object.
 * <p>Copyright (c) 2002 The Open For Business Project and repected authors.
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
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a> 
 * @version    1.1
 * @created    August 4, 2001
 */
public class ShoppingCartItem implements java.io.Serializable {
    
    private transient GenericValue _product;
    private String delegatorName;    
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
        this._product = product;
        this.productId = _product.getString("productId");        
        this.quantity = quantity;
        this.features = features;
        this.discountAmount = 0.00;
        this.itemComment = null;        
        this.type = 0;
        this.attributes = new HashMap();
        this.delegatorName = _product.getDelegator().getDelegatorName();
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
        
    /** Sets an item features. */
    public void setFeature(String name, String value) {
        features.put(name,value);
    }
    /** Return a specific features. */
    public String getFeature(String name) {
        return (String) features.get(name);
    }        
    
    /** Sets an item attribute. */
    public void setAttribute(String name, String value) {
        attributes.put(name,value);
    }
    /** Return a specific attribute. */
    public String getAttribute(String name) {
        return (String) attributes.get(name);
    }        
    
    /** Returns true if shipping charges apply to this item. */
    public boolean shippingApplies() {
        Boolean shipCharge = getProduct().getBoolean("chargeShipping");
        if (shipCharge == null)
            return true;
        else
            return shipCharge.booleanValue();      
    }
    
    /** Returns the item's productId. */
    public String getProductId() {
        return productId;
    }
    
    /** Returns the item's description. */
    public String getName() {
        return getProduct().getString("productName");
    }
    
    /** Returns the item's description. */
    public String getDescription() {
        return getProduct().getString("description");
    }
    
    /** Returns the item's comment. */
    public String getItemComment() {
        return itemComment;
    }
    
    /** Returns the item's unit weight */
    public double getWeight() {
        Double weight = getProduct().getDouble("weight");
        if (weight == null)
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
        Double defaultPrice = getProduct().getDouble("defaultPrice");
        if (defaultPrice != null)
            return defaultPrice.doubleValue();
        else
            return 0.0;
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
        if (features == null || features.size() < 1)
            return null;       
        return (Collection) features.keySet();
    }
    
    /** Returns a collection of attribute values. */
    public Collection getFeatureValues() {
        if (features == null || features.size() < 1)
            return null;
        return features.values();
    }     
    
    /** Compares the specified object with this cart item. */
    public boolean equals(ShoppingCartItem item) {
        if (item == null) 
            return false;
        if (!item.getProductId().equals(getProductId())) {
            return false;
        }

        if (this.features != null && item.features == null ||
                this.features == null && item.features != null) {
            return false;
        }
        if (this.features != null && item.features != null) {
            if (!item.features.equals(this.features)) {
                return false;
            }
        }

        if (this.attributes != null && item.attributes == null ||
                this.attributes == null && item.attributes != null) {
            return false;
        }
        if (this.attributes != null && item.attributes != null) {
            if (!item.attributes.equals(this.attributes)) {
                return false;
            }
        }

        return true;
    }      
    
    // Gets the Product entity if its not there
    public GenericValue getProduct() {
        if (_product != null) 
            return _product;
        if (delegatorName == null || productId == null)
            throw new IllegalStateException("Bad delegator name or product id");
        GenericDelegator delegator = GenericDelegator.getGenericDelegator(delegatorName);
        try {
            Map fields = UtilMisc.toMap("productId", productId);
            _product = delegator.findByPrimaryKeyCache("Product", fields);
        } catch (GenericEntityException e) {
            throw new RuntimeException("Error with Entity Engine ("+e.getMessage()+")");
        }
        return _product;
    }
}
