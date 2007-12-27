<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<div class="screenlet">
    <div class="screenlet-title-bar">
        <h3>${uiLabelMap.ProductProductCatalogsList}</h3>
    </div>
    <div class="screenlet-body">
        <table cellspacing="0" class="basic-table">
          <tr class="header-row">
            <td><b>${uiLabelMap.ProductCatalogNameId}</b></td>    
            <td><b>${uiLabelMap.ProductUseQuickAdd}?</b></td>
            <td><b>&nbsp;</b></td>
          </tr>
        <#assign rowClass = "2">
        <#list prodCatalogs as prodCatalog>
          <tr valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
            <td><a href="<@ofbizUrl>EditProdCatalog?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class="buttontext">${prodCatalog.catalogName} [${prodCatalog.prodCatalogId}]</a></td>   
            <td>${prodCatalog.useQuickAdd?if_exists}</td>
            <td>
              <a href="<@ofbizUrl>EditProdCatalog?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonEdit}</a>
            </td>
          </tr>
          <#-- toggle the row color -->
          <#if rowClass == "2">
            <#assign rowClass = "1">
          <#else>
            <#assign rowClass = "2">
          </#if> 
        </#list>
        </table>
    </div>
</div>
