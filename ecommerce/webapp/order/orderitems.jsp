<%
/**
 *  Title: Order Information
 *  Description: None
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
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>

<%@ page import="org.ofbiz.commonapp.order.order.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>

<%double total = 0.0;%>

<TABLE border=0 width='100%' cellpadding=1 cellspacing=0 bgcolor='black'>
  <TR>
    <TD width='100%'>
      <table width="100%" border="0" cellpadding="4" cellspacing="0" bgcolor="#678475">
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
      <table width='100%' border=0 cellpadding=4 cellspacing=0 bgcolor='white'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="1">
    <tr align=left valign=bottom>
      <th width="65%" align="left">Product</th>
      <th width="5%" align="right">Quantity</th>
      <th width="15%" align="right">Unit Price</th>
      <th width="15%" align="right">Line Price</th>
    </tr>
 <%if (orderItemList != null) pageContext.setAttribute("orderItemList", orderItemList);%>
 <ofbiz:iterator name="orderItem" property="orderItemList">
    <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>

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
          <a href="<ofbiz:url><%="/product?product_id=" + orderItem.getString("productId")%></ofbiz:url>" class="buttontext"><%=orderItem.getString("productId")%> - <%=orderItem.getString("itemDescription")%></a>
          </div>
        </td>
        <td align="right" valign="top">
            <div class="tabletext" nowrap>
              <%=UtilFormatOut.formatQuantity(orderItem.getDouble("quantity"))%>
            </div>
        </td>
        <td align="right" valign="top">
            <div class="tabletext" nowrap>
              <%=UtilFormatOut.formatQuantity(orderItem.getDouble("unitPrice"))%>
            </div>
        </td>
        <td align="right" valign="top" nowrap>
          <%double lineTotal = orderItem.getDouble("quantity").doubleValue()*orderItem.getDouble("unitPrice").doubleValue();%>
          <%total += lineTotal;%>
          <div class="tabletext"><%=UtilFormatOut.formatPrice(lineTotal)%></div>
        </td>
      <ofbiz:if name="maySelectItems">
        <td>
          <input name="item_id" value="<%=orderItem.getString("orderItemSeqId")%>" type="checkbox">
        </td>
      </ofbiz:if>
      </ofbiz:unless>
    </tr>
  </ofbiz:iterator>
  <ofbiz:unless name="orderItemList" size="0">
  <tr><td><font color="red">ERROR: Sales Order Lines lookup failed.</font></td></tr>
  </ofbiz:unless>

    <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>

    <tr>
        <td align="right" colspan="3"><div class="tabletext"><b>Subtotal</b></div></td>
        <td align="right" nowrap><div class="tabletext"><%= UtilFormatOut.formatPrice(total)%></div></td>
    </tr>

    <% if (orderAdjustmentIterator != null) pageContext.setAttribute("orderAdjustmentIterator", orderAdjustmentIterator); %>
    <ofbiz:iterator name="orderAdjustmentObject" type="java.lang.Object" property="orderAdjustmentIterator">
    <%Adjustment orderAdjustment = (Adjustment) orderAdjustmentObject;%>
    <tr>
        <td align="right" colspan="3"><div class="tabletext"><b><%=orderAdjustment.getDescription()%></b></div></td>
        <td align="right" nowrap><div class="tabletext"><%= UtilFormatOut.formatPrice(orderAdjustment.getAmount())%></div></td>
        <%total += orderAdjustment.getAmount();%>
    </tr>
    </ofbiz:iterator> 
    <tr><td colspan=2></td><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
    <tr>
        <td align="right" colspan="3"><div class="tabletext"><b>Total Due</b></div></td>
       <td align="right" nowrap>
      <div class="tabletext"><%= UtilFormatOut.formatPrice(total)%></div>
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
