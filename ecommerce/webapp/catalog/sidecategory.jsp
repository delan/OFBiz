
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%org.ofbiz.ecommerce.catalog.CatalogHelper.getRelatedCategories(pageContext,"CATALOG1");%>

<table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td align=center valign=center bgcolor='#678475' width='100%'>
      <div class="boxhead">Product&nbsp;Categories</div>
    </td>
  </tr>
  <tr>
    <td align=left valign=top bgcolor='white' width='100%'>
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
      <ofbiz:iterator name="category" property="categoryList">
        <tr><td align=left valign=top>
            <a href="<ofbiz:url>/category?category_id=<%=category.getString("productCategoryId")%></ofbiz:url>" class='buttontext'>&nbsp;<%=category.getString("description")%></a>
        </td></tr>
      </ofbiz:iterator>
    </table>
    </td>
  </tr>
</table>
