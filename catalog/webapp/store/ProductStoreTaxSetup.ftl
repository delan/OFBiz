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
      <a href="<@ofbizUrl>/ProductStoreWebSites?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButton">WebSites</a>
      <a href="<@ofbizUrl>/ProductStoreTaxSetup?productStoreId=${productStoreId}</@ofbizUrl>" class="tabButtonSelected">Sales Tax</a>
    </div>
  </#if>
  <div class="head1">Product Store WebSites <span class='head2'><#if (productStore.storeName)?has_content>"${productStore.storeName}"</#if> [ID:${productStoreId?if_exists}]</span></div>
  <a href="<@ofbizUrl>/EditProductStore</@ofbizUrl>" class="buttontext">[New Product Store]</a>
  <br>
  <br>   
  
  <table border="1" cellpadding="2" cellspacing="0" width="100%">
    <tr>
      <td nowrap><div class="tableheadtext">Country</td>
      <td nowrap><div class="tableheadtext">State</div></td>
      <td nowrap><div class="tableheadtext">Tax Category</div></td>
      <td nowrap><div class="tableheadtext">Min Purchase</div></td>
      <td nowrap><div class="tableheadtext">Tax Rate</div></td>
      <td nowrap><div class="tableheadtext">From Date</div></td>             
      <td nowrap><div class="tabletext">&nbsp;</div></td>
    </tr>
    <#list taxItems as taxItem>      
      <tr>                  
        <td><div class="tabletext">${taxItem.countryGeoId}</div></td>
        <td><div class="tabletext">${taxItem.stateProvinceGeoId}</div></td>
        <td><div class="tabletext">${taxItem.taxCategory}</div></td>
        <td><div class="tabletext">${taxItem.minPurchase?string("##0.00")}</div></td>
        <td><div class="tabletext">${taxItem.salesTaxPercentage?if_exists}</div></td>
        <td><div class="tabletext">${taxItem.fromDate?string}</div></td>
        <#if security.hasEntityPermission("TAXRATE", "_DELETE", session)>
          <td><div class="tabletext"><a href="<@ofbizUrl>/storeRemoveTaxRate?productStoreId=${productStoreId}&countryGeoId=${taxItem.countryGeoId}&stateProvinceGeoId=${taxItem.stateProvinceGeoId}&taxCategory=${taxItem.taxCategory}&minPurchase=${taxItem.minPurchase?string.number}&fromDate=${taxItem.get("fromDate").toString()}</@ofbizUrl>" class="buttontext">[Remove]</a></div></td>
        <#else>
          <td>&nbsp;</td>
        </#if>
      </tr>
    </#list>
  </table>
  
  <br>
  <table>
    <#if security.hasEntityPermission("TAXRATE", "_CREATE", session)>
      <form name="addrate" action="<@ofbizUrl>/storeCreateTaxRate</@ofbizUrl>">
        <input type="hidden" name="productStoreId" value="${productStoreId}">
        <tr>
          <td><span class="tableheadtext">Country</span></td>
          <td>
            <select name="countryGeoId" class="selectBox">
              <option value="_NA_">All</option>
              ${pages.get("/includes/countries.ftl")}
            </select>
          </td>
        </tr>
        <tr>
          <td><span class="tableheadtext">State/Province</span></td>          	
          <td>
            <select name="stateProvinceGeoId" class="selectBox">
              <option value="_NA_">All</option>
              ${pages.get("/includes/states.ftl")}
            </select>
          </td>
        </tr>
        <tr>
          <td><span class="tableheadtext">Tax Category</span></td>      
          <td><input type="text" size="20" name="taxCategory" class="inputBox"></td>      
        </tr>
        <tr>
          <td><span class="tableheadtext">Minimum Purchase</span></td>
          <td><input type="text" size="10" name="minPurchase" class="inputBox" value="0.00"></td>      
        </tr>
        <tr>
          <td><span class="tableheadtext">Tax Shipping?</span></td>
          <td>
            <select name="taxShipping" class="selectBox">
              <option value="N">No</option>
              <option value="Y">Yes</option>
            </select>
          </td>
        </tr>
        <tr>
          <td><span class="tableheadtext">Description</span></td>
          <td><input type="text" size="20" name="description" class="inputBox"></td>
        </tr>
        <tr>
          <td><span class="tableheadtext">Tax Rate</span></td>
          <td><input type="text" size="10" name="salesTaxPercentage" class="inputBox"></td>
        </tr>
        <tr>
          <td><span class="tableheadtext">From Date</span></td>
          <td><input type="text" name="fromDate" class="inputBox"><a href="javascript:call_cal(document.addrate.fromDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a></td>
        </tr>
       <tr>
          <td><span class="tableheadtext">Thru Date</span></td>
          <td><input type="text" name="thruDate" class="inputBox"><a href="javascript:call_cal(document.addrate.thruDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a></td>
        </tr>        
        <tr>
          <td><input type="submit" class="smallSubmit" value="Add"></td>
        </tr>
      </form>
    </#if>
  </table>  
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>

