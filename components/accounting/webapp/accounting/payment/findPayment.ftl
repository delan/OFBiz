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
 *@version    $Revision: 1.1 $
 *@since      3.0
-->

<script language="JavaScript">
<!-- //
function lookupPayment() {
    paymentIdValue = document.lookuppayment.paymentId.value;
    if (paymentIdValue.length > 1) {
        document.lookuppayment.action = "<@ofbizUrl>/editPayment</@ofbizUrl>";
    } else {
        document.lookuppayment.action = "<@ofbizUrl>/findPayment</@ofbizUrl>";
    }
    document.lookuppayment.submit();
}
// -->
</script>

<#if security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
<form method='post' name="lookuppayment" action="<@ofbizUrl>/findPayment</@ofbizUrl>">
<input type='hidden' name='lookupFlag' value='Y'>
<input type='hidden' name='hideFields' value='Y'>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td><div class='boxhead'>Find Payments</div></td>
          <td align='right'>
            <div class="tabletext">
              <#if requestParameters.hideFields?default("N") == "Y">
                <a href="<@ofbizUrl>/findPayment?hideFields=N${paramList}</@ofbizUrl>" class="submenutextright">Show Lookup Fields</a>
              <#else>
                <#if invoiceList?exists><a href="<@ofbizUrl>/findPayment?hideFields=Y${paramList}</@ofbizUrl>" class="submenutext">Hide Fields</a></#if>
                <a href="javascript:lookupPayment();" class="submenutextright">Lookup Payment(s)</a>                
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
                <td width='25%' align='right'><div class='tableheadtext'>Payment ID:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='paymentId'></td>
              </tr> 
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Payment Method Type:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='paymentMethodTypeId' class='selectBox'> 
                    <#if currentMethod?has_content>
                    <option value="${currentMethod.paymentMethodTypeId}">${currentMethod.description}</option>
                    <option value="${currentMethod.paymentMethodTypeId}">---</option>
                    </#if>                                     
                    <option value="ANY">Any Payment Method</option>                   
                    <#list paymentMethodTypes as paymentMethodType>
                      <option value="${paymentMethodType.paymentMethodTypeId}">${paymentMethodType.description}</option>
                    </#list>
                  </select>
                </td>
              </tr>                           
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Status:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='paymentStatusId' class='selectBox'> 
                    <#if currentStatus?has_content>
                    <option value="${currentStatus.statusId}">${currentStatus.description}</option>
                    <option value="${currentStatus.statusId}">---</option>
                    </#if>                                     
                    <option value="ANY">Any Payment Status</option>                   
                    <#list paymentStatuses as paymentStatus>
                      <option value="${paymentStatus.statusId}">${paymentStatus.description}</option>
                    </#list>
                  </select>
                </td>
              </tr> 
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>From Party:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='fromPartyId' value='${fromPartyId?if_exists}'></td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>To Party:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='toPartyId' value='${toPartyId?if_exists}'></td>
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
                        <a href="javascript:call_cal(document.lookupinvoice.minDate, '${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                        <span class='tabletext'>From</span>
                      </td>
                    </tr>
                    <tr>                  
                      <td>                       
                        <input type='text' size='25' class='inputBox' name='maxDate' value='${requestParameters.maxDate?if_exists}'>
                        <a href="javascript:call_cal(document.lookupinvoice.maxDate, '${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>                   
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
      </#if>
    </td>
  </tr>
</table>
<input type="image" src="/images/spacer.gif" onClick="javascript:lookupPayment();">
</form> 
<script language="JavaScript">
<!--//
document.lookuppayment.paymentId.focus();
//-->
</script>

<#if paymentList?exists>
<br>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="50%"><div class="boxhead">Payment(s) Found</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <#if 0 < paymentList?size>             
                <#if 0 < viewIndex>
                  <a href="<@ofbizUrl>/findPayment?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutext">Previous</a>
                <#else>
                  <span class="submenutextdisabled">Previous</span>
                </#if>
                <#if 0 < listSize>
                  <span class="submenutextinfo">${lowIndex+1} - ${highIndex} of ${listSize}</span>
                </#if>
                <#if highIndex < listSize>
                  <a href="<@ofbizUrl>/findPayment?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutextright">Next</a>
                <#else>
                  <span class="submenutextrightdisabled">Next</span>
                </#if>
              </#if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td align="left"><div class="tableheadtext">Payment #</div></td>
          <td align="left"><div class="tableheadtext">Type</div></td>
          <td align="left"><div class="tableheadtext">Method</div></td>
          <td align="left"><div class="tableheadtext">Status</div></td>
          <td align="left"><div class="tableheadtext">From Party</div></td>
          <td align="left"><div class="tableheadtext">To Party</div></td>
          <td align="left"><div class="tableheadtext">Effective</div></td>
          <td align="right"><div class="tableheadtext">Amount</div></td>
          <td width="5%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan='10'><hr class='sepbar'></td>
        </tr>
        <#if paymentList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list paymentList[lowIndex..highIndex-1] as payment>            
            <#assign statusItem = payment.getRelatedOne("StatusItem")?if_exists>
            <#assign paymentType = payment.getRelatedOne("PaymentType")>
            <#assign paymentMethodType = payment.getRelatedOne("PaymentMethodType")?if_exists>            
            <tr class='${rowClass}'>
              <td><a href="<@ofbizUrl>/editPayment?paymentId=${payment.paymentId}</@ofbizUrl>" class='buttontext'>${payment.paymentId}</a></td>            
              <td><div class='tabletext'>${paymentType.description?default(paymentType.paymentTypeId?default(""))}</div></td>
              <td><div class='tabletext'>${paymentMethodType.description?if_exists}</div></td>
              <td><div class='tabletext'>${statusItem.description?default("Not Set")}</div></td>
              <td><div class='tabletext'>${payment.partyIdFrom}</div></td>
              <td><div class='tabletext'>${payment.partyIdTo}</div></td>
              <td><div class='tabletext'>${(payment.effectiveDate?string)?if_exists}</div></td>
              <td align='right'><div class='tabletext'>${payment.amount?string.currency}</div></td>
              <td align='right'><a href="<@ofbizUrl>/editPayment?paymentId=${payment.paymentId}</@ofbizUrl>" class='buttontext'>Edit</a></td>
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
            <td colspan='4'><div class='head3'>No payments found.</div></td>
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
  <h3>You do not have permission to view this page. ("ACCOUNTING_VIEW" or "ACCOUNTING_ADMIN" needed)</h3>
</#if>
