
<% pageContext.setAttribute("PageName", "Main Page"); %> 
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %> 
<%@ page import="org.ofbiz.commonapp.product.category.*" %>

<%-- Get a list of all available products. --%>
<%CategoryWorker.getRelatedProducts(pageContext,"","PROMOTIONS");%>

<%-- Main Heading --%>
<br>
<%@ include file="/catalog/categorylisting.jsp" %>

<%@ include file="/includes/rightcolumn.jsp" %>
<%@ include file="/includes/footer.jsp" %>
