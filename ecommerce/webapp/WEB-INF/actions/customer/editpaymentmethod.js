/*
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones
 *@version    1.0
 */

importClass(Packages.java.util.HashMap);
importClass(Packages.org.ofbiz.core.util.SiteDefs);
importClass(Packages.org.ofbiz.core.util.UtilHttp);
importClass(Packages.org.ofbiz.commonapp.accounting.payment.PaymentWorker);
importClass(Packages.org.ofbiz.commonapp.party.contact.ContactMechWorker);

var userLogin = session.getAttribute("userLogin");
var person = null;
if (userLogin != null) {
    person = userLogin.getRelatedOne("Person");
}

var paymentResults = PaymentWorker.getPaymentMethodAndRelated(request, userLogin.getString("partyId")) 
//returns the following: "paymentMethod", "creditCard", "eftAccount", "paymentMethodId", "curContactMechId", "donePage", "tryEntity"
context.put("paymentMethod", paymentResults.get("paymentMethod"));
context.put("creditCard", paymentResults.get("creditCard"));
context.put("eftAccount", paymentResults.get("eftAccount"));
context.put("paymentMethodId", paymentResults.get("paymentMethodId"));
context.put("curContactMechId", paymentResults.get("curContactMechId"));
context.put("donePage", paymentResults.get("donePage"));
context.put("tryEntity", paymentResults.get("tryEntity"));

var curPostalAddressResults = ContactMechWorker.getCurrentPostalAddress(pageContext, userLogin.getString("partyId"), pageContext.getAttribute("curContactMechId")); 
//returns the following: "curPartyContactMech", "curContactMech", "curPostalAddress", "curPartyContactMechPurposes"
context.put("curPartyContactMech", paymentResults.get("curPartyContactMech"));
context.put("curContactMech", paymentResults.get("curContactMech"));
context.put("curPostalAddress", paymentResults.get("curPostalAddress"));
context.put("curPartyContactMechPurposes", paymentResults.get("curPartyContactMechPurposes"));

var postalAddressInfos = ContactMechWorker.getPartyPostalAddresses(pageContext, userLogin.getString("partyId"), pageContext.getAttribute("curContactMechId"));
context.put("postalAddressInfos", postalAddressInfos);

var personData = person;
if (!tryEntity) personData = UtilHttp.getParameterMap(request);
if (personData == null) personData = new HashMap();

if (!security.hasEntityPermission("PARTYMGR", "_VIEW", session) && context.get("creditCard") != null && context.get("paymentMethod") != null && !userLogin.get("partyId").equals((context.get("paymentMethod")).get("partyId"))) {
    context.put("canNotView", true);
} else {
    context.put("canNotView", false);
}

context.put("person", person);
context.put("personData", personData);
context.put("donePage", donePage);

