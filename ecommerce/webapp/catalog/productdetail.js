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

// set the content path prefix
var contentPathPrefix = CatalogWorker.getContentPathPrefix(request); 
request.setAttribute("contentPathPrefix", contentPathPrefix);

// get the product detail information
var product = request.getAttribute("product");
if (product != null) {
    var productId = product.getString("productId");        
    request.setAttribute("product_id", productId);        
    var productTypeId = product.getString("productTypeId");  
    var featureTypes = new HashMap();
    var featureOrder = new LinkedList();

    // get next/previous information for category
    var categoryId = request.getParameter("category_id");
    if (categoryId == null) categoryId = product.getString("primaryProductCategoryId");
    if (categoryId != null) request.setAttribute("categoryId", categoryId);

    var catNextPreviousResult;
    if (categoryId != null) {
        catNextPreviousResult = dispatcher.runSync("getPreviousNextProducts", UtilMisc.toMap("categoryId", categoryId, "productId", productId));
        if (catNextPreviousResult != null) {
            request.setAttribute("category", catNextPreviousResult.get("category"));
            request.setAttribute("previousProductId", catNextPreviousResult.get("previousProductId"));
            request.setAttribute("nextProductId", catNextPreviousResult.get("nextProductId"));
        }
    }

    // get the product price
    var catalogId = CatalogWorker.getCurrentCatalogId(request);
    var webSiteId = CatalogWorker.getWebSiteId(request);
    var autoUserLogin = request.getSession().getAttribute("autoUserLogin");
    var fieldMap = UtilMisc.toMap("product", product, "prodCatalogId", catalogId, "webSiteId", webSiteId, "autoUserLogin", autoUserLogin);
    var priceMap = dispatcher.runSync("calculateProductPrice", fieldMap);
    request.setAttribute("priceMap", priceMap);
    
    // Special Variant Code 
    if ("Y".equals(product.getString("isVirtual"))) {
        var featureMap = dispatcher.runSync("getProductFeatureSet", UtilMisc.toMap("productId", productId));
        var featureSet = featureMap.get("featureSet");
        if (featureSet != null && featureSet.size() > 0) {
            var variantTreeMap = dispatcher.runSync("getProductVariantTree", UtilMisc.toMap("productId", productId, "featureOrder", featureSet, "prodCatalogId", catalogId));
            var variantTree = variantTreeMap.get("variantTree");
            var imageMap = variantTreeMap.get("variantSample");                        
            request.setAttribute("variantTree", variantTree);
            request.setAttribute("varientTreeSize", new Integer(variantTree.size()));
            request.setAttribute("variantSample", imageMap);
            request.setAttribute("variantSampleKeys", imageMap.keySet());
            request.setAttribute("variantSampleSize", new Integer(imageMap.size()));
            request.setAttribute("featureSet", featureSet);
            if (variantTree != null && variantTree.size() > 0) {         
                featureOrder = new LinkedList(featureSet);                         
                var foi = featureOrder.iterator();
                while (foi.hasNext()) {
                    var featureKey = foi.next();
                    var featureValue = delegator.findByPrimaryKeyCache("ProductFeatureType", UtilMisc.toMap("productFeatureTypeId", featureKey));
                    var fValue = featureValue.get("description") != null ? featureValue.getString("description") : featureValue.getString("productFeatureTypeId");
                    featureTypes.put(featureKey, fValue);
                }
            } 
            request.setAttribute("featureTypes", featureTypes);
            request.setAttribute("featureOrder", featureOrder);
            request.setAttribute("featureOrderFirst", featureOrder.get(0));
         
            var jsBuf = new StringBuffer();            
            jsBuf.append("<script language=\"JavaScript\">");
            jsBuf.append("var IMG = new Array(" + variantTree.size() + ");"); 
            jsBuf.append("var OPT = new Array(" + featureOrder.size() + ");");            
            for (li = 0; li < featureOrder.size(); li++) {
                jsBuf.append("OPT[" + li + "] = \"" + featureOrder.get(li) + "\";");                  
            }
            
            // build the top level             
            var topLevelName = featureOrder.get(0);
            jsBuf.append("function list" + topLevelName + "() {");
            jsBuf.append("document.forms[\"addform\"].elements[\"" + topLevelName + "\"].options.length = 1;");
            jsBuf.append("document.forms[\"addform\"].elements[\"" + topLevelName + "\"].options[0] = new Option(\"" + featureTypes.get(topLevelName) + "\",\"\",true,true);");                               
            if (variantTree != null) {
                var vTreeKeySet = variantTree.keySet();
                var vti = vTreeKeySet.iterator();
                var counter = 0;
                while (vti.hasNext()) {
                    var key = vti.next();
                    var value = variantTree.get(key);
                    var opt = null;
                    if (featureOrder.size() == 1)
                        opt = value.iterator().next();
                    else
                        opt = "" + counter;                 
                    jsBuf.append("document.forms[\"addform\"].elements[\"" + topLevelName + "\"].options[" + (counter+1) + "] = new Option(\"" + key + "\",\"" + opt + "\");");
                    jsBuf.append("IMG[" + counter + "] = \"" + imageMap.get(key).getString("largeImageUrl") +"\";");                
                    counter++;
                }
            }                    
            jsBuf.append("}");                        
   
            // build dynamic lists             
            if (variantTree != null) {
                var topLevelKeys = variantTree.keySet();
                var tli = topLevelKeys.iterator();
                var topLevelKeysCt = 0;
                while (tli.hasNext()) {
                    var cnt = "" + topLevelKeysCt; 
                    var varTree = variantTree.get(tli.next());
                    if (varTree instanceof Map) {
                        jsBuf.append(buildNext(varTree, featureOrder, featureOrder.get(1), cnt, featureTypes));             
                    }
                    topLevelKeysCt++;
                }
            }
            jsBuf.append("function findIndex(name) { for (i=0; i<OPT.length; i++) { if (OPT[i] == name) return i; } return -1; }");    
            jsBuf.append("function getList(name, value, src) { var value2 = 'NULL'; currentOrderIndex = findIndex(name);");                    
            jsBuf.append("if (src == 1 && OPT.length == 1) { value2 = document.forms[\"addform\"].elements[name].options[(value*1)+1].value;");                        
            jsBuf.append("} if (currentOrderIndex < 0 || value == \"\") return; if (currentOrderIndex < (OPT.length - 1) || OPT.length == 1) {");                        
            jsBuf.append("if (IMG[value] != null) { if (document.images['mainImage'] != null) document.images['mainImage'].src = IMG[value];");
            jsBuf.append("document.addform." + topLevelName + ".selectedIndex = (value*1)+1;");
            jsBuf.append("} if (OPT.length != 1) { eval(\"list\" + OPT[currentOrderIndex+1] + value + \"()\");");                        
            jsBuf.append("document.addform.add_product_id.value = 'NULL'; } else { if (value2 == 'NULL')");
            jsBuf.append("value2 = value; document.addform.add_product_id.value = value2;");
            jsBuf.append("} } else { document.addform.add_product_id.value = value; } }");
            jsBuf.append("</script>");  
            
            request.setAttribute("virtualJavaScript", jsBuf.toString());          
        }
    } 
    
    // get product associations
    var obsoleteProducts = dispatcher.runSync("getAssociatedProducts", UtilMisc.toMap("productId", productId, "type", "PRODUCT_OBSOLESCENCE"));
    request.setAttribute("obsoleteProducts", obsoleteProducts.get("assocProducts"));
    var crossSellProducts = dispatcher.runSync("getAssociatedProducts", UtilMisc.toMap("productId", productId, "type", "PRODUCT_COMPLEMENT"));
    request.setAttribute("crossSellProducts", crossSellProducts.get("assocProducts")); 
    var upSellProducts = dispatcher.runSync("getAssociatedProducts", UtilMisc.toMap("productId", productId, "type", "PRODUCT_UPGRADE"));
    request.setAttribute("upSellProducts", upSellProducts.get("assocProducts"));
    var obsolenscenseProducts = dispatcher.runSync("getAssociatedProducts", UtilMisc.toMap("productIdTo", productId, "type", "PRODUCT_OBSOLESCENCE"));
    request.setAttribute("obsolenscenseProducts", obsolenscenseProducts.get("assocProducts"));                 
}
 
function buildNext(map, order, current, prefix, featureTypes) {
    var keySet = map.keySet();
    var ct = 0;
    var i = keySet.iterator();
    var buf = new StringBuffer();
    buf.append("function list" + current + prefix + "() { ");
    buf.append("document.forms[\"addform\"].elements[\"" + current + "\"].options.length = 1;");
    buf.append("document.forms[\"addform\"].elements[\"" + current + "\"].options[0] = new Option(\"" + featureTypes.get(current) + "\",\"\",true,true);");
    while (i.hasNext()) {
        var key = i.next();
        var value = map.get(key);        
        var optValue = null;
        if (order.indexOf(current) == (order.size()-1)) {
            optValue = value.iterator().next();
        } else {
            optValue = prefix + "" + ct;
        }
        buf.append("document.forms[\"addform\"].elements[\"" + current + "\"].options[" + (ct + 1) + "] = new Option(\"" + key + "\",\"" + optValue + "\");");
        ct++;
    }
    buf.append(" }");
    if (order.indexOf(current) < (order.size()-1)) {
        var i2 = keySet.iterator();
        ct = 0;
        while (i2.hasNext()) {
            var nextOrder = order.get(order.indexOf(current)+1);
            var key = i2.next();
            var value = map.get(key);
            var newPrefix = prefix + "_" + ct;
            buf.append(buildNext(value, order, nextOrder, newPrefix, featureTypes));
        }
    }
    return buf.toString();
}    