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
<%@ page import="org.ofbiz.commonapp.order.shoppingcart.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="shoppingCart" type="org.ofbiz.commonapp.order.shoppingcart.ShoppingCart" scope="session"/>

<%if(security.hasEntityPermission("ORDERMGR", "_CREATE", session)) {%>

<%	
    String paymentMethodType = request.getParameter("paymentMethodType");
	String partyId = (String) session.getAttribute("orderPartyId");
	GenericValue party = null;
	Collection paymentMethodList = null;

	if (partyId != null && !partyId.equals("_NA_"))
		party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
	
	if (party != null) {
		pageContext.setAttribute("party", party);
		paymentMethodList = EntityUtil.filterByDate(party.getRelated("PaymentMethod"), true);
	}

	if (paymentMethodList != null)
		pageContext.setAttribute("paymentMethodList", paymentMethodList);

    String checkOutPaymentId = null;
    if (shoppingCart != null) {
        if (shoppingCart.getPaymentMethodIds().size() > 0) {        	
            checkOutPaymentId = (String) shoppingCart.getPaymentMethodIds().get(0);            
            if (party == null) {            	            	
            	GenericValue paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", checkOutPaymentId));
            	GenericValue account = null;
            	GenericValue address = null;
            	if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD")) {
            		paymentMethodType = "CC";
            		account = paymentMethod.getRelatedOne("CreditCard");       
            		if (account != null) pageContext.setAttribute("creditCard", account);     		
                } else if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("EFT_ACCOUNT")) {
                	paymentMethodType = "EFT";
                	account = paymentMethod.getRelatedOne("EftAccount");
                	if (account != null) pageContext.setAttribute("eftAccount", account);
                } else {
                	paymentMethodType = "offline";
                }
                if (account != null) {
                	address = account.getRelatedOne("PostalAddress");
                	pageContext.setAttribute("postalAddress", address);
                }
            }            
        } else if (shoppingCart.getPaymentMethodTypeIds().size() > 0) {
            checkOutPaymentId = (String) shoppingCart.getPaymentMethodTypeIds().get(0);
        }
    }	
    
    if (request.getAttribute("OFFLINE_PAYMENT") != null || (paymentMethodType != null && paymentMethodType.equals("OFFLINE_PAYMENT")))
    	pageContext.setAttribute("OFFLINE_PAYMENT", new Boolean(true));
    	
	if (checkOutPaymentId != null) pageContext.setAttribute("checkOutPaymentId", checkOutPaymentId);
	if (paymentMethodType != null) pageContext.setAttribute("paymentMethodType", paymentMethodType);
%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='70%' >
            <div class='boxhead'>&nbsp;Order Entry Payment Settings</div>
          </TD> 
          <TD align="right">
            <div class="tabletext">
              <a href="<ofbiz:url>/setBilling</ofbiz:url>" class="lightbuttontext">[Refresh]</a>
              <a href="javascript:document.billsetupform.submit();" class="lightbuttontext">[Continue]</a>
            </div>
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
          
<ofbiz:if name="OFFLINE_PAYMENT">
  <form method="post" name="billsetupform">
    <input type="hidden" name="finalizeMode" value="offline_payments">
    <table width="100%" cellpadding="1" cellspacing="0" border="0">
      <%
          List pmtFields = UtilMisc.toList(new EntityExpr("paymentMethodTypeId", EntityOperator.NOT_EQUAL, "EXT_OFFLINE"));
          List paymentMethodTypes = delegator.findByAnd("PaymentMethodType", pmtFields);
          if (paymentMethodTypes != null) pageContext.setAttribute("paymentMethodTypes", paymentMethodTypes);
      %>
      <ofbiz:iterator name="payType" property="paymentMethodTypes">      
      <tr>
        <td width="30%" align="right"><div class="tabletext"><%=UtilFormatOut.checkNull(payType.getString("description"))%></div></td>
        <td width="5">&nbsp;</td>
        <td width="70%"><input type="text" size="6" name="<%=payType.getString("paymentMethodTypeId")%>" style="font-size: x-small;"></td>
      </tr>
      </ofbiz:iterator>
    </table>
  </form>
</ofbiz:if>

<ofbiz:unless name="OFFLINE_PAYMENT">
  <ofbiz:if name="party">
    <form method="post" action="<ofbiz:url>/finalizeOrder</ofbiz:url>" name="billsetupform">
      <input type="hidden" name="finalizeMode" value="payment">
      <table width="100%" cellpadding="1" cellspacing="0" border="0">
        <tr><td colspan="2">    
          <a href="/partymgr/control/editcreditcard?party_id=<%=partyId%>" target="_blank" class="buttontext">[Add Credit Card]</a>
          <a href="/partymgr/control/editeftaccount?party_id=<%=partyId%>" target="_blank" class="buttontext">[Add EFT Account]</a>
        </td></tr>
        <tr><td colspan="3"><hr class='sepbar'></td></tr>
        <tr>
          <td width="1%" nowrap>
            <input type="radio" name="checkOutPaymentId" value="OFFLINE_PAYMENT"
            <%="OFFLINE_PAYMENT".equals(checkOutPaymentId) ? "CHECKED" : ""%>>
          </td>
          <td colpan="2" width="50%" nowrap>
            <span class="tabletext">Payment already received</span>
          </td>
        </tr>
        <tr><td colspan="3"><hr class='sepbar'></td></tr>    
        <tr>
          <td width="1%" nowrap>
            <input type="radio" name="checkOutPaymentId" value="EXT_OFFLINE"
            <%="EXT_OFFLINE".equals(checkOutPaymentId) ? "CHECKED" : ""%>>
          </td>
          <td colpan="2" width="50%" nowrap>
            <span class="tabletext">Offline:&nbsp;Check/Money Order</span>
          </td>
        </tr>
        <ofbiz:if name="paymentMethodList" size="0">
          <ofbiz:iterator name="paymentMethod" property="paymentMethodList">
            <tr><td colspan="3"><hr class='sepbar'></td></tr>
            <%if ("CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {%>
              <%GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");%>
              <tr>
                <td width="1%" nowrap>
                  <%String paymentMethodId = paymentMethod.getString("paymentMethodId");%>
                  <input type="radio" name="checkOutPaymentId" value="<%=paymentMethodId%>"
                  <%=paymentMethodId.equals(checkOutPaymentId) ? "CHECKED" : ""%>>
                </td>
                <td width="50%" nowrap>
                  <span class="tabletext">CC:&nbsp;<%=ContactHelper.formatCreditCard(creditCard)%></span>              
                </td>
                <td align="right"><a href="/partymgr/control/editcreditcard?party_id=<%=partyId%>&paymentMethodId=<%=paymentMethod.getString("paymentMethodId")%>" target="_blank" class="buttontext">[Update]</a></td>
              </tr>
            <%} else if ("EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {%>
              <%GenericValue eftAccount = paymentMethod.getRelatedOne("EftAccount");%>
              <%pageContext.setAttribute("eftAccount", eftAccount);%>
              <tr>
                <td width="1%" nowrap>
                  <%String paymentMethodId = paymentMethod.getString("paymentMethodId");%>
                  <input type="radio" name="checkOutPaymentId" value="<%=paymentMethodId%>"
                    <%=paymentMethodId.equals(checkOutPaymentId) ? "CHECKED" : ""%>>
                </td>
                <td width="50%" nowrap>
                  <span class="tabletext">
                    EFT:&nbsp;<%EntityField.run("eftAccount", "bankName", pageContext);%>
                    <%EntityField.run("eftAccount", "accountNumber", ": ", "", pageContext);%>
                  </span>              
                </td>
                <td align="right"><a href="/partymgr/control/editeftaccount?party_id=<%=partyId%>&paymentMethodId=<%=paymentMethod.getString("paymentMethodId")%>" target="_blank" class="buttontext">[Update]</a></td>
              </tr>
            <%}%>      
          </ofbiz:iterator>
        </ofbiz:if>
      </table>
    </form>
    <ofbiz:unless name="paymentMethodList" size="0">
      <div class='tabletext'><b>There are no payment methods on file.</b></div>
    </ofbiz:unless>
  </ofbiz:if>

  <ofbiz:unless name="party">
    <%
  	    String shippingContactMech = shoppingCart.getShippingContactMechId();
	    String useShipAddr = request.getParameter("useShipAddr");
	    GenericValue postalAddress = null;
	    if (shippingContactMech != null) {
		    pageContext.setAttribute("shippingContactMechId", shippingContactMech);
		    if (useShipAddr != null && useShipAddr.equals("Y")) {
			    postalAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", shippingContactMech));
			    if (postalAddress != null) pageContext.setAttribute("postalAddress", postalAddress);
		    }
	    }
    %>

    <script language="javascript">
    <!-- //
    function shipBillAddr() {
	    if (document.billsetupform.useShipAddr.checked)
		    window.location.replace("/ordermgr/control/setBilling?paymentMethodType=<%=paymentMethodType%>&useShipAddr=Y");
	    else 
    	    window.location.replace("/ordermgr/control/setBilling?paymentMethodType=<%=paymentMethodType%>");
    }
    // -->
    </script>

    <ofbiz:if name="paymentMethodType">
      <ofbiz:if name="paymentMethodType" value="CC">
        <ofbiz:if name="postalAddress">
          <form method="post" action="<ofbiz:url>/updateCreditCardAndPostalAddress</ofbiz:url>" name="billsetupform">
            <input type="hidden" name="paymentMethodId" value="<ofbiz:inputvalue field="paymentMethodId" entityAttr="creditCard" tryEntityAttr="tryEntity"/>">
            <input type="hidden" name="contactMechId" value="<ofbiz:inputvalue field="contactMechId" entityAttr="postalAddress" tryEntityAttr="tryEntity"/>">
        </ofbiz:if>
        <ofbiz:unless name="postalAddress">
          <form method="post" action="<ofbiz:url>/createCreditCardAndPostalAddress</ofbiz:url>" name="billsetupform">
        </ofbiz:unless>
      </ofbiz:if>
      <ofbiz:if name="paymentMethodType" value="EFT">
        <ofbiz:if name="postalAddress">
          <form method="post" action="<ofbiz:url>/updateEftAndPostalAddress</ofbiz:url>" name="billsetupform">
            <input type="hidden" name="paymentMethodId" value="<ofbiz:inputvalue field="paymentMethodId" entityAttr="eftAccount" tryEntityAttr="tryEntity"/>">
            <input type="hidden" name="contactMechId" value="<ofbiz:inputvalue field="contactMechId" entityAttr="postalAddress" tryEntityAttr="tryEntity"/>">
        </ofbiz:if>
        <ofbiz:unless name="postalAddress">
          <form method="post" action="<ofbiz:url>/createEftAndPostalAddress</ofbiz:url>" name="billsetupform">
        </ofbiz:unless>        
      </ofbiz:if>
      <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS">
      <input type="hidden" name="partyId" value="_NA_">
      <input type="hidden" name="finalizeMode" value="payment">
      <table width="100%" border="0" cellpadding="1" cellspacing="0">
        <ofbiz:unless name="checkOutPaymentId">
          <ofbiz:if name="shippingContactMechId">
          <tr>
            <td width="26%" align="right"= valign="top">
              <input type="checkbox" name="useShipAddr" value="Y" onClick="javascript:shipBillAddr();" <%=useShipAddr != null ? "checked" : ""%>>
            </td>
            <td colspan="2" align="left" valign="center">
              <div class="tabletext">Billing address is the same as the shipping address</div>
            </td>
          </tr>
          <tr>
            <td colspan="3"><hr class="sepbar"></td>
          </tr>
          </ofbiz:if>
        </ofbiz:unless>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">To Name</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="toName" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
          </td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Attention Name</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="attnName" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
          </td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Address Line 1</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="30" <ofbiz:inputvalue field="address1" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Address Line 2</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="30" <ofbiz:inputvalue field="address2" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
          </td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">City</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="30" <ofbiz:inputvalue field="city" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">State/Province</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <select name="stateProvinceGeoId">
              <option><ofbiz:inputvalue field="stateProvinceGeoId" entityAttr="postalAddress" tryEntityAttr="tryEntity"/></option>
              <option></option>
              <%@ include file="/includes/states.jsp" %>
            </select>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Zip/Postal Code</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="12" maxlength="10" <ofbiz:inputvalue field="postalCode" entityAttr="postalAddress" tryEntityAttr="tryEntity" fullattrs="true"/>>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Country</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <select name="countryGeoId" >
              <option><ofbiz:inputvalue field="countryGeoId" entityAttr="postalAddress" tryEntityAttr="tryEntity"/></option>
              <option></option>
              <%@ include file="/includes/countries.jsp" %>
            </select>
          *</td>
        </tr>

	    <ofbiz:if name="paymentMethodType" value="CC">
        <tr>
          <td colspan="3"><hr class="sepbar"></td>
        </tr>
  	    <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Name on Card</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="nameOnCard" entityAttr="creditCard" tryEntityAttr="tryEntity" fullattrs="true"/>>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Company Name on Card</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="companyNameOnCard" entityAttr="creditCard" tryEntityAttr="tryEntity" fullattrs="true"/>>
          </td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Card Type</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <select name="cardType">
              <option><ofbiz:inputvalue field="cardType" entityAttr="creditCard" tryEntityAttr="tryEntity"/></option>
              <option></option>
              <option>Visa</option>
              <option value='MasterCard'>Master Card</option>
              <option value='AmericanExpress'>American Express</option>
              <option value='DinersClub'>Diners Club</option>
              <option>Discover</option>
              <option>EnRoute</option>
              <option>JCB</option>
            </select>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Card Number</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="20" maxlength="30" <ofbiz:inputvalue field="cardNumber" entityAttr="creditCard" tryEntityAttr="tryEntity" fullattrs="true"/>>
          *</td>
        </tr>
        <%--<tr>
          <td width="26%" align=right valign=top><div class="tabletext">Card Security Code</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="5" maxlength="10" <ofbiz:inputvalue field="cardSecurityCode" entityAttr="creditCard" tryEntityAttr="tryEntity" fullattrs="true"/>>
          </td>
        </tr>--%>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Expiration Date</div></td>        
          <td width="5">&nbsp;</td>
          <td width="74%">
            <%String expMonth = "";%>
            <%String expYear = "";%>
            <%if (pageContext.getAttribute("creditCard") != null) {%>
              <%String expDate = ((GenericValue) pageContext.getAttribute("creditCard")).getString("expireDate");%>
              <%Debug.logError("EXPDATE: " + expDate);%>
              <%if (expDate != null && expDate.indexOf('/') > 0){%>
                <%expMonth = expDate.substring(0,expDate.indexOf('/'));%>
                <%expYear = expDate.substring(expDate.indexOf('/')+1);%>
                <%Debug.logError("M: " + expMonth + "  Y: " + expYear);%>
              <%}%>
            <%}%>
            <select name="expMonth">
              <option><ofbiz:if name="creditCard"><%=UtilFormatOut.checkNull(expMonth)%></ofbiz:if><ofbiz:unless name="tryEntity"><%=UtilFormatOut.checkNull(request.getParameter("expMonth"))%></ofbiz:unless></option>
              <option></option>
              <option>01</option>
              <option>02</option>
              <option>03</option>
              <option>04</option>
              <option>05</option>
              <option>06</option>
              <option>07</option>
              <option>08</option>
              <option>09</option>
              <option>10</option>
              <option>11</option>
              <option>12</option>
            </select>
            <select name="expYear">
              <option><ofbiz:if name="creditCard"><%=UtilFormatOut.checkNull(expYear)%></ofbiz:if><ofbiz:unless name="tryEntity"><%=UtilFormatOut.checkNull(request.getParameter("expYear"))%></ofbiz:unless></option>
              <option></option>          
              <option>2003</option>
              <option>2004</option>
              <option>2005</option>
              <option>2006</option>
              <option>2007</option>
              <option>2008</option>
              <option>2009</option>
              <option>2010</option>
            </select>
          *</td>
        </tr>
  	    </ofbiz:if>
	
  	    <ofbiz:if name="paymentMethodType" value="EFT">
  	    <tr>
          <td colspan="3"><hr class="sepbar"></td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Name on Account</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="nameOnAccount" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Company Name on Account</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="companyNameOnAccount" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
          </td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Bank Name</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="30" maxlength="60" <ofbiz:inputvalue field="bankName" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Routing Number</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="10" maxlength="30" <ofbiz:inputvalue field="routingNumber" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Account Type</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <select name="accountType">
              <option><ofbiz:inputvalue field="accountType" entityAttr="eftAccount" tryEntityAttr="tryEntity"/></option>
              <option></option>
              <option>Checking</option>
              <option>Savings</option>
            </select>
          *</td>
        </tr>
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">Account Number</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="text" size="20" maxlength="40" <ofbiz:inputvalue field="accountNumber" entityAttr="eftAccount" tryEntityAttr="tryEntity" fullattrs="true"/>>
          *</td>
        </tr>
        </ofbiz:if>
      </table>
    </form>
    </ofbiz:if>
    
    <ofbiz:unless name="paymentMethodType">
    <form method="post" action="<ofbiz:url>/finalizeOrder</ofbiz:url>" name="billsetupform">
      <input type="hidden" name="finalizeMode" value="payoption">
      <table width="100%" border="0" cellpadding="1" cellspacing="0">
        <tr>
          <td><div class="tabletext">Offline Payment: Check/Money Order</div></td>
          <td><input type="radio" name="paymentMethodType" value="offline" <%=(paymentMethodType != null && paymentMethodType.equals("offline") ? "checked" : "")%>>
        </tr>
        <tr><td colspan="2"><hr class='sepbar'></td></tr>
        <tr>
          <td><div class="tabletext">Credit Card: Visa/Mastercard/Amex/Discover</div></td>
          <td><input type="radio" name="paymentMethodType" value="CC">  
        </tr>
        <tr><td colspan="2"><hr class='sepbar'></td></tr>
        <tr>
          <td><div class="tabletext">EFT Account: AHC/Electronic Check</div></td>
          <td><input type="radio" name="paymentMethodType" value="EFT">
        </tr>
        <tr>
          <td><div class="tabletext">Payment already received</div></td>
          <td><input type="radio" name="paymentMethodType" value="offline_payment"></td>
        </tr>
      </table>
    </form>
    </ofbiz:unless>
  </ofbiz:unless>
</ofbiz:unless>

          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("ORDERMGR_CREATE" or "ORDERMGR_ADMIN" needed)</h3>
<%}%>
