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
	String partyId = (String) session.getAttribute("orderPartyId");
	GenericValue party = null;
	Collection paymentMethodList = null;

	if (partyId != null)
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
        } else if (shoppingCart.getPaymentMethodTypeIds().size() > 0) {
            checkOutPaymentId = (String) shoppingCart.getPaymentMethodTypeIds().get(0);
        }
    }		

	// just for now
	pageContext.setAttribute("paymentMethodType", "CC");
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

<ofbiz:if name="party">
<form method="post" action="<ofbiz:url>/finalizeOrder</ofbiz:url>" name="billsetupform">
<table width="100%" cellpadding="1" cellspacing="0" border="0">
  <tr><td colspan="2">    
    <a href="/partymgr/control/editcreditcard?party_id=<%=partyId%>" target="_blank" class="buttontext">[Add Credit Card]</a>
    <a href="/partymgr/control/editeftaccount?party_id=<%=partyId%>" target="_blank" class="buttontext">[Add EFT Account]</a>
  </td></tr>
  <tr><td colspan="3"><hr class='sepbar'></td></tr>
  <tr>
    <td width="1%" nowrap>
      <input type="radio" name="checkOutPaymentId" value="OFFLINE"
        <%="OFFLINE".equals(checkOutPaymentId) ? "CHECKED" : ""%>>
    </td>
    <td colpan="2" width="50%" nowrap>
      <span class="tabletext">Offline:&nbsp;Check/Money Order</span>
    </td>
  </tr>
  <tr><td colspan="3"><hr class='sepbar'></td></tr>
 <ofbiz:if name="paymentMethodList" size="0">
  <ofbiz:iterator name="paymentMethod" property="paymentMethodList">
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
      <tr><td colspan="3"><hr class='sepbar'></td></tr>
  </ofbiz:iterator>
 </ofbiz:if>
</table>
</form>
<ofbiz:unless name="paymentMethodList" size="0">
   <div class='tabletext'><b>There are no payment methods on file.</b></div>
</ofbiz:unless>
</ofbiz:if>
<ofbiz:unless name="party">
  <ofbiz:if name="paymentMethodType" value="CC">
    <form method="post" action="<ofbiz:url>/createCreditCardAndPostalAddress</ofbiz:url>" name="billsetupform">
  </ofbiz:if>
  <ofbiz:if name="paymentMethodType" value="EFT">
    <form method="post" action="<ofbiz:url>/createEftAndPostalAddress</ofbiz:url>" name="billsetupform">
  </ofbiz:if>
  <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS">
  <input type="hidden" name="partyId" value="_NA_">
  <table width="100%" border="0" cellpadding="1" cellspacing="0">
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
          <%if (expDate != null && expDate.indexOf('/') > 0){%>
            <%expMonth = expDate.substring(0,expDate.indexOf('/'));%>
            <%expYear = expDate.substring(expDate.indexOf('/')+1);%>
          <%}%>
        <%}%>
        <select name="expMonth">
          <option><ofbiz:if name="tryEntity"><%=UtilFormatOut.checkNull(expMonth)%></ofbiz:if><ofbiz:unless name="tryEntity"><%=UtilFormatOut.checkNull(request.getParameter("expMonth"))%></ofbiz:unless></option>
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
          <option><ofbiz:if name="tryEntity"><%=UtilFormatOut.checkNull(expYear)%></ofbiz:if><ofbiz:unless name="tryEntity"><%=UtilFormatOut.checkNull(request.getParameter("expYear"))%></ofbiz:unless></option>
          <option></option>
          <option>2001</option>
          <option>2002</option>
          <option>2003</option>
          <option>2004</option>
          <option>2005</option>
          <option>2006</option>
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
