/*
 * $Id$
 */

package org.ofbiz.ecommerce.shoppingcart;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.order.order.Adjustment;

/**
 * <p><b>Title:</b> ShoppingCart.java
 * <p><b>Description:</b> Shopping Cart Object.
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
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a> 
 * @version    1.0
 * @created    August 4, 2001
 */
public class ShoppingCart implements java.io.Serializable {
    private ArrayList cartLines;
    
    //either creditCardId or poNumber must be null (use one or the other)
    private String creditCardId;
    private String poNumber;
    
    private String shippingContactMechId;
    private String billingAccountId;
    private String shippingInstructions;
    private Boolean maySplit;
    
    /** stored in the format of <shipment method type id>@<carrier party id> */
    private String shipmentMethodTypeId;
    private String carrierPartyId;
    private String cartDiscountString;
    private String orderAdditionalEmails;
    private String taxString;
    private double salesTax;
    private double shipping;
    private double cartDiscount;
    private boolean viewCartOnAdd = true;
    
    
    /** Creates new empty ShoppingCart object. */
    public ShoppingCart() {
        cartLines = new ArrayList();
        shipping = 0.00;
        salesTax = 0.00;
        cartDiscount = 0.00;
        shippingContactMechId = "";
        taxString = "";
        cartDiscountString = "";
    }
    
    /** Add an item to the shopping cart, or if already there, increase the quantity.
     *  @return the new/increased item index
     */
    public int addOrIncreaseItem(org.ofbiz.core.entity.GenericValue product, double quantity, HashMap attributes) {
        // create a new shopping cart item.
        ShoppingCartItem newItem = new ShoppingCartItem(product,quantity,attributes);
        Debug.logInfo("New item created: " + newItem.getProductId());
        
        // Check for existing cart item.
        Debug.logInfo("Cart size: " + this.size());
        for (int i = 0; i < this.cartLines.size(); i++) {
            ShoppingCartItem sci = (ShoppingCartItem) cartLines.get(i);
            Debug.logInfo("Comparing to item: " + sci.getProductId());
            if ( sci.equals(newItem) ) {
                Debug.logInfo("Found a match, updating quantity.");
                sci.setQuantity(sci.getQuantity() + quantity);
                return i;
            }
        }
        
        // Add the item to the shopping cart if it wasn't found.
        return this.addItem(0,newItem);
    }
    
    /** Add an item to the shopping cart. */
    public int addItem(org.ofbiz.core.entity.GenericValue product, double quantity, HashMap attributes) {
        return addItem(new ShoppingCartItem(product,quantity,attributes));
    }
    
    /** Add an item to the shopping cart. */
    public int addItem(int index, ShoppingCartItem item) {
        cartLines.add(index,item);
        return index;
    }
    
    /** Add an item to the shopping cart. */
    public int addItem(ShoppingCartItem item) {
        cartLines.add(item);
        return cartLines.size()-1;
    }
    
    /** Get an ShoppingCartItem from the cart object. */
    public ShoppingCartItem findCartItem(int index) {
        if(cartLines.size() <= index)
            return null;
        return (ShoppingCartItem) cartLines.get(index);
    }
    
    /** Remove an item from the cart object. */
    public ShoppingCartItem removeCartItem(int index) {
        if(cartLines.size() <= index) return null;
        return (ShoppingCartItem) cartLines.remove(index);
    }
    
    /** Returns the number of items in the cart object. */
    public int size() {
        return cartLines.size();
    }
    
    /** Returns a Collection of items in the cart object. */
    public Collection items() {
        return (Collection) cartLines;
    }
    
    /** Returns an iterator of cart items. */
    public Iterator iterator() {
        return cartLines.iterator();
    }
    
    /** Returns an collection of order items. */
    public Collection makeOrderItems(GenericDelegator delegator, String orderId) {
        synchronized(cartLines) {
            Collection result = new ArrayList(cartLines.size());
            Iterator itemIter = cartLines.iterator();
            int seqId = 1;
            while (itemIter.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) itemIter.next();
                String orderItemSeqId = String.valueOf(seqId++);
                GenericValue orderItem = delegator.makeValue("OrderItem", UtilMisc.toMap(
                "orderId", orderId,
                "orderItemSeqId", orderItemSeqId,
                "orderItemTypeId", "SALES_ORDER_ITEM",
                "productId", item.getProductId(),
                "quantity", new Double(item.getQuantity()),
                "unitPrice", new Double(item.getPrice())));
                orderItem.set("itemDescription", item.getName());
                orderItem.set("comments", item.getItemComment());
                orderItem.set("correspondingPoId", this.getPoNumber());
                orderItem.set("statusId", "Ordered");
                result.add(orderItem);
            }
            return result;
        }
    }
    
    /** Returns a List of order item strings */
    public List makeItemList() {
        // productId|productName|price|quantity|comment|poNumber
        List result = new ArrayList();
        Iterator i = iterator();
        while ( i.hasNext() ) {
            StringBuffer buf = new StringBuffer();
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            buf.append(item.getProductId()); buf.append("|");            
            buf.append(item.getName()); buf.append("|");
            buf.append(item.getPrice()); buf.append("|");
            buf.append(item.getQuantity()); buf.append("|");
            buf.append(item.getItemComment()); buf.append("|");
            buf.append(this.getPoNumber());
            result.add(buf.toString());
        }
        return result;
    }
    
    /** Returns a Map of cart values */
    public Map makeCartMap() {
        Map result = new HashMap();
        result.put("orderItems", makeItemList());
        result.put("shippingInstructions",getShippingInstructions());
        result.put("billingAccountId",getBillingAccountId());
        result.put("cartDiscount", new Double(getCartDiscount()));
        result.put("shippingAmount", new Double(getShipping()));
        result.put("taxAmount", new Double(getSalesTax()));
        result.put("shippingContactMechId", getShippingContactMechId());
        result.put("shipmentMethodTypeId", getShipmentMethodTypeId());
        result.put("carrierPartyId", getCarrierPartyId());
        result.put("maySplit", getMaySplit());
        result.put("creditCardId", getCreditCardId());
        return result;
    }
                
    public Collection getAdjustments() {
        Collection result = new ArrayList(3);
        result.add(new Adjustment("Shipping and Handling", this.getShipping()));
        result.add(new Adjustment("Sales Tax", this.getSalesTax()));
        return result;
    }
    
    /** Returns this item's index. */
    public int getItemIndex(Object item) {
        return cartLines.indexOf(item);
    }
    
    /** Clears out the cart. */
    public void clear() {
        salesTax = 0.00;
        shipping = 0.00;
        cartLines.clear();
    }
    
    /** Moves a line item to a differnt index. */
    public void moveCartItem(int fromIndex, int toIndex) {
        if(toIndex < fromIndex)
            cartLines.add(toIndex,cartLines.remove(fromIndex));
        else if(toIndex > fromIndex)
            cartLines.add(toIndex-1,cartLines.remove(fromIndex));
    }
    
    /** Sets the credit card id in the cart. */
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
    
    /** Sets the credit card id in the cart. */
    public void setCreditCardId(String creditCardId) {
        this.creditCardId = creditCardId;
    }
    
    /** Sets the shipping amount in the cart. */
    public void setShipping(double shipping) {
        this.shipping = shipping;
    }
    
    /** Sets the billing message string. */
    public void setBillingAccountId(String billingAccountId) {
        this.billingAccountId = billingAccountId;
    }
    
    /** Sets the shipping message string. */
    public void setShippingContactMechId(String shippingContactMechId) {
        this.shippingContactMechId = shippingContactMechId;
    }
    
    /** Sets the shipping instructions. */
    public void setShippingInstructions(String shippingInstructions) {
        this.shippingInstructions = shippingInstructions;
    }
    
    public void setMaySplit(Boolean maySplit) {
        this.maySplit = maySplit;
    }
    
    /** Sets the shipment method type. */
    public void setShipmentMethodTypeId(String shipmentMethodTypeId) {
        this.shipmentMethodTypeId = shipmentMethodTypeId;
    }
    
    public void setCarrierPartyId(String carrierPartyId) {
        this.carrierPartyId = carrierPartyId;
    }
    
    public void setOrderAdditionalEmails(String orderAdditionalEmails) {
        this.orderAdditionalEmails = orderAdditionalEmails;
    }
    
    /** Sets the tax dollar amount in the cart. */
    public void setTax(double salesTax) {
        this.salesTax = salesTax;
    }
    
    /** Sets the tax message string. */
    public void setTaxString(String taxString) {
        this.taxString = taxString;
    }
    
    /** Sets the cart discount amount. */
    public void setCartDiscount(double cartDiscount) {
        this.cartDiscount = cartDiscount;
    }
    
    /** Sets the cart discount message string. */
    public void setCartDiscountString(String cartDiscountString) {
        this.cartDiscountString = cartDiscountString;
    }
    
    /** Returns the credit card id. */
    public String getCreditCardId() {
        return creditCardId;
    }
    
    /** Returns the po number. */
    public String getPoNumber() {
        return poNumber;
    }
    
    /** Returns the shipping amount from the cart object. */
    public double getShipping() {
        return shipping;
    }
    
    /** Returns the shipping message string. */
    public String getShippingContactMechId() {
        return shippingContactMechId;
    }
    
    /** Returns the billing message string. */
    public String getBillingAccountId() {
        return billingAccountId;
    }
    
    /** Returns the shipping instructions. */
    public String getShippingInstructions() {
        return shippingInstructions;
    }
    
    /** Returns Boolean.TRUE if the order may be shipped (null if unspecified) */
    public Boolean getMaySplit() {
        return maySplit;
    }
    
    /** Returns the shipment method type */
    public String getShipmentMethodTypeId() {
        return shipmentMethodTypeId;
    }
    
    public String getCarrierPartyId() {
        return carrierPartyId;
    }
    
    public String getOrderAdditionalEmails() {
        return orderAdditionalEmails;
    }
    
    public GenericValue getCreditCardInfo(GenericDelegator delegator) {
        if (this.creditCardId != null) {
            try { return delegator.findByPrimaryKey("CreditCardInfo", UtilMisc.toMap("creditCardId", creditCardId)); }
            catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); return null; }
        } else {
            return null;
        }
    }
    
    public GenericValue getShippingAddress(GenericDelegator delegator) {
        if (this.shippingContactMechId != null) {
            try { return delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", shippingContactMechId)); }
            catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); return null; }
        } else {
            return null;
        }
    }
    
    public GenericValue getBillingAddress(GenericDelegator delegator) {
        if (this.billingAccountId != null) {
            try { return delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", billingAccountId)); }
            catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); return null; }
        } else {
            return null;
        }
    }
    
    /** Returns the dollar tax amount from the cart object. */
    public double getSalesTax() {
        return salesTax;
    }
    
    /** Returns the tax shipping string. */
    public String getTaxString() {
        return taxString;
    }
    
    /** Returns the cart discount amount. */
    public double getCartDiscount() {
        return cartDiscount;
    }
    
    /** Returns the cart discount message string. */
    public String getCartDiscountString() {
        return cartDiscountString;
    }
    
    /** Returns the item-total in the cart (not including discount/tax/shipping). */
    public double getItemTotal() {
        double itemTotal = 0.00;
        Iterator i = iterator();
        while ( i.hasNext() )
            itemTotal += ((ShoppingCartItem) i.next()).getTotalPrice();
        return itemTotal;
    }
    
    /** Returns the sub-total in the cart (item-total - discount). */
    public double getSubTotal() {
        return (getItemTotal() - cartDiscount);
    }
    
    
    /** Returns the total from the cart, including tax/shipping. */
    public double getGrandTotal() {
        return (getSubTotal() + shipping + salesTax);
    }
    
    /** Returns the SHIPABLE item-total in the cart. */
    public double getShippableTotal() {
        double itemTotal = 0.00;
        Iterator i = iterator();
        while ( i.hasNext() ) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            if ( item.shippingApplies() )
                itemTotal += item.getTotalPrice();
        }
        return itemTotal;
    }
    
    /** Returns the total quantity in the cart. */
    public double getTotalQuantity() {
        double count = 0.000000;
        Iterator i = iterator();
        while ( i.hasNext() )
            count += ((ShoppingCartItem) i.next()).getQuantity();
        return count;
    }
    
    /** Returns the total SHIPABLE quantity in the cart. */
    public double getShippableQuantity() {
        double count = 0.000000;
        Iterator i = iterator();
        while ( i.hasNext() ) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            if ( item.shippingApplies() )
                count += item.getQuantity();
        }
        return count;
    }
    
    /** Returns the total SHIPABLE weight in the cart. */
    public double getShippableWeight() {
        double weight = 0.000000;
        Iterator i = iterator();
        while ( i.hasNext() ) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            if ( item.shippingApplies() )
                weight += (item.getWeight() * item.getQuantity());
        }
        return weight;
    }
    
    
    /** Returns the total weight in the cart. */
    public double getTotalWeight() {
        double weight = 0.000000;
        Iterator i = iterator();
        while ( i.hasNext() ) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            weight += (item.getWeight() * item.getQuantity());
        }
        return weight;
    }
    
    /** Returns true if the user wishes to view the cart everytime an item is added. */
    public boolean viewCartOnAdd() {
        return viewCartOnAdd;
    }
    
    /** Returns true if the user wishes to view the cart everytime an item is added. */
    public boolean viewCartOnAdd(boolean viewCartOnAdd) {
        this.viewCartOnAdd = viewCartOnAdd;
        return this.viewCartOnAdd;
    }
}
