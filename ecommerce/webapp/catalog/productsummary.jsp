<ofbiz:if name="product">
  <table border="0" width='100%' cellpadding='0' cellspacing='0'>
    <tr>
      <td valign="top">
        <%GenericValue localProduct = (GenericValue) pageContext.getAttribute("product");%>
        <%if (request.getParameter("category_id") != null) pageContext.setAttribute("category_id", request.getParameter("category_id"));%>
        <%String smallImageUrl = localProduct.getString("smallImageUrl");%>
        <%if(smallImageUrl == null || smallImageUrl.length() <= 0) smallImageUrl = "/images/defaultImage.jpg";%>
          <a href='<ofbiz:url>/product?<ofbiz:if name="category_id">category_id=<ofbiz:print attribute="category_id"/>&</ofbiz:if>product_id=<%EntityField.run("product", "productId", pageContext);%></ofbiz:url>'>
            <img src='<ofbiz:contenturl><%=smallImageUrl%></ofbiz:contenturl>' align="left" height="50" class='imageborder' border='0'>
          </a>
      </td>
      <td align="left" valign="top" width="100%">
          <div class="tabletext">
            <a href='<ofbiz:url>/product?<ofbiz:if name="category_id">category_id=<ofbiz:print attribute="category_id"/>&</ofbiz:if>product_id=<%EntityField.run("product", "productId", pageContext);%></ofbiz:url>' class='buttontext'><%EntityField.run("product", "productName", pageContext);%></a>
          </div>
          <div class="tabletext"><%EntityField.run("product", "description", pageContext);%></div>
          <div class="tabletext">
            <nobr>
              <b><%EntityField.run("product", "productId", pageContext);%></b>,
              <b><font color="#006633"><%EntityField.run("product", "defaultPrice", pageContext);%></font>,</b>
              Reg. <%EntityField.run("product", "listPrice", pageContext);%>
            </nobr>
          </div>
      </td>
      <td valign=center align=right>
          <%if (localProduct.get("introductionDate") != null && UtilDateTime.nowTimestamp().before(localProduct.getTimestamp("introductionDate"))) {%>
              <%-- check to see if introductionDate hasn't passed yet --%>
              <div class='tabletext' style='color: red;'>Not Yet Available</div>
          <%} else if (localProduct.get("salesDiscontinuationDate") != null && UtilDateTime.nowTimestamp().after(localProduct.getTimestamp("salesDiscontinuationDate"))) {%>
              <%-- check to see if salesDiscontinuationDate has passed --%>
              <div class='tabletext' style='color: red;'>No Longer Available</div>
          <%} else if ("Y".equals(localProduct.getString("isVirtual"))) {%>
              <%-- check to see if the product is a virtual product --%>
              <%--<div class='tabletext' style='color: red;'>Virtual Product</div>--%>
              <a href='<ofbiz:url>/product?<ofbiz:if name="category_id">category_id=<ofbiz:print attribute="category_id"/>&</ofbiz:if>product_id=<%EntityField.run("product", "productId", pageContext);%></ofbiz:url>' class="buttontext"><nobr>[Add to Cart]</nobr></a>
          <%} else {%>
                <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="the<%=UtilFormatOut.formatQuantity(listIndex)%>form" style='margin: 0;'>
                  <input type='hidden' name='add_product_id' value='<%EntityField.run("product", "productId", pageContext);%>'>
                  <input type="text" size="5" name="quantity" value="1">
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("product_id"), "<input type='hidden' name='product_id' value='", "'>")%>
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("VIEW_INDEX"), "<input type='hidden' name='VIEW_INDEX' value='", "'>")%>
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_STRING"), "<input type='hidden' name='SEARCH_STRING' value='", "'>")%>
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_CATEGORY_ID"), "<input type='hidden' name='SEARCH_CATEGORY_ID' value='", "'>")%>
                  <br><a href="javascript:document.the<%=UtilFormatOut.formatQuantity(listIndex)%>form.submit()" class="buttontext"><nobr>[Add to Cart]</nobr></a>
                </form>
                <ofbiz:if name="productCategoryMember">
                    <%GenericValue prodCatMemberTemp = (GenericValue) pageContext.getAttribute("productCategoryMember");%>
                    <%if (prodCatMemberTemp != null && prodCatMemberTemp.get("quantity") != null && prodCatMemberTemp.getDouble("quantity").doubleValue() > 0.0) {%>
                        <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="the<%=UtilFormatOut.formatQuantity(listIndex)%>defaultform" style='margin: 0;'>
                          <input type='hidden' name='add_product_id' value='<%EntityField.run("productCategoryMember", "productId", pageContext);%>'>
                          <input type='hidden' name="quantity" value='<%EntityField.run("productCategoryMember", "quantity", pageContext);%>'>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("product_id"), "<input type='hidden' name='product_id' value='", "'>")%>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("VIEW_INDEX"), "<input type='hidden' name='VIEW_INDEX' value='", "'>")%>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_STRING"), "<input type='hidden' name='SEARCH_STRING' value='", "'>")%>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_CATEGORY_ID"), "<input type='hidden' name='SEARCH_CATEGORY_ID' value='", "'>")%>
                          <a href="javascript:document.the<%=UtilFormatOut.formatQuantity(listIndex)%>defaultform.submit()" class="buttontext"><nobr>[Add Default(<%EntityField.run("productCategoryMember", "quantity", pageContext);%>) to Cart]</nobr></a>
                        </form>
                    <%}%>
                </ofbiz:if>
          <%}%>
      </td>
    </tr>
  </table>
</ofbiz:if>

<ofbiz:unless name="product">
&nbsp;ERROR: Product not found.<br>
</ofbiz:unless>
