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

<%@ page import="java.io.*" %>

<%pageContext.setAttribute("PageName", "Upload Image");%>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%if(security.hasEntityPermission("CATALOG", "_VIEW", request.getSession())) {%>
<%
  String fileType = request.getParameter("upload_file_type");
  if(fileType == null || fileType.length() <= 0) fileType="small";

  String productId = request.getParameter("PRODUCT_ID");
  GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
  if(product != null) {
%>
    <%
      String contentType = request.getContentType();
      if(contentType != null && contentType.indexOf("boundary=") > 0)
      {
        String fileName = "/images/catalog/" + productId + "." + fileType + ".";
        String imageUrl = null;
    %>
      <p>Filename: <%=fileName%>
      <p>File size: <%=request.getContentLength()%>
      <p><%=request.getCharacterEncoding()%>
      <p><%=contentType%>
      <%-- <p><%=request.getInputStream()%> --%>
    <%
//===============================================================================
        ServletInputStream servletinputstream = request.getInputStream();
        String name = productId + "." + fileType;
        String dir = "../../ofbiz/ecommerce/images/catalog";
        String characterEncoding = request.getCharacterEncoding();

        int i1;
        if((i1 = contentType.indexOf("boundary=")) != -1)
        {
            contentType = contentType.substring(i1 + 9);
            contentType = "--" + contentType;
        }

        //these aren't static in the JSP context, which may make them not work...
        Object forLock = new Object();
        Object forLock1 = new Object();
        Object SessionIdLock = new Object();
        int HOW_LONG = 6;
        String newline = "\n";

        String s6 = null;
        String fileNameToUse = "";
        byte abyte0[] = new byte[4096];
        byte abyte1[] = new byte[4096];
        int arrayOneLength = 0;
        int arrayTwoLength = 0;
        long l = 0L;
        String idString; // = getId();
        synchronized(SessionIdLock)
        {
            long time = System.currentTimeMillis();
            Random random = new Random();
            idString = String.valueOf(time);
            for(int i2 = 1; i2 <= HOW_LONG; i2++)
                idString = idString + (int)(1.0D + (double)HOW_LONG * random.nextDouble());
        }

        String clientFileName = null;
        while((arrayOneLength = servletinputstream.readLine(abyte0, 0, abyte0.length)) > 0)
        {
            if(characterEncoding == null) clientFileName = new String(abyte0, 0, arrayOneLength);
            else clientFileName = new String(abyte0, 0, arrayOneLength, characterEncoding);

            int j = clientFileName.indexOf("filename=");
            if(j >= 0)
            {
                clientFileName = clientFileName.substring(j + 10);
                j = clientFileName.indexOf("\"");
                if(j > 0)
                {
                    clientFileName = clientFileName.substring(0, j);
                    //eat another line
                    servletinputstream.readLine(abyte0, 0, abyte0.length);
                }
                break;
            }
        }
        out.print("<p>The file on you computer: <b>" + clientFileName + "</b>");

        //fileNameToUse = clientFileName;
        if(clientFileName.lastIndexOf(".") > 0) name = name + clientFileName.substring(clientFileName.indexOf("."));
        else name = name + ".jpg";

        fileNameToUse = name;
        out.print("<p>server file name: <b>" + fileNameToUse + "</b>");
        out.print("<p>server directory: <b>" + dir + "</b>");
        imageUrl = "/images/catalog/" + fileNameToUse;
        out.print("<p>The URL of your uploaded file: <b><a href=\"" + imageUrl + "\">" + imageUrl + "</a></b>");

        if(fileNameToUse != null)
        {
            String s5 = null;
            if((arrayOneLength = servletinputstream.readLine(abyte0, 0, abyte0.length)) > 0)
            {
              if(characterEncoding == null) clientFileName = new String(abyte0, 0, arrayOneLength);
              else clientFileName = new String(abyte0, 0, arrayOneLength, characterEncoding);
            }
            if(s5 != null && s5.indexOf("Content-Type") >= 0)
            {
                servletinputstream.readLine(abyte0, 0, abyte0.length);
            }

            File file = new File(dir, idString);
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            //while((s5 = readLine(abyte0, ai, servletinputstream, characterEncoding)) != null)
            while((arrayOneLength = servletinputstream.readLine(abyte0, 0, abyte0.length)) > 0)
            {
              if(characterEncoding == null) s5 = new String(abyte0, 0, arrayOneLength);
              else s5 = new String(abyte0, 0, arrayOneLength, characterEncoding);

              l++;
              if(s5 != null && s5.indexOf(contentType) == 0 && abyte0[0] == 45)
              {
                  break;
              }
              if(s6 != null && l <= 75L)
              {
                  fileoutputstream.write(abyte1, 0, arrayTwoLength);
                  fileoutputstream.flush();
              }

              //s6 = readLine(abyte1, ai1, servletinputstream, characterEncoding);
              s6 = null;
              if((arrayTwoLength = servletinputstream.readLine(abyte1, 0, abyte1.length)) > 0)
              {
                if(characterEncoding == null) s6 = new String(abyte1, 0, arrayTwoLength);
                else s6 = new String(abyte1, 0, arrayTwoLength, characterEncoding);
              }
              if(s6 == null || s6.indexOf(contentType) == 0 && abyte1[0] == 45)
              {
                break;
              }
              fileoutputstream.write(abyte0, 0, arrayOneLength);
              fileoutputstream.flush();
            }
            byte byte0;
            if(newline.length() == 1)
                byte0 = 2;
            else
                byte0 = 1;
            if(s6 != null && abyte1[0] != 45 && arrayTwoLength > newline.length() * byte0)
                fileoutputstream.write(abyte1, 0, arrayTwoLength - newline.length() * byte0);
            if(s5 != null && abyte0[0] != 45 && arrayOneLength > newline.length() * byte0)
                fileoutputstream.write(abyte0, 0, arrayOneLength - newline.length() * byte0);
            fileoutputstream.flush();
            fileoutputstream.close();
            try
            {
                synchronized(forLock1)
                {
                    File file1 = new File(dir, fileNameToUse);
                    try
                    {
                        file1.delete();
                    }
                    catch(Exception _ex) { }
                    file.renameTo(file1);
                }
            }
            catch(Exception _ex) { }
        }

        if(imageUrl != null && imageUrl.length() > 0)
        {
          if(fileType.compareTo("large") == 0)
          {
            out.print("<p>Setting <b>large</b> image url to <b>\"" + imageUrl + "\"</b>");
            product.set("largeImageUrl", imageUrl);
          }
          else
          {
            out.print("<p>Setting <b>small</b> image url to <b>\"" + imageUrl + "\"</b>");
            product.set("smallImageUrl", imageUrl);
          }
          product.store();
          //refresh cache value if necessary HERE
        }
    %>
    <hr>
    <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Return to Edit Product]</a>
  <%}%>
    <form method="POST" enctype="multipart/form-data" action="<ofbiz:url>/UploadImage?PRODUCT_ID=<%=productId%>&upload_file_type=<%=fileType%></ofbiz:url>">
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

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
