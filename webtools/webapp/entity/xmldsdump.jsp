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
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.model.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<%
  String filename = request.getParameter("filename");
  String[] entityName = request.getParameterValues("entityName");
  boolean checkAll = "true".equals(request.getParameter("checkAll"));
  boolean tobrowser = request.getParameter("tobrowser")!=null?true:false;
%>
<%if (tobrowser) {%>
<%
    session.setAttribute("xmlrawdump_entitylist", entityName);
%>
    <br>
    <h2>XML Export from DataSource(s)</h2>
    <div>This page can be used to export data from the database. The exported documents will have a root tag of "&lt;entity-engine-xml&gt;".</div>
    <hr>
    <%if(security.hasPermission("ENTITY_MAINT", session)) {%>
        <a href='<ofbiz:url>/xmldsrawdump</ofbiz:url>' class='buttontext' target='_blank'>Click Here to Get Data (or save to file)</a>
    <%} else {%>
      <div>You do not have permission to use this page (ENTITY_MAINT needed)</div>
    <%}%>
<%} else {%>
<%
  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entityNames = new TreeSet(ec);

  int numberOfEntities = 0;
  long numberWritten = 0;
  Document document = null;
  if(entityName != null && entityName.length > 0) {
    TreeSet passedEntityNames = new TreeSet();
    for(int inc=0; inc<entityName.length; inc++) {
      passedEntityNames.add(entityName[inc]);
    }
    
    numberOfEntities = passedEntityNames.size();
    
    document = GenericEntity.makeXmlDocument(null);
    Iterator i = passedEntityNames.iterator();
    while(i.hasNext()) { 
      String curEntityName = (String)i.next();
      Collection values = delegator.findAll(curEntityName, null);
      numberWritten += values.size();
      GenericEntity.addToXmlDocument(values, document);
    }
  }
%>
    <br>
    <h2>XML Export from DataSource(s)</h2>
    <div>This page can be used to export data from the database. The exported documents will have a root tag of "&lt;entity-engine-xml&gt;".</div>
    <hr>
    <%if(security.hasPermission("ENTITY_MAINT", session)) {%>
      <h3>Results:</h3>
    
    
      <%if(filename != null && filename.length() > 0 && document != null) {%>
        <div>Trying to Write XML for all data in <%=numberOfEntities%> entities.</div>
        <div>Trying to Write <%=numberWritten%> records to XML file <%=filename%></div>
        <%
          try { UtilXml.writeXmlDocument(filename, document); }
          catch(Exception e) {
            %><div>ERROR writing XML document: <%=e.toString()%></div><%
          }
        %>
      <%} else {%>
        <div>No filename specified or no entity names specified, doing nothing.</div>
      <%}%>
    
      <hr>
    
      <h3>Export:</h3>
      <FORM method=POST action='<ofbiz:url>/xmldsdump</ofbiz:url>'>
        <div>Filename: <INPUT type=text size='60' name='filename' value='<%=UtilFormatOut.checkNull(filename)%>'></div>
        <div>OR Out to Browser: <INPUT type=checkbox name='tobrowser' <%=tobrowser?"checked":""%>></div>
        <br>
        <div>Entity Names:</div>
        <INPUT type=submit value='Export'>
        <A href='<ofbiz:url>/xmldsdump?checkAll=true</ofbiz:url>' class='buttontext'>Check All</A>
        <A href='<ofbiz:url>/xmldsdump</ofbiz:url>' class='buttontext'>Un-Check All</A>
        <TABLE>
          <TR>
            <%Iterator iter = entityNames.iterator();%>
            <%int entCount = 0;%>
            <%while(iter.hasNext()) {%>
              <%String curEntityName = (String)iter.next();%>
              <%if(entCount % 3 == 0) {%></TR><TR><%}%>
              <%entCount++;%>
              <%-- don't check view entities... --%>
              <%boolean check = checkAll;%>
              <%if (check) {%>
                <%ModelEntity curModelEntity = delegator.getModelEntity(curEntityName);%>
                <%if (curModelEntity instanceof ModelViewEntity) check = false;%>
              <%}%>
              <TD><INPUT type=checkbox name='entityName' value='<%=curEntityName%>' <%=check?"checked":""%>><%=curEntityName%></TD>
            <%}%>
          </TR>
        </TABLE>
    
        <INPUT type=submit value='Export'>
        <A href='<ofbiz:url>/xmldsdump?checkAll=true</ofbiz:url>' class='buttontext'>Check All</A>
        <A href='<ofbiz:url>/xmldsdump</ofbiz:url>' class='buttontext'>Un-Check All</A>
      </FORM>
    <%} else {%>
      <div>You do not have permission to use this page (ENTITY_MAINT needed)</div>
    <%}%>
<%}%>
