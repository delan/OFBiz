
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
 *@created    Fri Jun 29 12:51:09 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.securitygroup.*" %>


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

<b><u>View Entity: SecurityPermission with (PERMISSION_ID: <%=permissionId%>).</u></b>
<br>
<a href="<%=response.encodeURL("FindSecurityPermission.jsp")%>" class="buttontext">[Find SecurityPermission]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityPermission.jsp")%>" class="buttontext">[Create SecurityPermission]</a>
<%}%>
<%if(securityPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityPermission.jsp?WEBEVENT=UPDATE_SECURITY_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Delete this SecurityPermission]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(permissionId != null){%>
    <a href="<%=response.encodeURL("EditSecurityPermission.jsp?" + "SECURITY_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Edit SecurityPermission]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(securityPermission == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified SecurityPermission was not found.</h3></td></tr>
<%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PERMISSION_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityPermission.getPermissionId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>DESCRIPTION</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityPermission.getDescription())%>
    
    </td>
  </tr>

<%} //end if securityPermission == null %>
</table>

<a href="<%=response.encodeURL("FindSecurityPermission.jsp")%>" class="buttontext">[Find SecurityPermission]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityPermission.jsp")%>" class="buttontext">[Create SecurityPermission]</a>
<%}%>
<%if(securityPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityPermission.jsp?WEBEVENT=UPDATE_SECURITY_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Delete this SecurityPermission]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(permissionId != null){%>
    <a href="<%=response.encodeURL("EditSecurityPermission.jsp?" + "SECURITY_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Edit SecurityPermission]</a>
  <%}%>
<%}%>

  
  
  
<%-- Start Relation for SecurityGroupPermission, type: many --%>
<%if(securityPermission != null){%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session)){%>    
    <%Iterator relatedIterator = SecurityGroupPermissionHelper.findByPermissionIdIterator(securityPermission.getPermissionId());%>
    <hr>
    <b>Related Entities: SecurityGroupPermission with (PERMISSION_ID: <%=securityPermission.getPermissionId()%>)</b>
    <br>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_DELETE", session);%>
    <%
      String rowColorResultHeader = "99CCFF";
      String rowColorResult1 = "99FFCC";
      String rowColorResult2 = "CCFFCC"; 
      String rowColorResult = "";
    %>
    <%if(relatedCreatePerm){%>
      
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityPermission.getPermissionId())%>" class="buttontext">[Create SecurityGroupPermission]</a>
    <%}%>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr bgcolor="<%=rowColorResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>GROUP_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PERMISSION_ID</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(relatedUpdatePerm){%>
        <td>&nbsp;</td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
    <%
     if(relatedIterator != null && relatedIterator.hasNext())
     {
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        SecurityGroupPermission securityGroupPermission = (SecurityGroupPermission)relatedIterator.next();
        if(securityGroupPermission != null)
        {
    %>
    <%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(securityGroupPermission.getGroupId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(securityGroupPermission.getPermissionId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("/commonapp/security/securitygroup/ViewSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USERNAME=" + username + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewSecurityGroupPermission.jsp?" + "SECURITY_PERMISSION_PERMISSION_ID=" + permissionId + "&WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
<td colspan="8">
<h3>No SecurityGroupPermissions Found.</h3>
</td>
</tr>
<%}%>
</table>
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityPermission.getPermissionId())%>" class="buttontext">[Create SecurityGroupPermission]</a>
    <%}%>
  <%}%>
<%}%>
<%-- End Relation for SecurityGroupPermission, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_PERMISSION_ADMIN, or SECURITY_PERMISSION_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
