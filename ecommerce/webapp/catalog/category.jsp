<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<% pageContext.setAttribute("PageName", "categoryDisplay"); %> 
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %> 

<%
  // Get a list of all available categories.
  //org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedCategories(pageContext,"curCategoryList",request.getParameter("category_id"));
  // Get a list of all available products.
  org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedProducts(pageContext,"",request.getParameter("category_id"));
%>
<br>
<%@ include file="/catalog/categorylisting.jsp" %>

<%@ include file="/includes/rightcolumn.jsp" %>
<%@ include file="/includes/footer.jsp" %>

