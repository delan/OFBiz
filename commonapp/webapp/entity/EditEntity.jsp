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

<jsp:useBean id="helper" type="org.ofbiz.core.entity.GenericHelper" scope="application" />
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<%
  String entityName = request.getParameter("entityName");
  ModelReader reader = helper.getModelReader();
  ModelEntity entity = reader.getModelEntity(entityName);
  TreeSet entSet = new TreeSet(reader.getEntityNames());
  Collection typesCol = reader.getFieldTypeNames();
  TreeSet types = new TreeSet(typesCol);
  String errorMsg = "";

  String event = request.getParameter("event");
  if("removeField".equals(event))
  {
    String fieldName = request.getParameter("fieldName");
    entity.removeField(fieldName);
  }
  else if("updateField".equals(event))
  {
    String fieldName = request.getParameter("fieldName");
    String fieldType = request.getParameter("fieldType");
    String primaryKey = request.getParameter("primaryKey");
    ModelField field = entity.getField(fieldName);
    field.type = fieldType;
    if(primaryKey != null) field.isPk = true;
    else field.isPk = false;
  }
  else if("addField".equals(event))
  {
    String colName = request.getParameter("colName");
    String fieldType = request.getParameter("fieldType");
    ModelField field = new ModelField();
    field.colName = colName;
    field.name = ModelUtil.dbNameToVarName(colName);
    field.type = fieldType;
    entity.fields.add(field);
  }
  else if("addRelation".equals(event))
  {
    String relEntityName = request.getParameter("relEntityName");
    String type = request.getParameter("type");
    String title = request.getParameter("title");
    ModelRelation relation = new ModelRelation();

    ModelEntity relEntity = reader.getModelEntity(relEntityName);
    if(relEntity == null) errorMsg = errorMsg + "<li> Related Entity \"" + relEntityName + "\" not found, not adding.";
    else
    {
      relation.relEntityName = relEntityName;
      relation.relTableName = relEntity.tableName;
      relation.type = type;
      relation.title = title;
      relation.mainEntity = entity;
      entity.relations.add(relation);
      if("one".equals(type))
      {
        for(int pk=0; pk<relEntity.pks.size(); pk++)
        {
          ModelField pkf = (ModelField)relEntity.pks.get(pk);
          ModelKeyMap keyMap = new ModelKeyMap();
          keyMap.colName = pkf.colName;
          keyMap.fieldName = pkf.name;
          keyMap.relColName = pkf.colName;
          keyMap.relFieldName = pkf.name;
          relation.keyMaps.add(keyMap);
        }
      }
    }
  }
  else if("updateRelation".equals(event))
  {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    String type = request.getParameter("type");
    String title = request.getParameter("title");

    ModelRelation relation = (ModelRelation)entity.relations.get(relNum);
    relation.type = type;
    relation.title = title;
  }
  else if("removeRelation".equals(event))
  {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    if(relNum < entity.relations.size() && relNum >= 0) entity.relations.removeElementAt(relNum);
    else errorMsg = errorMsg + "<li> Relation number " + relNum + " is out of bounds.";
  }
  else if("updateKeyMap".equals(event))
  {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    int kmNum = Integer.parseInt(request.getParameter("kmNum"));
    String fieldName = request.getParameter("fieldName");
    String relFieldName = request.getParameter("relFieldName");
    
    ModelRelation relation = (ModelRelation)entity.relations.get(relNum);
    ModelEntity relEntity = reader.getModelEntity(relation.relEntityName);
    ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(kmNum);
    ModelField field = entity.getField(fieldName);
    ModelField relField = relEntity.getField(relFieldName);

    keyMap.colName = field.colName;
    keyMap.fieldName = field.name;
    keyMap.relColName = relField.colName;
    keyMap.relFieldName = relField.name;
  }
  else if("removeKeyMap".equals(event))
  {
    int relNum = Integer.parseInt(request.getParameter("relNum"));
    int kmNum = Integer.parseInt(request.getParameter("kmNum"));

    ModelRelation relation = (ModelRelation)entity.relations.get(relNum);
    relation.keyMaps.removeElementAt(kmNum);
  }
  else if("addKeyMap".equals(event))
  {
    int relNum = Integer.parseInt(request.getParameter("relNum"));

    ModelRelation relation = (ModelRelation)entity.relations.get(relNum);
    ModelKeyMap keyMap = new ModelKeyMap();
    relation.keyMaps.add(keyMap);
  }
%>

<html>
<head><title>Entity Editor</title></head>
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

<%if(entity == null){%>
  <H4>Entity not found with name "<%=entityName%>"</H4>
<%}else{%>

<A href='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName)%>'>Reload Current Entity: <%=entityName%></A><BR>
<BR>
Entity Name: <%=entityName%><br>
Column Name: <%=entity.tableName%><br>
<B>FIELDS</B>
  <TABLE border='1' cellpadding='2' cellspacing='0'>
    <TR><TD>Field Name</TD><TD>Column Name (Length)</TD><TD>Field Type</TD><TD>&nbsp;</TD><TD>&nbsp;</TD></TR>
    <%for(int f=0; f<entity.fields.size(); f++){%>
      <%ModelField field = (ModelField)entity.fields.get(f);%>
      <TR>
        <TD><%=field.isPk?"<B>":""%><%=field.name%><%=field.isPk?"</B>":""%></TD>
        <TD><%=field.colName%> (<%=field.colName.length()%>)</TD><TD><%=field.type%></TD>
        <TD>
          <FORM method=POST action='<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName + "&fieldName=" + field.name + "&event=updateField")%>' style='margin: 0;'>
            <INPUT type=CHECKBOX name='primaryKey'<%=field.isPk?" checked":""%>>
            <SELECT name='fieldType'>
              <OPTION selected><%=field.type%></OPTION>
              <%Iterator iter = types.iterator();%>
              <%while(iter.hasNext()){ String typeName = (String)iter.next();%>
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
  Add new field with <u>Column Name</u> and field type.<BR>
  <INPUT type=text size='40' maxlength='30' name='colName'>
  <SELECT name='fieldType'>
    <%Iterator iter = types.iterator();%>
    <%while(iter.hasNext()){ String typeName = (String)iter.next();%>
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
        <td align="left"><%=relation.title%><b><%=relation.relEntityName%></b></td>
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
