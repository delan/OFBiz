<%@page contentType="text/html"%>
<%
 /*  Copyright (c) 2001 The Open For Business Project and respected authors.
 
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
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.entity.model.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<%
if (security.hasPermission("ENTITY_MAINT", session)) {
  String entityName = request.getParameter("entityName");
  ModelReader reader = delegator.getModelReader();
  ModelEntity entity = reader.getModelEntity(entityName);
  ModelViewEntity modelViewEntity = null;
  if (entity instanceof ModelViewEntity) modelViewEntity = (ModelViewEntity)entity;
  TreeSet entSet = new TreeSet(reader.getEntityNames());
  String errorMsg = "";

  String event = request.getParameter("event");
  if ("addEntity".equals(event)) {
    if (entity == null) {
      entity = new ModelEntity();
      entity.setEntityName(request.getParameter("entityName"));
      entity.setTableName(ModelUtil.javaNameToDbName(entity.getEntityName()));
      reader.getEntityCache().put(entity.getEntityName(), entity);
      entityName = entity.getEntityName();
      
      String entityGroup = request.getParameter("entityGroup");
      delegator.getModelGroupReader().getGroupCache().put(entityName, entityGroup);
    }
  } else if ("updateEntity".equals(event)) {
    entity.setTableName(request.getParameter("tableName"));
    entity.setPackageName(request.getParameter("packageName"));
    entity.setDependentOn(request.getParameter("dependentOn"));
    entity.setTitle(request.getParameter("title"));
    entity.setDescription(request.getParameter("description"));
    entity.setCopyright(request.getParameter("copyright"));
    entity.setAuthor(request.getParameter("author"));
    entity.setVersion(request.getParameter("version"));

    String entityGroup = request.getParameter("entityGroup");
    delegator.getModelGroupReader().getGroupCache().put(entityName, entityGroup);

    String filename = request.getParameter("filename");
    delegator.getModelReader().addEntityToFile(entityName, filename);
    delegator.getModelReader().rebuildFileNameEntities();
  } else if ("removeField".equals(event)) {
    String fieldName = request.getParameter("fieldName");
    entity.removeField(fieldName);
  } else if ("updateField".equals(event)) {
    String fieldName = request.getParameter("fieldName");
    String fieldType = request.getParameter("fieldType");
    String primaryKey = request.getParameter("primaryKey");
    ModelField field = entity.getField(fieldName);
    field.setType(fieldType);
    if (primaryKey != null) field.setIsPk(true);
    else field.setIsPk(false);
    entity.updatePkLists();
  } else if ("addField".equals(event)) {
    ModelField field = new ModelField();
    field.setName(request.getParameter("name"));
    field.setColName(ModelUtil.javaNameToDbName(field.getName()));
    field.setType(request.getParameter("fieldType"));
    entity.addField(field);
  } else if ("addRelation".equals(event)) {
    String relEntityName = request.getParameter("relEntityName");
    String type = request.getParameter("type");
    String title = request.getParameter("title");
    ModelRelation relation = new ModelRelation();

    ModelEntity relEntity = reader.getModelEntity(relEntityName);
    if (relEntity == null) {
        errorMsg = errorMsg + "<li> Related Entity \"" + relEntityName + "\" not found, not adding.";
    } else {
      relation.setRelEntityName(relEntityName);
      relation.setType(type);
      relation.setTitle(title);
      relation.setMainEntity(entity);
      entity.addRelation(relation);
      if ("one".equals(type)) {
        for (int pk = 0; pk < relEntity.getPksSize(); pk++) {
          ModelField pkf = relEntity.getPk(pk);
          ModelKeyMap keyMap = new ModelKeyMap();
          keyMap.setFieldName(pkf.getName());
          keyMap.setRelFieldName(pkf.getName());
          relation.addKeyMap(keyMap);
        }
      }
    }
  } else if ("updateRelation".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    String type = request.getParameter("type");
    String title = request.getParameter("title");

    ModelRelation relation = entity.getRelation(relNum);
    relation.setType(type);
    relation.setTitle(title);
  } else if ("removeRelation".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    if (relNum < entity.getRelationsSize() && relNum >= 0) entity.removeRelation(relNum);
    else errorMsg = errorMsg + "<li> Relation number " + relNum + " is out of bounds.";
  } else if ("updateKeyMap".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    int kmNum = Integer.parseInt(request.getParameter("kmNum"));
    String fieldName = request.getParameter("fieldName");
    String relFieldName = request.getParameter("relFieldName");
    
    ModelRelation relation = entity.getRelation(relNum);
    ModelEntity relEntity = reader.getModelEntity(relation.getRelEntityName());
    ModelKeyMap keyMap = relation.getKeyMap(kmNum);
    ModelField field = entity.getField(fieldName);
    ModelField relField = relEntity.getField(relFieldName);

    keyMap.setFieldName(field.getName());
    keyMap.setRelFieldName(relField.getName());
  } else if ("removeKeyMap".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    int kmNum = Integer.parseInt(request.getParameter("kmNum"));

    ModelRelation relation = entity.getRelation(relNum);
    relation.removeKeyMap(kmNum);
  } else if ("addKeyMap".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));

    ModelRelation relation = entity.getRelation(relNum);
    ModelKeyMap keyMap = new ModelKeyMap();
    relation.addKeyMap(keyMap);
  } else if ("addReverse".equals(event)) {
    int relNum = Integer.parseInt(request.getParameter("relNum"));

    ModelRelation relation = entity.getRelation(relNum);
    ModelEntity relatedEnt = reader.getModelEntity(relation.getRelEntityName());
    if (relatedEnt != null) {
      if (relatedEnt.getRelation(relation.getTitle() + entity.getEntityName()) == null) {
        ModelRelation newRel = new ModelRelation();
        relatedEnt.addRelation(newRel);

        newRel.setRelEntityName(entity.getEntityName());
        newRel.setTitle(relation.getTitle());
        if (relation.getType().equalsIgnoreCase("one")) newRel.setType("many");
        else newRel.setType("one");

        for (int kmn = 0; kmn < relation.getKeyMapsSize(); kmn++) {
          ModelKeyMap curkm = relation.getKeyMap(kmn);
          ModelKeyMap newkm = new ModelKeyMap();
          newRel.addKeyMap(newkm);
          newkm.setFieldName(curkm.getRelFieldName());
          newkm.setRelFieldName(curkm.getFieldName());
        }

        newRel.setMainEntity(relatedEnt);
      } else {
        errorMsg = errorMsg + "<li> Related entity already has a relation with name " + relation.getTitle() + entity.getEntityName() + ", no reverse relation added.";
      }
    } else {
      errorMsg = errorMsg + "<li> Could not find related entity " + relation.getRelEntityName() + ", no reverse relation added.";
    }
  }

  Collection typesCol = delegator.getEntityFieldTypeNames(entity);
  TreeSet types = null;
  if (typesCol != null) types = new TreeSet(typesCol);
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

<%if (errorMsg.length() > 0) {%>
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
    <%while (entIter1.hasNext()) {%>
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
<%if (entity == null) {%>
  <H4>Entity not found with name "<%=entityName%>"</H4>
<%}else{%>

<BR>
<A href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName)%>'>Reload Current Entity: <%=entityName%></A><BR>
<BR>
Entity Name: <%=entityName%><br>
Column Name: <%=(modelViewEntity == null)?entity.getTableName():"What column name? This is a VIEW Entity."%><br>

<FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&event=updateEntity")%>' style='margin: 0;'>
  <%if (modelViewEntity == null) {%>
    <INPUT type=text size='60' name='tableName' value='<%=UtilFormatOut.checkNull(entity.getTableName())%>'> (Table Name)
    <BR>
  <%}%>
  <INPUT type=text size='60' name='packageName' value='<%=entity.getPackageName()%>'> (Package Name)
  <BR>
  <SELECT name='dependentOn'>
    <OPTION selected><%=entity.getDependentOn()%></OPTION>
    <OPTION></OPTION>
    <%Iterator depIter = entSet.iterator();%>
    <%while (depIter.hasNext()) {%>
      <OPTION><%=(String)depIter.next()%></OPTION>
    <%}%>
  </SELECT>
  (Dependent On Entity)
  <BR>
  <INPUT type=text size='60' name='title' value='<%=entity.getTitle()%>'> (Title)
  <BR>
  <TEXTAREA cols='60' rows='5' name='description'><%=entity.getDescription()%></TEXTAREA> (Description)
  <BR>
  <INPUT type=text size='60' name='copyright' value='<%=entity.getCopyright()%>'> (Copyright)
  <BR>
  <INPUT type=text size='60' name='author' value='<%=entity.getAuthor()%>'> (Author)
  <BR>
  <INPUT type=text size='60' name='version' value='<%=entity.getVersion()%>'> (Version)
  <BR>
  <BR>
  <INPUT type=text size='60' name='entityGroup' value='<%=UtilFormatOut.checkNull(delegator.getModelGroupReader().getEntityGroupName(entityName))%>'> (Group)
  <BR>(This group is for the "<%=delegator.getDelegatorName()%>" delegator)
  <BR>
  <BR>
  <INPUT type=text size='60' name='filename' value='<%=UtilFormatOut.checkNull((String) delegator.getModelReader().getEntityFileName(entityName))%>'> (Filename)
  <BR>
  <INPUT type=submit value='Update Entity'>
</FORM>

<HR>
<%if (modelViewEntity == null) {%>
<B>FIELDS</B>
  <TABLE border='1' cellpadding='2' cellspacing='0'>
    <TR><TD>Field Name</TD><TD>Column Name (Length)</TD><TD>Field Type</TD><TD>&nbsp;</TD><TD>&nbsp;</TD></TR>
    <%for (int f = 0; f < entity.getFieldsSize(); f++) {%>
      <%ModelField field = entity.getField(f);%>
      <TR>
        <TD><%=field.getIsPk()?"<B>":""%><%=field.getName()%><%=field.getIsPk()?"</B>":""%></TD>
        <TD><%=field.getColName()%> (<%=field.getColName().length()%>)</TD>
        <TD><%=field.getType()%></TD>
        <TD>
          <FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&fieldName=" + field.getName() + "&event=updateField")%>' style='margin: 0;'>
            <INPUT type=CHECKBOX name='primaryKey'<%=field.getIsPk()?" checked":""%>>
            <SELECT name='fieldType'>
              <OPTION selected><%=field.getType()%></OPTION>
              <%Iterator iter = UtilMisc.toIterator(types);%>
              <%while (iter != null && iter.hasNext()){ String typeName = (String)iter.next();%>
                <OPTION><%=typeName%></OPTION>
              <%}%>
            </SELECT>
            <INPUT type=submit value='Set'>
          </FORM>
        </TD>
        <TD><A href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&fieldName=" + field.getName() + "&event=removeField")%>'>Remove</A></TD>
      </TR>
    <%}%>
  </TABLE>

<FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&event=addField")%>'>
  Add new field with <u>Field Name (Java style)</u> and field type.<BR>
  <INPUT type=text size='40' maxlength='30' name='name'>
  <SELECT name='fieldType'>
    <%Iterator iter = UtilMisc.toIterator(types);%>
    <%while (iter != null && iter.hasNext()){ String typeName = (String)iter.next();%>
      <OPTION><%=typeName%></OPTION>
    <%}%>
  </SELECT>
  <INPUT type=submit value='Create'>
</FORM>
<%} else {%>
<div>ERROR: Alias editing not yet implemented for view entities, try again later (or just edit the XML by hand, and not at the same time you are editing here...)</div>
<%}%>
<HR>

<B>RELATIONS</B>
  <TABLE border='1' cellpadding='2' cellspacing='0'>
  <%for (int r = 0; r < entity.getRelationsSize(); r++) {%>
    <%ModelRelation relation = entity.getRelation(r);%>
    <%ModelEntity relEntity = reader.getModelEntity(relation.getRelEntityName());%>
    <tr bgcolor='#CCCCFF'>
      <FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&event=updateRelation&relNum=" + r)%>'>
        <td align="left"><%=relation.getTitle()%><A class='listtext' href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + relation.getRelEntityName())%>'><%=relation.getRelEntityName()%></A></td>
        <td>
          <INPUT type=TEXT name='title' value='<%=relation.getTitle()%>'>
          <SELECT name='type'>
            <OPTION selected><%=relation.getType()%></OPTION>
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
    <%for (int km=0; km<relation.getKeyMapsSize(); km++){ ModelKeyMap keyMap = (ModelKeyMap)relation.getKeyMap(km);%>
      <tr>
        <FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&event=updateKeyMap&relNum=" + r + "&kmNum=" + km)%>'>
          <td></td>
          <td colspan='2'>
            Main:
            <SELECT name='fieldName'>
              <OPTION selected><%=keyMap.getFieldName()%></OPTION>
              <OPTION>&nbsp;</OPTION>
              <%for (int fld=0; fld<entity.getFieldsSize(); fld++) {%>
                <OPTION><%=entity.getField(fld).getName()%></OPTION>
              <%}%>
            </SELECT>
            Related:
            <SELECT name='relFieldName'>
              <OPTION selected><%=keyMap.getRelFieldName()%></OPTION>
              <OPTION>&nbsp;</OPTION>
              <%for (int fld=0; fld<relEntity.getFieldsSize(); fld++) {%>
                <OPTION><%=relEntity.getField(fld).getName()%></OPTION>
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
    <%while (entIter.hasNext()) {%>
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

<%} else {%>
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
