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
<#-- variable setup and worker calls -->
<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
<#if (requestAttributes.curCategoryId)?exists><#assign curCategoryId = requestAttributes.curCategoryId></#if>

<#-- looping macro -->
<#macro categoryList parentCategory category>
  <#if parentCategory.productCategoryId != category.productCategoryId>
    <#local pStr = "/~pcategory=" + parentCategory.productCategoryId>
  </#if>
  <#if curCategoryId?exists && curCategoryId == category.productCategoryId>
    <div class="browsecategorytext">
      -&nbsp;<a href="<@ofbizUrl>category/~category_id=${category.productCategoryId}${pStr?if_exists}</@ofbizUrl>" class="browsecategorybuttondisabled">${category.description?if_exists}</a>
    </div>
  <#else>
    <div class="browsecategorytext">
      -&nbsp;<a href="<@ofbizUrl>category/~category_id=${category.productCategoryId}${pStr?if_exists}</@ofbizUrl>" class="browsecategorybutton">${category.description?if_exists}</a>
    </div>
  </#if>

  <#if (Static["org.ofbiz.product.category.CategoryWorker"].checkTrailItem(request, category.getString("productCategoryId"))) || (curCategoryId?exists && curCategoryId == category.productCategoryId)>
    <#local subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category.getString("productCategoryId"), true)>
    <#if subCatList?exists>
      <#list subCatList as subCat>
        <div style="margin-left: 10px">
          <@categoryList parentCategory=category category=subCat/>
        </div>
      </#list>
    </#if>
  </#if>
</#macro>

<#if topLevelList?has_content>
<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">${uiLabelMap.ProductBrowseCategories}</div>
    </div>
    <div class="screenlet-body">
        <div style='margin-left: 10px;'>
          <#list topLevelList as category>
            <@categoryList parentCategory=category category=category/>
          </#list>
        </div>
    </div>
</div>
</#if>
