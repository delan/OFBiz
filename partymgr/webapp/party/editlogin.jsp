<%
    /**
     *  Title: Change Password Page
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
     *@author     Andy Zeneski
     *@created    May 15, 2002
     *@version    1.0
     */
%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>

<%
        String userLoginId = request.getParameter("userlogin_id");
        if (userLoginId == null) userLoginId = (String) request.getSession().getAttribute("userLoginId");
        else request.getSession().setAttribute("userLoginId", userLoginId);

        GenericValue userUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        if (userUserLogin != null) pageContext.setAttribute("userUserLogin", userUserLogin);

        boolean tryEntity = true;
        if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
        pageContext.setAttribute("tryEntity", new Boolean(tryEntity));

        String donePage = request.getParameter("DONE_PAGE");
        if(donePage == null || donePage.length() <= 0) donePage="viewprofile";
%>
  <p class="head1">Edit UserLogin</p>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.changepasswordform.submit()" class="buttontext">[Save]</a>

  <form method="post" action="<ofbiz:url>/updateLogin/<%=donePage%></ofbiz:url>" name="changepasswordform">
  <input type="hidden" name="userLoginId" value="<%=userLoginId%>">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">New Password</div></td>
      <td width="74%">
        <input type="password" name="newPassword" size="20" maxlength="20">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">New Password Verify</div></td>
      <td width="74%">
        <input type="password" name="newPasswordVerify" size="20" maxlength="20">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Password Hint</div></td>
      <td width="74%">
        <input type="text" size="40" maxlength="100" name="passwordHint" value="<ofbiz:entityfield attribute="userUserLogin" field="passwordHint"/>">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Account Enabled</div></td>
      <td width="74%">
        <%
           Boolean enabledFlag = userUserLogin.getBoolean("enabled");
           if (enabledFlag == null) enabledFlag = new Boolean(true);
        %>
        <select name="enabled">
            <option value="true" <%=enabledFlag.booleanValue() ? "SELECTED" : ""%>>Yes</option>
            <option value="false" <%=!enabledFlag.booleanValue() ? "SELECTED" : ""%>>No</option>
        </select>
      </td>
    </tr>
  </table>
  </form>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.changepasswordform.submit()" class="buttontext">[Save]</a>
<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
