
<%--
NOTE: This page is meant to be included, not called independently

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
 *@author     David E. Jones
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
              <a href='javascript:document.addOrderToCartForm.add_all.value="true";document.addOrderToCartForm.submit()' class="lightbuttontext">[Add All to Cart]</a>
              <a href='javascript:document.addOrderToCartForm.add_all.value="false";document.addOrderToCartForm.submit()' class="lightbuttontext">[Add Checked to Cart]</a>
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
              <table width="100%" border="0" cellpadding="0">
                <tr align=left valign=bottom>
                  <th width="65%" align="left"><span class="tabletext"><b>Product</b></span></th>
                  <th width="5%" align="right"><span class="tabletext"><b>Quantity</b></span></th>
                  <th width="10%" align="right"><span class="tabletext"><b>Unit Price</b></span></th>
                  <th width="10%" align="right"><span class="tabletext"><b>Adjustments</b></span></th>
                  <th width="10%" align="right"><span class="tabletext"><b>Subtotal</b></span></th>
                </tr>
             <%if (orderItems != null) pageContext.setAttribute("orderItems", orderItems);%>
             <ofbiz:iterator name="orderItem" property="orderItems">
                <tr><td colspan="7"><hr class='sepbar'></td></tr>

                <tr>
                  <%pageContext.setAttribute("productId", orderItem.getString("productId"));%>
                  <ofbiz:if type="String" name="productId" value="shoppingcart.CommentLine">
                    <td colspan="1" valign="top">    
                      <b><div class="tabletext"> &gt;&gt; <%EntityField.run("orderItem", "itemDescription", pageContext);%></div></b>
                    </td>
                  </ofbiz:if>
                  <ofbiz:unless type="String" name="productId" value="shoppingcart.CommentLine">
                    <td valign="top">
                      <div class="tabletext">
                      <a href="<%=response.encodeUrl(serverRoot + controlPath + "/product?product_id=" + orderItem.getString("productId"))%>" class="buttontext"><%EntityField.run("orderItem", "productId", pageContext);%> - <%EntityField.run("orderItem", "itemDescription", pageContext);%></a>
                      <%-- <a href="<ofbiz:url><%="/product?product_id=" + orderItem.getString("productId")%></ofbiz:url>" class="buttontext"><%=orderItem.getString("productId")%> - <%=orderItem.getString("itemDescription")%></a> --%>
                      </div>
                    </td>
                    <td align="right" valign="top">
                        <div class="tabletext" nowrap><%EntityField.run("orderItem", "quantity", pageContext);%></div>
                    </td>
                    <td align="right" valign="top">
                        <div class="tabletext" nowrap><%EntityField.run("orderItem", "unitPrice", pageContext);%></div>
                    </td>
                    <td align="right" valign="top">
                        <div class="tabletext" nowrap><ofbiz:format type="currency"><%=UtilFormatOut.formatPrice(OrderReadHelper.getOrderItemAdjustments(orderItem, orderAdjustments, true, false, false))%></ofbiz:format></div>
                    </td>
                    <td align="right" valign="top" nowrap>
                      <div class="tabletext"><ofbiz:format type="currency"><%=UtilFormatOut.formatPrice(OrderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustments))%></ofbiz:format></div>
                    </td>
                  <ofbiz:if name="maySelectItems">
                    <td>
                      <input name="item_id" value="<%=orderItem.getString("orderItemSeqId")%>" type="checkbox">
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
                        <td align="right"><div class="tabletext" style='font-size: xx-small;'><b><i>Adjustment</i>:</b> <b><%=adjustmentType.getString("description")%></b> <%=UtilFormatOut.ifNotEmpty(orderItemAdjustment.getString("description"), ": ", "")%></div></td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td align="right"><div class="tabletext" style='font-size: xx-small;'><ofbiz:format type="currency"><%=UtilFormatOut.formatPrice(OrderReadHelper.calcItemAdjustment(orderItemAdjustment, orderItem))%></ofbiz:format></div></td>
                        <td>&nbsp;</td>
                        <ofbiz:if name="maySelectItems"><td>&nbsp;</td></ofbiz:if>
                    </tr>
                </ofbiz:iterator>
              </ofbiz:iterator>
              <ofbiz:unless name="orderItems" size="0">
              <tr><td><font color="red">ERROR: Sales Order Lines lookup failed.</font></td></tr>
              </ofbiz:unless>

                <tr><td colspan="8"><hr class='sepbar'></td></tr>

                <tr>
                    <td align="right" colspan="4"><div class="tabletext"><b>Subtotal</b></div></td>
                    <td align="right" nowrap><div class="tabletext"><ofbiz:format type="currency"><%=UtilFormatOut.formatPrice(orderSubTotal)%></ofbiz:format></div></td>
                </tr>

                <%Collection headerAdjustmentsToShow = OrderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false);%>
                <%if (headerAdjustmentsToShow != null) pageContext.setAttribute("headerAdjustmentsToShow", headerAdjustmentsToShow);%>
                <ofbiz:iterator name="orderHeaderAdjustment" property="headerAdjustmentsToShow">
                    <%GenericValue adjustmentType = orderHeaderAdjustment.getRelatedOneCache("OrderAdjustmentType");%>
                    <tr>
                        <td align="right" colspan="4"><div class="tabletext"><b><%=adjustmentType.getString("description")%></b></div></td>
                        <td align="right" nowrap><div class="tabletext"><ofbiz:format type="currency"><%=UtilFormatOut.formatPrice(OrderReadHelper.calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal))%></ofbiz:format></div></td>
                    </tr>
                </ofbiz:iterator>
                <%-- do tax and shipping separate so that we can total up the line item adjustments and the order header adjustments --%>
                <%double shippingAmount = OrderReadHelper.getOrderItemsAdjustments(orderItems, orderAdjustments, false, false, true);%>
                <%shippingAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true);%>
                <tr>
                    <td align="right" colspan="4"><div class="tabletext"><b>Shipping and Handling</b></div></td>
                    <td align="right" nowrap><div class="tabletext"><ofbiz:format type="currency"><%=UtilFormatOut.formatPrice(shippingAmount)%></ofbiz:format></div></td>
                </tr>
                <%double taxAmount = OrderReadHelper.getOrderItemsAdjustments(orderItems, orderAdjustments, false, true, false);%>
                <%taxAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false);%>
                <tr>
                    <td align="right" colspan="4"><div class="tabletext"><b>Sales Tax</b></div></td>
                    <td align="right" nowrap><div class="tabletext"><ofbiz:format type="currency"><%=UtilFormatOut.formatPrice(taxAmount)%></ofbiz:format></div></td>
                </tr>
                <tr><td colspan=2></td><td colspan="8"><hr class='sepbar'></td></tr>
                <tr>
                    <td align="right" colspan="4"><div class="tabletext"><b>Total Due</b></div></td>
                    <td align="right" nowrap>
                        <div class="tabletext"><ofbiz:format type="currency"><%=UtilFormatOut.formatPrice(OrderReadHelper.getTotalPrice(orderItems, orderAdjustments))%></ofbiz:format></div>
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
