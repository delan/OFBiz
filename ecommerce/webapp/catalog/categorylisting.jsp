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
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*, org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
    String productCategoryId = (String) request.getAttribute("productCategoryId");
    GenericValue productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
    if (productCategory != null) {
        //now figure out which template to use for this product...
        String detailTemplate = productCategory.getString("detailTemplate");
        if (UtilValidate.isEmpty(detailTemplate)) {
            detailTemplate = "/catalog/categorydetail.jsp";
        }
        String templatePathPrefix = CatalogWorker.getTemplatePathPrefix(pageContext);
        if (Debug.infoOn()) Debug.logInfo("Catalog template: prefix=" + templatePathPrefix + ", template=" + detailTemplate);
        detailTemplate = templatePathPrefix + detailTemplate;

        RequestDispatcher rd = null;
        try {
            rd = application.getRequestDispatcher(detailTemplate);
        } catch (Exception e) {
            Debug.logError(e, "Error getting request dispatcher");
        }
        if (rd != null) {
            try {
                rd.include(request, response);
            } catch (java.io.FileNotFoundException e) {
                Debug.logError(e, "Error dispatching request");
%>
    <br>
    <center><div class='head2'>ERROR: The template for this category was not found at <%=detailTemplate%>.</div></center>
    <center><div class='head2'>Please try back later.</div></center>
<%
            }
        } else {
            Debug.logError("ERROR: The template for this category was not found at " + detailTemplate);
%>
    <br>
    <center><div class='head2'>ERROR: The template for this category was not found at <%=detailTemplate%>.</div></center>
    <center><div class='head2'>Please try back later.</div></center>
<%
        }
    } else {
%>
    <br>
    <center><div class='head2'>Could not find category with ID <%=productCategoryId%>.</div></center>
<%}%>
