/*
 * $Id$
 * $Log$
 * Revision 1.24  2001/10/17 06:50:16  jonesde
 * Finished round of reserved word replacements
 *
 * Revision 1.23  2001/10/14 05:57:42  jonesde
 * Changed ecommerce.properties access to go through URL object
 *
 * Revision 1.22  2001/10/03 20:22:45  jonesde
 * Added sorting by metric on occurances and quantity to quick reorder
 *
 * Revision 1.21  2001/10/03 05:17:32  jonesde
 * Added reorder products and associated products to sidebars
 *
 * Revision 1.20  2001/09/27 07:10:16  jonesde
 * Moved ecommerce.properties to WEB-INF
 *
 * Revision 1.19  2001/09/26 18:41:44  epabst
 * renamed getActive to filterByDate()
 * renamed getContactMech to getContactMechByPurpose/ByType
 *
 * Revision 1.18  2001/09/26 17:13:27  epabst
 * store the distributorId for an order
 *
 * Revision 1.17  2001/09/20 22:47:36  jonesde
 * Fixed illegal use of getRelatedOne
 *
 * Revision 1.16  2001/09/19 08:42:08  jonesde
 * Initial checkin of refactored entity engine.
 *
 * Revision 1.15  2001/09/19 00:24:13  jonesde
 * Updates related to moving server root from session to request; added error messages.
 *
 * Revision 1.14  2001/09/18 22:31:48  jonesde
 * Cleaned up messages, fixed a few small bugs.
 *
 * Revision 1.13  2001/09/14 21:34:32  epabst
 * added security code to confirmorder page
 * cleaned up
 *
 * Revision 1.12  2001/09/14 21:19:27  epabst
 * added security code to confirmorder page
 * cleaned up
 *
 * Revision 1.11  2001/09/14 20:06:16  epabst
 * fixed E-mail sending
 *
 * Revision 1.10  2001/09/14 19:13:24  epabst
 * added method to ShoppingCart to make the order items
 * added event for checking if the shopping cart is empty
 * created new session attribute called SiteDefs.SERVER_ROOT_URL that contains something like:
 * "http://myserver.com:1234"
 *
 * Revision 1.9  2001/09/14 00:32:44  epabst
 * standardized order information for checkout and orderstatus.  Yet to do confirmorder and all of their product sections
 *
 * Revision 1.8  2001/09/13 18:31:36  epabst
 * cleaned up
 * fixed billing account handling
 *
 * Revision 1.7  2001/09/13 15:12:25  epabst
 * cleaned up a little
 * separated shippingMethod into carrierPartyId and shipmentMethodTypeId
 *
 * Revision 1.6  2001/09/12 17:18:40  epabst
 * added SMTP sender
 *
 * Revision 1.5  2001/09/11 21:11:06  jonesde
 * Improved error handling.
 *
 * Revision 1.4  2001/09/11 17:27:14  epabst
 * updated order process to be more complete
 *
 * Revision 1.3  2001/09/10 21:56:22  epabst
 * updated/improved
 *
 * Revision 1.2  2001/09/06 16:02:54  epabst
 * renamed Address vars to Location
 * fixed bug where contactMechPurposeTypeId should end in _LOCATION instead of _ADDRESS
 *
 * Revision 1.1.1.1  2001/08/24 01:01:44  azeneski
 * Initial Import
 *
 */

package org.ofbiz.ecommerce.checkout;

import javax.servlet.*;
import javax.servlet.http.*;

import java.net.*;
import java.util.*;

import org.ofbiz.core.entity.*;
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
  
  public static String createOrder(HttpServletRequest request, HttpServletResponse response) {
    ShoppingCart cart = (ShoppingCart)request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
    GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    StringBuffer errorMessage = new StringBuffer();

    //remove this whenever creating an order so quick reorder cache will refresh/recalc
    request.getSession().removeAttribute("_QUICK_REORDER_PRODUCTS_");

    if (cart != null && cart.size() > 0) {
      String orderId = delegator.getNextSeqId("OrderHeader").toString();
      Debug.log("[createOrder] Got next OrderHeader seq id " + orderId);

      GenericValue order = delegator.makeValue("OrderHeader", UtilMisc.toMap("orderId", orderId, "orderTypeId", "SALES_ORDER", "orderDate", UtilDateTime.nowTimestamp(), "entryDate", UtilDateTime.nowTimestamp(), "statusId", "Ordered", "shippingInstructions", cart.getShippingInstructions()));
      order.set("billingAccountId", cart.getBillingAccountId());
      if (cart.getCartDiscount() != 0.0) {
        order.preStoreOther(delegator.makeValue("OrderAdjustment", UtilMisc.toMap( "orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment").toString(), "orderAdjustmentTypeId", "DISCOUNT_ADJUSTMENT", "orderId", orderId, "orderItemSeqId", "NA", "percentage", new Double(cart.getCartDiscount()))));
      }
      order.preStoreOther(delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment").toString(), "orderAdjustmentTypeId", "SHIPPING_CHARGES", "orderId", orderId, "orderItemSeqId", null, "amount", new Double(cart.getShipping()))));
      order.preStoreOther(delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment").toString(), "orderAdjustmentTypeId", "SALES_TAX", "orderId", orderId, "orderItemSeqId", null, "amount", new Double(cart.getSalesTax()))));
      order.preStoreOther(delegator.makeValue("OrderContactMech", UtilMisc.toMap( "contactMechId", cart.getShippingContactMechId(), "contactMechPurposeTypeId", "SHIPPING_LOCATION", "orderId", orderId)));

      GenericValue orderShipmentPreference = delegator.makeValue("OrderShipmentPreference", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", DataModelConstants.SEQ_ID_NA, "shipmentMethodTypeId", cart.getShipmentMethodTypeId(), "carrierPartyId", cart.getCarrierPartyId(), "carrierRoleTypeId", "CARRIER" /* XXX */, "shippingInstructions", cart.getShippingInstructions()));
      orderShipmentPreference.set("maySplit", cart.getMaySplit());
      order.preStoreOther(orderShipmentPreference);
      
      order.preStoreOthers(cart.makeOrderItems(delegator, orderId));
      
      final String[] USER_ORDER_ROLE_TYPES = {"END_USER_CUSTOMER", "SHIP_TO_CUSTOMER", "BILL_TO_CUSTOMER", "PLACING_CUSTOMER"};
      for (int i = 0; i < USER_ORDER_ROLE_TYPES.length; i++) {
        order.preStoreOther(delegator.makeValue("OrderRole", UtilMisc.toMap(
        "orderId", orderId,
        "partyId", userLogin.get("partyId"),
        "roleTypeId", USER_ORDER_ROLE_TYPES[i])));
      }
      String distributorId = DistributorEvents.getDistributorId(request);
      if (UtilValidate.isNotEmpty(distributorId)) {
          order.preStoreOther(delegator.makeValue("OrderRole", UtilMisc.toMap(
            "orderId", orderId,
            "partyId", distributorId,
            "roleTypeId", "DISTRIBUTOR")));
      } 
      
      order.preStoreOther(delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", delegator.getNextSeqId("OrderStatus").toString(), "statusId", "Requested", "orderId", orderId, "statusDatetime", UtilDateTime.nowTimestamp())));
      
      String creditCardId = cart.getCreditCardId();
      if (creditCardId != null) {
        order.preStoreOther(delegator.makeValue("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString(), "orderId", orderId, "paymentMethodTypeId", "CREDIT_CARD", "paymentInfoId", creditCardId)));
      } else {
        //XXX CASH should not be assumed!!
        order.preStoreOther(delegator.makeValue("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString(), "orderId", orderId, "paymentMethodTypeId", "CASH", "paymentInfoId", creditCardId)));
      }
      
      try {
        delegator.create(order);
        cart.clear();
        
        request.setAttribute("order_id", orderId);
        request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());
      }
      catch(GenericEntityException e) {
        Debug.logWarning(e.getMessage());
        errorMessage.append("<li>ERROR: Could not create order (write error: " + e.getMessage() + ").");
      }
      
    }
    else {
      errorMessage.append("<li>There are no items in the cart.");
    }
    
    if ( errorMessage.length() > 0 ) {
      request.setAttribute(SiteDefs.ERROR_MESSAGE,errorMessage.toString());
      return "error";
    }
    else {
      return "success";
    }
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
        SendMailSMTP mail = new SendMailSMTP(SMTP_SERVER, ORDER_SENDER_EMAIL, emails.toString(), content);
        mail.setLocalMachine(LOCAL_MACHINE);
        if (UtilValidate.isNotEmpty(ORDER_CC)) {
          mail.setRecipientCC(ORDER_CC);
        }
        if (UtilValidate.isNotEmpty(ORDER_BCC)) {
          mail.setRecipientBCC(ORDER_BCC);
        }
        String orderId = (String) request.getAttribute("order_id");
        mail.setSubject(UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name", "") + " Order" + UtilFormatOut.ifNotEmpty(orderId, " #", "") + " Confirmation");
        mail.setExtraHeader("MIME-Version: 1.0\nContent-type: text/html; charset=us-ascii\n");
        mail.setMessage(content);
        mail.send();
        return "success";
      } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute(SiteDefs.ERROR_MESSAGE, "error e-mailing order confirmation, but it was created and will be processed.");
        return "success"; //"error";
      }
    } catch (RuntimeException re) {
      re.printStackTrace();
      request.setAttribute(SiteDefs.ERROR_MESSAGE, "error e-mailing order confirmation, but it was created and will be processed.");
      return "success"; //"error";
    } catch (Error e) {
      e.printStackTrace();
      request.setAttribute(SiteDefs.ERROR_MESSAGE, "error e-mailing order confirmation, but it was created and will be processed.");
      return "success"; //"error";
    }
  }
}
