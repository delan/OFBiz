
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
 *@created    Fri Jul 06 16:51:34 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.login.*" %>

<%@ page import="org.ofbiz.commonapp.party.party.*" %>

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
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String userLoginId = request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID");  
  String userLoginSeqId = request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID");  

  
  

  LoginAccountHistory loginAccountHistory = LoginAccountHistoryHelper.findByPrimaryKey(userLoginId, userLoginSeqId);
%>

<br>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: LoginAccountHistory with (USER_LOGIN_ID, USER_LOGIN_SEQ_ID: <%=userLoginId%>, <%=userLoginSeqId%>).</b>
</div>

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
<tr class="<%=rowClass1%>"><td><h3>Specified LoginAccountHistory was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>USER_LOGIN_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getUserLoginId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>USER_LOGIN_SEQ_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getUserLoginSeqId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
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

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
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

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getPartyId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>USER_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(loginAccountHistory.getUserId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
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
<br>
<br>
<SCRIPT language='JavaScript'>  
var numTabs=2;
function ShowTab(lname) 
{
  for(inc=1; inc <= numTabs; inc++)
  {
    document.all['tab' + inc].className = (lname == 'tab' + inc) ? 'ontab' : 'offtab';
    document.all['lnk' + inc].className = (lname == 'tab' + inc) ? 'onlnk' : 'offlnk';
    document.all['area' + inc].style.visibility = (lname == 'tab' + inc) ? 'visible' : 'hidden';
  }
}
</SCRIPT>
<%if(loginAccountHistory != null){%>
<table cellpadding='0' cellspacing='0'><tr>

  
    <%if(Security.hasEntityPermission("USER_LOGIN", "_VIEW", session)){%>
    <td id=tab1 class=ontab>
      <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> UserLogin</a>
    </td>
    <%}%>

  
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <td id=tab2 class=offtab>
      <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> Party</a>
    </td>
    <%}%>

</tr></table>
<%}%>
  

  
  
  
<%-- Start Relation for UserLogin, type: one --%>
<%if(loginAccountHistory != null){%>
  <%if(Security.hasEntityPermission("USER_LOGIN", "_VIEW", session)){%>
    <%UserLogin userLoginRelated = UserLoginHelper.findByPrimaryKey(loginAccountHistory.getUserLoginId());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>UserLogin</b> with (USER_LOGIN_ID: <%=loginAccountHistory.getUserLoginId()%>)
    </div>
    <%if(loginAccountHistory.getUserLoginId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/security/login/ViewUserLogin.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + loginAccountHistory.getUserLoginId())%>" class="buttontext">[View UserLogin Details]</a>
      
    <%if(userLoginRelated != null){%>
      <%if(Security.hasEntityPermission("USER_LOGIN", "_EDIT", session)){%>
        <a href="<%=response.encodeURL("/commonapp/security/login/EditUserLogin.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + loginAccountHistory.getUserLoginId())%>" class="buttontext">[Edit UserLogin]</a>
      <%}%>
    <%}else{%>
      <%if(Security.hasEntityPermission("USER_LOGIN", "_CREATE", session)){%>
        <a href="<%=response.encodeURL("/commonapp/security/login/EditUserLogin.jsp?" + "USER_LOGIN_USER_LOGIN_ID=" + loginAccountHistory.getUserLoginId())%>" class="buttontext">[Create UserLogin]</a>
      <%}%>
    <%}%>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(userLoginRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified UserLogin was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>USER_LOGIN_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLoginRelated.getUserLoginId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLoginRelated.getPartyId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTACT_MECHANISM_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLoginRelated.getContactMechanismId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CURRENT_USER_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLoginRelated.getCurrentUserId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CURRENT_PASSWORD</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(userLoginRelated.getCurrentPassword())%>
    
    </td>
  </tr>

    <%} //end if userLoginRelated == null %>
    </table>
  </div>
  <%}%>
<%}%>
<%-- End Relation for UserLogin, type: one --%>
  

  
  
  
<%-- Start Relation for Party, type: one --%>
<%if(loginAccountHistory != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%Party partyRelated = PartyHelper.findByPrimaryKey(loginAccountHistory.getPartyId());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=loginAccountHistory.getPartyId()%>)
    </div>
    <%if(loginAccountHistory.getPartyId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/party/party/ViewParty.jsp?" + "PARTY_PARTY_ID=" + loginAccountHistory.getPartyId())%>" class="buttontext">[View Party Details]</a>
      
    <%if(partyRelated != null){%>
      <%if(Security.hasEntityPermission("PARTY", "_EDIT", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditParty.jsp?" + "PARTY_PARTY_ID=" + loginAccountHistory.getPartyId())%>" class="buttontext">[Edit Party]</a>
      <%}%>
    <%}else{%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditParty.jsp?" + "PARTY_PARTY_ID=" + loginAccountHistory.getPartyId())%>" class="buttontext">[Create Party]</a>
      <%}%>
    <%}%>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Party was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyRelated.getPartyId())%>
    
    </td>
  </tr>

    <%} //end if partyRelated == null %>
    </table>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Party, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (LOGIN_ACCOUNT_HISTORY_ADMIN, or LOGIN_ACCOUNT_HISTORY_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
