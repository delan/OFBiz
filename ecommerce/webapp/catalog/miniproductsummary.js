/*
 *  Copyright (c) 2001, 2002, 2003 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Revision$
 *@since      2.1
 */

importPackage(Packages.java.lang);
importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.org.ofbiz.core.service);
importPackage(Packages.org.ofbiz.commonapp.product.catalog);

var miniProduct = request.getAttribute("miniProduct");

if (miniProduct != null) {
    var userLogin = session.getAttribute("userLogin");
    var dispatcher = request.getAttribute("dispatcher");
    
    // calculate the "your" price
    var priceParams = UtilMisc.toMap("product", miniProduct, 
        "prodCatalogId", CatalogWorker.getCurrentCatalogId(request),
        "webSiteId", CatalogWorker.getWebSiteId(request),
        "autoUserLogin", session.getAttribute("autoUserLogin"));
    if (userLogin != null) priceParams.put("partyId", userLogin.get("partyId"));
    var priceResult = dispatcher.runSync("calculateProductPrice", priceParams);
    // returns: isSale, price, orderItemPriceInfos

    context.put("priceResult", priceResult);

    context.put("miniProduct", miniProduct);
    context.put("nowTimeLong", UtilDateTime.nowTimestamp().getTime());

    context.put("miniProdFormName", request.getAttribute("miniProdFormName"));
    context.put("miniProdQuantity", request.getAttribute("miniProdQuantity"));
}

