<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*, org.ofbiz.commonapp.product.category.*" %>
<%@ page import="java.util.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />

<%-- Get a list of all products in the current category. --%>
<%
  String categoryId = request.getParameter("category_id");
  if(categoryId == null || categoryId.length() <= 0) categoryId = CatalogWorker.getCatalogQuickaddCategoryPrimary(pageContext);
  Collection quickAddCategories = CatalogWorker.getCatalogQuickaddCategories(pageContext);
  pageContext.setAttribute("quickAddCats", quickAddCategories);
%>
<%GenericValue category = null;
  try { category = delegator.findByPrimaryKeyCache("ProductCategory",UtilMisc.toMap("productCategoryId",categoryId)); }
  catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); category = null; }
  if(category != null) pageContext.setAttribute("listingCategory", category);
%>
<%CategoryWorker.getRelatedProducts(pageContext,"",categoryId,false);%>

<br>

<ofbiz:if name="listingCategory">
<table border='0' width="100%" cellpadding='3' cellspacing='0'>
  <tr>
    <td align=left>
      <div class="head2"><ofbiz:entityfield attribute="listingCategory" field="description"/></div>
    </td>
    <td align=right>
      <form name="choosequickaddform" method="POST" action="<ofbiz:url>/quickadd</ofbiz:url>" style='margin: 0;'>
        <SELECT name='category_id'>
          <OPTION value='<%=categoryId%>'><ofbiz:entityfield attribute="listingCategory" field="description"/></OPTION>
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
  <%String categoryImageUrl = category.getString("categoryImageUrl");%>
  <%String categoryLongDescription = category.getString("longDescription");%>
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

<ofbiz:if name="productList" size="0">
<br>
<center>
<form method="POST" action="<ofbiz:url>/addtocartbulk</ofbiz:url>" name="bulkaddform" style='margin: 0;'>
  <input type='hidden' name='category_id' value='<%=categoryId%>'>
  <table border='1' width='100%' cellpadding='2' cellspacing='0'>
    <ofbiz:iterator name="product" property="productList">
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
            <nobr>Reg.<ofbiz:entityfield attribute="product" field="defaultPrice"/></nobr>
          </div>
        </td>
        <td align="right" valign="middle" width="5%">
          <div class="tabletext">
            <b><font color="#006633"><ofbiz:entityfield attribute="product" field="defaultPrice"/></font></b>
          </div>
        </td>
        <td valign=top align=right>
          <input type="text" size="5" name='quantity_<ofbiz:entityfield attribute="product" field="productId"/>' value="">
        </td>
      </tr>
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

<ofbiz:unless name="productList" size="0">
<table border="0" width="100%" cellpadding="2">
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td>
          <DIV class='tabletext'>There are no products in this category.</DIV>
      </td>
    </tr>
</table>
</ofbiz:unless>
