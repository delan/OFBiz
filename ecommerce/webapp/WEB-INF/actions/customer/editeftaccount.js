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

var userLogin = session.getAttribute("userLogin");
var person = null;
if (userLogin != null) {
    person = userLogin.getRelatedOne("Person");
}

<%PaymentWorker.getPaymentMethodAndRelated(pageContext, userLogin.getString("partyId"), 
    "paymentMethod", "creditCard", "eftAccount", "paymentMethodId", "curContactMechId", "donePage", "tryEntity");%>

<%
    GenericValue efta = (GenericValue) pageContext.getAttribute("eftAccount");
    System.out.println("EFT Account: " + efta);
    System.out.println("Try Entity: " + pageContext.getAttribute("tryEntity"));
%>

<%ContactMechWorker.getCurrentPostalAddress(pageContext, userLogin.getString("partyId"), 
    (String) pageContext.getAttribute("curContactMechId"), "curPartyContactMech", "curContactMech", 
    "curPostalAddress", "curPartyContactMechPurposes");%>

<%ContactMechWorker.getPartyPostalAddresses(pageContext, userLogin.getString("partyId"), (String) pageContext.getAttribute("curContactMechId"), "postalAddressInfos");%>

var personData = person;
if (!tryEntity) personData = UtilHttp.getParameterMap(request);
if (personData == null) personData = new HashMap();

if (!security.hasEntityPermission("PARTYMGR", "_VIEW", session) && context.get("eftAccount") != null && context.get("paymentMethod") != null && !userLogin.get("partyId").equals((context.get("paymentMethod")).get("partyId"))) {
    context.put("canNotView", true);
} else {
    context.put("canNotView", false);
}

context.put("person", person);
context.put("personData", personData);
context.put("donePage", donePage);

