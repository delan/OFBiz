<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*"%>

<% pageContext.setAttribute("PageName", "Product Detail"); %>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %> 

<%
  // Get the value object of the request product id.
  org.ofbiz.ecommerce.catalog.CatalogHelper.getProduct(pageContext,"productValue",request.getParameter("product_id"));
  org.ofbiz.ecommerce.catalog.CatalogHelper.getAssociatedProducts(pageContext,"productValue","");
%>

<ofbiz:unless name="productValue">
  <center><h2>Product not found for Product ID "<%=UtilFormatOut.checkNull(request.getParameter("product_id"))%>"!</h2></center>
</ofbiz:unless>

<ofbiz:if name="productValue">
  <br>
  <table border="0" width="100%" cellpadding="3">
  <ofbiz:object name="mainProduct" property="productValue" />
    <tr><td colspan="2"><div style='height: 1; background-color: #999999;'></div></td></tr>
    <tr>
      <td align="left" valign="top" width="0">
        <%String largeImageUrl = mainProduct.getString("largeImageUrl");%>
        <% if(largeImageUrl != null && largeImageUrl.length() > 0) { %>
          <img src="<%=largeImageUrl%>" vspace="5" hspace="5" border="1" width='200' align=left>
        <% } %>
      </td>
      <td align="right" valign="top">
        <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="addform" style='margin: 0;'>
          <input type='hidden' name="product_id" value="<%=mainProduct.getString("productId")%>">
          <input type='hidden' name="add_product_id" value="<%=mainProduct.getString("productId")%>">
          <input type="text" size="5" name="quantity" value="1">
          <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
          <a href="javascript:document.addform.submit()" class="buttontext"><nobr>[Add to Cart]</nobr></a>
        </form>
        <br>
        <div class="head2"><%=UtilFormatOut.checkNull(mainProduct.getString("name"))%></div>
        <div class="tabletext"><%=UtilFormatOut.checkNull(mainProduct.getString("description"))%></div>
        <div class="tabletext"><b><%=UtilFormatOut.checkNull(mainProduct.getString("productId"))%></b></div>
        <div class="tabletext"><b>Our price: <font color="#126544"><%=UtilFormatOut.formatPrice(mainProduct.getDouble("defaultPrice"))%></font></b>
           (Reg. <%=UtilFormatOut.formatPrice(mainProduct.getDouble("defaultPrice"))%>)</div>
      </td>
    </tr>
    <tr><td colspan="2"><div style='height: 1; background-color: #999999;'></div></td></tr>
    <tr>
      <td colspan="2">
        <div class="tabletext"><%=UtilFormatOut.checkNull(mainProduct.getString("longDescription"))%></div>
      </td>
    </tr>
    <tr><td colspan="2"><div style='height: 1; background-color: #999999;'></div></td></tr>
  </table>

  <table width='100%'>
<%int listIndex = 1;%>
<!-- obsolete by -->
    <ofbiz:if name="obsoleteby">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2"><%=UtilFormatOut.checkNull(mainProduct.getString("name"))%> is made obsolete by these products:</div></td></tr>
      <tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>

      <ofbiz:iterator name="productAssoc" property="obsoleteby">
        <tr><td>
          <div class="tabletext">
            <a href="<ofbiz:url>/product?product_id=<%=productAssoc.getString("productIdTo")%></ofbiz:url>" class="buttontext"><%=productAssoc.getString("productIdTo")%></a>
            - <b><%=UtilFormatOut.checkNull(productAssoc.getString("reason"))%></b>
          </div>
        </td></tr>
        <%{%>
          <%GenericValue product = productAssoc.getRelatedOneCache("AssocProduct");%>
          <%pageContext.setAttribute("product", product);%>
          <tr>
            <td>
              <%@ include file="/catalog/productsummary.jsp"%>
            </td>
          </tr>
          <%listIndex++;%>
        <%}%>
        <tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

<!-- cross sells -->
    <ofbiz:if name="complement">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2">You might be interested in these as well:</div></td></tr>
      <tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>

      <ofbiz:iterator name="productAssoc" property="complement">
        <tr><td>
          <div class="tabletext">
            <a href="<ofbiz:url>/product?product_id=<%=productAssoc.getString("productIdTo")%></ofbiz:url>" class="buttontext"><%=productAssoc.getString("productIdTo")%></a>
            - <b><%=UtilFormatOut.checkNull(productAssoc.getString("reason"))%></b>
          </div>
        </td></tr>
        <%{%>
          <%GenericValue product = productAssoc.getRelatedOneCache("AssocProduct");%>
          <%pageContext.setAttribute("product", product);%>
          <tr>
            <td>
              <%@ include file="/catalog/productsummary.jsp"%>
            </td>
          </tr>
          <%listIndex++;%>
        <%}%>
        <tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

<!-- up sells -->
    <ofbiz:if name="upgrade">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2">Try these instead of <%=UtilFormatOut.checkNull(mainProduct.getString("name"))%>:</div></td></tr>
      <tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>

      <ofbiz:iterator name="productAssoc" property="upgrade">
        <tr><td>
          <div class="tabletext">
            <a href="<ofbiz:url>/product?product_id=<%=productAssoc.getString("productIdTo")%></ofbiz:url>" class="buttontext"><%=productAssoc.getString("productIdTo")%></a>
            - <b><%=UtilFormatOut.checkNull(productAssoc.getString("reason"))%></b>
          </div>
        </td></tr>
        <%{%>
          <%GenericValue product = productAssoc.getRelatedOneCache("AssocProduct");%>
          <%pageContext.setAttribute("product", product);%>
          <tr>
            <td>
              <%@ include file="/catalog/productsummary.jsp"%>
            </td>
          </tr>
          <%listIndex++;%>
        <%}%>
        <tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

<!-- obsolescence -->
    <ofbiz:if name="obsolescence">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2"><%=UtilFormatOut.checkNull(mainProduct.getString("name"))%> makes these products obsolete:</div></td></tr>
      <tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>

      <ofbiz:iterator name="productAssoc" property="obsolescence">
        <tr><td>
          <div class="tabletext">
            <a href="<ofbiz:url>/product?product_id=<%=productAssoc.getString("productId")%></ofbiz:url>" class="buttontext"><%=productAssoc.getString("productId")%></a>
            - <b><%=UtilFormatOut.checkNull(productAssoc.getString("reason"))%></b>
          </div>
        </td></tr>
        <%{%>
          <%GenericValue product = productAssoc.getRelatedOneCache("MainProduct");%>
          <%pageContext.setAttribute("product", product);%>
          <tr>
            <td>
              <%@ include file="/catalog/productsummary.jsp"%>
            </td>
          </tr>
          <%listIndex++;%>
        <%}%>
        <tr><td><div style='height: 1; background-color: #999999;'></div></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

  </table>
</ofbiz:if>


<%@ include file="/includes/rightcolumn.jsp" %> 
<%@ include file="/includes/footer.jsp" %>
