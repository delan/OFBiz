
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%CatalogHelper.getRelatedCategories(pageContext, "topLevelList", UtilProperties.getPropertyValue("ecommerce", "catalog.id.default"));%>
<%String curCategoryId = UtilFormatOut.checkNull(request.getParameter("category_id"), request.getParameter("CATEGORY_ID"));%>
<%CatalogHelper.getRelatedCategories(pageContext, "curCategoryList", curCategoryId);%>

<TABLE border=0 width='100%' cellpadding=1 cellspacing=0 bgcolor='black'>
  <TR>
    <TD width='100%'>
      <table width="100%" border="0" cellpadding="4" cellspacing="0" bgcolor="#678475">
        <tr>
          <td valign=middle align=center>
      <div class="boxhead">Browse&nbsp;Catalog</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border=0 cellpadding=4 cellspacing=0 bgcolor='white'>
        <tr>
          <td>
          <ul style='margin-left: 15;'>
      <ofbiz:iterator name="category" property="topLevelList">
        <%if(curCategoryId.equals(category.getString("productCategoryId"))) {%>
          <li style='margin: 1;'><div class='tabletext'><b><%=category.getString("description")%></b></div></li>
        <%}else{%>
          <li style='margin: 1;'><a href="<ofbiz:url>/category?category_id=<%=category.getString("productCategoryId")%></ofbiz:url>" class='buttontext'><%=category.getString("description")%></a></li>
        <%}%>

        <%if(CatalogHelper.checkTrailItem(pageContext,category.getString("productCategoryId"))) {%>
          <%CatalogHelper.getRelatedCategories(pageContext,"subCatList",category.getString("productCategoryId"));%>
          <ofbiz:if name="subCatList">
            <ul style='margin-left: 10;'>
            <ofbiz:iterator name="subcat" property="subCatList">
            <%if(curCategoryId.equals(subcat.getString("productCategoryId"))) {%>
              <li style='margin: 1;'><div class='tabletext'><b><%=subcat.getString("description")%></b></div></li>
            <%}else{%>
              <li style='margin: 1;'><a href="<ofbiz:url>/category?pcategory=<%=category.getString("productCategoryId")%>&category_id=<%=subcat.getString("productCategoryId")%></ofbiz:url>" class='buttontext'><%=subcat.getString("description")%></a></li>
            <%}%>
            </ofbiz:iterator>
            </ul>
          </ofbiz:if>
        <%}%>		
      </ofbiz:iterator>
          </ul>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
