<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<% pageContext.setAttribute("PageName", "categoryDisplay"); %> 
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %> 

<%
  // Get a list of all available categories.
  org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedCategories(pageContext,"curCategoryList",request.getParameter("category_id"));
  // Get a list of all available products.
  org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedProducts(pageContext,"productList",request.getParameter("category_id"));
%>

<br>

<ofbiz:if name="curCategoryList">
  <hr>
  <b>Categories:</b>
  <hr>
  <br>
</ofbiz:if>

<center>
  <table>
    <ofbiz:iterator name="category" property="curCategoryList">
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
    <%int listIndex = 1;%>
    <ofbiz:iterator name="product" property="productList">
      <tr>
        <td>
          <%@ include file="/catalog/productsummary.jsp" %>
        </td>
      </tr>
      <%listIndex++;%>
    </ofbiz:iterator>
  </table>
</center>

<center>
<%

java.util.Collection co = org.ofbiz.ecommerce.catalog.CatalogHelper.getTrail(pageContext); 
java.util.Iterator it = co.iterator();
while ( it.hasNext() ) {
	String s = (String) it.next();
%>
<p><%= s %><br></p>

<% } %>


<%@ include file="/includes/rightcolumn.jsp" %>
<%@ include file="/includes/footer.jsp" %>

