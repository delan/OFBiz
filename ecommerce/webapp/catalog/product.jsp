<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.product.product.*"%>

<%
  // Get the value object of the request product id.
  ProductWorker.getProduct(pageContext,"productValue",request.getParameter("product_id"));
  ProductWorker.getAssociatedProducts(pageContext,"productValue","");
%>

<ofbiz:unless name="productValue">
  <center><h2>Product not found for Product ID "<%=UtilFormatOut.checkNull(request.getParameter("product_id"))%>"!</h2></center>
</ofbiz:unless>

<ofbiz:if name="productValue">
  <ofbiz:object name="productValue" property="productValue" />
  <br>
  <table border="0" width="100%" cellpadding="3">
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td align="left" valign="top" width="0">
        <ofbiz:entityfield attribute="productValue" field="largeImageUrl" prefix="<img src='" suffix="' vspace='5' hspace='5' border='1' width='200' align=left>"/>
      </td>
      <td align="right" valign="top">
        <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="addform" style='margin: 0;'>
          <input type='hidden' name="product_id" value='<ofbiz:entityfield attribute="productValue" field="productId"/>'>
          <input type='hidden' name="add_product_id" value='<ofbiz:entityfield attribute="productValue" field="productId"/>'>
          <input type="text" size="5" name="quantity" value="1">
          <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
          <a href="javascript:document.addform.submit()" class="buttontext"><nobr>[Add to Cart]</nobr></a>
        </form>
        <br>
        <div class="head2"><ofbiz:entityfield attribute="productValue" field="productName"/></div>
        <div class="tabletext"><ofbiz:entityfield attribute="productValue" field="description"/></div>
        <div class="tabletext"><b><ofbiz:entityfield attribute="productValue" field="productId"/></b></div>
        <div class="tabletext"><b>Our price: <font color="#126544"><ofbiz:entityfield attribute="productValue" field="defaultPrice"/></font></b>
           (Reg. <ofbiz:entityfield attribute="productValue" field="defaultPrice"/>)</div>
        <div class="tabletext">Size:
            <%if (productValue.get("quantityIncluded") != null && productValue.getDouble("quantityIncluded").doubleValue() != 0) {%>
                <ofbiz:entityfield attribute="productValue" field="quantityIncluded"/>
            <%}%>
            <ofbiz:entityfield attribute="productValue" field="quantityUomId"/>
        </div>
        <%if (productValue.get("piecesIncluded") != null && productValue.getLong("piecesIncluded").longValue() != 0) {%>
            <ofbiz:entityfield attribute="productValue" field="piecesIncluded" prefix="<div class='tabletext'>Pieces: " suffix="</div>"/>
        <%}%>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td colspan="2">
        <div class="tabletext"><ofbiz:entityfield attribute="productValue" field="longDescription"/></div>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
  </table>

  <table width='100%'>
<%int listIndex = 1;%>
<!-- obsolete by -->
    <ofbiz:if name="obsoleteby">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2"><ofbiz:entityfield attribute="productValue" field="productName"/> is made obsolete by these products:</div></td></tr>
      <tr><td><hr class='sepbar'></td></tr>

      <ofbiz:iterator name="productAssoc" property="obsoleteby">
        <tr><td>
          <div class="tabletext">
            <a href='<ofbiz:url>/product?product_id=<ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></ofbiz:url>' class="buttontext"><ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></a>
            - <b><ofbiz:entityfield attribute="productAssoc" field="reason"/></b>
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
        <tr><td><hr class='sepbar'></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

<!-- cross sells -->
    <ofbiz:if name="complement">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2">You might be interested in these as well:</div></td></tr>
      <tr><td><hr class='sepbar'></td></tr>

      <ofbiz:iterator name="productAssoc" property="complement">
        <tr><td>
          <div class="tabletext">
            <a href='<ofbiz:url>/product?product_id=<ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></ofbiz:url>' class="buttontext"><ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></a>
            - <b><ofbiz:entityfield attribute="productAssoc" field="reason"/></b>
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
        <tr><td><hr class='sepbar'></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

<!-- up sells -->
    <ofbiz:if name="upgrade">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2">Try these instead of <ofbiz:entityfield attribute="productValue" field="productName"/>:</div></td></tr>
      <tr><td><hr class='sepbar'></td></tr>

      <ofbiz:iterator name="productAssoc" property="upgrade">
        <tr><td>
          <div class="tabletext">
            <a href='<ofbiz:url>/product?product_id=<ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></ofbiz:url>' class="buttontext"><ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></a>
            - <b><ofbiz:entityfield attribute="productAssoc" field="reason"/></b>
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
        <tr><td><hr class='sepbar'></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

<!-- obsolescence -->
    <ofbiz:if name="obsolescence">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2"><ofbiz:entityfield attribute="productValue" field="productName"/> makes these products obsolete:</div></td></tr>
      <tr><td><hr class='sepbar'></td></tr>

      <ofbiz:iterator name="productAssoc" property="obsolescence">
        <tr><td>
          <div class="tabletext">
            <a href="<ofbiz:url>/product?product_id=<%=productAssoc.getString("productId")%></ofbiz:url>" class="buttontext"><%=productAssoc.getString("productId")%></a>
            - <b><ofbiz:entityfield attribute="productAssoc" field="reason"/></b>
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
        <tr><td><hr class='sepbar'></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

  </table>
</ofbiz:if>
