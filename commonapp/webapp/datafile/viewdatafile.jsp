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
<%@ page import="org.ofbiz.core.datafile.*" %>

<%
  List errorMessages = new LinkedList();

  String dataFileLoc = request.getParameter("DATAFILE_LOCATION");
  String definitionLoc = request.getParameter("DEFINITION_LOCATION");
  String definitionName = request.getParameter("DEFINITION_NAME");
  boolean dataFileIsUrl = request.getParameter("DATAFILE_IS_URL")!=null?true:false;
  boolean definitionIsUrl = request.getParameter("DEFINITION_IS_URL")!=null?true:false;

  URL dataFileUrl = null;
  try { dataFileUrl = dataFileIsUrl?new URL(dataFileLoc):UtilURL.fromFilename(dataFileLoc); }
  catch(java.net.MalformedURLException e) { errorMessages.add(e.getMessage()); }
  URL definitionUrl = null;
  try { definitionUrl = definitionIsUrl?new URL(definitionLoc):UtilURL.fromFilename(definitionLoc); }
  catch(java.net.MalformedURLException e) { errorMessages.add(e.getMessage()); }

  DataFile dataFile = null;
  if(dataFileUrl != null && definitionUrl != null && definitionName != null && definitionName.length() > 0) {
    try { dataFile = DataFile.readFile(dataFileUrl, definitionUrl, definitionName); }
    catch(Exception e) { errorMessages.add(e.getMessage()); }
  }

  ModelDataFile modelDataFile = null;
  if(dataFile != null) modelDataFile = dataFile.modelDataFile;
%>
<h3>View Data File</h3>
<div>This page is used to view data from data files parsed by the configurable data file parser.</div>

<%if(security.hasPermission("DATAFILE_MAINT", session)) {%>
  <FORM method=POST action='<ofbiz:url>/viewdatafile</ofbiz:url>'>
    Data Filename or URL: <INPUT name='DATAFILE_LOCATION' type=text size='60' value='<%=UtilFormatOut.checkNull(dataFileLoc)%>'> Is URL?:<INPUT type=checkbox name='DATAFILE_IS_URL' <%=dataFileIsUrl?"checked":""%>><BR>
    Definition Filename or URL: <INPUT name='DEFINITION_LOCATION' type=text size='60' value='<%=UtilFormatOut.checkNull(definitionLoc)%>'> Is URL?:<INPUT type=checkbox name='DEFINITION_IS_URL' <%=definitionIsUrl?"checked":""%>><BR>
    Data File Definition Name: <INPUT name='DEFINITION_NAME' type=text size='30' value='<%=UtilFormatOut.checkNull(definitionName)%>'><BR>
    <INPUT type=submit value='View'>
  </FORM>

  <hr>

  <%if(errorMessages.size() > 0) {%>
    <H4>The following errors occurred:</H4>
    <UL>
    <%Iterator errMsgIter = errorMessages.iterator();%>
    <%while(errMsgIter.hasNext()) {%>
      <LI><%=errMsgIter.next()%>
    <%}%>
    </UL>
  <%}%>

  <%if(dataFile != null && modelDataFile != null) {%>
    <TABLE cellpadding='2' cellspacing='0' border='1'>
      <TR>
        <TD><B>Name</B></TD>
        <TD><B>Type-Code</B></TD>
        <TD><B>Sender</B></TD>
        <TD><B>Receiver</B></TD>
        <TD><B>Record Length</B></TD>
        <TD><B>Separator Style</B></TD>
      </TR>
      <TR>
        <TD><%=modelDataFile.name%></TD>
        <TD><%=modelDataFile.typeCode%></TD>
        <TD><%=modelDataFile.sender%></TD>
        <TD><%=modelDataFile.receiver%></TD>
        <TD><%=modelDataFile.recordLength%></TD>
        <TD><%=modelDataFile.separatorStyle%></TD>
      </TR>
      <TR>
        <TD colspan='6'>Description: <%=modelDataFile.description%></TD>
      </TR>
    </TABLE>
    <BR>
    <%request.setAttribute("CUR_RECORD_LIST", dataFile.records);%>
    <%pageContext.include("/datafile/showrecords.jsp");%>
  <%}%>

<%}else{%>
  <hr>
  <div>You do not have permission to use this page (DATAFILE_MAINT needed)</div>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %> 
<%@ include file="/includes/footer.jsp" %>
