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
 *@version    $Revision$
 *@since      2.1
-->

<#-- looping macro -->
<#macro categoryList parentCategory category>
  <#if parentCategory.productCategoryId != category.productCategoryId>
    <#local pStr = "&pcategory=" + parentCategory.productCategoryId>  
  </#if>
  <#if curCategoryId?exists && curCategoryId == category.productCategoryId>
    <div class="browsecategorytext">
      -&nbsp;<a href="<@ofbizUrl>/EditCategory?productCategoryId=${category.productCategoryId}${pStr?if_exists}</@ofbizUrl>" class="browsecategorybuttondisabled">${category.description?if_exists} [${category.productCategoryId}]</a>
    </div>
  <#else>
    <div class="browsecategorytext">
      -&nbsp;<a href="<@ofbizUrl>/EditCategory?productCategoryId=${category.productCategoryId}${pStr?if_exists}</@ofbizUrl>" class="browsecategorybutton">${category.description?if_exists} [${category.productCategoryId}]</a>
    </div>
  </#if>
  
  <#if (Static["org.ofbiz.commonapp.product.category.CategoryWorker"].checkTrailItem(request, category.getString("productCategoryId"))) || (curCategoryId?exists && curCategoryId == category.productCategoryId)>
    <#local subCatList = Static["org.ofbiz.commonapp.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category.getString("productCategoryId"), true)>
    <#if subCatList?exists>
      <#list subCatList as subCat>
        <div style="margin-left: 10px">
          <@categoryList parentCategory=category category=subCat/>
        </div>
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
          <td valign=middle align=right>
            <#if isOpen>
                <a href='<@ofbizUrl>/main?BrowseCategoriesState=close</@ofbizUrl>' class='lightbuttontext'>&nbsp;_&nbsp;</a>
            <#else>
                <a href='<@ofbizUrl>/main?BrowseCategoriesState=open</@ofbizUrl>' class='lightbuttontext'>&nbsp;[]&nbsp;</a>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
<#if isOpen>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <div><a href='<@ofbizUrl>/ChooseTopCategory</@ofbizUrl>' class='buttontext'>Choose Top Category</a></div>
            <div style='margin-left: 10px;'>
            <#if currentTopCategory?exists>
              <#if curCategoryId?exists && curCategoryId == currentTopCategory.productCategoryId>
                <div class='tabletext' style='text-indent: -10px;'><b>-&nbsp;${currentTopCategory.description} [${currentTopCategory.productCategoryId}]</b></div>
              <#else>
                <div class='browsecategorytext'>-&nbsp;<a href="<@ofbizUrl>/EditCategory?productCategoryId=${currentTopCategory.productCategoryId}</@ofbizUrl>" class='browsecategorybutton'>${currentTopCategory.description?if_exists} [${currentTopCategory.productCategoryId}]</a></div>
              </#if>
            </#if>
              <div style='margin-left: 10px;'>
                <#if topLevelList?exists>
                  <#list topLevelList as category>
                    <@categoryList parentCategory=category category=category/>
                  </#list>
                </#if>
              </div>
            </div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</#if>
</TABLE>
