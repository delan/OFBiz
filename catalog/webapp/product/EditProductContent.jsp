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

<%@ page import="java.util.*, java.io.*, java.net.URL" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<%!
    //these should be static in the JSP context, which should make them work...
    static Object forLock = new Object();
    static Object forLock1 = new Object();
    static Object SessionIdLock = new Object();
%>

<br>
<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    URL catalogPropertiesURL = application.getResource("/WEB-INF/catalog.properties");
    String productId = request.getParameter("productId");
    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
    if (product != null) pageContext.setAttribute("product", product);

    boolean tryEntity = true;
    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
    if (product == null) tryEntity = false;
    if("true".equalsIgnoreCase((String) request.getParameter("tryEntity"))) tryEntity = true;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));
%>

<%-- ==================================================================== --%>
<%
  String fileType = request.getParameter("upload_file_type");
  if (fileType == null || fileType.length() <= 0) fileType="small";
  if (product != null) {
      String contentType = request.getContentType();
      if (contentType != null && contentType.indexOf("boundary=") > 0) {
        String fileName = UtilProperties.getPropertyValue(catalogPropertiesURL, "image.url.prefix") + "/product." + productId + "." + fileType + ".";
        String imageUrl = null;
%>
  <div class='head3'>Result of Image Upload</div>
<%--
  <div class='tabletext'>Filename: <%=fileName%></div>
  <div class='tabletext'>Characer Encoding: <%=request.getCharacterEncoding()%></div>
  <div class='tabletext'>Content Type: <%=contentType%></div>
--%>
<%
        String name = productId + "." + fileType;
        String dir = UtilProperties.getPropertyValue(catalogPropertiesURL, "image.server.path");
        String characterEncoding = request.getCharacterEncoding();

        int i1;
        if ((i1 = contentType.indexOf("boundary=")) != -1) {
            contentType = contentType.substring(i1 + 9);
            contentType = "--" + contentType;
        }

        int HOW_LONG = 6;
        String newline = "\n";

        String s6 = null;
        String fileNameToUse = "";

        long l = 0L;
        String idString; // = getId();
        synchronized (SessionIdLock) {
            long time = System.currentTimeMillis();
            Random random = new Random();
            idString = String.valueOf(time);
            for(int i2 = 1; i2 <= HOW_LONG; i2++)
                idString = idString + (int)(1.0D + (double)HOW_LONG * random.nextDouble());
        }

        HttpRequestFileUpload uploadObject = new HttpRequestFileUpload();
        uploadObject.setOverrideFilename(idString);
        uploadObject.setSavePath(dir + System.getProperty("file.separator"));
        uploadObject.doUpload(request);

        String clientFileName = uploadObject.getFilename();
        if (clientFileName == null || clientFileName.length() == 0) {
%>
<div class='tabletext'>No file specified, not uploading.</div>
<%
        } else {
            if (clientFileName.lastIndexOf(".") > 0) name += clientFileName.substring(clientFileName.indexOf("."));
            else name += ".jpg";

            fileNameToUse = "product." + name;
            imageUrl = UtilProperties.getPropertyValue(catalogPropertiesURL, "image.url.prefix") + "/" + java.net.URLEncoder.encode(fileNameToUse);
%>
<div class='tabletext'>The file on you computer: <b><%=clientFileName%></b></div>
<%-- this isn't the real size, need to add feature to HttpRequestFileUpload to do that <div class='tabletext'>File size: <%=request.getContentLength()%></div> --%>
<div class='tabletext'>Server file name: <b><%=fileNameToUse%></b></div>
<div class='tabletext'>Server directory: <b><%=dir%></b></div>
<%-- <div class='tabletext'>The URL of your uploaded file: <b><a href="<%=imageUrl%>"><%=imageUrl%></a></b></div> --%>
<%
            try {
                File file = new File(dir, idString);
                synchronized(forLock1) {
                    File file1 = new File(dir, fileNameToUse);
                    try {
                        file1.delete();
                    } catch(Exception e) { 
                        System.out.println("error deleting existing file (not neccessarily a problem)");
                    }
                    file.renameTo(file1);
                }
            } catch(Exception e) { 
                e.printStackTrace();
            }

            if (imageUrl != null && imageUrl.length() > 0) {
                product.set(fileType + "ImageUrl", imageUrl);
                product.store();
            }
    %>
<div class='tabletext'>The image URL has been set for you (see below).</div>
<hr class='sepbar'>
    <%}%>
  <%}%>
<%}%>


<%-- ==================================================================== --%>
<SCRIPT language='JavaScript'>
function insertNowTimestamp(field) {
  eval('document.productForm.' + field + '.value="<%=UtilDateTime.nowTimestamp().toString()%>";');
};
function insertImageName(size,ext) {
  eval('document.productForm.' + size + 'ImageUrl.value="<%=UtilProperties.getPropertyValue(catalogPropertiesURL, "image.url.prefix")%>/product.<%=productId%>.' + size + '.' + ext + '";');
};
</SCRIPT>
<%if(productId != null && productId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="tabButton">Product</a>
  <a href="<ofbiz:url>/EditProductPrices?productId=<%=productId%></ofbiz:url>" class="tabButton">Prices</a>
  <a href="<ofbiz:url>/EditProductContent?productId=<%=productId%></ofbiz:url>" class="tabButtonSelected">Content</a>
  <a href="<ofbiz:url>/EditProductCategories?productId=<%=productId%></ofbiz:url>" class="tabButton">Categories</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Keywords</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Associations</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="tabButton">Attributes</a>
  <a href="<ofbiz:url>/EditProductFeatures?productId=<%=productId%></ofbiz:url>" class="tabButton">Features</a>
  <a href="<ofbiz:url>/EditProductInventoryItems?productId=<%=productId%></ofbiz:url>" class="tabButton">Inventory</a>
  <%if (product != null && "Y".equals(product.getString("isVirtual"))) {%>
    <a href="<ofbiz:url>/QuickAddVariants?productId=<%=productId%></ofbiz:url>" class="tabButton">Variants</a>
  <%}%>
  </div>
<%}%>

<div class="head1">Content <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(productId)%>]</span></div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[Product Page]</a>
<%}%>
<br>
<br>

<%if (product == null) {%>
    <h3>Could not find product with ID "<%=productId%>".</h3>
<%}else{%>
  <form action="<ofbiz:url>/updateProductContent</ofbiz:url>" method=POST style='margin: 0;' name="productForm">
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="productId" value="<%=productId%>">
  <input type=hidden name="productName" value="<%=product.getString("productName")%>">
  <input type=hidden name="productTypeId" value="<%=product.getString("productTypeId")%>">

  <tr>
    <td width="20%" align=right valign=top><div class="tabletext"><b>Detail Template</b></div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4' valign=top>
        <input type="text" <ofbiz:inputvalue entityAttr='product' field='detailTemplate' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="250" style='font-size: x-small;'>
        <br><span class='tabletext'>If not specified defaults to "/catalog/productdetail.jsp"</span>
    </td>
  </tr>

  <tr>
    <td width="20%" align=right valign=top>
        <div class="tabletext"><b>Small Image</b></div>
        <%if (UtilValidate.isNotEmpty(product.getString("smallImageUrl"))) {%>
            <a href='<ofbiz:contenturl><%=product.getString("smallImageUrl")%></ofbiz:contenturl>' target='_blank'><img alt='Small Image' src='<ofbiz:contenturl><%=product.getString("smallImageUrl")%></ofbiz:contenturl>' height='40' width='40'></a>
        <%}%>
    </td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4' valign=top>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='smallImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255" style='font-size: x-small;'>
      <%if(productId != null && productId.length() > 0) {%>
        <div>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('small', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('small', 'gif');" class="buttontext">[.gif]</a>
        </div>
      <%}%>
    </td>
  </tr>
  <tr>
    <td width="20%" align=right valign=top>
        <div class="tabletext"><b>Medium Image</b></div>
        <%if (UtilValidate.isNotEmpty(product.getString("mediumImageUrl"))) {%>
            <a href='<ofbiz:contenturl><%=product.getString("mediumImageUrl")%></ofbiz:contenturl>' target='_blank'><img alt='Medium Image' src='<ofbiz:contenturl><%=product.getString("mediumImageUrl")%></ofbiz:contenturl>' height='40' width='40'></a>
        <%}%>
    </td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4' valign=top>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='mediumImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255" style='font-size: x-small;'>
      <%if(productId != null && productId.length() > 0) {%>
        <div>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('medium', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('medium', 'gif');" class="buttontext">[.gif]</a>
        </div>
      <%}%>
    </td>
  </tr>
  <tr>
    <td width="20%" align=right valign=top>
        <div class="tabletext"><b>Large Image</b></div>
        <%if (UtilValidate.isNotEmpty(product.getString("largeImageUrl"))) {%>
            <a href='<ofbiz:contenturl><%=product.getString("largeImageUrl")%></ofbiz:contenturl>' target='_blank'><img alt='Large Image' src='<ofbiz:contenturl><%=product.getString("largeImageUrl")%></ofbiz:contenturl>' height='40' width='40'></a>
        <%}%>
    </td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4' valign=top>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='largeImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255" style='font-size: x-small;'>
      <%if(productId != null && productId.length() > 0) {%>
        <div>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('large', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('large', 'gif');" class="buttontext">[.gif]</a>
        </div>
      <%}%>
    </td>
  </tr>
  <tr>
    <td width="20%" align=right valign=top>
        <div class="tabletext"><b>Detail Image</b></div>
        <%if (UtilValidate.isNotEmpty(product.getString("detailImageUrl"))) {%>
            <a href='<ofbiz:contenturl><%=product.getString("detailImageUrl")%></ofbiz:contenturl>' target='_blank'><img alt='Detail Image' src='<ofbiz:contenturl><%=product.getString("detailImageUrl")%></ofbiz:contenturl>' height='40' width='40'></a>
        <%}%>
    </td>
    <td>&nbsp;</td>
    <td width="80%" colspan='4' valign=top>
      <input type="text" <ofbiz:inputvalue entityAttr='product' field='detailImageUrl' tryEntityAttr="tryEntity" fullattrs="true"/> size="60" maxlength="255" style='font-size: x-small;'>
      <%if(productId != null && productId.length() > 0) {%>
        <div>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('detail', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('detail', 'gif');" class="buttontext">[.gif]</a>
        </div>
      <%}%>
    </td>
  </tr>

  <tr>
    <td colspan='2'>&nbsp;</td>
    <td><input type="submit" name="Update" value="Update" style='font-size: x-small;'></td>
    <td colspan='3'>&nbsp;</td>
  </tr>
</table>
</form>
<hr class="sepbar">
<SCRIPT language='JavaScript'>
    function setUploadUrl(newUrl) {
      var toExec = 'document.imageUploadForm.action="' + newUrl + '";';
      eval(toExec);
    };
</SCRIPT>
<div class='head3'>Upload Image</div>
<form method="POST" enctype="multipart/form-data" action="<ofbiz:url>/UploadProductImage?productId=<%=productId%>&upload_file_type=small</ofbiz:url>" name='imageUploadForm'>
    <input type="file" size="50" name="fname" style='font-size: x-small;'>
    <br>
    <span class='tabletext'>
        <input type=RADIO name='upload_file_type_bogus' value='small' checked onclick='setUploadUrl("<ofbiz:url>/UploadProductImage?productId=<%=productId%>&upload_file_type=small</ofbiz:url>");'>Small
        <input type=RADIO name='upload_file_type_bogus' value='medium' onclick='setUploadUrl("<ofbiz:url>/UploadProductImage?productId=<%=productId%>&upload_file_type=medium</ofbiz:url>");'>Medium
        <input type=RADIO name='upload_file_type_bogus' value='large' onclick='setUploadUrl("<ofbiz:url>/UploadProductImage?productId=<%=productId%>&upload_file_type=large</ofbiz:url>");'>Large
        <input type=RADIO name='upload_file_type_bogus' value='detail' onclick='setUploadUrl("<ofbiz:url>/UploadProductImage?productId=<%=productId%>&upload_file_type=detail</ofbiz:url>");'>Detail
    </span>
    <input type="submit" value="Upload Image" style='font-size: x-small;'>
</form>
<%}%>
<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
