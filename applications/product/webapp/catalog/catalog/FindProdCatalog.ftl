<#--

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

<div class="head1">${uiLabelMap.ProductProductCatalogsList}</div>
<div><a href="<@ofbizUrl>EditProdCatalog</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductCreateNewProdCatalog}]</a></div>
<br/>
<table border="1" cellpadding="2" cellspacing="0">
  <tr>
    <td><div class="tabletext"><b>${uiLabelMap.ProductCatalogNameId}</b></div></td>    
    <td><div class="tabletext"><b>${uiLabelMap.ProductUseQuickAdd}?</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<#list prodCatalogs as prodCatalog>
  <tr valign="middle">
    <td><div class="tabletext">&nbsp;<a href="<@ofbizUrl>EditProdCatalog?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class="buttontext">${prodCatalog.catalogName} [${prodCatalog.prodCatalogId}]</a></div></td>   
    <td><div class="tabletext">&nbsp;${prodCatalog.useQuickAdd?if_exists}</div></td>
    <td>
      <a href="<@ofbizUrl>EditProdCatalog?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class="buttontext">
      [${uiLabelMap.CommonEdit}]</a>
    </td>
  </tr>
</#list>
</table>
<br/>
