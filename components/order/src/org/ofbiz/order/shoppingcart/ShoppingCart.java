/*
 * $Id: ShoppingCart.java,v 1.31 2003/11/28 18:48:46 jonesde Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.order.shoppingcart;

import java.text.NumberFormat;
import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.product.store.ProductStoreWorker;

/**
 * <p><b>Title:</b> ShoppingCart.java
 * <p><b>Description:</b> Shopping Cart Object.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.31 $
 * @since      2.0
 */
public class ShoppingCart implements java.io.Serializable {
    public static final String module = ShoppingCart.class.getName();

    private List paymentMethodIds = new LinkedList();
    private List singleUsePaymentIds = new LinkedList();
    private Map paymentMethodAmounts = new HashMap();
    private List paymentMethodTypeIds = new LinkedList();
    private Map paymentMethodTypeAmounts = new HashMap();
    private String orderType = "SALES_ORDER"; // default orderType
    private String poNumber = null;
    private String orderId = null;
    private String firstAttemptOrderId = null;
    private String billingAccountId = null;
    private double billingAccountAmt = 0.00;
    private String currencyUom = null;

    private GenericValue orderShipmentPreference = null;
    private String orderAdditionalEmails = null;
    private boolean viewCartOnAdd = false;

    /** Holds value of order adjustments. */
    private List adjustments = new LinkedList();
    private List cartLines = new ArrayList();
    private Map contactMechIdsMap = new HashMap();

    public static class ProductPromoUseInfo {
        public String productPromoId;
        public String productPromoCodeId;
        public ProductPromoUseInfo(String productPromoId, String productPromoCodeId) {
            this.productPromoId = productPromoId;
            this.productPromoCodeId = productPromoCodeId;
        }
        public String getProductPromoId() { return this.productPromoId; }
        public String getProductPromoCodeId() { return this.productPromoCodeId; }
    }
    /** Contains a List for each productPromoId (key) containing a productPromoCodeId (or empty string for no code) for each use of the productPromoId */
    private List productPromoUseInfoList = new LinkedList();
    /** Contains the promo codes entered */
    private Set productPromoCodes = new HashSet();
    private List freeShippingProductPromoActions = new ArrayList();

    private transient GenericDelegator delegator = null;
    private String delegatorName = null;
    private String productStoreId = null;
    private String webSiteId = null;

    private GenericValue userLogin = null;
    private GenericValue autoUserLogin = null;

    private Locale locale;  // holds the locale from the user session

    /** don't allow empty constructor */
    protected ShoppingCart() {}

    /** Creates a new cloned ShoppingCart Object. */
    public ShoppingCart(ShoppingCart cart) {
        this.delegator = cart.getDelegator();
        this.delegatorName = delegator.getDelegatorName();
        this.productStoreId = cart.getProductStoreId();
        this.paymentMethodIds = cart.getPaymentMethodIds();
        this.paymentMethodTypeIds = cart.getPaymentMethodTypeIds();
        this.poNumber = cart.getPoNumber();
        this.orderId = cart.getOrderId();
        this.firstAttemptOrderId = cart.getFirstAttemptOrderId();
        this.billingAccountId = cart.getBillingAccountId();
        this.orderShipmentPreference = cart.getOrderShipmentPreference();
        this.orderAdditionalEmails = cart.getOrderAdditionalEmails();
        this.adjustments = new LinkedList(cart.getAdjustments());
        this.contactMechIdsMap = new HashMap(cart.getOrderContactMechIds());
        this.freeShippingProductPromoActions = new ArrayList(cart.getFreeShippingProductPromoActions());
        this.productPromoUseInfoList = new LinkedList(cart.productPromoUseInfoList);
        this.productPromoCodes = new HashSet(cart.productPromoCodes);
        this.locale = cart.getLocale();
        this.currencyUom = cart.getCurrency();
        this.viewCartOnAdd = cart.viewCartOnAdd();

        // clone the items
        List items = cart.items();
        Iterator itIt = items.iterator();
        while (itIt.hasNext()) {
            cartLines.add(new ShoppingCartItem((ShoppingCartItem) itIt.next()));
        }
    }

    /** Creates new empty ShoppingCart object. */
    public ShoppingCart(GenericDelegator delegator, String productStoreId, String webSiteId, String currencyUom) {
        this.delegator = delegator;
        this.delegatorName = delegator.getDelegatorName();
        this.productStoreId = productStoreId;
        this.webSiteId = webSiteId;
        this.currencyUom = currencyUom;
        this.orderShipmentPreference = delegator.makeValue("OrderShipmentPreference", null);
        // make sure locale is initialized if nothing other than from jvm
        // for a web shopping cart this would be set later by the webShoppingCart methods
        this.locale = Locale.getDefault();

        // set the default view cart on add for this store
        GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
        String storeViewCartOnAdd = productStore.getString("viewCartOnAdd");
        if (storeViewCartOnAdd != null && "Y".equalsIgnoreCase(storeViewCartOnAdd)) {
            this.viewCartOnAdd = true;
        }
    }

    public GenericDelegator getDelegator() {
        if (delegator == null) {
            delegator = GenericDelegator.getGenericDelegator(delegatorName);
        }
        return delegator;
    }

    public String getProductStoreId() {
        return this.productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }


    // =======================================================================
    // Methods for cart items
    // =======================================================================

    /** Add an item to the shopping cart, or if already there, increase the quantity.
     * @return the new/increased item index
     * @throws CartItemModifyException
     */
    public int addOrIncreaseItem(String productId, double selectedAmount, double quantity, Map features, Map attributes, String prodCatalogId, LocalDispatcher dispatcher) throws CartItemModifyException {
        // public int addOrIncreaseItem(GenericValue product, double quantity, HashMap features) {

        // Check for existing cart item.
        for (int i = 0; i < this.cartLines.size(); i++) {
            ShoppingCartItem sci = (ShoppingCartItem) cartLines.get(i);

            if (sci.equals(productId, features, attributes, prodCatalogId, selectedAmount)) {
                double newQuantity = sci.getQuantity() + quantity;

                if (Debug.verboseOn()) Debug.logVerbose("Found a match for id " + productId + " on line " + i + ", updating quantity to " + newQuantity, module);
                sci.setQuantity(newQuantity, dispatcher, this);
                return i;
            }
        }

        // Add the new item to the shopping cart if it wasn't found.
        return this.addItem(0, ShoppingCartItem.makeItem(new Integer(0), productId, selectedAmount, quantity, features, attributes, prodCatalogId, dispatcher, this));
    }
    public int addOrIncreaseItem(String productId, double quantity, Map features, Map attributes, String prodCatalogId, LocalDispatcher dispatcher) throws CartItemModifyException {
        return addOrIncreaseItem(productId, 0.00, quantity, features, attributes, prodCatalogId, dispatcher);
    }

    /** Add a non-product item to the shopping cart.
     * @return the new item index
     * @throws CartItemModifyException
     */
    public int addNonProductItem(String itemType, String description, String categoryId, double price, double quantity, Map attributes, String prodCatalogId, LocalDispatcher dispatcher) throws CartItemModifyException {
        return this.addItem(0, ShoppingCartItem.makeItem(new Integer(0), itemType, description, categoryId, price, 0.00, quantity, attributes, prodCatalogId, dispatcher, this, true));
    }

    /** Add an item to the shopping cart. */
    public int addItem(int index, ShoppingCartItem item) {
        if (!cartLines.contains(item)) {
            cartLines.add(index, item);
            return index;
        } else {
            return this.getItemIndex(item);
        }
    }

    /** Add an item to the shopping cart. */
    public int addItemToEnd(String productId, double amount, double quantity, HashMap features, HashMap attributes, String prodCatalogId, LocalDispatcher dispatcher) throws CartItemModifyException {
        return addItemToEnd(ShoppingCartItem.makeItem(null, productId, amount, quantity, features, attributes, prodCatalogId, dispatcher, this));
    }

    /** Add an item to the shopping cart. */
    public int addItemToEnd(ShoppingCartItem item) {
        if (!cartLines.contains(item)) {
            cartLines.add(item);
            return cartLines.size() - 1;
        } else {
            return this.getItemIndex(item);
        }
    }

    /** Get a ShoppingCartItem from the cart object. */
    public ShoppingCartItem findCartItem(String productId, Map features, Map attributes, String prodCatalogId, double selectedAmount) {
        // Check for existing cart item.
        for (int i = 0; i < this.cartLines.size(); i++) {
            ShoppingCartItem cartItem = (ShoppingCartItem) cartLines.get(i);

            if (cartItem.equals(productId, features, attributes, prodCatalogId, selectedAmount)) {
                return cartItem;
            }
        }
        return null;
    }

    /** Get all ShoppingCartItems from the cart object with the given productId. */
    public List findAllCartItems(String productId) {
        if (productId == null) return new LinkedList(this.cartLines);
        List itemsToReturn = new LinkedList();

        // Check for existing cart item.
        for (int i = 0; i < this.cartLines.size(); i++) {
            ShoppingCartItem cartItem = (ShoppingCartItem) cartLines.get(i);

            if (productId.equals(cartItem.getProductId())) {
                itemsToReturn.add(cartItem);
            }
        }
        return itemsToReturn;
    }

    /** Remove quantity 0 ShoppingCartItems from the cart object. */
    public void removeEmptyCartItems() {
        // Check for existing cart item.
        for (int i = 0; i < this.cartLines.size();) {
            ShoppingCartItem cartItem = (ShoppingCartItem) cartLines.get(i);

            if (cartItem.getQuantity() == 0.0) {
                cartLines.remove(i);
            } else {
                i++;
            }
        }
    }

    /** Returns this item's index. */
    public int getItemIndex(ShoppingCartItem item) {
        return cartLines.indexOf(item);
    }

    /** Get a ShoppingCartItem from the cart object. */
    public ShoppingCartItem findCartItem(int index) {
        if (cartLines.size() <= index)
            return null;
        return (ShoppingCartItem) cartLines.get(index);
    }

    /** Remove an item from the cart object. */
    public void removeCartItem(int index, LocalDispatcher dispatcher) throws CartItemModifyException {
        if (index < 0) return;
        if (cartLines.size() <= index) return;
        ShoppingCartItem item = (ShoppingCartItem) cartLines.remove(index);

        // set quantity to 0 to trigger necessary events
        item.setQuantity(0.0, dispatcher, this);
    }

    /** Moves a line item to a differnt index. */
    public void moveCartItem(int fromIndex, int toIndex) {
        if (toIndex < fromIndex) {
            cartLines.add(toIndex, cartLines.remove(fromIndex));
        } else if (toIndex > fromIndex) {
            cartLines.add(toIndex - 1, cartLines.remove(fromIndex));
        }
    }

    /** Returns the number of items in the cart object. */
    public int size() {
        return cartLines.size();
    }

    /** Returns a Collection of items in the cart object. */
    public List items() {
        return cartLines;
    }

    /** Returns an iterator of cart items. */
    public Iterator iterator() {
        return cartLines.iterator();
    }

    /** Gets the userLogin associated with the cart; may be null */
    public GenericValue getUserLogin() {
        return this.userLogin;
    }

    public void setUserLogin(GenericValue userLogin, LocalDispatcher dispatcher) throws CartItemModifyException {
        this.userLogin = userLogin;
        this.handleNewUser(dispatcher);
       }

    public GenericValue getAutoUserLogin() {
        return this.autoUserLogin;
    }

    public void setAutoUserLogin(GenericValue autoUserLogin, LocalDispatcher dispatcher) throws CartItemModifyException {
        this.autoUserLogin = autoUserLogin;
        if (getUserLogin() == null) {
            this.handleNewUser(dispatcher);
        }
    }
    
    public void handleNewUser(LocalDispatcher dispatcher) throws CartItemModifyException {
        String partyId = this.getPartyId();
        if (UtilValidate.isNotEmpty(partyId)) {
            // recalculate all prices
            Iterator cartItemIter = this.iterator();
            while (cartItemIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) cartItemIter.next();
                cartItem.updatePrice(dispatcher, this);
            }
            
            // check all promo codes, remove on failed check
            Iterator promoCodeIter = this.productPromoCodes.iterator();
            while (promoCodeIter.hasNext()) {
                String promoCode = (String) promoCodeIter.next();
                String checkResult = ProductPromoWorker.checkCanUsePromoCode(promoCode, partyId, this.getDelegator());
                if (checkResult != null) {
                    promoCodeIter.remove();
                    Debug.logWarning("On user change promo code was removed because: " + checkResult, module);
                }
            }
            
            // rerun promotions
            ProductPromoWorker.doPromotions(this, dispatcher);
        }
    }

    public String getWebSiteId() {
        return this.webSiteId;
    }

    public void setWebSiteId(String webSiteId) {
        this.webSiteId = webSiteId;
    }

    public String getPartyId() {
        String partyId = null;

        if (partyId == null && getUserLogin() != null) {
            partyId = getUserLogin().getString("partyId");
        }
        if (partyId == null && getAutoUserLogin() != null) {
            partyId = getAutoUserLogin().getString("partyId");
        }
        return partyId;
    }

    public Double getPartyDaysSinceCreated(Timestamp nowTimestamp) {
        String partyId = this.getPartyId();
        if (UtilValidate.isEmpty(partyId)) {
            return null;
        }
        try {
            GenericValue party = this.getDelegator().findByPrimaryKeyCache("Party", UtilMisc.toMap("partyId", partyId));
            if (party == null) {
                return null;
            }
            Timestamp createdDate = party.getTimestamp("createdDate");
            if (createdDate == null) {
                return null;
            }
            double diffMillis = nowTimestamp.getTime() - createdDate.getTime();
            // millis per day: 1000.0 * 60.0 * 60.0 * 24.0 = 86400000.0
            return new Double((diffMillis) / 86400000.0);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up party when getting createdDate", module);
            return null;
        }
    }

    // =======================================================================
    // Methods for cart fields
    // =======================================================================

    /** Clears out the cart. */
    public void clear() {
        this.firstAttemptOrderId = null;
        this.poNumber = null;
        this.orderId = null;

        this.orderShipmentPreference.remove("shippingInstructions");
        this.orderShipmentPreference.remove("maySplit");
        this.orderShipmentPreference.remove("giftMessage");
        this.orderShipmentPreference.remove("isGift");

        this.orderAdditionalEmails = null;
        this.freeShippingProductPromoActions.clear();
        this.productPromoUseInfoList.clear();
        this.productPromoCodes.clear();

        this.paymentMethodIds.clear();
        this.paymentMethodTypeIds.clear();
        this.adjustments.clear();
        this.expireSingleUsePayments();
        this.cartLines.clear();
    }

    private void expireSingleUsePayments() {
        Iterator i = singleUsePaymentIds.iterator();
        Timestamp now = UtilDateTime.nowTimestamp();
        while (i.hasNext()) {
            String paymentMethodId = (String) i.next();
            GenericValue paymentMethod = null;
            try {
                paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Unable to get payment method record to expire : " + paymentMethodId, module);
            }
            if (paymentMethod != null) {
                paymentMethod.set("thruDate", now);
                try {
                    paymentMethod.store();
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to store single use PaymentMethod record : " + paymentMethod, module);
                }
            } else {
                Debug.logError("ERROR: Received back a null payment method record for expired ID : " + paymentMethodId, module);
            }
        }
    }

    /** Sets the order type. */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    /** Returns the order type. */
    public String getOrderType() {
        return this.orderType;
    }

    /** Sets the PO Number in the cart. */
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    /** Returns the po number. */
    public String getPoNumber() {
        return poNumber;
    }

    /** Checks to see if a Payment Method is selected. */
    public boolean isPaymentMethodSelected(String paymentMethodId) {
        if (paymentMethodId != null) {
            return this.paymentMethodIds.contains(paymentMethodId);
        } else {
            return false;
        }
    }

    /** Sets the amount of a Payment Method. */
    public void setPaymentMethodAmount(String paymentMethodId, Double amount, boolean isSingleUse) {
        if (paymentMethodId != null) {
            if (!this.paymentMethodIds.contains(paymentMethodId)) {
                this.paymentMethodIds.add(paymentMethodId);
            }
            this.paymentMethodAmounts.put(paymentMethodId, amount);
        }
        if (isSingleUse) {
            this.singleUsePaymentIds.add(paymentMethodId);
        }
    }

    public void setPaymentMethodAmount(String paymentMethodId, Double amount) {
        setPaymentMethodAmount(paymentMethodId, amount, false);
    }

    /** Returns the Payment Method Ids. */
    public List getPaymentMethodIds() {
        return paymentMethodIds;
    }

    /** Clears the list of Payment Method Ids. */
    public void clearPaymentMethodIds() {
        this.paymentMethodIds.clear();
    }

    /** Clears a specific Payment Method Id. */
    public void clearPaymentMethodId(String paymentMethodId) {
        this.paymentMethodIds.remove(paymentMethodId);
        this.paymentMethodAmounts.remove(paymentMethodId);
    }

    /** Add the Payment Method Type Id to the cart. */
    public void addPaymentMethodTypeId(String paymentMethodTypeId) {
        addPaymentMethodTypeId(paymentMethodTypeId, null);
    }

    /** Add the Payment Method Type Id with amount to the cart. */
    public void addPaymentMethodTypeId(String paymentMethodTypeId, Double amount) {
        if (paymentMethodTypeId != null) {
            this.paymentMethodTypeIds.add(paymentMethodTypeId);
            this.paymentMethodTypeAmounts.put(paymentMethodTypeId, amount);
        }
    }

    /** Returns set amount of the Payment Method. */
    public Double getPaymentMethodAmount(String paymentMethodId) {
        if (this.paymentMethodAmounts.get(paymentMethodId) != null) {
            return (Double) this.paymentMethodAmounts.get(paymentMethodId);
        } else {
            return new Double(0.00);
        }
    }

    /** Returns the total amount of the selected Payment Method(s). */
    public double getSelectedPaymentMethodsTotal() {
        double paymentMethodsTotal = 0.00;
        Iterator i = this.getPaymentMethodIds().iterator();
        while (i.hasNext()) {
            String paymentMethodId = (String) i.next();
            Double paymentTotal = (Double) this.paymentMethodAmounts.get(paymentMethodId);
            if (paymentTotal != null) {
                paymentMethodsTotal += paymentTotal.doubleValue();
            }
        }
        return paymentMethodsTotal;
    }

    /** Returns a list of PaymentMethod value objects selected in the cart. */
    public List getSelectedPaymentMethods() {
        List ids = getPaymentMethodIds();
        List methods = new ArrayList();
        try {
            Iterator i = ids.iterator();
            while (i.hasNext()) {
                String id = (String) i.next();
                GenericValue paymentMethod = this.getDelegator().findByPrimaryKeyCache("PaymentMethod", UtilMisc.toMap("paymentMethodId", id));
                methods.add(paymentMethod);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get selected payment methods from the database", module);
            return null;
        }
        return methods;
    }

    /** Returns the Payment Method Ids. */
    public List getPaymentMethodTypeIds() {
        return paymentMethodTypeIds;
    }

    /** Clears the list of Payment Method Type Ids. */
    public void clearPaymentMethodTypeIds() {
        this.paymentMethodTypeIds.clear();
    }

    /** Sets the billing account id string. */
    public void setBillingAccount(String billingAccountId, double amount) {
        this.billingAccountId = billingAccountId;
        this.billingAccountAmt = amount;
    }

    /** Returns the billing message string. */
    public String getBillingAccountId() {
        return this.billingAccountId;
    }

    /** Returns the amount to be billed to the billing account.*/
    public double getBillingAccountAmount() {
        return this.billingAccountAmt;
    }

    /** Sets the shipping contact mech id. */
    public void setShippingContactMechId(String shippingContactMechId) {
        // set the shipping address
        this.addContactMech("SHIPPING_LOCATION", shippingContactMechId);
    }

    /** Returns the shipping message string. */
    public String getShippingContactMechId() {
        return this.getContactMech("SHIPPING_LOCATION");
    }

    /** Returns the order level shipping amount */
    public double getOrderShipping() {
        return OrderReadHelper.calcOrderAdjustments(this.getAdjustments(), this.getSubTotal(), false, false, true);
    }

    /** Sets the shipment method type. */
    public void setShipmentMethodTypeId(String shipmentMethodTypeId) {
        orderShipmentPreference.set("shipmentMethodTypeId", shipmentMethodTypeId);
    }

    /** Returns the shipment method type */
    public String getShipmentMethodTypeId() {
        return orderShipmentPreference.getString("shipmentMethodTypeId");
    }

    /** Sets the shipping instructions. */
    public void setShippingInstructions(String shippingInstructions) {
        orderShipmentPreference.set("shippingInstructions", shippingInstructions);
    }

    /** Returns the shipping instructions. */
    public String getShippingInstructions() {
        return orderShipmentPreference.getString("shippingInstructions");
    }

    public void setMaySplit(Boolean maySplit) {
        orderShipmentPreference.set("maySplit", maySplit);
    }

    /** Returns Boolean.TRUE if the order may be split (null if unspecified) */
    public Boolean getMaySplit() {
        return orderShipmentPreference.getBoolean("maySplit");
    }

    public void setGiftMessage(String giftMessage) {
        orderShipmentPreference.set("giftMessage", giftMessage);
    }

    public String getGiftMessage() {
        return orderShipmentPreference.getString("giftMessage");
    }

    public void setIsGift(Boolean isGift) {
        orderShipmentPreference.set("isGift", isGift);
    }

    public Boolean getIsGift() {
        return orderShipmentPreference.getBoolean("isGift");
    }

    public GenericValue getOrderShipmentPreference() {
        return this.orderShipmentPreference;
    }

    public void setCarrierPartyId(String carrierPartyId) {
        orderShipmentPreference.set("carrierPartyId", carrierPartyId);
    }

    public String getCarrierPartyId() {
        return orderShipmentPreference.getString("carrierPartyId");
    }

    public void setOrderAdditionalEmails(String orderAdditionalEmails) {
        this.orderAdditionalEmails = orderAdditionalEmails;
    }

    public String getOrderAdditionalEmails() {
        return orderAdditionalEmails;
    }

    public List getPaymentMethods() {
        List paymentMethods = new LinkedList();

        if (paymentMethodIds != null && paymentMethodIds.size() > 0) {
            Iterator pmIdsIter = paymentMethodIds.iterator();

            while (pmIdsIter.hasNext()) {
                String paymentMethodId = (String) pmIdsIter.next();

                try {
                    paymentMethods.add(getDelegator().findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId)));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            }
        }
        return paymentMethods;
    }

    public GenericValue getShippingAddress() {
        if (this.getShippingContactMechId() != null) {
            try {
                return getDelegator().findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", this.getShippingContactMechId()));
            } catch (GenericEntityException e) {
                Debug.logWarning(e.toString(), module);
                return null;
            }
        } else {
            return null;
        }
    }

    /** Returns the tax amount from the cart object. */
    public double getTotalSalesTax() {
        double tempTax = 0.0;
        Iterator i = iterator();

        while (i.hasNext()) {
            tempTax += ((ShoppingCartItem) i.next()).getItemTax();
        }

        tempTax += OrderReadHelper.calcOrderAdjustments(this.getAdjustments(), getSubTotal(), false, true, false);

        return tempTax;
    }

    /** Returns the shipping amount from the cart object. */
    public double getTotalShipping() {
        double tempShipping = 0.0;
        Iterator i = iterator();

        while (i.hasNext()) {
            tempShipping += ((ShoppingCartItem) i.next()).getItemShipping();
        }

        tempShipping += OrderReadHelper.calcOrderAdjustments(this.getAdjustments(), getSubTotal(), false, false, true);

        return tempShipping;
    }

    /** Returns the item-total in the cart (not including discount/tax/shipping). */
    public double getItemTotal() {
        double itemTotal = 0.00;
        Iterator i = iterator();

        while (i.hasNext()) {
            itemTotal += ((ShoppingCartItem) i.next()).getBasePrice();
        }
        return itemTotal;
    }

    /** Returns the sub-total in the cart (item-total - discount). */
    public double getSubTotal() {
        double itemTotal = 0.00;
        Iterator i = iterator();

        while (i.hasNext()) {
            itemTotal += ((ShoppingCartItem) i.next()).getItemSubTotal();
        }
        return itemTotal;
    }

    /** Add a contact mech to this purpose; the contactMechPurposeTypeId is required */
    public void addContactMech(String contactMechPurposeTypeId, String contactMechId) {
        if (contactMechPurposeTypeId == null) throw new IllegalArgumentException("You must specify a contactMechPurposeTypeId to add a ContactMech");
        contactMechIdsMap.put(contactMechPurposeTypeId, contactMechId);
    }

    /** Get the contactMechId for this cart given the contactMechPurposeTypeId */
    public String getContactMech(String contactMechPurposeTypeId) {
        return (String) contactMechIdsMap.get(contactMechPurposeTypeId);
    }

    /** Remove the contactMechId from this cart given the contactMechPurposeTypeId */
    public String removeContactMech(String contactMechPurposeTypeId) {
        return (String) contactMechIdsMap.remove(contactMechPurposeTypeId);
    }

    public Map getOrderContactMechIds() {
        return this.contactMechIdsMap;
    }

    /** Get a List of adjustments on the order (ie cart) */
    public List getAdjustments() {
        return adjustments;
    }

    /** Add an adjustment to the order; don't worry about setting the orderId, orderItemSeqId or orderAdjustmentId; they will be set when the order is created */
    public void addAdjustment(GenericValue adjustment) {
        adjustments.add(adjustment);
    }

    public void removeAdjustment(int index) {
        adjustments.remove(index);
    }

    /** go through the order adjustments and remove all adjustments with the given type */
    public void removeAdjustmentByType(String orderAdjustmentTypeId) {
        if (orderAdjustmentTypeId == null) return;

        // make a list of adjustment lists including the cart adjustments and the cartItem adjustments for each item
        List adjsLists = new LinkedList();

        if (this.getAdjustments() != null) {
            adjsLists.add(this.getAdjustments());
        }
        Iterator cartIterator = this.iterator();

        while (cartIterator.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) cartIterator.next();

            if (item.getAdjustments() != null) {
                adjsLists.add(item.getAdjustments());
            }
        }

        Iterator adjsListsIter = adjsLists.iterator();

        while (adjsListsIter.hasNext()) {
            List adjs = (List) adjsListsIter.next();

            if (adjs != null) {
                for (int i = 0; i < adjs.size();) {
                    GenericValue orderAdjustment = (GenericValue) adjs.get(i);

                    if (orderAdjustmentTypeId.equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                        adjs.remove(i);
                    } else {
                        i++;
                    }
                }
            }
        }
    }

    public double getOrderOtherAdjustmentTotal() {
        return OrderReadHelper.calcOrderAdjustments(this.getAdjustments(), getSubTotal(), true, false, false);
    }

    /** Returns the total from the cart, including tax/shipping. */
    public double getGrandTotal() {
        List orderAdjustments = this.makeAllAdjustments();
        List orderItems = this.makeOrderItems();
        return OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
    }

    /** Returns the SHIPPABLE item-total in the cart. */
    public double getShippableTotal() {
        double itemTotal = 0.0;
        Iterator i = iterator();

        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();

            if (item.shippingApplies())
                itemTotal += item.getItemSubTotal();
        }
        return itemTotal;
    }

    /** Returns the total quantity in the cart. */
    public double getTotalQuantity() {
        double count = 0.0;
        Iterator i = iterator();

        while (i.hasNext()) {
            count += ((ShoppingCartItem) i.next()).getQuantity();
        }
        return count;
    }

    /** Returns the total SHIPPABLE quantity in the cart. */
    public double getShippableQuantity() {
        double count = 0.0;
        Iterator i = iterator();

        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();

            if (item.shippingApplies()) {
                count += item.getQuantity();
            }
        }
        return count;
    }

    /** Returns the total SHIPPABLE weight in the cart. */
    public double getShippableWeight() {
        double weight = 0.0;
        Iterator i = iterator();

        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();

            if (item.shippingApplies()) {
                weight += (item.getWeight() * item.getQuantity());
            }
        }
        return weight;
    }

    /** Returns a List of shippable item's size. */
    public List getShippableSizes() {
        List shippableSizes = new LinkedList();
        Iterator i = iterator();

        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();

            if (item.shippingApplies()) {
                shippableSizes.add(new Double(item.getSize()));
            }
        }
        return shippableSizes;
    }

    /** Returns the total weight in the cart. */
    public double getTotalWeight() {
        double weight = 0.0;
        Iterator i = iterator();

        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();

            weight += (item.getWeight() * item.getQuantity());
        }
        return weight;
    }

    /** Returns a Map of all features applied to products in the cart with quantities. */
    public Map getFeatureIdQtyMap() {
        Map featureMap = new HashMap();
        Iterator i = iterator();
        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            featureMap.putAll(item.getFeatureIdQtyMap());
        }
        return featureMap;
    }

    /** Returns true if the user wishes to view the cart everytime an item is added. */
    public boolean viewCartOnAdd() {
        return viewCartOnAdd;
    }

    /** Returns true if the user wishes to view the cart everytime an item is added. */
    public void setViewCartOnAdd(boolean viewCartOnAdd) {
        this.viewCartOnAdd = viewCartOnAdd;
    }

    /** Returns the order ID associated with this cart or null if no order has been created yet. */
    public String getOrderId() {
        return this.orderId;
    }

    /** Returns the first attempt order ID associated with this cart or null if no order has been created yet. */
    public String getFirstAttemptOrderId() {
        return this.firstAttemptOrderId;
    }

    /** Sets the orderId associated with this cart. */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /** Sets the first attempt orderId for this cart. */
    public void setFirstAttemptOrderId(String orderId) {
        this.firstAttemptOrderId = orderId;
    }

    /** Sets the currency for the cart. */
    public void setCurrency(LocalDispatcher dispatcher, String currencyUom) throws CartItemModifyException {
        String previousCurrency = this.currencyUom;
        this.currencyUom = currencyUom;
        if (!previousCurrency.equals(this.currencyUom)) {
            Iterator itemIterator = this.iterator();
            while (itemIterator.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) itemIterator.next();
                item.updatePrice(dispatcher, this);
            }
        }
    }

    /** Get the current currency setting. */
    public String getCurrency() {
        if (this.currencyUom != null) {
            return this.currencyUom;
        } else {
            return UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
        }
    }

    public void removeAllFreeShippingProductPromoActions() {
        this.freeShippingProductPromoActions.clear();
    }

    /** Removes a free shipping ProductPromoAction by trying to find one in the list with the same primary key. */
    public void removeFreeShippingProductPromoAction(GenericPK productPromoActionPK) {
        if (productPromoActionPK == null) return;

        Iterator fsppas = this.freeShippingProductPromoActions.iterator();
        while (fsppas.hasNext()) {
            if (productPromoActionPK.equals(((GenericValue) fsppas.next()).getPrimaryKey())) {
                fsppas.remove();
            }
        }
    }

    /** Adds a ProductPromoAction to be used for free shipping (must be of type free shipping, or nothing will be done). */
    public void addFreeShippingProductPromoAction(GenericValue productPromoAction) {
        if (productPromoAction == null) return;
        // is this a free shipping action?
        if (!"PROMO_FREE_SHIPPING".equals(productPromoAction.getString("productPromoActionTypeId"))) return;

        // to easily make sure that no duplicate exists, do a remove first
        this.removeFreeShippingProductPromoAction(productPromoAction.getPrimaryKey());
        this.freeShippingProductPromoActions.add(productPromoAction);
    }

    public List getFreeShippingProductPromoActions() {
        return this.freeShippingProductPromoActions;
    }

    public void addProductPromoUse(String productPromoId, String productPromoCodeId) {
        if (UtilValidate.isNotEmpty(productPromoCodeId) && !this.productPromoCodes.contains(productPromoCodeId)) {
            throw new IllegalStateException("Cannot add a use to a promo code use for a code that has not been entered.");
        }
        this.productPromoUseInfoList.add(new ProductPromoUseInfo(productPromoId, productPromoCodeId));
    }

    public void clearProductPromoUseInfo() {
        // clear out info for general promo use
        this.productPromoUseInfoList.clear();
        // clear out info about which cart items have been used in promos
        Iterator cartLineIter = this.iterator();
        while (cartLineIter.hasNext()) {
            ShoppingCartItem cartLine = (ShoppingCartItem) cartLineIter.next();
            cartLine.clearPromoRuleUseInfo();
        }
    }

    public Iterator getProductPromoUseInfoIter() {
        return productPromoUseInfoList.iterator();
    }

    public int getProductPromoUseCount(String productPromoId) {
        if (productPromoId == null) return 0;
        int useCount = 0;
        Iterator productPromoUseInfoIter = this.productPromoUseInfoList.iterator();
        while (productPromoUseInfoIter.hasNext()) {
            ProductPromoUseInfo productPromoUseInfo = (ProductPromoUseInfo) productPromoUseInfoIter.next();
            if (productPromoId.equals(productPromoUseInfo.productPromoId)) {
                useCount++;
            }
        }
        return useCount;
    }

    public int getProductPromoCodeUse(String productPromoCodeId) {
        if (productPromoCodeId == null) return 0;
        int useCount = 0;
        Iterator productPromoUseInfoIter = this.productPromoUseInfoList.iterator();
        while (productPromoUseInfoIter.hasNext()) {
            ProductPromoUseInfo productPromoUseInfo = (ProductPromoUseInfo) productPromoUseInfoIter.next();
            if (productPromoCodeId.equals(productPromoUseInfo.productPromoCodeId)) {
                useCount++;
            }
        }
        return useCount;
    }

    /** Adds a promotion code to the cart, checking if it is valid. If it is valid this will return null, otherwise it will return a message stating why it was not valid 
     * @param productPromoCodeId The promotion code to check and add
     * @return String that is null if valid, and added to cart, or an error message of the code was not valid and not added to the cart. 
     */
    public String addProductPromoCode(String productPromoCodeId, LocalDispatcher dispatcher) {
        if (this.productPromoCodes.contains(productPromoCodeId)) {
            return "The promotion code [" + productPromoCodeId + "] has already been entered.";
        }
        // if the promo code requires it make sure the code is valid
        String checkResult = ProductPromoWorker.checkCanUsePromoCode(productPromoCodeId, this.getPartyId(), this.getDelegator());
        if (checkResult == null) {
            this.productPromoCodes.add(productPromoCodeId);
            // new promo code, re-evaluate promos
            ProductPromoWorker.doPromotions(this, dispatcher);
            return null;
        } else {
            return checkResult;
        }
    }

    public Set getProductPromoCodesEntered() {
        return this.productPromoCodes;
    }

    public synchronized void resetPromoRuleUse(String productPromoId, String productPromoRuleId) {
        Iterator lineIter = this.iterator();
        while (lineIter.hasNext()) {
            ShoppingCartItem cartItem = (ShoppingCartItem) lineIter.next();
            cartItem.resetPromoRuleUse(productPromoId, productPromoRuleId);
        }
    }

    public synchronized void confirmPromoRuleUse(String productPromoId, String productPromoRuleId) {
        Iterator lineIter = this.iterator();
        while (lineIter.hasNext()) {
            ShoppingCartItem cartItem = (ShoppingCartItem) lineIter.next();
            cartItem.confirmPromoRuleUse(productPromoId, productPromoRuleId);
        }
    }

    // =======================================================================
    // Methods used for order creation
    // =======================================================================

    private void explodeItems(LocalDispatcher dispatcher) {
        synchronized (cartLines) {
            if (dispatcher != null) {
                List cartLineItems = new LinkedList(cartLines);
                Iterator itemIter = cartLineItems.iterator();

                while (itemIter.hasNext()) {
                    ShoppingCartItem item = (ShoppingCartItem) itemIter.next();

                    Debug.logInfo("Item qty: " + item.getQuantity(), module);
                    try {
                        item.explodeItem(this, dispatcher);
                    } catch (CartItemModifyException e) {
                        Debug.logError(e, "Problem exploding item! Item not exploded.", module);
                    }
                }
            }
        }
    }

    public List makeOrderItems() {
        return makeOrderItems(false, null);
    }

    public List makeOrderItems(boolean explodeItems, LocalDispatcher dispatcher) {
        // do the explosion
        if (explodeItems && dispatcher != null)
            explodeItems(dispatcher);

        // now build the lines
        synchronized (cartLines) {
            List result = new LinkedList();
            long cartLineSize = cartLines.size();
            long seqId = 1;
            Iterator itemIter = cartLines.iterator();

            while (itemIter.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) itemIter.next();

                // format the string with enough leading zeroes for the number of cartLines
                NumberFormat nf = NumberFormat.getNumberInstance();

                if (cartLineSize > 9) {
                    nf.setMinimumIntegerDigits(2);
                } else if (cartLineSize > 99) {
                    nf.setMinimumIntegerDigits(3);
                } else if (cartLineSize > 999) {
                    nf.setMinimumIntegerDigits(4);
                } else if (cartLineSize > 9999) {
                    // if it's more than 9999, something's up... hit the sky
                    nf.setMinimumIntegerDigits(18);
                }

                String orderItemSeqId = nf.format(seqId);

                seqId++;
                item.setOrderItemSeqId(orderItemSeqId);

                // the initial status for all item types
                String initialStatus = "ITEM_CREATED";

                GenericValue orderItem = getDelegator().makeValue("OrderItem", null);
                orderItem.set("orderItemSeqId", orderItemSeqId);
                orderItem.set("orderItemTypeId", item.getItemType());
                orderItem.set("productId", item.getProductId());
                orderItem.set("prodCatalogId", item.getProdCatalogId());
                orderItem.set("productCategoryId", item.getProductCategoryId());
                orderItem.set("quantity", new Double(item.getQuantity()));
                orderItem.set("unitPrice", new Double(item.getBasePrice()));
                orderItem.set("unitListPrice", new Double(item.getListPrice()));

                orderItem.set("shoppingListId", item.getShoppingListId());
                orderItem.set("shoppingListItemSeqId", item.getShoppingListItemSeqId());

                orderItem.set("itemDescription", item.getName());
                orderItem.set("comments", item.getItemComment());
                orderItem.set("correspondingPoId", this.getPoNumber());
                orderItem.set("statusId", initialStatus);
                result.add(orderItem);
                // don't do anything with adjustments here, those will be added below in makeAllAdjustments
            }
            return result;
        }
    }

    /** make a list of all adjustments including order adjustments, order line adjustments, and special adjustments (shipping and tax if applicable) */
    public List makeAllAdjustments() {
        List allAdjs = new LinkedList();

        // before returning adjustments, go through them to find all that need counter adjustments (for instance: free shipping)
        Iterator allAdjsIter = this.getAdjustments().iterator();

        while (allAdjsIter.hasNext()) {
            GenericValue orderAdjustment = (GenericValue) allAdjsIter.next();

            allAdjs.add(orderAdjustment);

            if ("SHIPPING_CHARGES".equals(orderAdjustment.get("orderAdjustmentTypeId"))) {
                Iterator fsppas = this.freeShippingProductPromoActions.iterator();

                while (fsppas.hasNext()) {
                    GenericValue productPromoAction = (GenericValue) fsppas.next();

                    if ((productPromoAction.get("productId") == null || productPromoAction.getString("productId").equals(this.getShipmentMethodTypeId())) &&
                        (productPromoAction.get("partyId") == null || productPromoAction.getString("partyId").equals(this.getCarrierPartyId()))) {
                        Double shippingAmount = new Double(-OrderReadHelper.calcOrderAdjustment(orderAdjustment, getSubTotal()));
                        // always set orderAdjustmentTypeId to SHIPPING_CHARGES for free shipping adjustments
                        GenericValue fsOrderAdjustment = getDelegator().makeValue("OrderAdjustment",
                                UtilMisc.toMap("orderItemSeqId", orderAdjustment.get("orderItemSeqId"), "orderAdjustmentTypeId", "SHIPPING_CHARGES", "amount", shippingAmount,
                                    "productPromoId", productPromoAction.get("productPromoId"), "productPromoRuleId", productPromoAction.get("productPromoRuleId"),
                                    "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));

                        allAdjs.add(fsOrderAdjustment);

                        // if free shipping IS applied to this orderAdjustment, break
                        // out of the loop so that even if there are multiple free
                        // shipping adjustments that apply to this orderAdjustment it
                        // will only be compensated for once
                        break;
                    }
                }
            }
        }

        // add all of the item adjustments to this list too
        Iterator itemIter = cartLines.iterator();

        while (itemIter.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) itemIter.next();
            Collection adjs = item.getAdjustments();

            if (adjs != null) {
                Iterator adjIter = adjs.iterator();

                while (adjIter.hasNext()) {
                    GenericValue orderAdjustment = (GenericValue) adjIter.next();

                    orderAdjustment.set("orderItemSeqId", item.getOrderItemSeqId());
                    allAdjs.add(orderAdjustment);

                    if ("SHIPPING_CHARGES".equals(orderAdjustment.get("orderAdjustmentTypeId"))) {
                        Iterator fsppas = this.freeShippingProductPromoActions.iterator();

                        while (fsppas.hasNext()) {
                            GenericValue productPromoAction = (GenericValue) fsppas.next();

                            if ((productPromoAction.get("productId") == null || productPromoAction.getString("productId").equals(item.getShipmentMethodTypeId())) &&
                                (productPromoAction.get("partyId") == null || productPromoAction.getString("partyId").equals(item.getCarrierPartyId()))) {
                                Double shippingAmount = new Double(-OrderReadHelper.calcItemAdjustment(orderAdjustment, new Double(item.getQuantity()), new Double(item.getItemSubTotal())));
                                // always set orderAdjustmentTypeId to SHIPPING_CHARGES for free shipping adjustments
                                GenericValue fsOrderAdjustment = getDelegator().makeValue("OrderAdjustment",
                                        UtilMisc.toMap("orderItemSeqId", orderAdjustment.get("orderItemSeqId"), "orderAdjustmentTypeId", "SHIPPING_CHARGES", "amount", shippingAmount,
                                            "productPromoId", productPromoAction.get("productPromoId"), "productPromoRuleId", productPromoAction.get("productPromoRuleId"),
                                            "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));

                                allAdjs.add(fsOrderAdjustment);

                                // if free shipping IS applied to this orderAdjustment, break
                                // out of the loop so that even if there are multiple free
                                // shipping adjustments that apply to this orderAdjustment it
                                // will only be compensated for once
                                break;
                            }
                        }
                    }
                }
            }
        }

        return allAdjs;
    }

    /** make a list of all OrderPaymentPreferences including all payment methods and types */
    public List makeAllOrderPaymentPreferences() {
        List allOpPrefs = new LinkedList();

        // first create the payment methods (online payments?)
        List paymentMethods = this.getPaymentMethods();
        Iterator pmi = paymentMethods.iterator();
        while (pmi.hasNext()) {
            GenericValue paymentMethod = (GenericValue) pmi.next();
            GenericValue p = delegator.makeValue("OrderPaymentPreference", new HashMap());
            p.set("paymentMethodTypeId", paymentMethod.get("paymentMethodTypeId"));
            p.set("paymentMethodId", paymentMethod.get("paymentMethodId"));
            p.set("statusId", "PAYMENT_NOT_AUTH");
            if (this.paymentMethodAmounts.get(paymentMethod.getString("paymentMethodId")) != null) {
                p.set("maxAmount", this.paymentMethodAmounts.get(paymentMethod.getString("paymentMethodId")));
            }
            allOpPrefs.add(p);
        }

        // next create the payment types (offline payments?)
        Iterator pti = this.getPaymentMethodTypeIds().iterator();
        while (pti.hasNext()) {
            String paymentMethodTypeId = (String) pti.next();
            GenericValue p = delegator.makeValue("OrderPaymentPreference", new HashMap());
            p.set("paymentMethodTypeId", paymentMethodTypeId);
            p.set("statusId", "PAYMENT_NOT_RECEIVED");
            if (this.paymentMethodTypeAmounts.get(paymentMethodTypeId) != null) {
                p.set("maxAmount", this.paymentMethodTypeAmounts.get(paymentMethodTypeId));
            }
            allOpPrefs.add(p);
        }

        return allOpPrefs;
    }

    /** make a list of all OrderShipmentPreferences including ones for the order and order lines */
    public List makeAllOrderShipmentPreferences() {
        List allOshPrefs = new LinkedList();

        // if nothing has been put into the value, don't set it; must at least have a carrierPartyId and a shipmentMethodTypeId
        if (this.orderShipmentPreference.size() > 1) {
            allOshPrefs.add(this.orderShipmentPreference);
        }

        // add all of the item adjustments to this list too
        Iterator itemIter = cartLines.iterator();

        while (itemIter.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) itemIter.next();
            // if nothing has been put into the value, don't set it; must at least have a carrierPartyId and a shipmentMethodTypeId
            GenericValue itemOrderShipmentPreference = item.getOrderShipmentPreference();

            if (itemOrderShipmentPreference != null && itemOrderShipmentPreference.size() > 1) {
                itemOrderShipmentPreference.set("orderItemSeqId", item.getOrderItemSeqId());
                allOshPrefs.add(item.getOrderShipmentPreference());
            }
        }

        return allOshPrefs;
    }

    /** make a list of OrderItemPriceInfos from the ShoppingCartItems */
    public List makeAllOrderItemPriceInfos() {
        List allInfos = new LinkedList();

        // add all of the item adjustments to this list too
        Iterator itemIter = cartLines.iterator();

        while (itemIter.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) itemIter.next();
            Collection infos = item.getOrderItemPriceInfos();

            if (infos != null) {
                Iterator infosIter = infos.iterator();

                while (infosIter.hasNext()) {
                    GenericValue orderItemPriceInfo = (GenericValue) infosIter.next();

                    orderItemPriceInfo.set("orderItemSeqId", item.getOrderItemSeqId());
                    allInfos.add(orderItemPriceInfo);
                }
            }
        }

        return allInfos;
    }

    public List makeProductPromoUses() {
        List productPromoUses = new ArrayList(this.productPromoUseInfoList.size());
        String partyId = this.getPartyId();
        int sequenceValue = 0;
        Iterator productPromoUseInfoIter = this.productPromoUseInfoList.iterator();
        while (productPromoUseInfoIter.hasNext()) {
            ProductPromoUseInfo productPromoUseInfo = (ProductPromoUseInfo) productPromoUseInfoIter.next();
            GenericValue productPromoUse = this.getDelegator().makeValue("ProductPromoUse", null);
            productPromoUse.set("promoSequenceId", UtilFormatOut.formatPaddedNumber(sequenceValue, 5));
            productPromoUse.set("productPromoId", productPromoUseInfo.getProductPromoId());
            productPromoUse.set("productPromoCodeId", productPromoUseInfo.getProductPromoCodeId());
            productPromoUse.set("partyId", partyId);
            productPromoUses.add(productPromoUse);
            sequenceValue++;
        }
        return productPromoUses;
    }
        
    /** make a list of SurveyResponse object to update with order information set */
    public List makeAllOrderItemSurveyResponses() {
        List allInfos = new LinkedList();
        Iterator itemIter = this.iterator();
        while (itemIter.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) itemIter.next();
            List responses = (List) item.getAttribute("surveyResponses");
            if (responses != null) {
                Iterator ri = responses.iterator();
                while (ri.hasNext()) {
                    String responseId = (String) ri.next();
                    GenericValue response = null;
                    try {
                        response = delegator.findByPrimaryKey("SurveyResponse", UtilMisc.toMap("surveyResponseId", responseId));
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Unable to obtain SurveyResponse record for ID : " + responseId, module);
                    }
                    if (response != null) {
                        response.set("orderItemSeqId", item.getOrderItemSeqId());
                        allInfos.add(response);
                    }
                }
            }
        }
        return allInfos;
    }

    /** make a list of OrderContactMechs from the ShoppingCart and the ShoppingCartItems */
    public List makeAllOrderContactMechs() {
        List allOrderContactMechs = new LinkedList();

        Map contactMechIds = this.getOrderContactMechIds();

        if (contactMechIds != null) {
            Iterator cMechIdsIter = contactMechIds.entrySet().iterator();

            while (cMechIdsIter.hasNext()) {
                Map.Entry entry = (Map.Entry) cMechIdsIter.next();
                GenericValue orderContactMech = getDelegator().makeValue("OrderContactMech", null);

                orderContactMech.set("contactMechPurposeTypeId", entry.getKey());
                orderContactMech.set("contactMechId", entry.getValue());
                allOrderContactMechs.add(orderContactMech);
            }
        }

        return allOrderContactMechs;
    }

    /** make a list of OrderContactMechs from the ShoppingCart and the ShoppingCartItems */
    public List makeAllOrderItemContactMechs() {
        List allOrderContactMechs = new LinkedList();

        Iterator itemIter = cartLines.iterator();

        while (itemIter.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) itemIter.next();
            Map itemContactMechIds = item.getOrderItemContactMechIds();

            if (itemContactMechIds != null) {
                Iterator cMechIdsIter = itemContactMechIds.entrySet().iterator();

                while (cMechIdsIter.hasNext()) {
                    Map.Entry entry = (Map.Entry) cMechIdsIter.next();
                    GenericValue orderContactMech = getDelegator().makeValue("OrderItemContactMech", null);

                    orderContactMech.set("contactMechPurposeTypeId", entry.getKey());
                    orderContactMech.set("contactMechId", entry.getValue());
                    orderContactMech.set("orderItemSeqId", item.getOrderItemSeqId());
                    allOrderContactMechs.add(orderContactMech);
                }
            }
        }

        return allOrderContactMechs;
    }

    /** Returns a Map of cart values to pass to the storeOrder service */
    public Map makeCartMap(LocalDispatcher dispatcher, boolean explodeItems) {
        Map result = new HashMap();

        result.put("orderTypeId", this.getOrderType());
        result.put("orderItems", this.makeOrderItems(explodeItems, dispatcher));
        result.put("orderAdjustments", this.makeAllAdjustments());
        result.put("orderItemPriceInfos", this.makeAllOrderItemPriceInfos());
        result.put("orderProductPromoUses", this.makeProductPromoUses());

        result.put("orderContactMechs", this.makeAllOrderContactMechs());
        result.put("orderItemContactMechs", this.makeAllOrderItemContactMechs());
        result.put("orderPaymentPreferences", this.makeAllOrderPaymentPreferences());
        result.put("orderShipmentPreferences", this.makeAllOrderShipmentPreferences());
        result.put("orderItemSurveyResponses", this.makeAllOrderItemSurveyResponses());

        result.put("firstAttemptOrderId", this.getFirstAttemptOrderId());
        result.put("currencyUom", this.getCurrency());
        result.put("billingAccountId", this.getBillingAccountId());
        return result;
    }

    protected void finalize() throws Throwable {
        this.clear();
        super.finalize();
    }

    public List getLineListOrderedByBasePrice(boolean ascending) {
        List result = new ArrayList(this.cartLines);
        Collections.sort(result, new BasePriceOrderComparator(ascending));
        return result;
    }

    static class BasePriceOrderComparator implements Comparator {

        private boolean ascending = false;

        BasePriceOrderComparator(boolean ascending) {
            this.ascending = ascending;
        }

        public int compare(java.lang.Object obj, java.lang.Object obj1) {
            ShoppingCartItem cartItem = (ShoppingCartItem) obj;
            ShoppingCartItem cartItem1 = (ShoppingCartItem) obj1;

            int compareValue = new Double(cartItem.getBasePrice()).compareTo(new Double(cartItem1.getBasePrice()));
            if (this.ascending) {
                return compareValue;
            } else {
                return -compareValue;
            }
        }

        public boolean equals(java.lang.Object obj) {
            if (obj instanceof BasePriceOrderComparator) {
                return this.ascending == ((BasePriceOrderComparator) obj).ascending;
            } else {
                return false;
            }
        }
    }
}
