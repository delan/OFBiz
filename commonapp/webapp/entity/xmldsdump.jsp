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
  String[] entityName = request.getParameterValues("entityName");
  boolean checkAll = "true".equals(request.getParameter("checkAll"));

  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entityNames = new TreeSet(ec);
%>
<br>
<h2>XML Export from DataSource(s)</h2>
<div>This page can be used to export data from the database. The exported documents will have a root tag of "&lt;entity-engine-xml&gt;".</div>
<hr>
  <h3>Results:</h3>


<%if(filename != null && filename.length() > 0 && entityName != null && entityName.length > 0) {%>
  <% 
    TreeSet passedEntityNames = new TreeSet();
    for(int inc=0; inc<entityName.length; inc++) {
      passedEntityNames.add(entityName[inc]);
    }

    int numberOfEntities = passedEntityNames.size();
    long numberWritten = 0;
  %>

  <%
    Document document = GenericEntity.makeXmlDocument(null);
    Iterator i = passedEntityNames.iterator();
    while(i.hasNext()) { 
      String curEntityName = (String)i.next();
      %><%--<div>ENTITY: <%=curEntityName%></div>--%><%
      Collection values = delegator.findAll(curEntityName, null);
      numberWritten += values.size();
      GenericEntity.addToXmlDocument(values, document);
    }
  %>
  <div>Trying to Write XML for all data in <%=numberOfEntities%> entities.</div>
  <div>Trying to Write <%=numberWritten%> records to XML file <%=filename%></div>
  <%
    try { GenericEntity.writeXmlDocument(filename, document); }
    catch(Exception e) {
      %><div>ERROR writing XML document: <%=e.toString()%></div><%
    }
  %>
<%}else{%>
  <div>No filename specified or no entity names specified, doing nothing.</div>
<%}%>

<hr>

<h3>Export:</h3>
<FORM method=POST action='<%=response.encodeURL(controlPath + "/xmldsdump")%>'>
  <div>Filename: <INPUT type=text size='60' name='filename' value='<%=UtilFormatOut.checkNull(filename)%>'></div>
  <br>
  <div>Entity Names:</div>
  <INPUT type=submit value='Export'>
  <A href='<%=response.encodeURL(controlPath + "/xmldsdump?checkAll=true")%>'>Check All</A>
  <A href='<%=response.encodeURL(controlPath + "/xmldsdump")%>'>Un-Check All</A>
  <TABLE>
    <TR>
      <%Iterator iter = entityNames.iterator();%>
      <%int entCount = 0;%>
      <%while(iter.hasNext()) {%>
        <%String curEntityName = (String)iter.next();%>
        <%if(entCount % 3 == 0) {%></TR><TR><%}%>
        <%entCount++;%>
        <TD><INPUT type=checkbox name='entityName' value='<%=curEntityName%>'<%=checkAll?"checked":""%>><%=curEntityName%></TD>
      <%}%>
    </TR>
  </TABLE>

  <INPUT type=submit value='Export'>
</FORM>

<%@ include file="/includes/onecolumnclose.jsp" %> 
<%@ include file="/includes/footer.jsp" %>
