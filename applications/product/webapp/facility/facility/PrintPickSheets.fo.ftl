<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
                    margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right="1in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>

        <#list orderHeaderList as order>
            <fo:page-sequence master-reference="main">
                <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
                    <#include "component://order/webapp/ordermgr/order/companyHeader.fo.ftl"/>
                    <#assign orderId = order.orderId>
                    <#assign orderDate = order.orderDate>
                    <#list orderInfoList as orderInfo>
                        <#if orderInfo.get("${orderId}")?exists>
                            <#assign orderDetail = orderInfo.get("${orderId}")>
                            <#assign orderDate = orderDetail.orderDate>
                            <#if orderDetail.billingAddress?exists>
                                <#assign billAddress = orderDetail.billingAddress>
                            </#if>
                            <#assign shipAddress = orderDetail.shippingAddress>
                            <#assign shipmentMethodType = orderDetail.shipmentMethodType>
                            <#assign carrierPartyId = orderDetail.carrierPartyId>
                            <#assign shipGroupSeqId = orderDetail.shipGroupSeqId>

                            <fo:block text-align="right">
                                <fo:instream-foreign-object>
                                    <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
                                            message="${orderId}/${shipGroupSeqId}">
                                        <barcode:code39>
                                            <barcode:height>8mm</barcode:height>
                                        </barcode:code39>
                                    </barcode:barcode>
                                </fo:instream-foreign-object>
                            </fo:block>

                            <fo:table>
                                <fo:table-column column-width="200pt"/>
                                <fo:table-column column-width="200pt"/>
                                <fo:table-body>
                                    <fo:table-row>
                                         <fo:table-cell>
                                             <fo:block font-weight="bold">${uiLabelMap.OrderOrderId}:</fo:block><fo:block> ${orderId} (${shipGroupSeqId})</fo:block>
                                             <fo:block font-weight="bold">${uiLabelMap.OrderOrderDate}:</fo:block><fo:block> ${orderDate}</fo:block>
                                         </fo:table-cell>
                                         <fo:table-cell>
                                             <fo:table>
                                                 <fo:table-column column-width="200pt"/>
                                                 <fo:table-column column-width="200pt"/>
                                                 <fo:table-body>
                                                     <fo:table-row>
                                                         <fo:table-cell>
                                                             <fo:block font-weight="bold">${uiLabelMap.OrderShipToParty}:</fo:block>
                                                             <fo:block>${shipAddress.toName?if_exists}</fo:block>
                                                             <fo:block> ${shipAddress.address1?if_exists}</fo:block>
                                                             <fo:block> ${shipAddress.city?if_exists}</fo:block>
                                                             <fo:block> ${shipAddress.countryGeoId?if_exists}</fo:block>
                                                             <fo:block> ${shipAddress.postalCode?if_exists}</fo:block>
                                                             <fo:block> ${shipAddress.stateProvinceGeoId?if_exists}</fo:block>
                                                         </fo:table-cell>
                                                         <fo:table-cell>
                                                             <fo:table>
                                                                 <fo:table-column column-width="200pt"/>
                                                                 <fo:table-body>
                                                                     <fo:table-row>
                                                                         <fo:table-cell>
                                                                             <#if billAddress?has_content>
                                                                                 <fo:block font-weight="bold">${uiLabelMap.OrderOrderBillToParty}:</fo:block>
                                                                                 <fo:block> ${billAddress.toName?if_exists}</fo:block>
                                                                                 <fo:block> ${billAddress.address1?if_exists}</fo:block>
                                                                                 <fo:block> ${billAddress.city?if_exists}</fo:block>
                                                                                 <fo:block> ${billAddress.countryGeoId?if_exists}</fo:block>
                                                                                 <fo:block> ${billAddress.postalCode?if_exists}</fo:block>
                                                                                 <fo:block> ${billAddress.stateProvinceGeoId?if_exists}</fo:block>
                                                                             </#if>
                                                                         </fo:table-cell>
                                                                     </fo:table-row>
                                                                 </fo:table-body>
                                                             </fo:table> 
                                                         </fo:table-cell>
                                                     </fo:table-row>
                                                 </fo:table-body>
                                             </fo:table>
                                         </fo:table-cell>
                                     </fo:table-row>
                                 </fo:table-body>
                             </fo:table>
                             <fo:block space-after.optimum="10pt" font-size="14pt">
                                 <fo:table>
                                     <fo:table-column column-width="50pt"/>
                                     <fo:table-column column-width="400pt"/>
                                     <fo:table-column column-width="50pt"/>
                                     <fo:table-body>
                                          <fo:table-row>
                                             <fo:table-cell></fo:table-cell>
                                             <fo:table-cell padding="2pt">
                                                 <fo:table border-width="1pt" border-style="solid">
                                                     <fo:table-column column-width="150pt"/>
                                                     <fo:table-column column-width="250pt"/>
                                                     <fo:table-body>
                                                         <fo:table-row>
                                                             <fo:table-cell>
                                                                  <fo:block>${uiLabelMap.ProductShipmentMethod}:</fo:block>
                                                             </fo:table-cell>
                                                             <fo:table-cell>
                                                                 <fo:block font-weight="bold">${carrierPartyId?if_exists}-${shipmentMethodType?if_exists}</fo:block>
                                                             </fo:table-cell>
                                                         </fo:table-row>
                                                     </fo:table-body>
                                                 </fo:table>
                                             </fo:table-cell>
                                         </fo:table-row>
                                     </fo:table-body>
                                 </fo:table>
                             </fo:block>
                         </#if>
                     </#list>
                     <fo:block space-after.optimum="10pt" font-size="12pt">
                         <fo:table border-width="1pt" border-style="solid">
                             <fo:table-column column-width="90pt"/>
                             <fo:table-column column-width="90pt"/>
                             <fo:table-column column-width="110pt"/>
                             <fo:table-column column-width="140pt"/>
                             <fo:table-column column-width="40pt"/>
                             <fo:table-column column-width="70pt"/>
                             <fo:table-body>
                                 <fo:table-row>
                                     <fo:table-cell><fo:block>${uiLabelMap.ProductLocation}</fo:block></fo:table-cell>
                                     <fo:table-cell><fo:block>${uiLabelMap.ProductItemId}</fo:block></fo:table-cell>
                                     <fo:table-cell><fo:block>${uiLabelMap.ProductProductName}</fo:block></fo:table-cell>
                                     <fo:table-cell><fo:block>${uiLabelMap.FormFieldTitle_supplierProductId}</fo:block></fo:table-cell>
                                     <fo:table-cell><fo:block>${uiLabelMap.OrderQty}</fo:block></fo:table-cell>
                                     <fo:table-cell><fo:block>${uiLabelMap.OrderUnitPrice}</fo:block></fo:table-cell>
                                </fo:table-row >
                                <#assign totalQty = 0>
                                <#list itemInfoList as itemInfo>
                                    <#if itemInfo.get("${orderId}")?exists >
                                        <#assign infoItems = itemInfo.get("${orderId}")>
                                        <#list infoItems as infoItem>
                                            <fo:table-row>
                                                <#assign orderItemShipGrpInvRes = infoItem.orderItemShipGrpInvRes>
                                                <#assign orderItem = orderItemShipGrpInvRes.getRelatedOne("OrderItem")>
                                                <#assign product = orderItem.getRelatedOne("Product")>
                                                <#assign supplierProduct = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(product.getRelated("SupplierProduct"))?if_exists>
                                                <#assign inventoryItem = infoItem.inventoryItem>
                                                <#if infoItem.facilityLocation?has_content>
                                                    <#assign facilityLocation = infoItem.facilityLocation>
                                                    <fo:table-cell><fo:block font-size="10pt">${facilityLocation.locationSeqId?if_exists}</fo:block></fo:table-cell>
                                                <#else>
                                                    <fo:table-cell><fo:block>  </fo:block></fo:table-cell>
                                                </#if>
                                                <fo:table-cell><fo:block font-size="10pt">${product.productId} </fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block font-size="10pt">${product.internalName?if_exists} </fo:block></fo:table-cell>
                                                <#if supplierProduct?has_content >
                                                    <fo:table-cell><fo:block font-size="10pt">${supplierProduct.supplierProductId?if_exists} </fo:block></fo:table-cell>
                                                <#else>
                                                    <fo:table-cell><fo:block font-size="10pt">  </fo:block></fo:table-cell>
                                                </#if>
                                                <#assign quantity = Static["java.lang.Integer"].parseInt("${orderItemShipGrpInvRes.quantity}")/>
                                                <#assign totalQty = totalQty + quantity>
                                                <fo:table-cell><fo:block font-size="10pt">${orderItemShipGrpInvRes.quantity?if_exists} </fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block font-size="10pt"><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></fo:block></fo:table-cell>
                                            </fo:table-row>
                                         </#list>
                                     </#if>
                                 </#list>
                             </fo:table-body>
                         </fo:table>
                     </fo:block>

                     <fo:block text-align="right">
                         <fo:table>
                             <fo:table-column column-width="425pt"/>
                             <fo:table-column column-width="100pt"/>
                             <fo:table-body>
                                 <#list orderHeaderAdjustments as orderHeaderAdjustment>
                                     <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
                                     <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
                                     <#if adjustmentAmount != 0>
                                         <fo:table-row>
                                             <fo:table-cell><fo:block>${adjustmentType.get("description",locale)}:</fo:block></fo:table-cell>
                                             <fo:table-cell><fo:block><@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/></fo:block></fo:table-cell>
                                         </fo:table-row>
                                     </#if>
                                 </#list>
                                 <#list orderChargeList as orderCharge>
                                     <#if orderCharge.get("${orderId}")?exists >
                                         <#assign charges = orderCharge.get("${orderId}")>
                                         <fo:table-row>
                                             <fo:table-cell><fo:block>${uiLabelMap.OrderSubTotal}:</fo:block></fo:table-cell>
                                             <fo:table-cell><fo:block><@ofbizCurrency amount=charges.orderSubTotal isoCode=currencyUomId/></fo:block></fo:table-cell>
                                         </fo:table-row>
                                         <fo:table-row>
                                             <fo:table-cell><fo:block>${uiLabelMap.OrderTotalSalesTax}:</fo:block></fo:table-cell>
                                             <fo:table-cell><fo:block><@ofbizCurrency amount=charges.taxAmount isoCode=currencyUomId/></fo:block></fo:table-cell>
                                         </fo:table-row>
                                         <fo:table-row>
                                             <fo:table-cell><fo:block>${uiLabelMap.OrderTotalShippingAndHandling}:</fo:block></fo:table-cell>
                                             <fo:table-cell><fo:block><@ofbizCurrency amount=charges.shippingAmount isoCode=currencyUomId/></fo:block></fo:table-cell>
                                         </fo:table-row>
                                         <fo:table-row>
                                             <fo:table-cell><fo:block>${uiLabelMap.OrderTotalOtherOrderAdjustments}:</fo:block></fo:table-cell>
                                             <fo:table-cell><fo:block><@ofbizCurrency amount=charges.otherAdjAmount isoCode=currencyUomId/></fo:block></fo:table-cell>
                                         </fo:table-row>
                                         <fo:table-row>
                                             <fo:table-cell><fo:block>${uiLabelMap.OrderGrandTotal}:</fo:block></fo:table-cell>
                                             <fo:table-cell><fo:block><@ofbizCurrency amount=charges.grandTotal isoCode=currencyUomId/></fo:block></fo:table-cell>
                                         </fo:table-row>
                                         <fo:table-row>
                                              <fo:table-cell><fo:block text-align="left"> ${uiLabelMap.OrderPickedBy}: ______________</fo:block></fo:table-cell>
                                          </fo:table-row>
                                          <fo:table-row>
                                              <fo:table-cell><fo:block text-align="center"> ${uiLabelMap.OrderTotalNoOfItems}: ${totalQty}</fo:block></fo:table-cell>
                                          </fo:table-row>
                                     </#if>
                                 </#list>
                             </fo:table-body>
                         </fo:table>
                     </fo:block>
                 </fo:flow>
             </fo:page-sequence>
         </#list>
     </fo:root>
 </#escape>