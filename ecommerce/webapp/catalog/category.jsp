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
<ofbiz:object name="viewIndex" property="viewIndex" type='java.lang.Integer' />
<ofbiz:object name="viewSize" property="viewSize" type='java.lang.Integer' />
<ofbiz:object name="lowIndex" property="lowIndex" type='java.lang.Integer' />
<ofbiz:object name="highIndex" property="highIndex" type='java.lang.Integer' />
<ofbiz:object name="listSize" property="listSize" type='java.lang.Integer' />
<ofbiz:object name="categoryId" property="categoryId" type='java.lang.String' />

<br>
<%--
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
--%>

<ofbiz:if name="productList">
<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="button" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <%=lowIndex%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="button" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>
</ofbiz:if>

<center>
  <table width='100%'>
    <%int listIndex = lowIndex.intValue();%>
    <ofbiz:iterator name="product" property="productList">
      <tr><td height="1" bgcolor="#999999"></td></tr>
      <tr>
        <td>
          <%@ include file="/catalog/productsummary.jsp" %>
        </td>
      </tr>
      <%listIndex++;%>
    </ofbiz:iterator>
  </table>
</center>

<ofbiz:if name="productList">
<table border="0" width="100%" cellpadding="2">
    <tr><td colspan="2" height="1" bgcolor="#999999"></td></tr>
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="button" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <%=lowIndex%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="button" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>
</ofbiz:if>

<!--
<%
java.util.Collection co = org.ofbiz.ecommerce.catalog.CatalogHelper.getTrail(pageContext); 
java.util.Iterator it = co.iterator();
while ( it.hasNext() ) {
	String s = (String) it.next();
%>
<p><%= s %><br></p>

<% } %>
-->
<%@ include file="/includes/rightcolumn.jsp" %>
<%@ include file="/includes/footer.jsp" %>

