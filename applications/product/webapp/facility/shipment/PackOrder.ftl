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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Rev$
 *@since      3.0
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
    <#assign useGrid = requestParameters.useGrid?default("N")>
    <div class="head1">Pack Order<span class='head2'>&nbsp;in&nbsp;${facility.facilityName?if_exists} [${uiLabelMap.CommonId}:${facilityId?if_exists}]</span></div>
    <div>&nbsp;</div>

    <!-- select order form -->
    <form name="selectOrderForm" method="post" action="<@ofbizUrl>PackOrder</@ofbizUrl>" style='margin: 0;'>
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
      <table border='0' cellpadding='2' cellspacing='0'>
        <tr>
          <td width="25%" align='right'><div class="tabletext">Order #</div></td>
          <td width="1">&nbsp;</td>
          <td width="25%">
            <input type="text" class="inputBox" name="orderId" size="20" maxlength="20" value="${orderId?if_exists}"/>
            <span class="tabletext">/</span>
            <input type="text" class="inputBox" name="shipGroupSeqId" size="6" maxlength="6" value="${shipGroupSeqId?if_exists}"/
          </td>
          <td><div class="tabletext">Use Grid:&nbsp;<input type="checkbox" name="useGrid" value="Y" <#if (useGrid == "Y")>checked</#if>></div></td>
          <td><div class='tabletext'>&nbsp;</div></td>
        </tr>
        <tr>
          <td colspan="2">&nbsp;</td>
          <td colspan="2">
            <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:document.selectOrderForm.submit();">
            <a href="javascript:document.selectOrderForm.submit();" class="buttontext">Pack Order</a>
          </td>
        </tr>
      </table>
    </form>

    <form name="completePackForm" method="post" action="<@ofbizUrl>CompletePack</@ofbizUrl>" style='margin: 0;'>
      <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
      <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
      <input type="hidden" name="handlingInstructions" value="${packingSession.getHandlingInstructions()?if_exists}"/>
    </form>
    <form name="clearPackForm" method="post" action="<@ofbizUrl>ClearPackAll</@ofbizUrl>" style='margin: 0;'>
      <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
      <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
    </form>
    <form name="incPkgSeq" method="post" action="<@ofbizUrl>SetNextPackageSeq</@ofbizUrl>" style='margin: 0;'>
      <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
      <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
    </form>

    <#if orderHeader?exists && orderHeader?has_content>
      <hr class="sepbar"/>
      <div class='head2'>Order #${orderId} / ShipGroup #${shipGroupSeqId}</div>
      <div class="tableheadtext">${packingSession.getPrimaryOrderId()?default("N/A")} / ${packingSession.getPrimaryShipGroupSeqId()?default("N/A")}</div>
      <div>&nbsp;</div>

      <!-- manual per item form -->
      <#if useGrid != "Y">
        <form name="singlePackForm" method="post" action="<@ofbizUrl>ProcessPackOrder</@ofbizUrl>" style='margin: 0;'>
          <input type="hidden" name="packageSeq" value="${packingSession.getCurrentPackageSeq()}"/>
          <input type="hidden" name="orderId" value="${orderId}"/>
          <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId}"/>
          <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
          <input type="hidden" name="useGrid" value="${useGrid}"/>
          <table border='0' cellpadding='2' cellspacing='0'>
            <tr>
              <td align="right" colspan="4">
                <nobr>
                <div class="tabletext">Handling Instructions:&nbsp;
                  <input type="text" class="inputBox" name="handlingInstructions" size="30" value="${packingSession.getHandlingInstructions()?if_exists}" onchange="javascript:document.completePackForm.handlingInstructions.value = document.singlePackForm.handlingInstructions.value;"/>
                </div>
                </nobr>
              </td>
            </tr>
            <tr>
              <td align="right" colspan="4">
                <div class="tabletext">
                  Current Package Sequence: <b>${packingSession.getCurrentPackageSeq()}</b>
                  <input type="button" value="Next Package" onclick="javascript:document.incPkgSeq.submit();">
                </div>
                <div>
                  <input type="button" value="Complete" onclick="javascript:document.completePackForm.submit();"/>
                </div>
              </td>
            <tr>
              <td width="25%" align='right'><div class="tabletext">Product #</div></td>
              <td width="1">&nbsp;</td>
              <td width="25%">
                <input type="text" class="inputBox" name="productId" size="20" maxlength="20" value=""/>
                <span class="tabletext">@</span>
                <input type="text" class="inputBox" name="quantity" size="6" maxlength="6" value="1"/>
              </td>
              <td><div class='tabletext'>&nbsp;</div></td>
            </tr>
            <tr>
              <td colspan="2">&nbsp;</td>
              <td colspan="2">
                <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:document.singlePackForm.submit();">
                <a href="javascript:document.singlePackForm.submit();" class="buttontext">Pack Item</a>
              </td>
            </tr>
          </table>
        </form>
      </#if>

      <!-- auto grid form -->
      <#if useGrid == "Y" && itemInfos?has_content>
        <hr class="sepbar"/>
        <div>&nbsp;</div>
        <form name="multiPackForm" method="post" action="<@ofbizUrl>ProcessBulkPackOrder</@ofbizUrl>" style='margin: 0;'>
          <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
          <input type="hidden" name="orderId" value="${orderId?if_exists}">
          <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}">
          <input type="hidden" name="originFacilityId" value="${facilityId?if_exists}">
          <input type="hidden" name="useGrid" value="${useGrid}"/>

          <table border='0' width="100%" cellpadding='2' cellspacing='0'>
            <tr>
              <td>&nbsp;</td>
              <td><div class="tableheadtext">Item #</td>
              <td><div class="tableheadtext">Sku</td>
              <td><div class="tableheadtext">Desciption</td>
              <td align="right"><div class="tableheadtext">Ordered Qty</td>
              <td align="right"><div class="tableheadtext">Packed Qty</td>
              <td>&nbsp;</td>
              <td align="center"><div class="tableheadtext">Pack Qty</td>
              <td align="center"><div class="tableheadtext">Package</td>
            </tr>
            <tr>
              <td colspan="9">
                <hr class="sepbar"/>
              </td>
            </tr>

            <#list itemInfos as orderItem>
              <tr>
                <td><input type="checkbox" name="sel_${orderItem.orderItemSeqId}" value="Y"/></td>
                <td><div class="tabletext">${orderItem.orderItemSeqId}</td>
                <td><div class="tabletext">${orderItem.productId?default("N/A")}</td>
                <td><div class="tabletext">${orderItem.itemDescription?if_exists}</td>
                <td align="right"><div class="tabletext">${orderItem.quantity}</td>
                <td align="right"><div class="tabletext">${packingSession.getPackedQuantity(orderId, orderItem.orderItemSeqId, shipGroupSeqId)}</td>
                <td>&nbsp;&nbsp;</td>
                <td align="center">
                  <#assign inputQty = (orderItem.quantity - packingSession.getPackedQuantity(orderId, orderItem.orderItemSeqId, shipGroupSeqId))>
                  <input type="text" class="inputBox" size="7" name="qty_${orderItem.orderItemSeqId}" value="${inputQty}">
                </td>
                <td align="center">
                  <select name="pkg_${orderItem.orderItemSeqId}">
                    <option value="1">Package 1</option>
                    <option value="2">Package 2</option>
                    <option value="3">Package 3</option>
                    <option value="4">Package 4</option>
                    <option value="5">Package 5</option>
                  </select>
                </td>
                <input type="hidden" name="prd_${orderItem.orderItemSeqId}" value="${orderItem.productId?if_exists}">
              </tr>
            </#list>
            <tr><td colspan="9">&nbsp;</td></tr>
            <tr>
              <td colspan="5">
                <div class="tabletext">Handling Instructions:&nbsp;
                  <input type="text" class="inputBox" name="handlingInstructions" size="30" value="${packingSession.getHandlingInstructions()?if_exists}" onchange="javascript:document.completePackForm.handlingInstructions.value = document.multiPackForm.handlingInstructions.value;"/>
                </div>
              </td>
              <td colspan="4" align="right">
                <input type="submit" value="Pack Items">
                &nbsp;
                <input type="button" value="Clear" onclick="javascript:document.clearPackForm.submit();"/>
                &nbsp;
                <input type="button" value="Complete" onclick="javascript:document.completePackForm.submit();"/>
              </td>
            </tr>
          </table>
        </form>
      </#if>

      <!-- packed items display -->
      <#assign packedLines = packingSession.getLines()?if_exists>
      <#if packedLines?has_content>
        <hr class="sepbar"/>
        <div>&nbsp;</div>
        <table border='0' width="100%" cellpadding='2' cellspacing='0'>
          <tr>
            <td><div class="tableheadtext">Item #</td>
            <td><div class="tableheadtext">Sku</td>
            <td><div class="tableheadtext">Desciption</td>
            <td><div class="tableheadtext">Inv Item #</td>
            <td align="right"><div class="tableheadtext">Packed Qty</td>
            <td align="right"><div class="tableheadtext">Package #</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td colspan="7">
              <hr class="sepbar"/>
            </td>
          </tr>
          <#list packedLines as line>
            <#assign orderItem = orderReadHelper.getOrderItem(line.getOrderItemSeqId())?if_exists>
            <tr>
              <td><div class="tabletext">${line.getOrderItemSeqId()}</td>
              <td><div class="tabletext">${line.getProductId()?default("N/A")}</td>
              <td><div class="tabletext">${(orderItem.itemDescription)?default("[N/A]")}</td>
              <td><div class="tabletext">${line.getInventoryItemId()}</td>
              <td align="right"><div class="tabletext">${line.getQuantity()}</td>
              <td align="right"><div class="tabletext">${line.getPackageSeq()}</td>
              <td align="right"><a href="<@ofbizUrl>ClearPackLine?facilityId=${facilityId}&orderId=${line.getOrderId()}&orderItemSeqId=${line.getOrderItemSeqId()}&shipGroupSeqId=${line.getShipGroupSeqId()}&inventoryItemId=${line.getInventoryItemId()}&packageSeqId=${line.getPackageSeq()}</@ofbizUrl>" class="buttontext">Clear</a></td>
            </tr>
          </#list>
        </table>
      </#if>
    </#if>

    <#if orderId?has_content>
      <#if useGrid != "Y">
        <script language="javascript">
          document.singlePackForm.productId.focus();
        </script>
      </#if>
    <#else>
      <script language="javascript">
          document.selectOrderForm.orderId.focus();
        </script>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>