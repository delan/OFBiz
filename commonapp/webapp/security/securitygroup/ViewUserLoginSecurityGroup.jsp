
<%
/**
 *  Title: Security Component - User Login Security Group Entity
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
 *@created    Fri Jun 29 12:51:10 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.securitygroup.*" %>

<%@ page import="org.ofbiz.commonapp.security.login.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditUserLoginSecurityGroup"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String userLoginId = request.getParameter("USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID");  
  String groupId = request.getParameter("USER_LOGIN_SECURITY_GROUP_GROUP_ID");  

  
  

  UserLoginSecurityGroup userLoginSecurityGroup = UserLoginSecurityGroupHelper.findByPrimaryKey(userLoginId, groupId);
%>

<b><u>View Entity: UserLoginSecurityGroup with (USER_LOGIN_ID, GROUP_ID: <%=userLoginId%>, <%=groupId%>).</u></b>
<br>
<a href="<%=response.encodeURL("FindUserLoginSecurityGroup.jsp")%>" class="buttontext">[Find UserLoginSecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp")%>" class="buttontext">[Create UserLoginSecurityGroup]</a>
<%}%>
<%if(userLoginSecurityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp?WEBEVENT=UPDATE_USER_LOGIN_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this UserLoginSecurityGroup]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(userLoginId != null && groupId != null){%>
    <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Edit UserLoginSecurityGroup]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(userLoginSecurityGroup == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified UserLoginSecurityGroup was not found.</h3></td></tr>
<%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>USER_LOGIN_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLoginSecurityGroup.getUserLoginId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>GROUP_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLoginSecurityGroup.getGroupId())%>
    
    </td>
  </tr>

<%} //end if userLoginSecurityGroup == null %>
</table>

<a href="<%=response.encodeURL("FindUserLoginSecurityGroup.jsp")%>" class="buttontext">[Find UserLoginSecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp")%>" class="buttontext">[Create UserLoginSecurityGroup]</a>
<%}%>
<%if(userLoginSecurityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp?WEBEVENT=UPDATE_USER_LOGIN_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this UserLoginSecurityGroup]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(userLoginId != null && groupId != null){%>
    <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Edit UserLoginSecurityGroup]</a>
  <%}%>
<%}%>

  
  
  
<%-- Start Relation for UserLogin, type: one --%>
<%if(userLoginSecurityGroup != null){%>
  <%if(Security.hasEntityPermission("USER_LOGIN", "_VIEW", session)){%>
    <%UserLogin userLogin = UserLoginHelper.findByPrimaryKey(userLoginSecurityGroup.getUserLoginId());%>
    <hr>
    <b>Related Entity: UserLogin with (USER_LOGIN_ID: <%=userLoginSecurityGroup.getUserLoginId()%>)</b>
    <br>
    <%if(userLoginSecurityGroup.getUserLoginId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/security/login/ViewUserLogin.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginSecurityGroup.getUserLoginId())%>" class="buttontext">[View UserLogin Details]</a>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(userLogin == null){%>
    <tr bgcolor="<%=rowColor1%>"><td><h3>Specified UserLogin was not found.</h3></td></tr>
    <%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>USER_LOGIN_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLogin.getUserLoginId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLogin.getPartyId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>CONTACT_MECHANISM_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLogin.getContactMechanismId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>CURRENT_USER_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLogin.getCurrentUserId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>CURRENT_PASSWORD</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLogin.getCurrentPassword())%>
    
    </td>
  </tr>

    <%} //end if userLogin == null %>
    </table>
  <%}%>
<%}%>
<%-- End Relation for UserLogin, type: one --%>
  

  
  
  
<%-- Start Relation for SecurityGroup, type: one --%>
<%if(userLoginSecurityGroup != null){%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session)){%>
    <%SecurityGroup securityGroup = SecurityGroupHelper.findByPrimaryKey(userLoginSecurityGroup.getGroupId());%>
    <hr>
    <b>Related Entity: SecurityGroup with (GROUP_ID: <%=userLoginSecurityGroup.getGroupId()%>)</b>
    <br>
    <%if(userLoginSecurityGroup.getGroupId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/ViewSecurityGroup.jsp?" + "SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[View SecurityGroup Details]</a>
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
  <%}%>
<%}%>
<%-- End Relation for SecurityGroup, type: one --%>
  

  
  
  
<%-- Start Relation for SecurityGroupPermission, type: many --%>
<%if(userLoginSecurityGroup != null){%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session)){%>    
    <%Iterator relatedIterator = SecurityGroupPermissionHelper.findByGroupIdIterator(userLoginSecurityGroup.getGroupId());%>
    <hr>
    <b>Related Entities: SecurityGroupPermission with (GROUP_ID: <%=userLoginSecurityGroup.getGroupId()%>)</b>
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
      
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[Create SecurityGroupPermission]</a>
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
          <a href="<%=response.encodeURL("ViewSecurityGroupPermission.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId + "&WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a>
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
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditSecurityGroupPermission.jsp?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[Create SecurityGroupPermission]</a>
    <%}%>
  <%}%>
<%}%>
<%-- End Relation for SecurityGroupPermission, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (USER_LOGIN_SECURITY_GROUP_ADMIN, or USER_LOGIN_SECURITY_GROUP_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
