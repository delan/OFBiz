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
 *@created    May 22 2001
 *@version    1.0
--%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
    String state = request.getParameter("BrowseCatalogsState");
    boolean isOpen = true;
    if (state != null) {
        session.setAttribute("BrowseCatalogsState", state);
        isOpen = "open".equals(state);
    } else {
        state = (String) session.getAttribute("BrowseCatalogsState");
        if (state != null) {
            isOpen = "open".equals(state);
        }
    }
%>
<%
    //prodCatalogs
    if (isOpen) {
        Collection prodCatalogs = delegator.findAll("ProdCatalog");
        if (prodCatalogs != null) pageContext.setAttribute("prodCatalogs", prodCatalogs);
    }

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
          <td valign=middle align=right>
            <%if (isOpen) {%>
                <a href='<ofbiz:url>/main?BrowseCatalogsState=close</ofbiz:url>' class='lightbuttontext'>&nbsp;_&nbsp;</a>
            <%} else {%>
                <a href='<ofbiz:url>/main?BrowseCatalogsState=open</ofbiz:url>' class='lightbuttontext'>&nbsp;[]&nbsp;</a>
            <%}%>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
<%if (isOpen) {%>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <div><a href='<ofbiz:url>/FindProdCatalog</ofbiz:url>' class='buttontext'>Catalog Detail List</a></div>
            <div style='margin-left: 10px;'>
              <ofbiz:iterator name="prodCatalog" property="prodCatalogs">
              <%if(curProdCatalogId != null && curProdCatalogId.equals(prodCatalog.getString("prodCatalogId"))) {%>
                <%Collection prodCatalogCategories = prodCatalog.getRelatedCache("ProdCatalogCategory", null, UtilMisc.toList("prodCatalogCategoryTypeId", "sequenceNum", "productCategoryId"));%>
                <%if (prodCatalogCategories != null) pageContext.setAttribute("prodCatalogCategories", prodCatalogCategories);%>
                <div style='text-indent: -10px;'><a href="<ofbiz:url>/EditProdCatalog?prodCatalogId=<%=prodCatalog.getString("prodCatalogId")%></ofbiz:url>" class='buttontext'>-&nbsp;<%=UtilFormatOut.checkNull(prodCatalog.getString("catalogName"))%></a></div>
                <%-- <div class='tabletext' style='text-indent: -10px;'><b>-&nbsp;<%=currentTopCategory.getString("description")%> [<%=currentTopCategory.getString("productCategoryId")%>]</b></div> --%>
                  <div style='margin-left: 10px;'>
                    <ofbiz:iterator name="prodCatalogCategory" property="prodCatalogCategories">
                      <%GenericValue productCategory = prodCatalogCategory.getRelatedOneCache("ProductCategory");%>
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
<%}%>
</TABLE>
