
<%
/**
 *  Title: Upload Image Page
 *  Description: None
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
 */
%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<table cellpadding=0 cellspacing=0 border=0 width="100%"><tr><td>&nbsp;&nbsp;</td><td>
<%!
    //these should be static in the JSP context, which should make them work...
    static Object forLock = new Object();
    static Object forLock1 = new Object();
    static Object SessionIdLock = new Object();
%>
<%if(security.hasEntityPermission("CATALOG", "_VIEW", request.getSession())) {%>
<%
  String fileType = request.getParameter("upload_file_type");
  if (fileType == null || fileType.length() <= 0) fileType="small";

  String productId = request.getParameter("productId");
  GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
  if (product != null) {
%>
    <%
      String contentType = request.getContentType();
      if (contentType != null && contentType.indexOf("boundary=") > 0) {
        String fileName = "/images/catalog/product." + productId + "." + fileType + ".";
        String imageUrl = null;
    %>
      <p>Filename: <%=fileName%>
      <p>File size: <%=request.getContentLength()%>
      <p><%=request.getCharacterEncoding()%>
      <p><%=contentType%>
      <%-- <p><%=request.getInputStream()%> --%>
    <%
//===============================================================================
        String name = productId + "." + fileType;
        String dir = UtilProperties.getPropertyValue(application.getResource("/WEB-INF/catalog.properties"), 
            "image.server.path") + "/catalog";
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

        out.print("<p>The file on you computer: <b>" + clientFileName + "</b>");

        if (clientFileName.lastIndexOf(".") > 0) name += clientFileName.substring(clientFileName.indexOf("."));
        else name += ".jpg";

        fileNameToUse = "product." + name;
        out.print("<p>server file name: <b>" + fileNameToUse + "</b>");
        out.print("<p>server directory: <b>" + dir + "</b>");
        imageUrl = "/images/catalog/" + java.net.URLEncoder.encode(fileNameToUse);
        out.print("<p>The URL of your uploaded file: <b><a href=\"" + imageUrl + "\">" + imageUrl + "</a></b>");
          
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
            out.print("<p>Setting field <b>" + fileType + "ImageUrl</b> to <b>\"" + imageUrl + "\"</b>");
            product.set(fileType + "ImageUrl", imageUrl);
            product.store();
            //refresh cache value if necessary HERE
        }
    %>
    <hr>
    <a href="<ofbiz:url>/EditProduct?productId=<%=productId%></ofbiz:url>" class="buttontext">[Return to Edit Product]</a>
  <%}%>
    <form method="POST" enctype="multipart/form-data" action="<ofbiz:url>/UploadImage?productId=<%=productId%>&upload_file_type=<%=fileType%></ofbiz:url>">
    Upload a <b><%=fileType%></b> image for the product with the ID: "<b><%=productId%></b>" and Name "<b><%=product.getString("productName")%></b>".
    <br>
    Enter local file name to upload:
    <br>
    <input type="file" size="80" name="fname">
    <br>
    <input type="submit" value="Upload Now">
    </form>
  <%}else{%>
    <h3>ERROR: No product id was passed.</h3>
  <%}%>
<%}else{%>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>

</td><td>&nbsp;&nbsp;</td></tr></table>
