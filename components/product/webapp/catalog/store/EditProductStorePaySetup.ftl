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
 *@version    $Rev:$
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>
  ${pages.get("/store/ProductStoreTabBar.ftl")}
  <div class="head1">${uiLabelMap.ProductProductStorePaymentSettings} <span class='head2'><#if (productStore.storeName)?has_content>"${productStore.storeName}"</#if> [${uiLabelMap.CommonId}:${productStoreId?if_exists}]</span></div>
  <a href="<@ofbizUrl>/EditProductStore</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewProductStore}]</a>
  <br>
  <br>   
  
  <table border="1" cellpadding="2" cellspacing="0" width="100%">
    <tr>
      <td nowrap><div class="tableheadtext">${uiLabelMap.AccountingPaymentMethod}</td>
      <td nowrap><div class="tableheadtext">${uiLabelMap.ProductServiceType}</div></td>
      <td nowrap><div class="tableheadtext">${uiLabelMap.ProductServiceName}</div></td>
      <td nowrap><div class="tableheadtext">${uiLabelMap.AccountingPaymentProps}</div></td>         
      <td nowrap><div class="tabletext">&nbsp;</div></td>
    </tr>
    <#list paymentSettings as setting>
      <#assign payMeth = setting.getRelatedOne("PaymentMethodType")>
      <#assign enum = setting.getRelatedOne("Enumeration")>      
      <tr>                  
        <td><div class="tabletext">${payMeth.description}</div></td>
        <td><div class="tabletext">${enum.description}</div></td>
        <td><div class="tabletext">${setting.paymentService?default("N/A")}</div></td>
        <td><div class="tabletext">${setting.paymentPropertiesPath?default("[global]")}</div></td>
        <td align="center" nowrap>
          <div class="tabletext"><#if security.hasEntityPermission("CATALOG", "_DELETE", session)><a href="<@ofbizUrl>/storeRemovePaySetting?productStoreId=${productStoreId}&paymentMethodTypeId=${setting.paymentMethodTypeId}&paymentServiceTypeEnumId=${setting.paymentServiceTypeEnumId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonDelete}]</a></#if> <a href="<@ofbizUrl>/EditProductStorePaySetup?productStoreId=${productStoreId}&paymentMethodTypeId=${setting.paymentMethodTypeId}&paymentServiceTypeEnumId=${setting.paymentServiceTypeEnumId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonEdit}]</a></div>
        </td>        
      </tr>
    </#list>
  </table>
  
  <br>
  <table>
    <#if security.hasEntityPermission("CATALOG", "_CREATE", session)>
      <#if editSetting?has_content>
        <#assign requestName = "/storeUpdatePaySetting">
        <#assign buttonText = "Update">
      <#else>
        <#assign requestName = "/storeCreatePaySetting">
        <#assign buttonText = "Create">
      </#if>
      <form method="get" name="addrate" action="<@ofbizUrl>${requestName}</@ofbizUrl>">
        <input type="hidden" name="productStoreId" value="${productStoreId}">
        <tr>
          <td><span class="tableheadtext">${uiLabelMap.AccountingPaymentMethodType}</span></td>
          <td>
            <select name="paymentMethodTypeId" class="selectBox"> 
              <#if editSetting?has_content>
                <#assign paymentMethodType = editSetting.getRelatedOne("PaymentMethodType")>
                <option value="${editSetting.paymentMethodTypeId}">${paymentMethodType.description}</option>
                <option value="${editSetting.paymentMethodTypeId}">---</option>
              </#if>
              <#list paymentMethodTypes as paymentMethodType>
                <option value="${paymentMethodType.paymentMethodTypeId}">${paymentMethodType.description}</option>
              </#list>
            </select>
          </td>
        </tr>
        <tr>
          <td><span class="tableheadtext">${uiLabelMap.ProductServiceType}</span></td>          	
          <td>
            <select name="paymentServiceTypeEnumId" class="selectBox"> 
              <#if editSetting?has_content>
                <#assign enum = editSetting.getRelatedOne("Enumeration")>
                <option value="${editSetting.paymentServiceTypeEnumId}">${enum.description}</option>
                <option value="${editSetting.paymentServiceTypeEnumId}">---</option>
              </#if>          
              <#list serviceTypes as type>
                <option value="${type.enumId}">${type.description}</option>
              </#list>
            </select>
          </td>
        </tr>
        <tr>
          <td><span class="tableheadtext">${uiLabelMap.ProductServiceName}</span></td>      
          <td><input type="text" size="30" name="paymentService" class="inputBox" value="${(editSetting.paymentService)?if_exists}"></td>      
        </tr>
        <tr>
          <td><span class="tableheadtext">${uiLabelMap.AccountingPaymentProperties}</span></td>
          <td><input type="text" size="30" name="paymentPropertiesPath" class="inputBox" value="${(editSetting.paymentPropertiesPath)?if_exists}"></td>      
        </tr>               
        <tr>
          <td><input type="submit" class="smallSubmit" value="${buttonText}"></td>
        </tr>
      </form>
    </#if>
  </table>  
<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>

