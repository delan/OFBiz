/*
 * $Id$
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.order.shoppinglist;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Iterator;
import java.sql.Timestamp;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.calendar.RecurrenceInfo;
import org.ofbiz.service.calendar.RecurrenceInfoException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.product.store.ProductStoreWorker;

/**
 * Shopping List Services
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */
public class ShoppingListServices {

    public static final String module = ShoppingListServices.class.getName();

    public static Map setShoppingListRecurrence(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Timestamp startDate = (Timestamp) context.get("startDateTime");
        Timestamp endDate = (Timestamp) context.get("endDateTime");
        Integer frequency = (Integer) context.get("frequency");
        Integer interval = (Integer) context.get("intervalNumber");

        if (frequency == null || interval == null) {
            Debug.logWarning("Frequency or interval was not specified", module);
            return ServiceUtil.returnSuccess();
        }

        if (startDate == null) {
            switch (frequency.intValue()) {
                case 5:
                    startDate = UtilDateTime.getWeekStart(UtilDateTime.nowTimestamp(), 0, interval.intValue());
                    break;
                case 6:
                    startDate = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp(), 0, interval.intValue());
                    break;
                case 7:
                    startDate = UtilDateTime.getYearStart(UtilDateTime.nowTimestamp(), 0, interval.intValue());
                    break;
                default:
                    return ServiceUtil.returnError("Invalid frequency for shopping list recurrence");
            }
        }

        long startTime = startDate.getTime();
        long endTime = 0;
        if (endDate != null) {
            endTime = endDate.getTime();
        }

        RecurrenceInfo recInfo = null;
        try {
            recInfo = RecurrenceInfo.makeInfo(delegator, startTime, frequency.intValue(), interval.intValue(), -1, endTime);
        } catch (RecurrenceInfoException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to create shopping list recurrence information");
        }

        Debug.log("Next Recurrence - " + UtilDateTime.getTimestamp(recInfo.next()), module);
        Map result = ServiceUtil.returnSuccess();
        result.put("recurrenceInfoId", recInfo.getID());

        return result;
    }

    public static Map createListReorders(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        List exprs = UtilMisc.toList(new EntityExpr("shoppingListTypeId", EntityOperator.EQUALS, "SLT_AUTO_REODR"),
                new EntityExpr("isActive", EntityOperator.EQUALS, "Y"));
        EntityCondition cond = new EntityConditionList(exprs, EntityOperator.AND);
        List order = UtilMisc.toList("-lastOrderedDate");

        EntityListIterator eli = null;
        try {
            eli = delegator.findListIteratorByCondition("ShoppingList", cond, null, order);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        if (eli != null) {
            GenericValue shoppingList;
            while (((shoppingList = (GenericValue) eli.next()) != null)) {
                Timestamp lastOrder = shoppingList.getTimestamp("lastOrderedDate");
                GenericValue recurrenceInfo = null;
                try {
                    recurrenceInfo = shoppingList.getRelatedOne("RecurrenceInfo");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }

                Timestamp startDateTime = recurrenceInfo.getTimestamp("startDateTime");
                RecurrenceInfo recurrence = null;
                if (recurrenceInfo != null) {
                    try {
                        recurrence = new RecurrenceInfo(recurrenceInfo);
                    } catch (RecurrenceInfoException e) {
                        Debug.logError(e, module);
                    }
                }

                // check the next recurrence
                if (recurrence != null) {
                    long next = lastOrder == null ? recurrence.next(startDateTime.getTime()) : recurrence.next(lastOrder.getTime());
                    Timestamp now = UtilDateTime.nowTimestamp();
                    Timestamp nextOrder = UtilDateTime.getDayStart(UtilDateTime.getTimestamp(next));

                    if (nextOrder.after(now)) {
                        continue;
                    }
                } else {
                    continue;
                }

                ShoppingCart listCart = makeShoppingListCart(dispatcher, shoppingList, locale);
                CheckOutHelper helper = new CheckOutHelper(dispatcher, delegator, listCart);

                // store the order
                Map createResp = helper.createOrder(userLogin);
                if (createResp != null && ServiceUtil.isError(createResp)) {
                    Debug.logError("Cannot create order for shopping list - " + shoppingList, module);
                } else {
                    String orderId = (String) createResp.get("orderId");

                    // authorize the payments
                    Map payRes = null;
                    try {
                        payRes = helper.processPayment(ProductStoreWorker.getProductStore(listCart.getProductStoreId(), delegator), userLogin);
                    } catch (GeneralException e) {
                        Debug.logError(e, module);
                    }

                    if (payRes != null && ServiceUtil.isError(payRes)) {
                        Debug.logError("Payment processing problems with shopping list - " + shoppingList, module);
                    }

                    shoppingList.set("lastOrderedDate", UtilDateTime.nowTimestamp());
                    try {
                        shoppingList.store();
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                    }

                    // send notification
                    try {
                        dispatcher.runAsync("sendOrderPayRetryNotification", UtilMisc.toMap("orderId", orderId));
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                    }

                    // increment the recurrence
                    try {
                        recurrence.incrementCurrentCount();
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                    }
                }
            }

            try {
                eli.close();
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map splitShipmentMethodString(DispatchContext dctx, Map context) {
        String shipmentMethodString = (String) context.get("shippingMethodString");
        Map result = ServiceUtil.returnSuccess();

        if (UtilValidate.isNotEmpty(shipmentMethodString)) {
            int delimiterPos = shipmentMethodString.indexOf('@');
            String shipmentMethodTypeId = null;
            String carrierPartyId = null;

            if (delimiterPos > 0) {
                shipmentMethodTypeId = shipmentMethodString.substring(0, delimiterPos);
                carrierPartyId = shipmentMethodString.substring(delimiterPos + 1);
                result.put("shipmentMethodTypeId", shipmentMethodTypeId);
                result.put("carrierPartyId", carrierPartyId);
            }
        }
        return result;
    }

    public static ShoppingCart makeShoppingListCart(LocalDispatcher dispatcher, GenericValue shoppingList, Locale locale) {
        GenericDelegator delegator = dispatcher.getDelegator();
        ShoppingCart listCart = null;
        if (shoppingList != null && shoppingList.get("productStoreId") != null) {
            String productStoreId = shoppingList.getString("productStoreId");
            String currencyUom = shoppingList.getString("currencyUom");
            if (currencyUom == null) {
                GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
                if (productStore == null) {
                    return null;
                }
                currencyUom = productStore.getString("defaultCurrencyUomId");
            }
            if (locale == null) {
                locale = Locale.getDefault();
            }

            List items = null;
            try {
                items = shoppingList.getRelated("ShoppingListItem", UtilMisc.toList("shoppingListItemSeqId"));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }

            if (UtilValidate.isNotEmpty(items)) {
                listCart = new ShoppingCart(delegator, productStoreId, locale, currencyUom);
                listCart.setOrderPartyId(shoppingList.getString("partyId"));

                Iterator i = items.iterator();
                while (i.hasNext()) {
                    GenericValue shoppingListItem = (GenericValue) i.next();
                    String productId = shoppingListItem.getString("productId");
                    Double quantity = shoppingListItem.getDouble("quantity");
                    if (UtilValidate.isNotEmpty(productId) && quantity != null) {
                        // list items are noted in the shopping cart
                        String listId = shoppingListItem.getString("shoppingListId");
                        String itemId = shoppingListItem.getString("shoppingListItemSeqId");
                        Map attributes = UtilMisc.toMap("shoppingListId", listId, "shoppingListItemSeqId", itemId);

                        try {
                            listCart.addOrIncreaseItem(productId, quantity.doubleValue(), null, attributes, null, dispatcher);
                        } catch (CartItemModifyException e) {
                            Debug.logError(e, "Unable to add product to List Cart - " + productId, module);
                        } catch (ItemNotFoundException e) {
                            Debug.logError(e, "Product not found - " + productId, module);
                        }
                    }
                }

                if (listCart.size() > 0) {
                    if (shoppingList.get("paymentMethodId") != null) {
                        listCart.addPayment(shoppingList.getString("paymentMethodId"));
                    }
                    if (shoppingList.get("contactMechId") != null) {
                        listCart.setShippingContactMechId(0, shoppingList.getString("contactMechId"));
                    }
                    if (shoppingList.get("shipmentMethodTypeId") != null) {
                        listCart.setShipmentMethodTypeId(0, shoppingList.getString("shipmentMethodTypeId"));
                    }
                    if (shoppingList.get("carrierPartyId") != null) {
                        listCart.setCarrierPartyId(0, shoppingList.getString("carrierPartyId"));
                    }
                }
            }
        }
        return listCart;
    }

    public static ShoppingCart makeShoppingListCart(LocalDispatcher dispatcher, String shoppingListId, Locale locale) {
        GenericDelegator delegator = dispatcher.getDelegator();
        GenericValue shoppingList = null;
        try {
            shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return makeShoppingListCart(dispatcher, shoppingList, locale);
    }

}
