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
 *@version    $Revision$
 *@since      2.1
-->

<#if hasPermission>

<#if webSiteId?has_content>
  <div class='tabContainer'>
  <a href="<@ofbizUrl>/EditWebSite?webSiteId=${webSiteId}</@ofbizUrl>" class="tabButton">WebSite</a>
  <a href="<@ofbizUrl>/EditWebSiteParties?webSiteId=${webSiteId}</@ofbizUrl>" class="tabButton">Parties</a>
  <a href="<@ofbizUrl>/EditWebSiteProdCatalogs?webSiteId=${webSiteId}</@ofbizUrl>" class="tabButtonSelected">Catalogs</a>
  </div>
</#if>

<div class="head1">Catalogs <span class='head2'>for <#if (webSite.siteName)?has_content>"${webSite.siteName}"</#if> [ID:${webSiteId?if_exists}]</span></div>
<a href="<@ofbizUrl>/EditWebSite</@ofbizUrl>" class="buttontext">[New WebSite]</a>
<br>
<br>
<#if webSiteId?has_content>
${updateProdCatalogToWebSiteWrapper.renderFormString()}
<br>
${addProdCatalogToWebSiteWrapper.renderFormString()}
</#if>
<br>

<#else>
 <h3>You do not have permission to view this page. ("CONTENTMGR_VIEW" or "CONTENTMGR_ADMIN" needed)</h3>
</#if>
