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
importPackage(Packages.org.ofbiz.core.service);
importPackage(Packages.org.ofbiz.commonapp.product.catalog);

// set the default view size
var defaultViewSize = request.getAttribute("defaultViewSize");
if (defaultViewSize == null) {
    defaultViewSize = new java.lang.Integer(10);
}
request.setAttribute("defaultViewSize", defaultViewSize);

// set the limit view
var limitViewObj = request.getAttribute("limitView");
var limitView;
if (limitViewObj == null) {
    limitView = new java.lang.Boolean(true);
} else {
    limitView = new java.lang.Boolean(limitViewObj);
}
request.setAttribute("limitView", limitView);


// get the product category & members
var dispatcher = request.getAttribute("dispatcher");
var catResult = dispatcher.runSync("getProductCategoryAndLimitedMembers", UtilMisc.toMap("productCategoryId", 
        request.getAttribute("productCategoryId"), "viewIndexString", request.getParameter("VIEW_INDEX"),
        "viewSizeString", request.getParameter("VIEW_SIZE"), "defaultViewSize", defaultViewSize, "limitView", limitView));
Debug.logInfo("" + catResult);        
if (catResult != null) {
    request.setAttribute("productCategoryMembers", catResult.get("productCategoryMembers"));
    request.setAttribute("productCategory", catResult.get("productCategory"));
    request.setAttribute("viewIndex", catResult.get("viewIndex"));
    request.setAttribute("viewSize", catResult.get("viewSize"));
    request.setAttribute("lowIndex", catResult.get("lowIndex"));
    request.setAttribute("highIndex", catResult.get("highIndex"));
    request.setAttribute("listSize", catResult.get("listSize"));
}

// set the content path prefix
var contentPathPrefix = CatalogWorker.getContentPathPrefix(request); 
request.setAttribute("contentPathPrefix", contentPathPrefix);

// little routine to see if any members have a quantity > 0 assigned
var members = request.getAttribute("productCategoryMembers");
/*
if (members != null && members.size() > 0) {
    for (i = 0; i < members.size(); i++) {      
        var productCategoryMember = (GenericValue) members.get(i);
        if (productCategoryMember.get("quantity") != null && productCategoryMember.getDouble("quantity").doubleValue() > 0.0) {            
            request.setAttribute("hasQuantities", new Boolean(true));
            break;
        }        
    }    
}
*/


