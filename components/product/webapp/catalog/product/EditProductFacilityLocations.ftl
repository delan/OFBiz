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
 *@version    $Revision: 1.6 $
 *@since      2.2
-->

<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">Facility Locations <span class="head2">for <#if product?exists>${(product.internalName)?if_exists}</#if>[ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?exists>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <p>    
    <#if productId?exists && product?exists>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Facility</b></div></td>
            <td><div class="tabletext"><b>Location</b></div></td>
            <td align="center"><div class="tabletext"><b>Minimum&nbsp;Stock&nbsp;&amp;&nbsp;Move&nbsp;Quantity</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#list productFacilityLocations as productFacilityLocation>
	        <#assign facility = productFacilityLocation.getRelatedOneCache("Facility")>
	        <#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation")?if_exists>
        	<#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOneCache("TypeEnumeration"))?if_exists>
	        <tr valign="middle">
	            <td><div class="tabletext"><#if facility?exists>${facility.facilityName}<#else>[${productFacilityLocation.facilityId}]</#if></div></td>
	            <td><div class="tabletext"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?has_content>(${facilityLocationTypeEnum.description})</#if>[${productFacilityLocation.locationSeqId}]</div></td>
	            <td align="center">
	                <FORM method=POST action="<@ofbizUrl>/updateProductFacilityLocation</@ofbizUrl>" name="lineForm${productFacilityLocation_index}">
	                    <input type="hidden" name="productId" value="${(productFacilityLocation.productId)?if_exists}">
	                    <input type="hidden" name="facilityId" value="${(productFacilityLocation.facilityId)?if_exists}">
	                    <input type="hidden" name="locationSeqId" value="${(productFacilityLocation.locationSeqId)?if_exists}">
	                    <input type="text" size="10" name="minimumStock" value="${(productFacilityLocation.minimumStock)?if_exists}" class="inputBox">
	                    <input type="text" size="10" name="moveQuantity" value="${(productFacilityLocation.moveQuantity)?if_exists}" class="inputBox">
	                    <INPUT type="submit" value="Update" style="font-size: x-small;">
	                </FORM>
	            </td>
	            <td align="center">
	            <a href="<@ofbizUrl>/deleteProductFacilityLocation?productId=${(productFacilityLocation.productId)?if_exists}&facilityId=${(productFacilityLocation.facilityId)?if_exists}&locationSeqId=${(productFacilityLocation.locationSeqId)?if_exists}</@ofbizUrl>" class="buttontext">
	            [Delete]</a>
	            </td>
	        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createProductFacilityLocation</@ofbizUrl>" style="margin: 0;" name="createProductFacilityLocationForm">
            <input type="hidden" name="productId" value="${productId?if_exists}">
            <input type="hidden" name="useValues" value="true">
            <div class="head2">Add Facility Location:</div>
            <div class="tabletext">
                Facility:
                <select name="facilityId" class="selectBox">
                    <#list facilities as facility>
                        <option value="${(facility.facilityId)?if_exists}">${(facility.facilityName)?if_exists}</option>
                    </#list>
                </select>
                Location Seq ID:&nbsp;<input type=text size="10" name="locationSeqId" class="inputBox">
                Minimum&nbsp;Stock:&nbsp;<input type=text size="10" name="minimumStock" class="inputBox">
                Move&nbsp;Quantity:&nbsp;<input type=text size="10" name="moveQuantity" class="inputBox">
                <input type="submit" value="Add" style="font-size: x-small;">
            </div>
        </form>
    </#if>    
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
