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
package org.ofbiz.commonapp.thirdparty.worldpay;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.product.catalog.*;

import com.worldpay.core.*;
import com.worldpay.protocols.http.HTTPURL;
import com.worldpay.protocols.http.URLParameters;
import com.worldpay.select.*;
import com.worldpay.util.Currency;
import com.worldpay.util.CurrencyAmount;

/**
 * WorldPay Select Pro Events/Services
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class WorldPayEvents {
    
    public static final String module = WorldPayEvents.class.getName();
    
    public static String worldPayRequest(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        
        // we need the websiteId for the correct properties file
        String webSiteId = CatalogWorker.getWebSiteId(request);
        
        // get the orderId from the request, stored by previous event(s)
        String orderId = (String) request.getAttribute("order_id");
        
        if (orderId == null) {
            Debug.logError("Problems getting orderId, was not found in request", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>OrderID not found, please contact customer service.");
            return "error";
        }
        
        // get the order header for total and other information
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot not get OrderHeader from datasource", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems getting order information, please contact customer service.");
            return "error";
        }
        
        // get the contact address to pass over
        GenericValue contactAddress = null;
        try {
            List addresses = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "BILLING_LOCATION"));
            if (addresses == null || addresses.size() == 0)
                addresses = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION"));
            GenericValue contactMech = EntityUtil.getFirst(addresses); 
            contactAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", contactMech.getString("contactMechId")));                      
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting order contact information", module);
        }
        
        // get the country geoID
        GenericValue countryGeo = null;
        if (contactAddress != null) {
            try {
                countryGeo = contactAddress.getRelatedOne("CountryGeo");
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Problems getting country geo entity", module);                
            }
        }
        
        // string of customer's name
        String name = null;
        if (contactAddress != null) {
            if (contactAddress.get("attnName") != null && contactAddress.getString("attnName").length() > 0)
                name = contactAddress.getString("attnName");
            else if (contactAddress.get("toName") != null && contactAddress.getString("toName").length() > 0)
                name = contactAddress.getString("toName");
        }
        
        // build an address string
        StringBuffer address = null;
        if (contactAddress != null) {
            address = new StringBuffer();
            if (contactAddress.get("address1") != null) {            
                address.append(contactAddress.getString("address1").trim());
            }
            if (contactAddress.get("address2") != null) {
                if (address.length() > 0)
                    address.append("&#10;");
                address.append(contactAddress.getString("address2").trim());                
            }
            if (contactAddress.get("city") != null) {
                if (address.length() > 0)
                    address.append("&#10;");
                address.append(contactAddress.getString("city").trim());                
            }
            if (contactAddress.get("stateProvinceGeoId") != null) {
                if (contactAddress.get("city") != null)
                    address.append(", ");
                address.append(contactAddress.getString("stateProvinceGeoId").trim());
            }            
        }
        
        // get the telephone number to pass over
        String phoneNumber = null;
        GenericValue phoneContact = null;        
        
        // get the email address to pass over
        String emailAddress = null;
        GenericValue emailContact = null;
        try {
            List emails = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "ORDER_EMAIL"));
            GenericValue firstEmail = EntityUtil.getFirst(emails);
            emailContact = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", firstEmail.getString("contactMechId")));                        
            emailAddress = emailContact.getString("infoString");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting order email address", module);
        }
        
        // get the properties file
        String configString = null;
        try {
            GenericValue webSitePayment = delegator.findByPrimaryKey("WebSitePaymentSetting", UtilMisc.toMap("webSiteId", webSiteId, "paymentMethodTypeId", "EXT_WORLDPAY"));
            if (webSitePayment != null)
                configString = webSitePayment.getString("paymentConfiguration");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Cannot find webSitePayment Settings", module);
        }
        if (configString == null)
        configString = "payment.properties";
            
        String instId = UtilProperties.getPropertyValue(configString, "payment.worldpay.instId", "NONE");
        String authMode = UtilProperties.getPropertyValue(configString, "payment.worldpay.authMode", "A");
        String testMode = UtilProperties.getPropertyValue(configString, "payment.worldpay.testMode", "100");
        String fixContact = UtilProperties.getPropertyValue(configString, "payment.worldpay.fixContact", "N");
        String hideContact = UtilProperties.getPropertyValue(configString, "payment.worldpay.hideContact", "N");
        String timeout = UtilProperties.getPropertyValue(configString, "payment.worldpay.timeout", "0");
        String company = UtilProperties.getPropertyValue(configString, "payment.general.company", "");
        String defCur = UtilProperties.getPropertyValue(configString, "payment.general.defaultCurrency", "USD");
        
        // order description
        String description = "Order #" + orderId;
        if (company != null && company.length() > 0)
        description = description + " from " + company;
        
        // check the instId - very important
        if (instId == null || instId.equals("NONE")) {
            Debug.logError("Worldpay InstId not found, cannot continue", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems getting merchant configuration, please contact customer service.");
            return "error";
        }  
        
        int instIdInt = 0;
        try {
            instIdInt = Integer.parseInt(instId);
        } catch (NumberFormatException nfe) {
            Debug.logError(nfe, "Problem converting instId string to integer", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems getting merchant configuration, please contact customer service.");
            return "error";
        }
        
        // check the testMode
        int testModeInt = -1;
        if (testMode != null) {
            try {
                testModeInt = Integer.parseInt(testMode);
            } catch (NumberFormatException nfe) {
                Debug.logWarning(nfe, "Problems getting the testMode value, setting to 0", module);
                testModeInt = 0;
            }            
        }
        
        // create the purchase link
        String purchaseURL = null;
        HTTPURL link = null;
        URLParameters linkParms = null;
        try {
            purchaseURL = Select.getPurchaseURL();
            link = new HTTPURL(purchaseURL);
            linkParms = link.getParameters();
        } catch (SelectException e) {
            Debug.logError(e, "Problems creating the purchase url", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problem creating link to WorldPay, please contact customer service.");
            return "error";
        } catch (ArgumentException e) {
            Debug.logError(e, "Problems creating HTTPURL link", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problem creating link to WorldPay, please contact customer service.");
            return "error";
        }                                                      
        
        // create the currency amount
        double orderTotal = orderHeader.getDouble("grandTotal").doubleValue();
        CurrencyAmount currencyAmount = null;         
        try {
            Currency currency = SelectCurrency.getInstanceByISOCode(defCur);
            currencyAmount = new CurrencyAmount(orderTotal, currency);
        } catch (ArgumentException ae) {
            Debug.logError(ae, "Problems building CurrencyAmount", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Merchant Configuration Error, please contact customer service.");
            return "error";
        }
                
        // create a purchase token
        PurchaseToken token = null;   
        try {
            token = new PurchaseToken(instIdInt, currencyAmount, orderId);
        } catch (SelectException e) {
            Debug.logError(e, "Cannot create purchase token", module);
        } catch (ArgumentException e) {
            Debug.logError(e, "Cannot create purchase token", module);
        } 
        if (token == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems creating a purchase token, please contact customer service.");
            return "error"; 
        }
        
        // set the auth/test modes        
        try {
            token.setAuthorisationMode(authMode);
        } catch (SelectException e) {
            Debug.logWarning(e, "Problems setting the authorization mode", module);
        }
        token.setTestMode(testModeInt);
                        
        // set the token to the purchase link
        try {
            linkParms.setValue(SelectDefs.SEL_purchase, token.produce());
        } catch (SelectException e) {
            Debug.logError(e, "Problems producing token", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems producing purchase token, please contact customer service.");
            return "error";
        }
        
        // set the customer data in the link
        linkParms.setValue(SelectDefs.SEL_desc, description);
        linkParms.setValue(SelectDefs.SEL_name, name != null ? name : "");
        linkParms.setValue(SelectDefs.SEL_address, address != null ? address.toString() : "");
        linkParms.setValue(SelectDefs.SEL_postcode, contactAddress != null ? contactAddress.getString("postalCode") : "");
        linkParms.setValue(SelectDefs.SEL_country, countryGeo.getString("geoCode"));
        linkParms.setValue(SelectDefs.SEL_tel, phoneNumber != null ? phoneNumber : ""); 
        linkParms.setValue(SelectDefs.SEL_email, emailAddress != null ? emailAddress : "");
        
        // set some optional data
        if (fixContact != null && fixContact.toUpperCase().startsWith("Y")) {
            linkParms.setValue(SelectDefs.SEL_fixContact, "Y");
        }
        if (hideContact != null && hideContact.toUpperCase().startsWith("Y")) {
            linkParms.setValue("hideContact", "Y"); // why is this not in SelectDefs??
        }
            
        
        // redirect to worldpay
        try {
            response.sendRedirect(link.produce());
        } catch (IOException e) {
            Debug.logError(e, "Problems redirecting to Worldpay", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems connecting with WorldPay, please contact customer service.");
            return "error";
        }
        
        return "success";
    }

}
