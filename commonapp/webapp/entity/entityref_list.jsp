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

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<% 
  String search = null;
  //GenericDelegator delegator = GenericHelperFactory.getDefaultHelper();
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

<a href="<%=response.encodeURL(controlPath + "/sitemap")%>" target='main' class='listtext'>Pop up Site Map</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/entityref_main")%>" target="entityFrame" class='listtext'>Entity Reference Main Page</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/ModelWriter")%>" target="entityFrame" class='listtext'>Generate Entity Model XML</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/ModelWriter?savetofile=true")%>" target="entityFrame" class='listtext'>Save Entity Model XML to File</A><BR>
<a href="<%=response.encodeURL(controlPath + "/view/checkdb")%>" target="entityFrame" class='listtext'>Check/Update Database</A>
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
<a href="<%=response.encodeURL(controlPath + "/view/EditEntity?entityName=" + entityName)%>" target="entityFrame" class='listtext'>[EditDef]</a>
<a href="<%=response.encodeURL(controlPath + "/view/" + url)%>" target="entityFrame" class='listtext'><%= entityName %></a>
<br>
<%
		}	
	}
%>  
</div>
</body>
</html>
