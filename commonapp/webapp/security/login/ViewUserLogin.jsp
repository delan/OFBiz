
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
 *@created    Wed Jul 04 01:03:19 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.login.*" %>

<%@ page import="org.ofbiz.commonapp.party.party.*" %>
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

<br>
<div style='color:yellow;width:100%;background-color:#330033;padding:3;'>
  <b>View Entity: UserLogin with (USER_LOGIN_ID: <%=userLoginId%>).</b>
</div>

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
<br>

  
  
  
<%-- Start Relation for Party, type: one --%>
<%if(userLogin != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%Party partyRelated = PartyHelper.findByPrimaryKey(userLogin.getPartyId());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=userLogin.getPartyId()%>)
    </div>
    <%if(userLogin.getPartyId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/party/party/ViewParty.jsp?" + "PARTY_PARTY_ID=" + userLogin.getPartyId())%>" class="buttontext">[View Party Details]</a>
      
    <%if(partyRelated != null){%>
      <%if(Security.hasEntityPermission("PARTY", "_EDIT", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditParty.jsp?" + "PARTY_PARTY_ID=" + userLogin.getPartyId())%>" class="buttontext">[Edit Party]</a>
      <%}%>
    <%}else{%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditParty.jsp?" + "PARTY_PARTY_ID=" + userLogin.getPartyId())%>" class="buttontext">[Create Party]</a>
      <%}%>
    <%}%>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyRelated == null){%>
    <tr bgcolor="<%=rowColor1%>"><td><h3>Specified Party was not found.</h3></td></tr>
    <%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyRelated.getPartyId())%>
    
    </td>
  </tr>

    <%} //end if partyRelated == null %>
    </table>
  <%}%>
<%}%>
<%-- End Relation for Party, type: one --%>
  

  
  
  
<%-- Start Relation for UserLoginSecurityGroup, type: many --%>
<%if(userLogin != null){%>
  <%if(Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_VIEW", session)){%>    
    <%Iterator relatedIterator = UserLoginSecurityGroupHelper.findByUserLoginIdIterator(userLogin.getUserLoginId());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
      <b></b> Related Entities: <b>UserLoginSecurityGroup</b> with (USER_LOGIN_ID: <%=userLogin.getUserLoginId()%>)
    </div>
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
    
    <%String curFindString = "SEARCH_TYPE=UserLoginId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + userLogin.getUserLoginId();%>
    <a href="<%=response.encodeURL("/commonapp/security/securitygroup/FindUserLogin.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find UserLoginSecurityGroup]</a>

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
      int relatedLoopCount = 0;
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        relatedLoopCount++; if(relatedLoopCount > 10) break;
        UserLoginSecurityGroup userLoginSecurityGroupRelated = (UserLoginSecurityGroup)relatedIterator.next();
        if(userLoginSecurityGroupRelated != null)
        {
    %>
    <%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(userLoginSecurityGroupRelated.getUserLoginId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(userLoginSecurityGroupRelated.getGroupId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("/commonapp/security/securitygroup/ViewUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginSecurityGroupRelated.getUserLoginId() + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroupRelated.getGroupId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/security/securitygroup/EditUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginSecurityGroupRelated.getUserLoginId() + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroupRelated.getGroupId())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewUserLoginSecurityGroup.jsp?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginSecurityGroupRelated.getUserLoginId() + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroupRelated.getGroupId() + "&" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId + "&WEBEVENT=UPDATE_USER_LOGIN_SECURITY_GROUP&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  <%}%>
<%}%>
<%-- End Relation for UserLoginSecurityGroup, type: many --%>
  

  
  
  
<%-- Start Relation for LoginAccountHistory, type: many --%>
<%if(userLogin != null){%>
  <%if(Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_VIEW", session)){%>    
    <%Iterator relatedIterator = LoginAccountHistoryHelper.findByUserLoginIdIterator(userLogin.getUserLoginId());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
      <b></b> Related Entities: <b>LoginAccountHistory</b> with (USER_LOGIN_ID: <%=userLogin.getUserLoginId()%>)
    </div>
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
    
    <%String curFindString = "SEARCH_TYPE=UserLoginId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + userLogin.getUserLoginId();%>
    <a href="<%=response.encodeURL("/commonapp/security/login/FindUserLogin.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find LoginAccountHistory]</a>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr bgcolor="<%=rowColorResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>USER_LOGIN_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>USER_LOGIN_SEQ_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
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
      int relatedLoopCount = 0;
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        relatedLoopCount++; if(relatedLoopCount > 10) break;
        LoginAccountHistory loginAccountHistoryRelated = (LoginAccountHistory)relatedIterator.next();
        if(loginAccountHistoryRelated != null)
        {
    %>
    <%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(loginAccountHistoryRelated.getUserLoginId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(loginAccountHistoryRelated.getUserLoginSeqId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%{
        String dateString = null;
        String timeString = null;
        if(loginAccountHistoryRelated != null)
        {
          java.util.Date date = loginAccountHistoryRelated.getFromDate();
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
        if(loginAccountHistoryRelated != null)
        {
          java.util.Date date = loginAccountHistoryRelated.getThruDate();
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
    
      <%=UtilFormatOut.checkNull(loginAccountHistoryRelated.getPartyId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(loginAccountHistoryRelated.getUserId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(loginAccountHistoryRelated.getPassword())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("/commonapp/security/login/ViewLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + loginAccountHistoryRelated.getUserLoginId() + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + loginAccountHistoryRelated.getUserLoginSeqId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/security/login/EditLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + loginAccountHistoryRelated.getUserLoginId() + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + loginAccountHistoryRelated.getUserLoginSeqId())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + loginAccountHistoryRelated.getUserLoginId() + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + loginAccountHistoryRelated.getUserLoginSeqId() + "&" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId + "&WEBEVENT=UPDATE_LOGIN_ACCOUNT_HISTORY&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  <%}%>
<%}%>
<%-- End Relation for LoginAccountHistory, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (USER_LOGIN_ADMIN, or USER_LOGIN_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
