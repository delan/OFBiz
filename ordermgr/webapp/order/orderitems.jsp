<%
/**
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
 *@created    May 22 2001
 *@version    1.0
 */
%>

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
              <table width="100%" border="0" cellpadding="1" cellspacing='0'>
                <tr align=left valign=bottom>
                  <th width="35%" align="left"><div class="tabletext"><b>Product</b></div></th>
                  <th width="30%" align="left"><div class="tabletext"><b>Status</b></div></th>
                  <th width="5%" align="right"><div class="tabletext"><b>Quantity</b></div></th>
                  <th width="10%" align="right"><div class="tabletext"><b>Unit Price</b></div></th>
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
                        <div class='tabletext'>Current: <ofbiz:entityfield attribute="currentItemStatus" field="description"/> [<ofbiz:entityfield attribute="orderItem" field="statusId"/>]</div>
                        <%Collection orderItemStatuses = orderReadHelper.getOrderItemStatuses(orderItem);%>
                        <%if (orderItemStatuses != null) pageContext.setAttribute("orderItemStatuses", orderItemStatuses);%>
                        <ofbiz:iterator name="orderItemStatus" property="orderItemStatuses">
                            <%GenericValue loopStatusItem = orderItemStatus.getRelatedOneCache("StatusItem");%>
                            <%if (loopStatusItem != null) pageContext.setAttribute("loopStatusItem", loopStatusItem);%>
                            <div class='tabletext'>
                                <ofbiz:entityfield attribute="orderItemStatus" field="statusDatetime"/>: 
                                <ofbiz:entityfield attribute="loopStatusItem" field="description"/>
                                [<ofbiz:entityfield attribute="orderItemStatus" field="statusId"/>]
                            </div>
                        </ofbiz:iterator>
                    </td>
                    <td align="right" valign="top">
                        <div class="tabletext" nowrap><%=UtilFormatOut.formatQuantity(orderItem.getDouble("quantity"))%></div>
                    </td>
                    <td align="right" valign="top">
                        <div class="tabletext" nowrap><%=UtilFormatOut.formatQuantity(orderItem.getDouble("unitPrice"))%></div>
                    </td>
                    <td align="right" valign="top" nowrap>
                        <div class="tabletext" nowrap><%=UtilFormatOut.formatPrice(OrderReadHelper.getOrderItemAdjustments(orderItem, orderAdjustments, true, false, false))%></div>
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
                        <td align="right" colspan="4"><div class="tabletext"><b><%=adjustmentType.getString("description")%></b> <%=UtilFormatOut.ifNotEmpty(orderItemAdjustment.getString("comments"), ": ", "")%></div></td>
                        <td align="right"><div class="tabletext"><%=UtilFormatOut.formatPrice(OrderReadHelper.calcItemAdjustment(orderItemAdjustment, orderItem))%></div></td>
                        <td>&nbsp;</td>
                    </tr>
                </ofbiz:iterator>
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
                <%double shippingAmount = OrderReadHelper.getOrderItemsAdjustments(orderItems, orderAdjustments, false, false, true);%>
                <%shippingAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true);%>
                <tr>
                    <td align="right" colspan="5"><div class="tabletext"><b>Total Shipping and Handling</b></div></td>
                    <td align="right" nowrap><div class="tabletext"><%=UtilFormatOut.formatPrice(shippingAmount)%></div></td>
                </tr>
                <%double taxAmount = OrderReadHelper.getOrderItemsAdjustments(orderItems, orderAdjustments, false, true, false);%>
                <%taxAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false);%>
                <tr>
                    <td align="right" colspan="5"><div class="tabletext"><b>Total Sales Tax</b></div></td>
                    <td align="right" nowrap><div class="tabletext"><%=UtilFormatOut.formatPrice(taxAmount)%></div></td>
                </tr>
                <tr><td colspan=1></td><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                    <td align="right" colspan="5"><div class="tabletext"><b>Total Due</b></div></td>
                   <td align="right" nowrap>
                  <div class="tabletext"><%=UtilFormatOut.formatPrice(OrderReadHelper.getTotalPrice(orderItems, orderAdjustments))%></div>
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
