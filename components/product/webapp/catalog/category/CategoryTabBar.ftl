<#--
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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Revision: 1.1 $
 *@since      2.2
-->

<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if productCategoryId?has_content> 
    <div class="tabContainer">
        <a href="<@ofbizUrl>/EditCategory?productCategoryId=${productCategoryId}</@ofbizUrl>" class="${selectedClassMap.EditCategory?default(unselectedClassName)}">Category</a>
        <a href="<@ofbizUrl>/EditCategoryRollup?showProductCategoryId=${productCategoryId}</@ofbizUrl>" class="${selectedClassMap.EditCategoryRollup?default(unselectedClassName)}">Rollup</a>
        <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId}</@ofbizUrl>" class="${selectedClassMap.EditCategoryProducts?default(unselectedClassName)}">Products</a>
        <a href="<@ofbizUrl>/EditCategoryProdCatalogs?productCategoryId=${productCategoryId}</@ofbizUrl>" class="${selectedClassMap.EditCategoryProdCatalogs?default(unselectedClassName)}">Catalogs</a>
        <a href="<@ofbizUrl>/EditCategoryFeatureCats?productCategoryId=${productCategoryId}</@ofbizUrl>" class="${selectedClassMap.EditCategoryFeatureCats?default(unselectedClassName)}">FeatureCats</a>
        <a href="<@ofbizUrl>/EditCategoryParties?productCategoryId=${productCategoryId}</@ofbizUrl>" class="${selectedClassMap.EditCategoryParties?default(unselectedClassName)}">Parties</a>
    </div>
</#if> 
