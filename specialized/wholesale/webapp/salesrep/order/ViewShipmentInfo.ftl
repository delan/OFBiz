<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@author     Si Chen (sichen@sinfoniasolutions.com)
 *@version    $Revision$
 *@since      3.0
-->
  <#assign uiLabelMap = requestAttributes.uiLabelMap>
  <#if shipment?exists>
    <table border="0" cellpadding="2" cellspacing="0">
      <tr>
        <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.ProductShipmentId}:</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left"><span class="tabletext">${shipment.shipmentId}</span></td>
      </tr>     
      <tr>
        <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.ProductOrigin}:</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left">
          <div class="tabletext"><#if originPostalAddress?has_content>${originPostalAddress.toName?if_exists}, ${uiLabelMap.CommonAttn} : ${originPostalAddress.attnName?if_exists}, ${originPostalAddress.address1?if_exists}, ${originPostalAddress.address2?if_exists}, ${originPostalAddress.city?if_exists}, ${originPostalAddress.stateProvinceGeoId?if_exists}, ${originPostalAddress.postalCode?if_exists}, ${originPostalAddress.countryGeoId?if_exists}</#if></div>
        </td>
      </tr>  
      <tr>
        <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.ProductDestination}:</span></td>
        <td><span class="tabletext">&nbsp;</span></td>
        <td width="80%" align="left">
          <div class="tabletext"><#if destinationPostalAddress?has_content>${destinationPostalAddress.toName?if_exists}, ${uiLabelMap.CommonAttn} : ${destinationPostalAddress.attnName?if_exists}, ${destinationPostalAddress.address1?if_exists}, ${destinationPostalAddress.address2?if_exists}, ${destinationPostalAddress.city?if_exists}, ${destinationPostalAddress.stateProvinceGeoId?if_exists}, ${destinationPostalAddress.postalCode?if_exists}, ${destinationPostalAddress.countryGeoId?if_exists}</#if></div>
        </td>
      </tr>
    </table>  
  </#if>
