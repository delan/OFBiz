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
  <#if security.hasEntityPermission("SHIPRATE", "_VIEW", session)>
  <a href="<@ofbizUrl>/shipsetup</@ofbizUrl>" class='tabButtonSelected'>Ship&nbsp;Rate&nbsp;Setup</a>
  </#if> 
  <#if security.hasEntityPermission("PAYPROC", "_VIEW", session)>
  <a href="<@ofbizUrl>/paysetup</@ofbizUrl>" class='tabButton'>Payment&nbsp;Setup</a>
  </#if>
</div>

<#if security.hasEntityPermission("SHIPRATE", "_VIEW", session)>
<table border=0 width='100%' cellpadding='0' cellspacing=0 class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxtop'>
        <tr>
          <td align=left width='90%' >
            <div class='boxhead'>&nbsp;Shipping Rate Editor</div>
          </td>
          <td align=right width='10%'></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
        <tr>
          <td>                 
            <table width="100%" cellpadding="2" cellspacing="2" border="0">
              <tr class="viewOneTR1">
                <td nowrap><div class="tabletext"><b>Method</b></div></td>
                <td nowrap><div class="tabletext"><b>From</b></div></td>
               	<td nowrap><div class="tabletext"><b>To</b></div></td>
      			<td nowrap><div class="tabletext"><b>Party</b></div></td>
                <td nowrap><div class="tabletext"><b>Role</b></div></td>
                <td nowrap><div class="tabletext"><b>Min-Max (w)</b></div></td>
                <td nowrap><div class="tabletext"><b>WeightAmt</b></div></td>
                <td nowrap><div class="tabletext"><b>Min-Max (q)</b></div></td>
                <td nowrap><div class="tabletext"><b>QtyAmt</b></div></td>
                <td nowrap><div class="tabletext"><b>Min-Max (p)</b></div></td>
                <td nowrap><div class="tabletext"><b>PriceAmt</b></div></td>
                <td nowrap><div class="tabletext"><b>Base%</b></div></td>
                <td nowrap><div class="tabletext"><b>BasePrc</b></div></td>
                <td nowrap><div class="tabletext"><b>ItemPrc</b></div></td>
                <td nowrap><div class="tabletext"><b>&nbsp;</b></div></td>
              </tr>
              <#list estimates as estimate>
                <#assign weightValue = estimate.getRelatedOne("WeightQuantityBreak")?if_exists>
                <#assign quantityValue = estimate.getRelatedOne("QuantityQuantityBreak")?if_exists>
                <#assign priceValue = estimate.getRelatedOne("PriceQuantityBreak")?if_exists>
                <#if rowStyle?exists && rowStyle == "viewManyTR1">
                  <#assign rowStyle = "viewManyTR2">
                <#else>
                  <#assign rowStyle = "viewManyTR1">
                </#if>                
                <tr class="${rowStyle}">
                  <td><div class="tabletext">${estimate.shipmentMethodTypeId}&nbsp;(${estimate.carrierPartyId})</div></td>
                  <td><div class="tabletext">${estimate.geoIdFrom?if_exists}</div></td>
                  <td><div class="tabletext">${estimate.geoIdTo?if_exists}</div></td>
                  <td><div class="tabletext">${estimate.partyId?if_exists}</div></td>
                  <td><div class="tabletext">${estimate.roleTypeId?if_exists}</div></td>
                  <td><div class="tabletext">${weightValue.fromQuantity?if_exists}-${weightValue.thruQuantity?if_exists}</div></td>
                  <td><div class="tabletext">${estimate.weightUnitPrice?default(0)?string.currency}</div></td>
                  <td><div class="tabletext">${quantityValue.fromQuantity?if_exists}-${quantityValue.thruQuantity?if_exists}</div></td>
                  <td><div class="tabletext">${estimate.quantityUnitPrice?default(0)?string.currency}</div></td>
                  <td><div class="tabletext">${priceValue.fromQuantity?if_exists}-${priceValue.thruQuantity?if_exists}</div></td>
                  <td><div class="tabletext">${estimate.priceUnitPrice?default(0)?string.currency}</div></td>
                  <td><div class="tabletext">${estimate.orderPricePercent?default(0)?string.number}%</div></td>
                  <td><div class="tabletext">${estimate.orderFlatPrice?default(0)?string.currency}</div></td>
                  <td><div class="tabletext">${estimate.orderItemFlatPrice?default(0)?string.currency}</div></td>
                  <#if security.hasEntityPermission("SHIPRATE", "_DELETE", session)>
                    <td><div class="tabletext"><a href="<@ofbizUrl>/removeshipestimate?shipmentCostEstimateId=${estimate.shipmentCostEstimateId}</@ofbizUrl>" class="buttontext">[Remove]</a></div></td>
                  <#else>
                    <td>&nbsp;</td>
                  </#if>
                </tr>
              </#list>
            </table>
            <#if security.hasEntityPermission("SHIPRATE", "_CREATE", session)>
              <br>
              <form name="addform" method="post" action="<@ofbizUrl>/createshipestimate</@ofbizUrl>">    
                <b>Add New Record:</b>
                <br>
                <span class="info">
                  Base Info; Specify GeoID / Party ID; No GeoID means anywhere NOT already defined.
                </span>
                <table width="100%" border="0" class="edittable">
                  <tr>
                    <td width="100%">
                      <table width="100%" cellpadding="2" cellspacing="2" border="0">
                        <tr class="viewOneTR1">
                          <td nowrap><div class="tabletext"><b>Ship Method</b></div></td>
                          <td nowrap><div class="tabletext"><b>FromGeo</b></div></td>
                          <td nowrap><div class="tabletext"><b>ToGeo</b></div></td>
                          <td nowrap><div class="tabletext"><b>PartyID</b></div></td>
                          <td nowrap><div class="tabletext"><b>RoleTypeID</b></div></td>
                        </tr>
                        <tr class="viewManyTR1">
                          <td>
                            <select name="shipMethod" class="selectBox">
                              <#list shipmentMethods as shipmentMethod>
                                <option value="${shipmentMethod.partyId}|${shipmentMethod.shipmentMethodTypeId}">${shipmentMethod.description} (${shipmentMethod.partyId})</option>
                              </#list>
                            </select>
                          </td>
                          <td>
                            <select name="fromGeo" class="selectBox">
                              <option value="">None</option>
                              <#list geoList as geo>
                                <option value="${geo.geoId}">${geo.geoName}</option>
                              </#list>
                            </select>
                          </td>
                          <td>
                            <select name="toGeo" class="selectBox">
                              <option value="">None</option>
                              <#list geoList as geo>
                                <option value="${geo.geoId}">${geo.geoName}</option>
                              </#list>
                            </select>
                          </td>
                          <td><div class="tabletext"><input type="text" class="inputBox" name="partyId" size="6"></div></td>
                          <td><div class="tabletext"><input type="text" class="inputBox" name="roleTyeId" size="6"></div></td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <span class="info">
                  Flat Rate Info; Will be added to the shipping calculation.
                </span>
                <table width="100%" border="0" class="edittable">
                  <tr>
                    <td width="100%">
                      <table width="100%" cellpadding="2" cellspacing="2" border="0">
                        <tr class="viewOneTR1">
                          <td nowrap><div class="tabletext"><b>FlatBasePercent</b></div></td>
                          <td nowrap><div class="tabletext"><b>FlatBasePrice</b></div></td>
                          <td nowrap><div class="tabletext"><b>FlatItemPrice</b></div></td>
                        </tr>
                        <tr class="viewManyTR1">
                          <td><div class="tabletext"><input type="text" class="inputBox" name="flatPercent" value="0" size="5">&nbsp;%</div></td>
                          <td><div class="tabletext"><input type="text" class="inputBox" name="flatPrice" value="0.00" size="5"></div></td>
                          <td><div class="tabletext"><input type="text" class="inputBox" name="flatItemPrice" value="0.00" size="5"></div></td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <span class="info">
                  Unit Span Info; Units must fall in between the span to qualify. An empty span will effect only units which do not have a matching span.
                </span>
                <table width="100%" border="0" class="edittable">
                  <tr>
                    <td width="100%">
                      <table width="100%" cellpadding="2" cellspacing="2" border="0">
                        <tr class="viewOneTR1">
                          <td nowrap><div class="tabletext"><b>Min - Max (Weight)</b></div></td>
                          <td nowrap><div class="tabletext"><b>WeightUOM</b></div></td>
                          <td nowrap><div class="tabletext"><b>UnitWeightAmt</b></div></td>
                        </tr>
                        <tr class="viewManyTR1">
                          <td><div class="tabletext"><input type="text" class="inputBox" name="wmin" size="4"> - <input type="text" class="inputBox" name="wmax" size="4"></div></td>
                          <td>
                            <select name="wuom" class="selectBox">
                              <#list weightUoms as uom>
                                <option value="${uom.uomId}">${uom.description}</option>
                              </#list>
                            </select>
                          </td>
                          <td><div class="tabletext"><input type="text" class='inputBox' name="wprice" size="5"></div></td>
                        </tr>
                        <tr class="viewOneTR1">
                          <td nowrap><div class="tabletext"><b>Min - Max (Qty)</b></div></td>
                          <td nowrap><div class="tabletext"><b>QtyUOM</b></div></td>
                          <td nowrap><div class="tabletext"><b>UnitQtyAmt</b></div></td>
                        </tr>
                        <tr class="viewManyTR1">
                          <td><div class="tabletext"><input type="text" class="inputBox" name="qmin" size="4"> - <input type="text" class="inputBox" name="qmax" size="4"></div></td>
                          <td>
                            <select name="quom" class="selectBox">
                              <#list quantityUoms as uom>
                                <option value="${uom.uomId}">${uom.description}</option>
                              </#list>
                            </select>
                          </td>
                          <td><div class="tabletext"><input type="text" class='inputBox' name="qprice" size="5"></div></td>
                        </tr>
                        <tr class="viewOneTR1">
                          <td nowrap><div class="tabletext"><b>Min - Max (Price)</b></div></td>
                          <td nowrap><div class="tabletext"><b>&nbsp;</b></div></td>
                          <td nowrap><div class="tabletext"><b>UnitPriceAmt</b></div></td>
                        </tr>
                        <tr class="viewManyTR1">
                          <td><div class="tabletext"><input type="text" class="inputBox" name="pmin" size="4"> - <input type="text" class="inputBox" name="pmax" size="4"></div></td>
                          <td><div class="tabletext">&nbsp;</div></td>
                          <td><div class="tabletext"><input type="text" class="inputBox" name="pprice" size="5"></div></td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <a href="javascript:document.addform.submit();" class="buttontext">[Add/Save]</a>    
              </form>
              <br>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<#else>
  <br>
  <h3>You do not have permission to view this page. ("SHIPRATE_VIEW" or "SHIPRATE_ADMIN" needed)</h3>
</#if>

