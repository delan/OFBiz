<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*, org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.core.pseudotag.*, org.ofbiz.commonapp.product.product.*"%>
<%@ page import="java.util.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%-- Get a list of all products in the current category. --%>
<%
  String categoryId = request.getParameter("category_id");
  if(categoryId == null || categoryId.length() <= 0) categoryId = CatalogWorker.getCatalogQuickaddCategoryPrimary(pageContext);
  Collection quickAddCategories = CatalogWorker.getCatalogQuickaddCategories(pageContext);
  pageContext.setAttribute("quickAddCats", quickAddCategories);
%>

<ofbiz:service name='getProductCategoryAndLimitedMembers'>
    <ofbiz:param name='productCategoryId' value='<%=categoryId%>'/>
    <ofbiz:param name='defaultViewSize' value='<%=new Integer(10)%>'/>
    <ofbiz:param name='limitView' value='<%=new Boolean(false)%>'/>
    <%-- Returns: viewIndex, viewSize, lowIndex, highIndex, listSize, productCategory, productCategoryMembers --%>
</ofbiz:service>
<ofbiz:object name='productCategory' type='org.ofbiz.core.entity.GenericValue'/>

<br>

<ofbiz:if name="productCategory">
<table border='0' width="100%" cellpadding='3' cellspacing='0'>
  <tr>
    <td align=left>
      <div class="head2"><ofbiz:entityfield attribute="productCategory" field="description"/></div>
    </td>
    <td align=right>
      <form name="choosequickaddform" method="POST" action="<ofbiz:url>/quickadd</ofbiz:url>" style='margin: 0;'>
        <SELECT name='category_id'>
          <OPTION value='<%=categoryId%>'><ofbiz:entityfield attribute="productCategory" field="description"/></OPTION>
          <OPTION value='<%=categoryId%>'></OPTION>
          <ofbiz:iterator name="quickAddCatalogId" property="quickAddCats" type="java.lang.String">
            <%GenericValue loopCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", quickAddCatalogId));%>
            <%if(loopCategory != null) {%>
              <OPTION value='<%=quickAddCatalogId%>'><%=loopCategory.getString("description")%></OPTION>
            <%}%>
          </ofbiz:iterator>
        </SELECT>
        <div><a href="javascript:document.choosequickaddform.submit()" class="buttontext">Choose&nbsp;QuickAdd&nbsp;Category</a></div>
      </form>
    </td>
  </tr>
  <%String categoryImageUrl = productCategory.getString("categoryImageUrl");%>
  <%String categoryLongDescription = productCategory.getString("longDescription");%>
  <%if(UtilValidate.isNotEmpty(categoryImageUrl) || UtilValidate.isNotEmpty(categoryLongDescription)) pageContext.setAttribute("showCategoryDetails", "true");%>
  <ofbiz:if name="showCategoryDetails">
    <tr><td colspan='2'><hr class='sepbar'></td></tr>
    <tr>
      <td align="left" valign="top" width="0" colspan='2'>
        <div class="tabletext">
        <% if(UtilValidate.isNotEmpty(categoryImageUrl)) {%>
          <img src="<%=categoryImageUrl%>" vspace="5" hspace="5" border="1" height='100' align=left>
        <% } %>
        <%=UtilFormatOut.checkNull(categoryLongDescription)%>
        </div>
      </td>
    </tr>
  </ofbiz:if>
</table>
</ofbiz:if>

<ofbiz:if name="productCategoryMembers" size="0">
<br>
<center>
<form method="POST" action="<ofbiz:url>/addtocartbulk</ofbiz:url>" name="bulkaddform" style='margin: 0;'>
  <input type='hidden' name='category_id' value='<%=categoryId%>'>
  <table border='1' width='100%' cellpadding='2' cellspacing='0'>
    <ofbiz:iterator name="productCategoryMember" property="productCategoryMembers">
        <%GenericValue product = productCategoryMember.getRelatedOneCache("Product");%>
        <%if (product != null) {%>
            <%pageContext.setAttribute("product", product);%>
        <%-- calculate the "your" price --%>
        <%pageContext.removeAttribute("listPrice");%>
        <%pageContext.removeAttribute("defaultPrice");%>
        <ofbiz:service name='calculateProductPrice'>
            <ofbiz:param name='product' attribute='product'/>
            <ofbiz:param name='prodCatalogId' value='<%=CatalogWorker.getCurrentCatalogId(pageContext)%>'/>
            <ofbiz:param name='webSiteId' value='<%=CatalogWorker.getWebSiteId(pageContext)%>'/>
            <ofbiz:param name='autoUserLogin' attribute='autoUserLogin'/>
            <%-- don't need to pass the partyId because it will use the one from the currently logged in user, if there user logged in --%>
            <%-- returns: isSale, price, orderItemPriceInfos and optionally: listPrice, defaultPrice, averageCost --%>
        </ofbiz:service>
        <%boolean isSale = pageContext.getAttribute("isSale") != null ? ((Boolean) pageContext.getAttribute("isSale")).booleanValue() : false;%>
      <%-- <tr><td><hr class='sepbar'></td></tr> --%>
      <tr>
        <td align="left" valign="middle" width="5%">
          <div class="tabletext">
            <b><ofbiz:entityfield attribute="product" field="productId"/></b>
          </div>
        </td>
        <td align="left" valign="middle" width="90%">
          <a href='<ofbiz:url>/product?product_id=<ofbiz:entityfield attribute="product" field="productId"/></ofbiz:url>' class='buttontext'><ofbiz:entityfield attribute="product" field="productName"/></a>
        </td>
        <td align="left" valign="middle" width="5%">
          <div class="tabletext">
            <nobr>List:<ofbiz:field attribute="listPrice" type="currency"/></nobr>
          </div>
        </td>
        <td align="right" valign="middle" width="5%">
          <div class='<%if (isSale) {%>salePrice<%} else {%>normalPrice<%}%>'>
            <b><ofbiz:field attribute="price" type="currency"/></b>
          </div>
        </td>
        <td valign=top align=right>
          <%if (product.get("introductionDate") != null && UtilDateTime.nowTimestamp().before(product.getTimestamp("introductionDate"))) {%>
              <%-- check to see if introductionDate hasn't passed yet --%>
              <div class='tabletext' style='color: red;'>Not Yet Available</div>
          <%} else if (product.get("salesDiscontinuationDate") != null && UtilDateTime.nowTimestamp().after(product.getTimestamp("salesDiscontinuationDate"))) {%>
              <%-- check to see if salesDiscontinuationDate has passed --%>
              <div class='tabletext' style='color: red;'>No Longer Available</div>
          <%} else if ("Y".equals(product.getString("isVirtual"))) {%>
              <%-- check to see if the product is a virtual product --%>
              <%--<div class='tabletext' style='color: red;'>Virtual Product</div>--%>
              <a href='<ofbiz:url>/product?<ofbiz:if name="category_id">category_id=<ofbiz:print attribute="category_id"/>&</ofbiz:if>product_id=<%EntityField.run("product", "productId", pageContext);%></ofbiz:url>' class="buttontext"><nobr>[Choose Variation...]</nobr></a>
          <%} else {%>
              <input type="text" size="5" name='quantity_<ofbiz:entityfield attribute="product" field="productId"/>' value="">
          <%}%>
        </td>
      </tr>
      <%}%>
    </ofbiz:iterator>
    <%-- <tr><td colspan="2"><hr class='sepbar'></td></tr> --%>
    <tr>
      <td colspan="5" align=right>
        <a href="javascript:document.bulkaddform.submit()" class="buttontext"><nobr>[Add All to Cart]</nobr></a>
      </td>
    </tr>
  </table>
</form>
</center>
</ofbiz:if>

<ofbiz:unless name="productCategoryMembers" size="0">
<table border="0" width="100%" cellpadding="2">
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td>
          <DIV class='tabletext'>There are no products in this category.</DIV>
      </td>
    </tr>
</table>
</ofbiz:unless>
