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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Brad Steiner (bsteiner@thehungersite.com)
 *@version    $Revision: 1.2 $
 *@since      2.2
-->

<#if hasPermission>
    <#if prodCatalogId?has_content>
        <div class="tabContainer">
        <a href="<@ofbizUrl>/EditProdCatalog?prodCatalogId=${prodCatalogId}</@ofbizUrl>" class="tabButton">Catalog</a>
        <a href="<@ofbizUrl>/EditProdCatalogStores?prodCatalogId=${prodCatalogId}</@ofbizUrl>" class="tabButton">Stores</a>
        <a href="<@ofbizUrl>/EditProdCatalogParties?prodCatalogId=${prodCatalogId}</@ofbizUrl>" class="tabButton">Parties</a>
        <a href="<@ofbizUrl>/EditProdCatalogCategories?prodCatalogId=${prodCatalogId}</@ofbizUrl>" class="tabButtonSelected">Categories</a>
        </div>
    </#if>
    <div class="head1">Categories <span class="head2">for <#if prodCatalogId?has_content>${prodCatalogId}</#if>
    </span></div>
    
    <a href="<@ofbizUrl>/EditProdCatalog</@ofbizUrl>" class="buttontext">[New ProdCatalog]</a>
    <p>
    <#if prodCatalogId?exists && prodCatalog?exists>    
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>Category [ID]</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td align="center"><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time,&nbsp;Sequence&nbsp;&amp;&nbsp;Type</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line=0>
        <#list prodCatalogCategories as prodCatalogCategory>
        <#assign productCategory = prodCatalogCategory.getRelatedOne("ProductCategory")>
        <#assign curProdCatalogCategoryType = prodCatalogCategory.getRelatedOneCache("ProdCatalogCategoryType")>
        
        <tr valign="middle">
            <td><a href="<@ofbizUrl>/EditCategory?productCategoryId=${prodCatalogCategory.productCategoryId}</@ofbizUrl>" class="buttontext">
            <div class="buttontext"><#if productCategory?exists>${productCategory.description?if_exists}</#if>&nbsp;[${prodCatalogCategory.productCategoryId}]</div>
            </td>
            <#assign hasntStarted = false>
            <#if prodCatalogCategory.getTimestamp("fromDate")?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().before(prodCatalogCategory.getTimestamp("fromDate"))>
                <#assign hasntStarted = true>
            </#if>
            <td><div class="tabletext" <#if hasntStarted == true> style="color: red;"</#if>>${prodCatalogCategory.fromDate}</div></td>
            <td align="center">
                <#assign hasExpired = false>
                <#if prodCatalogCategory.getTimestamp("thruDate")?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(prodCatalogCategory.getTimestamp("thruDate"))>
                    <#assign hasExpired = true>
                </#if>
                <FORM method=POST action="<@ofbizUrl>/updateProductCategoryToProdCatalog</@ofbizUrl>" name="lineForm${line}">
                    <input type="hidden" name="prodCatalogId" value="${prodCatalogCategory.prodCatalogId}">
                    <input type="hidden" name="productCategoryId" value="${prodCatalogCategory.productCategoryId}">
                    <input type="hidden" name="fromDate" value="${prodCatalogCategory.fromDate}">
                    <input type="text" name="thruDate" class="inputBox" size="25" <#if hasExpired == true> style="color: red;"</#if> value="<#if prodCatalogCategory.getTimestamp("thruDate")?exists> ${prodCatalogCategory.getTimestamp("thruDate")}</#if>">
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                    <input type="text" class="inputBox" size="5" name="sequenceNum" value="<#if prodCatalogCategory.sequenceNum?exists>${prodCatalogCategory.sequenceNum}</#if>">
                    <select class="selectBox" name="prodCatalogCategoryTypeId" size="1">
                        <#if prodCatalogCategory.get("prodCatalogCategoryTypeId")?exists>
                        <option value="${prodCatalogCategory.getString("prodCatalogCategoryTypeId")}">
                            <#if curProdCatalogCategoryType?exists && curProdCatalogCategoryType.getString("description")?exists>
                            	${curProdCatalogCategoryType.getString("description")}
                            <#else>
                            	${prodCatalogCategory.getString("prodCatalogCategoryTypeId")}
                            </#if>
                        </option>
                        <option value="${prodCatalogCategory.getString("prodCatalogCategoryTypeId")}">---</option>
                        <#else>
                        <option value="">&nbsp;</option>
                        </#if>
                        <#list prodCatalogCategoryTypes as prodCatalogCategoryType>
                        <option value="${prodCatalogCategoryType.getString("prodCatalogCategoryTypeId")}">${prodCatalogCategoryType.getString("description")}</option>
                        </list>
                    </select>
                    <INPUT type=submit value="Update">
                    <td align="center">
                        <a href="<@ofbizUrl>/removeProductCategoryFromProdCatalog?prodCatalogId=${prodCatalogCategory.prodCatalogId}&productCategoryId=${prodCatalogCategory.productCategoryId}&fromDate=${prodCatalogCategory.fromDate}</@ofbizUrl>" class="buttontext">[Delete]</a>
                    </td>
                    <td align="center">
                        <a href="<@ofbizUrl>/EditCategory?CATALOG_TOP_CATEGORY=${prodCatalogCategory.productCategoryId}&productCategoryId=${prodCatalogCategory.productCategoryId}</@ofbizUrl>" class="buttontext"> [MakeTop]</a>
                    </td>            
                </FORM>
            </td>
        </tr>
        <#assign line = line + 1>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/addProductCategoryToProdCatalog</@ofbizUrl>" style="margin: 0;" name="addNewForm">
        <input type="hidden" name="prodCatalogId" value="${prodCatalogId}">
        <input type="hidden" name="tryEntity" value="true">
        <div class="head2">Add Catalog Product Category (select Category and Type, then enter optional From Date):</div>
        <br>
        <select class="selectBox" name="productCategoryId">
        <#list productCategories as productCategory>
            <option value="${productCategory.productCategoryId}"> ${productCategory.description?if_exists}&nbsp;[${productCategory.productCategoryId}]</option>
        </#list>
        </select>
            <select class="selectBox" name="prodCatalogCategoryTypeId" size=1>
                <!-- <option value="">&nbsp;</option> -->
                <#list prodCatalogCategoryTypes as prodCatalogCategoryType>
                <option value="${prodCatalogCategoryType.prodCatalogCategoryTypeId}">${prodCatalogCategoryType.description?if_exists}</option>
                </#list>
            </select>
        <input type="text" class="inputBox" size="25" name="fromDate">
        <a href="javascript:call_cal(document.addNewForm.fromDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
        <input type="submit" value="Add">
        </form>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
