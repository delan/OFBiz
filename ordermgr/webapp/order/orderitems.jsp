<%--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     Eric Pabst
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    May 22 2001
 *@version    1.0
--%>
    
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Order Items</div>
          </td>
          <ofbiz:if name="maySelectItems">
            <td valign="middle" align="right" nowrap>
              <a href='javascript:void(0);' class="lightbuttontext">[Edit]</a>             
            </td>
          </ofbiz:if>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
              <table width="100%" border="0" cellpadding="0" cellspacing='0'>
                <tr align=left valign=bottom>
                  <th width="35%" align="left"><div class="tabletext"><b>Product</b></div></th>
                  <th width="30%" align="left"><div class="tabletext"><b>Status</b></div></th>
                  <th width="5%" align="right"><div class="tabletext"><b>Quantity</b></div></th>
                  <th width="10%" align="right"><div class="tabletext"><b>Unit / List Price</b></div></th>
                  <th width="10%" align="right"><div class="tabletext"><b>Adjustments</b></div></th>
                  <th width="10%" align="right"><div class="tabletext"><b>Subtotal</b></div></th>
                </tr>
             <%if (orderItemList != null) pageContext.setAttribute("orderItemList", orderItemList);%>
             <ofbiz:iterator name="orderItem" property="orderItemList">
                <tr><td colspan="7"><hr class='sepbar'></td></tr>

                <tr>
                  <%pageContext.setAttribute("productId", orderItem.getString("productId"));%>
                  <ofbiz:if type="String" name="productId" value="shoppingcart.CommentLine">
                    <td colspan="1" valign="top">    
                      <b><div class="tabletext"> &gt;&gt; <%=orderItem.getString("itemDescription")%></div></b>
                    </td>
                  </ofbiz:if>
                  <ofbiz:unless type="String" name="productId" value="shoppingcart.CommentLine">
                    <td valign="top">
                      <div class="tabletext">
                        <%=orderItem.getString("productId")%> - <%=orderItem.getString("itemDescription")%>
                      </div>
                      <div class="tabletext">
                        <a href="/catalog/control/EditProduct?productId=<%=orderItem.getString("productId")%>" class="buttontext" target='_blank'>[catalog]</a>
                        <a href="/ecommerce/control/product?product_id=<%=orderItem.getString("productId")%>" class="buttontext" target='_blank'>[ecommerce]</a>
                      </div>
                    </td>
                    <%-- now show status details per line item --%>
                    <%GenericValue currentItemStatus = orderItem.getRelatedOneCache("StatusItem");%>
                    <%if (currentItemStatus != null) pageContext.setAttribute("currentItemStatus", currentItemStatus);%>
                    <td align="left" colspan="1">                    
                        <%-- Item Status Changed --%>
                        <%if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session)) {%>
                          <% if (orderItem.getString("statusId") != null && !orderItem.getString("statusId").equals("ITEM_COMPLETED") && !orderItem.getString("statusId").equals("ITEM_CANCELLED")) { %>
                            <% List itemStatusChange = delegator.findByAnd("StatusValidChange",UtilMisc.toMap("statusId",orderItem.getString("statusId"))); %>
                            <% pageContext.setAttribute("itemStatusChange", itemStatusChange); %>
                            <form name="statusUpdate<%=orderItem.getString("orderItemSeqId")%>" method="post" action="<ofbiz:url>/changeOrderItemStatus?<%=qString%></ofbiz:url>">
                               <input type="hidden" name="orderId" value="<%=orderItem.getString("orderId")%>"> 
                               <input type="hidden" name="orderItemSeqId" value="<%=orderItem.getString("orderItemSeqId")%>">     
                               <select name="statusId" style="font-size: x-small;">
                                 <option value="<%=orderItem.getString("statusId")%>"><%=currentItemStatus == null ? orderItem.getString("statusId") : currentItemStatus.getString("description")%></option>
                                 <option value="<%=orderItem.getString("statusId")%>">----</option>
                                 <ofbiz:iterator name="status" property="itemStatusChange">
                                   <%GenericValue changeStatusItem = status.getRelatedOneCache("ToStatusItem");%>
                                   <option value="<%=status.getString("statusIdTo")%>"><%=changeStatusItem == null ? status.getString("statusIdTo") : changeStatusItem.getString("description")%></option>               
                                 </ofbiz:iterator>
                               </select>
                               <a href="javascript:document.statusUpdate<%=orderItem.getString("orderItemSeqId")%>.submit();" class="buttontext">[Save]</a>
                            </form>                                                                              					
                          <%}%>
                        <%} else {%>                                                                                  
                        <div class='tabletext'>Current: <ofbiz:entityfield attribute="currentItemStatus" field="description"/><%-- [<ofbiz:entityfield attribute="orderItem" field="statusId"/>]--%></div>
                        <%}%>
                        <%-- End of item status changes --%>       
                        
                        <%Collection orderItemStatuses = orderReadHelper.getOrderItemStatuses(orderItem);%>
                        <%if (orderItemStatuses != null) pageContext.setAttribute("orderItemStatuses", orderItemStatuses);%>
                        <ofbiz:iterator name="orderItemStatus" property="orderItemStatuses">
                            <%GenericValue loopStatusItem = orderItemStatus.getRelatedOneCache("StatusItem");%>
                            <%if (loopStatusItem != null) pageContext.setAttribute("loopStatusItem", loopStatusItem);%>
                            <div class='tabletext'>
                                <ofbiz:entityfield attribute="orderItemStatus" field="statusDatetime"/>: 
                                <ofbiz:entityfield attribute="loopStatusItem" field="description"/>
                                <%-- [<ofbiz:entityfield attribute="orderItemStatus" field="statusId"/>] --%>
                            </div>
                        </ofbiz:iterator>                       
                    </td>
                    <td align="center" valign="top">
                        <div class="tabletext" nowrap><%=UtilFormatOut.formatQuantity(orderItem.getDouble("quantity"))%>&nbsp;</div>
                    </td>
                    <td align="right" valign="top">
                        <div class="tabletext" nowrap><%=UtilFormatOut.formatPrice(orderItem.getDouble("unitPrice"))%> / <%=UtilFormatOut.formatPrice(orderItem.getDouble("unitListPrice"))%></div>
                    </td>
                    <td align="right" valign="top" nowrap>
                        <div class="tabletext" nowrap><%=UtilFormatOut.formatPrice(OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false))%></div>
                    </td>
                    <td align="right" valign="top" nowrap>
                      <div class="tabletext"><%=UtilFormatOut.formatPrice(OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustments))%></div>
                    </td>
                  <ofbiz:if name="maySelectItems">
                    <td>
                      &nbsp;<%--<input name="item_id" value="<%=orderItem.getString("orderItemSeqId")%>" type="checkbox">--%>
                    </td>
                  </ofbiz:if>
                  </ofbiz:unless>
                </tr>
                <%-- now show adjustment details per line item --%>
                <%Collection orderItemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orderAdjustments);%>
                <%if (orderItemAdjustments != null) pageContext.setAttribute("orderItemAdjustments", orderItemAdjustments);%>
                <ofbiz:iterator name="orderItemAdjustment" property="orderItemAdjustments">
                    <%GenericValue adjustmentType = orderItemAdjustment.getRelatedOneCache("OrderAdjustmentType");%>
                    <tr>
                        <td align="right" colspan="2"><div class="tabletext" style='font-size: xx-small;'><b><i>Adjustment</i>:</b> <b><%=adjustmentType.getString("description")%></b> <%=UtilFormatOut.ifNotEmpty(orderItemAdjustment.getString("description"), ": ", "")%> <%=UtilFormatOut.ifNotEmpty(orderItemAdjustment.getString("comments"), "(", ")")%></div></td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td align="right"><div class="tabletext" style='font-size: xx-small;'><%=UtilFormatOut.formatPrice(OrderReadHelper.calcItemAdjustment(orderItemAdjustment, orderItem))%></div></td>
                        <td>&nbsp;</td>
                    </tr>
                </ofbiz:iterator>

                <%-- now show price info per line item --%>
                <%Collection orderItemPriceInfos = orderReadHelper.getOrderItemPriceInfos(orderItem);%>
                <%if (orderItemPriceInfos != null) pageContext.setAttribute("orderItemPriceInfos", orderItemPriceInfos);%>
                <ofbiz:iterator name="orderItemPriceInfo" property="orderItemPriceInfos">
                    <tr>
                        <td align="right" colspan="2"><div class="tabletext" style='font-size: xx-small;'><b><i>Price Rule</i>:</b> [<%=orderItemPriceInfo.getString("productPriceRuleId")%>:<%=orderItemPriceInfo.getString("productPriceActionSeqId")%>] <%=orderItemPriceInfo.getString("description")%></div></td>
                        <td>&nbsp;</td>
                        <td align="right"><div class="tabletext" style='font-size: xx-small;'><%=UtilFormatOut.formatPrice(orderItemPriceInfo.getDouble("modifyAmount"))%></div></td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
                </ofbiz:iterator>

                <%-- now show inventory reservation info per line item --%>
                <%Collection orderItemInventoryReses = orderReadHelper.getOrderItemInventoryReses(orderItem);%>
                <%if (orderItemInventoryReses != null) pageContext.setAttribute("orderItemInventoryReses", orderItemInventoryReses);%>
                <ofbiz:if name="orderItemInventoryReses" size="0">
                    <ofbiz:iterator name="orderItemInventoryRes" property="orderItemInventoryReses">
                        <tr>
                            <td align="right" colspan="2">
                                <div class="tabletext" style='font-size: xx-small;'>
                                    <b><i>Inventory</i>:</b>
                                    <a target='facility' href='/facility/control/EditInventoryItem?inventoryItemId=<%=UtilFormatOut.checkNull(orderItemInventoryRes.getString("inventoryItemId"))%>' class='buttontext' style='font-size: xx-small;'><%=UtilFormatOut.checkNull(orderItemInventoryRes.getString("inventoryItemId"))%></a>
                                </div>
                            </td>
                            <td align="center"><div class="tabletext" style='font-size: xx-small;'><%=UtilFormatOut.formatQuantity(orderItemInventoryRes.getDouble("quantity"))%>&nbsp;</div></td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                            <td>&nbsp;</td>
                        </tr>
                    </ofbiz:iterator>
                </ofbiz:if>
              </ofbiz:iterator>
              <ofbiz:unless name="orderItemList" size="0">
                <tr><td><font color="red">ERROR: Sales Order Lines lookup failed.</font></td></tr>
              </ofbiz:unless>

                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                <%if (orderHeaderAdjustments != null) pageContext.setAttribute("orderHeaderAdjustments", orderHeaderAdjustments);%>
                <ofbiz:iterator name="orderHeaderAdjustment" property="orderHeaderAdjustments">
                    <%GenericValue adjustmentType = orderHeaderAdjustment.getRelatedOneCache("OrderAdjustmentType");%>
                    <tr>
                        <td align="right" colspan="4"><div class="tabletext"><b><%=adjustmentType.getString("description")%></b> <%=UtilFormatOut.ifNotEmpty(orderHeaderAdjustment.getString("comments"), ": ", "")%></div></td>                       
                        <td align="right" nowrap><div class="tabletext"><%=UtilFormatOut.formatPrice(OrderReadHelper.calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal))%></div></td>
                        <td>&nbsp;</td>                        
                    </tr>
                </ofbiz:iterator>
                
                <%-- Add new Adjustment --%>
                <%if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session)) {%>
                <% List adjustmentTypes = delegator.findAll("OrderAdjustmentType", UtilMisc.toList("description")); %>
                <% pageContext.setAttribute("adjustmentTypes", adjustmentTypes); %>
                <tr>                  
                  <form name="addAdjustmentForm" method="post" action="<ofbiz:url>/addOrderAdjustment?<%=qString%></ofbiz:url>">
                    <input type="hidden" name="orderId" value="<%=orderId%>">
                    <td align="right" colspan="4">
                      <select name="orderAdjustmentTypeId" style="font-size: x-small;">
                        <ofbiz:iterator name="type" property="adjustmentTypes">
                          <option value="<%=UtilFormatOut.checkNull(type.getString("orderAdjustmentTypeId"))%>"><%=UtilFormatOut.checkNull(type.getString("description"))%></option>
                        </ofbiz:iterator>
                      </select>
                    </td>
                    <td align="right">
                      <input type="text" name="amount" size="6" value="0.00" style="font-size: x-small;">
                    </td>
                    <td align="right">
                      <a href="javascript:document.addAdjustmentForm.submit();" class="buttontext">[Add]</a>
                    </td>
                  </form>
                </tr>
                <%}%>

                <tr><td colspan=1></td><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                    <td align="right" colspan="5"><div class="tabletext"><b>Items Subtotal</b></div></td>
                    <td align="right" nowrap><div class="tabletext"><%= UtilFormatOut.formatPrice(orderSubTotal)%></div></td>
                </tr>

                <%double otherAdjAmount = OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false);%>
                <tr>
                    <td align="right" colspan="5"><div class="tabletext"><b>Total Other Order Adjustments</b></div></td>
                    <td align="right" nowrap><div class="tabletext"><%=UtilFormatOut.formatPrice(otherAdjAmount)%></div></td>
                </tr>
                <%-- do tax and shipping separate so that we can total up the line item adjustments and the order header adjustments --%>
                <%double shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);%>
                <%shippingAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true);%>
                <tr>
                    <td align="right" colspan="5"><div class="tabletext"><b>Total Shipping and Handling</b></div></td>
                    <td align="right" nowrap><div class="tabletext"><%=UtilFormatOut.formatPrice(shippingAmount)%></div></td>
                </tr>
                <%double taxAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, true, false);%>
                <%taxAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false);%>
                <tr>
                    <td align="right" colspan="5"><div class="tabletext"><b>Total Sales Tax</b></div></td>                    
                    <td align="right" nowrap><div class="tabletext"><%=UtilFormatOut.formatPrice(taxAmount)%></div></td>
                </tr>
                <tr><td colspan=1></td><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                    <td align="right" colspan="5"><div class="tabletext"><b>Total Due</b></div></td>
                   <td align="right" nowrap>
                  <div class="tabletext"><%=UtilFormatOut.formatPrice(OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments))%></div>
                    </td>
                </tr>
            <%-- } else { %>
              <tr><td><font color="red">ERROR: Sales Order lookup failed.</font></td></tr> --%>
            </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
