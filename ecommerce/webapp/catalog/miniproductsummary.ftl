
<#if miniProduct?exists>
    <#assign nowTimestamp = Static["org.ofbiz.core.util.UtilDateTime"].nowTimestamp()>
    <#-- calculate the "your" price -->
    <#assign priceParams = {
        "product":miniProduct, 
        "prodCatalogId":Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getCurrentCatalogId(request),
        "webSiteId":Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getWebSiteId(request),
        "autoUserLogin":sessionAttributes.autoUserLogin,
        "partyId":userLogin.partId
        }>
    <#assign priceResult = dispatcher.runSync("calculateProductPrice", priceParams)>
    <#-- returns: isSale, price, orderItemPriceInfos -->
    
  <a href='<transform ofbizUrl>/product?product_id=${miniProduct.productId}</transform>' class='buttontext'>${miniProduct.productName}</a>
  <div class='tabletext'><b>${miniProduct.productId}, 
        <span class='<#if priceResult.isSale>salePrice<#else>normalPrice</#if>'>${priceResult.price?string.currency}</span></b></div>
        
  <#if miniProduct.introductionDate?exists && nowTimestamp.before(miniProduct.introductionDate)>
      <#-- check to see if introductionDate hasn't passed yet -->
      <div class='tabletext' style='color: red;'>Not Yet Available</div>
  <#elseif miniProduct.salesDiscontinuationDate?exists && nowTimestamp.after(miniProduct.salesDiscontinuationDate)>
      <#-- check to see if salesDiscontinuationDate has passed -->
      <div class='tabletext' style='color: red;'>No Longer Available</div>
  <#elseif miniProduct.isVirtual = "Y"> 
      <a href='<transform ofbizUrl>/product?<#if requestParameters.category_id?exists>category_id=${requestParameters.category_id}&</#if>product_id=${miniProduct.productId}</transform>' class="buttontext"><nobr>[Choose Variation...]</nobr></a>
  <#else>
      <form method="POST" action="<transform ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></transform>" name="${miniProdFormName}" style='margin: 0;'>
        <input type='hidden' name="add_product_id" value='${miniProduct.productId}'>
        <input type='hidden' name="quantity" value="${miniProdQuantity}">
        <#if requestParameters.order_id?has_content><input type='hidden' name='order_id' value='${requestParameters.order_id}'></#if>
        <#if requestParameters.product_id?has_content><input type='hidden' name='product_id' value='${requestParameters.product_id}'></#if>
        <#if requestParameters.category_id?has_content><input type='hidden' name='category_id' value='${requestParameters.category_id}'></#if>
        <#if requestParameters.VIEW_INDEX?has_content><input type='hidden' name='VIEW_INDEX' value='${requestParameters.VIEW_INDEX}'></#if>
        <#if requestParameters.SEARCH_STRING?has_content><input type='hidden' name='SEARCH_STRING' value='${requestParameters.SEARCH_STRING}'></#if>
        <#if requestParameters.SEARCH_CATEGORY_ID?has_content><input type='hidden' name='SEARCH_CATEGORY_ID' value='${requestParameters.SEARCH_CATEGORY_ID}'></#if>
        <a href="javascript:document.${miniProdFormName}.submit()" class="buttontext"><nobr>[Add ${miniProdQuantity} to Cart]</nobr></a>
      </form>
  </#if>
</#if>

