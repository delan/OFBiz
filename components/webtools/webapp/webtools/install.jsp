<%--
 *  Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*, java.net.*, java.sql.*, org.w3c.dom.*"%>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.model.*, org.ofbiz.entity.config.*, org.ofbiz.entity.jdbc.*, org.ofbiz.entity.util.*"%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />

<%
	String groupfile = request.getParameter("groupfile");
	String loadFile = request.getParameter("loadFile");
	String groupName = request.getParameter("groupName");
	String helperName = delegator.getGroupHelperName(groupName);
	String paths = EntityDataLoader.getPathsString(helperName);
	List urlList = EntityDataLoader.getUrlList(helperName);
%>
<br>
Specify the group name for the entity group whose data you want to load:<br>
<form method=post action='<ofbiz:url>/install?groupfile=group</ofbiz:url>'>
  Group Name: <INPUT type=text class="inputBox" name='groupName' value='<%=groupName!=null?groupName:"org.ofbiz"%>' size='60'>
  <INPUT type=submit style="font-size: x-small;" value='Load Data'>
</form>
<br>
OR Specify the filename of a ".xml" file to load:<br>
<form method=post action='<ofbiz:url>/install?groupfile=file</ofbiz:url>'>
  Server File Path/Name: <INPUT type=text class="inputBox" name='loadFile' value='<%=loadFile!=null?loadFile:""%>' size='60'>
  <INPUT type=submit style="font-size: x-small;" value='Load Data File'>
</form>
<div>OR click <a href='<ofbiz:url>/install?groupfile=gensecurity</ofbiz:url>'>here</a> for entity granularity security settings only (auto generated, not in a file)</div>
<hr>
<%if ("group".equals(groupfile)) {%>
  <%if (groupName != null && groupName.length() > 0) {%>
    <%if (request.getParameter("loadfiles") == null) {%>
      <br>
      <DIV class='head1'>Open For Business Installation (Data Load) Page</DIV>
      <DIV class='head2'>Do you want to load the following XML files?</DIV>
      <DIV class='tabletext'>(From component resources and the path list: "<%=UtilFormatOut.checkNull(paths)%>")</DIV>
      <UL>
        <%if (urlList.size() > 0) {%>
          <%Iterator urlIter = urlList.iterator();%>
          <%while (urlIter.hasNext()) {%>
            <%URL dataUrl = (URL) urlIter.next();%>
            <LI><DIV class='tabletext'><%=dataUrl.toExternalForm()%></DIV>
          <%}%>
        <%} else {%>
          <LI><DIV class='tabletext'>No XML Files found.</DIV>
        <%}%>
      </UL>
      <A href='<ofbiz:url>/install?loadfiles=true&groupfile=group&groupName=<%=groupName%></ofbiz:url>' class='buttontext'>[Yes, Load Now]</A>
    <%} else {%>
      <%List errorMessages = new LinkedList();%>
      <br>
      <DIV class='head1'>Open For Business Installation (Data Load) Page</DIV>
      <DIV class='head2'>Loading the XML files...</DIV>
      <DIV class='tabletext'>(From component resources and the path list: "<%=UtilFormatOut.checkNull(paths)%>")</DIV>
      <UL>
        <%int totalRowsChanged = 0;%>
        <%if (urlList.size() > 0) {%>
          <%Iterator urlIter = urlList.iterator();%>
          <%while (urlIter.hasNext()) {%>
            <%URL dataUrl = (URL) urlIter.next();%>
            <%int rowsChanged = EntityDataLoader.loadData(dataUrl, helperName, delegator, errorMessages);%>
            <%totalRowsChanged += rowsChanged;%>
            <LI><DIV class='tabletext'>Loaded <%=rowsChanged%> rows from <%=dataUrl.toExternalForm()%> (<%=totalRowsChanged%> total rows so far)</DIV>
          <%}%>
        <%} else {%>
          <LI><DIV class='tabletext'>No XML Files found.</DIV>
        <%}%>
      </UL>
      <DIV class='head2'>Finished loading all data; <%=totalRowsChanged%> total rows updated.</DIV>

      <DIV class='head2'>Error Messages:</DIV>
      <UL>
        <%Iterator errIter = errorMessages.iterator();%>
        <%while (errIter.hasNext()){%>
          <LI><%=(String) errIter.next()%>
        <%}%>
      </UL>

    <%}%>
  <%}%>
<%} else if("file".equals(groupfile)) {%>
  <%if (loadFile != null && loadFile.length() > 0) {%>
    <%URL dataUrl = UtilURL.fromFilename(loadFile);%>
    <%List errorMessages = new LinkedList();%>
    <%int rowsChanged = EntityDataLoader.loadData(dataUrl, helperName, delegator, errorMessages);%>
    <DIV class='head2'>Finished loading file data; <%=rowsChanged%> total rows updated.</DIV>

    <DIV class='head2'>Error Messages:</DIV>
    <UL>
      <%Iterator errIter = errorMessages.iterator();%>
      <%while (errIter.hasNext()){%>
        <LI><%=(String) errIter.next()%>
      <%}%>
    </UL>
  <%}%>
<%}else if("gensecurity".equals(groupfile)) {%>
    <%List errorMessages = new LinkedList();%>
    <%int rowsChanged = EntityDataLoader.generateData(delegator, errorMessages);%>
    <DIV class='head2'>Finished loading file data; <%=rowsChanged%> total rows updated.</DIV>

    <DIV class='head2'>Error Messages:</DIV>
    <UL>
      <%Iterator errIter = errorMessages.iterator();%>
      <%while (errIter.hasNext()){%>
        <LI><%=(String) errIter.next()%>
      <%}%>
    </UL>
<%}%>
