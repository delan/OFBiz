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
	search = (String) request.getParameter("search");
%>

<html>
<head>
<title>Entity Reference</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body bgcolor="#FFFFFF">
<div align="left">
	
<%
	Iterator i = entities.iterator();
	while ( i.hasNext() ) {
		Object o = i.next();
		String entityName = (String) o;
		if ( search == null || entityName.toLowerCase().indexOf(search.toLowerCase()) != -1 ) {						
			String url = search == null ? "entityref_main.jsp#"+entityName : "entityref_main.jsp#"+entityName+"?search="+search;
%>	
<a href="<%= url %>" target="entityFrame"><%= entityName %></a><br>
<%
		}	
	}
%>  
</div>
</body>
</html>
