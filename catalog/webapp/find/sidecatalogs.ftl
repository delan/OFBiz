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

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">Browse&nbsp;Catalogs</div>
          </td>
          <td valign=middle align=right>
            <#if isOpen>
                <a href='<@ofbizUrl>/main?BrowseCatalogsState=close</@ofbizUrl>' class='lightbuttontext'>&nbsp;_&nbsp;</a>
            <#else>
                <a href='<@ofbizUrl>/main?BrowseCatalogsState=open</@ofbizUrl>' class='lightbuttontext'>&nbsp;[]&nbsp;</a>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
<#if isOpen>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <div><a href='<@ofbizUrl>/FindProdCatalog</@ofbizUrl>' class='buttontext'>Catalog Detail List</a></div>
            <div style='margin-left: 10px;'>
              <#assign sortList = Static["org.ofbiz.core.util.UtilMisc"].toList("prodCatalogCategoryTypeId", "sequenceNum", "productCategoryId")>
              <#list prodCatalogs as prodCatalog>
              <#if curProdCatalogId?exists && curProdCatalogId == prodCatalog.prodCatalogId>
                <#assign prodCatalogCategories = prodCatalog.getRelatedOrderByCache("ProdCatalogCategory", sortList)>
                <div class='browsecategorytext'>-&nbsp;<a href="<@ofbizUrl>/EditProdCatalog?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class='browsecategorybutton'>${prodCatalog.catalogName?if_exists}</a></div>
                  <div style='margin-left: 10px;'>
                  	<#list prodCatalogCategories as prodCatalogCategory>
                      <#assign productCategory = prodCatalogCategory.getRelatedOneCache("ProductCategory")>
                      <div class='browsecategorytext'>-&nbsp;<a href='<@ofbizUrl>/EditCategory?CATALOG_TOP_CATEGORY=${prodCatalogCategory.productCategoryId}&productCategoryId=${prodCatalogCategory.productCategoryId}</@ofbizUrl>' class="browsecategorybutton">${(productCategory.description)?if_exists}</a></div>
                    </#list>
                  </div>
              <#else>
                <div class='browsecategorytext'>-&nbsp;<a href="<@ofbizUrl>/EditProdCatalog?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class='browsecategorybutton'>${prodCatalog.catalogName?if_exists}</a></div>
              </#if>
              </#list>
            </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</#if>
</table>
