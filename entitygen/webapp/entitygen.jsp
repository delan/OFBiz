<%
/**
 *  Title: Entity Generator
 *  Description: None
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *
 *@author     David Jones
 *@created    May 15, 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="java.util.*" %>

<html>
<head>
<title>Entity Code Generator Home</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>

<h3>Entity Code Generator Home</h3>
The generated text is meant to be saved as is, and includes no HTML tags.
<br>It can be used either by viewing the page source, or right click and save as.
<br>NOTE: For JSPs, replace "[ltp]" with an actual &lt;%.
<br>NOTE: Be careful when saving over old generated code, it may have custom changes in it. Save it under a different name and do a diff/merge.

<%String ejbName=request.getParameter("ejbName");%>
<%String defFileName=request.getParameter("defFileName");%>
<%
  Iterator classNamesIterator = null;
  if(defFileName!=null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
%>

<hr>
<B><U>Definition XML File (enter the path of the file on the SERVER):</U></B>
<br>Current:  <b><%=(defFileName!=null?defFileName:"None")%></b>
<FORM method="POST" action="entitygen.jsp" name="defFileNameForm">
  <input type="file" size="50" name="defFileName">
  <a href="javascript:document.location='entitygen.jsp?defFileName=' + document.defFileNameForm.defFileName.value">Use</a>
</FORM>
<hr>
<%if(classNamesIterator != null){%>
<ul>
  <li>Combined Entity Snippets for all Entities in the XML definition file
  <ul>
    <li><a href="SnippetWebUpdateEvent.jsp?defFileName=<%=defFileName%>">WebUpdateEvents</a>
    <li><a href="SnippetWebEvent.properties.jsp?defFileName=<%=defFileName%>">webevent.properties</a>
    <li><a href="SnippetMySql.sql.jsp?defFileName=<%=defFileName%>">mysql.sql</a>
    <li><a href="SnippetDataMySql.sql.jsp?defFileName=<%=defFileName%>">data-mysql.sql</a>
    <li><a href="SnippetEjbJar.xml.enterprise-beans.jsp?defFileName=<%=defFileName%>">ejb-jar.xml.enterprise-beans</a>
    <li><a href="SnippetEjbJar.xml.assembly-descriptor.jsp?defFileName=<%=defFileName%>">ejb-jar.xml.assembly-descriptor</a>
    <li><a href="SnippetJboss.xml.jsp?defFileName=<%=defFileName%>">jboss.xml</a>
    <li><a href="SnippetJaws.xml.jsp?defFileName=<%=defFileName%>">jaws.xml</a>
    <li><a href="Snippeteventmaint.jsp.jsp?defFileName=<%=defFileName%>">eventmaint.jsp</a>
  </ul>
</ul>
<hr>
Select Entity for Entity specific code:
<form method="POST" action="entitygen.jsp?defFileName=<%=defFileName%>">
  <select name="ejbName">
    <option></option>
    <%while(classNamesIterator.hasNext()){%>
      <option><%=(String)classNamesIterator.next()%></option>
    <%}%>
  </select>
  <input type="submit" value="Use">
</form>
<%}else{%>
<h4>No Entity ejb-names found in specified XML file.</h4>
<%}%>
<hr>
<%if(ejbName != null && ejbName.length() > 0){%>
<b><u><%=ejbName%></u></b>
<ul>
  <li>Entity EJB Java Code
  <ul>
    <li><a href="Entity.java.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%>.java</a>
    <li><a href="EntityHome.java.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%>Home.java</a>
    <li><a href="EntityBean.java.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%>Bean.java</a>
    <li><a href="EntityPK.java.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%>PK.java</a> NOTE: This is only needed if you have multiple primary keys.
    <li><a href="EntityValue.java.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%>Value.java</a>
    <li><a href="EntityHelper.java.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%>Helper.java</a>
  </ul>
  <li>Entity JSPs
  <ul>
    <li><a href="EditEntity.jsp.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>">Edit<%=ejbName%>.jsp</a>
    <li><a href="FindEntity.jsp.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>">Find<%=ejbName%>.jsp</a>
    <li><a href="ViewEntity.jsp.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>">View<%=ejbName%>.jsp</a>
  </ul>
  <li>Entity Snippets
  <ul>
    <li><a href="SnippetWebUpdateEvent.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%> WebUpdateEvent</a>
    <li><a href="SnippetWebEvent.properties.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%> WebEvent Properties</a>
    <li><a href="SnippetMySql.sql.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%> SQL</a>
    <li><a href="SnippetDataMySql.sql.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>">SQL Data - permissions</a>
    <li><a href="SnippetEjbJar.xml.enterprise-beans.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%> ejb-jar.xml.enterprise-beans</a>
    <li><a href="SnippetEjbJar.xml.assembly-descriptor.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%> ejb-jar.xml.assembly-descriptor</a>
    <li><a href="SnippetJboss.xml.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%> jboss.xml</a>
    <li><a href="SnippetJaws.xml.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%> jaws.xml</a>
    <li><a href="Snippeteventmaint.jsp.jsp?defFileName=<%=defFileName%>&ejbName=<%=ejbName%>"><%=ejbName%> eventmaint.jsp</a>
  </ul>
</ul>
<%}%>

</body>
</html>