
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.commonapp.product.category.*, org.ofbiz.core.util.*" %>
<%String defaultTopCategoryId = request.getParameter("TOP_CATEGORY") != null ? 
                                request.getParameter("TOP_CATEGORY") : 
                                UtilProperties.getPropertyValue(application.getResource("/WEB-INF/catalog.properties"), "top.category.default");%>
<%String currentTopCategoryId = CategoryWorker.getCatalogTopCategory(pageContext, defaultTopCategoryId);%>
<%CategoryWorker.getRelatedCategories(pageContext, "topLevelList", currentTopCategoryId);%>
<%GenericValue currentTopCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", currentTopCategoryId));%>
<%String curCategoryId = UtilFormatOut.checkNull(request.getParameter("PRODUCT_CATEGORY_ID"));%>
<%CategoryWorker.setTrail(pageContext, curCategoryId);%>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
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
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
            <div><a href='<ofbiz:url>/ChooseTopCategory</ofbiz:url>' class='buttontext'>Choose Top Category</a></div>
            <div style='margin-left: 10px;'>
            <%if(currentTopCategory != null) {%>
              <%if(curCategoryId != null && curCategoryId.equals(currentTopCategory.getString("productCategoryId"))) {%>
                <div class='tabletext' style='text-indent: -10px;'><b>-&nbsp;<%=currentTopCategory.getString("description")%> [<%=currentTopCategory.getString("productCategoryId")%>]</b></div>
              <%}else{%>
                <div style='text-indent: -10px;'><a href="<ofbiz:url>/EditCategory?PRODUCT_CATEGORY_ID=<%=currentTopCategory.getString("productCategoryId")%></ofbiz:url>" class='buttontext'>-&nbsp;<%=currentTopCategory.getString("description")%>  [<%=currentTopCategory.getString("productCategoryId")%>]</a></div>
              <%}%>
            <%}%>
              <div style='margin-left: 10px;'>
                <ofbiz:iterator name="category" property="topLevelList">
                  <%request.setAttribute("subcat", category);%>
                  <%request.setAttribute("curcatid", curCategoryId);%>
                  <%request.setAttribute("topPageContext", pageContext);%>
                  <jsp:include page="/find/sidesubcategory.jsp" />        
                </ofbiz:iterator>
              </div>
            </div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
