
<% pageContext.setAttribute("PageName", "Main Page"); %> 
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %> 
<%@ page import="org.ofbiz.commonapp.product.category.*" %>

<%-- Get a list of all available products in the promotions category for the current catalog. --%>
<%CategoryWorker.getRelatedProductCategoryMembers(pageContext,"",CatalogWorker.getCatalogPromotionsCategoryId(pageContext, CatalogWorker.getCurrentCatalogId(pageContext)),true);%>

<%-- Main Heading --%>
<br>
<%@ include file="/catalog/categorylisting.jsp" %>

<%@ include file="/includes/rightcolumn.jsp" %>
<%@ include file="/includes/footer.jsp" %>
