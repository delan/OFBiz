/*
 * $Id$ 
 */

package org.ofbiz.ecommerce.checkout;

import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.party.contact.*;
import org.ofbiz.ecommerce.shoppingcart.*;
import org.ofbiz.ecommerce.distributor.*;

/**
 * <p><b>Title:</b> CheckOutEvents.java
 * <p><b>Description:</b> Events used for processing checkout and orders.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on August 23, 2001, 7:58 PM
 */
public class CheckOutEvents {
    public static String cartNotEmpty(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart)request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        if (cart != null && cart.size() > 0) {
            return "success";
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"Cart is empty.");
            return "error";
        }
    }
    
    public static String setCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart)request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        StringBuffer errorMessage = new StringBuffer();
        if (cart != null && cart.size() > 0) {
            String shippingMethod = request.getParameter("shipping_method");
            String shippingContactMechId = request.getParameter("shipping_contact_mech_id");
            String creditCardId = request.getParameter("credit_card_id");
            String billingAccountId = request.getParameter("billing_account_id");
            String correspondingPoId = request.getParameter("corresponding_po_id");
            String shippingInstructions = request.getParameter("shipping_instructions");
            String orderAdditionalEmails = request.getParameter("order_additional_emails");
            String maySplit = request.getParameter("may_split");
            
            if (UtilValidate.isNotEmpty(shippingMethod)) {
                int delimiterPos = shippingMethod.indexOf('@');
                String shipmentMethodTypeId = null;
                String carrierPartyId = null;
                if(delimiterPos > 0) {
                    shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                    carrierPartyId = shippingMethod.substring(delimiterPos+1);
                }
                
                cart.setShipmentMethodTypeId(shipmentMethodTypeId);
                cart.setCarrierPartyId(carrierPartyId);
            } else {
                errorMessage.append("<li>Please Select a Shipping Method");
            }
            cart.setShippingInstructions(shippingInstructions);
            if (UtilValidate.isNotEmpty(maySplit)) {
                cart.setMaySplit(Boolean.valueOf(maySplit));
            } else {
                errorMessage.append("<li>Please Select a Splitting Preference");
            }
            cart.setOrderAdditionalEmails(orderAdditionalEmails);
            
            if (UtilValidate.isNotEmpty(shippingContactMechId)) {
                cart.setShippingContactMechId(shippingContactMechId);
            } else {
                errorMessage.append("<li>Please Select a Shipping Destination");
            }
            
            if(UtilValidate.isNotEmpty(creditCardId)) {
                cart.setCreditCardId(creditCardId);
            }
            if (UtilValidate.isNotEmpty(billingAccountId)) {
                cart.setBillingAccountId(billingAccountId);
                cart.setPoNumber(correspondingPoId);
                if (UtilValidate.isEmpty(cart.getPoNumber())) {
                    cart.setPoNumber("(none)");
                }//else ok
            } else if (UtilValidate.isEmpty(creditCardId)) {
                errorMessage.append("<li>Please Select a Method of Billing");
            }
        } else {
            errorMessage.append("<li>There are no items in the cart.");
        }
        
        if ( errorMessage.length() > 0 ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,errorMessage.toString());
            return "error";
        } else {
            return "success";
        }
    }
    
    // Create order event - uses createOrder service for processing
    public static String createOrder(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart)request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getSession().getServletContext().getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        
        //remove this whenever creating an order so quick reorder cache will refresh/recalc
        request.getSession().removeAttribute("_QUICK_REORDER_PRODUCTS_");
        
        // build the service context
        Map context = cart.makeCartMap();        
        String distributorId = DistributorEvents.getDistributorId(request);
        context.put("distributorId", distributorId);
        context.put("partyId", userLogin.get("partyId"));
        
        // invoke the service
        Map result = null;
        try {
            result = dispatcher.runSync("storeOrder",context);
            cart.clear();
        }
        catch ( GenericServiceException e ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"ERROR: Problem invoking the service: " + e.getMessage());
            return "error";
        }
        
        // check for error message(s)
        if ( result.containsKey(ModelService.ERROR_MESSAGE) )
            request.setAttribute(SiteDefs.ERROR_MESSAGE,result.get(ModelService.ERROR_MESSAGE));
        
        // set the orderId for future use
        request.setAttribute("order_id", result.get("orderId"));
        request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());
            
        // return the result
        return result.containsKey(ModelService.RESPONSE_MESSAGE) ? (String)result.get(ModelService.RESPONSE_MESSAGE) : "success";
    }
            
    public static String renderConfirmOrder(HttpServletRequest request, HttpServletResponse response) {
        String contextRoot=(String)request.getAttribute(SiteDefs.CONTEXT_ROOT);
        //getServletContext appears to be new on the session object for Servlet 2.3
        ServletContext application = request.getSession().getServletContext();
        URL ecommercePropertiesUrl = null;
        try { ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties"); }
        catch(java.net.MalformedURLException e) { Debug.logWarning(e); }
        
        final String ORDER_SECURITY_CODE = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "order.confirmation.securityCode");
        
        String controlPath = (String)request.getAttribute(SiteDefs.CONTROL_PATH);
        if(controlPath == null) {
            Debug.logError("[CheckOutEvents.renderConfirmOrder] CONTROL_PATH is null.");
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error generating order confirmation, but it was recorded and will be processed.");
            return "error";
        }
        String serverRoot = (String)request.getAttribute(SiteDefs.SERVER_ROOT_URL);
        if(serverRoot == null) {
            Debug.logError("[CheckOutEvents.renderConfirmOrder] SERVER_ROOT_URL is null.");
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error generating order confirmation, but it was recorded and will be processed.");
            return "error";
        }
        
        try {
            java.net.URL url = new java.net.URL(serverRoot + controlPath + "/confirmorder?order_id=" + request.getAttribute("order_id") + "&security_code=" + ORDER_SECURITY_CODE);
            HttpClient httpClient = new HttpClient(url);
            String content = httpClient.get();
            request.setAttribute("confirmorder", content);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error generating order confirmation, but it was recorded and will be processed.");
            return "error";
        }
    }
    
    public static String emailOrder(HttpServletRequest request, HttpServletResponse response) {
        String contextRoot=(String)request.getAttribute(SiteDefs.CONTEXT_ROOT);
        //getServletContext appears to be new on the session object for Servlet 2.3
        ServletContext application = request.getSession().getServletContext();
        URL ecommercePropertiesUrl = null;
        try { ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties"); }
        catch(java.net.MalformedURLException e) { Debug.logWarning(e); }
        try {
            final String SMTP_SERVER = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "smtp.relay.host");
            final String LOCAL_MACHINE = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "smtp.local.machine");
            final String ORDER_SENDER_EMAIL = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "order.confirmation.email");
            final String ORDER_BCC = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "order.confirmation.email.bcc");
            final String ORDER_CC = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "order.confirmation.email.cc");
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
            StringBuffer emails = new StringBuffer((String) request.getAttribute("orderAdditionalEmails"));
            GenericValue party = null;
            try { party = userLogin.getRelatedOne("Party"); }
            catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); party = null; }
            if(party != null) {
                Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false));
                while(emailIter != null && emailIter.hasNext()) {
                    GenericValue email = (GenericValue) emailIter.next();
                    emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
                }
            }
            
            String content = (String) request.getAttribute("confirmorder");
            try {
                // JavaMail contribution from Chris Nelson 11/21/2001
                Properties props = new Properties();
                props.put("mail.smtp.host", SMTP_SERVER);
                Session session = Session.getDefaultInstance(props);
                
                MimeMessage mail = new MimeMessage(session);
                mail.setFrom(new InternetAddress(ORDER_SENDER_EMAIL));
                mail.addRecipients(Message.RecipientType.TO, emails.toString());
                
                if (UtilValidate.isNotEmpty(ORDER_CC))
                    mail.addRecipients(Message.RecipientType.CC, ORDER_CC);
                if (UtilValidate.isNotEmpty(ORDER_BCC))
                    mail.addRecipients(Message.RecipientType.BCC, ORDER_BCC);
                
                String orderId = (String) request.getAttribute("order_id");
                mail.setSubject(UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name", "") + " Order" + UtilFormatOut.ifNotEmpty(orderId, " #", "") + " Confirmation");
                //mail.addHeaderLine("MIME-Version: 1.0\nContent-type: text/html; charset=us-ascii\n");
                mail.setContent(content, "text/html");
                Transport.send(mail);
                return "success";
            }
            catch (Exception e) {
                e.printStackTrace();
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "error e-mailing order confirmation, but it was created and will be processed.");
                return "success"; //"error";
            }
        }
        catch (RuntimeException re) {
            re.printStackTrace();
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "error e-mailing order confirmation, but it was created and will be processed.");
            return "success"; //"error";
        }
        catch (Error e) {
            e.printStackTrace();
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "error e-mailing order confirmation, but it was created and will be processed.");
            return "success"; //"error";
        }
    }
}
