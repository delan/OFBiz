/*
 * $Id$
 *
 *  Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.service.LocalDispatcher;

import org.apache.commons.collections.map.LinkedMap;

/**
 * Shopping Cart Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class ShoppingCart implements Serializable {

    public static final String module = ShoppingCart.class.getName();

    private String orderType = "SALES_ORDER"; // default orderType
    private String poNumber = null;
    private String orderId = null;
    private String firstAttemptOrderId = null;
    private String billingAccountId = null;
    private double billingAccountAmt = 0.00;
    private String currencyUom = null;

    private String defaultItemDeliveryDate = null;
    private String defaultItemComment = null;

    private String orderAdditionalEmails = null;
    private boolean viewCartOnAdd = false;

    private Timestamp lastListRestore = null;
    private String autoSaveListId = null;

    /** Holds value of order adjustments. */
    private List adjustments = new LinkedList();
    private List cartLines = new LinkedList();
    private List paymentInfo = new LinkedList();
    private List shipInfo = new LinkedList();
    private Map contactMechIdsMap = new HashMap();
    
    /** contains a list of partyId for each roleTypeId (key) */
    private Map additionalPartyRole = new HashMap();

    public static class ProductPromoUseInfo implements Serializable {
        public String productPromoId = null;
        public String productPromoCodeId = null;
        public double totalDiscountAmount = 0;
        public double quantityLeftInActions = 0;

        public ProductPromoUseInfo(String productPromoId, String productPromoCodeId, double totalDiscountAmount, double quantityLeftInActions) {
            this.productPromoId = productPromoId;
            this.productPromoCodeId = productPromoCodeId;
            this.totalDiscountAmount = totalDiscountAmount;
            this.quantityLeftInActions = quantityLeftInActions;
        }

        public String getProductPromoId() { return this.productPromoId; }
        public String getProductPromoCodeId() { return this.productPromoCodeId; }
        public double getTotalDiscountAmount() { return this.totalDiscountAmount; }
        public double getQuantityLeftInActions() { return this.quantityLeftInActions; }
    }

    public static class CartShipInfo implements Serializable {
        public LinkedMap shipItemInfo = new LinkedMap();
        public List shipTaxAdj = new LinkedList();
        public String contactMechId = null;
        public String shipmentMethodTypeId = null;
        public String carrierRoleTypeId = null;
        public String carrierPartyId = null;
        public String giftMessage = null;
        public String shippingInstructions = null;
        public String maySplit = "N";
        public String isGift = "N";
        public double shipEstimate = 0.00;

        public List makeItemShipGroupAndAssoc(GenericDelegator delegator, ShoppingCart cart, long groupIndex) {
            String shipGroupSeqId = UtilFormatOut.formatPaddedNumber(groupIndex, 5);
            List values = new LinkedList();

            // create order contact mech for shipping address
            if (contactMechId != null) {
                GenericValue orderCm = delegator.makeValue("OrderContactMech", null);
                orderCm.set("contactMechPurposeTypeId", "SHIPPING_LOCATION");
                orderCm.set("contactMechId", contactMechId);
                values.add(orderCm);
            }

            // create the ship group
            GenericValue shipGroup = delegator.makeValue("OrderItemShipGroup", null);
            shipGroup.set("shipmentMethodTypeId", shipmentMethodTypeId);
            shipGroup.set("carrierRoleTypeId", carrierRoleTypeId);
            shipGroup.set("carrierPartyId", carrierPartyId);
            shipGroup.set("shippingInstructions", shippingInstructions);
            shipGroup.set("giftMessage", giftMessage);
            shipGroup.set("contactMechId", contactMechId);
            shipGroup.set("maySplit", new String(maySplit));
            shipGroup.set("isGift", new String(isGift));
            shipGroup.set("shipGroupSeqId", shipGroupSeqId);
            values.add(shipGroup);

            // create the shipping estimate adjustments
            if (shipEstimate != 0) {
                GenericValue shipAdj = delegator.makeValue("OrderAdjustment", null);
                shipAdj.set("orderAdjustmentTypeId", "SHIPPING_CHARGES");
                shipAdj.set("amount", new Double(shipEstimate));
                shipAdj.set("shipGroupSeqId", shipGroupSeqId);
                values.add(shipAdj);
            }

            // create the top level tax adjustments
            Iterator ti = shipTaxAdj.iterator();
            while (ti.hasNext()) {
                GenericValue taxAdj = (GenericValue) ti.next();
                taxAdj.set("shipGroupSeqId", shipGroupSeqId);
                values.add(taxAdj);
            }

            // create the ship group item associations
            Iterator i = shipItemInfo.keySet().iterator();
            while (i.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) i.next();
                CartShipItemInfo itemInfo = (CartShipItemInfo) shipItemInfo.get(item);

                GenericValue assoc = delegator.makeValue("OrderItemShipGroupAssoc", null);
                assoc.set("orderItemSeqId", item.getOrderItemSeqId());
                assoc.set("shipGroupSeqId", shipGroupSeqId);
                assoc.set("quantity", new Double(itemInfo.quantity));
                values.add(assoc);

                // create the item tax adjustment                
                Iterator iti = itemInfo.itemTaxAdj.iterator();
                while (iti.hasNext()) {
                    GenericValue taxAdj = (GenericValue) iti.next();
                    taxAdj.set("orderItemSeqId", item.getOrderItemSeqId());
                    taxAdj.set("shipGroupSeqId", shipGroupSeqId);
                    values.add(taxAdj);
                }
            }

            return values;
        }

        public CartShipItemInfo setItemInfo(ShoppingCartItem item, double quantity, List taxAdj) {
            CartShipItemInfo itemInfo = (CartShipItemInfo) shipItemInfo.get(item);
            if (itemInfo == null) {
                itemInfo = new CartShipItemInfo();
                itemInfo.item = item;
                shipItemInfo.put(item, itemInfo);
            }
            itemInfo.quantity = quantity;
            itemInfo.itemTaxAdj.clear();
            itemInfo.itemTaxAdj.addAll(taxAdj);
            return itemInfo;
        }

        public CartShipItemInfo setItemInfo(ShoppingCartItem item, List taxAdj) {
            CartShipItemInfo itemInfo = (CartShipItemInfo) shipItemInfo.get(item);
            if (itemInfo == null) {
                itemInfo = new CartShipItemInfo();
                itemInfo.item = item;
                shipItemInfo.put(item, itemInfo);
            }
            itemInfo.itemTaxAdj.clear();
            itemInfo.itemTaxAdj.addAll(taxAdj);
            return itemInfo;
        }

        public CartShipItemInfo setItemInfo(ShoppingCartItem item, double quantity) {
            CartShipItemInfo itemInfo = (CartShipItemInfo) shipItemInfo.get(item);
            if (itemInfo == null) {
                itemInfo = new CartShipItemInfo();
                itemInfo.item = item;
                shipItemInfo.put(item, itemInfo);
            }
            itemInfo.quantity = quantity;
            return itemInfo;
        }

        public CartShipItemInfo getShipItemInfo(ShoppingCartItem item) {
            return (CartShipItemInfo) shipItemInfo.get(item);
        }

        public Set getShipItems() {
            return shipItemInfo.keySet();
        }

        public double getTotalTax(ShoppingCart cart) {
            double taxTotal = 0.00;
            for (int i = 0; i < shipTaxAdj.size(); i++) {
                GenericValue v = (GenericValue) shipTaxAdj.get(i);
                taxTotal += OrderReadHelper.calcOrderAdjustment(v, cart.getSubTotal());
            }

            Iterator iter = shipItemInfo.values().iterator();
            while (iter.hasNext()) {
                CartShipItemInfo info = (CartShipItemInfo) iter.next();
                taxTotal += info.getItemTax(cart);
            }

            return taxTotal;
        }

        public static class CartShipItemInfo implements Serializable {
            public List itemTaxAdj = new LinkedList();
            public ShoppingCartItem item = null;
            public double quantity = 0;

            public double getItemTax(ShoppingCart cart) {
                double itemTax = 0.00;

                for (int i = 0; i < itemTaxAdj.size(); i++) {
                    GenericValue v = (GenericValue) itemTaxAdj.get(i);
                    itemTax += OrderReadHelper.calcItemAdjustment(v, new Double(quantity), new Double(item.getBasePrice()));
                }

                return itemTax;
            }

            public double getItemQuantity() {
                return this.quantity;
            }
        }
    }

    public static class CartPaymentInfo implements Serializable, Comparable {
        public String paymentMethodTypeId = null;
        public String paymentMethodId = null;
        public String postalCode = null;
        public String[] refNum = new String[2];
        public Double amount = null;
        public boolean singleUse = false;

        public GenericValue getValueObject(GenericDelegator delegator) {
            String entityName = null;
            Map lookupFields = null;
            if (paymentMethodId != null) {
                lookupFields = UtilMisc.toMap("paymentMethodId", paymentMethodId);
                entityName = "PaymentMethod";
            } else if (paymentMethodTypeId != null) {
                lookupFields = UtilMisc.toMap("paymentMethodTypeId", paymentMethodTypeId);
                entityName = "PaymentMethodType";
            }

            try {
                return delegator.findByPrimaryKeyCache(entityName, lookupFields);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }

            return null;
        }

        public List makeOrderPaymentInfos(GenericDelegator delegator) {
            GenericValue valueObj = this.getValueObject(delegator);
            List values = new LinkedList();
            if (valueObj != null) {
                // first create a BILLING_LOCATION for the payment method address if there is one
                if ("PaymentMethod".equals(valueObj.getEntityName())) {
                    String paymentMethodTypeId = valueObj.getString("paymentMethodTypeId");
                    String paymentMethodId = valueObj.getString("paymentMethodId");
                    Map lookupFields = UtilMisc.toMap("paymentMethodId", paymentMethodId);
                    String billingAddressId = null;

                    // billing account, credit card, gift card, eft account all have postal address
                    try {
                        GenericValue pmObj = null;
                        if ("CREDIT_CARD".equals(paymentMethodTypeId)) {
                            pmObj = delegator.findByPrimaryKey("CreditCard", lookupFields);
                        } else if ("GIFT_CARD".equals(paymentMethodTypeId)) {
                            pmObj = delegator.findByPrimaryKey("GiftCard", lookupFields);
                        } else if ("EFT_ACCOUNT".equals(paymentMethodTypeId)) {
                            pmObj = delegator.findByPrimaryKey("BillingAccount", lookupFields);
                        } else if ("EXT_BILLACT".equals(paymentMethodTypeId)) {
                            pmObj = delegator.findByPrimaryKey("BillingAccount", lookupFields);
                        }
                        if (pmObj != null) {
                            billingAddressId = pmObj.getString("contactMechId");
                        } else {
                            Debug.logInfo("No PaymentMethod Object Found - " + paymentMethodId, module);
                        }
                    } catch (GenericEntityException e) {
                        Debug.logError(e ,module);
                    }
                    if (UtilValidate.isNotEmpty(billingAddressId)) {
                        GenericValue orderCm = delegator.makeValue("OrderContactMech", null);
                        orderCm.set("contactMechPurposeTypeId", "BILLING_LOCATION");
                        orderCm.set("contactMechId", billingAddressId);
                        values.add(orderCm);
                    }
                }

                // create the OrderPaymentPreference record
                GenericValue opp = delegator.makeValue("OrderPaymentPreference", new HashMap());
                opp.set("paymentMethodTypeId", valueObj.getString("paymentMethodTypeId"));
                opp.set("paymentMethodId", paymentMethodId);
                opp.set("billingPostalCode", postalCode);
                opp.set("maxAmount", amount);
                if (refNum != null) {
                    opp.set("manualRefNum", refNum[0]);
                    opp.set("manualAuthCode", refNum[1]);
                }
                if (paymentMethodId != null) {
                    opp.set("statusId", "PAYMENT_NOT_AUTH");
                } else if (paymentMethodTypeId != null) {
                    // external payment method types require notification when received
                    // internal payment method types are assumed to be in-hand
                    if (paymentMethodTypeId.startsWith("EXT_")) {
                        opp.set("statusId", "PAYMENT_NOT_RECEIVED");
                    } else {
                        opp.set("statusId", "PAYMENT_RECEIVED");
                    }
                }
                Debug.log("Creating OrderPaymentPreference - " + opp, module);
                values.add(opp);
            }

            return values;
        }

        public int compareTo(Object o) {
            CartPaymentInfo that = (CartPaymentInfo) o;
            if (this.paymentMethodId != null) {
                if (that.paymentMethodId == null) {
                    return 1;
                } else {
                    int pmCmp = this.paymentMethodId.compareTo(that.paymentMethodId);
                    if (pmCmp == 0) {
                        if (this.refNum != null && this.refNum[0] != null) {
                            if (that.refNum != null && that.refNum[0] != null) {
                                return this.refNum[0].compareTo(that.refNum[0]);
                            } else {
                                return 1;
                            }
                        } else {
                            if (that.refNum != null && that.refNum[0] != null) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    } else {
                        return pmCmp;
                    }
                }
            } else {
                if (that.paymentMethodId != null) {
                    return -1;
                } else {
                    int pmtCmp = this.paymentMethodTypeId.compareTo(that.paymentMethodTypeId);
                    if (pmtCmp == 0) {
                        if (this.refNum != null && this.refNum[0] != null) {
                            if (that.refNum != null && that.refNum[0] != null) {
                                return this.refNum[0].compareTo(that.refNum[0]);
                            } else {
                                return 1;
                            }
                        } else {
                            if (that.refNum != null && that.refNum[0] != null) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    } else {
                        return pmtCmp;
                    }
                }
            }
        }

        public String toString() {
            return "Pm: " + paymentMethodId + " / PmType: " + paymentMethodTypeId + " / Amt: " + amount + " / Ref: " + refNum;
        }
    }

    /** Contains a List for each productPromoId (key) containing a productPromoCodeId (or empty string for no code) for each use of the productPromoId */
    private List productPromoUseInfoList = new LinkedList();
    /** Contains the promo codes entered */
    private Set productPromoCodes = new HashSet();
    private List freeShippingProductPromoActions = new ArrayList();
    /** Note that even though this is promotion info, it should NOT be cleared when the promos are cleared, it is a preference that will be used in the next promo calculation */
    private Map desiredAlternateGiftByAction = new HashMap();
    private Timestamp cartCreatedTs = UtilDateTime.nowTimestamp();

    private transient GenericDelegator delegator = null;
    private String delegatorName = null;

    protected String productStoreId = null;
    protected String transactionId = null;
    protected String facilityId = null;
    protected String webSiteId = null;
    protected String terminalId = null;
    protected String orderPartyId = null;

    protected GenericValue userLogin = null;
    protected GenericValue autoUserLogin = null;

    protected Locale locale;  // holds the locale from the user session

    /** don't allow empty constructor */
    protected ShoppingCart() {}

    /** Creates a new cloned ShoppingCart Object. */
    public ShoppingCart(ShoppingCart cart) {
        this.delegator = cart.getDelegator();
        this.delegatorName = delegator.getDelegatorName();
        this.productStoreId = cart.getProductStoreId();
        this.poNumber = cart.getPoNumber();
        this.orderId = cart.getOrderId();
        this.firstAttemptOrderId = cart.getFirstAttemptOrderId();
        this.billingAccountId = cart.getBillingAccountId();
        this.orderAdditionalEmails = cart.getOrderAdditionalEmails();
        this.adjustments = new LinkedList(cart.getAdjustments());
        this.contactMechIdsMap = new HashMap(cart.getOrderContactMechIds());
        this.freeShippingProductPromoActions = new ArrayList(cart.getFreeShippingProductPromoActions());
        this.desiredAlternateGiftByAction = cart.getAllDesiredAlternateGiftByActionCopy();
        this.productPromoUseInfoList = new LinkedList(cart.productPromoUseInfoList);
        this.productPromoCodes = new HashSet(cart.productPromoCodes);
        this.locale = cart.getLocale();
        this.currencyUom = cart.getCurrency();
        this.viewCartOnAdd = cart.viewCartOnAdd();
        
        // clone the additionalPartyRoleMap
        this.additionalPartyRole = new HashMap();
        Iterator it = cart.additionalPartyRole.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            this.additionalPartyRole.put(me.getKey(), new LinkedList((Collection) me.getValue()));
        }

        // clone the items
        List items = cart.items();
        Iterator itIt = items.iterator();
        while (itIt.hasNext()) {
            cartLines.add(new ShoppingCartItem((ShoppingCartItem) itIt.next()));
        }
    }

    /** Creates new empty ShoppingCart object. */
    public ShoppingCart(GenericDelegator delegator, String productStoreId, String webSiteId, Locale locale, String currencyUom) {
        this.delegator = delegator;
        this.delegatorName = delegator.getDelegatorName();
        this.productStoreId = productStoreId;
        this.webSiteId = webSiteId;
        this.currencyUom = currencyUom;
        this.locale = locale;
        if (this.locale == null) {
            this.locale = Locale.getDefault();
        }

        if (productStoreId == null) {
            throw new IllegalArgumentException("productStoreId cannot be null");
        }

        // set the default view cart on add for this store
        GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
        String storeViewCartOnAdd = productStore.getString("viewCartOnAdd");
        if (storeViewCartOnAdd != null && "Y".equalsIgnoreCase(storeViewCartOnAdd)) {
            this.viewCartOnAdd = true;
        }
    }

    /** Creates a new empty ShoppingCart object. */
    public ShoppingCart(GenericDelegator delegator, String productStoreId, Locale locale, String currencyUom) {
        this(delegator, productStoreId, null, locale, currencyUom);
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

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTerminalId() {
        return this.terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
    
    public String getFacilityId() {
        return this.facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Timestamp getCartCreatedTime() {
        return this.cartCreatedTs;
    }

    // =======================================================================
    // Methods for cart items
    // =======================================================================

    /** Add an item to the shopping cart, or if already there, increase the quantity.
     * @return the new/increased item index
     * @throws CartItemModifyException
     */
    public int addOrIncreaseItem(String productId, double selectedAmount, double quantity, Map features, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher) throws CartItemModifyException, ItemNotFoundException {
        // public int addOrIncreaseItem(GenericValue product, double quantity, HashMap features) {

        // Check for existing cart item.
        for (int i = 0; i < this.cartLines.size(); i++) {
            ShoppingCartItem sci = (ShoppingCartItem) cartLines.get(i);

            if (sci.equals(productId, features, attributes, prodCatalogId, configWrapper, selectedAmount)) {
                double newQuantity = sci.getQuantity() + quantity;

                if (Debug.verboseOn()) Debug.logVerbose("Found a match for id " + productId + " on line " + i + ", updating quantity to " + newQuantity, module);
                sci.setQuantity(newQuantity, dispatcher, this);
                return i;
            }
        }
        // Add the new item to the shopping cart if it wasn't found.
        return this.addItem(0, ShoppingCartItem.makeItem(new Integer(0), productId, selectedAmount, quantity, features, attributes, prodCatalogId, configWrapper, dispatcher, this));
    }
    public int addOrIncreaseItem(String productId, double selectedAmount, double quantity, Map features, Map attributes, String prodCatalogId, LocalDispatcher dispatcher) throws CartItemModifyException, ItemNotFoundException {
        return addOrIncreaseItem(productId, 0.00, quantity, features, attributes, prodCatalogId, null, dispatcher);
    }
    public int addOrIncreaseItem(String productId, double quantity, Map features, Map attributes, String prodCatalogId, LocalDispatcher dispatcher) throws CartItemModifyException, ItemNotFoundException {
        return addOrIncreaseItem(productId, 0.00, quantity, features, attributes, prodCatalogId, null, dispatcher);
    }
    public int addOrIncreaseItem(String productId, double quantity, LocalDispatcher dispatcher) throws CartItemModifyException, ItemNotFoundException {
        return addOrIncreaseItem(productId, quantity, null, null, null, dispatcher);
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
    public int addItemToEnd(String productId, double amount, double quantity, HashMap features, HashMap attributes, String prodCatalogId, LocalDispatcher dispatcher) throws CartItemModifyException, ItemNotFoundException {
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
                this.clearItemShipInfo(cartItem);
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

    protected void setUserLogin(GenericValue userLogin) {
        if (this.userLogin == null) {
            this.userLogin = userLogin;
        } else {
            throw new IllegalArgumentException("Cannot change UserLogin object with this method");
        }
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

    protected void setAutoUserLogin(GenericValue autoUserLogin) {
        if (this.autoUserLogin == null) {
            this.autoUserLogin = autoUserLogin;
        } else {
            throw new IllegalArgumentException("Cannot change AutoUserLogin object with this method");
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

    public String getOrderPartyId() {
        return this.orderPartyId;
    }

    public void setOrderPartyId(String orderPartyId) {
        this.orderPartyId = orderPartyId;
    }
    
    public String getPartyId() {
        String partyId = this.orderPartyId;

        if (partyId == null && getUserLogin() != null) {
            partyId = getUserLogin().getString("partyId");
        }
        if (partyId == null && getAutoUserLogin() != null) {
            partyId = getAutoUserLogin().getString("partyId");
        }
        return partyId;
    }

    public void setAutoSaveListId(String id) {
        this.autoSaveListId = id;
    }

    public String getAutoSaveListId() {
        return this.autoSaveListId;
    }

    public void setLastListRestore(Timestamp time) {
        this.lastListRestore = time;
    }

    public Timestamp getLastListRestore() {
        return this.lastListRestore;
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

        this.orderAdditionalEmails = null;
        this.freeShippingProductPromoActions.clear();
        this.desiredAlternateGiftByAction.clear();
        this.productPromoUseInfoList.clear();
        this.productPromoCodes.clear();
        this.shipInfo.clear();
        this.clearPayments();
        
        this.adjustments.clear();
        this.expireSingleUsePayments();
        this.cartLines.clear();
        // clear the additionalPartyRole Map
        Iterator it = this.additionalPartyRole.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            ((LinkedList) me.getValue()).clear();
        }
        this.additionalPartyRole.clear();        
        
        // clear the auto-save info
        if (org.ofbiz.product.store.ProductStoreWorker.autoSaveCart(delegator, productStoreId)) {
            GenericValue ul = this.getUserLogin();
            if (ul == null) {
                ul = this.getAutoUserLogin();
            }

            // load the auto-save list ID
            if (autoSaveListId == null) {
                try {
                    autoSaveListId = org.ofbiz.order.shoppinglist.ShoppingListEvents.getAutoSaveListId(delegator, null, ul);
                } catch (GeneralException e) {
                    Debug.logError(e, module);
                }
            }

            // clear the list
            if (autoSaveListId != null) {
                try {
                    org.ofbiz.order.shoppinglist.ShoppingListEvents.clearListInfo(delegator, autoSaveListId);
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            }
            this.lastListRestore = null;
            this.autoSaveListId = null;
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

    public boolean isPurchaseOrder() {
        return "PURCHASE_ORDER".equals(this.orderType);
    }

    public boolean isSalesOrder() {
        return "SALES_ORDER".equals(this.orderType);
    }

    /** Sets the PO Number in the cart. */
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    /** Returns the po number. */
    public String getPoNumber() {
        return poNumber;
    }

    public void setDefaultItemDeliveryDate(String date) {
        this.defaultItemDeliveryDate = date;
    }

    public String getDefaultItemDeliveryDate() {
        return this.defaultItemDeliveryDate;
    }

    public void setDefaultItemComment(String comment) {
        this.defaultItemComment = comment;
    }

    public String getDefaultItemComment() {
        return this.defaultItemComment;
    }

    // =======================================================================
    // Payment Method
    // =======================================================================

    public String getPaymentMethodTypeId(String paymentMethodId) {
        try {
            GenericValue pm = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));
            if (pm != null) {
                return pm.getString("paymentMethodTypeId");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return null;
    }

    /** Creates a CartPaymentInfo object */
    public CartPaymentInfo makePaymentInfo(String id, String refNum, Double amount) {
        CartPaymentInfo inf = new CartPaymentInfo();
        inf.refNum[0] = refNum;
        inf.amount = amount;

        if (!isPaymentMethodType(id)) {
            inf.paymentMethodTypeId = this.getPaymentMethodTypeId(id);
            inf.paymentMethodId = id;
        } else {
            inf.paymentMethodTypeId = id;
        }
        return inf;
    }

    /** Locates the index of an existing CartPaymentInfo object or -1 if none found */
    public int getPaymentInfoIndex(String id, String refNum) {
        CartPaymentInfo thisInf = this.makePaymentInfo(id, refNum, null);
        for (int i = 0; i < paymentInfo.size(); i++) {
            CartPaymentInfo inf = (CartPaymentInfo) paymentInfo.get(i);
            if (inf.compareTo(thisInf) == 0) {
                return i;
            }
        }
        return -1;
    }

    /** Returns the CartPaymentInfo objects which have matching fields */
    public List getPaymentInfos(boolean isPaymentMethod, boolean isPaymentMethodType, boolean hasRefNum) {
        List foundRecords = new LinkedList();
        Iterator i = paymentInfo.iterator();
        while (i.hasNext()) {
            CartPaymentInfo inf = (CartPaymentInfo) i.next();
            if (isPaymentMethod && inf.paymentMethodId != null) {
                if (hasRefNum && inf.refNum != null) {
                    foundRecords.add(inf);
                } else if (!hasRefNum && inf.refNum == null) {
                    foundRecords.add(inf);
                }
            } else if (isPaymentMethodType && inf.paymentMethodTypeId != null) {
                if (hasRefNum && inf.refNum != null) {
                    foundRecords.add(inf);
                } else if (!hasRefNum && inf.refNum == null) {
                    foundRecords.add(inf);
                }
            }
        }
        return foundRecords;
    }

    /** Locates an existing CartPaymentInfo object by index */
    public CartPaymentInfo getPaymentInfo(int index) {
        return (CartPaymentInfo) paymentInfo.get(index);
    }

    /** Locates an existing (or creates a new) CartPaymentInfo object */
    public CartPaymentInfo getPaymentInfo(String id, String refNum, String authCode, Double amount) {
        CartPaymentInfo thisInf = this.makePaymentInfo(id, refNum, amount);
        Iterator i = paymentInfo.iterator();
        while (i.hasNext()) {
            CartPaymentInfo inf = (CartPaymentInfo) i.next();
            if (inf.compareTo(thisInf) == 0) {
                // update the info
                inf.refNum[0] = refNum;
                inf.refNum[1] = authCode;
                inf.amount = amount;
                return inf;
            }
        }

        return thisInf;
    }

    /** Locates an existing (or creates a new) CartPaymentInfo object */
    public CartPaymentInfo getPaymentInfo(String id) {
        return this.getPaymentInfo(id, null, null, null);
    }

    /** adds a payment method/payment method type */
    public void addPaymentAmount(String id, Double amount, String refNum, String authCode, boolean isSingleUse, boolean replace) {
        CartPaymentInfo inf = this.getPaymentInfo(id, refNum, authCode, amount);
        inf.singleUse = isSingleUse;
        if (replace) {
            paymentInfo.remove(inf);
        }
        paymentInfo.add(inf);
    }

    /** adds a payment method/payment method type */
    public void addPaymentAmount(String id, Double amount, boolean isSingleUse) {
        this.addPaymentAmount(id, amount, null, null, isSingleUse, true);
    }

    /** adds a payment method/payment method type */
    public void addPaymentAmount(String id, double amount, boolean isSingleUse) {
        this.addPaymentAmount(id, new Double(amount), isSingleUse);
    }

    /** adds a payment method/payment method type */
    public void addPaymentAmount(String id, Double amount) {
        this.addPaymentAmount(id, amount, false);
    }

    /** adds a payment method/payment method type */
    public void addPaymentAmount(String id, double amount) {
        this.addPaymentAmount(id, new Double(amount), false);
    }

    /** adds a payment method/payment method type */
    public void addPayment(String id) {
        this.addPaymentAmount(id, null, false);
    }

    /** returns the payment method/payment method type amount */
    public Double getPaymentAmount(String id) {
        return this.getPaymentInfo(id).amount;
    }

    public void addPaymentRef(String id, String ref, String authCode) {
        this.getPaymentInfo(id).refNum[0] = ref;
        this.getPaymentInfo(id).refNum[1] = authCode;
    }

    public String getPaymentRef(String id) {
        return this.getPaymentInfo(id).refNum[0];
    }

    /** returns the total payment amounts */
    public double getPaymentTotal() {
        double total = 0.00;
        Iterator i = paymentInfo.iterator();
        while (i.hasNext()) {
            CartPaymentInfo inf = (CartPaymentInfo) i.next();
            if (inf.amount != null) {
                total += inf.amount.doubleValue();
            }
        }
        return total;
    }

    public int selectedPayments() {
        return paymentInfo.size();
    }

    public boolean isPaymentSelected(String id) {
        CartPaymentInfo inf = this.getPaymentInfo(id);
        return paymentInfo.contains(inf);
    }

    /** removes a specific payment method/payment method type */
    public void clearPayment(String id) {
        CartPaymentInfo inf = this.getPaymentInfo(id);
        paymentInfo.remove(inf);
    }

    /** removes a specific payment info from the list */
    public void clearPayment(int index) {
        paymentInfo.remove(index);
    }

    /** clears all payment method/payment method types */
    public void clearPayments() {
        this.expireSingleUsePayments();
        paymentInfo.clear();
    }

    private void expireSingleUsePayments() {
        Timestamp now = UtilDateTime.nowTimestamp();
        Iterator i = paymentInfo.iterator();
        while (i.hasNext()) {
            CartPaymentInfo inf = (CartPaymentInfo) i.next();
            if (inf.paymentMethodId == null || !inf.singleUse) {
                continue;
            }

            GenericValue paymentMethod = null;
            try {
                paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", inf.paymentMethodId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Unable to get payment method record to expire : " + inf.paymentMethodId, module);
            }
            if (paymentMethod != null) {
                paymentMethod.set("thruDate", now);
                try {
                    paymentMethod.store();
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to store single use PaymentMethod record : " + paymentMethod, module);
                }
            } else {
                Debug.logError("ERROR: Received back a null payment method record for expired ID : " + inf.paymentMethodId, module);
            }
        }
    }

    /** Returns the Payment Method Ids */
    public List getPaymentMethodIds() {
        List pmi = new LinkedList();
        Iterator i = paymentInfo.iterator();
        while (i.hasNext()) {
            CartPaymentInfo inf = (CartPaymentInfo) i.next();
            if (inf.paymentMethodId != null) {
                pmi.add(inf.paymentMethodId);
            }
        }
        return pmi;
    }

    /** Returns the Payment Method Ids */
    public List getPaymentMethodTypeIds() {
       List pmt = new LinkedList();
        Iterator i = paymentInfo.iterator();
        while (i.hasNext()) {
            CartPaymentInfo inf = (CartPaymentInfo) i.next();
            if (inf.paymentMethodTypeId != null) {
                pmt.add(inf.paymentMethodTypeId);
            }
        }
        return pmt;
    }

    /** Returns a list of PaymentMethod value objects selected in the cart */
    public List getPaymentMethods() {
        List methods = new LinkedList();
        if (paymentInfo != null && paymentInfo.size() > 0) {
            Iterator i = getPaymentMethodIds().iterator();
            while (i.hasNext()) {
                String id = (String) i.next();
                try {
                    methods.add(this.getDelegator().findByPrimaryKeyCache("PaymentMethod", UtilMisc.toMap("paymentMethodId", id)));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to get payment method from the database", module);
                }
            }
        }

        return methods;
    }

    /** Returns a list of PaymentMethodType value objects selected in the cart */
    public List getPaymentMethodTypes() {
        List types = new LinkedList();
        if (paymentInfo != null && paymentInfo.size() > 0) {
            Iterator i = getPaymentMethodTypeIds().iterator();
            while (i.hasNext()) {
                String id = (String) i.next();
                try {
                    types.add(this.getDelegator().findByPrimaryKeyCache("PaymentMethodType", UtilMisc.toMap("paymentMethodTypeId", id)));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to get payment method type from the database", module);
                }
            }
        }

        return types;
    }

    public List getCreditCards() {
        List paymentMethods = this.getPaymentMethods();
        List creditCards = new LinkedList();
        if (paymentMethods != null) {
            Iterator i = paymentMethods.iterator();
            while (i.hasNext()) {
                GenericValue pm = (GenericValue) i.next();
                if ("CREDIT_CARD".equals(pm.getString("paymentMethodTypeId"))) {
                    try {
                        GenericValue cc = pm.getRelatedOne("CreditCard");
                        creditCards.add(cc);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Unable to get credit card record from payment method : " + pm, module);
                    }
                }
            }
        }

        return creditCards;
    }

    public List getGiftCards() {
        List paymentMethods = this.getPaymentMethods();
        List giftCards = new LinkedList();
        if (paymentMethods != null) {
            Iterator i = paymentMethods.iterator();
            while (i.hasNext()) {
                GenericValue pm = (GenericValue) i.next();
                if ("GIFT_CARD".equals(pm.getString("paymentMethodTypeId"))) {
                    try {
                        GenericValue gc = pm.getRelatedOne("GiftCard");
                        giftCards.add(gc);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Unable to get gift card record from payment method : " + pm, module);
                    }
                }
            }
        }

        return giftCards;
    }

    /* determines if the id supplied is a payment method or not by searching in the entity engine */
    public boolean isPaymentMethodType(String id){
    	GenericValue paymentMethodType = null;
    	try{
    		paymentMethodType = delegator.findByPrimaryKeyCache("PaymentMethodType",UtilMisc.toMap("paymentMethodTypeId", id));
    	}
    	catch(GenericEntityException e) {
    		Debug.logInfo(e,"Problems getting PaymentMethodType", module);
    	}
   	 	if (paymentMethodType == null){
   	 		return false;
   	 	}
   	 	else{
   	 		return true;
   	 	}
    }

    // =======================================================================
    // Billing Accounts
    // =======================================================================

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

    // =======================================================================
    // Shipping Charges
    // =======================================================================

    /** Returns the order level shipping amount */
    public double getOrderShipping() {
        return OrderReadHelper.calcOrderAdjustments(this.getAdjustments(), this.getSubTotal(), false, false, true);
    }

    // ----------------------------------------
    // Ship Group Methods
    // ----------------------------------------

    public List getShipGroups() {
        return this.shipInfo;
    }

    public Map getShipGroups(ShoppingCartItem item) {
        Map shipGroups = new LinkedMap();
        if (item != null) {
            for (int i = 0; i < shipInfo.size(); i++) {
                CartShipInfo csi = (CartShipInfo) shipInfo.get(i);
                CartShipInfo.CartShipItemInfo csii = (CartShipInfo.CartShipItemInfo) csi.shipItemInfo.get(item);
                if (csii != null) {
                    if (this.checkShipItemInfo(csi, csii)) {
                        shipGroups.put(new Integer(i), new Double(csii.quantity));
                    }
                }
            }
        }
        return shipGroups;
    }

    public Map getShipGroups(int itemIndex) {
        return this.getShipGroups(this.findCartItem(itemIndex));
    }

    public CartShipInfo getShipInfo(int idx) {
        if (idx == -1 ) {
            return null;
        }

        if (shipInfo.size() == 0) {
            shipInfo.add(new CartShipInfo());
        }

        return (CartShipInfo) shipInfo.get(idx);
    }

    public int getShipGroupSize() {
        return this.shipInfo.size();
    }

    /** Returns the ShoppingCartItem (key) and quantity (value) associated with the ship group */
    public Map getShipGroupItems(int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        Map qtyMap = new HashMap();
        Iterator i = csi.shipItemInfo.keySet().iterator();
        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            CartShipInfo.CartShipItemInfo csii = (CartShipInfo.CartShipItemInfo) csi.shipItemInfo.get(item);
            qtyMap.put(item, new Double(csii.quantity));
        }
        return qtyMap;
    }

    public void clearItemShipInfo(ShoppingCartItem item) {
        for (int i = 0; i < shipInfo.size(); i++) {
            CartShipInfo csi = this.getShipInfo(i);
            csi.shipItemInfo.remove(item);
        }
        this.cleanUpShipGroups();
    }

    public void setItemShipGroupEstimate(double amount, int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        csi.shipEstimate = amount;
    }

    public double getItemShipGroupEstimate(int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        return csi.shipEstimate;
    }

    public void setItemShipGroupQty(int itemIndex, double quantity, int idx) {
        this.setItemShipGroupQty(this.findCartItem(itemIndex), itemIndex, quantity, idx);
    }

    public void setItemShipGroupQty(ShoppingCartItem item, double quantity, int idx) {
        this.setItemShipGroupQty(item, this.getItemIndex(item), quantity, idx);
    }

    public void setItemShipGroupQty(ShoppingCartItem item, int itemIndex, double quantity, int idx) {
        if (itemIndex > -1) {
            CartShipInfo csi = this.getShipInfo(idx);

            // never set less than zero
            if (quantity < 0) {
                quantity = 0;
            }

            // never set more than quantity ordered
            if (quantity > item.getQuantity()) {
                quantity = item.getQuantity();
            }
            CartShipInfo.CartShipItemInfo csii = csi.setItemInfo(item, quantity);
            this.checkShipItemInfo(csi, csii);
        }
    }

    public double getItemShipGroupQty(ShoppingCartItem item, int idx) {
        if (item != null) {
            CartShipInfo csi = this.getShipInfo(idx);
            CartShipInfo.CartShipItemInfo csii = (CartShipInfo.CartShipItemInfo) csi.shipItemInfo.get(item);
            if (csii != null) {
                return csii.quantity;
            }
        }
        return 0;
    }

    public double getItemShipGroupQty(int itemIndex, int idx) {
        return this.getItemShipGroupQty(this.findCartItem(itemIndex), idx);
    }

    public void positionItemToGroup(int itemIndex, double quantity, int fromIndex, int toIndex) {
        this.positionItemToGroup(this.findCartItem(itemIndex), quantity, fromIndex, toIndex);
    }

    public void positionItemToGroup(ShoppingCartItem item, double quantity, int fromIndex, int toIndex) {
        if (fromIndex == toIndex || quantity <= 0) {
            // do nothing
            return;
        }

        // get the ship groups; create the TO group if needed
        CartShipInfo fromGroup = this.getShipInfo(fromIndex);
        CartShipInfo toGroup = null;
        if (toIndex == -1) {
            toGroup = new CartShipInfo();
            shipInfo.add(toGroup);
            toIndex = shipInfo.size() - 1;
        } else {
            toGroup = this.getShipInfo(toIndex);
        }

        // adjust the quantities
        if (fromGroup != null && toGroup != null) {
            double fromQty = this.getItemShipGroupQty(item, fromIndex);
            double toQty = this.getItemShipGroupQty(item, toIndex);
            if (fromQty > 0) {
                if (quantity > fromQty) {
                    quantity = fromQty;
                }
                fromQty -= quantity;
                toQty += quantity;
                this.setItemShipGroupQty(item, fromQty, fromIndex);
                this.setItemShipGroupQty(item, toQty, toIndex);
            }

            // remove any empty ship groups
            this.cleanUpShipGroups();
        }
    }

    // removes 0 quantity items
    protected boolean checkShipItemInfo(CartShipInfo csi, CartShipInfo.CartShipItemInfo csii) {
        if (csii.quantity == 0 || csii.item.getQuantity() == 0) {
            csi.shipItemInfo.remove(csii.item);
            return false;
        }
        return true;
    }

    protected void cleanUpShipGroups() {
        for (int i = 0; i < shipInfo.size(); i++) {
            CartShipInfo csi = this.getShipInfo(i);
            Iterator si = csi.shipItemInfo.keySet().iterator();
            while (si.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) si.next();
                if (item.getQuantity() == 0.0) {
                    si.remove();
                }
            }
            if (csi.shipItemInfo.size() == 0) {
                shipInfo.remove(csi);
            }
        }
    }

    /** Sets the shipping contact mech id. */
    public void setShippingContactMechId(int idx, String shippingContactMechId) {
        CartShipInfo csi = this.getShipInfo(idx);
        csi.contactMechId = shippingContactMechId;
    }

    public void setShippingContactMechId(String shippingContactMechId) {
        this.setShippingContactMechId(0, shippingContactMechId);
    }

    /** Returns the shipping contact mech id. */
    public String getShippingContactMechId(int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        return csi.contactMechId;
    }

    public String getShippingContactMechId() {
        return this.getShippingContactMechId(0);
    }

    /** Sets the shipment method type. */
    public void setShipmentMethodTypeId(int idx, String shipmentMethodTypeId) {
        CartShipInfo csi = this.getShipInfo(idx);
        csi.shipmentMethodTypeId = shipmentMethodTypeId;
    }

    public void setShipmentMethodTypeId(String shipmentMethodTypeId) {
        this.setShipmentMethodTypeId(0, shipmentMethodTypeId);
    }

    /** Returns the shipment method type ID */
    public String getShipmentMethodTypeId(int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        return csi.shipmentMethodTypeId;
    }

    public String getShipmentMethodTypeId() {
        return this.getShipmentMethodTypeId(0);
    }

    /** Returns the shipment method type. */
    public GenericValue getShipmentMethodType(int idx) {
        String shipmentMethodTypeId = this.getShipmentMethodTypeId(idx);
        if (UtilValidate.isNotEmpty(shipmentMethodTypeId)) {
            try {
                return delegator.findByPrimaryKey("ShipmentMethodType",
                        UtilMisc.toMap("shipmentMethodTypeId", shipmentMethodTypeId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        return null;
    }

    /** Sets the shipping instructions. */
    public void setShippingInstructions(int idx, String shippingInstructions) {
        CartShipInfo csi = this.getShipInfo(idx);
        csi.shippingInstructions = shippingInstructions;
    }

    public void setShippingInstructions(String shippingInstructions) {
        this.setShippingInstructions(0, shippingInstructions);
    }

    /** Returns the shipping instructions. */
    public String getShippingInstructions(int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        return csi.shippingInstructions;
    }

    public String getShippingInstructions() {
        return this.getShippingInstructions(0);
    }

    public void setMaySplit(int idx, Boolean maySplit) {
        CartShipInfo csi = this.getShipInfo(idx);
        csi.maySplit = maySplit.booleanValue() ? "Y" : "N";
    }

    public void setMaySplit(Boolean maySplit) {
        this.setMaySplit(0, maySplit);
    }

    /** Returns Boolean.TRUE if the order may be split (null if unspecified) */
    public String getMaySplit(int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        return csi.maySplit;
    }

    public String getMaySplit() {
        return this.getMaySplit(0);
    }

    public void setGiftMessage(int idx, String giftMessage) {
        CartShipInfo csi = this.getShipInfo(idx);
        csi.giftMessage = giftMessage;
    }

    public void setGiftMessage(String giftMessage) {
        this.setGiftMessage(0, giftMessage);
    }

    public String getGiftMessage(int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        return csi.giftMessage;
    }

    public String getGiftMessage() {
        return this.getGiftMessage(0);
    }

    public void setIsGift(int idx, Boolean isGift) {
        CartShipInfo csi = this.getShipInfo(idx);
        csi.isGift = isGift.booleanValue() ? "Y" : "N";
    }

    public void setIsGift(Boolean isGift) {
        this.setIsGift(0, isGift);
    }

    public String getIsGift(int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        return csi.isGift;
    }

    public String getIsGift() {
        return this.getIsGift(0);
    }

    public void setCarrierPartyId(int idx, String carrierPartyId) {
        CartShipInfo csi = this.getShipInfo(idx);
        csi.carrierPartyId = carrierPartyId;
    }

    public void setCarrierPartyId(String carrierPartyId) {
        this.setCarrierPartyId(0, carrierPartyId);
    }

    public String getCarrierPartyId(int idx) {
        CartShipInfo csi = this.getShipInfo(idx);
        return csi.carrierPartyId;
    }

    public String getCarrierPartyId() {
        return this.getCarrierPartyId(0);
    }

    public void setOrderAdditionalEmails(String orderAdditionalEmails) {
        this.orderAdditionalEmails = orderAdditionalEmails;
    }

    public String getOrderAdditionalEmails() {
        return orderAdditionalEmails;
    }

    public GenericValue getShippingAddress(int idx) {
        if (this.getShippingContactMechId(idx) != null) {
            try {
                return getDelegator().findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", this.getShippingContactMechId(idx)));
            } catch (GenericEntityException e) {
                Debug.logWarning(e.toString(), module);
                return null;
            }
        } else {
            return null;
        }
    }

    public GenericValue getShippingAddress() {
        return this.getShippingAddress(0);
    }

    // Returns the tax amount for a ship group. */
    public double getTotalSalesTax(int shipGroup) {
        CartShipInfo csi = this.getShipInfo(shipGroup);
        return csi.getTotalTax(this);
    }

    /** Returns the tax amount from the cart object. */
    public double getTotalSalesTax() {
        double totalTax = 0.00;
        for (int i = 0; i < shipInfo.size(); i++) {
            CartShipInfo csi = this.getShipInfo(i);
            totalTax += csi.getTotalTax(this);
        }
        return totalTax;
    }

    /** Returns the shipping amount from the cart object. */
    public double getTotalShipping() {
        double tempShipping = 0.0;

        Iterator shipIter = shipInfo.iterator();
        while (shipIter.hasNext()) {
            CartShipInfo csi = (CartShipInfo) shipIter.next();
            tempShipping += csi.shipEstimate;

        }
                
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
        double itemsTotal = 0.00;
        Iterator i = iterator();

        while (i.hasNext()) {
            itemsTotal += ((ShoppingCartItem) i.next()).getItemSubTotal();
        }
        return itemsTotal;
    }

    /** Returns the total from the cart, including tax/shipping. */
    public double getGrandTotal() {
        // sales tax and shipping are not stored as adjustments but rather as part of the ship group
        return this.getSubTotal() + this.getTotalShipping() + this.getTotalSalesTax() + this.getOrderOtherAdjustmentTotal();
    }

    public double getOrderOtherAdjustmentTotal() {
        return OrderReadHelper.calcOrderAdjustments(this.getAdjustments(), getSubTotal(), true, false, false);
    }

    /** Returns the sub-total in the cart (item-total - discount). */
    public double getSubTotalForPromotions() {
        double itemsTotal = 0.00;
        Iterator i = iterator();

        while (i.hasNext()) {
            ShoppingCartItem cartItem = (ShoppingCartItem) i.next();
            GenericValue product = cartItem.getProduct();
            if (product != null && "N".equals(product.getString("includeInPromotions"))) {
                // don't include in total if this is the case...
                continue;
            }
            itemsTotal += cartItem.getItemSubTotal();
        }
        return itemsTotal;
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
    public int addAdjustment(GenericValue adjustment) {
        adjustments.add(adjustment);
        return adjustments.indexOf(adjustment);
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

    /** Returns the total quantity in the cart. */
    public double getTotalQuantity() {
        double count = 0.0;
        Iterator i = iterator();

        while (i.hasNext()) {
            count += ((ShoppingCartItem) i.next()).getQuantity();
        }
        return count;
    }

    /** Returns the SHIPPABLE item-total in the cart for a specific ship group. */
    public double getShippableTotal(int idx) {
        CartShipInfo info = this.getShipInfo(idx);
        double itemTotal = 0.0;

        Iterator i = info.shipItemInfo.keySet().iterator();
        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            CartShipInfo.CartShipItemInfo csii = (CartShipInfo.CartShipItemInfo) info.shipItemInfo.get(item);
            if (csii != null && csii.quantity > 0) {
                if (item.shippingApplies()) {
                    itemTotal += item.getItemSubTotal(csii.quantity);
                }
            }
        }

        return itemTotal;
    }

    /** Returns the total SHIPPABLE quantity in the cart for a specific ship group. */
    public double getShippableQuantity(int idx) {
        CartShipInfo info = this.getShipInfo(idx);
        double count = 0.0;

        Iterator i = info.shipItemInfo.keySet().iterator();
        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            CartShipInfo.CartShipItemInfo csii = (CartShipInfo.CartShipItemInfo) info.shipItemInfo.get(item);
            if (csii != null && csii.quantity > 0) {
                if (item.shippingApplies()) {
                    count += csii.quantity;
                }
            }
        }

        return count;
    }

    /** Returns the total SHIPPABLE weight in the cart for a specific ship group. */
    public double getShippableWeight(int idx) {
        CartShipInfo info = this.getShipInfo(idx);
        double weight = 0.0;

        Iterator i = info.shipItemInfo.keySet().iterator();
        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            CartShipInfo.CartShipItemInfo csii = (CartShipInfo.CartShipItemInfo) info.shipItemInfo.get(item);
            if (csii != null && csii.quantity > 0) {
                if (item.shippingApplies()) {
                    weight += (item.getWeight() * csii.quantity);
                }
            }
        }

        return weight;
    }

    /** Returns a List of shippable item's size for a specific ship group. */
    public List getShippableSizes(int idx) {
        CartShipInfo info = this.getShipInfo(idx);
        List shippableSizes = new LinkedList();

        Iterator i = info.shipItemInfo.keySet().iterator();
        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            CartShipInfo.CartShipItemInfo csii = (CartShipInfo.CartShipItemInfo) info.shipItemInfo.get(item);
            if (csii != null && csii.quantity > 0) {
                if (item.shippingApplies()) {
                    shippableSizes.add(new Double(item.getSize()));
                }
            }
        }

        return shippableSizes;
    }

    /** Returns a List of shippable item info (quantity, size, weight) for a specific ship group */
    public List getShippableItemInfo(int idx) {
        CartShipInfo info = this.getShipInfo(idx);
        List itemInfos = new LinkedList();

        Iterator i = info.shipItemInfo.keySet().iterator();
        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            CartShipInfo.CartShipItemInfo csii = (CartShipInfo.CartShipItemInfo) info.shipItemInfo.get(item);
            if (csii != null && csii.quantity > 0) {
                if (item.shippingApplies()) {
                    Map itemInfo = item.getItemProductInfo();
                    itemInfo.put("quantity", new Double(csii.quantity));
                    itemInfos.add(itemInfo);
                }
            }
        }

        return itemInfos;
    }

    /** Returns a Map of all features applied to products in the cart with quantities for a specific ship group. */
    public Map getFeatureIdQtyMap(int idx) {
        CartShipInfo info = this.getShipInfo(idx);
        Map featureMap = new HashMap();

        Iterator i = info.shipItemInfo.keySet().iterator();
        while (i.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            CartShipInfo.CartShipItemInfo csii = (CartShipInfo.CartShipItemInfo) info.shipItemInfo.get(item);
            if (csii != null && csii.quantity > 0) {
                featureMap.putAll(item.getFeatureIdQtyMap(csii.quantity));
            }
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
            // uh oh, not good, should always be passed in on init, we can't really do anything without it, so throw an exception
            throw new IllegalStateException("The Currency UOM is not set in the shopping cart, this is not a valid state, it should always be passed in when the cart is created.");
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
        if (!"PROMO_FREE_SHIPPING".equals(productPromoAction.getString("productPromoActionEnumId"))) return; // Changed 1-5-04 by Si Chen

        // to easily make sure that no duplicate exists, do a remove first
        this.removeFreeShippingProductPromoAction(productPromoAction.getPrimaryKey());
        this.freeShippingProductPromoActions.add(productPromoAction);
    }
    public List getFreeShippingProductPromoActions() {
        return this.freeShippingProductPromoActions;
    }
    
    public void removeAllDesiredAlternateGiftByActions() {
        this.desiredAlternateGiftByAction.clear();
    }
    public void setDesiredAlternateGiftByAction(GenericPK productPromoActionPK, String productId) {
        this.desiredAlternateGiftByAction.put(productPromoActionPK, productId);
    }
    public String getDesiredAlternateGiftByAction(GenericPK productPromoActionPK) {
        return (String) this.desiredAlternateGiftByAction.get(productPromoActionPK);
    }
    public Map getAllDesiredAlternateGiftByActionCopy() {
        return new HashMap(this.desiredAlternateGiftByAction);
    }

    public void addProductPromoUse(String productPromoId, String productPromoCodeId, double totalDiscountAmount, double quantityLeftInActions) {
        if (UtilValidate.isNotEmpty(productPromoCodeId) && !this.productPromoCodes.contains(productPromoCodeId)) {
            throw new IllegalStateException("Cannot add a use to a promo code use for a code that has not been entered.");
        }
        if (Debug.verboseOn()) Debug.logVerbose("Used promotion [" + productPromoId + "] with code [" + productPromoCodeId + "] for total discount [" + totalDiscountAmount + "] and quantity left in actions [" + quantityLeftInActions + "]", module);
        this.productPromoUseInfoList.add(new ProductPromoUseInfo(productPromoId, productPromoCodeId, totalDiscountAmount, quantityLeftInActions));
    }

    public void clearProductPromoUseInfo() {
        // clear out info for general promo use
        this.productPromoUseInfoList.clear();
    }
    
    public void clearCartItemUseInPromoInfo() {
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

    public double getProductPromoUseTotalDiscount(String productPromoId) {
        if (productPromoId == null) return 0;
        double totalDiscount = 0;
        Iterator productPromoUseInfoIter = this.productPromoUseInfoList.iterator();
        while (productPromoUseInfoIter.hasNext()) {
            ProductPromoUseInfo productPromoUseInfo = (ProductPromoUseInfo) productPromoUseInfoIter.next();
            if (productPromoId.equals(productPromoUseInfo.productPromoId)) {
                totalDiscount += productPromoUseInfo.getTotalDiscountAmount();
            }
        }
        return totalDiscount;
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

    public void clearAllPromotionInformation() {
        this.clearAllPromotionAdjustments();
        
        // remove all free shipping promo actions
        this.removeAllFreeShippingProductPromoActions();

        // clear promo uses & reset promo code uses, and reset info about cart items used for promos (ie qualifiers and benefiters)
        this.clearProductPromoUseInfo();
        this.clearCartItemUseInPromoInfo();
    }
    
    public void clearAllPromotionAdjustments() {
        // remove cart adjustments from promo actions
        List cartAdjustments = this.getAdjustments();
        if (cartAdjustments != null) {
            Iterator cartAdjustmentIter = cartAdjustments.iterator();
            while (cartAdjustmentIter.hasNext()) {
                GenericValue checkOrderAdjustment = (GenericValue) cartAdjustmentIter.next();
                if (UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoId")) &&
                        UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoRuleId")) &&
                        UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoActionSeqId"))) {
                    cartAdjustmentIter.remove();
                }
            }
        }

        // remove cart lines that are promos (ie GWPs) and cart line adjustments from promo actions
        Iterator cartItemIter = this.iterator();
        while (cartItemIter.hasNext()) {
            ShoppingCartItem checkItem = (ShoppingCartItem) cartItemIter.next();
            if (checkItem.getIsPromo()) {
                this.clearItemShipInfo(checkItem);
                cartItemIter.remove();
            } else {
                // found a promo item with the productId, see if it has a matching adjustment on it
                Iterator checkOrderAdjustments = UtilMisc.toIterator(checkItem.getAdjustments());
                while (checkOrderAdjustments != null && checkOrderAdjustments.hasNext()) {
                    GenericValue checkOrderAdjustment = (GenericValue) checkOrderAdjustments.next();
                    if (UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoId")) &&
                            UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoRuleId")) &&
                            UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoActionSeqId"))) {
                        checkOrderAdjustments.remove();
                    }
                }
            }
        }
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

    /**
     * Associates a party with a role to the order.
     * @param partyId identifier of the party to associate to order
     * @param roleTypeId identifier of the role used in party-order association
     */
    public void addAdditionalPartyRole(String partyId, String roleTypeId) {
        // search if there is an existing entry
        List parties = (List) additionalPartyRole.get(roleTypeId);
        if (parties != null) {
            Iterator it = parties.iterator();
            while (it.hasNext()) {
                if (((String) it.next()).equals(partyId)) {
                    return;
                }
            }
        } else {
            parties = new LinkedList();
            additionalPartyRole.put(roleTypeId, parties);
        }

        parties.add(0, partyId);
    }
    
    /**
     * Removes a previously associated party to the order.
     * @param partyId identifier of the party to associate to order
     * @param roleTypeId identifier of the role used in party-order association
     */    
    public void removeAdditionalPartyRole(String partyId, String roleTypeId) {
        List parties = (List) additionalPartyRole.get(roleTypeId);

        if (parties != null) {
            Iterator it = parties.iterator();
            while (it.hasNext()) {
                if (((String) it.next()).equals(partyId)) {
                    it.remove();

                    if (parties.isEmpty()) {
                        additionalPartyRole.remove(roleTypeId);
                    }
                    return;
                }
            }
        }
    }
    
    public Map getAdditionalPartyRoleMap() {
        return additionalPartyRole;
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
            Iterator itemIter = cartLines.iterator();
            long seqId = 1;

            while (itemIter.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) itemIter.next();

                String orderItemSeqId = UtilFormatOut.formatPaddedNumber(seqId, 5);
                item.setOrderItemSeqId(orderItemSeqId);
                seqId++;

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
                orderItem.set("estimatedDeliveryDate", item.getDesiredDeliveryDate());
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

                    // TODO - we need to change the way free shipping promotions work
                    /*
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
                    */
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

                            // TODO - fix this!!
                            /*
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
                            */
                        }
                    }
                }
            }
        }

        return allAdjs;
    }

    /** make a list of all OrderPaymentPreferences and Billing info including all payment methods and types */
    public List makeAllOrderPaymentInfos() {
        List allOpPrefs = new LinkedList();
        Iterator i = paymentInfo.iterator();
        while (i.hasNext()) {
            CartPaymentInfo inf = (CartPaymentInfo) i.next();
            allOpPrefs.addAll(inf.makeOrderPaymentInfos(this.getDelegator()));

        }
        
        return allOpPrefs;
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
            productPromoUse.set("totalDiscountAmount", new Double(productPromoUseInfo.getTotalDiscountAmount()));
            productPromoUse.set("quantityLeftInActions", new Double(productPromoUseInfo.getQuantityLeftInActions()));
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

    public List makeAllShipGroupInfos() {
        List groups = new LinkedList();
        Iterator grpIterator = shipInfo.iterator();
        long seqId = 1;
        while (grpIterator.hasNext()) {
            CartShipInfo csi = (CartShipInfo) grpIterator.next();
            groups.addAll(csi.makeItemShipGroupAndAssoc(delegator, this, seqId));
            seqId++;
        }
        return groups;
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
        result.put("orderPaymentInfo", this.makeAllOrderPaymentInfos());
        result.put("orderItemShipGroupInfo", this.makeAllShipGroupInfos());
        result.put("orderItemSurveyResponses", this.makeAllOrderItemSurveyResponses());
        result.put("orderAdditionalPartyRoleMap", this.getAdditionalPartyRoleMap());

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

    static class BasePriceOrderComparator implements Comparator, Serializable {

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
