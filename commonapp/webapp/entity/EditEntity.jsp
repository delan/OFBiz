<%@page contentType="text/html"%>
<!--
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @author David E. Jones (jonesde@ofbiz.org)
 * @version 1.0
-->

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.entity.model.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<%
if(security.hasPermission("ENTITY_MAINT", session)) {
  String entityName = request.getParameter("entityName");
  ModelReader reader = delegator.getModelReader();
  ModelEntity entity = reader.getModelEntity(entityName);
  TreeSet entSet = new TreeSet(reader.getEntityNames());
  String errorMsg = "";

  String event = request.getParameter("event");
  if("addEntity".equals(event)) {
    if(entity == null) {
      entity = new ModelEntity();
      entity.entityName = request.getParameter("entityName");
      entity.tableName = ModelUtil.javaNameToDbName(entity.entityName);
      reader.entityCache.put(entity.entityName, entity);
      entityName = entity.entityName;
      
      String entityGroup = request.getParameter("entityGroup");
      delegator.getModelGroupReader().getGroupCache().put(entityName, entityGroup);
    }
  }
  else if("updateEntity".equals(event)) {
    entity.tableName = request.getParameter("tableName");
    entity.packageName = request.getParameter("packageName");
    entity.dependentOn = request.getParameter("dependentOn");
    entity.title = request.getParameter("title");
    entity.description = request.getParameter("description");
    entity.copyright = request.getParameter("copyright");
    entity.author = request.getParameter("author");
    entity.version = request.getParameter("version");

    String entityGroup = request.getParameter("entityGroup");
    delegator.getModelGroupReader().getGroupCache().put(entityName, entityGroup);

    String filename = request.getParameter("filename");
    delegator.getModelReader().entityFile.put(entityName, filename);
    delegator.getModelReader().rebuildFileNameEntities();
  }
  else if("removeField".equals(event)) {
    String fieldName = request.getParameter("fieldName");
    entity.removeField(fieldName);
  }
  else if("updateField".equals(event)) {
    String fieldName = request.getParameter("fieldName");
    String fieldType = request.getParameter("fieldType");
    String primaryKey = request.getParameter("primaryKey");
    ModelField field = entity.getField(fieldName);
    field.type = fieldType;
    if(primaryKey != null) field.isPk = true;
    else field.isPk = false;
    entity.updatePkLists();
  }
  else if("addField".equals(event)) {
    ModelField field = new ModelField();
    field.name = request.getParameter("name");
    field.colName = ModelUtil.javaNameToDbName(field.name);
    field.type = request.getParameter("fieldType");
    entity.fields.add(field);
  }
  else if("addRelation".equals(event)) {
    String relEntityName = request.getParameter("relEntityName");
    String type = request.getParameter("type");
    String title = request.getParameter("title");
    ModelRelation relation = new ModelRelation();

    ModelEntity relEntity = reader.getModelEntity(relEntityName);
    if(relEntity == null) errorMsg = errorMsg + "<li> Related Entity \"" + relEntityName + "\" not found, not adding.";
    else {
      relation.relEntityName = relEntityName;
      relation.relTableName = relEntity.tableName;
      relation.type = type;
      relation.title = title;
      relation.mainEntity = entity;
      entity.relations.add(relation);
      if("one".equals(type)) {
        for(int pk=0; pk<relEntity.pks.size(); pk++) {
          ModelField pkf = (ModelField)relEntity.pks.get(pk);
          ModelKeyMap keyMap = new ModelKeyMap();
          keyMap.fieldName = pkf.name;
          keyMap.relFieldName = pkf.name;
          relation.keyMaps.add(keyMap);
        }
      }
    }
  }
  else if("updateRelation".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    String type = request.getParameter("type");
    String title = request.getParameter("title");

    ModelRelation relation = (ModelRelation)entity.relations.get(relNum);
    relation.type = type;
    relation.title = title;
  }
  else if("removeRelation".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    if(relNum < entity.relations.size() && relNum >= 0) entity.relations.removeElementAt(relNum);
    else errorMsg = errorMsg + "<li> Relation number " + relNum + " is out of bounds.";
  }
  else if("updateKeyMap".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    int kmNum = Integer.parseInt(request.getParameter("kmNum"));
    String fieldName = request.getParameter("fieldName");
    String relFieldName = request.getParameter("relFieldName");
    
    ModelRelation relation = (ModelRelation)entity.relations.get(relNum);
    ModelEntity relEntity = reader.getModelEntity(relation.relEntityName);
    ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(kmNum);
    ModelField field = entity.getField(fieldName);
    ModelField relField = relEntity.getField(relFieldName);

    keyMap.fieldName = field.name;
    keyMap.relFieldName = relField.name;
  }
  else if("removeKeyMap".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    int kmNum = Integer.parseInt(request.getParameter("kmNum"));

    ModelRelation relation = (ModelRelation)entity.relations.get(relNum);
    relation.keyMaps.removeElementAt(kmNum);
  }
  else if("addKeyMap".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));

    ModelRelation relation = (ModelRelation)entity.relations.get(relNum);
    ModelKeyMap keyMap = new ModelKeyMap();
    relation.keyMaps.add(keyMap);
  }
  else if("addReverse".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));

    ModelRelation relation = (ModelRelation)entity.relations.get(relNum);
    ModelEntity relatedEnt = reader.getModelEntity(relation.relEntityName);
    if(relatedEnt != null) {
      if(relatedEnt.getRelation(relation.title + entity.entityName) == null) {
        ModelRelation newRel = new ModelRelation();
        relatedEnt.relations.add(newRel);

        newRel.relEntityName = entity.entityName;
        newRel.relTableName = entity.tableName;
        newRel.title = relation.title;
        if(relation.type.equalsIgnoreCase("one")) newRel.type = "many";
        else newRel.type = "one";

        for(int kmn=0; kmn<relation.keyMaps.size(); kmn++) {
          ModelKeyMap curkm = (ModelKeyMap)relation.keyMaps.get(kmn);
          ModelKeyMap newkm = new ModelKeyMap();
          newRel.keyMaps.add(newkm);
          newkm.fieldName = curkm.relFieldName;
          newkm.relFieldName = curkm.fieldName;
        }

        newRel.mainEntity = relatedEnt;
      }
      else errorMsg = errorMsg + "<li> Related entity already has a relation with name " + relation.title + entity.entityName + ", no reverse relation added.";
    }
    else errorMsg = errorMsg + "<li> Could not find related entity " + relation.relEntityName + ", no reverse relation added.";
  }

  Collection typesCol = delegator.getEntityFieldTypeNames(entity);
  TreeSet types = null;
  if(typesCol != null) types = new TreeSet(typesCol);
%>

<html>
<head>
  <title>Entity Editor</title>
  <style>
    A.listtext {font-family: Helvetica,sans-serif; font-size: 10pt; font-weight: bold; text-decoration: none; color: blue;}
    A.listtext:hover {color:red;}
  </style>
</head>
<body>

<H3>Entity Editor</H3>

<%if(errorMsg.length() > 0){%>
The following errors occurred:
<ul><%=errorMsg%></ul>
<%}%>

<FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity")%>' style='margin: 0;'>
  <INPUT type=TEXT size='30' name='entityName'>
  <INPUT type=SUBMIT value='Edit Specified Entity'>
</FORM>
<FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity")%>' style='margin: 0;'>
  <SELECT name='entityName'>
    <OPTION selected>&nbsp;</OPTION>
    <%Iterator entIter1 = entSet.iterator();%>
    <%while(entIter1.hasNext()){%>
      <OPTION><%=(String)entIter1.next()%></OPTION>
    <%}%>
  </SELECT>
  <INPUT type=SUBMIT value='Edit Specified Entity'>
</FORM>
<hr>
<FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?event=addEntity")%>' style='margin: 0;'>
  Entity Name (Java style): <INPUT type=TEXT size='60' name='entityName'><br>
  Entity Group: <INPUT type=TEXT size='60' name='entityGroup' value='org.ofbiz.commonapp'>
  <INPUT type=SUBMIT value='Create Entity'>
</FORM>
<hr>
<%if(entity == null){%>
  <H4>Entity not found with name "<%=entityName%>"</H4>
<%}else{%>

<BR>
<A href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName)%>'>Reload Current Entity: <%=entityName%></A><BR>
<BR>
Entity Name: <%=entityName%><br>
Column Name: <%=entity.tableName%><br>

<FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&event=updateEntity")%>' style='margin: 0;'>
  <INPUT type=text size='60' name='tableName' value='<%=entity.tableName%>'> (Table Name)
  <BR>
  <INPUT type=text size='60' name='packageName' value='<%=entity.packageName%>'> (Package Name)
  <BR>
  <SELECT name='dependentOn'>
    <OPTION selected><%=entity.dependentOn%></OPTION>
    <OPTION></OPTION>
    <%Iterator depIter = entSet.iterator();%>
    <%while(depIter.hasNext()){%>
      <OPTION><%=(String)depIter.next()%></OPTION>
    <%}%>
  </SELECT>
  (Dependent On Entity)
  <BR>
  <INPUT type=text size='60' name='title' value='<%=entity.title%>'> (Title)
  <BR>
  <INPUT type=text size='60' name='description' value='<%=entity.description%>'> (Description)
  <BR>
  <INPUT type=text size='60' name='copyright' value='<%=entity.copyright%>'> (Copyright)
  <BR>
  <INPUT type=text size='60' name='author' value='<%=entity.author%>'> (Author)
  <BR>
  <INPUT type=text size='60' name='version' value='<%=entity.version%>'> (Version)
  <BR>
  <BR>
  <INPUT type=text size='60' name='entityGroup' value='<%=UtilFormatOut.checkNull(delegator.getModelGroupReader().getEntityGroupName(entityName))%>'> (Group)
  <BR>(This group is for the "<%=delegator.getDelegatorName()%>" delegator)
  <BR>
  <BR>
  <INPUT type=text size='60' name='filename' value='<%=UtilFormatOut.checkNull((String)delegator.getModelReader().entityFile.get(entityName))%>'> (Filename)
  <BR>
  <INPUT type=submit value='Update Entity'>
</FORM>
<BR>
<B>FIELDS</B>
  <TABLE border='1' cellpadding='2' cellspacing='0'>
    <TR><TD>Field Name</TD><TD>Column Name (Length)</TD><TD>Field Type</TD><TD>&nbsp;</TD><TD>&nbsp;</TD></TR>
    <%for(int f=0; f<entity.fields.size(); f++){%>
      <%ModelField field = (ModelField)entity.fields.get(f);%>
      <TR>
        <TD><%=field.isPk?"<B>":""%><%=field.name%><%=field.isPk?"</B>":""%></TD>
        <TD><%=field.colName%> (<%=field.colName.length()%>)</TD>
        <TD><%=field.type%></TD>
        <TD>
          <FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&fieldName=" + field.name + "&event=updateField")%>' style='margin: 0;'>
            <INPUT type=CHECKBOX name='primaryKey'<%=field.isPk?" checked":""%>>
            <SELECT name='fieldType'>
              <OPTION selected><%=field.type%></OPTION>
              <%Iterator iter = UtilMisc.toIterator(types);%>
              <%while(iter != null && iter.hasNext()){ String typeName = (String)iter.next();%>
                <OPTION><%=typeName%></OPTION>
              <%}%>
            </SELECT>
            <INPUT type=submit value='Set'>
          </FORM>
        </TD>
        <TD><A href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&fieldName=" + field.name + "&event=removeField")%>'>Remove</A></TD>
      </TR>
    <%}%>
  </TABLE>

<FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&event=addField")%>'>
  Add new field with <u>Field Name (Java style)</u> and field type.<BR>
  <INPUT type=text size='40' maxlength='30' name='name'>
  <SELECT name='fieldType'>
    <%Iterator iter = UtilMisc.toIterator(types);%>
    <%while(iter != null && iter.hasNext()){ String typeName = (String)iter.next();%>
      <OPTION><%=typeName%></OPTION>
    <%}%>
  </SELECT>
  <INPUT type=submit value='Create'>
</FORM>
<HR>
<B>RELATIONS</B>
  <TABLE border='1' cellpadding='2' cellspacing='0'>
  <%for(int r=0; r<entity.relations.size(); r++){%>
    <%ModelRelation relation = (ModelRelation)entity.relations.elementAt(r);%>
    <%ModelEntity relEntity = reader.getModelEntity(relation.relEntityName);%>
    <tr bgcolor='#CCCCFF'>
      <FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&event=updateRelation&relNum=" + r)%>'>
        <td align="left"><%=relation.title%><A class='listtext' href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + relation.relEntityName)%>'><%=relation.relEntityName%></A></td>
        <td align="left"><b><%=relation.relTableName%></b></td>
        <td>
          <INPUT type=TEXT name='title' value='<%=relation.title%>'>
          <SELECT name='type'>
            <OPTION selected><%=relation.type%></OPTION>
            <OPTION>&nbsp;</OPTION>
            <OPTION>one</OPTION>
            <OPTION>many</OPTION>
          </SELECT>
        </td>
        <td>
          <INPUT type=SUBMIT value='Set'>
        </td>
        <TD><A href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&relNum=" + r + "&event=removeRelation")%>'>Remove</A></TD>
        <TD><A href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&relNum=" + r + "&event=addKeyMap")%>'>Add&nbsp;KeyMap</A></TD>
        <TD><A href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&relNum=" + r + "&event=addReverse")%>'>Add&nbsp;Reverse</A></TD>
      </FORM>
    </tr>
    <%for(int km=0; km<relation.keyMaps.size(); km++){ ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(km);%>
      <tr>
        <FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&event=updateKeyMap&relNum=" + r + "&kmNum=" + km)%>'>
          <td></td>
          <td colspan='2'>
            Main:
            <SELECT name='fieldName'>
              <OPTION selected><%=keyMap.fieldName%></OPTION>
              <OPTION>&nbsp;</OPTION>
              <%for(int fld=0; fld<entity.fields.size(); fld++){%>
                <OPTION><%=((ModelField)entity.fields.get(fld)).name%></OPTION>
              <%}%>
            </SELECT>
            Related:
            <SELECT name='relFieldName'>
              <OPTION selected><%=keyMap.relFieldName%></OPTION>
              <OPTION>&nbsp;</OPTION>
              <%for(int fld=0; fld<relEntity.fields.size(); fld++){%>
                <OPTION><%=((ModelField)relEntity.fields.get(fld)).name%></OPTION>
              <%}%>
            </SELECT>
          </td>
          <td>
            <INPUT type=SUBMIT value='Set'>
          </td>          
          <TD><A href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&relNum=" + r + "&kmNum=" + km + "&event=removeKeyMap")%>'>Remove</A></TD>
        </FORM>
      </tr>
    <%}%>			
  <%}%>
  </TABLE>

<FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&event=addRelation")%>'>
  Add new relation with <u>Title</u>, <u>Related Entity Name</u> and relation type.<BR>
  <INPUT type=text size='40' maxlength='30' name='title'>
  <%-- <INPUT type=text size='40' maxlength='30' name='relEntityName'> --%>
  <SELECT name='relEntityName'>
    <OPTION selected>&nbsp;</OPTION>
    <%Iterator entIter = entSet.iterator();%>
    <%while(entIter.hasNext()){%>
      <OPTION><%=(String)entIter.next()%></OPTION>
    <%}%>
  </SELECT>
  <SELECT name='type'>
    <OPTION>one</OPTION>
    <OPTION>many</OPTION>
  </SELECT>
  <INPUT type=submit value='Create'>
</FORM>

<%}%>

</body>
</html>

<%}else{%>
<html>
<head>
  <title>Entity Editor</title>
</head>
<body>

<H3>Entity Editor</H3>

ERROR: You do not have permission to use this page (ENTITY_MAINT needed)

</body>
</html>
<%}%>
