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
 *@version        $Rev$
 *@since          3.5
-->

<div class="screenlet">
    <div class="screenlet-header">
        <div class="simple-right-half">
            <a href="<@ofbizUrl>PicklistOptions?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="submenutext">${uiLabelMap.ProductPicklistOptions}</a>
            <a href="<@ofbizUrl>PicklistManage?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="submenutext">${uiLabelMap.ProductPicklistManage}</a>
            <a href="<@ofbizUrl>PickMoveStock?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="submenutextright">${uiLabelMap.ProductStockMoves}</a>
        </div>
        <div class="boxhead">${uiLabelMap.ProductPicklistManage}</div>
    </div>
    <div class="screenlet-body">
        <#if picklistInfoList?has_content>
            <#list picklistInfoList as picklistInfo>
                <#assign picklist = picklistInfo.picklist>
                
                <#-- Picklist -->
                <div class="tabletext">
                    <b>Picklist</b> <span class="head2">${picklist.picklistId}</span>
                    <b>date</b> ${picklist.picklistDate}
                    <form method="POST" action="<@ofbizUrl>updatePicklist</@ofbizUrl>" style="display: inline;">
                        <input type="hidden" name="facilityId" value="${facilityId}"/>
                        <input type="hidden" name="picklistId" value="${picklist.picklistId}"/>
                        <select name="statusId" class="smallSelect">
                            <option value="${picklistInfo.statusItem.statusId}" selected>${picklistInfo.statusItem.description}</option>
                            <option value="${picklistInfo.statusItem.statusId}">---</option>
                            <#list picklistInfo.statusValidChangeToDetailList as statusValidChangeToDetail>
                                <option value="${statusValidChangeToDetail.statusIdTo}">${statusValidChangeToDetail.description} (${statusValidChangeToDetail.transitionName})</option>
                            </#list>
                        </select>
                        <input type="submit" value="${uiLabelMap.CommonUpdate}" class="smallSubmit"/>
                    </form>
                    <b>created/modified by</b> ${picklist.createdByUserLogin}/${picklist.lastModifiedByUserLogin}
                    <a href="<@ofbizUrl>PicklistReport.pdf?picklistId=${picklist.picklistId}</@ofbizUrl>" class="buttontext">[Pick/Pack Report]</a>
                </div>
                <#if picklistInfo.shipmentMethodType?has_content>
                    <div class="tabletext" style="margin-left: 15px;">
                        <b>for Shipment Method Type</b> ${picklistInfo.shipmentMethodType.description?default(picklistInfo.shipmentMethodType.shipmentMethodTypeId)}
                    </div>
                </#if>
                
                <#-- PicklistRole -->
                <#list picklistInfo.picklistRoleInfoList?if_exists as picklistRoleInfo>
                    <div class="tabletext" style="margin-left: 15px;">
                        <b>Party</b> ${picklistRoleInfo.partyNameView.firstName?if_exists} ${picklistRoleInfo.partyNameView.middleName?if_exists} ${picklistRoleInfo.partyNameView.lastName?if_exists} ${picklistRoleInfo.partyNameView.groupName?if_exists}
                        <b>in role</b> ${picklistRoleInfo.roleType.description}
                        <b>from</b> ${picklistRoleInfo.picklistRole.fromDate}
                        <#if picklistRoleInfo.picklistRole.thruDate?exists><b>thru</b> ${picklistRoleInfo.picklistRole.thruDate}</#if>
                    </div>
                </#list>
                <div class="tabletext" style="margin-left: 15px;">
                    <b>Assign Picker:</b>
                    <form method="POST" action="<@ofbizUrl>createPicklistRole</@ofbizUrl>" style="display: inline;">
                        <input type="hidden" name="facilityId" value="${facilityId}"/>
                        <input type="hidden" name="picklistId" value="${picklist.picklistId}"/>
                        <input type="hidden" name="roleTypeId" value="PICKER"/>
                        <select name="partyId" class="smallSelect">
                            <#list partyRoleAndPartyDetailList as partyRoleAndPartyDetail>
                                <option value="${partyRoleAndPartyDetail.partyId}">${partyRoleAndPartyDetail.firstName?if_exists} ${partyRoleAndPartyDetail.middleName?if_exists} ${partyRoleAndPartyDetail.lastName?if_exists} ${partyRoleAndPartyDetail.groupName?if_exists} [${partyRoleAndPartyDetail.partyId}]</option>
                            </#list>
                        </select>
                        <input type="submit" value="${uiLabelMap.CommonAdd}" class="smallSubmit"/>
                    </form>
                </div>
                
                <#-- PicklistStatusHistory -->
                <#list picklistInfo.picklistStatusHistoryInfoList?if_exists as picklistStatusHistoryInfo>
                    <div class="tabletext" style="margin-left: 15px;">
                        <b>Status</b> change from ${picklistStatusHistoryInfo.statusItem.description} 
                        to ${picklistStatusHistoryInfo.statusItemTo.description} 
                        on ${picklistStatusHistoryInfo.picklistStatusHistory.changeDate} 
                        by ${picklistStatusHistoryInfo.picklistStatusHistory.changeUserLoginId}
                    </div>
                </#list>
 
                <#-- PicklistBin -->
                <#list picklistInfo.picklistBinInfoList?if_exists as picklistBinInfo>
                    <div class="tabletext" style="margin-left: 15px;">
                        <b>Bin</b> ${picklistBinInfo.picklistBin.binLocationNumber}
                        <#if picklistBinInfo.primaryOrderHeader?exists><b>Primary Order</b> ${picklistBinInfo.primaryOrderHeader.orderId}</#if>
                        <#if picklistBinInfo.primaryOrderItemShipGroup?exists><b>Primary Ship Group</b> ${picklistBinInfo.primaryOrderItemShipGroup.shipGroupSeqId}</#if>
                    </div>
                    <div class="tabletext" style="margin-left: 30px;">
                        <b>Update Bin:</b>
	                    <form method="POST" action="<@ofbizUrl>updatePicklistBin</@ofbizUrl>" style="display: inline;">
	                        <input type="hidden" name="facilityId" value="${facilityId}"/>
	                        <input type="hidden" name="picklistBinId" value="${picklistBinInfo.picklistBin.picklistBinId}"/>
	                        Location#:
	                        <input type"text" size="2" name="binLocationNumber" value="${picklistBinInfo.picklistBin.binLocationNumber}"/>
                            Picklist:
	                        <select name="picklistId" class="smallSelect">
	                            <#list picklistActiveList as picklistActive>
	                                <#assign picklistActiveStatusItem = picklistActive.getRelatedOneCache("StatusItem")>
	                                <option value="${picklistActive.picklistId}"<#if picklistActive.picklistId == picklist.picklistId> selected</#if>>${picklistActive.picklistId} [Date:${picklistActive.picklistDate},Status:${picklistActiveStatusItem.description}]</option>
	                            </#list>
	                        </select>
	                        <input type="submit" value="${uiLabelMap.CommonUpdate}" class="smallSubmit"/>
	                    </form>
                    </div>
                    <#list picklistBinInfo.picklistItemInfoList?if_exists as picklistItemInfo>
                        <#assign picklistItem = picklistItemInfo.picklistItem>
                        <#assign inventoryItemAndLocation = picklistItemInfo.inventoryItemAndLocation>
                        <div class="tabletext" style="margin-left: 30px;">
                            <b>Order:Group:Item</b> ${picklistItem.orderId}:${picklistItem.shipGroupSeqId}:${picklistItem.orderItemSeqId}
                            <b>Product</b> ${picklistItemInfo.orderItem.productId}
                            <b>Inventory Item</b> ${inventoryItemAndLocation.inventoryItemId}
                            <b>Location</b> ${inventoryItemAndLocation.areaId?if_exists}-${inventoryItemAndLocation.aisleId?if_exists}-${inventoryItemAndLocation.sectionId?if_exists}-${inventoryItemAndLocation.levelId?if_exists}-${inventoryItemAndLocation.positionId?if_exists}
                            <b>Quantity</b> ${picklistItem.quantity}<#-- of ${picklistItemInfo.orderItem.quantity}-->
                        </div>
                        <#-- picklistItem.orderItemShipGrpInvRes (do we want to display any of this info?) -->
                        <#-- picklistItemInfo.itemIssuanceList -->
                        <#list picklistItemInfo.itemIssuanceList?if_exists as itemIssuance>
	                        <div class="tabletext" style="margin-left: 40px;">
	                            <b>Issued To Shipment Item:</b> ${itemIssuance.shipmentId}:${itemIssuance.shipmentItemSeqId}
	                            <b>Quantity:</b> ${itemIssuance.quantity}
	                            <b>Date: </b> ${itemIssuance.issuedDateTime}
	                        </div>
                        </#list>
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
