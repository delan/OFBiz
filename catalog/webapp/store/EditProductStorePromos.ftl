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
 *@author     Brad Steiner (bsteiner@thehungersite.com)
 *@version    $Revision$
 *@since      2.2
-->

<#if hasPermission>
  <#if productStoreId?has_content>
    <div class='tabContainer'>
	  <a href="<@ofbizUrl>/EditProductStore?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Store</a>
	  <a href="<@ofbizUrl>/EditProductStorePromos?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButtonSelected">Promos</a>
	  <a href="<@ofbizUrl>/EditProductStoreCatalogs?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Catalogs</a>
	  <a href="<@ofbizUrl>/EditProductStoreWebSites?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">WebSites</a>
	  <a href="<@ofbizUrl>/EditProductStoreTaxSetup?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Sales Tax</a>
	  <a href="<@ofbizUrl>/EditProductStoreShipSetup?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Shipping</a>
	  <a href="<@ofbizUrl>/EditProductStorePaySetup?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Payments</a>
	  <a href="<@ofbizUrl>/EditProductStoreEmails?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Emails</a>
    </div>
  </#if>
  <div class="head1">Promotions <span class='head2'>for <#if (productStore.storeName)?has_content>"${productStore.storeName}"</#if> [ID:${productStoreId?if_exists}]</span></div>
  <a href="<@ofbizUrl>/EditProductStore</@ofbizUrl>" class="buttontext">[New Product Store]</a>
  <br>
  <br>

    <#if productStoreId?exists && productStore?exists>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Promo&nbsp;Name&nbsp;[ID]</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list productStorePromoAppls as productStorePromoAppl>
        <#assign line = line+1>
        <#assign productPromo = productStorePromoAppl.getRelatedOne("ProductPromo")>
        <tr valign="middle">
            <td><a href="<@ofbizUrl>/EditProductPromo?productPromoId=${(productStorePromoAppl.productPromoId)?if_exists}</@ofbizUrl>" class="buttontext"><#if productPromo?exists>${(productPromo.promoName)?if_exists}</#if> [${(productStorePromoAppl.productPromoId)?if_exists}]</a></td>
            <#assign hasntStarted = false>
            <#if productStorePromoAppl.getTimestamp("fromDate")?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().before(productStorePromoAppl.getTimestamp("fromDate"))> <#assign hasntStarted = true> </#if>
            <td><div class="tabletext" <#if hasntStarted> style="color: red;"</#if> >${productStorePromoAppl.getTimestamp("fromDate").toString()}</div></td>
            <td align="center">
                <#assign hasExpired = false>
                <#if productStorePromoAppl.getTimestamp("thruDate")?exists && Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp().after(productStorePromoAppl.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                <FORM method=POST action="<@ofbizUrl>/updateProductStorePromoAppl</@ofbizUrl>" name="lineForm${line}">
                    <input type=hidden name="productStoreId" value="${(productStorePromoAppl.productStoreId)?if_exists}">
                    <input type=hidden name="productPromoId" value="${(productStorePromoAppl.productPromoId)?if_exists}">
                    <input type=hidden name="fromDate" value="${(productStorePromoAppl.fromDate)?if_exists}">
                    <input type=text size="25" name="thruDate" value="${(productStorePromoAppl.thruDate)?if_exists}" class="inputBox" style="<#if (hasExpired) >color: red;</#if>">
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(productStorePromoAppl.thruDate)?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                    <input type=text size="5" name="sequenceNum" value="${(productStorePromoAppl.sequenceNum)?if_exists}" class="inputBox">
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/deleteProductStorePromoAppl?productStoreId=${(productStorePromoAppl.productStoreId)?if_exists}&productPromoId=${(productStorePromoAppl.productPromoId)?if_exists}&fromDate=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(productStorePromoAppl.getTimestamp("fromDate").toString())}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createProductStorePromoAppl</@ofbizUrl>" style="margin: 0;" name="addNewForm">
        <input type="hidden" name="productStoreId" value="${productStoreId?if_exists}">
        <input type="hidden" name="tryEntity" value="true">
        
        <div class="head2">Add Catalog Promo (select Promo, enter optional From Date):</div>
        <br>
        <select name="productPromoId" class="selectBox">
        <#list productPromos as productPromo>
            <option value="${productPromo.productPromoId?if_exists}">${productPromo.promoName?if_exists} [${productPromo.productPromoId?if_exists}]</option>
        </#list>
        </select>
        <input type=text size="25" name="fromDate" class="inputBox">
        <a href="javascript:call_cal(document.addNewForm.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
        <input type="submit" value="Add">
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
