<%@ page contentType="text/plain" %><%@ page import="java.util.*, java.io.*, java.net.*, java.sql.*, org.ofbiz.core.util.*, org.ofbiz.core.entity.*, org.ofbiz.core.entity.model.*" %><jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" /><jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" /><%!
  class ColumnCheckInfo {
    public String tableName;
    public String columnName;
    public String typeName;
    public int columnSize;
    public int decimalDigits;
    public String isNullable; //YES/NO or "" = ie nobody knows
  }
%><%

if(security.hasPermission("ENTITY_MAINT", session)) {
    String helperName = request.getParameter("helperName");
    if(helperName == null || helperName.length() <= 0) helperName = "localmysql";
    ModelFieldTypeReader fieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(helperName);
    Collection messages = new LinkedList();

    Connection connection = null;
    try { connection = ConnectionFactory.getConnection(helperName); }
    catch(SQLException sqle) {
      String message = "Unable to esablish a connection with the database... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.checkDb] " + message);
      if(messages != null) messages.add(message);
      return;
    }
    
    DatabaseMetaData dbData = null;
    try { dbData = connection.getMetaData(); }
    catch(SQLException sqle) {
      String message = "Unable to get database meta data... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.checkDb] " + message);
      if(messages != null) messages.add(message);
      return;
    }
    
    //get ALL tables from this database
    TreeSet tableNames = new TreeSet();
    ResultSet tableSet = null;
    try { tableSet = dbData.getTables(null, null, null, null); }
    catch(SQLException sqle) {
      String message = "Unable to get list of table information... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.checkDb] " + message);
      if(messages != null) messages.add(message);
      return;
    }
    
    try {
      while(tableSet.next()) {
        try {
          String tableName = tableSet.getString("TABLE_NAME");
          String tableType = tableSet.getString("TABLE_TYPE");
          //String remarks = tableSet.getString("REMARKS");
          tableNames.add(tableName.toUpperCase());
          //Debug.logInfo("[GenericDAO.checkDb] Found table named \"" + tableName + "\" of type \"" + tableType + "\" with remarks: " + remarks);
        }
        catch(SQLException sqle) {
          String message = "Error getting table information... Error was:" + sqle.toString();
          Debug.logError("[GenericDAO.checkDb] " + message);
          if(messages != null) messages.add(message);
          continue;
        }
      }
    }
    catch(SQLException sqle) {
      String message = "Error getting next table information... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.checkDb] " + message);
      if(messages != null) messages.add(message);
      return;
    }
    
    try{ tableSet.close(); }
    catch(SQLException sqle) {
      String message = "Unable to close ResultSet for table list, continuing anyway... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.checkDb] " + message);
      if(messages != null) messages.add(message);
    }
    
    //get ALL column info, put into hashmap by table name
    Map colInfo = new HashMap();
    try {
      ResultSet rsCols = dbData.getColumns(null, null, null, null);
      while(rsCols.next()) {
        try {
          ColumnCheckInfo ccInfo = new ColumnCheckInfo();
          ccInfo.tableName = rsCols.getString("TABLE_NAME").toUpperCase();
          ccInfo.columnName = rsCols.getString("COLUMN_NAME").toUpperCase();
          ccInfo.typeName = rsCols.getString("TYPE_NAME").toUpperCase();
          ccInfo.columnSize = rsCols.getInt("COLUMN_SIZE");
          ccInfo.decimalDigits = rsCols.getInt("DECIMAL_DIGITS");
          ccInfo.isNullable = rsCols.getString("IS_NULLABLE").toUpperCase();
          
          List tableColInfo = (List)colInfo.get(ccInfo.tableName);
          if(tableColInfo == null) {
            tableColInfo = new Vector();
            colInfo.put(ccInfo.tableName, tableColInfo);
          }
          tableColInfo.add(ccInfo);
        }
        catch(SQLException sqle) {
          String message = "Error getting column info for column. Error was:" + sqle.toString();
          Debug.logError("[GenericDAO.checkDb] " + message);
          if(messages != null) messages.add(message);
          continue;
        }
      }
      
      try{ rsCols.close(); }
      catch(SQLException sqle) {
        String message = "Unable to close ResultSet for column list, continuing anyway... Error was:" + sqle.toString();
        Debug.logError("[GenericDAO.checkDb] " + message);
        if(messages != null) messages.add(message);
      }
    }
    catch(SQLException sqle) {
      String message = "Error getting column meta data for Error was:" + sqle.toString() + ". Not checking columns.";
      Debug.logError("[GenericDAO.checkDb] " + message);
      if(messages != null) messages.add(message);
      colInfo = null;
    }

    //go through each table and make a ModelEntity object, add to list
    //for each entity make corresponding ModelField objects
    //then print out XML for the entities/fields
    List newEntList = new LinkedList();
    
    //iterate over the table names is alphabetical order
    Iterator tableNamesIter = new TreeSet(colInfo.keySet()).iterator();
    while(tableNamesIter.hasNext()) {
      String tableName = (String)tableNamesIter.next();
      Vector colList = (Vector)colInfo.get(tableName);

      ModelEntity newEntity = new ModelEntity();
      newEntity.tableName = tableName.toUpperCase();
      newEntity.entityName = ModelUtil.dbNameToClassName(newEntity.tableName);
      newEntList.add(newEntity);

      Iterator columns = colList.iterator();
      while(columns.hasNext()) {
        ColumnCheckInfo ccInfo = (ColumnCheckInfo)columns.next();
        ModelField newField = new ModelField();
        newEntity.fields.add(newField);
        newField.colName = ccInfo.columnName.toUpperCase();
        newField.name =  ModelUtil.dbNameToVarName(newField.colName);
        
        //figure out the type according to the typeName, columnSize and decimalDigits
        newField.type = ModelUtil.induceFieldType(ccInfo.typeName, ccInfo.columnSize, ccInfo.decimalDigits, fieldTypeReader);
        
        //how do we find out if it is a primary key? for now, if not nullable, assume it is a pk
        //this is a bad assumption, but since this output must be edited by hand later anyway, oh well
        if("NO".equals(ccInfo.isNullable)) newField.isPk = true;
        else newField.isPk = false;
      }
      newEntity.updatePkLists();
    }

    try{ connection.close(); }
    catch(SQLException sqle) {
      String message = "Unable to close database connection, continuing anyway... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.checkDb] " + message);
      if(messages != null) messages.add(message);
    }
  
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
  else {
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
