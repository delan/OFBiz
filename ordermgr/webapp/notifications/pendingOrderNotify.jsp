
<%@ page import="java.util.*, org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.workflow.*, org.ofbiz.core.workflow.client.*" %>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
	String server = UtilProperties.getPropertyValue("url.properties", "force.http.host", "localhost");
	String port = UtilProperties.getPropertyValue("url.properties", "port.http", "80");

	StringBuffer serverRoot = new StringBuffer();
	serverRoot.append("http://");
	serverRoot.append(server);
	if (!"80".equals(port)) {
		serverRoot.append(":");
		serverRoot.append(port);
	}
	serverRoot.append("/ordermgr/control/orderview?order_id=" + request.getParameter("orderId")); 
	
	String workEffortId = request.getParameter("workEffortId");
	GenericValue activity = null;
	List assignments = null;
	if (workEffortId != null) {
		activity = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
		assignments = activity.getRelated("WorkEffortPartyAssignment");
	}
%>

<html>
<head>
  <%@page contentType='text/html; charset=UTF-8'%>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body>

<h1>Attention!</h1>
<div>&nbsp;</div>

<table width="70%">
  <tr>
    <td align="right"><b>Order #:</b></td>
    <td><%=request.getParameter("orderId")%></td>
  </tr>
  <tr>
    <td align="right"><b>Order Date:</b></td>
    <td><%=request.getParameter("orderDate")%></td>
  </tr>
  <tr>
    <td colspan="2">&nbsp;</td>
  </tr>
  <tr>
    <td align="right"><b>Estimated Start Date:</b></td>
    <td><%=request.getParameter("estimatedStartDate")%></td>
  </tr>
  <tr>
    <td align="right"><b>Actual Start Date:</b></td>
    <td><%=request.getParameter("actualStartDate")%></td>
  </tr>
  <tr>
    <td align="right"><b>Current State:<b></td>
    <td><%=WfUtil.getOMGStatus(request.getParameter("currentStatusId"))%></td>
  </tr>
  <tr>
    <td colspan="2">&nbsp;</td>
  </tr>
 
  <% 
      if (assignments != null) {
	      Iterator i = assignments.iterator();
          while (i.hasNext()) {
              GenericValue assign = (GenericValue) i.next();
  %>  
  <tr>
    <td align="right"><b>Assigned Party ID:</b></td>
    <td><%=assign.getString("partyId")%></td>
  </tr>
  <tr>
    <td align="right"><b>Assigned Role Type:</b></td>
    <td><%=assign.getString("roleTypeId")%></td>
  </tr>
  <tr>
    <td align="right"><b>Assignment Status:</b></td>
    <td><%=assign.getString("statusId")%></td>
  </tr>
  <% 
          }
      }
  %>

  <tr>
    <td colspan="2">&nbsp;</td>
  </tr>
  <tr>
    <td colspan="2' align="center">
	  <a href="<%=serverRoot.toString()%>"><%=serverRoot.toString()%></a>
    </td>
  </tr>
</table>

</body>
</html>