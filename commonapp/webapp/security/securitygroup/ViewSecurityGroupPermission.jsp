
<%
/**
 *  Title: Security Component - Security Group Permission Entity
 *  Description: Defines a permission available to a security group
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
 *@created    Fri May 25 14:29:14 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.commonapp.security.securitygroup.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditSecurityGroupPermission"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String groupId = request.getParameter("SECURITY_GROUP_PERMISSION_GROUP_ID");
    String permissionId = request.getParameter("SECURITY_GROUP_PERMISSION_PERMISSION_ID");

  
  

  SecurityGroupPermission securityGroupPermission = SecurityGroupPermissionHelper.findByPrimaryKey(groupId, permissionId);
%>

<a href="<%=response.encodeURL("FindSecurityGroupPermission.jsp")%>" class="buttontext">[Find SecurityGroupPermission]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp")%>" class="buttontext">[Create SecurityGroupPermission]</a>
<%}%>
<%if(securityGroupPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp?WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Delete this SecurityGroupPermission]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(groupId != null && permissionId != null){%>
    <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Edit SecurityGroupPermission]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(securityGroupPermission == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified SecurityGroupPermission was not found.</h3></td></tr>
<%}else{%>
  <input type="hidden" name="WEBEVENT" value="UPDATE_SECURITY_GROUP_PERMISSION">
  <input type="hidden" name="UPDATE_MODE" value="UPDATE">

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>GROUP_ID</td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroupPermission.getGroupId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>PERMISSION_ID</td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroupPermission.getPermissionId())%>
    
    </td>
  </tr>

<%} //end if securityGroupPermission == null %>
</table>

<a href="<%=response.encodeURL("FindSecurityGroupPermission.jsp")%>" class="buttontext">[Find SecurityGroupPermission]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp")%>" class="buttontext">[Create SecurityGroupPermission]</a>
<%}%>
<%if(securityGroupPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp?WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Delete this SecurityGroupPermission]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(groupId != null && permissionId != null){%>
    <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Edit SecurityGroupPermission]</a>
  <%}%>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_GROUP_PERMISSION_ADMIN, or SECURITY_GROUP_PERMISSION_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
