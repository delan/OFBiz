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

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
    <#assign showInput = requestParameters.showInput?default("Y")>
    <#assign hideGrid = requestParameters.hideGrid?default("N")>    

    <#if (requestParameters.forceComplete?has_content && !shipmentId?has_content)>
        <#assign forceComplete = "true">
        <#assign showInput = "Y">
    </#if>

<div class="screenlet">
    <span class="head1">${uiLabelMap.ProductPackOrder}</span><span class='head2'>&nbsp;in&nbsp;${facility.facilityName?if_exists} [<a href="<@ofbizUrl>/EditFacility?facilityId=${facilityId?if_exists}</@ofbizUrl>" class="buttontext">${facilityId?if_exists}</a>]</div>
    <#if shipmentId?has_content>
      <div class="tabletext">
        ${uiLabelMap.CommonView} <a href="<@ofbizUrl>/PackingSlip.pdf?shipmentId=${shipmentId}</@ofbizUrl>" target="_blank" class="buttontext">${uiLabelMap.ProductPackingSlip}</a> ${uiLabelMap.CommonOr} 
        ${uiLabelMap.CommonView} <a href="<@ofbizUrl>/ShipmentBarCode.pdf?shipmentId=${shipmentId}</@ofbizUrl>" target="_blank" class="buttontext">${uiLabelMap.ProductBarcode}</a> ${uiLabelMap.CommonFor} ${uiLabelMap.ProductShipmentId} <a href="<@ofbizUrl>/ViewShipment?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext">${shipmentId}</a>
       </div>
       <#if invoiceIds?exists && invoiceIds?has_content>
         <div class="tabletext">
           <p>${uiLabelMap.AccountingInvoices}:</p> 
           <ul>
             <#list invoiceIds as invoiceId>
               <li>
                 #<a href="/accounting/control/invoiceOverview?invoiceId=${invoiceId}&externalLoginKey=${externalLoginKey}" target="_blank" class="buttontext">${invoiceId}</a>
                 (<a href="/accounting/control/invoice.pdf?invoiceId=${invoiceId}&externalLoginKey=${externalLoginKey}" target="_blank" class="buttontext">PDF</a>)
               </li>
             </#list>
           </ul>
         </div>
       </#if>
    </#if>
    <br/>

    <!-- select order form -->
    <form name="selectOrderForm" method="post" action="<@ofbizUrl>PackOrder</@ofbizUrl>">
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
      <table border='0' cellpadding='2' cellspacing='0'>
        <tr>
          <td width="25%" align='right'><div class="tabletext">${uiLabelMap.ProductOrderId} #</div></td>
          <td width="1">&nbsp;</td>
          <td width="25%">
            <input type="text" name="orderId" size="20" maxlength="20" value="${orderId?if_exists}"/>
            /
            <input type="text" name="shipGroupSeqId" size="6" maxlength="6" value="${shipGroupSeqId?default("00001")}"/>
          </td>
          <td>${uiLabelMap.ProductHideGrid}:&nbsp;<input type="checkbox" name="hideGrid" value="Y" <#if (hideGrid == "Y")>checked=""</#if>></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td colspan="2">&nbsp;</td>
          <td colspan="2">
            <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:document.selectOrderForm.submit();">
            <a href="javascript:document.selectOrderForm.submit();" class="buttontext">${uiLabelMap.ProductPackOrder}</a>
          </td>
        </tr>
      </table>
    </form>
    <br/>

    <!-- select picklist bin form -->
    <form name="selectPicklistBinForm" method="post" action="<@ofbizUrl>PackOrder</@ofbizUrl>" style="margin: 0;">
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
      <table border='0' cellpadding='2' cellspacing='0'>
        <tr>
          <td width="25%" align='right'><div class="tabletext">${uiLabelMap.FormFieldTitle_picklistBinId} #</div></td>
          <td width="1">&nbsp;</td>
          <td width="25%">
            <input type="text" name="picklistBinId" size="29" maxlength="60" value="${picklistBinId?if_exists}"/>            
          </td>
          <td>${uiLabelMap.ProductHideGrid}:&nbsp;<input type="checkbox" name="hideGrid" value="Y" <#if (hideGrid == "Y")>checked=""</#if>></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td colspan="2">&nbsp;</td>
          <td colspan="1">
            <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:document.selectPicklistBinForm.submit();">
            <a href="javascript:document.selectPicklistBinForm.submit();" class="buttontext">${uiLabelMap.ProductPackOrder}</a>
          </td>
        </tr>
      </table>
    </form>

    <form name="clearPackForm" method="post" action="<@ofbizUrl>ClearPackAll</@ofbizUrl>">
      <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
      <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
    </form>
    <form name="incPkgSeq" method="post" action="<@ofbizUrl>SetNextPackageSeq</@ofbizUrl>">
      <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
      <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
    </form>

    <#if showInput != "N" && ((orderHeader?exists && orderHeader?has_content))>
      <hr/>
      <div class='head2'>${uiLabelMap.ProductOrderId} #<a href="/ordermgr/control/orderview?orderId=${orderId}" class="buttontext">${orderId}</a> / ${uiLabelMap.ProductOrderShipGroupId} #${shipGroupSeqId}</div>
      <br/>
      <#if orderItemShipGroup?has_content>
        <#assign postalAddress = orderItemShipGroup.getRelatedOne("PostalAddress")>
        <#assign carrier = orderItemShipGroup.carrierPartyId?default("N/A")>
        <table border='0' cellpadding='4' cellspacing='4' width="100%">
          <tr>
            <td valign="top">
              <${uiLabelMap.ProductShipToAddress}:<br/>
              <b>${uiLabelMap.CommonTo}: </b>${postalAddress.toName?default("")}<br/>
              <#if postalAddress.attnName?has_content>
                  <b>${uiLabelMap.CommonAttn}: </b>${postalAddress.attnName}<br/>
              </#if>
              ${postalAddress.address1}<br/>
              <#if postalAddress.address2?has_content>
                  ${postalAddress.address2}<br/>
              </#if>
              ${postalAddress.city?if_exists}, ${postalAddress.stateProvinceGeoId?if_exists} ${postalAddress.postalCode?if_exists}<br/>
              ${postalAddress.countryGeoId}<br/>
            </td>
            <td>&nbsp;</td>
            <td valign="top">
              ${uiLabelMap.ProductCarrierShipmentMethod}:<br/>
              <#if carrier == "USPS">
                <#assign color = "red">
              <#elseif carrier == "UPS">
                <#assign color = "green">
              <#else>
                <#assign color = "black">
              </#if>
              <#if carrier != "_NA_">
                <font color="${color}">${carrier}</font>
                &nbsp;
              </#if>
              ${orderItemShipGroup.shipmentMethodTypeId?default("??")}
              <br/>
              ${uiLabelMap.ProductEstimatedShipCostForShipGroup}:<br/>
              <#if shipmentCostEstimateForShipGroup?exists>
                  <@ofbizCurrency amount=shipmentCostEstimateForShipGroup isoCode=orderReadHelper.getCurrency()?if_exists/><br/>
              </#if>
            </td>
            <td>&nbsp;</td>
            <td valign="top">
              ${uiLabelMap.OrderShipping} ${uiLabelMap.ProductInstruction}:<br/>
              ${orderItemShipGroup.shippingInstructions?default("(none)")}
            </td>
          </tr>
        </table>
        <br/>
      </#if>

      <!-- manual per item form -->
      <#if showInput != "N">
        <hr/>
        <br/>
        <form name="singlePackForm" method="post" action="<@ofbizUrl>ProcessPackOrder</@ofbizUrl>">
          <input type="hidden" name="packageSeq" value="${packingSession.getCurrentPackageSeq()}"/>
          <input type="hidden" name="orderId" value="${orderId}"/>
          <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId}"/>
          <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
          <input type="hidden" name="hideGrid" value="${hideGrid}"/>
          <table border='0' cellpadding='2' cellspacing='0' width="100%">
            <tr>
              <td><div class="tabletext">${uiLabelMap.ProductProduct} #</div></td>
              <td width="1">&nbsp;</td>
              <td>
                <input type="text" name="productId" size="20" maxlength="20" value=""/>
                @
                <input type="text" name="quantity" size="6" maxlength="6" value="1"/>
              </td>
              <td><div class='tabletext'>&nbsp;</div></td>
              <td align="right">
                  ${uiLabelMap.CommonCurrent} ${uiLabelMap.ProductPackage} ${uiLabelMap.CommonSequence}: <b>${packingSession.getCurrentPackageSeq()}</b>
                  <input type="button" value="${uiLabelMap.CommonNext} ${uiLabelMap.ProductPackage}" onclick="javascript:document.incPkgSeq.submit();">
              </td>
            </tr>
            <tr>
              <td colspan="2">&nbsp;</td>
              <td valign="top">
                <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:document.singlePackForm.submit();">
                <a href="javascript:document.singlePackForm.submit();" class="buttontext">${uiLabelMap.ProductPackItem}</a>
              </td>
              <td>&nbsp;</td>
            </tr>
          </table>
        </form>
        <br/>
      </#if>

      <!-- auto grid form -->
      <#assign itemInfos = packingSession.getItemInfos()?if_exists>
      <#if showInput != "N" && hideGrid != "Y" && itemInfos?has_content>
        <hr/>
        <br/>
        <form name="multiPackForm" method="post" action="<@ofbizUrl>ProcessBulkPackOrder</@ofbizUrl>">
          <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
          <input type="hidden" name="orderId" value="${orderId?if_exists}">
          <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}">
          <input type="hidden" name="originFacilityId" value="${facilityId?if_exists}">
          <input type="hidden" name="hideGrid" value="${hideGrid}"/>

          <table class="basic-table" cellspacing='0'>
            <tr class="header-row">
              <td>&nbsp;</td>
              <td>${uiLabelMap.ProductItem} #</td>
              <td>${uiLabelMap.ProductProductId}</td>
              <td>${uiLabelMap.ProductDescription}</td>
              <td align="right">${uiLabelMap.ProductOrderedQuantity}</td>
              <td align="right">${uiLabelMap.ProductQuantityShipped}</td>
              <td align="right">${uiLabelMap.ProductPackedQty}</td>
              <td>&nbsp;</td>
              <td align="center">${uiLabelMap.ProductPackQty}</td>
              <#--td align="center">${uiLabelMap.ProductPackedWeight}&nbsp;(${("uiLabelMap.ProductShipmentUomAbbreviation_" + defaultWeightUomId)?eval})</td-->
              <td align="center">${uiLabelMap.ProductPackage}</td>
            </tr>
                        
            <#if (itemInfos?has_content)>              
              <#assign rowKey = 1>
              <#list itemInfos as itemInfo>                                            
              <#-- <#list itemInfos as orderItem>  -->
                <#assign orderItem = itemInfo.orderItem/>
                <#assign shippedQuantity = orderReadHelper.getItemShippedQuantity(orderItem)?if_exists>
                <#assign orderItemQuantity = itemInfo.quantity/>
                <#assign orderProduct = orderItem.getRelatedOne("Product")?if_exists/>
                <#assign product = Static["org.ofbiz.product.product.ProductWorker"].findProduct(delegator, itemInfo.productId)?if_exists/>
                <#--
                <#if orderItem.cancelQuantity?exists>
                  <#assign orderItemQuantity = orderItem.quantity - orderItem.cancelQuantity>
                <#else>
                  <#assign orderItemQuantity = orderItem.quantity>
                </#if>
                -->

                <#assign inputQty = (orderItemQuantity - shippedQuantity - packingSession.getPackedQuantity(orderId, orderItem.orderItemSeqId, shipGroupSeqId, itemInfo.productId))>
                <tr>
                  <td><input type="checkbox" name="sel_${rowKey}" value="Y" <#if (inputQty >0)>checked=""</#if>/></td>
                  <td>${orderItem.orderItemSeqId}</td>
                  <td>
                          ${orderProduct.productId?default("N/A")}
                          <#if orderProduct.productId != product.productId>
                              &nbsp;${product.productId?default("N/A")}
                          </#if>
                  </td>
                  <td>
                          <a href="/catalog/control/EditProduct?productId=${orderProduct.productId?if_exists}${externalKeyParam}" class="linktext" target="_blank">${(orderProduct.internalName)?if_exists}</a>
                          <#if orderProduct.productId != product.productId>
                              &nbsp;[<a href="/catalog/control/EditProduct?productId=${product.productId?if_exists}${externalKeyParam}" class="linktext" target="_blank">${(product.internalName)?if_exists}</a>]
                          </#if>
                  </td>
                  <td align="right">${orderItemQuantity}</td>
                  <td align="right">${shippedQuantity?default(0)}</td>
                  <td align="right">${packingSession.getPackedQuantity(orderId, orderItem.orderItemSeqId, shipGroupSeqId, itemInfo.productId)}</td>
                  <td>&nbsp;</td>
                  <td align="center">
                    <input type="text" size="7" name="qty_${rowKey}" value="${inputQty}">
                  </td>
                  <#--td align="center">
                    <input type="text" size="7" name="wgt_${rowKey}" value="">
                  </td-->
                  <td align="center">
                    <select name="pkg_${rowKey}">
                      <option value="1">${uiLabelMap.ProductPackage} 1</option>
                      <option value="2">${uiLabelMap.ProductPackage} 2</option>
                      <option value="3">${uiLabelMap.ProductPackage} 3</option>
                      <option value="4">${uiLabelMap.ProductPackage} 4</option>
                      <option value="5">${uiLabelMap.ProductPackage} 5</option>
                    </select>
                  </td>
                  <input type="hidden" name="prd_${rowKey}" value="${itemInfo.productId?if_exists}"/>
                  <input type="hidden" name="ite_${rowKey}" value="${orderItem.orderItemSeqId}"/>
                </tr>
                <#assign rowKey = rowKey + 1>
              </#list>
            </#if>
            <tr><td colspan="10">&nbsp;</td></tr>
            <tr>
              <td colspan="10" align="right">
                <input type="submit" value="${uiLabelMap.ProductPackItem}">
                &nbsp;
                <input type="button" value="${uiLabelMap.CommonClear}" onclick="javascript:document.clearPackForm.submit();"/>
              </td>
            </tr>
          </table>
        </form>
        <br/>
      </#if>

      <!-- complete form -->
      <#if showInput != "N">
        <form name="completePackForm" method="post" action="<@ofbizUrl>CompletePack</@ofbizUrl>">
          <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
          <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
          <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
          <input type="hidden" name="forceComplete" value="${forceComplete?default('false')}"/>
          <input type="hidden" name="weightUomId" value="${defaultWeightUomId}"/>
          <input type="hidden" name="showInput" value="N"/>
          <hr class="sepbar">
          <br/>
          <table border='0' cellpadding='2' cellspacing='0' width="100%">
            <tr>
                <#assign packageSeqIds = packingSession.getPackageSeqIds()/>
                <#if packageSeqIds?has_content>
                    <td>
                        ${uiLabelMap.ProductPackedWeight} (${("uiLabelMap.ProductShipmentUomAbbreviation_" + defaultWeightUomId)?eval}):<br/>
                            <#list packageSeqIds as packageSeqId>
                                ${uiLabelMap.ProductPackage} ${packageSeqId}  <input type="text" size="7" name="packageWeight_${packageSeqId}" value="${packingSession.getPackageWeight(packageSeqId?int)?if_exists}"><br/>
                            </#list>
                            <#if orderItemShipGroup?has_content>
                                <input type="hidden" name="shippingContactMechId" value="${orderItemShipGroup.contactMechId?if_exists}"/>
                                <input type="hidden" name="shipmentMethodTypeId" value="${orderItemShipGroup.shipmentMethodTypeId?if_exists}"/>
                                <input type="hidden" name="carrierPartyId" value="${orderItemShipGroup.carrierPartyId?if_exists}"/>
                                <input type="hidden" name="carrierRoleTypeId" value="${orderItemShipGroup.carrierRoleTypeId?if_exists}"/>
                                <input type="hidden" name="productStoreId" value="${productStoreId?if_exists}"/>
                            </#if>
                    </td>
                </#if>
                <td nowrap="nowrap">
                    ${uiLabelMap.ProductAdditionalShippingCharge}:<br/>
                    <input type="text" name="additionalShippingCharge" value="${packingSession.getAdditionalShippingCharge()?if_exists}" size="20"/>
                    <#if packageSeqIds?has_content>
                        <a href="javascript:document.completePackForm.action='<@ofbizUrl>calcPackSessionAdditionalShippingCharge</@ofbizUrl>';document.completePackForm.submit();" class="buttontext">${uiLabelMap.ProductEstimateShipCost}</a>
                        <br/>
                    </#if>
                </td>
              <td>
                ${uiLabelMap.ProductHandlingInstructions}:<br/v>
                <textarea name="handlingInstructions" rows="2" cols="30">${packingSession.getHandlingInstructions()?if_exists}</textarea>
              </td>
              <td align="right">
                <div>
                  <#assign buttonName = "${uiLabelMap.ProductComplete}">
                  <#if forceComplete?default("false") == "true">
                    <#assign buttonName = "${uiLabelMap.ProductCompleteForce}">
                  </#if>
                  <input type="button" value="${buttonName}" onclick="javascript:document.completePackForm.submit();"/>
                </div>
              </td>
            </tr>
          </table>
          <br/>
        </form>
      </#if>

      <!-- packed items display -->
      <#assign packedLines = packingSession.getLines()?if_exists>
      <#if packedLines?has_content>
        <hr/>
        <br/>
        <table class="basic-table" cellspacing='0'>
          <tr class="header-row">
            <td>${uiLabelMap.ProductItem} #</td>
            <td>${uiLabelMap.ProductProductId}</td>
            <td>${uiLabelMap.ProductDescription}</td>
            <td>${uiLabelMap.ProductInventoryItem} #</td>
            <td align="right">${uiLabelMap.ProductPackedQty}</td>
            <#--td align="right"><div class="tableheadtext">${uiLabelMap.ProductPackedWeight}&nbsp;(${("uiLabelMap.ProductShipmentUomAbbreviation_" + defaultWeightUomId)?eval})</td-->
            <td align="right">${uiLabelMap.ProductPackage} #</td>
            <td>&nbsp;</td>
          </tr>
          <#list packedLines as line>
            <#assign product = Static["org.ofbiz.product.product.ProductWorker"].findProduct(delegator, line.getProductId())/>
            <tr>
              <td>${line.getOrderItemSeqId()}</td>
              <td>${line.getProductId()?default("N/A")}</td>
              <td>
                  <a href="/catalog/control/EditProduct?productId=${line.getProductId()?if_exists}${externalKeyParam}" class="linktext" target="_blank">${product.internalName?if_exists?default("[N/A]")}</a>
              </td>
              <td>${line.getInventoryItemId()}</td>
              <td align="right">${line.getQuantity()}</td>
              <#--td align="right"><div class="tabletext">${line.getWeight()}</td-->
              <td align="right">${line.getPackageSeq()}</td>
              <td align="right"><a href="<@ofbizUrl>ClearPackLine?facilityId=${facilityId}&orderId=${line.getOrderId()}&orderItemSeqId=${line.getOrderItemSeqId()}&shipGroupSeqId=${line.getShipGroupSeqId()}&amp;productId=${line.getProductId()?default("")}&inventoryItemId=${line.getInventoryItemId()}&packageSeqId=${line.getPackageSeq()}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonClear}</a></td>
            </tr>
          </#list>
        </table>
      </#if>
    </#if>

    <#if orderId?has_content>
      <script language="javascript">
        document.singlePackForm.productId.focus();
      </script>
    <#else>
      <script language="javascript">
        document.selectOrderForm.orderId.focus();
      </script>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>
</div>
