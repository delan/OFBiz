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
  boolean addMissing = false;
  String addMissingStr = request.getParameter("addMissing");
  if("true".equalsIgnoreCase(addMissingStr)) addMissing = true;
  
  Collection messages = new LinkedList();
  GenericDAO dao = GenericDAO.getGenericDAO(helper.getServerName());
  dao.checkDb(messages, addMissing);
  Iterator miter = messages.iterator();
%>

<html>
<head><title>Check/Update Database</title></head>
<body>

<H3>Check/Update Database</H3>
<A href='<%=response.encodeURL(controlPath + "/view/checkdb")%>'>Check Only</A>
<A href='<%=response.encodeURL(controlPath + "/view/checkdb?addMissing=true")%>'>Check and Add Missing</A>

<UL>
<%while(miter.hasNext()){%>
  <%String message = (String)miter.next();%>
  <LI><%=message%>
<%}%>
</UL>

</body>
</html>
