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
 *@author     Johan Isacsson (johan@oddjob.se)
 *@version    $Revision: 1.2 $
 *@since      3.0
-->

<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">Content <span class='head2'>for ${product.productName?if_exists} <#if product?has_content>[ID:${productId}]</#if></span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class='buttontext' target='_blank'>[Product Page]</a>
    </#if>
    <br>
    <br>
    <table border="1" cellpadding="2" cellspacing="0" width="100%">
    <tr class="tableheadtext"><td>Content ID</td><td>Type</td><td>From</td><td>Thru</td><td>Purchase from</td><td>Purchase thru</td><td>Use count limit</td><td>Use days limit</td><td>Actions</td></tr>
    <#list productContentList as entry>
        <#assign productContent=entry.productContent />
        <tr class="tabletext">
            <td><a href="<@ofbizUrl>/EditProductContentContent?productId=${productContent.productId}&amp;contentId=${productContent.contentId}&amp;productContentTypeId=${productContent.productContentTypeId}&amp;fromDate=${productContent.fromDate}</@ofbizUrl>" class="buttontext">${entry.content.description?default("[No description]")} [${entry.content.contentId}]</td>
            <td>${entry.productContent.productContentTypeId}</td>
            <td>${entry.productContent.fromDate?default("N/A")}</td>
            <td>${entry.productContent.thruDate?default("N/A")}</td>
            <td>${entry.productContent.purchaseFromDate?default("N/A")}</td>
            <td>${entry.productContent.purchaseThruDate?default("N/A")}</td>
            <td>${entry.productContent.useCountLimit?default("N/A")}</td>
            <td>${entry.productContent.useDaysLimit?default("N/A")}</td>
            <td><a href="<@ofbizUrl>/removeContentFromProduct?productId=${productContent.productId}&amp;contentId=${productContent.contentId}&amp;productContentTypeId=${productContent.productContentTypeId}&amp;fromDate=${productContent.fromDate}</@ofbizUrl>" class="buttontext">[Delete]</a></td>
         </tr>
    </#list>
    </table>
    <br/>
    <div class="head1">Create new content for product</div>
    <br>
    <#if productId?has_content && product?has_content>
        ${addProductContentWrapper.renderFormString()}
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
