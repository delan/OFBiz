
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.commonapp.product.category.*, org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%CategoryWorker.getRelatedCategories(pageContext, "topLevelList", CatalogWorker.getCatalogTopCategoryId(pageContext, CatalogWorker.getCurrentCatalogId(pageContext)));%>
<%String curCategoryId = UtilFormatOut.checkNull(request.getParameter("category_id"), request.getParameter("CATEGORY_ID"));%>
<%CategoryWorker.setTrail(pageContext, curCategoryId);%>
<BR>
<TABLE border=0 width='100%' cellpadding='<%EntityField.run("layoutSettings", "boxBorderWidth", pageContext);%>' cellspacing=0 bgcolor='<%EntityField.run("layoutSettings", "boxBorderColor", pageContext);%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%EntityField.run("layoutSettings", "boxTopPadding", pageContext);%>' cellspacing='0' bgcolor='<%EntityField.run("layoutSettings", "boxTopColor", pageContext);%>'>
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
      <table width='100%' border='0' cellpadding='<%EntityField.run("layoutSettings", "boxBottomPadding", pageContext);%>' cellspacing='0' bgcolor='<%EntityField.run("layoutSettings", "boxBottomColor", pageContext);%>'>
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
