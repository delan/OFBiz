
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
 *@created    Tue Jul 17 02:16:53 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.login.*" %>

<%@ page import="org.ofbiz.commonapp.party.party.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewLoginAccountHistory"); %>

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
<SCRIPT language='JavaScript'>  
function ShowViewTab(lname) 
{
    document.all.viewtab.className = (lname == 'view') ? 'ontab' : 'offtab';
    document.all.viewlnk.className = (lname == 'view') ? 'onlnk' : 'offlnk';
    document.all.viewarea.style.visibility = (lname == 'view') ? 'visible' : 'hidden';

    document.all.edittab.className = (lname == 'edit') ? 'ontab' : 'offtab';
    document.all.editlnk.className = (lname == 'edit') ? 'onlnk' : 'offlnk';
    document.all.editarea.style.visibility = (lname == 'edit') ? 'visible' : 'hidden';
}
</SCRIPT>
<table cellpadding='0' cellspacing='0'><tr>  
  <td id=viewtab class=ontab>
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View LoginAccountHistory</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit LoginAccountHistory</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: LoginAccountHistory with (USER_LOGIN_ID, USER_LOGIN_SEQ_ID: <%=userLoginId%>, <%=userLoginSeqId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindLoginAccountHistory")%>" class="buttontext">[Find LoginAccountHistory]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewLoginAccountHistory")%>" class="buttontext">[Create New LoginAccountHistory]</a>
<%}%>
<%if(loginAccountHistory != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateLoginAccountHistory?UPDATE_MODE=DELETE&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLoginId + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + userLoginSeqId)%>" class="buttontext">[Delete this LoginAccountHistory]</a>
  <%}%>
<%}%>

<%if(loginAccountHistory == null){%>
<div style='width:100%;height:400px;overflow:visible;border-style:inset;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
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
  </div>
<%LoginAccountHistory loginAccountHistorySave = loginAccountHistory;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(loginAccountHistory == null && (userLoginId != null || userLoginSeqId != null)){%>
    LoginAccountHistory with (USER_LOGIN_ID, USER_LOGIN_SEQ_ID: <%=userLoginId%>, <%=userLoginSeqId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    loginAccountHistory = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateLoginAccountHistory")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(loginAccountHistory == null){%>
  <%if(hasCreatePermission){%>
    You may create a LoginAccountHistory by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID" value="<%=UtilFormatOut.checkNull(userLoginId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_SEQ_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID" value="<%=UtilFormatOut.checkNull(userLoginSeqId)%>">
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
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="LOGIN_ACCOUNT_HISTORY_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.LOGIN_ACCOUNT_HISTORY_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="LOGIN_ACCOUNT_HISTORY_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
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
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="LOGIN_ACCOUNT_HISTORY_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.LOGIN_ACCOUNT_HISTORY_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="LOGIN_ACCOUNT_HISTORY_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="LOGIN_ACCOUNT_HISTORY_PARTY_ID" value="<%if(loginAccountHistory!=null){%><%=UtilFormatOut.checkNull(loginAccountHistory.getPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("LOGIN_ACCOUNT_HISTORY_PARTY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>USER_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="LOGIN_ACCOUNT_HISTORY_USER_ID" value="<%if(loginAccountHistory!=null){%><%=UtilFormatOut.checkNull(loginAccountHistory.getUserId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("LOGIN_ACCOUNT_HISTORY_USER_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PASSWORD</td>
    <td>
      <input class='editInputBox' type="text" size="60" maxlength="60" name="LOGIN_ACCOUNT_HISTORY_PASSWORD" value="<%if(loginAccountHistory!=null){%><%=UtilFormatOut.checkNull(loginAccountHistory.getPassword())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("LOGIN_ACCOUNT_HISTORY_PASSWORD"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>
  </div>
<%}%>
</div>
<%if((hasUpdatePermission || hasCreatePermission) && loginAccountHistory == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the loginAccountHistory for cases when removed to retain passed form values --%>
<%loginAccountHistory = loginAccountHistorySave;%>

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
      <a href="<%=response.encodeURL(controlPath + "/ViewUserLogin?" + "USER_LOGIN_USER_LOGIN_ID=" + loginAccountHistory.getUserLoginId())%>" class="buttontext">[View UserLogin]</a>      
    <%if(userLoginRelated == null){%>
      <%if(Security.hasEntityPermission("USER_LOGIN", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewUserLogin?" + "USER_LOGIN_USER_LOGIN_ID=" + loginAccountHistory.getUserLoginId())%>" class="buttontext">[Create UserLogin]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
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
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + loginAccountHistory.getPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + loginAccountHistory.getPartyId())%>" class="buttontext">[Create Party]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
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
