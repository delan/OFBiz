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
 * @version 1.0
-->

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.entity.model.*" %>

<jsp:useBean id="helper" type="org.ofbiz.core.entity.GenericHelper" scope="application" />

<% 
	String search = null;
	//GenericHelper helper = GenericHelperFactory.getDefaultHelper();
	ModelReader reader = helper.getModelReader();
	Collection ec = reader.getEntityNames();
	TreeSet entities = new TreeSet(ec);
	int numberOfEntities = entities.size();
	int numberShowed = 0;
	search = (String) request.getParameter("search");
  //as we are iterating through, check a few things and put any warnings here inside <li></li> tags
  String warningString = "";
%>

<html>
<head>
<title>Entity Reference</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<style>
  .toptext {font-family: Helvetica,sans-serif; font-size: 16pt; font-weight: bold; text-decoration: none; color: black;}
  .titletext {font-family: Helvetica,sans-serif; font-size: 12pt; font-weight: bold; text-decoration: none; color: blue;}
  .headertext {font-family: Helvetica,sans-serif; font-size: 8pt; font-weight: bold; text-decoration: none; background-color: blue; color: white;}
  .enametext {font-family: Helvetica,sans-serif; font-size: 8pt; font-weight: bold; text-decoration: none; color: black;}
  .entitytext {font-family: Helvetica,sans-serif; font-size: 8pt; text-decoration: none; color: black;}
  .relationtext {font-family: Helvetica,sans-serif; font-size: 8pt; text-decoration: none; color: black;}
  A.rlinktext {font-family: Helvetica,sans-serif; font-size: 8pt; font-weight: bold; text-decoration: none; color: blue;}
  A.rlinktext:hover {color:red;}
</style>
</head>

<body bgcolor="#FFFFFF">
<div align="center">

  <DIV class='toptext'>Entity Reference Chart<br>
    <%= numberOfEntities %> Total Entities
    </DIV>

<A href='#WARNINGS'>View Warnings</A>
<%
	Iterator i = entities.iterator();
	while ( i.hasNext() ) {
		Object o = i.next();
		String entityName = (String) o;
		if ( search == null || entityName.toLowerCase().indexOf(search.toLowerCase()) != -1 ) {
			ModelEntity entity = reader.getModelEntity(entityName);			
    if(entity.tableName.length() > 30)
      warningString = warningString + "<li><div style=\"color: red;\">[TableNameGT30]</div> Table name <b>" + entity.tableName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is longer than 30 characters.</li>";
%>	
  <a name="<%= entityName %>"></a>
  <table width="95%" border="1" cellpadding='2' cellspacing='0'>
    <tr bgcolor="#CCCCCC"> 
      <td colspan="4"> 
        <div align="center" class='titletext'>ENTITY: <%= entityName %> | TABLE: <%= entity.tableName %></div>
      </td>
    </tr>
    <tr class='headertext'>
      <td width="30%" align=center>Java Name</td>
      <td width="30%" align=center>DB Name</td>
      <td width="20%" align=center>Java-Type</td>
      <td width="20%" align=center nowrap>SQL-Type</td>
    </tr>
	
<%
  TreeSet ufields = new TreeSet();
			for ( int y = 0; y < entity.fields.size(); y++ ) {
				ModelField field = (ModelField) entity.fields.elementAt(y);	
				ModelFieldType type = reader.getModelFieldType(field.type);
				String javaName = new String();
				javaName = field.isPk ? "<div style=\"color: red;\">" + field.name + "</div>" : field.name;
    if(ufields.contains(field.name))
      warningString = warningString + "<li><div style=\"color: red;\">[FieldNotUnique]</div> Field <b>" + field.name + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is not unique for that entity.</li>";
    else
      ufields.add(field.name);
    if(field.colName.length() > 30)
      warningString = warningString + "<li><div style=\"color: red;\">[FieldNameGT30]</div> Column name <b>" + field.colName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is longer than 30 characters.</li>";
%>	
    <tr bgcolor="#EFFFFF">
      <td><div align="left" class='enametext'><%= javaName %></div></td>
      <td><div align="left" class='entitytext'><%= field.colName %></div></td>
    <%if(type != null){%>
      <td><div align="left" class='entitytext'><%= type.javaType %></div></td>
      <td><div align="left" class='entitytext'><%= type.sqlType %></div></td>
    <%}else{%>
      <td><div align="left" class='entitytext'>NOT FOUND</div></td>
      <td><div align="left" class='entitytext'>NOT FOUND</div></td>
      <%warningString = warningString + "<li><div style=\"color: red;\">[FieldTypeNotFound]</div> Field type <b>" + field.type + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> not found in field type definitions.</li>";%>
    <%}%>
    </tr>
<%	
			}
			if ( entity.relations != null && entity.relations.size() > 0 ) {
%>
	<tr bgcolor="#FFCCCC">
	  <td colspan="4"><hr></td>
	</tr>
    <tr class='headertext'> 
      <td align="center">Relation</td>
      <td align="center">Table</td>
      <td align="center" colspan='2'>Type</td>	  
      
    </tr>
<%
  TreeSet relations = new TreeSet();
  for ( int r = 0; r < entity.relations.size(); r++ ) {
    ModelRelation relation = (ModelRelation) entity.relations.elementAt(r);
    
    if(!entities.contains(relation.relEntityName))
      warningString = warningString + "<li><div style=\"color: red;\">[RelatedEntityNotFound]</div> Related entity <b>" + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> not found.</li>";
    if(relations.contains(relation.title + relation.relEntityName))
      warningString = warningString + "<li><div style=\"color: red;\">[RelationNameNotUnique]</div> Relation <b>" + relation.title + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is not unique for that entity.</li>";
    else
      relations.add(relation.title + relation.relEntityName);

    ModelEntity relatedEntity = reader.getModelEntity(relation.relEntityName);
    if(relatedEntity != null)
    {
      //if relation is of type one, make sure keyMaps match the PK of the relatedEntity
      if(relation.type.equalsIgnoreCase("one"))
      {
        if(relatedEntity.pks.size() != relation.keyMaps.size())
          warningString = warningString + "<li><div style=\"color: red;\">[RelatedOneKeyMapsWrongSize]</div> The number of primary keys (" + relatedEntity.pks.size() + ") of related entity <b>" + relation.relEntityName + "</b> does not match the number of keymaps (" + relation.keyMaps.size() + ") for relation of type one \"" +  relation.title + relation.relEntityName + "\" of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A>.</li>";
        for(int repks=0; repks<relatedEntity.pks.size(); repks++)
        {
          ModelField pk = (ModelField)relatedEntity.pks.get(repks);
          if(relation.findKeyMapByRelated(pk.name) == null)
            warningString = warningString + "<li><div style=\"color: red;\">[RelationOneRelatedPrimaryKeyMissing]</div> The primary key \"<b>" + pk.name + "</b>\" of related entity <b>" + relation.relEntityName + "</b> is missing in the keymaps for relation of type one <b>" +  relation.title + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A>.</li>";
        }
      }
    }

    //make sure all keyMap 'fieldName's match fields of this entity
    //make sure all keyMap 'relFieldName's match fields of the relatedEntity
    for(int rkm=0; rkm<relation.keyMaps.size(); rkm++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(rkm);
      if(relatedEntity != null)
      {
        if(relatedEntity.getField(keyMap.relFieldName) == null)
          warningString = warningString + "<li><div style=\"color: red;\">[RelationRelatedFieldNotFound]</div> The field \"<b>" + keyMap.relFieldName + "</b>\" of related entity <b>" + relation.relEntityName + "</b> was specified in the keymaps but is not found for relation <b>" +  relation.title + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A>.</li>";
      }
      if(entity.getField(keyMap.fieldName) == null)
        warningString = warningString + "<li><div style=\"color: red;\">[RelationFieldNotFound]</div> The field <b>" + keyMap.fieldName + "</b> was specified in the keymaps but is not found for relation <b>" +  relation.title + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A>.</li>";
    }

%>
    <tr bgcolor="#FEEEEE"> 
      <td> 
        <div align="left" class='relationtext'>
          <b><%=relation.title%></b><A href='#<%=relation.relEntityName%>' class='rlinktext'><%=relation.relEntityName%></A>
        </div>
      </td>
      <td><div align="left" class='relationtext'><%= relation.relTableName %></div></td>
      <td with="25%" colspan='2'><div align="left" class='relationtext'>
        <%= relation.type %>:<%if(relation.type.length()==3){%>&nbsp;<%}%>
        <%for(int km=0; km<relation.keyMaps.size(); km++){ ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(km);%>
          <%=km+1%>)&nbsp;
          <%if(keyMap.fieldName.equals(keyMap.relFieldName)){%><%=keyMap.fieldName%>
          <%}else{%><%=keyMap.fieldName%> : <%=keyMap.relFieldName%><%}%>
          <%if(km != relation.keyMaps.size() -1){%><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%}%>
        <%}%>
      </div></td>
    </tr>				
<%
				}
			}
%>
    <tr bgcolor="#CCCCCC">
	  <td colspan="4">&nbsp;</td>
	</tr>
  </table>
  <br>
<%
		numberShowed++;
		}	
	}
%>  
  <br><br>
  <p align="center">Displayed: <%= numberShowed %></p>
</div>

<A name='WARNINGS'>WARNINGS:</A>
<OL>
<%=warningString%>
</OL>

</body>
</html>
