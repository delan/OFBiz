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
 *@since      2.2
-->

<#if hasPermission>
  <#if productStoreId?has_content>
    <div class='tabContainer'>
      <a href="<@ofbizUrl>/EditProductStore?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Store</a>
      <a href="<@ofbizUrl>/EditProductStorePromos?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Promos</a>
      <a href="<@ofbizUrl>/EditProductStoreCatalogs?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Catalogs</a>
      <a href="<@ofbizUrl>/EditProductStoreWebSites?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">WebSites</a>
      <a href="<@ofbizUrl>/EditProductStoreTaxSetup?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Sales Tax</a>
      <a href="<@ofbizUrl>/EditProductStoreShipSetup?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">Shipping</a>
      <a href="<@ofbizUrl>/EditProductStorePaySetup?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButtonSelected">Payments</a>
    </div>
  </#if>
  <div class="head1">Product Store Payment Settings <span class='head2'><#if (productStore.storeName)?has_content>"${productStore.storeName}"</#if> [ID:${productStoreId?if_exists}]</span></div>
  <a href="<@ofbizUrl>/EditProductStore</@ofbizUrl>" class="buttontext">[New Product Store]</a>
  <br>
  <br>   
  
  <table border="1" cellpadding="2" cellspacing="0" width="100%">
    <tr>
      <td nowrap><div class="tableheadtext">Payment Method</td>
      <td nowrap><div class="tableheadtext">Service Type</div></td>
      <td nowrap><div class="tableheadtext">Service Name</div></td>
      <td nowrap><div class="tableheadtext">Payment Props</div></td>         
      <td nowrap><div class="tabletext">&nbsp;</div></td>
    </tr>
    <#list paymentSettings as setting>
      <#assign payMeth = setting.getRelatedOne("PaymentMethodType")>
      <#assign enum = setting.getRelatedOne("Enumeration")>      
      <tr>                  
        <td><div class="tabletext">${payMeth.description}</div></td>
        <td><div class="tabletext">${enum.description}</div></td>
        <td><div class="tabletext">${setting.paymentService}</div></td>
        <td><div class="tabletext">${setting.paymentPropertiesPath?default("[global]")}</div></td>
        <td>
          <div class="tabletext"><#if security.hasEntityPermission("CATALOG", "_DELETE", session)><a href="<@ofbizUrl>/storeRemovePaySetting?productStoreId=${productStoreId}&paymentMethodTypeId=${setting.paymentMethodTypeId}&paymentServiceTypeEnumId=${setting.paymentServiceTypeEnumId}</@ofbizUrl>" class="buttontext">[Remove]</a></#if> <a href="<@ofbizUrl>/EditProductStorePaySetup?productStoreId=${productStoreId}&paymentMethodTypeId=${setting.paymentMethodTypeId}&paymentServiceTypeEnumId=${setting.paymentServiceTypeEnumId}</@ofbizUrl>" class="buttontext">[Edit]</a></div>
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
          <td><span class="tableheadtext">Payment Method Type</span></td>
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
          <td><span class="tableheadtext">Service Type</span></td>          	
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
          <td><span class="tableheadtext">Service Name</span></td>      
          <td><input type="text" size="30" name="paymentService" class="inputBox" value="${(editSetting.paymentService)?if_exists}"></td>      
        </tr>
        <tr>
          <td><span class="tableheadtext">Payment Properties</span></td>
          <td><input type="text" size="30" name="paymentPropertiesPath" class="inputBox" value="${(editSetting.paymentPropertiesPath)?if_exists}"></td>      
        </tr>               
        <tr>
          <td><input type="submit" class="smallSubmit" value="${buttonText}"></td>
        </tr>
      </form>
    </#if>
  </table>  
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>

