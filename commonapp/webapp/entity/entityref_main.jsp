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

<% 
	String search = null;
	GenericHelper helper = GenericHelperFactory.getDefaultHelper();
	ModelReader reader = helper.getModelReader();
	Collection ec = reader.getEntityNames();
	TreeSet entities = new TreeSet(ec);
	int numberOfEntities = entities.size();
	int numberShowed = 0;
	search = (String) request.getParameter("search");
%>

<html>
<head>
<title>Entity Reference</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFFF">
<div align="center">
  <p><font face="Verdana, Arial, Helvetica, sans-serif" size="3"><b>Entity Reference Chart<br>
    <%= numberOfEntities %> Total Entities
    <br>
    <br>
    </b></font></p>
	
<%
	Iterator i = entities.iterator();
	while ( i.hasNext() ) {
		Object o = i.next();
		String entityName = (String) o;
		if ( search == null || entityName.toLowerCase().indexOf(search.toLowerCase()) != -1 ) {
			ModelEntity entity = reader.getModelEntity(entityName);			
%>	
  <a name="<%= entityName %>"></a>
  <table width="85%" border="1">
    <tr bgcolor="#CCCCCC"> 
      <td colspan="3"> 
        <div align="center"><font color="#3333FF"><b><font face="Verdana, Arial, Helvetica, sans-serif" size="2">ENTITY: 
          <%= entityName %></font></b></font></div>
      </td>
    </tr>
    <tr bgcolor="#3333FF"> 
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF">Java 
          Name</font></div>
      </td>
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF">DB 
          Name</font></div>
      </td>
      <td width="33.3%">  
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF">Data-Type</font></div>
      </td>
    </tr>
	
<%
			for ( int y = 0; y < entity.fields.size(); y++ ) {
				ModelField field = (ModelField) entity.fields.elementAt(y);	
				ModelFieldType type = reader.getModelFieldType(field.type);
				String javaName = new String();
				javaName = field.isPk ? "<i>" + field.name + "</i>" : field.name;
%>	
    <tr bgcolor="#EFFFFF"> 
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><%= javaName %></i></font></div>
      </td>
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><%= field.colName %></font></div>
      </td>
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><%= type.javaType %></font></div>
      </td>
    </tr>
<%	
			}
%>

<%
			if ( entity.relations != null && entity.relations.size() > 0 ) {
%>
	<tr bgcolor="#FFCCCC">
	  <td colspan="3"><hr></td>
	</tr>
    <tr bgcolor="#3333FF"> 
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF"> 
          Relation</font></div>
      </td>
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF"> 
          Table</font></div>
      </td>
	  
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#FFFFFF"> 
          Type</font></div>
      </td>	  
    </tr>

<%				for ( int r = 0; r < entity.relations.size(); r++ ) {
					ModelRelation relation = (ModelRelation) entity.relations.elementAt(r);
%>

    <tr bgcolor="#FEEEEE"> 
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><%= relation.title + relation.relEntityName %></i></font></div>
      </td>
      <td width="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><%= relation.relTableName %></i></font></div>
      </td>
      <td with="33.3%"> 
        <div align="center"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><%= relation.type %></i></font></div>
      </td>	  
    </tr>				

<%
				}
			}
%>
    <tr bgcolor="#CCCCCC">
	  <td colspan="3">&nbsp;</td>
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
</body>
</html>
