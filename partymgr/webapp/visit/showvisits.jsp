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
	String partyId = request.getParameter("party_id");
	List visitList = null;
	if (partyId != null) {
		visitList = delegator.findByAnd("Visit", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("-fromDate"));	
	} else {
		// show active visits
		List exprs = UtilMisc.toList(new EntityExpr("thruDate", EntityOperator.EQUALS, null));
		visitList = delegator.findByAnd("Visit", exprs, UtilMisc.toList("-fromDate"));		
	}
	if (visitList != null) pageContext.setAttribute("visitList", visitList);
	String rowClass = "";
%>
		

<div class="head1"><%= partyId == null ? "Active" : "Party"%>&nbsp;Visit&nbsp;Listing</div>
<br>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td><div class="tableheadtext">VisitId</div></td>
    <% if (partyId == null) { %>
    <td><div class="tableheadtext">PartyId</div></td>
    <% } else { %>
    <td><div class="tableheadtext">UserLoginId</div></td>
    <% } %>
    <td><div class="tableheadtext">New User</div></td>
    <td><div class="tableheadtext">WebApp</div></td>
    <td><div class="tableheadtext">Client IP</div></td>
    <td><div class="tableheadtext">From Date</div></td>
    <td><div class="tableheadtext">Thru Date</div></td>
  </tr>
  <tr>
    <td colspan="7"><hr class="sepbar"></td>
  </tr>
  
  <ofbiz:iterator name="visit" property="visitList">
  <tr class="<%=rowClass = rowClass.equals("viewManyTR1") ? "viewManyTR2" : "viewManyTR1"%>">
    <td><a href="<ofbiz:url>/visitdetail?visitId=<%=UtilFormatOut.checkNull(visit.getString("visitId"))%></ofbiz:url>" class="buttontext"><%=UtilFormatOut.checkNull(visit.getString("visitId"))%></a></td>
    <% if (partyId == null) { %>
    <td><a href="<ofbiz:url>/viewprofile?party_id=<%=UtilFormatOut.checkNull(visit.getString("partyId"))%></ofbiz:url>" class="buttontext"><%=UtilFormatOut.checkNull(visit.getString("partyId"))%></a></td>
    <% } else { %>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("userLoginId"))%></div></td>
    <% } %>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("userCreated"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("webappName"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("clientIpAddress"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("fromDate"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visit.getString("thruDate"))%></div></td>
  </tr>
  </ofbiz:iterator>
</table>

<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>

    
  
    
