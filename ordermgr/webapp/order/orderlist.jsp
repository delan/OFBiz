<%
/**
 *  Title: Main Page
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
 *@author     Andy Zeneski (jaz@zsolv.com)
 *@author     David E. Jones
 *@created    October 18, 2001
 *@version    1.0
 */
%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*, org.ofbiz.commonapp.party.contact.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>

<%
    Collection orderHeaderList = null;
    String listStatusId = request.getParameter("listStatusId");
    if (listStatusId == null || listStatusId.length() == 0) listStatusId = "ORDER_ORDERED";
    String partyId = request.getParameter("partyId");
    String pageParamString = "";
    if (partyId != null && partyId.length() > 0) {
        //TODO: this is not an efficient way to do this, better to use a view or something to make the query easy and to still have all order fields available just in case this is a list by status
        Debug.logInfo("Getting order by party.");
        pageParamString = "partyId=" + partyId;
        pageContext.setAttribute("PARTY_MODE", "YES");
        Collection orderRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "PLACING_CUSTOMER"));
        Debug.logInfo("OrderRoles: " + orderRoles);
        List exprs = new ArrayList();
        if (orderRoles != null && orderRoles.size() > 0) {
            Iterator i = orderRoles.iterator();
            while (i.hasNext() ) {
                GenericValue v = (GenericValue) i.next();
                exprs.add(new EntityExpr("orderId", EntityOperator.EQUALS, v.getString("orderId")));
            }
            Debug.logInfo("Expressions: " + exprs);
            orderHeaderList = delegator.findByOr("OrderHeader", exprs, UtilMisc.toList("orderDate DESC"));
            Debug.logInfo("OrderHeaderList: " + orderHeaderList);
        }
    } else {
        Debug.logInfo("Getting order by status.");
        pageParamString = "listStatusId=" + listStatusId;
        Collection statusItems = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "ORDER_STATUS"), UtilMisc.toList("sequenceId"));
        if (statusItems != null) pageContext.setAttribute("statusItems", statusItems);
        orderHeaderList = delegator.findByAnd("OrderHeader", UtilMisc.toMap("statusId", listStatusId), UtilMisc.toList("orderDate DESC"));
    }
    if (orderHeaderList != null) pageContext.setAttribute("orderHeaderList", orderHeaderList);


    int viewIndex = 0;
    int viewSize = 20;
    int highIndex = 0;
    int lowIndex = 0;
    int listSize = 0;

    try {
        viewIndex = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_INDEX")).intValue();
    } catch (Exception e) {
        viewIndex = 0;
    }
    try {
        viewSize = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_SIZE")).intValue();
    } catch (Exception e) {
        viewSize = 20;
    }
    if (orderHeaderList != null) {
        listSize = orderHeaderList.size();
    }
    lowIndex = viewIndex * viewSize;
    highIndex = (viewIndex + 1) * viewSize;
    if (listSize < highIndex) {
        highIndex = listSize;
    }

    Debug.logInfo("Low Index: " + lowIndex);
    Debug.logInfo("View Size: " + viewSize);
%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Order List Page</div>
          </TD>
          <TD align=right width='35%'>
            <ofbiz:unless name="PARTY_MODE">
              <FORM name="liststatus" action="<ofbiz:url>/orderlist</ofbiz:url>" method="POST">
                <select name="listStatusId">
                  <option><%=listStatusId%></option>
                  <option value="<%=listStatusId%>">----</option>
                  <!-- <option>ALL_ORDERS</option> is this really a good idea? -->
                  <ofbiz:iterator name="statusItem" property="statusItems">
                    <option><%=statusItem.getString("statusId")%></option>               
                  </ofbiz:iterator>
                </select>&nbsp;<a href="javascript:document.liststatus.submit();" class="lightbuttontext">[Use&nbsp;Status]</a>
              </FORM>
            </ofbiz:unless>
            <ofbiz:if name="PARTY_MODE">
              &nbsp;
            </ofbiz:if>
          </TD>
          <TD align=right width='25%'>
              <FORM name="lookup" action="<ofbiz:url>/orderview</ofbiz:url>" method="POST">
                  <input type="text" name="order_id" size="9">&nbsp;<a href="javascript:document.lookup.submit();" class="lightbuttontext">[Lookup&nbsp;Order]</a>
              </FORM>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <ofbiz:unless name="PARTY_MODE">
              <div class="tabletext"><b>Orders with the status: <%=listStatusId%></b></div>
            </ofbiz:unless>
            <ofbiz:if name="PARTY_MODE">
              <div class="tabletext"><b>Orders for the party with ID: <%=partyId%></b></div>
            </ofbiz:if>

<ofbiz:if name="orderHeaderList" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/orderlist?" + pageParamString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/orderlist?" + pageParamString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>

<!-- Insert in here -->
<center>
  <table width="100%" border="0" class="edittable">
    <tr>
      <td>
        <table width="100%" cellpadding="3" cellspacing="0" border="0">
          <tr class="viewOneTR1">
            <td width="15%"><div class="tabletext"><b>Order&nbsp;#</b></div></td>
            <td width="25%"><div class="tabletext"><b>Amount</b></div></td>
            <%--<td width="25%"><div class="tabletext"><b>Status</b></div></td>--%>
            <td width="25%"><div class="tabletext"><b>Date</b></div></td>
            <td width="10%"><div class="tabletext">&nbsp;</div></td>
          </tr>
          <%String rowClass = "viewManyTR2";%>
          <ofbiz:iterator name="orderHeader" property="orderHeaderList" offset="<%=lowIndex%>" limit="<%=viewSize%>">
              <%OrderReadHelper order = new OrderReadHelper(orderHeader); %>
              <%pageContext.setAttribute("totalPrice", new Double(order.getTotalPrice()));%>
              <%String orderStatus = order.getStatusString();%>
              <%rowClass = rowClass.equals("viewManyTR2") ? "viewManyTR1" : "viewManyTR2";%>
              <tr class="<%=rowClass%>">
                <td><a href="<ofbiz:url>/orderview?order_id=<%EntityField.run("orderHeader", "orderId", pageContext);%></ofbiz:url>" class='buttontext'><%EntityField.run("orderHeader", "orderId", pageContext);%></a>
                <td><div class="tabletext"><ofbiz:field attribute="totalPrice" type="currency"/></div></td>
                <%--<td>
                  <div class="tabletext"><%EntityField.run("orderHeader", "statusId", pageContext);%>
                  <div class="tabletext"><%=orderStatus%></div>
                </td>--%>
                <td><div class="tabletext"><nobr><%EntityField.run("orderHeader", "orderDate", pageContext);%></nobr></div></td>
                <td align=right>
                  <a href="<ofbiz:url>/orderview?order_id=<%EntityField.run("orderHeader", "orderId", pageContext);%></ofbiz:url>" class='buttontext'>[View]</a>
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
</center>
<!-- Between here -->
<ofbiz:if name="orderHeaderList" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/orderlist?" + pageParamString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/orderlist?" + pageParamString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<%}else{%>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
<%}%>
