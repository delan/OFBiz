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
 *@version    $Revision: 1.3 $
 *@since      2.2
-->

<script language="JavaScript">
<!--
function setAssocFields(select) {
    var index = select.selectedIndex;
    var opt = select.options[index];
    var optStr = opt.value;
    var optLen = optStr.length;
    
    var shipmentMethodTypeId = "";
    var sequenceNumber = "";
    var roleTypeId = "";
    var partyId = "";
    var delIdx = 1;
    
    for (i=0; i<optLen; i++) {        
        if (optStr[i] == '|') {
            delIdx++;           
        } else {
            if (delIdx == 1) {
                partyId = partyId + optStr[i];              
            } else if (delIdx == 2) {
                roleTypeId = roleTypeId + optStr[i];
            } else if (delIdx == 3) {
                shipmentMethodTypeId = shipmentMethodTypeId + optStr[i];
            } else if (delIdx == 4) {
                sequenceNumber = sequenceNumber + optStr[i];
            }
        }
    }   
        
    document.addscarr.roleTypeId.value = roleTypeId;
    document.addscarr.partyId.value = partyId;
    document.addscarr.shipmentMethodTypeId.value = shipmentMethodTypeId;
    document.addscarr.sequenceNumber.value = sequenceNumber;
}
// -->
</script>   

<#if hasPermission>
  ${pages.get("/store/ProductStoreTabBar.ftl")}
  <div class="head1">Product Store Shipment Settings <span class='head2'><#if (productStore.storeName)?has_content>"${productStore.storeName}"</#if> [ID:${productStoreId?if_exists}]</span></div>
  <a href="<@ofbizUrl>/EditProductStore</@ofbizUrl>" class="buttontext">[New Product Store]</a>
  <#if !requestParameters.createNew?exists || requestParameters.createNew != "Y">
    <a href="<@ofbizUrl>/EditProductStoreShipSetup?viewProductStoreId=${productStoreId}&createNew=Y</@ofbizUrl>" class="buttontext">[New Shipment Estimate]</a>
  </#if>
  <#if !requestParameters.newShipMethod?exists || requestParameters.newShipMethod != "Y">
    <a href="<@ofbizUrl>/EditProductStoreShipSetup?viewProductStoreId=${productStoreId}&newShipMethod=Y</@ofbizUrl>" class="buttontext">[New Shipment Method]</a>
  </#if>
  <#if requestParameters.newShipMethod?default("N") == "Y" || requestParameters.createNew?default("N") == "Y">
    <a href="<@ofbizUrl>/EditProductStoreShipSetup?viewProductStoreId=${productStoreId}</@ofbizUrl>" class="buttontext">[View Estimates]</a>
  </#if>
  <br>
  <br>   
  
  <#if !requestParameters.createNew?exists && !requestParameters.newShipMethod?exists>
    <table border="1" cellpadding="2" cellspacing="0" width="100%">
      <tr>
        <td nowrap><div class="tableheadtext">Estimate ID</div></td>
        <td nowrap><div class="tableheadtext">Method</div></td>
        <td nowrap><div class="tableheadtext">To</div></td>
        <td nowrap><div class="tableheadtext">Party</div></td>
        <td nowrap><div class="tableheadtext">Role</div></td>      
        <td nowrap><div class="tableheadtext">Base%</div></td>
        <td nowrap><div class="tableheadtext">BasePrc</div></td>
        <td nowrap><div class="tableheadtext">ItemPrc</div></td>
        <td nowrap><div class="tableheadtext">&nbsp;</div></td>
      </tr>
      <#list estimates as estimate>
        <#assign weightValue = estimate.getRelatedOne("WeightQuantityBreak")?if_exists>
        <#assign quantityValue = estimate.getRelatedOne("QuantityQuantityBreak")?if_exists>
        <#assign priceValue = estimate.getRelatedOne("PriceQuantityBreak")?if_exists>                             
        <tr>
          <td><div class="tabletext">${estimate.shipmentCostEstimateId}</div></td>
          <td><div class="tabletext">${estimate.shipmentMethodTypeId}&nbsp;(${estimate.carrierPartyId})</div></td>       
          <td><div class="tabletext">${estimate.geoIdTo?default("All")}</div></td>
          <td><div class="tabletext">${estimate.partyId?default("All")}</div></td>
          <td><div class="tabletext">${estimate.roleTypeId?default("All")}</div></td>        
          <td><div class="tabletext">${estimate.orderPricePercent?default(0)?string.number}%</div></td>
          <td><div class="tabletext">${estimate.orderFlatPrice?default(0)?string.currency}</div></td>
          <td><div class="tabletext">${estimate.orderItemFlatPrice?default(0)?string.currency}</div></td>
          <td align="center">
            <div class="tabletext"><#if security.hasEntityPermission("SHIPRATE", "_DELETE", session)><a href="<@ofbizUrl>/storeRemoveShipRate?viewProductStoreId=${productStoreId}&shipmentCostEstimateId=${estimate.shipmentCostEstimateId}</@ofbizUrl>" class="buttontext">[Delete]</a></#if> <a href="<@ofbizUrl>/EditProductStoreShipSetup?viewProductStoreId=${productStoreId}&shipmentCostEstimateId=${estimate.shipmentCostEstimateId}</@ofbizUrl>" class="buttontext">[View]</a></div>     
          </td>
        </tr>
      </#list>
    </table>
  </#if>
  
  <#if shipEstimate?has_content>
    <#assign estimate = shipEstimate>
    <#assign weightValue = estimate.getRelatedOne("WeightQuantityBreak")?if_exists>
    <#assign quantityValue = estimate.getRelatedOne("QuantityQuantityBreak")?if_exists>
    <#assign priceValue = estimate.getRelatedOne("PriceQuantityBreak")?if_exists>  
    <br>
      <table cellspacing="2" cellpadding="2">
        <tr>
          <td align='right'><span class="tableheadtext">Shipment Method</span></td>
          <td><span class="tabletext">${estimate.shipmentMethodTypeId}&nbsp;(${estimate.carrierPartyId})</span></td>
          <td>&nbsp;</td>
        </tr>    
        <tr>
          <td align='right'><span class="tableheadtext">From Geo</span></td>
          <td><span class="tabletext">${estimate.geoIdFrom?default("All")}</span></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">To Geo</span></td>
          <td><span class="tabletext">${estimate.geoIdTo?default("All")}</span></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Party</span></td>
          <td><span class="tabletext">${estimate.partyId?default("All")}</span></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Role</span></td>
          <td><span class="tabletext">${estimate.roleTypeId?default("All")}</span></td>
          <td>&nbsp;</td>
        </tr>
        <tr><td colspan="3"><hr class="sepbar"></td></tr>
        <tr>
          <td align='right'><span class="tableheadtext">Flat Base Percent</span></td>
          <td>
            <span class="tabletext">${estimate.orderPricePercent?default(0)?string.number}%</span>
            <span class="tabletext"> - shipamount = shipamount + (orderTotal * percent)</span>
          </td>
          <td>&nbsp;</td>
        </tr>                          
        <tr>
          <td align='right'><span class="tableheadtext">Flat Base Price</span></td>
          <td>
            <span class="tabletext">${estimate.orderFlatPrice?default(0)?string.currency}</span>
            <span class="tabletext"> - shipamount = shipamount + price</span>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Flat Item Price</span></td>
          <td>
            <span class="tabletext">${estimate.orderItemFlatPrice?default(0)?string.currency}</span>
            <span class="tabletext"> - shipamount = shipamount + (totalQuantity * price)</span>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr><td colspan="3"><hr class="sepbar"></td></tr>
        <tr>
          <td colspan="1"><span class="tableheadtext">Weight</span></td>
          <td colspan="2"><span class="tabletext">0 min = up to max; 0 max = from min and up</span></td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Min - Max (span)</span></td>
          <td><span class="tabletext">${weightValue.fromQuantity?if_exists}-${weightValue.thruQuantity?if_exists}</span></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Unit of Measure</span></td>
          <td><span class="tabletext">${estimate.weightUomId?if_exists}</span></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Per Unit Price</span></td>
          <td>
            <span class="tabletext">${estimate.weightUnitPrice?default(0)?string.currency}</span>
            <span class="tabletext"> - only applies if within span</span>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr><td colspan="3"><hr class="sepbar"></td></tr>
        <tr>
          <td colspan="1"><span class="tableheadtext">Quantity</span></td
          <td colspan="2"><span class="tabletext">0 min = up to max; 0 max = from min and up</span></td>
        <tr>
        <tr>
          <td align='right'><span class="tableheadtext">Min - Max (span)</span></td>
          <td><span class="tabletext">${quantityValue.fromQuantity?if_exists}-${quantityValue.thruQuantity?if_exists}</span></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Unit of Measure</span></td>
          <td><span class="tabletext">${estimate.quantityUomId?if_exists}</span></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Per Unit Price</span></td>
          <td>
            <span class="tabletext">${estimate.quantityUnitPrice?default(0)?string.currency}</span>
            <span class="tabletext"> - only applies if within span</span>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr><td colspan="3"><hr class="sepbar"></td></tr>
        <tr>
          <td colspan="1"><span class="tableheadtext">Price</span></td>
          <td colspan="2"><span class="tabletext">0 min = up to max; 0 max = from min and up</span></td>
        <tr>
        <tr>
          <td align='right'><span class="tableheadtext">Min - Max (span)</span></td>
          <td><span class="tabletext">${priceValue.fromQuantity?if_exists}-${priceValue.thruQuantity?if_exists}</span></td>
          <td>&nbsp;</td>
        </tr>        
        <tr>
          <td align='right'><span class="tableheadtext">Per Unit Price</span></td>
          <td>
            <span class="tabletext">${estimate.priceUnitPrice?default(0)?string.currency}</span>
            <span class="tabletext"> - only applies if within span</span>
          </td>
          <td>&nbsp;</td>
        </tr>                          
      </table>               
  </#if>
            
  <#if requestParameters.createNew?exists>
    <div class="head2">New Shipment Estimate:</div>    
    <form name="addform" method="post" action="<@ofbizUrl>/storeCreateShipRate</@ofbizUrl>">    
      <input type="hidden" name="viewProductStoreId" value="${productStoreId}">
      <input type="hidden" name="productStoreId" value="${productStoreId}">
      <table cellspacing="2" cellpadding="2">
        <tr>
          <td align='right'><span class="tableheadtext">Shipment Method</span></td>
          <td>
            <select name="shipMethod" class="selectBox">
              <#list storeShipMethods as shipmentMethod>
                <option value="${shipmentMethod.partyId}|${shipmentMethod.shipmentMethodTypeId}">${shipmentMethod.description} (${shipmentMethod.partyId})</option>
              </#list>
            </select>            
          </td>
          <td>&nbsp;</td>
        </tr>    
        <tr>
          <td align='right'><span class="tableheadtext">From Geo</span></td>
          <td>
            <select name="fromGeo" class="selectBox">
              <option value="">All</option>
              <#list geoList as geo>
                <option value="${geo.geoId}">${geo.geoName}</option>
              </#list>
            </select>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">To Geo</span></td>
          <td>
            <select name="toGeo" class="selectBox">
              <option value="">All</option>
              <#list geoList as geo>
                <option value="${geo.geoId}">${geo.geoName}</option>
              </#list>
            </select>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Party</span></td>
          <td><input type="text" class="inputBox" name="partyId" size="6"></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Role</span></td>
          <td><input type="text" class="inputBox" name="roleTyeId" size="6"></td>
          <td>&nbsp;</td>
        </tr>
        <tr><td colspan="3"><hr class="sepbar"></td></tr>
        <tr>
          <td align='right'><span class="tableheadtext">Flat Base Percent</span></td>
          <td>
            <input type="text" class="inputBox" name="flatPercent" value="0" size="5">
            <span class="tabletext">shipamount = shipamount + (orderTotal * percent)</span>
          </td>
          <td>&nbsp;</td>
        </tr>                          
        <tr>
          <td align='right'><span class="tableheadtext">Flat Base Price</span></td>
          <td>
            <input type="text" class="inputBox" name="flatPrice" value="0.00" size="5">
            <span class="tabletext">shipamount = shipamount + price</span>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Flat Item Price</span></td>
          <td>
            <input type="text" class="inputBox" name="flatItemPrice" value="0.00" size="5">
            <span class="tabletext">shipamount = shipamount + (totalQuantity * price)</span>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr><td colspan="3"><hr class="sepbar"></td></tr>
        <tr>
          <td colspan="1"><span class="tableheadtext">Weight</span></td>
          <td colspan="2"><span class="tabletext">0 min = up to max; 0 max = from min and up</span></td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Min - Max (span)</span></td>
          <td>
            <input type="text" class="inputBox" name="wmin" size="4"> - <input type="text" class="inputBox" name="wmax" size="4">            
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Unit of Measure</span></td>
          <td>
            <select name="wuom" class="selectBox">
              <#list weightUoms as uom>
                <option value="${uom.uomId}">${uom.description}</option>
              </#list>
            </select>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Per Unit Price</span></td>
          <td>
            <input type="text" class='inputBox' name="wprice" size="5">
            <span class="tabletext">only applies if within span</span>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr><td colspan="3"><hr class="sepbar"></td></tr>
        <tr>
          <td colspan="1"><span class="tableheadtext">Quantity</span></td
          <td colspan="2"><span class="tabletext">0 min = up to max; 0 max = from min and up</span></td>
        <tr>
        <tr>
          <td align='right'><span class="tableheadtext">Min - Max (span)</span></td>
          <td><input type="text" class="inputBox" name="qmin" size="4"> - <input type="text" class="inputBox" name="qmax" size="4"></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Unit of Measure</span></td>
          <td>
            <select name="quom" class="selectBox">
              <#list quantityUoms as uom>
                <option value="${uom.uomId}">${uom.description}</option>
              </#list>
            </select>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td align='right'><span class="tableheadtext">Per Unit Price</span></td>
          <td>
            <input type="text" class='inputBox' name="qprice" size="5">
            <span class="tabletext">only applies if within span</span>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr><td colspan="3"><hr class="sepbar"></td></tr>
        <tr>
          <td colspan="1"><span class="tableheadtext">Price</span></td>
          <td colspan="2"><span class="tabletext">0 min = up to max; 0 max = from min and up</span></td>
        <tr>
        <tr>
          <td align='right'><span class="tableheadtext">Min - Max (span)</span></td>          
          <td><input type="text" class="inputBox" name="pmin" size="4"> - <input type="text" class="inputBox" name="pmax" size="4"></td>
          <td>&nbsp;</td>
        </tr>        
        <tr>
          <td align='right'><span class="tableheadtext">Per Unit Price</span></td>
          <td>
            <input type="text" class='inputBox' name="pprice" size="5">
            <span class="tabletext">only applies if within span</span>
          </td>
          <td>&nbsp;</td>
        </tr> 
        
        <tr>
          <td colspan="3">
            <input type="submit" class="smallSubmit" value="Add">
          </td>
        </tr>               
      </table>                                                               
    </form>
  </#if> 
  
  <#if requestParameters.newShipMethod?exists>
    <div class="head2">Store Shipment Method Associations</div>
    <table border="1" cellpadding="2" cellspacing="0" width="100%">
      <tr>
        <td><span class="tableheadtext">Method Type</span></td>
        <td><span class="tableheadtext">Party</span></td>
        <td><span class="tableheadtext">Role</span></td>
        <td><span class="tableheadtext">Sequence</span></td>
        <td>&nbsp;</td>
      </tr>
      <#if storeShipMethods?has_content>
        <#assign idx = 0>
        <#list storeShipMethods as meth>
          <#assign idx = idx + 1>
          <form name="methUpdate${idx}" method="post" action="<@ofbizUrl>/storeUpdateShipMeth</@ofbizUrl>">
            <input type="hidden" name="shipmentMethodTypeId" value="${meth.shipmentMethodTypeId}">
            <input type="hidden" name="partyId" value="${meth.partyId}">
            <input type="hidden" name="roleTypeId" value="${meth.roleTypeId}">
            <input type="hidden" name="productStoreId" value="${meth.productStoreId}">
            <input type="hidden" name="viewProductStoreId" value="${productStoreId}">
            <input type="hidden" name="newShipMethod" value="Y">
            <tr> 
              <td><span class="tabletext">${meth.description}</span></td>
              <td><span class="tabletext">${meth.partyId}</span></td>
              <td><span class="tabletext">${meth.roleTypeId}</span></td>
              <td><input type="text" size="5" class="inputBox" name="sequenceNumber" value="${meth.sequenceNumber}"></td>
              <td width='1' align="right">
                <nobr>
                  <a href="javascript:document.methUpdate${idx}.submit();" class="buttontext">[Update]</a>
                  <a href="<@ofbizUrl>/storeRemoveShipMeth?viewProductStoreId=${productStoreId}&productStoreId=${meth.productStoreId}&shipmentMethodTypeId=${meth.shipmentMethodTypeId}&partyId=${meth.partyId}&roleTypeId=${meth.roleTypeId}</@ofbizUrl>" class="buttontext">[Remove]</a>
                </nobr>
              </td>
            </tr>
          </form>
        </#list>
      </#if>
    </table>
    <br>           
    <table cellspacing="2" cellpadding="2">      
      <form name="addscarr" method="post" action="<@ofbizUrl>/storeCreateShipMeth</@ofbizUrl>">
        <input type="hidden" name="viewProductStoreId" value="${productStoreId}">        
        <input type="hidden" name="newShipMethod" value="Y">
        <input type="hidden" name="productStoreId" value="${productStoreId}">
        <input type="hidden" name="shipmentMethodTypeId">
        <input type="hidden" name="roleTypeId">
        <input type="hidden" name="partyId">
        <tr>
          <td align="right"><span class="tableheadtext">Carrier Shipment Method</span></td>
          <td>            
            <select class="selectBox" name="carrierShipmentString" onChange="javascript:setAssocFields(this);">
              <option>Select One</option>
              <#list shipmentMethods as shipmentMethod>
                <option value="${shipmentMethod.partyId}|${shipmentMethod.roleTypeId}|${shipmentMethod.shipmentMethodTypeId}|${shipmentMethod.sequenceNumber?default(1)}">${shipmentMethod.description} (${shipmentMethod.partyId}/${shipmentMethod.roleTypeId})</option>
              </#list>
            </select> *                           
          </td>
        </tr>                                   
        <tr>
          <td align="right"><span class="tableheadtext">Sequence #</span></td>
          <td>
            <input type="text" class="inputBox" name="sequenceNumber" size="5">
            <span class="tabletext">Used for display ordering</span>
          </td>
        </tr>
        <tr>
          <td>            
            <input type="submit" class="smallSubmit" value="Add">
          </td>
        </tr>       
      </form>
    </table> 
    <br>    
    
    <div class="head2">Shipment Method Type:</div>                
    <table cellspacing="2" cellpadding="2">
      <form name="editmeth" method="post" action="<@ofbizUrl>/EditProductStoreShipSetup</@ofbizUrl>">
        <input type="hidden" name="viewProductStoreId" value="${productStoreId}">
        <input type="hidden" name="newShipMethod" value="Y">
        <tr>
          <td align="right"><span class="tableheadtext">Select To Edit</span></td>
          <td>
            <select class="selectBox" name="editShipmentMethodTypeId">
              <#list shipmentMethodTypes as shipmentMethodType>
                <option value="${shipmentMethodType.shipmentMethodTypeId}">${shipmentMethodType.description?default(shipmentMethodType.shipmentMethodTypeId)}</option>
              </#list>
            </select>
            <input type="submit" class="smallSubmit" value="Edit">
          </td>
        </tr>
      </form>   
      <#if shipmentMethodType?has_content>
        <#assign webRequest = "/updateShipmentMethodType">
        <#assign buttonText = "Update">
      <#else>
        <#assign webRequest = "/createShipmentMethodType">
        <#assign buttonText = "Create">
      </#if> 
      <form name="addmeth" method="post" action="<@ofbizUrl>${webRequest}</@ofbizUrl>">
        <input type="hidden" name="viewProductStoreId" value="${productStoreId}">
        <input type="hidden" name="newShipMethod" value="Y">
        <tr>
          <td align="right"><span class="tableheadtext">Shipment Method Type ID</span></td>
          <td>
            <#if shipmentMethodType?has_content>
              <div class="tabletext">${shipmentMethodType.shipmentMethodTypeId}</div>
              <input type="hidden" name="shipmentMethodTypeId" value="${shipmentMethodType.shipmentMethodTypeId}">
            <#else>
              <input type="text" class="inputBox" name="shipmentMethodTypeId" size="20"> *</td>
            </#if>
          </td>
        </tr>
        <tr>
          <td align="right"><span class="tableheadtext">Description</span></td>
          <td><input type="text" class="inputBox" name="description" size="30" value="${shipmentMethodType.description?if_exists}"> *</td>
        </tr>        
        <tr>
          <td>           
            <input type="submit" class="smallSubmit" value="${buttonText}">
          </td>
        </tr>       
      </form>
    </table> 
    
    <br>
     
    <div class="head2">Carrier Shipment Method:</div>          
    <table cellspacing="2" cellpadding="2">
      <form name="editcarr" method="post" action="<@ofbizUrl>/EditProductStoreShipSetup</@ofbizUrl>">
        <input type="hidden" name="viewProductStoreId" value="${productStoreId}">
        <input type="hidden" name="newShipMethod" value="Y">
        <tr>
          <td align="right"><span class="tableheadtext">Select To Edit</span></td>
          <td>
            <select class="selectBox" name="editCarrierShipmentMethodId">
              <#list shipmentMethods as shipmentMethod>
                <option value="${shipmentMethod.partyId}|${shipmentMethod.roleTypeId}|${shipmentMethod.shipmentMethodTypeId}">${shipmentMethod.description} (${shipmentMethod.partyId}/${shipmentMethod.roleTypeId})</option>
              </#list>
            </select>
            <input type="submit" class="smallSubmit" value="Edit">
          </td>
        </tr>
      </form>
      <#if carrierShipmentMethod?has_content>
        <#assign webRequest = "/updateCarrierShipmentMethod">
        <#assign buttonText = "Update">
      <#else>
        <#assign webRequest = "/createCarrierShipmentMethod">
        <#assign buttonText = "Create">
      </#if> 
      <form name="addcarr" method="post" action="<@ofbizUrl>${webRequest}</@ofbizUrl>">
        <input type="hidden" name="viewProductStoreId" value="${productStoreId}">
        <#if carrierShipmentMethod?has_content>
          <input type="hidden" name="newShipMethod" value="Y">
        <#else>
          <input type="hidden" name="createNew" value="Y">
        </#if>
        <tr>
          <td align="right"><span class="tableheadtext">Shipment Method</span></td>
          <td>
            <#if carrierShipmentMethod?has_content>
              <input type="hidden" name="shipmentMethodTypeId" value="${carrierShipmentMethod.shipmentMethodTypeId}">
              <div class="tabletext">${carrierShipmentMethod.shipmentMethodTypeId}</div>
            <#else>
              <select class="selectBox" name="shipmentMethodTypeId">
                <#list shipmentMethodTypes as shipmentMethodType>
                  <option value="${shipmentMethodType.shipmentMethodTypeId}">${shipmentMethodType.description?default(shipmentMethodType.shipmentMethodTypeId)}</option>
                </#list>
              </select> *
            </#if>
          </td>
        </tr>
        <tr>
          <td align="right"><span class="tableheadtext">RoleType ID</span></td>
          <td>
            <#if carrierShipmentMethod?has_content>
              <input type="hidden" name="roleTypeId" value="${carrierShipmentMethod.roleTypeId}">
              <div class="tabletext">${carrierShipmentMethod.roleTypeId}</div>
            <#else>
              <select class="selectBox" name="roleTypeId">
                <#list roleTypes as roleType>
                  <option value="${roleType.roleTypeId}" <#if roleType.roleTypeId == "CARRIER" && !carrierShipmentMethod?has_content>selected</#if>>${roleType.description?default(roleType.roleTypeId)}</option>
                </#list>
              </select> *
            </#if>
          </td>
        </tr> 
        <tr>
          <td align="right"><span class="tableheadtext">Party ID</span></td>
          <td>
            <#if carrierShipmentMethod?has_content>
              <input type="hidden" name="partyId" value="${carrierShipmentMethod.partyId}">
              <div class="tabletext">${carrierShipmentMethod.partyId}</div>
            <#else>
              <input type="text" class="inputBox" name="partyId" size="20" value="${carrierShipmentMethod.partyId?if_exists}"> *
            </#if>
          </td>
        </tr>                      
        <tr>
          <td align="right"><span class="tableheadtext">Carrier Service Code</span></td>
          <td><input type="text" class="inputBox" name="carrierServiceCode" size="20" value="${carrierShipmentMethod.carrierServiceCode?if_exists}"></td>
        </tr>                 
        <tr>
          <td align="right"><span class="tableheadtext">Sequence #</span></td>
          <td>
            <input type="text" class="inputBox" name="sequenceNumber" size="5" value="${carrierShipmentMethod.sequenceNumber?if_exists}">
            <span class="tabletext">Used for display ordering</span>
          </td>
        </tr>       
        <tr>
          <td>            
            <input type="submit" class="smallSubmit" value="${buttonText}">
          </td>
        </tr>       
      </form>
    </table>
  </#if>        
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
