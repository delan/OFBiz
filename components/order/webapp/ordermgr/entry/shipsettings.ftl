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
 *@version    $Revision: 1.2 $
 *@since      2.2
-->

<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align='left'>
            <div class='boxhead'>&nbsp;Order Entry Ship-To Settings</div>
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
            <#if shippingContactMechList?has_content && !requestParameters.createNew?exists>
            <table width="100%" border="0" cellpadding="1" cellspacing="0">
              <tr>
                <td colspan="3">
                  <a href="<@ofbizUrl>/setShipping?createNew=Y</@ofbizUrl>" class="buttontext">[Create New]</a>
                </td>
              </tr>
              <form method="post" action="<@ofbizUrl>/finalizeOrder</@ofbizUrl>" name="shipsetupform"> 
                <input type="hidden" name="finalizeMode" value="ship">
                                
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
                  <#if shippingContactMech_has_next>
                  <tr><td colspan="3"><hr class='sepbar'></td></tr>
                  </#if>
                </#list>
              </form>
            </table>  
            <#else>
              <#if postalAddress?has_content>            
              <form method="post" action="<@ofbizUrl>/updatePostalAddress</@ofbizUrl>" name="shipsetupform">
                <input type="hidden" name="contactMechId" value="${shipContactMechId?if_exists}">
              <#else>
              <form method="post" action="<@ofbizUrl>/createPostalAddress</@ofbizUrl>" name="shipsetupform">
                <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS">
                <input type="hidden" name="contactMechPurposeTypeId" value="SHIPPING_LOCATION">
              </#if>
                <input type="hidden" name="partyId" value="${cart.partyId?default("_NA_")}">
                <input type="hidden" name="finalizeMode" value="ship">
                <#if person?exists && person?has_content>
                  <#assign toName = "">
                  <#if person.personalTitle?has_content><#assign toName = person.personalTitle + " "></#if>
                  <#assign toName = toName + person.firstName + " ">
                  <#if person.middleName?has_content><#assign toName = toName + person.middleName + " "></#if>
                  <#assign toName = toName + person.lastName>
                  <#if person.suffix?has_content><#assign toName = toName + " " + person.suffix></#if>
                <#else>
                  <#assign toName = "">
                </#if>
                <table width="100%" border="0" cellpadding="1" cellspacing="0">
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">To Name</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="toName" value="${toName}">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Attention Name</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="60" name="attnName" value="">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Address Line 1</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="30" name="address1" value="">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Address Line 2</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="30" name="address2" value="">
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">City</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="30" maxlength="30" name="city" value="">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">State/Province</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <select name="stateProvinceGeoId" class="selectBox">
                        <option value=""></option>                       
                        ${pages.get("/includes/states.ftl")}
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Zip/Postal Code</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <input type="text" class="inputBox" size="12" maxlength="10" name="postalCode" value="">
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Country</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <select name="countryGeoId" class="selectBox">                        
                        ${pages.get("/includes/countries.ftl")}
                      </select>
                    *</td>
                  </tr>
                  <tr>
                    <td width="26%" align=right valign=top><div class="tabletext">Allow Solicitation?</div></td>
                    <td width="5">&nbsp;</td>
                    <td width="74%">
                      <select name="allowSolicitation" class='selectBox'>                       
                        <option></option><option>Y</option><option>N</option>
                      </select>
                    </td>
                  </tr>                                    
                </td>
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
