
<%
/**
 *  Title: Login Account History Entity
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
 *@created    Fri Jun 29 16:05:17 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.login.*" %>


<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditLoginAccountHistory"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String userLoginId = request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID");  
  String userLoginSeqId = request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID");  

  
  

  LoginAccountHistory loginAccountHistory = LoginAccountHistoryHelper.findByPrimaryKey(userLoginId, userLoginSeqId);
%>

<b><u>View Entity: LoginAccountHistory with (USER_LOGIN_ID, USER_LOGIN_SEQ_ID: <%=userLoginId%>, <%=userLoginSeqId%>).</u></b>
<br>
<a href="<%=response.encodeURL("FindLoginAccountHistory.jsp")%>" class="buttontext">[Find LoginAccountHistory]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp")%>" class="buttontext">[Create LoginAccountHistory]</a>
<%}%>
<%if(loginAccountHistory != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp?WEBEVENT=UPDATE_LOGIN_ACCOUNT_HISTORY&UPDATE_MODE=DELETE&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLoginId + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + userLoginSeqId)%>" class="buttontext">[Delete this LoginAccountHistory]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(userLoginId != null && userLoginSeqId != null){%>
    <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLoginId + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + userLoginSeqId)%>" class="buttontext">[Edit LoginAccountHistory]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(loginAccountHistory == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified LoginAccountHistory was not found.</h3></td></tr>
<%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>USER_LOGIN_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getUserLoginId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>USER_LOGIN_SEQ_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getUserLoginSeqId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>FROM_DATE</b></td>
    <td>
    
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
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>THRU_DATE</b></td>
    <td>
    
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
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getPartyId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>USER_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getUserId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PASSWORD</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getPassword())%>
    
    </td>
  </tr>

<%} //end if loginAccountHistory == null %>
</table>

<a href="<%=response.encodeURL("FindLoginAccountHistory.jsp")%>" class="buttontext">[Find LoginAccountHistory]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp")%>" class="buttontext">[Create LoginAccountHistory]</a>
<%}%>
<%if(loginAccountHistory != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp?WEBEVENT=UPDATE_LOGIN_ACCOUNT_HISTORY&UPDATE_MODE=DELETE&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLoginId + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + userLoginSeqId)%>" class="buttontext">[Delete this LoginAccountHistory]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(userLoginId != null && userLoginSeqId != null){%>
    <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLoginId + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + userLoginSeqId)%>" class="buttontext">[Edit LoginAccountHistory]</a>
  <%}%>
<%}%>

  
  
  
<%-- Start Relation for UserLogin, type: one --%>
<%if(loginAccountHistory != null){%>
  <%if(Security.hasEntityPermission("USER_LOGIN", "_VIEW", session)){%>
    <%UserLogin userLogin = UserLoginHelper.findByPrimaryKey(loginAccountHistory.getUserLoginId());%>
    <hr>
    <b>Related Entity: UserLogin with (USER_LOGIN_ID: <%=loginAccountHistory.getUserLoginId()%>)</b>
    <br>
    <%if(loginAccountHistory.getUserLoginId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/security/login/ViewUserLogin.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + loginAccountHistory.getUserLoginId())%>" class="buttontext">[View UserLogin Details]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (LOGIN_ACCOUNT_HISTORY_ADMIN, or LOGIN_ACCOUNT_HISTORY_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
