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
    Collection productGlAccounts = product.getRelated("ProductGlAccount", null, UtilMisc.toList("productGlAccountTypeId"));
    if (productGlAccounts != null) pageContext.setAttribute("productGlAccounts", productGlAccounts);

    Collection productGlAccountTypes = delegator.findAllCache("ProductGlAccountType", UtilMisc.toList("description"));
    if (productGlAccountTypes != null) pageContext.setAttribute("productGlAccountTypes", productGlAccountTypes);

    Collection glAccounts = delegator.findAllCache("GlAccount", UtilMisc.toList("accountCode"));
    if (glAccounts != null) pageContext.setAttribute("glAccounts", glAccounts);

    if ("true".equalsIgnoreCase((String)request.getParameter("useValues"))) useValues = true;
%>

<br>
<%if(productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButton">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductContent?productId=<%=productId%></ofbiz:url>" class="tabButton">Content</a>
  <a href="<ofbiz:url>/EditProductGoodIdentifications?productId=<%=productId%></ofbiz:url>" class="tabButton">IDs</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButton">Features</a>
  <a href="<ofbiz:url>/EditProductFacilities?productId=<%=productId%></ofbiz:url>" class="tabButton">Facilities</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButton">Inventory</a>
  <a href="<ofbiz:url>/EditProductGlAccounts?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">Accounts</a>
  <%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <a href="<ofbiz:url>/QuickAddVariants?productId=<%=productId%></ofbiz:url>" class="tabButton">Variants</a>
  <%}%>
  </div>
<%}%>

<div class="head1">GL Accounts <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
<%}%>
<br>
<br>

<%if(productId!=null && product!=null){%>
<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>Account&nbsp;Type</b></div></td>
    <td align="center"><div class="tabletext"><b>GL&nbsp;Account</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<%int line = 0;%>
<ofbiz:iterator name="productGlAccount" property="productGlAccounts">
  <%line++;%>
  <%GenericValue productGlAccountType = productGlAccount.getRelatedOneCache("ProductGlAccountType");%>
  <%GenericValue curGlAccount = productGlAccount.getRelatedOneCache("GlAccount");%>
  <%if (curGlAccount != null) pageContext.setAttribute("curGlAccount", curGlAccount);%>
  <tr valign="middle">
    <td><div class='tabletext'><%if (productGlAccountType != null) {%><%=productGlAccountType.getString("description")%><%} else {%>[<ofbiz:inputvalue entityAttr="productGlAccount" field="productGlAccountTypeId"/>]<%}%></div></td>
    <td align="center">
        <FORM method=POST action='<ofbiz:url>/updateProductGlAccount</ofbiz:url>' name='lineForm<%=line%>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="productGlAccount" field="productId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productGlAccount" field="productGlAccountTypeId" fullattrs="true"/>>
	        <select name="glAccountId" style='font-size: x-small;'>
	        	<%if (curGlAccount != null) {%>
	                <option value='<ofbiz:entityfield attribute="curGlAccount" field="glAccountId"/>'><ofbiz:entityfield attribute="curGlAccount" field="accountCode"/> <ofbiz:entityfield attribute="curGlAccount" field="accountName"/><%--[<ofbiz:entityfield attribute="curGlAccount" field="glAccountId"/>]--%></option>
	                <option value='<ofbiz:entityfield attribute="curGlAccount" field="glAccountId"/>'>-----</option>
	            <%}%>
	            <ofbiz:iterator name="glAccount" property="glAccounts">
	                <option value='<ofbiz:entityfield attribute="glAccount" field="glAccountId"/>'><ofbiz:entityfield attribute="glAccount" field="accountCode"/> <ofbiz:entityfield attribute="glAccount" field="accountName"/><%--[<ofbiz:entityfield attribute="glAccount" field="glAccountId"/>]--%></option>
	            </ofbiz:iterator>
	        </select>
            <input type=text size='20' <ofbiz:inputvalue entityAttr="productGlAccount" field="idValue" fullattrs="true"/> style='font-size: x-small;'>
            <INPUT type=submit value='Update' style='font-size: x-small;'>
        </FORM>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/deleteProductGlAccount?productId=<ofbiz:entityfield attribute="productGlAccount" field="productId"/>&productGlAccountTypeId=<ofbiz:entityfield attribute="productGlAccount" field="productGlAccountTypeId"/></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/createProductGlAccount</ofbiz:url>" style='margin: 0;' name='createProductGlAccountForm'>
    <input type="hidden" name="productId" value="<%=productId%>">
    <input type="hidden" name="useValues" value="true">

    <div class='head2'>Add GL Account:</div>
    <div class='tabletext'>
        Account Type:
        <select name="productGlAccountTypeId" style='font-size: x-small;'>
            <ofbiz:iterator name="productGlAccountType" property="productGlAccountTypes">
                <option value='<ofbiz:entityfield attribute="productGlAccountType" field="productGlAccountTypeId"/>'><ofbiz:entityfield attribute="productGlAccountType" field="description"/><%--[<ofbiz:entityfield attribute="productGlAccountType" field="productGlAccountTypeId"/>]--%></option>
            </ofbiz:iterator>
        </select>
        GL Account: 
        <select name="glAccountId" style='font-size: x-small;'>
            <ofbiz:iterator name="glAccount" property="glAccounts">
                <option value='<ofbiz:entityfield attribute="glAccount" field="glAccountId"/>'><ofbiz:entityfield attribute="glAccount" field="accountCode"/> <ofbiz:entityfield attribute="glAccount" field="accountName"/><%--[<ofbiz:entityfield attribute="glAccount" field="glAccountId"/>]--%></option>
            </ofbiz:iterator>
        </select>
        <input type="submit" value="Add" style='font-size: x-small;'>
    </div>

</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
