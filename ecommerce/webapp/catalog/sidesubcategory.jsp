
<%@taglib uri="ofbizTags" prefix="ofbiz" %>

<%@page import="org.ofbiz.core.entity.*" %>
<%@page import="org.ofbiz.ecommerce.catalog.*" %>

<%GenericValue category = (GenericValue)request.getAttribute("subcat");%>
<%GenericValue pcategory = (GenericValue)request.getAttribute("category");%>
<%String curcatid = (String)request.getAttribute("curcatid");%>
<%PageContext topPageContext = (PageContext)request.getAttribute("topPageContext");%>

  <%if(curcatid != null && curcatid.equals(category.getString("productCategoryId"))) {%>
    <div class='tabletext' style='text-indent: -10px;'><b>-&nbsp;<%=category.getString("description")%></b></div>
  <%}else{%>
    <%String pstr = "";%><%if(pcategory != null) pstr = "&pcategory=" + pcategory.getString("productCategoryId");%>
    <div style='text-indent: -10px;'><a href="<ofbiz:url>/category?category_id=<%=category.getString("productCategoryId")%><%=pstr%></ofbiz:url>" class='buttontext'>-&nbsp;<%=category.getString("description")%></a></div>
  <%}%>

  <%if(CatalogHelper.checkTrailItem(topPageContext,category.getString("productCategoryId"))) {%>
    <%CatalogHelper.getRelatedCategories(pageContext,"subCatList",category.getString("productCategoryId"));%>
    <ofbiz:if name="subCatList">
      <ofbiz:iterator name="subcat" property="subCatList">
        <div style='margin-left: 10px;'>
          <%request.setAttribute("subcat", subcat);%>
          <%request.setAttribute("category", category);%>
          <%request.setAttribute("curcatid", curcatid);%>
          <%request.setAttribute("topPageContext", topPageContext);%>
          <jsp:include page="/catalog/sidesubcategory.jsp" />
        </div>
      </ofbiz:iterator>
    </ofbiz:if>
  <%}%>
