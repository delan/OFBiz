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

<#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
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
            <#if partyTasks?has_content>
              <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
                <tr>
                  <td>
                    <div class='head3'>Workflow Activities Assigned to User</div>
                    <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                      <tr>
                        <td><a href="<@ofbizUrl>/tasklist?sort=orderId</@ofbizUrl>" class="tableheadbutton">Order&nbsp;Number</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=name</@ofbizUrl>" class="tableheadbutton">Customer&nbsp;Name</a></td>
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
                            <a href="/partymgr/control/viewprofile?party_id=${task.customerPartyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${Static["org.ofbiz.ordermgr.task.TaskWorker"].getCustomerName(task)}</a>
                          </td>
                          <td>
                            <div class="tabletext">
                              ${task.get("orderDate").toString()}
                            </div>
                          </td>  
                          <td width="1" align="right"><div class='tabletext'>${task.grandTotal?string.currency}</div></td>
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
                              ${Static["org.ofbiz.ordermgr.task.TaskWorker"].getPrettyStatus(task)}
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
              <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
                <tr>
                  <td>
                    <div class='head3'>Workflow Activities Assigned to User Role</div>
                    <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                      <tr>
                        <td><a href="<@ofbizUrl>/tasklist?sort=orderId</@ofbizUrl>" class="tableheadbutton">Order&nbsp;Number</a></td>
                        <td><a href="<@ofbizUrl>/tasklist?sort=name</@ofbizUrl>" class="tableheadbutton">Customer&nbsp;Name</a></td>
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
                          <#if task.statusId == "CAL_SENT">
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
                              <a href="/partymgr/control/viewprofile?party_id=${task.customerPartyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${Static["org.ofbiz.ordermgr.task.TaskWorker"].getCustomerName(task)}</a>
                            </td>
                            <td>
                              <div class="tabletext">
                                ${task.get("orderDate").toString()}
                              </div>
                            </td> 
                            <td width="1" align="right"><div class='tabletext'>${task.grandTotal?string.currency}</div></td>
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
                            <td><div class='tabletext'>${Static["org.ofbiz.ordermgr.task.TaskWorker"].getRoleDescription(task)}</div></td>
                            <td><div class='tabletext'>${task.priority?default("0")}</div></td>
                            <td>
                              <a href="/workeffort/control/activity?workEffortId=${task.workEffortId}" target="workeffort" class="buttontext">
                                ${Static["org.ofbiz.ordermgr.task.TaskWorker"].getPrettyStatus(task)}
                              </a>
                            </td>
                            <#if task.statusId == "CAL_SENT">
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
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table> 
<#else>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>         
</#if>