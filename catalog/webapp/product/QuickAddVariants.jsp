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
    String productId = request.getParameter("productId");
    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));

    List featureTypes = new ArrayList();
    pageContext.setAttribute("featureTypes", featureTypes);
    //this will contain a list of ProductFeatureAndAppls for each feature type
    List featureTypeValues = new ArrayList();
    pageContext.setAttribute("featureTypeValues", featureTypeValues);

    //just get the selectable features
    Collection productFeatureAndAppls = EntityUtil.filterByDate(delegator.findByAnd("ProductFeatureAndAppl", 
            UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE"), 
            UtilMisc.toList("sequenceNum", "productFeatureApplTypeId", "productFeatureTypeId", "description")), true);
    if (productFeatureAndAppls != null) {
        pageContext.setAttribute("productFeatureAndAppls", productFeatureAndAppls);

        //get the list of unique feature types in the order they came from the db
        Iterator productFeatureAndApplIter = productFeatureAndAppls.iterator();
        while (productFeatureAndApplIter.hasNext()) {
            GenericValue productFeatureAndAppl = (GenericValue) productFeatureAndApplIter.next();
            String featureType = productFeatureAndAppl.getString("productFeatureTypeId");
            if (!featureTypes.contains(featureType)) {
                featureTypes.add(featureType);
            }
        }

    }

    int featureTypeSize = featureTypes.size();

    int[] indices = new int[featureTypeSize];
    //for each feature type get the list of features
    for (int i = 0; i < featureTypes.size(); i++) {
        String featureType = (String) featureTypes.get(i);
        featureTypeValues.add(i, EntityUtil.filterByAnd(productFeatureAndAppls, UtilMisc.toMap("productFeatureTypeId", featureType)));
        indices[i] = 0;
    }
%>
<br>

<%if(productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButton">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductContent?productId=<%=productId%></ofbiz:url>" class="tabButton">Content</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButton">Features</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButton">Inventory</a>
  <%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <a href="<ofbiz:url>/QuickAddVariants?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">Variants</a>
  <%}%>
  </div>
<%}%>

<div class="head1">Quick Add Variants <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<%if (product != null && !"Y".equals(product.getString("isVirtual"))) {%>
    WARNING: This product is not a virtual product, variants will not generally be used.
<%}%>

<br>
<ofbiz:if name="productFeatureAndAppls" size="0">
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <ofbiz:iterator name="featureType" property="featureTypes" type="java.lang.String">
        <td><div class="tabletext"><b><%=featureType%></b></div></td>
    </ofbiz:iterator>
    <td><div class="tabletext"><b>New Product ID and Create!</b></div></td>
    <td><div class="tabletext"><b>Existing Variant IDs:</b></div></td>
  </tr>

<%boolean carryIncrement = false;%>
<%while (true) {%>
    <tr valign="middle">
        <FORM method=POST action='<ofbiz:url>/QuickAddChosenVariant</ofbiz:url>'>
            <input type=hidden name='productId' value='<%=productId%>'>
            <input type=hidden name='featureTypeSize' value='<%=featureTypeSize%>'>
            
            <%List curProductFeatureAndAppls = new ArrayList();%>
            <%for (int featureTypeIndex = 0; featureTypeIndex < featureTypeSize; featureTypeIndex++) {%>
                <%List featureValues = (List) featureTypeValues.get(featureTypeIndex);%>
                <%GenericValue productFeatureAndAppl = (GenericValue) featureValues.get(indices[featureTypeIndex]);%>
                <%curProductFeatureAndAppls.add(productFeatureAndAppl);%>
                <td>
                    <div class='tabletext'><%=UtilFormatOut.checkNull(productFeatureAndAppl.getString("description"))%></div>
                    <input type=hidden name='feature_<%=featureTypeIndex%>' value='<%=UtilFormatOut.checkNull(productFeatureAndAppl.getString("productFeatureId"))%>'>
                </td>
                <%
                //Use the cascading index method for recursion to iteration conversion
                //here's the fun part: go through the types to increment and overflow
                if (featureTypeIndex == 0) {
                    //always increment the 0 position
                    indices[featureTypeIndex]++;
                    if (indices[featureTypeIndex] >= featureValues.size()) {
                        indices[featureTypeIndex] = 0;
                        carryIncrement = true;
                    }
                } else if (carryIncrement) {
                    //increment this position if the flag is set
                    indices[featureTypeIndex]++;
                    carryIncrement = false;

                    if (indices[featureTypeIndex] >= featureValues.size()) {
                        indices[featureTypeIndex] = 0;
                        carryIncrement = true;
                    }
                }
                %>
            <%}%>
            <td>
                <input type=text size='20' maxlength='20' name='variantProductId'>
                <INPUT type=submit value='Create!'>
            </td>
            <td>
                <div class='tabletext'>&nbsp;
                <%-- find PRODUCT_VARIANT associations that have these features as STANDARD_FEATUREs --%>
                <%
                    Collection productAssocs = EntityUtil.filterByDate(delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT")), true);
                    if (productAssocs != null && productAssocs.size() > 0) {
                        Iterator productAssocIter = productAssocs.iterator();
                        while (productAssocIter.hasNext()) {
                            GenericValue productAssoc = (GenericValue) productAssocIter.next();
                            
                            //for each associated product, if it has all standard features, display it's productId
                            boolean hasAllFeatures = true;
                            Iterator curProductFeatureAndApplIter = curProductFeatureAndAppls.iterator();
                            while (curProductFeatureAndApplIter.hasNext()) {
                                GenericValue productFeatureAndAppl = (GenericValue) curProductFeatureAndApplIter.next();
                                Map findByMap = UtilMisc.toMap("productId", productAssoc.getString("productIdTo"), 
                                        "productFeatureTypeId", productFeatureAndAppl.get("productFeatureTypeId"),
                                        "description", productFeatureAndAppl.get("description"),
                                        "productFeatureApplTypeId", "STANDARD_FEATURE");
                                //Debug.logInfo("Using findByMap: " + findByMap);

                                Collection standardProductFeatureAndAppls = EntityUtil.filterByDate(delegator.findByAnd("ProductFeatureAndAppl", findByMap), true);
                                if (standardProductFeatureAndAppls == null || standardProductFeatureAndAppls.size() == 0) {
                                    //Debug.logInfo("Does NOT have this standard feature");
                                    hasAllFeatures = false;
                                    break;
                                } else {
                                    //Debug.logInfo("DOES have this standard feature");
                                }
                            }

                            if (hasAllFeatures) {
                                %>[<a href="<ofbiz:url>/EditProduct?productId=<%=productAssoc.getString("productIdTo")%></ofbiz:url>" class="buttontext"><%=productAssoc.getString("productIdTo")%></a>] &nbsp;<%
                            }
                        }
                    }
                %>
                </div>
            </td>
        </FORM>
    </tr>
    <%-- if carryIncrement is still set then the last value turned over, so we quit... --%>
    <%if (carryIncrement) { break; }%>
<%}%>
</table>
</ofbiz:if>
<ofbiz:unless name="productFeatureAndAppls" size="0">
    <div class='tabletext'><b>No selectable features found. Please create some and try again.</b></div>
</ofbiz:unless>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
