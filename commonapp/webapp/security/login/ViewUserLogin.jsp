
<%
/**
 *  Title: User Login Entity
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
 *@created    Fri Jun 29 12:51:06 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.login.*" %>

<%@ page import="org.ofbiz.commonapp.security.securitygroup.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditUserLogin"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("USER_LOGIN", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("USER_LOGIN", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("USER_LOGIN", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("USER_LOGIN", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String userLoginId = request.getParameter("USER_LOGIN_USER_LOGIN_ID");  

  

  UserLogin userLogin = UserLoginHelper.findByPrimaryKey(userLoginId);
%>

<b><u>View Entity: UserLogin with (USER_LOGIN_ID: <%=userLoginId%>).</u></b>
<br>
<a href="<%=response.encodeURL("FindUserLogin.jsp")%>" class="buttontext">[Find UserLogin]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditUserLogin.jsp")%>" class="buttontext">[Create UserLogin]</a>
<%}%>
<%if(userLogin != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditUserLogin.jsp?WEBEVENT=UPDATE_USER_LOGIN&UPDATE_MODE=DELETE&" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId)%>" class="buttontext">[Delete this UserLogin]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(userLoginId != null){%>
    <a href="<%=response.encodeURL("EditUserLogin.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId)%>" class="buttontext">[Edit UserLogin]</a>
  <%}%>
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

<a href="<%=response.encodeURL("FindUserLogin.jsp")%>" class="buttontext">[Find UserLogin]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditUserLogin.jsp")%>" class="buttontext">[Create UserLogin]</a>
<%}%>
<%if(userLogin != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditUserLogin.jsp?WEBEVENT=UPDATE_USER_LOGIN&UPDATE_MODE=DELETE&" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId)%>" class="buttontext">[Delete this UserLogin]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(userLoginId != null){%>
    <a href="<%=response.encodeURL("EditUserLogin.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId)%>" class="buttontext">[Edit UserLogin]</a>
  <%}%>
<%}%>

  
  
  
<%-- Start Relation for UserLoginSecurityGroup, type: many --%>
<%if(userLogin != null){%>
  <%if(Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_VIEW", session)){%>    
    <%Iterator relatedIterator = UserLoginSecurityGroupHelper.findByUserLoginIdIterator(userLogin.getUserLoginId());%>
    <hr>
    <b>Related Entities: UserLoginSecurityGroup with (USER_LOGIN_ID: <%=userLogin.getUserLoginId()%>)</b>
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
      
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLogin.getUserLoginId())%>" class="buttontext">[Create UserLoginSecurityGroup]</a>
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
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewUserLoginSecurityGroup.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId + "&WEBEVENT=UPDATE_USER_LOGIN_SECURITY_GROUP&UPDATE_MODE=DELETE&" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginSecurityGroup.getUserLoginId() + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[Delete]</a>
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
      <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLogin.getUserLoginId())%>" class="buttontext">[Create UserLoginSecurityGroup]</a>
    <%}%>
  <%}%>
<%}%>
<%-- End Relation for UserLoginSecurityGroup, type: many --%>
  

  
  
  
<%-- Start Relation for LoginAccountHistory, type: many --%>
<%if(userLogin != null){%>
  <%if(Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_VIEW", session)){%>    
    <%Iterator relatedIterator = LoginAccountHistoryHelper.findByUserLoginIdIterator(userLogin.getUserLoginId());%>
    <hr>
    <b>Related Entities: LoginAccountHistory with (USER_LOGIN_ID: <%=userLogin.getUserLoginId()%>)</b>
    <br>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_DELETE", session);%>
    <%
      String rowColorResultHeader = "99CCFF";
      String rowColorResult1 = "99FFCC";
      String rowColorResult2 = "CCFFCC"; 
      String rowColorResult = "";
    %>
    <%if(relatedCreatePerm){%>
      
      <a href="<%=response.encodeURL("/commonapp/security/login/EditLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLogin.getUserLoginId())%>" class="buttontext">[Create LoginAccountHistory]</a>
    <%}%>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr bgcolor="<%=rowColorResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>USER_LOGIN_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>USER_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PASSWORD</nobr></b></div></td>
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
        LoginAccountHistory loginAccountHistory = (LoginAccountHistory)relatedIterator.next();
        if(loginAccountHistory != null)
        {
    %>
    <%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getPartyId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getUserLoginId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%{
        String dateString = null;
        String timeString = null;
        if(loginAccountHistory != null)
        {
          java.util.Date date = loginAccountHistory.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%{
        String dateString = null;
        String timeString = null;
        if(loginAccountHistory != null)
        {
          java.util.Date date = loginAccountHistory.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getUserId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getPassword())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("/commonapp/security/login/ViewLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_PARTY_ID=" + loginAccountHistory.getPartyId() + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + loginAccountHistory.getUserLoginId() + "&" + "LOGIN_ACCOUNT_HISTORY_FROM_DATE=" + loginAccountHistory.getFromDate())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/security/login/EditLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_PARTY_ID=" + loginAccountHistory.getPartyId() + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + loginAccountHistory.getUserLoginId() + "&" + "LOGIN_ACCOUNT_HISTORY_FROM_DATE=" + loginAccountHistory.getFromDate())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewLoginAccountHistory.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId + "&WEBEVENT=UPDATE_LOGIN_ACCOUNT_HISTORY&UPDATE_MODE=DELETE&" + "LOGIN_ACCOUNT_HISTORY_PARTY_ID=" + loginAccountHistory.getPartyId() + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + loginAccountHistory.getUserLoginId() + "&" + "LOGIN_ACCOUNT_HISTORY_FROM_DATE=" + loginAccountHistory.getFromDate())%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
<td colspan="8">
<h3>No LoginAccountHistorys Found.</h3>
</td>
</tr>
<%}%>
</table>
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL("/commonapp/security/login/EditLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLogin.getUserLoginId())%>" class="buttontext">[Create LoginAccountHistory]</a>
    <%}%>
  <%}%>
<%}%>
<%-- End Relation for LoginAccountHistory, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (USER_LOGIN_ADMIN, or USER_LOGIN_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
