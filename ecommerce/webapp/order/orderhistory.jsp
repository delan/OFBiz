<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>

<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>


<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.ecommerce.order.*" %>

<% pageContext.setAttribute("PageName", "orderhistory"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<p class="head1">Order History</p>
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

<a href="<%=response.encodeURL(controlPath + "/main")%>" class="buttonlink">&nbsp;[Back Home]</a>

  <table width="90%" cellpadding="3" cellspacing="0" border="1">
    <tr>
      <td width="15%">
        <div class="tabletext"><b>Date</b></div>
      </td>
      <td width="15%">
        <div class="tabletext"><b><nobr>Order #</nobr></b></div>
      </td>
<%--      <td width="15%">
        <div class="tabletext"><b><nobr>Store</nobr></b></div>
      </td> --%>
      <td width="15%">
        <div class="tabletext"><b>Amount</b></div>
      </td>
      <td width="15%">
        <div class="tabletext"><b>Status</b></div>
      </td>
      <td width="15%"><b></b>
      </td>
    </tr>
<%
 if(orderHeaderCollection != null && orderHeaderCollection.size() > 0) {
  Iterator orderHeaderIter = orderHeaderCollection.iterator();
  //XXX should be most recent first
  while (orderHeaderIter.hasNext()) {
    GenericValue orderHeader = (GenericValue) orderHeaderIter.next();
    String orderStatusString = OrderHelper.getOrderStatusString(orderHeader);
%>
    <tr>
      <td>
        <div class="tabletext"><nobr><%=orderHeader.getTimestamp("orderDate")%></nobr></div>
      </td>
      <td>
        <div class="tabletext"><%=orderHeader.getString("orderId")%></div>
      </td>
      <%-- <td>
        <div class="tabletext"><%=orderHeader.getStoreId()%></div>
      </td>
--%>      <td>
        <div class="tabletext"><%=UtilFormatOut.formatPrice(OrderHelper.getOrderTotalPrice(orderHeader))%></div>
      </td>
      <td>
        <div class="tabletext"><%=orderStatusString%></div>
      </td>
      <td>
        <a href="<%=response.encodeURL(controlPath + "/orderstatus?order_identifier=" + orderHeader.getString("orderId"))%>">[View]</a>
      </td>
    </tr>
<%
   }
 }
 else
 {
%>
<tr>
<td colspan="8">
<h3>No Orders Found</h3>
</td>
</tr>
<%}%>
  </table>
<a href="<%=response.encodeURL(controlPath + "/main")%>" class="buttonlink">&nbsp;[Back Home]</a>
<br>
<br>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>



