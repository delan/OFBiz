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
<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.product.product.*"%>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>

<%
	String idValue = request.getParameter("idValue");

	//grab this just to see if the idValue happens to be a productId of some product
	GenericValue idProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", idValue));
	if (idProduct != null) pageContext.setAttribute("idProduct", idProduct);
	
	List goodIdentifications = delegator.findByAnd("GoodIdentification", UtilMisc.toMap("idValue", idValue));
	if (goodIdentifications != null) pageContext.setAttribute("goodIdentifications", goodIdentifications);
%>



<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=left>
            <div class="boxhead">Find&nbsp;Products&nbsp;by&nbsp;ID&nbsp;Value</div>
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
            <form name="idsearchform" method="POST" action="<ofbiz:url>/FindProductById</ofbiz:url>" style='margin: 0;'>
              <div class='tabletext'>ID Value: <input type="text" name="idValue" size="20" maxlength="50" value='<%=UtilFormatOut.checkNull(idValue)%>'>&nbsp;<a href="javascript:document.idsearchform.submit()" class="buttontext">Find</a></div>
            </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>
<div class='head1'>
    Search Results for Product with ID Value: [<%=UtilFormatOut.checkNull(idValue)%>]
</div>

<ofbiz:unless name="goodIdentifications" size="0">
  <ofbiz:unless name="idProduct">
    <br><div class='head2'>&nbsp;No results found.</div>
  </ofbiz:unless>
</ofbiz:unless>

  <table cellpadding='2'>
    <ofbiz:if name="idProduct">
        <td>
          <div class='tabletext'><b>[<ofbiz:entityfield attribute="idProduct" field="productId"/>]</b></div>
        </td>
        <td>&nbsp;&nbsp;</td>
        <td>
            <a href='<ofbiz:url>/EditProduct?productId=<ofbiz:entityfield attribute="idProduct" field="productId"/></ofbiz:url>' class='buttontext'>
              <ofbiz:entityfield attribute="idProduct" field="productName"/>
            </a> <span class='tabletext'>(ID Value was the actual productId of this product.)</span>
        </td>
    </ofbiz:if>
    <ofbiz:iterator name="goodIdentification" property="goodIdentifications">
      <%GenericValue product = goodIdentification.getRelatedOneCache("Product");%>
      <%pageContext.setAttribute("product", product);%>
      <%GenericValue goodIdentificationType = goodIdentification.getRelatedOneCache("GoodIdentificationType");%>
      <%pageContext.setAttribute("goodIdentificationType", goodIdentificationType);%>
      <tr>
        <td>
          <div class='tabletext'><b>[<ofbiz:entityfield attribute="product" field="productId"/>]</b></div>
        </td>
        <td>&nbsp;&nbsp;</td>
        <td>
            <a href='<ofbiz:url>/EditProduct?productId=<ofbiz:entityfield attribute="product" field="productId"/></ofbiz:url>' class='buttontext'>
              <ofbiz:entityfield attribute="product" field="productName"/>
            </a> <span class='tabletext'>(ID Value was the <b><ofbiz:entityfield attribute="goodIdentificationType" field="description"/></b>.)</span>
        </td>
      </tr>
    </ofbiz:iterator>
  </table>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
