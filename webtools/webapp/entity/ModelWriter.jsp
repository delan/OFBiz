<%@ page contentType="text/plain" %><%@ page import="java.util.*, java.io.*, java.net.*, org.ofbiz.core.util.*, org.ofbiz.core.entity.*, org.ofbiz.core.entity.model.*" %><jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" /><jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" /><%

if(security.hasPermission("ENTITY_MAINT", session) || request.getParameter("originalFileName") != null) {
  if("true".equals(request.getParameter("savetofile"))) {
    //save to the file specified in the ModelReader config
    String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);
    String serverRootUrl=(String)request.getAttribute(SiteDefs.SERVER_ROOT_URL);
    ModelReader modelReader = delegator.getModelReader();

    Map fileNameEntities = modelReader.fileNameEntities;
    Iterator filenameIter = fileNameEntities.keySet().iterator();
    while(filenameIter.hasNext()) {
      String filename = (String)filenameIter.next();

      java.net.URL url = new java.net.URL(serverRootUrl + controlPath + "/view/ModelWriter");
      HashMap params = new HashMap();
      params.put("originalFileName", filename);
      HttpClient httpClient = new HttpClient(url, params);
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
      %>
      If you aren't seeing any exceptions, XML was written successfully to:
      <%=filename%>
      from the URL:
      <%=url.toString()%>
      <%
    }
  }
  else
  {
    String title = "Entity of an Open For Business Project Component";
    String description = "None";
    String copyright = "Copyright (c) 2002 The Open For Business Project - www.ofbiz.org";
    String author = "None";
    String version = "1.0";
%><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE entitymodel PUBLIC "-//OFBiz//DTD Entity Model//EN" "http://www.ofbiz.org/dtds/entitymodel.dtd">
<!--
/**
 *  Title: Entity Generator Definitions for the General Data Model
 *  Description: None
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author David E. Jones (jonesde@ofbiz.org) <%-- *@created    <%=(new Date()).toString()%> --%>
 *@author Andy Zeneski (jaz@zsolv.com)
 *@version    1.0
 */
-->
<% 
  //GenericDelegator delegator = GenericHelperFactory.getDefaultHelper();
  ModelReader reader = delegator.getModelReader();
  Map packages = new HashMap();
  TreeSet packageNames = new TreeSet();

  //put the entityNames TreeSets in a HashMap by packageName
  Collection ec = null;

  String originalFileName = request.getParameter("originalFileName");
  if(originalFileName != null) {
    ec = (Collection)reader.fileNameEntities.get(originalFileName);
  }
  else {
    ec = reader.getEntityNames();
  }

  Iterator ecIter = ec.iterator();
  while(ecIter.hasNext()) {
    String eName = (String)ecIter.next();
    ModelEntity ent = reader.getModelEntity(eName);
    TreeSet entities = (TreeSet)packages.get(ent.packageName);
    if(entities == null) {
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
  <!-- The modules in this file are as follows:                  --><%
  Iterator packageNameIter = packageNames.iterator();
  while(packageNameIter.hasNext()) {
    String pName = (String)packageNameIter.next();%>
  <!--  - <%=pName%> --><%
  }%>
  <!-- ========================================================= -->
<%
  Iterator piter = packageNames.iterator();
  while(piter.hasNext()) {
    String pName = (String)piter.next();
    TreeSet entities = (TreeSet)packages.get(pName);
%>

  <!-- ========================================================= -->
  <!-- <%=pName%> -->
  <!-- ========================================================= -->
<%
    Iterator i = entities.iterator();
    while ( i.hasNext() ) {
      String entityName = (String)i.next();
      ModelEntity entity = reader.getModelEntity(entityName);
      if(entity instanceof ModelViewEntity) {
        ModelViewEntity viewEntity = (ModelViewEntity)entity;
%>	
    <view-entity entity-name="<%=entity.entityName%>" 
            package-name="<%=entity.packageName%>"<%if(entity.dependentOn.length() > 0){%>
            dependent-on="<%=entity.dependentOn%>"<%}%><%if(!title.equals(entity.title)){%>
            title="<%=entity.title%>"<%}%><%if(!copyright.equals(entity.copyright)){%>
            copyright="<%=entity.copyright%>"<%}%><%if(!author.equals(entity.author)){%>
            author="<%=entity.author%>"<%}%><%if(!version.equals(entity.version)){%>
            version="<%=entity.version%>"<%}%>><%if(!description.equals(entity.description)){%>
      <description><%=entity.description%></description><%}%><%
  Iterator meIter = viewEntity.memberEntities.entrySet().iterator();
  while(meIter.hasNext()) {
    Map.Entry entry = (Map.Entry)meIter.next();%>	
      <member-entity entity-alias="<%=(String)entry.getKey()%>" entity-name="<%=(String)entry.getValue()%>" /><%
  }
  for(int y=0; y<viewEntity.aliases.size(); y++) {
    ModelViewEntity.ModelAlias alias = (ModelViewEntity.ModelAlias)viewEntity.aliases.get(y);%>
      <alias entity-alias="<%=alias.entityAlias%>" name="<%=alias.name%>"<%if(!alias.name.equals(alias.field)){
      %> field="<%=alias.field%>"<%}%><%if(alias.isPk){%> prim-key="true"<%}%> /><%
  }
  for(int r=0; r<viewEntity.viewLinks.size(); r++) {
    ModelViewEntity.ModelViewLink viewLink = (ModelViewEntity.ModelViewLink)viewEntity.viewLinks.get(r);%>
      <view-link entity-alias="<%=viewLink.entityAlias%>" rel-entity-alias="<%=viewLink.relEntityAlias%>"><%for(int km=0; km<viewLink.keyMaps.size(); km++){ ModelKeyMap keyMap = (ModelKeyMap)viewLink.keyMaps.get(km);%>
        <key-map field-name="<%=keyMap.fieldName%>"<%if(!keyMap.fieldName.equals(keyMap.relFieldName)){%> rel-field-name="<%=keyMap.relFieldName%>"<%}%> /><%}%>
      </view-link><%
  }
  if(entity.relations != null && entity.relations.size() > 0) {
    for(int r=0; r<entity.relations.size(); r++) {
      ModelRelation relation = (ModelRelation) entity.relations.get(r);%>
      <relation type="<%=relation.type%>"<%if(relation.title.length() > 0){%> title="<%=relation.title%>"<%}
              %> rel-entity-name="<%=relation.relEntityName%>"><%for(int km=0; km<relation.keyMaps.size(); km++){ ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(km);%>
        <key-map field-name="<%=keyMap.fieldName%>"<%if(!keyMap.fieldName.equals(keyMap.relFieldName)){%> rel-field-name="<%=keyMap.relFieldName%>"<%}%> /><%}%>
      </relation><%
    }
  }%>
    </view-entity><%
      }
      else {
%>
    <entity entity-name="<%=entity.entityName%>"<%if(!entity.entityName.equals(ModelUtil.dbNameToClassName(entity.tableName))){
          %> table-name="<%=entity.tableName%>"<%}%> 
            package-name="<%=entity.packageName%>"<%if(entity.dependentOn.length() > 0){%>
            dependent-on="<%=entity.dependentOn%>"<%}%><%if(!title.equals(entity.title)){%>
            title="<%=entity.title%>"<%}%><%if(!copyright.equals(entity.copyright)){%>
            copyright="<%=entity.copyright%>"<%}%><%if(!author.equals(entity.author)){%>
            author="<%=entity.author%>"<%}%><%if(!version.equals(entity.version)){%>
            version="<%=entity.version%>"<%}%>><%if(!description.equals(entity.description)){%>
      <description><%=entity.description%></description><%}%><%
  for(int y = 0; y < entity.fields.size(); y++) {
    ModelField field = (ModelField) entity.fields.get(y);%>
      <field name="<%=field.name%>"<%if(!field.name.equals(ModelUtil.dbNameToVarName(field.colName))){
      %> col-name="<%=field.colName%>"<%}%> type="<%=field.type%>"><%
    for(int v = 0; v<field.validators.size(); v++) {
      String valName = (String)field.validators.get(v);
      %><validate name="<%=valName%>" /><%
    }%></field><%
  }
  for(int y = 0; y < entity.pks.size(); y++) {
    ModelField field = (ModelField) entity.pks.get(y);%>	
      <prim-key field="<%=field.name%>" /><%
  }
  if(entity.relations != null && entity.relations.size() > 0) {
    for(int r=0; r<entity.relations.size(); r++) {
      ModelRelation relation = (ModelRelation) entity.relations.get(r);%>
      <relation type="<%=relation.type%>"<%if(relation.title.length() > 0){%> title="<%=relation.title%>"<%}
              %> rel-entity-name="<%=relation.relEntityName%>"><%for(int km=0; km<relation.keyMaps.size(); km++){ ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(km);%>
        <key-map field-name="<%=keyMap.fieldName%>"<%if(!keyMap.fieldName.equals(keyMap.relFieldName)){%> rel-field-name="<%=keyMap.relFieldName%>"<%}%> /><%}%>
      </relation><%
    }
  }%>
    </entity><%
      }
    }
  }%>  
</entitymodel>
<%
  }
} 
else {
  %>ERROR: You do not have permission to use this page (ENTITY_MAINT needed)<%
}
%>
