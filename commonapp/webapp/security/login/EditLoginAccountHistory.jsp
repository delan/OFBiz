
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
 *@created    Fri Jul 06 18:25:22 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.commonapp.security.login.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditLoginAccountHistory"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasCreatePermission=Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_DELETE", session);%>
<%if(hasCreatePermission || hasUpdatePermission){%>

<%
  boolean showFields = true;
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String userLoginId = request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID");  
  String userLoginSeqId = request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID");  


  LoginAccountHistory loginAccountHistory = LoginAccountHistoryHelper.findByPrimaryKey(userLoginId, userLoginSeqId);
%>

<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    loginAccountHistory = null;
  }
%>

<a href="<%=response.encodeURL("FindLoginAccountHistory.jsp")%>" class="buttontext">[Find LoginAccountHistory]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp")%>" class="buttontext">[Create LoginAccountHistory]</a>
<%}%>
<%if(loginAccountHistory != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp?WEBEVENT=UPDATE_LOGIN_ACCOUNT_HISTORY&UPDATE_MODE=DELETE&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLoginId + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + userLoginSeqId)%>" class="buttontext">[Delete this LoginAccountHistory]</a>
  <%}%>
<%}%>
<%if(userLoginId != null && userLoginSeqId != null){%>
  <a href="<%=response.encodeURL("ViewLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLoginId + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + userLoginSeqId)%>" class="buttontext">[View LoginAccountHistory Details]</a>
<%}%>
<br>

<%if(loginAccountHistory == null && (userLoginId != null || userLoginSeqId != null)){%>
    LoginAccountHistory with (USER_LOGIN_ID, USER_LOGIN_SEQ_ID: <%=userLoginId%>, <%=userLoginSeqId%>) not found.<br>
<%}%>
<form action="<%=response.encodeURL("EditLoginAccountHistory.jsp")%>" method="POST" name="updateForm">
  <input type="hidden" name="WEBEVENT" value="UPDATE_LOGIN_ACCOUNT_HISTORY">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(loginAccountHistory == null){%>
  <%if(hasCreatePermission){%>
    You may create a LoginAccountHistory by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID" value="<%=UtilFormatOut.checkNull(userLoginId)%>">
      

      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_SEQ_ID</td>
      <td>
      
        <input type="text" size="20" maxlength="20" name="LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID" value="<%=UtilFormatOut.checkNull(userLoginSeqId)%>">
      

      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a LoginAccountHistory (LOGIN_ACCOUNT_HISTORY_ADMIN, or LOGIN_ACCOUNT_HISTORY_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID" value="<%=userLoginId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_ID</td>
      <td>
        <b><%=userLoginId%></b> (This cannot be changed without re-creating the loginAccountHistory.)
      </td>
    </tr>
      <input type="hidden" name="LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID" value="<%=userLoginSeqId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_SEQ_ID</td>
      <td>
        <b><%=userLoginSeqId%></b> (This cannot be changed without re-creating the loginAccountHistory.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a LoginAccountHistory (LOGIN_ACCOUNT_HISTORY_ADMIN, or LOGIN_ACCOUNT_HISTORY_UPDATE needed).
  <%}%>
<%} //end if loginAccountHistory == null %>

<%if(showFields){%>

  

  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FROM_DATE</td>
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
        else
        {
          dateString = request.getParameter("LOGIN_ACCOUNT_HISTORY_FROM_DATE_DATE");
          timeString = request.getParameter("LOGIN_ACCOUNT_HISTORY_FROM_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input type="text" name="LOGIN_ACCOUNT_HISTORY_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.LOGIN_ACCOUNT_HISTORY_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input type="text" size="6" maxlength="10" name="LOGIN_ACCOUNT_HISTORY_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    
    </td>
  </tr>
  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>THRU_DATE</td>
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
        else
        {
          dateString = request.getParameter("LOGIN_ACCOUNT_HISTORY_THRU_DATE_DATE");
          timeString = request.getParameter("LOGIN_ACCOUNT_HISTORY_THRU_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input type="text" name="LOGIN_ACCOUNT_HISTORY_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.LOGIN_ACCOUNT_HISTORY_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input type="text" size="6" maxlength="10" name="LOGIN_ACCOUNT_HISTORY_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    
    </td>
  </tr>
  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_ID</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="LOGIN_ACCOUNT_HISTORY_PARTY_ID" value="<%if(loginAccountHistory!=null){%><%=UtilFormatOut.checkNull(loginAccountHistory.getPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("LOGIN_ACCOUNT_HISTORY_PARTY_ID"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>USER_ID</td>
    <td>
    
      <input type="text" size="20" maxlength="20" name="LOGIN_ACCOUNT_HISTORY_USER_ID" value="<%if(loginAccountHistory!=null){%><%=UtilFormatOut.checkNull(loginAccountHistory.getUserId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_ID"))%><%}%>">
    
    </td>
  </tr>
  

  
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PASSWORD</td>
    <td>
    
      <input type="text" size="60" maxlength="60" name="LOGIN_ACCOUNT_HISTORY_PASSWORD" value="<%if(loginAccountHistory!=null){%><%=UtilFormatOut.checkNull(loginAccountHistory.getPassword())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("LOGIN_ACCOUNT_HISTORY_PASSWORD"))%><%}%>">
    
    </td>
  </tr>
  

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>

<a href="<%=response.encodeURL("FindLoginAccountHistory.jsp")%>" class="buttontext">[Find LoginAccountHistory]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp")%>" class="buttontext">[Create LoginAccountHistory]</a>
<%}%>
<%if(loginAccountHistory != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditLoginAccountHistory.jsp?WEBEVENT=UPDATE_LOGIN_ACCOUNT_HISTORY&UPDATE_MODE=DELETE&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLoginId + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + userLoginSeqId)%>" class="buttontext">[Delete this LoginAccountHistory]</a>
  <%}%>
<%}%>
<%if(userLoginId != null && userLoginSeqId != null){%>
  <a href="<%=response.encodeURL("ViewLoginAccountHistory.jsp?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLoginId + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + userLoginSeqId)%>" class="buttontext">[View LoginAccountHistory Details]</a>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (LOGIN_ACCOUNT_HISTORY_ADMIN, LOGIN_ACCOUNT_HISTORY_CREATE, or LOGIN_ACCOUNT_HISTORY_UPDATE needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

