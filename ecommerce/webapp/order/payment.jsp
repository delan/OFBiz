<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>

<%@ page import="java.util.*" %>

<%@ page import="org.ofbiz.core.entity.*" %>
 


 
 

<% pageContext.setAttribute("PageName", "payment"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%
  Customer customer = (Customer)session.getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);
  boolean hasUserPermission = Security.hasPermission(Security.USER_ADMIN, session);
  if(hasUserPermission)
  {
    Customer tempCustomer = (Customer)session.getAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER);
    if(tempCustomer != null) customer = tempCustomer;
  }

  Iterator paymentIterator = null;
  if(CommonConstants.CREDITCARD_MANAGE_INFO)
  {
    Collection paymentCollection = CustomerPaymentHelper.findByCustomerId(customer.getCustomerId());
    if(paymentCollection != null)	paymentIterator = paymentCollection.iterator();
  }

  boolean isLynx = UtilFormatOut.isLynx(request);
%>

<p class="head1">Payment</p>
<br>
  <form method="POST" action="checkout.jsp" name="poform">
  <input type="hidden" name="<%="event"%>" value="<%=EventConstants.UPDATE_ORDER_USE_PURCHASE_ORDER%>">
    To pay with store credit, enter your Purchase Order (PO) number here:
    <br>
    <input type="text" name="<%=HttpRequestConstants.CUSTOMER_PO_NUMBER%>" size="20">
    <a href="<%=response.encodeURL(controlPath + "/javascript:document.poform.submit()" class="buttonlink">[Pay with Store Credit]</a>
  </form>
<br>
&nbsp;<a href="<%=response.encodeURL(controlPath + "/selectaddress.jsp" class="buttonlink">[Back]</a>
<a href="<%=response.encodeURL(controlPath + "/../user/profilenewcc.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/payment.jsp" class="buttonlink">[Add Credit Card]</a>

<% if(paymentIterator != null) { %>
<table width="100%" cellpadding="1" cellspacing="0" border="0">
  <tr>
    <td colspan="3"><hr size="1"></td>
  </tr>
<%
    while(paymentIterator.hasNext())
    {
      CustomerPayment customerPayment = (CustomerPayment)paymentIterator.next();
%>
  <tr>
    <td width="50%">
      <div class="tabletext">
        <%=customerPayment.getCardType()%>
        <%if(customerPayment.getCardNumber() != null && customerPayment.getCardNumber().length() > 4) {%>
          <%=customerPayment.getCardNumber().substring(customerPayment.getCardNumber().length()-4)%>
        <% } %>
        <%=customerPayment.getExpireDate()%>
      </div>
    </td>
    <td width="30%" align="right">
      <a href="<%=response.encodeURL(controlPath + "/../user/profileeditcc.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/payment.jsp&<%=HttpRequestConstants.CUSTOMER_PAYMENT_ID%>=<%=customerPayment.getPaymentId()%>" class="buttonlink">[Edit Card]</a>
    </td>
    <td width="20%" align="right">
      <a href="<%=response.encodeURL(controlPath + "/checkout.jsp?<%="event"%>=<%=EventConstants.UPDATE_ORDER_USE_CREDIT_CARD%>&<%=HttpRequestConstants.CUSTOMER_PAYMENT_ID%>=<%=customerPayment.getPaymentId()%>" class="buttonlink">[Use Card]</a>
    </td>
  </tr>
  <tr>
    <td colspan="3" height="1" bgcolor="888888"></td>
  </tr>
<%  } %>
</table>
<% } else { %>
<h3>There are no credit cards on file.</h3>
<% } //end if paymentIterator %>

<%-- the back and add new credit card buttons --%>

&nbsp;<a href="<%=response.encodeURL(controlPath + "/selectaddress.jsp" class="buttonlink">[Back]</a>
<a href="<%=response.encodeURL(controlPath + "/../user/profilenewcc.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/payment.jsp" class="buttonlink">[Add Credit Card]</a>

<%@ include file="/includes/footer.jsp" %>












