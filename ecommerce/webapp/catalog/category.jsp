<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>

<%-- Get a list of all products in the current category. --%>
<%CategoryWorker.getRelatedProductCategoryMembers(pageContext,"",request.getParameter("category_id"),true,10);%>
<br>
<%@ include file="/catalog/categorylisting.jsp" %>
