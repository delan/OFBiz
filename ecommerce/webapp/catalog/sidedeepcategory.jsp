
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.commonapp.product.category.*, org.ofbiz.commonapp.product.catalog.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%CategoryWorker.getRelatedCategories(pageContext, "topLevelList", CatalogWorker.getCatalogTopCategoryId(pageContext, CatalogWorker.getCurrentCatalogId(pageContext)), true);%>
<%String curCategoryId = UtilFormatOut.checkNull(request.getParameter("category_id"), request.getParameter("CATEGORY_ID"));%>
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
            <div style='margin-left: 10px;'>
              <ofbiz:iterator name="category" property="topLevelList">
                <%CatalogWorker.printSubCategories(null, category, curCategoryId, pageContext);%>
              </ofbiz:iterator>
            </div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
