
<%@ include file="/includes/envsetup.jsp" %>
<%@ taglib uri='regions' prefix='region' %>

<%@page contentType='text/html; charset=UTF-8'%>

<html>
<head>
    <title><%EntityField.run("layoutSettings", "companyName", pageContext);%>: <region:render section='title'/></title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/includes/maincss.css" type="text/css">
</head>
<body <%EntityField.run("layoutSettings", "bodyTopMargin", "topmargin='","'", pageContext);%> <%EntityField.run("layoutSettings", "bodyLeftMargin", "leftmargin='","'", pageContext);%> <%EntityField.run("layoutSettings", "bodyRightMargin", "rightmargin='","'", pageContext);%> <%EntityField.run("layoutSettings", "bodyMarginHeight", "marginheight='","'", pageContext);%> <%EntityField.run("layoutSettings", "bodyMarginWidth", "marginwidth='","'", pageContext);%>>
<region:render section='header'/>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
 <tr>
  <td width='150' valign=top align=left>
    <region:render section='leftbar'/>
  </td>
  <td width='5'>&nbsp;&nbsp;&nbsp;</td>
  <td width='100%' valign=top align=left>
    <region:render section='error'/>
    <region:render section='content'/>
  </td>
  <td width='5'>&nbsp;&nbsp;&nbsp;</td>
  <td width='150' valign=top align=right>
    <region:render section='rightbar'/>
  </td>
 </tr>
</table>

<region:render section='footer'/>

</body>
</html>
