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

    public static Map authorizeCC(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();

        String currency = (String) context.get("currency");
        String orderId = (String) context.get("orderId");
        String paymentMethodId = (String) context.get("paymentMethodId");

        // Create a new ICSClient using the cybersource properties found on global classpath.
        ICSClientRequest request = null;
        ICSClient client = null;
        ICSReply reply = null;

        try {
            client = new ICSClient(UtilProperties.getProperties("cybersource.properties"));
            request = buildAuthRequest(client, delegator, orderId, paymentMethodId);
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

        StringBuffer apps = new StringBuffer();
        boolean fraudScore = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "fraudScore", "Y");
        boolean enableDAV = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "enableDAV", "Y");
        boolean autoBill = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "autoBill", "Y");
        boolean disableAVS = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "disableAVS", "Y");
        boolean enableRetry = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "enableRetry", "Y");

        String defCur = UtilProperties.getPropertyValue("cybersource.properties", "defaultCurrency", "USD");
        String timeout = UtilProperties.getPropertyValue("cybersource.properties", "timeout", "90");
        String retryWait = UtilProperties.getPropertyValue("cybersource.properties", "retryWait", "90");

        apps.append("ics_auth");
        if (fraudScore)
            apps.append(",ics_score");
        if (enableDAV)
            apps.append(",ics_dav");
        if (autoBill)
            apps.append(",ics_bill");

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

        return processResult(reply, result);
    }

    public static Map billCC(DispatchContext dctx, Map context) {
        return new HashMap();
    }

    public static Map creditCC(DispatchContext dctx, Map context) {
        return new HashMap();
    }

    public static Map debitECP(DispatchContext dctx, Map context) {
        return new HashMap();
    }

    public static Map creditECP(DispatchContext dctx, Map context) {
        return new HashMap();
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

    private static ICSClientRequest buildAuthRequest(ICSClient client, GenericDelegator delegator,
                                                     String orderId, String paymentMethodId) throws GenericEntityException, GeneralException, ICSException {
        GenericValue orderHeader = null;
        GenericValue paymentMethod = null;
        GenericValue creditCard = null;
        GenericValue billingAddress = null;
        Collection adjustments = null;

        orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        adjustments = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId));
        paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));

        if (paymentMethod.get("paymentMethodTypeId") != null &&
                !paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD"))
            throw new GeneralException("Payment method is not a credit card.");

        creditCard = paymentMethod.getRelatedOne("CreditCard");
        billingAddress = creditCard.getRelatedOne("PostalAddress");

        if (billingAddress == null || creditCard == null)
            throw new GeneralException("Null billing or payment information");

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        Collection orderItems = orh.getOrderItems();

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
        request.setBillCountry(billingAddress.getString("countryGeoId"));
        request.setBillZip(billingAddress.getString("postalCode"));
        if (billingAddress.get("stateProvinceGeoId") != null)
            request.setBillState(billingAddress.getString("stateProvinceGeoId"));

        // Order Shipping Information
        GenericValue shippingAddress = orh.getShippingAddress();
        request.setShipToAddress1(shippingAddress.getString("address1"));
        if (shippingAddress.get("address2") != null)
            request.setShipToAddress2(shippingAddress.getString("address2"));
        request.setShipToCity(shippingAddress.getString("city"));
        request.setShipToCountry(shippingAddress.getString("countryGeoId"));
        request.setShipToZip(shippingAddress.getString("postalCode"));
        if (shippingAddress.get("stateProvinceGeoId") != null)
            request.setShipToState(shippingAddress.getString("stateProvinceGeoId"));

        // Create the offers (one for each line item)
        Iterator itemIterator = orderItems.iterator();
        while (itemIterator.hasNext()) {
            ICSClientOffer offer = new ICSClientOffer();
            GenericValue item = (GenericValue) itemIterator.next();
            GenericValue product = item.getRelatedOne("Product");

            offer.setProductName(product.getString("productName"));
            offer.setMerchantProductSKU(product.getString("productId"));

            // Get the quantity and price to do some testing.
            Double quantity = item.getDouble("quantity");
            Double price = item.getDouble("unitPrice");

            // Test quantity if INT pass as is; if not do the math and pass price w/ qty 1
            long roundQ = Math.round(quantity.doubleValue());
            Double rounded = new Double(new Long(roundQ).toString());
            if (rounded.doubleValue() != quantity.doubleValue()) {
                offer.setAmount((price.doubleValue() * quantity.doubleValue()));
                offer.setQuantity(1);
            } else {
                offer.setAmount(price.doubleValue());
                offer.setQuantity(quantity.intValue());
            }

            //offer.setProductCode("electronic_software");
            //offer.setPackerCode("portland10");

            Collection taxItems = EntityUtil.filterByAnd(adjustments, UtilMisc.toMap("orderAdjustmentTypeId",
                    "SALES_TAX", "orderItemSeqId", item.getString("orderItemSeqId")));
            double taxAmount = 0.00;
            Iterator taxIt = taxItems.iterator();
            while (taxIt.hasNext()) {
                GenericValue adj = (GenericValue) taxIt.next();
                taxAmount += adj.getDouble("amount").doubleValue();
            }
            if (taxAmount > 0.00) {
                Double tax = new Double(taxAmount);
                offer.setTaxAmount(tax.toString());
            }
            request.addOffer(offer);
        }
        return request;
    }

    private static Map processResult(ICSReply reply, Map result) {
        // Process the return codes and return a nice response
        return result;
    }
}
