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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision$
 *@since      2.1
-->
<#-- variable setup -->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#assign product = requestAttributes.product?if_exists>
<#assign price = requestAttributes.priceMap?if_exists>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
<#-- end variable setup -->

<#-- virtual product javascript -->
${requestAttributes.virtualJavaScript?if_exists}
<script language="JavaScript">
<!--
    function addItem() {
       if (document.addform.add_product_id.value == 'NULL') {
           alert("Please enter all the required information.");
           return;
       } else {
           document.addform.submit();
       }
    }
 //-->
 </script>


<table border="0" width="100%" cellpadding="2" cellspacing='0'>
  <#-- Category next/previous -->
  <#if requestAttributes.category?exists>
    <tr>
      <td colspan="2" align="right">
        <#if requestAttributes.previousProductId?exists>
          <a href='<@ofbizUrl>/product/~category_id=${requestAttributes.categoryId?if_exists}/~product_id=${requestAttributes.previousProductId?if_exists}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonPrevious}]</a>&nbsp;|&nbsp;
        </#if>
        <a href="<@ofbizUrl>/category/~category_id=${requestAttributes.categoryId?if_exists}</@ofbizUrl>" class="buttontext">${requestAttributes.category.description?if_exists}</a>
        <#if requestAttributes.nextProductId?exists>
          &nbsp;|&nbsp;<a href='<@ofbizUrl>/product/~category_id=${requestAttributes.categoryId?if_exists}/~product_id=${requestAttributes.nextProductId?if_exists}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonNext}]</a>
        </#if>
      </td>
    </tr>
  </#if>

  <tr><td colspan="2"><hr class='sepbar'></td></tr>

  <#-- Product image/name/price -->
  <tr>
    <td align="left" valign="top" width="0">
      <#if product.largeImageUrl?exists>
        <img src='<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${product.largeImageUrl?if_exists}</@ofbizContentUrl>' name='mainImage' vspace='5' hspace='5' border='1' align='left'>
      </#if>
    </td>
    <td align="left" valign="top">
      <div class="head2">${product.productName?if_exists}</div>
      <div class="tabletext">${product.longDescription?if_exists}</div>		<br>
      <#if daysToShip?exists>
        <div class="tabletext"><b>${uiLabelMap.ProductUsuallyShipsIn} <font color='red'>${daysToShip}</font> ${uiLabelMap.CommonDays}!<b></div>
      </#if>
		<br/>
      <div class="tabletext"><a href="<@ofbizUrl>/productfaq</@ofbizUrl>">FAQ</div></a>
	   <br/>
	  <div class="tabletext"><a href="<@ofbizUrl>/productdetail.pdf/~product_id=${product.productId?if_exists}</@ofbizUrl>">View as PDF</a></div>
		<br/>	   
      <div class="tabletext"><b>Item: ${product.productId?if_exists}</b>
      <#-- for prices:
              - if price < listPrice, show
              - if price < defaultPrice and defaultPrice < listPrice, show default
              - if isSale show price with salePrice style and print "On Sale!"
      -->

      <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
        ${uiLabelMap.ProductListPrice}: <@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed/>
      </#if>
      <#if price.listPrice?exists && price.basePrice?exists && price.price?exists && price.price?double < price.defaultPrice?double && price.defaultPrice?double < price.listPrice?double>
        ${uiLabelMap.ProductRegularPrice}: <@ofbizCurrency amount=price.defaultPrice isoCode=price.currencyUsed/>
      </#if>
        <b>
          <#if price.isSale>
            <span class='salePrice'>${uiLabelMap.EcommerceOnSale}!</span>
            <#assign priceStyle = "salePrice">
          <#else>
            <#assign priceStyle = "regularPrice">
          </#if>
            ${uiLabelMap.EcommerceYourPrice}: <span class='${priceStyle}'><@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></span>
        </b>
      </div>

      <#-- Included quantities/pieces -->
      <#if product.quantityIncluded?exists && product.quantityIncluded?double != 0>
        <div class="tabletext">${uiLabelMap.EcommerceIncludes}:
          ${product.quantityIncluded?if_exists}
          ${product.quantityUomId?if_exists}
        </div>
      </#if>
      <#if product.piecesIncluded?exists && product.piecesIncluded?long != 0>
        <div class="tabletext">${uiLabelMap.EcommercePieces}:
          ${product.piecesIncluded}
        </div>
      </#if>

      <#-- tell a friend
      <div class="tabletext">&nbsp;</div>
      <div class="tabletext">
        <a href="javascript:popUpSmall('<@ofbizUrl>/tellafriend?productId=${product.productId}</@ofbizUrl>','tellafriend');" class="buttontext">${uiLabelMap.CommonTellAFriend}</a>
      </div>-->
      <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="addform" style='margin: 0;'>
        <#assign inStock = true>
        <#-- Variant Selection -->
        <#if product.isVirtual?exists && product.isVirtual?upper_case == "Y">
          <#if requestAttributes.variantTree?exists && 0 < requestAttributes.variantTree.size()>
            <p>&nbsp;</p>
            <#list requestAttributes.featureSet as currentType>
              <div class="tabletext">
                <select name="${currentType}" class="selectBox" onChange="getList(this.name, this.options[this.selectedIndex].value)">
                  <option>${requestAttributes.featureTypes.get(currentType)}</option>
                </select>
              </div>
            </#list>
            <input type='hidden' name="product_id" value='${product.productId}'>
            <input type='hidden' name="add_product_id" value='NULL'>
          <#else>
            <input type='hidden' name="product_id" value='${product.productId}'>
            <input type='hidden' name="add_product_id" value='NULL'>
            <div class='tabletext'><b>${uiLabelMap.ProductItemOutofStock}.</b></div>
            <#assign inStock = false>
          </#if>
        <#else>
          <input type='hidden' name="product_id" value='${product.productId}'>
          <input type='hidden' name="add_product_id" value='${product.productId}'>
          <#if !Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryAvailable(request, product.productId?string, 1.0?double)>
            <#if Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequired(request, product)>
              <div class='tabletext'><b>${uiLabelMap.ProductItemOutofStock}.</b></div>
              <#assign inStock = false>
            <#else>
              <div class='tabletext'><b>${product.inventoryMessage?if_exists}</b></div>
            </#if>
          </#if>
        </#if>
        <p>&nbsp;</p>

        <#-- check to see if introductionDate hasn't passed yet -->
        <#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
          <div class='tabletext' style='color: red;'>${uiLabelMap.ProductProductNotYetMadeAvailable}.</div>
        <#-- check to see if salesDiscontinuationDate has passed -->
        <#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)>
          <div class='tabletext' style='color: red;'>${uiLabelMap.ProductProductNoLongerAvailable}.</div>
        <#-- check to see if the product requires inventory check and has inventory -->
        <#else>
          <#if inStock>
            <#if product.requireAmount?default("N") == "Y">
              <#assign hiddenStyle = "tabletext">
            <#else>
              <#assign hiddenStyle = "tabletexthidden">
            </#if>
            <div id="add_amount" class="${hiddenStyle}">
              <nobr><b>Amount:</b></nobr>&nbsp;
              <input type="text" class="inputBox" size="5" name="add_amount" value="">
            </div>
          <#if security.hasEntityPermission("WHOLESALE_ORDER", "_CREATE", session)><nobr>              
          	<input type="text" class="inputBox" size="5" name="quantity" value="1">&nbsp;
            <a href="javascript:addItem()" class="buttontext">[${uiLabelMap.EcommerceAddtoCart}]</nobr></a>
          </#if>
          </#if>
          <#if requestParameters.category_id?exists>
            <input type='hidden' name='category_id' value='${requestParameters.category_id}'>
          </#if>
        </#if>
      </form>
	<div class="tabletext">
      <#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
        <hr class="sepbar">
        <form name="addToShoppingList" method="post" action="<@ofbizUrl>/addItemToShoppingList<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>">
          <input type="hidden" name="productId" value="${requestParameters.product_id}">
          <input type="hidden" name="product_id" value="${requestParameters.product_id}">
          <select name="shoppingListId" class="selectBox">
            <#if shoppingLists?has_content>
              <#list shoppingLists as shoppingList>
                <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
              </#list>
            </#if>
            <option value="">---</option>
            <option value="">${uiLabelMap.WholesaleNewShoppingList}</option>
          </select>
          &nbsp;&nbsp;
          <input type="text" size="5" class="inputBox" name="quantity" value="1">
          <a href="javascript:document.addToShoppingList.submit();" class="buttontext">[${uiLabelMap.WholesaleAddtoShoppingList}]</a>
        </form>
      <#else> <br>
        ${uiLabelMap.EcommerceYouMust} <a href="<@ofbizUrl>/checkLogin/showcart</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonLogin}</a>
        $uiLabelMap.WholesaleToAddSelectedItemsToShoppingList}.&nbsp;
      </#if>
	  </div>
      <#-- Prefill first select box (virtual products only) -->
      <#if requestAttributes.variantTree?exists && 0 < requestAttributes.variantTree.size()>
        <script language="JavaScript">eval("list" + "${requestAttributes.featureOrderFirst}" + "()");</script>
      </#if>

      <#-- Swatches (virtual products only) -->
      <#if requestAttributes.variantSample?exists && 0 < requestAttributes.variantSample.size()>
        <#assign imageKeys = requestAttributes.variantSample.keySet()>
        <#assign imageMap = requestAttributes.variantSample>
        <p>&nbsp;</p>
        <table cellspacing="0" cellpadding="0">
          <tr>
            <#assign indexer = 0>
            <#list imageKeys as key>
              <#assign swatchProduct = imageMap.get(key)>
              <#assign imageUrl = swatchProduct.smallImageUrl?if_exists>
              <#if swatchProduct?exists && swatchProduct.smallImageUrl?exists>
                <td align="center" valign="bottom">
                  <a href="#"><img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${swatchProduct.smallImageUrl}</@ofbizContentUrl>" border="0" width="60" height="60" onclick="javascript:getList('${requestAttributes.featureOrderFirst}','${indexer}',1);"></a>
                  <br>
                  <a href="#" class="buttontext" onclick="javascript:getList('${requestAttributes.featureOrderFirst}','${indexer}',1);">${key}</a>
                </td>
                <#assign indexer = indexer + 1>
              </#if>
            </#list>
          </tr>
        </table>
      </#if>

<#-- Upgrades/Up-Sell/Cross-Sell -->
  <#macro associated assocProducts beforeName showName afterName formNamePrefix targetRequestName>
  <#assign targetRequest = "product">
  <#if targetRequestName?has_content>
    <#assign targetRequest = targetRequestName>
  </#if>
  <#if assocProducts?has_content>
    <tr><td>&nbsp;</td></tr>
    <tr><td colspan="2"><div class="head2">${beforeName?if_exists}<#if showName == "Y">${productValue.productName}</#if>${afterName?if_exists}</div></td></tr>
    <tr><td><hr class='sepbar'></td></tr>
	<tr><td>
	<table>
      <tr>
    <#list assocProducts as productAssoc>
      ${setRequestAttribute("optProductId", productAssoc.productIdTo)}
      ${setRequestAttribute("listIndex", listIndex)}
      ${setRequestAttribute("formNamePrefix", formNamePrefix)}
      <#if targetRequestName?has_content>
        ${setRequestAttribute("targetRequestName", targetRequestName)}
      </#if>
        <td>
          ${pages.get("/catalog/productshortsum.ftl")}
        </td>
      <#local listIndex = listIndex + 1>
    </#list>
     </tr>
    </table>
    ${setRequestAttribute("optProductId", "")}
    ${setRequestAttribute("formNamePrefix", "")}
    ${setRequestAttribute("targetRequestName", "")}
  </#if>
</#macro>
<#assign productValue = product>
<#assign listIndex = 1>
${setRequestAttribute("productValue", productValue)}

<table width='100%'>
  <#-- obsolete -->
  <@associated assocProducts=requestAttributes.obsoleteProducts beforeName="" showName="Y" afterName=" is made obsolete by these products:" formNamePrefix="obs" targetRequestName=""/>
  <#-- cross sell -->
  <@associated assocProducts=requestAttributes.crossSellProducts beforeName="" showName="N" afterName="Cross-sell suggestions:" formNamePrefix="cssl" targetRequestName="crosssell"/>
  <#-- up sell -->
  <@associated assocProducts=requestAttributes.upSellProducts beforeName="Upsell suggestions:" showName="N" afterName="" formNamePrefix="upsl" targetRequestName="upsell"/>
</table>
  <#-- end of upgrade/up-sell/replacement code -->
    </td>
  </tr>


  <#-- Any attributes/etc may go here -->

  <#-- Product Reviews 
  <tr>
    <td colspan="2">
      <div class="tableheadtext">${uiLabelMap.EcommerceCustomerReviews}:</div>
    </td>
  </tr>
  <tr><td colspan="2"><hr class='sepbar'></td></tr>
  <#if requestAttributes.productReviews?has_content>
    <#list requestAttributes.productReviews as productReview>
      <#assign postedUserLogin = productReview.getRelatedOne("UserLogin")>
      <#assign postedPerson = postedUserLogin.getRelatedOne("Person")>
      <tr>
        <td colspan="2">
          <table border="0" width="100%" cellpadding="0" cellspacing='0'>
            <tr>
              <td>
                <div class="tabletext"><b>${uiLabelMap.CommonBy}: </b><#if productReview.postedAnonymous?default("N") == "Y">${uiLabelMap.EcommerceAnonymous}<#else>${postedPerson.firstName} ${postedPerson.lastName}</#if></div>
              </td>
              <td>
                <div class="tabletext"><b>${uiLabelMap.CommonOn}: </b>${productReview.postedDateTime?if_exists}</div>
              </td>
              <td>
                <div class="tabletext"><b>${uiLabelMap.EcommerceRanking}: </b>${productReview.productRating?if_exists?string}</div>
              </td>
            </tr>
            <tr>
              <td colspan="3">
                <div class="tabletext">&nbsp;</div>
              </td>
            </tr>
            <tr>
              <td colspan="3">
                <div class="tabletext">${productReview.productReview?if_exists}</div>
              </td>
            </tr>
            <tr><td colspan="3"><hr class='sepbar'></td></tr>
          </table>
        </td>
      </tr>
    </#list>
    <tr>
      <td colspan="2">
        <a href="<@ofbizUrl>/reviewProduct?category_id=${requestAttributes.categoryId?if_exists}&product_id=${product.productId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductReviewThisProduct}!</a>
      </td>
    </tr>
  <#else>
    <tr>
      <td colspan="2">
        <div class="tabletext">${uiLabelMap.ProductProductNotReviewedYet}.</div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <a href="<@ofbizUrl>/reviewProduct?category_id=${requestAttributes.categoryId?if_exists}&product_id=${product.productId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductBeTheFirstToReviewThisProduct}!</a>
      </td>
    </tr>
  </#if> -->
</table>


