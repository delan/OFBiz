<SCRIPT language="JavaScript">
function insertNowTimestamp(field) {
  eval('document.productForm.' + field + '.value="${nowTimestampString}";');
};
function insertImageName(size,ext) {
  eval('document.productForm.' + size + 'ImageUrl.value="${Static["org.ofbiz.core.util.UtilProperties"].getPropertyValue(catalogPropertiesURL, "image.url.prefix")}/product.${productId}.' + size + '.' + ext + '";');
};
</SCRIPT>

<#if hasPermission>
    <#if fileType?has_content>
        <div class='head3'>Result of Image Upload</div>
        <#if !(clientFileName?has_content)>
            <div class='tabletext'>No file specified for upload.</div>
        <#else>
            <div class='tabletext'>The file on your computer: <b>${clientFileName?if_exists}</b></div>
            <div class='tabletext'>Server file name: <b>${fileNameToUse?if_exists}</b></div>
            <div class='tabletext'>Server directory: <b>${imageServerPath?if_exists}</b></div>
            <div class='tabletext'>The URL of your uploaded file: <b><a href="<@ofbizContentUrl>${imageUrl?if_exists}</@ofbizContentUrl>">${imageUrl?if_exists}</a></b></div>
        </#if>
    </#if>    
    <br>
    <#if productId?has_content>
        <div class="tabContainer">
        <a href="<@ofbizUrl>/EditProduct?productId=${productId}</@ofbizUrl>" class="tabButton">Product</a>
        <a href="<@ofbizUrl>/EditProductPrices?productId=${productId}</@ofbizUrl>" class="tabButton">Prices</a>
        <a href="<@ofbizUrl>/EditProductContent?productId=${productId}</@ofbizUrl>" class="tabButtonSelected">Content</a>
        <a href="<@ofbizUrl>/EditProductGoodIdentifications?productId=${productId}</@ofbizUrl>" class="tabButton">IDs</a>
        <a href="<@ofbizUrl>/EditProductCategories?productId=${productId}</@ofbizUrl>" class="tabButton">Categories</a>
        <a href="<@ofbizUrl>/EditProductKeyword?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Keywords</a>
        <a href="<@ofbizUrl>/EditProductAssoc?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Associations</a>
        <a href="<@ofbizUrl>/EditProductAttributes?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Attributes</a>
        <a href="<@ofbizUrl>/EditProductFeatures?productId=${productId}</@ofbizUrl>" class="tabButton">Features</a>
        <a href="<@ofbizUrl>/EditProductFacilities?productId=${productId}</@ofbizUrl>" class="tabButton">Facilities</a>
        <a href="<@ofbizUrl>/EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="tabButton">Inventory</a>
        <a href="<@ofbizUrl>/EditProductGlAccounts?productId=${productId}</@ofbizUrl>" class="tabButton">Accounts</a>
        <#if product?exists && product.isVirtual.equals("Y")>
            <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="tabButton">Variants</a>
        </#if>
        </div>
    </#if>

    <div class="head1">Content <span class="head2">for <#if product?exists>${(product.productName)?if_exists}</#if>[ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <p>
    
    <#if !(product?exists)>
        <h3>Could not find product with ID "${productId}".</h3>
    <#else>
        <form action="<@ofbizUrl>/updateProductContent</@ofbizUrl>" method=POST style="margin: 0;" name="productForm">
        <table border="0" cellpadding="2" cellspacing="0">
        <input type=hidden name="productId" value="${productId?if_exists}">
        <input type=hidden name="productName" value="${(product.productName)?if_exists}">
        <input type=hidden name="productTypeId" value="${(product.productTypeId)?if_exists}">        
        <tr>
            <td width="20%" align=right valign=top><div class="tabletext"><b>Detail Template</b></div></td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
                <input type="text" class="inputBox" name="detailTemplate" value="${(product.detailTemplate)?if_exists}" size="60" maxlength="250">
                <br><span class="tabletext">If not specified defaults to "/catalog/productdetail.jsp"</span>
            </td>
        </tr>        
        <tr>
            <td width="20%" align=right valign=top>
                <div class="tabletext"><b>Small Image</b></div>
                <#if (product.smallImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${(product.smallImageUrl)?if_exists}</@ofbizContentUrl>" target="_blank"><img alt="Small Image" src="<@ofbizContentUrl>${(product.smallImageUrl)?if_exists}</@ofbizContentUrl>" height="40" width="40"></a>
                </#if>
            </td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
            <input type="text" class="inputBox" name="smallImageUrl" value="${(product.smallImageUrl)?if_exists}" size="60" maxlength="255">
            <#if productId?has_content>
                <div>
                <span class="tabletext">Insert Default Image URL: </span>
                <a href="javascript:insertImageName('small', 'jpg');" class="buttontext">[.jpg]</a>
                <a href="javascript:insertImageName('small', 'gif');" class="buttontext">[.gif]</a>
                </div>
            </#if>
            </td>
        </tr>
        <tr>
            <td width="20%" align=right valign=top>
                <div class="tabletext"><b>Medium Image</b></div>
                <#if (product.mediumImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.mediumImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Medium Image" src="<@ofbizContentUrl>${product.mediumImageUrl}</@ofbizContentUrl>" height="40" width="40"></a>
                </#if>
            </td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
            <input type="text" class="inputBox" name="mediumImageUrl" value="${(product.mediumImageUrl)?if_exists}" size="60" maxlength="255">
            <#if productId?has_content>
                <div>
                <span class="tabletext">Insert Default Image URL: </span>
                <a href="javascript:insertImageName('medium', 'jpg');" class="buttontext">[.jpg]</a>
                <a href="javascript:insertImageName('medium', 'gif');" class="buttontext">[.gif]</a>
                </div>
            </#if>
            </td>
        </tr>
        <tr>
            <td width="20%" align=right valign=top>
                <div class="tabletext"><b>Large Image</b></div>
                <#if (product.largeImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.largeImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Large Image" src="<@ofbizContentUrl>${product.largeImageUrl}</@ofbizContentUrl>" height="40" width="40"></a>
                </#if>
            </td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
            <input type="text" class="inputBox" name="largeImageUrl" value="${(product.largeImageUrl)?if_exists}" size="60" maxlength="255">
            <#if productId?has_content>
                <div>
                <span class="tabletext">Insert Default Image URL: </span>
                <a href="javascript:insertImageName('large', 'jpg');" class="buttontext">[.jpg]</a>
                <a href="javascript:insertImageName('large', 'gif');" class="buttontext">[.gif]</a>
                </div>
            </#if>
            </td>
        </tr>
        <tr>
            <td width="20%" align=right valign=top>
                <div class="tabletext"><b>Detail Image</b></div>
                <#if (product.detailImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.detailImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Detail Image" src="<@ofbizContentUrl>${product.detailImageUrl}</@ofbizContentUrl>" height="40" width="40"></a>
                </#if>
            </td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
            <input type="text" class="inputBox" name="detailImageUrl" value="${(product.detailImageUrl)?if_exists}" size="60" maxlength="255">
            <#if productId?has_content>
                <div>
                <span class="tabletext">Insert Default Image URL: </span>
                <a href="javascript:insertImageName('detail', 'jpg');" class="buttontext">[.jpg]</a>
                <a href="javascript:insertImageName('detail', 'gif');" class="buttontext">[.gif]</a>
                </div>
            </#if>
            </td>
        </tr>        
        <tr>
            <td colspan="2">&nbsp;</td>
            <td><input type="submit" name="Update" value="Update"></td>
            <td colspan="3">&nbsp;</td>
        </tr>
        </table>
        </form>
        <hr class="sepbar">
        <SCRIPT language="JavaScript">
            function setUploadUrl(newUrl) {
            var toExec = 'document.imageUploadForm.action="' + newUrl + '";';
            eval(toExec);
            };
        </SCRIPT>
        <div class="head3">Upload Image</div>
        <form method="POST" enctype="multipart/form-data" action="<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=small</@ofbizUrl>" name="imageUploadForm">
            <input type="file" class="inputBox" size="50" name="fname">
            <br>
            <span class="tabletext">
                <input class="radioButton" type=RADIO name="upload_file_type_bogus" value="small" checked onclick='setUploadUrl("<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=small</@ofbizUrl>");'>Small
                <input class="radioButton" type=RADIO name="upload_file_type_bogus" value="medium" onclick='setUploadUrl("<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=medium</@ofbizUrl>");'>Medium
                <input class="radioButton" type=RADIO name="upload_file_type_bogus" value="large"onclick='setUploadUrl("<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=large</@ofbizUrl>");'>Large
                <input class="radioButton" type=RADIO name="upload_file_type_bogus" value="detail" onclick='setUploadUrl("<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=detail</@ofbizUrl>");'>Detail
            </span>
            <input type="submit" value="Upload Image">
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
