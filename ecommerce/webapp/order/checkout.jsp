<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>

<%@ page import="java.util.*" %>


<%@ page import="com.relmsoft.commerce.catalog.*" %>
<%@ page import="com.relmsoft.commerce.shoppingcart.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
 

 
 

<% pageContext.setAttribute("PageName", "checkout"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<p class="head1">Final Checkout Review</p>
<p>NOTE: This is a DEMO store-front.  Orders placed here will NOT be billed, and will NOT be fulfilled.</p>

<%  //Handle the parameters from shipping.jsp
  String shippingMethod = (String)session.getAttribute(HttpSessionConstants.SHIPPING_METHOD);
  String shippingAccount = (String)session.getAttribute(HttpSessionConstants.SHIPPING_ACCOUNT);
  String splittingPreferenceCode = (String)session.getAttribute(HttpSessionConstants.SPLITTING_PREFERENCE_CODE);
  String specialInstructions = (String)session.getAttribute(HttpSessionConstants.SPECIAL_INSTRUCTIONS);
%>

<%  //Handle the parameters from selectaddress.jsp
  String addressIdString = UtilFormatOut.checkNull((String)session.getAttribute(HttpSessionConstants.ADDRESS_KEY));
    int addressId = 0;
    try { addressId = Integer.parseInt(addressIdString); }
    catch (java.lang.NumberFormatException nfe) { addressId = 0; }
  Address shippingAddress = AddressHelper.findByPrimaryKey(new Integer(addressId));

  String customerPaymentType = (String)session.getAttribute(HttpRequestConstants.CUSTOMER_PAYMENT_TYPE);
  String customerPoNumber = (String)session.getAttribute(HttpRequestConstants.CUSTOMER_PO_NUMBER);

  String customerPaymentId = (String)session.getAttribute(HttpRequestConstants.CUSTOMER_PAYMENT_ID);

  CustomerPayment customerPayment = null;
  if(customerPaymentId != null)
    customerPayment = CustomerPaymentHelper.findByPaymentId(Integer.valueOf(customerPaymentId));

  Address billingAddress = null;
  if(customerPayment != null)
  {
    billingAddress = AddressHelper.findByPrimaryKey(customerPayment.getBillingAddress());
  }


  boolean isLynx = UtilFormatOut.isLynx(request);
%>

<% if(shippingAddress == null) { %>
  <h3>ERROR: Shipping address must be specified, please bo back or start over.</h3>
<% } else { // shippingAddress check %>
<% if (isLynx) { %><hr><% } %>
<%
  Person person = (Person)session.getAttribute(HttpSessionConstants.LOGIN_PERSON);
  Customer customer = (Customer)session.getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);
  boolean hasUserPermission = Security.hasPermission(Security.USER_ADMIN, session);
  if(hasUserPermission)
  {
    Person tempPerson = (Person)session.getAttribute(HttpSessionConstants.ACTING_AS_PERSON);
    if(tempPerson != null) person = tempPerson;
    Customer tempCustomer = (Customer)session.getAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER);
    if(tempCustomer != null) customer = tempCustomer;
  }
%>
<% Cart shoppingCart = (Cart)session.getAttribute(HttpSessionConstants.SHOPPING_CART); %>


<table width="100%" cellpadding="3" cellspacing="0" border="0">
  <%-- row to set cell widths --%>
  <tr>
    <td width="5%"></td>
    <td width="95%"></td>
  </tr>
 <tr>
    <td colspan="2" align="left"><div class="head2">Shipping Destination</div></td>
 </tr>

 <tr>
    <td>&nbsp;</td>
    <td align="left" valign="top" nowrap>
      <div class="tabletext">
        <%=UtilFormatOut.checkNull(person.getString("firstName"))%> <%=UtilFormatOut.checkNull(person.getString("middleName"))%> <%=UtilFormatOut.checkNull(person.getString("lastName"))%><br>
        <%=UtilFormatOut.checkNull(shippingAddress.getString("address1"))%><br>
        <% if(shippingAddress.getString("address2") != null && shippingAddress.getString("address2").length() != 0) { %> <%=shippingAddress.getString("address2")%><br> <% } %>
        <%=UtilFormatOut.checkNull(shippingAddress.getString("city"))%><br>
        <% if(shippingAddress.getRelatedOne("CountryGeo").getString("name") != null && shippingAddress.getRelatedOne("CountryGeo").getString("name").length() != 0) { %> <%=shippingAddress.getRelatedOne("CountryGeo").getString("name")%> <% } %>
        <%=UtilFormatOut.checkNull(shippingAddress.getRelatedOne("StateProvinceGeo").getString("name"))%> &nbsp; <%=UtilFormatOut.checkNull(shippingAddress.getString("postalCode"))%><br>
      </div>
    </td>
  </tr>
  <tr>
<% if (isLynx) { %><hr><% } %>
    <td align="left" colspan="2"><div class="head2">Shipment Splitting Preference</div></td>
  </tr>
  <tr>
      <td>&nbsp;</td>
      <td align="left">
        <div class="tabletext"><%=splittingPreferenceCode%></div>
      </td>
  </tr>



  <tr>
    <td align="left" colspan="2"><div class="head2">Special Instructions</div></td>
  </tr>
  <tr>
      <td>&nbsp;</td>
      <td align="left">
  <div class="tabletext"><%=specialInstructions %></div>
      </td>
  </tr>

  <tr>
    <td align="left" colspan="5"><div class="head2">Shipping Method</div></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td align="left">
        <div class="tabletext">
          <%=shippingMethod%>
          <% if(shippingAccount != null && shippingAccount.length() > 0) { %>
            <br>Use Account: <%=shippingAccount%>
          <% } %>
        </div>
    </td>
  </tr>
<%
    if(customerPaymentType != null && customerPaymentType.compareTo("STORE_CREDIT") == 0)
    {
%>
      <tr>
        <td align="left" colspan="5"><div class="head2">Payment Information</div></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td align="left">
            <div class="tabletext"><b>Store Credit</b></div>
        </td>
      </tr>
<%
    }
    else if(customerPaymentType != null && customerPaymentType.compareTo("PURCHASE_ORDER") == 0)
    {
%>
      <tr>
        <td align="left" colspan="5"><div class="head2">Payment Information</div></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td align="left">
            <div class="tabletext"><b>Purchase Order Number: <%=UtilFormatOut.checkNull(customerPoNumber)%></b></div>
        </td>
      </tr>
<%
    }
    else if(customerPaymentType != null && customerPaymentType.compareTo("CREDIT_CARD") == 0)
    {
      if(CommonConstants.CREDITCARD_MANAGE_INFO)
      {
%>
  <tr>
    <td align="left" colspan="5"><div class="head2">Payment Information</div></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td align="left" valign="top" nowrap>
      <div class="tabletext">
        <%=customerPayment.getNameOnCard()%><br>
        <%=UtilFormatOut.checkNull(billingAddress.getString("address1"))%><br>
        <% if(billingAddress.getString("address2") != null && billingAddress.getString("address2").length() != 0) { %> <%=billingAddress.getString("address2")%><br> <% } %>
        <%=UtilFormatOut.checkNull(billingAddress.getString("city"))%><br>
        <% if(billingAddress.getRelatedOne("CountryGeo").getString("name") != null && billingAddress.getRelatedOne("CountryGeo").getString("name").length() != 0) { %> <%=billingAddress.getRelatedOne("CountryGeo").getString("name")%> <% } %>
        <%=UtilFormatOut.checkNull(billingAddress.getRelatedOne("StateProvinceGeo").getString("name"))%> &nbsp; <%=UtilFormatOut.checkNull(billingAddress.getString("postalCode"))%><br>
      </div>
    </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td align="left">
        <div class="tabletext"><b><%=customerPayment.getCardType()%>: <%=customerPayment.getCardNumber().substring(customerPayment.getCardNumber().length()-4)%> <%=customerPayment.getExpireDate()%></b></div>
    </td>
  </tr>
<%
      } // end if cc manage info
    }
%>
</table>
<%-- table for shoppingcartline items --%>
<% if (isLynx == false) { %>
  <table border="0" cellpadding="1" width="100%">
    <%-- use this row to set the widths on all the columns --%>
    <tr>
      <td width="15%"><img src="../images/shim.gif" width="2" height="1"></td>
      <td width="50%"><img src="../images/shim.gif" width="2" height="1"></td>
      <td width="5%"><img src="../images/shim.gif" width="2" height="1"></td>
      <td width="15%"><img src="../images/shim.gif" width="2" height="1"></td>
      <td width="15%"><img src="../images/shim.gif" width="2" height="1"></td>
    </tr>

    <tr>
      <td colspan="5" bgcolor="#678475">
      <div class="tabletext"><font color="white"><b>Order</b></font></div>
      </td>
    </tr>

    <tr>
      <td align="left" valign="bottom"><div class="tabletext"><b>ID</b></div></td>
      <td align="left" valign="bottom"><div class="tabletext"><b>Description</b></div></td>
      <td align="center" valign="bottom"><div class="tabletext"><b>Quantity</b></div></td>
      <td align="center" valign="bottom"><div class="tabletext"><b>Our Price</b></div></td>
      <td align="center" valign="bottom"><div class="tabletext"><b>SubTotal</b></div></td>
    </tr>
    <tr>
      <td colspan="5"><hr size="1"></td>
    </tr>
<% } else { %>
  <b>ID</b>
  <b>Description</b>
  <b>Quantity</b>
  <b>Our Price</b>
  <b>SubTotal</b>
<% } %>

<%  if(shoppingCart != null && shoppingCart.size() > 0)
    {
%>

<%  for(int i=0; i<shoppingCart.size(); i++)
    {
      LineItem shoppingCartLine = shoppingCart.getLineItem(i);
%>
<% if(isLynx == false) { %>
<% if(shoppingCartLine.getSku().compareTo("shoppingcart.CommentLine") == 0) { %>
    <tr>
      <td valign="middle" colspan="5">
        <b><div class="tabletext"> >> <%=shoppingCartLine.getString("description")%></div></b>
      </td>
    </tr>

<% } else { %>
    <tr>
      <td nowrap valign="top">
        <div class="tabletext"><%=shoppingCartLine.getSku()%></div>
      </td>
      <td valign="top">
        <div class="tabletext"><%=shoppingCartLine.getString("description")%>
        <% //Get the RandomLengthString, if one exists...
           String RLString = null;
        %>
        <% if(shoppingCartLine.containsKey(HttpRequestConstants.RANDOM_LENGTH_STRING))
          {
            RLString = shoppingCartLine.getString(HttpRequestConstants.RANDOM_LENGTH_STRING);
            if(RLString != null)
            {
        %>
              [[RandomLength:<%=(String)RLString%>]]
        <%
            }
          }
        %>
        </div>
      </td>
      <td align="center" valign="top">
        <div class="tabletext"><%=UtilFormatOut.formatQuantity(shoppingCartLine.getDouble("quantity"))%></div>
      </td>
      <td align="right" nowrap valign="top">
        <div class="tabletext"><%-- shoppingCartLine.getUnitPrice().getCurrency() --%>
        <%=UtilFormatOut.formatPrice(shoppingCartLine.getPrice(customer))%></div>
      </td>
      <td align="right" nowrap valign="top">
        <div class="tabletext"><%-- lineTotal.getCurrency() --%>
        <%=UtilFormatOut.formatPrice(shoppingCartLine.priceTotal(customer))%></div>
      </td>
    </tr>
<% } %>
    <tr>
      <td colspan="5" height="1"><hr size="1"></td>
    </tr>
<% } else { %>
   <%=shoppingCartLine.getSku()%>
   <%=shoppingCartLine.getString("description")%>
        <% //Get the RandomLengthString, if one exists...
           String RLString = null;
        %>
        <% if(shoppingCartLine.containsKey(HttpRequestConstants.RANDOM_LENGTH_STRING))
  {
               RLString = shoppingCartLine.getString(HttpRequestConstants.RANDOM_LENGTH_STRING);
          if(RLString != null)
    { %> [[RandomLength:<%= (String)RLString %>]]
      <%  }
  } %>
    <%=UtilFormatOut.formatQuantity(shoppingCartLine.getDouble("quantity"))%>
    <%-- shoppingCartLine.getUnitPrice().getCurrency() --%>
    <%=UtilFormatOut.formatPrice(shoppingCartLine.getPrice(customer))%>
    <%-- lineTotal.getCurrency() --%>
    <%=UtilFormatOut.formatPrice(shoppingCartLine.priceTotal(customer))%><br>
<% } // end Lynx %>
  <% } //end for loop%>

<% if (isLynx == false) { %>
    <tr>
      <td colspan="4" align="right"><div class="tabletext">Shipping & handling</div></td>
      <td align="right" nowrap>
  <div class="tabletext"><%-- shipping.getCurrency() --%>
  <%="0.00"%></div>
      </td>
    </tr>

    <tr>
      <td colspan="4" align="right"><div class="tabletext">Total tax</div></td>
      <td align="right" nowrap>
  <div class="tabletext"><%-- tax.getCurrency() --%>
  <%="0.00"%></div>
      </td>
    </tr>

    <tr>
      <td colspan="4" align="right"><div class="tabletext"><b>Total due</b></div></td>
     <td align="right" bgcolor="#99BBAA" nowrap>
  <div class="tabletext"><b><%-- total.getCurrency() --%>
  <%=UtilFormatOut.formatPrice(shoppingCart.priceTotal(customer))%></b></div>
      </td>
    </tr>
    <tr>
      <td colspan="5"><hr size="1"></td>
    </tr>
   <tr>
      <td colspan="4" align="left">
      <%
        String prevPage = "selectaddress.jsp";
        if(CommonConstants.CREDITCARD_MANAGE_INFO) prevPage = "allcheckoutoptions.jsp";
      %>
      <a href="<%=prevPage%>" class="buttonlinkbig">&nbsp;[Back]</a></td>
      <td align="right">
        <a href="<%=response.encodeURL(controlPath + "/confirmorder.jsp?<%="event"%>=<%=EventConstants.CREATE_ORDER%>" class="buttonlinkbig">[Submit&nbsp;Order]&nbsp;</a>
      </td>
    </tr>
  </table>
<% } else { %>
  <hr>
  Shipping & handling: <%-- shipping.getCurrency() --%><%="0.00"%><br>
  Total tax: <%-- tax.getCurrency() --%><%="0.00"%><br>
  <b>Total due:</b><%-- total.getCurrency() --%><%=UtilFormatOut.formatPrice(shoppingCart.priceTotal(customer))%><br>

<%
  String prevPage = "selectaddress.jsp";
  if(CommonConstants.CREDITCARD_MANAGE_INFO) prevPage = "allcheckoutoptions.jsp";
%>
  &nbsp;<a href="<%=prevPage%>" class="buttonlink">[Back]</a></td>
  <a href="<%=response.encodeURL(controlPath + "/confirmorder.jsp?<%="event"%>=<%=EventConstants.CREATE_ORDER%>" class="buttonlink">[Submit Order]</a>
<% } //end if isLynx %>
<% } else { %>
<h3>ERROR: Shopping cart empty, please start over.</h3>
<% } //end if shopping cart %>

<% } // shippingAddress check %>

<%@ include file="/includes/footer.jsp" %>


