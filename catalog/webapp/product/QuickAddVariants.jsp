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
<%try {%>
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
    Collection productFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", 
            UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE"), 
            UtilMisc.toList("sequenceNum", "productFeatureApplTypeId", "productFeatureTypeId", "description"));
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

<%if (productId != null && productId.length() > 0){%>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="buttontext">[Back to Product]</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="buttontext">[Back to Product Features]</a>
<%}%>

<div class="head1">Quick Add Variants for Product with ID "<%=UtilFormatOut.checkNull(productId)%>"</div>

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
    <td><div class="tabletext">New Product ID and Create!</div></td>
  </tr>

<%boolean carryIncrement = false;%>
<%while (true) {%>
    <tr valign="middle">
        <FORM method=POST action='<ofbiz:url>/QuickAddChosenVariant</ofbiz:url>'>
            <input type=hidden name='productId' value='<%=productId%>'>
            <%for (int featureTypeIndex = 0; featureTypeIndex < featureTypeSize; featureTypeIndex++) {%>
                <%List featureValues = (List) featureTypeValues.get(featureTypeIndex);%>
                <%GenericValue productFeatureAndAppl = (GenericValue) featureValues.get(indices[featureTypeIndex]);%>
                <td><div class='tabletext'><%=UtilFormatOut.checkNull(productFeatureAndAppl.getString("description"))%></div></td>
                <%
                //here's the fun part: go through the types backward to increment and overflow
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
                <input type=text size='20' name='variantProductId'>
                <INPUT type=submit value='Create!'>
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

<%} catch (Exception e) { Debug.logError(e); throw e; }%>
