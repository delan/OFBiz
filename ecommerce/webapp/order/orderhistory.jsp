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
<%@ page import="org.ofbiz.commonapp.order.order.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<% pageContext.setAttribute("PageName", "orderhistory"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%
  Iterator statusIterator = null;
  Iterator methodIterator = null;

  Collection orderRoleCollection = delegator.findByAnd("OrderRole", 
          UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", "PLACING_CUSTOMER"), null);
  Collection orderHeaderList = EntityUtil.orderBy(EntityUtil.getRelated("OrderHeader", orderRoleCollection), UtilMisc.toList("orderDate DESC"));
  pageContext.setAttribute("orderHeaderList", orderHeaderList);
%>
<br>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td align=left><div class="boxhead">Order History</div></td>
          <td align=right>
            <a href="<ofbiz:url>/main</ofbiz:url>" class="lightbuttontext">[Back Home]&nbsp;&nbsp;</a>
          </td>
        </tr>
      </table>      
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
  <table width="100%" cellpadding="3" cellspacing="0" border="0">
    <tr>
      <td width="30%">
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
    <ofbiz:iterator name="orderHeader" property="orderHeaderList">
      <%-- XXX should be most recent first --%>
	<%OrderReadHelper order = new OrderReadHelper(orderHeader); %>
        <%String orderStatusString = order.getStatusString();%>
        <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
        <tr>
          <td>
            <div class="tabletext"><nobr><%=orderHeader.getTimestamp("orderDate")%></nobr></div>
          </td>
          <td>
            <div class="tabletext"><%=orderHeader.getString("orderId")%></div>
          </td>
          <td>
            <div class="tabletext"><%=UtilFormatOut.formatPrice(order.getTotalPrice())%></div>
          </td>
          <td>
            <div class="tabletext"><%=orderStatusString%></div>
          </td>
          <td align=right>
            <a href="<ofbiz:url><%="/orderstatus?order_id=" + orderHeader.getString("orderId")%></ofbiz:url>" class='buttontext'>[View]</a>
          </td>
        </tr>
    </ofbiz:iterator>
    <ofbiz:unless name="orderHeaderList" size="0">
      <tr><td colspan="8"><div class='head3'>No Orders Found</div></td></tr>
    </ofbiz:unless>
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<%-- <a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">&nbsp;[Back Home]</a> --%>
<br>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
