/*
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
 *
 *@author     Andy Zeneski
 *@version    $Revision$
 *@since      2.1
*/

importPackage(Packages.java.lang);
importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.org.ofbiz.core.entity);

var cart = session.getAttribute(SiteDefs.SHOPPING_CART);
var userLogin = session.getAttribute("userLogin");
var party = userLogin.getRelatedOne("Party");

context.setAttribute("shoppingCart", cart);
context.setAttribute("carrierShipmentMethodList", delegator.findAllCache("CarrierShipmentMethod", UtilMisc.toList("sequenceNumber")));
context.setAttribute("shippingContactMechList", ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false));   
context.setAttribute("paymentMethodList", EntityUtil.filterByDate(party.getRelated("PaymentMethod"), true)); 
context.setAttribute("emailList",  ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false));
context.setAttribute("billingAccountRoleList", delegator.findByAnd("BillingAccountRole", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "roleTypeId", "BILL_TO_CUSTOMER"), null));

<%String chosenShippingMethod = cart.getShipmentMethodTypeId() + '@' + cart.getCarrierPartyId();%>  
