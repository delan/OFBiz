<%
/**
 *  Title: Main Page
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
 *@author     Andy Zeneski (jaz@zsolv.com)
 *@author     David E. Jones
 *@created    October 18, 2001
 *@version    1.0
 */
%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*, java.sql.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*, org.ofbiz.commonapp.party.contact.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<% 

	    Timestamp endTime = UtilDateTime.nowTimestamp();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.AM_PM, Calendar.AM);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);	
		cal.set(Calendar.MILLISECOND, 0);	
		Timestamp dayBegin = new Timestamp(cal.getTime().getTime());	
		Debug.logInfo("Day Begin: " + dayBegin);
		
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		Timestamp weekBegin = new Timestamp(cal.getTime().getTime());
		Debug.logInfo("Week Begin: " + weekBegin);
		
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Timestamp monthBegin = new Timestamp(cal.getTime().getTime());
		Debug.logInfo("Month Begin: " + monthBegin);
		
		cal.set(Calendar.MONTH, 1);
		Timestamp yearBegin = new Timestamp(cal.getTime().getTime());
		Debug.logInfo("Year Begin: " + yearBegin);
		
		List dayList = delegator.findByAnd("OrderStatus", UtilMisc.toList(new EntityExpr("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
		List dayOrder = EntityUtil.filterByAnd(dayList, UtilMisc.toMap("statusId", "ORDER_ORDERED"));
		List dayApprove = EntityUtil.filterByAnd(dayList, UtilMisc.toMap("statusId", "ORDER_APPROVED"));
		List dayComplete = EntityUtil.filterByAnd(dayList, UtilMisc.toMap("statusId", "ORDER_COMPLETED"));
		
		List weekList = delegator.findByAnd("OrderStatus", UtilMisc.toList(new EntityExpr("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, weekBegin)));
		List weekOrder = EntityUtil.filterByAnd(weekList, UtilMisc.toMap("statusId", "ORDER_ORDERED"));
		List weekApprove = EntityUtil.filterByAnd(weekList, UtilMisc.toMap("statusId", "ORDER_APPROVED"));
		List weekComplete = EntityUtil.filterByAnd(weekList, UtilMisc.toMap("statusId", "ORDER_COMPLETED"));
		
		List monthList = delegator.findByAnd("OrderStatus", UtilMisc.toList(new EntityExpr("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin)));
		List monthOrder = EntityUtil.filterByAnd(monthList, UtilMisc.toMap("statusId", "ORDER_ORDERED"));
		List monthApprove = EntityUtil.filterByAnd(monthList, UtilMisc.toMap("statusId", "ORDER_APPROVED"));
		List monthComplete = EntityUtil.filterByAnd(monthList, UtilMisc.toMap("statusId", "ORDER_COMPLETED"));
				
		List yearList = delegator.findByAnd("OrderStatus", UtilMisc.toList(new EntityExpr("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, yearBegin)));
		List yearOrder = EntityUtil.filterByAnd(yearList, UtilMisc.toMap("statusId", "ORDER_ORDERED"));
		List yearApprove = EntityUtil.filterByAnd(yearList, UtilMisc.toMap("statusId", "ORDER_APPROVED"));
		List yearComplete = EntityUtil.filterByAnd(yearList, UtilMisc.toMap("statusId", "ORDER_COMPLETED"));				

%>
<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='70%' >
            <div class='boxhead'>&nbsp;Order Manager Main Page</div>
          </TD>
          <TD align=right width='30%'>            
              <FORM name="lookup" action="<ofbiz:url>/orderview</ofbiz:url>" method="GET">
                  <input type="text" name="order_id" size="9" style="font-size: x-small;">&nbsp;
                  <a href="javascript:document.lookup.submit();" class="lightbuttontext">[Lookup Order]</a>
              </FORM>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <ul>
              <li><div class="head2">Current Order Statistics</div>
              <br><ul>
                           
              <li><div class="head1">Today</div></li>
              <table>
                <tr>
                  <td align="right"><div class="tabletext"><b>Ordered:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=dayOrder == null ? 0 : dayOrder.size()%></div>
                </tr>
                <tr>
                  <td align="right"><div class="tabletext"><b>Approved:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=dayApprove == null ? 0 : dayApprove.size()%></div>
                </tr>
                <tr>
                  <td align="right"><div class="tabletext"><b>Completed:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=dayComplete == null ? 0 : dayComplete.size()%></div>
                </tr> 
                 <tr>
                  <td colspan="2">&nbsp;</td>
                </tr> 
              </table>
              <li><div class="head1">Week To Date</div></li>
              <table>
                <tr>
                  <td align="right"><div class="tabletext"><b>Ordered:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=weekOrder == null ? 0 : weekOrder.size()%></div>
                </tr>
                <tr>
                  <td align="right"><div class="tabletext"><b>Approved:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=weekApprove == null ? 0 : weekApprove.size()%></div>
                </tr>
                <tr>
                  <td align="right"><div class="tabletext"><b>Completed:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=weekComplete == null ? 0 : weekComplete.size()%></div>
                </tr>  
                 <tr>
                  <td colspan="2">&nbsp;</td>
                </tr>
              </table>              
              <li><div class="head1">Month To Date</div></li>
              <table>
                <tr>
                  <td align="right"><div class="tabletext"><b>Ordered:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=monthOrder == null ? 0 : monthOrder.size()%></div>
                </tr>
                <tr>
                  <td align="right"><div class="tabletext"><b>Approved:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=monthApprove == null ? 0 : monthApprove.size()%></div>
                </tr>
                <tr>
                  <td align="right"><div class="tabletext"><b>Completed:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=monthComplete == null ? 0 : monthComplete.size()%></div>
                </tr>  
                 <tr>
                  <td colspan="2">&nbsp;</td>
                </tr>
              </table>
              <li><div class="head1">Year To Date</div></li>
              <table>
                <tr>
                  <td align="right"><div class="tabletext"><b>Ordered:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=yearOrder == null ? 0 : yearOrder.size()%></div>
                </tr>
                <tr>
                  <td align="right"><div class="tabletext"><b>Approved:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=yearApprove == null ? 0 : yearApprove.size()%></div>
                </tr>
                <tr>
                  <td align="right"><div class="tabletext"><b>Completed:</b></div>
                  <td>&nbsp;</td>
                  <td><div class="tabletext"><%=yearComplete == null ? 0 : yearComplete.size()%></div>
                </tr>  
                <tr>
                  <td colspan="2">&nbsp;</td>
                </tr>
              </table>                                                                                                                                     
            </ul></ul>                        
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
