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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision$
 *@since      2.2
-->

<#if hasPermission>
  ${pages.get("/store/ProductStoreTabBar.ftl")}
  <div class="head1">Product Store WebSites <span class='head2'><#if (productStore.storeName)?has_content>"${productStore.storeName}"</#if> [ID:${productStoreId?if_exists}]</span></div>
  <a href="<@ofbizUrl>/EditProductStore</@ofbizUrl>" class="buttontext">[New Product Store]</a>
  <br>
  <br> 
  
  <table border="1" cellpadding="2" cellspacing="0" width="100%">
    <tr>
      <td><span class="tableheadtext">WebSite [ID]</span></td>
      <td><span class="tableheadtext">Host</span></td>
      <td><span class="tableheadtext">Port</span></td>
    </tr>
    <#if storeWebSites?has_content>
      <#list storeWebSites as webSite>
        <tr> 
          <td><a href="/content/control/EditWebSite?webSiteId=${webSite.webSiteId}&externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">${webSite.siteName} [${webSite.webSiteId}]</a></td>
          <td><span class="tabletext">${webSite.httpHost?default('&nbsp;')}</span></td>
          <td><span class="tabletext">${webSite.httpPort?default('&nbsp;')}</span></td>
        </tr>
      </#list>
    </#if>
  </table>
  
  <br>
  <div class="head2">Set store on WebSite:</div>
  <form name="addWebSite" action="<@ofbizUrl>/storeUpdateWebSite</@ofbizUrl>" method="post">
    <input type="hidden" name="productStoreId" value="${productStoreId}">
    <select class="selectBox" name="webSiteId">
      <#list webSites as webSite>
        <option value="${webSite.webSiteId}">${webSite.siteName} [${webSite.webSiteId}]</option>
      </#list>
    </select>
    <input type="submit" class="smallSubmit" value="Set">
  </form>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
