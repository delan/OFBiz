<%--
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
 *@author     David E. Jones
 *@created    December 12 2002
 *@version    1.0
--%> 

<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
	String findOrganizationPartyId = request.getParameter("findOrganizationPartyId");
	String findParentPeriodId = request.getParameter("findParentPeriodId");

	List periodTypes = delegator.findAllCache("PeriodType");
	if (periodTypes != null) pageContext.setAttribute("periodTypes", periodTypes);
	
%>

<br>
<h2 style='margin:0;'>Custom Time Period Maintenance Page</h2>

<%if(security.hasPermission("PERIOD_MAINT", session)){%>
<!--
        <attribute name="customTimePeriodId" type="String" mode="IN" optional="false"/>
        <attribute name="parentPeriodId" type="String" mode="IN" optional="true"/>
        <attribute name="organizationPartyId" type="String" mode="IN" optional="true"/>
        <attribute name="periodTypeId" type="String" mode="IN" optional="false"/>
        <attribute name="periodNum" type="Long" mode="IN" optional="true"/>
        <attribute name="fromDate" type="Timestamp" mode="IN" optional="true"/>
        <attribute name="thruDate" type="Timestamp" mode="IN" optional="true"/>
-->
<TABLE border='1' cellpadding='2' cellspacing='0'>
  <TR>
    <TD><div class='tabletext'>ID</div></TD>
    <TD><div class='tabletext'>Parent&nbsp;ID</div></TD>
    <TD><div class='tabletext'>Org&nbsp;Party&nbsp;ID</div></TD>
    <TD><div class='tabletext'>Period&nbsp;Type</div></TD>
    <TD><div class='tabletext'>Period&nbsp;Number</div></TD>
    <TD><div class='tabletext'>From&nbsp;Date</div></TD>
    <TD><div class='tabletext'>Thru&nbsp;Date</div></TD>
    <TD><div class='tabletext'>&nbsp;</div></TD>
    <TD><div class='tabletext'>&nbsp;</div></TD>
  </TR>

</TABLE>

<%} else {%>
  <h3>You do not have permission to view this page (PERIOD_MAINT needed).</h3>
<%}%>
