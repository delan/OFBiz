<%--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     David E. Jones
 *@created    Sep 10 2001
 *@version    1.0
--%>
<%@ page import="org.ofbiz.commonapp.product.catalog.*"%>
<ofbiz:if name="product">
    <%
        if (request.getParameter("category_id") != null) {
            pageContext.setAttribute("category_id", request.getParameter("category_id"));
        } else if (pageContext.findAttribute("productCategoryId") != null) {
            pageContext.setAttribute("category_id", pageContext.findAttribute("productCategoryId"));
        }
    %>
    <%-- calculate the "your" price --%>
    <ofbiz:service name='calculateProductPrice'>
        <ofbiz:param name='product' attribute='product'/>
        <ofbiz:param name='prodCatalogId' value='<%=CatalogWorker.getCurrentCatalogId(pageContext)%>'/>
        <ofbiz:param name='webSiteId' value='<%=CatalogWorker.getWebSiteId(pageContext)%>'/>
        <ofbiz:param name='partyId' attribute='orderPartyId'/>
        <%-- don't need to pass the partyId because it will use the one from the currently logged in user, if there user logged in --%>
        <%-- returns: isSale, price, orderItemPriceInfos and optionally: listPrice, defaultPrice, averageCost --%>
    </ofbiz:service>

    <%boolean isSale = pageContext.getAttribute("isSale") != null ? ((Boolean) pageContext.getAttribute("isSale")).booleanValue() : false;%>

  <table border="0" width='100%' cellpadding='0' cellspacing='0'>
    <tr>
      <%GenericValue localProduct = (GenericValue) pageContext.getAttribute("product");%>      
      <td align="left" valign="top" width="100%">
          <div class="tabletext">
            <a href='<ofbiz:url>/product?<ofbiz:if name="category_id">category_id=<ofbiz:print attribute="category_id"/>&</ofbiz:if>product_id=<%EntityField.run("product", "productId", pageContext);%></ofbiz:url>' class='buttontext'><%EntityField.run("product", "productName", pageContext);%></a>
            &nbsp;-&nbsp;<%EntityField.run("product", "description", pageContext);%>
          </div>
          <div class="tabletext">
            <nobr>
              <b><%EntityField.run("product", "productId", pageContext);%></b>,
                <%if (pageContext.getAttribute("listPrice") != null && pageContext.getAttribute("price") != null && 
                        ((Double) pageContext.getAttribute("price")).doubleValue() < ((Double) pageContext.getAttribute("listPrice")).doubleValue()) {%>
                    List price: <span class='basePrice'><ofbiz:field attribute="listPrice" type="currency"/></span>
                <%}%>
                <b>
                    <%if (isSale) {%><span class='salePrice'>On Sale!</span><%}%>
                    Your price: <span class='<%if (isSale) {%>salePrice<%} else {%>normalPrice<%}%>'><ofbiz:field attribute="price" type="currency"/></span>
                </b>
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
              <a href='<ofbiz:url>/product?<ofbiz:if name="category_id">category_id=<ofbiz:print attribute="category_id"/>&</ofbiz:if>product_id=<%EntityField.run("product", "productId", pageContext);%></ofbiz:url>' class="buttontext"><nobr>[Choose Variation...]</nobr></a>
          <%} else {%>
                <form method="POST" action="<ofbiz:url>/additem/salesentry</ofbiz:url>" name="the<%=UtilFormatOut.formatQuantity(listIndex)%>form" style='margin: 0;'>
                  <input type='hidden' name='add_product_id' value='<%EntityField.run("product", "productId", pageContext);%>'>
                  <nobr><input type="text" size="5" name="quantity" value="1">
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("product_id"), "<input type='hidden' name='product_id' value='", "'>")%>
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("VIEW_INDEX"), "<input type='hidden' name='VIEW_INDEX' value='", "'>")%>
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_STRING"), "<input type='hidden' name='SEARCH_STRING' value='", "'>")%>
                  <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_CATEGORY_ID"), "<input type='hidden' name='SEARCH_CATEGORY_ID' value='", "'>")%>
                  <a href="javascript:document.the<%=UtilFormatOut.formatQuantity(listIndex)%>form.submit()" class="buttontext">[Add]</nobr></a>
                </form>
                <ofbiz:if name="productCategoryMember">
                    <%GenericValue prodCatMemberTemp = (GenericValue) pageContext.getAttribute("productCategoryMember");%>
                    <%if (prodCatMemberTemp != null && prodCatMemberTemp.get("quantity") != null && prodCatMemberTemp.getDouble("quantity").doubleValue() > 0.0) {%>
                        <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="the<%=UtilFormatOut.formatQuantity(listIndex)%>defaultform" style='margin: 0;'>
                          <input type='hidden' name='add_product_id' value='<%EntityField.run("productCategoryMember", "productId", pageContext);%>'>
                          <nobr><input type='hidden' name="quantity" value='<%EntityField.run("productCategoryMember", "quantity", pageContext);%>'>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("product_id"), "<input type='hidden' name='product_id' value='", "'>")%>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("VIEW_INDEX"), "<input type='hidden' name='VIEW_INDEX' value='", "'>")%>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_STRING"), "<input type='hidden' name='SEARCH_STRING' value='", "'>")%>
                          <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_CATEGORY_ID"), "<input type='hidden' name='SEARCH_CATEGORY_ID' value='", "'>")%>
                          <a href="javascript:document.the<%=UtilFormatOut.formatQuantity(listIndex)%>defaultform.submit()" class="buttontext">[Add Default(<%EntityField.run("productCategoryMember", "quantity", pageContext);%>)]</nobr></a>
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
