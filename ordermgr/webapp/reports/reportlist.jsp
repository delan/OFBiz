<%--
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
 *@author     Eric Pabst
 *@author     David E. Jones
 *@author     Andy Zeneski
 *@created    May 22 2001
 *@version    1.0
--%>
<%@ page import="java.util.*, java.text.*" %>

<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*, org.ofbiz.commonapp.party.party.*, org.ofbiz.commonapp.party.contact.*" %>


<%@ include file="/includes/ts_picker.js" %> 

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<br>
<div class='tabletext'>NOTE: These report are for demonstration purposes only. 
They use the JasperReports reporting tool. They have not been polished yet, but 
they are good examples of creating detailed reports that you have a lot of 
control over. special thanks for Britton LaRoche for creating the first pass of
these reports and helping to improve them.</div>
<br>

<FORM METHOD="post" NAME="list" ACTION="/ordermgr/control/orderreport.pdf" TARGET="none">   
<Table>
<TR>
<TD>From Date:         
<INPUT TYPE="TEXT" NAME="fromDate" TABINDEX="10"  SIZE="15" MAXLENGTH="20" ALIGN="MIDDLE">
 <A
   TABINDEX="10"
   TARGET="_self"
    HREF="javascript:show_calendar('document.list.fromDate', '', 0);"
   onfocus="checkForChanges = true;"
   onblur="checkForChanges = true;"
 >
  <IMG SRC='/images/cal.gif' WIDTH='16' HEIGHT='16' BORDER='0' ALT='Click here for calendar'>
 </A>
</TD>
</TR>
<TR>
<TD>To Date: 
<INPUT TYPE="TEXT" NAME="toDate" TABINDEX="12"  SIZE="15" MAXLENGTH="20" ALIGN="MIDDLE">
 <A
   TABINDEX="12"
   TARGET="_self"
    HREF="javascript:show_calendar('document.list.toDate', '', 0);"
   onfocus="checkForChanges = true;"
   onblur="checkForChanges = true;"
 >
  <IMG SRC='/images/cal.gif' WIDTH='16' HEIGHT='16' BORDER='0' ALT='Click here for calendar'>
 </A>
</TD>
</TR>
<Table>

<table width="100%" border=0 cellspacing=0 cellpadding=0>
    <tr>
    <td>
	<SELECT NAME="groupName" tabindex="10"  CLASS="stateSelectBox">
		<OPTION VALUE="orderStatus"></OPTION>
		<OPTION VALUE="orderStatus">Orders by Order Status</OPTION>
		<OPTION VALUE="ship">Orders by Ship Method</OPTION>
		<OPTION VALUE="payment">Orders by Payment Method</OPTION>
		<OPTION VALUE="adjustment">Order Items by Adjustment</OPTION>
		<OPTION VALUE="itemStatus">Order Items by Status</OPTION>
		<OPTION VALUE="product">Order Items by Product</OPTION>
		</SELECT>
    </td>
    </tr>
</table>
 <INPUT TYPE="submit" CLASS="button" NAME="GoReport" VALUE="Run Report">
</form>           