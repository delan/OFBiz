<%@ page import="org.ofbiz.core.util.SiteDefs" %>
<html>
<head>
<title>Open For Business Message</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<% String ERROR_MSG = (String) request.getAttribute(SiteDefs.ERROR_MESSAGE); %>

<body bgcolor="#FFFFFF">
<div align="center"><br>
  <br>
  <br>
  <table width="400" border="1" height="200">
    <tr>
      <td>
        <table width="400" border="0" height="200">
          <tr bgcolor="#6666CC"> 
            <td height="45"> 
              <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="4" color="#FFFFFF"><b>:MESSAGE:</b></font></div>
            </td>
          </tr>
          <tr> 
            <td>
              <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><%= ERROR_MSG %></font></div>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</div>
<div align="center"></div>
</body>
</html>
