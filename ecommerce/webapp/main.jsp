<% pageContext.setAttribute("PageName", "main"); %> 

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<br>

<p><a href="<%=response.encodeURL(controlPath + "/catalog?catalog_id=CATALOG1")%>">Click Here For Catalog</a></p>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
