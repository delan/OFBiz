/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project and repected authors.
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

package org.ofbiz.commonapp.order.shoppingcart;


import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.order.order.*;


/**
 * <p><b>Title:</b> ShoppingCartItem.java
 * <p><b>Description:</b> Shopping cart item object.
 *
 * @author     <a href="mailto:jaz@jflow.net.com">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    1.1
 * @created    August 4, 2001
 */
public class ShoppingCartItem implements java.io.Serializable {

    private transient GenericDelegator delegator = null;
    private transient GenericValue _product = null;
    /** this is a virtual product that the current product may inherit information from */
    private transient GenericValue _parentProduct = null;

    private String delegatorName = null;
    private String prodCatalogId = null;
    private String productId = null;
    private String itemComment = null;
    private double quantity = 0.0;
    private double basePrice = 0.0;
    private double listPrice = 0.0;
    private Map additionalProductFeatureAndAppls = null;
    private Map attributes = null;
    private String orderItemSeqId = null;
    private GenericValue orderShipmentPreference = null;

    private Map contactMechIdsMap = new HashMap();
    private List orderItemPriceInfos = null;
    private List itemAdjustments = new LinkedList();
    private boolean isPromo = false;

    /** makes a ShoppingCartItem and adds it to the cart at cartLocation, or at the end if cartLocation is null */
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double quantity, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException {
        return makeItem(cartLocation, product, quantity, null, null, prodCatalogId, dispatcher, cart);
    }

    /** makes a ShoppingCartItem and adds it to the cart at cartLocation, or at the end if cartLocation is null */
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericDelegator delegator, String productId, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException {
        GenericValue product = null;

        try {
            product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            product = null;
        }

        if (product == null) {
            String excMsg = "Product not found, not adding to cart. [productId: " + productId + "]";

            Debug.logWarning(excMsg);
            throw new CartItemModifyException(excMsg);
        }

        return makeItem(cartLocation, product, quantity, additionalProductFeatureAndAppls, attributes, prodCatalogId, dispatcher, cart);
    }

    /** makes a ShoppingCartItem and adds it to the cart at cartLocation, or at the end if cartLocation is null */
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException {
        return makeItem(cartLocation, product, quantity, additionalProductFeatureAndAppls, attributes, prodCatalogId, dispatcher, cart, true);
    }

    /** makes a ShoppingCartItem and adds it to the cart at cartLocation, or at the end if cartLocation is null */
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        ShoppingCartItem newItem = new ShoppingCartItem(product, additionalProductFeatureAndAppls, attributes, prodCatalogId);

        // check to see if product is virtual
        if ("Y".equals(product.getString("isVirtual"))) {
            String excMsg = "Tried to add the Virtual Product " + product.getString("productName") +
                " (productId: " + product.getString("productId") + ") to the cart, not adding.";

            Debug.logWarning(excMsg);
            throw new CartItemModifyException(excMsg);
        }

        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        // check to see if introductionDate hasn't passed yet
        if (product.get("introductionDate") != null && nowTimestamp.before(product.getTimestamp("introductionDate"))) {
            String excMsg = "Tried to add the Product " + product.getString("productName") +
                " (productId: " + product.getString("productId") + ") to the cart. This product has not yet been made available for sale, so not adding.";

            Debug.logWarning(excMsg);
            throw new CartItemModifyException(excMsg);
        }

        // check to see if salesDiscontinuationDate has passed
        if (product.get("salesDiscontinuationDate") != null && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"))) {
            String excMsg = "Tried to add the Product " + product.getString("productName") +
                " (productId: " + product.getString("productId") + ") to the cart. This product is no longer available for sale, so not adding.";

            Debug.logWarning(excMsg);
            throw new CartItemModifyException(excMsg);
        }

        // add to cart before setting quantity so that we can get order total, etc
        if (cartLocation == null) {
            cart.addItemToEnd(newItem);
        } else {
            cart.addItem(cartLocation.intValue(), newItem);
        }

        try {
            newItem.setQuantity(quantity, dispatcher, cart, doPromotions);
        } catch (CartItemModifyException e) {
            cart.removeEmptyCartItems();
            throw e;
        }
        return newItem;
    }

    /** Clone an item. */
    public ShoppingCartItem(ShoppingCartItem item) {
        this.delegator = item.getDelegator();
        this._product = item.getProduct();
        this.delegatorName = delegator.getDelegatorName();
        this.prodCatalogId = getProdCatalogId();
        this.productId = item.getProductId();
        this.itemComment = item.getItemComment();
        this.quantity = item.getQuantity();
        this.basePrice = item.getBasePrice();
        this.listPrice = item.getListPrice();
        this.isPromo = item.getIsPromo();
        this.orderItemSeqId = item.getOrderItemSeqId();
        this.orderShipmentPreference = item.getOrderShipmentPreference();
        this.additionalProductFeatureAndAppls = item.getAdditionalProductFeatureAndAppls() == null ?
                null : new HashMap(item.getAdditionalProductFeatureAndAppls());
        this.attributes = item.getAttributes() == null ? null : new HashMap(item.getAttributes());
        this.contactMechIdsMap = item.getOrderItemContactMechIds() == null ? null : new HashMap(item.getOrderItemContactMechIds());
        this.orderItemPriceInfos = item.getOrderItemPriceInfos() == null ? null : new LinkedList(item.getOrderItemPriceInfos());
        this.itemAdjustments = item.getAdjustments() == null ? null : new LinkedList(item.getAdjustments());
    }

    /** can't create shopping cart item with no parameters */
    protected ShoppingCartItem() {}

    /** Creates new ShoppingCartItem object. */
    protected ShoppingCartItem(GenericValue product, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId) {
        this._product = product;
        this.productId = _product.getString("productId");
        this.prodCatalogId = prodCatalogId;
        this.itemComment = null;
        this.attributes = attributes;
        this.delegator = _product.getDelegator();
        this.delegatorName = _product.getDelegator().getDelegatorName();
        this.orderShipmentPreference = delegator.makeValue("OrderShipmentPreference", null);
        this.addAllProductFeatureAndAppls(additionalProductFeatureAndAppls);
    }

    public String getProdCatalogId() {
        return this.prodCatalogId;
    }

    /** Sets the quantity for the item and validates the change in quantity, etc */
    public void setQuantity(double quantity, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException {
        setQuantity(quantity, dispatcher, cart, true);
    }

    /** Sets the quantity for the item and validates the change in quantity, etc */
    public void setQuantity(double quantity, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        if (this.quantity == quantity) {
            return;
        }

        if (this.isPromo) {
            throw new CartItemModifyException("Sorry, you can't change the quantity on the promotion item " + getProduct().getString("productName") + " (product ID: " + productId + "), not setting quantity.");
        }

        // check inventory if new quantity is greater than old quantity; don't worry about inventory getting pulled out from under, that will be handled at checkout time
        if (quantity > this.quantity) {
            if (org.ofbiz.commonapp.product.catalog.CatalogWorker.isCatalogInventoryRequired(this.prodCatalogId, this.getProduct(), this.getDelegator())) {
                if (!org.ofbiz.commonapp.product.catalog.CatalogWorker.isCatalogInventoryAvailable(this.prodCatalogId, productId, quantity, getDelegator(), dispatcher)) {
                    String excMsg = "Sorry, we do not have enough (you tried " + UtilFormatOut.formatQuantity(quantity) + ") of the product " + getProduct().getString("productName") + " (product ID: " + productId + ") in stock, not adding to cart. Please try a lower quantity, try again later, or call customer service for more information.";

                    Debug.logWarning(excMsg);
                    throw new CartItemModifyException(excMsg);
                }
            }
        }

        // set quantity before promos so order total, etc will be updated
        double oldQuantity = this.quantity;

        this.quantity = quantity;

        // set basePrice using the calculateProductPrice service
        try {
            Map priceContext = new HashMap();

            priceContext.put("product", this.getProduct());
            priceContext.put("prodCatalogId", prodCatalogId);
            if (cart.getUserLogin() != null) {
                priceContext.put("partyId", cart.getUserLogin().getString("partyId"));
            } else if (cart.getAutoUserLogin() != null) {
                priceContext.put("partyId", cart.getAutoUserLogin().getString("partyId"));
            }
            priceContext.put("quantity", new Double(quantity));
            Map priceResult = dispatcher.runSync("calculateProductPrice", priceContext);

            if (ModelService.RESPOND_ERROR.equals(priceResult.get(ModelService.RESPONSE_MESSAGE))) {
                throw new CartItemModifyException("There was an error while calculating the price: " + priceResult.get(ModelService.ERROR_MESSAGE));
            }

            if (priceResult.get("price") != null) this.basePrice = ((Double) priceResult.get("price")).doubleValue();
            if (priceResult.get("listPrice") != null) this.listPrice = ((Double) priceResult.get("listPrice")).doubleValue();
            this.orderItemPriceInfos = (List) priceResult.get("orderItemPriceInfos");
        } catch (GenericServiceException e) {
            throw new CartItemModifyException("There was an error while calculating the price", e);
        }

        // apply/unapply promotions
        if (doPromotions) {
            org.ofbiz.commonapp.product.promo.ProductPromoWorker.doPromotions(prodCatalogId, cart, this, oldQuantity, getDelegator(), dispatcher);
        }
    }

    /** Returns the quantity. */
    public double getQuantity() {
        return quantity;
    }

    /** Sets the item comment. */
    public void setItemComment(String itemComment) {
        this.itemComment = itemComment;
    }

    /** Returns the item's comment. */
    public String getItemComment() {
        return itemComment;
    }

    public void setOrderItemSeqId(String orderItemSeqId) {
        this.orderItemSeqId = orderItemSeqId;
    }

    public String getOrderItemSeqId() {
        return orderItemSeqId;
    }

    /** Returns true if shipping charges apply to this item. */
    public boolean shippingApplies() {
        Boolean shipCharge = getProduct().getBoolean("chargeShipping");

        if (shipCharge == null) {
            return true;
        } else {
            return shipCharge.booleanValue();
        }
    }

    /** Returns true if tax charges apply to this item. */
    public boolean taxApplies() {
        Boolean taxable = getProduct().getBoolean("taxable");

        if (taxable == null) {
            return true;
        } else {
            return taxable.booleanValue();
        }
    }

    /** Returns the item's productId. */
    public String getProductId() {
        return productId;
    }

    /** Returns the item's description. */
    public String getName() {
        String productName = getProduct().getString("productName");
        
        // if the productName is null or empty, see if there is an associated virtual product and get the productName of that product
        if (UtilValidate.isEmpty(productName)) {
            GenericValue parentProduct = this.getParentProduct();
            if (parentProduct != null) productName = parentProduct.getString("productName");
        }
        
        return productName;
    }

    /** Returns the item's description. */
    public String getDescription() {
        String description = getProduct().getString("description");
        
        // if the description is null or empty, see if there is an associated virtual product and get the description of that product
        if (UtilValidate.isEmpty(description)) {
            GenericValue parentProduct = this.getParentProduct();
            if (parentProduct != null) description = parentProduct.getString("description");
        }
        
        return description;
    }

    /** Returns the item's unit weight */
    public double getWeight() {
        Double weight = getProduct().getDouble("weight");

        // if the weight is null, see if there is an associated virtual product and get the weight of that product
        if (weight == null) {
            GenericValue parentProduct = this.getParentProduct();
            if (parentProduct != null) weight = parentProduct.getDouble("weight");
        }
        
        if (weight == null) {
            return 0;
        } else {
            return weight.doubleValue();
        }
    }

    /** Returns the base price. */
    public double getBasePrice() {
        return basePrice;
    }

    /** Returns the list price. */
    public double getListPrice() {
        return listPrice;
    }

    /** Returns the "other" adjustments. */
    public double getOtherAdjustments() {
        return OrderReadHelper.calcItemAdjustments(new Double(quantity), new Double(getBasePrice()), this.getAdjustments(), true, false, false, false, false);
    }

    /** Returns the total line price. */
    public double getItemSubTotal() {
        return (getBasePrice() * quantity) + getOtherAdjustments();
    }

    /** Returns the total line price for tax calculation purposes. */
    public double getItemSubTotalForTax() {
        return (getBasePrice() * quantity) + OrderReadHelper.calcItemAdjustments(new Double(quantity), new Double(getBasePrice()), this.getAdjustments(), true, false, false, true, false);
    }

    /** Returns the total line price for shipping calculation purposes. */
    public double getItemSubTotalForShipping() {
        return (getBasePrice() * quantity) + OrderReadHelper.calcItemAdjustments(new Double(quantity), new Double(getBasePrice()), this.getAdjustments(), true, false, false, false, true);
    }

    /** Returns the tax adjustments. */
    public double getItemTax() {
        return OrderReadHelper.calcItemAdjustments(new Double(quantity), new Double(getBasePrice()), this.getAdjustments(), false, true, false, false, false);
    }

    /** Returns the shipping adjustments. */
    public double getItemShipping() {
        return OrderReadHelper.calcItemAdjustments(new Double(quantity), new Double(getBasePrice()), this.getAdjustments(), false, false, true, false, false);
    }

    public void addAllProductFeatureAndAppls(Map additionalProductFeatureAndAppls) {
        if (additionalProductFeatureAndAppls == null) return;
        Iterator additionalProductFeatureAndApplsIter = additionalProductFeatureAndAppls.values().iterator();

        while (additionalProductFeatureAndApplsIter.hasNext()) {
            GenericValue additionalProductFeatureAndAppl = (GenericValue) additionalProductFeatureAndApplsIter.next();

            this.putAdditionalProductFeatureAndAppl(additionalProductFeatureAndAppl);
        }
    }

    public void putAdditionalProductFeatureAndAppl(GenericValue additionalProductFeatureAndAppl) {
        if (this.additionalProductFeatureAndAppls == null) this.additionalProductFeatureAndAppls = new HashMap();
        if (additionalProductFeatureAndAppl == null) return;

        // if one already exists with the given type, remove it with the corresponding adjustment
        removeAdditionalProductFeatureAndAppl(additionalProductFeatureAndAppl.getString("productFeatureTypeId"));

        // adds to additional map and creates an adjustment with given price
        this.additionalProductFeatureAndAppls.put(additionalProductFeatureAndAppl.get("productFeatureTypeId"), additionalProductFeatureAndAppl);
        GenericValue orderAdjustment = this.getDelegator().makeValue("OrderAdjustment", null);

        orderAdjustment.set("orderAdjustmentTypeId", "ADDITIONAL_FEATURE");
        orderAdjustment.set("description", additionalProductFeatureAndAppl.get("description"));
        orderAdjustment.set("productFeatureId", additionalProductFeatureAndAppl.get("productFeatureId"));
        // NOTE: this is a VERY simple pricing scheme for additional features and will likely need to be extended for most real applications
        orderAdjustment.set("amountPerQuantity", additionalProductFeatureAndAppl.get("amount"));

        this.addAdjustment(orderAdjustment);
    }

    public GenericValue getAdditionalProductFeatureAndAppl(String productFeatureTypeId) {
        if (this.additionalProductFeatureAndAppls == null) return null;
        return (GenericValue) this.additionalProductFeatureAndAppls.get(productFeatureTypeId);
    }

    public GenericValue removeAdditionalProductFeatureAndAppl(String productFeatureTypeId) {
        if (this.additionalProductFeatureAndAppls == null) return null;

        GenericValue oldAdditionalProductFeatureAndAppl = (GenericValue) this.additionalProductFeatureAndAppls.remove(productFeatureTypeId);

        if (oldAdditionalProductFeatureAndAppl != null) {
            removeFeatureAdjustment(oldAdditionalProductFeatureAndAppl.getString("productFeatureId"));
        }

        if (this.additionalProductFeatureAndAppls.size() == 0) this.additionalProductFeatureAndAppls = null;

        return oldAdditionalProductFeatureAndAppl;
    }

    public Map getAdditionalProductFeatureAndAppls() {
        return this.additionalProductFeatureAndAppls;
    }

    /** Removes an item attribute. */
    public void removeAttribute(String name) {
        if (attributes == null) attributes = new HashMap();
        if (attributes.containsKey(name))
            attributes.remove(name);
    }

    /** Sets an item attribute. */
    public void setAttribute(String name, Object value) {
        if (attributes == null) attributes = new HashMap();
        attributes.put(name, value);
    }

    /** Return a specific attribute. */
    public Object getAttribute(String name) {
        if (attributes == null) return null;
        return attributes.get(name);
    }

    /** Returns the attributes for the item. */
    public Map getAttributes() {
        return attributes;
    }

    /** Add an adjustment to the order item; don't worry about setting the orderId, orderItemSeqId or orderAdjustmentId; they will be set when the order is created */
    public void addAdjustment(GenericValue adjustment) {
        itemAdjustments.add(adjustment);
    }

    public void removeAdjustment(GenericValue adjustment) {
        itemAdjustments.remove(adjustment);
    }

    public void removeAdjustment(int index) {
        itemAdjustments.remove(index);
    }

    public List getAdjustments() {
        return itemAdjustments;
    }

    public void removeFeatureAdjustment(String productFeatureId) {
        if (productFeatureId == null) return;
        Iterator itemAdjustmentsIter = itemAdjustments.iterator();

        while (itemAdjustmentsIter.hasNext()) {
            GenericValue itemAdjustment = (GenericValue) itemAdjustmentsIter.next();

            if (productFeatureId.equals(itemAdjustment.getString("productFeatureId"))) {
                itemAdjustmentsIter.remove();
            }
        }
    }

    public List getOrderItemPriceInfos() {
        return orderItemPriceInfos;
    }

    /** Add a contact mech to this purpose; the contactMechPurposeTypeId is required */
    public void addContactMech(String contactMechPurposeTypeId, String contactMechId) {
        if (contactMechPurposeTypeId == null) throw new IllegalArgumentException("You must specify a contactMechPurposeTypeId to add a ContactMech");
        contactMechIdsMap.put(contactMechPurposeTypeId, contactMechId);
    }

    /** Get the contactMechId for this item given the contactMechPurposeTypeId */
    public String getContactMech(String contactMechPurposeTypeId) {
        return (String) contactMechIdsMap.get(contactMechPurposeTypeId);
    }

    /** Remove the contactMechId from this item given the contactMechPurposeTypeId */
    public String removeContactMech(String contactMechPurposeTypeId) {
        return (String) contactMechIdsMap.remove(contactMechPurposeTypeId);
    }

    public Map getOrderItemContactMechIds() {
        return contactMechIdsMap;
    }

    public void setIsPromo(boolean isPromo) {
        this.isPromo = isPromo;
    }

    public boolean getIsPromo() {
        return this.isPromo;
    }

    public GenericValue getOrderShipmentPreference() {
        return orderShipmentPreference;
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

    public void setCarrierPartyId(String carrierPartyId) {
        orderShipmentPreference.set("carrierPartyId", carrierPartyId);
    }

    public String getCarrierPartyId() {
        return orderShipmentPreference.getString("carrierPartyId");
    }

    /** Compares the specified object with this cart item. */
    public boolean equals(ShoppingCartItem item) {
        if (item == null) return false;
        return this.equals(item.getProductId(), item.additionalProductFeatureAndAppls, item.attributes, item.prodCatalogId, item.getIsPromo());
    }

    /** Compares the specified object with this cart item. Defaults isPromo to false. */
    public boolean equals(String productId, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId) {
        return equals(productId, additionalProductFeatureAndAppls, attributes, prodCatalogId, false);
    }

    /** Compares the specified object with this cart item. */
    public boolean equals(String productId, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, boolean isPromo) {
        if (!this.productId.equals(productId)) {
            return false;
        }

        if (!this.prodCatalogId.equals(prodCatalogId)) {
            return false;
        }

        if (this.isPromo != isPromo) {
            return false;
        }

        boolean featuresEqual = this.additionalProductFeatureAndAppls == null ?
            additionalProductFeatureAndAppls == null :
            this.additionalProductFeatureAndAppls.equals(additionalProductFeatureAndAppls);

        if (!featuresEqual) return false;

        boolean attributesEqual = this.attributes == null ? attributes == null : this.attributes.equals(attributes);

        if (!attributesEqual) return false;

        return true;
    }

    /** Gets the Product entity. If it is not already retreived gets it from the delegator */
    public GenericValue getProduct() {
        if (this._product != null) {
            return this._product;
        }
        if (this.productId == null) {
            throw new IllegalStateException("Bad product id");
        }
        try {
            this._product = this.getDelegator().findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            throw new RuntimeException("Entity Engine error getting Product (" + e.getMessage() + ")");
        }
        return this._product;
    }
    
    public GenericValue getParentProduct() {
        if (this._parentProduct != null) {
            return this._parentProduct;
        }
        if (this.productId == null) {
            throw new IllegalStateException("Bad product id");
        }
        
        try {
            List virtualProductAssocs = this.getDelegator().findByAndCache("ProductAssoc", UtilMisc.toMap("productIdTo", productId, "productAssocTypeId", "PRODUCT_VARIANT"), UtilMisc.toList("-fromDate"));
            virtualProductAssocs = EntityUtil.filterByDate(virtualProductAssocs, true);
            if (virtualProductAssocs == null || virtualProductAssocs.size() == 0) {
                //okay, not a variant, try a UNIQUE_ITEM
                virtualProductAssocs = this.getDelegator().findByAndCache("ProductAssoc", UtilMisc.toMap("productIdTo", productId, "productAssocTypeId", "UNIQUE_ITEM"), UtilMisc.toList("-fromDate"));
                virtualProductAssocs = EntityUtil.filterByDate(virtualProductAssocs, true);
            }
            if (virtualProductAssocs != null && virtualProductAssocs.size() > 0) {
                //found one, set this first as the parent product
                GenericValue productAssoc = EntityUtil.getFirst(virtualProductAssocs);
                this._parentProduct = productAssoc.getRelatedOneCache("MainProduct");
            }
        } catch (GenericEntityException e) {
            throw new RuntimeException("Entity Engine error getting Parent Product (" + e.getMessage() + ")");
        }
        return this._parentProduct;
    }

    public GenericDelegator getDelegator() {
        if (delegator == null) {
            if (UtilValidate.isEmpty(delegatorName)) {
                throw new IllegalStateException("Bad delegator name");
            }
            delegator = GenericDelegator.getGenericDelegator(delegatorName);
        }
        return delegator;
    }

    public void explodeItem(ShoppingCart cart, LocalDispatcher dispatcher) throws CartItemModifyException {
        double baseQuantity = this.getQuantity();
        int thisIndex = cart.items().indexOf(this);
        List newItems = new ArrayList();

        if (baseQuantity > 1) {
            for (int i = 1; i < baseQuantity; i++) {
                // clone the item
                ShoppingCartItem item = new ShoppingCartItem(this);

                // set the new item's quantity
                item.setQuantity(1, dispatcher, cart, false);

                // now copy/calc the adjustments
                Debug.logInfo("Clone's adj: " + item.getAdjustments());
                if (item.getAdjustments() != null && item.getAdjustments().size() > 0) {
                    List adjustments = new LinkedList(item.getAdjustments());
                    Iterator adjIterator = adjustments.iterator();

                    while (adjIterator.hasNext()) {
                        GenericValue adjustment = (GenericValue) adjIterator.next();

                        if (adjustment != null) {
                            item.removeAdjustment(adjustment);
                            GenericValue newAdjustment = new GenericValue(adjustment);
                            Double adjAmount = newAdjustment.getDouble("amount");

                            // we use != becuase adjustments can be +/-
                            if (adjAmount != null && adjAmount.doubleValue() != 0.00)
                                newAdjustment.set("amount", new Double(adjAmount.doubleValue() / baseQuantity));
                            Debug.logInfo("Cloned adj: " + newAdjustment);
                            item.addAdjustment(newAdjustment);
                        } else {
                            Debug.logInfo("Clone Adjustment is null");
                        }
                    }
                }
                newItems.add(item);
            }

            // set this item's quantity
            this.setQuantity(1, dispatcher, cart, false);

            Debug.logInfo("BaseQuantity: " + baseQuantity);
            Debug.logInfo("Item's Adj: " + this.getAdjustments());

            // re-calc this item's adjustments
            if (this.getAdjustments() != null && this.getAdjustments().size() > 0) {
                List adjustments = new LinkedList(this.getAdjustments());
                Iterator adjIterator = adjustments.iterator();

                while (adjIterator.hasNext()) {
                    GenericValue adjustment = (GenericValue) adjIterator.next();

                    if (adjustment != null) {
                        this.removeAdjustment(adjustment);
                        GenericValue newAdjustment = new GenericValue(adjustment);
                        Double adjAmount = newAdjustment.getDouble("amount");

                        // we use != becuase adjustments can be +/-
                        if (adjAmount != null && adjAmount.doubleValue() != 0.00)
                            newAdjustment.set("amount", new Double(adjAmount.doubleValue() / baseQuantity));
                        Debug.logInfo("Updated adj: " + newAdjustment);
                        this.addAdjustment(newAdjustment);
                    }
                }
            }

            // add the cloned item(s) to the cart
            Iterator newItemsItr = newItems.iterator();

            while (newItemsItr.hasNext())
                cart.addItem(thisIndex, (ShoppingCartItem) newItemsItr.next());
        }
    }
}
