<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>

<% pageContext.setAttribute("PageName", "categoryDisplay"); %> 
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %> 

<%-- Get a list of all products in the current category. --%>
<%CategoryWorker.getRelatedProductCategoryMembers(pageContext,"",request.getParameter("category_id"),true);%>
<br>
<%@ include file="/catalog/categorylisting.jsp" %>

<%@ include file="/includes/rightcolumn.jsp" %>
<%@ include file="/includes/footer.jsp" %>
