<%
/**
 *  Title: Confirm Order Page
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
 *@author     Eric Pabst
 *@created    May 22 2001
 *@version    1.0
 */
%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.utils.SendMailSMTP" %>
<%@ page import="javax.servlet.jsp.tagext.BodyContent" %>


<%-- Insert the same formatting code here as exists in orderstatus.jsp --%>


<%-- SendMailSMTP Code --%>
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
