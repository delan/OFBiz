<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<% pageContext.setAttribute("PageName", "category"); %> 
<% pageContext.setAttribute("catList",CatalogHelper.getRelatedCategories(request.getParameter("category_id"))); %>
<% pageContext.setAttribute("prdList",CatalogHelper.getRelatedProducts(request.getParameter("category_id"))); %>

<br><br>

<center>
  <table>
    <ofbiz:iterator name="category" property="catList">
      <tr>
	    <td>
          <a href="<ofbiz:url>/category?category_id=<%= category.getString("productCategoryId") %></ofbiz:url>"><%= category.getString("description") %></a>
        </td>
	  </tr>
    </ofbiz:iterator>
  </table>
</center>

<hr>

<center>
  <table>
    <ofbiz:iterator name="product" property="prdList">
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

