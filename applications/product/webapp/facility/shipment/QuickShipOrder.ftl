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
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Rev$
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<script language="JavaScript">
<!-- //
// function called from ShipmentScaleApplet when a weight is read
function setWeight(weight) {
  document.weightForm.weight.value = weight;
}
// -->
</script>

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
  <div class="head1">${uiLabelMap.ProductQuickShipOrderFrom}<span class='head2'>${facility.facilityName?if_exists} [${uiLabelMap.CommonId}:${facilityId?if_exists}]</span></div>
  <a href="<@ofbizUrl>/quickShipOrder?facilityId=${facilityId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNextShipment}]</a>
  <#if shipment?has_content>
    <a href="<@ofbizUrl>/EditShipment?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductEditShipment}]</a>
  </#if>
  <br><br>
  
  <#if shipment?exists>   
    <#if 1 < shipmentPackages.size()>
      <#-- multiple packages -->
      <div class="tabletext"><font color="red">${uiLabelMap.ProductMorePackageFoundShipment}.</font></div>
    <#else>
      <#-- single package -->            
      <#assign shipmentPackage = (Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipmentPackages))?if_exists>
      <#if shipmentPackage?has_content>
        <#assign weight = (shipmentPackage.weight)?default(0.00)>
        <#if (0 < weight?double) && !requestParameters.reweigh?exists>
          <#if 1 < shipmentRoutes.size()>
            <#-- multiple routes -->
            <div class="tabletext"><font color="red">${uiLabelMap.ProductMoreRouteSegmentFound}.</font></div>
          <#elseif !requestParameters.shipmentRouteSegmentId?exists || requestAttributes._ERROR_MESSAGE_?exists>
            <form name="routeForm" method="post" action="<@ofbizUrl>/setQuickRouteInfo</@ofbizUrl>" style='margin: 0;'>
              <#assign shipmentRoute = (Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipmentRoutes))?if_exists>
              <#assign carrierPerson = (shipmentRoute.getRelatedOne("CarrierPerson"))?if_exists>
              <#assign carrierPartyGroup = (shipmentRoute.getRelatedOne("CarrierPartyGroup"))?if_exists>
              <#assign shipmentMethodType = (shipmentRoute.getRelatedOne("ShipmentMethodType"))?if_exists>
              <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
              <input type="hidden" name="shipmentId" value="${shipmentRoute.shipmentId}">
              <input type="hidden" name="shipmentRouteSegmentId" value="${shipmentRoute.shipmentRouteSegmentId}"/>
              <table border="0" cellpadding="2" cellspacing="0">
                <tr>
                  <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.ProductCarrier}</span></td>
                  <td><span class="tabletext">&nbsp;</span></td>
                  <td width="1%" align="left" nowrap>
                    <select name="carrierPartyId" class="selectBox">
                      <#if shipmentRoute.carrierPartyId?has_content>
                        <option value="${shipmentRoute.carrierPartyId}">${(carrierPerson.firstName)?if_exists} ${(carrierPerson.middleName)?if_exists} ${(carrierPerson.lastName)?if_exists} ${(carrierPartyGroup.groupName)?if_exists} [${shipmentRoute.carrierPartyId}]</option>
                        <option value="${shipmentRoute.carrierPartyId}">---</option>
                      <#else>
                        <option value="">&nbsp;</option>
                      </#if>
                      <#list carrierPartyDatas as carrierPartyData>
                        <option value="${carrierPartyData.party.partyId}">${(carrierPartyData.person.firstName)?if_exists} ${(carrierPartyData.person.middleName)?if_exists} ${(carrierPartyData.person.lastName)?if_exists} ${(carrierPartyData.partyGroup.groupName)?if_exists} [${carrierPartyData.party.partyId}]</option>
                      </#list>
                    </select>                    
                  </td>
                  <td><span class="tabletext">&nbsp;</span></td>
                  <td width="80%">                                       
                    <a href="javascript:document.routeForm.submit();" class="buttontext">${uiLabelMap.ProductConfirmShipmentUps}</a>
                  </td>
                </tr>              
                <tr>
                  <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.ProductShipMethod}</span></td>
                  <td><span class="tabletext">&nbsp;</span></td>
                  <td width="1%" align="left" nowrap>
                    <select name="shipmentMethodTypeId" class="selectBox">
                      <#if shipmentMethodType?has_content>
                        <option value="${shipmentMethodType.shipmentMethodTypeId}">${shipmentMethodType.description}</option>
                        <option value="${shipmentMethodType.shipmentMethodTypeId}">---</option>
                      <#else>
                        <option value="">&nbsp;</option>
                      </#if>
                      <#list shipmentMethodTypes as shipmentMethodTypeOption>
                        <option value="${shipmentMethodTypeOption.shipmentMethodTypeId}">${shipmentMethodTypeOption.description}</option>
                      </#list>
                    </select>              
                  </td>
                  <td><span class="tabletext">&nbsp;</span></td>
                  <td width="80%">
                    <a href="<@ofbizUrl>/quickShipOrder?facilityId=${facilityId}&shipmentId=${shipmentId}&reweigh=Y</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductReWeighPackage}</a>                  
                  </td>
                </tr>
                <tr>
                  <td width="20%" align="right"><span class="tableheadtext">&nbsp;</span></td>
                  <td><span class="tabletext">&nbsp;</span></td>
                  <td width="1%" align="left" nowrap>
                    &nbsp;
                  </td>
                  <td><span class="tabletext">&nbsp;</span></td>
                  <td width="80%">
                    <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:document.routeForm.submit();">  
                  </td>
                </tr>                
              </table>              
            </form>
            <script language="javascript">
            <!-- //
              document.routeForm.carrierPartyId.focus();
            // -->
            </script>
          <#else>
            <#-- display the links for label/packing slip -->
            <#assign allDone = "yes">
            <center>
              <a href="<@ofbizUrl>/viewShipmentPackageRouteSegLabelImage?shipmentId=${requestParameters.shipmentId}&shipmentRouteSegmentId=${requestParameters.shipmentRouteSegmentId}&shipmentPackageSeqId=00001</@ofbizUrl>" target="_blank" class="buttontext">${uiLabelMap.ProductShippingLabel}</a><br>
              <a href="<@ofbizUrl>/ShipmentManifestReport.pdf?shipmentId=${requestParameters.shipmentId}&shipmentRouteSegmentId=${requestParameters.shipmentRouteSegmentId}</@ofbizUrl>" target="_blank" class="buttontext">${uiLabelMap.ProductPackingSlip}</a>
            </center>                
          </#if>
        <#else>
          <form name="weightForm" method="post" action="<@ofbizUrl>/setQuickPackageWeight</@ofbizUrl>" style='margin: 0;'>
            <#assign weightUom = shipmentPackage.getRelatedOne("WeightUom")?if_exists>
            <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
            <input type="hidden" name="shipmentId" value="${shipmentPackage.shipmentId}">
            <input type="hidden" name="shipmentPackageSeqId" value="${shipmentPackage.shipmentPackageSeqId}">
            <table border="0" cellpadding="2" cellspacing="0">
              <tr>
                <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.ProductPackage} ${shipmentPackage.shipmentPackageSeqId} ${uiLabelMap.ProductWeight}</span></td>
                <td><span class="tabletext">&nbsp;</span></td>
                <td width="80%" align="left">
                  <input type="text" class="inputBox" name="weight">&nbsp;
                  <select name="weightUomId" class="selectBox">
                    <#if weightUom?has_content>
                      <option value="${weightUom.uomId}">${weightUom.description}</option>
                      <option value="${weightUom.uomId}">---</option>
                    </#if>                              
                    <#list weightUomList as weightUomOption>
                      <option value="${weightUomOption.uomId}">${weightUomOption.description} [${weightUomOption.abbreviation}]</option>
                    </#list>
                  </select>    
                </td>
              </tr>
              <tr>
                <td colspan="2">&nbsp;</td>
                <td width="80%" align="left">
                  <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:document.weightForm.submit();">
                  <a href="javascript:document.weightForm.submit();" class="buttontext">${uiLabelMap.ProductSetWeight}</a>
                </td>
              </tr>
            </table>
          </form>
          <script language="javascript">
          <!-- // 
            document.weightForm.weight.focus();
          // -->
          </script>           
          <#-- todo embed the applet
          <applet code="ShipmentScaleApplet.class" codebase="/images/" name="Package Weight Reader" width="0" height="0" MAYSCRIPT>
            <param name="serialPort" value="com1">
            <param name="fakeWeight" value="22">
          </applet>
          -->
        </#if>
      <#else>
        <div class="tabletext"><font color="red">${uiLabelMap.ProductErrorNoPackagesFoundForShipment} !</font></div>
      </#if>      
      <hr class="sepbar">
      ${pages.get("/shipment/ViewShipmentInfo.ftl")}         
      <br>${pages.get("/shipment/ViewShipmentItemInfo.ftl")}
      <br>${pages.get("/shipment/ViewShipmentPackageInfo.ftl")}
      <#if allDone?default("no") == "yes">
        <br>${pages.get("/shipment/ViewShipmentRouteInfo.ftl")}
      </#if>
    </#if>
  <#else>
    <form name="selectOrderForm" method="post" action="<@ofbizUrl>/createQuickShipment</@ofbizUrl>" style='margin: 0;'>
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
      <input type="hidden" name="originFacilityId" value="${facilityId?if_exists}">
      <input type="hidden" name="setPackedOnly" value="Y">
      <table border='0' cellpadding='2' cellspacing='0'>	  
        <tr>        
          <td width="25%" align='right'><div class="tabletext">${uiLabelMap.ProductOrderNumber}</div></td>
          <td width="1">&nbsp;</td>
          <td width="25%">
            <input type="text" class="inputBox" name="orderId" size="20" maxlength="20" value="${requestParameters.orderId?if_exists}">          
          </td> 
          <td><div class='tabletext'>&nbsp;</div></td>
        </tr>      
        <tr>
          <td colspan="2">&nbsp;</td>
          <td colspan="2">
            <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onClick="javascript:document.selectOrderForm.submit();">
            <a href="javascript:document.selectOrderForm.submit();" class="buttontext">${uiLabelMap.ProductShipOrder}</a>
          </td>
        </tr>        
      </table>
    </form>
    <script language="javascript">
    <!-- //
        document.selectOrderForm.orderId.focus();
    // -->
    </script>
  </#if>  
</#if>
