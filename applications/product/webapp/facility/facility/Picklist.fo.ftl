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
    <fo:block font-size="14pt">Items to Pick in Facility ${facility.facilityName} <fo:inline font-size="8pt">[${facility.facilityId}]</fo:inline></fo:block>

    <#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

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
                        <#assign product = picklistItemInfo.product>
                        <#assign quantity = picklistItemInfo.quantity>
                        <#assign inventoryItemList = picklistItemInfo.inventoryItemList>
                        <#assign orderItemList = picklistItemInfo.orderItemList>
                        <fo:table-row> <#-- TODO: set the row color -->
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${facilityLocation.areaId?if_exists}-${facilityLocation.aisleId?if_exists}-${facilityLocation.sectionId?if_exists}-${facilityLocation.levelId?if_exists}-${facilityLocation.positionId?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#if product?has_content>
                                    <fo:block>${product.internalName} [${product.productId}]</fo:block>
                                <#else>
                                    <fo:block> </fo:block>
                                </#if>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${quantity}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#list orderItemList as orderItem>
                                    <fo:block>${orderItem.orderId}:${orderItem.orderItemSeqId}-${orderItem.quantity}</fo:block>
                                </#list>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>
                                <#list inventoryItemList as inventoryItem>
                                    ${inventoryItem.inventoryItemId}<#if inventoryItem.binNumber?exists>:${inventoryItem.binNumber}</#if>
                                </#list>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
                    </#list>
                </#list>
                </#if>
                <#-- noLocationProductInfoList: product, picklistItemInfoList (picklistItem, orderItem, product, inventoryItemAndLocation, orderItemShipGrpInvRes, itemIssuanceList) -->
                <#if noLocationProductInfoList?has_content>
                <#list noLocationProductInfoList as noLocationProductInfo>
                    <#if !noLocationProductInfo.facilityLocation?exists>
                        <#assign product = noLocationProductInfo.product>
                        <#assign picklistItemInfoList = noLocationProductInfo.picklistItemInfoList>
                        <fo:table-row>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#if facilityLocation?has_content>
                                    <fo:block>${facilityLocation.areaId?if_exists}-${facilityLocation.aisleId?if_exists}-${facilityLocation.sectionId?if_exists}-${facilityLocation.levelId?if_exists}-${facilityLocation.positionId?if_exists}</fo:block>
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
                                <fo:block>${quantity}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#list orderItems as orderItem>
                                    <fo:block>${orderItem.orderId}:${orderItem.orderItemSeqId}-${orderItem.quantity}</fo:block>
                                </#list>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${inventoryItem.inventoryItemId}<#if inventoryItem.binNumber?exists>:${inventoryItem.binNumber}</#if></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
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

<#if orderHeaderInfoList?has_content>
    <#list orderHeaderInfoList as orderHeaderInfo>
        <#assign rowColor = "white">
        <#-- orderHeaderInfoList: List of Maps with orderHeader and orderItemInfoList which is List of Maps with orderItem, product and orderItemShipGrpInvResList -->
        <#assign orderHeader = orderHeaderInfo.orderHeader>
        <#assign orderItemInfoList = orderHeaderInfo.orderItemInfoList>
        <fo:page-sequence master-reference="main">
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <fo:block font-size="14pt">Order ${orderHeaderInfo_index+1} to Pack, ID: ${orderHeader.orderId}</fo:block>
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
                    <#list orderItemInfoList as orderItemInfo>
                        <#assign orderItemAndShipGroupAssoc = orderItemInfo.orderItemAndShipGroupAssoc>
                        <#assign product = orderItemInfo.product>
                        <#assign orderItemShipGrpInvResList = orderItemInfo.orderItemShipGrpInvResList>
                        <fo:table-row>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItemAndShipGroupAssoc.orderItemSeqId}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#if product?has_content>
                                    <fo:block>${product.internalName} [${product.productId}]</fo:block>
                                <#else>
                                    <fo:block>&nbsp;</fo:block>
                                </#if>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${orderItemAndShipGroupAssoc.quantity}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#list orderItemShipGrpInvResList as orderItemShipGrpInvRes>
                                    <fo:block>${orderItemShipGrpInvRes.inventoryItemId}:${orderItemShipGrpInvRes.quantity}:${orderItemShipGrpInvRes.quantityNotAvailable?if_exists}</fo:block>
                                </#list>
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
