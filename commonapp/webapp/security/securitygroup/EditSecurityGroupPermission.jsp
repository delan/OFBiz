
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
 *@created    Fri Jun 29 12:51:09 MDT 2001
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

<%boolean hasCreatePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String groupId = request.getParameter("SECURITY_GROUP_PERMISSION_GROUP_ID");  
  String permissionId = request.getParameter("SECURITY_GROUP_PERMISSION_PERMISSION_ID");  


  SecurityGroupPermission securityGroupPermission = SecurityGroupPermissionHelper.findByPrimaryKey(groupId, permissionId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    securityGroupPermission = null;
  }
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
<%if(groupId != null && permissionId != null){%>
  <a href="<%=response.encodeURL("ViewSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[View SecurityGroupPermission Details]</a>
<%}%>
<br>

<%if(securityGroupPermission == null && (groupId != null || permissionId != null)){%>
    SecurityGroupPermission with (GROUP_ID, PERMISSION_ID: <%=groupId%>, <%=permissionId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditSecurityGroupPermission.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_SECURITY_GROUP_PERMISSION">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(securityGroupPermission == null){%>
  <%if(hasCreatePermission){%>
    You may create a SecurityGroupPermission by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>GROUP_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="SECURITY_GROUP_PERMISSION_GROUP_ID" value="<%=UtilFormatOut.checkNull(groupId)%>">
      

      </td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PERMISSION_ID</td>
      <td>
      
        <input type="text" size="60" maxlength="60" name="SECURITY_GROUP_PERMISSION_PERMISSION_ID" value="<%=UtilFormatOut.checkNull(permissionId)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a SecurityGroupPermission (SECURITY_GROUP_PERMISSION_ADMIN, or SECURITY_GROUP_PERMISSION_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
    <input type="hidden" name="SECURITY_GROUP_PERMISSION_GROUP_ID" value="<%=groupId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>GROUP_ID</td>
      <td>
        <b><%=groupId%></b> (This cannot be changed without re-creating the securityGroupPermission.)
      </td>
    </tr>
    <input type="hidden" name="SECURITY_GROUP_PERMISSION_PERMISSION_ID" value="<%=permissionId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>PERMISSION_ID</td>
      <td>
        <b><%=permissionId%></b> (This cannot be changed without re-creating the securityGroupPermission.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a SecurityGroupPermission (SECURITY_GROUP_PERMISSION_ADMIN, or SECURITY_GROUP_PERMISSION_UPDATE needed).
  <%}%>
<%} //end if securityGroupPermission == null %>

<%if(showFields){%>

  

  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindSecurityGroupPermission.jsp")%>" class="buttontext">[Find SecurityGroupPermission]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp")%>" class="buttontext">[Create SecurityGroupPermission]</a>
<%}%>
<%if(securityGroupPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityGroupPermission.jsp?WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Delete this SecurityGroupPermission]</a>
  <%}%>
<%}%>
<%if(groupId != null && permissionId != null){%>
  <a href="<%=response.encodeURL("ViewSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[View SecurityGroupPermission Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_GROUP_PERMISSION_ADMIN, SECURITY_GROUP_PERMISSION_CREATE, or SECURITY_GROUP_PERMISSION_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

