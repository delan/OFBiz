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
	String showAll = request.getParameter("showAll");
	String sort = request.getParameter("sort");
	if (showAll == null || showAll.equals("")) showAll = "false";
	
	EntityListIterator visitListIt = null;			
	List sortList = UtilMisc.toList("-fromDate");
	if (sort != null) sortList.add(0, sort);
	
	if (partyId != null) {
		visitListIt = delegator.findListIteratorByCondition("Visit", new EntityExpr("partyId", EntityOperator.EQUALS, partyId), null, null, sortList, new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));	
	} else if (showAll.equalsIgnoreCase("true")) {
		visitListIt = delegator.findListIteratorByCondition("Visit", null, null, null, sortList, new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
	} else {
		// show active visits		
		visitListIt = delegator.findListIteratorByCondition("Visit", new EntityExpr("thruDate", EntityOperator.EQUALS, null), null, null, sortList, new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));	
	}

	String rowClass = "";
	
    int viewIndex = 0;
    int viewSize = 20;
    int highIndex = 0;
    int lowIndex = 0;
    int listSize = 0;
    
    try {
        viewIndex = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_INDEX")).intValue();
    } catch (Exception e) {
        viewIndex = 0;
    }
    try {
        viewSize = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_SIZE")).intValue();
    } catch (Exception e) {
        viewSize = 20;
    }
    
    lowIndex = viewIndex * viewSize;
    highIndex = (viewIndex + 1) * viewSize;
    
    // attempt to get the full size
    visitListIt.last();
    int visitSize = visitListIt.currentIndex();
    visitListIt.first();
            
    List visitList = visitListIt.getPartialList(lowIndex + 1, viewSize + 1);
	if (visitList != null) pageContext.setAttribute("visitList", visitList);
    
    if (visitList != null) {
    	listSize = lowIndex + visitList.size();
    }

    if (listSize < highIndex) {
        highIndex = listSize;
    }	
%>
		

<div class="head1"><%= partyId == null ? "Active" : "Party"%>&nbsp;Visit&nbsp;Listing</div>
<%if (partyId == null && showAll.equalsIgnoreCase("true")) {%>
<a href="<ofbiz:url>/showvisits?showAll=false</ofbiz:url>" class="buttontext">[Show Active]</a>
<%} else if (partyId == null) {%>
<a href="<ofbiz:url>/showvisits?showAll=true</ofbiz:url>" class="buttontext">[Show All]</a>
<%}%> 
<br>

<ofbiz:if name="visitList" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/showvisits?VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%><%=UtilFormatOut.ifNotEmpty(sort, "&sort=","")%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%>&showAll=<%=new Boolean(showAll).toString()%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <span class="tabletext"><%=lowIndex+1%> - <%=highIndex%> of <%=visitSize%></span>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/showvisits?VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%><%=UtilFormatOut.ifNotEmpty(sort, "&sort=","")%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%>&showAll=<%=new Boolean(showAll).toString()%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<ofbiz:unless name="visitList" size="0">
<br>
</ofbiz:unless>

<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td><a href="<ofbiz:url>/showvisits?sort=visitId&showAll=<%=new Boolean(showAll).toString()%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%></ofbiz:url>" class="tableheadbutton">VisitId</a></td>
    <% if (partyId == null) { %>
    <td><a href="<ofbiz:url>/showvisits?sort=partyId&showAll=<%=new Boolean(showAll).toString()%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%></ofbiz:url>" class="tableheadbutton">PartyId</a></td>
    <% } %>
    <td><a href="<ofbiz:url>/showvisits?sort=userLoginId&showAll=<%=new Boolean(showAll).toString()%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%></ofbiz:url>" class="tableheadbutton">UserLoginId</a></td>
    <td><a href="<ofbiz:url>/showvisits?sort=userCreated&showAll=<%=new Boolean(showAll).toString()%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%></ofbiz:url>" class="tableheadbutton">New User</a></td>
    <td><a href="<ofbiz:url>/showvisits?sort=webappName&showAll=<%=new Boolean(showAll).toString()%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%></ofbiz:url>" class="tableheadbutton">WebApp</a></td>
    <td><a href="<ofbiz:url>/showvisits?sort=clientIpAddress&showAll=<%=new Boolean(showAll).toString()%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%></ofbiz:url>" class="tableheadbutton">Client IP</a></td>
    <td><a href="<ofbiz:url>/showvisits?sort=fromDate&showAll=<%=new Boolean(showAll).toString()%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%></ofbiz:url>" class="tableheadbutton">From Date</a></td>
    <td><a href="<ofbiz:url>/showvisits?sort=thruDate&showAll=<%=new Boolean(showAll).toString()%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%></ofbiz:url>" class="tableheadbutton">Thru Date</a></td>
  </tr>
  <tr>
    <td colspan="<%=partyId == null ? 8 : 7%>"><hr class="sepbar"></td>
  </tr>
  
  <ofbiz:iterator name="visitObj" property="visitList" limit="<%=viewSize%>">
  <tr class="<%=rowClass = rowClass.equals("viewManyTR1") ? "viewManyTR2" : "viewManyTR1"%>">
    <td><a href="<ofbiz:url>/visitdetail?visitId=<%=UtilFormatOut.checkNull(visitObj.getString("visitId"))%></ofbiz:url>" class="buttontext"><%=UtilFormatOut.checkNull(visitObj.getString("visitId"))%></a></td>
    <% if (partyId == null) { %>
    <td><a href="<ofbiz:url>/viewprofile?party_id=<%=UtilFormatOut.checkNull(visitObj.getString("partyId"))%></ofbiz:url>" class="buttontext"><%=UtilFormatOut.checkNull(visitObj.getString("partyId"))%></a></td>
    <% } %>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visitObj.getString("userLoginId"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visitObj.getString("userCreated"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visitObj.getString("webappName"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visitObj.getString("clientIpAddress"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visitObj.getString("fromDate"))%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(visitObj.getString("thruDate"))%></div></td>
  </tr>
  </ofbiz:iterator>
</table>

<ofbiz:if name="visitList" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/showvisits?VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%><%=UtilFormatOut.ifNotEmpty(sort, "&sort=","")%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%>&showAll=<%=new Boolean(showAll).toString()%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <span class="tabletext"><%=lowIndex+1%> - <%=highIndex%> of <%=visitSize%></span>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/showvisits?VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%><%=UtilFormatOut.ifNotEmpty(sort, "&sort=","")%><%=UtilFormatOut.ifNotEmpty(partyId, "&party_id=","")%>&showAll=<%=new Boolean(showAll).toString()%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>

<% visitListIt.close(); %>

<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>

    
  
    
