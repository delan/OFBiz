<%@ page contentType="text/plain" %><%@ page import="java.util.*, java.io.*, java.net.*, java.sql.*, org.ofbiz.core.util.*, org.ofbiz.core.entity.*, org.ofbiz.core.entity.model.*" %><jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" /><jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" /><%

if(security.hasPermission("ENTITY_MAINT", session)) {
  String helperName = request.getParameter("helperName");
  if(helperName == null || helperName.length() <= 0) helperName = "localmysql";
  Collection messages = new LinkedList();
  GenericDAO dao = GenericDAO.getGenericDAO(helperName);
  List newEntList = dao.induceModelFromDb(messages);

  if(messages.size() > 0) {
%>
ERRORS:
<%
    Iterator mIter = messages.iterator();
    while(mIter.hasNext()) {
%>
<%=(String)mIter.next()%><%
    }
  }
  if(newEntList != null) {
    String title = "Entity of an Open For Business Project Component";
    String description = "None";
    String copyright = "Copyright (c) 2001 The Open For Business Project - www.ofbiz.org";
    String author = "None";
    String version = "1.0";
%><?xml version="1.0" encoding="UTF-8" standalone="yes"?>
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
 *@author David E. Jones (jonesde@ofbiz.org) <%-- *@created    <%=(new Date()).toString()%> --%>
 *@version    1.0
 */
-->

<!DOCTYPE entitymodel [
    <!-- ====================== Root Element ======================= -->
    <!ELEMENT entitymodel ( title?, description?, copyright?, author?, version?, entity* )>
    <!-- ================= Children of entitymodel =================== -->
    <!ELEMENT entity ( description?, field*, prim-key*, relation* )>
    <!ELEMENT title ( #PCDATA  )>
    <!ELEMENT description ( #PCDATA  )>
    <!ELEMENT copyright ( #PCDATA  )>
    <!ELEMENT author ( #PCDATA  )>
    <!ELEMENT version ( #PCDATA  )>
    <!-- ================== Children of entity ===================== -->
    <!-- see the children of entitymodel section for description, etc. -->
    <!ATTLIST entity
	entity-name CDATA #REQUIRED >
    <!ATTLIST entity
	table-name CDATA #IMPLIED >
    <!ATTLIST entity
	package-name CDATA #REQUIRED >
    <!ATTLIST entity
	dependent-on CDATA #IMPLIED >
    <!ATTLIST entity
	title CDATA #IMPLIED >
    <!ATTLIST entity
	copyright CDATA #IMPLIED >
    <!ATTLIST entity
	author CDATA #IMPLIED >
    <!ATTLIST entity
	version CDATA #IMPLIED >
    <!ELEMENT field ( validate* )>
    <!ELEMENT prim-key EMPTY>
    <!ATTLIST prim-key
	field CDATA #REQUIRED >
    <!ELEMENT relation ( key-map* )>
    <!-- ==================== Children of field ===================== -->
    <!ATTLIST field
	name CDATA #REQUIRED >
    <!ATTLIST field
	col-name CDATA #IMPLIED >
    <!ATTLIST field
	type CDATA #REQUIRED >
    <!ELEMENT validate EMPTY>
    <!ATTLIST validate
	name CDATA #REQUIRED >
    <!-- ==================== Children of relation ====================== -->
    <!-- specifies whether or not the relation is a dependent one; ie if the related entity can exist without the main entity -->
    <!ATTLIST relation
	type ( one | many ) #REQUIRED >
    <!ATTLIST relation
	title CDATA #IMPLIED >
    <!ATTLIST relation
	rel-entity-name CDATA #REQUIRED >
    <!ATTLIST relation
	rel-table-name CDATA #IMPLIED >
    <!ELEMENT key-map EMPTY>
    <!-- see definition of relation in entity section above -->
    <!-- ===================== Children of key-map ====================== -->
    <!ATTLIST key-map
	field-name CDATA #REQUIRED >
    <!ATTLIST key-map
	rel-field-name CDATA #IMPLIED >
]>

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
  <!-- ========================================================= -->

  <!-- ========================================================= -->
  <!-- No Package Name -->
  <!-- ========================================================= -->
<% 
  Iterator ecIter = newEntList.iterator();
  while(ecIter.hasNext()) {
    ModelEntity entity = (ModelEntity)ecIter.next();
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
    ModelField field = (ModelField) entity.fields.elementAt(y);%>
      <field name="<%=field.name%>"<%if(!field.name.equals(ModelUtil.dbNameToVarName(field.colName))){
      %> col-name="<%=field.colName%>"<%}%> type="<%=field.type%>"><%
    for(int v = 0; v<field.validators.size(); v++) {
      String valName = (String)field.validators.get(v);
      %><validate name="<%=valName%>" /><%
    }%></field><%
  }
  for(int y = 0; y < entity.pks.size(); y++) {
    ModelField field = (ModelField) entity.pks.elementAt(y);%>	
      <prim-key field="<%=field.name%>" /><%
  }
  if(entity.relations != null && entity.relations.size() > 0) {
    for(int r=0; r<entity.relations.size(); r++) {
      ModelRelation relation = (ModelRelation) entity.relations.elementAt(r);%>
      <relation type="<%=relation.type%>"<%if(relation.title.length() > 0){%> title="<%=relation.title%>"<%}
              %> rel-entity-name="<%=relation.relEntityName%>"<%if(!relation.relEntityName.equals(ModelUtil.dbNameToClassName(relation.relTableName))) {%>
                rel-table-name="<%=relation.relTableName%>"<%}%>><%for(int km=0; km<relation.keyMaps.size(); km++){ ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(km);%>
        <key-map field-name="<%=keyMap.fieldName%>"<%if(!keyMap.fieldName.equals(keyMap.relFieldName)){%> rel-field-name="<%=keyMap.relFieldName%>"<%}%> /><%}%>
      </relation><%
    }
  }%>
    </entity><%
  }%>  
</entitymodel>
<%
  }
} 
else {
  %>ERROR: You do not have permission to use this page (ENTITY_MAINT needed)<%
}
%>
