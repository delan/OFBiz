
<%@taglib uri="ofbizTags" prefix="ofbiz" %>

<%@page import="org.ofbiz.core.entity.*" %>
<%@page import="org.ofbiz.commonapp.product.category.*" %>

<%GenericValue category = (GenericValue)request.getAttribute("subcat");%>
<%GenericValue pcategory = (GenericValue)request.getAttribute("category");%>
<%String curcatid = (String)request.getAttribute("curcatid");%>
<%PageContext topPageContext = (PageContext)request.getAttribute("topPageContext");%>

  <%if(curcatid != null && curcatid.equals(category.getString("productCategoryId"))) {%>
    <div class='tabletext' style='text-indent: -10px;'><b>-&nbsp;<%=category.getString("description")%> (<%=category.getString("productCategoryId")%>)</b></div>
  <%}else{%>
    <%String pstr = "";%><%if(pcategory != null) pstr = "&pcategory=" + pcategory.getString("productCategoryId");%>
    <div style='text-indent: -10px;'><a href="<ofbiz:url>/EditCategory?PRODUCT_CATEGORY_ID=<%=category.getString("productCategoryId")%><%=pstr%></ofbiz:url>" class='buttontext'>-&nbsp;<%=category.getString("description")%>  (<%=category.getString("productCategoryId")%>)</a></div>
  <%}%>

  <%if(CategoryWorker.checkTrailItem(topPageContext,category.getString("productCategoryId")) || (curcatid != null && curcatid.equals(category.getString("productCategoryId")))) {%>
    <%CategoryWorker.getRelatedCategories(pageContext,"subCatList",category.getString("productCategoryId"));%>
    <ofbiz:if name="subCatList">
      <ofbiz:iterator name="subcat" property="subCatList">
        <div style='margin-left: 10px;'>
          <%request.setAttribute("subcat", subcat);%>
          <%request.setAttribute("category", category);%>
          <%request.setAttribute("curcatid", curcatid);%>
          <%request.setAttribute("topPageContext", topPageContext);%>
          <jsp:include page="/find/sidesubcategory.jsp" />
        </div>
      </ofbiz:iterator>
    </ofbiz:if>
  <%}%>
