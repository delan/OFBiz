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

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("ORDERMGR", "_UPDATE", session)) {%>

<%
	String orderId = request.getParameter("order_id");
    List pmtFields = UtilMisc.toList(new EntityExpr("paymentMethodTypeId", EntityOperator.NOT_EQUAL, "EXT_OFFLINE"));
    List paymentMethodTypes = delegator.findByAnd("PaymentMethodType", pmtFields);
    if (paymentMethodTypes != null) pageContext.setAttribute("paymentMethodTypes", paymentMethodTypes);	
    
    String workEffortId = request.getParameter("workEffortId");
    String partyId = request.getParameter("partyId");
    String roleTypeId = request.getParameter("roleTypeId");
    String fromDate = request.getParameter("fromDate");
    
    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) 
    	donePage="orderview?order_id=" + orderId;
    	if (workEffortId != null && workEffortId.length() > 0)
    		donePage = donePage + "&workEffortId=" + workEffortId;
        if (partyId != null && partyId.length() > 0)
        	donePage = donePage + "&partyId=" + partyId;
        if (roleTypeId != null && roleTypeId.length() > 0)
        	donePage = donePage + "&roleTypeId=" + roleTypeId;
        if (fromDate != null && fromDate.length() > 0)
        	donePage = donePage + "&fromDate=" + fromDate;    
%>

  <br>
  <p class="head1">Receive Offline Payment(s)</p>

  &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
  &nbsp;<a href="javascript:document.paysetupform.submit()" class="buttontext">[Save]</a>

  <form method="post" action="<ofbiz:url>/receiveOfflinePayments/<%=donePage%></ofbiz:url>" name="paysetupform">    
    <input type="hidden" name="orderId" value="<%=orderId%>">
    <input type="hidden" name="workEffortId" value="<%=workEffortId%>">                                  
    <table width="100%" cellpadding="1" cellspacing="0" border="0">
      <tr>
        <td width="30%" align="right"><div class="tableheadtext"><u>Payment Type</u></div></td>
        <td width="10">&nbsp;&nbsp;</td>
        <td width="70%" align="left"><div class="tableheadtext"><u>Amount</u></div></td>
      </tr>    
      <ofbiz:iterator name="payType" property="paymentMethodTypes">      
      <tr>
        <td width="30%" align="right"><div class="tabletext"><%=UtilFormatOut.checkNull(payType.getString("description"))%></div></td>
        <td width="10">&nbsp;</td>
        <td width="70%"><input type="text" size="12" name="<%=payType.getString("paymentMethodTypeId")%>" style="font-size: x-small;"></td>
      </tr>
      </ofbiz:iterator>
    </table>
  </form>
  
  &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
  &nbsp;<a href="javascript:document.paysetupform.submit()" class="buttontext">[Save]</a>
   
<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("ORDERMGR_CREATE" or "ORDERMGR_ADMIN" needed)</h3>
<%}%>