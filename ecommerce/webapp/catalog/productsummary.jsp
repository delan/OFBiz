
<%if(product != null) {%>
<table border="0" width="100%" cellpadding="0">
  <tr>
    <td valign="top">
      <%String smallImageUrl = product.getString("smallImageUrl");%>
      <%if(smallImageUrl == null || smallImageUrl.length() <= 0) smallImageUrl = "/images/defaultImage.jpg";%>
        <a href="<ofbiz:url>/product?product_id=<%= product.getString("productId") %></ofbiz:url>">
          <img src="<%=smallImageUrl%>" align="left" height="50" width="50" class='imageborder' border='0'>
        </a>
    </td>
    <td align="left" valign="top" width="100%">
        <div class="tabletext">
          <a href="<ofbiz:url>/product?product_id=<%= product.getString("productId") %></ofbiz:url>" class="buttontext"><%=product.getString("name")%></a>
        </div>
        <div class="tabletext"><%=product.getString("description")%></div>
        <div class="tabletext">
          <nobr>
            <b><%=product.getString("productId")%></b>,
            <b><%=UtilFormatOut.formatPrice(product.getDouble("defaultPrice"))%></b>
            , Reg. <%=UtilFormatOut.formatPrice(product.getDouble("defaultPrice"))%>
          </nobr>
        </div>
    </td>
    <td valign=top align=right>
      <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="the<%=UtilFormatOut.formatQuantity(listIndex)%>form" style='margin: 0;'>
        <input type='hidden' name="product_id" value="<%=product.getString("productId")%>">
        <input type="text" size="5" name="quantity" value="1">
        <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
        <%=UtilFormatOut.ifNotEmpty(request.getParameter("VIEW_INDEX"), "<input type='hidden' name='VIEW_INDEX' value='", "'>")%>
        <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_STRING"), "<input type='hidden' name='SEARCH_STRING' value='", "'>")%>
        <a href="javascript:document.the<%=UtilFormatOut.formatQuantity(listIndex)%>form.submit()" class="buttontext"><nobr>[Add to Cart]</nobr></a>
      </form>
    </td>
  </tr>
<%--
  <tr>
    <td colspan="4" height="1" bgcolor="888888"></td>
  </tr>
--%>
</table>
<%} else {%>
&nbsp;ERROR: Product not found.<br>
<%}%>
