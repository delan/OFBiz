
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.commonapp.product.category.*, org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%CategoryWorker.getRelatedCategories(pageContext, "topLevelList", CatalogWorker.getCatalogTopCategoryId(pageContext, CatalogWorker.getCurrentCatalogId(pageContext)));%>
<%String curCategoryId = UtilFormatOut.checkNull(request.getParameter("category_id"), request.getParameter("CATEGORY_ID"));%>
<%CategoryWorker.setTrail(pageContext, curCategoryId);%>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign=middle align=center>
      <div class="boxhead">Browse&nbsp;Categories</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
            <div style='margin-left: 10px;'>
      <ofbiz:iterator name="category" property="topLevelList">
        <%printSubCategories(null, category, curCategoryId, pageContext);%>
      </ofbiz:iterator>
            </div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<%!
public static void printSubCategories(GenericValue pcategory, GenericValue category, String curcatid, PageContext pageContext) throws java.io.IOException {
    String controlPath = (String) pageContext.getRequest().getAttribute(SiteDefs.CONTROL_PATH);
    JspWriter out = pageContext.getOut();

    if (curcatid != null && curcatid.equals(category.getString("productCategoryId"))) {
        out.print("<div class='tabletext' style='text-indent: -10px;'><b>-&nbsp;");
        out.print(category.getString("description"));
        out.print("</b></div>");
    } else {
        String pstr = "";
        if (pcategory != null) pstr = "&pcategory=" + pcategory.getString("productCategoryId");
        out.print("<div style='text-indent: -10px;'><a href='");
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        out.print(response.encodeURL(controlPath + "/category?category_id=" + category.getString("productCategoryId") + pstr));
        out.print("' class='buttontext'>-&nbsp;");
        out.print(category.getString("description"));
        out.println("</a></div>");
    }

    if (CategoryWorker.checkTrailItem(pageContext, category.getString("productCategoryId")) || 
            (curcatid != null && curcatid.equals(category.getString("productCategoryId")))) {
        List subCatList = CategoryWorker.getRelatedCategoriesRet(pageContext, "subCatList", category.getString("productCategoryId"));
        if (subCatList != null && subCatList.size() > 0) {
            Iterator iter = subCatList.iterator();
            while (iter.hasNext()) {
                GenericValue subcat = (GenericValue) iter.next();
                out.println("<div style='margin-left: 10px;'>");
                printSubCategories(category, subcat, curcatid, pageContext);
                out.println("</div>");
            }
        }
    }
}
%>
