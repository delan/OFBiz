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
    Collection userLoginSecurityGroups = delegator.findByAnd("UserLoginSecurityGroup", 
            UtilMisc.toMap("groupId", groupId), 
            UtilMisc.toList("userLoginId"));
    if (userLoginSecurityGroups != null) pageContext.setAttribute("userLoginSecurityGroups", userLoginSecurityGroups);

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
    if (userLoginSecurityGroups != null) {
        listSize = userLoginSecurityGroups.size();
    }
    lowIndex = viewIndex * viewSize;
    highIndex = (viewIndex + 1) * viewSize;
    if (listSize < highIndex) {
        highIndex = listSize;
    }
%>
<br>

<a href="<ofbiz:url>/FindSecurityGroup</ofbiz:url>" class="buttontext">[All SecurityGroups]</a>
<a href="<ofbiz:url>/EditSecurityGroup</ofbiz:url>" class="buttontext">[New SecurityGroup]</a>
<%if(groupId != null && groupId.length() > 0){%>
  <a href="<ofbiz:url>/EditSecurityGroup?groupId=<%=groupId%></ofbiz:url>" class="buttontext">[SecurityGroup]</a>
  <a href="<ofbiz:url>/EditSecurityGroupPermissions?groupId=<%=groupId%></ofbiz:url>" class="buttontext">[Permissions]</a>
  <a href="<ofbiz:url>/EditSecurityGroupUserLogins?groupId=<%=groupId%></ofbiz:url>" class="buttontextdisabled">[UserLogins]</a>
<%}%>

<div class="head1">Inventory Items for SecurityGroup with ID "<%=UtilFormatOut.checkNull(groupId)%>"</div>
<a href='<ofbiz:url>/EditPermission?groupId=<%=groupId%></ofbiz:url>' class="buttontext">
[Create New Inventory Item for this SecurityGroup]</a>

<ofbiz:if name="userLoginSecurityGroups" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditSecurityGroupUserLogins?groupId=" + groupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditSecurityGroupUserLogins?groupId=" + groupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<%if (groupId != null){%>
<table border="1" cellpadding='2' cellspacing='0' width='100%'>
  <tr>
    <td><div class="tabletext"><b>UserLogin&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
    <td><div class="tabletext"><b>Thru&nbsp;Date</b></div></td>
    <td width='1%'><div class="tabletext">&nbsp;</div></td>
    <td width='1%'><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="userLoginSecurityGroup" property="userLoginSecurityGroups" offset="<%=lowIndex%>" limit="<%=viewSize%>">
  <tr valign="middle">
    <td><a href='<ofbiz:url>/editlogin?userLoginId=<ofbiz:entityfield attribute="userLoginSecurityGroup" field="userLoginId"/></ofbiz:url>' class='buttontext'>
        <ofbiz:entityfield attribute="userLoginSecurityGroup" field="userLoginId"/></a></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="fromDate"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="userLoginSecurityGroup" field="thruDate"/></div></td>
    <td>
      <a href='<ofbiz:url>/editlogin?userLoginId=<ofbiz:entityfield attribute="userLoginSecurityGroup" field="userLoginId"/></ofbiz:url>' class="buttontext">
      [Edit]</a>
    </td>
    <td>
      <a href='<ofbiz:url>/removeUserLoginFromSecurityGroup?inventoryItemId=<ofbiz:inputvalue entityAttr="inventoryItem" field="inventoryItemId"/></ofbiz:url>' class="buttontext">
      [Remove]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<ofbiz:if name="userLoginSecurityGroups" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditSecurityGroupUserLogins?groupId=" + groupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditSecurityGroupUserLogins?groupId=" + groupId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<br>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("SECURITY_VIEW" or "SECURITY_ADMIN" needed)</h3>
<%}%>
