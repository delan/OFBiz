<%
/**
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
 */
%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    String productId = request.getParameter("productId");
    String productFeatureCategoryId = request.getParameter("productFeatureCategoryId");

    GenericValue curProductFeatureCategory = delegator.findByPrimaryKey("ProductFeatureCategory", UtilMisc.toMap("productFeatureCategoryId", productFeatureCategoryId));
    //if (curProductFeatureCategory != null) pageContext.setAttribute("curProductFeatureCategory", curProductFeatureCategory);

    Collection productFeatures = delegator.findByAnd("ProductFeature",
            UtilMisc.toMap("productFeatureCategoryId", productFeatureCategoryId),
            UtilMisc.toList("productFeatureTypeId", "description"));
    if (productFeatures != null) pageContext.setAttribute("productFeatures", productFeatures);

    Collection productFeatureTypes = delegator.findAll("ProductFeatureType", UtilMisc.toList("description"));
    if (productFeatureTypes != null) pageContext.setAttribute("productFeatureTypes", productFeatureTypes);
    Collection productFeatureCategories = delegator.findAll("ProductFeatureCategory", UtilMisc.toList("description"));
    if (productFeatureCategories != null) pageContext.setAttribute("productFeatureCategories", productFeatureCategories);

    //we only need these if we will be showing the apply feature to category forms
    if (productId != null && productId.length() > 0) {
        Collection productFeatureApplTypes = delegator.findAll("ProductFeatureApplType", UtilMisc.toList("description"));
        if (productFeatureApplTypes != null) pageContext.setAttribute("productFeatureApplTypes", productFeatureApplTypes);
    }
%>
<br>

<div class="head1">Edit Features for Feature Category "<%=curProductFeatureCategory.getString("description")%>"</div>
<%if (productId != null && productId.length() > 0) {%>
<div class="head2">And Apply Features to Product with ID "<%=productId%>"</div>
<div>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="buttontext">[Return to Edit Product]</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="buttontext">[Return to Edit Product Features]</a>
</div>
<%}%>
<br>
<p class="head2">Product Feature Maintenance</p>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr class='viewOneTR1'>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>Feature&nbsp;Type</b></div></td>
    <td><div class="tabletext"><b>Feature&nbsp;Category</b></div></td>
    <td><div class="tabletext"><b>Unit of Measure ID</b></div></td>
    <td><div class="tabletext"><b>Quantity</b></div></td>
    <td><div class="tabletext"><b>DSeqNum</b></div></td>
    <td><div class="tabletext"><b>ID Code</b></div></td>
    <td><div class="tabletext"><b>Abbrev</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
    <%if (productId != null && productId.length() > 0) {%>
      </tr>
      <tr class='viewOneTR2'>
        <td><div class="tabletext">&nbsp;</div></td>
        <td><div class="tabletext"><b>Appl&nbsp;Type</b></div></td>
        <td><div class="tabletext"><b>From&nbsp;Date</b></div></td>
        <td><div class="tabletext"><b>Thru&nbsp;Date</b></div></td>
        <td><div class="tabletext">&nbsp;</div></td>
        <td><div class="tabletext"><b>Sequence</b></div></td>
        <td colspan='3'><div class="tabletext">&nbsp;</div></td>
    <%}%>
  </tr>
<ofbiz:iterator name="productFeature" property="productFeatures">
  <tr valign="middle" class='viewOneTR1'>
    <FORM method=POST action='<ofbiz:url>/UpdateProductFeature</ofbiz:url>'>
        <%if (productId != null && productId.length() > 0) {%><input type="hidden" name="productId" value="<%=productId%>"><%}%>
        <input type=hidden <ofbiz:inputvalue entityAttr="productFeature" field="productFeatureId" fullattrs="true"/>>
      <td><input type=text size='20' <ofbiz:inputvalue entityAttr="productFeature" field="description" fullattrs="true"/>></td>
      <td><select name='productFeatureTypeId' size=1>
        <%if (productFeature.get("productFeatureTypeId") != null) {%>
          <option value='<%=productFeature.getString("productFeatureTypeId")%>'> [<%=productFeature.getString("productFeatureTypeId")%>]</option>
          <option value='<%=productFeature.getString("productFeatureTypeId")%>'></option>
        <%}%>
        <ofbiz:iterator name="productFeatureType" property="productFeatureTypes">
          <option value='<%=productFeatureType.getString("productFeatureTypeId")%>'><%=productFeatureType.getString("description")%> [<%=productFeatureType.getString("productFeatureTypeId")%>]</option>
        </ofbiz:iterator>
      </select></td>
      <td><select name='productFeatureCategoryId' size=1>
        <%if (productFeature.get("productFeatureCategoryId") != null) {%>
          <%GenericValue curProdFeatCat = productFeature.getRelatedOne("ProductFeatureCategory");%>
          <option value='<%=productFeature.getString("productFeatureCategoryId")%>'><%=curProdFeatCat!=null?curProdFeatCat.getString("description"):""%> [<%=productFeature.getString("productFeatureCategoryId")%>]</option>
          <option value='<%=productFeature.getString("productFeatureCategoryId")%>'></option>
        <%}%>
        <ofbiz:iterator name="productFeatureCategory" property="productFeatureCategories">
          <option value='<%=productFeatureCategory.getString("productFeatureCategoryId")%>'><%=productFeatureCategory.getString("description")%> [<%=productFeatureCategory.getString("productFeatureCategoryId")%>]</option>
        </ofbiz:iterator>
      </select></td>
      <td><input type=text size='10' <ofbiz:inputvalue entityAttr="productFeature" field="uomId" fullattrs="true"/>></td>
      <td><input type=text size='5' <ofbiz:inputvalue entityAttr="productFeature" field="numberSpecified" fullattrs="true"/>></td>
      <td><input type=text size='5' <ofbiz:inputvalue entityAttr="productFeature" field="defaultSequenceNum" fullattrs="true"/>></td>
      <td><input type=text size='5' <ofbiz:inputvalue entityAttr="productFeature" field="idCode" fullattrs="true"/>></td>
      <td><input type=text size='5' <ofbiz:inputvalue entityAttr="productFeature" field="abbrev" fullattrs="true"/>></td>
      <td><INPUT type=submit value='Update'></td>
    </FORM>
    <%if (productId != null && productId.length() > 0) {%>
      </tr>
      <tr class='viewOneTR2'>
      <FORM method=POST action='<ofbiz:url>/ApplyFeatureToProduct</ofbiz:url>'>
        <input type=hidden name='productId' value='<%=productId%>'>
        <input type=hidden <ofbiz:inputvalue entityAttr="productFeature" field="productFeatureId" fullattrs="true"/>>
        <td><div class="tabletext">&nbsp;</div></td>
        <td>
          <select name='productFeatureApplTypeId' size=1>
            <ofbiz:iterator name="productFeatureApplType" property="productFeatureApplTypes">
              <option value='<%=productFeatureApplType.getString("productFeatureApplTypeId")%>'><%=productFeatureApplType.getString("description")%> [<%=productFeatureApplType.getString("productFeatureApplTypeId")%>]</option>
            </ofbiz:iterator>
          </select>
        </td>
        <td><input type=text size='18' name='fromDate'></td>
        <td><input type=text size='18' name='thruDate'></td>
        <td>&nbsp;</td>
        <td><input type=text size='5' name='sequenceNum' value='<ofbiz:inputvalue entityAttr="productFeature" field="defaultSequenceNum"/>'></td>
      <td colspan='3' align=left><INPUT type=submit value='Apply'></td>
      </FORM>
    <%}%>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/CreateProductFeature</ofbiz:url>" style='margin: 0;'>
  <%if (productId != null && productId.length() > 0) {%><input type="hidden" name="productId" value="<%=productId%>"><%}%>
  <input type="hidden" name="productFeatureCategoryId" value="<%=productFeatureCategoryId%>">
  <div class='head2'>Create ProductFeature in this Category:</div>
  <br>
  <table>
    <tr>
      <td><div class='tabletext'>Feature Type:</div></td>
      <td>
        <select name='productFeatureTypeId' size=1>
        <ofbiz:iterator name="productFeatureType" property="productFeatureTypes">
          <option value='<%=productFeatureType.getString("productFeatureTypeId")%>'><%=productFeatureType.getString("description")%> [<%=productFeatureType.getString("productFeatureTypeId")%>]</option>
        </ofbiz:iterator>
        </select>
      </td>
    </tr>
<%-- This will always be the same, ie we will use the productFeatureCategoryId for this page
    <tr>
      <td><div class='tabletext'>Feature Category:</div></td>
      <td><select name='productFeatureCategoryId' size=1>
        <ofbiz:iterator name="productFeatureCategory" property="productFeatureCategories">
          <option value='<%=productFeatureCategory.getString("productFeatureCategoryId")%>'><%=productFeatureCategory.getString("description")%> [<%=productFeatureCategory.getString("productFeatureCategoryId")%>]</option>
        </ofbiz:iterator>
      </select></td>
    </tr>
--%>
    <tr>
      <td><div class='tabletext'>Description:</div></td>
      <td><input type=text size='30' name='description' value=''></td>
    </tr>
    <tr>
      <td><div class='tabletext'>Unit of Measure ID:</div></td>
      <td><input type=text size='10' name='uomId' value=''></td>
    </tr>
    <tr>
      <td><div class='tabletext'>Number/Quantity:</div></td>
      <td><input type=text size='10' name='numberSpecified' value=''></td>
    </tr>
    <tr>
      <td><div class='tabletext'>Default Sequence Number:</div></td>
      <td><input type=text size='10' name='defaultSequenceNum' value=''></td>
    </tr>
    <tr>
      <td><div class='tabletext'>ID Code:</div></td>
      <td><input type=text size='10' name='idCode' value=''></td>
    </tr>
    <tr>
      <td><div class='tabletext'>Abbreviation:</div></td>
      <td><input type=text size='10' name='abbrev' value=''></td>
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
