<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision$
 *@since      2.2
-->

<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align='left'>
            <div class='boxhead'>&nbsp;Order Entry ShipTo Settings</div>
          </td> 
          <td nowrap align="right">
            <div class="tabletext">
              <a href="<@ofbizUrl>/setShipping</@ofbizUrl>" class="submenutext">Refresh</a><a href="<@ofbizUrl>/orderentry</@ofbizUrl>" class="submenutext">Items</a><a href="javascript:document.shipsetupform.submit();" class="submenutextright">Continue</a>
            </div>
          </td>         
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <#if party?has_content>
            <table width="100%" border="0" cellpadding="1" cellspacing="0">
              <tr>
                <td colspan="3">
                  <a href="/partymgr/control/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&party_id=<%=partyId%>" target="_blank" class="buttontext">[Add New Address]</a>
                </td>
              </tr>
              <form method="post" action="<@ofbizUrl>/finalizeOrder</@ofbizUrl>" name="shipsetupform"> 
                <input type="hidden" name="finalizeMode" value="ship">
                
                <#if shippingContactMechList?has_content>
                  <tr><td colspan="3"><hr class='sepbar'></td></tr>
                  <#list shippingContactMechList as shippingContactMech>
                    <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
                    <tr>
                      <td align="left" valign="top" width="1%" nowrap>
                        <input type="radio" name="shipping_contact_mech_id" value="${shippingAddress.contactMechId}" <#if cart.getShippingContactMechId()?default("") == shippingAddress.contactMechId>checked</#if>>        
                      </td>
                      <td align="left" valign="top" width="99%" nowrap>
                        <div class="tabletext">
                          <#if shippingAddress.toName?has_content><b>To:</b>&nbsp;${shippingAddress.toName}<br></#if>
                          <#if shippingAddress.attnName?has_content><b>Attn:</b>&nbsp;${shippingAddress.attnName}<br></#if>
                          <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br></#if>
                          <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br></#if>
                          <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
                          <#if shippingAddress.stateProvinceGeoId?has_content><br>${shippingAddress.stateProvinceGeoId}</#if>
                          <#if shippingAddress.postalCode?has_content><br>${shippingAddress.postalCode}</#if>
                          <#if shippingAddress.countryGeoId?has_content><br>${shippingAddress.countryGeoId}</#if>                                                                                     
                        </div>
                      </td>
                      <td>
                        <div class="tabletext"><a href="/partymgr/control/editcontactmech?party_id=<%=partyId%>&contactMechId=<%=shippingContactMechId%>" target="_blank" class="buttontext">[Update]</a></div>
                      </td>                      
                    </tr>
                    <tr><td colspan="3"><hr class='sepbar'></td></tr>
                  </#list>
                </#if>                                                                                         
              </form>
            </table>  
            <#else>
              <#if postalAddress?has_content>            
              <form method="post" action="<@ofbizUrl>/updatePostalAddress</@ofbizUrl>" name="shipsetupform">
                <input type="hidden" name="contactMechId" value="${shipContactMechId}">
              <#else>
              <form method="post" action="<@ofbizUrl>/createPostalAddress</@ofbizUrl>" name="shipsetupform">
                <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS">
              </#if>
                <input type="hidden" name="partyId" value="_NA_">
                <input type="hidden" name="finalizeMode" value="ship">
                <table width="100%" border="0" cellpadding="1" cellspacing="0">
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">To Name</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="toName" value="${postalFields.toName?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Attention Name</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="attnName" value="${postalFields.attnName?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Address Line 1</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="30" name="address1" value="${postalFields.address1?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Address Line 2</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="30" name="address2" value="${postalFields.address2?if_exists}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">City</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="30" name="city" value="${postalFields.city?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">State/Province</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <select name="stateProvinceGeoId" class="selectBox">
                        <#if postalFields.stateProvinceGeoId?has_content>
                        <option>${postalFields.stateProvinceGeoId}</option>
                        <option value="${postalFields.stateProvinceGeoId}">---</option>
                        </#if>
                        ${pages.get("/includes/states.ftl")}
                      </select>
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Zip/Postal Code</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="12" maxlength="10" name="postalCode" value="${postalFields.postalCode?if_exists}">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Country</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <select name="countryGeoId" class="selectBox">
                        <#if postalFields.countryGeoId?has_content>
                        <option>${postalFields.countryGeoId}</option>
                        <option value="${postalFields.countryGeoId}">---</option>
                        </#if>
                        ${pages.get("/includes/countries.ftl")}
                      </select>
                    *</td>
                  </tr>
                </table>
              </form>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<br>
<#else>
  <h3>You do not have permission to view this page. ("ORDERMGR_CREATE" or "ORDERMGR_ADMIN" needed)</h3>
</#if>
