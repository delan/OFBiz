<%
/**
 *  Title: Order History Page
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
 *@author     David E. Jones
 *@created    May 22 2001
 *@version    1.0
 */
%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.ecommerce.order.*" %>

<% pageContext.setAttribute("PageName", "orderhistory"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%
  Iterator statusIterator = null;
  Iterator methodIterator = null;

  Collection orderRoleCollection = helper.findByAnd("OrderRole", 
          UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", "PLACING_CUSTOMER"), null);
  Collection orderHeaderCollection = new ArrayList(orderRoleCollection.size());
  Iterator orderRoleIter = orderRoleCollection.iterator();
  while (orderRoleIter.hasNext()) {
    orderHeaderCollection.add(((GenericValue) orderRoleIter.next()).getRelatedOne("OrderHeader"));
  }
%>
<br>
<table width='100%' border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td bgcolor='#678475' align=left valign=center width='100%'>
      <table width='100%' cellpadding='0' cellspacing='0' border='0'>
        <tr>
          <td align=left><div class="boxhead">Order History</div></td>
          <td align=right>
            <a href="<%=response.encodeURL(controlPath + "/main")%>" class="lightbuttontext">[Back Home]&nbsp;&nbsp;</a>
          </td>
        </tr>
      </table>      
    </td>
  </tr>
  <tr>
    <td align="center" valign="center" bgcolor='white' width='100%'>
  <table width="100%" cellpadding="3" cellspacing="0" border="0">
    <tr>
      <td width="15%">
        <div class="tabletext"><b>Date</b></div>
      </td>
      <td width="15%">
        <div class="tabletext"><b><nobr>Order #</nobr></b></div>
      </td>
      <td width="15%">
        <div class="tabletext"><b>Amount</b></div>
      </td>
      <td width="15%">
        <div class="tabletext"><b>Status</b></div>
      </td>
      <td width="15%"><b></b>
      </td>
    </tr>
    <%if(orderHeaderCollection != null && orderHeaderCollection.size() > 0) {%>
      <%Iterator orderHeaderIter = orderHeaderCollection.iterator();%>
      <%-- XXX should be most recent first --%>
      <%while (orderHeaderIter.hasNext()) {%>
        <%GenericValue orderHeader = (GenericValue) orderHeaderIter.next();%>
        <%String orderStatusString = OrderHelper.getOrderStatusString(orderHeader);%>
        <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
        <tr>
          <td>
            <div class="tabletext"><nobr><%=orderHeader.getTimestamp("orderDate")%></nobr></div>
          </td>
          <td>
            <div class="tabletext"><%=orderHeader.getString("orderId")%></div>
          </td>
          <td>
            <div class="tabletext"><%=UtilFormatOut.formatPrice(OrderHelper.getOrderTotalPrice(orderHeader))%></div>
          </td>
          <td>
            <div class="tabletext"><%=orderStatusString%></div>
          </td>
          <td align=right>
            <a href="<%=response.encodeURL(controlPath + "/orderstatus?order_id=" + orderHeader.getString("orderId"))%>" class='buttontext'>[View]</a>
          </td>
        </tr>
      <%}%>
    <%}else{%>
      <tr><td colspan="8"><div class='head3'>No Orders Found</div></td></tr>
    <%}%>
  </table>
      </td>
    </tr>
  </table>
<%-- <a href="<%=response.encodeURL(controlPath + "/main")%>" class="buttontext">&nbsp;[Back Home]</a> --%>
<br>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
