/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.commonapp.thirdparty.cybersource;

import java.net.*;
import java.text.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

import com.cybersource.ics.base.message.*;
import com.cybersource.ics.base.exception.*;
import com.cybersource.ics.client.message.*;
import com.cybersource.ics.client.*;

/**
 * CyberSource Integration Services
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class CSPaymentServices {

    public static final String module = CSPaymentServices.class.getName();

    public static Map ccProcessor(DispatchContext dctx, Map context) {
        String configUrlStr = (String) context.get("configUrl");
        URL configUrl = null;
        try {
            configUrl = new URL(configUrlStr); 
        } catch (MalformedURLException e) {
            Debug.logError(e, "Bad URL for cybersource payment properties file; fatal error.", module);
            return ServiceUtil.returnError("Bad properties URL; cannot find settings");
        }
        
        boolean fraudScore = true;
        boolean enableDav = true;
        boolean autoBill = false;
        
        if (configUrl != null) {
            fraudScore = UtilProperties.propertyValueEqualsIgnoreCase(configUrl, "fraudScore", "Y");
            enableDav = UtilProperties.propertyValueEqualsIgnoreCase(configUrl, "enableDav", "Y");
            autoBill = UtilProperties.propertyValueEqualsIgnoreCase(configUrl, "autoBill", "Y");            
        } else {        
            fraudScore = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "fraudScore", "Y");
            enableDav = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "enableDav", "Y");
            autoBill = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "autoBill", "Y");
        }

        StringBuffer apps = new StringBuffer();
        apps.append("ics_auth");
        if (fraudScore)
            apps.append(",ics_score");
        if (enableDav)
            apps.append(",ics_dav");
        if (autoBill)
            apps.append(",ics_bill");
        return process(dctx, context, apps.toString());
    }

    private static Map process(DispatchContext dctx, Map context, String appString) {
        Map result = new HashMap();
        String orderId = (String) context.get("orderId");
        String currency = (String) context.get("currency");
        String configUrl = (String) context.get("configUrl");

        // Some default values
        Properties properties = null;
        String defCur = null;
        String timeout = null;
        String retryWait = null;
        String avsDeclineCodes = null;
        boolean disableAvs = false;
        boolean enableRetry = true;
        
        if (configUrl != null) {
            URL propsUrl = null;
            try {
                propsUrl = new URL(configUrl);
            } catch (MalformedURLException e) {
                Debug.logError(e, "Bad URL for cybersource payment properties file; fatal error.", module);
                return ServiceUtil.returnError("Bad properties URL; cannot find settings");
            }
            properties = UtilProperties.getProperties(propsUrl);
            defCur = UtilProperties.getPropertyValue(propsUrl, "defaultCurrency", "USD");
            timeout = UtilProperties.getPropertyValue(propsUrl, "timeout", "90");
            retryWait = UtilProperties.getPropertyValue(propsUrl, "retryWait", "90");
            avsDeclineCodes = UtilProperties.getPropertyValue(propsUrl, "avsDeclineCodes", "");
            disableAvs = UtilProperties.propertyValueEqualsIgnoreCase(propsUrl, "disableAuthAvs", "Y");
            enableRetry = UtilProperties.propertyValueEqualsIgnoreCase(propsUrl, "enableRetry", "Y");            
        } else {
            properties = UtilProperties.getProperties("cybersource.properties");                
            defCur = UtilProperties.getPropertyValue("cybersource.properties", "defaultCurrency", "USD");
            timeout = UtilProperties.getPropertyValue("cybersource.properties", "timeout", "90");
            retryWait = UtilProperties.getPropertyValue("cybersource.properties", "retryWait", "90");
            avsDeclineCodes = UtilProperties.getPropertyValue("cybersource.properties", "avsDeclineCodes", "");
            disableAvs = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "disableAuthAvs", "Y");
            enableRetry = UtilProperties.propertyValueEqualsIgnoreCase("cybersource.properties", "enableRetry", "Y");
        }

        ICSClientRequest request = null;
        ICSClient client = null;
        ICSReply reply = null;

        try {
            client = new ICSClient(properties);
            request = buildRequest(client, context);
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

        // some parameters
        GenericValue person = (GenericValue) context.get("contactPerson");

        // basic info
        request.setMerchantID(client.getMerchantID());
        request.setCurrency((currency == null ? defCur : currency));
        request.addApplication(appString);
        request.setMerchantRefNo(orderId);

        // set the timeout/restart info
        request.setRetryStart(retryWait);
        request.setTimeout(timeout);
        request.setRetryEnabled(enableRetry ? "yes" : "no");

        // set AVS and decline codes
        request.setDisableAVS(disableAvs);
        if (!disableAvs && avsDeclineCodes != null && avsDeclineCodes.length() > 0) {
            String delcineString = getAvsDeclineCodes(person, avsDeclineCodes);
            request.setField("decline_avs_flags", avsDeclineCodes);
        }

        // verbose debugging
        if (Debug.verboseOn()) Debug.logVerbose("---- CyberSource Request ----", module);
        if (Debug.verboseOn()) Debug.logVerbose("[REQ]: " + request, module);
        if (Debug.verboseOn()) Debug.logVerbose("---- End Request ----", module);

        // send the request
        try {
            reply = client.send(request);
        } catch (ICSException ie) {
            ie.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: ICS Problem (" + ie.getMessage() + ").");
            return result;
        }

        // more verbose debugging
        if (Debug.verboseOn()) Debug.logVerbose("---- CyberSource Response ----", module);
        if (Debug.verboseOn()) Debug.logVerbose("[RES]: " + reply, module);
        if (Debug.verboseOn()) Debug.logVerbose("---- End Reply ----", module);

        // process the response
        processAuthResult(reply, result);
        return result;
    }

    private static ICSClientRequest buildRequest(ICSClient client, Map context)
        throws GenericEntityException, GeneralException, ICSException {

        // Create a new ICSClientRequest Object.
        ICSClientRequest request = new ICSClientRequest(client);

        // Person Info
        GenericValue person = (GenericValue) context.get("contactPerson");

        request.setCustomerFirstName(person.getString("firstName"));
        request.setCustomerLastName(person.getString("lastName"));

        // Contact Info
        GenericValue email = (GenericValue) context.get("contactEmail");

        request.setCustomerEmailAddress(email.getString("infoString"));

        // Phone number seems to not be used; possibly only for reporting.

        // Payment Info
        GenericValue creditCard = (GenericValue) context.get("creditCard");
        List expDateList = StringUtil.split(creditCard.getString("expireDate"), "/");

        request.setCustomerCreditCardNumber(creditCard.getString("cardNumber"));
        request.setCustomerCreditCardExpirationMonth((String) expDateList.get(0));
        request.setCustomerCreditCardExpirationYear((String) expDateList.get(1));

        // Payment Contact Info
        GenericValue billingAddress = (GenericValue) context.get("billingAddress");

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
        GenericValue shippingAddress = (GenericValue) context.get("shippingAddress");

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
        Double processAmount = (Double) context.get("processAmount");
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setGroupingUsed(false);
        String totalStr = nf.format(processAmount);
        ICSClientOffer mainOffer = new ICSClientOffer();

        mainOffer.setAmount(totalStr.substring(1));
        request.addOffer(mainOffer);

        // Create the offers (one for each line item)
        List orderItems = (List) context.get("orderItems");
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
            // Set the amount to 0.0000 -- we will send a total too.
            offer.setAmount("0.0000");

            // offer.setProductCode("electronic_software");
            // offer.setPackerCode("portland10");

            request.addOffer(offer);
        }
        return request;
    }

    private static void processAuthResult(ICSReply reply, Map result) {
        try {
            if (reply.getReplyCode() > 0) {
                result.put("authCode", reply.getField("auth_auth_code"));
                result.put("authResult", new Boolean(true));
            } else {
                result.put("authResult", new Boolean(false));
            }
            if (reply.getField("auth_auth_amount") != null)
                result.put("processAmount", new Double(reply.getField("auth_auth_amount")));
            else 
                result.put("processAmount", new Double(0.00));
            result.put("authRefNum", reply.getField("request_id"));
            result.put("authFlag", reply.getField("ics_rflag"));
            result.put("authMessage", reply.getErrorMessage());
            result.put("avsCode", reply.getField("auth_auth_avs"));
            result.put("scoreCode", reply.getField("score_score_result"));
        } catch (ICSException ie) {
            ie.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: ICS problem (" + ie.getMessage() + ").");
        }
    }

    private static String getAvsDeclineCodes(GenericValue person, String defaultString) {
        GenericValue avsOverride = null;

        try {
            avsOverride = person.getDelegator().findByPrimaryKey("PartyIcsAvsOverride",
                        UtilMisc.toMap("partyId", person.getString("partyId")));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        if (avsOverride != null && avsOverride.get("avsDeclineString") != null)
            return avsOverride.getString("avsDeclineString");
        else
            return defaultString;
    }
}
