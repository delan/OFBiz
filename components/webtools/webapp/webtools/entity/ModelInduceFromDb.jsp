<%@ page import="java.util.*, java.io.*, java.net.*, java.sql.*, org.ofbiz.base.util.*, org.ofbiz.entity.*, org.ofbiz.entity.model.*, org.ofbiz.entity.datasource.*" %><jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" /><jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" /><%

if(security.hasPermission("ENTITY_MAINT", session)) {
  String helperName = request.getParameter("helperName");
  if(helperName == null || helperName.length() <= 0) {
    response.setContentType("text/html");
%>

<div class='head3'><b>Please specify the helperName to induce from:</b></div>
<form action='' method=POST>
    <input type='TEXT' class='inputBox' size='40' name='helperName'>
    <input type=SUBMIT value='Induce!'>
</form>
<%
  } else {
      response.setContentType("text/xml");
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
%><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE entitymodel PUBLIC "-//OFBiz//DTD Entity Model//EN" "http://www.ofbiz.org/dtds/entitymodel.dtd">
<!--
 *  Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
-->

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
    ModelEntity entity = (ModelEntity) ecIter.next();
%>
    <entity entity-name="<%=entity.getEntityName()%>"<%if(!entity.getEntityName().equals(ModelUtil.dbNameToClassName(entity.getPlainTableName()))){
          %> table-name="<%=entity.getPlainTableName()%>"<%}%> 
            package-name="<%=entity.getPackageName()%>"<%if(entity.getDependentOn().length() > 0){%>
            dependent-on="<%=entity.getDependentOn()%>"<%}%><%if(!title.equals(entity.getTitle())){%>
            title="<%=entity.getTitle()%>"<%}%><%if(!copyright.equals(entity.getCopyright())){%>
            copyright="<%=entity.getCopyright()%>"<%}%><%if(!author.equals(entity.getAuthor())){%>
            author="<%=entity.getAuthor()%>"<%}%><%if(!version.equals(entity.getVersion())){%>
            version="<%=entity.getVersion()%>"<%}%>><%if(!description.equals(entity.getDescription())){%>
      <description><%=entity.getDescription()%></description><%}%><%
  for (int y = 0; y < entity.getFieldsSize(); y++) {
    ModelField field = entity.getField(y);%>
      <field name="<%=field.getName()%>"<%if(!field.getColName().equals(ModelUtil.javaNameToDbName(field.getName()))){
      %> col-name="<%=field.getColName()%>"<%}%> type="<%=field.getType()%>"><%
    for (int v = 0; v<field.getValidatorsSize(); v++) {
      String valName = (String) field.getValidator(v);
      %><validate name="<%=valName%>" /><%
    }%></field><%
  }
  for (int y = 0; y < entity.getPksSize(); y++) {
    ModelField field = entity.getPk(y);%>	
      <prim-key field="<%=field.getName()%>" /><%
  }
  if (entity.getRelationsSize() > 0) {
    for (int r = 0; r < entity.getRelationsSize(); r++) {
      ModelRelation relation = entity.getRelation(r);%>
      <relation type="<%=relation.getType()%>"<%if(relation.getTitle().length() > 0){%> title="<%=relation.getTitle()%>"<%}
              %> rel-entity-name="<%=relation.getRelEntityName()%>"><%for(int km=0; km<relation.getKeyMapsSize(); km++){ ModelKeyMap keyMap = relation.getKeyMap(km);%>
        <key-map field-name="<%=keyMap.getFieldName()%>"<%if(!keyMap.getFieldName().equals(keyMap.getRelFieldName())){%> rel-field-name="<%=keyMap.getRelFieldName()%>"<%}%> /><%}%>
      </relation><%
    }
  }%>
    </entity><%
  }%>  
</entitymodel>
<%
      }
    } 
  }
else {
  %>ERROR: You do not have permission to use this page (ENTITY_MAINT needed)<%
}
%>
