<%--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@author     Britton LaRoche
 *@author     David E. Jones
 *@author     Andy Zeneski
 *@version    1.0
--%>
<%@ page import="java.util.*, java.sql.*, java.text.*" %>

<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*, org.ofbiz.commonapp.party.party.*, org.ofbiz.commonapp.party.contact.*" %>

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

<%
    Calendar fromCal = Calendar.getInstance();
    fromCal.setTimeInMillis(System.currentTimeMillis());
    fromCal.set(Calendar.DAY_OF_WEEK, fromCal.getActualMinimum(Calendar.DAY_OF_WEEK));
    fromCal.set(Calendar.HOUR_OF_DAY, fromCal.getActualMinimum(Calendar.HOUR_OF_DAY));
    fromCal.set(Calendar.MINUTE, fromCal.getActualMinimum(Calendar.MINUTE));
    fromCal.set(Calendar.SECOND, fromCal.getActualMinimum(Calendar.SECOND));
    Timestamp fromTs = new Timestamp(fromCal.getTimeInMillis());
    String fromStr = fromTs.toString();
    fromStr = fromStr.substring(0, fromStr.indexOf('.'));

    Calendar toCal = Calendar.getInstance();
    toCal.setTimeInMillis(System.currentTimeMillis());
    toCal.set(Calendar.DAY_OF_WEEK, toCal.getActualMaximum(Calendar.DAY_OF_WEEK));
    toCal.set(Calendar.HOUR_OF_DAY, toCal.getActualMaximum(Calendar.HOUR_OF_DAY));
    toCal.set(Calendar.MINUTE, toCal.getActualMaximum(Calendar.MINUTE));
    toCal.set(Calendar.SECOND, toCal.getActualMaximum(Calendar.SECOND));
    Timestamp toTs = new Timestamp(toCal.getTimeInMillis());
    String toStr = toTs.toString();
    toStr = toStr.substring(0, toStr.indexOf('.'));
%>

<FORM METHOD="post" NAME="list" ACTION="<ofbiz:url>/orderreport.pdf</ofbiz:url>" TARGET="OrderReport">   
<Table>
<TR>
<TD><div class="tableheadtext">From Date:</div></td>
<td><INPUT TYPE="TEXT" NAME="fromDate" TABINDEX="10"  SIZE="22" MAXLENGTH="25" ALIGN="MIDDLE">
 <A
   TABINDEX="10"
   TARGET="_self"
    HREF="javascript:call_cal(document.list.fromDate, '<%=fromStr%>');"
   onfocus="checkForChanges = true;"
   onblur="checkForChanges = true;"
 >
  <IMG SRC='/images/cal.gif' WIDTH='16' HEIGHT='16' BORDER='0' ALT='Click here For Calendar'>
 </A>
</TD>
</TR>
<TR>
<TD><div class="tableheadtext">To Date:</div></td>
<td><INPUT TYPE="TEXT" NAME="toDate" TABINDEX="12"  SIZE="22" MAXLENGTH="25" ALIGN="MIDDLE">
 <A
   TABINDEX="12"
   TARGET="_self"
    HREF="javascript:call_cal(document.list.toDate, '<%=toStr%>');"
   onfocus="checkForChanges = true;"
   onblur="checkForChanges = true;"
 >
  <IMG SRC='/images/cal.gif' WIDTH='16' HEIGHT='16' BORDER='0' ALT='Click here For Calendar'>
 </A>
</TD>
</TR>
<tr>
<td><div class="tableheadtext">Report:</div></td>
<td>
   <SELECT NAME="groupName" tabindex="14"  CLASS="stateSelectBox">
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
 <INPUT TYPE="submit" TABINDEX="16" CLASS="button" NAME="GoReport" VALUE="Run Report">
</form>
