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

<#assign currentCatalogId = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getCurrentCatalogId(request)>
<#assign otherSearchProdCatalogCategories = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getProdCatalogCategories(request, currentCatalogId, "PCCT_OTHER_SEARCH")>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">Search&nbsp;Catalog</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td align=center>
            <form name="keywordsearchform" method="POST" action="<@ofbizUrl>/keywordsearch</@ofbizUrl>" style='margin: 0;'>
              <input type='hidden' name="VIEW_SIZE" value="10">
              <div class='tabletext'>
                <input type='text' class='inputBox' name="SEARCH_STRING" size="14" maxlength="50" value="${requestParameters.SEARCH_STRING?if_exists}">
              </div>
              <#if 0 < otherSearchProdCatalogCategories?size>
                <#assign searchCategory = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getCatalogSearchCategoryId(request, currentCatalogId)>
                <div class='tabletext'>
                  <select name='SEARCH_CATEGORY_ID' size='1' class='selectBox'>
                    <option value="${searchCategory?if_exists}">Entire Catalog</option>
                    <#list otherSearchProdCatalogCategory as searchCat>
                      <#assign searchProductCategory = otherSearchProdCatalogCategory.getRelatedOneCache("ProductCategory")>
                      <#if searchProductCategory?exists>
                        <option value="${searchProductCategory.productCategoryId}">${searchProductCategory.description?exists}</option>
                      </#if>
                    </#list>
                  </select>
                </div>
              <#else>
                <#assign searchCategory = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getCatalogSearchCategoryId(request, currentCatalogId)>
                <input type='hidden' name="SEARCH_CATEGORY_ID" value="${searchCategory}">
              </#if>
              <div class='tabletext'>
                <#assign searchOperator = requestParameters.SEARCH_OPERATOR?if_exists>
                <#if searchOperator?upper_case != "AND" && searchOperator?upper_case != "OR">
                  <assign searchOperator = "OR">
                </#if>
                Any<input type='RADIO' name='SEARCH_OPERATOR' value='OR' <#if searchOperator?upper_case == "OR">checked</#if>>
                All<input type='RADIO' name='SEARCH_OPERATOR' value='AND' <#if searchOperator?upper_case == "AND">checked</#if>>
                &nbsp;<a href="javascript:document.keywordsearchform.submit()" class="buttontext">Find</a>
              </div>
            </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
