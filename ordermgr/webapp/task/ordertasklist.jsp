<%--
 *  Description: None
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski 
 *@version    $Revision$
 *@since      2.0
--%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ordermgr.task.TaskWorker" %>


<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>

<%	
	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	// get user level tasks
	List partyTasks = delegator.findByAnd("OrderTaskList", UtilMisc.toMap("orderRoleTypeId", "PLACING_CUSTOMER", "wepaPartyId", userLogin.getString("partyId")));

	// get this user's roles
	List partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId")));
	if (partyRoles != null) partyTasks = EntityUtil.filterByDate(partyTasks);
	if (partyRoles != null) pageContext.setAttribute("partyRoles", partyRoles);		

	// get role level tasks
	List roleTasks = delegator.findByAnd("OrderTaskList", UtilMisc.toMap("orderRoleTypeId", "PLACING_CUSTOMER", "roleTypeId", "ORDER_CLERK"));
	if (roleTasks != null) roleTasks = EntityUtil.filterByDate(roleTasks);	
	if (roleTasks != null) pageContext.setAttribute("roleTasks", roleTasks);				
%>

<script language="JavaScript">
    function viewOrder(form) {
        if (form.taskStatus.value == "WF_NOT_STARTED") {
            form.action = "<ofbiz:url>/acceptassignment</ofbiz:url>";
        } else if (form.delegate.checked) {
            form.action = "<ofbiz:url>/delegateassignment</ofbiz:url>";
        } else {
            form.action = "<ofbiz:url>/orderview</ofbiz:url>";
        }
        form.submit();
    }
</script>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='70%' >
            <div class='boxhead'>&nbsp;Orders Needing Attention</div>
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

  <ofbiz:if name="partyTasks" size="0">
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User</div>
                  <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <TR>
                      <td><a href="<ofbiz:url>/tasklist?sort=orderId</ofbiz:url>" class="tableheadbutton">Order&nbsp;Number</a></td>
                      <td><a href="<ofbiz:url>/tasklist?sort=name</ofbiz:url>" class="tableheadbutton">Customer&nbsp;Name</a></td>
                      <td><a href="<ofbiz:url>/tasklist?sort=orderDate</ofbiz:url>" class="tableheadbutton">Order&nbsp;Date</a></td>
                      <TD><a href="<ofbiz:url>/tasklist?sort=actualStartDate</ofbiz:url>" class="tableheadbutton">Start&nbsp;Date/Time</a></td>
                      <TD><a href="<ofbiz:url>/tasklist?sort=priority</ofbiz:url>" class="tableheadbutton">Priority</a></td>
                      <TD><a href="<ofbiz:url>/tasklist?sort=currentStatusId</ofbiz:url>" class="tableheadbutton">My&nbsp;Status</a></td>
                    </TR>
                    <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                    <ofbiz:iterator name="task" property="partyTasks" type="org.ofbiz.core.entity.GenericValue" expandMap="true">
                      <TR>
                        <td>                          
                          <a href="<ofbiz:url>/orderview?<ofbiz:print attribute="orderId"/></ofbiz:url>" class="buttontext">
                            <ofbiz:print attribute="orderId"/>
                          </a>
                        </td>
                        <td>
                          <a href="/partymgr/control/viewprofile?party_id=<%=task.getString("customerPartyId")%>" target="partymgr" class="buttontext"><%=TaskWorker.getCustomerName(task)%></a>
                        </td>
                        <td>
                          <div class="tabletext">
                            <ofbiz:print attribute="orderDate"/>
                          </div>
                        </td>                                            
                        <TD><DIV class='tabletext'><ofbiz:print attribute="actualStartDate" default="N/A"/></DIV></TD>
                        <TD><DIV class='tabletext'><ofbiz:print attribute="priority"/></DIV></TD>
                        <td>
                          <a href="/workeffort/control/activity?workEffortId=<ofbiz:print attribute="workEffortId"/>" target="workeffort" class="buttontext">
                          <%=TaskWorker.getPrettyStatus(task)%>
                        </a>
                      </td>
                      </TR>
                    </ofbiz:iterator>
                  </TABLE>
              </td>
            </tr>
          </table>
        </TD>
      </TR>
  </ofbiz:if>

  <ofbiz:if name="roleTasks" size="0">
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
          <tr>
            <td>
              <div class='head3'>Workflow Activities Assigned to User Role</div>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <td><a href="<ofbiz:url>/tasklist?sort=orderId</ofbiz:url>" class="tableheadbutton">Order&nbsp;Number</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=name</ofbiz:url>" class="tableheadbutton">Customer&nbsp;Name</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=orderDate</ofbiz:url>" class="tableheadbutton">Order&nbsp;Date</a></td>                                  
                  <td><a href="<ofbiz:url>/tasklist?sort=actualStartDate</ofbiz:url>" class="tableheadbutton">Start&nbsp;Date/Time</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=wepaPartyId</ofbiz:url>" class="tableheadbutton">Party</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=roleTypeId</ofbiz:url>" class="tableheadbutton">Role</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=priority</ofbiz:url>" class="tableheadbutton">Priority</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=currentStatusId</ofbiz:url>" class="tableheadbutton">Status</a></td>
                  <td>&nbsp;</td>
                </TR>
                <TR><TD colspan='11'><HR class='sepbar'></TD></TR>
                <ofbiz:iterator name="task" property="roleTasks" type="org.ofbiz.core.entity.GenericValue" expandMap="true">
                  <form method="get" name="F<ofbiz:print attribute="workEffortId"/>">
                    <input type="hidden" name="order_id" value="<ofbiz:print attribute="orderId"/>">
                    <input type="hidden" name="partyId" value="<%=userLogin.getString("partyId")%>">
                    <input type="hidden" name="roleTypeId" value="<ofbiz:print attribute="roleTypeId"/>">
                    <input type="hidden" name="fromDate" value="<ofbiz:print attribute="fromDate"/>">
                    <input type="hidden" name="workEffortId" value="<ofbiz:print attribute="workEffortId"/>">
                    <input type="hidden" name="taskStatus" value="<ofbiz:print attribute="currentStatusId"/>">
                    <tr>
                      <td>
                        <% String acceptString = "orderId=" + task.getString("orderId") + "&partyId=" + userLogin.getString("partyId") + "&roleTypeId=" + task.getString("roleTypeId") + "&workEffortId=" + task.getString("workEffortId") + "&fromDate=" + task.getString("fromDate"); %>
                        <a href="javascript:viewOrder(document.F<ofbiz:print attribute="workEffortId"/>);" class="buttontext">
                          <ofbiz:print attribute="orderId"/>
                        </a>
                      </td>
                      <td>
                        <a href="/partymgr/control/viewprofile?party_id=<%=task.getString("customerPartyId")%>" target="partymgr" class="buttontext"><%=TaskWorker.getCustomerName(task)%></a>
                      </td>
                      <td>
                        <div class="tabletext">
                          <ofbiz:print attribute="orderDate"/>
                        </div>
                      </td>                                          
                      <td><div class='tabletext'><ofbiz:print attribute="actualStartDate" default="N/A"/></div></td>
                      <td><div class='tabletext'><ofbiz:print attribute="wepaPartyId" default="N/A"/></div></td>
                      <td><div class='tabletext'><%=TaskWorker.getRoleDescription(task)%></div></td>
                      <td><div class='tabletext'><ofbiz:print attribute="priority"/></div></td>
                      <td>
                        <a href="/workeffort/control/activity?workEffortId=<ofbiz:print attribute="workEffortId"/>" target="workeffort" class="buttontext">
                          <%=TaskWorker.getPrettyStatus(task)%>
                        </a>
                      </td>
                      <ofbiz:if name="currentStatusId" value="WF_NOT_STARTED">
                        <td align="right"><input type="checkbox" name="delegate" value="true" checked></td>
                      </ofbiz:if>
                      <ofbiz:unless name="currentStatusId" value="WF_NOT_STARTED">
                        <td align="right"><input type="checkbox" name="delegate" value="true"></td>
                      </ofbiz:unless>
                    </tr>
                  </form>
                </ofbiz:iterator>
              </TABLE>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </ofbiz:if>

  <ofbiz:unless name="partyTasks">
    <ofbiz:unless name="roleTasks">
      <div class="tabletext"><b>&nbsp;No orders pending.</b></div>
    </ofbiz:unless>
  </ofbiz:unless>

          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
<%}%>
