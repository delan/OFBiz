
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%CatalogHelper.getRelatedCategories(pageContext, "topLevelList", "CATALOG1");%>
<%String curCategoryId = UtilFormatOut.checkNull(request.getParameter("category_id"), request.getParameter("CATEGORY_ID"));%>
<%CatalogHelper.getRelatedCategories(pageContext, "curCategoryList", curCategoryId);%>

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
        <%if(curCategoryId.equals(category.getString("productCategoryId"))) {%>
          <tr><td align=left valign=top><div class='tabletext'><b>&nbsp;<%=category.getString("description")%></b></div></td></tr>
        <%}else{%>
          <tr><td align=left valign=top><a href="<ofbiz:url>/category?category_id=<%=category.getString("productCategoryId")%></ofbiz:url>" class='buttontext'>&nbsp;<%=category.getString("description")%></a></td></tr>
        <%}%>

        <%if(CatalogHelper.checkTrailItem(pageContext,category.getString("productCategoryId"))) {%>
          <%CatalogHelper.getRelatedCategories(pageContext,"subCatList",category.getString("productCategoryId"));%>
          <ofbiz:if name="subCatList">
            <ofbiz:iterator name="subcat" property="subCatList">
            <%if(curCategoryId.equals(subcat.getString("productCategoryId"))) {%>
              <tr><td align=left valign=top><div class='tabletext'><b>&nbsp;&nbsp;&nbsp;<%=subcat.getString("description")%></b></div></td></tr>
            <%}else{%>
              <tr><td align=left valign=top><a href="<ofbiz:url>/category?pcategory=<%=category.getString("productCategoryId")%>&category_id=<%=subcat.getString("productCategoryId")%></ofbiz:url>" class='buttontext'>&nbsp;&nbsp;&nbsp;<%=subcat.getString("description")%></a></td></tr>
            <%}%>
            </ofbiz:iterator>
          </ofbiz:if>
        <%}%>		
      </ofbiz:iterator>
    </table>
    </td>
  </tr>
</table>
