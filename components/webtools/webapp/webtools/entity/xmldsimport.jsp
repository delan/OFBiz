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

<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.model.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<%
  String filename = request.getParameter("filename");
  boolean isUrl = request.getParameter("IS_URL")!=null?true:false;
  String fulltext = request.getParameter("fulltext");
%>

<h3>XML Import to DataSource(s)</h3>
<div>This page can be used to import exported Entity Engine XML documents. These documents all have a root tag of "&lt;entity-engine-xml&gt;".</div>
<hr>
<%if(security.hasPermission("ENTITY_MAINT", session)){%>
  <h3>Import:</h3>

  <FORM method=POST action='<ofbiz:url>/xmldsimport</ofbiz:url>'>
    <div>Absolute Filename or URL:</div>
    <INPUT type=text class='inputBox' size='60' name='filename' value='<%=UtilFormatOut.checkNull(filename)%>'> Is URL?:<INPUT type=checkbox name='IS_URL' <%=isUrl?"checked":""%>>
    <INPUT type=submit value='Import File'>
  </FORM>
  <FORM method=POST action='<ofbiz:url>/xmldsimport</ofbiz:url>'>
    <div>Complete XML document (root tag: entity-engine-xml):</div>
    <TEXTAREA class='textAreaBox' rows="8" cols="85" name='fulltext'><%=UtilFormatOut.checkNull(fulltext)%></TEXTAREA>
    <BR><INPUT type=submit value='Import Text'>
  </FORM>
  <hr>
    <h3>Results:</h3>


  <%if (filename != null && filename.length() > 0) {%>
  <%
    URL url = null;
    try { url = isUrl?new URL(filename):UtilURL.fromFilename(filename); }
    catch(java.net.MalformedURLException e) { %><div>ERROR: <%=e.toString()%></div><% }

    EntitySaxReader reader = new EntitySaxReader(delegator);
    long numberRead = reader.parse(url);
  %>
      <div>Got <%=numberRead%> entities to write to the datasource.</div>
<%-- The OLD way:
  <%
    List toBeStored = null;
    try {
      toBeStored = delegator.readXmlDocument(url);
      delegator.storeAll(toBeStored);
    } catch(Exception e) {
      %><div>ERROR: <%=e.toString()%></div><%
    }
  %>
    <%if(toBeStored != null) {%>
      <div>Got <%=toBeStored.size()%> entities to write to the datasource.</div>
    <%}else{%>
      <div>Could not get any toBeStored from the XML file.</div>
    <%}%>
--%>
  <%} else if (fulltext != null && fulltext.length() > 0) {%>
  <%
    EntitySaxReader reader = new EntitySaxReader(delegator);
    long numberRead = reader.parse(fulltext);
  %>
      <div>Got <%=numberRead%> entities to write to the datasource.</div>
<%-- The OLD way:
  <%
    List toBeStored = null;
    try {
      Document document = UtilXml.readXmlDocument(fulltext);
      toBeStored = delegator.makeValues(document);
      delegator.storeAll(toBeStored);
    }
    catch(Exception e) {
      %><div>ERROR: <%=e.toString()%></div><%
    }
  %>
    <%if(toBeStored != null) {%>
      <div>Got <%=toBeStored.size()%> entities to write to the datasource.</div>
    <%}else{%>
      <div>Could not get any toBeStored from the XML text.</div>
    <%}%>
--%>
  <%} else {%>
    <div>No filename/URL or complete XML document specified, doing nothing.</div>
  <%}%>
<%}else{%>
  <div>You do not have permission to use this page (ENTITY_MAINT needed)</div>
<%}%>
