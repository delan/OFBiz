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

<div class='tabContainer'>
  <#if security.hasEntityPermission("SHIPRATE", "_VIEW", session)>
  <a href="<@ofbizUrl>/shipsetup</@ofbizUrl>" class='tabButton'>Ship&nbsp;Rate&nbsp;Setup</a>
  </#if>
  <#if security.hasEntityPermission("TAXRATE", "_VIEW", session)>
  <a href="<@ofbizUrl>/taxsetup</@ofbizUrl>" class='tabButtonSelected'>Tax&nbsp;Rate&nbsp;Setup</a>
  </#if>
  <#if security.hasEntityPermission("PAYPROC", "_VIEW", session)>
  <a href="<@ofbizUrl>/paysetup</@ofbizUrl>" class='tabButton'>Payment&nbsp;Setup</a>
  </#if>
</div>

<#if security.hasEntityPermission("TAXRATE", "_VIEW", session)>

<table border=0 width='100%' cellpadding='0' cellspacing=0 class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxtop'>
        <tr>
          <td align=left width='90%' >
            <div class='boxhead'>&nbsp;Tax Rate Editor</div>
          </td>
          <td align=right width='10%'>&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
        <tr>
          <td>
            <table width="100%" cellpadding="2" cellspacing="2" border="0">
              <tr class="viewOneTR1">
                <td nowrap><div class="tableheadtext">Country GeoId</td>
                <td nowrap><div class="tableheadtext">State GeoId</div></td>
                <td nowrap><div class="tableheadtext">Tax Category</div></td>
                <td nowrap><div class="tableheadtext">Min Purchase</div></td>
                <td nowrap><div class="tableheadtext">Tax Shipping</div></td>
                <td nowrap><div class="tableheadtext">Description</div></td>
                <td nowrap><div class="tableheadtext">Tax Rate</div></td>
                <td nowrap><div class="tableheadtext">From-Date</div></td>
                <!--<td nowrap><div class="tablehead">Thru-Date</div></td>-->
                <td nowrap><div class="tabletext">&nbsp;</div></td>
              </tr>
              <#list taxItems as taxItem>
                <#if rowStyle?exists && rowStyle == "viewManyTR1">
                  <#assign rowStyle = "viewManyTR2">
                <#else>
                  <#assign rowStyle = "viewManyTR1">
                </#if>
                <tr class="${rowStyle}">                  
                  <td><div class="tabletext">${taxItem.countryGeoId}</div></td>
                  <td><div class="tabletext">${taxItem.stateProvinceGeoId}</div></td>
                  <td><div class="tabletext">${taxItem.taxCategory}</div></td>
                  <td><div class="tabletext">${taxItem.minPurchase?string("##0.00")}</div></td>      
                  <td><div class="tabletext">${taxItem.taxShipping?if_exists}</div></td>
                  <td><div class="tabletext">${taxItem.description?if_exists}</div></td>
                  <td><div class="tabletext">${taxItem.salesTaxPercentage?if_exists}</div></td>
                  <td><div class="tabletext">${taxItem.get("fromDate").toString()}</div></td>
                  <#if security.hasEntityPermission("TAXRATE", "_DELETE", session)>
                    <td><div class="tabletext"><a href="<@ofbizUrl>/removetaxrate?countryGeoId=${taxItem.countryGeoId}&stateProvinceGeoId=${taxItem.stateProvinceGeoId}&taxCategory=${taxItem.taxCategory}&minPurchase=${taxItem.minPurchase?string.number}&fromDate=${taxItem.get("fromDate").toString()}</@ofbizUrl>" class="buttontext">[Remove]</a></div></td>
                  <#else>
                    <td>&nbsp;</td>
                  </#if>
                </tr>
              </#list>
              <#if security.hasEntityPermission("TAXRATE", "_CREATE", session)>
                <form name="addrate" action="<@ofbizUrl>/createtaxrate</@ofbizUrl>">
                  <tr bgcolor="#CCCCCC">
                    <td>
                      <select name="countryGeoId" class="selectBox">
                        <option value="_NA_">All</option>
                        ${pages.get("/includes/countries.ftl")}
                      </select>
                    </td>          	
                    <td>
                      <select name="stateProvinceGeoId" class="selectBox">
                        <option value="_NA_">All</option>
                        ${pages.get("/includes/states.ftl")}
                      </select>
                    </td>      
                    <td><input type="text" size="20" name="taxCategory" class="inputBox"></td>      
                    <td><input type="text" size="10" name="minPurchase" class="inputBox" value="0.00"></td>      
                    <td>
                      <select name="taxShipping" class="selectBox">
                        <option value="N">No</option>
                        <option value="Y">Yes</option>
                      </select>
                    </td>
                    <td><input type="text" size="20" name="description" class="inputBox"></td>
                    <td><input type="text" size="10" name="salesTaxPercentage" class="inputBox"></td>
                    <td><input type="text" name="fromDate" class="inputBox"></td>   
                    <td><div class="tabletext"><a href="javascript:document.addrate.submit();" class="buttontext">[Add]</a></div></td>
                  </tr>
                </form>
              </#if>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<#else>
  <br>
  <h3>You do not have permission to view this page. ("TAXRATE_VIEW" or "TAXRATE_ADMIN" needed)</h3>
</#if>

