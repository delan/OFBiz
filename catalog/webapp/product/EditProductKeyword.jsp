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

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("CATALOG", "_VIEW", request.getSession())) {%>
<%
  String productId = request.getParameter("PRODUCT_ID");
  GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
%>

<br>
<%if(productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButton">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButtonSelected">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButton">Features</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButton">Inventory</a>
  <%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <a href="<ofbiz:url>/QuickAddVariants?productId=<%=productId%></ofbiz:url>" class="tabButton">Variants</a>
  <%}%>
  </div>
<%}%>

<div class="head1">Keywords <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
<%}%>
<br>

<%if(productId!=null&&product!=null){%>
<br>
<div class='tabletext'>NOTE: Keywords are automatically created when product information is changed, but you may manually CREATE or DELETE keywords here as well.</div>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left>
            <DIV class='boxhead'>Add product-keyword (enter keyword):</DIV>
          </TD>
          <TD align=right>
            <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="lightbuttontext">[Edit Product]</a>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
      <form method="POST" action="<ofbiz:url>/UpdateProductKeyword</ofbiz:url>" style='margin: 0;'>
        <input type="hidden" name="UPDATE_MODE" value="CREATE">
        <input type="hidden" name="PRODUCT_ID" value="<%=productId%>">
        <input type="text" size="20" name="KEYWORD" value="">
        <input type="submit" value="Add">
      </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<BR>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left>
            <DIV class='boxhead'>Keywords</DIV>
          </TD>
          <TD align=right>
            <a href="<ofbiz:url>/UpdateProductKeywords?UPDATE_MODE=CREATE&PRODUCT_ID=<%=productId%></ofbiz:url>" class="lightbuttontext">[Re-induce Keywords]</a>
            <a href="<ofbiz:url>/UpdateProductKeywords?UPDATE_MODE=DELETE&PRODUCT_ID=<%=productId%></ofbiz:url>" class="lightbuttontext">[Delete All Keywords]</a>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td valign=top>
      <TABLE width='100%' cellpadding='0' cellspacing='0' border='0'>
<%Collection pkCol = product.getRelated("ProductKeyword");%>
<%Iterator pkIterator = UtilMisc.toIterator(pkCol);%>
<%if(pkIterator != null && pkIterator.hasNext()) {%>
  <%int colSize = pkCol.size()/3 + 1;%>
  <%int kIdx = 0;%>
  <%while(pkIterator.hasNext()) {%>
    <%GenericValue productKeyword = (GenericValue)pkIterator.next();%>
    <tr>
      <td align=left>&nbsp;<%=productKeyword.getString("keyword")%></td>
      <td>&nbsp;&nbsp;</td>
      <td align=left>
        <a href="<ofbiz:url>/UpdateProductKeyword?UPDATE_MODE=DELETE&PRODUCT_ID=<%=productId%>&KEYWORD=<%=productKeyword.getString("keyword")%></ofbiz:url>" class="buttontext">
        [Delete]</a>
      </td>
    </tr>
    <%kIdx++;%>
    <%if(kIdx >= colSize) {%>
      <%colSize += colSize;%>
      </TABLE>
    </TD>
    <TD bgcolor='#FFFFFF' valign=top>
      <TABLE width='100%' cellpadding='0' cellspacing='0' border='0'>      
    <%}%>
  <%}%>
<%}else{%>
    <tr>
      <td colspan='3'><div class='tabletext'>No Keywords Found</div></td>
    </tr>
<%}%>
      </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<BR>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left>
            <DIV class='boxhead'>Add product-keyword (enter keyword):</DIV>
          </TD>
          <TD align=right>
            <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="lightbuttontext">[Edit Product]</a>
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
              <form method="POST" action="<ofbiz:url>/UpdateProductKeyword</ofbiz:url>" style='margin: 0;'>
                <input type="hidden" name="UPDATE_MODE" value="CREATE">
                <input type="hidden" name="PRODUCT_ID" value="<%=productId%>">
                <input type="text" size="20" name="KEYWORD" value="">
                <input type="submit" value="Add">
              </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<%}else{%>
  <div class='head2'>Product not found with Product ID "<%=productId%>"</div>
<%}%>

<%}else{%>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
