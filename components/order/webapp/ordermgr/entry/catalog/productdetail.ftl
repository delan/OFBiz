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
 *@version    $Revision: 1.4 $
 *@since      2.1
-->

<#-- variable setup -->
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
          <a href='<@ofbizUrl>/product/~category_id=${requestAttributes.categoryId?if_exists}/~product_id=${requestAttributes.previousProductId?if_exists}</@ofbizUrl>' class="buttontext">[Previous]</a>&nbsp;|&nbsp;
        </#if>
        <a href="<@ofbizUrl>/category/~category_id=${requestAttributes.categoryId?if_exists}</@ofbizUrl>" class="buttontext">${requestAttributes.category.description?if_exists}</a>
        <#if requestAttributes.nextProductId?exists>
          &nbsp;|&nbsp;<a href='<@ofbizUrl>/product/~category_id=${requestAttributes.categoryId?if_exists}/~product_id=${requestAttributes.nextProductId?if_exists}</@ofbizUrl>' class="buttontext">[Next]</a>
        </#if>
      </td>
    </tr>
  </#if>
  
  <tr><td colspan="2"><hr class='sepbar'></td></tr>
  
  <#-- Product image/name/price -->
  <tr>
    <td align="left" valign="top" width="0">
      <#if product.largeImageUrl?exists>
        <img src='<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${product.largeImageUrl?if_exists}</@ofbizContentUrl>' name='mainImage' vspace='5' hspace='5' border='1' width='200' align='left'>
      </#if>
    </td>
    <td align="right" valign="top">
      <div class="head2">${product.productName?if_exists}</div>
      <div class="tabletext">${product.description?if_exists}</div>
      <#if product.productId?has_content>
        <div class="tabletext"><b>${product.productId}</b> <a href="/catalog/control/EditProduct?productId=${product.productId}&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class="buttontext">[Edit&nbsp;Product]</a></div>
      </#if>
      <#-- for prices:
              - if price < listPrice, show
              - if price < defaultPrice and defaultPrice < listPrice, show default
              - if isSale show price with salePrice style and print "On Sale!"
      -->
      <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
        <div class="tabletext">List price: <span class='basePrice'>${price.listPrice?string.currency}</span></div>
      </#if>
      <#if price.listPrice?exists && price.basePrice?exists && price.price?exists && price.price?double < price.defaultPrice?double && price.defaultPrice?double < price.listPrice?double>
        <div class="tabletext">Regular price: <span class='basePrice'>${price.defaultPrice?string.currency}</span></div>
      </#if>     
      <div class="tabletext">
        <b>
          <#if price.isSale>
            <span class='salePrice'>On Sale!</span>
            <#assign priceStyle = "salePrice">
          <#else>
            <#assign priceStyle = "regularPrice">
          </#if>
            Your price: <span class='${priceStyle}'>${price.price?string.currency}</span>
        </b>
      </div>
      
      <#-- Included quantities/pieces -->
      <#if product.quantityIncluded?exists && product.quantityIncluded?double != 0>     
        <div class="tabletext">Includes:
          ${product.quantityIncluded?if_exists}
          ${product.quantityUomId?if_exists}
        </div>
      </#if>
      <#if product.piecesIncluded?exists && product.piecesIncluded?long != 0>
        <div class="tabletext">Pieces:
          ${product.piecesIncluded}
        </div>
      </#if>
                
      <form method="POST" action="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="addform" style='margin: 0;'>
        <#assign inStock = true>     
        <#-- Variant Selection -->
        <#if product.isVirtual?exists && product.isVirtual?upper_case == "Y">
          <#if requestAttributes.variantTree?exists && 0 < requestAttributes.variantTree.size()>
            <p>&nbsp;</p>
            <#list requestAttributes.featureSet as currentType>
              <div class="tabletext">
                <select name="${currentType}" onChange="getList(this.name, this.options[this.selectedIndex].value)">
                  <option>${requestAttributes.featureTypes.get(currentType)}</option>
                </select>
              </div>
            </#list>
            <input type='hidden' name="product_id" value='${product.productId}'>
            <input type='hidden' name="add_product_id" value='NULL'>
          <#else>
            <input type='hidden' name="product_id" value='${product.productId}'>
            <input type='hidden' name="add_product_id" value='NULL'>
            <div class='tabletext'><b>This item is out of stock.</b></div>
            <#assign inStock = false>
          </#if>
        <#else>          
          <input type='hidden' name="product_id" value='${product.productId}'>
          <input type='hidden' name="add_product_id" value='${product.productId}'>
          <#if !Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryAvailable(request, product.productId?string, 1.0?double)>
            <#if Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequired(request, product)> 
              <div class='tabletext'><b>This item is out of stock.</b></div>
              <#assign inStock = false>
            <#else>
              <div class='tabletext'><b>${product.inventoryMessage?if_exists}</b></div>
            </#if>
          </#if>
        </#if>

        <p>&nbsp;</p>
        
        <#-- check to see if introductionDate hasn't passed yet -->
        <#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
          <div class='tabletext' style='color: red;'>This product has not yet been made available for sale.</div>
        <#-- check to see if salesDiscontinuationDate has passed -->
        <#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)>
          <div class='tabletext' style='color: red;'>This product is no longer available for sale.</div>
        <#-- check to see if the product requires inventory check and has inventory -->
        <#else>        
          <#if inStock>
            <a href="javascript:addItem()" class="buttontext"><nobr>[Add to Cart]</nobr></a>&nbsp;
            <input type="text" class="inputBox" size="5" name="quantity" value="1">
          </#if>
          <#if requestParameters.category_id?exists>
            <input type='hidden' name='category_id' value='${requestParameters.category_id}'>
          </#if>
        </#if>
      </form>
      
      <#if shoppingLists?has_content>
        <hr class="sepbar">
        <form name="addToShoppingList" method="post" action="<@ofbizUrl>/addItemToShoppingList</@ofbizUrl>">
          <input type="hidden" name="productId" value="${requestParameters.product_id}">
          <select name="shoppingListId" class="selectBox">
            <#list shoppingLists as shoppingList>
              <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
            </#list>
          </select>
          &nbsp;
          <input type="text" size="5" class="inputBox" name="quantity" value="1">
          <a href="javascript:document.addToShoppingList.submit();" class="buttontext">[Add To Shopping List]</a>
        </form>
      </#if>
       
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
              <#assign product = imageMap.get(key)>
              <#assign imageUrl = product.smallImageUrl?if_exists>       
              <#if product?exists && product.smallImageUrl?exists>                  
                <td align="center" valign="bottom">
                  <a href="#"><img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${product.smallImageUrl}</@ofbizContentUrl>" border="0" width="60" height="60" onclick="javascript:getList('${requestAttributes.featureOrderFirst}','${indexer}',1);"></a>
                  <br>
                  <a href="#" class="buttontext" onclick="javascript:getList('${requestAttributes.featureOrderFirst}','${indexer}',1);">${key}</a>
                </td>
                <#assign indexer = indexer + 1>
              </#if>
            </#list>
          </tr>
        </table>        
      </#if>    
    </td>
  </tr>
  
  <tr><td colspan="2"><hr class='sepbar'></td></tr>
  
  <#-- Long description of product -->
  <tr>
    <td colspan="2">
      <div class="tabletext">${product.longDescription?if_exists}</div>
    </td>
  </tr>
  
  <tr><td colspan="2"><hr class='sepbar'></td></tr>
  
  <#-- Any attributes/etc may go here -->
  
  <#-- Product Reviews -->
  <tr>
    <td colspan="2">
      <div class="tableheadtext">Customer Reviews:</div>
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
                <div class="tabletext"><b>By: </b><#if productReview.postedAnonymous?default("N") == "Y">Anonymous<#else>${postedPerson.firstName} ${postedPerson.lastName}</#if></div>
              </td>
              <td>
                <div class="tabletext"><b>On: </b>${productReview.postedDateTime?if_exists}</div>
              </td>
              <td>
                <div class="tabletext"><b>Ranking: </b>${productReview.productRating?if_exists?string}</div>
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
        <a href="<@ofbizUrl>/reviewProduct?category_id=${requestAttributes.categoryId?if_exists}&product_id=${product.productId}</@ofbizUrl>" class="buttontext">Review This Product!</a>      
      </td>
    </tr>    
  <#else>
    <tr>
      <td colspan="2">
        <div class="tabletext">This product hasn't been reviewed yet.</div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <a href="<@ofbizUrl>/reviewProduct?category_id=${requestAttributes.categoryId?if_exists}&product_id=${product.productId}</@ofbizUrl>" class="buttontext">Be The First To Review This Product!</a>      
      </td>
    </tr>       
  </#if>  
</table>

<#-- Upgrades/Up-Sell/Cross-Sell -->
<#macro associated assocProducts beforeName showName afterName formNamePrefix>
  <#if assocProducts?has_content>
    <tr><td>&nbsp;</td></tr> 
    <tr><td colspan="2"><div class="head2">${beforeName?if_exists}<#if showName == "Y">${productValue.productName}</#if>${afterName?if_exists}</div></td></tr>
    <tr><td><hr class='sepbar'></td></tr>    
    <#list assocProducts as productAssoc>
      <tr><td>
        <div class="tabletext">
          <a href='<@ofbizUrl>/product/~product_id=${productAssoc.productIdTo?if_exists}</@ofbizUrl>' class="buttontext">
            ${productAssoc.productIdTo?if_exists}
          </a>
          - <b>${productAssoc.reason?if_exists}</b>
        </div>
      </td></tr>      
      ${setRequestAttribute("optProductId", productAssoc.productIdTo)}
      ${setRequestAttribute("listIndex", listIndex)}
      ${setRequestAttribute("formNamePrefix", formNamePrefix)}
      <tr>
        <td>
          ${pages.get("/entry/catalog/productsummary.ftl")}
        </td>
      </tr>
      <#local listIndex = listIndex + 1>
      <tr><td><hr class='sepbar'></td></tr>
    </#list>           
    ${setRequestAttribute("optProductId", "")}
  </#if>      
</#macro>
<#assign productValue = product>
<#assign listIndex = 1>
${setRequestAttribute("productValue", productValue)}

<table width='100%'>
  <#-- obsolete -->
  <@associated assocProducts=requestAttributes.obsoleteProducts beforeName="" showName="Y" afterName=" is made obsolete by these products:" formNamePrefix="obs"/>
  <#-- cross sell -->
  <@associated assocProducts=requestAttributes.crossSellProducts beforeName="" showName="N" afterName="You might be interested in these as well:" formNamePrefix="cssl"/>
  <#-- up sell -->
  <@associated assocProducts=requestAttributes.upSellProducts beforeName="Try these instead of " showName="Y" afterName=":" formNamePrefix="upsl"/>
  <#-- obsolescence -->
  <@associated assocProducts=requestAttributes.obsolenscenseProducts beforeName="" showName="Y" afterName=" makes these products obsolete:" formNamePrefix="obce"/>
</table>

