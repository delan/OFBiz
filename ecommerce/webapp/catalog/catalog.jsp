<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<% pageContext.setAttribute("PageName", "catalog"); %>
<%
	// Get a list of all available categories
	org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedCategories(pageContext,request.getParameter("catalog_id"));
%>

<br><br>

<ofbiz:unless name="categoryList">
  <center><h2>No categories found for this catalog ID.</h2><br></center>
</ofbiz:unless>
  
<ofbiz:if name="categoryList">
  <center>
    <table>
      <ofbiz:iterator name="category" property="categoryList">
        <tr>
          <td>
            <a href="<ofbiz:url>/category?category_id=<%= category.getString("productCategoryId") %></ofbiz:url>"><%= category.getString("description") %></a>
          </td>
          <ofbiz:iterateNext>
            <td>
              <a href="<ofbiz:url>/category?category_id=<%= next.getString("productCategoryId") %></ofbiz:url>"><%= next.getString("description") %></a>
            </td>
          </ofbiz:iterateNext>		
        </tr>
      </ofbiz:iterator>
    </table>
  </center>
</ofbiz:if>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
