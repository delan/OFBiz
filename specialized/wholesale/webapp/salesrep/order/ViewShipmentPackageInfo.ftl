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
<#if shipmentPackageDatas?has_content>
  <table width="100%" cellspacing="0" cellpadding="2" border="1">
    <tr>
      <td><div class="tableheadtext">${uiLabelMap.ProductPackage}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.CommonCreated}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.ProductWeight}</div></td>
      <td><div class="tableheadtext">${uiLabelMap.ProductCarrier}</div></td>
    </tr>
    <#list shipmentPackageDatas as shipmentPackageData>
      <#assign shipmentPackage = shipmentPackageData.shipmentPackage>
      <#assign shipmentPackageContents = shipmentPackageData.shipmentPackageContents?if_exists>
      <#assign shipmentPackageRouteSegsData = shipmentPackageData.shipmentPackageRouteSegsData?if_exists>
      <#assign weightUom = shipmentPackageData.weightUom?if_exists>
      <tr>
        <td width=7%><div class="tabletext">${shipmentPackage.shipmentPackageSeqId}</div></td>
        <td width=15%><div class="tabletext">${(shipmentPackage.dateCreated.toString())?if_exists}</div></td>
        <td width=10%><span class="tabletext">${shipmentPackage.weight?if_exists} ${weightUom.description?default(shipmentPackage.weightUomId?if_exists)}</span></td>
        <td>
			<table border="0">
				<#list shipmentPackageRouteSegsData as shipmentPackageRouteSegData>
        			<#assign shipmentPackageRouteSeg = shipmentPackageRouteSegData.shipmentPackageRouteSeg?if_exists>
        			<#assign shipmentMethodType = shipmentPackageRouteSegData.shipmentMethodType?if_exists>
        			<#assign carrierPerson = shipmentPackageRouteSegData.carrierPerson?if_exists>
        			<#assign carrierPartyGroup = shipmentPackageRouteSegData.carrierPartyGroup?if_exists>
        			<#assign shipmentRouteSegment = shipmentPackageRouteSegData.shipmentRouteSegment?if_exists>
        			<tr>
          				<td><div class="tabletext">
            				<span class="tabletext">${(carrierPerson.firstName)?if_exists} ${(carrierPerson.middleName)?if_exists} ${(carrierPerson.lastName)?if_exists} ${(carrierPartyGroup.groupName)?if_exists}</span>
            				<span class="tabletext">${shipmentMethodType.description?default(shipmentRouteSegment.shipmentMethodTypeId?if_exists)}</span>
            				<br/> 
         				</div></td>
          				<td><span class="tabletext">${shipmentPackageRouteSeg.trackingCode?if_exists}</span></td>
          				<td><span class="tabletext"><#if shipmentPackageRouteSeg.boxNumber?has_content>${uiLabelMap.ProductBox} : ${shipmentPackageRouteSeg.boxNumber}</#if></span></td>
        			</tr>
     			</#list>
      		</table>
      	</td>
      </tr>
    </#list>
  </table>
</#if>
