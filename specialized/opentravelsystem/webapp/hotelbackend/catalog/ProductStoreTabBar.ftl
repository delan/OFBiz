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
 *@author     Catherine.Heintz@nereide.biz (migration to UiLabel)
 *@version    $Rev: 3103 $
 *@since      2.2
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
  <#assign unselectedClassName = "tabButton">
  <#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>
  <#if productStoreId?has_content>
    <div class='tabContainer'>
      <a href="<@ofbizUrl>/EditProductStore</@ofbizUrl>" class="${selectedClassMap.EditProductStore?default(unselectedClassName)}">${uiLabelMap.ProductStore}</a>
      <a href="<@ofbizUrl>/EditProductStoreRoles</@ofbizUrl>" class="${selectedClassMap.EditProductStoreRoles?default(unselectedClassName)}">${uiLabelMap.PartyRoles}</a>
      <!--a href="<@ofbizUrl>/EditProductStorePromos</@ofbizUrl>" class="${selectedClassMap.EditProductStorePromos?default(unselectedClassName)}">${uiLabelMap.ProductPromos}</a>
	  <a href="<@ofbizUrl>/EditProductStoreCatalogs</@ofbizUrl>" class="${selectedClassMap.EditProductStoreCatalogs?default(unselectedClassName)}">${uiLabelMap.ProductCatalogs}</a>
	  <a href="<@ofbizUrl>/EditProductStoreWebSites?viewProductStoreId=${productStoreId}</@ofbizUrl>" class="${selectedClassMap.EditProductStoreWebSites?default(unselectedClassName)}">${uiLabelMap.ProductWebSites}</a>
-->	  <a href="<@ofbizUrl>/EditProductStoreTaxSetup</@ofbizUrl>" class="${selectedClassMap.EditProductStoreTaxSetup?default(unselectedClassName)}">${uiLabelMap.ProductSalesTax}</a>
	  <!--a href="<@ofbizUrl>/EditProductStoreShipSetup?viewProductStoreId=${productStoreId}</@ofbizUrl>" class="${selectedClassMap.EditProductStoreShipSetup?default(unselectedClassName)}">${uiLabelMap.OrderShipping}</a>
-->	  <a href="<@ofbizUrl>/EditProductStorePaySetup</@ofbizUrl>" class="${selectedClassMap.EditProductStorePaySetup?default(unselectedClassName)}">${uiLabelMap.AccountingPayments}</a>
	  <a href="<@ofbizUrl>/EditProductStoreEmails</@ofbizUrl>" class="${selectedClassMap.EditProductStoreEmails?default(unselectedClassName)}">${uiLabelMap.CommonEmails}</a>
	  <!--a href="<@ofbizUrl>/EditProductStoreSurveys</@ofbizUrl>" class="${selectedClassMap.EditProductStoreSurveys?default(unselectedClassName)}">Surveys</a>
	  <a href="<@ofbizUrl>/editProductStoreKeywordOvrd</@ofbizUrl>" class="${selectedClassMap.EditProductStoreKeywordOvrd?default(unselectedClassName)}">${uiLabelMap.ProductOverride}</a>
-->    </div>
  </#if>  
