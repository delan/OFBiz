
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
    //prodCatalogs
    Collection prodCatalogs = delegator.findAll("ProdCatalog");
    if (prodCatalogs != null) pageContext.setAttribute("prodCatalogs", prodCatalogs);

    //get the current prodCatalogId
    String curProdCatalogId = request.getParameter("prodCatalogId");
    if (UtilValidate.isNotEmpty(curProdCatalogId)) {
        session.setAttribute("curProdCatalogId", curProdCatalogId);
    } else {
        curProdCatalogId = (String) session.getAttribute("curProdCatalogId");
    }
%>
<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">Browse&nbsp;Catalogs</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <div><a href='<ofbiz:url>/FindProdCatalog</ofbiz:url>' class='buttontext'>Catalog Detail List</a></div>
            <div style='margin-left: 10px;'>
              <ofbiz:iterator name="prodCatalog" property="prodCatalogs">
              <%if(curProdCatalogId != null && curProdCatalogId.equals(prodCatalog.getString("prodCatalogId"))) {%>
                <%Collection prodCatalogCategories = prodCatalog.getRelated("ProdCatalogCategory", null, UtilMisc.toList("prodCatalogCategoryTypeId", "sequenceNum", "productCategoryId"));%>
                <%if (prodCatalogCategories != null) pageContext.setAttribute("prodCatalogCategories", prodCatalogCategories);%>
                <div style='text-indent: -10px;'><a href="<ofbiz:url>/EditProdCatalog?prodCatalogId=<%=prodCatalog.getString("prodCatalogId")%></ofbiz:url>" class='buttontext'>-&nbsp;<%=UtilFormatOut.checkNull(prodCatalog.getString("catalogName"))%></a></div>
                <%-- <div class='tabletext' style='text-indent: -10px;'><b>-&nbsp;<%=currentTopCategory.getString("description")%> [<%=currentTopCategory.getString("productCategoryId")%>]</b></div> --%>
                  <div style='margin-left: 10px;'>
                    <ofbiz:iterator name="prodCatalogCategory" property="prodCatalogCategories">
                      <%GenericValue productCategory = prodCatalogCategory.getRelatedOne("ProductCategory");%>
                      <div style='text-indent: -10px;'><a href='<ofbiz:url>/EditCategory?CATALOG_TOP_CATEGORY=<ofbiz:entityfield attribute="prodCatalogCategory" field="productCategoryId"/>&productCategoryId=<ofbiz:inputvalue entityAttr="prodCatalogCategory" field="productCategoryId"/></ofbiz:url>' class="buttontext">-&nbsp;<%if (productCategory!=null) {%><%=productCategory.getString("description")%><%}%><%-- [<ofbiz:inputvalue entityAttr="prodCatalogCategory" field="productCategoryId"/>]--%></a></div>
                    </ofbiz:iterator>
                  </div>
              <%}else{%>
                <div style='text-indent: -10px;'><a href="<ofbiz:url>/EditProdCatalog?prodCatalogId=<%=prodCatalog.getString("prodCatalogId")%></ofbiz:url>" class='buttontext'>-&nbsp;<%=UtilFormatOut.checkNull(prodCatalog.getString("catalogName"))%></a></div>
              <%}%>
              </ofbiz:iterator>
            </div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
