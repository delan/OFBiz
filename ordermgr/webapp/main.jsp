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

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*, java.sql.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*, org.ofbiz.commonapp.party.contact.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<% if(security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)) { %>

<%!
	public static double calcItemTotal(List items) {
		double total = 0.00;
		Iterator i = items.iterator();
		while (i.hasNext()) {
			GenericValue item = (GenericValue) i.next();
			total += (item.getDouble("quantity").doubleValue() * item.getDouble("unitPrice").doubleValue());
		}
		return total;
	}
%>

<% 
	Timestamp endTime = UtilDateTime.nowTimestamp();
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.AM_PM, Calendar.AM);
	cal.set(Calendar.HOUR, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);	
	cal.set(Calendar.MILLISECOND, 0);	
	Timestamp dayBegin = new Timestamp(cal.getTime().getTime());			
	
	cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	Timestamp weekBegin = new Timestamp(cal.getTime().getTime());		
	
	cal.set(Calendar.DAY_OF_MONTH, 1);
	Timestamp monthBegin = new Timestamp(cal.getTime().getTime());		
	
	cal.set(Calendar.MONTH, 1);
	Timestamp yearBegin = new Timestamp(cal.getTime().getTime());		
	
	// order status report
	List dayList = delegator.findByAnd("OrderStatus", UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null), new EntityExpr("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
	List dayOrder = EntityUtil.filterByAnd(dayList, UtilMisc.toMap("statusId", "ORDER_ORDERED"));
	List dayApprove = EntityUtil.filterByAnd(dayList, UtilMisc.toMap("statusId", "ORDER_APPROVED"));
	List dayComplete = EntityUtil.filterByAnd(dayList, UtilMisc.toMap("statusId", "ORDER_COMPLETED"));
	List dayCancelled = EntityUtil.filterByAnd(dayList, UtilMisc.toMap("statusId", "ORDER_CANCELLED"));
	List dayRejected = EntityUtil.filterByAnd(dayList, UtilMisc.toMap("statusId", "ORDER_REJECTED"));
	
	List weekList = delegator.findByAnd("OrderStatus", UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null), new EntityExpr("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, weekBegin)));
	List weekOrder = EntityUtil.filterByAnd(weekList, UtilMisc.toMap("statusId", "ORDER_ORDERED"));
	List weekApprove = EntityUtil.filterByAnd(weekList, UtilMisc.toMap("statusId", "ORDER_APPROVED"));
	List weekComplete = EntityUtil.filterByAnd(weekList, UtilMisc.toMap("statusId", "ORDER_COMPLETED"));
	List weekCancelled = EntityUtil.filterByAnd(weekList, UtilMisc.toMap("statusId", "ORDER_CANCELLED"));
	List weekRejected = EntityUtil.filterByAnd(weekList, UtilMisc.toMap("statusId", "ORDER_REJECTED"));
	
	List monthList = delegator.findByAnd("OrderStatus", UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null), new EntityExpr("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin)));
	List monthOrder = EntityUtil.filterByAnd(monthList, UtilMisc.toMap("statusId", "ORDER_ORDERED"));
	List monthApprove = EntityUtil.filterByAnd(monthList, UtilMisc.toMap("statusId", "ORDER_APPROVED"));
	List monthComplete = EntityUtil.filterByAnd(monthList, UtilMisc.toMap("statusId", "ORDER_COMPLETED"));
	List monthCancelled = EntityUtil.filterByAnd(monthList, UtilMisc.toMap("statusId", "ORDER_CANCELLED"));
	List monthRejected = EntityUtil.filterByAnd(monthList, UtilMisc.toMap("statusId", "ORDER_REJECTED"));
	
	List yearList = delegator.findByAnd("OrderStatus", UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null), new EntityExpr("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO, yearBegin)));
	List yearOrder = EntityUtil.filterByAnd(yearList, UtilMisc.toMap("statusId", "ORDER_ORDERED"));
	List yearApprove = EntityUtil.filterByAnd(yearList, UtilMisc.toMap("statusId", "ORDER_APPROVED"));
	List yearComplete = EntityUtil.filterByAnd(yearList, UtilMisc.toMap("statusId", "ORDER_COMPLETED"));
	List yearCancelled = EntityUtil.filterByAnd(yearList, UtilMisc.toMap("statusId", "ORDER_CANCELLED"));
	List yearRejected = EntityUtil.filterByAnd(yearList, UtilMisc.toMap("statusId", "ORDER_REJECTED"));
	
	// order totals
	List dayItems = delegator.findByAnd("OrderHeaderAndItems", UtilMisc.toList(new EntityExpr("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
	double dayItemTotal = calcItemTotal(dayItems);
	List weekItems = delegator.findByAnd("OrderHeaderAndItems", UtilMisc.toList(new EntityExpr("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, weekBegin)));
	double weekItemTotal = calcItemTotal(weekItems);
	List monthItems = delegator.findByAnd("OrderHeaderAndItems", UtilMisc.toList(new EntityExpr("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin)));
	double monthItemTotal = calcItemTotal(monthItems);
	List yearItems = delegator.findByAnd("OrderHeaderAndItems", UtilMisc.toList(new EntityExpr("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, yearBegin)));
	double yearItemTotal = calcItemTotal(yearItems);
	
	// order state report
	List waitingPayment = delegator.findByAnd("OrderHeader", UtilMisc.toMap("statusId", "ORDER_ORDERED"));
	List waitingApproval = delegator.findByAnd("OrderHeader", UtilMisc.toMap("statusId", "ORDER_PROCESSING"));
	List waitingComplete = delegator.findByAnd("OrderHeader", UtilMisc.toMap("statusId", "ORDER_APPROVED"));
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
          
            <div class='head3'>Order Statistics</div>
            <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
              <TR>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td><div class="tableheadtext">Today</div></td>
                <td><div class="tableheadtext">WTD</div></td>
                <TD><div class="tableheadtext">MTD</div></td>
                <TD><div class="tableheadtext">YTD</div></td>              
              </TR>
              
              <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
              <tr>
                <td colspan="7">
                  <div class="tableheadtext">Orders Totals</div>
                </td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Dollar Amounts (before adjustments)</div></td>
                <td><div class="tabletext"><ofbiz:format type="C"><%=dayItemTotal%></ofbiz:format></div></td>
                <td><div class="tabletext"><ofbiz:format type="C"><%=weekItemTotal%></ofbiz:format></div></td>
                <td><div class="tabletext"><ofbiz:format type="C"><%=monthItemTotal%></ofbiz:format></div></td>
                <td><div class="tabletext"><ofbiz:format type="C"><%=yearItemTotal%></ofbiz:format></div></td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Items Sold (includes promotions)</div></td>
                <td><div class="tabletext"><%=dayItems == null ? 0 : dayItems.size()%></div></td>
                <td><div class="tabletext"><%=weekItems == null ? 0 : weekItems.size()%></div></td>
                <td><div class="tabletext"><%=monthItems == null ? 0 : monthItems.size()%></div></td>
                <td><div class="tabletext"><%=yearItems == null ? 0 : yearItems.size()%></div></td>
              </tr>
              
              <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
              <tr>
                <td colspan="7">
                  <div class="tableheadtext">Orders Pending</div>
                </td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Waiting Payment</div></td>
                <td><div class="tabletext"><%=waitingPayment == null ? 0 : waitingPayment.size()%></div></td>
                <td><div class="tabletext">--</div></td>
                <td><div class="tabletext">--</div></td>
                <td><div class="tabletext">--</div></td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Waiting Approval</div></td>
                <td><div class="tabletext"><%=waitingApproval == null ? 0 : waitingApproval.size()%></div></td>
                <td><div class="tabletext">--</div></td>
                <td><div class="tabletext">--</div></td>
                <td><div class="tabletext">--</div></td>
              </tr> 
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Waiting Completion</div></td>
                <td><div class="tabletext"><%=waitingComplete == null ? 0 : waitingComplete.size()%></div></td>
                <td><div class="tabletext">--</div></td>
                <td><div class="tabletext">--</div></td>
                <td><div class="tabletext">--</div></td>
              </tr>                     
                                      
              <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
              <tr>
                <td colspan="7">
                  <div class="tableheadtext">Status Changes</div>
                </td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Ordered</div></td>
                <td><div class="tabletext"><%=dayOrder == null ? 0 : dayOrder.size()%></div></td>
                <td><div class="tabletext"><%=weekOrder == null ? 0 : weekOrder.size()%></div></td>
                <td><div class="tabletext"><%=monthOrder == null ? 0 : monthOrder.size()%></div></td>
                <td><div class="tabletext"><%=yearOrder == null ? 0 : yearOrder.size()%></div></td>
              </tr>
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Approved</div></td>
                <td><div class="tabletext"><%=dayApprove == null ? 0 : dayApprove.size()%></div></td>
                <td><div class="tabletext"><%=weekApprove == null ? 0 : weekApprove.size()%></div></td>
                <td><div class="tabletext"><%=monthApprove == null ? 0 : monthApprove.size()%></div></td>
                <td><div class="tabletext"><%=yearApprove == null ? 0 : yearApprove.size()%></div></td>
              </tr>              
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Completed</div></td>
                <td><div class="tabletext"><%=dayComplete == null ? 0 : dayComplete.size()%></div></td>
                <td><div class="tabletext"><%=weekComplete == null ? 0 : weekComplete.size()%></div></td>
                <td><div class="tabletext"><%=monthComplete == null ? 0 : monthComplete.size()%></div></td>
                <td><div class="tabletext"><%=yearComplete == null ? 0 : yearComplete.size()%></div></td>
              </tr>      
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Cancelled</div></td>
                <td><div class="tabletext"><%=dayCancelled == null ? 0 : dayCancelled.size()%></div></td>
                <td><div class="tabletext"><%=weekCancelled == null ? 0 : weekCancelled.size()%></div></td>
                <td><div class="tabletext"><%=monthCancelled == null ? 0 : monthCancelled.size()%></div></td>
                <td><div class="tabletext"><%=yearCancelled == null ? 0 : yearCancelled.size()%></div></td>
              </tr>  
              <tr>
                <td>&nbsp;</td>
                <td><div class="tabletext">Rejected</div></td>
                <td><div class="tabletext"><%=dayRejected == null ? 0 : dayRejected.size()%></div></td>
                <td><div class="tabletext"><%=weekRejected == null ? 0 : weekRejected.size()%></div></td>
                <td><div class="tabletext"><%=monthRejected == null ? 0 : monthRejected.size()%></div></td>
                <td><div class="tabletext"><%=yearRejected == null ? 0 : yearRejected.size()%></div></td>
              </tr>                                         
                    
            </TABLE>                    
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>
<%}else{%>
  <h3>You do not have permission to view this page.</h3>
<%}%>
