/*
 * $Id$
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
package org.ofbiz.commonapp.order.shoppingcart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.commonapp.order.order.OrderReadHelper;
import org.ofbiz.commonapp.product.catalog.CatalogWorker;
import org.ofbiz.commonapp.product.category.CategoryWorker;
import org.ofbiz.core.entity.EntityUtil;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilDateTime;
import org.ofbiz.core.util.UtilFormatOut;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.UtilValidate;

/**
 * <p><b>Title:</b> ShoppingCartItem.java
 * <p><b>Description:</b> Shopping cart item object.
 *
 * @author     <a href="mailto:jaz@ofbiz.org.com">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ShoppingCartItem implements java.io.Serializable {

    public static String module = ShoppingCartItem.class.getName();
    public static String[] attributeNames = { "shoppingListId", "shoppingListItemSeqId" };
    
    private transient GenericDelegator delegator = null;
    private transient GenericValue _product = null;       // the actual product    
    private transient GenericValue _parentProduct = null; // the virtual product 

    private String delegatorName = null;
    private String prodCatalogId = null;
    private String productId = null;
    private String itemType = null;
    private String itemComment = null;
    private String productCategoryId = null;
    private String itemDescription = null;  // special field for non-product items
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

    /** 
     * Makes a ShoppingCartItem and adds it to the cart.
     * NOTE: This method will get the product entity and check to make sure it can be purchased.
     * 
     * @param cartLocation The location to place this item; null will place at the end
     * @param delegator GenericDelegator used to lookup the product
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
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericDelegator delegator, String productId, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException {
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
            String excMsg = "Product not found, not adding to cart. [productId: " + productId + "]";

            Debug.logWarning(excMsg, module);
            throw new CartItemModifyException(excMsg);
        }        

        return makeItem(cartLocation, product, quantity, additionalProductFeatureAndAppls, attributes, prodCatalogId, dispatcher, cart, true);
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
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        ShoppingCartItem newItem = new ShoppingCartItem(product, additionalProductFeatureAndAppls, attributes, prodCatalogId);

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
        return newItem;
    }
    
    /**
     * Makes a non-product ShoppingCartItem and adds it to the cart. 
     * NOTE: This is only for non-product items; items without a product entity (work items, bulk items, etc)
     * 
     * @param cartLocation The location to place this item; null will place at the end
     * @param delegator Delegator used for this item
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
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericDelegator delegator, String itemType, String itemDescription, String productCategoryId, double basePrice, double quantity, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart, boolean doPromotions) throws CartItemModifyException {
        ShoppingCartItem newItem = new ShoppingCartItem(delegator, itemType, itemDescription, productCategoryId, basePrice, attributes, prodCatalogId);
        
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
        try {
            this._product = item.getProduct();
        } catch (IllegalStateException e) {
            this._product = null;
        }
        this.delegator = item.getDelegator();      
        this.delegatorName = delegator.getDelegatorName();
        this.prodCatalogId = item.getProdCatalogId();
        this.productId = item.getProductId();
        this.itemType = item.getItemType();
        this.itemComment = item.getItemComment(); 
        this.productCategoryId = item.getProductCategoryId();               
        this.quantity = item.getQuantity();
        this.basePrice = item.getBasePrice();
        this.listPrice = item.getListPrice();
        this.isPromo = item.getIsPromo();
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
    protected ShoppingCartItem(GenericValue product, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId) {
        this._product = product;
        this.productId = _product.getString("productId");
        this.itemType = "PRODUCT_ORDER_ITEM";
        this.prodCatalogId = prodCatalogId;
        this.itemComment = null;
        this.attributes = attributes;
        this.delegator = _product.getDelegator();
        this.delegatorName = _product.getDelegator().getDelegatorName();
        this.orderShipmentPreference = delegator.makeValue("OrderShipmentPreference", null);
        this.addAllProductFeatureAndAppls(additionalProductFeatureAndAppls);
    }
    
    /** Creates new ShopingCartItem object. */
    protected ShoppingCartItem(GenericDelegator delegator, String itemTypeId, String description, String categoryId, double basePrice, Map attributes, String prodCatalogId) {
        this.delegator = delegator;      
        this.itemType = itemTypeId;
        this.itemDescription = description;
        this.productCategoryId = categoryId;
        this.basePrice = basePrice;
        this.attributes = attributes;
        this.prodCatalogId = prodCatalogId;
        this.delegatorName = delegator.getDelegatorName();
    }

    public String getProdCatalogId() {
        return this.prodCatalogId;
    }
    
    /** Sets the base price for the item; use with caution */
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice; 
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
            throw new CartItemModifyException("Sorry, you can't change the quantity on the promotion item " + this.getName() + " (product ID: " + productId + "), not setting quantity.");
        }

        // check inventory if new quantity is greater than old quantity; don't worry about inventory getting pulled out from under, that will be handled at checkout time
        if (_product != null && quantity > this.quantity) {
            String productStoreId = cart.getProductStoreId();
            
            if (org.ofbiz.commonapp.product.store.ProductStoreWorker.isStoreInventoryRequired(productStoreId, this.getProduct(), this.getDelegator())) {
            	if (!org.ofbiz.commonapp.product.store.ProductStoreWorker.isStoreInventoryAvailable(productStoreId, productId, quantity, this.getDelegator(), dispatcher)) {
                    String excMsg = "Sorry, we do not have enough (you tried " + UtilFormatOut.formatQuantity(quantity) + ") of the product " + this.getName() + " (product ID: " + productId + ") in stock, not adding to cart. Please try a lower quantity, try again later, or call customer service for more information.";

                    Debug.logWarning(excMsg, module);
                    throw new CartItemModifyException(excMsg);
                }
            }
        }

        // set quantity before promos so order total, etc will be updated
        double oldQuantity = this.quantity;

        this.quantity = quantity;
        this.updatePrice(dispatcher, cart);

        // apply/unapply promotions - only for sales orders
        if (doPromotions && cart.getOrderType().equals("SALES_ORDER")) {
            org.ofbiz.commonapp.product.promo.ProductPromoWorker.doPromotions(cart, this, oldQuantity, getDelegator(), dispatcher);
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
                if (partyId != null)
                	priceContext.put("partyId", partyId);
                
                priceContext.put("quantity", new Double(this.getQuantity()));
                Map priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
            
                if (ModelService.RESPOND_ERROR.equals(priceResult.get(ModelService.RESPONSE_MESSAGE))) {
                    throw new CartItemModifyException("There was an error while calculating the price: " + priceResult.get(ModelService.ERROR_MESSAGE));
                }
                
                if (priceResult.get("listPrice") != null) this.listPrice = ((Double) priceResult.get("listPrice")).doubleValue();
                if (cart.getOrderType().equals("PURCHASE_ORDER")) {
                    if (priceResult.get("averageCost") != null) 
                        this.basePrice = ((Double) priceResult.get("averageCost")).doubleValue();
                } else {                
                    if (priceResult.get("price") != null) 
                        this.basePrice = ((Double) priceResult.get("price")).doubleValue();
                }
                
                this.orderItemPriceInfos = (List) priceResult.get("orderItemPriceInfos");
            } catch (GenericServiceException e) {
                throw new CartItemModifyException("There was an error while calculating the price", e);
            }
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
        GenericValue itemType = null;
        try {
            GenericDelegator delegator = this.getDelegator();
            itemType = this.getDelegator().findByPrimaryKeyCache("OrderItemType", UtilMisc.toMap("orderItemTypeId", this.getItemType()));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting OrderItemType for: " + this.getItemType(), module);
        }
        if (itemType != null)
            return itemType.getString("description");
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
        if (_product != null) {        
            Boolean shipCharge = getProduct().getBoolean("chargeShipping");
    
            if (shipCharge == null) {
                return true;
            } else {
                return shipCharge.booleanValue();
            }
        } else {
            // we don't ship non-product items
            return false;
        }
    }

    /** Returns true if tax charges apply to this item. */
    public boolean taxApplies() {
        if (_product != null) {        
            Boolean taxable = getProduct().getBoolean("taxable");
    
            if (taxable == null) {
                return true;
            } else {
                return taxable.booleanValue();
            }
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
        if (_product != null) {       
            String productName = getProduct().getString("productName");
            
            // if the productName is null or empty, see if there is an associated virtual product and get the productName of that product
            if (UtilValidate.isEmpty(productName)) {
                GenericValue parentProduct = this.getParentProduct();
                if (parentProduct != null) productName = parentProduct.getString("productName");
            }
            
            return productName;
        } else {
            return itemDescription;
        }
    }

    /** Returns the item's description. */
    public String getDescription() {
        if (_product != null) {        
            String description = getProduct().getString("description");
            
            // if the description is null or empty, see if there is an associated virtual product and get the description of that product
            if (UtilValidate.isEmpty(description)) {
                GenericValue parentProduct = this.getParentProduct();
                if (parentProduct != null) description = parentProduct.getString("description");
            }
            
            return description;
        } else {
            return null;
        }
    }

    /** Returns the item's unit weight */
    public double getWeight() {
        if (_product != null) {        
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
        } else {
            // non-product items have 0 weight
            return 0;
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
        if (this.productId == null || productId == null) {
            // all non-product items are unique
            return false; 
        }
        
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

            while (newItemsItr.hasNext())
                cart.addItem(thisIndex, (ShoppingCartItem) newItemsItr.next());
        }
    }
}
