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
 *@author         Andy Zeneski (jaz@ofbiz.org)
 *@author   	  thierry.grauss@etu.univ-tours.fr (migration to uiLabelMap)
 *@version        $Rev:$
 *@since            2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

${pages.get("/facility/FacilityTabBar.ftl")}

<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
        <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
                <tr>
                    <td><div class="boxhead">${uiLabelMap.ProductPicksStartedInProgress}</div></td>
                    <td align="right">&nbsp;</td>
                </tr>
            </table>
            <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
                <tr>
                    <td align="left" width="100%">
                        <table border="1" cellspacing="0" cellpadding="2">
                            <tr>
                                <td><div class="tableheadtext">${uiLabelMap.ProductPickStarted}</div></td>
                                <td><div class="tableheadtext">${uiLabelMap.OrderOrders}</div></td>
                                <td><div class="tableheadtext">${uiLabelMap.OrderOrderItem}</div></td>
                                <td><div class="tableheadtext">${uiLabelMap.ProductInventoryItems}</div></td>
                                <td><div class="tableheadtext">${uiLabelMap.ProductAvailable}</div></td>
                                <td><div class="tableheadtext">${uiLabelMap.ProductNotAvailable}</div></td>
                            </tr>
                            <#if orderItemInventoryResAndItemList?has_content>
                                <#list orderItemInventoryResAndItemList as orderItemInventoryResAndItem>
                                    <tr>
                                        <td>
                                            <div class="tabletext">
                                                <#if !lastPickStartDate?exists || orderItemInventoryResAndItem.pickStartDate != lastPickStartDate>
                                                    ${orderItemInventoryResAndItem.pickStartDate.toString()}
                                                    <a href="<@ofbizUrl>/clearPickStarted?facilityId=${facilityId?if_exists}&pickStartDate=${orderItemInventoryResAndItem.pickStartDate.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductResetDate}]</a>
                                                <#else>
                                                    &nbsp;
                                                </#if>
                                            </div>
                                        </td>
                                        <td>
                                            <div class="tabletext">
                                                <#if !lastOrderId?exists || orderItemInventoryResAndItem.orderId != lastOrderId>
                                                    ${orderItemInventoryResAndItem.orderId}
                                                    <a href="<@ofbizUrl>/clearPickStarted?facilityId=${facilityId?if_exists}&pickStartDate=${orderItemInventoryResAndItem.pickStartDate.toString()}&orderId=${orderItemInventoryResAndItem.orderId}</@ofbizUrl>" class="buttontext">[Reset&nbsp;Order]</a>
                                                <#else>
                                                    &nbsp;
                                                </#if>
                                            </div>
                                        </td>
                                        <td>
                                            <div class="tabletext">${orderItemInventoryResAndItem.orderItemSeqId}</div>
                                        </td>
                                        <td><div class="tabletext">${orderItemInventoryResAndItem.inventoryItemId}</div></td>
                                        <td><div class="tabletext">${orderItemInventoryResAndItem.quantity?default("&nbsp;")}</div></td>
                                        <td><div class="tabletext">${orderItemInventoryResAndItem.quantityNotAvailable?default("&nbsp;")}</div></td>
                                    </tr>
                                    <#assign lastPickStartDate = orderItemInventoryResAndItem.pickStartDate>
                                    <#assign lastOrderId = orderItemInventoryResAndItem.orderId>
                                </#list>
                            <#else>
                                <tr><td colspan="7"><div class="head3">${uiLabelMap.ProductNoPicksStarted}.</div></td></tr>
                            </#if>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<#else>
    <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>
