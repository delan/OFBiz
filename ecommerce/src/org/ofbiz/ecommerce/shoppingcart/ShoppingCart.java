/*
 * $Id$
 * $Log$
 * Revision 1.4  2001/09/05 21:51:13  epabst
 * added more data that is used for staging the Order in the shopping cart
 *
 * Revision 1.3  2001/08/31 17:45:36  epabst
 * added method to simplify code
 *
 * Revision 1.2  2001/08/28 02:24:34  azeneski
 * Updated shopping cart to use store a reference to the product entity, rather then individual attributes.
 * Worked on the equals() method in ShoppingCartItem.java. Might be fixed now.
 *
 * Revision 1.1.1.1  2001/08/24 01:01:42  azeneski
 * Initial Import
 *
 */

package org.ofbiz.ecommerce.shoppingcart;

import java.io.Serializable;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on August 4, 2001, 8:21 PM
 */
public class ShoppingCart {
    private ArrayList cartLines;
    
    //either creditCardId or poNumber must be null (use one or the other)
    private String creditCardId;
    private String poNumber;
    
    private String shippingContactMechId;
    private String billingContactMechId;
    private String shippingInstructions;
    
    /** stored in the format of <shipment method type id>@<carrier party id> */
    private String shippingMethod;
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
        Debug.log("New item created: " + newItem.getProductId());
        
        // Check for existing cart item.
        Debug.log("Cart size: " + this.size());
        for (int i = 0; i < this.cartLines.size(); i++) {
            ShoppingCartItem sci = (ShoppingCartItem) cartLines.get(i);
            Debug.log("Comparing to item: " + sci.getProductId());
            if ( sci.equals(newItem) ) {
                Debug.log("Found a match, updating quantity.");
                sci.setQuantity(sci.getQuantity() + quantity);
                return i;
            }
        }
                        
        // Add the item to the shopping cart if it wasn't found.
        return this.addItem(0,newItem);
    }

    /** Add an item to the shopping cart. */
    public int addItem(org.ofbiz.core.entity.GenericValue product, double quantity, HashMap attributes) {
        cartLines.add(new ShoppingCartItem(product,quantity,attributes));
        return cartLines.size()-1;
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
        if ( cartLines.size() <= index )
            return null;
        return (ShoppingCartItem) cartLines.get(index);
    }
        
    /** Remove an item from the cart object. */
    public ShoppingCartItem removeCartItem(int index) {
        if ( cartLines.size() <= index )
            return null;
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
        this.creditCardId = null;
    }
    
    /** Sets the credit card id in the cart. */
    public void setCreditCardId(String creditCardId) {
        this.poNumber = null;
        this.creditCardId = creditCardId;
    }
    
    /** Sets the shipping amount in the cart. */
    public void setShipping(double shipping) {
        this.shipping = shipping;
    }
    
    /** Sets the billing message string. */
    public void setBillingContactMechId(String billingContactMechId) {
        this.billingContactMechId = billingContactMechId;
    }
    
    /** Sets the shipping message string. */
    public void setShippingContactMechId(String shippingContactMechId) {
        this.shippingContactMechId = shippingContactMechId;
    }
    
    /** Sets the shipping instructions. */
    public void setShippingInstructions(String shippingInstructions) {
        this.shippingInstructions = shippingInstructions;
    }
    
    /** Sets the shipping instructions. */
    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
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
    public String getBillingContactMechId() {
        return billingContactMechId;
    }
    
    /** Returns the shipping instructions. */
    public String getShippingInstructions() {
        return shippingInstructions;
    }
    
    /** Returns the shipping instructions. */
    public String getShippingMethod() {
        return shippingMethod;
    }
    
    public String getOrderAdditionalEmails() {
        return orderAdditionalEmails;
    }
    
    public GenericValue getCreditCardInfo(GenericHelper helper) {
        if (this.creditCardId != null) {
            return helper.findByPrimaryKey("CreditCardInfo", UtilMisc.toMap(
                    "creditCardId", creditCardId));
        } else {
            return null;
        }
    }
    
    public GenericValue getShippingAddress(GenericHelper helper) {
        if (this.shippingContactMechId != null) {
            return helper.findByPrimaryKey("PostalAddress", UtilMisc.toMap(
                    "contactMechId", shippingContactMechId));
        } else {
            return null;
        }
    }
    
    public GenericValue getBillingAddress(GenericHelper helper) {
        if (this.billingContactMechId != null) {
            return helper.findByPrimaryKey("PostalAddress", UtilMisc.toMap(
                    "contactMechId", billingContactMechId));
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
