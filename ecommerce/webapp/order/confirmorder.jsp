<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>

<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

 

<%@ page import="org.ofbiz.core.entity.*" %>
 

 
 

<%@ page import="org.ofbiz.ecommerce.utils.SendMailSMTP" %>
<%@ page import="javax.servlet.jsp.tagext.BodyContent" %>


<%
  GenericValue orderHeader = (GenericValue)session.getAttribute(HttpSessionConstants.SALES_TRANSACTION);
  Collection orderHeaderCollection = (Collection)session.getAttribute(HttpSessionConstants.SALES_TRANSACTION_LINES);
  Iterator orderHeaderLines = null;
  if(orderHeaderCollection!=null) orderHeaderLines = orderHeaderCollection.iterator();

  Person person = (Person)session.getAttribute(HttpSessionConstants.LOGIN_PERSON);
  Customer customer = (Customer)session.getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);

  boolean hasPermission = Security.hasPermission(Security.USER_ADMIN, session);
  if(hasPermission)
  {
    Person tempPerson = (Person)session.getAttribute(HttpSessionConstants.ACTING_AS_PERSON);
    if(tempPerson != null) person = tempPerson;
    Customer tempCustomer = (Customer)session.getAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER);
    if(tempCustomer != null) customer = tempCustomer;
  }

  Address shippingAddress = null;
  if(orderHeader!=null) shippingAddress = AddressHelper.findByPrimaryKey(orderHeader.getShippingAddr());

  Integer paymentAddressId = null;
  if(orderHeader!=null) paymentAddressId = orderHeader.getPaymentAddress();
  Address billingAddress = null;
  if(paymentAddressId != null && paymentAddressId.intValue() > 0)
  {
    billingAddress = AddressHelper.findByPrimaryKey(paymentAddressId);
  }

  String myServerProtocol = request.getScheme();
  String myServerName = request.getServerName().toString();
  int myServerPort = request.getServerPort();

  String companyName = CommonConstants.COMPANY_NAME;
%>

<% pageContext.setAttribute("PageName", "confirmorder"); %>

<%
StringBuffer outString = new StringBuffer();

  outString.append("<html>\r\n<head>\r\n<title>");
  outString.append(companyName);
  outString.append(" online! - ");
  outString.append( pageContext.getAttribute("AboutFileName") );
  outString.append("</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\r\n\r\n");
  outString.append("<style>\r\n\tp {\r\n\t\tMARGIN: 0.2em;\r\n\t\tFONT-FAMILY: Helvetica,sans-serif;\r\n\t\tFONT-SIZE: 10pt;\r\n\t}\r\n\t.head1 {\r\n\t\tFONT-FAMILY: Helvetica,sans-serif;\r\n\t\tMARGIN: 0;\r\n\t\tFONT-SIZE: 15pt;\r\n\t\tFONT-WEIGHT: bold;\r\n\t\tCOLOR: #3A4C37;\r\n\t}\r\n\t.head2 {\r\n\t\tFONT-FAMILY: Helvetica,sans-serif;\r\n\t\tMARGIN: 0;\r\n\t\tFONT-SIZE: 12pt;\r\n\t\tFONT-WEIGHT: bold;\r\n\t\tCOLOR: #000000;\r\n\t}\r\n\t.tabletext {\r\n\t\tFONT-FAMILY: Verdana,sans-serif;\r\n\t\tFONT-SIZE: 9pt;\r\n\t}\r\n\t.commentary {\r\n\t\tFONT-FAMILY: Helvetica,sans-serif;\r\n\t\tFONT-SIZE: 8pt;\r\n\t\tFONT-WEIGHT: bold;\r\n\t}\r\n\tul {\r\n\t\tFONT-FAMILY: Helvetica,sans-serif;\r\n\t\tFONT-SIZE: 9pt;\r\n\t\t}\r\n\tol {\r\n\t\tFONT-FAMILY: Helvetica,sans-serif;\r\n\t\tFONT-SIZE: 9pt;\r\n\t}\r\n\r\n");
  outString.append("A.buttonlinkbig {\r\n\t\tFONT-FAMILY: Helvetica,sans-serif;\r\n\t\tFONT-SIZE: 14pt;\r\n\t\tFONT-WEIGHT: bold;\r\n\ttext-decoration: none;\r\n\t\tcolor: blue;\r\n        }\r\n        A.buttonlinkbig:hover { color: red; }\r\n\r\n\tA.headerlink {\r\n\t\tFONT-FAMILY: Helvetica,sans-serif;\r\n\t\tFONT-SIZE: 8pt;\r\n\t\tFONT-WEIGHT: bold;\r\n\ttext-decoration: none;\r\n\t\tcolor: blue;\r\n        }\r\n        A.headerlink:hover { color: red; }\r\n\r\n        .headertext {\r\n\t\tFONT-FAMILY: Helvetica,sans-serif;\r\n\t\tFONT-SIZE: 8pt;\r\n\t\tFONT-WEIGHT: bold;\r\n                text-decoration: none;\r\n                color: #567856;\r\n\t}\r\n</style>\r\n</head>\r\n<body bgcolor=\"white\">\r\n<a name=\"top\"></a>\r\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"610\" height=\"60\">\r\n  <tr valign=\"top\">\r\n    <td width=\"353\">\r\n ");
  outString.append("<img name=\"header_01\" src=\"" + myServerProtocol + "://");
  outString.append(myServerName);
  outString.append(":");
  outString.append(myServerPort);
  outString.append("/images/header_01.gif\" border=\"0\" usemap=\"#home_main\">\r\n ");
  outString.append("<map name=\"home_main\">\r\n    <area shape=\"rect\" coords=\"0,0,353,60\" href=\"" + myServerProtocol + "://");
  outString.append(myServerName);
  outString.append(":");
  outString.append(myServerPort);
  outString.append("/commerce\">\r\n  </map>\r\n    </td>\r\n    <td width=\"257\" bgcolor=\"white\">\r\n    </td>\r\n  </tr>\r\n</table>\r\n");
  outString.append("\r\n<table width=\"90%\" cellpadding=\"2\" cellspacing=\"0\" border=\"0\">\r\n    <tr>\r\n      <td colspan=\"2\">");
  outString.append("\r\n\t<h1><div class=\"head1\">Order Confirmation</div></h1>");
  outString.append("\r\n\t<p>NOTE: This is a DEMO store-front.  Orders placed here will NOT be billed, and will NOT be fulfilled.</p>");
  outString.append("\r\n\t<div class=\"tabletext\">Thank you for shopping at ");
  outString.append(companyName);
  outString.append(" online. Don't forget to stop back for more great deals, contests, new store openings and specials.<br></div>\r\n\t<br>\r\n      </td>\r\n    </tr>\r\n    <tr>\r\n      <td align=\"left\" colspan=\"2\"><div class=\"head2\"><b>Order &#35;");
  if(orderHeader != null)
  {
    outString.append(orderHeader.getString("orderId"));
  }
  outString.append("</b></div></td>\r\n    </tr>\r\n    <tr>\r\n    \t<td width=\"50%\" align=\"left\" valign=\"top\"><div class=\"tabletext\"><b>Will be shipped to:</b></div></td>\r\n    \t<td width=\"50%\" align=\"left\" valign=\"top\"><div class=\"tabletext\"><b>Preferences:</b></div></td>\r\n    </tr>\r\n    <tr>\r\n    \t<td align=\"left\" valign=\"top\"><div class=\"tabletext\">\r\n\t  ");
  outString.append(UtilFormatOut.checkNull(person.getString("lastName")));
  outString.append(" ");
  outString.append(UtilFormatOut.checkNull(person.getString("middleName")));
  outString.append(" ");
  outString.append(UtilFormatOut.checkNull(person.getString("lastName")));
  outString.append("<br>\r\n\t  ");
  if(shippingAddress != null)
  {
    outString.append(UtilFormatOut.checkNull(shippingAddress.getString("address1")));
    outString.append("<br>\r\n\t  ");
    if(shippingAddress.getString("address2") != null && shippingAddress.getString("address2").length() != 0)
    {
      outString.append(" ");
      outString.append(shippingAddress.getString("address2"));
      outString.append("<br> ");
    }
    outString.append("\r\n\t  ");
    outString.append(UtilFormatOut.checkNull(shippingAddress.getString("city")));
    outString.append("<br>\r\n\t  ");
    if(shippingAddress.getRelatedOne("CountryGeo").getString("name") != null && shippingAddress.getRelatedOne("CountryGeo").getString("name").length() != 0)
    {
      outString.append(" ");
      outString.append(shippingAddress.getRelatedOne("CountryGeo").getString("name"));
      outString.append(" ");
    }
    outString.append("\r\n\t  ");
    outString.append(UtilFormatOut.checkNull(shippingAddress.getRelatedOne("StateProvinceGeo").getString("name")));
    outString.append("&nbsp;");
    outString.append(UtilFormatOut.checkNull(shippingAddress.getString("postalCode")));
  }
  outString.append("<br>\r\n</div>\r\n\t");

  outString.append("<br><div class=\"tabletext\"><b>Will be billed to:</b><br>");
  if(orderHeader != null && orderHeader.getPaymentName() != null && orderHeader.getPaymentName().length() > 0) outString.append(orderHeader.getPaymentName() + "<br>");
  if(billingAddress != null)
  {
    outString.append(UtilFormatOut.checkNull(billingAddress.getString("address1")));
    outString.append("<br>\r\n\t  ");
    if(billingAddress.getString("address2") != null && billingAddress.getString("address2").length() != 0)
    {
      outString.append(" ");
      outString.append(billingAddress.getString("address2"));
      outString.append("<br> ");
    }
    outString.append("\r\n\t  ");
    outString.append(UtilFormatOut.checkNull(billingAddress.getString("city")));
    outString.append("<br>\r\n\t  ");
    if(billingAddress.getRelatedOne("CountryGeo").getString("name") != null && billingAddress.getRelatedOne("CountryGeo").getString("name").length() != 0)
    {
      outString.append(" ");
      outString.append(billingAddress.getRelatedOne("CountryGeo").getString("name"));
      outString.append(" ");
    }
    outString.append("\r\n\t  ");
    outString.append(UtilFormatOut.checkNull(billingAddress.getRelatedOne("StateProvinceGeo").getString("name")));
    outString.append("&nbsp;");
    outString.append(UtilFormatOut.checkNull(billingAddress.getString("postalCode")));
    outString.append("<br>");
  }
  if(orderHeader != null && orderHeader.getPaymentType() != null && orderHeader.getPaymentType().length() > 0) outString.append(orderHeader.getPaymentType());
  if(orderHeader != null && orderHeader.getPaymentNumber() != null)
  {
    if(orderHeader.getPaymentType() != null && (orderHeader.getPaymentType().compareTo("STORE_CREDIT") == 0 || orderHeader.getPaymentType().compareTo("PURCHASE_ORDER") == 0))
      outString.append(" " + orderHeader.getPaymentNumber());
    else if(orderHeader.getPaymentNumber().length() > 4)
      outString.append(" " + orderHeader.getPaymentNumber().substring(orderHeader.getPaymentNumber().length()-4));
  }
  if(orderHeader != null && orderHeader.getPaymentExpireDate() != null && orderHeader.getPaymentExpireDate().length() > 0) outString.append(" " + orderHeader.getPaymentExpireDate());
  outString.append("</div></td>\r\n");

  outString.append("<td align=\"left\" valign=\"top\">\r\n\t  <div class=\"tabletext\"><b>Splitting Preference</b><br>");
  if(orderHeader != null) outString.append(orderHeader.getSplittingPreference());
  outString.append("</div><br>\r\n\t  <div class=\"tabletext\"><b>Special Instructions</b><br>");
  if(orderHeader != null) outString.append(orderHeader.getSpecialInstructions());
  outString.append("</div><br>\r\n\t  <div class=\"tabletext\"><b>Shipping Method</b><br>");
  if(orderHeader != null) outString.append(orderHeader.getShippingMethod());
  if(orderHeader != null && orderHeader.getShippingAccount() != null && orderHeader.getShippingAccount().length() > 0)
  {
    outString.append("<br>Account: " + orderHeader.getShippingAccount());
  }
  outString.append("</div><br>\r\n\t</td>\r\n      </tr>\r\n");
  outString.append("<tr>\r\n\t<td colspan=\"2\" valign=\"top\">\r\n");
  outString.append("\r\n<table border=\"1\" width=\"100%\" cellpadding=\"4\" cellspacing=\"0\">\r\n    <tr bgcolor=\"#99BBAA\">\r\n      <td bgcolor=\"#99BBAA\" width=\"15%\" valign=\"bottom\"><div class=\"tabletext\"><b>ID</b></div></td>\r\n      <td bgcolor=\"#99BBAA\" width=\"55%\" valign=\"bottom\"><div class=\"tabletext\"><b>Description</b></div></td>\r\n      <td bgcolor=\"#99BBAA\" width=\"5%\" valign=\"bottom\" align=\"center\"><div class=\"tabletext\"><b>Quantity</b></div></td>\r\n      <td bgcolor=\"#99BBAA\" width=\"15%\" valign=\"bottom\" align=\"center\"><div class=\"tabletext\"><b>Unit Price</b></div></td>\r\n      <td bgcolor=\"#99BBAA\" width=\"15%\" valign=\"bottom\" align=\"center\"><div class=\"tabletext\"><b>Total</b></div></td>\r\n    </tr>\r\n\r\n");
  outString.append("\r\n");
  while(orderHeaderLines != null && orderHeaderLines.hasNext())
  {
    GenericValueLine orderHeaderLine = (GenericValueLine)orderHeaderLines.next();
    if(orderHeaderLine.getString("productId").compareTo("shoppingcart.CommentLine") == 0)
    {
      outString.append("\r\n  <tr>\r\n      <td valign=\"top\" align=\"left\" colspan=\"5\">\r\n        <div class=\"tabletext\"><b> >>");
      outString.append(orderHeaderLine.getString("itemDescription"));
      outString.append("</b></div>\r\n      </td>\r\n\r\n  </tr>\r\n");
    }
    else
    {
      outString.append("\r\n  <tr>\r\n      <td valign=\"top\" align=\"left\">\r\n        <div class=\"tabletext\">");
      outString.append(orderHeaderLine.getString("productId"));
      outString.append("</div>\r\n      </td>\r\n      <td valign=\"top\" align=\"left\">\r\n        <div class=\"tabletext\">");
      outString.append(orderHeaderLine.getString("itemDescription"));
      outString.append("</div>\r\n      </td>\r\n      <td align=\"center\" valign=\"top\">\r\n        <div class=\"tabletext\">");
      outString.append(UtilFormatOut.formatQuantity(orderHeaderLine.getDouble("quantity")));
      outString.append("</div>\r\n      </td>\r\n      <td align=\"right\" valign=\"top\" nowrap>\r\n        <div class=\"tabletext\">");
      outString.append(UtilFormatOut.formatPrice(orderHeaderLine.getDouble("defaultPrice")));
      outString.append("</div>\r\n      </td>\r\n      <td align=\"right\" valign=\"top\" nowrap>\r\n        <div class=\"tabletext\">");
      outString.append(UtilFormatOut.formatPrice(orderHeaderLine.getDouble("defaultPrice").doubleValue()*orderHeaderLine.getDouble("quantity").doubleValue()));
      outString.append("</div>\r\n      </td>\r\n\r\n  </tr>\r\n");
    }
  }
  outString.append("\r\n  <tr>\r\n      <td colspan=\"2\" rowspan=\"3\" valign=\"middle\" align=\"center\" bgcolor=\"#99BBAA\"><div class=\"commentary\">Print this page for your records.</div></td>\r\n      <td colspan=\"2\" align=\"right\"><div class=\"tabletext\"><b>Shipping</b><br>\r\n          <font size=\"1\">");
  if(orderHeader != null) outString.append(orderHeader.getShippingMethod());
  outString.append("</font>\r\n\t  </div>\r\n      </td>\r\n      <td align=\"right\" nowrap valign=\"top\">\r\n\t<div class=\"tabletext\">");
  if(orderHeader != null) outString.append(orderHeader.getShippingAmount());
  outString.append("</div>\r\n      </td>\r\n\r\n  </tr>\r\n\r\n  <tr>\r\n      <td align=\"right\" colspan=\"2\"><div class=\"tabletext\"><b>Total Tax</b></div></td>\r\n      <td align=\"right\" nowrap>\r\n\t<div class=\"tabletext\">");
  outString.append("0.00");
  outString.append("</div>\r\n      </td>\r\n\r\n  </tr>\r\n\r\n  <tr>\r\n      <td align=\"right\" colspan=\"2\"><div class=\"tabletext\"><b>Total Due</b></div></td>\r\n     <td align=\"right\" nowrap>\r\n\t<div class=\"tabletext\">");
  if(orderHeader != null) outString.append(UtilFormatOut.formatPrice(orderHeader.getPriceAmount()));
  outString.append("</div>\r\n      </td>\r\n  </tr>\r\n</table>\r\n");
  outString.append("\r\n     </td>\r\n    </tr>\r\n   </table>\r\n<a href=\"" + myServerProtocol + "://");
  outString.append(myServerName);
  outString.append(":");
  outString.append(myServerPort);
  outString.append("/commerce/order/orderstatus.jsp?");
  if(orderHeader != null) outString.append("order_identifier" + "=" + orderHeader.getString("orderId"));
  outString.append("\" class=\"buttonlinkbig\">[View Order]</a>&nbsp;&nbsp;\r\n");
  outString.append("<a href=\"" + myServerProtocol + "://");
  outString.append(myServerName);
  outString.append(":");
  outString.append(myServerPort);
  outString.append("/commerce\" class=\"buttonlinkbig\">[Continue Shopping]</a>\r\n</body>\r\n</html>\r\n\r\n");
%>
<% pageContext.getOut().print(outString); %>

<%-- Get ready to send the mail message... --%>
<%  String userEMail = person.getEmail();  %>
<% System.out.println("userEMail: " + userEMail); %>
<%  String customerEMail = customer.getEmail();  %>
<% System.out.println("customerEMail: " + customerEMail); %>
<%  String userOrderEMails = customer.getOrderEmail();  %>
<% System.out.println("userOrderEMails: " + userOrderEMails); %>
<%  String OrderEMailsCC = CommonConstants.CONFIRMATION_ORDER_EMAIL_CC;  %>
<%-- System.out.println("OrderEMailsCC: " + OrderEMailsCC); --%>
<%  String OrderEMailsBCC = CommonConstants.CONFIRMATION_ORDER_EMAIL_BCC;  %>
<% System.out.println("OrderEMailsBCC: " + OrderEMailsBCC); %>
<%--  String OrderEMailRetAddr = "orders@abclumber.com";  --%>
<%  String OrderEMailRetAddr = CommonConstants.CONFIRMATION_ORDER_EMAIL;  %>
<% System.out.println("OrderEMailRetAddr: " + OrderEMailRetAddr); %>
<% String additionalEmails = (String)session.getAttribute(HttpRequestConstants.ORDER_ADDITIONAL_EMAILS); %>

<%
  String addrTO = new String("");
  if(userEMail != null && userEMail.length() > 0) addrTO = addrTO + userEMail;
  if(customerEMail != null && customerEMail.length() > 0)
  {
    if(addrTO.length() != 0) addrTO = addrTO + ", ";
    addrTO = addrTO + customerEMail;
  }
  if(userOrderEMails != null && userOrderEMails.length() != 0)
  {
    if(addrTO.length() != 0) addrTO = addrTO + ", ";
    addrTO = addrTO + userOrderEMails;
  }

  if(additionalEmails != null && additionalEmails.length() != 0)
  {
    if(OrderEMailsCC.length() != 0) OrderEMailsCC = addrTO + ", ";
    OrderEMailsCC = OrderEMailsCC + additionalEmails;
  }
%>
<% System.out.println("addrTO: " + addrTO); %>

<h3>This page will be sent to the following addresses:</h3>
<p>TO:<%=UtilFormatOut.checkNull(addrTO)%>
<p>CC:<%=UtilFormatOut.checkNull(OrderEMailsCC)%>

<%-- Send the mail message now... --%>
<%
 String contentString = new String(outString); //headerString + mainBodyString;
 //System.out.println("------------------About to output string...----------------------");
 //System.out.println(contentString);
 //System.out.println("------------------Done to output string...----------------------");
 //System.out.println(OrderEMailRetAddr);
 if(orderHeader != null && OrderEMailRetAddr != null && contentString != null &&
    OrderEMailRetAddr.length() > 0 && contentString.length() > 0)
 {
   SendMailSMTP sendMail = new SendMailSMTP();
   sendMail.setSender(OrderEMailRetAddr);

   sendMail.setRecipientTO(addrTO);
   if(OrderEMailsCC != null) sendMail.setRecipientCC(OrderEMailsCC);
   if(OrderEMailsBCC != null) sendMail.setRecipientBCC(OrderEMailsBCC);
   sendMail.setSubject(companyName + " Order Confirmation #" + orderHeader.getString("orderId"));
   sendMail.setMessage(contentString);
   sendMail.setExtraHeader("MIME-Version: 1.0\nContent-type: text/html; charset=us-ascii\n");
   //This messes things up (what would work here?): Content-Transfer-Encoding: quoted-printable
   // sendMail.setLocalMachine("ivanhoe.relmsoft.com");
   // sendMail.setDestinationSMTPServer("www.relmsoft.com");
   sendMail.setLocalMachine(CommonConstants.SMTP_LOCAL_MACHINE);
   sendMail.setDestinationSMTPServer(CommonConstants.RELAY_SMTP_HOST);
   sendMail.setLogging(false);

   try
   {
      //System.out.println("About to do the Mail send...");
      sendMail.send();
   }
   catch(java.io.IOException ioe)
   {
      System.out.println("Mail send failed from IOException!");
      ioe.printStackTrace();
   }
   catch(RuntimeException rte)
   {
      System.out.println("Mail send failed from RuntimeException!");
      rte.printStackTrace();
   }
 }
%>





