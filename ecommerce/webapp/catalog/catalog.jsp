<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<% pageContext.setAttribute("PageName", "catalog"); %>
<% pageContext.setAttribute("catList",CatalogHelper.getRelatedCategories(request.getParameter("catalog_id"))); %>

<br><br>

<center>
  <table>
    <ofbiz:iterator name="category" property="catList">
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

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
