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

<%
  String filename = request.getParameter("filename");
  String entityName = request.getParameter("entityName");
  String impExp = request.getParameter("impExp");
%>

<h2>XML DataSource Import/Export</h2>
<div>This page can be used to export data from the database, or import exported 
Entity Engine XML documents. These documents all have a root tag of "&lt;entity-engine-xml&gt;".</div>
<hr>
<h3>Export:</h3>
<FORM method=POST action='<%=response.encodeURL(controlPath + "/xmldsdump?impExp=export")%>'>
  <div>Filename: <INPUT type=text size='60' name='filename' value='<%=UtilFormatOut.checkNull(filename)%>'></div>
  <div>Entity Name (Optional): <INPUT type=text size='30' name='entityName' value='<%=UtilFormatOut.checkNull(entityName)%>'></div>
  <INPUT type=submit value='Export'>
</FORM>
<hr>
<h3>Import:</h3>

<FORM method=POST action='<%=response.encodeURL(controlPath + "/xmldsdump?impExp=import")%>'>
  <div>Filename: <INPUT type=text size='60' name='filename' value='<%=UtilFormatOut.checkNull(filename)%>'></div>
  <INPUT type=submit value='Import'>
</FORM>
<hr>
  <h3>Results:</h3>


<%if(filename != null && filename.length() > 0) {%>
  <%if("export".equals(impExp)) {%>
    <% 
      ModelReader reader = delegator.getModelReader();
      Map packages = new HashMap();
      TreeSet packageNames = new TreeSet();

      //put the entityNames TreeSets in a HashMap by packageName
      Collection ec = reader.getEntityNames();
      TreeSet entityNames = new TreeSet(ec);
      Iterator ecIter = ec.iterator();
      while(ecIter.hasNext())
      {
        String eName = (String)ecIter.next();
        ModelEntity ent = reader.getModelEntity(eName);
        TreeSet entities = (TreeSet)packages.get(ent.packageName);
        if(entities == null)
        {
          entities = new TreeSet();
          packages.put(ent.packageName, entities);
          packageNames.add(ent.packageName);
        }
        entities.add(eName);
      }
      int numberOfEntities = ec.size();
      long numberWritten = 0;
    %>

    <%=numberOfEntities%> Total Entities to write data for...<br>

    <%
      Document document = GenericEntity.makeXmlDocument(null);
      Iterator piter = packageNames.iterator();
      while(piter.hasNext())
      {
        String pName = (String)piter.next();
        TreeSet entities = (TreeSet)packages.get(pName);
        %><%-- <h3><%=pName%></h3> --%><%
        Iterator i = entities.iterator();
        while(i.hasNext()) { 
          String curEntityName = (String)i.next();
          %><%--<div>ENTITY: <%=curEntityName%></div>--%><%
          Collection values = delegator.findAll(curEntityName, null);
          numberWritten += values.size();
          GenericEntity.addToXmlDocument(values, document);
        }
      }

      GenericEntity.writeXmlDocument(filename, document);
    %>
    <div>Wrote xml for all data in <%=numberOfEntities%> entities.</div>
    <div>Wrote <%=numberWritten%> records to XML file <%=filename%></div>
  <%} else if("import".equals(impExp)) {%>
  <%}%>
<%}else{%>
  <div>No filename specified, doing nothing.</div>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %> 
<%@ include file="/includes/footer.jsp" %>
