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
	if (partyRoles != null)
		pageContext.setAttribute("partyRoles", partyRoles);

	// get role level tasks
	List roleTasks = delegator.findByAnd("OrderTaskList", UtilMisc.toMap("orderRoleTypeId", "PLACING_CUSTOMER", "roleTypeId", "ORDER_CLERK"));
	if (roleTasks != null)
		pageContext.setAttribute("roleTasks", roleTasks);

		
%>

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
                      <td><a href="<ofbiz:url>/tasklist?sort=ORDERNUM</ofbiz:url>" class="sortbutton">Order&nbsp;Number</a></td>
                      <td><a href="<ofbiz:url>/tasklist?sort=FULLNAME</ofbiz:url>" class="sortbutton">Customer&nbsp;Name</a></td>
                      <td><a href="<ofbiz:url>/tasklist?sort=ORDERDATE</ofbiz:url>" class="sortbutton">Order&nbsp;Date</a></td>
                      <td><a href="<ofbiz:url>/tasklist?sort=ORDERDATE</ofbiz:url>" class="sortbutton">Site</a></td>
                      <td><a href="<ofbiz:url>/tasklist?sort=ORDERDATE</ofbiz:url>" class="sortbutton">Type</a></td>
                      <TD><a href="<ofbiz:url>/tasklist?sort=TASKDATE</ofbiz:url>" class="sortbutton">Start&nbsp;Date/Time</a></td>
                      <TD><a href="<ofbiz:url>/tasklist?sort=PRIORITY</ofbiz:url>" class="sortbutton">Priority</a></td>
                      <TD><a href="<ofbiz:url>/tasklist?sort=TASKSTATUS</ofbiz:url>" class="sortbutton">My&nbsp;Status</a></td>
                    </TR>
                    <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                    <ofbiz:iterator name="task" property="partyTasks" type="org.ofbiz.core.entity.GenericValue" expandMap="true">
                      <TR>
                        <td>
                          <a href="<ofbiz:url>/orderview<ofbiz:print attribute="ACCEPTSTR"/>&orderId=<ofbiz:print attribute="ORDERNUM"/></ofbiz:url>" class="adminbutton">
                            <ofbiz:print attribute="ORDERNUM"/>
                          </a>
                        </td>
                        <td>
                          <div class="tabletext"><ofbiz:print attribute="FULLNAME"/></div>
                        </td>
                        <td>
                          <div class="tabletext">
                            <ofbiz:print attribute="ORDERDATE"/>
                          </div>
                        </td>
                        <td><div class="tabletext" align="center">R</div></td>
                        <td><div class="tabletext" align="center">W</div></td>
                        <TD><DIV class='tabletext'><ofbiz:print attribute="TASKDATE" default="N/A"/></DIV></TD>
                        <TD><DIV class='tabletext'><ofbiz:print attribute="PRIORITY"/></DIV></TD>
                        <td>
                          <a href="<ofbiz:url>/activity?workEffortId=<ofbiz:print attribute="WORKEFFORT"/></ofbiz:url>" class="adminbutton">
                            <ofbiz:print attribute="TASKSTATUS"/>
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
                  <td><a href="<ofbiz:url>/tasklist?sort=orderId</ofbiz:url>" class="sortbutton">Order&nbsp;Number</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=name</ofbiz:url>" class="sortbutton">Customer&nbsp;Name</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=orderDate</ofbiz:url>" class="sortbutton">Order&nbsp;Date</a></td>                                  
                  <td><a href="<ofbiz:url>/tasklist?sort=actualStartDate</ofbiz:url>" class="sortbutton">Start&nbsp;Date/Time</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=wepaPartyId</ofbiz:url>" class="sortbutton">Party</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=roleTypeId</ofbiz:url>" class="sortbutton">Role</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=priority</ofbiz:url>" class="sortbutton">Priority</a></td>
                  <td><a href="<ofbiz:url>/tasklist?sort=currentStatusId</ofbiz:url>" class="sortbutton">Status</a></td>
                  <td>&nbsp;</td>
                </TR>
                <TR><TD colspan='11'><HR class='sepbar'></TD></TR>
                <ofbiz:iterator name="task" property="roleTasks" type="org.ofbiz.core.entity.GenericValue" expandMap="true">
                  <form method="get" name="F<ofbiz:print attribute="workEffortId"/>">
                    <input type="hidden" name="orderId" value="<ofbiz:print attribute="orderId"/>">
                    <input type="hidden" name="partyId" value="<ofbiz:print attribute="PARTY"/>">
                    <input type="hidden" name="roleTypeId" value="<ofbiz:print attribute="AROLE"/>">
                    <input type="hidden" name="fromDate" value="<ofbiz:print attribute="fromDate"/>">
                    <input type="hidden" name="workEffortId" value="<ofbiz:print attribute="workEffortId"/>">
                    <input type="hidden" name="taskStatus" value="<ofbiz:print attribute="currentStatusId"/>">
                    <tr>
                      <td>
                        <div class="tabletext"><ofbiz:print attribute="orderId"/></div>
                      </td>
                      <td>
                        <div class="tabletext"><%=TaskWorker.getCustomerName(task)%></div>
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
                      <ofbiz:if name="TASKSTATUS" value="Pending">
                        <td align="right"><input type="checkbox" name="delegate" value="true" checked></td>
                      </ofbiz:if>
                      <ofbiz:unless name="TASKSTATUS" value="Pending">
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
