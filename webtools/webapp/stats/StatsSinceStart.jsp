<%
/**
 *  Title: View Stats Since Server Start
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
 *@created    February 4, 2002
 *@version    1.0
 */
%> 

<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="java.util.*" %>

<%pageContext.setAttribute("PageName", "Server Stats Since Start");%>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<br>
<h2 style='margin:0;'>Server Statistics Since Start Page</h2>

<%if (security.hasPermission("SERVER_STATS_VIEW", session)) {%>
<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";
%>

<%-- REQUEST --%>

<TABLE border='0' cellpadding='2' cellspacing='2'>
  <TR bgcolor='CCCCFF'>
    <TD>Reqest&nbsp;ID</TD>
    <TD>Start</TD>
    <TD>Stop</TD>
    <TD>Mins</TD>
    <TD>Hits</TD>
    <TD>Min</TD>
    <TD>Avg</TD>
    <TD>Max</TD>
    <TD>Hits/Minute</TD>
    <TD>View&nbsp;Bins</TD>
  </TR>

  <%TreeSet requestIds = new TreeSet(ServerHitBin.requestSinceStarted.keySet());%>
  <%Iterator requestIdIter = requestIds.iterator();%>
  <%if(requestIdIter!=null && requestIdIter.hasNext()){%>
    <%while(requestIdIter.hasNext()){%>
      <%String statsId = (String)requestIdIter.next();%>
      <%ServerHitBin bin = (ServerHitBin) ServerHitBin.requestSinceStarted.get(statsId);%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
      <tr bgcolor="<%=rowColor%>">
        <TD><%=bin.getId()%></TD>
        <TD><%=bin.getStartTimeString()%></TD>
        <TD><%=bin.getEndTimeString()%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getBinLengthMinutes())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getNumberHits())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getMinTimeSeconds())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getAvgTimeSeconds())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getMaxTimeSeconds())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getHitsPerMinute())%></TD>
        <TD><a href='<ofbiz:url>/StatBinsHistory?statsId=<%=bin.getId()%>&type=<%=bin.getType()%></ofbiz:url>' class='buttontext'>View&nbsp;Bins</a></TD>
      </TR>
    <%}%>
  <%}else{%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <TD colspan="9">No Request stats found.</TD>
      </TR>
  <%}%>
</TABLE>
<BR>
<%-- EVENT --%>

<TABLE border='0' cellpadding='2' cellspacing='2'>
  <TR bgcolor='CCCCFF'>
    <TD>Event&nbsp;ID</TD>
    <TD>Start</TD>
    <TD>Stop</TD>
    <TD>Mins</TD>
    <TD>Hits</TD>
    <TD>Min</TD>
    <TD>Avg</TD>
    <TD>Max</TD>
    <TD>Hits/Minute</TD>
    <TD>View&nbsp;Bins</TD>
  </TR>

  <%TreeSet eventIds = new TreeSet(ServerHitBin.eventSinceStarted.keySet());%>
  <%Iterator eventIdIter = eventIds.iterator();%>
  <%if(eventIdIter!=null && eventIdIter.hasNext()){%>
    <%while(eventIdIter.hasNext()){%>
      <%String statsId = (String)eventIdIter.next();%>
      <%ServerHitBin bin = (ServerHitBin) ServerHitBin.eventSinceStarted.get(statsId);%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
      <tr bgcolor="<%=rowColor%>">
        <TD><%=bin.getId()%></TD>
        <TD><%=bin.getStartTimeString()%></TD>
        <TD><%=bin.getEndTimeString()%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getBinLengthMinutes())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getNumberHits())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getMinTimeSeconds())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getAvgTimeSeconds())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getMaxTimeSeconds())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getHitsPerMinute())%></TD>
        <TD><a href='<ofbiz:url>/StatBinsHistory?statsId=<%=bin.getId()%>&type=<%=bin.getType()%></ofbiz:url>' class='buttontext'>View&nbsp;Bins</a></TD>
      </TR>
    <%}%>
  <%}else{%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <TD colspan="9">No Event stats found.</TD>
      </TR>
  <%}%>
</TABLE>
<BR>
<%-- VIEW --%>

<TABLE border='0' cellpadding='2' cellspacing='2'>
  <TR bgcolor='CCCCFF'>
    <TD>View&nbsp;ID</TD>
    <TD>Start</TD>
    <TD>Stop</TD>
    <TD>Mins</TD>
    <TD>Hits</TD>
    <TD>Min</TD>
    <TD>Avg</TD>
    <TD>Max</TD>
    <TD>Hits/Minute</TD>
    <TD>View&nbsp;Bins</TD>
  </TR>

  <%TreeSet viewIds = new TreeSet(ServerHitBin.viewSinceStarted.keySet());%>
  <%Iterator viewIdIter = viewIds.iterator();%>
  <%if(viewIdIter!=null && viewIdIter.hasNext()){%>
    <%while(viewIdIter.hasNext()){%>
      <%String statsId = (String)viewIdIter.next();%>
      <%ServerHitBin bin = (ServerHitBin) ServerHitBin.viewSinceStarted.get(statsId);%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
      <tr bgcolor="<%=rowColor%>">
        <TD><%=bin.getId()%></TD>
        <TD><%=bin.getStartTimeString()%></TD>
        <TD><%=bin.getEndTimeString()%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getBinLengthMinutes())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getNumberHits())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getMinTimeSeconds())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getAvgTimeSeconds())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getMaxTimeSeconds())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(bin.getHitsPerMinute())%></TD>
        <TD><a href='<ofbiz:url>/StatBinsHistory?statsId=<%=bin.getId()%>&type=<%=bin.getType()%></ofbiz:url>' class='buttontext'>View&nbsp;Bins</a></TD>
      </TR>
    <%}%>
  <%}else{%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <TD colspan="9">No View stats found.</TD>
      </TR>
  <%}%>
</TABLE>

<%}else{%>
  <h3>You do not have permission to view this page (SERVER_STATS_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
