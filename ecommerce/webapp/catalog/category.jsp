<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<% pageContext.setAttribute("PageName", "categoryDisplay"); %> 
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%
	// Get a list of all available categories.
	org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedCategories(pageContext,request.getParameter("category_id"));
	// Get a list of all available products.
	org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedProducts(pageContext,request.getParameter("category_id"));
%>

<br><br>

<ofbiz:if name="categoryList">
  <hr>
  <b>Categories:</b>
  <hr>
  <br>
</ofbiz:if>

<center>
  <table>
    <ofbiz:iterator name="category" property="categoryList">
      <tr>
	    <td>
          <a href="<ofbiz:url>/category?category_id=<%= category.getString("productCategoryId") %></ofbiz:url>"><%= category.getString("description") %></a>
        </td>
	  </tr>
    </ofbiz:iterator>
  </table>
</center>

<ofbiz:if name="productList">
  <hr>
  <b>Products:</b>
  <hr>
  <br>
</ofbiz:if>

<center>
  <table>
    <ofbiz:iterator name="product" property="productList">
      <tr>
	    <td>
          <a href="<ofbiz:url>/product?product_id=<%= product.getString("productId") %></ofbiz:url>"><%= product.getString("name") %> : <%= product.getString("description") %></a>
        </td>
	  </tr>
    </ofbiz:iterator>
  </table>
</center>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

