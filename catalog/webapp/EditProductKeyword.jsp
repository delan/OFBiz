<%
/**
 *  Title: Edit Product Page
 *  Description: None
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
 */
%>

<% pageContext.setAttribute("PageName", "Edit Product Keywords"); %>

<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %>

<%if(security.hasEntityPermission("CATALOG", "_VIEW", request.getSession())) {%>
<%
  String productId = request.getParameter("PRODUCT_ID");
  GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
%>
<br>
<div class="head1">Edit Keywords for Product with ID "<%=productId%>"</div>

<%if(productId != null && productId.length() > 0){%>
  <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Product]</a>
<%}%>

<%if(productId!=null&&product!=null){%>
<br>
<div class='tabletext'>NOTE: Keywords are automatically created when product information is changed, but you may manually CREATE or DELETE keywords here as well.</div>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <TD align=left>
            <DIV class='boxhead'>Add product-keyword (enter keyword):</DIV>
          </TD>
          <TD align=right>
            <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="lightbuttontext">[Edit Product]</a>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
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

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
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
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
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

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <TD align=left>
            <DIV class='boxhead'>Add product-keyword (enter keyword):</DIV>
          </TD>
          <TD align=right>
            <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="lightbuttontext">[Edit Product]</a>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
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
<%if(productId != null && productId.length() > 0){%>
  <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Product]</a>
<%}%>

<%}else{%>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
