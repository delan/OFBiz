<#--
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski
 *@version    $Revision$
 *@since      2.1
-->

<#-- variable setup and worker calls -->
<#assign currentCatalogId = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getCurrentCatalogId(request)>
<#assign topLevelList = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getCatalogTopCategoryId(request, currentCatalogId, true)>
<#if requestParameters.category_id?exists>
  <#assign curCategoryId = requestParameters.category_id>
<#elseif requestParameters.CATEGORY_ID?exists>
  <#assign curCategoryId = requestParameters.CATEGORY_ID>
</#if>
<#if curCategoryId?exists>
  <#assign trail = Static["org.ofbiz.commonapp.product.category.CategoryWorker"].setTrail(request, curCategoryId)>
</#if>

<#-- looping macro -->
<#macro categoryList parentCategory category, currentCategoryId>
  <#if curCategoryId?exists && curCategoryId == category.productCategoryId>
    <div class="browsecategorytext">
      -&nbsp;<a href="<@ofbizUrl>/category?category_id=${category.productCategoryId}</@ofbizUrl>" class="browsecategorybuttondisabled">${category.description?if_exists}</a>
    </div>
  <#else>
    <div class="browsecategorytext">
      -&nbsp;<a href="<@ofbizUrl>/category?category)id=${category.productCategoryId}</@ofbizUrl>" class="browsecategorybutton">${category.description?if_exists}</a>
    </div>
  </#if>
  
  <#if (Static["org.ofbiz.commonapp.product.category.CategoryWorker"].checkTrailItem(request, category.getString("productCategoryId"))) || (curCategoryId?exists && curCategoryId == category.produtCategoryId)>
    <#local subCatList = Static["org.ofbiz.commonapp.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category.getString("productCategoryId"), true)>
    <#if subCatList?exists && 0 < subCatList.size>
      <#list subCatList as subCat>
        <div style="margin-left: 10px">
        <@categoryList parentCategory=category category=subCat/>
      </#list>
    </#if>
  </#if>
</#macro>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">Browse&nbsp;Categories</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <div style='margin-left: 10px;'>
              <#list topLevelList as category>
                <@categoryList parentCategory=null category=category/>
              </#list>
            </div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
