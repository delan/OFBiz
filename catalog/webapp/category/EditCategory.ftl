<#if hasPermission>

<#if productCategoryId?has_content> 
  <div class='tabContainer'>
    <a href="<@ofbizUrl>/EditCategory?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButtonSelected">Category</a>
    <a href="<@ofbizUrl>/EditCategoryRollup?showProductCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Rollup</a>
    <a href="<@ofbizUrl>/EditCategoryProducts?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Products</a>
    <a href="<@ofbizUrl>/EditCategoryProdCatalogs?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Catalogs</a>
    <a href="<@ofbizUrl>/EditCategoryFeatureCats?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">FeatureCats</a>
    <a href="<@ofbizUrl>/EditCategoryParties?productCategoryId=${productCategoryId}</@ofbizUrl>" class="tabButton">Parties</a> 
  </div>
</#if> 

<div class="head1">Category <span class='head2'>  <#if productCategory?has_content> 
${productCategory.description?if_exists} 
</#if>[ID:${productCategoryId?if_exists}]</span></div>
<a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[New Category]</a>
<#if productCategoryId?has_content> 
  <a href="/ecommerce/control/category?category_id=${productCategoryId}" class="buttontext" target='_blank'>[Category Page]</a>
  <a href="<@ofbizUrl>/createProductInCategoryStart?productCategoryId=${productCategoryId}</@ofbizUrl>" class="buttontext">[Create Product In Category]</a>
</#if>
<br>
<br>

<#if ! productCategory?has_content> 
  <#if productCategoryId?has_content> 
    <h3>Could not find Product Category with ID "${productCategoryId}".</h3>
    <form action="<@ofbizUrl>/createProductCategory</@ofbizUrl>" method=POST style='margin: 0;' name='productCategoryForm'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">Product Category ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="productCategoryId" size="20" maxlength="40" value="${productCategoryId}" class='inputBox'>
      </td>
    </tr>
  <#else>
    <form action="<@ofbizUrl>/createProductCategory</@ofbizUrl>" method=POST style='margin: 0;' name='productCategoryForm'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <tr>
      <td align=right><div class="tabletext">Product Category ID</div></td>
      <td>&nbsp;</td>
      <td>
        <input type="text" name="productCategoryId" size="20" maxlength="40" value="" class='inputBox'>
      </td>
    </tr>
  </#if>
<#else>
  <form action="<@ofbizUrl>/updateProductCategory</@ofbizUrl>" method=POST style='margin: 0;' name='productCategoryForm'>
  <table border='0' cellpadding='2' cellspacing='0'>
  <input type=hidden name="productCategoryId" value="${productCategoryId}">
  <tr>
    <td align=right><div class="tabletext">Product Category ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b>${productCategoryId}</b> (This cannot be changed without re-creating the category.)
    </td>
  </tr>
</#if>

      <tr>
        <td width="26%" align=right><div class="tabletext">ProductCategory Type</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <select name="productCategoryTypeId" size=1 class='selectBox'>
	    <option value=''>&nbsp;</option>
	    <#list productCategoryTypes as productCategoryTypeData>
	    <option <#if productCategoryId?has_content><#if productCategory.productCategoryTypeId==productCategoryTypeData.productCategoryTypeId>selected='${productCategoryTypeData.description}'</#if></#if> value='${productCategoryTypeData.productCategoryTypeId}'>${productCategoryTypeData.description}</option>
            
           </#list>
          </select>
        </td>
      </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext">Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" <#if productCategoryId?has_content>value="${productCategory.description?if_exists}"</#if> name="description" size="60" maxlength="60" class='inputBox'></td>
  </tr>
  <tr>
    <td width="26%" align=right valign=top><div class="tabletext">Long Description</div></td>
    <td>&nbsp;</td>
    <td width="74%"><textarea cols="60" rows="3" name="longDescription" maxlength="2000" class='textAreaBox'><#if productCategoryId?has_content>${productCategory.longDescription?if_exists}</#if></textarea></td>
  </tr>

<#if productCategoryId?has_content> 
    <SCRIPT language='JavaScript'>
    function insertImageName(type,ext) {
      eval('document.forms.productCategoryForm.' + type + 'ImageUrl.value="<%=UtilProperties.getPropertyValue(catalogPropertiesURL, "image.url.prefix")%>/category.${productCategoryId}.' + type + '.' + ext + '";');
    };
    </SCRIPT>
</#if>
  <tr>
    <td width="26%" align=right><div class="tabletext">Category Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" <#if productCategoryId?has_content>value="${productCategory.categoryImageUrl?if_exists}"</#if> name="categoryImageUrl" size="60" maxlength="250" class='inputBox'>
      <#if productCategoryId?has_content> 
        <div>
          <a href="<@ofbizUrl>/UploadCategoryImage?productCategoryId=${productCategoryId}&upload_file_type=category</@ofbizUrl>" class="buttontext">[Upload Category Image]</a>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('category', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('category', 'gif');" class="buttontext">[.gif]</a>
        </div>
      </#if>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Link One Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" <#if productCategoryId?has_content>value="${productCategory.linkOneImageUrl?if_exists}"</#if> name="linkOneImageUrl" size="60" maxlength="250" class='inputBox'>
      <#if productCategoryId?has_content> 
        <div>
          <a href="<@ofbizUrl>/UploadCategoryImage?productCategoryId=${productCategoryId}&upload_file_type=linkOne</@ofbizUrl>" class="buttontext">[Upload Link One Image]</a>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('linkOne', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('linkOne', 'gif');" class="buttontext">[.gif]</a>
        </div>
      </#if>
    </td>
  </tr>
  <tr>
    <td width="26%" align=right><div class="tabletext">Link Two Image URL</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <input type="text" <#if productCategoryId?has_content>value="${productCategory.linkTwoImageUrl?if_exists}"</#if> name="linkTwoImageUrl" size="60" maxlength="250" class='inputBox'>
      <#if productCategoryId?has_content> 
        <div>
          <a href="<@ofbizUrl>/UploadCategoryImage?productCategoryId=${productCategoryId}&upload_file_type=linkTwo</@ofbizUrl>" class="buttontext">[Upload Link Two Image]</a>
          <span class='tabletext'>Insert Default Image URL: </span>
          <a href="javascript:insertImageName('linkTwo', 'jpg');" class="buttontext">[.jpg]</a>
          <a href="javascript:insertImageName('linkTwo', 'gif');" class="buttontext">[.gif]</a>
        </div>
      </#if>
    </td>
  </tr>

  <tr>
    <td width="26%" align=right><div class="tabletext">Detail Template</div></td>
    <td>&nbsp;</td>
    <td width="74%" colspan='4'>
        <input type="text" <#if productCategoryId?has_content>value="${productCategory.detailTemplate?if_exists}"</#if> name="detailTemplate" size="60" maxlength="250" class='inputBox'>
        <br><span class='tabletext'>Defaults to "/catalog/categorydetail.ftl"</span>
    </td>
  </tr>

  <tr>
  <#--
      <td><div class='tabletext'>Primary Parent Category ID:</div></td>
      <td><select name='primaryParentCategoryId' size=1 class='selectbox'>
        <option value=''>&nbsp;</option>
        <#list productFeatureCategories as productFeatureCategory>
          <option value='${productCategory.primaryParentCategoryId}'>${productCategory.description?if_exists} [${productCategory. primaryParentCategoryId}]</option>
        </#list>
      </select></td>  -->
  </tr>
  <tr>
    <td colspan='2'>&nbsp;</td>
    <td><input type="submit" name="Update" value="Update" style='font-size: x-small;'></td>
  </tr>
</table>
</form>
<br>

<#else>
  <h3>You do not have permission to view this page.  ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
