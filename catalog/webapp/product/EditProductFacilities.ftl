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

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">Facilities <span class="head2">for <#if product?exists>${(product.productName)?if_exists}</#if>[ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?exists>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <p>    
    <#if productId?exists && product?exists>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Facility</b></div></td>
            <td align="center"><div class="tabletext"><b>Minimum&nbsp;Stock&nbsp;&amp;&nbsp;Reorder&nbsp;Quantity&nbsp;&amp;&nbsp;Days To Ship</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list productFacilities as productFacility>
        <#assign line = line+1>
        <#assign facility = productFacility.getRelatedOneCache("Facility")>
        <tr valign="middle">
            <td><div class="tabletext"><#if facility?exists>${facility.facilityName}<#else>[${productFacility.facilityId}]</#if></div></td>
            <td align="center">
                <FORM method=POST action="<@ofbizUrl>/updateProductFacility</@ofbizUrl>" name="lineForm${line}">
                    <input type=hidden name="productId" value="${(productFacility.productId)?if_exists}">
                    <input type=hidden name="facilityId" value="${(productFacility.facilityId)?if_exists}">
                    <input type=text size="10" name="minimumStock" value="${(productFacility.minimumStock)?if_exists}" class="inputBox">
                    <input type=text size="10" name="reorderQuantity" value="${(productFacility.reorderQuantity)?if_exists}" class="inputBox">
                    <input type=text size="10" name="daysToShip" value="${(productFacility.daysToShip)?if_exists}" class="inputBox">
                    <INPUT type=submit value="Update" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/deleteProductFacility?productId=${(productFacility.productId)?if_exists}&facilityId=${(productFacility.facilityId)?if_exists}</@ofbizUrl>" class="buttontext">
            [Delete]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createProductFacility</@ofbizUrl>" style="margin: 0;" name="createProductFacilityForm">
            <input type="hidden" name="productId" value="${productId?if_exists}">
            <input type="hidden" name="useValues" value="true">
        
            <div class="head2">Add Facility:</div>
            <div class="tabletext">
                Facility:
                <select name="facilityId" class="selectBox">
                    <#list facilities as facility>
                        <option value="${(facility.facilityId)?if_exists}">${(facility.facilityName)?if_exists}</option>
                    </#list>
                </select>
                Minimum&nbsp;Stock:&nbsp;<input type=text size="10" name="minimumStock" class="inputBox">
                Reorder&nbsp;Quantity:&nbsp;<input type=text size="10" name="reorderQuantity" class="inputBox">
                Days&nbsp;To&nbsp;Ship:&nbsp;<input type=text size="10" name="daysToShip" class="inputBox">
                <input type="submit" value="Add" style="font-size: x-small;">
            </div>
        
        </form>
    </#if>    
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
