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
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.order.shoppingcart.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="shoppingCart" type="org.ofbiz.commonapp.order.shoppingcart.ShoppingCart" scope="session"/>

<%	
	String partyId = (String) session.getAttribute("orderPartyId");
	GenericValue party = null;
	Collection shippingContactMechList = null;

	if (partyId != null)
		party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
	if (party != null) {
		pageContext.setAttribute("party", party);
		shippingContactMechList = ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
	}
	
	if (shippingContactMechList != null)
		pageContext.setAttribute("shippingContactMechList", shippingContactMechList);

	String shipContactMechId = shoppingCart.getShippingContactMechId();
	GenericValue postalAddress = null;
	if (shipContactMechId != null)
		postalAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", shipContactMechId));
	if (postalAddress != null) pageContext.setAttribute("postalAddress", postalAddress);
		
%>


<%if(security.hasEntityPermission("ORDERMGR", "_CREATE", session)) {%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='70%' >
            <div class='boxhead'>&nbsp;Order Entry ShipTo Settings</div>
          </TD> 
          <TD align="right">
            <div class="tabletext">
              <a href="<ofbiz:url>/setShipping</ofbiz:url>" class="lightbuttontext">[Refresh]</a>
              <a href="javascript:document.shipsetupform.submit();" class="lightbuttontext">[Continue]</a>
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
<table width="100%" border="0" cellpadding="1" cellspacing="0">
  <tr><td colspan="3"><a href="/partymgr/control/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&party_id=<%=partyId%>" target="_blank" class="buttontext">[Add New Address]</a></td></tr>
  <form method="post" action="<ofbiz:url>/setShipping</ofbiz:url>" name="shipsetupform">
  <ofbiz:if name="shippingContactMechList" size="0">
    <tr><td colspan="3"><hr class='sepbar'></td></tr>
    <ofbiz:iterator name="shippingContactMech" property="shippingContactMechList">
      <%GenericValue shippingAddress = shippingContactMech.getRelatedOne("PostalAddress");%>
      <%pageContext.setAttribute("shippingAddress", shippingAddress);%>
      <tr>
        <td align="left" valign="top" width="1%" nowrap>
            <%String shippingContactMechId = (String) shippingAddress.get("contactMechId");%>
            <input type="radio" name="shipping_contact_mech_id" value="<%=shippingContactMechId%>"
              <ofbiz:if name="shoppingCart"><%=shippingContactMechId.equals(shoppingCart.getShippingContactMechId()) ? "CHECKED" : ""%></ofbiz:if>>
        </td>
        <td align="left" valign="top" width="99%" nowrap>
          <div class="tabletext">
            <ofbiz:entityfield attribute="shippingAddress" field="toName" prefix="" suffix="<br>"/>
            <ofbiz:entityfield attribute="shippingAddress" field="attnName" prefix="" suffix="<br>"/>
            <ofbiz:entityfield attribute="shippingAddress" field="address1"/><br>
            <ofbiz:entityfield attribute="shippingAddress" field="address2" prefix="" suffix="<br>"/>
            <ofbiz:entityfield attribute="shippingAddress" field="city"/>,
            <ofbiz:entityfield attribute="shippingAddress" field="stateProvinceGeoId"/>
            <ofbiz:entityfield attribute="shippingAddress" field="postalCode"/>
            <ofbiz:entityfield attribute="shippingAddress" field="countryGeoId" prefix="<br>" suffix=""/>            
          </div>
        </td>
        <td>
          <div class="tabletext"><a href="/partymgr/control/editcontactmech?party_id=<%=partyId%>&contactMechId=<%=shippingContactMechId%>" target="_blank" class="buttontext">[Update]</a></div>
        </td>
      </tr>
      <tr><td colspan="3"><hr class='sepbar'></td></tr>
  </ofbiz:iterator>
 </ofbiz:if>
 </form>
</table>  
</ofbiz:if>

<ofbiz:unless name="party">
  <form method="post" action="<ofbiz:url>/createPostalAddress</ofbiz:url>" name="shipsetupform">
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
