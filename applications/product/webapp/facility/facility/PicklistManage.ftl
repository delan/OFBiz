<#--
 *    Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author         thierry.grauss@etu.univ-tours.fr (migration to uiLabelMap)
 *@version        $Rev$
 *@since            2.2
-->

<div class="screenlet">
    <div class="screenlet-header">
        <div class="simple-right-half">
            <a href="<@ofbizUrl>/PicklistOptions?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="submenutext">${uiLabelMap.ProductPicklistOptions}</a>
            <a href="<@ofbizUrl>/PicklistManage?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="submenutext">${uiLabelMap.ProductPicklistManage}</a>
            <a href="<@ofbizUrl>/PickMoveStock?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="submenutextright">${uiLabelMap.ProductStockMoves}</a>
        </div>
        <div class="boxhead">${uiLabelMap.ProductPicklistManage}</div>
    </div>
    <div class="screenlet-body">
        <#if picklistInfoList?has_content>
            <#list picklistInfoList as picklistInfo>
                <#assign picklist = picklistInfo.picklist>
                
                <!-- display picklist -->
                <div class="tabletext">
                    <b>Picklist</b> ${picklist.picklistId} 
                    <b>in status</b> ${picklistInfo.statusItem.description} 
                    <b>created/modified by</b> ${picklist.createdByUserLogin}/${picklist.lastModifiedByUserLogin}
                </div>
                
                <#list picklistInfo.picklistRoleInfoList?if_exists as picklistRoleInfo>
                    <div class="tabletext">
                        &nbsp;&nbsp;&nbsp;
                        <b>Party</b> ${picklistRoleInfo.partyNameView.firstName?if_exists} ${picklistRoleInfo.partyNameView.middleName?if_exists} ${picklistRoleInfo.partyNameView.lastName?if_exists} ${picklistRoleInfo.partyNameView.groupName?if_exists}
                        <b>in role</b> ${picklistRoleInfo.roleType.description}
                        <b>from</b> ${picklistRoleInfo.picklistRole.fromDate}
                        <#if picklistRoleInfo.picklistRole.thruDate?exists><b>thru</b> ${picklistRoleInfo.picklistRole.thruDate}</#if>
                    </div>
                </#list>
                
                <#list picklistInfo.picklistStatusHistoryInfoList?if_exists as picklistStatusHistoryInfo>
                    <div class="tabletext">
                        &nbsp;&nbsp;&nbsp;
                        <b>Status</b> change from ${picklistStatusHistoryInfo.statusItem.description} 
                        to ${picklistStatusHistoryInfo.statusItemTo.description} 
                        on ${picklistStatusHistoryInfo.picklistStatusHistory.changeDate} 
                        by ${picklistStatusHistoryInfo.picklistStatusHistory.changeUserLoginId}
                    </div>
                </#list>
 
                <div class="tabletext">&nbsp;</div>
                
                <#list picklistInfo.picklistBinInfoList?if_exists as picklistBinInfo>
                    <div class="tabletext">
                        &nbsp;&nbsp;&nbsp;
                        <b>Bin</b> ${picklistBinInfo.picklistBin.binLocationNumber} 
                        <#if picklistBinInfo.primaryOrderHeader?exists><b>Primary Order</b> ${picklistBinInfo.primaryOrderHeader.orderId}</#if>
                        <#if picklistBinInfo.primaryOrderItemShipGroup?exists><b>Primary Ship Group</b> ${picklistBinInfo.primaryOrderItemShipGroup.shipGroupSeqId}</#if>
                    </div>
                    <#list picklistBinInfo.picklistItemInfoList?if_exists as picklistItemInfo>
                        <#assign picklistItem = picklistItemInfo.picklistItem>
                        <#assign inventoryItemAndLocation = picklistItemInfo.inventoryItemAndLocation>
                        <div class="tabletext">
                            &nbsp;&nbsp;&nbsp;
                            &nbsp;&nbsp;&nbsp;
                            <b>Order:Group:Item</b> ${picklistItem.orderId}:${picklistItem.shipGroupSeqId}:${picklistItem.orderItemSeqId}
                            <b>Product</b> ${picklistItemInfo.orderItem.productId}
                            <b>Inventory Item</b> ${inventoryItemAndLocation.inventoryItemId}
                            <b>Location</b> ${inventoryItemAndLocation.areaId?if_exists}-${inventoryItemAndLocation.aisleId?if_exists}-${inventoryItemAndLocation.sectionId?if_exists}-${inventoryItemAndLocation.levelId?if_exists}-${inventoryItemAndLocation.positionId?if_exists}
                            <b>Quantity</b> ${picklistItem.quantity} of ${picklistItemInfo.orderItem.quantity}
                            <#-- picklistItem.orderItemShipGrpInvRes -->
                        </div>
                    </#list>
                </#list>
                
                <#if picklistInfo_has_next>
                   <hr class="sepbar"/>
                </#if>
            </#list>
        <#else/>
            <div class="head3">${uiLabelMap.ProductNoPicksStarted}.</div>
        </#if>

    </div>
</div>
