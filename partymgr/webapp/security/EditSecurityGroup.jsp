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
 *@author     David E. Jones
 *@created    May 10 2002
 *@version    1.0
--%>
<%try {%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<table cellpadding=0 cellspacing=0 border=0 width="100%"><tr><td>&nbsp;&nbsp;</td><td>

<%if(security.hasEntityPermission("SECURITY", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String groupId = request.getParameter("groupId");
    if (UtilValidate.isEmpty(groupId) && UtilValidate.isNotEmpty((String) request.getAttribute("groupId"))) {
        groupId = (String) request.getAttribute("groupId");
    }
    GenericValue securityGroup = delegator.findByPrimaryKey("SecurityGroup", UtilMisc.toMap("groupId", groupId));
    if (securityGroup == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("securityGroup", securityGroup);
    }
%>

<br>
<a href="<ofbiz:url>/EditSecurityGroup</ofbiz:url>" class="buttontext">[New SecurityGroup]</a>
<%if(groupId != null && groupId.length() > 0){%>
  <a href="<ofbiz:url>/EditSecurityGroup?groupId=<%=groupId%></ofbiz:url>" class="buttontextdisabled">[SecurityGroup]</a>
  <a href="<ofbiz:url>/EditSecurityGroupPermissions?groupId=<%=groupId%></ofbiz:url>" class="buttontext">[Permissions]</a>
<%}%>

<div class="head1">Edit SecurityGroup with ID "<%=UtilFormatOut.checkNull(groupId)%>"</div>
<%if (securityGroup == null) {%>
  <%if (groupId != null) {%>
    <form action="<ofbiz:url>/CreateSecurityGroup</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <h3>Could not find securityGroup with ID "<%=groupId%>".</h3>
  <%} else {%>
    <form action="<ofbiz:url>/CreateSecurityGroup</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
  <%}%>
<%} else {%>
  <form action="<ofbiz:url>/UpdateSecurityGroup</ofbiz:url>" method=POST style='margin: 0;'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="groupId" value="<%=groupId%>">
  <tr>
    <td align=right><div class="tabletext">SecurityGroup ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=groupId%></b> (This cannot be changed without re-creating the securityGroup.)
    </td>
  </tr>
<%}%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Description</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="securityGroup" field="description" fullattrs="true"/> size="60" maxlength="250"></td>
      </tr>

  <tr>
    <td colspan='1' align=right><input type="submit" name="Update" value="Update"></td>
    <td colspan='2'>&nbsp;</td>
  </tr>
</table>
</form>

<%} else {%>
  <h3>You do not have permission to view this page. ("SECURITY_VIEW" or "SECURITY_ADMIN" needed)</h3>
<%}%>
</td><td>&nbsp;&nbsp;</td></tr></table>
<%} catch (Exception e) { Debug.logError(e); throw e; } %>
