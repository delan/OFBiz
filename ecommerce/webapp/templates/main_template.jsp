
<%@ page import="org.ofbiz.commonapp.product.catalog.*"%>
<%@ include file="/includes/envsetup.jsp" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='regions' prefix='region' %>

<html>
<head>
    <%@page contentType='text/html; charset=UTF-8'%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><%EntityField.run("layoutSettings", "companyName", pageContext);%>: <region:render section='title'/></title>
    <link rel='stylesheet' href='<ofbiz:contenturl>/images/maincss.css</ofbiz:contenturl>' type='text/css'>
    
    <%-- Append CSS for catalog --%>
    <%if (CatalogWorker.getProdCatalog(pageContext) != null && CatalogWorker.getProdCatalog(pageContext).get("styleSheet") != null) {%>
    <link rel='stylesheet' href="<%=CatalogWorker.getProdCatalog(pageContext).getString("styleSheet")%>" type="text/css">
    <%}%>
     
    <%-- Append CSS for tracking codes --%>
	<%if (session.getAttribute("overrideCss") != null) {%>
	<link rel='stylesheet' href="<%=session.getAttribute("overrideCss")%>" type="text/css">
    <%}%>

</head>
<body>
<region:render section='header'/>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
 <tr>
  <region:render section='leftbar'/>
  <td width='100%' valign=top align=left>
    <region:render section='error'/>
    <region:render section='content'/>
  </td>
  <region:render section='rightbar'/>
 </tr>
</table>

<region:render section='footer'/>

</body>
</html>
