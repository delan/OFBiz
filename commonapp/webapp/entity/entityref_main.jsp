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
	
<%
	Iterator i = entities.iterator();
	while ( i.hasNext() ) {
		Object o = i.next();
		String entityName = (String) o;
		if ( search == null || entityName.toLowerCase().indexOf(search.toLowerCase()) != -1 ) {
			ModelEntity entity = reader.getModelEntity(entityName);			
%>	
  <a name="<%= entityName %>"></a>
  <table width="95%" border="1" cellpadding='2' cellspacing='0'>
    <tr bgcolor="#CCCCCC"> 
      <td colspan="4"> 
        <div align="center" class='titletext'>ENTITY: <%= entityName %></div>
      </td>
    </tr>
    <tr class='headertext'>
      <td width="30%" align=center>Java Name</td>
      <td width="30%" align=center>DB Name</td>
      <td width="20%" align=center>Java-Type</td>
      <td width="20%" align=center nowrap>SQL-Type</td>
    </tr>
	
<%
			for ( int y = 0; y < entity.fields.size(); y++ ) {
				ModelField field = (ModelField) entity.fields.elementAt(y);	
				ModelFieldType type = reader.getModelFieldType(field.type);
				String javaName = new String();
				javaName = field.isPk ? "<div style=\"color: red;\">" + field.name + "</div>" : field.name;
%>	
    <tr bgcolor="#EFFFFF">
      <td><div align="center" class='enametext'><%= javaName %></div></td>
      <td><div align="center" class='entitytext'><%= field.colName %></div></td>
      <td><div align="center" class='entitytext'><%= type.javaType %></div></td>
      <td><div align="center" class='entitytext'><%= type.sqlType %></div></td>
    </tr>
<%	
			}
%>

<%
			if ( entity.relations != null && entity.relations.size() > 0 ) {
%>
	<tr bgcolor="#FFCCCC">
	  <td colspan="4"><hr></td>
	</tr>
    <tr class='headertext'> 
      <td align="center">Relation</td>
      <td align="center">Table</td>
      <td align="center">Type</td>	  
      <td>&nbsp;</TD>
    </tr>

<%
  TreeSet relations = new TreeSet();
  for ( int r = 0; r < entity.relations.size(); r++ ) {
    ModelRelation relation = (ModelRelation) entity.relations.elementAt(r);
    
    if(!entities.contains(relation.relEntityName))
      warningString = warningString + "<li>Related entity <b>" + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> not found.</li>";
    if(relations.contains(relation.title + relation.relEntityName))
      warningString = warningString + "<li>Relation <b>" + relation.title + relation.relEntityName + "</b> of entity <A href=\"#" + entity.entityName + "\">" + entity.entityName + "</A> is not unique for that entity.</li>";
    else
      relations.add(relation.title + relation.relEntityName);
%>


    <tr bgcolor="#FEEEEE"> 
      <td> 
        <div align="center">
          <A href='#<%=relation.relEntityName%>' class='rlinktext'><%= relation.title + relation.relEntityName %></A>
        </div>
      </td>
      <td><div align="center" class='relationtext'><%= relation.relTableName %></div></td>
      <td with="25%"><div align="center" class='relationtext'><%= relation.type %></div></td>
      <td>&nbsp;</TD>
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

WARNINGS:
<OL>
<%=warningString%>
</OL>

</body>
</html>
