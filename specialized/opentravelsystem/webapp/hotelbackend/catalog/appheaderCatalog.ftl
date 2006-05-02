<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Rev: 5462 $
 *@since      2.1
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.security)?exists><#assign security = requestAttributes.security></#if>
<#if (requestAttributes.userLogin)?exists><#assign userLogin = requestAttributes.userLogin></#if>
<#if (requestAttributes.checkLoginUrl)?exists><#assign checkLoginUrl = requestAttributes.checkLoginUrl></#if>

<#assign unselectedTabClassName = "appButton">
<#assign selectedTabClassMap = {page.headerItem?default("void") : "appButtonSelected"}>

<#if requestAttributes.userLogin?has_content>
<div class="apptitle">&nbsp;${uiLabelMap.opentravelsystemCatalog}</div>
<div class="appContainer">
  <a href="<@ofbizUrl>mainCatalog</@ofbizUrl>" class="${selectedTabClassMap.main?default(unselectedTabClassName)}">${uiLabelMap.opentravelsystemMain}</a> 
  <a href="<@ofbizUrl>EditProdCatalogCategories</@ofbizUrl>" class="${selectedTabClassMap.categories?default(unselectedTabClassName)}">${uiLabelMap.opentravelsystemCategories}</a>    
  <a href="<@ofbizUrl>EditProducts</@ofbizUrl>" class="${selectedTabClassMap.products?default(unselectedTabClassName)}">${uiLabelMap.opentravelsystemProducts}</a>   
  <a href="<@ofbizUrl>EditFixedAssets</@ofbizUrl>" class="${selectedTabClassMap.fixedassets?default(unselectedTabClassName)}">${uiLabelMap.opentravelsystemFixedAssets}</a> 
  <a href="<@ofbizUrl>EditProductStore</@ofbizUrl>" class="${selectedTabClassMap.store?default(unselectedTabClassName)}">${uiLabelMap.opentravelsystemStore}</a>   
  <!--div class="col"><a href="<@ofbizUrl>selectStore</@ofbizUrl>" class="${selectedTabClassMap.selectStore?default(unselectedTabClassName)}">${uiLabelMap.selectStore}</a>-->  
  <a href="<@ofbizUrl>importProduct</@ofbizUrl>" class="${selectedTabClassMap.importProduct?default(unselectedTabClassName)}">importProduct</a>    

  <#if userLogin?has_content>
   <a href="<@ofbizUrl>logout</@ofbizUrl>" class="${selectedTabClassMap.logout?default(unselectedTabClassName)}">${uiLabelMap.CommonLogout}</a>
  <#else>
   <a href='<@ofbizUrl>${checkLoginUrl?if_exists}</@ofbizUrl>' class='${selectedTabClassMap.login?default(unselectedTabClassName)}'>${uiLabelMap.CommonLogin}</a>
  </#if>
  
</div>
</#if>