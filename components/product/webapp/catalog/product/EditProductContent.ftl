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
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Revision: 1.7 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<SCRIPT language="JavaScript">
function insertNowTimestamp(field) {
  eval('document.productForm.' + field + '.value="${nowTimestampString}";');
};
function insertImageName(size,ext) {
  eval('document.productForm.' + size + 'ImageUrl.value="${Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue(catalogPropertiesURL, "image.url.prefix")}/product.${productId}.' + size + '.' + ext + '";');
};
</SCRIPT>

<#if hasPermission>
    <#if fileType?has_content>
        <div class='head3'>${uiLabelMap.ProductResultOfImageUpload}</div>
        <#if !(clientFileName?has_content)>
            <div class='tabletext'>${uiLabelMap.ProductNoFileSpecifiedForUpload}.</div>
        <#else>
            <div class='tabletext'>${uiLabelMap.ProductTheFileOnYourComputer}: <b>${clientFileName?if_exists}</b></div>
            <div class='tabletext'>${uiLabelMap.ProductServerFileName}: <b>${fileNameToUse?if_exists}</b></div>
            <div class='tabletext'>${uiLabelMap.ProductServerDirectory}: <b>${imageServerPath?if_exists}</b></div>
            <div class='tabletext'>${uiLabelMap.ProductTheUrlOfYourUploadedFile}: <b><a href="<@ofbizContentUrl>${imageUrl?if_exists}</@ofbizContentUrl>">${imageUrl?if_exists}</a></b></div>
        </#if>
    <br>
    </#if>    

${pages.get("/product/ProductTabBar.ftl")}

    <div class="head1">${uiLabelMap.ProductContent} <span class="head2">${uiLabelMap.CommonFor} <#if product?exists>${(product.internalName)?if_exists}</#if>[${uiLabelMap.CommonId}:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewProduct}]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[${uiLabelMap.ProductProductPage}]</a>
    </#if>
    <p>
    
    <#if !(product?exists)>
        <h3>${uiLabelMap.ProductCouldNotFindProduct} "${productId}".</h3>
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
        <#list productContentList as entry>
            <#assign productContent=entry.productContent/>
            <#assign productContentType=productContent.getRelatedOneCache("ProductContentType")/>
            <tr class="tabletext">
                <td><a href="<@ofbizUrl>/EditProductContentContent?productId=${productContent.productId}&amp;contentId=${productContent.contentId}&amp;productContentTypeId=${productContent.productContentTypeId}&amp;fromDate=${productContent.fromDate}</@ofbizUrl>" class="buttontext">${entry.content.description?default("[No description]")} [${entry.content.contentId}]</td>
                <td>${productContentType.description?default(productContent.productContentTypeId)}</td>
                <td>${productContent.fromDate?default("N/A")}</td>
                <td>${productContent.thruDate?default("N/A")}</td>
                <td>${productContent.purchaseFromDate?default("N/A")}</td>
                <td>${productContent.purchaseThruDate?default("N/A")}</td>
                <td>${productContent.useCountLimit?default("N/A")}</td>
                <td>${productContent.useDaysLimit?default("N/A")}</td>
                <td><a href="<@ofbizUrl>/removeContentFromProduct?productId=${productContent.productId}&amp;contentId=${productContent.contentId}&amp;productContentTypeId=${productContent.productContentTypeId}&amp;fromDate=${productContent.fromDate}</@ofbizUrl>" class="buttontext">[Delete]</a></td>
                <td><a href="/content/control/EditContent?contentId=${productContent.contentId}&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class="buttontext">[Edit Content ${entry.content.contentId}]</td>
             </tr>
        </#list>
        </table>
        <div class="head2">Create New Product Content</div>
        <#if productId?has_content && product?has_content>
            ${prepareAddProductContentWrapper.renderFormString()}
        </#if>
        <div class="head2">Add Content to Product</div>
        <#if productId?has_content && product?has_content>
            ${addProductContentWrapper.renderFormString()}
        </#if>

        <hr class="sepbar"/>
        
        <div class="head2">Override Simple Fields</div>
        <form action="<@ofbizUrl>/updateProductContent</@ofbizUrl>" method=POST style="margin: 0;" name="productForm">
        <table border="0" cellpadding="2" cellspacing="0">
        <input type=hidden name="productId" value="${productId?if_exists}">
        <tr>
            <td width="20%" align=right valign=top><div class="tabletext"><b>${uiLabelMap.ProductProductName}</b></div></td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
                <input type="text" class="inputBox" name="productName" value="${(product.productName)?if_exists}" size="30" maxlength="60">
            </td>
        </tr>        
        <tr>
            <td width="20%" align=right valign=top><div class="tabletext"><b>${uiLabelMap.ProductProductDescription}</b></div></td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
                <textarea class="textAreaBox" name="description" cols="60" rows="2">${(product.description)?if_exists}</textarea>
            </td>
        </tr>        
        <tr>
            <td width="20%" align=right valign=top><div class="tabletext"><b>Long Description</b></div></td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
                <textarea class="textAreaBox" name="longDescription" cols="60" rows="7">${(product.longDescription)?if_exists}</textarea>
            </td>
        </tr>        
        <tr>
            <td width="20%" align=right valign=top><div class="tabletext"><b>${uiLabelMap.ProductDetailTemplate}</b></div></td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
                <input type="text" class="inputBox" name="detailTemplate" value="${(product.detailTemplate)?if_exists}" size="60" maxlength="250">
                <br><span class="tabletext">${uiLabelMap.ProductIfNotSpecifiedDefaultsIsProductdetail} "/catalog/productdetail.ftl"</span>
            </td>
        </tr>        
        <tr>
            <td width="20%" align=right valign=top>
                <div class="tabletext"><b>${uiLabelMap.ProductSmallImage}</b></div>
                <#if (product.smallImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${(product.smallImageUrl)?if_exists}</@ofbizContentUrl>" target="_blank"><img alt="Small Image" src="<@ofbizContentUrl>${(product.smallImageUrl)?if_exists}</@ofbizContentUrl>" height="40" width="40"></a>
                </#if>
            </td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
            <input type="text" class="inputBox" name="smallImageUrl" value="${(product.smallImageUrl)?if_exists}" size="60" maxlength="255">
            <#if productId?has_content>
                <div>
                <span class="tabletext">${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                <a href="javascript:insertImageName('small', 'jpg');" class="buttontext">[.jpg]</a>
                <a href="javascript:insertImageName('small', 'gif');" class="buttontext">[.gif]</a>
                </div>
            </#if>
            </td>
        </tr>
        <tr>
            <td width="20%" align=right valign=top>
                <div class="tabletext"><b>${uiLabelMap.ProductMediumImage}</b></div>
                <#if (product.mediumImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.mediumImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Medium Image" src="<@ofbizContentUrl>${product.mediumImageUrl}</@ofbizContentUrl>" height="40" width="40"></a>
                </#if>
            </td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
            <input type="text" class="inputBox" name="mediumImageUrl" value="${(product.mediumImageUrl)?if_exists}" size="60" maxlength="255">
            <#if productId?has_content>
                <div>
                <span class="tabletext">${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                <a href="javascript:insertImageName('medium', 'jpg');" class="buttontext">[.jpg]</a>
                <a href="javascript:insertImageName('medium', 'gif');" class="buttontext">[.gif]</a>
                </div>
            </#if>
            </td>
        </tr>
        <tr>
            <td width="20%" align=right valign=top>
                <div class="tabletext"><b>${uiLabelMap.ProductLargeImage}</b></div>
                <#if (product.largeImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.largeImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Large Image" src="<@ofbizContentUrl>${product.largeImageUrl}</@ofbizContentUrl>" height="40" width="40"></a>
                </#if>
            </td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
            <input type="text" class="inputBox" name="largeImageUrl" value="${(product.largeImageUrl)?if_exists}" size="60" maxlength="255">
            <#if productId?has_content>
                <div>
                <span class="tabletext">${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                <a href="javascript:insertImageName('large', 'jpg');" class="buttontext">[.jpg]</a>
                <a href="javascript:insertImageName('large', 'gif');" class="buttontext">[.gif]</a>
                </div>
            </#if>
            </td>
        </tr>
        <tr>
            <td width="20%" align=right valign=top>
                <div class="tabletext"><b>${uiLabelMap.ProductDetailImage}</b></div>
                <#if (product.detailImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.detailImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Detail Image" src="<@ofbizContentUrl>${product.detailImageUrl}</@ofbizContentUrl>" height="40" width="40"></a>
                </#if>
            </td>
            <td>&nbsp;</td>
            <td width="80%" colspan="4" valign=top>
            <input type="text" class="inputBox" name="detailImageUrl" value="${(product.detailImageUrl)?if_exists}" size="60" maxlength="255">
            <#if productId?has_content>
                <div>
                <span class="tabletext">${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                <a href="javascript:insertImageName('detail', 'jpg');" class="buttontext">[.jpg]</a>
                <a href="javascript:insertImageName('detail', 'gif');" class="buttontext">[.gif]</a>
                </div>
            </#if>
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

        <SCRIPT language="JavaScript">
            function setUploadUrl(newUrl) {
            var toExec = 'document.imageUploadForm.action="' + newUrl + '";';
            eval(toExec);
            };
        </SCRIPT>
        <div class="head3">${uiLabelMap.ProductUploadImage}</div>
        <form method="POST" enctype="multipart/form-data" action="<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=small</@ofbizUrl>" name="imageUploadForm">
            <input type="file" class="inputBox" size="50" name="fname">
            <br>
            <span class="tabletext">
                <input class="radioButton" type=RADIO name="upload_file_type_bogus" value="small" checked onclick='setUploadUrl("<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=small</@ofbizUrl>");'>${uiLabelMap.CommonSmall}
                <input class="radioButton" type=RADIO name="upload_file_type_bogus" value="medium" onclick='setUploadUrl("<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=medium</@ofbizUrl>");'>${uiLabelMap.CommonMedium}
                <input class="radioButton" type=RADIO name="upload_file_type_bogus" value="large"onclick='setUploadUrl("<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=large</@ofbizUrl>");'>${uiLabelMap.CommonLarge}
                <input class="radioButton" type=RADIO name="upload_file_type_bogus" value="detail" onclick='setUploadUrl("<@ofbizUrl>/UploadProductImage?productId=${productId}&upload_file_type=detail</@ofbizUrl>");'>${uiLabelMap.CommonDetail}
            </span>
            <input type="submit" class="smallSubmit" value="${uiLabelMap.ProductUploadImage}">
        </form>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductViewPermissionError}</h3>
</#if>
