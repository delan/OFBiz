<#--
 *  Copyright (c) 2003-2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Rev:$
 *@since      3.0
-->

<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">Edit Content <span class='head2'>for ${(product.internalName)?default("[Name Unknown]")} <#if content?has_content>[ID:${contentId}]</#if></span></div>
    
    <a href="<@ofbizUrl>/EditProductContent?productId=${productId}</@ofbizUrl>" class="buttontext">[Product Content List]</a>
    <#if contentId?has_content>
        <a href="/content/control/gotoContent?contentId=${contentId}" class='buttontext' target='_blank'>[Content Page]</a>
    </#if>
    <br>

    <#if productId?has_content && productContent?has_content>
        ${updateProductContentWrapper.renderFormString()}
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
