
<%
/**
 *  Title: Security Component - Security Group Entity
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
 *@created    Fri Jun 29 12:51:08 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditSecurityGroup"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("SECURITY_GROUP", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SECURITY_GROUP", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SECURITY_GROUP", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String groupId = request.getParameter("SECURITY_GROUP_GROUP_ID");  

  

  SecurityGroup securityGroup = SecurityGroupHelper.findByPrimaryKey(groupId);
%>

<b><u>View Entity: SecurityGroup with (GROUP_ID: <%=groupId%>).</u></b>
<br>
<a href="<%=response.encodeURL("FindSecurityGroup.jsp")%>" class="buttontext">[Find SecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityGroup.jsp")%>" class="buttontext">[Create SecurityGroup]</a>
<%}%>
<%if(securityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityGroup.jsp?WEBEVENT=UPDATE_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this SecurityGroup]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(groupId != null){%>
    <a href="<%=response.encodeURL("EditSecurityGroup.jsp?" + "SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Edit SecurityGroup]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(securityGroup == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified SecurityGroup was not found.</h3></td></tr>
<%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>GROUP_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroup.getGroupId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>DESCRIPTION</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(securityGroup.getDescription())%>
    
    </td>
  </tr>

<%} //end if securityGroup == null %>
</table>

<a href="<%=response.encodeURL("FindSecurityGroup.jsp")%>" class="buttontext">[Find SecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditSecurityGroup.jsp")%>" class="buttontext">[Create SecurityGroup]</a>
<%}%>
<%if(securityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditSecurityGroup.jsp?WEBEVENT=UPDATE_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this SecurityGroup]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(groupId != null){%>
    <a href="<%=response.encodeURL("EditSecurityGroup.jsp?" + "SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Edit SecurityGroup]</a>
  <%}%>
<%}%>

  
  
  
<%-- Start Relation for UserLoginSecurityGroup, type: many --%>
<%if(securityGroup != null){%>
  <%if(Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_VIEW", session)){%>    
    <%Iterator relatedIterator = UserLoginSecurityGroupHelper.findByGroupIdIterator(securityGroup.getGroupId());%>
    <hr>
    <b>Related Entities: UserLoginSecurityGroup with (GROUP_ID: <%=securityGroup.getGroupId()%>)</b>
    <br>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_DELETE", session);%>
    <%
      String rowColorResultHeader = "99CCFF";
      String rowColorResult1 = "99FFCC";
      String rowColorResult2 = "CCFFCC"; 
      String rowColorResult = "";
    %>
    <%if(relatedCreatePerm){%>
      
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + securityGroup.getGroupId())%>" class="buttontext">[Create UserLoginSecurityGroup]</a>
    <%}%>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr bgcolor="<%=rowColorResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>USER_LOGIN_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GROUP_ID</nobr></b></div></td>
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
        UserLoginSecurityGroup userLoginSecurityGroup = (UserLoginSecurityGroup)relatedIterator.next();
        if(userLoginSecurityGroup != null)
        {
    %>
    <%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(userLoginSecurityGroup.getUserLoginId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(userLoginSecurityGroup.getGroupId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("/commonapp/security/securitygroup/ViewUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginSecurityGroup.getUserLoginId() + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginSecurityGroup.getUserLoginId() + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USERNAME=" + username + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewUserLoginSecurityGroup.jsp?" + "SECURITY_GROUP_GROUP_ID=" + groupId + "&WEBEVENT=UPDATE_USER_LOGIN_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginSecurityGroup.getUserLoginId() + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
<td colspan="8">
<h3>No UserLoginSecurityGroups Found.</h3>
</td>
</tr>
<%}%>
</table>
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + securityGroup.getGroupId())%>" class="buttontext">[Create UserLoginSecurityGroup]</a>
    <%}%>
  <%}%>
<%}%>
<%-- End Relation for UserLoginSecurityGroup, type: many --%>
  

  
  
  
<%-- Start Relation for SecurityGroupPermission, type: many --%>
<%if(securityGroup != null){%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session)){%>    
    <%Iterator relatedIterator = SecurityGroupPermissionHelper.findByGroupIdIterator(securityGroup.getGroupId());%>
    <hr>
    <b>Related Entities: SecurityGroupPermission with (GROUP_ID: <%=securityGroup.getGroupId()%>)</b>
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
      
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroup.getGroupId())%>" class="buttontext">[Create SecurityGroupPermission]</a>
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
          <a href="<%=response.encodeURL("ViewSecurityGroupPermission.jsp?" + "SECURITY_GROUP_GROUP_ID=" + groupId + "&WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a>
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
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroup.getGroupId())%>" class="buttontext">[Create SecurityGroupPermission]</a>
    <%}%>
  <%}%>
<%}%>
<%-- End Relation for SecurityGroupPermission, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_GROUP_ADMIN, or SECURITY_GROUP_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
