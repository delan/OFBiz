
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
        <%request.setAttribute("subcat", category);%>
        <%request.setAttribute("curcatid", curCategoryId);%>
        <%request.setAttribute("topPageContext", pageContext);%>
        <jsp:include page="/sidesubcategory.jsp">        
      </ofbiz:iterator>
    </table>
    </td>
  </tr>
</table>
