<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*"%>

<% pageContext.setAttribute("PageName", "product"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %> 

<%
	// Get the value object of the request product id.
	org.ofbiz.ecommerce.catalog.CatalogHelper.getProduct(pageContext,"productValue",request.getParameter("product_id"));
%>

<ofbiz:unless name="productValue">
  <center><h2>Product not found for Product ID "<%=UtilFormatOut.checkNull(request.getParameter("product_id"))%>"!</h2></center>
</ofbiz:unless>

<ofbiz:if name="productValue">
  <ofbiz:object name="product" property="productValue" />
  <br>
  <table border="0" width="100%" cellpadding="3">
    <tr><td colspan="2" height="1" bgcolor="#999999"></td></tr>
    <tr>
      <td align="left" valign="top" width="0">
        <%String largeImageUrl = product.getString("largeImageUrl");%>
        <% if(largeImageUrl != null && largeImageUrl.length() > 0) { %>
          <img src="<%=largeImageUrl%>" vspace="5" hspace="5" border="1" width='200' align=left>
        <% } %>
      </td>
      <td align="right" valign="top">
        <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="addform" style='margin: 0;'>
          <input type='hidden' name="product_id" value="<%=product.getString("productId")%>">
          <input type="text" size="5" name="quantity" value="1">
          <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
          <a href="javascript:document.addform.submit()" class="buttontext"><nobr>[Add to Cart]</nobr></a>
        </form>
        <br>
        <div class="head2"><%=product.getString("name")%></div>
        <div class="tabletext"><%=product.getString("description")%></div>
        <div class="tabletext"><b><%=product.getString("productId")%></b></div>
        <div class="tabletext"><b>Our price: <font color="#126544"><%=UtilFormatOut.formatPrice(product.getDouble("defaultPrice"))%></font></b>
           (Reg. <%=UtilFormatOut.formatPrice(product.getDouble("defaultPrice"))%>)</div>
      </td>
    </tr>
    <tr><td colspan="2" height="1" bgcolor="#999999"></td></tr>
    <tr>
      <td colspan="2">
        <div class="tabletext"><%=product.getString("longDescription")%></div>
      </td>
    </tr>
    <tr><td colspan="2" height="1" bgcolor="#999999"></td></tr>
  </table>
</ofbiz:if>


<%@ include file="/includes/rightcolumn.jsp" %> 
<%@ include file="/includes/footer.jsp" %>
