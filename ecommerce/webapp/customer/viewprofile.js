<#--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@created    May 22 2001
 *@version    1.0
-->

importPackage(Packages.java.lang);
importPackage(Packages.java.util);
importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.org.ofbiz.core.entity);
importPackage(Packages.org.ofbiz.commonapp.party.contact.ContactMechWorker);
importPackage(Packages.org.ofbiz.commonapp.accounting.payment.PaymentWorker);

var dispatcher = request.getAttribute("dispatcher");
var delegator = request.getAttribute("delegator");

var userLogin = session.getAttribute("userLogin");
if (userLogin != null) {
    var partyId = userLogin.getString("partyId");
    var partyIdMap = UtilMisc.toMap("partyId", partyId);
    
    var party = delegator.findByPrimaryKey("Party", partyIdMap);
    var person = delegator.findByPrimaryKey("Person", partyIdMap);
    var partyGroup = delegator.findByPrimaryKey("PartyGroup", partyIdMap);
    
    var showOld = "true".equals(request.getParameter("SHOW_OLD"));
     
    var partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, userLogin.getString("partyId"), showOld);
    var paymentMethodValueMaps = PaymentWorker.getPartyPaymentMethodValueMaps(delegator, userLogin.getString("partyId"), showOld);
    
    context.put("party", party);
    context.put("person", person);
    context.put("partyGroup", partyGroup);
    
    context.put("showOld", showOld);
    
    context.put("partyContactMechValueMaps", partyContactMechValueMaps);
    context.put("paymentMethodValueMaps", paymentMethodValueMaps);
}

