
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%String defaultTopCategoryId = request.getParameter("TOP_CATEGORY") != null ? 
                                request.getParameter("TOP_CATEGORY") : 
                                UtilProperties.getPropertyValue(application.getResource("/WEB-INF/catalog.properties"), "top.category.default");%>
<%String currentTopCategoryId = CategoryWorker.getCatalogTopCategory(pageContext, defaultTopCategoryId);%>
<%CategoryWorker.getRelatedCategories(pageContext, "topLevelList", currentTopCategoryId, false);%>
<%GenericValue currentTopCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", currentTopCategoryId));%>
<%String curCategoryId = UtilFormatOut.checkNull(request.getParameter("productCategoryId"));%>
<%CategoryWorker.setTrail(pageContext, curCategoryId);%>
<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
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
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <div><a href='<ofbiz:url>/ChooseTopCategory</ofbiz:url>' class='buttontext'>Choose Top Category</a></div>
            <div style='margin-left: 10px;'>
            <%if(currentTopCategory != null) {%>
              <%if(curCategoryId != null && curCategoryId.equals(currentTopCategory.getString("productCategoryId"))) {%>
                <div class='tabletext' style='text-indent: -10px;'><b>-&nbsp;<%=currentTopCategory.getString("description")%> [<%=currentTopCategory.getString("productCategoryId")%>]</b></div>
              <%}else{%>
                <div style='text-indent: -10px;'><a href="<ofbiz:url>/EditCategory?productCategoryId=<%=currentTopCategory.getString("productCategoryId")%></ofbiz:url>" class='buttontext'>-&nbsp;<%=currentTopCategory.getString("description")%>  [<%=currentTopCategory.getString("productCategoryId")%>]</a></div>
              <%}%>
            <%}%>
              <div style='margin-left: 10px;'>
                <ofbiz:iterator name="category" property="topLevelList">
                  <%printSubCategories(null, category, curCategoryId, pageContext);%>
                </ofbiz:iterator>
              </div>
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
        out.print(" [");
        out.print(category.getString("productCategoryId"));
        out.print("]");
        out.print("</b></div>");
    } else {
        String pstr = "";
        if (pcategory != null) pstr = "&pcategory=" + pcategory.getString("productCategoryId");
        out.print("<div style='text-indent: -10px;'><a href='");
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        out.print(response.encodeURL(controlPath + "/EditCategory?productCategoryId=" + category.getString("productCategoryId") + pstr));
        out.print("' class='buttontext'>-&nbsp;");
        out.print(category.getString("description"));
        out.print(" [");
        out.print(category.getString("productCategoryId"));
        out.print("]");
        out.println("</a></div>");
    }

    if (CategoryWorker.checkTrailItem(pageContext, category.getString("productCategoryId")) || 
            (curcatid != null && curcatid.equals(category.getString("productCategoryId")))) {
        List subCatList = CategoryWorker.getRelatedCategoriesRet(pageContext, "subCatList", category.getString("productCategoryId"), false);
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
