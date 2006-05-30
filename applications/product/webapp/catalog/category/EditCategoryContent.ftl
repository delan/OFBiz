<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     Arukala  (arukala@gmx.de)
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Rev$
 *@since      2.2
-->
<script language="JavaScript" type="text/javascript">
function insertNowTimestamp(field) {
  eval('document.categoryForm.' + field + '.value="${nowTimestamp?string}";');
};

</script>

    <#if !(productCategory?exists)>
        <h3>${uiLabelMap.ProductCouldNotFindProduct} "${productCategoryId}".</h3>
    <#else>
        <table border="1" cellpadding="2" cellspacing="0">
        <tr class="tableheadtext">
            <td>${uiLabelMap.CommonContent}</td>
            <td>${uiLabelMap.CommonType}</td>
            <td>${uiLabelMap.CommonFrom}</td>
            <td>${uiLabelMap.CommonThru}</td>
            <td>${uiLabelMap.ProductPurchaseFrom}</td>
            <td>${uiLabelMap.ProductPurchaseThru}</td>
            <td>${uiLabelMap.ProductUseCount}</td>
            <td>${uiLabelMap.ProductUseDays}</td>
            <td>&nbsp;</td>
        </tr>
      
          <#list categoryContentList as entry>
            <#assign categoryContent=entry.categoryContent/>
            <#assign prodCatContentType=categoryContent.getRelatedOneCache("ProductCategoryContentType")/>
            <tr class="tabletext">
                <td><a href="<@ofbizUrl>EditCategoryContentContent?productCategoryId=${categoryContent.productCategoryId}&amp;contentId=${categoryContent.contentId}&amp;prodCatContentTypeId=${categoryContent.prodCatContentTypeId}&amp;fromDate=${categoryContent.fromDate}</@ofbizUrl>" class="buttontext">${entry.content.get("description",locale)?default("[No description]")} [${entry.content.contentId}]</td>
                <td>${prodCatContentType.get("description",locale)?default(categoryContent.prodCatContentTypeId)}</td>
                <td>${categoryContent.fromDate?default("N/A")}</td>
                <td>${categoryContent.thruDate?default("N/A")}</td>
                <td>${categoryContent.purchaseFromDate?default("N/A")}</td>
                <td>${categoryContent.purchaseThruDate?default("N/A")}</td>
                <td>${categoryContent.useCountLimit?default("N/A")}</td>
                <td>${categoryContent.useDaysLimit?default("N/A")}</td>
                <td><a href="<@ofbizUrl>removeContentFromCategory?productCategoryId=${categoryContent.productCategoryId}&amp;contentId=${categoryContent.contentId}&amp;prodCatContentTypeId=${categoryContent.prodCatContentTypeId}&amp;fromDate=${categoryContent.fromDate}</@ofbizUrl>" class="buttontext">[Delete]</a></td>
                <td><a href="/content/control/EditContent?contentId=${categoryContent.contentId}&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class="buttontext">[${uiLabelMap.CommonEdit} ${uiLabelMap.Content} ${entry.content.contentId}]</td>
             </tr>
        </#list>
      
        </table>
    
   <div class="head2">${uiLabelMap.ProductAddProductCategoryContentFromDate}</div>
   
        <#if productCategoryId?has_content && productCategory?has_content>
           ${prepareAddCategoryContentWrapper.renderFormString()}  
        </#if>
        <div class="head2">${uiLabelMap.ProductAddContentCategory}</div>
        <#if productCategoryId?has_content && productCategory?has_content>
            ${addCategoryContentWrapper.renderFormString()}   
        </#if>

        <hr class="sepbar"/>
        
        <div class="head2">${uiLabelMap.ProductOverrideSimpleFields}</div>
        <form action="<@ofbizUrl>updateCategoryContent</@ofbizUrl>" method="post" style="margin: 0;" name="categoryForm">
        <table border="0" cellpadding="2" cellspacing="0">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
    <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductProductCategoryType}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <select name="productCategoryTypeId" size="1" class="selectBox">
        <option value="">&nbsp;</option>
        <#list productCategoryTypes as productCategoryTypeData>
          <option <#if productCategory?has_content><#if productCategory.productCategoryTypeId==productCategoryTypeData.productCategoryTypeId> selected</#if></#if> value="${productCategoryTypeData.productCategoryTypeId}">${productCategoryTypeData.get("description",locale)}</option>
       </#list>
      </select>
    </td>
  </tr>    
        
  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductDescription}</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan="4" valign="top">
        <textarea class="textAreaBox" name="description" cols="60" rows="2">${(productCategory.description)?if_exists}</textarea>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right" valign="top"><div class="tabletext">${uiLabelMap.ProductLongDescription}</div></td>
    <td>&nbsp;</td>
    <td width="80%" colspan="4" valign="top">
       <textarea class="textAreaBox" name="longDescription" cols="60" rows="7">${(productCategory.longDescription)?if_exists}</textarea>
    </td>
  </tr>
  <tr>
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductDetailScreen}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
        <input type="text" <#if productCategory?has_content>value="${productCategory.detailScreen?if_exists}"</#if> name="detailScreen" size="60" maxlength="250" class="inputBox">
        <br/><span class="tabletext">${uiLabelMap.ProductDefaultsTo} &quot;categorydetail&quot;, ${uiLabelMap.ProductDetailScreenMessage}: &quot;component://ecommerce/widget/CatalogScreens.xml#categorydetail&quot;</span>
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
