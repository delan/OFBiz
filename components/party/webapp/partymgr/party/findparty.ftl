<#--
 *  Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Revision: 1.11 $
 *@since      3.0
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<script language="JavaScript">
<!-- //
function lookupParty(click) {
    partyIdValue = document.lookupparty.partyId.value;
    userLoginIdValue = document.lookupparty.userlogin_id.value;
    if (partyIdValue.length > 1 || userLoginIdValue.length > 1) {
        document.lookupparty.action = "<@ofbizUrl>/viewprofile</@ofbizUrl>";
    } else {
        document.lookupparty.action = "<@ofbizUrl>/findparty</@ofbizUrl>";
    }

    if (click) {
        document.lookupparty.submit();
    }
    return true;
}
function refreshInfo() {
    document.lookupparty.lookupFlag.value = "N";
    document.lookupparty.hideFields.value = "N";
    document.lookupparty.submit();
}
// -->
</script>

<#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>
  <form method='post' name="lookupparty" action="<@ofbizUrl>/findparty</@ofbizUrl>" onsubmit="javascript:lookupParty();">
    <input type='hidden' name='lookupFlag' value='Y'>
    <input type='hidden' name='hideFields' value='Y'>
    <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
      <tr>
        <td width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
            <tr>
              <td><div class='boxhead'>Find Party</div></td>
              <td align='right'>
                <div class="tabletext">
                  <#if requestParameters.hideFields?default("N") == "Y">
                    <a href="<@ofbizUrl>/findparty?hideFields=N${paramList}</@ofbizUrl>" class="submenutextright">Show Lookup Fields</a>
                  <#else>
                    <#if partyList?exists><a href="<@ofbizUrl>/findparty?hideFields=Y${paramList}</@ofbizUrl>" class="submenutext">Hide Fields</a></#if>
                    <a href="javascript:lookupParty(true);" class="submenutextright">Lookup Party(s)</a>
                  </#if>
                </div>
              </td>
            </tr>
          </table>
          <#assign extInfo = requestParameters.extInfo?default("N")>
          <#if requestParameters.hideFields?default("N") != "Y">
            <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
              <tr>
                <td align='center' width='100%'>
                  <table border='0' cellspacing='0' cellpadding='2'>
                    <tr>
                      <td width='25%' align='right' nowrap><div class='tableheadtext'>Contact Info:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td nowrap>
                        <div class="tabletext">
                          <input type="radio" name="extInfo" value="N" onclick="javascript:refreshInfo();" <#if extInfo == "N">checked</#if>>None&nbsp;
                          <input type="radio" name="extInfo" value="P" onclick="javascript:refreshInfo();" <#if extInfo == "P">checked</#if>>Postal&nbsp;
                          <input type="radio" name="extInfo" value="T" onclick="javascript:refreshInfo();" <#if extInfo == "T">checked</#if>>Telecom&nbsp;
                          <input type="radio" name="extInfo" value="O" onclick="javascript:refreshInfo();" <#if extInfo == "O">checked</#if>>Other&nbsp;
                      </td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>${uiLabelMap.PartyPartyId}:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td><input type='text' class='inputBox' name='partyId'></td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>${uiLabelMap.PartyUserLogin}:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td><input type='text' class='inputBox' name='userlogin_id' value='${requestParameters.userLoginId?if_exists}'></td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>${uiLabelMap.PartyLastName}:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td><input type='text' class='inputBox' name='lastName' value='${requestParameters.lastName?if_exists}'></td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>${uiLabelMap.PartyFirstName}:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td><input type='text' class='inputBox' name='firstName' value='${requestParameters.firstName?if_exists}'></td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>${uiLabelMap.PartyPartyGroupName}:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td><input type='text' class='inputBox' name='groupName' value='${requestParameters.groupName?if_exists}'></td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>Role Type:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td>
                        <select name='roleTypeId' class='selectBox'>
                          <#if currentRole?has_content>
                            <option value="${currentRole.roleTypeId}">${currentRole.description}</option>
                            <option value="${currentRole.roleTypeId}">---</option>
                          </#if>
                          <option value="ANY">Any Role Type</option>
                          <#list roleTypes as roleType>
                            <option value="${roleType.roleTypeId}">${roleType.description}</option>
                          </#list>
                        </select>
                      </td>
                    </tr>
                    <#if extInfo == 'P'>
                      <tr><td colspan="3"><hr class="sepbar"></td></tr>
                      <tr>
                        <td width='25%' align='right'><div class='tableheadtext'>Address 1:</div></td>
                        <td width='5%'>&nbsp;</td>
                        <td><input type='text' class='inputBox' name='address1' value='${requestParameters.address1?if_exists}'></td>
                      </tr>
                      <tr>
                        <td width='25%' align='right'><div class='tableheadtext'>Address 2:</div></td>
                        <td width='5%'>&nbsp;</td>
                        <td><input type='text' class='inputBox' name='address2' value='${requestParameters.address2?if_exists}'></td>
                      </tr>
                      <tr>
                        <td width='25%' align='right'><div class='tableheadtext'>City:</div></td>
                        <td width='5%'>&nbsp;</td>
                        <td><input type='text' class='inputBox' name='city' value='${requestParameters.city?if_exists}'></td>
                      </tr>
                      <tr>
                        <td width='25%' align='right'><div class='tableheadtext'>State/Province:</div></td>
                        <td width='5%'>&nbsp;</td>
                        <td>
                          <select name='stateProvinceGeoId' class='selectBox'>
                            <#if currentStateGeo?has_content>
                              <option value="${currentStateGeo.geoId}">${currentStateGeo.geoName?default(currentStateGeo.geoId)}</option>
                              <option value="${currentStateGeo.geoId}">---</option>
                            </#if>
                            <option value="ANY">Any State/Province</option>
                            ${pages.get("/includes/states.ftl")}
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td width='25%' align='right'><div class='tableheadtext'>Postal Code:</div></td>
                        <td width='5%'>&nbsp;</td>
                        <td><input type='text' class='inputBox' name='postalCode' value='${requestParameters.postalCode?if_exists}'></td>
                      </tr>
                    </#if>
                    <#if extInfo == 'T'>
                      <tr><td colspan="3"><hr class="sepbar"></td></tr>
                      <tr>
                        <td width='25%' align='right'><div class='tableheadtext'>Country Code:</div></td>
                        <td width='5%'>&nbsp;</td>
                        <td><input type='text' class='inputBox' name='countryCode' value='${requestParameters.countryCode?if_exists}'></td>
                      </tr>
                      <tr>
                        <td width='25%' align='right'><div class='tableheadtext'>Area Code:</div></td>
                        <td width='5%'>&nbsp;</td>
                        <td><input type='text' class='inputBox' name='areaCode' value='${requestParameters.areaCode?if_exists}'></td>
                      </tr>
                      <tr>
                        <td width='25%' align='right'><div class='tableheadtext'>Contact Number:</div></td>
                        <td width='5%'>&nbsp;</td>
                        <td><input type='text' class='inputBox' name='contactNumber' value='${requestParameters.contactNumber?if_exists}'></td>
                      </tr>
                    </#if>
                    <#if extInfo == 'O'>
                      <tr><td colspan="3"><hr class="sepbar"></td></tr>
                      <tr>
                        <td width='25%' align='right'><div class='tableheadtext'>Contact Info (Email, URL, etc):</div></td>
                        <td width='5%'>&nbsp;</td>
                        <td><input type='text' class='inputBox' name='infoString' value='${requestParameters.infoString?if_exists}'></td>
                      </tr>
                    </#if>
                    <tr><td colspan="3"><hr class="sepbar"></td></tr>
                    <tr>
                      <td width='25%' align='right'>&nbsp;</td>
                      <td width='5%'>&nbsp;</td>
                      <td>
                        <div class="tabletext">
                          <input type='checkbox' name='showAll' value='Y' onclick="javascript:lookupParty(true);">&nbsp;Show All Records
                        </div>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </#if>
        </td>
      </tr>
    </table>
    <input type="image" src="/images/spacer.gif" onClick="javascript:document.lookupparty.submit();">
  </form>
<#if requestParameters.hideFields?default("N") != "Y">
  <script language="JavaScript">
    <!--//
      document.lookupparty.partyId.focus();
    //-->
  </script>
</#if>
  <#if partyList?exists>
    <br>
    <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
      <tr>
        <td width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
            <tr>
              <td width="50%"><div class="boxhead">Party(s) Found</div></td>
              <td width="50%">
                 <div class="boxhead" align=right>
                  <#if (partyListSize > 0)>
                    <#if (viewIndex > 1)>
                      <a href="<@ofbizUrl>/findparty?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutext">Previous</a>
                    <#else>
                      <span class="submenutextdisabled">Previous</span>
                    </#if>
                    <#if (partyListSize > 0)>
                      <span class="submenutextinfo">${lowIndex} - ${highIndex} of ${partyListSize}</span>
                    </#if>
                    <#if (partyListSize > highIndex)>
                      <a href="<@ofbizUrl>/findparty?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutextright">Next</a>
                    <#else>
                      <span class="submenutextrightdisabled">Next</span>
                    </#if>
                  </#if>
                  &nbsp;
                </div>
              </td>
            </tr>
          </table>

          <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td><div class="tableheadtext">Party #</div></td>
              <td><div class="tableheadtext">User Login</div></td>
              <td><div class="tableheadtext">Name</div></td>
              <#if extInfo?default("") == "P">
                <td><div class="tableheadtext">Postal Code</div></td>
              </#if>
              <#if extInfo?default("") == "T">
                <td><div class="tableheadtext">Area Code</div></td>
              </#if>
              <td><div class="tableheadtext">Type</div></td>
              <td>&nbsp;</td>
            </tr>
            <tr><td colspan="6"><hr class="sepbar"></td></tr>
            <#if partyList?has_content>
              <#assign rowClass = "viewManyTR2">
              <#list partyList as partyRow>
                <#assign partyType = partyRow.getRelatedOne("PartyType")?if_exists>
                <tr class='${rowClass}'>
                  <td><a href="<@ofbizUrl>/viewprofile?partyId=${partyRow.partyId}</@ofbizUrl>" class="buttontext">${partyRow.partyId}</a></td>
                  <td>
                    <div class="tabletext">
                      <#if partyRow.containsKey("userLoginId")>
                        ${partyRow.userLoginId?default("N/A")}
                      <#else>
                        <#assign userLogins = partyRow.getRelated("UserLogin")>
                        <#if (userLogins.size() > 0)>
                          <#if (userLogins.size() > 1)>
                            (Many)
                          <#else>
                            <#assign userLogin = userLogins.get(0)>
                            ${userLogin.userLoginId}
                          </#if>
                        <#else>
                          (None)
                        </#if>
                      </#if>
                    </div>
                  </td>
                  <td>
                    <div class="tabletext">
                      <#if partyRow.containsKey("lastName")>
                        <#if partyRow.lastName?has_content>
                          ${partyRow.lastName}<#if partyRow.firstName?has_content>, ${partyRow.firstName}</#if>
                        <#else>
                          (No Name Found)
                        </#if>
                      <#elseif partyRow.containsKey("groupName")>
                        <#if partyRow.groupName?has_content>
                          ${partyRow.groupName}
                        <#else>
                          (No Name Found)
                        </#if>
                      <#else>
                        ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(partyRow, true)}
                      </#if>
                    </div>
                  </td>
                  <#if extInfo?default("") == "P">
                    <td><div class="tabletext">${partyRow.postalCode?if_exists}</div></td>
                  </#if>
                  <#if extInfo?default("") == "T">
                    <td><div class="tabletext">${partyRow.areaCode?if_exists}</div></td>
                  </#if>
                  <td><div class="tabletext">${partyType.description?default("???")}</div></td>
                  <td align="right">
                    <!-- this is all on one line so that no break will be inserted -->
                    <div class="tabletext"><nobr>
                      <a href='<@ofbizUrl>/viewprofile?partyId=${partyRow.partyId}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonDetails}]</a>&nbsp;
                      <#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
                        <a href='/ordermgr/control/findorders?lookupFlag=Y&hideFields=Y&partyId=${partyRow.partyId + externalKeyParam}' class="buttontext">[${uiLabelMap.OrderOrders}]</a>&nbsp;
                      </#if>
                      <#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
                        <a href='/ordermgr/control/orderentry?mode=SALES_ORDER&partyId=${partyRow.partyId + externalKeyParam}' class="buttontext">[${uiLabelMap.OrderNewOrder}]</a>&nbsp;
                      </#if>
                    </nobr></div>
                  </td>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "viewManyTR2">
                  <#assign rowClass = "viewManyTR1">
                <#else>
                  <#assign rowClass = "viewManyTR2">
                </#if>
              </#list>
            <#else>
              <tr>
                <td colspan='5'><div class='head3'>No party(s) found.</div></td>
              </tr>
            </#if>
            <#if lookupErrorMessage?exists>
              <tr>
                <td colspan='5'><div class="head3">${lookupErrorMessage}</div></td>
              </tr>
            </#if>
          </table>
        </td>
      </tr>
    </table>
  </#if>
<#else>
  <h3>${uiLabelMap.PartyMgrViewPermissionError}</h3>
</#if>