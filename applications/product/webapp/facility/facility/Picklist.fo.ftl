<?xml version="1.0" encoding="UTF-8" ?>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

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
 *@author     thierry.grauss@etu.univ-tours.fr (migration to uiLabelMap)
 *@version    $Rev$
 *@since      3.0
-->

<#macro picklistItemInfoDetail picklistItemInfo product facilityLocation>
    <#local picklistItem = picklistItemInfo.picklistItem>
    <#local orderItem = picklistItemInfo.orderItem>
    <fo:table-row>
        <fo:table-cell padding="2pt" background-color="${rowColor}">
            <#if facilityLocation?has_content>
                <fo:block>${facilityLocation.areaId?if_exists}-${facilityLocation.aisleId?if_exists}-${facilityLocation.sectionId?if_exists}-${facilityLocation.levelId?if_exists}-${facilityLocation.positionId?if_exists}</fo:block>
            <#elseif product?has_content>
                <fo:block>[${uiLabelMap.ProductNoLocation}:${product.productId}]</fo:block>
            <#else>
                <fo:block>[${uiLabelMap.ProductNoLocation}]</fo:block>
            </#if>
        </fo:table-cell>
        <fo:table-cell padding="2pt" background-color="${rowColor}">
            <#if product?has_content>
                <fo:block>${product.internalName} [${product.productId}]</fo:block>
            <#else>
                <fo:block> </fo:block>
            </#if>
        </fo:table-cell>
        <fo:table-cell padding="2pt" background-color="${rowColor}">
            <fo:block>${picklistItem.quantity}</fo:block>
        </fo:table-cell>
        <fo:table-cell padding="2pt" background-color="${rowColor}">
            <fo:block>${orderItem.orderId}:${orderItem.orderItemSeqId} [of total: ${orderItem.quantity}]</fo:block>
        </fo:table-cell>
        <fo:table-cell padding="2pt" background-color="${rowColor}">
            <fo:block>
                ${picklistItemInfo.inventoryItemAndLocation.inventoryItemId}<#if picklistItemInfo.inventoryItemAndLocation.binNumber?exists>:${picklistItemInfo.inventoryItemAndLocation.binNumber}</#if>
            </fo:block>
        </fo:table-cell>
    </fo:table-row>
    <#-- toggle the row color -->
    <#if rowColor == "white">
        <#assign rowColor = "#D4D0C8">
    <#else>
        <#assign rowColor = "white">
    </#if>
</#macro>

<!--
     - picklist
     - facility
     - statusItem
     - statusValidChangeToDetailList
     - picklistRoleInfoList (picklistRole, partyNameView, roleType)
     - picklistStatusHistoryInfoList (picklistStatusHistory, statusItem, statusItemTo)
     - picklistBinInfoList
       - picklistBin
       - primaryOrderHeader
       - primaryOrderItemShipGroup
       - picklistItemInfoList (picklistItem, orderItem, product, inventoryItemAndLocation, orderItemShipGrpInvRes, itemIssuanceList) 
-->
<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
            margin-top="0.5in" margin-bottom="1in" margin-left="1in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>

<fo:page-sequence master-reference="main">
<fo:flow flow-name="xsl-region-body" font-family="Helvetica">

    <#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

    <#if picklistInfo?has_content>
        <fo:block font-size="14pt">Items to Pick in Facility ${picklistInfo.facility.facilityName} <fo:inline font-size="8pt">[${picklistInfo.facility.facilityId}]</fo:inline></fo:block>
    </#if>

    <fo:block space-after.optimum="10pt" font-size="10pt">
    <fo:table>
        <fo:table-column column-width="80pt"/>
        <fo:table-column column-width="180pt"/>
        <fo:table-column column-width="50pt"/>
        <fo:table-column column-width="60pt"/>
        <fo:table-column column-width="80pt"/>
        <fo:table-header>
            <fo:table-row font-weight="bold">
                <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.ProductLocation}</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.ProductProductId}</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.ProductToPick}</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.OrderOrderItems}</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.ProductInventoryItems}</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#if facilityLocationInfoList?has_content || noLocationProductInfoList?has_content>
                <#assign rowColor = "white">
                <#-- facilityLocationInfoList: facilityLocation, picklistItemInfoList (picklistItem, orderItem, product, inventoryItemAndLocation, orderItemShipGrpInvRes, itemIssuanceList) -->
                <#if facilityLocationInfoList?has_content>
                <#list facilityLocationInfoList as facilityLocationInfo>
                    <#assign facilityLocation = facilityLocationInfo.facilityLocation>
                    <#assign picklistItemInfoList = facilityLocationInfo.picklistItemInfoList>
                    <#list picklistItemInfoList as picklistItemInfo>
                        <#assign product = picklistItemInfo.product/>
                        <@picklistItemInfoDetail picklistItemInfo=picklistItemInfo product=product facilityLocation=facilityLocation/>
                    </#list>
                </#list>
                </#if>
                <#-- noLocationProductInfoList: product, picklistItemInfoList (picklistItem, orderItem, product, inventoryItemAndLocation, orderItemShipGrpInvRes, itemIssuanceList) -->
                <#if noLocationProductInfoList?has_content>
                <#list noLocationProductInfoList as noLocationProductInfo>
                    <#if !noLocationProductInfo.facilityLocation?exists>
                        <#assign product = noLocationProductInfo.product>
                        <#assign picklistItemInfoList = noLocationProductInfo.picklistItemInfoList>
                        <#list picklistItemInfoList as picklistItemInfo>
                            <@picklistItemInfoDetail picklistItemInfo=picklistItemInfo product=product facilityLocation=null/>
                        </#list>
                    </#if>
                </#list>
                </#if>
            <#else>
                <fo:table-row font-weight="bold">
                    <fo:table-cell><fo:block>${uiLabelMap.ProductNoInventoryFoundToPick}.</fo:block></fo:table-cell>
                </fo:table-row>
            </#if>
        </fo:table-body>
    </fo:table>
    </fo:block>
</fo:flow>
</fo:page-sequence>

<#if picklistInfo?has_content>
    <#list picklistInfo.picklistBinInfoList as picklistBinInfo>
        <#assign rowColor = "white">
        <#assign picklistBin = picklistBinInfo.picklistBin>
        <#assign picklistItemInfoList = picklistBinInfo.picklistItemInfoList>
        <fo:page-sequence master-reference="main">
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <fo:block font-size="14pt">Order ${picklistBinInfo_index+1} to Pack, ID: ${picklistBinInfo.primaryOrderHeader.orderId}, Ship Group ID: ${picklistBinInfo.primaryOrderItemShipGroup.shipGroupSeqId}</fo:block>
            <fo:block space-after.optimum="10pt" font-size="10pt">
            <fo:table>
                <fo:table-column column-width="60pt"/>
                <fo:table-column column-width="180pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="150pt"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold">
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.OrderOrderItem}</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.ProductProductId}</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.ProductToPack}</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.ProductInventoryAvailNotAvail}</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#list picklistItemInfoList as picklistItemInfo>
                        <#assign picklistItem = picklistItemInfo.picklistItem>
                        <#assign orderItem = picklistItemInfo.orderItem>
                        <#assign product = picklistItemInfo.product>
                        <#assign orderItemShipGrpInvRes = picklistItemInfo.orderItemShipGrpInvRes>
                        <fo:table-row>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${picklistItem.orderId}:${picklistItem.shipGroupSeqId}:${picklistItem.orderItemSeqId}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#if product?has_content>
                                    <fo:block>${product.internalName} [${product.productId}]</fo:block>
                                <#else/>
                                    <fo:block>&nbsp;</fo:block>
                                </#if>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${picklistItem.quantity} of ${orderItem.quantity}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItemShipGrpInvRes.inventoryItemId}:${orderItemShipGrpInvRes.quantity}:${orderItemShipGrpInvRes.quantityNotAvailable?if_exists}</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>        
                    </#list>
                </fo:table-body>
            </fo:table>
            </fo:block>
        </fo:flow>
        </fo:page-sequence>
    </#list>          
</#if>

    <#else>
        <fo:block font-size="14pt">
            ${uiLabelMap.ProductFacilityViewPermissionError}
        </fo:block>
    </#if>
</fo:root>
