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
 *@version    $Revision: 1.6 $
 *@since      2.2
-->

<script language="JavaScript">
<!-- //
    function viewOrder(form) {
        if (form.taskStatus.value == "WF_NOT_STARTED") {
        	if (form.delegate.checked) {
            	form.action = "<@ofbizUrl>/acceptassignment</@ofbizUrl>";
            } else {
            	form.action = "<@ofbizUrl>/orderview</@ofbizUrl>";
            }	
        } else {
        	if (form.delegate.checked) {
            	form.action = "<@ofbizUrl>/delegateassignment</@ofbizUrl>";
        	} else {
            	form.action = "<@ofbizUrl>/orderview</@ofbizUrl>";
        	}
        }
        form.submit();
    }
// -->
</script>

<#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session) || security.hasRolePermission("ORDERMGR_ROLE", "_VIEW", "", "", session)>
<#assign tasksFound = false>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='70%' >
            <div class='boxhead'>&nbsp;Orders Needing Attention</div>
          </td>             
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>        
          <td width='100%'>  
            <#if poList?has_content>
              <#assign tasksFound = true>
              <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
                <tr>
                  <td>
                    <div class='head3'>Purchase Orders To Be Scheduled</div>
                    <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                      <tr>
                        <td><div class="tableheadtext">Order&nbsp;Number</div></td>
                        <td><div class="tableheadtext">Name</div></td>
                        <td><div class="tableheadtext">Order&nbsp;Date</div></td>
                        <td><div class="tableheadtext">Status</div></td>
                        <td width="1" align="right"><div class="tableheadtext">Items</div></td>
                        <td width="1" align="right"><div class="tableheadtext">Total</div></td>
                        <td width="1">&nbsp;&nbsp;</td>
                        <td width="1">&nbsp;&nbsp;</td>
                      </tr>
                      <tr><td colspan='8'><hr class='sepbar'></td></tr>
                      <#list poList as orderHeaderAndRole>
                        <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeaderAndRole)>
                        <#assign statusItem = orderHeaderAndRole.getRelatedOneCache("StatusItem")>
                        <#assign placingParty = orh.getPlacingParty()?if_exists>
                        <tr>
                          <td><a href="<@ofbizUrl>/orderview?order_id=${orderHeaderAndRole.orderId}</@ofbizUrl>" class='buttontext'>${orderHeaderAndRole.orderId}</a></td>                          
                          <td>
                            <div class='tabletext'>
                              <#assign partyId = "_NA_">
                              <#if placingParty?has_content>
                                <#assign partyId = placingParty.partyId>
                                <#if placingParty.getEntityName() == "Person">
                                  <#if placingParty.lastName?exists>
                                    ${placingParty.lastName}<#if placingParty.firstName?exists>, ${placingParty.firstName}</#if>
                                  <#else>
                                    N/A
                                  </#if>
                                <#else>
                                  <#if placingParty.groupName?exists>
                                    ${placingParty.groupName}
                                  <#else>
                                    N/A
                                  </#if>
                                </#if>
                              <#else>
                                N/A
                              </#if>
                            </div>
                          </td>
                          <td><div class="tabletext"><nobr>${orderHeaderAndRole.getString("orderDate")}</nobr></div></td>
                          <td><div class="tabletext">${statusItem.description?default(statusItem.statusId?default("N/A"))}</div></td>
                          <td align="right"><div class="tabletext">${orh.getTotalOrderItemsQuantity()?string.number}</div></td>
                          <td align="right"><div class="tabletext"><@ofbizCurrency amount=orh.getOrderGrandTotal() isoCode=orderHeaderAndRole.currencyUom?if_exists/></div></td>
                          <td width="1">&nbsp;&nbsp;</td>
                          <td align='right'>
                            <a href="<@ofbizUrl>/OrderDeliveryScheduleInfo?orderId=${orderHeaderAndRole.orderId}</@ofbizUrl>" class='buttontext'>Schedule&nbsp;Delivery</a>
                          </td>                       
                        </tr>
                      </#list>
                    </table>
                  </td>
                </tr>
              </table>
            </#if>
                              
            <#if partyTasks?has_content>
              <#assign tasksFound = true>
              <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
                <tr>
                  <td>
                    <div class='head3'>Workflow Activities Assigned to User</div>
                    <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                      <tr>
                        <td><a href="<@ofbizUrl>/tasklist?sort=orderId</@ofbizUrl>" class="tableheadbutton">Order&nbsp;Number</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=name</@ofbizUrl>" class="tableheadbutton">Name</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=orderDate</@ofbizUrl>" class="tableheadbutton">Order&nbsp;Date</a></td>
                        <td width="1" align="right"><a href="<@ofbizUrl>/tasklist?sort=grandTotal</@ofbizUrl>" class="tableheadbutton">Total</a></td>
                        <td width="1">&nbsp;&nbsp;</td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=actualStartDate</@ofbizUrl>" class="tableheadbutton">Start&nbsp;Date/Time</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=priority</@ofbizUrl>" class="tableheadbutton">Priority</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=currentStatusId</@ofbizUrl>" class="tableheadbutton">My&nbsp;Status</a></td>
                      </tr>
                      <tr><td colspan='8'><hr class='sepbar'></td></tr>
                      <#list partyTasks as task>
                        <tr>
                          <td>               
                            <#assign orderStr = "order_id=" + task.orderId + "&partyId=" + userLogin.partyId + "&roleTypeId=" + task.roleTypeId + "&workEffortId=" + task.workEffortId + "&fromDate=" + task.get("fromDate").toString()>           
                            <a href="<@ofbizUrl>/orderview?${orderStr}</@ofbizUrl>" class="buttontext">
                              ${task.orderId}
                            </a>
                          </td>
                          <td>
                            <div class="tabletext">
                              <#if task.customerPartyId?exists>
                                <a href="/partymgr/control/viewprofile?party_id=${task.customerPartyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${Static["org.ofbiz.order.task.TaskWorker"].getCustomerName(task)}</a>
                              <#else>
                                N/A
                              </#if>
                            </div>
                          </td>
                          <td>
                            <div class="tabletext">
                              ${task.get("orderDate").toString()}
                            </div>
                          </td>  
                          <td width="1" align="right"><div class='tabletext'><@ofbizCurrency amount=task.grandTotal isoCode=orderCurrencyMap.get(task.orderId)/></div></td>
                          <td width="1">&nbsp;&nbsp;</td>
                          <td>
                            <#if task.actualStartDate?exists>
                              <#assign actualStartDate = task.get("actualStartDate").toString()>
                            <#else>
                              <#assign actualStartDate = "N/A">
                            </#if>                                
                            <div class='tabletext'>${actualStartDate}</div>
                          </td>          
                          <td><div class='tabletext'>${task.priority?default("0")}</div></td>
                          <td>
                            <a href="/workeffort/control/activity?workEffortId=${task.workEffortId}${requestAttributes.externalKeyParam}" target="workeffort" class="buttontext">                         
                              ${Static["org.ofbiz.order.task.TaskWorker"].getPrettyStatus(task)}
                            </a>
                          </td>
                        </tr>
                      </#list>
                    </table>
                  </td>
                </tr>
              </table>
            </#if> 
            
            <#if roleTasks?has_content>
              <#assign tasksFound = true>
              <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
                <tr>
                  <td>
                    <div class='head3'>Workflow Activities Assigned to User Role</div>
                    <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                      <tr>
                        <td><a href="<@ofbizUrl>/tasklist?sort=orderId</@ofbizUrl>" class="tableheadbutton">Order&nbsp;Number</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=name</@ofbizUrl>" class="tableheadbutton">Name</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=orderDate</@ofbizUrl>" class="tableheadbutton">Order&nbsp;Date</a></td>                                  
                        <td width="1" align="right"><a href="<@ofbizUrl>/tasklist?sort=grandTotal</@ofbizUrl>" class="tableheadbutton">Total</a></td>
                        <td width="1">&nbsp;&nbsp;</td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=actualStartDate</@ofbizUrl>" class="tableheadbutton">Start&nbsp;Date/Time</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=wepaPartyId</@ofbizUrl>" class="tableheadbutton">Party</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=roleTypeId</@ofbizUrl>" class="tableheadbutton">Role</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=priority</@ofbizUrl>" class="tableheadbutton">Priority</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=currentStatusId</@ofbizUrl>" class="tableheadbutton">Status</a></td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr><td colspan='11'><hr class='sepbar'></td></tr>
                      <#list roleTasks as task>
                        <form method="get" name="F${task.workEffortId}">
                          <input type="hidden" name="order_id" value="${task.orderId}">
                          <input type="hidden" name="workEffortId" value="${task.workEffortId}">
                          <input type="hidden" name="taskStatus" value="${task.currentStatusId}">                    
                          <#if task.statusId?exists && task.statusId == "CAL_SENT">
                            <input type="hidden" name="partyId" value="${userLogin.partyId}">
                            <input type="hidden" name="roleTypeId" value="${task.roleTypeId}">
                            <input type="hidden" name="fromDate" value="${task.get("fromDate").toString()}">
                          <#else>                          
                            <input type="hidden" name="partyId" value="${userLogin.partyId}">
                            <input type="hidden" name="roleTypeId" value="${task.roleTypeId}">
                            <input type="hidden" name="fromDate" value="${task.get("fromDate").toString()}">
                            <input type="hidden" name="fromPartyId" value="${task.wepaPartyId}">
                            <input type="hidden" name="fromRoleTypeId" value="${task.roleTypeId}">
                            <input type="hidden" name="fromFromDate" value="${task.get("fromDate").toString()}">  
                            <input type="hidden" name="toPartyId" value="${userLogin.partyId}">
                            <input type="hidden" name="toRoleTypeId" value="${task.roleTypeId}">
                            <input type="hidden" name="toFromDate" value="${now}">
                            <input type="hidden" name="startActivity" value="true">
                          </#if>
                          <tr>
                            <td>                        
                              <a href="javascript:viewOrder(document.F${task.workEffortId});" class="buttontext">
                                ${task.orderId}
                              </a>
                            </td>
                            <td>
                              <#if task.customerPartyId?exists>
                              <a href="/partymgr/control/viewprofile?party_id=${task.customerPartyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${Static["org.ofbiz.order.task.TaskWorker"].getCustomerName(task)}</a>
                              <#else>
                              &nbsp;
                              </#if>
                            </td>
                            <td>
                              <div class="tabletext">
                                ${task.get("orderDate").toString()}
                              </div>
                            </td> 
                            <td width="1" align="right"><div class='tabletext'><@ofbizCurrency amount=task.grandTotal isoCode=orderCurrencyMap.get(task.orderId)/></div></td>
                            <td width="1">&nbsp;&nbsp;</td>
                            <td>
                              <#if task.actualStartDate?exists>
                                <#assign actualStartDate = task.get("actualStartDate").toString()>
                              <#else>
                                <#assign actualStartDate = "N/A">
                              </#if>                                
                              <div class='tabletext'>${actualStartDate}</div>
                            </td>                                                      
                            <td>
                              <#if task.wepaPartyId == "_NA_">
                                <div class="tabletext">N/A</div>
                              <#else>                              
                                <a href="/partymgr/control/viewprofile?party_id=${task.wepaPartyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${task.wepaPartyId}</a>
                              </#if>
                            </td>  
                            <td><div class='tabletext'>${Static["org.ofbiz.order.task.TaskWorker"].getRoleDescription(task)}</div></td>
                            <td><div class='tabletext'>${task.priority?default("0")}</div></td>
                            <td>
                              <a href="/workeffort/control/activity?workEffortId=${task.workEffortId}" target="workeffort" class="buttontext">
                                ${Static["org.ofbiz.order.task.TaskWorker"].getPrettyStatus(task)}
                              </a>
                            </td>
                            <#if task.statusId?exists && task.statusId == "CAL_SENT">
                              <td align="right"><input type="checkbox" name="delegate" value="true" checked></td>
                            <#else>
                              <td align="right"><input type="checkbox" name="delegate" value="true"></td>
                            </#if>
                          </tr>
                        </form>
                      </#list>
                    </table>
                  </td>
                </tr>
              </table>  
            </#if>
            <#if !tasksFound>
              <div class="tabletext">No tasks currently require your attention.</div>
            </#if>                                                                       
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table> 
<#else>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>         
</#if>