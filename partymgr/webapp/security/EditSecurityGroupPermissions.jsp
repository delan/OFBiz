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
 *@created    May 10, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("SECURITY", "_VIEW", session)) {%>
<%
    String groupId = request.getParameter("groupId");
    Collection securityGroupPermissions = delegator.findByAnd("SecurityGroupPermission", 
            UtilMisc.toMap("groupId", groupId), 
            UtilMisc.toList("permissionId"));
    if (securityGroupPermissions != null) pageContext.setAttribute("securityGroupPermissions", securityGroupPermissions);

    Collection securityPermissions = delegator.findAll("SecurityPermission", UtilMisc.toList("permissionId"));
    if (securityPermissions != null) pageContext.setAttribute("securityPermissions", securityPermissions);

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
    if (securityGroupPermissions != null) {
        listSize = securityGroupPermissions.size();
    }
    lowIndex = viewIndex * viewSize;
    highIndex = (viewIndex + 1) * viewSize;
    if (listSize < highIndex) {
        highIndex = listSize;
    }
%>

<a href="<ofbiz:url>/FindSecurityGroup</ofbiz:url>" class="buttontext">[All SecurityGroups]</a>
<a href="<ofbiz:url>/EditSecurityGroup</ofbiz:url>" class="buttontext">[New SecurityGroup]</a>
<%if(groupId != null && groupId.length() > 0){%>
  <a href="<ofbiz:url>/EditSecurityGroup?groupId=<%=groupId%></ofbiz:url>" class="buttontext">[SecurityGroup]</a>
  <a href="<ofbiz:url>/EditSecurityGroupPermissions?groupId=<%=groupId%></ofbiz:url>" class="buttontextdisabled">[Permissions]</a>
  <a href="<ofbiz:url>/EditSecurityGroupUserLogins?groupId=<%=groupId%></ofbiz:url>" class="buttontext">[UserLogins]</a>
<%}%>

<div class="head1">Permissions for SecurityGroup with ID "<%=UtilFormatOut.checkNull(groupId)%>"</div>

<ofbiz:if name="securityGroupPermissions" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditSecurityGroupPermissions?groupId=" + groupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditSecurityGroupPermissions?groupId=" + groupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<%if (groupId != null){%>
<table border="1" cellpadding='2' cellspacing='0' width='100%'>
  <tr>
    <td><div class="tabletext"><b>Permission&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td width='1%'><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="securityGroupPermission" property="securityGroupPermissions" offset="<%=lowIndex%>" limit="<%=viewSize%>">
  <%GenericValue securityPermission = securityGroupPermission.getRelatedOneCache("SecurityPermission");%>
  <%if (securityPermission != null) pageContext.setAttribute("securityPermission", securityPermission);%>
  <tr valign="middle">
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="securityGroupPermission" field="permissionId"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="securityPermission" field="description"/></div></td>
    <td>
      <a href='<ofbiz:url>/removeSecurityPermissionFromSecurityGroup?permissionId=<ofbiz:inputvalue entityAttr="securityGroupPermission" field="permissionId"/>&groupId=<ofbiz:inputvalue entityAttr="securityGroupPermission" field="groupId"/></ofbiz:url>' class="buttontext">
      [Remove]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<ofbiz:if name="securityGroupPermissions" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditSecurityGroupPermissions?groupId=" + groupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditSecurityGroupPermissions?groupId=" + groupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<br>
<form method="POST" action="<ofbiz:url>/addSecurityPermissionToSecurityGroup</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="groupId" value="<%=groupId%>">
  <input type="hidden" name="useValues" value="true">

  <div class='head2'>Add Permission (from list) to SecurityGroup:</div>
  <div class='tabletext'>
    Permission:
      <select name="permissionId" class='selectBox'>
      <ofbiz:iterator name="securityPermission" property="securityPermissions">
        <option value='<ofbiz:entityfield attribute="securityPermission" field="permissionId"/>'><ofbiz:entityfield attribute="securityPermission" field="description"/> [<ofbiz:entityfield attribute="securityPermission" field="permissionId"/>]</option>
      </ofbiz:iterator>
      </select>
    <input type="submit" value="Add">
  </div>
</form>
<form method="POST" action="<ofbiz:url>/addSecurityPermissionToSecurityGroup</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="groupId" value="<%=groupId%>">
  <input type="hidden" name="useValues" value="true">

  <div class='head2'>Add Permission (manually) to SecurityGroup:</div>
  <div class='tabletext'>
    Permission: <input type=text size='60' name='permissionId'>
    <input type="submit" value="Add">
  </div>
</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("SECURITY_VIEW" or "SECURITY_ADMIN" needed)</h3>
<%}%>
