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
importClass(Packages.org.ofbiz.commonapp.party.contact.ContactMechWorker);
importClass(Packages.org.ofbiz.core.util.UtilHttp);
importClass(Packages.org.ofbiz.core.util.UtilMisc);

var userLogin = session.getAttribute("userLogin");
var security = request.getAttribute("security");
var delegator = request.getAttribute("delegator");

/* puts the following in the context: "contactMech", "contactMechId", 
        "partyContactMech", "partyContactMechPurposes", "contactMechTypeId", 
        "contactMechType", "purposeTypes", "postalAddress", "telecomNumber", 
        "requestName", "donePage", "tryEntity", "contactMechTypes"
 */
var target = new HashMap();
ContactMechWorker.getContactMechAndRelated(request, userLogin.getString("partyId"), target);
var targetIter = target.keySet().iterator();
while (targetIter.hasNext()) {
    var key = targetIter.next();
    context.put(key, target.get(key));
}


if (!security.hasEntityPermission("PARTYMGR", "_VIEW", session) && context.get("partyContactMech") == null && context.get("contactMech") != null) {
    context.put("canNotView", true);
} else {
    context.put("canNotView", false);
}

var preContactMechTypeId = request.getParameter("preContactMechTypeId");
if (preContactMechTypeId == null) preContactMechTypeId = request.getAttribute("preContactMechTypeId");
context.put("preContactMechTypeId", preContactMechTypeId);

var paymentMethodId = request.getParameter("paymentMethodId");
if (paymentMethodId == null) paymentMethodId = request.getAttribute("paymentMethodId");
context.put("paymentMethodId", paymentMethodId);

var cmNewPurposeTypeId = request.getParameter("contactMechPurposeTypeId");
if (cmNewPurposeTypeId == null) cmNewPurposeTypeId = request.getAttribute("contactMechPurposeTypeId");
if (cmNewPurposeTypeId != null) {
    var contactMechPurposeType = delegator.findByPrimaryKey("ContactMechPurposeType", UtilMisc.toMap("contactMechPurposeTypeId", cmNewPurposeTypeId));
    if (contactMechPurposeType != null) {
        context.put("contactMechPurposeType", contactMechPurposeType);
    } else {
        cmNewPurposeTypeId = null;
    }
}
context.put("cmNewPurposeTypeId", cmNewPurposeTypeId);

var tryEntity = context.get("tryEntity");
var requestParameters = UtilHttp.getParameterMap(request);

var contactMechData = context.get("contactMech");
if (!tryEntity.booleanValue()) contactMechData = requestParameters;
if (contactMechData == null) contactMechData = new HashMap();
context.put("contactMechData", contactMechData);

var partyContactMechData = context.get("partyContactMech");
if (!tryEntity.booleanValue()) partyContactMechData = requestParameters; 
if (partyContactMechData == null) partyContactMechData = new HashMap();
context.put("partyContactMechData", partyContactMechData);

var postalAddressData = context.get("postalAddress");
if (!tryEntity.booleanValue()) postalAddressData = requestParameters; 
if (postalAddressData == null) postalAddressData = new HashMap();
context.put("postalAddressData", postalAddressData);

var telecomNumberData = context.get("telecomNumber");
if (!tryEntity.booleanValue()) telecomNumberData = requestParameters; 
if (telecomNumberData == null) telecomNumberData = new HashMap();
context.put("telecomNumberData", telecomNumberData);


