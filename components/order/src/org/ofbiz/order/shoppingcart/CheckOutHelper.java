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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.ObjectType;

/**
 * A facade over the ShoppingCart to simplify the relatively complex
 * processing required to create an order in the system.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:tristana@twibble.org">Tristan Austin</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class CheckOutHelper {

    public static final String module = CheckOutHelper.class.getName();
    public static final String resource = "OrderUiLabels";

    protected LocalDispatcher dispatcher = null;
    protected GenericDelegator delegator = null;
    protected ShoppingCart cart = null;

    public CheckOutHelper(LocalDispatcher dispatcher, GenericDelegator delegator, ShoppingCart cart) {
        this.delegator = delegator;
        this.dispatcher = dispatcher;
        this.cart = cart;
    }

    public Map setCheckOutShippingAddress(String shippingContactMechId) {
        List errorMessages = new ArrayList();
        Map result;
        String errMsg = null;

        if (this.cart != null && this.cart.size() > 0) {
            errorMessages.addAll(setCheckOutShippingAddressInternal(shippingContactMechId));
        } else {
            errMsg = UtilProperties.getMessage(resource,"checkhelper.no_items_in_cart", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }
        if (errorMessages.size() == 1) {
            result = ServiceUtil.returnError(errorMessages.get(0).toString());
        } else if (errorMessages.size() > 0) {
            result = ServiceUtil.returnError(errorMessages);
        } else {
            result = ServiceUtil.returnSuccess();
        }

        return result;
    }

    private List setCheckOutShippingAddressInternal(String shippingContactMechId) {
        List errorMessages = new ArrayList();
        String errMsg = null;

        // set the shipping address
        if (UtilValidate.isNotEmpty(shippingContactMechId)) {
            this.cart.setShippingContactMechId(shippingContactMechId);
        } else if (cart.shippingApplies()) {
            // only return an error if shipping is required for this purchase
            errMsg = UtilProperties.getMessage(resource,"checkhelper.select_shipping_destination", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        return errorMessages;
    }

    public Map setCheckOutShippingOptions(String shippingMethod, String correspondingPoId, String shippingInstructions,
            String orderAdditionalEmails, String maySplit, String giftMessage, String isGift, String shipBeforeDate, String shipAfterDate ) {
        List errorMessages = new ArrayList();
        Map result;
        String errMsg = null;

        if (this.cart != null && this.cart.size() > 0) {
            errorMessages.addAll(setCheckOutShippingOptionsInternal(shippingMethod, correspondingPoId,
                    shippingInstructions, orderAdditionalEmails, maySplit, giftMessage, isGift, shipBeforeDate, shipAfterDate));
        } else {
            errMsg = UtilProperties.getMessage(resource,"checkhelper.no_items_in_cart", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        if (errorMessages.size() == 1) {
            result = ServiceUtil.returnError(errorMessages.get(0).toString());
        } else if (errorMessages.size() > 0) {
            result = ServiceUtil.returnError(errorMessages);
        } else {
            result = ServiceUtil.returnSuccess();
        }

        return result;
    }

    private List setCheckOutShippingOptionsInternal(String shippingMethod, String correspondingPoId, String shippingInstructions,
            String orderAdditionalEmails, String maySplit, String giftMessage, String isGift, String shipBeforeDate, String shipAfterDate ) {
        List errorMessages = new ArrayList();
        String errMsg = null;

        // set the general shipping options
        if (UtilValidate.isNotEmpty(shippingMethod)) {
            int delimiterPos = shippingMethod.indexOf('@');
            String shipmentMethodTypeId = null;
            String carrierPartyId = null;

            if (delimiterPos > 0) {
                shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                carrierPartyId = shippingMethod.substring(delimiterPos + 1);
            }

            this.cart.setShipmentMethodTypeId(shipmentMethodTypeId);
            this.cart.setCarrierPartyId(carrierPartyId);
        } else if (cart.shippingApplies()) {
            // only return an error if shipping is required for this purchase
            errMsg = UtilProperties.getMessage(resource,"checkhelper.select_shipping_method", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        // set the shipping instructions
        this.cart.setShippingInstructions(shippingInstructions);

        if (UtilValidate.isNotEmpty(maySplit)) {
            cart.setMaySplit(Boolean.valueOf(maySplit));
        } else {
            errMsg = UtilProperties.getMessage(resource,"checkhelper.select_splitting_preference", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        // set the gift message
        this.cart.setGiftMessage(giftMessage);

        if (UtilValidate.isNotEmpty(isGift)) {
            cart.setIsGift(Boolean.valueOf(isGift));
        } else {
            errMsg = UtilProperties.getMessage(resource, "checkhelper.specify_if_order_is_gift", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        // set the shipping dates
        if (UtilValidate.isDate(shipBeforeDate)) {
            cart.setShipBeforeDate(UtilDateTime.toTimestamp(shipBeforeDate));
        } else {
            errMsg = UtilProperties.getMessage(resource, "checkhelper.specify_if_shipBeforeDate_is_date", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        if (UtilValidate.isDate(shipAfterDate)) {
            cart.setShipAfterDate(UtilDateTime.toTimestamp(shipAfterDate));
        } else {
            errMsg = UtilProperties.getMessage(resource, "checkhelper.specify_if_shipAfterDate_is_date", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        // set any additional notification emails
        this.cart.setOrderAdditionalEmails(orderAdditionalEmails);

        // set the PO number
        if (UtilValidate.isNotEmpty(correspondingPoId)) {
            this.cart.setPoNumber(correspondingPoId);
        } else {
            this.cart.setPoNumber("(none)");
        }

        return errorMessages;
    }

    public Map setCheckOutPayment(Map selectedPaymentMethods, List singleUsePayments, String billingAccountId, Double billingAccountAmt) {
        List errorMessages = new ArrayList();
        Map result;
        String errMsg = null;

        if (this.cart != null && this.cart.size() > 0) {
            errorMessages.addAll(setCheckOutPaymentInternal(selectedPaymentMethods, singleUsePayments, billingAccountId, billingAccountAmt));
        } else {
            errMsg = UtilProperties.getMessage(resource,"checkhelper.no_items_in_cart", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        if (errorMessages.size() == 1) {
            result = ServiceUtil.returnError(errorMessages.get(0).toString());
        } else if (errorMessages.size() > 0) {
            result = ServiceUtil.returnError(errorMessages);
        } else {
            result = ServiceUtil.returnSuccess();
        }

        return result;
    }

    private List setCheckOutPaymentInternal(Map selectedPaymentMethods, List singleUsePayments, String billingAccountId, Double billingAccountAmt) {
        List errorMessages = new ArrayList();
        String errMsg = null;

        if (singleUsePayments == null) {
            singleUsePayments = new ArrayList();
        }

        // set the billing account amount
        if (billingAccountId != null && billingAccountAmt != null && !billingAccountId.equals("_NA_")) {
            cart.setBillingAccount(billingAccountId, billingAccountAmt.doubleValue());
        } else {
            cart.setBillingAccount(null, 0.00);
        }

        // set the payment method option
        if (selectedPaymentMethods != null && selectedPaymentMethods.size() > 0) {
            // clear out the old payments
            cart.clearPayments();

            // if we are EXT_BILLACT (billing account only) then we need to make sure we have enough credit
            if (selectedPaymentMethods.containsKey("EXT_BILLACT")) {
                double accountCredit = this.availableAccountBalance(cart.getBillingAccountId());
                // make sure we have enough to cover; if this is selected we don't have other payment methods
                if (cart.getGrandTotal() > accountCredit) {
                    errMsg = UtilProperties.getMessage(resource,"checkhelper.insufficient_credit_available_on_account",
                            (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                }
            }

            Set paymentMethods = selectedPaymentMethods.keySet();
            Iterator i = paymentMethods.iterator();
            while (i.hasNext()) {
                String checkOutPaymentId = (String) i.next();

                // get the selected amount to use
                Double paymentAmount = null;
                if (selectedPaymentMethods.get(checkOutPaymentId) != null) {
                    paymentAmount = (Double) selectedPaymentMethods.get(checkOutPaymentId);
                }

                boolean singleUse = singleUsePayments.contains(checkOutPaymentId);
                cart.addPaymentAmount(checkOutPaymentId, paymentAmount, singleUse);                
            }
        } else if (cart.getGrandTotal() != 0.00) {
            // only return an error if the order total is not 0.00
            errMsg = UtilProperties.getMessage(resource,"checkhelper.select_method_of_payment",
                    (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        return errorMessages;
    }

    public Map setCheckOutDates(Timestamp shipBefore, Timestamp shipAfter) {
          List errorMessages = new ArrayList();
          Map result = null;
          String errMsg = null;

          if (this.cart != null && this.cart.size() > 0) {
          	this.cart.setShipBeforeDate(shipBefore);
          	this.cart.setShipAfterDate(shipAfter);
          } else {
          	errMsg = UtilProperties.getMessage(resource,"checkhelper.no_items_in_cart",
                                                     (cart != null ? cart.getLocale() : Locale.getDefault()));
          	errorMessages.add(errMsg);
          }

          if (errorMessages.size() == 1) {
          	result = ServiceUtil.returnError(errorMessages.get(0).toString());
          } else if (errorMessages.size() > 0) {
          	result = ServiceUtil.returnError(errorMessages);
          } else {
          	result = ServiceUtil.returnSuccess();
          }
          return result;
      }


    public Map setCheckOutOptions(String shippingMethod, String shippingContactMechId, Map selectedPaymentMethods,
            List singleUsePayments, String billingAccountId, Double billingAccountAmt, String correspondingPoId,
            String shippingInstructions, String orderAdditionalEmails, String maySplit, String giftMessage, String isGift, String shipBeforeDate, String shipAfterDate) {
        List errorMessages = new ArrayList();
        Map result = null;
        String errMsg = null;


        if (this.cart != null && this.cart.size() > 0) {
            // set the general shipping options and method
            errorMessages.addAll(setCheckOutShippingOptionsInternal(shippingMethod, correspondingPoId,
                    shippingInstructions, orderAdditionalEmails, maySplit, giftMessage, isGift, shipBeforeDate, shipAfterDate));

            // set the shipping address
            errorMessages.addAll(setCheckOutShippingAddressInternal(shippingContactMechId));

            // set the payment method(s) option
            errorMessages.addAll(setCheckOutPaymentInternal(selectedPaymentMethods, singleUsePayments, billingAccountId, billingAccountAmt));

        } else {
            errMsg = UtilProperties.getMessage(resource,"checkhelper.no_items_in_cart", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        if (errorMessages.size() == 1) {
            result = ServiceUtil.returnError(errorMessages.get(0).toString());
        } else if (errorMessages.size() > 0) {
            result = ServiceUtil.returnError(errorMessages);
        } else {
            result = ServiceUtil.returnSuccess();
        }

        return result;
    }

    public Map checkGiftCard(Map params, Map selectedPaymentMethods) {
        List errorMessages = new ArrayList();
        Map errorMaps = new HashMap();
        Map result = new HashMap();
        String errMsg = null;
        // handle gift card payment
        if (params.get("addGiftCard") != null) {
            String gcNum = (String) params.get("giftCardNumber");
            String gcPin = (String) params.get("giftCardPin");
            String gcAmt = (String) params.get("giftCardAmount");
            double gcAmount = -1;

            boolean gcFieldsOkay = true;
            if (gcNum == null || gcNum.length() == 0) {
                errMsg = UtilProperties.getMessage(resource,"checkhelper.enter_gift_card_number", (cart != null ? cart.getLocale() : Locale.getDefault()));
                errorMessages.add(errMsg);
                gcFieldsOkay = false;
            }
            if (gcPin == null || gcPin.length() == 0) {
                errMsg = UtilProperties.getMessage(resource,"checkhelper.enter_gift_card_pin_number", (cart != null ? cart.getLocale() : Locale.getDefault()));
                errorMessages.add(errMsg);
                gcFieldsOkay = false;
            }
            if (selectedPaymentMethods != null && selectedPaymentMethods.size() > 0) {
                if (gcAmt == null || gcAmt.length() == 0) {
                    errMsg = UtilProperties.getMessage(resource,"checkhelper.enter_amount_to_place_on_gift_card", (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                    gcFieldsOkay = false;
                }
            }
            if (gcAmt != null && gcAmt.length() > 0) {
                try {
                    gcAmount = Double.parseDouble(gcAmt);
                } catch (NumberFormatException e) {
                    Debug.logError(e, module);
                    errMsg = UtilProperties.getMessage(resource,"checkhelper.invalid_amount_for_gift_card", (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                    gcFieldsOkay = false;
                }
            }

            if (gcFieldsOkay) {
                // store the gift card
                Map gcCtx = new HashMap();
                gcCtx.put("partyId", params.get("partyId"));
                gcCtx.put("cardNumber", gcNum);
                gcCtx.put("pinNumber", gcPin);
                gcCtx.put("userLogin", cart.getUserLogin());
                Map gcResult = null;
                try {
                    gcResult = dispatcher.runSync("createGiftCard", gcCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    errorMessages.add(e.getMessage());
                }
                if (gcResult != null) {
                    this.addErrors(errorMessages, errorMaps, gcResult);

                    if (errorMessages.size() == 0 && errorMaps.size() == 0) {
                        // set the GC payment method
                        Double giftCardAmount = null;
                        if (gcAmount > 0) {
                            giftCardAmount = new Double(gcAmount);
                        }
                        String gcPaymentMethodId = (String) gcResult.get("paymentMethodId");
                        result = ServiceUtil.returnSuccess();
                        result.put("paymentMethodId", gcPaymentMethodId);
                        result.put("amount", giftCardAmount);
                    }
                } else {
                    errMsg = UtilProperties.getMessage(resource,"checkhelper.problem_with_gift_card_information", (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                }
            }
        } else {
            result = ServiceUtil.returnSuccess();
        }

        // see whether we need to return an error or not
        if (errorMessages.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessages);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }
        if (errorMaps.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_MAP, errorMaps);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }

        return result;
    }

    public Map createOrder(GenericValue userLogin) {
        return createOrder(userLogin, null, null, null, false, null, null);
    }

    // Create order event - uses createOrder service for processing
    public Map createOrder(GenericValue userLogin, String distributorId, String affiliateId,
            List trackingCodeOrders, boolean areOrderItemsExploded, String visitId, String webSiteId) {
        if (this.cart == null) {
            return null;
        }
        String orderId = this.cart.getOrderId();

        // format the grandTotal
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);
        double cartTotal = this.cart.getGrandTotal();
        String grandTotalString = formatter.format(cartTotal);
        Double grandTotal = null;
        try {
            grandTotal = new Double(formatter.parse(grandTotalString).doubleValue());
        } catch (ParseException e) {
            Debug.logError(e, "Problem getting parsed currency amount from DecimalFormat", module);
            String errMsg = UtilProperties.getMessage(resource,"checkhelper.could_not_create_order_parsing_totals", (cart != null ? cart.getLocale() : Locale.getDefault()));
            return ServiceUtil.returnError(errMsg);
        }

        // store the order - build the context
        Map context = this.cart.makeCartMap(this.dispatcher, areOrderItemsExploded);

        //get the TrackingCodeOrder List
        context.put("trackingCodeOrders", trackingCodeOrders);

        if (distributorId != null) context.put("distributorId", distributorId);
        if (affiliateId != null) context.put("affiliateId", affiliateId);

        // need the partyId; don't use userLogin in case of an order via order mgr
        String partyId = this.cart.getPartyId();
        String facilityId = this.cart.getFacilityId();
        String terminalId = this.cart.getTerminalId();
        String transactionId = cart.getTransactionId();
        String productStoreId = cart.getProductStoreId();

        context.put("grandTotal", grandTotal);
        context.put("userLogin", userLogin);
        context.put("partyId", partyId);
        context.put("productStoreId", productStoreId);
        context.put("transactionId", transactionId);
        context.put("originFacilityId", facilityId);
        context.put("visitId", visitId);
        context.put("terminalId", terminalId);
        context.put("webSiteId", webSiteId);

        // store the order - invoke the service
        Map storeResult = null;

        try {
            storeResult = dispatcher.runSync("storeOrder", context);
            orderId = (String) storeResult.get("orderId");
            if (orderId != null && orderId.length() > 0) {
                this.cart.setOrderId(orderId);
                if (this.cart.getFirstAttemptOrderId() == null) {
                    this.cart.setFirstAttemptOrderId(orderId);
                }
            }
        } catch (GenericServiceException e) {
            String service = e.getMessage();
            Map messageMap = UtilMisc.toMap("service", service);
            String errMsg = UtilProperties.getMessage(resource, "checkhelper.could_not_create_order_invoking_service", messageMap, (cart != null ? cart.getLocale() : Locale.getDefault()));
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        // check for error message(s)
        if (ServiceUtil.isError(storeResult)) {
            String errMsg = UtilProperties.getMessage(resource, "checkhelper.did_not_complete_order_following_occurred", (cart != null ? cart.getLocale() : Locale.getDefault()));
            return ServiceUtil.returnError(errMsg);
        }

        // ----------
        // If needed, the production runs are created and linked to the order lines.
        //
        Iterator orderItems = ((List)context.get("orderItems")).iterator();
        int counter = 0;
        while (orderItems.hasNext()) {
            GenericValue orderItem = (GenericValue)orderItems.next();
            String productId = orderItem.getString("productId");
            if (productId != null) {
                try {
                    GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
                    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
                    if ("AGGREGATED".equals(product.getString("productTypeId"))) {
                        org.ofbiz.product.config.ProductConfigWrapper config = this.cart.findCartItem(counter).getConfigWrapper();
                        Map prunResult = null;
                        Map inputMap = new HashMap();
                        inputMap.put("config", config);
                        inputMap.put("facilityId", productStore.getString("inventoryFacilityId"));
                        inputMap.put("orderId", orderId);
                        inputMap.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                        inputMap.put("quantity", orderItem.getDouble("quantity"));
                        inputMap.put("userLogin", userLogin);
                        prunResult = dispatcher.runSync("createProductionRunFromConfiguration", inputMap);
                    }
                } catch (Exception e) {
                    String service = e.getMessage();
                    Map messageMap = UtilMisc.toMap("service", service);
                    String errMsg = UtilProperties.getMessage(resource, "checkhelper.could_not_create_order_invoking_service", messageMap, (cart != null ? cart.getLocale() : Locale.getDefault()));
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
            counter++;
        }
        // ----------

        // set the orderId for use by chained events
        Map result = ServiceUtil.returnSuccess();
        result.put("order_id", orderId);
        result.put("orderId", orderId);
        result.put("orderAdditionalEmails", this.cart.getOrderAdditionalEmails());

        // save the emails to the order
        List toBeStored = new LinkedList();

        GenericValue party = null;
        try {
            party = this.delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting Party record", module);
            party = null;
        }

        // create order contact mechs for the email address(s)
        if (party != null) {
            Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false));
            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = (GenericValue) emailIter.next();
                GenericValue orderContactMech = this.delegator.makeValue("OrderContactMech",
                        UtilMisc.toMap("orderId", orderId, "contactMechId", email.getString("contactMechId"), "contactMechPurposeTypeId", "ORDER_EMAIL"));
                toBeStored.add(orderContactMech);
            }
        }

        // create dummy contact mechs and order contact mechs for the additional emails
        String additionalEmails = this.cart.getOrderAdditionalEmails();
        List emailList = StringUtil.split(additionalEmails, ",");
        if (emailList == null) emailList = new ArrayList();
        Iterator eli = emailList.iterator();
        while (eli.hasNext()) {
            String email = (String) eli.next();
            String contactMechId = this.delegator.getNextSeqId("ContactMech").toString();
            GenericValue contactMech = this.delegator.makeValue("ContactMech",
                    UtilMisc.toMap("contactMechId", contactMechId, "contactMechTypeId", "EMAIL_ADDRESS", "infoString", email));

            GenericValue orderContactMech = this.delegator.makeValue("OrderContactMech",
                    UtilMisc.toMap("orderId", orderId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "ORDER_EMAIL"));
            toBeStored.add(contactMech);
            toBeStored.add(orderContactMech);
        }

        if (toBeStored.size() > 0) {
            try {
                if (Debug.verboseOn()) Debug.logVerbose("To Be Stored: " + toBeStored, module);
                this.delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                // not a fatal error; so just print a message
                Debug.logWarning(e, "Problems storing order email contact information", module);
            }
        }

        return result;
    }

    public void calcAndAddTax() throws GeneralException {
        calcAndAddTax(null);
    }

    public void calcAndAddTax(GenericValue shipAddress) throws GeneralException {
        if (!"SALES_ORDER".equals(cart.getOrderType())) {
            return;
        }

        int shipGroups = this.cart.getShipGroupSize();
        for (int i = 0; i < shipGroups; i++) {
            Map serviceContext = this.makeTaxContext(i, shipAddress);
            List taxReturn = this.getTaxAdjustments(dispatcher, "calcTax", serviceContext);

            if (Debug.verboseOn()) Debug.logVerbose("ReturnList: " + taxReturn, module);
            ShoppingCart.CartShipInfo csi = cart.getShipInfo(i);
            List orderAdj = (List) taxReturn.get(0);
            List itemAdj = (List) taxReturn.get(1);

            // set the item adjustments
            if (itemAdj != null) {
                for (int x = 0; x < itemAdj.size(); x++) {
                    List adjs = (List) itemAdj.get(x);
                    ShoppingCartItem item = (ShoppingCartItem) csi.shipItemInfo.get(x);
                    csi.setItemInfo(item, adjs);
                    Debug.log("Added item adjustments to ship group [" + i + " / " + x + "] - " + adjs, module);
                }
            }            

            // need to manually clear the order adjustments
            csi.shipTaxAdj.clear();
            csi.shipTaxAdj.addAll(orderAdj);
        }
    }

    private Map makeTaxContext(int shipGroup, GenericValue shipAddress) throws GeneralException {
        String productStoreId = cart.getProductStoreId();
        ShoppingCart.CartShipInfo csi = cart.getShipInfo(shipGroup);
        int totalItems = csi.shipItemInfo.size();

        List product = new ArrayList(totalItems);
        List amount = new ArrayList(totalItems);
        List shipAmt = new ArrayList(totalItems);

        Iterator iter = csi.shipItemInfo.keySet().iterator();
        for (int i = 0; i < totalItems; i++) {
            ShoppingCartItem cartItem = (ShoppingCartItem) csi.shipItemInfo.get(i);                                
            ShoppingCart.CartShipInfo.CartShipItemInfo itemInfo =
                    (ShoppingCart.CartShipInfo.CartShipItemInfo) csi.shipItemInfo.get(cartItem);

            product.add(i, cartItem.getProduct());
            amount.add(i, new Double(cartItem.getItemSubTotal(itemInfo.quantity)));
            shipAmt.add(i, new Double(0.00)); // no per item shipping
        }

        Double shipAmount = new Double(csi.shipEstimate);
        if (shipAddress == null) {
            shipAddress = cart.getShippingAddress(shipGroup);
        }

        // no shipping address; try the billing address
        if (shipAddress == null) {
            for (int i = 0; i < cart.selectedPayments(); i++) {
                ShoppingCart.CartPaymentInfo cpi = cart.getPaymentInfo(i);
                GenericValue billAddr = cpi.getBillingAddress(delegator);
                if (billAddr != null) {
                    shipAddress = billAddr;
                    Debug.logInfo("Found address from payment method.", module);
                }
                break;
            }
        }

        Map serviceContext = UtilMisc.toMap("productStoreId", productStoreId, "itemProductList", product, "itemAmountList", amount,
                "itemShippingList", shipAmt, "orderShippingAmount", shipAmount, "shippingAddress", shipAddress);

        return serviceContext;

    }

    // Calc the tax adjustments.
    private List getTaxAdjustments(LocalDispatcher dispatcher, String taxService, Map serviceContext) throws GeneralException {
        Map serviceResult = null;

        try {
            serviceResult = dispatcher.runSync(taxService, serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException("Problem occurred in tax service (" + e.getMessage() + ")", e);
        }

        if (ServiceUtil.isError(serviceResult)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(serviceResult));
        }

        // the adjustments (returned in order) from taxware.
        List orderAdj = (List) serviceResult.get("orderAdjustments");
        List itemAdj = (List) serviceResult.get("itemAdjustments");

        return UtilMisc.toList(orderAdj, itemAdj);
    }

    public Map processPayment(GenericValue productStore, GenericValue userLogin) throws GeneralException {
        return processPayment(productStore, userLogin, false);
    }

    public Map processPayment(GenericValue productStore, GenericValue userLogin, boolean faceToFace) throws GeneralException {
        // Get some payment related strings
        String DECLINE_MESSAGE = productStore.getString("authDeclinedMessage");
        String ERROR_MESSAGE = productStore.getString("authErrorMessage");
        String RETRY_ON_ERROR = productStore.getString("retryFailedAuths");
        if (RETRY_ON_ERROR == null) {
            RETRY_ON_ERROR = "Y";
        }

        // Get the orderId from the cart.
        String orderId = this.cart.getOrderId();

        // Get the paymentMethodTypeIds - this will need to change when ecom supports multiple payments
        List paymentMethodTypeIds = this.cart.getPaymentMethodTypeIds();

        // Check the payment preferences; if we have ANY w/ status PAYMENT_NOT_AUTH invoke payment service.
        boolean requireAuth = false;
        List allPaymentPreferences = null;
        try {
            allPaymentPreferences = this.delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            throw new GeneralException("Problems getting payment preferences", e);
        }

        // check for online payment methods or in-hand payment types with verbal or external refs
        List exprs = UtilMisc.toList(new EntityExpr("manualRefNum", EntityOperator.NOT_EQUAL, null));
        List manualRefPaymentPrefs = EntityUtil.filterByAnd(allPaymentPreferences, exprs);
        if (manualRefPaymentPrefs != null && manualRefPaymentPrefs.size() > 0) {
            Iterator i = manualRefPaymentPrefs.iterator();
            while (i.hasNext()) {
                GenericValue opp = (GenericValue) i.next();
                Map authCtx = new HashMap();
                authCtx.put("orderPaymentPreference", opp);
                if (opp.get("paymentMethodId") == null) {
                    authCtx.put("serviceTypeEnum", "PRDS_PAY_EXTERNAL");
                }
                authCtx.put("processAmount", opp.getDouble("maxAmount"));
                authCtx.put("authRefNum", opp.getString("manualRefNum"));
                authCtx.put("authResult", new Boolean(true));
                authCtx.put("userLogin", userLogin);

                Map authResp = dispatcher.runSync("processAuthResult", authCtx);
                if (authResp != null && ServiceUtil.isError(authResp)) {
                    throw new GeneralException(ServiceUtil.getErrorMessage(authResp));
                }

                // approve the order
                OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);

                if (productStore.get("manualAuthIsCapture") != null &&
                        "Y".equalsIgnoreCase(productStore.getString("manualAuthIsCapture"))) {
                    Map captCtx = new HashMap();
                    captCtx.put("orderPaymentPreference", opp);
                    if (opp.get("paymentMethodId") == null) {
                        captCtx.put("serviceTypeEnum", "PRDS_PAY_EXTERNAL");
                    }
                    captCtx.put("isFromAuth", new Boolean(false));
                    captCtx.put("captureResult", new Boolean(true));
                    captCtx.put("captureAmount", opp.getDouble("maxAmount"));
                    captCtx.put("captureRefNum", opp.getString("manualRefNum"));
                    captCtx.put("userLogin", userLogin);

                    Map capResp = dispatcher.runSync("processCaptureResult", captCtx);
                    if (capResp != null && ServiceUtil.isError(capResp)) {
                        throw new GeneralException(ServiceUtil.getErrorMessage(capResp));
                    }
                }
            }
        }

        // check for online payment methods needing authorization
        Map paymentFields = UtilMisc.toMap("statusId", "PAYMENT_NOT_AUTH");
        List onlinePaymentPrefs = EntityUtil.filterByAnd(allPaymentPreferences, paymentFields);

        if (onlinePaymentPrefs != null && onlinePaymentPrefs.size() > 0) {
            requireAuth = true;
        }

        // Invoke payment processing.
        if (requireAuth) {
            Map paymentResult = null;
            try {
                // invoke the payment gateway service.
                paymentResult = dispatcher.runSync("authOrderPayments",
                        UtilMisc.toMap("orderId", orderId, "userLogin", userLogin), 180, true);
            } catch (GenericServiceException e) {
                Debug.logWarning(e, module);
            }
            if (Debug.verboseOn()) Debug.logVerbose("Finsished w/ Payment Service", module);

            // grab the customer messages -- only passed back in the case of an error or failure
            List messages = (List) paymentResult.get("authResultMsgs");

            if (paymentResult != null && paymentResult.containsKey("processResult")) {
                String authResp = (String) paymentResult.get("processResult");

                if (authResp.equals("FAILED")) {
                    // order was NOT approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was NOT a success!", module);

                    boolean ok = OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId);
                    if (!ok) {
                        throw new GeneralException("Problem with order change; see above error");
                    }

                    // null out the orderId for next pass.
                    cart.setOrderId(null);
                    if (messages == null || messages.size() == 0) {
                        return ServiceUtil.returnError(DECLINE_MESSAGE);
                    } else {
                        return ServiceUtil.returnError(messages);
                    }
                } else if (authResp.equals("APPROVED")) {
                    // order WAS approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was a success!", module);

                    // set the order and item status to approved
                    boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
                    if (!ok) {
                        throw new GeneralException("Problem with order change; see above error");
                    }                    
                } else if (authResp.equals("ERROR")) {
                    // service failed
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth failed due to processor trouble.", module);
                    if (!faceToFace && "Y".equalsIgnoreCase(RETRY_ON_ERROR)) {
                        // never do this for a face to face purchase regardless of store setting
                        return ServiceUtil.returnSuccess(ERROR_MESSAGE);
                    } else {
                        boolean ok = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);
                        if (!ok) {
                            throw new GeneralException("Problem with order change; see above error");
                        }
                        // null out orderId for next pass
                        this.cart.setOrderId(null);
                        if (messages == null || messages.size() == 0) {
                            return ServiceUtil.returnError(ERROR_MESSAGE);
                        } else {
                            return ServiceUtil.returnError(messages);
                        }
                    }
                } else {
                    // should never happen
                    return ServiceUtil.returnError("Please contact customer service; payment return code unknown.");
                }
            } else {
                // result returned null == service failed
                if (Debug.verboseOn()) Debug.logVerbose("Payment auth failed due to processor trouble.", module);
                if (!faceToFace && "Y".equalsIgnoreCase(RETRY_ON_ERROR)) {
                    // never do this for a face to face purchase regardless of store setting
                    return ServiceUtil.returnSuccess(ERROR_MESSAGE);
                } else {
                    boolean ok = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);
                    if (!ok) {
                        throw new GeneralException("Problem with order change; see above error");
                    }
                    // null out orderId for next pass
                    this.cart.setOrderId(null);
                    return ServiceUtil.returnError(ERROR_MESSAGE);
                }
            }
        } else if (paymentMethodTypeIds.contains("CASH") || paymentMethodTypeIds.contains("EXT_COD") || paymentMethodTypeIds.contains("EXT_BILLACT")) {
            boolean hasOther = false;
            boolean validAmount = false;

            Iterator pmti = paymentMethodTypeIds.iterator();
            while (pmti.hasNext()) {
                String type = (String) pmti.next();
                if (!"CASH".equals(type) && !"EXT_COD".equals(type) && !"EXT_BILLACT".equals(type)) {
                    hasOther = true;
                    break;
                }
            }

            if (!hasOther) {
                if (!paymentMethodTypeIds.contains("CASH") && !paymentMethodTypeIds.contains("EXT_COD")) {
                    // only billing account, make sure we have enough to cover
                    String billingAccountId = cart.getBillingAccountId();
                    double billAcctCredit = this.availableAccountBalance(billingAccountId);
                    double billingAcctAmt = cart.getBillingAccountAmount();
                    if (billAcctCredit >= billingAcctAmt) {
                        if (cart.getGrandTotal() > billAcctCredit) {
                            validAmount = false;
                        } else {
                            validAmount = true;
                        }
                    }
                }

                // approve this as long as there are only COD and Billing Account types
                boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
                if (!ok) {
                    throw new GeneralException("Problem with order change; see above error");
                }
            }
        } else {
            // There is nothing to do, we just treat this as a success
        }

        // check to see if we should auto-invoice/bill
        if (faceToFace) {
            Debug.log("Face-To-Face Sale - " + orderId, module);
            this.adjustFaceToFacePayment(allPaymentPreferences, userLogin);
            boolean ok = OrderChangeHelper.completeOrder(dispatcher, userLogin, orderId);
            Debug.log("Complete Order Result - " + ok, module);
            if (!ok) {
                throw new GeneralException("Problem with order change; see error logs");
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public void adjustFaceToFacePayment(List allPaymentPrefs, GenericValue userLogin) throws GeneralException {
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);

        double cartTotal = this.cart.getGrandTotal();
        String grandTotalString = formatter.format(cartTotal);
        Double grandTotal = null;
        try {
            grandTotal = new Double(formatter.parse(grandTotalString).doubleValue());
        } catch (ParseException e) {
            throw new GeneralException("Problem getting parsed currency amount from DecimalFormat", e);
        }

        double prefTotal = 0.00;
        if (allPaymentPrefs != null) {
            Iterator i = allPaymentPrefs.iterator();
            while (i.hasNext()) {
                GenericValue pref = (GenericValue) i.next();
                Double maxAmount = pref.getDouble("maxAmount");
                if (maxAmount == null) maxAmount = new Double(0.00);
                prefTotal += maxAmount.doubleValue();
            }
        }

        String payTotalString = formatter.format(prefTotal);
        Double payTotal = null;
        try {
            payTotal = new Double(formatter.parse(payTotalString).doubleValue());
        } catch (ParseException e) {
            throw new GeneralException("Problem getting parsed currency amount from DecimalFormat", e);
        }

        if (grandTotal == null) grandTotal = new Double(0.00);
        if (payTotal == null) payTotal = new Double(0.00);

        if (payTotal.doubleValue() > grandTotal.doubleValue()) {
            double diff = (payTotal.doubleValue() - grandTotal.doubleValue()) * -1;
            String diffString = formatter.format(diff);
            Double change = null;
            try {
                change = new Double(formatter.parse(diffString).doubleValue());
            } catch (ParseException e) {
                throw new GeneralException("Problem getting parsed currency amount from DecimalFormat", e);
            }
            GenericValue newPref = delegator.makeValue("OrderPaymentPreference", null);
            newPref.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference"));
            newPref.set("orderId", cart.getOrderId());
            newPref.set("paymentMethodTypeId", "CASH");
            newPref.set("statusId", "PAYMENT_RECEIVED");
            newPref.set("maxAmount", change);
            newPref.set("createdDate", UtilDateTime.nowTimestamp());
            if (userLogin != null) {
                newPref.set("createdByUserLogin", userLogin.getString("userLoginId"));
            }
            delegator.create(newPref);
        }
    }

    public Map checkOrderBlacklist(GenericValue userLogin) {
    	if (cart == null) {
            return ServiceUtil.returnSuccess("success");
    	}
        GenericValue shippingAddressObj = this.cart.getShippingAddress();
    	if (shippingAddressObj == null) {
            return ServiceUtil.returnSuccess("success");
    	}
        String shippingAddress = UtilFormatOut.checkNull(shippingAddressObj.getString("address1")).toUpperCase();
        List exprs = UtilMisc.toList(new EntityExpr(
                new EntityExpr(new EntityFunction.UPPER(new EntityFieldValue("blacklistString")), EntityOperator.EQUALS, new EntityFunction.UPPER(shippingAddress)), EntityOperator.AND,
                new EntityExpr("orderBlacklistTypeId", EntityOperator.EQUALS, "BLACKLIST_ADDRESS")));
        String errMsg=null;

        List paymentMethods = this.cart.getPaymentMethods();
        Iterator i = paymentMethods.iterator();
        while (i.hasNext()) {
            GenericValue paymentMethod = (GenericValue) i.next();
            if (paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD")) {
                GenericValue creditCard = null;
                GenericValue billingAddress = null;
                try {
                    creditCard = paymentMethod.getRelatedOne("CreditCard");
                    if (creditCard != null)
                        billingAddress = creditCard.getRelatedOne("PostalAddress");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems getting credit card from payment method", module);
                    errMsg = UtilProperties.getMessage(resource,"checkhelper.problems_reading_database", (cart != null ? cart.getLocale() : Locale.getDefault()));
                    return ServiceUtil.returnError(errMsg);
                }
                if (creditCard != null) {
                    String creditCardNumber = UtilFormatOut.checkNull(creditCard.getString("cardNumber"));
                    exprs.add(new EntityExpr(
                            new EntityExpr("blacklistString", EntityOperator.EQUALS, creditCardNumber), EntityOperator.AND,
                            new EntityExpr("orderBlacklistTypeId", EntityOperator.EQUALS, "BLACKLIST_CREDITCARD")));
                }
                if (billingAddress != null) {
                    String address = UtilFormatOut.checkNull(billingAddress.getString("address1").toUpperCase());
                    exprs.add(new EntityExpr(
                            new EntityExpr(new EntityFunction.UPPER(new EntityFieldValue("blacklistString")), EntityOperator.EQUALS, new EntityFunction.UPPER(address)), EntityOperator.AND,
                            new EntityExpr("orderBlacklistTypeId", EntityOperator.EQUALS, "BLACKLIST_ADDRESS")));
                }
            }
        }

        List blacklistFound = null;
        if (exprs.size() > 0) {
            try {
                blacklistFound = this.delegator.findByOr("OrderBlacklist", exprs);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems with OrderBlacklist lookup.", module);
                errMsg = UtilProperties.getMessage(resource,"checkhelper.problems_reading_database", (cart != null ? cart.getLocale() : Locale.getDefault()));
                return ServiceUtil.returnError(errMsg);
            }
        }

        if (blacklistFound != null && blacklistFound.size() > 0) {
            return ServiceUtil.returnError("failed");
        } else {
            return ServiceUtil.returnSuccess("success");
        }
    }

    public Map failedBlacklistCheck(GenericValue userLogin, GenericValue productStore) {
        Map result;
        String errMsg=null;

        String REJECT_MESSAGE = productStore.getString("authFraudMessage");

        // Get the orderId from the cart.
        String orderId = this.cart.getOrderId();

        // set the order/item status - reverse inv
        OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId);

        // nuke the userlogin
        userLogin.set("enabled", "N");
        try {
            userLogin.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems de-activating userLogin.", module);
            errMsg = UtilProperties.getMessage(resource,"checkhelper.database_error", (cart != null ? cart.getLocale() : Locale.getDefault()));
            result = ServiceUtil.returnError(errMsg);
            return result;
        }
        result = ServiceUtil.returnSuccess();
        result.put(ModelService.ERROR_MESSAGE, REJECT_MESSAGE);

        // wipe the cart and session
        this.cart.clear();
        return result;
    }

    public Map checkExternalPayment(String orderId) {
        Map result;
        String errMsg=null;
        // warning there can only be ONE payment preference for this to work
        // you cannot accept multiple payment type when using an external gateway
        GenericValue orderHeader = null;
        try {
            orderHeader = this.delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting order header", module);
            errMsg = UtilProperties.getMessage(resource,"checkhelper.problems_getting_order_header", (cart != null ? cart.getLocale() : Locale.getDefault()));
            result = ServiceUtil.returnError(errMsg);
            return result;
        }
        if (orderHeader != null) {
            List paymentPrefs = null;
            try {
                paymentPrefs = orderHeader.getRelated("OrderPaymentPreference");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems getting order payments", module);
                errMsg = UtilProperties.getMessage(resource,"checkhelper.problems_getting_payment_preference", (cart != null ? cart.getLocale() : Locale.getDefault()));
                result = ServiceUtil.returnError(errMsg);
                return result;
            }
            if (paymentPrefs != null && paymentPrefs.size() > 0) {
                if (paymentPrefs.size() > 1) {
                    Debug.logError("Too many payment preferences, you cannot have more then one when using external gateways", module);
                }
                GenericValue paymentPreference = EntityUtil.getFirst(paymentPrefs);
                String paymentMethodTypeId = paymentPreference.getString("paymentMethodTypeId");
                if (paymentMethodTypeId.startsWith("EXT_")) {
                    String type = paymentMethodTypeId.substring(4);
                    result = ServiceUtil.returnSuccess();
                    result.put("type", type.toLowerCase());
                    return result;
                }
            }
            result = ServiceUtil.returnSuccess();
            result.put("type", "none");
            return result;
        } else {
            errMsg = UtilProperties.getMessage(resource,"checkhelper.problems_getting_order_header", (cart != null ? cart.getLocale() : Locale.getDefault()));
            result = ServiceUtil.returnError(errMsg);
            result.put("type", "error");
            return result;
        }
    }

    /**
     * Sets the shipping contact mechanism on the cart
     *
     * @param shippingContactMechId The identifier of the contact
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages
     */
    public Map finalizeOrderEntryShip(String shippingContactMechId) {
        Map result;
        String errMsg=null;
        //Verify the field is valid
        if (UtilValidate.isNotEmpty(shippingContactMechId)) {
            this.cart.setShippingContactMechId(shippingContactMechId);
            result = ServiceUtil.returnSuccess();
        } else {
            errMsg = UtilProperties.getMessage(resource,"checkhelper.enter_shipping_address", (cart != null ? cart.getLocale() : Locale.getDefault()));
            result = ServiceUtil.returnError(errMsg);
        }

        return result;
    }

    /**
     * Sets the options associated with the order
     *
     * @param shippingMethod The shipping method indicating the carrier and
     * shipment type to use
     * @param shippingInstructions Any additional handling instructions
     * @param maySplit "true" or anything else for <code>false</code>
     * @param giftMessage A message to have included for the recipient
     * @param isGift "true" or anything else for <code>false</code>
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages
     */
    public Map finalizeOrderEntryOptions(String shippingMethod, String shippingInstructions, String maySplit,
            String giftMessage, String isGift, String shipBeforeDate, String shipAfterDate) {

        Map result;
        String errMsg=null;
        //Verify the shipping method is valid
        if (UtilValidate.isNotEmpty(shippingMethod)) {
            int delimiterPos = shippingMethod.indexOf('@');
            String shipmentMethodTypeId = null;
            String carrierPartyId = null;

            if (delimiterPos > 0) {
                shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                carrierPartyId = shippingMethod.substring(delimiterPos + 1);
            }

            this.cart.setShipmentMethodTypeId(shipmentMethodTypeId);
            this.cart.setCarrierPartyId(carrierPartyId);
        } else {
            errMsg = UtilProperties.getMessage(resource,"checkhelper.select_shipping_method", (cart != null ? cart.getLocale() : Locale.getDefault()));
            result = ServiceUtil.returnError(errMsg);
        }

        //Set the remaining order options
        this.cart.setShippingInstructions(shippingInstructions);
        this.cart.setGiftMessage(giftMessage);
        this.cart.setMaySplit(Boolean.valueOf(maySplit));
        this.cart.setIsGift(Boolean.valueOf(isGift));

        // set ship before date
        if ((shipBeforeDate != null) && (shipBeforeDate.length() > 8)) {
           shipBeforeDate = shipBeforeDate.trim();
           if (shipBeforeDate.length() < 14) {
               shipBeforeDate = shipBeforeDate + " " + "00:00:00.000";
           }

           try {
               this.cart.setShipBeforeDate((Timestamp) ObjectType.simpleTypeConvert(shipBeforeDate, "Timestamp", null, null));
           } catch (Exception e) {
               errMsg = "Ship Before Date must be a valid date formed ";
               result = ServiceUtil.returnError(errMsg);
           }
        }

        // set ship after date
        if ((shipAfterDate != null) && (shipAfterDate.length() > 8)) {
           shipAfterDate = shipAfterDate.trim();
           if (shipAfterDate.length() < 14) {
               shipAfterDate = shipAfterDate + " " + "00:00:00.000";
           }

           try {
               this.cart.setShipAfterDate((Timestamp) ObjectType.simpleTypeConvert(shipAfterDate,"Timestamp", null, null));
            } catch (Exception e) {
              errMsg = "Ship After Date must be a valid date formed ";
              result = ServiceUtil.returnError(errMsg);
            }
        }

        result = ServiceUtil.returnSuccess();
        return result;
    }

    /**
     * Indicates whether the payment should be made offline or
     * whether further settings will be given for the electronic
     * payment method.
     *
     * @param paymentMthodType "offline" to indicate that is to be paid
     * offline, <code>null</code> or anything else for online.
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages
     */
    public Map finalizeOrderEntryMethodType(String paymentMthodType) {
        Map result = null;
        cart.clearPayments();

        if (paymentMthodType != null && paymentMthodType.equals("offline")) {
            cart.addPayment("EXT_OFFLINE");
        }

        result = ServiceUtil.returnSuccess();
        return result;
    }

    /**
     * Sets the payment ID to use during the checkout process
     *
     * @param checkOutPaymentId The type of payment to use, should
     * be "OFFLINE_PAYMENT" to indicate offlinem otherwise the payment ID
     * will be associated with the cart
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages. Includes the field "OFFLINE_PAYMENT"
     * containing a <code>Boolean</code> indicating whether it's an offline
     * payment or not.
     */
    public Map finalizeOrderEntryPayment(String checkOutPaymentId, Double amount, boolean singleUse, boolean append) {
        Map result = ServiceUtil.returnSuccess();

        if (UtilValidate.isNotEmpty(checkOutPaymentId)) {
            if (!checkOutPaymentId.equals("OFFLINE_PAYMENT")) {
                // clear out the old payments
                if (!append) {
                    cart.clearPayments();
                }
                cart.addPaymentAmount(checkOutPaymentId, amount, singleUse);
            } else {
                cart.clearPayments();
                result.put("OFFLINE_PAYMENT", new Boolean(true));
            }
        }

        return result;
    }

    /**
     * Defines the payment options for an order.
     *
     * @param params Contains the amount associated with
     * each <code>paymentMethodTypeId</code>.
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages. Includes the field "OFFLINE_PAYMENTS"
     * containing a <code>Boolean</code> indicating whether it's an offline
     * payment or not.
     */
    public Map finalizeOrderEntryOfflinePayments(Map params) {
        Map result = ServiceUtil.returnSuccess();
        List errorMessages = new ArrayList();
        String errMsg=null;

        // get a list of payment types
        List paymentTypes = null;
        try {
            paymentTypes = delegator.findAll("PaymentMethodType");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get payment method types from datasource", module);
        }
        if (paymentTypes != null) {
            Map paymentPrefs = new HashMap();
            double paymentTally = 0.00;
            Iterator pi = paymentTypes.iterator();
            while (pi.hasNext()) {
                GenericValue paymentMethodType = (GenericValue) pi.next();
                String paymentType = null;
                if (paymentMethodType != null && paymentMethodType.get("paymentMethodTypeId") != null) {
                    paymentType = paymentMethodType.getString("paymentMethodTypeId");
                }

                // get the amount by type
                double paymentAmount = 0.00;
                if (paymentType != null && !paymentType.equals("OFFLINE")) {
                    String amount = (String) params.get(paymentMethodType.getString("paymentMethodTypeId"));
                    if (amount != null && amount.length() > 0) {
                        try {
                            paymentAmount = NumberFormat.getNumberInstance().parse(amount).doubleValue();
                        } catch (java.text.ParseException pe) {
                            errMsg = UtilProperties.getMessage(resource,"checkhelper.problems_parsing_amount", (cart != null ? cart.getLocale() : Locale.getDefault()));
                            result = ServiceUtil.returnError(errMsg);
                            return result;
                        }
                    }
                }

                // only worry about types w/ an amount
                if (paymentAmount > 0.00) {
                    paymentPrefs.put(paymentType, new Double(paymentAmount));
                    paymentTally += paymentAmount;
                }
            }

            double cartTotal = cart.getGrandTotal();
            if (cartTotal != paymentTally) {
                errMsg = UtilProperties.getMessage(resource,"checkhelper.totals_do_not_match_order_total", (cart != null ? cart.getLocale() : Locale.getDefault()));
                result = ServiceUtil.returnError(errMsg);
                return result;
            } else {
                Set keySet = paymentPrefs.keySet();
                Iterator i = keySet.iterator();
                while (i.hasNext()) {
                    String type = (String) i.next();
                    Double amt = (Double) paymentPrefs.get(type);
                    cart.addPaymentAmount(type, amt);
                }
                result.put("OFFLINE_PAYMENTS", new Boolean(true));
            }
        }

        return result;
    }

    /**
     * Performs all the finalization settings and combines all the results.
     * This is a convenience method, primarily to match the original
     * code structure of the adapter class <code>CheckOutEvents</code>.
     * <p>
     * I would prefer to remove this altogether and move the method
     * {@link #addErrors(List, Map, Map) addErrors} to the utility
     * class {@link ServiceUtil ServiceUtil}
     *
     * @see CheckOutHelper#finalizeOrderEntryMethodType(String)
     * @see CheckOutHelper#finalizeOrderEntryOfflinePayments(Map)
     * @see CheckOutHelper#finalizeOrderEntryOptions(String, String, String, String, String, String, String)
     * @see CheckOutHelper#finalizeOrderEntryPayment(String, Double, boolean, boolean)
     * @see CheckOutHelper#finalizeOrderEntryShip(String)
     */
    public Map finalizeOrderEntry(String finalizeMode, String shippingContactMechId, String shippingMethod,
            String shippingInstructions, String maySplit, String giftMessage, String isGift, String methodType,
            String checkOutPaymentId, boolean isSingleUsePayment, boolean appendPayment, Map params,
            String shipBeforeDate, String shipAfterDate) {

        Map result = ServiceUtil.returnSuccess();
        Map errorMaps = new HashMap();
        Map callResult;
        List errorMessages = new ArrayList();

        // set the shipping method
        if (finalizeMode != null && finalizeMode.equals("ship")) {
            callResult = this.finalizeOrderEntryShip(shippingContactMechId);
            this.addErrors(errorMessages, errorMaps, callResult);
        }

        // set the options
        if (finalizeMode != null && finalizeMode.equals("options")) {
            callResult = this.finalizeOrderEntryOptions(shippingMethod, shippingInstructions, maySplit, giftMessage, isGift, shipBeforeDate, shipAfterDate);
            this.addErrors(errorMessages, errorMaps, callResult);
        }

        // payment option; if offline we skip the payment screen
        if (finalizeMode != null && finalizeMode.equals("payoption")) {
            callResult = this.finalizeOrderEntryMethodType(methodType);
            this.addErrors(errorMessages, errorMaps, callResult);
        }

        // set the payment
        if (finalizeMode != null && finalizeMode.equals("payment")) {
            Map selectedPaymentMethods = null;
            if (checkOutPaymentId != null) {
                callResult = this.finalizeOrderEntryPayment(checkOutPaymentId, null, isSingleUsePayment, appendPayment);
                this.addErrors(errorMessages, errorMaps, callResult);
                selectedPaymentMethods = UtilMisc.toMap(checkOutPaymentId, null);
            }
            callResult = checkGiftCard(params, selectedPaymentMethods);
            this.addErrors(errorMessages, errorMaps, callResult);
            if (errorMessages.size() == 0 && errorMaps.size() == 0) {
                String gcPaymentMethodId = (String) callResult.get("paymentMethodId");
                Double giftCardAmount = (Double) callResult.get("amount");
                Map gcCallRes = this.finalizeOrderEntryPayment(gcPaymentMethodId, giftCardAmount, true, true);
                this.addErrors(errorMessages, errorMaps, gcCallRes);
            }
        }

        // create offline payments
        if (finalizeMode != null && finalizeMode.equals("offline_payments")) {
            callResult = this.finalizeOrderEntryOfflinePayments(params);
            this.addErrors(errorMessages, errorMaps, callResult);
        }

        //See whether we need to return an error or not
        if (errorMessages.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessages);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }
        if (errorMaps.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_MAP, errorMaps);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }

        return result;
    }

    /**
     * Takes the result of an invocation and extracts any error messages
     * and adds them to the targetList. This will handle both List and String
     * error messags.
     *
     * @param targetList    The List to add the error messages to
     * @param targetMap The Map to add any Map error messages to
     * @param callResult The result from an invocation
     */
    private void addErrors(List targetList, Map targetMap, Map callResult) {
        List newList;
        Map.Entry entry;
        Iterator mapIter;
        Map errorMsgMap;
        StringBuffer outMsg;

        //See if there is a single message
        if (callResult.containsKey(ModelService.ERROR_MESSAGE)) {
            targetList.add(callResult.get(ModelService.ERROR_MESSAGE));
        }

        //See if there is a message list
        if (callResult.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
            newList = (List) callResult.get(ModelService.ERROR_MESSAGE_LIST);
            targetList.addAll(newList);
        }

        //See if there are an error message map
        if (callResult.containsKey(ModelService.ERROR_MESSAGE_MAP)) {
            errorMsgMap = (Map) callResult.get(ModelService.ERROR_MESSAGE_MAP);
            targetMap.putAll(errorMsgMap);
        }
    }

    public double availableAccountBalance(String billingAccountId) {
        GenericValue billingAccount = null;
        Double accountBalance = new Double(0.00);
        Double accountLimit = new Double(0.00);

        if (billingAccountId != null) {
            try {
                Map res = dispatcher.runSync("calcBillingAccountBalance", UtilMisc.toMap("billingAccountId", billingAccountId));
                billingAccount = (GenericValue) res.get("billingAccount");
                accountBalance = (Double) res.get("accountBalance");
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }
            if (billingAccount != null) {
                accountLimit = billingAccount.getDouble("accountLimit");
            }

            if (accountLimit == null) {
                accountLimit = new Double(0.00);
            }
            if (accountBalance == null) {
                accountBalance = new Double(0.00);
            }
        }

        double availableBalance = (accountLimit.doubleValue() - accountBalance.doubleValue());
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);
        String availableString = formatter.format(availableBalance);
        Double available = null;
        try {
            available = new Double(formatter.parse(availableString).doubleValue());
        } catch (ParseException e) {
            Debug.logError(e, "Problem getting parsed available amount", module);
        }
        //Debug.logInfo("Billing Account : " + billingAccountId + " - " + available, module);
        return available.doubleValue();
    }

    public Map makeBillingAccountMap(List paymentPrefs) {
        Map accountMap = new HashMap();
        if (paymentPrefs != null) {
            Iterator i = accountMap.keySet().iterator();
            while (i.hasNext()) {
                GenericValue pp = (GenericValue) i.next();
                if (pp.get("billingAccountId") != null) {
                    accountMap.put(pp.getString("billingAccountId"), pp.getDouble("maxAmount"));
                }
            }
        }
        return accountMap;
    }

    public Map validatePaymentMethods() {
        String errMsg = null;
        String billingAccountId = cart.getBillingAccountId();
        double billingAccountAmt = cart.getBillingAccountAmount();
        double availableAmount = this.availableAccountBalance(billingAccountId);
        if (billingAccountAmt > availableAmount) {
            Map messageMap = UtilMisc.toMap("billingAccountId", billingAccountId);
            errMsg = UtilProperties.getMessage(resource, "checkevents.not_enough_available_on_account", messageMap, (cart != null ? cart.getLocale() : Locale.getDefault()));
            return ServiceUtil.returnError(errMsg);
        }

        // payment by billing account only requires more checking
        List paymentMethods = cart.getPaymentMethodIds();
        List paymentTypes = cart.getPaymentMethodTypeIds();
        if (paymentTypes.contains("EXT_BILLACT") && paymentTypes.size() == 1 && paymentMethods.size() == 0) {
            if (cart.getGrandTotal() > availableAmount) {
                errMsg = UtilProperties.getMessage(resource, "checkevents.insufficient_credit_available_on_account", (cart != null ? cart.getLocale() : Locale.getDefault()));
                return ServiceUtil.returnError(errMsg);
            }
        }

        // validate any gift card balances
        this.validateGiftCardAmounts();

        // update the selected payment methods amount with valid numbers
        if (paymentMethods != null) {
            List nullPaymentIds = new ArrayList();
            Iterator i = paymentMethods.iterator();
            while (i.hasNext()) {
                String paymentMethodId = (String) i.next();
                Double paymentAmount = cart.getPaymentAmount(paymentMethodId);
                if (paymentAmount == null || paymentAmount.doubleValue() == 0) {
                    Debug.log("Found null paymentMethodId - " + paymentMethodId, module);
                    nullPaymentIds.add(paymentMethodId);
                }
            }
            Iterator npi = nullPaymentIds.iterator();
            while (npi.hasNext()) {
                String paymentMethodId = (String) npi.next();
                double selectedPaymentTotal = cart.getPaymentTotal();
                double requiredAmount = cart.getGrandTotal() - cart.getBillingAccountAmount();
                double nullAmount = requiredAmount - selectedPaymentTotal;
                if (nullAmount > 0) {
                    Debug.log("Reset null paymentMethodId - " + paymentMethodId + " / " + nullAmount, module);
                    cart.addPaymentAmount(paymentMethodId, nullAmount);
                }
            }
        }

        // verify the selected payment method amounts will cover the total
        double selectedPaymentTotal = cart.getPaymentTotal();
        double requiredAmount = cart.getGrandTotal() - cart.getBillingAccountAmount();
        if (paymentMethods != null && paymentMethods.size() > 0 && requiredAmount > selectedPaymentTotal) {
            Debug.logError("Required Amount : " + requiredAmount + " / Selected Amount : " + selectedPaymentTotal, module);
            errMsg = UtilProperties.getMessage(resource, "checkevents.payment_not_cover_this_order", (cart != null ? cart.getLocale() : Locale.getDefault()));
            return ServiceUtil.returnError(errMsg);
        }

        return ServiceUtil.returnSuccess();
    }

    public void validateGiftCardAmounts() {
        // get the product store
        GenericValue productStore = ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator);
        if (productStore != null && !"Y".equalsIgnoreCase(productStore.getString("checkGcBalance"))) {
            return;
        }

        // get the payment config
        String paymentConfig = ProductStoreWorker.getProductStorePaymentProperties(delegator, cart.getProductStoreId(), "GIFT_CARD", null, true);

        // get the gift card objects to check
        Iterator i = cart.getGiftCards().iterator();
        while (i.hasNext()) {
            GenericValue gc = (GenericValue) i.next();
            Map gcBalanceMap = null;
            double gcBalance = 0.00;
            try {
                Map ctx = UtilMisc.toMap("paymentConfig", paymentConfig);
                ctx.put("currency", cart.getCurrency());
                ctx.put("cardNumber", gc.getString("cardNumber"));
                ctx.put("pin", gc.getString("pinNumber"));
                gcBalanceMap = dispatcher.runSync("balanceInquireGiftCard", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }
            if (gcBalanceMap != null) {
                Double bal = (Double) gcBalanceMap.get("balance");
                if (bal != null) {
                    gcBalance = bal.doubleValue();
                }
            }

            // get the bill-up to amount
            Double billUpTo = cart.getPaymentAmount(gc.getString("paymentMethodId"));

            // null bill-up to means use the full balance || update the bill-up to with the balance
            if (billUpTo == null || billUpTo.doubleValue() == 0 || gcBalance < billUpTo.doubleValue()) {
                cart.addPaymentAmount(gc.getString("paymentMethodId"), new Double(gcBalance));
            }
        }
    }
}
