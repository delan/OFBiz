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

<% if(security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)) { %>

<%		
	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	String sort = request.getParameter("sort");
	List sortOrder = null;
	
	// create the sort order
	sortOrder = UtilMisc.toList("currentStatusId", "-priority", "orderDate");
	if (sort != null) {
		if (sort.equals("name")) {
			sortOrder.add(0, "firstName");
			sortOrder.add(0, "lastName");
		} else {
			sortOrder.add(0, sort);
		}
	}
	List partyTasks = delegator.findByAnd("OrderTaskList", UtilMisc.toMap("statusId", "CAL_ACCEPTED", "orderRoleTypeId", "PLACING_CUSTOMER", "wepaPartyId", userLogin.getString("partyId")), sortOrder);
	if (partyTasks != null) partyTasks = EntityUtil.filterByDate(partyTasks);
	if (partyTasks != null) pageContext.setAttribute("partyTasks", partyTasks);	

	// get this user's roles
	List partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId")));	
	
	// build the role list
	Iterator pri = partyRoles.iterator();
	List pRolesList = new ArrayList();
	while (pri.hasNext()) {
		GenericValue partyRole = (GenericValue) pri.next();	
		if (!partyRole.getString("roleTypeId").equals("_NA_"))
			pRolesList.add(new EntityExpr("roleTypeId", EntityOperator.EQUALS, partyRole.getString("roleTypeId")));
	}
		
	/* This does not work -- need to find out why
	// constant values for getting single orders (by customer)
	List customerRoles = UtilMisc.toList(new EntityExpr("orderRoleTypeId", EntityOperator.EQUALS, "PLACING_CUSTOMER"));
	
	// get all activities, not finished or delegated
	List taskStatuses = UtilMisc.toList(new EntityExpr("statusId", EntityOperator.EQUALS, "CAL_SENT"), new EntityExpr("statusId", EntityOperator.EQUALS, "CAL_ACCEPTED"));
	
	// build the expressions into a single list
	List expressions = UtilMisc.toList(new EntityExprList(pRolesList, EntityOperator.OR), new EntityExprList(taskStatuses, EntityOperator.OR), new EntityExprList(customerRoles, EntityOperator.OR));	
	EntityCondition conditions = new EntityConditionList(expressions, EntityOperator.AND);
	*/
	
	List baseList = UtilMisc.toList(new EntityExpr("orderRoleTypeId", EntityOperator.EQUALS, "PLACING_CUSTOMER"), new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_COMPLETED"), new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "CAL_DELEGATED"));
	List expressions = UtilMisc.toList(new EntityExprList(pRolesList, EntityOperator.OR), new EntityExprList(baseList, EntityOperator.AND));
	EntityCondition conditions = new EntityConditionList(expressions, EntityOperator.AND);
	
	// invoke the query
	List roleTasks = delegator.findByCondition("OrderTaskList", conditions, null, sortOrder);
	//List roleTasks = delegator.findByOr("OrderTaskList", pRolesList, sortOrder);
	if (roleTasks != null) roleTasks = EntityUtil.filterByAnd(roleTasks, baseList);
	if (roleTasks != null) roleTasks = EntityUtil.filterByDate(roleTasks);	
	if (roleTasks != null) pageContext.setAttribute("roleTasks", roleTasks);
%>

<script language="JavaScript">
    function viewOrder(form) {
        if (form.taskStatus.value == "WF_NOT_STARTED") {
        	if (form.delegate.checked) {
            	form.action = "<ofbiz:url>/acceptassignment</ofbiz:url>";
            } else {
            	form.action = "<ofbiz:url>/orderview</ofbiz:url>";
            }	
        } else {
        	if (form.delegate.checked) {
            	form.action = "<ofbiz:url>/delegateassignment</ofbiz:url>";
        	} else {
            	form.action = "<ofbiz:url>/orderview</ofbiz:url>";
        	}
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
                      <td><a href="<ofbiz:url>/tasklist?sort=grandTotal</ofbiz:url>" class="tableheadbutton">Total</a></td>
                      <TD><a href="<ofbiz:url>/tasklist?sort=actualStartDate</ofbiz:url>" class="tableheadbutton">Start&nbsp;Date/Time</a></td>
                      <TD><a href="<ofbiz:url>/tasklist?sort=priority</ofbiz:url>" class="tableheadbutton">Priority</a></td>
                      <TD><a href="<ofbiz:url>/tasklist?sort=currentStatusId</ofbiz:url>" class="tableheadbutton">My&nbsp;Status</a></td>
                    </TR>
                    <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                    <ofbiz:iterator name="task" property="partyTasks" type="org.ofbiz.core.entity.GenericValue" expandMap="true">
                      <TR>
                        <td>               
                          <% String orderStr = "order_id=" + task.getString("orderId") + "&partyId=" + userLogin.getString("partyId") + "&roleTypeId=" + task.getString("roleTypeId") + "&workEffortId=" + task.getString("workEffortId") + "&fromDate=" + task.getString("fromDate"); %>           
                          <a href="<ofbiz:url>/orderview?<%=orderStr%></ofbiz:url>" class="buttontext">
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
                        <td><div class='tabletext'><%=UtilFormatOut.formatPrice(task.getDouble("grandTotal"))%></div></td>                                          
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
                  <td><a href="<ofbiz:url>/tasklist?sort=grandTotal</ofbiz:url>" class="tableheadbutton">Total</a></td>
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
                    <input type="hidden" name="workEffortId" value="<ofbiz:print attribute="workEffortId"/>">
                    <input type="hidden" name="taskStatus" value="<ofbiz:print attribute="currentStatusId"/>">                    
                    <ofbiz:if name="statusId" value="CAL_SENT">
                      <input type="hidden" name="partyId" value="<%=userLogin.getString("partyId")%>">
                      <input type="hidden" name="roleTypeId" value="<ofbiz:print attribute="roleTypeId"/>">
                      <input type="hidden" name="fromDate" value="<ofbiz:print attribute="fromDate"/>">                      
                    </ofbiz:if>
                    <ofbiz:unless name="statusId" value="CAL_SENT">
                      <% java.sql.Timestamp now = UtilDateTime.nowTimestamp(); %>
                      <input type="hidden" name="partyId" value="<%=userLogin.getString("partyId")%>">
                      <input type="hidden" name="roleTypeId" value="<ofbiz:print attribute="roleTypeId"/>">
                      <input type="hidden" name="fromDate" value="<ofbiz:print attribute="fromDate"/>">
                      <input type="hidden" name="fromPartyId" value="<ofbiz:print attribute="wepaPartyId"/>">
                      <input type="hidden" name="fromRoleTypeId" value="<ofbiz:print attribute="roleTypeId"/>">
                      <input type="hidden" name="fromFromDate" value="<ofbiz:print attribute="fromDate"/>">  
                      <input type="hidden" name="toPartyId" value="<%=userLogin.getString("partyId")%>">                    
                      <input type="hidden" name="toRoleTypeId" value="<ofbiz:print attribute="roleTypeId"/>">
                      <input type="hidden" name="toFromDate" value="<%=now%>">
                      <input type="hidden" name="startActivity" value="true">
                    </ofbiz:unless>
                    <tr>
                      <td>                        
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
                      <td><div class='tabletext'><%=UtilFormatOut.formatPrice(task.getDouble("grandTotal"))%></div></td>                                        
                      <td><div class='tabletext'><ofbiz:print attribute="actualStartDate" default="N/A"/></div></td>
                      <td>
                        <ofbiz:if name="wepaPartyId" value="_NA_">
                          <div class="tabletext">N/A</div>
                        </ofbiz:if>
                        <ofbiz:unless name="wepaPartyId" value="_NA_">
                          <a href="/partymgr/control/viewprofile?party_id=<%=task.getString("wepaPartyId")%>" target="partymgr" class="buttontext"><ofbiz:print attribute="wepaPartyId"/></a>
                        </ofbiz:unless>
                      </td>  
                      <td><div class='tabletext'><%=TaskWorker.getRoleDescription(task)%></div></td>
                      <td><div class='tabletext'><ofbiz:print attribute="priority"/></div></td>
                      <td>
                        <a href="/workeffort/control/activity?workEffortId=<ofbiz:print attribute="workEffortId"/>" target="workeffort" class="buttontext">
                          <%=TaskWorker.getPrettyStatus(task)%>
                          <%--<%=task.getString("statusId")%>--%>
                        </a>
                      </td>
                      <ofbiz:if name="statusId" value="CAL_SENT">
                        <td align="right"><input type="checkbox" name="delegate" value="true" checked></td>
                      </ofbiz:if>
                      <ofbiz:unless name="statusId" value="CAL_SENT">
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

  <ofbiz:unless name="partyTasks" size="0">
    <ofbiz:unless name="roleTasks" size="0">
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
