<%
/**
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
 * @author     David E. Jones
 * @author     Andy Zeneski
 * @created    June 18, 2002
 * @version    1.0
 */
%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    Collection productPriceRules = delegator.findAll("ProductPriceRule");
    if (productPriceRules != null && productPriceRules.size() > 0) pageContext.setAttribute("productPriceRules", productPriceRules);
%>

<br>

<a href="<ofbiz:url>/EditProductPriceRules</ofbiz:url>" class="buttontext">[Create Rule]</a>

<ofbiz:if name="productPriceRules">
  <table border="1" cellpadding='2' cellspacing='0'>
    <tr>
      <td><div class="tabletext"><b>ProductPriceRuleo&nbsp;ID</b></div></td>
      <td><div class="tabletext"><b>Rule&nbsp;Name</b></div></td>
      <td><div class="tabletext"><b>Sale&nbsp;Rule?</b></div></td>
      <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
      <td><div class="tabletext"><b>Thru&nbsp;Date</b></div></td>
      <td><div class="tabletext">&nbsp;</div></td>
    </tr>
    <ofbiz:iterator name="rule" property="productPriceRules">
    <tr>
      <td><div class="tabletext">&nbsp;<ofbiz:entityfield attribute="rule" field="productPriceRuleId"/></div></td>
      <td><div class="tabletext">&nbsp;<ofbiz:entityfield attribute="rule" field="ruleName"/></div></td>
      <td><div class="tabletext">&nbsp;<ofbiz:entityfield attribute="rule" field="isSale"/></div></td>
      <td><div class="tabletext">&nbsp;<ofbiz:entityfield attribute="rule" field="fromDate"/></div></td>
      <td><div class="tabletext">&nbsp;<ofbiz:entityfield attribute="rule" field="thruDate"/></div></td>
      <td>
        &nbsp;
        <a href="<ofbiz:url>/EditProductPriceRules?productPriceRuleId=<ofbiz:entityfield attribute="rule" field="productPriceRuleId"/></ofbiz:url>" class="buttontext">[Edit]</a>
        &nbsp;
        <a href='<ofbiz:url>/deleteProductPriceRule?productPriceRuleId=<ofbiz:entityfield attribute="rule" field="productPriceRuleId"/></ofbiz:url>' class="buttontext">[Delete]</a>
      </td>
    </tr>
    </ofbiz:iterator>
  </table>
</ofbiz:if>
<ofbiz:unless name="productPriceRules">
    <h3>No price rules found.</h3>
</ofbiz:unless>

<%} else {%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>