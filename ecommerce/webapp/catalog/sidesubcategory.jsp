
<%GenericValue category = (GenericValue)request.getAttribute("subcat");%>
<%String curcatid = (String)request.getAttribute("curcatid");%>
<%PageContext topPageContext = (PageContext)request.getAttribute("topPageContext");%>

  <%if(curCategoryId.equals(category.getString("productCategoryId"))) {%>
    <div class='tabletext'><b>&nbsp;<%=category.getString("description")%></b></div>
  <%}else{%>
    <a href="<ofbiz:url>/category?category_id=<%=category.getString("productCategoryId")%></ofbiz:url>" class='buttontext'>&nbsp;<%=category.getString("description")%></a>
  <%}%>

  <%if(CatalogHelper.checkTrailItem(topPageContext,category.getString("productCategoryId"))) {%>
    <%CatalogHelper.getRelatedCategories(pageContext,"subCatList",category.getString("productCategoryId"));%>
    <ofbiz:if name="subCatList">
      <ofbiz:iterator name="subcat" property="subCatList">
      <%if(curCategoryId.equals(subcat.getString("productCategoryId"))) {%>
        <tr><td align=left valign=top><div class='tabletext'><b>&nbsp;&nbsp;&nbsp;<%=subcat.getString("description")%></b></div></td></tr>
      <%}else{%>
        <tr><td align=left valign=top><a href="<ofbiz:url>/category?pcategory=<%=category.getString("productCategoryId")%>&category_id=<%=subcat.getString("productCategoryId")%></ofbiz:url>" class='buttontext'>&nbsp;&nbsp;&nbsp;<%=subcat.getString("description")%></a></td></tr>
      <%}%>
      </ofbiz:iterator>
    </ofbiz:if>
  <%}%>
