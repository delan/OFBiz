
<%
/**
 *  Title: Security Component - Security Permission Entity
 *  Description: None
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
 *@created    Wed May 23 02:37:06 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditSecurityPermission"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("SECURITY_PERMISSION", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("SECURITY_PERMISSION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SECURITY_PERMISSION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SECURITY_PERMISSION", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

    String permissionId = request.getParameter("SECURITY_PERMISSION_PERMISSION_ID");

  

  SecurityPermission securityPermission = SecurityPermissionHelper.findByPrimaryKey(permissionId);
%>

<a href="FindSecurityPermission.jsp" class="buttontext">[Find SecurityPermission]</a>
<%if(hasCreatePermission){%>
  <a href="EditSecurityPermission.jsp" class="buttontext">[Create SecurityPermission]</a>
<%}%>
<%if(hasDeletePermission){%>
  <%if(securityPermission != null){%>
    <a href="EditSecurityPermission.jsp?WEBEVENT=UPDATE_SECURITY_PERMISSION&UPDATE_MODE=DELETE&SECURITY_PERMISSION_PERMISSION_ID=<%=permissionId%>" class="buttontext">[Delete this SecurityPermission]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(permissionId != null){%>
    <a href="EditsecurityPermission.jsp?SECURITY_PERMISSION_PERMISSION_ID=<%=permissionId%>" class="buttontext">[Edit SecurityPermission]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(securityPermission == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified SecurityPermission was not found.</h3></td></tr>
<%}else{%>
  <input type="hidden" name="WEBEVENT" value="UPDATE_SECURITY_PERMISSION">
  <input type="hidden" name="UPDATE_MODE" value="UPDATE">

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>PERMISSION_ID</td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityPermission.getPermissionId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td>DESCRIPTION</td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityPermission.getDescription())%>
    
    </td>
  </tr>

<%} //end if securityPermission == null %>
</table>

<a href="FindSecurityPermission.jsp" class="buttontext">[Find SecurityPermission]</a>
<%if(hasCreatePermission){%>
  <a href="EditSecurityPermission.jsp" class="buttontext">[Create SecurityPermission]</a>
<%}%>
<%if(hasDeletePermission){%>
  <%if(securityPermission != null){%>
    <a href="EditSecurityPermission.jsp?WEBEVENT=UPDATE_SECURITY_PERMISSION&UPDATE_MODE=DELETE&SECURITY_PERMISSION_PERMISSION_ID=<%=permissionId%>" class="buttontext">[Delete this SecurityPermission]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(permissionId != null){%>
    <a href="EditsecurityPermission.jsp?SECURITY_PERMISSION_PERMISSION_ID=<%=permissionId%>" class="buttontext">[Edit SecurityPermission]</a>
  <%}%>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_PERMISSION_ADMIN, or SECURITY_PERMISSION_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
