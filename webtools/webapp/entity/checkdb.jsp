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

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<%
if(security.hasPermission("ENTITY_MAINT", session)) {
  boolean addMissing = false;
  String addMissingStr = request.getParameter("addMissing");
  if("true".equalsIgnoreCase(addMissingStr)) addMissing = true;
  
  String groupName = request.getParameter("groupName");
  
  Iterator miter = null;

  if(groupName != null && groupName.length() > 0) {
    String helperName = delegator.getGroupHelperName(groupName);

    Collection messages = new LinkedList();
    GenericHelper helper = GenericHelperFactory.getHelper(helperName);
    Map modelEntities = delegator.getModelEntityMapByGroup(groupName);

    helper.checkDataSource(modelEntities, messages, addMissing);
    miter = messages.iterator();
  }
%>

<H3>Check/Update Database</H3>

<form method=post action='<%=response.encodeURL(controlPath + "/view/checkdb")%>'>
  Group Name: <INPUT type=text name='groupName' value='<%=groupName!=null?groupName:"org.ofbiz.commonapp"%>' size='60'>
  <INPUT type=submit value='Check Only'>
</form>
<form method=post action='<%=response.encodeURL(controlPath + "/view/checkdb?addMissing=true")%>'>
  Group Name: <INPUT type=text name='groupName' value='<%=groupName!=null?groupName:"org.ofbiz.commonapp"%>' size='60'>
  <INPUT type=submit value='Check and Add Missing'>
</form>

<hr>
<UL>
<%while(miter != null && miter.hasNext()){%>
  <%String message = (String)miter.next();%>
  <LI><%=message%>
<%}%>
</UL>
<%}else{%>
<H3>Entity Editor</H3>

ERROR: You do not have permission to use this page (ENTITY_MAINT needed)
<%}%>
