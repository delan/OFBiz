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
 *@author     Andy Zeneski (jaz@zsolv.com)
 *@author     David E. Jones
 *@created    October 18, 2001
 *@version    1.0
--%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*, org.ofbiz.commonapp.party.contact.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%if(security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)) {%>

<%
    Collection orderHeaderList = null;
    // search by status info
    String listStatusId = request.getParameter("listStatusId");
    // search by party info
    String partyId = request.getParameter("partyId");
    // search by date info
    String minDate = request.getParameter("minDate");
    String maxDate = request.getParameter("maxDate");

    String lookupErrorMessage = null;

    String pageParamString = "";
    if (partyId != null) {
        if (partyId.length() != 0) {
            pageParamString = "partyId=" + partyId;
            pageContext.setAttribute("PARTY_MODE", "YES");
            Collection orderRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "PLACING_CUSTOMER"));
            List exprs = new ArrayList();
            if (orderRoles != null && orderRoles.size() > 0) {
                Iterator i = orderRoles.iterator();
                while (i.hasNext() ) {
                    GenericValue v = (GenericValue) i.next();
                    exprs.add(new EntityExpr("orderId", EntityOperator.EQUALS, v.getString("orderId")));
                }
                if (exprs.size() > 0) {
                    try {
                    orderHeaderList = delegator.findByOr("OrderHeader", exprs, UtilMisc.toList("-orderDate"));
                    } catch (Throwable t) { Debug.logError(t); }
                }
            }
        } else {
            lookupErrorMessage = "Required parameter 'partyId' cannot be empty.";
        }
    } else if (minDate != null || maxDate != null) {
        // lookup by date
        if (minDate.length() != 0 || maxDate.length() != 0) {
            pageParamString = "minDate=" + minDate + "&maxDate=" + maxDate;
            pageContext.setAttribute("DATE_MODE", "YES");
            List exprs = new ArrayList();
            try {
                if (minDate != null && minDate.length() > 8) {
                    minDate = minDate.trim();
                    if (minDate.length() < 14)
                        minDate = minDate + " " + "00:00:00.000";
                    exprs.add(new EntityExpr("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(minDate, "Timestamp", null, null)));
                }
                if (maxDate != null && maxDate.length() > 8) {
                    maxDate = maxDate.trim();
                    if (maxDate.length() < 14)
                        maxDate = maxDate + " " + "23:59:59.999";
                    exprs.add(new EntityExpr("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(maxDate, "Timestamp", null, null)));
                }
                orderHeaderList = delegator.findByAnd("OrderHeader", exprs, UtilMisc.toList("-orderDate"));
            } catch (Exception e) { Debug.logError(e); }
        } else {
            lookupErrorMessage = "Both 'minDate' and 'maxDate' cannot be empty.";
        }
    } else if (listStatusId != null) {
        // lookup by status
        pageParamString = "listStatusId=" + listStatusId;
        pageContext.setAttribute("STATUS_MODE", "YES");
        orderHeaderList = delegator.findByAnd("OrderHeader", UtilMisc.toMap("statusId", listStatusId), UtilMisc.toList("-orderDate"));
    }

    if (orderHeaderList != null && orderHeaderList.size() > 0) pageContext.setAttribute("orderHeaderList", orderHeaderList);

    Collection statusItems = delegator.findByAndCache("StatusItem", UtilMisc.toMap("statusTypeId", "ORDER_STATUS"), UtilMisc.toList("sequenceId"));
    if (statusItems != null) pageContext.setAttribute("statusItems", statusItems);

    GenericValue listStatusItem = null;
    if (UtilValidate.isNotEmpty(listStatusId)) {
        listStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", listStatusId));
    }

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
    if (lookupErrorMessage != null) pageContext.setAttribute("lookupErrorMessage", lookupErrorMessage);
%>

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="50%"><div class="boxhead">Find Orders</div></td>
          <TD align=right width='50%'>
          <%--
              <FORM name="lookup" action="<ofbiz:url>/orderview</ofbiz:url>" method="POST">
                  <input type="text" name="order_id" size="9" style="font-size: x-small;">&nbsp;<a href="javascript:document.lookup.submit();" class="lightbuttontext">[Quick&nbsp;Find]</a>
              </FORM>
          --%>&nbsp;
          </TD>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td>
            <table width='100%' border='0' cellspacing='0' cellpadding='2'>
              <form method="post" action="<ofbiz:url>/orderview</ofbiz:url>" name="lookupordero">
                <tr>
                  <td width="25%" align=right><div class="tabletext">Order ID</div></td>
                  <td width="40%">
                    <input type="text" name="order_id" size="20" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("order_id"))%>'>
                  </td>
                  <td width="35%"><a href="javascript:document.lookupordero.submit()" class="buttontext">[Lookup Order]</a></td>
                </tr>
              </form>  
                            
              <form method="post" action="<ofbiz:url>/orderlist</ofbiz:url>" name="lookuporderp">
                <tr>
                  <td width="25%" align=right><div class="tabletext">Party ID</div></td>
                  <td width="40%">
                    <input type="text" name="partyId" size="20" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("partyId"))%>'>
                  </td>
                  <td width="35%"><a href="javascript:document.lookuporderp.submit()" class="buttontext">[Lookup Orders]</a></td>
                </tr>
              </form>  
                
              <form method="post" action="/partymgr/control/findparty" name="findnameform">
                <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
                <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
                <tr>
                  <td width="25%" align=right><div class="tabletext">First Name</div></td>
                  <td width="40%">
                    <input type="text" name="first_name" size="30" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("first_name"))%>'>
                  </td>
                  <td width="35%">&nbsp;</td>
                </tr>
                <tr>
                  <td width="25%" align=right><div class="tabletext">Last Name</div></td>
                  <td width="40%">
                    <input type="text" name="last_name" size="30" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("last_name"))%>'>
                  </td>
                  <td width="35%"><a href="javascript:document.findnameform.submit()" class="buttontext">[Lookup Party]</a></td>
                </tr>
              </form>         
            </table>
          </td>
          <td>
            <table width='100%' border='0' cellspacing='0' cellpadding='2'>
              <form method="post" action="<ofbiz:url>/orderlist</ofbiz:url>" name="lookuporderd">
                <tr>
                  <td width="25%" align=right><div class="tabletext">Date&nbsp;Span:&nbsp;Min</div></td>
                  <td width="40%">
                    <input type="text" name="minDate" size="22" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("minDate"))%>'>
                  </td>
                  <td width="35%">&nbsp</td>
                </tr>
                <tr>
                  <td width="25%" align=right><div class="tabletext">Date&nbsp;Span:&nbsp;Max</div></td>
                  <td width="40%">
                    <input type="text" name="maxDate" size="22" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("maxDate"))%>'>
                  </td>
                  <td width="35%"><a href="javascript:document.lookuporderd.submit()" class="buttontext">[Lookup Orders]</a></td>
                </tr>
              </form>
              
              <form method="post" action="<ofbiz:url>/orderlist</ofbiz:url>" name="findorderstatus">
                <tr>
                  <td width="25%" align=right><div class="tabletext">Current Status</div></td>
                  <td width="40%">
                    <select name="listStatusId" style="font-size: x-small;">
                      <option><%=listStatusId == null ? "Choose a status:" : (listStatusItem == null ? listStatusId : listStatusItem.getString("description"))%></option>
                      <option value="<%=listStatusId == null?"":listStatusId%>">----</option>
                      <ofbiz:iterator name="statusItem" property="statusItems">
                        <option value='<%=statusItem.getString("statusId")%>'><%=statusItem.getString("description")%></option>
                      </ofbiz:iterator>
                    </select>
                  </td>
                  <td width="35%"><a href="javascript:document.findorderstatus.submit()" class="buttontext">[Lookup Orders]</a></td>
                </tr>
              </form>   
                         
              <form method="post" action="/partymgr/control/findparty" name="findemailform">
                <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
                <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
                <tr>
                  <td width="25%" align=right><div class="tabletext">E-Mail Address</div></td>
                  <td width="40%">
                    <input type="text" name="email" size="30" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("email"))%>'>
                  </td>
                  <td width="35%"><a href="javascript:document.findemailform.submit()" class="buttontext">[Lookup Party]</a></td>
                </tr>
              </form>

              <form method="post" action="/partymgr/control/findparty" name="findloginform">
                <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
                <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
                <tr>
                  <td width="25%" align=right><div class="tabletext">User Login ID</div></td>
                  <td width="40%">
                    <input type="text" name="userlogin_id" size="30" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("userlogin_id"))%>'>
                  </td>
                  <td width="35%"><a href="javascript:document.findloginform.submit()" class="buttontext">[Lookup Party]</a></td>
                </tr>
              </form>              
            </table            
          </td>                      
        </tr>
      </table>
    </TD>
  </TR>
</table>

<BR>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="50%"><div class="boxhead">Orders Found</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <ofbiz:if name="orderHeaderList" size="0">
                <%if (viewIndex > 0) {%>
                  <a href="<ofbiz:url><%="/orderlist?" + pageParamString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="lightbuttontext">[Previous]</a> |
                <%}%>
                <%if (listSize > 0) {%>
                  <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
                <%}%>
                <%if (listSize > highIndex) {%>
                  | <a href="<ofbiz:url><%="/orderlist?" + pageParamString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="lightbuttontext">[Next]</a>
                <%}%>
              </ofbiz:if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td width="5%" align="left"><div class="tableheadtext">OrderID</div></td>
          <td width="20%" align="left"><div class="tableheadtext">Name</div></td>
          <td width="5%" align="right"><div class="tableheadtext">Total Items</div></td>
          <td width="10%" align="right"><div class="tableheadtext">Order Total</div></td>
          <td width="5%" align="left"><div class="tableheadtext">&nbsp;</div></td>
          <td width="20%" align="left"><div class="tableheadtext">Status</div></td>
          <td width="20%" align="left"><div class="tableheadtext">Order Date</div></td>
          <td width="5%" align="left"><div class="tableheadtext">PartyID</div></td>
          <td width="10%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan='9'><hr class='sepbar'></td>
        </tr>
        <ofbiz:if name="orderHeaderList">
          <% String rowClass = "viewManyTR2"; %>
          <ofbiz:iterator name="orderHeader" property="orderHeaderList" offset="<%=lowIndex%>" limit="<%=viewSize%>">
            <%
                OrderReadHelper order = new OrderReadHelper(orderHeader);
                GenericValue headerStatusItem = orderHeader.getRelatedOneCache("StatusItem");
                String orderStatus = headerStatusItem == null ? orderHeader.getString("statusId") : headerStatusItem.getString("description");
                GenericValue placingParty = order.getPlacingParty();
                if (placingParty != null) pageContext.setAttribute("placingParty", placingParty);
                rowClass = rowClass.equals("viewManyTR2") ? "viewManyTR1" : "viewManyTR2";
            %>
              <tr class="<%=rowClass%>">
                <td><a href="<ofbiz:url>/orderview?order_id=<%EntityField.run("orderHeader", "orderId", pageContext);%></ofbiz:url>" class='buttontext'><%EntityField.run("orderHeader", "orderId", pageContext);%></a>
                </td>
                <td>
                  <ofbiz:if name="placingParty">
                    <%EntityField.run("placingParty", "lastName", pageContext);%>
                    <% if (placingParty.get("lastName") != null && placingParty.getString("lastName").length() > 0) { %>
                      &cedil;&nbsp;
                    <%}%>
                    <%EntityField.run("placingParty", "firstName", pageContext);%>
                  </ofbiz:if>
                  <ofbiz:unless name="placingParty">
                    &nbsp;
                  </ofbiz:unless>
                </td>
                <td align="right"><div class="tabletext"><%=UtilFormatOut.formatQuantity(new Double(order.getTotalOrderItemsQuantity()))%></div></td>
                <td align="right"><div class="tabletext"><%=UtilFormatOut.formatPrice(new Double(order.getOrderGrandTotal()))%></div></td>
                <td>&nbsp;</td>
                <td><div class="tabletext"><%=UtilFormatOut.checkNull(orderStatus)%></div></td>
                <td><div class="tabletext"><nobr><%=orderHeader.getTimestamp("orderDate")%></nobr></div></td>
                <td><a href="/partymgr/control/viewprofile?party_id=<%EntityField.run("placingParty", "partyId", pageContext);%>" class="buttontext"><%EntityField.run("placingParty", "partyId", pageContext);%></a></td>
                <td align=right>
                  <a href="<ofbiz:url>/orderview?order_id=<%EntityField.run("orderHeader", "orderId", pageContext);%></ofbiz:url>" class='buttontext'>[View]</a>
                </td>
              </tr>
          </ofbiz:iterator>
        </ofbiz:if>
        <ofbiz:if name="lookupErrorMessage">
          <tr>
            <td colspan='4'><div class="head3"><ofbiz:print attribute="lookupErrorMessage"/></div></td>
          </tr>
        </ofbiz:if>
        <ofbiz:unless name="orderHeaderList">
          <tr>
            <td colspan='4'><div class='head3'>No orders found.</div></td>
          </tr>
        </ofbiz:unless>
      </table>
    </TD>
  </TR>
</TABLE>

<%}else{%>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
<%}%>
