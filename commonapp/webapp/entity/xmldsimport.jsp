<%--
 *  Copyright (c) 2001 The Open For Business Project and respected authors.
 
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
 * @author David E. Jones (jonesde@ofbiz.org)
 * @version 1.0
--%>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%@ page import="org.ofbiz.core.entity.model.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="java.net.*" %>

<%
  String filename = request.getParameter("filename");
  String entityName = request.getParameter("entityName");
%>

<h2>XML Import to DataSource(s)</h2>
<div>This page can be used to import exported Entity Engine XML documents. These documents all have a root tag of "&lt;entity-engine-xml&gt;".</div>
<hr>
<%if(security.hasPermission("ENTITY_MAINT", session)){%>
  <h3>Import:</h3>

  <FORM method=POST action='<%=response.encodeURL(controlPath + "/xmldsimport")%>'>
    <div>Absolute Filename or URL:</div>
    <INPUT type=text size='60' name='filename' value='<%=UtilFormatOut.checkNull(filename)%>'>
    <INPUT type=submit value='Import'>
  </FORM>
  <hr>
    <h3>Results:</h3>


  <%if(filename != null && filename.length() > 0) {%>
  <%
    URL url = null;
    if(filename.charAt(0) == '/') {
      url = UtilURL.fromFilename(filename);
    }
    else {
      url = new URL(filename);
    }

    Collection values = null;
    try {
      values = delegator.readXmlDocument(url);
      delegator.storeAll(values);
    }
    catch(Exception e) {
      %><div>ERROR: <%=e.toString()%></div><%
    }
  %>
    <%if(values != null) {%>
      <div>Wrote <%=values.size()%> entities to the datasource.</div>
    <%}else{%>
      <div>Could not get any values from the XML file.</div>
    <%}%>
  <%}else{%>
    <div>No filename/URL specified, doing nothing.</div>
  <%}%>
<%}else{%>
  <div>You do not have permission to use this page (ENTITY_MAINT needed)</div>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %> 
<%@ include file="/includes/footer.jsp" %>
