<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>

<%@ page import="java.util.*" %>


<%@ page import="org.ofbiz.core.entity.*" %>
 

 
 

<% pageContext.setAttribute("PageName", "selectaddress"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

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

  //Customer's shipping addresses list
  Iterator custShipAddressesIterator = CustomerShipAddressHelper.findByCustomerIdIterator(customer.getCustomerId());
%>

    <p class="head1">Select Shipping Address</p>

  <%
  if (custShipAddressesIterator != null) {
    while(custShipAddressesIterator.hasNext())
    {
      CustomerShipAddress customerShipAddress = (CustomerShipAddress)custShipAddressesIterator.next();
      Address shippingAddress = AddressHelper.findByPrimaryKey(customerShipAddress.getAddressId());
  %>

  <table width="100%" border="0" cellpadding="6" cellspacing="0">
    <tr>
      <td align="left" valign="top" width="30%" nowrap>
  <%=UtilFormatOut.checkNull(shippingAddress.getString("address1"))%><br>
  <% if(shippingAddress.getString("address2") != null && shippingAddress.getString("address2").length() != 0) { %> <%=shippingAddress.getString("address2")%><br> <% } %>
  <%=UtilFormatOut.checkNull(shippingAddress.getString("city"))%><br>
  <% if(shippingAddress.getRelatedOne("CountryGeo").getString("name") != null && shippingAddress.getRelatedOne("CountryGeo").getString("name").length() != 0) { %> <%=shippingAddress.getRelatedOne("CountryGeo").getString("name")%> <% } %>
  <%=UtilFormatOut.checkNull(shippingAddress.getRelatedOne("StateProvinceGeo").getString("name"))%> &nbsp; <%=UtilFormatOut.checkNull(shippingAddress.getString("postalCode"))%><br>
      </td>
      <td align="left" valign="top" width="5%" >
        <div class="commentary">
          <a href="<%=response.encodeURL(controlPath + "/../user/profileeditaddress.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/selectaddress.jsp&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=shippingAddress.getAddressId()%>" class="buttonlink">[Update]</a>
        </div>
      </td>
    <%if(shippingAddress.getAddressId().intValue() != customer.getAddressId().intValue()) { %>
      <td align="left" valign="top" width="5%" >
        <div class="commentary">
          <a href="<%=response.encodeURL(controlPath + "/selectaddress.jsp?<%="event"%>=<%=EventConstants.DELETE_SHIPPING_ADDRESS%>&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=shippingAddress.getAddressId()%>" class="buttonlink">[Delete]</a>
        </div>
      </td>
    <% } else { %>
      <td align="left" valign="top" width="5%" >
        <div class="commentary">
        </div>
      </td>
    <% } %>
      <td align="left" valign="top" width="5%" >
        <div class="commentary">
          <%
            String nextPage = "checkout.jsp";
            if(CommonConstants.CREDITCARD_MANAGE_INFO) nextPage = "payment.jsp";
          %>
          <a href="<%=nextPage%>?<%="event"%>=<%=EventConstants.UPDATE_ORDER_USE_ADDRESS%>&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=shippingAddress.getAddressId()%>" class="buttonlink">[Use This Address]</a>
        </div>
      </td>
    </tr>
    <tr>
      <td colspan="4" height="1" bgcolor="888888"></td>
    </tr>
  </table>
  <%
    }
  }
  %>
    &nbsp;<a href="<%=response.encodeURL(controlPath + "/shipping.jsp" class="buttonlink">[Back]</a>
    <a href="<%=response.encodeURL(controlPath + "/../user/profilenewaddress.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/selectaddress.jsp" class="buttonlink">[Add New Address]</a>
  <br>

<%@ include file="/includes/footer.jsp" %>



