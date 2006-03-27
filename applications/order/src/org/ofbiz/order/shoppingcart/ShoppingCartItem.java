/*
 * $Id$
 *
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.set.ListOrderedSet;
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
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.order.shoppinglist.ShoppingListEvents;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import javolution.util.FastMap;

/**
 * <p><b>Title:</b> ShoppingCartItem.java
 * <p><b>Description:</b> Shopping cart item object.
 *
 * @author     <a href="mailto:jaz@ofbiz.org.com">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.0
 */
public class ShoppingCartItem implements java.io.Serializable {

    public static String module = ShoppingCartItem.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    public static String[] attributeNames = { "shoppingListId", "shoppingListItemSeqId", "surveyResponses",
                                              "itemDesiredDeliveryDate", "itemComment"};

    private transient GenericDelegator delegator = null;
    private transient GenericValue _product = null;       // the actual product
    private transient GenericValue _parentProduct = null; // the virtual product

    private String delegatorName = null;
    private String prodCatalogId = null;
    private String productId = null;
    private String itemType = null;            // ends up in orderItemTypeId
    private String productCategoryId = null;
    private String itemDescription = null;
    private Timestamp reservStart = null;      // for reservations: date start
    private double reservLength = 0;           // for reservations: length
    private double reservPersons = 0;          // for reservations: number of persons using
    private double quantity = 0.0;
    private double basePrice = 0.0;
    private Double displayPrice = null;
    private double reserv2ndPPPerc = 0.0;      // for reservations: extra % 2nd person
    private double reservNthPPPerc = 0.0;      // for reservations: extra % Nth person
    private double listPrice = 0.0;
    private boolean isModifiedPrice = false;   // flag to know if the price have been modified
    private double selectedAmount = 0.0;
    private String requirementId = null;
    private String quoteId = null;
    private String quoteItemSeqId = null;
    private String statusId = null;
    private Map orderItemAttributes = null;
    private Map attributes = null;
    private String orderItemSeqId = null;
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
    private List featuresForSupplier = new LinkedList();

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
     * @param selectedAmount ?
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
        return ShoppingCartItem.makeItem(cartLocation, productId, selectedAmount, quantity, null, 0.00, 0.00, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, dispatcher, cart);
    }

    /**
     * Makes a ShoppingCartItem and adds it to the cart.
     * NOTE: This method will get the product entity and check to make sure it can be purchased.
     *
     * @param cartLocation The location to place this item; null will place at the end
     * @param productId The primary key of the product being added
     * @param selectedAmount ?
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
    public static ShoppingCartItem makeItem(Integer cartLocation, String productId, double selectedAmount, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps) throws CartItemModifyException, ItemNotFoundException {
        return ShoppingCartItem.makeItem(cartLocation, productId, selectedAmount, quantity, null, 0.00, 0.00, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, dispatcher, cart, triggerExternalOps);
    }

    /**
     * Makes a ShoppingCartItem and adds it to the cart.
     * NOTE: This method will get the product entity and check to make sure it can be purchased.
     *
     * @param cartLocation The location to place this item; null will place at the end
     * @param productId The primary key of the product being added
     * @param selectedAmount ?
     * @param quantity The quantity to add
     * @param reservStart start of the reservation
     * @param reservLength length of the reservation
     * @param reservPersons nbr of persons taking advantage of the reservation
     * @param additionalProductFeatureAndAppls Product feature/appls map
     * @param attributes All unique attributes for this item (NOT features)
     * @param prodCatalogId The catalog this item was added from
     * @param configWrapper The product configuration wrapper (null if the product is not configurable)
     * @param dispatcher LocalDispatcher object for doing promotions, etc
     * @param cart The parent shopping cart object this item will belong to
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, String productId, double selectedAmount, double quantity, Timestamp reservStart, double reservLength, double reservPersons, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException, ItemNotFoundException {
        return ShoppingCartItem.makeItem(cartLocation, productId, selectedAmount, quantity, reservStart, reservLength, reservPersons, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, dispatcher, cart, true);
    }

    public static ShoppingCartItem makeItem(Integer cartLocation, String productId, double selectedAmount, double quantity, Timestamp reservStart, double reservLength, double reservPersons, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps) throws CartItemModifyException, ItemNotFoundException {
        return ShoppingCartItem.makeItem(cartLocation, productId, selectedAmount, quantity, 0.00, reservStart, reservLength, reservPersons, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, dispatcher, cart, triggerExternalOps, true);
    }
    /**
     * Makes a ShoppingCartItem and adds it to the cart.
     * NOTE: This method will get the product entity and check to make sure it can be purchased.
     *
     * @param cartLocation The location to place this item; null will place at the end
     * @param productId The primary key of the product being added
     * @param selectedAmount ?
     * @param quantity The quantity to add
     * @param reservStart start of the reservation
     * @param reservLength length of the reservation
     * @param reservPersons nbr of persons taking advantage of the reservation
     * @param additionalProductFeatureAndAppls Product feature/appls map
     * @param attributes All unique attributes for this item (NOT features)
     * @param prodCatalogId The catalog this item was added from
     * @param configWrapper The product configuration wrapper (null if the product is not configurable)
     * @param dispatcher LocalDispatcher object for doing promotions, etc
     * @param cart The parent shopping cart object this item will belong to
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, String productId, double selectedAmount, double quantity, double unitPrice, Timestamp reservStart, double reservLength, double reservPersons, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps, boolean triggerProductPrice) throws CartItemModifyException, ItemNotFoundException {
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

    return makeItem(cartLocation, product, selectedAmount, quantity, unitPrice, reservStart, reservLength, reservPersons, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, dispatcher, cart, triggerExternalOps, triggerProductPrice);
}

    /**
     * Makes a ShoppingCartItem for a purchase order item and adds it to the cart.
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
     * @param supplierProduct GenericValue of SupplierProduct entity, containing product description and prices
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makePurchaseOrderItem(Integer cartLocation, String productId, double selectedAmount, double quantity, Map additionalProductFeatureAndAppls, Map attributes,
                                                         String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart, GenericValue supplierProduct) throws CartItemModifyException, ItemNotFoundException {
        GenericDelegator delegator = cart.getDelegator();
        GenericValue product = null;

        try {
            product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
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
        ShoppingCartItem newItem = new ShoppingCartItem(product, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, cart.getLocale());

        // check to see if product is virtual
        if ("Y".equals(product.getString("isVirtual"))) {
            String excMsg = "Tried to add the Virtual Product " + product.getString("productName") +
                " (productId: " + product.getString("productId") + ") to the cart, not adding.";

            Debug.logWarning(excMsg, module);
            throw new CartItemModifyException(excMsg);
        }

        // Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

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

        if (selectedAmount > 0) {
            newItem.setSelectedAmount(selectedAmount);
        }

        try {
            newItem.setQuantity(quantity, dispatcher, cart, true);
        } catch (CartItemModifyException e) {
            cart.removeCartItem(cart.getItemIndex(newItem), dispatcher);
            cart.clearItemShipInfo(newItem);
            cart.removeEmptyCartItems();
            throw e;
        }

        // specific for purchase orders - description is set to supplierProductId + supplierProductName, price set to lastPrice of SupplierProduct
        // if supplierProduct has no supplierProductName, use the regular supplierProductId
        if (supplierProduct != null) {
            newItem.setName(getPurchaseOrderItemDescription(product, supplierProduct, cart.getLocale()));
            newItem.setBasePrice(supplierProduct.getDouble("lastPrice").doubleValue());
        } else {
            newItem.setName(product.getString("internalName"));
        }
        return newItem;

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
     * @param triggerExternalOps Indicates if we should run external operations (promotions, auto-save, etc)
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double selectedAmount, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps) throws CartItemModifyException {
        return ShoppingCartItem.makeItem(cartLocation, product, selectedAmount, quantity, additionalProductFeatureAndAppls, attributes, prodCatalogId, null, dispatcher, cart, triggerExternalOps);
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
     * @param triggerExternalOps Indicates if we should run external operations (promotions, auto-save, etc)
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double selectedAmount, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps) throws CartItemModifyException {
        return ShoppingCartItem.makeItem(cartLocation, product, selectedAmount, quantity, null, 0.00, 0.00, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, dispatcher, cart, triggerExternalOps);
    }

    /**
     * Makes a ShoppingCartItem and adds it to the cart.
     * WARNING: This method does not check if the product is in a purchase category.
     * rental fields were added.
     *
     * @param cartLocation The location to place this item; null will place at the end
     * @param product The product entity relating to the product being added
     * @param quantity The quantity to add
     * @param reservStart the start of the reservation
     * @param reservLength the reservation length
     * @param reservPersons the number of persons using the reservation
     * @param additionalProductFeatureAndAppls Product feature/appls map
     * @param attributes All unique attributes for this item (NOT features)
     * @param prodCatalogId The catalog this item was added from
     * @param configWrapper The product configuration wrapper (null if the product is not configurable)
     * @param dispatcher LocalDispatcher object for doing promotions, etc
     * @param cart The parent shopping cart object this item will belong to
     * @param triggerExternalOps Indicates if we should run external operations (promotions, auto-save, etc)
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    //public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double selectedAmount, double quantity, Timestamp reservStart, double reservLength, double reservPersons, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps) throws CartItemModifyException {
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double selectedAmount, double quantity, double unitPrice, Timestamp reservStart, double reservLength, double reservPersons, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps, boolean triggerPriceRules) throws CartItemModifyException {
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


        // check to see if the product is a rental item
        if ("ASSET_USAGE".equals(product.getString("productTypeId"))) {
            if (reservStart == null)    {
                String excMsg = "The starting date of the reservation is missing....";
                throw new CartItemModifyException(excMsg);
            }

            if (reservStart.before(UtilDateTime.nowTimestamp()))    {
                String excMsg = "You can only make reservation starting tomorrow....";
                throw new CartItemModifyException(excMsg);
            }
            newItem.setReservStart(reservStart);

            if (reservLength < 1)    {
                String excMsg = "Please enter a number of days, 1, or more....";
                throw new CartItemModifyException(excMsg);
            }
            newItem.setReservLength(reservLength);

            if (product.get("reservMaxPersons") != null) {
                 double reservMaxPersons = product.getDouble("reservMaxPersons").doubleValue();
                 if (reservMaxPersons < reservPersons)    {
                     String excMsg = "The maximum number of persons renting this object is " + product.getString("reservMaxPersons") + " however you have requested: " + reservPersons + " !";
                     Debug.logInfo(excMsg,module);
                     throw new CartItemModifyException(excMsg);
                 }
             }
             newItem.setReservPersons(reservPersons);

             if (product.get("reserv2ndPPPerc") != null)
                 newItem.setReserv2ndPPPerc(product.getDouble("reserv2ndPPPerc").doubleValue());

             if (product.get("reservNthPPPerc") != null)
                 newItem.setReservNthPPPerc(product.getDouble("reservNthPPPerc").doubleValue());

            // check to see if the related fixed asset is available for rent
            String isAvailable = checkAvailability(product.getString("productId"), quantity, reservStart, reservLength, cart);
            if(isAvailable.compareTo("OK") != 0) {
                String excMsg = "Product not available, ProductId:" + product.getString("productId") + " message:" + isAvailable.toString();
                Debug.logInfo(excMsg, module);
                throw new CartItemModifyException(isAvailable);
            }
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

        // set the product unit price as base price
        // if triggerPriceRules is true this price will be overriden
        newItem.setBasePrice(unitPrice);

        // add to cart before setting quantity so that we can get order total, etc
        if (cartLocation == null) {
            cart.addItemToEnd(newItem);
        } else {
            cart.addItem(cartLocation.intValue(), newItem);
        }

        // We have to set the selectedAmount before calling setQuantity because
        // selectedAmount changes the item's base price (used in the updatePrice
        // method called inside the setQuantity method)
        if (selectedAmount > 0) {
            newItem.setSelectedAmount(selectedAmount);
        }

        try {
            newItem.setQuantity(quantity, dispatcher, cart, triggerExternalOps, true, triggerPriceRules);
        } catch (CartItemModifyException e) {
            cart.removeCartItem(cart.getItemIndex(newItem), dispatcher);
            cart.clearItemShipInfo(newItem);
            cart.removeEmptyCartItems();
            throw e;
        }

        return newItem;
    }

    // Calls makeItem(...) setting a default value, 0.00, for the selectedAmount
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double quantity, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps) throws CartItemModifyException {
        return makeItem(cartLocation, product, 0.00, quantity, additionalProductFeatureAndAppls, attributes, prodCatalogId, dispatcher, cart, triggerExternalOps);
    }

    // Calls makeItem(...) setting a default value, 0.00, for unitPrice and setting the triggerProductPrice to true;
    // in this way, the prices and price rules are automatically set by the updatePrice(...) method
    public static ShoppingCartItem makeItem(Integer cartLocation, GenericValue product, double selectedAmount, double quantity, Timestamp reservStart, double reservLength, double reservPersons, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps) throws CartItemModifyException {
        return makeItem(cartLocation, product, selectedAmount, quantity, 0.0, reservStart, reservLength, reservPersons, additionalProductFeatureAndAppls, attributes, prodCatalogId, configWrapper, dispatcher, cart, triggerExternalOps, true);
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
     * @param selectedAmount
     * @param quantity The quantity to add
     * @param attributes All unique attributes for this item (NOT features)
     * @param prodCatalogId The catalog this item was added from
     * @param dispatcher LocalDispatcher object for doing promotions, etc
     * @param cart The parent shopping cart object this item will belong to
     * @param triggerExternalOps Indicates if we should run external operations (promotions, auto-save, etc)
     * @return a new ShoppingCartItem object
     * @throws CartItemModifyException
     */
    public static ShoppingCartItem makeItem(Integer cartLocation, String itemType, String itemDescription, String productCategoryId, double basePrice, double selectedAmount, double quantity, Map attributes, String prodCatalogId, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps) throws CartItemModifyException {
        GenericDelegator delegator = cart.getDelegator();
        ShoppingCartItem newItem = new ShoppingCartItem(delegator, itemType, itemDescription, productCategoryId, basePrice, attributes, prodCatalogId, cart.getLocale());

        // add to cart before setting quantity so that we can get order total, etc
        if (cartLocation == null) {
            cart.addItemToEnd(newItem);
        } else {
            cart.addItem(cartLocation.intValue(), newItem);
        }

        try {
            newItem.setQuantity(quantity, dispatcher, cart, triggerExternalOps);
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
        this.reservStart = item.getReservStart();
        this.reservLength = item.getReservLength();
        this.reservPersons = item.getReservPersons();
        this.selectedAmount = item.getSelectedAmount();
        this.setBasePrice(item.getBasePrice());
        this.setDisplayPrice(item.getDisplayPrice());
        this.listPrice = item.getListPrice();
        this.reserv2ndPPPerc = item.getReserv2ndPPPerc();
        this.reservNthPPPerc = item.getReservNthPPPerc();
        this.requirementId = item.getRequirementId();
        this.quoteId = item.getQuoteId();
        this.quoteItemSeqId = item.getQuoteItemSeqId();
        this.isPromo = item.getIsPromo();
        this.promoQuantityUsed = item.promoQuantityUsed;
        this.locale = item.locale;
        this.quantityUsedPerPromoCandidate = new HashMap(item.quantityUsedPerPromoCandidate);
        this.quantityUsedPerPromoFailed = new HashMap(item.quantityUsedPerPromoFailed);
        this.quantityUsedPerPromoActual = new HashMap(item.quantityUsedPerPromoActual);
        this.orderItemSeqId = item.getOrderItemSeqId();
        this.additionalProductFeatureAndAppls = item.getAdditionalProductFeatureAndAppls() == null ?
                null : new HashMap(item.getAdditionalProductFeatureAndAppls());
        this.attributes = item.getAttributes() == null ? null : new HashMap(item.getAttributes());
        this.contactMechIdsMap = item.getOrderItemContactMechIds() == null ? null : new HashMap(item.getOrderItemContactMechIds());
        this.orderItemPriceInfos = item.getOrderItemPriceInfos() == null ? null : new LinkedList(item.getOrderItemPriceInfos());
        this.itemAdjustments = item.getAdjustments() == null ? null : new LinkedList(item.getAdjustments());
        if (this._product == null) {
            this.itemDescription = item.getName();
        }
        if (item.configWrapper != null) {
            this.configWrapper = new ProductConfigWrapper(item.configWrapper);
        }
    }

    /** Cannot create shopping cart item with no parameters */
    protected ShoppingCartItem() {}

    /** Creates new ShoppingCartItem object. */
    protected ShoppingCartItem(GenericValue product, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, Locale locale) {
        this(product, additionalProductFeatureAndAppls, attributes, prodCatalogId, null, locale);
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
                this.itemDescription= "";
            } else {
                this.itemDescription= productName;
            }
        }
    }

    /** Creates new ShoppingCartItem object. */
    protected ShoppingCartItem(GenericValue product, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, Locale locale) {
        this._product = product;
        this.productId = _product.getString("productId");
        if (_product.getString("productTypeId").equals("ASSET_USAGE")) {
            this.itemType = "RENTAL_ORDER_ITEM";  // will create additional workeffort/asset usage records
        } else {
            this.itemType = "PRODUCT_ORDER_ITEM";
        }
        this.prodCatalogId = prodCatalogId;
        this.attributes = attributes;
        this.delegator = _product.getDelegator();
        this.delegatorName = _product.getDelegator().getDelegatorName();
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
        this.setBasePrice(basePrice);
        this.setDisplayPrice(basePrice);
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

    /** Sets the display price for the item; use with caution */
    public void setDisplayPrice(double displayPrice) {
        this.displayPrice = new Double(displayPrice);
    }

    /** Sets the extra % for second person */
    public void setReserv2ndPPPerc(double reserv2ndPPPerc) {
        this.reserv2ndPPPerc = reserv2ndPPPerc;
    }
    /** Sets the extra % for third and following person */
    public void setReservNthPPPerc(double reservNthPPPerc) {
        this.reservNthPPPerc = reservNthPPPerc;
    }
    /** Sets the reservation start date */
    public void setReservStart(Timestamp reservStart)    {
        this.reservStart = reservStart;
    }
    /** Sets the reservation length */
    public void setReservLength(double reservLength)    {
        this.reservLength = reservLength;
    }
    /** Sets number of persons using the reservation */
    public void setReservPersons(double reservPersons)    {
        this.reservPersons = reservPersons;
    }

    /** Sets the quantity for the item and validates the change in quantity, etc */
    public void setQuantity(double quantity, LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException {
        this.setQuantity(quantity, dispatcher, cart, true);
    }

    /** Sets the quantity for the item and validates the change in quantity, etc */
    public void setQuantity(double quantity, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps) throws CartItemModifyException {
        this.setQuantity(quantity, dispatcher, cart, triggerExternalOps, true);
    }

    /** Sets the quantity for the item and validates the change in quantity, etc */
    public void setQuantity(double quantity, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps, boolean resetShipGroup) throws CartItemModifyException {
        this.setQuantity((int) quantity, dispatcher, cart, triggerExternalOps, resetShipGroup, true);
    }

    /** Sets the quantity for the item and validates the change in quantity, etc */
    public void setQuantity(double quantity, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps, boolean resetShipGroup, boolean updateProductPrice) throws CartItemModifyException {
        this.setQuantity((int) quantity, dispatcher, cart, triggerExternalOps, resetShipGroup, updateProductPrice);
    }

    /** returns "OK" when the product can be booked or returns a string with the dates the related fixed Asset is not available */
    public static String checkAvailability(String productId, double quantity, Timestamp reservStart, double reservLength, ShoppingCart cart) {
        GenericDelegator delegator = cart.getDelegator();
        // find related fixedAsset
        List selFixedAssetProduct = null;
        GenericValue fixedAssetProduct = null;
        try {
            List allFixedAssetProduct = delegator.findByAnd("FixedAssetProduct", UtilMisc.toMap("productId", productId, "fixedAssetProductTypeId", "FAPT_USE"));
            selFixedAssetProduct = EntityUtil.filterByDate(allFixedAssetProduct, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
        } catch (GenericEntityException e) {
            return "Could not find a related Fixed Asset for the product: " + productId;
        }
        if (selFixedAssetProduct != null && selFixedAssetProduct.size() > 0) {
            Iterator firstOne = selFixedAssetProduct.iterator();
            fixedAssetProduct = (GenericValue) firstOne.next();
        } else {
            return "Could not find a related Fixed Asset for the product: " + productId;
        }

        // find the fixed asset itself
        GenericValue fixedAsset = null;
        try {
            fixedAsset = fixedAssetProduct.getRelatedOne("FixedAsset");
        } catch (GenericEntityException e) {
            return "fixed_Asset_not_found. Fixed AssetId: " + fixedAssetProduct.getString("fixedAssetId");
        }
        if (fixedAsset == null) {
            return "fixed_Asset_not_found. Fixed AssetId: " + fixedAssetProduct.getString("fixedAssetId");
        }
        //Debug.logInfo("Checking availability for product: " + productId.toString() + " and related FixedAsset: " + fixedAssetProduct.getString("fixedAssetId"),module);

        // see if this fixed asset has a calendar, when no create one and attach to fixed asset
        // DEJ20050725 this isn't being used anywhere, commenting out for now and not assigning from the getRelatedOne: GenericValue techDataCalendar = null;
        try {
            fixedAsset.getRelatedOne("TechDataCalendar");
        } catch (GenericEntityException e) {
            // no calendar ok, when not more that total capacity
            if (fixedAsset.getDouble("productionCapacity").doubleValue() >= quantity) {
                return "OK";
            } else {
                return "Quantity requested: " + quantity + " Quantity available: " + fixedAsset.getString("productionCapacity");
            }
        }
        // now find all the dates and check the availabilty for each date
        // please note that calendarId is the same for (TechData)Calendar, CalendarExcDay and CalendarExWeek
        long dayCount = 0;
        String resultMessage = "";
        while (dayCount < (long) reservLength) {
            GenericValue techDataCalendarExcDay = null;
            // find an existing Day exception record
            Timestamp exceptionDateStartTime = new Timestamp((long) (reservStart.getTime() + (dayCount++ * 86400000)));
            try {
                techDataCalendarExcDay = delegator.findByPrimaryKey("TechDataCalendarExcDay",
                        UtilMisc.toMap("calendarId", fixedAsset.get("calendarId"), "exceptionDateStartTime", exceptionDateStartTime));
            } catch (GenericEntityException e) {
                if (fixedAsset.get("productionCapacity") != null) {
                    //Debug.logInfo(" No exception day record found, available: " + fixedAsset.getString("productionCapacity") + " Requested now: " + quantity, module);
                    if (fixedAsset.getDouble("productionCapacity").doubleValue() < quantity)
                        resultMessage = resultMessage.concat(exceptionDateStartTime.toString().substring(0, 10) + ", ");
                }
            }
            if (techDataCalendarExcDay != null) {
                // see if we can get the number of assets available
                // first try techDataCalendarExcDay(exceptionCapacity) and then FixedAsset(productionCapacity)
                // if still zero, do not check availability
                double exceptionCapacity = 0.00;
                if (techDataCalendarExcDay.get("exceptionCapacity") != null)
                    exceptionCapacity = techDataCalendarExcDay.getDouble("exceptionCapacity").doubleValue();
                if (exceptionCapacity == 0.00 && fixedAsset.get("productionCapacity") != null)
                    exceptionCapacity = fixedAsset.getDouble("productionCapacity").doubleValue();
                if (exceptionCapacity != 0.00) {
                    double usedCapacity = 0.00;
                    if (techDataCalendarExcDay.get("usedCapacity") != null)
                        usedCapacity = techDataCalendarExcDay.getDouble("usedCapacity").doubleValue();
                    if (exceptionCapacity < (quantity + usedCapacity)) {
                        resultMessage = resultMessage.concat(exceptionDateStartTime.toString().substring(0, 10) + ", ");
                        Debug.logInfo("No rental fixed Asset available: " + exceptionCapacity +
                                " already used: " + usedCapacity +
                                " Requested now: " + quantity, module);
                    }
                }
            }
        }
        if (resultMessage.compareTo("") == 0)
            return "OK";
        else
            return "I am sorry, not available at these dates: " + resultMessage + "item not added to the shopping cart.....";
    }

    protected void setQuantity(int quantity, LocalDispatcher dispatcher, ShoppingCart cart, boolean triggerExternalOps, boolean resetShipGroup, boolean updateProductPrice) throws CartItemModifyException {
        if (this.quantity == quantity) {
            return;
        }

        if (this.isPromo) {
            throw new CartItemModifyException("Sorry, you can't change the quantity on the promotion item " + this.getName() + " (product ID: " + productId + "), not setting quantity.");
        }

        // needed for inventory checking and auto-save
        String productStoreId = cart.getProductStoreId();

        if (!"PURCHASE_ORDER".equals(cart.getOrderType())) {
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
        }

        // set quantity before promos so order total, etc will be updated
        this.quantity = quantity;

        if (updateProductPrice) {
            this.updatePrice(dispatcher, cart);
        }

        // apply/unapply promotions
        if (triggerExternalOps) {
            ProductPromoWorker.doPromotions(cart, dispatcher);
        }

        if (!"PURCHASE_ORDER".equals(cart.getOrderType())) {
            // store the auto-save cart
            if (triggerExternalOps && ProductStoreWorker.autoSaveCart(delegator, productStoreId)) {
                try {
                    ShoppingListEvents.fillAutoSaveList(cart, dispatcher);
                } catch (GeneralException e) {
                    Debug.logWarning(e, UtilProperties.getMessage(resource_error,"OrderUnableToStoreAutoSaveCart", locale));
                }
            }
        }

        // set the item ship group
        if (resetShipGroup) {
            cart.clearItemShipInfo(this);
            cart.setItemShipGroupQty(this, quantity, 0);
        }
    }

    public void updatePrice(LocalDispatcher dispatcher, ShoppingCart cart) throws CartItemModifyException {
        // set basePrice using the calculateProductPrice service
        if (_product != null && isModifiedPrice==false) {
            try {
                Map priceContext = new HashMap();
                priceContext.put("currencyUomId", cart.getCurrency());

                String partyId = cart.getPartyId();
                if (partyId != null) {
                    priceContext.put("partyId", partyId);
                }
                priceContext.put("quantity", new Double(this.getQuantity()));
                priceContext.put("product", this.getProduct());
                if (cart.getOrderType().equals("PURCHASE_ORDER")) {
                    Map priceResult = dispatcher.runSync("calculatePurchasePrice", priceContext);
                    if (ServiceUtil.isError(priceResult)) {
                        throw new CartItemModifyException("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));
                    }
                    Boolean validPriceFound = (Boolean) priceResult.get("validPriceFound");
                    if (!validPriceFound.booleanValue()) {
                        throw new CartItemModifyException("Could not find a valid price for the product with ID [" + this.getProductId() + "] and supplier with ID [" + partyId + "], not adding to cart.");
                    }

                    this.setBasePrice(((Double) priceResult.get("price")).doubleValue());
                    this.setDisplayPrice(this.basePrice);
                    this.orderItemPriceInfos = (List) priceResult.get("orderItemPriceInfos");
                } else {
                    priceContext.put("prodCatalogId", this.getProdCatalogId());
                    priceContext.put("webSiteId", cart.getWebSiteId());
                    priceContext.put("productStoreId", cart.getProductStoreId());
                    Map priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
                    if (ServiceUtil.isError(priceResult)) {
                        throw new CartItemModifyException("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));
                    }

                    Boolean validPriceFound = (Boolean) priceResult.get("validPriceFound");
                    if (!validPriceFound.booleanValue()) {
                        throw new CartItemModifyException("Could not find a valid price for the product with ID [" + this.getProductId() + "], not adding to cart.");
                    }

                    if (priceResult.get("listPrice") != null) {
                        this.listPrice = ((Double) priceResult.get("listPrice")).doubleValue();
                    }

                    if (priceResult.get("basePrice") != null) {
                        this.setBasePrice(((Double) priceResult.get("basePrice")).doubleValue());
                    }

                    if (priceResult.get("displayPrice") != null) {
                        this.setDisplayPrice(((Double) priceResult.get("price")).doubleValue());
                    }

                    this.orderItemPriceInfos = (List) priceResult.get("orderItemPriceInfos");

                    // If product is configurable, the price is taken from the configWrapper.
                    if (configWrapper != null) {
                        // TODO: for configurable products need to do something to make them VAT aware... for now base and display prices are the same
                        this.setBasePrice(configWrapper.getTotalPrice());
                        this.setDisplayPrice(configWrapper.getTotalPrice());
                    }
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

    /** Returns the reservation start date. */
    public Timestamp getReservStart() {
        return this.getReservStart(0);
    }
    /** Returns the reservation start date with a number of days added. */
    public Timestamp getReservStart(double addDays) {
        if (addDays == 0)
                return this.reservStart;
        else    {
            if(this.reservStart != null)
                return new Timestamp((long)(this.reservStart.getTime() + (addDays * 86400000.0)));
            else
                return null;
        }
    }
    /** Returns the reservation length. */
    public double getReservLength() {
        return this.reservLength;
    }
    /** Returns the reservation number of persons. */
    public double getReservPersons() {
        return this.reservPersons;
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
        if (ddDate != null) {
            this.setAttribute("itemDesiredDeliveryDate", ddDate.toString());
        }
    }

    /** Returns the item's customer desired delivery date. */
    public Timestamp getDesiredDeliveryDate() {
        String ddDate = (String) this.getAttribute("itemDesiredDeliveryDate");

        if (ddDate != null) {
            try {
                return Timestamp.valueOf(ddDate);
            } catch (IllegalArgumentException e) {
                Debug.logWarning(e, UtilProperties.getMessage(resource_error,"OrderProblemGettingItemDesiredDeliveryDateFor", UtilMisc.toMap("productId",this.getProductId()), locale));
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
        return this.itemType;
    }

    /** Returns the item type description. */
    public String getItemTypeDescription() {
        GenericValue orderItemType = null;
        try {
            orderItemType = this.getDelegator().findByPrimaryKeyCache("OrderItemType", UtilMisc.toMap("orderItemTypeId", this.getItemType()));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, UtilProperties.getMessage(resource_error,"OrderProblemsGettingOrderItemTypeFor", UtilMisc.toMap("orderItemTypeId",this.getItemType()), locale));
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

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public void setOrderItemSeqId(String orderItemSeqId) {
        Debug.log("Setting orderItemSeqId - " + orderItemSeqId, module);
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

    /** Sets the requirementId. */
    public void setRequirementId(String requirementId) {
        this.requirementId = requirementId;
    }

    /** Returns the requirementId. */
    public String getRequirementId() {
        return this.requirementId;
    }

    /** Sets the quoteId. */
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    /** Returns the quoteId. */
    public String getQuoteId() {
        return this.quoteId;
    }

    /** Sets the quoteItemSeqId. */
    public void setQuoteItemSeqId(String quoteItemSeqId) {
        this.quoteItemSeqId = quoteItemSeqId;
    }

    /** Returns the quoteItemSeqId. */
    public String getQuoteItemSeqId() {
        return this.quoteItemSeqId;
    }

    public String getStatusId() {
        return this.statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
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
    /** Set the item's description. */
    public void setName(String itemName) {
        this.itemDescription = itemName;
    }
    /** Returns the item's description. */
    public String getName() {
       if (itemDescription != null) {
          return itemDescription;
       } else {
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
               return "";
            }
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
    /** Returns a list of the item's standard features */
    public List getStandardFeatureList() {
        List features = null;
        GenericValue product = this.getProduct();
        if (product != null) {
            try {
                List featureAppls = product.getRelated("ProductFeatureAndAppl");
                features=EntityUtil.filterByAnd(featureAppls,UtilMisc.toMap("productFeatureApplTypeId","STANDARD_FEATURE"));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Unable to get features from product : " + product.get("productId"), module);
            }
        }
        return features;
    }

    /** Returns a List of the item's features for supplier*/
   public List getFeaturesForSupplier(LocalDispatcher dispatcher,String partyId) {
       List featureAppls = getStandardFeatureList();
       if (featureAppls != null && featureAppls.size() > 0) {
           try {
              Map result = dispatcher.runSync("convertFeaturesForSupplier", UtilMisc.toMap("partyId", partyId, "productFeatures", featureAppls));
              featuresForSupplier = (List) result.get("convertedProductFeatures");
           } catch (GenericServiceException e) {
               Debug.logError(e, "Unable to get features for supplier from product : " + this.productId, module);
           }
       }
       return featuresForSupplier;
   }

    /** Returns the item's size (length + girth) */
    public double getSize() {
        GenericValue product = getProduct();
        if (product != null) {
            Double height = product.getDouble("shippingHeight");
            Double width = product.getDouble("shippingWidth");
            Double depth = product.getDouble("shippingDepth");

            // if all are null, see if there is an associated virtual product and get the info of that product
            if (height == null & width == null && depth == null) {
                GenericValue parentProduct = this.getParentProduct();
                if (parentProduct != null) {
                    height = parentProduct.getDouble("shippingHeight");
                    width = product.getDouble("shippingWidth");
                    depth = product.getDouble("shippingDepth");
                }
            }

            if (height == null) height = new Double(0);
            if (width == null) width = new Double(0);
            if (depth == null) depth = new Double(0);

            // determine girth (longest field is length)
            double[] sizeInfo = { height.doubleValue(), width.doubleValue(), depth.doubleValue() };
            Arrays.sort(sizeInfo);

            return (sizeInfo[0] * 2) + (sizeInfo[1] * 2) + sizeInfo[2];
        } else {
            // non-product items have 0 size
            return 0;
        }
    }

    public Map getItemProductInfo() {
        Map itemInfo = new HashMap();
        itemInfo.put("productId", this.getProductId());
        itemInfo.put("weight", new Double(this.getWeight()));
        itemInfo.put("size",  new Double(this.getSize()));
        itemInfo.put("piecesIncluded", new Long(this.getPiecesIncluded()));
        itemInfo.put("featureSet", this.getFeatureSet());
        return itemInfo;
    }

    /** Returns the base price. */
    public double getBasePrice() {
        double curBasePrice;
        if (selectedAmount > 0) {
            curBasePrice = basePrice * selectedAmount;
        } else {
            curBasePrice = basePrice;
        }
        return curBasePrice;
    }
    
    public double getDisplayPrice() {
        double curDisplayPrice;
        if (this.displayPrice == null) {
            curDisplayPrice = this.getBasePrice();
        } else {
            if (selectedAmount > 0) {
                curDisplayPrice = this.displayPrice.doubleValue() * this.selectedAmount;
            } else {
                curDisplayPrice = this.displayPrice.doubleValue();
            }
        }
        return curDisplayPrice;
    }

    /** Returns the list price. */
    public double getListPrice() {
        return listPrice;
    }

    /** Returns isModifiedPrice */
    public boolean getIsModifiedPrice() {
        return isModifiedPrice;
    }

    /** Set isModifiedPrice */
    public void setIsModifiedPrice(boolean isModifiedPrice) {
        this.isModifiedPrice = isModifiedPrice;
    }

    /** get the percentage for the second person */
    public double getReserv2ndPPPerc() {
        return reserv2ndPPPerc;
    }

    /** get the percentage for the third and following person */
    public double getReservNthPPPerc() {
        return reservNthPPPerc;
    }


    /** Returns the "other" adjustments. */
    public double getOtherAdjustments() {
        return OrderReadHelper.calcItemAdjustments(new Double(quantity), new Double(getBasePrice()), this.getAdjustments(), true, false, false, false, false);
    }

    /** calculates for a reservation the percentage/100 extra for more than 1 person. */
    // similar code at editShoppingList.bsh
    public double getRentalAdjustment() {
        if (!this.itemType.equals("RENTAL_ORDER_ITEM")) {
            // not a rental item?
            return 1;
        }
        double persons = this.getReservPersons();
        double rentalValue = 0;
        if (persons > 1)    {
            if (persons > 2 ) {
                persons -= 2;
                if(getReservNthPPPerc() > 0) {
                    rentalValue = persons * getReservNthPPPerc();
                } else {
                    rentalValue = persons * getReserv2ndPPPerc();
                }
                persons = 2;
            }
            if (persons == 2) {
                rentalValue += getReserv2ndPPPerc();
            }
        }
        rentalValue += 100;    // add final 100 percent for first person
        //     Debug.log("rental parameters....Nbr of persons:" + getReservPersons() + " extra% 2nd person:" + getReserv2ndPPPerc()+ " extra% Nth person:" + getReservNthPPPerc() + "  total rental adjustment:" + rentalValue/100 * getReservLength() );
        return rentalValue/100 * getReservLength(); // return total rental adjustment
    }

    /** Returns the total line price. */
    public double getItemSubTotal(double quantity) {
//        Debug.logInfo("Price" + getBasePrice() + " quantity" +  quantity + " Rental adj:" + getRentalAdjustment() + " other adj:" + getOtherAdjustments(), module);
          return (getBasePrice() * quantity * getRentalAdjustment()) + getOtherAdjustments();
    }

    public double getItemSubTotal() {
        return this.getItemSubTotal(this.quantity);
    }

    public double getDisplayItemSubTotal() {
        return (getDisplayPrice() * this.quantity * getRentalAdjustment()) + getOtherAdjustments();
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
        double amount = 0;
        Double amountDbl = (Double) additionalProductFeatureAndAppl.get("amount");
        if (amountDbl != null) {
            amount = amountDbl.doubleValue() * this.getQuantity();
        }
        orderAdjustment.set("amount", new Double(amount));

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

    public Map getFeatureIdQtyMap(double quantity) {
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
                    Double newQuantity = new Double(lastQuantity.doubleValue() + quantity);
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
                Double newQuantity = new Double(lastQuantity.doubleValue() + quantity);
                featureMap.put(appl.getString("productFeatureId"), newQuantity);
            }
        }
        return featureMap;
    }

    /** Removes an item attribute. */
    public void removeAttribute(String name) {
        if (attributes != null) {
            attributes.remove(name);
        }
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

    /** Remove an OrderItemAttribute. */
    public void removeOrderItemAttribute(String name) {
        if (orderItemAttributes != null) {
            orderItemAttributes.remove(name);
        }
    }

    /** Creates an OrderItemAttribute entry. */
    public void setOrderItemAttribute(String name, String value) {
        if (orderItemAttributes == null) orderItemAttributes = new HashMap();
        this.orderItemAttributes.put(name, value);
    }

    /** Return an OrderItemAttribute. */
    public String getOrderItemAttribute(String name) {
        if (orderItemAttributes == null) return null;
        return (String) this.orderItemAttributes.get(name);
    }

    public Map getOrderItemAttributes() {
        Map attrs = FastMap.newInstance();
        if (orderItemAttributes != null) {
            attrs.putAll(orderItemAttributes);
        }
        return attrs;
    }

    /** Add an adjustment to the order item; don't worry about setting the orderId, orderItemSeqId or orderAdjustmentId; they will be set when the order is created */
    public int addAdjustment(GenericValue adjustment) {
        itemAdjustments.add(adjustment);
        return itemAdjustments.indexOf(adjustment);
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
        return equals(productId, null, 0.00, 0.00, additionalProductFeatureAndAppls, attributes, prodCatalogId, selectedAmount, configWrapper, false);
    }

    /** Compares the specified object with this cart item including rental data. Defaults isPromo to false. */
    public boolean equals(String productId, Timestamp reservStart, double reservLength, double reservPersons, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, ProductConfigWrapper configWrapper, double selectedAmount) {
        return equals(productId, reservStart, reservLength, reservPersons, additionalProductFeatureAndAppls, attributes, prodCatalogId, selectedAmount, configWrapper, false);
    }

    /** Compares the specified object with this cart item. Defaults isPromo to false. */
    public boolean equals(String productId, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, double selectedAmount, boolean isPromo) {
        return equals(productId, null, 0.00, 0.00, additionalProductFeatureAndAppls, attributes, prodCatalogId, selectedAmount, null, isPromo);
    }

    /** Compares the specified object with this cart item. */
    public boolean equals(String productId, Timestamp reservStart, double reservLength, double reservPersons, Map additionalProductFeatureAndAppls, Map attributes, String prodCatalogId, double selectedAmount, ProductConfigWrapper configWrapper, boolean isPromo) {
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

        if ((this.reservStart == null && reservStart != null) || (this.reservStart != null && reservStart == null)) {
            return false;
        }
        if (this.reservStart != null && reservStart != null && !this.reservStart.equals(reservStart)) {
            return false;
        }

        if (this.reservLength != reservLength) {
            return false;
        }

        if (this.reservPersons != reservPersons) {
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

        if (quoteId != null) {
            // all items linked to a quote are unique
            return false;
        }

        if (requirementId != null) {
            // all items linked to a requirement are unique
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

          this._parentProduct = ProductWorker.getParentProduct(productId, this.getDelegator());

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
                            GenericValue newAdjustment = GenericValue.create(adjustment);
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
                        GenericValue newAdjustment = GenericValue.create(adjustment);
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
    public static String getPurchaseOrderItemDescription(GenericValue product, GenericValue supplierProduct, Locale locale){
          String itemDescription = "";
          String supplierProductId = supplierProduct.getString("supplierProductId");
          if (supplierProductId == null) {
               supplierProductId = "";
          } else {
               supplierProductId += " ";
          }
          String supplierProductName = supplierProduct.getString("supplierProductName");
          if (supplierProductName == null) {
            if (supplierProductName == null) {
                supplierProductName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", locale);
             }
           }
          itemDescription = supplierProductId + supplierProductName;
          return itemDescription;
    }
}
