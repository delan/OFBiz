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
 *@author     David E. Jones
 *@author     Brad Steiner
 *@author     thierry.grauss@etu.univ-tours.fr (migration to uiLabelMap)
 *@version    $Revision: 1.6 $
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#if hasPermission>

${pages.get("/facility/FacilityTabBar.ftl")}
    
    <div class="head1">${uiLabelMap.ProductLocationFor} <span class="head2"><#if facility?exists>${(facility.facilityName)?if_exists}</#if> [${uiLabelMap.CommonId}:${facilityId?if_exists}]</span></div>
    <a href="<@ofbizUrl>/EditFacility</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewFacility}]</a>
    <a href="<@ofbizUrl>/EditFacilityLocation?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewFacilityLocation}]</a>
    <#if facilityId?exists && locationSeqId?exists>
        <a href="<@ofbizUrl>/EditInventoryItem?facilityId=${facilityId}&locationSeqId=${locationSeqId}</@ofbizUrl>" class="buttontext">[[${uiLabelMap.ProductNewInventoryItem}]</a>
    </#if>
    
    <#if facilityId?exists && !(facilityLocation?exists)> 
        <form action="<@ofbizUrl>/CreateFacilityLocation</@ofbizUrl>" method="POST" style="margin: 0;">
        <table border="0" cellpadding="2" cellspacing="0">
        <input type="hidden" name="facilityId" value="${facilityId}">  
    <#elseif facilityLocation?exists>
        <form action="<@ofbizUrl>/UpdateFacilityLocation</@ofbizUrl>" method="POST" style="margin: 0;">
        <table border="0" cellpadding="2" cellspacing="0">
        <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
        <input type="hidden" name="locationSeqId" value="${locationSeqId}">
        <tr>
            <td align=right><div class="tabletext">${uiLabelMap.ProductFacilityId}</div></td>
            <td>&nbsp;</td>
            <td>
            <b>${facilityId?if_exists}</b>
            </td>
        </tr>
        <tr>
            <td align="right"><div class="tabletext">${uiLabelMap.ProductLocationSeqId}</div></td>
            <td>&nbsp;</td>
            <td>
            <b>${locationSeqId}</b>
            </td>
        </tr>
    <#else>
        <div class="head1">${uiLabelMap.ProductNotCreateLocationFacilityId}</div>
    </#if>
    
    <#if facilityId?exists>      
        <tr>
            <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductType}</div></td>
            <td>&nbsp;</td>
            <td width="74%">
                <select name="locationTypeEnumId">
                    <#if (facilityLocation.locationTypeEnumId)?has_content>
                        <#assign locationTypeEnum = facilityLocation.getRelatedOneCache("TypeEnumeration")?if_exists>
                        <option value="${facilityLocation.locationTypeEnumId}">${(locationTypeEnum.description)?default(facilityLocation.locationTypeEnumId)}</option>
                        <option value="${facilityLocation.locationTypeEnumId}">----</option>
                    </#if>
                    <#list locationTypeEnums as locationTypeEnum>
                        <option value="${locationTypeEnum.enumId}">${locationTypeEnum.description}</option>
                    </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">${uiLabelMap.CommonArea}</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="areaId" value="${(facilityLocation.areaId)?if_exists}" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductAisle}</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="aisleId" value="${(facilityLocation.aisleId)?if_exists}" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductSection}</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="sectionId" value="${(facilityLocation.sectionId)?if_exists}" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductLevel}</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="levelId" value="${(facilityLocation.levelId)?if_exists}" size="19" maxlength="20"></td>
        </tr>
        <tr>
            <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductPosition}</div></td>
            <td>&nbsp;</td>
            <td width="74%"><input type="text" class="inputBox" name="positionId" value="${(facilityLocation.positionId)?if_exists}" size="19" maxlength="20"></td>
        </tr>    
        <tr>
            <td colspan="2">&nbsp;</td>
            <td colspan="1" align=left><input type="submit" value="Update"></td>
        </tr>
    </table>
    </form>
    
    <hr class="sepbar"/>
    
		<#-- ProductFacilityLocation stuff -->
        <div class="head2">${uiLabelMap.ProductLocationProduct}:</div>
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>${uiLabelMap.ProductProduct}</b></div></td>
            <td align="center"><div class="tabletext"><b>${uiLabelMap.ProductMinimumStockAndMoveQuantity}</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#list productFacilityLocations?if_exists as productFacilityLocation>
	        <#assign product = productFacilityLocation.getRelatedOne("Product")?if_exists>
	        <tr valign="middle">
	            <td><div class="tabletext"><#if product?exists>${product.internalName}</#if>[${productFacilityLocation.productId}]</div></td>
	            <td align="center">
	                <FORM method=POST action="<@ofbizUrl>/updateProductFacilityLocation</@ofbizUrl>" name="lineForm${productFacilityLocation_index}">
	                    <input type="hidden" name="productId" value="${(productFacilityLocation.productId)?if_exists}">
	                    <input type="hidden" name="facilityId" value="${(productFacilityLocation.facilityId)?if_exists}">
	                    <input type="hidden" name="locationSeqId" value="${(productFacilityLocation.facilityId)?if_exists}">
	                    <input type="text" size="10" name="minimumStock" value="${(productFacilityLocation.minimumStock)?if_exists}" class="inputBox">
	                    <input type="text" size="10" name="moveQuantity" value="${(productFacilityLocation.moveQuantity)?if_exists}" class="inputBox">
	                    <INPUT type="submit" value="Update" style="font-size: x-small;">
	                </FORM>
	            </td>
	            <td align="center">
	            <a href="<@ofbizUrl>/deleteProductFacilityLocation?productId=${(productFacilityLocation.productId)?if_exists}&facilityId=${(productFacilityLocation.facilityId)?if_exists}&locationSeqId=${(productFacilityLocation.locationSeqId)?if_exists}</@ofbizUrl>" class="buttontext">
	            [${uiLabelMap.CommonDelete}]</a>
	            </td>
	        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/createProductFacilityLocation</@ofbizUrl>" style="margin: 0;" name="createProductFacilityLocationForm">
            <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
            <input type="hidden" name="locationSeqId" value="${locationSeqId?if_exists}">
            <input type="hidden" name="useValues" value="true">
            <div class="head2">${uiLabelMap.ProductAddProduct}:</div>
            <div class="tabletext">
                ${uiLabelMap.ProductProductId}:&nbsp;<input type=text size="10" name="productId" class="inputBox">
                ${uiLabelMap.ProductMinimumStock}:&nbsp;<input type=text size="10" name="minimumStock" class="inputBox">
                ${uiLabelMap.ProductMoveQuantity}:&nbsp;<input type=text size="10" name="moveQuantity" class="inputBox">
                <input type="submit" value="Add" style="font-size: x-small;">
            </div>
        </form>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>
