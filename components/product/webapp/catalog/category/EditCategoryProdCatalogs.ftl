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
 *@author     Catherine Heintz (catherine.heintz@nereide.biz)
 *@version    $Revision: 1.8 $
 *@since      2.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if hasPermission>
${pages.get("/category/CategoryTabBar.ftl")}
    
    <div class="head1">${uiLabelMap.ProductCatalogs} <span class="head2">${uiLabelMap.CommonFor} <#if productCategory?exists>${(productCategory.description)?if_exists} </#if>[${uiLabelMap.CommonId}:${productCategoryId?if_exists}]</span></div>
   
    <a href="<@ofbizUrl>/EditCategory</@ofbizUrl>" class="buttontext">[${uiLabelMap.ProductNewCategory}]</a>
    <#if productCategoryId?has_content>
        <a href="/ecommerce/control/category?category_id=${productCategoryId}" class="buttontext" target="_blank">[${uiLabelMap.ProductCategoryPage}]</a>
    </#if>
    <br>
    <br>
    <#if productCategoryId?exists && productCategory?exists>    
        <table border="1" width="100%" cellpadding="2" cellspacing="0">
        <tr>
            <td><div class="tabletext"><b>${uiLabelMap.ProductCatalogNameId}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonFromDateTime}</b></div></td>
            <td align="center"><div class="tabletext"><b>${uiLabelMap.ProductThruDateTimeSequenceType}</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
        </tr>
        <#assign line = 0>
        <#list prodCatalogCategories as prodCatalogCategory>
        <#assign line = line + 1>
        <#assign prodCatalog = prodCatalogCategory.getRelatedOne("ProdCatalog")>
        <#assign curProdCatalogCategoryType = prodCatalogCategory.getRelatedOneCache("ProdCatalogCategoryType")>
        <tr valign="middle">
            <td><a href="<@ofbizUrl>/EditProdCatalog?prodCatalogId=${(prodCatalogCategory.prodCatalogId)?if_exists}</@ofbizUrl>" class="buttontext"><#if prodCatalog?exists>${(prodCatalog.catalogName)?if_exists}</#if> [${(prodCatalogCategory.prodCatalogId)?if_exists}]</a></td>
            <#assign hasntStarted = false>
            <#if (prodCatalogCategory.getTimestamp("fromDate"))?exists && nowTimestamp.before(prodCatalogCategory.getTimestamp("fromDate"))> <#assign hasntStarted = true></#if>
            <td><div class="tabletext"<#if hasntStarted> style="color: red;"</#if>>${(prodCatalogCategory.fromDate)?if_exists}</div></td>
            <td align="center">
                <FORM method=POST action="<@ofbizUrl>/category_updateProductCategoryToProdCatalog</@ofbizUrl>" name="lineForm${line}">
                    <#assign hasExpired = false>
                    <#if (prodCatalogCategory.getTimestamp("thruDate"))?exists && nowTimestamp.after(prodCatalogCategory.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                    <input type=hidden name="prodCatalogId" value="${(prodCatalogCategory.prodCatalogId)?if_exists}">
                    <input type=hidden name="productCategoryId" value="${(prodCatalogCategory.productCategoryId)?if_exists}">
                    <input type=hidden name="fromDate" value="${(prodCatalogCategory.fromDate)?if_exists}">
                    <input type=text size="25" name="thruDate" value="${(prodCatalogCategory.thruDate)?if_exists}" class="inputBox" style="<#if (hasExpired) >color: red;</#if>">
                    <a href="javascript:call_cal(document.lineForm${line}.thruDate, '${(prodCatalogCategory.thruDate)?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                    <input type=text size="5" name="sequenceNum" value="${(prodCatalogCategory.sequenceNum)?if_exists}" class="inputBox">
                    <select name="prodCatalogCategoryTypeId" size=1 class="selectBox">
                        <#if (prodCatalogCategory.prodCatalogCategoryTypeId)?exists>
                            <option value="${prodCatalogCategory.prodCatalogCategoryTypeId}"><#if curProdCatalogCategoryType?exists>${(curProdCatalogCategoryType.description)?if_exists}<#else> [${(prodCatalogCategory.prodCatalogCategoryTypeId)}]</#if></option>
                            <option value="${prodCatalogCategory.prodCatalogCategoryTypeId}"></option>
                        <#else>
                            <option value="">&nbsp;</option>
                        </#if>
                        <#list prodCatalogCategoryTypes as prodCatalogCategoryType>
                        <option value="${(prodCatalogCategoryType.prodCatalogCategoryTypeId)?if_exists}">${(prodCatalogCategoryType.description)?if_exists}</option>
                        </#list>
                    </select>
                    <INPUT type=submit value="${uiLabelMap.CommonUpdate}" style="font-size: x-small;">
                </FORM>
            </td>
            <td align="center">
            <a href="<@ofbizUrl>/category_removeProductCategoryFromProdCatalog?prodCatalogId=${(prodCatalogCategory.prodCatalogId)?if_exists}&productCategoryId=${(prodCatalogCategory.productCategoryId)?if_exists}&prodCatalogCategoryTypeId=${(prodCatalogCategory.prodCatalogCategoryTypeId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(prodCatalogCategory.getTimestamp("fromDate").toString())}</@ofbizUrl>" class="buttontext">
            [${uiLabelMap.CommonDelete}]</a>
            </td>
        </tr>
        </#list>
        </table>
        <br>
        <form method="POST" action="<@ofbizUrl>/category_addProductCategoryToProdCatalog</@ofbizUrl>" style="margin: 0;" name="addNewForm">
        <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}">
        
        <div class="head2">${uiLabelMap.ProductAddCatalogProductCategory}:</div>
        <br>
        <select name="prodCatalogId" class="selectBox">
        <#list prodCatalogs as prodCatalog>
            <option value="${(prodCatalog.prodCatalogId)?if_exists}">${(prodCatalog.catalogName)?if_exists} [${(prodCatalog.prodCatalogId)?if_exists}]</option>
        </#list>
        </select>
            <select name="prodCatalogCategoryTypeId" size=1 class="selectBox">
                <#list prodCatalogCategoryTypes as prodCatalogCategoryType>
                <option value="${(prodCatalogCategoryType.prodCatalogCategoryTypeId)?if_exists}">${(prodCatalogCategoryType.description)?if_exists}</option>
                </#list>
            </select>
        <input type=text size="25" name="fromDate" class="inputBox">
        <a href="javascript:call_cal(document.addNewForm.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
        <input type="submit" value="${uiLabelMap.CommonAdd}">
        </form>
    </#if>
<#else>
  <h3>${uiLabelMap.ProductCatalogViewPermissionError}</h3>
</#if>