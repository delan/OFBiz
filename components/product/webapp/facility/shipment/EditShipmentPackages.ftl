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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Revision: 1.2 $
 *@since      2.2
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
${pages.get("/shipment/ShipmentTabBar.ftl")}

<#if shipment?exists>
<table width="100%" cellspacing="0" cellpadding="2" border="1">
    <tr>
        <td><div class="tableheadtext">Package#</div></td>
        <td><div class="tableheadtext">Created</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
        <td><div class="tableheadtext">&nbsp;</div></td>
    </tr>
<#list shipmentPackageDatas as shipmentPackageData>
    <#assign shipmentPackage = shipmentPackageData.shipmentPackage>
    <#assign shipmentPackageContents = shipmentPackageData.shipmentPackageContents?if_exists>
    <#assign shipmentPackageRouteSegs = shipmentPackageData.shipmentPackageRouteSegs?if_exists>
    <#assign weightUom = shipmentPackageData.weightUom?if_exists>
    <form action="<@ofbizUrl>/updateShipmentPackage</@ofbizUrl>" name="updateShipmentPackageForm${shipmentPackageData_index}">
    <input type="hidden" name="shipmentId" value="${shipmentId}"/>
    <input type="hidden" name="shipmentPackageSeqId" value="${shipmentPackage.shipmentPackageSeqId}"/>
    <tr>
        <td><div class="tabletext">${shipmentPackage.shipmentPackageSeqId}</div></td>
        <td><div class="tabletext">${(shipmentPackage.dateCreated.toString())?if_exists}</div></td>
        <td><span class="tabletext">Weight:</span><input type="text" size="5" name="weight" value="${shipmentPackage.weight?if_exists}" class="inputBox"/></td>
        <td><span class="tabletext">WeightUnit:</span>
            <select name="weightUomId" class="selectBox">
                <#if weightUom?has_content>
                    <option value="${weightUom.uomId}">${weightUom.description}</option>
                    <option value="${weightUom.uomId}">---</option>
                <#else>
                    <option value="">&nbsp;</option>
                </#if>
                <#list weightUoms as weightUomOption>
                    <option value="${weightUomOption.uomId}">${weightUomOption.description} [${weightUomOption.abbreviation}]</option>
                </#list>
            </select>
        </td>
        <td><a href="javascript:document.updateShipmentPackageForm${shipmentPackageData_index}.submit();" class="buttontext">Update</a></td>
        <td><div class="tabletext"><a href="<@ofbizUrl>/deleteShipmentPackage?shipmentId=${shipmentId}&shipmentPackageSeqId=${shipmentPackage.shipmentPackageSeqId}</@ofbizUrl>" class="buttontext">Delete</a></div></td>
    </tr>
    </form>
    <#list shipmentPackageContents as shipmentPackageContent>
        <tr>
            <td><div class="tabletext">&nbsp;</div></td>
            <td><div class="tabletext">Item:${shipmentPackageContent.shipmentItemSeqId}</div></td>
            <td><div class="tabletext">Quantity:${shipmentPackageContent.quantity?if_exists}</div></td>
            <td><div class="tabletext">&nbsp;</div></td>
            <td><div class="tabletext">&nbsp;</div></td>
            <td><div class="tabletext"><a href="<@ofbizUrl>/deleteShipmentPackageContent?shipmentId=${shipmentId}&shipmentPackageSeqId=${shipmentPackageContent.shipmentPackageSeqId}&shipmentItemSeqId=${shipmentPackageContent.shipmentItemSeqId}</@ofbizUrl>" class="buttontext">Delete</a></div></td>
        </tr>
    </#list>
    <tr>
        <form action="<@ofbizUrl>/createShipmentPackageContent</@ofbizUrl>" name="createShipmentPackageContentForm${shipmentPackageData_index}">
        <input type="hidden" name="shipmentId" value="${shipmentId}"/>
        <input type="hidden" name="shipmentPackageSeqId" value="${shipmentPackage.shipmentPackageSeqId}"/>
        <td><div class="tabletext">&nbsp;</div></td>
        <td>
            <div class="tabletext">Add from Item:
            <select name="shipmentItemSeqId" class="selectBox">
                <#list shipmentItems as shipmentItem>
                    <option>${shipmentItem.shipmentItemSeqId}</option>
                </#list>
            </select>
            </div>
        </td>
        <td><div class="tabletext">Quantity:<input name="quantity" size="5" value="0" class="inputBox"/></div></td>
        <td><div class="tabletext">&nbsp;</div></td>
        <td><a href="javascript:document.createShipmentPackageContentForm${shipmentPackageData_index}.submit()" class="buttontext">Add</a></td>
        <td><div class="tabletext">&nbsp;</div></td>
        </form>
    </tr>
    <#list shipmentPackageRouteSegs as shipmentPackageRouteSeg>
        <form action="<@ofbizUrl>/updateShipmentPackageRouteSeg</@ofbizUrl>" name="updateShipmentPackageRouteSegForm${shipmentPackageData_index}${shipmentPackageRouteSeg_index}">
        <input type="hidden" name="shipmentId" value="${shipmentId}"/>
        <input type="hidden" name="shipmentRouteSegmentId" value="${shipmentPackageRouteSeg.shipmentRouteSegmentId}"/>
        <input type="hidden" name="shipmentPackageSeqId" value="${shipmentPackageRouteSeg.shipmentPackageSeqId}"/>
        <tr>
            <td><div class="tabletext">&nbsp;</div></td>
            <td><div class="tabletext">RouteSegment:${shipmentPackageRouteSeg.shipmentRouteSegmentId}</div></td>
            <td><span class="tabletext">Track#:</span><input type="text" size="22" name="trackingCode" value="${shipmentPackageRouteSeg.trackingCode?if_exists}" class="inputBox"/></td>
            <td><span class="tabletext">Box#:</span><input type="text" size="5" name="boxNumber" value="${shipmentPackageRouteSeg.boxNumber?if_exists}" class="inputBox"/></td>
            <td><a href="javascript:document.updateShipmentPackageRouteSegForm${shipmentPackageData_index}${shipmentPackageRouteSeg_index}.submit();" class="buttontext">Update</a></td>
            <td><div class="tabletext"><a href="<@ofbizUrl>/deleteShipmentPackageRouteSeg?shipmentId=${shipmentId}&shipmentPackageSeqId=${shipmentPackageRouteSeg.shipmentPackageSeqId}&shipmentRouteSegmentId=${shipmentPackageRouteSeg.shipmentRouteSegmentId}</@ofbizUrl>" class="buttontext">Delete</a></div></td>
        </tr>
        </form>
    </#list>
    <#--
    <tr>
        <form action="<@ofbizUrl>/createShipmentPackageRouteSeg</@ofbizUrl>" name="createShipmentPackageRouteSegForm${shipmentPackageData_index}">
        <input type="hidden" name="shipmentId" value="${shipmentId}"/>
        <input type="hidden" name="shipmentPackageSeqId" value="${shipmentPackage.shipmentPackageSeqId}"/>
        <td><div class="tabletext">&nbsp;</div></td>
        <td>
            <div class="tabletext">Add Route Segment Info:
            <select name="shipmentRouteSegmentId" class="selectBox">
                <#list shipmentRouteSegments as shipmentRouteSegment>
                    <option>${shipmentRouteSegment.shipmentRouteSegmentId}</option>
                </#list>
            </select>
            </div>
        </td>
        <td><span class="tabletext">Track#:</span><input type="text" size="22" name="trackingCode" class="inputBox"/></td>
        <td><span class="tabletext">Box#:</span><input type="text" size="5" name="boxNumber" class="inputBox"/></td>
        <td><a href="javascript:document.createShipmentPackageRouteSegForm${shipmentPackageData_index}.submit()" class="buttontext">Add</a></td>
        <td><div class="tabletext">&nbsp;</div></td>
        </form>
    </tr>
    -->
</#list>
<#--
<form action="<@ofbizUrl>/createShipmentPackage</@ofbizUrl>" name="createShipmentPackageForm">
    <input type="hidden" name="shipmentId" value="${shipmentId}"/>
    <tr>
        <td><div class="tabletext">New Package:</div></td>
        <td><div class="tabletext">&nbsp;</div></td>
        <td><span class="tabletext">Weight:</span><input type="text" size="5" name="weight" class="inputBox"/></td>
        <td><span class="tabletext">WeightUnit:</span>
            <select name="weightUomId" class="selectBox">
                <#list weightUoms as weightUomOption>
                    <option value="${weightUomOption.uomId}">${weightUomOption.description} [${weightUomOption.abbreviation}]</option>
                </#list>
            </select>
        </td>
        <td><a href="javascript:document.createShipmentPackageForm.submit();" class="buttontext">Create</a></td>
        <td>&nbsp;</td>
    </tr>
</form>
-->
</table>
<#else>
  <h3>The Shipment was not found with ID: [${shipmentId?if_exists}]</h3>
</#if>

<#else>
  <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
