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
 *@author     David E. Jones
 *@created    Sep 1 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<% pageContext.setAttribute("PageName", "Change Password"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%
  boolean useValues = true;
  if(request.getAttribute("ERROR_MESSAGE") != null) useValues = false;

  String donePage = request.getParameter("DONE_PAGE");
  if(donePage == null || donePage.length() <= 0) donePage="viewprofile";
%>
  <p class="head1">Change Password</p>
  <form method="post" action="<ofbiz:url>/updatepassword/<%=donePage%></ofbiz:url>" name="changepasswordform">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">Old Password</div></td>
      <td width="74%">
        <input type="password" name="OLD_PASSWORD" size="20" maxlength="20">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">New Password</div></td>
      <td width="74%">
        <input type="password" name="NEW_PASSWORD" size="20" maxlength="20">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Confirm Password</div></td>
      <td width="74%">
        <input type="password" name="NEW_PASSWORD_CONFIRM" size="20" maxlength="20">
      *</td>
    </tr>
  </table>
  </form>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Done/Cancel]</a>
    &nbsp;<a href="javascript:document.changepasswordform.submit()" class="buttontext">[Save]</a>
    <%--  <input type="image" value="[Save]" border="0" src="/commerce/images/btn_save.gif"> --%>

<%@ include file="/includes/footer.jsp" %>
