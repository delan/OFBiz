<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*"%>

<% pageContext.setAttribute("PageName", "product"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%
	// Get the value object of the request product id.
	org.ofbiz.ecommerce.catalog.CatalogHelper.getProduct(pageContext,request.getParameter("product_id"));
%>

<ofbiz:unless name="productValue">
  <center><h2>Product Not Found!</h2></center>
</ofbiz:unless>
<ofbiz:if name="productValue">
  <br><br>
  <ofbiz:object name="product" property="productValue">
    <center>
      <table border=1>
        <tr>
          <td>ProductID:</td>
          <td><%= product.getString("productId") %></td>
        </tr>
        <tr>
          <td>Name:</td>
          <td><%= product.getString("name") %></td>
        </tr>
        <tr>
          <td>Description:</td>
          <td><%= product.getString("description") %></td>
        </tr>
        <tr>
          <td>Price:</td>
          <td><ofbiz:format type="c"><%= product.getDouble("defaultPrice") %></ofbiz:format></td>
        </tr>		
        <tr>
          <td colspan="2" align="center">
            <form method="post" action="<ofbiz:url>/additem</ofbiz:url>">
              <input type="hidden" name="product_id" value="<%= product.getString("productId") %>">
              <input type="hidden" name="quantity" value="1">
              <input type="submit" value="Add To Cart">
            </form>
          </td>
        </tr>
      </table>
    </center>
  </ofbiz:object>
</ofbiz:if>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
