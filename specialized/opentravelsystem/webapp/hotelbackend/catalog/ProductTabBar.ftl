<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.security)?exists><#assign security = requestAttributes.security></#if>
<#if (requestAttributes.userLogin)?exists><#assign userLogin = requestAttributes.userLogin></#if>
<#if (requestAttributes.checkLoginUrl)?exists><#assign checkLoginUrl = requestAttributes.checkLoginUrl></#if>


<#if product?has_content>
	<#assign displayApps = [
 		{"title":"${uiLabelMap.ProductProduct}",		"menuname":"EditProduct",		"url":"EditProduct?productId=${productId}"}
		,{"title":"${uiLabelMap.ProductPrices}","menuname":"EditProductPrices",	"url":"EditProductPrices?productId=${productId}"}
		,{"title":"${uiLabelMap.ProductContent}",		"menuname":"EditProductContent",	"url":"EditProductContent?productId=${productId}"}
		,{"title":"${uiLabelMap.ProductCategories}","menuname":"EditProductCategories",			"url":"EditProductCategories?productId=${productId}"}
		,{"title":"${uiLabelMap.ProductVariants}", 	"menuname":"QuickAddVariants",		"url":"QuickAddVariants?productId=${productId}"}

		]>

<#assign unselectedClass = {"lowest_leftmenu_col" : "lowest_leftmenu_tabdownblock", "lowest_leftmenu_center" : "lowest_leftmenu_tabdowncenter", "lowest_leftmenu_link" : "lowest_leftmenu_tablink"}>
<#assign selectedClass = {"lowest_leftmenu_col" : "lowest_leftmenu_mainblock", "lowest_leftmenu_center" : "lowest_leftmenu_tabupcenter", "lowest_leftmenu_link" : "lowest_leftmenu_tablinkselected"}>
<#if !appTabButtonItem?has_content><#assign appTabButtonItem = page.appTabButtonItem?default("void")></#if>
<#assign thisAppOpt = page.tabButtonItem?default("void")>

<#if requestAttributes.userLogin?has_content>
<table  width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">      
    <td height="19px" width="100%">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
       <tr>
          <#list displayApps as display>
	          <#if thisAppOpt == display.menuname>
	            <#assign class = selectedClass>
	          <#else>
	            <#assign class = unselectedClass>
	          </#if>

	          <td valign="bottom" height="19px" class="${class.lowest_leftmenu_col}">
	            <table width="100%" border="0" cellspacing="0" cellpadding="0">
	              <tr>
	                <td class="${class.lowest_leftmenu_center}"><a href=<#if display.title == "Website">"${display.url}" target="newwindow"<#else>"<@ofbizUrl>${display.url}</@ofbizUrl>"</#if> title="" class="${class.lowest_leftmenu_link}">${display.title}</a></td>
					<td height="1px">&nbsp;</td>
					</tr>					
	            </table>
	          </td>
          </#list> 
          <#-- KAN DIT?? -->
          <#if product?exists && product.productTypeId?if_exists == "ASSET_USAGE">
          	<tr>
          		<td><a href="<@ofbizUrl>EditProductAssetUsage?productId=${productId}</@ofbizUrl>" class="${selectedClassMap.EditProductAssetUsage?default(unselectedClassName)}">${uiLabelMap.ProductAssetUsage}</a><br/></td>
          	</tr>	
       	</#if>         
        </tr>        
      </table>
    </td>
  </tr>
</table>
</#if>
</#if>