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
importPackage(Packages.java.net);
importPackage(Packages.java.util);
importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.org.ofbiz.core.entity);
importPackage(Packages.org.ofbiz.core.service);
importPackage(Packages.org.ofbiz.commonapp.product.catalog);

var dispatcher = request.getAttribute("dispatcher");
var delegator = request.getAttribute("delegator");
var productId = request.getAttribute("productId");
var webSiteId = CatalogWorker.getWebSiteId(request);
var catalogId = CatalogWorker.getCurrentCatalogId(request);
var autoUserLogin = session.getAttribute("autoUserLogin");
var userLogin = session.getAttribute(SiteDefs.USER_LOGIN);

// get the product entity
var product = null;
if (productId != null) {
    product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
    request.setAttribute("product", product);   
}

var categoryId = null;
if (product != null) {     
    categoryId = request.getParameter("category_id");
    if (categoryId == null) {
        categoryId = request.getAttribute("productCategoryId");
    }
    if (categoryId != null) {
        request.setAttribute("categoryId", categoryId);
    }
    
    // get the product price
    var priceMap = dispatcher.runSync("calculateProductPrice", 
            UtilMisc.toMap("product", product, "prodCatalogId", catalogId, 
            "webSiteId", webSiteId, "autoUserLogin", autoUserLogin, "userLogin", userLogin));
            
    request.setAttribute("priceMap", priceMap);
}
        
    
    
