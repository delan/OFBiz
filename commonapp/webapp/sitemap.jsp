<%
/**
 *  Title: Site Map Page
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
 *@author     David E. Jones
 *@created    May 22 2001
 *@version    1.0
 */
%> 

<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>

<% pageContext.setAttribute("PageName", "sitemap"); %> 

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<h2 style='margin:0;'>Site Map</h2>
<ul>
  <li><a href="<%=response.encodeURL(controlPath + "/main")%>" class="buttontext">Main</a>
  <%if(security.hasPermission("ENTITY_MAINT", session)){%>
    <li>Entity Maintenance Utilities
    <ul>
      <li><a href="<%=response.encodeURL(controlPath + "/entitymaint")%>" class='buttontext'>Entity Data Maintenance</A>
      <li><a href="<%=response.encodeURL(controlPath + "/view/entityref")%>" class='buttontext'>Entity Reference</A>
      <li><a href="<%=response.encodeURL(controlPath + "/view/tablesMySql")%>" class='buttontext'>MySQL Table Creation SQL</A>
      <li><a href="<%=response.encodeURL(controlPath + "/view/dataMySql")%>" class='buttontext'>MySQL Auto Data SQL</A>
      <li><a href="<%=response.encodeURL(controlPath + "/view/ModelWriter")%>" class='buttontext'>Generate Entity Model XML</A>
      <li><a href="<%=response.encodeURL(controlPath + "/view/EditEntity")%>" class='buttontext'>Edit Entity Definitions</A> (also see the Entity Reference for this)
    </ul>
  <%}%>
  <%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>
    <li><a href="<%=response.encodeURL(controlPath + "/FindUtilCache")%>" class='buttontext'>Cache Maintenance</A>
  <%}%>
</ul>
<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
