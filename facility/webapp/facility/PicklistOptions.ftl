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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision$
 *@since      2.2
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
<#if facilityId?has_content>
  <div class='tabContainer'>
    <a href="<@ofbizUrl>/EditFacility?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Facility</a>
    <a href="<@ofbizUrl>/EditFacilityGroups?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Groups</a>
    <a href="<@ofbizUrl>/FindFacilityLocations?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Locations</a>
    <a href="<@ofbizUrl>/EditFacilityRoles?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Roles</a>
    <a href="<@ofbizUrl>/EditFacilityInventoryItems?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Inventory&nbsp;Items</a>
    <a href="<@ofbizUrl>/ReceiveInventory?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Inventory&nbsp;Receive</a>
    <a href="<@ofbizUrl>/FindFacilityTransfers?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Inventory&nbsp;Xfers</a>
    <a href="<@ofbizUrl>/ReceiveReturn?facilityId=${facilityId}</@ofbizUrl>" class="tabButton">Receive Return</a>
    <a href="<@ofbizUrl>/PicklistOptions?facilityId=${facilityId}</@ofbizUrl>" class="tabButtonSelected">Picklist</a>
  </div>
</#if>

<form method='post' name="PicklistOptions" action="<@ofbizUrl>/Picklist</@ofbizUrl>">
  <#if requestParameters.facilityId?exists>
    <input type="hidden" name="facilityId" value="${requestParameters.facilityId}"/>
  </#if>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td><div class='boxhead'>Find Orders</div></td>
          <td align='right'>
            <a href="javascript:document.PicklistOptions.submit();" class="submenutextright">Create Pick List</a>
          </td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td align='center' width='100%'>
            <table border='0' cellspacing='0' cellpadding='2'>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Order ID:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='order_id'></td>
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
                <td width='25%' align='right'><div class='tableheadtext'>Order Type:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='orderTypeId' class='selectBox'>
                    <#if currentType?has_content>
                    <option value="${currentType.orderTypeId}">${currentType.description}</option>
                    <option value="${currentType.orderTypeId}">---</option>
                    </#if>
                    <option value="ANY">Any Order Type</option>                
                    <#list orderTypes as orderType>
                      <option value="${orderType.orderTypeId}">${orderType.description}</option>
                    </#list>
                  </select>
                </td>
              </tr>                                           
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Billing Acct:</div>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='billingAccountId' value='${requestParameters.billingAccountId?if_exists}'></td>
              </tr>              
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Created By:</div>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='createdBy' value='${requestParameters.createdBy?if_exists}'></td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Web Site:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='webSiteId' class='selectBox'>
                    <#if currentWebSite?has_content>
                    <option value="${currentWebSite.webSiteId}">${currentWebSite.siteName}</option>
                    <option value="${currentWebSite.webSiteId}">---</option>
                    </#if>
                    <option value="ANY">Any Web Site</option>                
                    <#list webSites as webSite>
                      <option value="${webSite.webSiteId}">${webSite.siteName}</option>
                    </#list>
                  </select>
                </td>
              </tr>                              
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Status:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='orderStatusId' class='selectBox'> 
                    <#if currentStatus?has_content>
                    <option value="${currentStatus.statusId}">${currentStatus.description}</option>
                    <option value="${currentStatus.statusId}">---</option>
                    </#if>                                     
                    <option value="ANY">Any Order Status</option>                   
                    <#list orderStatuses as orderStatus>
                      <option value="${orderStatus.statusId}">${orderStatus.description}</option>
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
                      <td>                        
                        <input type='text' size='25' class='inputBox' name='minDate' value='${requestParameters.minDate?if_exists}'>
                        <a href="javascript:call_cal(document.lookuporder.minDate, '${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                        <span class='tabletext'>From</span>
                      </td>
                    </tr>
                    <tr>                  
                      <td>                       
                        <input type='text' size='25' class='inputBox' name='maxDate' value='${requestParameters.maxDate?if_exists}'>
                        <a href="javascript:call_cal(document.lookuporder.maxDate, '${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>                   
                        <span class='tabletext'>Thru</span>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</form> 

<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
