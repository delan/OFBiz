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
package org.ofbiz.order.shoppingcart;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.store.ProductStoreSurveyWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.RequestHandler;

/**
 * Shopping cart events.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:tristana@twibble.org">Tristan Austin</a>
 * @version    $Rev$
 * @since      2.0
 */
public class ShoppingCartEvents {

    public static String module = ShoppingCartEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";

    private static final String NO_ERROR = "noerror";
    private static final String NON_CRITICAL_ERROR = "noncritical";
    private static final String ERROR = "error";

    public static String addProductPromoCode(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);
        String productPromoCodeId = request.getParameter("productPromoCodeId");
        if (UtilValidate.isNotEmpty(productPromoCodeId)) {
            String checkResult = cart.addProductPromoCode(productPromoCodeId, dispatcher);
            if (UtilValidate.isNotEmpty(checkResult)) {
                request.setAttribute("_ERROR_MESSAGE_", checkResult);
                return "error";
            }
        }
        return "success";
    }

    /** Event to add an item to the shopping cart. */
    public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective = null;
        Map result = null;
        String productId = null;
        String itemType = null;
        String itemDescription = null;
        String productCategoryId = null;
        String priceStr = null;
        double price = 0.00;
        String quantityStr = null;
        double quantity = 0;
        String reservStartStr = null;
        java.sql.Timestamp reservStart = null;
        String reservLengthStr = null;
        double reservLength = 0;
        String reservPersonsStr = null;
        double reservPersons = 0;

        // not used right now: Map attributes = null;
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        Locale locale = UtilHttp.getLocale(request);

        // Get the parameters as a MAP, remove the productId and quantity params.
        Map paramMap = UtilHttp.getParameterMap(request);

        // Get shoppingList info if passed
        String shoppingListId = request.getParameter("shoppingListId");
        String shoppingListItemSeqId = request.getParameter("shoppingListItemSeqId");
        if (paramMap.containsKey("ADD_PRODUCT_ID")) {
            productId = (String) paramMap.remove("ADD_PRODUCT_ID");
        } else if (paramMap.containsKey("add_product_id")) {
            productId = (String) paramMap.remove("add_product_id");
        }

        if (paramMap.containsKey("ADD_CATEGORY_ID")) {
            productCategoryId = (String) paramMap.remove("ADD_CATEGORY_ID");
        } else if (paramMap.containsKey("add_category_id")) {
            productCategoryId = (String) paramMap.remove("add_category_id");
        }
        if (productCategoryId != null && productCategoryId.length() == 0) {
            productCategoryId = null;
        }

        if (productId == null) {
            // before returning error; check make sure we aren't adding a special item type
            if (paramMap.containsKey("ADD_ITEM_TYPE")) {
                itemType = (String) paramMap.remove("ADD_ITEM_TYPE");
            } else if (paramMap.containsKey("add_item_type")) {
                itemType = (String) paramMap.remove("add_item_type");
            } else {
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource, "cart.addToCart.noProductInfoPassed", locale));
                return "success"; // not critical return to same page
            }
        }

        // check for an itemDescription
        if (paramMap.containsKey("ADD_ITEM_DESCRIPTION")) {
            itemDescription = (String) paramMap.remove("ADD_ITEM_DESCRIPTION");
        } else if (paramMap.containsKey("add_item_description")) {
            itemDescription = (String) paramMap.remove("add_item_description");
        }
        if (itemDescription != null && itemDescription.length() == 0) {
            itemDescription = null;
        }

        // Get the ProductConfigWrapper (it's not null only for configurable items)
        ProductConfigWrapper configWrapper = null;
        configWrapper = ProductConfigWorker.getProductConfigWrapper(productId, cart.getCurrency(), request);

        if (configWrapper != null) {
            // The choices selected by the user are taken from request and set in the wrapper
            ProductConfigWorker.fillProductConfigWrapper(configWrapper, request);
            if (!configWrapper.isCompleted()) {
                // The configuration is not valid
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource, "cart.addToCart.productConfigurationIsNotValid", locale));
                return "error";
            }
        }

        // get the override price
        if (paramMap.containsKey("PRICE")) {
            priceStr = (String) paramMap.remove("PRICE");
        } else if (paramMap.containsKey("price")) {
            priceStr = (String) paramMap.remove("price");
        }
        if (priceStr == null) {
            priceStr = "0.00";  // default price is 0.00
        }

        // get the renting data
        if (paramMap.containsKey("reservStart")) {
            reservStartStr = (String) paramMap.remove("reservStart");
            if (reservStartStr.length() == 10) // only date provided, no time string?
                    reservStartStr += " 00:00:00.000000000"; // should have format: yyyy-mm-dd hh:mm:ss.fffffffff
            if (reservStartStr.length() >0) {
                try {
                    reservStart = java.sql.Timestamp.valueOf((String) reservStartStr);
                } catch (Exception e) {
                    Debug.logWarning(e,"Problems parsing Reservation start string: "
                                + reservStartStr, module);
                    reservStart = null;
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,"cart.addToCart.rental.startDate", locale));
                    return "error";
                }
            }
            else reservStart = null;

            if (reservStart != null && paramMap.containsKey("reservLength")) {
                reservLengthStr = (String) paramMap.remove("reservLength");
                // parse the reservation Length
                try {
                    reservLength = NumberFormat.getNumberInstance().parse(
                            reservLengthStr).doubleValue();
                } catch (Exception e) {
                    Debug.logWarning(e,"Problems parsing reservation length string: "
                                    + reservLengthStr, module);
                    reservLength = 1;
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderReservationLengthShouldBeAPositiveNumber", locale));
                    return "error";
                }
            }

            if (reservStart != null && paramMap.containsKey("reservPersons")) {
                reservPersonsStr = (String) paramMap.remove("reservPersons");
                // parse the number of persons
                try {
                    reservPersons = NumberFormat.getNumberInstance().parse(
                            reservPersonsStr).doubleValue();
                } catch (Exception e) {
                    Debug.logWarning(e,"Problems parsing reservation number of persons string: " + reservPersonsStr, module);
                    reservPersons = 1;
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderNumberOfPersonsShouldBeOneOrLarger", locale));
                    return "error";
                }
            }
        }

        // get the quantity
        if (paramMap.containsKey("QUANTITY")) {
            quantityStr = (String) paramMap.remove("QUANTITY");
        } else if (paramMap.containsKey("quantity")) {
            quantityStr = (String) paramMap.remove("quantity");
        }
        if (quantityStr == null) {
            quantityStr = "1";  // default quantity is 1
        }

        // parse the price
        try {
            price = NumberFormat.getNumberInstance().parse(priceStr).doubleValue();
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing price string: " + priceStr, module);
            price = 0.00;
        }

        // parse the quantity
        try {
            quantity = NumberFormat.getNumberInstance().parse(quantityStr).doubleValue();
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
            quantity = 1;
        }

        // get the selected amount
        String selectedAmountStr = "0.00";
        if (paramMap.containsKey("ADD_AMOUNT")) {
            selectedAmountStr = (String) paramMap.remove("ADD_AMOUNT");
        } else if (paramMap.containsKey("add_amount")) {
            selectedAmountStr = (String) paramMap.remove("add_amount");
        }

        // parse the amount
        double amount = 0.00;
        if (selectedAmountStr != null && selectedAmountStr.length() > 0) {
            try {
                amount = NumberFormat.getNumberInstance().parse(selectedAmountStr).doubleValue();
            } catch (Exception e) {
                Debug.logWarning(e, "Problem parsing amount string: " + selectedAmountStr, module);
                amount = 0.00;
            }
        }

        // check for an add-to cart survey
        List surveyResponses = null;
        if (productId != null) {
            String productStoreId = ProductStoreWorker.getProductStoreId(request);
            List productSurvey = ProductStoreWorker.getProductSurveys(delegator, productStoreId, productId, "CART_ADD");
            if (productSurvey != null && productSurvey.size() > 0) {
                // TODO: implement multiple survey per product
                GenericValue survey = EntityUtil.getFirst(productSurvey);
                String surveyResponseId = (String) request.getAttribute("surveyResponseId");
                if (surveyResponseId != null) {
                    surveyResponses = UtilMisc.toList(surveyResponseId);
                } else {
                    Map surveyContext = UtilHttp.getParameterMap(request);
                    GenericValue userLogin = cart.getUserLogin();
                    String partyId = null;
                    if (userLogin != null) {
                        partyId = userLogin.getString("partyId");
                    }
                    String formAction = "/additemsurvey";
                    String nextPage = RequestHandler.getNextPageUri(request.getPathInfo());
                    if (nextPage != null) {
                        formAction = formAction + "/" + nextPage;
                    }
                    ProductStoreSurveyWrapper wrapper = new ProductStoreSurveyWrapper(survey, partyId, surveyContext);
                    request.setAttribute("surveyWrapper", wrapper);
                    request.setAttribute("surveyAction", formAction); // will be used as the form action of the survey
                    return "survey";
                }
            }
        }
        if (surveyResponses != null) {
            paramMap.put("surveyResponses", surveyResponses);
        }

        // Translate the parameters and add to the cart
        result = cartHelper.addToCart(catalogId, shoppingListId, shoppingListItemSeqId, productId, productCategoryId,
                itemType, itemDescription, price, amount, quantity, reservStart, reservLength, reservPersons, configWrapper, paramMap);
            controlDirective = processResult(result, request);

        // Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            if (cart.viewCartOnAdd()) {
                return "viewcart";
            } else {
                return "success";
            }
        }
    }

    public static String addToCartFromOrder(HttpServletRequest request, HttpServletResponse response) {
        String orderId = request.getParameter("order_id");
        String[] itemIds = request.getParameterValues("item_id");
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        Map result;
        String controlDirective;

        boolean addAll = ("true".equals(request.getParameter("add_all")));
        result = cartHelper.addToCartFromOrder(catalogId, orderId, itemIds, addAll);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Adds all products in a category according to quantity request parameter
     * for each; if no parameter for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public static String addToCartBulk(HttpServletRequest request, HttpServletResponse response) {
        String categoryId = request.getParameter("category_id");
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map result;
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        //Convert the params to a map to pass in
        Map paramMap = UtilHttp.getParameterMap(request);
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        result = cartHelper.addToCartBulk(catalogId, categoryId, paramMap);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Adds a set of requirements to the cart
     */
    public static String addToCartBulkRequirements(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map result;
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        //Convert the params to a map to pass in
        Map paramMap = UtilHttp.getParameterMap(request);
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        result = cartHelper.addToCartBulkRequirements(catalogId, paramMap);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Adds a set of requirements to the cart
     */
    public static String addToCartFromQuote(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map result;

        //Convert the params to a map to pass in
        Map paramMap = UtilHttp.getParameterMap(request);
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        result = cartHelper.addToCartFromQuote(catalogId, request.getParameter("quoteId"));
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Adds all products in a category according to default quantity on ProductCategoryMember
     * for each; if no default for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public static String addCategoryDefaults(HttpServletRequest request, HttpServletResponse response) {
        String categoryId = request.getParameter("category_id");
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map result;
        Double totalQuantity;
        Locale locale = UtilHttp.getLocale(request);

        result = cartHelper.addCategoryDefaults(catalogId, categoryId);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            totalQuantity = (Double)result.get("totalQuantity");
            Map messageMap = UtilMisc.toMap("totalQuantity", UtilFormatOut.formatQuantity(totalQuantity) );

            request.setAttribute("_EVENT_MESSAGE_",
                                  UtilProperties.getMessage(resource, "cart.add_category_defaults",
                                          messageMap, locale ));

            return "success";
        }
    }

    /** Delete an item from the shopping cart. */
    public static String deleteFromCart(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(null, dispatcher, cart);
        String controlDirective;
        Map result;
        Map paramMap = UtilHttp.getParameterMap(request);
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        //Delegate the cart helper
        result = cartHelper.deleteFromCart(paramMap);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Update the items in the shopping cart. */
    public static String modifyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = getCartObject(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Security security = (Security) request.getAttribute("security");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(null, dispatcher, cart);
        String controlDirective;
        Map result;
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        Map paramMap = UtilHttp.getParameterMap(request);

        String removeSelectedFlag = request.getParameter("removeSelected");
        String selectedItems[] = request.getParameterValues("selectedItem");
        boolean removeSelected = ("true".equals(removeSelectedFlag) && selectedItems != null && selectedItems.length > 0);
        result = cartHelper.modifyCart(security, userLogin, paramMap, removeSelected, selectedItems);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Empty the shopping cart. */
    public static String clearCart(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        cart.clear();
        return "success";
    }

    /** Totally wipe out the cart, removes all stored info. */
    public static String destroyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        clearCart(request, response);
        session.removeAttribute("shoppingCart");
        session.removeAttribute("orderPartyId");
        session.removeAttribute("orderMode");
        session.removeAttribute("productStoreId");
        session.removeAttribute("CURRENT_CATALOG_ID");
        return "success";
    }

    /** Gets or creates the shopping cart object */
    public static ShoppingCart getCartObject(HttpServletRequest request, Locale locale, String currencyUom) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession(true);
        ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");

        if (cart == null) {
            if (locale == null) {
                locale = UtilHttp.getLocale(request);
            }
            if (currencyUom == null) {
                currencyUom = UtilHttp.getCurrencyUom(request);
            }
            cart = new WebShoppingCart(request, locale, currencyUom);
            session.setAttribute("shoppingCart", cart);
        } else {
            if (locale != null && !locale.equals(cart.getLocale())) {
                cart.setLocale(locale);
            }
            if (currencyUom != null && !currencyUom.equals(cart.getCurrency())) {
                try {
                    cart.setCurrency(dispatcher, currencyUom);
                } catch (CartItemModifyException e) {
                    Debug.logError(e, "Unable to modify currency in cart", module);
                }
            }
        }
        return cart;
    }

    /** Main get cart method; uses the locale & currency from the session */
    public static ShoppingCart getCartObject(HttpServletRequest request) {
        return getCartObject(request, null, null);
    }

    /** Update the cart's UserLogin object if it isn't already set. */
    public static String keepCartUpdated(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        ShoppingCart cart = getCartObject(request);

        // if we just logged in set the UL
        if (cart.getUserLogin() == null) {
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            if (userLogin != null) {
                try {
                    cart.setUserLogin(userLogin, dispatcher);
                } catch (CartItemModifyException e) {
                    Debug.logWarning(e, module);
                }
            }
        }

        // same for autoUserLogin
        if (cart.getAutoUserLogin() == null) {
            GenericValue autoUserLogin = (GenericValue) session.getAttribute("autoUserLogin");
            if (autoUserLogin != null) {
                if (cart.getUserLogin() == null) {
                    try {
                        cart.setAutoUserLogin(autoUserLogin, dispatcher);
                    } catch (CartItemModifyException e) {
                        Debug.logWarning(e, module);
                    }
                } else {
                    cart.setAutoUserLogin(autoUserLogin);
                }
            }
        }

        // update the locale
        Locale locale = UtilHttp.getLocale(request);
        if (cart.getLocale() == null || !locale.equals(cart.getLocale())) {
            cart.setLocale(locale);
        }

        return "success";
    }

    /** For GWP Promotions with multiple alternatives, selects an alternative to the current GWP */
    public static String setDesiredAlternateGwpProductId(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String alternateGwpProductId = request.getParameter("alternateGwpProductId");
        String alternateGwpLineStr = request.getParameter("alternateGwpLine");
        Locale locale = UtilHttp.getLocale(request);

        if (UtilValidate.isEmpty(alternateGwpProductId)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotSelectAlternateGiftNoAlternateGwpProductIdPassed", locale));
            return "error";
        }
        if (UtilValidate.isEmpty(alternateGwpLineStr)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotSelectAlternateGiftNoAlternateGwpLinePassed", locale));
            return "error";
        }

        int alternateGwpLine = 0;
        try {
            alternateGwpLine = Integer.parseInt(alternateGwpLineStr);
        } catch (Exception e) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotSelectAlternateGiftAlternateGwpLineIsNotAValidNumber", locale));
            return "error";
        }

        ShoppingCartItem cartLine = cart.findCartItem(alternateGwpLine);
        if (cartLine == null) {
        	request.setAttribute("_ERROR_MESSAGE_", "Could not select alternate gift, no cart line item found for #" + alternateGwpLine + ".");
            return "error";
        }

        if (cartLine.getIsPromo()) {
            // note that there should just be one promo adjustment, the reversal of the GWP, so use that to get the promo action key
            Iterator checkOrderAdjustments = UtilMisc.toIterator(cartLine.getAdjustments());
            while (checkOrderAdjustments != null && checkOrderAdjustments.hasNext()) {
                GenericValue checkOrderAdjustment = (GenericValue) checkOrderAdjustments.next();
                if (UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoId")) &&
                        UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoRuleId")) &&
                        UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoActionSeqId"))) {
                    GenericPK productPromoActionPk = delegator.makeValidValue("ProductPromoAction", checkOrderAdjustment).getPrimaryKey();
                    cart.setDesiredAlternateGiftByAction(productPromoActionPk, alternateGwpProductId);
                    if (cart.getOrderType().equals("SALES_ORDER")) {
                        org.ofbiz.order.shoppingcart.product.ProductPromoWorker.doPromotions(cart, dispatcher);
                    }
                    return "success";
                }
            }
        }

        request.setAttribute("_ERROR_MESSAGE_", "Could not select alternate gift, cart line item found for #" + alternateGwpLine + " does not appear to be a valid promotional gift.");
        return "error";
    }

    /** Associates a party to order */
    public static String addAdditionalParty(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        String partyId = request.getParameter("additionalPartyId");
        String roleTypeId[] = request.getParameterValues("additionalRoleTypeId");
        List eventList = new LinkedList();
        Locale locale = UtilHttp.getLocale(request);
        int i;

        if (UtilValidate.isEmpty(partyId) || roleTypeId.length < 1) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderPartyIdAndOrRoleTypeIdNotDefined", locale));
            return "error";
        }

        if (request.getAttribute("_EVENT_MESSAGE_LIST_") != null) {
            eventList.addAll((List) request.getAttribute("_EVENT_MESSAGE_LIST_"));
        }

        for (i = 0; i < roleTypeId.length; i++) {
            try {
                cart.addAdditionalPartyRole(partyId, roleTypeId[i]);
            } catch (Exception e) {
                eventList.add(e.getLocalizedMessage());
            }
        }

        request.removeAttribute("_EVENT_MESSAGE_LIST_");
        request.setAttribute("_EVENT_MESSAGE_LIST_", eventList);
        return "success";
    }

    /** Removes a previously associated party to order */
    public static String removeAdditionalParty(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        String partyId = request.getParameter("additionalPartyId");
        String roleTypeId[] = request.getParameterValues("additionalRoleTypeId");
        List eventList = new LinkedList();
        Locale locale = UtilHttp.getLocale(request);
        int i;

        if (UtilValidate.isEmpty(partyId) || roleTypeId.length < 1) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderPartyIdAndOrRoleTypeIdNotDefined", locale));
            return "error";
        }

        if (request.getAttribute("_EVENT_MESSAGE_LIST_") != null) {
            eventList.addAll((List) request.getAttribute("_EVENT_MESSAGE_LIST_"));
        }

        for (i = 0; i < roleTypeId.length; i++) {
            try {
                cart.removeAdditionalPartyRole(partyId, roleTypeId[i]);
            } catch (Exception e) {
                Debug.logInfo(e.getLocalizedMessage(), module);
                eventList.add(e.getLocalizedMessage());
            }
        }

        request.removeAttribute("_EVENT_MESSAGE_LIST_");
        request.setAttribute("_EVENT_MESSAGE_LIST_", eventList);
        return "success";
    }

    /**
     * This should be called to translate the error messages of the
     * <code>ShoppingCartHelper</code> to an appropriately formatted
     * <code>String</code> in the request object and indicate whether
     * the result was an error or not and whether the errors were
     * critical or not
     *
     * @param result    The result returned from the
     * <code>ShoppingCartHelper</code>
     * @param request The servlet request instance to set the error messages
     * in
     * @return one of NON_CRITICAL_ERROR, ERROR or NO_ERROR.
     */
    private static String processResult(Map result, HttpServletRequest request) {
        //Check for errors
        StringBuffer errMsg = new StringBuffer();
        if (result.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
            List errorMsgs = (List)result.get(ModelService.ERROR_MESSAGE_LIST);
            Iterator iterator = errorMsgs.iterator();
            errMsg.append("<ul>");
            while (iterator.hasNext()) {
                errMsg.append("<li>");
                errMsg.append(iterator.next());
                errMsg.append("</li>");
            }
            errMsg.append("</ul>");
        } else if (result.containsKey(ModelService.ERROR_MESSAGE)) {
            errMsg.append(result.get(ModelService.ERROR_MESSAGE));
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
        }

        //See whether there was an error
        if (errMsg.length() > 0) {
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
            if (result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                return NON_CRITICAL_ERROR;
            } else {
                return ERROR;
            }
        } else {
            return NO_ERROR;
        }
    }

    /** Assign agreement **/
    public static String selectAgreement(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String agreementId = request.getParameter("agreementId");
        Map result = cartHelper.selectAgreement(agreementId);
        if (ServiceUtil.isError(result)) {
           request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
           return "error";
        }
        return "success";
    }

    /** Assign currency **/
    public static String setCurrency(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String currencyUomId = request.getParameter("currencyUomId");
        Map result = cartHelper.setCurrency(currencyUomId);
        if (ServiceUtil.isError(result)) {
           request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
           return "error";
        }
        return "success";
    }

    /** Add order term **/
   public static String addOrderTerm(HttpServletRequest request, HttpServletResponse response) {
       GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
       LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
       ShoppingCart cart = getCartObject(request);
       ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
       String termTypeId = request.getParameter("termTypeId");
       String termValue = request.getParameter("termValue");
       String termDays = request.getParameter("termDays");
       String termIndex = request.getParameter("termIndex");
       Locale locale = UtilHttp.getLocale(request);

       Double dTermValue = null;
       Long lTermDays = null;

       if (termValue.trim().equals("")) {
           termValue = null;
       }
       if (termDays.trim().equals("")) {
           termDays = null;
       }
       if (UtilValidate.isEmpty(termTypeId)) {
       	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderOrderTermTypeIsRequired", locale));
           return "error";
       }
       if (!UtilValidate.isSignedDouble(termValue)) {
       	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderOrderTermValue", UtilMisc.toMap("orderTermValue",UtilValidate.isSignedFloatMsg), locale));
          return "error";
       }
       if (termValue != null) {
          dTermValue =new Double(termValue);
       }
       if (!UtilValidate.isInteger(termDays)) {
       	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderOrderTermDays", UtilMisc.toMap("orderTermDays",UtilValidate.isLongMsg), locale));
          return "error";
       }
       if (termDays != null) {
          lTermDays = new Long(termDays);
       }
       if ((termIndex != null) && (termIndex != "-1") && (UtilValidate.isInteger(termIndex))) {
          cartHelper.removeOrderTerm(Integer.parseInt(termIndex));
       }

       Map result = cartHelper.addOrderTerm(termTypeId, dTermValue, lTermDays);
       if (ServiceUtil.isError(result)) {
          request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
          return "error";
       }
       return "success";
   }

   /** Add order term **/
  public static String removeOrderTerm(HttpServletRequest request, HttpServletResponse response) {
      GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
      LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
      ShoppingCart cart = getCartObject(request);
      ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
      String index = request.getParameter("termIndex");
      Map result = cartHelper.removeOrderTerm(Integer.parseInt(index));
      if (ServiceUtil.isError(result)) {
         request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
         return "error";
      }
      return "success";
  }

  /** Initialize order entry **/
  public static String initializeOrderEntry(HttpServletRequest request, HttpServletResponse response) {
      GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
      HttpSession session = request.getSession();
      Security security = (Security) request.getAttribute("security");
      GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
      String finalizeMode = (String)session.getAttribute("finalizeMode");
      Locale locale = UtilHttp.getLocale(request);

      String productStoreId = request.getParameter("productStoreId");

      if (UtilValidate.isNotEmpty(productStoreId)) {
          session.setAttribute("productStoreId", productStoreId);
      }
      ShoppingCart cart = getCartObject(request);

      String orderMode = request.getParameter("orderMode");
      if (orderMode != null) {
          cart.setOrderType(orderMode);
          session.setAttribute("orderMode", orderMode);
      } else {
      	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderPleaseSelectEitherSaleOrPurchaseOrder", locale));
          return "error";
      }

      // check the selected product store
      GenericValue productStore = null;
      if (UtilValidate.isNotEmpty(productStoreId)) {
          productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
          if (productStore != null) {

              // check permission for taking the order
              boolean hasPermission = false;
              if ((cart.getOrderType().equals("PURCHASE_ORDER")) && (security.hasEntityPermission("ORDERMGR", "_PURCHASE_CREATE", session))) {
                  hasPermission = true;
              } else if (cart.getOrderType().equals("SALES_ORDER")) {
                  if (security.hasEntityPermission("ORDERMGR", "_SALES_CREATE", session)) {
                      hasPermission = true;
                  } else {
                      // if the user is a rep of the store, then he also has permission
                      List storeReps = null;
                      try {
                          storeReps = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStore.getString("productStoreId"),
                              "partyId", userLogin.getString("partyId"), "roleTypeId", "SALES_REP"));
                      } catch(GenericEntityException gee) {
                          //
                      }
                      storeReps = EntityUtil.filterByDate(storeReps);
                      if (storeReps != null && storeReps.size() > 0) {
                          hasPermission = true;
                      }
                  }
              }

              if (hasPermission) {
                  cart = ShoppingCartEvents.getCartObject(request, null, productStore.getString("defaultCurrencyUomId"));
              } else {
                  request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToTakeOrdersForThisStore", locale));
                  cart.clear();
                  session.removeAttribute("orderMode");
                  return "error";
              }
              cart.setProductStoreId(productStoreId);
          } else {
              cart.setProductStoreId(null);
          }
      }

      if ("SALES_ORDER".equals(cart.getOrderType()) && UtilValidate.isEmpty(cart.getProductStoreId())) {
          request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderAProductStoreMustBeSelectedForASalesOrder", locale));
          cart.clear();
          session.removeAttribute("orderMode");
          return "error";
      }

      String salesChannelEnumId = request.getParameter("salesChannelEnumId");
      if (UtilValidate.isNotEmpty(salesChannelEnumId)) {
          cart.setChannelType(salesChannelEnumId);
      }

      // set party info
      String partyId = request.getParameter("supplierPartyId");
      if (!UtilValidate.isEmpty(request.getParameter("partyId"))) {
          partyId = request.getParameter("partyId");
      }
      String userLoginId = request.getParameter("userLoginId");
      if (partyId != null || userLoginId != null) {
          if ((partyId == null || partyId.length() == 0) && userLoginId != null && userLoginId.length() > 0) {
              GenericValue thisUserLogin = null;
              try {
                  thisUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
              } catch(GenericEntityException gee) {
                  //
              }
              if (thisUserLogin != null) {
                  partyId = thisUserLogin.getString("partyId");
              } else {
                  partyId = userLoginId;
              }
          }
          if (partyId != null && partyId.length() > 0) {
              GenericValue thisParty = null;
              try{
                  thisParty = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
              } catch(GenericEntityException gee) {
                  //
              }
              if (thisParty == null) {
              	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotLocateTheSelectedParty", locale));
                  return "error";
              } else {
                  cart.setOrderPartyId(partyId);
              }
          } else if (partyId != null && partyId.length() == 0) {
              cart.setOrderPartyId("_NA_");
              partyId = null;
          }
      } else {
          partyId = cart.getPartyId();
          if (partyId != null && partyId.equals("_NA_")) partyId = null;
      }

      return "success";
  }

  /** Route order entry **/
  public static String routeOrderEntry(HttpServletRequest request, HttpServletResponse response) {
      HttpSession session = request.getSession();

      String orderMode = (String)session.getAttribute("orderMode");
      String orderModePar = request.getParameter("orderMode"); // orderModePar != null when this request is coming from the init page

      if (orderMode == null) {
          return "init";
      }
      if (orderMode.equals("PURCHASE_ORDER") && orderModePar != null) {
          return "agreements";
      }
      return "cart";
  }
}
