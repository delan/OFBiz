<#--
 *    Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a 
 *    copy of this software and associated documentation files (the "Software"), 
 *    to deal in the Software without restriction, including without limitation 
 *    the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *    and/or sell copies of the Software, and to permit persons to whom the 
 *    Software is furnished to do so, subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included 
 *    in all copies or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *    OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *    IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *    CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *    OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *    THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author         David E. Jones (jonesde@ofbiz.org)
 *@author	  thierry.grauss@etu.univ-tours.fr (migration to uiLabelMap)
 *@version        $Rev:$
 *@since          3.0
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

${pages.get("/facility/FacilityTabBar.ftl")}

<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
        <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
                <tr>
                    <td><div class="boxhead">${uiLabelMap.ProductStockMovesNeeded}</div></td>
                    <td align="right">&nbsp;</td>
                </tr>
            </table>
            <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
                <tr>
                    <td align="left" width="100%">
                        <form method="post" action="<@ofbizUrl>/processPhysicalStockMove</@ofbizUrl>" name='selectAllForm' style='margin: 0;'>
                            <#-- general request fields -->
                            <input type="hidden" name="facilityId" value="${facilityId?if_exists}">   
                            <input type="hidden" name="_useRowSubmit" value="Y">
                            <#assign rowCount = 0>     
                          <table border="1" cellspacing="0" cellpadding="2">
                            <tr>
                                <td><div class="tableheadtext">${uiLabelMap.ProductProduct}</div></td>
                                <td><div class="tableheadtext">${uiLabelMap.ProductFromLocation}</div></td>
                                <td><div class="tableheadtext">${uiLabelMap.ProductToLocation}</div></td>
                                <td><div class="tableheadtext">${uiLabelMap.ProductQuantity}</div></td>
                                <td><div class="tableheadtext">${uiLabelMap.OrderConfirm}</div></td>
                                <td align="right">
                                    <span class="tableheadtext">${uiLabelMap.ProductSelectAll}</span>&nbsp;
                                    <input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this);">
                                </td>
                            </tr>
                            <#if moveByOiirInfoList?has_content || moveByPflInfoList?has_content>
                                <#list moveByOiirInfoList?if_exists as moveByOiirInfo>
                                    <#assign product = moveByOiirInfo.product>
                                    <#assign facilityLocationFrom = moveByOiirInfo.facilityLocationFrom>
                                    <#assign facilityLocationTypeEnumFrom = (facilityLocationFrom.getRelatedOneCache("TypeEnumeration"))?if_exists>
                                    <#assign facilityLocationTo = moveByOiirInfo.facilityLocationTo>
                                    <#assign facilityLocationTypeEnumTo = (facilityLocationTo.getRelatedOneCache("TypeEnumeration"))?if_exists>
                                    <#assign totalQuantity = moveByOiirInfo.totalQuantity>
                                    <tr>
                                        <td><div class="tabletext">${product.internalName?if_exists} [${product.productId}]</div></td>
                                        <td><div class="tabletext">${facilityLocationFrom.areaId?if_exists}:${facilityLocationFrom.aisleId?if_exists}:${facilityLocationFrom.sectionId?if_exists}:${facilityLocationFrom.levelId?if_exists}:${facilityLocationFrom.positionId?if_exists}<#if facilityLocationTypeEnumFrom?has_content>(${facilityLocationTypeEnumFrom.description})</#if>[${facilityLocationFrom.locationSeqId}]</div></td>
                                        <td><div class="tabletext">${facilityLocationTo.areaId?if_exists}:${facilityLocationTo.aisleId?if_exists}:${facilityLocationTo.sectionId?if_exists}:${facilityLocationTo.levelId?if_exists}:${facilityLocationTo.positionId?if_exists}<#if facilityLocationTypeEnumTo?has_content>(${facilityLocationTypeEnumTo.description})</#if>[${facilityLocationTo.locationSeqId}]</div></td>
                                        <td><div class="tabletext">${totalQuantity?string.number}</div></td>
                                        <td align="right">              
                                            <input type="hidden" name="productId_o_${rowCount}" value="${product.productId?if_exists}">
                                            <input type="hidden" name="facilityId_o_${rowCount}" value="${facilityId?if_exists}">
                                            <input type="hidden" name="locationSeqId_o_${rowCount}" value="${facilityLocationFrom.locationSeqId?if_exists}">
                                            <input type="hidden" name="targetLocationSeqId_o_${rowCount}" value="${facilityLocationTo.locationSeqId?if_exists}">
                                            <input type="text" class="inputBox" name="quantityMoved_o_${rowCount}" size="6" value="${totalQuantity?string.number}">
                                        </td>
                                        <td align="right">              
                                            <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this);">
                                        </td>
                                    </tr>
                                    <#assign rowCount = rowCount + 1>   
                                </#list>
                                <#list moveByPflInfoList?if_exists as moveByPflInfo>
                                    <#assign product = moveByPflInfo.product>
                                    <#assign facilityLocationFrom = moveByPflInfo.facilityLocationFrom>
                                    <#assign facilityLocationTypeEnumFrom = (facilityLocationFrom.getRelatedOneCache("TypeEnumeration"))?if_exists>
                                    <#assign facilityLocationTo = moveByPflInfo.facilityLocationTo>
                                    <#assign facilityLocationTypeEnumTo = (facilityLocationTo.getRelatedOneCache("TypeEnumeration"))?if_exists>
                                    <#assign totalQuantity = moveByPflInfo.totalQuantity>
                                    <tr>
                                        <td><div class="tabletext">${product.internalName?if_exists} [${product.productId}]</div></td>
                                        <td><div class="tabletext">${facilityLocationFrom.areaId?if_exists}:${facilityLocationFrom.aisleId?if_exists}:${facilityLocationFrom.sectionId?if_exists}:${facilityLocationFrom.levelId?if_exists}:${facilityLocationFrom.positionId?if_exists}<#if facilityLocationTypeEnumFrom?has_content>(${facilityLocationTypeEnumFrom.description})</#if>[${facilityLocationFrom.locationSeqId}]</div></td>
                                        <td><div class="tabletext">${facilityLocationTo.areaId?if_exists}:${facilityLocationTo.aisleId?if_exists}:${facilityLocationTo.sectionId?if_exists}:${facilityLocationTo.levelId?if_exists}:${facilityLocationTo.positionId?if_exists}<#if facilityLocationTypeEnumTo?has_content>(${facilityLocationTypeEnumTo.description})</#if>[${facilityLocationTo.locationSeqId}]</div></td>
                                        <td><div class="tabletext">${totalQuantity?string.number}</div></td>
                                        <td align="right">              
                                            <input type="hidden" name="productId_o_${rowCount}" value="${product.productId?if_exists}">
                                            <input type="hidden" name="facilityId_o_${rowCount}" value="${facilityId?if_exists}">
                                            <input type="hidden" name="locationSeqId_o_${rowCount}" value="${facilityLocationFrom.locationSeqId?if_exists}">
                                            <input type="hidden" name="targetLocationSeqId_o_${rowCount}" value="${facilityLocationTo.locationSeqId?if_exists}">
                                            <input type="text" class="inputBox" name="quantityMoved_o_${rowCount}" size="6" value="${totalQuantity?string.number}">
                                        </td>
                                        <td align="right">              
                                            <input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this);">
                                        </td>
                                    </tr>
                                    <#assign rowCount = rowCount + 1>   
                                </#list>
                                <tr>
                                    <td colspan="6" align="right">
                                        <a href="javascript:document.selectAllForm.submit();" class="buttontext">${uiLabelMap.ProductConfirmSelectedMoves}</a>
                                    </td>
                                </tr>
                            <#else>
                                <tr><td colspan="6"><div class="head3">${uiLabelMap.ProductNoStockMovesNeeded}.</div></td></tr>
                            </#if>
                            <#assign messageCount = 0>
                            <#list pflWarningMessageList?if_exists as pflWarningMessage>
                                <#assign messageCount = messageCount + 1>   
                                <tr><td colspan="6"><div class="head3">${messageCount}:${pflWarningMessage}.</div></td></tr>
                            </#list>
                        </table>
                        <input type="hidden" name="_rowCount" value="${rowCount}">
                      </form>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<#else>
    <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>
