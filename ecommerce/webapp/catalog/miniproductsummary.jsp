
<ofbiz:if name="miniProduct">
  <a href='<ofbiz:url>/product?product_id=<%entityField.run("miniProduct", "productId");%></ofbiz:url>' class='buttontext'>
    <%entityField.run("miniProduct", "productName");%>
  </a>
  <div class='tabletext'>
    <b>
      <%entityField.run("miniProduct", "productId");%>,
      <font color="#006633"><%entityField.run("miniProduct", "defaultPrice");%></font>
    </b>
  </div>
  <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="<%=miniProdFormName%>" style='margin: 0;'>
    <input type='hidden' name="add_product_id" value='<%entityField.run("miniProduct", "productId");%>'>
    <input type='hidden' name="quantity" value="<%=miniProdQuantity%>">
    <%=UtilFormatOut.ifNotEmpty(request.getParameter("order_id"), "<input type='hidden' name='order_id' value='", "'>")%>
    <%=UtilFormatOut.ifNotEmpty(request.getParameter("product_id"), "<input type='hidden' name='product_id' value='", "'>")%>
    <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
    <%=UtilFormatOut.ifNotEmpty(request.getParameter("VIEW_INDEX"), "<input type='hidden' name='VIEW_INDEX' value='", "'>")%>
    <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_STRING"), "<input type='hidden' name='SEARCH_STRING' value='", "'>")%>
    <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_CATEGORY_ID"), "<input type='hidden' name='SEARCH_CATEGORY_ID' value='", "'>")%>
    <a href="javascript:document.<%=miniProdFormName%>.submit()" class="buttontext"><nobr>[Add <%=miniProdQuantity%> to Cart]</nobr></a>
  </form>
</ofbiz:if>
