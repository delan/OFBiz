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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import org.apache.commons.collections.set.ListOrderedSet;

/**
 * <p><b>Title:</b> ShoppingCartItem.java
 * <p><b>Description:</b> Shopping cart item object.
 *
 * @author     <a href="mailto:jaz@ofbiz.org.com">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class ShoppingCartItem implements java.io.Serializable {

    public static String module = ShoppingCartItem.class.getName();
    public static final String resource = "OrderUiLabels";
    public static String[] attributeNames = { "shoppingListId", "shoppingListItemSeqId", "surveyResponses",
                                              "itemDesiredDeliveryDate", "itemComment"};

    private transient GenericDelegator delegator = null;
    private transient GenericValue _product = null;       // the actual product
    private transient GenericValue _parentProduct = null; // the virtual product

    private String delegatorName = null;
    private String prodCatalogId = null;
    private String productId = null;
    private String itemType = null;
    private String productCategoryId = null;
    private String itemDescription = null;  // special field for non-product items
    private double quantity = 0.0;
    private double basePrice = 0.0;
    private double listPrice = 0.0;
    private double selectedAmount = 0.0;
    private Map attributes = null;
    private String orderItemSeqId = null;
    private GenericValue orderShipmentPreference = null;
    
    private Locale locale = null;

    private Map contactMechIdsMap = new HashMap();
    private List orderItemPriceInfos = null;
    private List itemAdjustments = new LinkedList();
    private boolean isPromo = false;
    private double promoQuantityUsed = 0;
    private Map quantityUsedPerPromoCandidate = new HashMap();
    private Map quantityUsedPerPromoFailed = new HashMap();
    private Map quantityUsedPerPromoActual = new HashMap();
    private Map additionalProductFeatureAndAppls = new HashMap();
    private List alternativeOptionProductIds = null;
    private ProductConfigWrapper configWrapper = null;

    /**
     * Makes a ShoppingCartItem and adds it to the cart.
     * NOTE: This method will get the product entity and check to make sure it can be purchased.
     *
     * @param cartLocation The location to place this item; null will place at the end
     * @param productId The primary key of the product being added
     * @param quantity The quantity to add
     * @param additionalProductFeatureAndAppls Product feature/appls map
     * @param attributes All unique attributes for this item (NOT features)
     * @param prodCatalogId The catalog this item was added from
     * @param dispatcher LocalDispatcher object for doing promotions, etc
     * @param cart The parent shopping cart object this item will belong to
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, String productId, double selectedAmount, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException, ItemNotFoundException {
        return ShoppingCartItem.makeItem(cartLocation, productId, selectedAmount, quantity, additionalProductFeatureAndAppls, attributes, prodCatalogId, null, dispatcher, cart);
    }
    
    /**
     * Makes a ShoppingCartItem and adds it to the cart.
     * NOTE: This method will get the product entity and check to make sure it can be purchased.
     *
     * @param cartLocation The location to place this item; null will place at the end
     * @param productId The primary key of the product being added
     * @param quantity The quantity to add
     * @param additionalProductFeatureAndAppls Product feature/appls map
     * @param attributes All unique attributes for this item (NOT features)
     * @param prodCatalogId The catalog this item was added from
     * @param configWrapper The product configuration wrapper (null if the product is not configurable)
     * @param dispatcher LocalDispatcher object for doing promotions, etc
     * @param cart The parent shopping cart object this item will belong to
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, String productId, double selectedAmount, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException, ItemNotFoundException {
        GenericDelegator delegator = cart.getDelegator();
        GenericValue product = null;

        try {
            product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));

            // first see if there is a purchase allow category and if this product is in it or not
            String purchaseProductCategoryId = CatalogWorker.getCatalogPurchaseAllowCategoryId(delegator, prodCatalogId);
            if (purchaseProductCategoryId != null) {
                if (!CategoryWorker.isProductInCategory(delegator, product.getString("productId"), purchaseProductCategoryId)) {
                    // a Purchase allow productCategoryId was found, but the product is not in the category, axe it...
                    product = null;
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.toString(), module);
            product = null;
        }

        if (product == null) {
            Map messageMap = UtilMisc.toMap("productId", productId );

            String excMsg = UtilProperties.getMessage(resource, "item.product_not_found",
                                          messageMap , cart.getLocale() );

            Debug.logWarning(excMsg, module);
            throw new ItemNotFoundException(excMsg);
        }

        return makeItem(cartLocation, product, selectedAmount, quantity, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, dispatcher, cart, true);
    }
    
    /**
     * Makes a ShoppingCartItem and adds it to the cart.
     * WARNING: This method does not check if the product is in a purchase category.
     *
     * @param cartLocation The location to place this item; null will place at the end
     * @param product The product entity relating to the product being added
     * @param quantity The quantity to add
     * @param additionalProductFeatureAndAppls Product feature/appls map
     * @param attributes All unique attributes for this item (NOT features)
     * @param prodCatalogId The catalog this item was added from
     * @param dispatcher LocalDispatcher object for doing promotions, etc
     * @param cart The parent shopping cart object this item will belong to
     * @param doPromotions Indicates if we should run promotions
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double selectedAmount, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        return ShoppingCartItem.makeItem(cartLocation, product, selectedAmount, quantity, additionalProductFeatureAndAppls, attributes, prodCatalogId, null, dispatcher, cart, doPromotions);
    }
    
    /**
     * Makes a ShoppingCartItem and adds it to the cart.
     * WARNING: This method does not check if the product is in a purchase category.
     *
     * @param cartLocation The location to place this item; null will place at the end
     * @param product The product entity relating to the product being added
     * @param quantity The quantity to add
     * @param additionalProductFeatureAndAppls Product feature/appls map
     * @param attributes All unique attributes for this item (NOT features)
     * @param prodCatalogId The catalog this item was added from
     * @param configWrapper The product configuration wrapper (null if the product is not configurable)
     * @param dispatcher LocalDispatcher object for doing promotions, etc
     * @param cart The parent shopping cart object this item will belong to
     * @param doPromotions Indicates if we should run promotions
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double selectedAmount, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        ShoppingCartItem newItem = new ShoppingCartItem(product, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, cart.getLocale());

        // check to see if product is virtual
        if ("Y".equals(product.getString("isVirtual"))) {
            String excMsg = "Tried to add the Virtual Product " + product.getString("productName") +
                " (productId: " + product.getString("productId") + ") to the cart, not adding.";

            Debug.logWarning(excMsg, module);
            throw new CartItemModifyException(excMsg);
        }

        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        // check to see if introductionDate hasn't passed yet
        if (product.get("introductionDate") != null && nowTimestamp.before(product.getTimestamp("introductionDate"))) {
            String excMsg = "Tried to add the Product " + product.getString("productName") +
                " (productId: " + product.getString("productId") + ") to the cart. This product has not yet been made available for sale, so not adding.";

            Debug.logWarning(excMsg, module);
            throw new CartItemModifyException(excMsg);
        }

        // check to see if salesDiscontinuationDate has passed
        if (product.get("salesDiscontinuationDate") != null && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"))) {
            String excMsg = "Tried to add the Product " + product.getString("productName") +
                " (productId: " + product.getString("productId") + ") to the cart. This product is no longer available for sale, so not adding.";

            Debug.logWarning(excMsg, module);
            throw new CartItemModifyException(excMsg);
        }
        
        // check to see if the product is fully configured
        if ("AGGREGATED".equals(product.getString("productTypeId"))) {
            if (configWrapper == null || !configWrapper.isCompleted()) {
                String excMsg = "Tried to add the Product " + product.getString("productName") +
                    " (productId: " + product.getString("productId") + ") to the cart, not adding: the product is not configured correctly.";

                Debug.logWarning(excMsg, module);
                throw new CartItemModifyException(excMsg);
            }
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
            cart.removeCartItem(cart.getItemIndex(newItem), dispatcher);
            cart.removeEmptyCartItems();
            throw e;
        }

        if (selectedAmount > 0) {
            newItem.setSelectedAmount(selectedAmount);
        }
        return newItem;
    }
    
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        return makeItem(cartLocation, product, 0.00, quantity, additionalProductFeatureAndAppls, attributes, prodCatalogId, dispatcher, cart, doPromotions);
    }

    /**
     * Makes a non-product ShoppingCartItem and adds it to the cart.
     * NOTE: This is only for non-product items; items without a product entity (work items, bulk items, etc)
     *
     * @param cartLocation The location to place this item; null will place at the end
     * @param itemType The OrderItemTypeId for the item being added
     * @param itemDescription The optional description of the item
     * @param productCategoryId The optional category the product *will* go in
     * @param basePrice The price for this item
     * @param quantity The quantity to add
     * @param attributes All unique attributes for this item (NOT features)
     * @param prodCatalogId The catalog this item was added from
     * @param dispatcher LocalDispatcher object for doing promotions, etc
     * @param cart The parent shopping cart object this item will belong to
     * @param doPromotions Indicates if we should run promotions
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, String itemType, String itemDescription, String productCategoryId, double basePrice, double selectedAmount, double quantity, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        GenericDelegator delegator = cart.getDelegator();
        ShoppingCartItem newItem = new ShoppingCartItem(delegator, itemType, itemDescription, productCategoryId, basePrice, attributes, prodCatalogId, cart.getLocale());

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

        if (selectedAmount > 0) {
            newItem.setSelectedAmount(selectedAmount);
        }
        return newItem;
    }

    /** Clone an item. */
    public ShoppingCartItem(ShoppingCartItem item) {
        try {
            this._product = item.getProduct();
        } catch (IllegalStateException e) {
            this._product = null;
        }
        this.delegator = item.getDelegator();
        this.delegatorName = item.delegatorName;
        this.prodCatalogId = item.getProdCatalogId();
        this.productId = item.getProductId();
        this.itemType = item.getItemType();
        this.productCategoryId = item.getProductCategoryId();
        this.quantity = item.getQuantity();
        this.selectedAmount = item.getSelectedAmount();
        this.basePrice = item.getBasePrice();
        this.listPrice = item.getListPrice();
        this.isPromo = item.getIsPromo();
        this.promoQuantityUsed = item.promoQuantityUsed;
        this.locale = item.locale;
        this.quantityUsedPerPromoCandidate = new HashMap(item.quantityUsedPerPromoCandidate);
        this.quantityUsedPerPromoFailed = new HashMap(item.quantityUsedPerPromoFailed);
        this.quantityUsedPerPromoActual = new HashMap(item.quantityUsedPerPromoActual);
        this.orderItemSeqId = item.getOrderItemSeqId();
        this.orderShipmentPreference = new GenericValue(item.getOrderShipmentPreference());
        this.additionalProductFeatureAndAppls = item.getAdditionalProductFeatureAndAppls() == null ?
                null : new HashMap(item.getAdditionalProductFeatureAndAppls());
        this.attributes = item.getAttributes() == null ? null : new HashMap(item.getAttributes());
        this.contactMechIdsMap = item.getOrderItemContactMechIds() == null ? null : new HashMap(item.getOrderItemContactMechIds());
        this.orderItemPriceInfos = item.getOrderItemPriceInfos() == null ? null : new LinkedList(item.getOrderItemPriceInfos());
        this.itemAdjustments = item.getAdjustments() == null ? null : new LinkedList(item.getAdjustments());
        if (this._product == null) {
            this.itemDescription = item.getName();
        }
    }

    /** Cannot create shopping cart item with no parameters */
    protected ShoppingCartItem() {}

    /** Creates new ShoppingCartItem object. */
    protected ShoppingCartItem(GenericValue product, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, Locale locale) {
        this(product, additionalProductFeatureAndAppls, attributes, prodCatalogId, null, locale);
    }

    /** Creates new ShoppingCartItem object. */
    protected ShoppingCartItem(GenericValue product, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, Locale locale) {
        this._product = product;
        this.productId = _product.getString("productId");
        this.itemType = "PRODUCT_ORDER_ITEM";
        this.prodCatalogId = prodCatalogId;
        this.attributes = attributes;
        this.delegator = _product.getDelegator();
        this.delegatorName = _product.getDelegator().getDelegatorName();
        this.orderShipmentPreference = delegator.makeValue("OrderShipmentPreference", null);
        this.addAllProductFeatureAndAppls(additionalProductFeatureAndAppls);
        this.locale = locale;
        this.configWrapper = configWrapper;
    }

    /** Creates new ShopingCartItem object. */
    protected ShoppingCartItem(GenericDelegator delegator, String itemTypeId, String description, String categoryId, double basePrice, Map attributes, String prodCatalogId, Locale locale) {
        this.delegator = delegator;
        this.itemType = itemTypeId;
        this.itemDescription = description;
        this.productCategoryId = categoryId;
        this.basePrice = basePrice;
        this.attributes = attributes;
        this.prodCatalogId = prodCatalogId;
        this.delegatorName = delegator.getDelegatorName();
        this.locale = locale;
       }

    public String getProdCatalogId() {
        return this.prodCatalogId;
    }

    /** Sets the user selected amount */
    public void setSelectedAmount(double selectedAmount) {
        this.selectedAmount = selectedAmount;
    }

    /** Returns the user selected amount */
    public double getSelectedAmount() {
        return this.selectedAmount;
    }

    /** Sets the base price for the item; use with caution */
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    /** Sets the quantity for the item and validates the change in quantity, etc */
    public void setQuantity(double quantity, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException {
        this.setQuantity(quantity, dispatcher, cart, true);
    }

    /** Sets the quantity for the item and validates the change in quantity, etc */
    public void setQuantity(double quantity, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        this.setQuantity((int) quantity, dispatcher, cart, doPromotions);
    }

    protected void setQuantity(int quantity, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        if (this.quantity == quantity) {
            return;
        }

        if (this.isPromo) {
            throw new CartItemModifyException("Sorry, you can't change the quantity on the promotion item " + this.getName() + " (product ID: " + productId + "), not setting quantity.");
        }

        // needed for inventory checking and auto-save
        String productStoreId = cart.getProductStoreId();

        // check inventory if new quantity is greater than old quantity; don't worry about inventory getting pulled out from under, that will be handled at checkout time
        if (_product != null && quantity > this.quantity) {
            try {
                Map invReqResult = dispatcher.runSync("isStoreInventoryAvailableOrNotRequired", UtilMisc.toMap("productStoreId", productStoreId, "productId", productId, "product", this.getProduct(), "quantity", new Double(quantity)));
                if (ServiceUtil.isError(invReqResult)) {
                    Debug.logError("Error calling isStoreInventoryAvailableOrNotRequired service, result is: " + invReqResult, module);
                    throw new CartItemModifyException((String) invReqResult.get(ModelService.ERROR_MESSAGE));
                } else if (!"Y".equals((String) invReqResult.get("availableOrNotRequired"))) {
                    String excMsg = "Sorry, we do not have enough (you tried " + UtilFormatOut.formatQuantity(quantity) + ") of the product " + this.getName() + " (product ID: " + productId + ") in stock, not adding to cart. Please try a lower quantity, try again later, or call customer service for more information.";
                    Debug.logWarning(excMsg, module);
                    throw new CartItemModifyException(excMsg);
                }
            } catch (GenericServiceException e) {
                String errMsg = "Fatal error calling inventory checking services: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new CartItemModifyException(errMsg);
            }
        }

        // set quantity before promos so order total, etc will be updated
        this.quantity = quantity;
        this.updatePrice(dispatcher, cart);

        // apply/unapply promotions - only for sales orders
        if (doPromotions && cart.getOrderType().equals("SALES_ORDER")) {
            org.ofbiz.order.shoppingcart.product.ProductPromoWorker.doPromotions(cart, dispatcher);
        }

        // store the auto-save cart
        if (org.ofbiz.product.store.ProductStoreWorker.autoSaveCart(delegator, productStoreId)) {
            try {
                org.ofbiz.order.shoppinglist.ShoppingListEvents.fillAutoSaveList(cart, dispatcher);
            } catch (GeneralException e) {
                Debug.logWarning(e, "Unable to store auto-save cart", module);
            }
        }
    }

    public void updatePrice(LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException {
        // set basePrice using the calculateProductPrice service
        if (_product != null) {
            try {
                Map priceContext = new HashMap();
                priceContext.put("currencyUomId", cart.getCurrency());
                priceContext.put("product", this.getProduct());
                priceContext.put("prodCatalogId", this.getProdCatalogId());
                priceContext.put("webSiteId", cart.getWebSiteId());

                String partyId = cart.getPartyId();
                if (partyId != null) {
                    priceContext.put("partyId", partyId);
                }

                priceContext.put("quantity", new Double(this.getQuantity()));
                Map priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
                if (ModelService.RESPOND_ERROR.equals(priceResult.get(ModelService.RESPONSE_MESSAGE))) {
                    throw new CartItemModifyException("There was an error while calculating the price: " + priceResult.get(ModelService.ERROR_MESSAGE));
                }
                
                Boolean validPriceFound = (Boolean) priceResult.get("validPriceFound");
                if (!validPriceFound.booleanValue()) {
                    throw new CartItemModifyException("Could not find a valid price for the product with ID [" + this.getProductId() + "], not adding to cart.");
                }

                if (priceResult.get("listPrice") != null) this.listPrice = ((Double) priceResult.get("listPrice")).doubleValue();
                if (cart.getOrderType().equals("PURCHASE_ORDER")) {
                    if (priceResult.get("averageCost") != null) {
                        this.basePrice = ((Double) priceResult.get("averageCost")).doubleValue();
                    }
                } else {
                    if (priceResult.get("price") != null) {
                        this.basePrice = ((Double) priceResult.get("price")).doubleValue();
                    }
                }

                this.orderItemPriceInfos = (List) priceResult.get("orderItemPriceInfos");
                
                // If product is configurable, the price is taken from the configWrapper.
                if (configWrapper != null) {
                    this.basePrice = configWrapper.getTotalPrice();
                }
            } catch (GenericServiceException e) {
                throw new CartItemModifyException("There was an error while calculating the price", e);
            }
        }
    }

    /** Returns the quantity. */
    public double getQuantity() {
        return this.quantity;
    }

    public double getPromoQuantityUsed() {
        if (this.getIsPromo()) {
            return this.quantity;
        } else {
            return this.promoQuantityUsed;
        }
    }

    public double getPromoQuantityAvailable() {
        if (this.getIsPromo()) {
            return 0;
        } else {
            return this.quantity - this.promoQuantityUsed;
        }
    }

    public Iterator getQuantityUsedPerPromoActualIter() {
        return this.quantityUsedPerPromoActual.entrySet().iterator();
    }

    public Iterator getQuantityUsedPerPromoCandidateIter() {
        return this.quantityUsedPerPromoCandidate.entrySet().iterator();
    }

    public Iterator getQuantityUsedPerPromoFailedIter() {
        return this.quantityUsedPerPromoFailed.entrySet().iterator();
    }

    public synchronized double addPromoQuantityCandidateUse(double quantityDesired, GenericValue productPromoCondAction, boolean checkAvailableOnly) {
        if (quantityDesired == 0) return 0;
        double promoQuantityAvailable = this.getPromoQuantityAvailable();
        double promoQuantityToUse = quantityDesired;
        if (promoQuantityAvailable > 0) {
            if (promoQuantityToUse > promoQuantityAvailable) {
                promoQuantityToUse = promoQuantityAvailable;
            }

            if (!checkAvailableOnly) {
                // keep track of candidate promo uses on cartItem
                GenericPK productPromoCondActionPK = productPromoCondAction.getPrimaryKey();
                Double existingValue = (Double) this.quantityUsedPerPromoCandidate.get(productPromoCondActionPK);
                if (existingValue == null) {
                    this.quantityUsedPerPromoCandidate.put(productPromoCondActionPK, new Double(promoQuantityToUse));
                } else {
                    this.quantityUsedPerPromoCandidate.put(productPromoCondActionPK, new Double(promoQuantityToUse + existingValue.doubleValue()));
                }

                this.promoQuantityUsed += promoQuantityToUse;
                //Debug.logInfo("promoQuantityToUse=" + promoQuantityToUse + ", quantityDesired=" + quantityDesired + ", for promoCondAction: " + productPromoCondAction, module);
                //Debug.logInfo("promoQuantityUsed now=" + promoQuantityUsed, module);
            }

            return promoQuantityToUse;
        } else {
            return 0;
        }
    }

    public double getPromoQuantityCandidateUse(GenericValue productPromoCondAction) {
        GenericPK productPromoCondActionPK = productPromoCondAction.getPrimaryKey();
        Double existingValue = (Double) this.quantityUsedPerPromoCandidate.get(productPromoCondActionPK);
        if (existingValue == null) {
            return 0;
        } else {
            return existingValue.doubleValue();
        }
    }

    public double getPromoQuantityCandidateUseActionAndAllConds(GenericValue productPromoAction) {
        double totalUse = 0;
        String productPromoId = productPromoAction.getString("productPromoId");
        String productPromoRuleId = productPromoAction.getString("productPromoRuleId");

        GenericPK productPromoActionPK = productPromoAction.getPrimaryKey();
        Double existingValue = (Double) this.quantityUsedPerPromoCandidate.get(productPromoActionPK);
        if (existingValue != null) {
            totalUse = existingValue.doubleValue();
        }

        Iterator entryIter = this.quantityUsedPerPromoCandidate.entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            GenericPK productPromoCondActionPK = (GenericPK) entry.getKey();
            Double quantityUsed = (Double) entry.getValue();
            if (quantityUsed != null) {
                // must be in the same rule and be a condition
                if (productPromoId.equals(productPromoCondActionPK.getString("productPromoId")) &&
                        productPromoRuleId.equals(productPromoCondActionPK.getString("productPromoRuleId")) &&
                        productPromoCondActionPK.containsKey("productPromoCondSeqId")) {
                    totalUse += quantityUsed.doubleValue();
                }
            }
        }

        return totalUse;
    }

    public synchronized void resetPromoRuleUse(String productPromoId, String productPromoRuleId) {
        Iterator entryIter = this.quantityUsedPerPromoCandidate.entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            GenericPK productPromoCondActionPK = (GenericPK) entry.getKey();
            Double quantityUsed = (Double) entry.getValue();
            if (productPromoId.equals(productPromoCondActionPK.getString("productPromoId")) && productPromoRuleId.equals(productPromoCondActionPK.getString("productPromoRuleId"))) {
                entryIter.remove();
                Double existingValue = (Double) this.quantityUsedPerPromoFailed.get(productPromoCondActionPK);
                if (existingValue == null) {
                    this.quantityUsedPerPromoFailed.put(productPromoCondActionPK, quantityUsed);
                } else {
                    this.quantityUsedPerPromoFailed.put(productPromoCondActionPK, new Double(quantityUsed.doubleValue() + existingValue.doubleValue()));
                }
                this.promoQuantityUsed -= quantityUsed.doubleValue();
            }
        }
    }

    public synchronized void confirmPromoRuleUse(String productPromoId, String productPromoRuleId) {
        Iterator entryIter = this.quantityUsedPerPromoCandidate.entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            GenericPK productPromoCondActionPK = (GenericPK) entry.getKey();
            Double quantityUsed = (Double) entry.getValue();
            if (productPromoId.equals(productPromoCondActionPK.getString("productPromoId")) && productPromoRuleId.equals(productPromoCondActionPK.getString("productPromoRuleId"))) {
                entryIter.remove();
                Double existingValue = (Double) this.quantityUsedPerPromoActual.get(productPromoCondActionPK);
                if (existingValue == null) {
                    this.quantityUsedPerPromoActual.put(productPromoCondActionPK, quantityUsed);
                } else {
                    this.quantityUsedPerPromoActual.put(productPromoCondActionPK, new Double(quantityUsed.doubleValue() + existingValue.doubleValue()));
                }
            }
        }
    }

    public synchronized void clearPromoRuleUseInfo() {
        this.quantityUsedPerPromoActual.clear();
        this.quantityUsedPerPromoCandidate.clear();
        this.quantityUsedPerPromoFailed.clear();
        this.promoQuantityUsed = this.getIsPromo() ? this.quantity : 0;
    }

    /** Sets the item comment. */
    public void setItemComment(String itemComment) {
		this.setAttribute("itemComment", itemComment);
    }

    /** Returns the item's comment. */
    public String getItemComment() {
        return (String) this.getAttribute("itemComment");
    }

    /** Sets the item's customer desired delivery date. */
    public void setDesiredDeliveryDate(Timestamp ddDate) {
        this.setAttribute("itemDesiredDeliveryDate", UtilDateTime.toDateTimeString(ddDate));
    }
    
    /** Returns the item's customer desired delivery date. */
    public Timestamp getDesiredDeliveryDate() {
        String ddDate = (String) this.getAttribute("itemDesiredDeliveryDate");

        if (ddDate != null) {
            try {
                return Timestamp.valueOf(ddDate);
            } catch (IllegalArgumentException e) {
                Debug.logWarning(e, "Problem getting itemDesiredDeliveryDate for "
                        + this.getProductId(), module);
                return null;
            }
        }
        return null;
    }
    
    /** Sets the item type. */
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /** Returns the item type. */
    public String getItemType() {
        return itemType;
    }

    /** Returns the item type description. */
    public String getItemTypeDescription() {
        GenericValue orderItemType = null;
        try {
            orderItemType = this.getDelegator().findByPrimaryKeyCache("OrderItemType", UtilMisc.toMap("orderItemTypeId", this.getItemType()));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting OrderItemType for: " + this.getItemType(), module);
        }
        if (itemType != null) {
            return orderItemType.getString("description");
        }
        return null;
    }

    /** Returns the productCategoryId for the item or null if none. */
    public String getProductCategoryId() {
        return this.productCategoryId;
    }

    public void setOrderItemSeqId(String orderItemSeqId) {
        this.orderItemSeqId = orderItemSeqId;
    }

    public String getOrderItemSeqId() {
        return orderItemSeqId;
    }

    public void setShoppingList(String shoppingListId, String itemSeqId) {
        attributes.put("shoppingListId", shoppingListId);
        attributes.put("shoppingListItemSeqId", itemSeqId);
    }

    public String getShoppingListId() {
        if (attributes != null && attributes.containsKey("shoppingListId"))
            return (String) attributes.get("shoppingListId");
        return null;
    }

    public String getShoppingListItemSeqId() {
        if (attributes != null && attributes.containsKey("shoppingListItemSeqId"))
            return (String) attributes.get("shoppingListItemSeqId");
        return null;
    }

    /** Returns true if shipping charges apply to this item. */
    public boolean shippingApplies() {
        GenericValue product = getProduct();
        if (product != null) {
            return ProductWorker.shippingApplies(product);
        } else {
            // we don't ship non-product items
            return false;
        }
    }

    /** Returns true if tax charges apply to this item. */
    public boolean taxApplies() {
        GenericValue product = getProduct();
        if (product != null) {
            return ProductWorker.taxApplies(product);
        } else {
            // we do tax non-product items
            return true;
        }
    }

    /** Returns the item's productId. */
    public String getProductId() {
        return productId;
    }

    /** Returns the item's description. */
    public String getName() {
        GenericValue product = getProduct();
        
        if (product != null) {
            String productName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", this.locale);

            // if the productName is null or empty, see if there is an associated virtual product and get the productName of that product
            if (UtilValidate.isEmpty(productName)) {
                GenericValue parentProduct = this.getParentProduct();
                if (parentProduct != null) {
                    productName = ProductContentWrapper.getProductContentAsText(parentProduct, "PRODUCT_NAME", this.locale);
                }
            }

            if (productName == null) {
                return "";
            } else {
                return productName;
            }
        } else {
            return itemDescription;
        }
    }

    /** Returns the item's description. */
    public String getDescription() {
        GenericValue product = getProduct();
        
        if (product != null) {
            String description = ProductContentWrapper.getProductContentAsText(product, "DESCRIPTION", this.locale);

            // if the description is null or empty, see if there is an associated virtual product and get the description of that product
            if (UtilValidate.isEmpty(description)) {
                GenericValue parentProduct = this.getParentProduct();
                if (parentProduct != null) {
                    description = ProductContentWrapper.getProductContentAsText(parentProduct, "DESCRIPTION", this.locale);
                }
            }

            if (description == null) {
                return "";
            } else {
                return description;
            }
        } else {
            return null;
        }
    }

    public ProductConfigWrapper getConfigWrapper() {
        return configWrapper;
    }
    
    /** Returns the item's unit weight */
    public double getWeight() {
        GenericValue product = getProduct();
        if (product != null) {
            Double weight = product.getDouble("weight");

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
        } else {
            // non-product items have 0 weight
            return 0;
        }
    }

    /** Returns the item's pieces included */
    public long getPiecesIncluded() {
        GenericValue product = getProduct();
        if (product != null) {
            Long pieces = product.getLong("piecesIncluded");

            // if the piecesIncluded is null, see if there is an associated virtual product and get the piecesIncluded of that product
            if (pieces == null) {
                GenericValue parentProduct = this.getParentProduct();
                if (parentProduct != null) pieces = parentProduct.getLong("piecesIncluded");
            }

            if (pieces == null) {
                return 1;
            } else {
                return pieces.longValue();
            }
        } else {
            // non-product item assumed 1 piece
            return 1;
        }
    }

    /** Returns a Set of the item's features */
    public Set getFeatureSet() {
        Set featureSet = new ListOrderedSet();
        GenericValue product = this.getProduct();
        if (product != null) {
            List featureAppls = null;
            try {
                featureAppls = product.getRelated("ProductFeatureAppl");
                List filterExprs = UtilMisc.toList(new EntityExpr("productFeatureApplTypeId", EntityOperator.EQUALS, "STANDARD_FEATURE"));
                filterExprs.add(new EntityExpr("productFeatureApplTypeId", EntityOperator.EQUALS, "REQUIRED_FEATURE"));
                featureAppls = EntityUtil.filterByOr(featureAppls, filterExprs);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Unable to get features from product : " + product.get("productId"), module);
            }
            if (featureAppls != null) {
                Iterator fai = featureAppls.iterator();
                while (fai.hasNext()) {
                    GenericValue appl = (GenericValue) fai.next();
                    featureSet.add(appl.getString("productFeatureId"));
                }
            }
        }
        if (this.additionalProductFeatureAndAppls != null) {
            Iterator aapi = this.additionalProductFeatureAndAppls.values().iterator();
            while (aapi.hasNext()) {
                GenericValue appl = (GenericValue) aapi.next();
                featureSet.add(appl.getString("productFeatureId"));
            }
        }
        return featureSet;
    }

    /** Returns the item's size (height * width * depth) */
    public double getSize() {
        GenericValue product = getProduct();
        if (product != null) {
            Double height = product.getDouble("productHeight");
            Double width = product.getDouble("productWidth");
            Double depth = product.getDouble("productDepth");

            // if all are null, see if there is an associated virtual product and get the info of that product
            if (height == null & width == null && depth == null) {
                GenericValue parentProduct = this.getParentProduct();
                if (parentProduct != null) {
                    height = parentProduct.getDouble("productHeight");
                    width = product.getDouble("productWidth");
                    depth = product.getDouble("productDepth");
                }
            }

            if (height == null) height = new Double(0);
            if (width == null) width = new Double(0);
            if (depth == null) depth = new Double(0);

            double size = height.doubleValue() * width.doubleValue() * depth.doubleValue();
            return size;
        } else {
            // non-product items have 0 size
            return 0;
        }
    }

    public Map getItemProductInfo() {
        Map itemInfo = new HashMap();
        itemInfo.put("productId", this.getProductId());
        itemInfo.put("quantity", new Double(this.getQuantity()));
        itemInfo.put("weight", new Double(this.getWeight()));
        itemInfo.put("size",  new Double(this.getSize()));
        itemInfo.put("piecesIncluded", new Long(this.getPiecesIncluded()));
        itemInfo.put("featureSet", this.getFeatureSet());
        return itemInfo;
    }

    /** Returns the base price. */
    public double getBasePrice() {
        if (selectedAmount > 0) {
            return basePrice * selectedAmount;
        } else {
            return basePrice;
        }
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

    public void addAllProductFeatureAndAppls(Map productFeatureAndApplsToAdd) {
        if (productFeatureAndApplsToAdd == null) return;
        Iterator productFeatureAndApplsToAddIter = productFeatureAndApplsToAdd.values().iterator();

        while (productFeatureAndApplsToAddIter.hasNext()) {
            GenericValue additionalProductFeatureAndAppl = (GenericValue) productFeatureAndApplsToAddIter.next();

            this.putAdditionalProductFeatureAndAppl(additionalProductFeatureAndAppl);
        }
    }

    public void putAdditionalProductFeatureAndAppl(GenericValue additionalProductFeatureAndAppl) {
        if (additionalProductFeatureAndAppl == null) return;

        // if one already exists with the given type, remove it with the corresponding adjustment
        removeAdditionalProductFeatureAndAppl(additionalProductFeatureAndAppl.getString("productFeatureTypeId"));

        // adds to additional map and creates an adjustment with given price
        String featureType = additionalProductFeatureAndAppl.getString("productFeatureTypeId");
        this.additionalProductFeatureAndAppls.put(featureType, additionalProductFeatureAndAppl);

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

        //if (this.additionalProductFeatureAndAppls.size() == 0) this.additionalProductFeatureAndAppls = null;

        return oldAdditionalProductFeatureAndAppl;
    }

    public Map getAdditionalProductFeatureAndAppls() {
        return this.additionalProductFeatureAndAppls;
    }

    public Map getFeatureIdQtyMap() {
        Map featureMap = new HashMap();
        GenericValue product = this.getProduct();
        if (product != null) {
            List featureAppls = null;
            try {
                featureAppls = product.getRelated("ProductFeatureAppl");
                List filterExprs = UtilMisc.toList(new EntityExpr("productFeatureApplTypeId", EntityOperator.EQUALS, "STANDARD_FEATURE"));
                filterExprs.add(new EntityExpr("productFeatureApplTypeId", EntityOperator.EQUALS, "REQUIRED_FEATURE"));
                featureAppls = EntityUtil.filterByOr(featureAppls, filterExprs);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Unable to get features from product : " + product.get("productId"), module);
            }
            if (featureAppls != null) {
                Iterator fai = featureAppls.iterator();
                while (fai.hasNext()) {
                    GenericValue appl = (GenericValue) fai.next();
                    Double lastQuantity = (Double) featureMap.get(appl.getString("productFeatureId"));
                    if (lastQuantity == null) {
                        lastQuantity = new Double(0);
                    }
                    Double newQuantity = new Double(lastQuantity.doubleValue() + this.getQuantity());
                    featureMap.put(appl.getString("productFeatureId"), newQuantity);
                }
            }
        }
        if (this.additionalProductFeatureAndAppls != null) {
            Iterator aapi = this.additionalProductFeatureAndAppls.values().iterator();
            while (aapi.hasNext()) {
                GenericValue appl = (GenericValue) aapi.next();
                Double lastQuantity = (Double) featureMap.get(appl.getString("productFeatureId"));
                if (lastQuantity == null) {
                    lastQuantity = new Double(0);
                }
                Double newQuantity = new Double(lastQuantity.doubleValue() + this.getQuantity());
                featureMap.put(appl.getString("productFeatureId"), newQuantity);
            }
        }
        return featureMap;
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

    public List getAlternativeOptionProductIds() {
        return this.alternativeOptionProductIds;
    }
    public void setAlternativeOptionProductIds(List alternativeOptionProductIds) {
        this.alternativeOptionProductIds = alternativeOptionProductIds;
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
        return this.equals(item.getProductId(), item.additionalProductFeatureAndAppls, item.attributes, item.prodCatalogId, item.selectedAmount, item.getIsPromo());
    }

    /** Compares the specified object with this cart item. Defaults isPromo to false. */
    public boolean equals(String productId, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, double selectedAmount) {
        return equals(productId, additionalProductFeatureAndAppls, attributes, prodCatalogId, selectedAmount, false);
    }

    /** Compares the specified object with this cart item. Defaults isPromo to false. */
    public boolean equals(String productId, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, double selectedAmount) {
        return equals(productId, additionalProductFeatureAndAppls, attributes, prodCatalogId, selectedAmount, configWrapper, false);
    }

    /** Compares the specified object with this cart item. Defaults isPromo to false. */
    public boolean equals(String productId, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, double selectedAmount, boolean isPromo) {
        return equals(productId, additionalProductFeatureAndAppls, attributes, prodCatalogId, selectedAmount, null, isPromo);
    }
    
    /** Compares the specified object with this cart item. */
    public boolean equals(String productId, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, double selectedAmount, ProductConfigWrapper configWrapper, boolean isPromo) {
        if (this.productId == null || productId == null) {
            // all non-product items are unique
            return false;
        }
        if (!this.productId.equals(productId)) {
            return false;
        }

        if ((this.prodCatalogId == null && prodCatalogId != null) || (this.prodCatalogId != null && prodCatalogId == null)) {
            return false;
        }
        if (this.prodCatalogId != null && prodCatalogId != null && !this.prodCatalogId.equals(prodCatalogId)) {
            return false;
        }

        if (this.getSelectedAmount() != selectedAmount) {
            return false;
        }

        if (this.isPromo != isPromo) {
            return false;
        }

        if ((this.additionalProductFeatureAndAppls != null && additionalProductFeatureAndAppls != null) &&
                (this.additionalProductFeatureAndAppls.size() != additionalProductFeatureAndAppls.size()) &&
                !(this.additionalProductFeatureAndAppls.equals(additionalProductFeatureAndAppls))) {
            return false;
        }

        if ((this.attributes != null && attributes != null) &&
                ( (this.attributes.size() != attributes.size()) ||
                !(this.attributes.equals(attributes)) )) {
            return false;
        }

        if (configWrapper != null && !configWrapper.equals(this.configWrapper)) {
            return false;
        }
        
        return true;
    }

    /** Gets the Product entity. If it is not already retreived gets it from the delegator */
    public GenericValue getProduct() {
        if (this._product != null) {
            return this._product;
        }
        if (this.productId != null) {
            try {
                this._product = this.getDelegator().findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            } catch (GenericEntityException e) {
                throw new RuntimeException("Entity Engine error getting Product (" + e.getMessage() + ")");
            }
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

    public String getParentProductId() {
        GenericValue parentProduct = this.getParentProduct();
        if (parentProduct != null) {
            return parentProduct.getString("productId");
        } else {
            return null;
        }
    }

    public Map getOptionalProductFeatures() {
        if (_product != null) {
            return ProductWorker.getOptionalProductFeatures(getDelegator(), this.productId);
        } else {
            // non-product items do not have features
            return new HashMap();
        }
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
                Debug.logInfo("Clone's adj: " + item.getAdjustments(), module);
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
                            Debug.logInfo("Cloned adj: " + newAdjustment, module);
                            item.addAdjustment(newAdjustment);
                        } else {
                            Debug.logInfo("Clone Adjustment is null", module);
                        }
                    }
                }
                newItems.add(item);
            }

            // set this item's quantity
            this.setQuantity(1, dispatcher, cart, false);

            Debug.logInfo("BaseQuantity: " + baseQuantity, module);
            Debug.logInfo("Item's Adj: " + this.getAdjustments(), module);

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
                        Debug.logInfo("Updated adj: " + newAdjustment, module);
                        this.addAdjustment(newAdjustment);
                    }
                }
            }

            // add the cloned item(s) to the cart
            Iterator newItemsItr = newItems.iterator();

            while (newItemsItr.hasNext()) {
                cart.addItem(thisIndex, (ShoppingCartItem) newItemsItr.next());
            }
        }
    }
}
