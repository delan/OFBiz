
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%CategoryWorker.getRelatedCategories(pageContext, "topLevelList", UtilProperties.getPropertyValue(application.getResource("/WEB-INF/ecommerce.properties"), "catalog.id.default"));%>
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
        <%request.setAttribute("subcat", category);%>
        <%request.setAttribute("curcatid", curCategoryId);%>
        <%request.setAttribute("topPageContext", pageContext);%>
        <jsp:include page="/catalog/sidesubcategory.jsp" />        
      </ofbiz:iterator>
            </div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
