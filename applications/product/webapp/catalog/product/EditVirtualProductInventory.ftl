<#--
$Id: $

Copyright 2001-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->
<table border="1" cellpadding="2" cellspacing="0">
    <tr>
        <td><div class="tabletext"><b>${uiLabelMap.ProductProductId}</b></div></td>
            <#list featureTypeIds as featureTypeId>
                <#assign featureType = delegator.findByPrimaryKey("ProductFeatureType", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureTypeId", featureTypeId))>
                <td><div class="tabletext"><b>${featureType.description}</b></div></td>
            </#list>
        <td><div class="tabletext"><b>${uiLabelMap.ProductQoh}</b></div></td>
        <td><div class="tabletext"><b>${uiLabelMap.ProductAtp}</b></div></td>
    </tr>
    <#list variantInventorySummaries as variantSummary>
    <tr>
        <td><a href="/catalog/control/EditProductInventoryItems?productId=${variantSummary.productId}" class="buttontext">${variantSummary.productId}</a></td>
            <#list featureTypeIds as featureTypeId>
                <td><div class="tabletext"><b>${variantSummary[featureTypeId].description}</b></div></td>
            </#list>
        <td><div class="tabletext">${variantSummary.quantityOnHandTotal}</b></div></td>
        <td><div class="tabletext">${variantSummary.availableToPromiseTotal}</b></div></td>
    </tr>
    </#list>
</table>
