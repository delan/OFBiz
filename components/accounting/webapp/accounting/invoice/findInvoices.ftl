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
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap)
 *@version    $Rev:$
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<script language="JavaScript">
<!-- //
function lookupInvoices() {
    invoiceIdValue = document.lookupinvoice.invoiceId.value;
    if (invoiceIdValue.length > 1) {
        document.lookupinvoice.action = "<@ofbizUrl>/viewInvoice</@ofbizUrl>";
    } else {
        document.lookupinvoice.action = "<@ofbizUrl>/findInvoices</@ofbizUrl>";
    }
    document.lookupinvoice.submit();
}
// -->
</script>

<#if security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
<form method='post' name="lookupinvoice" action="<@ofbizUrl>/findInvoices</@ofbizUrl>">
<input type='hidden' name='lookupFlag' value='Y'>
<input type='hidden' name='hideFields' value='Y'>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td><div class='boxhead'>${uiLabelMap.AccountingFindInvoices}</div></td>
          <td align='right'>
            <div class="tabletext">
              <#if requestParameters.hideFields?default("N") == "Y">
                <a href="<@ofbizUrl>/findInvoices?hideFields=N${paramList}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonShowLookupFields}</a>
              <#else>
                <#if invoiceList?exists><a href="<@ofbizUrl>/findInvoices?hideFields=Y${paramList}</@ofbizUrl>" class="submenutext">${uiLabelMap.AccountingHideFields}</a></#if>
                <a href="javascript:lookupInvoices();" class="submenutextright">${uiLabelMap.AccountingLookupInvoices }(s)</a>                
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
                <td width='25%' align='right'><div class='tableheadtext'>${uiLabelMap.AccountingInvoiceID}:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='invoiceId'></td>
              </tr>              
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>${uiLabelMap.AccountingStatus}:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='invoiceStatusId' class='selectBox'> 
                    <#if currentStatus?has_content>
                    <option value="${currentStatus.statusId}">${currentStatus.description}</option>
                    <option value="${currentStatus.statusId}">---</option>
                    </#if>                                     
                    <option value="ANY">${uiLabelMap.AccountingAnyInvoiceStatus}</option>                   
                    <#list invoiceStatuses as invoiceStatus>
                      <option value="${invoiceStatus.statusId}">${invoiceStatus.description}</option>
                    </#list>
                  </select>
                </td>
              </tr>                            
              <tr>
                <td width='25%' align='right'>
                  <div class='tableheadtext'>${uiLabelMap.AccountingDateFilter}:</div>
                </td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <table border='0' cellspacing='0' cellpadding='0'>
                    <tr>                      
                      <td>                        
                        <input type='text' size='25' class='inputBox' name='minDate' value='${requestParameters.minDate?if_exists}'>
                        <a href="javascript:call_cal(document.lookupinvoice.minDate, '${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                        <span class='tabletext'>${uiLabelMap.CommonFrom}</span>
                      </td>
                    </tr>
                    <tr>                  
                      <td>                       
                        <input type='text' size='25' class='inputBox' name='maxDate' value='${requestParameters.maxDate?if_exists}'>
                        <a href="javascript:call_cal(document.lookupinvoice.maxDate, '${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>                   
                        <span class='tabletext'>${uiLabelMap.CommonThru}</span>
                      </td>
                    </tr>
                  </table>
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
<input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:lookupInvoices();">
</form> 
<script language="JavaScript">
<!--//
document.lookupinvoice.invoiceId.focus();
//-->
</script>

<#if invoiceList?exists>
<br>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="50%"><div class="boxhead">${uiLabelMap.AccountingInvoicesFound}</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <#if 0 < invoiceList?size>             
                <#if 0 < viewIndex>
                  <a href="<@ofbizUrl>/findInvoices?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutext">${uiLabelMap.CommonPrevious}</a>
                <#else>
                  <span class="submenutextdisabled">${uiLabelMap.CommonPrevious}</span>
                </#if>
                <#if 0 < listSize>
                  <span class="submenutextinfo">${lowIndex+1} - ${highIndex} of ${listSize}</span>
                </#if>
                <#if highIndex < listSize>
                  <a href="<@ofbizUrl>/findInvoices?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonNext}</a>
                <#else>
                  <span class="submenutextrightdisabled">${uiLabelMap.CommonNext}</span>
                </#if>
              </#if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td width="5%" align="left"><div class="tableheadtext">${uiLabelMap.AccountingType}</div></td>
          <td width="5%" align="left"><div class="tableheadtext">${uiLabelMap.AccountingInvoice} #</div></td>
          <td width="10%" align="right"><div class="tableheadtext">${uiLabelMap.AccountingInvoiceTotal}</div></td>
          <td width="5%" align="left"><div class="tableheadtext">&nbsp;</div></td>
          <td width="20%" align="left"><div class="tableheadtext">${uiLabelMap.AccountingStatus}</div></td>
          <td width="20%" align="left"><div class="tableheadtext">${uiLabelMap.AccountingInvoiceDate}</div></td>
          <td width="10%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan='10'><hr class='sepbar'></td>
        </tr>
        <#if invoiceList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list invoiceList[lowIndex..highIndex-1] as invoice>            
            <#assign statusItem = invoice.getRelatedOneCache("StatusItem")>
            <#assign invoiceType = invoice.getRelatedOneCache("InvoiceType")>
            <#assign invoiceTotal = Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceTotal(invoice)>        
            <tr class='${rowClass}'>
              <td><div class='tabletext'>${invoiceType.description?default(invoiceType.invoiceTypeId?default(""))}</div></td>
              <td><a href="<@ofbizUrl>/viewInvoice?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class='buttontext'>${invoice.invoiceId}</a></td>
              
              <td align="right"><div class="tabletext">${invoiceTotal?default(0.00)?string.currency}</div></td>
              <td>&nbsp;</td>
              <td><div class="tabletext">${statusItem.description?default(statusItem.statusId?default("N/A"))}</div></td>
              <td><div class="tabletext"><nobr>${invoice.invoiceDate?default("N/A").toString()}</nobr></div></td>                            
              <td align='right'>
                <a href="<@ofbizUrl>/viewInvoice?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class='buttontext'>${uiLabelMap.CommonView}</a>
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
            <td colspan='4'><div class='head3'>${uiLabelMap.AccountingNoInvoicesFound}.</div></td>
          </tr>        
        </#if>
        <#if lookupErrorMessage?exists>
          <tr>
            <td colspan='4'><div class="head3">${lookupErrorMessage}</div></td>
          </tr>
        </#if>
      </table>
    </td>
  </tr>
</table>
        
</#if> 
<#else>
  <h3>${uiLabelMap.AccountingViewPermissionError}</h3>
</#if>
