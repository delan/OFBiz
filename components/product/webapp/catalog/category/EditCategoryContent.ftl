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
 *@author     Brad Steiner (bsteiner@thehungersite.com)
 *@author	  Arukala  (arukala@gmx.de)
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Rev:$
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<SCRIPT language="JavaScript">
function insertNowTimestamp(field) {
  eval('document.categoryForm.' + field + '.value="${nowTimestampString}";');
};

</SCRIPT>

<#if hasPermission>

${pages.get("/category/CategoryTabBar.ftl")}

    <div class="head1">${uiLabelMap.ProductCategoryContent}  <span class="head2">${uiLabelMap.CommonFor} <#if productCategory?exists>${(productCategory.description)?if_exists}</#if> [${uiLabelMap.CommonId}:${productCategoryId?if_exists}]</span></div>
 
    <a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewCategory}]</a>
    <#if productCategoryId?has_content>
        <a href="/ecommerce/control/category?category_id=${productCategoryId}" class="buttontext" target="_blank">[${uiLabelMap.ProductCategoryPage}]</a>
    </#if>
    
    <p>
 
    <#if !(productCategory?exists)>
        <h3>${uiLabelMap.ProductCouldNotFindProduct} "${productCategoryId}".</h3>
    <#else>
        <table border="1" cellpadding="2" cellspacing="0" width="100%">
        <tr class="tableheadtext">
            <td>Content</td>
            <td>Type</td>
            <td>From</td>
            <td>Thru</td>
            <td>Purchase From</td>
            <td>Purchase Thru</td>
            <td>Use Count</td>
            <td>Use Days</td>
            <td>&nbsp;</td>
        </tr>
      
          <#list categoryContentList as entry>
            <#assign categoryContent=entry.categoryContent/>
            <#assign prodCatContentType=categoryContent.getRelatedOneCache("ProductCategoryContentType")/>
            <tr class="tabletext">
                <td><a href="<@ofbizUrl>/EditCategoryContentContent?productCategoryId=${categoryContent.productCategoryId}&amp;contentId=${categoryContent.contentId}&amp;prodCatContentTypeId=${categoryContent.prodCatContentTypeId}&amp;fromDate=${categoryContent.fromDate}</@ofbizUrl>" class="buttontext">${entry.content.description?default("[No description]")} [${entry.content.contentId}]</td>
                <td>${prodCatContentType.description?default(categoryContent.prodCatContentTypeId)}</td>
                <td>${categoryContent.fromDate?default("N/A")}</td>
                <td>${categoryContent.thruDate?default("N/A")}</td>
                <td>${categoryContent.purchaseFromDate?default("N/A")}</td>
                <td>${categoryContent.purchaseThruDate?default("N/A")}</td>
                <td>${categoryContent.useCountLimit?default("N/A")}</td>
                <td>${categoryContent.useDaysLimit?default("N/A")}</td>
                <td><a href="<@ofbizUrl>/removeContentFromCategory?productCategoryId=${categoryContent.productCategoryId}&amp;contentId=${categoryContent.contentId}&amp;prodCatContentTypeId=${categoryContent.prodCatContentTypeId}&amp;fromDate=${categoryContent.fromDate}</@ofbizUrl>" class="buttontext">[Delete]</a></td>
                <td><a href="/content/control/EditContent?contentId=${categoryContent.contentId}&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class="buttontext">[Edit Content ${entry.content.contentId}]</td>
             </tr>
        </#list>
      
        </table>
    
   <div class="head2">Create New Category Content</div>
   
        <#if productCategoryId?has_content && productCategory?has_content>
           ${prepareAddCategoryContentWrapper.renderFormString()}  
        </#if>
        <div class="head2">Add Content to Category</div>
        <#if productCategoryId?has_content && productCategory?has_content>
            ${addCategoryContentWrapper.renderFormString()}   
        </#if>

        <hr class="sepbar"/>
        
        <div class="head2">Override Simple Fields</div>
        <form action="<@ofbizUrl>/updateCategoryContent</@ofbizUrl>" method=POST style="margin: 0;" name="categoryForm">
        <table border="0" cellpadding="2" cellspacing="0">
        <input type=hidden name="productCategoryId" value="${productCategoryId?if_exists}">
    <tr>
    <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductProductCategoryType}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <select name="productCategoryTypeId" size=1 class="selectBox">
        <option value="">&nbsp;</option>
        <#list productCategoryTypes as productCategoryTypeData>
          <option <#if productCategory?has_content><#if productCategory.productCategoryTypeId==productCategoryTypeData.productCategoryTypeId> selected</#if></#if> value="${productCategoryTypeData.productCategoryTypeId}">${productCategoryTypeData.description}</option>
       </#list>
      </select>
    </td>
  </tr>    
        
  <tr>
    <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductDescription}</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan="4" valign=top>
        <textarea class="textAreaBox" name="description" cols="60" rows="2">${(productCategory.description)?if_exists}</textarea>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.ProductLongDescription}</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan="4" valign=top>
       <textarea class="textAreaBox" name="longDescription" cols="60" rows="7">${(productCategory.longDescription)?if_exists}</textarea>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductDetailTemplate}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
        <input type="text" <#if productCategory?has_content>value="${productCategory.detailTemplate?if_exists}"</#if> name="detailTemplate" size="60" maxlength="250" class="inputBox">
        <br><span class="tabletext">Defaults to "/catalog/categorydetail.ftl"</span>
    </td>
  </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
            <td><input type="submit" name="Update" value="${uiLabelMap.CommonUpdate}"></td>
            <td colspan="3">&nbsp;</td>
        </tr>
        </table>
        </form>

        <hr class="sepbar"/>
 
   </#if> 
<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
