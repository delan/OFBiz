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
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
${pages.get("/shipment/ShipmentTabBar.ftl")}

<#if shipment?exists>
    <div class="head1">Shipment Plan</div>
    ${findOrderItemsForm.renderFormString()}
    <br>
    <#if addToShipmentPlanRows?has_content>
        ${addToShipmentPlanForm.renderFormString()}
<SCRIPT language="javascript">
    function submitRows(rowCount) {
        var rowCountElement = document.createElement("input");
        rowCountElement.setAttribute("name", "_rowCount");
        rowCountElement.setAttribute("type", "hidden");
        rowCountElement.setAttribute("value", rowCount);
        document.forms.addToShipmentPlan.appendChild(rowCountElement);

        var shipmentIdElement = document.createElement("input");
        shipmentIdElement.setAttribute("name", "shipmentId");
        shipmentIdElement.setAttribute("type", "hidden");
        shipmentIdElement.setAttribute("value", ${shipmentId});
        document.forms.addToShipmentPlan.appendChild(shipmentIdElement);

        document.forms.addToShipmentPlan.submit();
    }
</SCRIPT>
<form><input type="submit" class="smallSubmit" onClick="submitRows('${rowCount?if_exists}');return false;" name="submitButton" value="${uiLabelMap.CommonAdd}"/></form>
    <hr class="sepbar">
	<br>
    </#if>
    ${listShipmentPlanForm.renderFormString()}
    ${shipmentPlanToOrderItemsForm.renderFormString()}

<#else>
  <h3>${uiLabelMap.ProductShipmentNotFoundId} : [${shipmentId?if_exists}]</h3>
</#if>

<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>
