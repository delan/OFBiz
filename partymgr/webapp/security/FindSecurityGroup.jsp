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
    //securityGroups
    Collection securityGroups = delegator.findAll("SecurityGroup");
    if (securityGroups != null) pageContext.setAttribute("securityGroups", securityGroups);
%>
<br>

<div class="head1">SecurityGroups List</div>

<br>
<div><a href='<ofbiz:url>/EditSecurityGroup</ofbiz:url>' class="buttontext">[Create New SecurityGroup]</a></div>
<br>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>SecurityGroup&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="securityGroup" property="securityGroups">
  <tr valign="middle">
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="securityGroup" field="groupId"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="securityGroup" field="description"/></div></td>
    <td>
      <a href='<ofbiz:url>/EditSecurityGroup?groupId=<ofbiz:inputvalue entityAttr="securityGroup" field="groupId"/></ofbiz:url>' class="buttontext">
      [Edit]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("SECURITY_VIEW" or "SECURITY_ADMIN" needed)</h3>
<%}%>
