<%--
 *  Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski
 *@version    1.0
--%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.*" %>

<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>
<%
    String partyId = request.getParameter("party_id");
    if (partyId == null) partyId = request.getParameter("partyId");
    if (partyId == null) partyId = (String) request.getAttribute("partyId");
    
    String userLoginId = request.getParameter("userlogin_id");
    if (userLoginId == null) userLoginId = request.getParameter("userLoginId");
    if (userLoginId == null) userLoginId = (String) request.getAttribute("userLoginId");

    GenericValue userUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
    if (userUserLogin != null) pageContext.setAttribute("userUserLogin", userUserLogin);

    boolean tryEntity = true;
    if(request.getAttribute("_ERROR_MESSAGE_") != null) tryEntity = false;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));

    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) donePage="viewprofile?partyId=" + partyId;
%>
 
  <p class="head1">Change UserLogin Password</p>

  <form method="post" action="<ofbiz:url>/updatePassword</ofbiz:url>" name="changepasswordform">
  <input type="hidden" name="partyId" value="<%=partyId%>">
  <input type="hidden" name="userLoginId" value="<%=userLoginId%>">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align="right"><div class="tabletext">New Password</div></td>
      <td width="74%">
        <input type="password" class="inputBox" name="newPassword" size="20" maxlength="20">
      *</td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tabletext">New Password Verify</div></td>
      <td width="74%">
        <input type="password" class="inputBox" name="newPasswordVerify" size="20" maxlength="20">
      *</td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tabletext">Password Hint</div></td>
      <td width="74%"><input type="text" class="inputBox" size="40" maxlength="100" name="passwordHint" value='<ofbiz:entityfield attribute="userUserLogin" field="passwordHint"/>'></td>
    </tr>
  </table>
  </form>
  <div>
    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.changepasswordform.submit()" class="buttontext">[Change&nbsp;Password]</a>
  </div>
<br/>
  <p class="head1">Update UserLogin Security Settings</p>
  <form method="post" action="<ofbiz:url>/updateUserLoginSecurity</ofbiz:url>" name="updatesecurityform">
  <input type="hidden" name="partyId" value="<%=partyId%>">
  <input type="hidden" name="userLoginId" value="<%=userLoginId%>">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align="right"><div class="tabletext">Account Enabled?</div></td>
      <td width="74%">
        <select name="enabled" class="selectBox">
          <option><ofbiz:inputvalue field="enabled" entityAttr="userUserLogin"/></option>
          <option value='<ofbiz:inputvalue field="enabled" entityAttr="userUserLogin"/>'></option>
          <option>Y</option>
          <option>N</option>
        </select>
      </td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tabletext">Disabled Date</div></td>
      <td width="74%"><input type="text" class="inputBox" size="20" name="disabledDateTime" value='<ofbiz:inputvalue entityAttr="userUserLogin" field="disabledDateTime"/>'></td>
    </tr>
    <tr>
      <td width="26%" align="right"><div class="tabletext">Successive Failed Logins</div></td>
      <td width="74%"><input type="text" class="inputBox" size="5" name="successiveFailedLogins" value='<ofbiz:inputvalue entityAttr="userUserLogin" field="successiveFailedLogins"/>'></td>
    </tr>
  </table>
  </form>

  <div>
    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.updatesecurityform.submit()" class="buttontext">[Update&nbsp;Security&nbsp;Settings]</a>
  </div>

<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
