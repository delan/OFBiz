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
 *@version    $Rev:$
 *@since      2.2
-->

<#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
  <p class="head1">Receive Offline Payment(s)</p>

  &nbsp;<a href="<@ofbizUrl>/authview/${donePage}</@ofbizUrl>" class="buttontext">[Go&nbsp;Back]</a>
  &nbsp;<a href="javascript:document.paysetupform.submit()" class="buttontext">[Save]</a>

  <form method="post" action="<@ofbizUrl>/receiveOfflinePayments/${donePage}</@ofbizUrl>" name="paysetupform">    
    <input type="hidden" name="orderId" value="${requestParameters.order_id}">
    <#if requestParameters.workEffortId?exists>
    	<input type="hidden" name="workEffortId" value="${requestParameters.workEffortId}">
    </#if>
    <table width="100%" cellpadding="1" cellspacing="0" border="0">
      <tr>
        <td width="30%" align="right"><div class="tableheadtext"><u>Payment Type</u></div></td>
        <td width="1">&nbsp;&nbsp;&nbsp;</td>
        <td width="1" align="left"><div class="tableheadtext"><u>Amount</u></div></td>
        <td width="1">&nbsp;&nbsp;&nbsp;</td>
        <td width="70%" align="left"><div class="tableheadtext"><u>Reference</u></div></td>
      </tr>    
      <#list paymentMethodTypes as payType>
      <tr>
        <td width="30%" align="right"><div class="tabletext">${payType.description?default(payType.paymentMethodTypeId)}</div></td>
        <td width="1">&nbsp;&nbsp;&nbsp;</td>
        <td width="1"><input type="text" size="7" name="${payType.paymentMethodTypeId}_amount" class="inputBox"></td>
        <td width="1">&nbsp;&nbsp;&nbsp;</td>
        <td width="70%"><input type="text" size="15" name="${payType.paymentMethodTypeId}_reference" class="inputBox"></td>
      </tr>
      </#list>
    </table>
  </form>
  
  &nbsp;<a href="<@ofbizUrl>/authview/${donePage}</@ofbizUrl>" class="buttontext">[Go&nbsp;Back]</a>
  &nbsp;<a href="javascript:document.paysetupform.submit()" class="buttontext">[Save]</a>
   
<br>
<#else>
  <h3>You do not have permission to view this page. ("ORDERMGR_UPDATE" or "ORDERMGR_ADMIN" needed)</h3>
</#if>