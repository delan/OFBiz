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
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.entity.model.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<% 
if(security.hasPermission("ENTITY_MAINT", session)) {
  String search = null;
  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entities = new TreeSet(ec);
  search = (String) request.getParameter("search");

  TreeSet packageNames = new TreeSet();

  //put the packageNames in a TreeSet
  Iterator ecIter = ec.iterator();
  while(ecIter.hasNext())
  {
    String eName = (String)ecIter.next();
    ModelEntity ent = reader.getModelEntity(eName);
    packageNames.add(ent.packageName);
  }
%>

<html>
<head>
<title>Entity Reference</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<style>
  A.listtext {font-family: Helvetica,sans-serif; font-size: 10pt; font-weight: bold; text-decoration: none; color: blue;}
  A.listtext:hover {color:red;}
</style>

</head>

<body bgcolor="#FFFFFF">
<div align="left">

<a href="<%=response.encodeURL(controlPath + "/main")%>" target='main' class='listtext'>Pop up CommonApp main</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/entityref_main")%>" target="entityFrame" class='listtext'>Entity Reference Main Page</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/entityref_main?CHECK_WARNINGS=true")%>" target="entityFrame" class='listtext'>Entity Reference Main With Warnings</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/checkdb")%>" target="entityFrame" class='listtext'>Check/Update Database</A>
<HR>
<a href="<%=response.encodeURL(controlPath + "/view/ModelWriter")%>" target='_blank' class='listtext'>Generate Entity Model XML (all in one)</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/ModelWriter?savetofile=true")%>" target='_blank' class='listtext'>Save Entity Model XML to Files</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/ModelGroupWriter")%>" target='_blank' class='listtext'>Generate Entity Group XML</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/ModelGroupWriter?savetofile=true")%>" target='_blank' class='listtext'>Save Entity Group XML to File</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/ModelInduceFromDb")%>" target='_blank' class='listtext'>Induce Model XML from Database</A><BR>
<HR>

<%
  Iterator piter = packageNames.iterator();
  while(piter.hasNext())
  {
    String pName = (String)piter.next();
%><a href="<%=response.encodeURL(controlPath + "/view/entityref_main#" + pName)%>" target="entityFrame" class='listtext'><%=pName%></a><br><%
  }
%>

<HR>

<%
	Iterator i = entities.iterator();
	while ( i.hasNext() ) {
		Object o = i.next();
		String entityName = (String) o;
		if ( search == null || entityName.toLowerCase().indexOf(search.toLowerCase()) != -1 ) {						
			String url = search == null ? "entityref_main#"+entityName : "entityref_main#"+entityName+"?search="+search;
%>	
<a href="<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName)%>" target="_blank" class='listtext'>[EditDef]</a>
<a href="<%=response.encodeURL(controlPath + "/view/" + url)%>" target="entityFrame" class='listtext'><%= entityName %></a>
<br>
<%
		}	
	}
%>  
</div>
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
