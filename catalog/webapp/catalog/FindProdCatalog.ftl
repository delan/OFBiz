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

<div class="head1">Product Catalogs List</div>
<div><a href="<@ofbizUrl>/EditProdCatalog</@ofbizUrl>" class="buttontext">[Create New ProdCatalog]</a></div>
<br>
<table border="1" cellpadding="2" cellspacing="0">
  <tr>
    <td><div class="tabletext"><b>Catalog&nbsp;Name&nbsp;[ID]</b></div></td>
    <td><div class="tabletext"><b>Title</b></div></td>
    <td><div class="tabletext"><b>Inventory Facility</b></div></td>
    <td><div class="tabletext"><b>OneInv Facility?</b></div></td>
    <td><div class="tabletext"><b>Check Inv?</b></div></td>
    <td><div class="tabletext"><b>Reserve Inv?</b></div></td>
    <td><div class="tabletext"><b>Reserve Order</b></div></td>
    <td><div class="tabletext"><b>Require Inv?</b></div></td>
    <td><div class="tabletext"><b>Use QuickAdd?</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<#list prodCatalogs as prodCatalog>
  <tr valign="middle">
    <#assign facility = prodCatalog.getRelatedOne("Facility")?if_exists>
    <#assign reserveOrderEnum = prodCatalog.getRelatedOne("ReserveOrderEnumeration")?if_exists>
    <td><div class="tabletext">&nbsp;<a href="<@ofbizUrl>/EditProdCatalog?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class="buttontext">${prodCatalog.catalogName} [${prodCatalog.prodCatalogId}]</a></div></td>
    <td><div class="tabletext">&nbsp;${prodCatalog.title}</div></td>
    <td><a href="/facility/control/EditFacility?facilityId=${prodCatalog.inventoryFacilityId}&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class="buttontext">&nbsp;
        ${(facility.facilityName)?if_exists} [${prodCatalog.inventoryFacilityId?if_exists}]</a></td>
    <td><div class="tabletext">&nbsp;${prodCatalog.oneInventoryFacility?if_exists}</div></td>
    <td><div class="tabletext">&nbsp;${prodCatalog.checkInventory?if_exists}</div></td>
    <td><div class="tabletext">&nbsp;${prodCatalog.reserveInventory?if_exists}</div></td>
    <td><div class="tabletext">&nbsp;${(reserveOrderEnum.description)?default(prodCatalog.reserveOrderEnumId?if_exists)}</div></td>
    <td><div class="tabletext">&nbsp;${prodCatalog.requireInventory?if_exists}</div></td>
    <td><div class="tabletext">&nbsp;${prodCatalog.useQuickAdd?if_exists}</div></td>
    <td>
      <a href="<@ofbizUrl>/EditProdCatalog?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class="buttontext">
      [Edit]</a>
    </td>
  </tr>
</#list>
</table>
<br>

<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
