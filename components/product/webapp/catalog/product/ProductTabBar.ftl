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

<#if productId?has_content>
    <div class="tabContainer">
	    <a href="<@ofbizUrl>/EditProduct?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProduct?default(unselectedClassName)}">Product</a>
	    <a href="<@ofbizUrl>/EditProductPrices?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductPrices?default(unselectedClassName)}">Prices</a>
	    <a href="<@ofbizUrl>/EditProductContent?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductContent?default(unselectedClassName)}">Content</a>
	    <a href="<@ofbizUrl>/EditProductGoodIdentifications?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductGoodIdentifications?default(unselectedClassName)}">IDs</a>
	    <a href="<@ofbizUrl>/EditProductCategories?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductCategories?default(unselectedClassName)}">Categories</a>
	    <a href="<@ofbizUrl>/EditProductKeyword?PRODUCT_ID=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductKeyword?default(unselectedClassName)}">Keywords</a>
	    <a href="<@ofbizUrl>/EditProductAssoc?PRODUCT_ID=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductAssoc?default(unselectedClassName)}">Associations</a>
	    <a href="<@ofbizUrl>/EditProductAttributes?PRODUCT_ID=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductAttributes?default(unselectedClassName)}">Attributes</a>
	    <a href="<@ofbizUrl>/EditProductFeatures?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductFeatures?default(unselectedClassName)}">Features</a>
	    <a href="<@ofbizUrl>/EditProductFacilities?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductFacilities?default(unselectedClassName)}">Facilities</a>
	    <a href="<@ofbizUrl>/EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductInventoryItems?default(unselectedClassName)}">Inventory</a>
	    <a href="<@ofbizUrl>/EditProductGlAccounts?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductGlAccounts?default(unselectedClassName)}">Accounts</a>
	    <#if product?exists && product.isVirtual?if_exists == "Y">
	        <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.QuickAddVariants?default(unselectedClassName)}">Variants</a>
	    </#if>
    </div>
</#if>
