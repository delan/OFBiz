
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
 *@created    Wed Jul 04 01:03:22 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditUserLoginSecurityGroup"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String userLoginId = request.getParameter("USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID");  
  String groupId = request.getParameter("USER_LOGIN_SECURITY_GROUP_GROUP_ID");  


  UserLoginSecurityGroup userLoginSecurityGroup = UserLoginSecurityGroupHelper.findByPrimaryKey(userLoginId, groupId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    userLoginSecurityGroup = null;
  }
%>

<a href="<%=response.encodeURL("FindUserLoginSecurityGroup.jsp")%>" class="buttontext">[Find UserLoginSecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp")%>" class="buttontext">[Create UserLoginSecurityGroup]</a>
<%}%>
<%if(userLoginSecurityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp?WEBEVENT=UPDATE_USER_LOGIN_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this UserLoginSecurityGroup]</a>
  <%}%>
<%}%>
<%if(userLoginId != null && groupId != null){%>
  <a href="<%=response.encodeURL("ViewUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[View UserLoginSecurityGroup Details]</a>
<%}%>
<br>

<%if(userLoginSecurityGroup == null && (userLoginId != null || groupId != null)){%>
    UserLoginSecurityGroup with (USER_LOGIN_ID, GROUP_ID: <%=userLoginId%>, <%=groupId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_USER_LOGIN_SECURITY_GROUP">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(userLoginSecurityGroup == null){%>
  <%if(hasCreatePermission){%>
    You may create a UserLoginSecurityGroup by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USER_LOGIN_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID" value="<%=UtilFormatOut.checkNull(userLoginId)%>">
      

      </td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>GROUP_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="USER_LOGIN_SECURITY_GROUP_GROUP_ID" value="<%=UtilFormatOut.checkNull(groupId)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a UserLoginSecurityGroup (USER_LOGIN_SECURITY_GROUP_ADMIN, or USER_LOGIN_SECURITY_GROUP_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID" value="<%=userLoginId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>USER_LOGIN_ID</td>
      <td>
        <b><%=userLoginId%></b> (This cannot be changed without re-creating the userLoginSecurityGroup.)
      </td>
    </tr>
      <input type="hidden" name="USER_LOGIN_SECURITY_GROUP_GROUP_ID" value="<%=groupId%>">
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <td>GROUP_ID</td>
      <td>
        <b><%=groupId%></b> (This cannot be changed without re-creating the userLoginSecurityGroup.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a UserLoginSecurityGroup (USER_LOGIN_SECURITY_GROUP_ADMIN, or USER_LOGIN_SECURITY_GROUP_UPDATE needed).
  <%}%>
<%} //end if userLoginSecurityGroup == null %>

<%if(showFields){%>

  

  

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindUserLoginSecurityGroup.jsp")%>" class="buttontext">[Find UserLoginSecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp")%>" class="buttontext">[Create UserLoginSecurityGroup]</a>
<%}%>
<%if(userLoginSecurityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditUserLoginSecurityGroup.jsp?WEBEVENT=UPDATE_USER_LOGIN_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this UserLoginSecurityGroup]</a>
  <%}%>
<%}%>
<%if(userLoginId != null && groupId != null){%>
  <a href="<%=response.encodeURL("ViewUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[View UserLoginSecurityGroup Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (USER_LOGIN_SECURITY_GROUP_ADMIN, USER_LOGIN_SECURITY_GROUP_CREATE, or USER_LOGIN_SECURITY_GROUP_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

