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
 *@author     Dustin Caldwell (dustin@dscv.org)
 *@version    $Rev$
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>
    <#assign externalKeyParam = "&externalLoginKey=" + requestAttributes.externalLoginKey?if_exists>

    ${pages.get("/product/ProductTabBar.ftl")}

    <!--              Name update section -->
    <form action="<@ofbizUrl>updateProductQuickAdminName</@ofbizUrl>" method=POST style="margin: 0;" name="editProduct">
        <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
            <tr>
                <input type=hidden name="productId" value="${productId?if_exists}">
                <#if (product.isVirtual)?if_exists == "Y">
                    <input type=hidden name="isVirtual" value="Y">
                </#if>
                <td><span class="head2">[${productId?if_exists}]</span></div></td>
                <td><input type="text" class="inputBox" name="productName" size="40" maxlength="40" value="${product.productName?if_exists}"></td>
                <td><input type="submit" value="${uiLabelMap.UpdateName}"></td>
            </tr>
        </table>
    </form>

    <!-- ***************************************************** Selectable features section -->
    <#if (product.isVirtual)?if_exists == "Y">
        <hr>
        <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
            <form action="<@ofbizUrl>updateProductQuickAdminSelFeat</@ofbizUrl>" method=POST style="margin: 0;" name="selectableFeature">
            <input type=hidden name="productId" value="${product.productId?if_exists}">
            <tr>
                <td colspan=2><span class="head2">${uiLabelMap.SelectableFeatures}</span></td>
                <td colspan=2>Type
                    <select name=productFeatureTypeId>
                        <option value="~~any~~">${uiLabelMap.AnyFeatureType}
                        <#list featureTypes as featureType>
                            <#if (featureType.productFeatureTypeId)?if_exists == (productFeatureTypeId)?if_exists>
                                <#assign selected="selected"/>
                            <#else>
                                <#assign selected=""/>
                            </#if>
                            <option ${selected} value="${featureType.productFeatureTypeId?if_exists}">${featureType.description?if_exists}
                        </#list>
                    </select>&nbsp;
                </td>
            </tr>
            <tr>
                <td>Product ID</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>SRCH</td>
                <td>DL</td>
            </tr>

            <input type=hidden name="productId" value="${product.productId?if_exists}">

        <#assign idx=0/>
        <#list productAssocs as productAssoc>
            <#assign assocProduct = productAssoc.getRelatedOne("AssocProduct")/>
            <tr>
                <td nowrap><a href="<@ofbizUrl>/EditProduct?productId=${assocProduct.productId}</@ofbizUrl>">[${assocProduct.productId?if_exists}]</a></td>
                <td width="100%"><a href="<@ofbizUrl>/EditProduct?productId=${assocProduct.productId}</@ofbizUrl>">${assocProduct.internalName?if_exists}</a></td>
                <input type=hidden name="productId${idx}" value="${assocProduct.productId?if_exists}">
                <td colspan=2><input class="inputBox" name="description${idx}" size="70" maxlength="100" value="${selFeatureDesc.get(assocProduct.productId)?if_exists}"></td>
                <#assign checked=""/>
                <#if ((assocProduct.smallImageUrl?if_exists != "") && (assocProduct.smallImageUrl?if_exists == product.smallImageUrl?if_exists) &&
                        (assocProduct.smallImageUrl?if_exists != "") && (assocProduct.smallImageUrl?if_exists == product.smallImageUrl?if_exists)) >
                    <#assign checked = "checked"/>
                </#if>
                <td><input type=radio ${checked} name=useImages value="${assocProduct.productId}"></td>
                <#assign fromDate = Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(productAssoc.getTimestamp("fromDate").toString())/>
                <td><a href="javascript:removeAssoc('${productAssoc.productIdTo}', '${fromDate}');">[x]</a></td>
            </tr>
            <#assign idx = idx + 1/>
        </#list>
            <tr>
                <td colspan=2>&nbsp;</td>
                <td>
                    <table border="0" cellpadding="0" cellspacing="0" class="tabletext">
                        <#list usedFeatureTypes as usedFeatureType>
                        <tr><td><a class="buttontext" href="javascript:removeSelectable('${(usedFeatureType.description)?if_exists}', '${usedFeatureType.productFeatureTypeId}', '${product.productId}')">[x]</a>
                            <a class="buttontext" href="<@ofbizUrl>EditProductQuickAdmin?productFeatureTypeId=${(usedFeatureType.productFeatureTypeId)?if_exists}&productId=${product.productId?if_exists}</@ofbizUrl>">${(usedFeatureType.description)?if_exists}</a></td></tr>
                        </#list>
                    </table>
                </td>
                <td align=right>
                    <table border="0" cellpadding="0" cellspacing="0" class="tabletext">
                        <tr><td align=right><input name="applyToAll" type="submit" value="${uiLabelMap.AddSelectableFeature}"></td></tr>
                    </table>
                </td>
            </tr>
            </table>
        </form>
        <hr>
    </#if>
    <#if (product.isVariant)?if_exists == "Y">
        <form action="<@ofbizUrl>updateProductQuickAdminDistFeat</@ofbizUrl>" method=POST style="margin: 0;" name="distFeature">
            <input type=hidden name="productId" value="${product.productId?if_exists}">
            <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
            <tr>
                <td colspan=3><span class="head2">${uiLabelMap.DistinguishingFeatures}</span></td>
            </tr>
            <tr>
                <td>Product ID</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
        <#assign idx=0/>
        <#list distinguishingFeatures as distinguishingFeature>
            <tr>
                <td><a href="<@ofbizUrl>/quickAdminRemoveProductFeature?productId=${productId}&productFeatureId=${distinguishingFeature.productFeatureId}</@ofbizUrl>">[x]</a></td>
                <td>[${distinguishingFeature.productFeatureId}] ${productFeatureTypeLookup.get(distinguishingFeature.productFeatureId).description}: ${distinguishingFeature.description} </td>
            </tr>
        </#list>

            </table>
        </form>
    </#if>

    <!-- ***************************************************** end Selectable features section -->


    <!-- ***************************************************** Shipping dimensions section -->
    <hr>
    <form action="<@ofbizUrl>updateProductQuickAdminShipping</@ofbizUrl>" method=POST style="margin: 0;" name="updateShipping">
        <input type=hidden name="productId" value="${product.productId?if_exists}">
        <input type=hidden name="heightUomId" value="LEN_in">
        <input type=hidden name="widthUomId" value="LEN_in">
        <input type=hidden name="depthUomId" value="LEN_in">
        <input type=hidden name="weightUomId" value="WT_oz">
        <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
            <tr>
                <td colspan=2><span class="head2">${uiLabelMap.ShippingDimensionsAndWeights}</span></td>
                <td>Height</td>
                <td>Width</td>
                <td>Depth</td>
                <td>Weight</td>
                <td>Fl. Oz.</td>
                <td>ML</td>
                <td>Nt. Wt.</td>
                <td>Grams</td>
                <td>HZ</td>
                <td>ST</td>
                <td>TD</td>
            </tr>

    <#if (product.isVirtual)?if_exists == "Y">
        <#assign idx=0/>
        <#list assocProducts as assocProduct>
            <tr>
                    <td nowrap>[${assocProduct.productId?if_exists}]</td>
                    <td width="100%">${assocProduct.internalName?if_exists}</td>
                <input type=hidden name="productId${idx}" value="${assocProduct.productId?if_exists}">
                    <td><input class="inputBox" name="productHeight${idx}" size="6" maxlength="20" value="${assocProduct.productHeight?if_exists}"></td>
                    <td><input class="inputBox" name="productWidth${idx}" size="6" maxlength="20" value="${assocProduct.productWidth?if_exists}"></td>
                    <td><input class="inputBox" name="productDepth${idx}" size="6" maxlength="20" value="${assocProduct.productDepth?if_exists}"></td>
                    <td><input class="inputBox" name="weight${idx}" size="6" maxlength="20" value="${assocProduct.weight?if_exists}"></td>
                    <td><input class="inputBox" name="~floz${idx}" size="6" maxlength="20" value="${featureFloz.get(assocProduct.productId)?if_exists}"></td>
                    <td><input class="inputBox" name="~ml${idx}" size="6" maxlength="20" value="${featureMl.get(assocProduct.productId)?if_exists}"></td>
                    <td><input class="inputBox" name="~ntwt${idx}" size="6" maxlength="20" value="${featureNtwt.get(assocProduct.productId)?if_exists}"></td>
                    <td><input class="inputBox" name="~grams${idx}" size="6" maxlength="20" value="${featureGrams.get(assocProduct.productId)?if_exists}"></td>
                    <td><a href="<@ofbizUrl>/EditProductFeatures?productId=${assocProduct.productId}</@ofbizUrl>">[${featureHazmat.get(assocProduct.productId)?if_exists}]</a></td>
                    <td><a href="<@ofbizUrl>/EditProduct?productId=${assocProduct.productId}</@ofbizUrl>">${featureSalesThru.get(assocProduct.productId)?if_exists}</a></td>
                    <td><a href="<@ofbizUrl>/EditProductAssoc?productId=${assocProduct.productId}</@ofbizUrl>">${featureThruDate.get(assocProduct.productId)?if_exists}</a></td>
                </tr>
            <#assign idx = idx + 1/>
        </#list>
            <tr>
                <td colspan=10 align=right><input name="applyToAll" type="submit" value="${uiLabelMap.ApplyToAll}">
                &nbsp;&nbsp;<input name="updateShipping" type="submit" value="${uiLabelMap.UpdateShipping}"></td>
            </tr>
    <#else>
            <tr>
                <td>[${productId?if_exists}]</td>
                <td>${product.internalName?if_exists}</td>
                <td><input class="inputBox" name="productHeight" size="6" maxlength="20" value="${product.productHeight?if_exists}"></td>
                <td><input class="inputBox" name="productWidth" size="6" maxlength="20" value="${product.productWidth?if_exists}"></td>
                <td><input class="inputBox" name="productDepth" size="6" maxlength="20" value="${product.productDepth?if_exists}"></td>
                <td><input class="inputBox" name="weight" size="6" maxlength="20" value="${product.weight?if_exists}"></td>
                <td><input class="inputBox" name="~floz" size="6" maxlength="20" value="${floz?if_exists}"></td>
                <td><input class="inputBox" name="~ml" size="6" maxlength="20" value="${ml?if_exists}"></td>
                <td><input class="inputBox" name="~ntwt" size="6" maxlength="20" value="${ntwt?if_exists}"></td>
                <td><input class="inputBox" name="~grams" size="6" maxlength="20" value="${grams?if_exists}"></td>
                <td><a href="<@ofbizUrl>/EditProductFeatures?productId=${product.productId}</@ofbizUrl>">[${hazmat?if_exists}]</a></td>
                <td><a href="<@ofbizUrl>/EditProduct?productId=${product.productId}</@ofbizUrl>">${salesthru?if_exists}</a></td>
                <td><a href="<@ofbizUrl>/EditProductAssoc?productId=${product.productId}</@ofbizUrl>">${thrudate?if_exists}</a></td>
            </tr>
            <tr>
                <td colspan=10 align=right><input type="submit" value="${uiLabelMap.UpdateShipping}"></td>
            </tr>
    </#if>

        </table>
    </form>
    <!--  **************************************************** end - Shipping dimensions section -->

    <!--  **************************************************** Standard Features section -->
    <hr>
    <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
    <tr>
    <td>
        <form method="POST" action="<@ofbizUrl>/quickAdminApplyFeatureToProduct</@ofbizUrl>" style="margin: 0;" name="addFeatureById">
        <input type=hidden name="productId" value="${product.productId?if_exists}">
        <input type=hidden name="productFeatureApplTypeId" value="STANDARD_FEATURE">
        <input type=hidden name="fromDate" value="${nowTimestampString}">
        <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
            <tr>
                <td colspan=2><span class="head2">${uiLabelMap.StandardFeatures}</span></td>
            </tr>
            <#list addedFeatureTypeIds as addedFeatureTypeId>
                <tr>
                    <td align=right>${addedFeatureTypes.get(addedFeatureTypeId).getString("description")}</td>
                    <td>

                        <select name="productFeatureId">
                            <option value="~~any~~">${uiLabelMap.AnyFeatureType}
                        <#list featuresByType.get(addedFeatureTypeId) as feature>
                            <option value="${feature.getString("productFeatureId")}"> ${feature.getString("description")}
                        </#list>
                        </select>
                    </td>
                </tr>
            </#list>
                <tr><td colspan=2 align=right><input type=submit value="${uiLabelMap.AddFeatures}"></td></tr>

        </table>
        </form>
    </td>
    <td width=20>&nbsp;</td>
    <td valign=top>
        <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
            <#list standardFeatureAppls as standardFeatureAppl>
                <#assign featureId = standardFeatureAppl.productFeatureId/>
                <tr>
                    <td><a href='<@ofbizUrl>/quickAdminRemoveFeatureFromProduct?productId=${standardFeatureAppl.productId?if_exists}&productFeatureId=${featureId?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(standardFeatureAppl.getTimestamp("fromDate").toString())}</@ofbizUrl>' class="buttontext">[x]</a></td>
                    <td>${productFeatureTypeLookup.get(featureId).getString("description")}:
                            ${standardFeatureLookup.get(featureId).getString("description")}</td>
                </tr>
            </#list>
        </table>
    </td>
    </tr>
    </table>
    <hr>
        <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
            <tr>
                <form action="<@ofbizUrl>EditProductQuickAdmin</@ofbizUrl>">
                <input type=hidden name="productFeatureTypeId" value="${(productFeatureTypeId)?if_exists}">
                <input type=hidden name="productId" value="${product.productId?if_exists}">
                <td align=right>${uiLabelMap.FeatureTypes}</td>
                <td>
                    <select multiple name=addFeatureTypeId>
                        <#list featureTypes as featureType>
                            <option value="${featureType.productFeatureTypeId?if_exists}">${featureType.description?if_exists}
                        </#list>
                    </select>&nbsp;
                </td>
                <td><input type=submit value="${uiLabelMap.AddFeatureType}"></td>
                </form>
            </tr>
        </table>
    <!--  **************************************************** end - Standard Features section -->

    <!--  **************************************************** Categories section -->
    <hr>
    <form action="<@ofbizUrl>quickAdminAddCategories</@ofbizUrl>">
    <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
    <tr>
    <td>
        <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
            <tr>
                <input type=hidden name="fromDate" value="${nowTimestampString}">
                <input type=hidden name="productId" value="${product.productId?if_exists}">
                <td align=right>${uiLabelMap.Categories}</td>
                <td>
                    <select multiple name="categoryId">
                        <#list allCategories as category>
                            <option value="${category.productCategoryId?if_exists}">${category.description?if_exists}
                        </#list>
                    </select>&nbsp;
                </td>
            </tr>
        </table>
    <td valign=top>
        <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
            <#list productCategoryMembers as prodCatMemb>
                <#assign prodCat = prodCatMemb.getRelatedOne("ProductCategory")/>
                <tr>
                    <td><a href='<@ofbizUrl>/quickAdminRemoveProductFromCategory?productId=${prodCatMemb.productId?if_exists}&productCategoryId=${prodCatMemb.productCategoryId}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(prodCatMemb.getTimestamp("fromDate").toString())}</@ofbizUrl>' class="buttontext">[x]</a></td>
                    <td>${prodCat.description?if_exists} [${prodCat.productCategoryId}]</td>
                </tr>
            </#list>
        </table>
    </td>
    </tr>
    <tr>
        <td colspan=2 align=right><input type=submit value="${uiLabelMap.UpdateCategories}"></td>
    </tr>
    </table>
    </form>

    <!--  **************************************************** end - Categories section -->

    <!--  **************************************************** publish section -->
    <hr>
    <table border="0" cellpadding="2" cellspacing="0" class="tabletext">
    <#if (showPublish == "true")>
        <tr>
            <form action="<@ofbizUrl>quickAdminAddCategories</@ofbizUrl>" name="publish">
            <input type=hidden name="productId" value="${product.productId?if_exists}">
            <input type=hidden name="categoryId" value="${allCategoryId?if_exists}">
            <td>
                <input type=text size="25" name="fromDate" class="inputBox">
                <a href="javascript:call_cal(document.publish.fromDate, '${nowTimestampString}');">
                    <img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar">
                </a>
            </td>
            <td><input type=button value="${uiLabelMap.PublishAndView}" onClick="doPublish();"></td>
            </form>
        </tr>
    <#else>
        <tr>
            <form action="<@ofbizUrl>quickAdminUnPublish</@ofbizUrl>" name="unpublish">
                <input type=hidden name="productId" value="${product.productId?if_exists}">
                <input type=hidden name="productCategoryId" value="${allCategoryId?if_exists}">
            <td>
                <input type=text size="25" name="thruDate" class="inputBox">
                <a href="javascript:call_cal(document.unpublish.thruDate, '${nowTimestampString}');">
                    <img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar">
                </a>
            </td>
            <td><input type=submit value="${uiLabelMap.RemoveFromSite}"></td>
            </form>
        </tr>
    </#if>
    </table>


    <!--  **************************************************** end - publish section -->
    
<form name=removeAssocForm action="<@ofbizUrl>quickAdminUpdateProductAssoc</@ofbizUrl>">
    <input type=hidden name=PRODUCT_ID value="${product.productId?if_exists}">
    <input type=hidden name=PRODUCT_ID_TO value="">
    <input type=hidden name=PRODUCT_ASSOC_TYPE_ID value="PRODUCT_VARIANT">
    <input type=hidden name=FROM_DATE value="">
    <input type=hidden name=useValues value=true>
</form>
<form name=removeSelectable action="<@ofbizUrl>updateProductQuickAdminDelFeatureTypes</@ofbizUrl>">
    <input type=hidden name=productId value="${product.productId?if_exists}">
    <input type=hidden name=productFeatureTypeId value="">
</form>
<script language="JavaScript" type="text/javascript">

function removeAssoc(productIdTo, fromDate) {
    if (confirm("Are you sure you want to remove the association of " + productIdTo + "?")) {
        document.removeAssocForm.PRODUCT_ID_TO.value = productIdTo;
        document.removeAssocForm.FROM_DATE.value = fromDate;
        document.removeAssocForm.submit();
    }
}

function removeSelectable(typeString, productFeatureTypeId, productId) {
    if (confirm("Are you sure you want to remove all the selectable features of type " + typeString + "?")) {
        document.removeSelectable.productId.value = productId;
        document.removeSelectable.productFeatureTypeId.value = productFeatureTypeId;
        document.removeSelectable.submit();

    }
}

function doPublish() {
    window.open('/ecommerce/control/product?product_id=${productId?if_exists}');
    document.publish.submit();
}

</script>
<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>
