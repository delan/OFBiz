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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision$
 *@since      2.1
*/

importPackage(Packages.java.lang);
importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.org.ofbiz.commonapp.product.catalog);
importPackage(Packages.org.ofbiz.commonapp.product.product);

var contentPathPrefix = CatalogWorker.getContentPathPrefix(request);
var searchCategoryId = request.getParameter("SEARCH_CATEGORY_ID");
var searchOperator = request.getParameter("SEARCH_OPERATOR");
if (!"AND".equalsIgnoreCase(searchOperator) && !"OR".equalsIgnoreCase(searchOperator)) { 
    searchOperator = "OR"; 
}
ProductWorker.getKeywordSearchProducts(request, "", searchCategoryId, true, true, searchOperator);
var keywordString = request.getAttribute("keywordString");
var viewIndex = request.getAttribute("viewIndex");
var viewSize = request.getAttribute("viewSize");

var baseSearchStr = "~SEARCH_STRING="+keywordString+"/~SEARCH_OPERATOR="+searchOperator+"/~SEARCH_CATEGORY_ID="+searchCategoryId+"/~VIEW_SIZE="+viewSize;
var nextStr = baseSearchStr+"/~VIEW_INDEX="+(viewIndex.intValue()+1);
var prevStr = baseSearchStr+"/~VIEW_INDEX="+(viewIndex.intValue()-1);
request.setAttribute("baseSearchStr", baseSearchStr);
request.setAttribute("nextStr", nextStr);
request.setAttribute("prevStr", prevStr);

Debug.logError("Search results: " + request.getAttribute("searchProductList"));

