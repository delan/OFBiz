
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedCategories(pageContext,"topLevelList","CATALOG1");%>
<%org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedCategories(pageContext,"curCategoryList");%>

<table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td align=center valign=center bgcolor='#678475' width='100%'>
      <div class="boxhead">Browse&nbsp;Catalog</div>
    </td>
  </tr>
  <tr>
    <td align=left valign=top bgcolor='white' width='100%'>
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <ofbiz:iterator name="category" property="topLevelList">
        <tr><td align=left valign=top><a href="<ofbiz:url>/category?category_id=<%=category.getString("productCategoryId")%></ofbiz:url>" class='buttontext'>&nbsp;<%=category.getString("description")%></a></td></tr>
		
		<% if ( request.getParameter("category_id") != null && request.getParameter("category_id").equals(category.getString("productCategoryId")) ) { %>
        <% org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedCategories(pageContext,"subCatList",category.getString("productCategoryId")); %>
        <ofbiz:if name="subCatList">
          <ofbiz:iterator name="subcat" property="subCatList">
            <tr><td align=left valign=top><a href="<ofbiz:url>/category?pcategory=<%=category.getString("productCategoryId")%>&category_id=<%=subcat.getString("productCategoryId")%></ofbiz:url>" class='buttontext'>&nbsp;&nbsp;&nbsp;<%=subcat.getString("description")%></a></td></tr>
          </ofbiz:iterator>
		</ofbiz:if>
		<% } %>
		
      </ofbiz:iterator>
    </table>
    </td>
  </tr>
</table>
