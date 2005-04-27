<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@version    $Rev$
 *@since      2.2
-->

<#if productId?exists && product?exists>
    <table border="1" cellpadding="2" cellspacing="0">
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
                <form method="post" action="<@ofbizUrl>/updateProductFacilityLocation</@ofbizUrl>" name="lineForm${productFacilityLocation_index}">
                    <input type="hidden" name="productId" value="${(productFacilityLocation.productId)?if_exists}"/>
                    <input type="hidden" name="facilityId" value="${(productFacilityLocation.facilityId)?if_exists}"/>
                    <input type="hidden" name="locationSeqId" value="${(productFacilityLocation.locationSeqId)?if_exists}"/>
                    <input type="text" size="10" name="minimumStock" value="${(productFacilityLocation.minimumStock)?if_exists}" class="inputBox"/>
                    <input type="text" size="10" name="moveQuantity" value="${(productFacilityLocation.moveQuantity)?if_exists}" class="inputBox"/>
                    <input type="submit" value="Update" style="font-size: x-small;"/>
                </form>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/deleteProductFacilityLocation?productId=${(productFacilityLocation.productId)?if_exists}&facilityId=${(productFacilityLocation.facilityId)?if_exists}&locationSeqId=${(productFacilityLocation.locationSeqId)?if_exists}</@ofbizUrl>" class="buttontext">
            [${uiLabelMap.CommonDelete}]</a>
            </td>
        </tr>
    </#list>
    </table>
    <br/>
    <form method="post" action="<@ofbizUrl>/createProductFacilityLocation</@ofbizUrl>" style="margin: 0;" name="createProductFacilityLocationForm">
        <input type="hidden" name="productId" value="${productId?if_exists}">
        <input type="hidden" name="useValues" value="true">
        <div class="head2">${uiLabelMap.CommonAdd} ${uiLabelMap.ProductFacilityLocation}:</div>
        <div class="tabletext">
            ${uiLabelMap.ProductFacility}:
            <select name="facilityId" class="selectBox">
                <#list facilities as facility>
                    <option value="${(facility.facilityId)?if_exists}">${(facility.facilityName)?if_exists}</option>
                </#list>
            </select>
            Location Seq ID:&nbsp;<input type="text" size="10" name="locationSeqId" class="inputBox"/>
            Minimum&nbsp;Stock:&nbsp;<input type="text" size="10" name="minimumStock" class="inputBox"/>
            Move&nbsp;Quantity:&nbsp;<input type="text" size="10" name="moveQuantity" class="inputBox"/>
            <input type="submit" value="${uiLabelMap.CommonAdd}" style="font-size: x-small;"/>
        </div>
    </form>
</#if>    
