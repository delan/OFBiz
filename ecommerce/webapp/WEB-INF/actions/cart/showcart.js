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

importClass(Packages.org.ofbiz.commonapp.product.catalog.CatalogWorker);
importClass(Packages.org.ofbiz.commonapp.product.promo.ProductPromoWorker);

var delegator = request.getAttribute("delegator");

// Get the Cart and Prepare Size
var shoppingCart = session.getAttribute("shoppingCart");
if (shoppingCart != null) {
    context.put("shoppingCartSize", shoppingCart.size());
} else {
    context.put("shoppingCartSize", 0);
}
context.put("shoppingCart", shoppingCart);

//Get Cart Associated Products Data
context.put("associatedProducts", CatalogWorker.getRandomCartProductAssoc(request));

//Get Promo Text Data
var productPromos = ProductPromoWorker.getCatalogProductPromos(delegator, request);
//Make sure that at least one promo has non-empty promoText
var showPromoText = false;
var productPromoIterator = productPromos.iterator(); 
while (productPromoIterator.hasNext()) {
    var productPromo = productPromoIterator.next(); 
    var promoText = productPromo.get("promoText"); 
    if (promoText != null && promoText.length() > 0) showPromoText = true;
}
context.put("productPromos", productPromos);
context.put("showPromoText", showPromoText);

context.put("contentPathPrefix", CatalogWorker.getContentPathPrefix(request));

