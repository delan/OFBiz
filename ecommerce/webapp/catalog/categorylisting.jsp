<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<ofbiz:object name="viewIndex" property="viewIndex" type='java.lang.Integer' />
<ofbiz:object name="viewSize" property="viewSize" type='java.lang.Integer' />
<ofbiz:object name="lowIndex" property="lowIndex" type='java.lang.Integer' />
<ofbiz:object name="highIndex" property="highIndex" type='java.lang.Integer' />
<ofbiz:object name="listSize" property="listSize" type='java.lang.Integer' />
<ofbiz:object name="categoryId" property="categoryId" type='java.lang.String' />

<%GenericValue category = null;
  try { category = delegator.findByPrimaryKeyCache("ProductCategory",UtilMisc.toMap("productCategoryId",categoryId)); }
  catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); category = null; }
  if(category != null) pageContext.setAttribute("listingCategory", category);%>
<ofbiz:if name="listingCategory">
<table border="0" width="100%" cellpadding="3">
<tr>
  <td colspan="2">
    <div class="head1">
    <%=UtilFormatOut.checkNull(category.getString("description"))%>
    </div>
  </td>
</tr>
<%String categoryImageUrl = category.getString("categoryImageUrl");%>
<%String categoryLongDescription = category.getString("longDescription");%>
<%if (UtilValidate.isNotEmpty(categoryImageUrl) || UtilValidate.isNotEmpty(categoryLongDescription)) pageContext.setAttribute("showCategoryDetails", "true");%>
<ofbiz:if name="showCategoryDetails">
<tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>
<tr>
  <td align="left" valign="top" width="0">
    <div class="tabletext">
    <% if(UtilValidate.isNotEmpty(categoryImageUrl)) {%>
      <img src="<%=categoryImageUrl%>" vspace="5" hspace="5" border="1" height='100' align=left>
    <% } %>
    <%=UtilFormatOut.checkNull(categoryLongDescription)%>
    </div>
  </td>
</tr>
</ofbiz:if>
</table>
</ofbiz:if>

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

<ofbiz:if name="productList" size="0">
<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <%=lowIndex%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="buttontext">[Next]</a>
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
      <tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>
      <tr>
        <td>
          <%@ include file="/catalog/productsummary.jsp" %>
        </td>
      </tr>
      <%listIndex++;%>
    </ofbiz:iterator>
  </table>
</center>

<ofbiz:if name="productList" size="0">
<table border="0" width="100%" cellpadding="2">
    <tr><td colspan="2"><div style='height: 1; background-color: #999999;'></div></td></tr>
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <%=lowIndex%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>
</ofbiz:if>

<ofbiz:unless name="productList" size="0">
<table border="0" width="100%" cellpadding="2">
    <tr><td colspan="2"><div style='height: 1; background-color: #999999;'></div></td></tr>
    <tr>
      <td>
          <DIV class='tabletext'>There are no products in this category.</DIV>
      </td>
    </tr>
</table>
</ofbiz:unless>