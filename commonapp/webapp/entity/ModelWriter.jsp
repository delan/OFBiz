<%@ page contentType="text/plain" %><%@ page import="java.util.*, java.io.*, java.net.*, org.ofbiz.core.util.*, org.ofbiz.core.entity.*, org.ofbiz.core.entity.model.*" %><jsp:useBean id="helper" type="org.ofbiz.core.entity.GenericHelper" scope="application" /><%

if(request.getParameter("savetofile") != null)
{
  //save to the file specified in the ModelReader config
  String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);
  String filename = helper.getModelReader().entityFileName;
  
  java.net.URL url = new java.net.URL(session.getAttribute(SiteDefs.SERVER_ROOT_URL) + controlPath + "/view/ModelWriter");
  HttpClient httpClient = new HttpClient(url, new HashMap());
  InputStream in = httpClient.getStream();
  
  File newFile = new File(filename);
  FileWriter newFileWriter = new FileWriter(newFile);
  
  BufferedReader post = new BufferedReader(new InputStreamReader(in));
  String line = null;
  while((line = post.readLine()) != null) {
    newFileWriter.write(line);
    newFileWriter.write("\n");
  }
  newFileWriter.close();
%>If you aren't seeing any exceptions, XML was written successfully to:
<%=filename%>
from the URL:
<%=url.toString()%>
<%
}
else
{
  String title = "Entity of an Open For Business Project Component";
  String description = "None";
  String copyright = "Copyright (c) 2001 The Open For Business Project - www.ofbiz.org";
  String author = "David E. Jones";
  String version = "1.0";
%><?xml version="1.0"?>
<!--
/**
 *  Title: Entity Generator Definitions for the General Data Model
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
 *@author David E. Jones (jonesde@ofbiz.org)
 *@created    <%=(new Date()).toString()%>
 *@version    1.0
 */
-->
<% 
  //GenericHelper helper = GenericHelperFactory.getDefaultHelper();
  ModelReader reader = helper.getModelReader();
  Map packages = new HashMap();
  TreeSet packageNames = new TreeSet();

  //put the entityNames TreeSets in a HashMap by packageName
  Collection ec = reader.getEntityNames();
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
  }%>

<entitymodel>
  <!-- ========================================================= -->
  <!-- ======================== Defaults ======================= -->
  <!-- ========================================================= -->
    <title><%=title%></title>
    <description><%=description%></description>
    <copyright><%=copyright%></copyright>
    <author><%=author%></author>
    <version><%=version%></version>

  <!-- ========================================================= -->
  <!-- ======================== Data Model ===================== -->
  <!-- The modules in this file are as follows:                  -->
  <!--   - Common                                               -->
  <!--   - Content                                              -->
  <!--   - Party                                                -->
  <!--   - Product                                              -->
  <!--   - Order                                                -->
  <!--   - Shipment                                             -->
  <!--   - Work Effort                                          -->
  <!--   - Accounting                                           -->
  <!--   - Human Resources                                      -->
  <!--   - Security                                            -->
  <!-- ========================================================= -->
<%
  Iterator piter = packageNames.iterator();
  while(piter.hasNext())
  {
    String pName = (String)piter.next();
    TreeSet entities = (TreeSet)packages.get(pName);
%>

  <!-- ========================================================= -->
  <!-- <%=pName%> -->
  <!-- ========================================================= -->
<%
    Iterator i = entities.iterator();
    while ( i.hasNext() ) 
    {
      String entityName = (String)i.next();
      ModelEntity entity = reader.getModelEntity(entityName);
%>	
    <entity><%if(!title.equals(entity.title)){%>
      <title><%=entity.title%></title><%}%><%if(!description.equals(entity.description)){%>
      <description><%=entity.description%></description><%}%><%if(!copyright.equals(entity.copyright)){%>
      <copyright><%=entity.copyright%></copyright><%}%><%if(!author.equals(entity.author)){%>
      <author><%=entity.author%></author><%}%><%if(!version.equals(entity.version)){%>
      <version><%=entity.version%></version><%}%>
      <package-name><%=entity.packageName%></package-name>
      <table-name><%=entity.tableName%></table-name><%if(!entity.entityName.equals(ModelUtil.dbNameToClassName(entity.tableName))){%>
      <entity-name><%=entity.entityName%></entity-name><%}%><%
  for(int y = 0; y < entity.fields.size(); y++)
  {
    ModelField field = (ModelField) entity.fields.elementAt(y);%>
      <field><%if(!field.name.equals(ModelUtil.dbNameToVarName(field.colName))){
      %><name><%=field.name%></name><%}%><col-name><%=field.colName%></col-name><type><%=field.type%></type><%
    for(int v = 0; v<field.validators.size(); v++)
    {
      String valName = (String)field.validators.get(v);
      %><validate><%=valName%></validate><%
    }%></field><%
  }
  for(int y = 0; y < entity.pks.size(); y++)
  {
    ModelField field = (ModelField) entity.pks.elementAt(y);%>	
      <prim-key-col><%=field.colName%></prim-key-col><%
  }
  if( entity.relations != null && entity.relations.size() > 0 ) 
  {
    for(int r=0; r<entity.relations.size(); r++)
    {
      ModelRelation relation = (ModelRelation) entity.relations.elementAt(r);%>
      <relation>
        <type><%=relation.type%></type><%if(relation.title.length() > 0){%><title><%=relation.title%></title><%}%>
        <rel-table-name><%=relation.relTableName%></rel-table-name><%for(int km=0; km<relation.keyMaps.size(); km++){ ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(km);%><%if(keyMap.fieldName.equals(keyMap.relFieldName)){%>
        <key-map><col-name><%=keyMap.colName%></col-name></key-map><%}else{%>
        <key-map><col-name><%=keyMap.colName%></col-name><rel-col-name><%=keyMap.relColName%></rel-col-name></key-map><%}%><%}%>
      </relation><%
    }
  }%>
    </entity><%
    }
  }%>  
</entitymodel>
<%}%>
