<%--
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 
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

<%@ page import="java.util.*, java.net.*, java.io.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.webapp.pseudotag.* " %>
<%@ page import="java.io.InputStream, java.io.StringWriter, java.io.FileReader, freemarker.template.*, freemarker.ext.dom.NodeModel, java.io.IOException, org.xml.sax.InputSource, freemarker.ext.beans.BeansWrapper " %>

<%@ page import="org.ofbiz.entity.model.*, org.ofbiz.entity.util.*, org.ofbiz.entity.condition.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<%
  String filename = request.getParameter("filename");
        Debug.logInfo("filename:" + filename, "JSP");
  String fmfilename = request.getParameter("fmfilename");
  boolean isUrl = request.getParameter("IS_URL") != null;

  String txTimeoutStr = UtilFormatOut.checkEmpty(request.getParameter("txTimeout"), "7200");
  Integer txTimeout = null;
  try {
      txTimeout = Integer.valueOf(txTimeoutStr);
  } catch (Exception e) {
      txTimeout = new Integer(7200);
      %><div>ERROR: TX Timeout not a valid number, setting to 7200 seconds (2 hours): <%=e%><%
  }
  boolean mostlyInserts = request.getParameter("mostlyInserts") != null;
  boolean keepStamps = request.getParameter("maintainTimeStamps") != null;
  boolean createDummyFks = request.getParameter("createDummyFks") != null;
  String fulltext = request.getParameter("fulltext");
%>

<div class="head1">XML Import to DataSource(s)</div>
<div>This page can be used to import exported Entity Engine XML documents. These documents all have a root tag of "&lt;entity-engine-xml&gt;".</div>
<hr>
<%if(security.hasPermission("ENTITY_MAINT", session)){%>
  <div class="head2">Import:</div>

  <form method="post" action="<ofbiz:url>/xmldsimport</ofbiz:url>">
    <div class="tabletext">Absolute Filename of FreeMarker template file to filter data by (optional):</div>
    <div><input type="text" class="inputBox" size="60" name="fmfilename" value="<%=UtilFormatOut.checkNull(fmfilename)%>"/></div>
    <div class="tabletext">Absolute Filename or URL:</div>
    <div><input type="text" class="inputBox" size="60" name="filename" value="<%=UtilFormatOut.checkNull(filename)%>"/></div>
    <div class="tabletext"><input type="checkbox" name="IS_URL" <%=isUrl?"checked":""%>/>Is URL?</div>
    <div class="tabletext"><input type="checkbox" name="mostlyInserts" <%=mostlyInserts?"checked":""%>/>Mostly Inserts?</div>
    <div class="tabletext"><input type="checkbox" name="maintainTimeStamps" <%=keepStamps?"checked":""%>/>Maintain Timestamps?</div>
    <div class="tabletext"><input type="checkbox" name="createDummyFks" <%=createDummyFks?"checked":""%>/>Create "Dummy" FKs?</div>
    <div class="tabletext">TX Timeout Seconds:<input type="text" size="6" value="<%=txTimeoutStr%>" name="txTimeout"/></div>
    <div><input type="submit" value="Import File"/></div>
  </form>
  <form method="post" action="<ofbiz:url>/xmldsimport</ofbiz:url>">
    <div class="tabletext">Complete XML document (root tag: entity-engine-xml):</div>
    <textarea class="textAreaBox" rows="20" cols="85" name="fulltext"><%=UtilFormatOut.checkNull(fulltext)%></textarea>
    <br/><input type="submit" value="Import Text"/>
  </form>
  <hr>
    <h3>Results:</h3>



  <%if (filename != null && filename.length() > 0) {%>
  <%
      long numberRead = -1;
      EntitySaxReader reader = new EntitySaxReader(delegator);
      if (mostlyInserts) {
        reader.setUseTryInsertMethod(true);
      }
      if (keepStamps) {
        reader.setMaintainTxStamps(keepStamps);
      }
      if (txTimeout != null) {
          reader.setTransactionTimeout(txTimeout.intValue());
      }
      if (createDummyFks) {
          reader.setCreateDummyFks(true);
      }
      URL url = null;
      try {
          url = isUrl?new URL(filename):UtilURL.fromFilename(filename);
      } catch(java.net.MalformedURLException e) {
          %><div class="tabletext">ERROR: <%=e.toString()%></div><%
      }
        Debug.logInfo("url:" + url, "JSP");

      if (UtilValidate.isNotEmpty(fmfilename)) {
        FileReader templateReader = null;
        try {
            templateReader = new FileReader(fmfilename);
        } catch(java.io.FileNotFoundException e) {
            %><div class="tabletext">ERROR: <%=e.toString()%></div><%
        }
        
        StringWriter outWriter = new StringWriter();
        Configuration conf = org.ofbiz.base.util.template.FreeMarkerWorker.makeDefaultOfbizConfig();
        
        Template template = null;
        try {
            template = new Template("FMImportFilter", templateReader, conf);
        } catch(IOException e) {
            %><div class="tabletext">ERROR: <%=e.toString()%></div><%
        }

        Map context = new HashMap();
        InputStream is = null;
        try {
            is = url.openStream();
        } catch(IOException e) {
            %><div class="tabletext">ERROR: <%=e.toString()%></div><%
        }

        NodeModel nodeModel = NodeModel.parse(new InputSource(is));
        context.put("doc", nodeModel);
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        context.put("Static", staticModels);

        template.process(context, outWriter);
        String s = outWriter.toString();
        //Debug.logInfo("filtered xml:" + s, "JSP");
        try {
            numberRead = reader.parse(s);
        } catch(Exception exc) {
            %><div class="tabletext">ERROR: <%=exc.toString()%></div><%
        }
        Debug.logInfo("numberRead(s):" + numberRead, "JSP");
      } else {
        try {
        numberRead = reader.parse(url);
        } catch(Exception exc) {
            %><div class="tabletext">ERROR: <%=exc.toString()%></div><%
        }
        Debug.logInfo("numberRead(url):" + numberRead, "JSP");
      }
  %>
      <div class="tabletext">Got <%=numberRead%> entities to write to the datasource.</div>

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
      long numberRead = -1;
      EntitySaxReader reader = new EntitySaxReader(delegator);
      if (keepStamps) {
        reader.setMaintainTxStamps(keepStamps);
      }
      if (createDummyFks) {
          reader.setCreateDummyFks(true);
      }
      if (UtilValidate.isNotEmpty(fmfilename)) {
        FileReader templateReader = null;
        try {
            templateReader = new FileReader(fmfilename);
        } catch(java.io.FileNotFoundException e) {
            %><div class="tabletext">ERROR: <%=e.toString()%></div><%
        }
        
        StringWriter outWriter = new StringWriter();
        Configuration conf = org.ofbiz.base.util.template.FreeMarkerWorker.makeDefaultOfbizConfig();
        
        Template template = null;
        try {
            template = new Template("FMImportFilter", templateReader, conf);
        } catch(IOException e) {
            %><div class="tabletext">ERROR: <%=e.toString()%></div><%
        }

        Map context = new HashMap();
        StringReader sr = new StringReader(fulltext);

        NodeModel nodeModel = NodeModel.parse(new InputSource(sr));
        context.put("doc", nodeModel);
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        context.put("Static", staticModels);

        template.process(context, outWriter);
        String s = outWriter.toString();
        //Debug.logInfo("filtered xml:" + s, "JSP");
        try {
            numberRead = reader.parse(s);
        } catch(Exception exc) {
            %><div class="tabletext">ERROR: <%=exc.toString()%></div><%
        }
        Debug.logInfo("numberRead(s):" + numberRead, "JSP");
      } else {
        try {
            numberRead = reader.parse(fulltext);
        } catch(Exception exc) {
            %><div class="tabletext">ERROR: <%=exc.toString()%></div><%
        }
        Debug.logInfo("numberRead(fulltext):" + numberRead, "JSP");
      }
  %>
      <div class="tabletext">Got <%=numberRead%> entities to write to the datasource.</div>

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
    <div class="tabletext">No filename/URL or complete XML document specified, doing nothing.</div>
  <%}%>
<%}else{%>
  <div class="tabletext">You do not have permission to use this page (ENTITY_MAINT needed)</div>
<%}%>
