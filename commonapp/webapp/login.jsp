<%
/**
 *  Title: Login Page
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
 *@created    May 22 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.core.util.*" %>

<% pageContext.setAttribute("PageName", "login"); %>
<%String previousParams=(String)session.getAttribute(SiteDefs.PREVIOUS_PARAMS);%>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<br>
<h2 style=margin:0;>Log In</h2>

<table border="1" cellpadding="3" cellspacing="0">
  <tr>
    <%String loginFormUrl=controlPath + "/login"; if(previousParams != null) loginUrl=loginUrl + "?" + previousParams;%>
    <form method="POST" action="<%=response.encodeURL(loginFormUrl)%>" name="loginform">
      <td><b>Registered&nbsp;User</b></td>
      <td> 
        Username: <input type="text" name="USERNAME" value="<%=UtilFormatOut.checkNull(request.getParameter("USERNAME"))%>" size="20">
        <br>
        Password: <input type="password" name="PASSWORD" value="" size="20">
      </td>
      <td><input type="submit" value="Login"></td>
    </form>
  </tr>
  <tr>
    <form method="POST" action="<%=response.encodeURL("person/RequestPerson.jsp")%>">
      <td><b>New&nbsp;User</b></td>
      <td>You may create a new account here:</td>
      <td><input type="submit" value="Create" disabled></td>
    </form>
  </tr>
</table>

<script language="JavaScript">
<!--
  document.loginform.USERNAME.focus();
//-->
</script>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
