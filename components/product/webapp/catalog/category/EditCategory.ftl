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
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel )
 *@version    $Rev$
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>

${pages.get("/category/CategoryTabBar.ftl")}

<div class="head1">${uiLabelMap.ProductCategory} <span class="head2">  <#if productCategory?has_content> 
${productCategory.description?if_exists} 
</#if>[${uiLabelMap.CommonId}:${productCategoryId?if_exists}]</span></div>
<a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewCategory}]</a>
<#if productCategoryId?has_content> 
  <a href="/ecommerce/control/category?category_id=${productCategoryId}" class="buttontext" target="_blank">[${uiLabelMap.ProductCategoryPage}]</a>
  <a href="<@ofbizUrl>/createProductInCategoryStart?productCategoryId=${productCategoryId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductCreateProductInCategory}]</a>
  <a href="<@ofbizUrl>/advancedsearch?SEARCH_CATEGORY_ID=${productCategoryId}</@ofbizUrl>" class="buttontext">[Search in Category]</a>
</#if>
<br>
<br>

<#if ! productCategory?has_content> 
  <#if productCategoryId?has_content> 
    <h3>${uiLabelMap.ProductCouldNotFindProductCategoryWithId} "${productCategoryId}".</h3>
    <form action="<@ofbizUrl>/createProductCategory</@ofbizUrl>" method=POST style="margin: 0;" name="productCategoryForm">
    <table border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td align=right><div class="tabletext">${uiLabelMap.ProductProductCategoryId}</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="productCategoryId" size="20" maxlength="40" value="${productCategoryId}" class="inputBox">
      </td>
    </tr>
  <#else>
    <form action="<@ofbizUrl>/createProductCategory</@ofbizUrl>" method=POST style="margin: 0;" name="productCategoryForm">
    <table border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td align=right><div class="tabletext">${uiLabelMap.ProductProductCategoryId}</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="productCategoryId" size="20" maxlength="40" value="" class="inputBox">
      </td>
    </tr>
  </#if>
<#else>
  <form action="<@ofbizUrl>/updateProductCategory</@ofbizUrl>" method=POST style="margin: 0;" name="productCategoryForm">
  <table border="0" cellpadding="2" cellspacing="0">
  <input type=hidden name="productCategoryId" value="${productCategoryId}">
  <tr>
    <td align=right><div class="tabletext">${uiLabelMap.ProductProductCategoryId}</div></td>
    <td>&nbsp;</td>
    <td>
      <b>${productCategoryId}</b> (${uiLabelMap.ProductNotModificationRecrationCategory}.)
    </td>
  </tr>
</#if>

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
<#--
  <tr>
    <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductDescription}</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" <#if productCategory?has_content>value="${productCategory.description?if_exists}"</#if> name="description" size="60" maxlength="60" class="inputBox"></td>
  </tr>
  <tr>
    <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.ProductLongDescription}</div></td>
    <td>&nbsp;</td>
    <td width="74%"><textarea cols="60" rows="3" name="longDescription" maxlength="2000" class="textAreaBox"><#if productCategory?has_content>${productCategory.longDescription?if_exists}</#if></textarea></td>
  </tr>
-->
<#if productCategoryId?has_content> 
    <SCRIPT language="JavaScript">
    function insertImageName(type,ext) {
        var imageUrlPrefix = '${Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("catalog", "image.url.prefix")}';
        eval('document.forms.productCategoryForm.' + type + 'ImageUrl.value="'+imageUrlPrefix+'.${productCategoryId}.' + type + '.' + ext + '";');
    };
    </SCRIPT>
</#if>
  <tr>
    <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductCategoryImageUrl}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" <#if productCategory?has_content>value="${productCategory.categoryImageUrl?if_exists}"</#if> name="categoryImageUrl" size="60" maxlength="250" class="inputBox">
      <#if productCategoryId?has_content> 
        <div>
          <a href="<@ofbizUrl>/UploadCategoryImage?productCategoryId=${productCategoryId}&upload_file_type=category</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductUploadCategoryImage}]</a>
          <span class="tabletext">${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
          <a href="javascript:insertImageName('category', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('category', 'gif');" class="buttontext">[.gif]</a>
        </div>
      </#if>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductLinkOneImageUrl}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" <#if productCategory?has_content>value="${productCategory.linkOneImageUrl?if_exists}"</#if> name="linkOneImageUrl" size="60" maxlength="250" class="inputBox">
      <#if productCategoryId?has_content> 
        <div>
          <a href="<@ofbizUrl>/UploadCategoryImage?productCategoryId=${productCategoryId}&upload_file_type=linkOne</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductUploadLinkOneImage}]</a>
          <span class="tabletext">${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
          <a href="javascript:insertImageName('linkOne', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('linkOne', 'gif');" class="buttontext">[.gif]</a>
        </div>
      </#if>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">${uiLabelMap.ProductLinkTwoImageUrl}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" <#if productCategory?has_content>value="${productCategory.linkTwoImageUrl?if_exists}"</#if> name="linkTwoImageUrl" size="60" maxlength="250" class="inputBox">
      <#if productCategoryId?has_content> 
        <div>
          <a href="<@ofbizUrl>/UploadCategoryImage?productCategoryId=${productCategoryId}&upload_file_type=linkTwo</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductUploadLinkTwoImage}]</a>
          <span class="tabletext">${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
          <a href="javascript:insertImageName('linkTwo', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('linkTwo', 'gif');" class="buttontext">[.gif]</a>
        </div>
      </#if>
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
    <td width="26%" align="right"><div class="tabletext">${uiLabelMap.ProductPrimaryParentCategory}</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <select name="primaryParentCategoryId" size=1 class="selectbox">
        <#if productCategory?has_content> 
        <#if (productCategory.primaryParentCategoryId)?exists>
          <option value="${productCategory.primaryParentCategoryId}">${(primaryParentCategory.description)?if_exists} [${productCategory. primaryParentCategoryId}]</option>
        </#if>
		</#if>
        <option value="">&nbsp;</option>
        <#list categoryList as curProductCategory>
          <option value="${curProductCategory.productCategoryId}">${curProductCategory.description?if_exists} [${curProductCategory.productCategoryId}]</option>
        </#list>
      </select>
    </td>
  </tr>
  <tr>
    <td colspan="2">&nbsp;</td>
    <td><input type="submit" name="Update" value="${uiLabelMap.CommonUpdate}" style="font-size: x-small;"></td>
  </tr>
</table>
</form>
<br>

<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
