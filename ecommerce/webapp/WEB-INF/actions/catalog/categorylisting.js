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
importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.org.ofbiz.core.entity);
importPackage(Packages.org.ofbiz.commonapp.product.catalog);

var detailTemplate = "/catalog/categorydetail.ftl";
var delegator = request.getAttribute("delegator");
var productCategoryId = request.getParameter("productCategoryId");
if (productCategoryId == null)
    productCategoryId = request.getAttribute("productCategoryId");

// get the category and its template
if (productCategoryId != null) {
    var productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
    if (productCategory != null && productCategory.get("detailTemplate") != null) {    
        var categoryTemplate = productCategory.getString("detailTemplate");
        if (categoryTemplate != null || categoryTemplate.length() > 0) {
            detailTemplate = categoryTemplate;
        }
    }
}

// check the catalog's template path and update
var templatePathPrefix = CatalogWorker.getTemplatePathPrefix(request);
if (templatePathPrefix != null) {
    detailTemplate = templatePathPrefix + detailTemplate;
}

// set the template for the view
request.setAttribute("detailTemplate", detailTemplate);

