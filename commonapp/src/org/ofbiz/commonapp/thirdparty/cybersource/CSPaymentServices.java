/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project and repected authors.
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

package org.ofbiz.commonapp.thirdparty.cybersource;

import java.text.*;
import java.net.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.order.order.*;
import org.ofbiz.commonapp.party.contact.ContactHelper;

import com.cybersource.ics.base.message.*;
import com.cybersource.ics.base.exception.*;
import com.cybersource.ics.client.message.*;
import com.cybersource.ics.client.*;

/**
 * CyberSource Integration Services
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    1.0
 * @created    May 29, 2002
 */
public class CSPaymentServices {

    public static final String module = CSPaymentServices.class.getName();

    public static Map authorize(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();

        String currency = (String) context.get("currency");
        String orderId = (String) context.get("orderId");

        GenericValue orderHeader = null;
        Collection paymentPreferences = null;

        // Some default values
        StringBuffer apps = new StringBuffer();
        boolean fraudScore = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "fraudScore", "Y");
        boolean enableDAV = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "enableDAV", "Y");
        boolean autoBill = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "autoBill", "Y");
        boolean disableAVS = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "disableAuthAVS", "Y");
        boolean enableRetry = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "enableRetry", "Y");

        String defCur = UtilProperties.getPropertyValue("cybersource.properties", "defaultCurrency", "USD");
        String timeout = UtilProperties.getPropertyValue("cybersource.properties", "timeout", "90");
        String retryWait = UtilProperties.getPropertyValue("cybersource.properties", "retryWait", "90");

        // TODO: move this below and implement EFT
        apps.append("ics_auth");
        if (fraudScore)
            apps.append(",ics_score");
        if (enableDAV)
            apps.append(",ics_dav");
        if (autoBill)
            apps.append(",ics_bill");

        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            paymentPreferences = orderHeader.getRelatedOrderBy("OrderPaymentPreference", UtilMisc.toList("maxAmount"));

            // filter out payment prefs which have been authorized declined or settled.
            List exprs = UtilMisc.toList(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_AUTHORIZED"),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_DECLINED"),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_SETTLED"));
            paymentPreferences = EntityUtil.filterByAnd(paymentPreferences, exprs);
        } catch (GenericEntityException gee) {
            gee.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get order information (" + gee.getMessage() + ").");
            return result;
        }

        boolean lastWasOkay = true;
        Iterator prefIterator = paymentPreferences.iterator();
        while (prefIterator.hasNext() && lastWasOkay) {
            GenericValue paymentPreference = (GenericValue) prefIterator.next();
            ICSClientRequest request = null;
            ICSClient client = null;
            ICSReply reply = null;

            try {
                client = new ICSClient(UtilProperties.getProperties("cybersource.properties"));
                request = buildRequest(client, orderHeader, paymentPreference);
                if (client == null)
                    throw new GeneralException("ICS returned a null client.");
            } catch (ICSException ie) {
                ie.printStackTrace();
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: ICS Problem (" + ie.getMessage() + ").");
                return result;
            } catch (GenericEntityException gee) {
                gee.printStackTrace();
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get order information (" + gee.getMessage() + ").");
                return result;
            } catch (GeneralException ge) {
                ge.printStackTrace();
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: GeneralException (" + ge.getMessage() + ").");
                return result;
            }

            // Basic Info
            request.setMerchantID(client.getMerchantID());
            request.addApplication(apps.toString());
            request.setMerchantRefNo(orderId);
            request.setDisableAVS(disableAVS);
            request.setRetryStart(retryWait);
            request.setTimeout(timeout);
            request.setRetryEnabled(enableRetry ? "yes" : "no");
            request.setCurrency((currency == null ? defCur : currency));

            Debug.logVerbose("---- CyberSource Request To: " + client.url.toString() + " ----", module);
            Debug.logVerbose("[REQ]: " + request, module);
            Debug.logVerbose("---- End Request ----", module);

            try {
                reply = client.send(request);
            } catch (ICSException ie) {
                ie.printStackTrace();
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: ICS Problem (" + ie.getMessage() + ").");
                return result;
            }

            Debug.logVerbose("---- CyberSource Response ----", module);
            Debug.logVerbose("[RES]: " + reply, module);
            Debug.logVerbose("---- End Reply ----", module);


            result = processAuthResult(reply, result, paymentPreference);
            if (result != null && result.containsKey("authResponse")) {
                String authResp = (String) result.get("authResponse");
                if ("FAIL".equalsIgnoreCase(authResp))
                    lastWasOkay = false;
            }
        }
        return result;
    }

    public static Map settle(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String currency = (String) context.get("currency");
        try {
            capture(delegator, orderId, currency);
        } catch (GeneralException e) {
            String message = "Cannot process order: " + orderId + "(" + e.getMessage() + ").";
            Debug.logError(e, message, module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, message);
            return result;
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map settleAll(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        Collection payments = null;
        try {
            payments = delegator.findByAnd("OrderHeaderAndPaymentPref",
                    UtilMisc.toMap("orderStatusId", "ORDER_SHIPPED", "paymentStatusId", "PAYMENT_AUTHORIZED"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "ERROR: Cannot get orders from view entity (" + e.getMessage() + ").");
            return ServiceUtil.returnError("Cannot get orders from view entity (" + e.getMessage() + ").");
        }
        List errors = new ArrayList();
        if (payments == null) {
            Debug.logInfo("No orders found to process.", module);
            result.put("ordersProcessed", new ArrayList());
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            return result;
        }
        Iterator i = payments.iterator();

        List processed = new ArrayList();
        while (i.hasNext()) {
            GenericValue p = (GenericValue) i.next();
            try {
                capture(delegator, p.getString("orderId"), null);
                processed.add(p.get("orderId"));
            } catch (GeneralException e) {
                Debug.logError(e, "Processing Error: " + e.getMessage(), module);
                errors.add(e.getMessage());
            }
        }
        if (errors.size() > 0)
            return ServiceUtil.returnError(errors);
        else {
            result.put("ordersProcessed", processed);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            return result;
        }
    }

    public static Map dav(DispatchContext dctx, Map context) {
        return new HashMap();
    }

    public static Map fraudScore(DispatchContext dctx, Map context) {
        return new HashMap();
    }

    public static Map taxCalc(DispatchContext dctx, Map context) {
        return new HashMap();
    }

    private static ICSClientRequest buildRequest(ICSClient client, GenericValue orderHeader, GenericValue paymentPref)
            throws GenericEntityException, GeneralException, ICSException {

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        GenericValue paymentMethod = paymentPref.getRelatedOne("PaymentMethod");

        Debug.logVerbose("PaymentMethod: " + paymentMethod, module);

        if (paymentMethod.get("paymentMethodTypeId") != null &&
                !paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD"))
            throw new GeneralException("Payment method is not a credit card.");

        GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");
        GenericValue billingAddress = creditCard.getRelatedOne("PostalAddress");

        //Debug.logVerbose("PaymentMethod: " + paymentMethod, module);
        //Debug.logVerbose("CreditCard: " + creditCard, module);
        //Debug.logVerbose("BillingAddress: " + billingAddress, module);

        if (billingAddress == null || creditCard == null)
            throw new GeneralException("Null billing or payment information");

        Collection orderItems = (Collection) orh.getOrderItems();

        // Create a new ICSClientRequest Object.
        ICSClientRequest request = new ICSClientRequest(client);

        // Person Info
        GenericValue person = orh.getBillToPerson();
        request.setCustomerFirstName(person.getString("firstName"));
        request.setCustomerLastName(person.getString("lastName"));

        // Contact Info
        Collection emails = ContactHelper.getContactMech(person.getRelatedOne("Party"), "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
        if (emails != null && emails.size() > 0) {
            GenericValue em = (GenericValue) emails.iterator().next();
            request.setCustomerEmailAddress(em.getString("infoString"));
        }
        // Phone number seems to not be used; possibly only for reporting.

        // Payment Info
        List expDateList = StringUtil.split(creditCard.getString("expireDate"), "/");
        request.setCustomerCreditCardNumber(creditCard.getString("cardNumber"));
        request.setCustomerCreditCardExpirationMonth((String) expDateList.get(0));
        request.setCustomerCreditCardExpirationYear((String) expDateList.get(1));

        // Payment Contact Info
        request.setBillAddress1(billingAddress.getString("address1"));
        if (billingAddress.get("address2") != null)
            request.setBillAddress2(billingAddress.getString("address2"));
        request.setBillCity(billingAddress.getString("city"));
        String bCountry = billingAddress.get("countryGeoId") != null ? billingAddress.getString("countryGeoId") : "USA";
        request.setBillCountry(bCountry);
        request.setBillZip(billingAddress.getString("postalCode"));
        if (billingAddress.get("stateProvinceGeoId") != null)
            request.setBillState(billingAddress.getString("stateProvinceGeoId"));

        // Order Shipping Information
        GenericValue shippingAddress = orh.getShippingAddress();
        request.setShipToAddress1(shippingAddress.getString("address1"));
        if (shippingAddress.get("address2") != null)
            request.setShipToAddress2(shippingAddress.getString("address2"));
        request.setShipToCity(shippingAddress.getString("city"));
        String sCountry = shippingAddress.get("countryGeoId") != null ? shippingAddress.getString("countryGeoId") : "USA";
        request.setShipToCountry(sCountry);
        request.setShipToZip(shippingAddress.getString("postalCode"));
        if (shippingAddress.get("stateProvinceGeoId") != null)
            request.setShipToState(shippingAddress.getString("stateProvinceGeoId"));

        // Send over a line item total offer w/ the total for billing; Don't trust CyberSource for calc.
        ICSClientOffer mainOffer = new ICSClientOffer();
        mainOffer.setAmount(new Double(orh.getTotalPrice()).toString());
        request.addOffer(mainOffer);

        // Create the offers (one for each line item)
        Iterator itemIterator = orderItems.iterator();
        while (itemIterator.hasNext()) {
            ICSClientOffer offer = new ICSClientOffer();
            GenericValue item = (GenericValue) itemIterator.next();
            GenericValue product = item.getRelatedOne("Product");

            offer.setProductName(product.getString("productName"));
            offer.setMerchantProductSKU(product.getString("productId"));

            // Get the quantity..
            Double quantity = item.getDouble("quantity");

            // Test quantity if INT pass as is; if not pass as 1
            long roundQ = Math.round(quantity.doubleValue());
            Double rounded = new Double(new Long(roundQ).toString());
            if (rounded.doubleValue() != quantity.doubleValue())
                offer.setQuantity(1);
            else
                offer.setQuantity(quantity.intValue());
            // Set the amount to 0.0099 -- we will send a total too.
            offer.setAmount("0.0000");

            //offer.setProductCode("electronic_software");
            //offer.setPackerCode("portland10");

            request.addOffer(offer);
        }
        return request;
    }

    private static void capture(GenericDelegator delegator, String orderId, String currency) throws GeneralException {
        GenericValue orderHeader = null;
        Collection paymentPreferences = null;

        // Some default values
        boolean disableAVS = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "disableBillAVS", "Y");
        boolean enableRetry = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "enableRetry", "Y");

        String defCur = UtilProperties.getPropertyValue("cybersource.properties", "defaultCurrency", "USD");
        String timeout = UtilProperties.getPropertyValue("cybersource.properties", "timeout", "90");
        String retryWait = UtilProperties.getPropertyValue("cybersource.properties", "retryWait", "90");

        String appString = "ics_bill";

        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            paymentPreferences = orderHeader.getRelatedOrderBy("OrderPaymentPreference", UtilMisc.toList("maxAmount"));

            // pull out payment prefs which have been authorized.
            List exprs = UtilMisc.toList(new EntityExpr("statusId", EntityOperator.EQUALS, "PAYMENT_AUTHORIZED"));
            paymentPreferences = EntityUtil.filterByAnd(paymentPreferences, exprs);
        } catch (GenericEntityException gee) {
            throw new GeneralException("ERROR: Could not get order information (" + gee.getMessage() + ").", gee);
        }

        boolean lastWasOkay = true;
        Iterator prefIterator = paymentPreferences.iterator();
        while (prefIterator.hasNext() && lastWasOkay) {
            GenericValue paymentPreference = (GenericValue) prefIterator.next();
            ICSClientRequest request = null;
            ICSClient client = null;
            ICSReply reply = null;

            try {
                client = new ICSClient(UtilProperties.getProperties("cybersource.properties"));
                request = buildRequest(client, orderHeader, paymentPreference);
                if (client == null)
                    throw new GeneralException("ICS returned a null client.");
            } catch (ICSException ie) {
                throw new GeneralException("ICS ERROR: " + ie.getMessage(), ie);
            } catch (GenericEntityException gee) {
                throw new GeneralException("ERROR: Could not get order information (" + gee.getMessage() + ").", gee);
            }

            // Basic Info
            request.setMerchantID(client.getMerchantID());
            request.addApplication(appString);
            request.setMerchantRefNo(orderId);
            request.setDisableAVS(disableAVS);
            request.setRetryStart(retryWait);
            request.setTimeout(timeout);
            request.setRetryEnabled(enableRetry ? "yes" : "no");
            request.setCurrency((currency == null ? defCur : currency));

            // Set the auth reference number
            String authRefNum = paymentPreference.getString("authRefNum");
            if (authRefNum == null)
                throw new GeneralException("No auth reference number to process: " + paymentPreference.getString("orderPaymentPreferenceId"));
            request.setAuthRequestId(authRefNum);

            Debug.logVerbose("---- CyberSource Request To: " + client.url.toString() + " ----", module);
            Debug.logVerbose("[REQ]: " + request, module);
            Debug.logVerbose("---- End Request ----", module);

            try {
                reply = client.send(request);
            } catch (ICSException ie) {
                throw new GeneralException("ICS ERROR: " + ie.getMessage(), ie);
            }

            Debug.logVerbose("---- CyberSource Response ----", module);
            Debug.logVerbose("[RES]: " + reply, module);
            Debug.logVerbose("---- End Reply ----", module);


            lastWasOkay = processBillResult(reply, paymentPreference);
            if (!lastWasOkay)
                throw new GeneralException("Payment: " + paymentPreference.getString("orderPaymentPreferenceId") + " could not be captured.");
        }
    }

    private static boolean processBillResult(ICSReply reply, GenericValue paymentPreference) {
        String paidTo = UtilProperties.getPropertyValue("cybersource.properties", "paidTo", "Company");
        GenericDelegator delegator = paymentPreference.getDelegator();
        GenericValue payment = null;
        try {
            if (reply.getReplyCode() > 0) {
                Long paymentId = delegator.getNextSeqId("Payment");
                if (paymentId == null) {
                    Debug.logError("Cannot get sequence ID for Payment entity.");
                    return false;
                }
                GenericValue orderRole = EntityUtil.getFirst(delegator.findByAnd("OrderRole",
                        UtilMisc.toMap("orderId", paymentPreference.get("orderId"), "roleTypeId", "BILL_TO_CUSTOMER")));
                payment = delegator.makeValue("Payment", UtilMisc.toMap("paymentId", paymentId.toString(),
                        "paymentTypeId", "RECEIPT", "paymentMethodTypeId", paymentPreference.get("paymentMethodTypeId"),
                        "paymentMethodId", paymentPreference.get("paymentMethodId"), "partyIdTo", paidTo,
                        "partyIdFrom", orderRole.get("partyId")));

                payment.set("paymentPreference", paymentPreference.get("orderPaymentPreferenceId"));
                payment.set("amount", new Double(reply.getField("bill_bill_amount")));
                payment.set("effectiveDate", UtilDateTime.nowTimestamp());
                payment.set("paymentRefNum", reply.getField("bill_trans_ref_no"));

                paymentPreference.set("authRefNum", reply.getField("request_id"));
                paymentPreference.set("statusId", "PAYMENT_SETTLED");
                paymentPreference.store();
                delegator.create(payment);
                return true;
            } else {
                Debug.logError("Capture failed - ICS: " + reply.getErrorMessage(), module);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem storing processing updates. Logging the data:", module);
            Debug.logError("PaymentPreference: " + paymentPreference, module);
            Debug.logError("Payment: " + payment, module);
            Debug.logError("ICS Reply: " + reply, module);
        } catch (ICSException ie) {
            Debug.logError(ie, "ICS Error: " + ie.getMessage(), module);
        }
        return false;
    }

    private static Map processAuthResult(ICSReply reply, Map result, GenericValue paymentPreference) {
        // Process the return codes and return a nice response
        try {
            if (reply.getReplyCode() > 0) {
                paymentPreference.set("authCode", reply.getField("auth_auth_code"));
                paymentPreference.set("statusId", "PAYMENT_AUTHORIZED");
                result.put("authResponse", "SUCCESS");
            } else {
                paymentPreference.set("statusId", "PAYMENT_DECLINED");
                result.put("authResponse", "FAIL");
            }
            paymentPreference.set("authRefNum", reply.getField("request_id"));
            paymentPreference.set("authFlag", reply.getField("ics_rflag"));
            paymentPreference.set("authMessage", reply.getErrorMessage());
            paymentPreference.set("authDate", UtilDateTime.nowTimestamp());
            paymentPreference.store();
        } catch (ICSException ie) {
            ie.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: ICS problem (" + ie.getMessage() + ").");
            return result;
        } catch (GenericEntityException gee) {
            gee.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not store payment info (" + gee.getMessage() + ").");
            return result;
        }

        return result;
    }
}
