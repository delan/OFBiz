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

<div class='tabContainer'>
    <a href="<@ofbizUrl>/returnMain?returnId=${returnId?if_exists}</@ofbizUrl>" class="tabButtonSelected">Return</a>  
    <a href="<@ofbizUrl>/returnItems?returnId=${returnId?if_exists}<#if requestParameters.orderId?exists>&orderId=${requestParameters.orderId}</#if></@ofbizUrl>" class="tabButton">Items</a>
    <a href="<@ofbizUrl>/returnRefund?returnId=${returnId?if_exists}</@ofbizUrl>" class="tabButton">Refund</a>
</div>

<form name="returnhead">
<table border='0' cellpadding='2' cellspacing='0'>
  <#if returnId?exists>
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Return ID:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <b>${returnId}</b>
    </td>                
  </tr>
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Entry Date:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <input type='text' class='inputBox' size='25' name='entryDate' value=''>
      <a href="javascript:call_cal(document.returnhead.entryDate, '');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
    </td>                
  </tr>
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Return Status:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <select name="statusId" class="selectBox">
        <#list returnStatus as status>
          <option value="${status.statusId}">${status.description}</option>
        </#list>
      </select>
    </td>                
  </tr>   
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%'>&nbsp;</td>
    <td width='6%'><hr class="sepbar"></td>   
    <td width='74%'>&nbsp;</td>
  </tr>
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Return To Facility:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <table border='0' cellpadding='1' cellspacing='0'>
        <tr>
          <td><input type='radio' name="destinationFacilityId" value=""></td>
          <td><div class='tabletext'>No Facility</div></td>
        </tr>
        <#list facilityList as facility>
        <tr>
        <tr>
          <td><input type='radio' name="destinationFacilityId" value="${facility.facilityId}"></td>
          <td><div class='tabletext'>${facility.facilityName}</div></td>
        </tr>
        </#list>
      </table>
    </td>                
  </tr>
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%'>&nbsp;</td>
    <td width='6%'><hr class="sepbar"></td>   
    <td width='74%'>&nbsp;</td>
  </tr>       
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%' align='right' nowrap><div class="tabletext">Return From Address:</div></td>
    <td width='6%'>&nbsp;</td>
    <td width='74%'>
      <table border='0' cellpadding='1' cellspacing='0'>
        <tr>
          <td><input type='radio' name="orginContactMechId" value=""></td>
          <td><div class='tabletext'>No Address</div></td>
        </tr>       
      </table>
    </td>                
  </tr> 
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%'>&nbsp;</td>
    <td width='6%'><hr class="sepbar"></td>   
    <td width='74%'>&nbsp;</td>
  </tr>       
  <tr>
    <td width='14%'>&nbsp;</td>
    <td width='6%'>&nbsp;</td>
    <td width='6%'>&nbsp;</td>   
    <td width='74%'>
      <input type="submit" class="standardButton" value="Update">      
    </td>
  </tr>     
  </#if>
</table>