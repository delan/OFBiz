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
 *@version        $Revision: 1.8 $
 *@since            2.2
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

${pages.get("/facility/FacilityTabBar.ftl")}

<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
        <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
                <tr>
                    <td><div class="boxhead">Find Orders to Pick</div></td>
                    <td align="right">
                        <a href="<@ofbizUrl>/PickStarted?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="submenutext">Picks In Progress</a>
                        <a href="<@ofbizUrl>/PickMoveStock?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="submenutext">Stock Moves</a>
                    </td>
                </tr>
            </table>
            <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
                <tr>
                    <td align="left" width="100%">
                        <table border="1" cellspacing="0" cellpadding="2">
                            <tr>
                                <td><div class="tableheadtext">Shipment Method</div></td>
                                <td><div class="tableheadtext">Ready To Pick</div></td>
                                <td><div class="tableheadtext">Need Stock Move</div></td>
                                <td><div class="tableheadtext">&nbsp;</div></td>
                            </tr>
                            <#if pickMoveByShipmentMethodInfoList?has_content>
                                <#assign orderReadyToPickInfoListSizeTotal = 0>
                                <#assign orderNeedsStockMoveInfoListSizeTotal = 0>
                                <#list pickMoveByShipmentMethodInfoList as pickMoveByShipmentMethodInfo>
                                    <#assign shipmentMethodType = pickMoveByShipmentMethodInfo.shipmentMethodType>
                                    <#assign orderReadyToPickInfoList = pickMoveByShipmentMethodInfo.orderReadyToPickInfoList?if_exists>
                                    <#assign orderNeedsStockMoveInfoList = pickMoveByShipmentMethodInfo.orderNeedsStockMoveInfoList?if_exists>
                                    <#assign orderReadyToPickInfoListSize = (orderReadyToPickInfoList.size())?default(0)>
                                    <#assign orderNeedsStockMoveInfoListSize = (orderNeedsStockMoveInfoList.size())?default(0)>
                                    <#assign orderReadyToPickInfoListSizeTotal = orderReadyToPickInfoListSizeTotal + orderReadyToPickInfoListSize>
                                    <#assign orderNeedsStockMoveInfoListSizeTotal = orderNeedsStockMoveInfoListSizeTotal + orderNeedsStockMoveInfoListSize>
                                    <tr>
                                        <td><div class="tabletext">${shipmentMethodType.description}</div></td>
                                        <td><div class="tabletext">${orderReadyToPickInfoListSize}</div></td>
                                        <td><div class="tabletext">${orderNeedsStockMoveInfoListSize}</div></td>
                                        <td>
                                            <div class="tabletext">
                                                <#if orderReadyToPickInfoList?has_content>
                                                    <form method="POST" action="<@ofbizUrl>/Picklist.pdf</@ofbizUrl>">
                                                        <input type="hidden" name="facilityId" value="${facilityId}"/>
                                                        <input type="hidden" name="shipmentMethodTypeId" value="${shipmentMethodType.shipmentMethodTypeId}"/>
                                                        Pick First:<input type="text" size="4" name="maxNumberOfOrders" value="20" class="inputBox"/>
                                                        <input type="checkbox" name="setPickStartedDate" value="Y" class="checkBox"/>Flag Picking Started?
                                                        <input type="submit" value="Pick" class="smallSubmit"/>
                                                    </form>
                                                <#else>
                                                    &nbsp;
                                                </#if>
                                            </div>
                                        </td>
                                    </tr>
                                </#list>
                                <tr>
                                    <td><div class="tableheadtext">All Methods</div></td>
                                    <td><div class="tableheadtext">${orderReadyToPickInfoListSizeTotal}</div></td>
                                    <td><div class="tableheadtext">${orderNeedsStockMoveInfoListSizeTotal}</div></td>
                                    <td>
                                        <div class="tabletext">
                                            <form method="POST" action="<@ofbizUrl>/Picklist.pdf</@ofbizUrl>">
                                                <input type="hidden" name="facilityId" value="${facilityId}"/>
                                                Pick First:<input type="text" size="4" name="maxNumberOfOrders" value="20" class="inputBox"/>
                                                <input type="checkbox" name="setPickStartedDate" value="Y" class="checkBox"/>Flag Picking Started?
                                                <input type="submit" value="Pick" class="smallSubmit"/>
                                        </form>
                                        </div>
                                    </td>
                                </tr>
                            <#else>
                                <tr><td colspan="4"><div class="head3">No orders found that are ready to pick or that need stock moves.</div></td></tr>
                            </#if>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<#else>
    <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
