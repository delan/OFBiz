<%--
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*, java.net.URL" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    boolean useValues = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

    String productId = request.getParameter("productId");
    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
    if (product == null) useValues = false;
    Collection goodIdentifications = product.getRelated("GoodIdentification", null, UtilMisc.toList("goodIdentificationTypeId", "idValue"));
    if (goodIdentifications != null) pageContext.setAttribute("goodIdentifications", goodIdentifications);

    Collection goodIdentificationTypes = delegator.findAllCache("GoodIdentificationType", UtilMisc.toList("description"));
    if (goodIdentificationTypes != null) pageContext.setAttribute("goodIdentificationTypes", goodIdentificationTypes);

    if ("true".equalsIgnoreCase((String)request.getParameter("useValues"))) useValues = true;
%>

<%if(productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButton">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductContent?productId=<%=productId%></ofbiz:url>" class="tabButton">Content</a>
  <a href="<ofbiz:url>/EditProductGoodIdentifications?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">IDs</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButton">Features</a>
  <a href="<ofbiz:url>/EditProductFacilities?productId=<%=productId%></ofbiz:url>" class="tabButton">Facilities</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButton">Inventory</a>
  <a href="<ofbiz:url>/EditProductGlAccounts?productId=<%=productId%></ofbiz:url>" class="tabButton">Accounts</a>
  <%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <a href="<ofbiz:url>/QuickAddVariants?productId=<%=productId%></ofbiz:url>" class="tabButton">Variants</a>
  <%}%>
  </div>
<%}%>

<div class="head1">IDs <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
<%}%>
<br>
<br>

<%if(productId!=null && product!=null){%>
<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>ID&nbsp;Type</b></div></td>
    <td align="center"><div class="tabletext"><b>ID&nbsp;Value</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<%int line = 0;%>
<ofbiz:iterator name="goodIdentification" property="goodIdentifications">
  <%line++;%>
  <%GenericValue goodIdentificationType = goodIdentification.getRelatedOneCache("GoodIdentificationType");%>
  <tr valign="middle">
    <td><div class='tabletext'><%if (goodIdentificationType != null) {%><%=goodIdentificationType.getString("description")%><%} else {%>[<ofbiz:inputvalue entityAttr="goodIdentification" field="goodIdentificationTypeId"/>]<%}%></div></td>
    <td align="center">
        <FORM method=POST action='<ofbiz:url>/updateGoodIdentification</ofbiz:url>' name='lineForm<%=line%>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="goodIdentification" field="productId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="goodIdentification" field="goodIdentificationTypeId" fullattrs="true"/>>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="goodIdentification" field="idValue" fullattrs="true"/> class='inputBox'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/deleteGoodIdentification?productId=<ofbiz:entityfield attribute="goodIdentification" field="productId"/>&goodIdentificationTypeId=<ofbiz:entityfield attribute="goodIdentification" field="goodIdentificationTypeId"/></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/createGoodIdentification</ofbiz:url>" style='margin: 0;' name='createGoodIdentificationForm'>
    <input type="hidden" name="productId" value="<%=productId%>">
    <input type="hidden" name="useValues" value="true">

    <div class='head2'>Add ID:</div>
    <div class='tabletext'>
        ID Type:
        <select name="goodIdentificationTypeId" class='selectBox'>
            <ofbiz:iterator name="goodIdentificationType" property="goodIdentificationTypes">
                <option value='<ofbiz:entityfield attribute="goodIdentificationType" field="goodIdentificationTypeId"/>'><ofbiz:entityfield attribute="goodIdentificationType" field="description"/><%--[<ofbiz:entityfield attribute="goodIdentificationType" field="goodIdentificationTypeId"/>]--%></option>
            </ofbiz:iterator>
        </select>
        ID Value: <input type=text size='20' name='idValue' class='inputBox'>&nbsp;<input type="submit" value="Add" style='font-size: x-small;'>
    </div>

</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
