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
 *@version    $Revision: 1.4 $
 *@since      3.0
-->

<script language="JavaScript">
<!-- //
function lookupReturn(click) {
    returnIdValue = document.lookupreturn.returnId.value;
    if (returnIdValue.length > 1) {
        document.lookupreturn.action = "<@ofbizUrl>/returnMain</@ofbizUrl>";
    } else {
        document.lookupreturn.action = "<@ofbizUrl>/findreturn</@ofbizUrl>";
    }

    if (click) {
        document.lookupreturn.submit();
    }
    return true;
}
// -->
</script>

<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>
  <form method='post' name="lookupreturn" action="<@ofbizUrl>/findreturn</@ofbizUrl>" onsubmit="javascript:lookupReturn();">
    <input type='hidden' name='lookupFlag' value='A'>
    <input type='hidden' name='hideFields' value='Y'>
    <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
      <tr>
        <td width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
            <tr>
              <td><div class='boxhead'>Find Return(s)</div></td>
              <td align='right'>
                <div class="tabletext">
                  <#if requestParameters.hideFields?default("N") == "Y">
                    <a href="<@ofbizUrl>/findreturn?hideFields=N${paramList}</@ofbizUrl>" class="submenutextright">Show Lookup Fields</a>
                  <#else>
                    <#if returnHeaderList?exists><a href="<@ofbizUrl>/findreturn?hideFields=Y${paramList}</@ofbizUrl>" class="submenutext">Hide Fields</a></#if>
                    <a href="javascript:void();" onclick="javascript:lookupReturn(true);" class="submenutext">Lookup Return(s)</a><a href="<@ofbizUrl>/returnMain</@ofbizUrl>" class="submenutextright">Create Return</a>
                  </#if>
                </div>
              </td>
            </tr>
          </table>
          <#if requestParameters.hideFields?default("N") != "Y">
            <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
              <tr>
                <td align='center' width='100%'>
                  <table border='0' cellspacing='0' cellpadding='2'>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>Return ID:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td><input type='text' class='inputBox' name='returnId'></td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>Party ID:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td><input type='text' class='inputBox' name='partyId' value='${requestParameters.partyId?if_exists}'></td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>UserLogin ID:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td><input type='text' class='inputBox' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'></td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>Billing Acct:</div>
                      <td width='5%'>&nbsp;</td>
                      <td><input type='text' class='inputBox' name='billingAccountId' value='${requestParameters.billingAccountId?if_exists}'></td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'><div class='tableheadtext'>Status:</div></td>
                      <td width='5%'>&nbsp;</td>
                      <td>
                        <select name='returnStatusId' class='selectBox'>
                          <#if currentStatus?has_content>
                            <option value="${currentStatus.statusId}">${currentStatus.description}</option>
                            <option value="${currentStatus.statusId}">---</option>
                          </#if>
                          <option value="ANY">Any Return Status</option>
                          <#list returnStatuses as returnStatus>
                            <option value="${returnStatus.statusId}">${returnStatus.description}</option>
                          </#list>
                        </select>
                      </td>
                    </tr>
                    <tr>
                      <td width='25%' align='right'>
                        <div class='tableheadtext'>Date Filter:</div>
                      </td>
                      <td width='5%'>&nbsp;</td>
                      <td>
                        <table border='0' cellspacing='0' cellpadding='0'>
                          <tr>
                            <td nowrap>
                              <input type='text' size='25' class='inputBox' name='minDate' value='${requestParameters.minDate?if_exists}'>
                              <a href="javascript:call_cal(document.lookupreturn.minDate, '${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                              <span class='tabletext'>From</span>
                            </td>
                          </tr>
                          <tr>
                            <td nowrap>
                              <input type='text' size='25' class='inputBox' name='maxDate' value='${requestParameters.maxDate?if_exists}'>
                              <a href="javascript:call_cal(document.lookupreturn.maxDate, '${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                              <span class='tabletext'>Thru</span>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr><td colspan="3"><hr class="sepbar"></td></tr>
                    <tr>
                      <td width='25%' align='right'>&nbsp;</td>
                      <td width='5%'>&nbsp;</td>
                      <td>
                        <div class="tabletext">
                          <input type='checkbox' name='showAll' value='Y' onclick="javascript:lookupReturn(true);">&nbsp;Show All Records
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
    <input type="image" src="/images/spacer.gif" onClick="javascript:document.lookupreturn.submit();">
  </form>
<#if requestParameters.hideFields?default("N") != "Y">
  <script language="JavaScript">
    <!--//
      document.lookupreturn.returnId.focus();
    //-->
  </script>
</#if>
  <#if returnHeaderList?exists>
    <br>
    <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
      <tr>
        <td width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
            <tr>
              <td width="50%"><div class="boxhead">Return(s) Found</div></td>
              <td width="50%">
                 <div class="boxhead" align=right>
                  <#if (returnHeaderListSize > 0)>
                    <#if (viewIndex > 1)>
                      <a href="<@ofbizUrl>/findreturn?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutext">Previous</a>
                    <#else>
                      <span class="submenutextdisabled">Previous</span>
                    </#if>
                    <#if (returnHeaderListSize > 0)>
                      <span class="submenutextinfo">${lowIndex} - ${highIndex} of ${returnHeaderListSize}</span>
                    </#if>
                    <#if (returnHeaderListSize > highIndex)>
                      <a href="<@ofbizUrl>/findreturn?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutextright">Next</a>
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
              <td><div class="tableheadtext">Return #</div></td>
              <td><div class="tableheadtext">Entry Date</div></td>
              <td><div class="tableheadtext">Party</div></td>
              <td><div class="tableheadtext">Facility</div></td>
              <td><div class="tableheadtext">Status</div></td>
              <td>&nbsp;</td>
            </tr>
            <tr><td colspan="6"><hr class="sepbar"></td></tr>
            <#if returnHeaderList?has_content>
              <#assign rowClass = "viewManyTR2">
              <#list returnHeaderList as returnHeader>
                <#assign statusItem = returnHeader.getRelatedOne("StatusItem")>
                <#if returnHeader.destinationFacilityId?exists>
                  <#assign facility = returnHeader.getRelatedOne("Facility")>
                </#if>
                <tr class='${rowClass}'>
                  <td><a href="<@ofbizUrl>/returnMain?returnId=${returnHeader.returnId}</@ofbizUrl>" class="buttontext">${returnHeader.returnId}</a></td>
                  <td><div class="tabletext">${returnHeader.entryDate.toString()}</div></td>
                  <td>
                    <#if returnHeader.fromPartyId?exists>
                      <a href="/partymgr/control/viewprofile?partyId=${returnHeader.fromPartyId}${requestAttributes.externalKeyParam}" class='buttontext'>${returnHeader.fromPartyId}</a>
                    <#else>
                      <span class="tabletext">N/A</span>
                    </#if>
                  </td>
                  <td><div class="tabletext"><#if facility?exists>${facility.facilityName?default(facility.facilityId)}<#else>None</#if></div></td>
                  <td><div class="tabletext">${statusItem.description}</div></td>
                  <td align="center"><a href="<@ofbizUrl>/returnMain?returnId=${returnHeader.returnId}</@ofbizUrl>" class="buttontext">View</a>
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
                <td colspan='5'><div class='head3'>No return(s) found.</div></td>
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
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
</#if>