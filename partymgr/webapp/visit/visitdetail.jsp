<%--
 *  Description: None
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski 
 *@version    $Revision$
 *@since      2.0
--%>

<%@ page import="java.util.*, org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>

<%
	String visitId = request.getParameter("visitId");
	if (visitId == null) visitId = (String) request.getAttribute("visitId");
	
	GenericValue visit = null;
	List serverHits = null;
	if (visitId != null) {
		visit = delegator.findByPrimaryKey("Visit", UtilMisc.toMap("visitId", visitId));
		if (visit != null)
			serverHits = delegator.findByAnd("ServerHit", UtilMisc.toMap("visitId", visitId), UtilMisc.toList("-hitStartDateTime"));
	}
	if (visit != null) pageContext.setAttribute("visit", visit);
	if (serverHits != null) pageContext.setAttribute("serverHits", serverHits);
	String rowClass = "";
%>

<div class='head1'>Visit Detail</div>
<br>
<table width="90%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">VisitID / SessionID</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("visitId"))%> / <%=UtilFormatOut.checkNull(visit.getString("sessionId"))%></div>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">PartyID / UserLoginID</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext">
        <a href="<ofbiz:url>/viewprofile?party_id=<%=UtilFormatOut.checkNull(visit.getString("partyId"))%></ofbiz:url>" class="buttontext"><%=UtilFormatOut.checkNull(visit.getString("partyId"))%></a> / <a href="<ofbiz:url>/viewprofile?party_id=<%=UtilFormatOut.checkNull(visit.getString("partyId"))%></ofbiz:url>" class="buttontext"><%=UtilFormatOut.checkNull(visit.getString("userLoginId"))%></a>
      </div>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">UserCreated</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("userCreated"))%></div>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">WebApp</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("webappName"))%></div>
    </td>
  </tr>  
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">Server</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext">
        <a href="http://uptime.netcraft.com/up/graph/?site=<%=UtilFormatOut.checkNull(visit.getString("serverIpAddress"))%>" class="buttontext" target="_blank"><%=UtilFormatOut.checkNull(visit.getString("serverIpAddress"))%></a> / <a href="http://uptime.netcraft.com/up/graph/?site=<%=UtilFormatOut.checkNull(visit.getString("serverIpAddress"))%>" class="buttontext" target="_blank"><%=UtilFormatOut.checkNull(visit.getString("serverHostName"))%></a>
      </div>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">Client</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext">
        <a href="http://ws.arin.net/cgi-bin/whois.pl?queryinput=<%=UtilFormatOut.checkNull(visit.getString("clientIpAddress"))%>" class="buttontext" target="_blank"><%=UtilFormatOut.checkNull(visit.getString("clientIpAddress"))%></a> / <a href="http://www.networksolutions.com/cgi-bin/whois/whois?STRING=<%=UtilFormatOut.checkNull(visit.getString("clientHostName"))%>&SearchType=do" class="buttontext" target="_blank"><%=UtilFormatOut.checkNull(visit.getString("clientHostName"))%></a>
      </div>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">Client User</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("clientUser"))%></div>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">Initial Locale</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("initialLocale"))%></div>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">Initial Request</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <a href="<%=UtilFormatOut.checkNull(visit.getString("initialRequest"))%>" class="buttontext"><%=UtilFormatOut.checkNull(visit.getString("initialRequest"))%></a>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">Initial Referer</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <a href="<%=UtilFormatOut.checkNull(visit.getString("initialReferrer"))%>" class="buttontext"><%=UtilFormatOut.checkNull(visit.getString("initialReferrer"))%></a>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">Initial User Agent</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("initialUserAgent"))%></div>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">Cookie</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("cookie"))%></div>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tableheadtext">From-Date / Thru-Date</div></td>
    <td width="5">:&nbsp;</td>
    <td width="74%" align="left">
      <div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("fromDate"))%> / <%=UtilFormatOut.checkNull(visit.getString("thruDate"), "[Still Active]")%></div>
    </td>
  </tr>                             
</table>

<br>
<div class="head1">Hit Tracker</div>
<br>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td><div class="tableheadtext">ContentID</div></td>
    <td><div class="tableheadtext">Type</div></td>
    <td><div class="tableheadtext">&nbsp;&nbsp;Size</div></td>    
    <td><div class="tableheadtext">Start Time</div></td>
    <td><div class="tableheadtext">&nbsp;&nbsp;Time(ms)</div></td>
    <td><div class="tableheadtext">URI</div></td>
  </tr>
  <tr>
    <td colspan="6"><hr class="sepbar"></td>
  </tr>
  <ofbiz:iterator name="hit" property="serverHits">
  <tr class="<%=rowClass = rowClass.equals("viewManyTR1") ? "viewManyTR2" : "viewManyTR1"%>">
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(hit.getString("contentId"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(hit.getString("hitTypeId"))%></div></td>
    <td><div class="tabletext">&nbsp;&nbsp;<%=UtilFormatOut.checkNull(hit.getString("numOfBytes"), "?")%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(hit.getString("hitStartDateTime"))%></div></td>
    <td><div class="tabletext">&nbsp;&nbsp;<%=UtilFormatOut.checkNull(hit.getString("runningTimeMillis"))%></div></td>
    <td><a href="<%=UtilFormatOut.checkNull(hit.getString("requestUrl"))%>" class="buttontext" target="_blank"><%=UtilFormatOut.checkNull(hit.getString("requestUrl"))%></a></td>
  </tr>
  </ofbiz:iterator>
</table>  

<%if (security.hasPermission("SEND_CONTROL_APPLET", session)) {%>
<br>
<div class="head1">Page Push/Following</div>
<br>
<table border="0" cellpadding="5" cellspacing="5">
  <tr>
    <form name="pushPage" method="get" action="<ofbiz:url>/pushPage</ofbiz:url>">
    <td><div class="tableheadtext">Push URL</div></td>    
    <td>
      <input type="hidden" name="followerSid" value="<%=visit.getString("sessionId")%>">
      <input type="hidden" name="visitId" value="<%=visitId%>">
      <input type="input" name="pageUrl" class="inputBox">
    </td>
    <td><input type="submit" value="Submit" style="font-size: x-small;"></td>
  </tr>
  <tr>
    <td colspan="3"><hr class="sepbar"></td>
  </tr>
  <tr>
    <form name="setFollower" method="get" action="<ofbiz:url>/setAppletFollower</ofbiz:url>">
    <td><div class="tableheadtext">Follow Session</div></td>
    <td>
      <input type="hidden" name="followerSid" value="<%=visit.getString("sessionId")%>">
      <input type="hidden" name="visitId" value="<%=visitId%>">
      <input type="text" name=followSid" class="inputBox">
    </td>
    <td><input type="submit" value="Submit" style="font-size: x-small;"></td>
  </tr>
</table>
<%}%>

<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
