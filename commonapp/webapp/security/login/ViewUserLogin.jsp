
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
 *@created    Wed Jul 18 08:43:48 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.login.*" %>

<%@ page import="org.ofbiz.commonapp.party.party.*" %>
<%@ page import="org.ofbiz.commonapp.security.securitygroup.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewUserLogin"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("USER_LOGIN", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("USER_LOGIN", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("USER_LOGIN", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("USER_LOGIN", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String userLoginId = request.getParameter("USER_LOGIN_USER_LOGIN_ID");  


  UserLogin userLogin = UserLoginHelper.findByPrimaryKey(userLoginId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View UserLogin</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit UserLogin</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: UserLogin with (USER_LOGIN_ID: <%=userLoginId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindUserLogin")%>" class="buttontext">[Find UserLogin]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewUserLogin")%>" class="buttontext">[Create New UserLogin]</a>
<%}%>
<%if(userLogin != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateUserLogin?UPDATE_MODE=DELETE&" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId)%>" class="buttontext">[Delete this UserLogin]</a>
  <%}%>
<%}%>

<%if(userLogin == null){%>
<div style='width:100%;height:400px;overflow:visible;border-style:inset;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(userLogin == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified UserLogin was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>USER_LOGIN_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLogin.getUserLoginId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLogin.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTACT_MECHANISM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLogin.getContactMechanismId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CURRENT_USER_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLogin.getCurrentUserId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CURRENT_PASSWORD</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLogin.getCurrentPassword())%>
    </td>
  </tr>

<%} //end if userLogin == null %>
</table>
  </div>
<%UserLogin userLoginSave = userLogin;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(userLogin == null && (userLoginId != null)){%>
    UserLogin with (USER_LOGIN_ID: <%=userLoginId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    userLogin = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateUserLogin")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(userLogin == null){%>
  <%if(hasCreatePermission){%>
    You may create a UserLogin by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="USER_LOGIN_USER_LOGIN_ID" value="<%=UtilFormatOut.checkNull(userLoginId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a UserLogin (USER_LOGIN_ADMIN, or USER_LOGIN_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="USER_LOGIN_USER_LOGIN_ID" value="<%=userLoginId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_ID</td>
      <td>
        <b><%=userLoginId%></b> (This cannot be changed without re-creating the userLogin.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a UserLogin (USER_LOGIN_ADMIN, or USER_LOGIN_UPDATE needed).
  <%}%>
<%} //end if userLogin == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARTY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="USER_LOGIN_PARTY_ID" value="<%if(userLogin!=null){%><%=UtilFormatOut.checkNull(userLogin.getPartyId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("USER_LOGIN_PARTY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>CONTACT_MECHANISM_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="USER_LOGIN_CONTACT_MECHANISM_ID" value="<%if(userLogin!=null){%><%=UtilFormatOut.checkNull(userLogin.getContactMechanismId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("USER_LOGIN_CONTACT_MECHANISM_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>CURRENT_USER_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="USER_LOGIN_CURRENT_USER_ID" value="<%if(userLogin!=null){%><%=UtilFormatOut.checkNull(userLogin.getCurrentUserId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("USER_LOGIN_CURRENT_USER_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>CURRENT_PASSWORD</td>
    <td>
      <input class='editInputBox' type="text" size="60" maxlength="60" name="USER_LOGIN_CURRENT_PASSWORD" value="<%if(userLogin!=null){%><%=UtilFormatOut.checkNull(userLogin.getCurrentPassword())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("USER_LOGIN_CURRENT_PASSWORD"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && userLogin == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the userLogin for cases when removed to retain passed form values --%>
<%userLogin = userLoginSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=3;
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
<%if(userLogin != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> Party</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> UserLoginSecurityGroup</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> LoginAccountHistory</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for Party, type: one --%>
<%if(userLogin != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%Party partyRelated = PartyHelper.findByPrimaryKey(userLogin.getPartyId());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=userLogin.getPartyId()%>)
    </div>
    <%if(userLogin.getPartyId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + userLogin.getPartyId())%>" class="buttontext">[View Party]</a>      
    <%if(partyRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewParty?" + "PARTY_PARTY_ID=" + userLogin.getPartyId())%>" class="buttontext">[Create Party]</a>
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
  

<%-- Start Relation for UserLoginSecurityGroup, type: many --%>
<%if(userLogin != null){%>
  <%if(Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_VIEW", session)){%>    
    <%Iterator relatedIterator = UserLoginSecurityGroupHelper.findByUserLoginIdIterator(userLogin.getUserLoginId());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>UserLoginSecurityGroup</b> with (USER_LOGIN_ID: <%=userLogin.getUserLoginId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUserLoginSecurityGroup?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLogin.getUserLoginId())%>" class="buttontext">[Create UserLoginSecurityGroup]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=UserLoginId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + userLogin.getUserLoginId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindUserLogin?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find UserLoginSecurityGroup]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>USER_LOGIN_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GROUP_ID</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(relatedDeletePerm){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
    <%
     int relatedLoopCount = 0;
     if(relatedIterator != null && relatedIterator.hasNext())
     {
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        relatedLoopCount++; //if(relatedLoopCount > 10) break;
        UserLoginSecurityGroup userLoginSecurityGroupRelated = (UserLoginSecurityGroup)relatedIterator.next();
        if(userLoginSecurityGroupRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
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
        <a href="<%=response.encodeURL(controlPath + "/ViewUserLoginSecurityGroup?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginSecurityGroupRelated.getUserLoginId() + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroupRelated.getGroupId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateUserLoginSecurityGroup?" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginSecurityGroupRelated.getUserLoginId() + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroupRelated.getGroupId() + "&" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No UserLoginSecurityGroups Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for UserLoginSecurityGroup, type: many --%>
  

<%-- Start Relation for LoginAccountHistory, type: many --%>
<%if(userLogin != null){%>
  <%if(Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_VIEW", session)){%>    
    <%Iterator relatedIterator = LoginAccountHistoryHelper.findByUserLoginIdIterator(userLogin.getUserLoginId());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>LoginAccountHistory</b> with (USER_LOGIN_ID: <%=userLogin.getUserLoginId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewLoginAccountHistory?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + userLogin.getUserLoginId())%>" class="buttontext">[Create LoginAccountHistory]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=UserLoginId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + userLogin.getUserLoginId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindUserLogin?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find LoginAccountHistory]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>USER_LOGIN_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>USER_LOGIN_SEQ_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>USER_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PASSWORD</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(relatedDeletePerm){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
    <%
     int relatedLoopCount = 0;
     if(relatedIterator != null && relatedIterator.hasNext())
     {
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        relatedLoopCount++; //if(relatedLoopCount > 10) break;
        LoginAccountHistory loginAccountHistoryRelated = (LoginAccountHistory)relatedIterator.next();
        if(loginAccountHistoryRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
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
        <a href="<%=response.encodeURL(controlPath + "/ViewLoginAccountHistory?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + loginAccountHistoryRelated.getUserLoginId() + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + loginAccountHistoryRelated.getUserLoginSeqId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateLoginAccountHistory?" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_ID=" + loginAccountHistoryRelated.getUserLoginId() + "&" + "LOGIN_ACCOUNT_HISTORY_USER_LOGIN_SEQ_ID=" + loginAccountHistoryRelated.getUserLoginSeqId() + "&" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="9">
<h3>No LoginAccountHistorys Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for LoginAccountHistory, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (USER_LOGIN_ADMIN, or USER_LOGIN_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
