<%--
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
--%>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%@ page import="java.net.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.workflow.definition.*" %>

<%
    List messages = new LinkedList();
    
    String xpdlLoc = request.getParameter("XPDL_LOCATION");
    boolean xpdlIsUrl = request.getParameter("XPDL_IS_URL")!=null?true:false;
    boolean xpdlImport = request.getParameter("XPDL_IMPORT")!=null?true:false;
    
    URL xpdlUrl = null;
    try { xpdlUrl = xpdlIsUrl?new URL(xpdlLoc):UtilURL.fromFilename(xpdlLoc); }
    catch (java.net.MalformedURLException e) { messages.add(e.getMessage()); messages.add(e.toString()); Debug.logWarning(e); }
    if (xpdlUrl == null) messages.add("Could not find file/URL: " + xpdlLoc);
    
    List values = null;
    try { if (xpdlUrl != null) values = XpdlReader.readXpdl(xpdlUrl, delegator); }
    catch (Exception e) { messages.add(e.getMessage()); messages.add(e.toString()); Debug.logWarning(e); }

    if (values != null && xpdlImport) {
        try {
            delegator.storeAll(values);
            messages.add("Wrote/Updated " + values.size() + " values objects to the data source.");
        } catch (GenericEntityException e) {
            messages.add(e.getMessage()); messages.add(e.toString()); Debug.logWarning(e);
        }
    }
%>
<h3>View Data File</h3>
<div>This page is used to view data from data files parsed by the configurable data file parser.</div>

<%if(security.hasPermission("WORKFLOW_MAINT", session)) {%>
  <FORM method=POST action='<ofbiz:url>/readxpdl</ofbiz:url>'>
    XPDL Filename or URL: <INPUT name='XPDL_LOCATION' type=text size='60' value='<%=UtilFormatOut.checkNull(xpdlLoc)%>'> Is URL?:<INPUT type=checkbox name='XPDL_IS_URL' <%=xpdlIsUrl?"checked":""%>><BR>
    Import/Update to DB?:<INPUT type=checkbox name='XPDL_IMPORT'> <INPUT type=submit value='View'>
  </FORM>

  <hr>

  <%if(messages.size() > 0) {%>
    <H4>The following occurred:</H4>
    <UL>
    <%Iterator errMsgIter = messages.iterator();%>
    <%while(errMsgIter.hasNext()) {%>
      <LI><%=errMsgIter.next()%>
    <%}%>
    </UL>
    <HR>
  <%}%>

    <%Iterator viter = UtilMisc.toIterator(values);%>
    <%while (viter != null && viter.hasNext()) {%>
        <PRE><%=viter.next().toString()%></PRE>
    <%}%>

    <%if (values != null) {%>
        <div>Read and printed <%=values.size()%> entities.</div>
    <%} else {%>
        <div>No values read.</div>
    <%}%>
    
    
<%}else{%>
  <hr>
  <div>You do not have permission to use this page (WORKFLOW_MAINT needed)</div>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %> 
<%@ include file="/includes/footer.jsp" %>
