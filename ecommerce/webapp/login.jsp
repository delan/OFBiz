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

<%pageContext.setAttribute("PageName", "Log In");%>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%String previousParams=(String)session.getAttribute(SiteDefs.PREVIOUS_PARAMS);%>
<%String loginFormUrl=controlPath + "/login"; if(previousParams != null) loginFormUrl=loginFormUrl + "?" + previousParams;%>

<br>
<div class="head1">Log&nbsp;In</div>
<br>
<table width='100%' border='0' cellpadding='0' cellspacing='0'>
  <tr>
    <td width='50%' valign=top>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign=middle align=center>
      <div class="boxhead">Registered&nbsp;User</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
      <form method="POST" action="<%=response.encodeURL(loginFormUrl)%>" name="loginform" style='margin: 0;'>
        <div align=center>Username:&nbsp;<input type="text" name="USERNAME" value="<%=UtilFormatOut.checkNull(request.getParameter("USERNAME"))%>" size="20"></div>
        <div align=center>Password:&nbsp;<input type="password" name="PASSWORD" value="" size="20"></div>
        <div align=center><input type="submit" value="Login"></div>
      </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

    </td>
    <td>&nbsp;&nbsp;&nbsp;</td>
    <td width='50%' valign=top>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign=middle align=center>
      <div class="boxhead">New&nbsp;User</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
          <form method="POST" action="<ofbiz:url>/newcustomer</ofbiz:url>" style='margin: 0;'>
            <div align=center>You may create a new account here:</div>
            <div align=center><input type="submit" value="Create"></div>
          </form>
        </tr>
      </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

    </td>
  </tr>
</table>

<script language="JavaScript">
<!--
  document.loginform.USERNAME.focus();
//-->
</script>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
