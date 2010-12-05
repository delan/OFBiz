<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#-- variable setup -->
<#assign price = priceMap?if_exists />
<#-- end variable setup -->

<#-- virtual product javascript -->
${virtualJavaScript?if_exists}
<script type="text/javascript">
//<![CDATA[
    var detailImageUrl = null;
    function setAddProductId(name) {
        document.addform.add_product_id.value = name;
        if (document.addform.quantity == null) return;
        if (name == '' || name == 'NULL' || isVirtual(name) == true) {
            document.addform.quantity.disabled = true;
            var elem = document.getElementById('product_id_display');
            var txt = document.createTextNode('');
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        } else {
            document.addform.quantity.disabled = false;
            var elem = document.getElementById('product_id_display');
            var txt = document.createTextNode(name);
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        }
    }
    function setVariantPrice(sku) {
        if (sku == '' || sku == 'NULL' || isVirtual(sku) == true) {
            var elem = document.getElementById('variant_price_display');
            var txt = document.createTextNode('');
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        }
        else {
            var elem = document.getElementById('variant_price_display');
            var price = getVariantPrice(sku);
            var txt = document.createTextNode(price);
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        }
    }
    function isVirtual(product) {
        var isVirtual = false;
        <#if virtualJavaScript?exists>
        for (i = 0; i < VIR.length; i++) {
            if (VIR[i] == product) {
                isVirtual = true;
            }
        }
        </#if>
        return isVirtual;
    }
    function addItem() {
       if (document.addform.add_product_id.value == 'NULL') {
           alert("Please select all of the required options.");
           return;
       } else {
           if (isVirtual(document.addform.add_product_id.value)) {
               document.location = '<@ofbizUrl>product?category_id=${categoryId?if_exists}&amp;product_id=</@ofbizUrl>' + document.addform.add_product_id.value;
               return;
           } else {
               document.addform.submit();
           }
       }
    }

    function popupDetail(specificDetailImageUrl) {
        if( specificDetailImageUrl ) {
            detailImageUrl = specificDetailImageUrl;
        }
        else {
            var defaultDetailImage = "${firstDetailImage?default(mainDetailImageUrl?default("_NONE_"))}";
            if (defaultDetailImage == null || defaultDetailImage == "null" || defaultDetailImage == "") {
               defaultDetailImage = "_NONE_";
            }

            if (detailImageUrl == null || detailImageUrl == "null") {
                detailImageUrl = defaultDetailImage;
            }
        }

        if (detailImageUrl == "_NONE_") {
            hack = document.createElement('span');
            hack.innerHTML="${uiLabelMap.CommonNoDetailImageAvailableToDisplay}";
            alert(hack.innerHTML);
            return;
        }
        detailImageUrl = detailImageUrl.replace(/\&\#47;/g, "/");
        popUp("<@ofbizUrl>detailImage?detail=" + detailImageUrl + "</@ofbizUrl>", 'detailImage', '600', '600');
    }

    function toggleAmt(toggle) {
        if (toggle == 'Y') {
            changeObjectVisibility("add_amount", "visible");
        }

        if (toggle == 'N') {
            changeObjectVisibility("add_amount", "hidden");
        }
    }

    function findIndex(name) {
        for (i = 0; i < OPT.length; i++) {
            if (OPT[i] == name) {
                return i;
            }
        }
        return -1;
    }

    function getList(name, index, src) {
        currentFeatureIndex = findIndex(name);

        if (currentFeatureIndex == 0) {
            // set the images for the first selection
            if (IMG[index] != null) {
                if (document.images['mainImage'] != null) {
                    document.images['mainImage'].src = IMG[index];
                    detailImageUrl = DET[index];
                }
            }

            // set the drop down index for swatch selection
            document.forms["addform"].elements[name].selectedIndex = (index*1)+1;
        }

        if (currentFeatureIndex < (OPT.length-1)) {
            // eval the next list if there are more
            var selectedValue = document.forms["addform"].elements[name].options[(index*1)+1].value;
            if (index == -1) {
              <#if featureOrderFirst?exists>
                var Variable1 = eval("list" + "${featureOrderFirst}" + "()");
              </#if>
            } else {
                var Variable1 = eval("list" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
            }
            // set the product ID to NULL to trigger the alerts
            setAddProductId('NULL');

            // set the variant price to NULL
            setVariantPrice('NULL');
        } else {
            // this is the final selection -- locate the selected index of the last selection
            var indexSelected = document.forms["addform"].elements[name].selectedIndex;

            // using the selected index locate the sku
            var sku = document.forms["addform"].elements[name].options[indexSelected].value;
            
            // display alternative packaging dropdown
            ajaxUpdateArea("product_uom", "<@ofbizUrl>ProductUomDropDownOnly</@ofbizUrl>", "productId=" + sku);

            // set the product ID
            setAddProductId(sku);

            // set the variant price
            setVariantPrice(sku);

            // check for amount box
            toggleAmt(checkAmtReq(sku));
        }
    }

    function validate(x){
        var msg=new Array();
        msg[0]="Please use correct date format [yyyy-mm-dd]";

        var y=x.split("-");
        if(y.length!=3){ alert(msg[0]);return false; }
        if((y[2].length>2)||(parseInt(y[2])>31)) { alert(msg[0]); return false; }
        if(y[2].length==1){ y[2]="0"+y[2]; }
        if((y[1].length>2)||(parseInt(y[1])>12)){ alert(msg[0]); return false; }
        if(y[1].length==1){ y[1]="0"+y[1]; }
        if(y[0].length>4){ alert(msg[0]); return false; }
        if(y[0].length<4) {
            if(y[0].length==2) {
                y[0]="20"+y[0];
            } else {
                alert(msg[0]);
                return false;
            }
        }
        return (y[0]+"-"+y[1]+"-"+y[2]);
    }

    function additemSubmit(){
        <#if product.productTypeId?if_exists == "ASSET_USAGE">
        newdatevalue = validate(document.addform.reservStart.value);
        if (newdatevalue == false) {
            document.addform.reservStart.focus();
        } else {
            document.addform.reservStart.value = newdatevalue;
            document.addform.submit();
        }
        <#else>
        document.addform.submit();
        </#if>
    }

    function addShoplistSubmit(){
        <#if product.productTypeId?if_exists == "ASSET_USAGE">
        if (document.addToShoppingList.reservStartStr.value == "") {
            document.addToShoppingList.submit();
        } else {
            newdatevalue = validate(document.addToShoppingList.reservStartStr.value);
            if (newdatevalue == false) {
                document.addToShoppingList.reservStartStr.focus();
            } else {
                document.addToShoppingList.reservStartStr.value = newdatevalue;
                // document.addToShoppingList.reservStart.value = ;
                document.addToShoppingList.reservStartStr.value.slice(0,9)+" 00:00:00.000000000";
                document.addToShoppingList.submit();
            }
        }
        <#else>
        document.addToShoppingList.submit();
        </#if>
    }

    <#if product.virtualVariantMethodEnum?if_exists == "VV_FEATURETREE" && featureLists?has_content>
        function checkRadioButton() {
            var block1 = document.getElementById("addCart1");
            var block2 = document.getElementById("addCart2");
            <#list featureLists as featureList>
                <#list featureList as feature>
                    <#if feature_index == 0>
                        var myList = document.getElementById("FT${feature.productFeatureTypeId}");
                         if (myList.options[0].selected == true){
                             block1.style.display = "none";
                             block2.style.display = "block";
                             return;
                         }
                        <#break>
                    </#if>
                </#list>
            </#list>
            block1.style.display = "block";
            block2.style.display = "none";
        }
    </#if>
    
    function displayProductVirtualVariantId(variantId) {
        document.addform.product_id.value = variantId;
        var elem = document.getElementById('product_id_display');
        var txt = document.createTextNode(variantId);
        if(elem.hasChildNodes()) {
            elem.replaceChild(txt, elem.firstChild);
        } else {
            elem.appendChild(txt);
        }
        setVariantPrice(variantId);
    }
//]]>
 </script>

<#macro showUnavailableVarients>
  <#if unavailableVariants?exists>
    <ul>
      <#list unavailableVariants as prod>
        <#assign features = prod.getRelated("ProductFeatureAppl")/>
        <li>
          <#list features as feature>
            <em>${feature.getRelatedOne("ProductFeature").description}</em><#if feature_has_next>, </#if>
          </#list>
          <span>${uiLabelMap.ProductItemOutOfStock}</span>
        </li>
      </#list>
    </ul>
  </#if>
</#macro>

<div id="productdetail">
<#assign productAdditionalImage1 = productContentWrapper.get("ADDITIONAL_IMAGE_1")?if_exists />
<#assign productAdditionalImage2 = productContentWrapper.get("ADDITIONAL_IMAGE_2")?if_exists />
<#assign productAdditionalImage3 = productContentWrapper.get("ADDITIONAL_IMAGE_3")?if_exists />
<#assign productAdditionalImage4 = productContentWrapper.get("ADDITIONAL_IMAGE_4")?if_exists />

  <#-- Category next/previous -->
  <#if category?exists>
      <div id="paginationBox">
        <#if previousProductId?exists>
          <a href="<@ofbizUrl>product/~category_id=${categoryId?if_exists}/~product_id=${previousProductId?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a>&nbsp;|&nbsp;
        </#if>
        <a href="<@ofbizUrl>category/~category_id=${categoryId?if_exists}</@ofbizUrl>" class="linktext">${(category.categoryName)?default(category.description)?if_exists}</a>
        <#if nextProductId?exists>
          &nbsp;|&nbsp;<a href="<@ofbizUrl>product/~category_id=${categoryId?if_exists}/~product_id=${nextProductId?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a>
        </#if>
      </div>
  </#if>

<hr />
<div id="productImageBox">
  <#-- Product image/name/price -->
    <div id="detailImageBox">
      <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")?if_exists />
      <#-- remove the next two lines to always display the virtual image first (virtual images must exist) -->
      <#if firstLargeImage?has_content>
        <#assign productLargeImageUrl = firstLargeImage />
      </#if>
      <#if productLargeImageUrl?string?has_content>
        <a href="javascript:popupDetail('${firstDetailImage?default(mainDetailImageUrl?default("_NONE_"))}');"><img id="detailImage" src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${productLargeImageUrl?if_exists}</@ofbizContentUrl>" name="mainImage" vspace="5" hspace="5"  alt="" /></a>
        <input type="hidden" id="originalImage" name="originalImage" value="<@ofbizContentUrl>${contentPathPrefix?if_exists}${productLargeImageUrl?if_exists}</@ofbizContentUrl>" />
      </#if>
      <#if !productLargeImageUrl?string?has_content>
        <img id="detailImage" src="/images/defaultImage.jpg" name="mainImage" alt="" />
      </#if>
    </div>
    <div id="additionalImageBox">
      <#if productAdditionalImage1?string?has_content>
        <#assign productAdditionalImage1Small = productContentWrapper.get("XTRA_IMG_1_SMALL")?if_exists />
        <#assign productAdditionalImage1Large = productContentWrapper.get("XTRA_IMG_1_LARGE")?if_exists />
        <#assign productAdditionalImage1Detail = productContentWrapper.get("XTRA_IMG_1_DETAIL")?if_exists />
        <div class="additionalImage">
          <#if productAdditionalImage1Small?string?has_content && productAdditionalImage1Large?string?has_content>
            <#if productAdditionalImage1Detail?string?has_content>
              <a href="javascript:popupDetail('${productAdditionalImage1Detail}');" swapDetail="<@ofbizContentUrl>${productAdditionalImage1Large?string}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage1Small?string}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /></a>
            <#else>
            <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage1Large?string}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage1Small?string}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /></a>
            </#if>
          <#else>
          <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>" vspace="5" hspace="5" width="200" alt="" /></a>
          </#if>
        </div>
      </#if>
      <#if productAdditionalImage2?string?has_content>
        <#assign productAdditionalImage2Small = productContentWrapper.get("XTRA_IMG_2_SMALL")?if_exists />
        <#assign productAdditionalImage2Large = productContentWrapper.get("XTRA_IMG_2_LARGE")?if_exists />
        <#assign productAdditionalImage2Detail = productContentWrapper.get("XTRA_IMG_2_DETAIL")?if_exists />
        <div class="additionalImage">
          <#if productAdditionalImage2Small?string?has_content && productAdditionalImage2Large?string?has_content>
            <#if productAdditionalImage2Detail?string?has_content>
              <a href="javascript:popupDetail('${productAdditionalImage2Detail}');" swapDetail="<@ofbizContentUrl>${productAdditionalImage2Large?string}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage2Small?string}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /></a>
            <#else>
            <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage2Large?string}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage2Small?string}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /></a>
            </#if>
          <#else>
          <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>" vspace="5" hspace="5" width="200" alt="" /></a>
          </#if>
         </div>
      </#if>
      <#if productAdditionalImage3?string?has_content>
        <#assign productAdditionalImage3Small = productContentWrapper.get("XTRA_IMG_3_SMALL")?if_exists />
        <#assign productAdditionalImage3Large = productContentWrapper.get("XTRA_IMG_3_LARGE")?if_exists />
        <#assign productAdditionalImage3Detail = productContentWrapper.get("XTRA_IMG_3_DETAIL")?if_exists />
        <div class="additionalImage">
          <#if productAdditionalImage3Small?string?has_content && productAdditionalImage3Large?string?has_content>
            <#if productAdditionalImage3Detail?string?has_content>
              <a href="javascript:popupDetail('${productAdditionalImage3Detail}');" swapDetail="<@ofbizContentUrl>${productAdditionalImage3Large?string}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage3Small?string}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /></a>
            <#else>
            <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage3Large?string}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage3Small?string}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /></a>
            </#if>
          <#else>
          <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>" vspace="5" hspace="5" width="200" alt="" /></a>
          </#if>
        </div>
      </#if>
      <#if productAdditionalImage4?string?has_content>
        <#assign productAdditionalImage4Small = productContentWrapper.get("XTRA_IMG_4_SMALL")?if_exists />
        <#assign productAdditionalImage4Large = productContentWrapper.get("XTRA_IMG_4_LARGE")?if_exists />
        <#assign productAdditionalImage4Detail = productContentWrapper.get("XTRA_IMG_4_DETAIL")?if_exists />
        <div class="additionalImage">
          <#if productAdditionalImage4Small?string?has_content && productAdditionalImage4Large?string?has_content>
            <#if productAdditionalImage4Detail?string?has_content>
              <a href="javascript:popupDetail('${productAdditionalImage4Detail}');" swapDetail="<@ofbizContentUrl>${productAdditionalImage4Large?string}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage4Small?string}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /></a>
            <#else>
            <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage4Large?string}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage4Small?string}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /></a>
            </#if>
          <#else>
          <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>" vspace="5" hspace="5" width="200" alt="" /></a>
          </#if>
        </div>
      </#if>
    </div>
    </div>
    <div id="productDetailBox">
      <h2>${productContentWrapper.get("PRODUCT_NAME")?if_exists}</h2>
      <div>${productContentWrapper.get("DESCRIPTION")?if_exists}</div>
      <div>${product.productId?if_exists}</div>
      <#-- example of showing a certain type of feature with the product -->
      <#if sizeProductFeatureAndAppls?has_content>
        <div>
          <#if (sizeProductFeatureAndAppls?size == 1)>
            <#-- TODO : i18n -->
            Size:
          <#else>
            Sizes Available:
          </#if>
          <#list sizeProductFeatureAndAppls as sizeProductFeatureAndAppl>
            ${sizeProductFeatureAndAppl.description?default(sizeProductFeatureAndAppl.abbrev?default(sizeProductFeatureAndAppl.productFeatureId))}<#if sizeProductFeatureAndAppl_has_next>,</#if>
          </#list>
        </div>
      </#if>

      <#-- for prices:
              - if price < competitivePrice, show competitive or "Compare At" price
              - if price < listPrice, show list price
              - if price < defaultPrice and defaultPrice < listPrice, show default
              - if isSale show price with salePrice style and print "On Sale!"
      -->
      <#if price.competitivePrice?exists && price.price?exists && price.price &lt; price.competitivePrice>
        <div>${uiLabelMap.ProductCompareAtPrice}: <span class="basePrice"><@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed /></span></div>
      </#if>
      <#if price.listPrice?exists && price.price?exists && price.price &lt; price.listPrice>
        <div>${uiLabelMap.ProductListPrice}: <span class="basePrice"><@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed /></span></div>
      </#if>
      <#if price.listPrice?exists && price.defaultPrice?exists && price.price?exists && price.price &lt; price.defaultPrice && price.defaultPrice &lt; price.listPrice>
        <div>${uiLabelMap.ProductRegularPrice}: <span class="basePrice"><@ofbizCurrency amount=price.defaultPrice isoCode=price.currencyUsed /></span></div>
      </#if>
      <#if price.specialPromoPrice?exists>
        <div>${uiLabelMap.ProductSpecialPromoPrice}: <span class="basePrice"><@ofbizCurrency amount=price.specialPromoPrice isoCode=price.currencyUsed /></span></div>
      </#if>
      <div>
        <strong>
          <#if price.isSale?exists && price.isSale>
            <span class="salePrice">${uiLabelMap.OrderOnSale}!</span>
            <#assign priceStyle = "salePrice" />
          <#else>
            <#assign priceStyle = "regularPrice" />
          </#if>
            ${uiLabelMap.OrderYourPrice}: <#if "Y" = product.isVirtual?if_exists> ${uiLabelMap.CommonFrom} </#if><span class="${priceStyle}"><@ofbizCurrency amount=price.price isoCode=price.currencyUsed /></span>
             <#if product.productTypeId?if_exists == "ASSET_USAGE">
            <#if product.reserv2ndPPPerc?exists && product.reserv2ndPPPerc != 0><br /><span class="${priceStyle}">${uiLabelMap.ProductReserv2ndPPPerc}<#if !product.reservNthPPPerc?exists || product.reservNthPPPerc == 0>${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}</#if> <@ofbizCurrency amount=product.reserv2ndPPPerc*price.price/100 isoCode=price.currencyUsed /></span></#if>
            <#if product.reservNthPPPerc?exists &&product.reservNthPPPerc != 0><br /><span class="${priceStyle}">${uiLabelMap.ProductReservNthPPPerc} <#if !product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0>${uiLabelMap.ProductReservSecond} <#else> ${uiLabelMap.ProductReservThird} </#if> ${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}, ${uiLabelMap.ProductEach}: <@ofbizCurrency amount=product.reservNthPPPerc*price.price/100 isoCode=price.currencyUsed /></span></#if>
            <#if (!product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0) && (!product.reservNthPPPerc?exists || product.reservNthPPPerc == 0)><br />${uiLabelMap.ProductMaximum} ${product.reservMaxPersons?if_exists} ${uiLabelMap.ProductPersons}.</#if>
             </#if>
         </strong>
      </div>
      <#if price.listPrice?exists && price.price?exists && price.price &lt; price.listPrice>
        <#assign priceSaved = price.listPrice - price.price />
        <#assign percentSaved = (priceSaved / price.listPrice) * 100 />
        <div>${uiLabelMap.OrderSave}: <span class="basePrice"><@ofbizCurrency amount=priceSaved isoCode=price.currencyUsed /> (${percentSaved?int}%)</span></div>
      </#if>
      <#-- show price details ("showPriceDetails" field can be set in the screen definition) -->
      <#if (showPriceDetails?exists && showPriceDetails?default("N") == "Y")>
          <#if price.orderItemPriceInfos?exists>
              <#list price.orderItemPriceInfos as orderItemPriceInfo>
                  <div>${orderItemPriceInfo.description?if_exists}</div>
              </#list>
          </#if>
      </#if>

      <#-- Included quantities/pieces -->
      <#if product.piecesIncluded?exists && product.piecesIncluded?long != 0>
        <div>
          ${uiLabelMap.OrderPieces}: ${product.piecesIncluded}
        </div>
      </#if>
      <#if (product.quantityIncluded?exists && product.quantityIncluded != 0) || product.quantityUomId?has_content>
        <#assign quantityUom = product.getRelatedOneCache("QuantityUom")?if_exists />
        <div>
          ${uiLabelMap.CommonQuantity}: ${product.quantityIncluded?if_exists} ${((quantityUom.abbreviation)?default(product.quantityUomId))?if_exists}
        </div>
      </#if>

      <#if (product.weight?exists && product.weight != 0) || product.weightUomId?has_content>
        <#assign weightUom = product.getRelatedOneCache("WeightUom")?if_exists />
        <div>
          ${uiLabelMap.CommonWeight}: ${product.weight?if_exists} ${((weightUom.abbreviation)?default(product.weightUomId))?if_exists}
        </div>
      </#if>
      <#if (product.productHeight?exists && product.productHeight != 0) || product.heightUomId?has_content>
        <#assign heightUom = product.getRelatedOneCache("HeightUom")?if_exists />
        <div>
          ${uiLabelMap.CommonHeight}: ${product.productHeight?if_exists} ${((heightUom.abbreviation)?default(product.heightUomId))?if_exists}
        </div>
      </#if>
      <#if (product.productWidth?exists && product.productWidth != 0) || product.widthUomId?has_content>
        <#assign widthUom = product.getRelatedOneCache("WidthUom")?if_exists />
        <div>
          ${uiLabelMap.CommonWidth}: ${product.productWidth?if_exists} ${((widthUom.abbreviation)?default(product.widthUomId))?if_exists}
        </div>
      </#if>
      <#if (product.productDepth?exists && product.productDepth != 0) || product.depthUomId?has_content>
        <#assign depthUom = product.getRelatedOneCache("DepthUom")?if_exists />
        <div>
          ${uiLabelMap.CommonDepth}: ${product.productDepth?if_exists} ${((depthUom.abbreviation)?default(product.depthUomId))?if_exists}
        </div>
      </#if>

      <#if daysToShip?exists>
        <div><strong>${uiLabelMap.ProductUsuallyShipsIn} ${daysToShip} ${uiLabelMap.CommonDays}!</strong></div>
      </#if>

      <#-- show tell a friend details only in ecommerce application -->
      <div>&nbsp;</div>
      <div>
          <a href="javascript:popUpSmall('<@ofbizUrl>tellafriend?productId=${product.productId}</@ofbizUrl>','tellafriend');" class="buttontext">${uiLabelMap.CommonTellAFriend}</a>
      </div>

      <#if disFeatureList?exists && 0 &lt; disFeatureList.size()>
      <p>&nbsp;</p>
        <#list disFeatureList as currentFeature>
            <#assign disFeatureType = currentFeature.getRelatedOneCache("ProductFeatureType") />
            <div>
                <#if disFeatureType.description?exists>${disFeatureType.get("description", locale)}<#else>${currentFeature.productFeatureTypeId}</#if>:&nbsp;${currentFeature.description}
            </div>
        </#list>
            <div>&nbsp;</div>
      </#if>
    </div>

    <div id="addItemForm">
      <form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="addform"  style="margin: 0;">
      <fieldset>
        <#assign inStock = true />
        <#-- Variant Selection -->
        <#if product.isVirtual?if_exists?upper_case == "Y">
          <#if product.virtualVariantMethodEnum?if_exists == "VV_FEATURETREE" && featureLists?has_content>
            <#list featureLists as featureList>
                <#list featureList as feature>
                    <#if feature_index == 0>
                        <div>${feature.description}: <select id="FT${feature.productFeatureTypeId}" name="FT${feature.productFeatureTypeId}" onchange="javascript:checkRadioButton();">
                        <option value="select" selected="selected"> select option </option>
                    <#else>
                        <option value="${feature.productFeatureId}">${feature.description} <#if feature.price?exists>(+ <@ofbizCurrency amount=feature.price?string isoCode=feature.currencyUomId />)</#if></option>
                    </#if>
                </#list>
                </select>
                </div>
            </#list>
              <input type="hidden" name="add_product_id" value="${product.productId}" />
            <div id="addCart1" style="display:none;">
              <span style="white-space: nowrap;"><strong>${uiLabelMap.CommonQuantity}:</strong></span>&nbsp;
              <input type="text" size="5" name="quantity" value="1" />
              <a href="javascript:javascript:addItem();" class="buttontext"><span style="white-space: nowrap;">${uiLabelMap.OrderAddToCart}</span></a>
              &nbsp;
            </div>
            <div id="addCart2" style="display:block;">
              <span style="white-space: nowrap;"><strong>${uiLabelMap.CommonQuantity}:</strong></span>&nbsp;
              <input type="text" size="5" value="1" disabled="disabled" />
              <a href="javascript:alert('Please select all features first');" class="buttontext"><span style="white-space: nowrap;">${uiLabelMap.OrderAddToCart}</span></a>
              &nbsp;
            </div>
          </#if>
          <#if !product.virtualVariantMethodEnum?exists || product.virtualVariantMethodEnum == "VV_VARIANTTREE">
           <#if variantTree?exists && (variantTree.size() &gt; 0)>
            <#list featureSet as currentType>
              <div>
                <select name="FT${currentType}" onchange="javascript:getList(this.name, (this.selectedIndex-1), 1);">
                  <option>${featureTypes.get(currentType)}</option>
                </select>
              </div>
            </#list>
            <span id="product_uom"></span>
            <input type="hidden" name="product_id" value="${product.productId}"/>
            <input type="hidden" name="add_product_id" value="NULL"/>
            <div>
              <strong><span id="product_id_display"> </span></strong>
              <strong><div id="variant_price_display"> </div></strong>
            </div>
          <#else>
            <input type="hidden" name="add_product_id" value="NULL"/>
            <#assign inStock = false />
          </#if>
         </#if>
        <#else>
          <input type="hidden" name="add_product_id" value="${product.productId}" />
          <#if (availableInventory?exists) && (availableInventory <= 0)>
            <#assign inStock = false />
          </#if>
        </#if>
        <#-- check to see if introductionDate hasnt passed yet -->
        <#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
        <p>&nbsp;</p>
          <div style="color: red;">${uiLabelMap.ProductProductNotYetMadeAvailable}.</div>
        <#-- check to see if salesDiscontinuationDate has passed -->
        <#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)>
          <div style="color: red;">${uiLabelMap.ProductProductNoLongerAvailable}.</div>
        <#-- check to see if the product requires inventory check and has inventory -->
        <#elseif product.virtualVariantMethodEnum?if_exists != "VV_FEATURETREE">
          <#if inStock>
            <#if product.requireAmount?default("N") == "Y">
              <#assign hiddenStyle = "visible" />
            <#else>
              <#assign hiddenStyle = "hidden"/>
            </#if>
            <div id="add_amount" class="${hiddenStyle}">
              <span style="white-space: nowrap;"><strong>${uiLabelMap.CommonAmount}:</strong></span>&nbsp;
              <input type="text" size="5" name="add_amount" value=""/>
            </div>
            <#if product.productTypeId?if_exists == "ASSET_USAGE">
              <div class="inline">
                <label>Start Date(yyyy-mm-dd)</label><input type="text" size="10" name="reservStart"/><a href="javascript:call_cal_notime(document.addform.reservStart, '${nowTimestamp.toString().substring(0,10)}');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" alt="Calendar" alt="" /></a>
                <label>End Date(yyyy-mm-dd)</label><input type="text" size="10" name="reservEnd"/><a href="javascript:call_cal_notime(document.addform.reservEnd, '${nowTimestamp.toString().substring(0,10)}');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" alt="Calendar" alt="" /></a>
              </div>
              <div>
                <#--td nowrap="nowrap" align="right">Number<br />of days</td><td><input type="textt" size="4" name="reservLength"/></td></tr><tr><td>&nbsp;</td><td align="right" nowrap="nowrap">&nbsp;</td-->
                Number of persons<input type="text" size="4" name="reservPersons" value="2"/>
                Number of rooms<input type="text" size="5" name="quantity" value="1"/>
              </div>
            <#else>
              <span><input name="quantity" id="quantity" value="1" size="4" maxLength="4" type="text" <#if product.isVirtual!?upper_case == "Y">disabled="disabled"</#if> /></span><a href="javascript:addItem()" id="addToCart" name="addToCart" class="buttontext">${uiLabelMap.OrderAddToCart}</a>
              <@showUnavailableVarients/>
            </#if>
            <#else>
              <#if productStore?exists>
                <#if productStore.requireInventory?exists && productStore.requireInventory == "N">
                  <span><input name="quantity" id="quantity" value="1" size="4" maxLength="4" type="text" <#if product.isVirtual!?upper_case == "Y">disabled="disabled"</#if> /></span><a href="javascript:addItem()" id="addToCart" name="addToCart" class="buttontext">${uiLabelMap.OrderAddToCart}</a>
                  <@showUnavailableVarients/>
                <#else>
                  <span><input name="quantity" id="quantity" value="1" size="4" maxLength="4" type="text" disabled="disabled" /></span><a href="javascript:void(0);" disabled="disabled" class="buttontext">${uiLabelMap.OrderAddToCart}</a><br />
                  <span>${uiLabelMap.ProductItemOutOfStock}<#if product.inventoryMessage?exists>&mdash; ${product.inventoryMessage}</#if></span>
                </#if>
              </#if>
          </#if>
        </#if>
        <#if variantPriceList?exists>
          <#list variantPriceList as vpricing>
            <#assign variantName = vpricing.get("variantName")?if_exists>
            <#assign secondVariantName = vpricing.get("secondVariantName")?if_exists>
            <#assign minimumQuantity = vpricing.get("minimumQuantity")>
            <#if minimumQuantity &gt; 0>
              <div>minimum order quantity for ${secondVariantName!} ${variantName!} is ${minimumQuantity!}</div>
            </#if>
          </#list>
        <#elseif minimumQuantity?exists && minimumQuantity?has_content && minimumQuantity &gt; 0>
           <div>minimum order quantity for ${productContentWrapper.get("PRODUCT_NAME")?if_exists} is ${minimumQuantity!}</div>
        </#if>
        </fieldset>
      </form>
    </div>
    <div>
      <#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
        <hr />
        <form name="addToShoppingList" method="post" action="<@ofbizUrl>addItemToShoppingList<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>">
          <fieldset>
          <input type="hidden" name="productId" value="${product.productId}" />
          <input type="hidden" name="product_id" value="${product.productId}" />
          <input type="hidden" name="productStoreId" value="${productStoreId}" />
          <input type="hidden" name="reservStart" value= "" />
          <select name="shoppingListId">
            <#if shoppingLists?has_content>
              <#list shoppingLists as shoppingList>
                <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
              </#list>
            </#if>
            <option value="">---</option>
            <option value="">${uiLabelMap.OrderNewShoppingList}</option>
          </select>
          &nbsp;&nbsp;
          <#if product.productTypeId?if_exists == "ASSET_USAGE">
              &nbsp;${uiLabelMap.CommonStartDate} (yyyy-mm-dd)<input type="text" size="10" name="reservStartStr" />Number of&nbsp;days<input type="text" size="4" name="reservLength" />&nbsp;Number of&nbsp;persons<input type="text" size="4" name="reservPersons" value="1" />Qty&nbsp;<input type="text" size="5" name="quantity" value="1" />
          <#else>
              <input type="text" size="5" name="quantity" value="1" />
              <input type="hidden" name="reservStartStr" value= "" />
          </#if>
          <a href="javascript:addShoplistSubmit();" class="buttontext">${uiLabelMap.OrderAddToShoppingList}</a>
          </fieldset>
        </form>
      <#else> <br />
        ${uiLabelMap.OrderYouMust} <a href="<@ofbizUrl>checkLogin/showcart</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonBeLogged}</a>
        ${uiLabelMap.OrderToAddSelectedItemsToShoppingList}.&nbsp;
      </#if>
      </div>
      <#-- Prefill first select box (virtual products only) -->
      <#if variantTree?exists && 0 &lt; variantTree.size()>
        <script type="text/javascript">eval("list" + "${featureOrderFirst}" + "()");</script>
      </#if>

      <#-- Swatches (virtual products only) -->
      <#if variantSample?exists && 0 &lt; variantSample.size()>
        <#assign imageKeys = variantSample.keySet() />
        <#assign imageMap = variantSample />
        <p>&nbsp;</p>
            <#assign maxIndex = 7 />
            <#assign indexer = 0 />
            <#list imageKeys as key>
              <#assign swatchProduct = imageMap.get(key) />
              <#if swatchProduct?has_content && indexer &lt; maxIndex>
                <#assign imageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(swatchProduct, "SMALL_IMAGE_URL", request)?if_exists />
                <#if !imageUrl?string?has_content>
                  <#assign imageUrl = productContentWrapper.get("SMALL_IMAGE_URL")?if_exists />
                </#if>
                <#if !imageUrl?string?has_content>
                  <#assign imageUrl = "/images/defaultImage.jpg" />
                </#if>
                  <a href="javascript:getList('FT${featureOrderFirst}','${indexer}',1);"><img src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${imageUrl}</@ofbizContentUrl>" width="60" height="60" alt="" /></a>
                  <br />
                  <a href="javascript:getList('FT${featureOrderFirst}','${indexer}',1);" class="linktext">${key}</a>
              </#if>
              <#assign indexer = indexer + 1 />
            </#list>
            <#if (indexer > maxIndex)>
              <div><strong>${uiLabelMap.ProductMoreOptions}</strong></div>
            </#if>
      </#if>

  <#-- Digital Download Files Associated with this Product -->
  <#if downloadProductContentAndInfoList?has_content>
    <div id="download-files">
      <div>${uiLabelMap.OrderDownloadFilesTitle}:</div>
      <#list downloadProductContentAndInfoList as downloadProductContentAndInfo>
        <div>${downloadProductContentAndInfo.contentName?if_exists}<#if downloadProductContentAndInfo.description?has_content> - ${downloadProductContentAndInfo.description}</#if></div>
      </#list>
    </div>
  </#if>

  <#-- Long description of product -->
  <div id="long-description">
      <div>${productContentWrapper.get("LONG_DESCRIPTION")?if_exists}</div>
      <div>${productContentWrapper.get("WARNINGS")?if_exists}</div>
  </div>

  <#-- Any attributes/etc may go here -->

  <#-- Product Reviews -->
    <div id="reviews">
      <div>${uiLabelMap.OrderCustomerReviews}:</div>
      <#if averageRating?exists && (averageRating &gt; 0) && numRatings?exists && (numRatings &gt; 1)>
          <div>${uiLabelMap.OrderAverageRating}: ${averageRating} <#if numRatings?exists>(${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})</#if></div>
      </#if>
<hr />
      <#if productReviews?has_content>
        <#list productReviews as productReview>
          <#assign postedUserLogin = productReview.getRelatedOne("UserLogin") />
          <#assign postedPerson = postedUserLogin.getRelatedOne("Person")?if_exists />
                    <div><strong>${uiLabelMap.CommonBy}: </strong><#if productReview.postedAnonymous?default("N") == "Y"> ${uiLabelMap.OrderAnonymous}<#else> ${postedPerson.firstName} ${postedPerson.lastName}&nbsp;</#if></div>
                    <div><strong>${uiLabelMap.CommonAt}: </strong>${productReview.postedDateTime?if_exists}&nbsp;</div>
                    <div><strong>${uiLabelMap.OrderRanking}: </strong>${productReview.productRating?if_exists?string}</div>
                    <div>&nbsp;</div>
                    <div>${productReview.productReview?if_exists}</div>
                <hr />
        </#list>
        <div>
            <a href="<@ofbizUrl>reviewProduct?category_id=${categoryId?if_exists}&amp;product_id=${product.productId}</@ofbizUrl>" class="linktext">${uiLabelMap.ProductReviewThisProduct}!</a>
        </div>
      <#else>
        <div>${uiLabelMap.ProductProductNotReviewedYet}.</div>
        <div>
            <a href="<@ofbizUrl>reviewProduct?category_id=${categoryId?if_exists}&amp;product_id=${product.productId}</@ofbizUrl>" class="linktext">${uiLabelMap.ProductBeTheFirstToReviewThisProduct}</a>
        </div>
    </div>
  </#if>
<#-- Upgrades/Up-Sell/Cross-Sell -->
  <#macro associated assocProducts beforeName showName afterName formNamePrefix targetRequestName>
  <#assign pageProduct = product />
  <#assign targetRequest = "product" />
  <#if targetRequestName?has_content>
    <#assign targetRequest = targetRequestName />
  </#if>
  <#if assocProducts?has_content>
    <h2>${beforeName?if_exists}<#if showName == "Y">${productContentWrapper.get("PRODUCT_NAME")?if_exists}</#if>${afterName?if_exists}</h2>

    <div class="productsummary-container">
    <#list assocProducts as productAssoc>
        <#if productAssoc.productId == product.productId>
            <#assign assocProductId = productAssoc.productIdTo />
        <#else>
            <#assign assocProductId = productAssoc.productId />
        </#if>
        <div>
          <a href="<@ofbizUrl>${targetRequest}/<#if categoryId?exists>~category_id=${categoryId}/</#if>~product_id=${assocProductId}</@ofbizUrl>" class="buttontext">
            ${assocProductId}
          </a>
        <#if productAssoc.reason?has_content>
          - <strong>${productAssoc.reason}</strong>
        </#if>
        </div>
      ${setRequestAttribute("optProductId", assocProductId)}
      ${setRequestAttribute("listIndex", listIndex)}
      ${setRequestAttribute("formNamePrefix", formNamePrefix)}
      <#if targetRequestName?has_content>
        ${setRequestAttribute("targetRequestName", targetRequestName)}
      </#if>
          ${screens.render(productsummaryScreen)}
      <#assign product = pageProduct />
      <#local listIndex = listIndex + 1 />
    </#list>
    </div>

    ${setRequestAttribute("optProductId", "")}
    ${setRequestAttribute("formNamePrefix", "")}
    ${setRequestAttribute("targetRequestName", "")}
  </#if>
</#macro>

<#assign productValue = product />
<#assign listIndex = 1 />
${setRequestAttribute("productValue", productValue)}
<div id="associated-products">
    <#-- also bought -->
    <@associated assocProducts=alsoBoughtProducts beforeName="" showName="N" afterName="${uiLabelMap.ProductAlsoBought}" formNamePrefix="albt" targetRequestName="" />
    <#-- obsolete -->
    <@associated assocProducts=obsoleteProducts beforeName="" showName="Y" afterName=" ${uiLabelMap.ProductObsolete}" formNamePrefix="obs" targetRequestName="" />
    <#-- cross sell -->
    <@associated assocProducts=crossSellProducts beforeName="" showName="N" afterName="${uiLabelMap.ProductCrossSell}" formNamePrefix="cssl" targetRequestName="crosssell" />
    <#-- up sell -->
    <@associated assocProducts=upSellProducts beforeName="${uiLabelMap.ProductUpSell} " showName="Y" afterName=":" formNamePrefix="upsl" targetRequestName="upsell" />
    <#-- obsolescence -->
    <@associated assocProducts=obsolenscenseProducts beforeName="" showName="Y" afterName=" ${uiLabelMap.ProductObsolescense}" formNamePrefix="obce" targetRequestName="" />
</div>

<#-- special cross/up-sell area using commonFeatureResultIds (from common feature product search) -->
<#if comsmonFeatureResultIds?has_content>
    <h2>${uiLabelMap.ProductSimilarProducts}</h2>

    <div class="productsummary-container">
        <#list commonFeatureResultIds as commonFeatureResultId>
            ${setRequestAttribute("optProductId", commonFeatureResultId)}
            ${setRequestAttribute("listIndex", commonFeatureResultId_index)}
            ${setRequestAttribute("formNamePrefix", "cfeatcssl")}
            <#-- ${setRequestAttribute("targetRequestName", targetRequestName)} -->
            ${screens.render(productsummaryScreen)}
        </#list>
    </div>
</#if>
</div>
