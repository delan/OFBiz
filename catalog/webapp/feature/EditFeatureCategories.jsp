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
 *@created    April 4, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    Collection productFeatureCategories = delegator.findAll("ProductFeatureCategory", UtilMisc.toList("description"));
    if (productFeatureCategories != null) pageContext.setAttribute("productFeatureCategories", productFeatureCategories);
%>
<br>

<div class="head1">Product Feature Categories</div>

<br>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>ID</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>Parent&nbsp;Category</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="productFeatureCategory" property="productFeatureCategories">
  <tr valign="middle">
    <FORM method=POST action='<ofbiz:url>/UpdateFeatureCategory</ofbiz:url>'>
        <input type=hidden <ofbiz:inputvalue entityAttr="productFeatureCategory" field="productFeatureCategoryId" fullattrs="true"/>>
    <td><a href='<ofbiz:url>/EditFeatureCategoryFeatures?productFeatureCategoryId=<ofbiz:inputvalue entityAttr="productFeatureCategory" field="productFeatureCategoryId"/></ofbiz:url>' class="buttontext"><ofbiz:entityfield attribute="productFeatureCategory" field="productFeatureCategoryId"/></a></td>
    <td><input type=text size='30' <ofbiz:inputvalue entityAttr="productFeatureCategory" field="description" fullattrs="true"/>></td>
    <td>
      <select name='parentCategoryId' size=1>
        <%if (productFeatureCategory.get("parentCategoryId") != null) {%>
          <%GenericValue curProdFeatCat = delegator.findByPrimaryKey("ProductFeatureCategory", UtilMisc.toMap("productFeatureCategoryId", productFeatureCategory.get("parentCategoryId")));%>
          <%if (curProdFeatCat != null) {%>
            <option value='<%=curProdFeatCat.getString("productFeatureCategoryId")%>'><%=curProdFeatCat.getString("description")%><%-- [<%=curProdFeatCat.getString("productFeatureCategoryId")%>]--%></option>
            <option value='<%=curProdFeatCat.getString("productFeatureCategoryId")%>'>&nbsp;</option>
          <%}%>
        <%}%>
        <ofbiz:iterator name="dropDownProductFeatureCategory" property="productFeatureCategories">
          <option value='<%=dropDownProductFeatureCategory.getString("productFeatureCategoryId")%>'><%=dropDownProductFeatureCategory.getString("description")%><%-- [<%=dropDownProductFeatureCategory.getString("productFeatureCategoryId")%>]--%></option>
        </ofbiz:iterator>
      </select>
    </td>
    <td><INPUT type=submit value='Update'></td>
    <td><a href='<ofbiz:url>/EditFeatureCategoryFeatures?productFeatureCategoryId=<ofbiz:inputvalue entityAttr="productFeatureCategory" field="productFeatureCategoryId"/></ofbiz:url>' class="buttontext">[Edit]</a></td>
    </FORM>
  </tr>
</ofbiz:iterator>
</table>
<br>

<form method="POST" action="<ofbiz:url>/CreateFeatureCategory</ofbiz:url>" style='margin: 0;'>
  <div class='head2'>Create a Product Feature Category:</div>
  <br>
  <table>
    <tr>
      <td><div class='tabletext'>Description:</div></td>
      <td><input type=text size='30' name='description' value=''></td>
    </tr>
    <tr>
      <td><div class='tabletext'>Parent Category:</div></td>
      <td><select name='parentCategoryId' size=1>
        <option value=''>&nbsp;</option>
        <ofbiz:iterator name="productFeatureCategory" property="productFeatureCategories">
          <option value='<%=productFeatureCategory.getString("productFeatureCategoryId")%>'><%=productFeatureCategory.getString("description")%> [<%=productFeatureCategory.getString("productFeatureCategoryId")%>]</option>
        </ofbiz:iterator>
      </select></td>
    </tr>
    <tr>
      <td colspan='2'><input type="submit" value="Create"></td>
    </tr>
  </table>
</form>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
