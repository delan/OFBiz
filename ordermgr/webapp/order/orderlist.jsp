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
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*, org.ofbiz.commonapp.party.contact.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<%String listStatusId = request.getParameter("listStatusId");%>
<%if (listStatusId == null || listStatusId.length() == 0) listStatusId = "ORDERED";%>
<%Collection statusItems = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "ORDER_STATUS"), UtilMisc.toList("sequenceId"));%>
<%if (statusItems != null) pageContext.setAttribute("statusItems", statusItems);%>
<%
  Collection orderHeaderList = delegator.findByAnd("OrderHeader", UtilMisc.toMap("statusId", listStatusId), UtilMisc.toList("orderDate DESC"));
  if (orderHeaderList != null) pageContext.setAttribute("orderHeaderList", orderHeaderList);
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
          </TD>
          <TD align=right width='25%'>
              <FORM name="lookup" action="<ofbiz:url>/orderview</ofbiz:url>" method="POST">
                  <input type="text" name="order_id" size="9">&nbsp;<a href="javascript:document.lookup.submit();" class="lightbuttontext">[LookUp&nbsp;Order]</a>            
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
<div class="tabletext"><b>Orders with the status: <%=listStatusId%></b></div>
<!-- Insert in here -->
<center>
  <table width="100%" border="0" class="edittable">
    <tr>
      <td>
        <table width="100%" cellpadding="3" cellspacing="0" border="0">
          <tr class="viewOneTR1">
            <td width="25%"><div class="tabletext"><b>Date</b></div></td>
            <td width="15%"><div class="tabletext"><b>Order&nbsp;#</b></div></td>
            <td width="25%"><div class="tabletext"><b>Amount</b></div></td>
            <td width="25%"><div class="tabletext"><b>Status</b></div></td>
            <td width="10%"><div class="tabletext">&nbsp;</div></td>
          </tr>
          <%String rowClass = "viewManyTR2";%>
          <ofbiz:iterator name="orderHeader" property="orderHeaderList">
          
	        <%OrderReadHelper order = new OrderReadHelper(orderHeader); %>
	        <%pageContext.setAttribute("totalPrice", new Double(order.getTotalPrice()));%>
	        <%pageContext.setAttribute("orderStatus", order.getStatusString());%>
            <%rowClass = rowClass.equals("viewManyTR2") ? "viewManyTR1" : "viewManyTR2";%>

          <tr class="<%=rowClass%>">
            <td>
              <div class="tabletext"><nobr><ofbiz:entityfield attribute="orderHeader" field="orderDate"/></nobr></div>
            </td>
            <td>
              <div class="tabletext"><ofbiz:entityfield attribute="orderHeader" field="orderId"/></div>
            </td>
            <td>
              <div class="tabletext"><ofbiz:field attribute="totalPrice" type="currency"/></div>
            </td>
            <td>
              <div class="tabletext"><ofbiz:entityfield attribute="orderHeader" field="statusId"/></div>
            </td>
            <td align=right>
              <a href="<ofbiz:url>/orderview?order_id=<ofbiz:entityfield attribute="orderHeader" field="orderId"/></ofbiz:url>" class='buttontext'>[View]</a>
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
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
