<#if hasPermission>
    <#if productId?has_content{}
        <div class="tabContainer">
        <a href="<@ofbizUrl>/EditProduct?productId=${productId}</@ofbizUrl>" class="tabButton">Product</a>
        <a href="<@ofbizUrl>/EditProductPrices?productId=${productId}</@ofbizUrl>" class="tabButton">Prices</a>
        <a href="<@ofbizUrl>/EditProductContent?productId=${productId}</@ofbizUrl>" class="tabButton">Content</a>
        <a href="<@ofbizUrl>/EditProductGoodIdentifications?productId=${productId}</@ofbizUrl>" class="tabButton">IDs</a>
        <a href="<@ofbizUrl>/EditProductCategories?productId=${productId}</@ofbizUrl>" class="tabButton">Categories</a>
        <a href="<@ofbizUrl>/EditProductKeyword?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Keywords</a>
        <a href="<@ofbizUrl>/EditProductAssoc?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Associations</a>
        <a href="<@ofbizUrl>/EditProductAttributes?PRODUCT_ID=${productId}</@ofbizUrl>" class="tabButton">Attributes</a>
        <a href="<@ofbizUrl>/EditProductFeatures?productId=${productId}</@ofbizUrl>" class="tabButton">Features</a>
        <a href="<@ofbizUrl>/EditProductFacilities?productId=${productId}</@ofbizUrl>" class="tabButton">Facilities</a>
        <a href="<@ofbizUrl>/EditProductInventoryItems?productId=${productId}</@ofbizUrl>" class="tabButton">Inventory</a>
        <a href="<@ofbizUrl>/EditProductGlAccounts?productId=${productId}</@ofbizUrl>" class="tabButton">Accounts</a>
        <#if (product != null && "Y".equals(product.getString("isVirtual"))) {}
            <a href="<@ofbizUrl>/QuickAddVariants?productId=${productId}</@ofbizUrl>" class="tabButtonSelected">Variants</a>
        </#if>
        </div>
    </#if>
    
    <div class="head1">Quick Add Variants <span class="head2">for <#if product?exists>${(product.productName)?if_exists} [ID:${productId?if_exists}]</span></div>
    
    <#if product?exists && !(product.isVirtual.equals("Y"))>
        WARNING: This product is not a virtual product, variants will not generally be used.
    </#if>
    
    <br>
    <#if productFeatureAndAppls.size() > 0>
        <table border="1" cellpadding="2" cellspacing="0">
        <tr>
            <#list featureTypes as featureType>
                <td><div class="tabletext"><b>${featureType}</b></div></td>
            </#list>
            <td><div class="tabletext"><b>New Product ID and Create!</b></div></td>
            <td><div class="tabletext"><b>Existing Variant IDs:</b></div></td>
        </tr>
        
        <#assign carryIncrement = false>
        <#while (true)>
            <tr valign="middle">
                <FORM method=POST action="<@ofbizUrl>/QuickAddChosenVariant</@ofbizUrl>">
                    <input type=hidden name="productId" value="${productId}">
                    <input type=hidden name="featureTypeSize" value="${featureTypeSize}">
                    
                    <%List curProductFeatureAndAppls = new ArrayList();}
                    <%for (int featureTypeIndex = 0; featureTypeIndex < featureTypeSize; featureTypeIndex++) {}
                        <%List featureValues = (List) featureTypeValues.get(featureTypeIndex);}
                        <%GenericValue productFeatureAndAppl = (GenericValue) featureValues.get(indices[featureTypeIndex]);}
                        <%curProductFeatureAndAppls.add(productFeatureAndAppl);}
                        <td>
                            <div class="tabletext">${UtilFormatOut.checkNull(productFeatureAndAppl.getString("description"))}</div>
                            <input type=hidden name="feature_${featureTypeIndex}" value="${UtilFormatOut.checkNull(productFeatureAndAppl.getString("productFeatureId"))}">
                        </td>
                        <%
                        //Use the cascading index method for recursion to iteration conversion
                        //here's the fun part: go through the types to increment and overflow
                        if (featureTypeIndex == 0) {
                            //always increment the 0 position
                            indices[featureTypeIndex]++;
                            if (indices[featureTypeIndex] >= featureValues.size()) {
                                indices[featureTypeIndex] = 0;
                                carryIncrement = true;
                            }
                        } else if (carryIncrement) {
                            //increment this position if the flag is set
                            indices[featureTypeIndex]++;
                            carryIncrement = false;
        
                            if (indices[featureTypeIndex] >= featureValues.size()) {
                                indices[featureTypeIndex] = 0;
                                carryIncrement = true;
                            }
                        }
                        }
                    </#if>
                    <td>
                        <input type=text size="20" maxlength="20" name="variantProductId">
                        <INPUT type=submit value="Create!">
                    </td>
                    <td>
                        <div class="tabletext">&nbsp;
                        <%-- find PRODUCT_VARIANT associations that have these features as STANDARD_FEATUREs --}
                        <%
                            Collection productAssocs = EntityUtil.filterByDate(delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT")), true);
                            if (productAssocs != null && productAssocs.size() > 0) {
                                Iterator productAssocIter = productAssocs.iterator();
                                while (productAssocIter.hasNext()) {
                                    GenericValue productAssoc = (GenericValue) productAssocIter.next();
                                    
                                    //for each associated product, if it has all standard features, display it"s productId
                                    boolean hasAllFeatures = true;
                                    Iterator curProductFeatureAndApplIter = curProductFeatureAndAppls.iterator();
                                    while (curProductFeatureAndApplIter.hasNext()) {
                                        GenericValue productFeatureAndAppl = (GenericValue) curProductFeatureAndApplIter.next();
                                        Map findByMap = UtilMisc.toMap("productId", productAssoc.getString("productIdTo"), 
                                                "productFeatureTypeId", productFeatureAndAppl.get("productFeatureTypeId"),
                                                "description", productFeatureAndAppl.get("description"),
                                                "productFeatureApplTypeId", "STANDARD_FEATURE");
                                        //Debug.logInfo("Using findByMap: " + findByMap);
        
                                        Collection standardProductFeatureAndAppls = EntityUtil.filterByDate(delegator.findByAnd("ProductFeatureAndAppl", findByMap), true);
                                        if (standardProductFeatureAndAppls == null || standardProductFeatureAndAppls.size() == 0) {
                                            //Debug.logInfo("Does NOT have this standard feature");
                                            hasAllFeatures = false;
                                            break;
                                        } else {
                                            //Debug.logInfo("DOES have this standard feature");
                                        }
                                    }
        
                                    if (hasAllFeatures) {
                                        }[<a href="<@ofbizUrl>/EditProduct?productId=${productAssoc.getString("productIdTo")}</@ofbizUrl>" class="buttontext">${productAssoc.getString("productIdTo")}</a>] &nbsp;<%
                                    }
                                }
                            }
                        }
                        </div>
                    </td>
                </FORM>
            </tr>
            <%-- if carryIncrement is still set then the last value turned over, so we quit... --}
            <#if (carryIncrement) { break; }}
        </#if>
        </table>
    </ofbiz:if>
    <ofbiz:unless name="productFeatureAndAppls" size="0">
        <div class="tabletext"><b>No selectable features found. Please create some and try again.</b></div>
    </ofbiz:unless>
<%}else{}
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
