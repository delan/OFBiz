<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<% pageContext.setAttribute("PageName", "Quick Add Page"); %> 
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %> 

<%-- Get a list of all products in the current category. --%>
<%
  Collection quickAddCategories = CatalogWorker.getCatalogQuickaddCategories(pageContext);
  String categoryId = request.getParameter("category_id");
  if(categoryId == null || categoryId.length() <= 0) categoryId = CatalogWorker.getCatalogQuickaddCategoryPrimary(pageContext);
%>
<%CategoryWorker.getRelatedProducts(pageContext,"",categoryId,false);%>
<br>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<%GenericValue category = null;
  try { category = delegator.findByPrimaryKeyCache("ProductCategory",UtilMisc.toMap("productCategoryId",categoryId)); }
  catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); category = null; }
  if(category != null) pageContext.setAttribute("listingCategory", category);%>
<ofbiz:if name="listingCategory">
<table border='0' width="100%" cellpadding='3' cellspacing='0'>
  <tr>
    <td colspan="2">
      <div class="head1"><%=UtilFormatOut.checkNull(category.getString("description"))%></div>
    </td>
  </tr>
  <%String categoryImageUrl = category.getString("categoryImageUrl");%>
  <%String categoryLongDescription = category.getString("longDescription");%>
  <%if(UtilValidate.isNotEmpty(categoryImageUrl) || UtilValidate.isNotEmpty(categoryLongDescription)) pageContext.setAttribute("showCategoryDetails", "true");%>
  <ofbiz:if name="showCategoryDetails">
    <tr><td><hr class='sepbar'></td></tr>
    <tr>
      <td align="left" valign="top" width="0">
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

<%@ include file="/includes/rightcolumn.jsp" %>
<%@ include file="/includes/footer.jsp" %>
