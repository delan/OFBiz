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
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>

<% pageContext.setAttribute("PageName", "Main Page"); %> 

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<BR>
<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
  <FORM name="lookup" action="<ofbiz:url>/orderview</ofbiz:url>" method="GET">
    <TD width='100%'>    
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <TD align=left width='70%' >
            <div class='boxhead'>&nbsp;Order Manager Main Page</div>
          </TD>
          <TD align=right width='30%'>            
              <input type="text" name="order_id" size="9">&nbsp;
              <a href="javascript:document.lookup.submit();" class="lightbuttontext">[LookUp Order]</a>            
          </TD>
        </tr>
      </table>
    </TD>
    </FORM>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
<%if(userLogin == null) {%>
<DIV class='tabletext'>For something interesting make sure you are logged in, try username:admin, password:ofbiz.</DIV>
<BR>
<%}%>

<DIV class='tabletext'>This application is primarily intended for those repsonsible for the maintenance of Order Manager related information.</DIV>
<br>
<div class="tabletext"><b>New Orders:</b></div>
<!-- Insert in here -->
<center>
  <table width="95%" border="0" class="edittable">
    <tr>
      <td>
        
        <%
          //Collection orderRoleCollection = delegator.findByAnd("OrderRole",
          //   UtilMisc.toMap("partyId", "%", "roleTypeId", "PLACING_CUSTOMER"), null);
          //Collection orderHeaderList = EntityUtil.orderBy(EntityUtil.getRelated("OrderHeader", orderRoleCollection), UtilMisc.toList("orderDate DESC"));
          Collection orderHeaderList = delegator.findAll("OrderHeader");
          pageContext.setAttribute("orderHeaderList", orderHeaderList);
        %>
        
        <table width="100%" cellpadding="3" cellspacing="0" border="0">
          <tr class="viewOneTR1">
            <td width="25%">
              <div class="tabletext"><b>Date</b></div>
            </td>
            <td width="15%">
              <div class="tabletext"><b><nobr>Order #</nobr></b></div>
            </td>
            <td width="25%">
              <div class="tabletext"><b>Amount</b></div>
            </td>
            <td width="25%">
              <div class="tabletext"><b>Status</b></div>
            </td>   
            <td width="10%"><div class="tabletext">&nbsp;</div></td>                              
          </tr>
          <%String rowClass = "viewManyTR2";%>
          <ofbiz:iterator name="orderHeader" property="orderHeaderList">
          
	        <%OrderReadHelper order = new OrderReadHelper(orderHeader); %>
	        <%pageContext.setAttribute("totalPrice", new Double(order.getTotalPrice()));%>
	        <%pageContext.setAttribute("orderStatus", order.getStatusString());%>          
            <%rowClass = rowClass.equals("viewManyTR2") ? "viewManyTR1" : "viewManyTR2";%> 
                   
          <tr class="<%=rowClass%>">
            <td>
              <div class="tabletext"><nobr><ofbiz:entityfield attribute="orderHeader" field="orderDate"/></nobr></div>
            </td>
            <td>
              <div class="tabletext"><ofbiz:entityfield attribute="orderHeader" field="orderId"/></div>
            </td>            
            <td>
              <div class="tabletext"><ofbiz:field attribute="totalPrice" type="currency"/></div>
            </td>
            <td>
              <div class="tabletext"><ofbiz:entityfield attribute="orderHeader" field="statusId"/></div>
            </td>
            <td align=right>
              <a href="<ofbiz:url>/orderview?order_id=<ofbiz:entityfield attribute="orderHeader" field="orderId"/></ofbiz:url>" class='buttontext'>[View]</a>
            </td>
          </tr>
          </ofbiz:iterator>
          <ofbiz:unless name="orderHeaderList" size="0">
          <tr><td colspan="8"><div class='head3'>No Orders Found</div></td></tr>
          </ofbiz:unless>
        </table>
                
      </td>
    </tr>
  </table>
</center>
<!-- Between here -->
<br>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
