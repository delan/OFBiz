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
function setWeight(weight) {
  document.weightForm.weight = weight;
}
// -->
</script>

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
  <div class="head1">Quick Ship Order From <span class='head2'>${facility.facilityName?if_exists} [ID:${facilityId?if_exists}]</span></div>
  <#if shipment?has_content>
    <a href="<@ofbizUrl>/EditShipment?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext">[Edit Shipment]</a>
  </#if>
  <br><br>
  
  <#if shipment?exists>   
    <#if 1 < shipmentPackages.size()>
      <#-- multiple packages -->
      <div class="tabletext"><font color="red">More then one package found for this shipment. You must ship this manually.</font></div>
    <#else>
      <#-- single package -->      
      <form name="weightForm" method="post" action="<@ofbizUrl>/setQuickPackageWeight</@ofbizUrl>" style='margin: 0;'>
        <#assign shipmentPackage = (Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipmentPackages))?if_exists>
        <#if shipmentPackage?has_content>
          <#assign weight = (shipmentPackage.weight)?if_exists>
          <#assign weightUom = shipmentPackage.getRelatedOne("WeightUom")?if_exists>
          <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
          <input type="hidden" name="shipmentId" value="${shipmentPackage.shipmentId}">
          <input type="hidden" name="shipmentPackageSeqId" value="${shipmentPackage.shipmentPackageSeqId}">
          <table border="0" cellpadding="2" cellspacing="0">
            <tr>
              <td width="20%" align="right"><span class="tableheadtext">Package #${shipmentPackage.shipmentPackageSeqId} Weight</span></td>
              <td><span class="tabletext">&nbsp;</span></td>
              <td width="80%" align="left">
                <input type="text" class="inputBox" name="weight" value="${(shipmentPackage.weight)?if_exists}" onfocus="javascript:document.weightForm.weight.value=''">&nbsp;
                <select name="weightUomId" class="selectBox">
                  <#if weightUom?has_content>
                    <option value="${weightUom.uomId}">${weightUom.description}</option>
                    <option value="${weightUom.uomId}">---</option>
                  </#if>                              
                  <#list weightUoms as weightUomOption>
                    <option value="${weightUomOption.uomId}">${weightUomOption.description} [${weightUomOption.abbreviation}]</option>
                  </#list>
                </select>    
              </td>
            </tr>
            <tr>
              <td colspan="2">&nbsp;</td>
              <td width="80%" align="left">
                <input type="image" src="/images/spacer.gif" onClick="javascript:document.weightForm.submit();">
                <a href="javascript:document.weightForm.submit();" class="buttontext">Set Weight</a>
              </td>
            </tr>
          </table>
          <script language="javascript">
          <!-- // 
            document.weightForm.weight.focus();
          // -->
          </script>           
          <#-- todo embed the applet -->
        <#else>
          <div class="tabletext"><font color="red">ERROR: No packages found for this shipment!</font></div>
        </#if>
      </form>
      <hr class="sepbar">
      ${pages.get("/shipment/ViewShipmentInfo.ftl")}         
      <br>${pages.get("/shipment/ViewShipmentItemInfo.ftl")}
      <br>${pages.get("/shipment/ViewShipmentPackageInfo.ftl")}
    </#if>
  <#else>
    <form name="selectOrderForm" method="post" action="<@ofbizUrl>/createQuickShipment</@ofbizUrl>" style='margin: 0;'>
      <input type="hidden" name="facilityId" value="${facilityId?if_exists}">
      <input type="hidden" name="originFacilityId" value="${facilityId?if_exists}">
      <input type="hidden" name="setPackedOnly" value="Y">
      <table border='0' cellpadding='2' cellspacing='0'>	  
        <tr>        
          <td width="25%" align='right'><div class="tabletext">Order Number</div></td>
          <td width="1">&nbsp;</td>
          <td width="25%">
            <input type="text" class="inputBox" name="orderId" size="20" maxlength="20" value="${requestParameters.orderId?if_exists}">          
          </td> 
          <td><div class='tabletext'>&nbsp;</div></td>
        </tr>      
        <tr>
          <td colspan="2">&nbsp;</td>
          <td colspan="2">
            <input type="image" src="/images/spacer.gif" onClick="javascript:document.selectOrderForm.submit();">
            <a href="javascript:document.selectOrderForm.submit();" class="buttontext">Ship Order</a>
          </td>
        </tr>        
      </table>
    </form>
  </#if>  
</#if>
