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
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *@version    $Rev:$
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<#if hasPermission>
<script language="JavaScript">
<!-- //
function lookupBom() {
    document.searchbom.productId.value=document.editProductAssocForm.productId.value;
    document.searchbom.productAssocTypeId.value=document.editProductAssocForm.productAssocTypeId.options[document.editProductAssocForm.productAssocTypeId.selectedIndex].value;
    document.searchbom.submit();
}
// -->
</script>

${pages.get("/bom/BomTabBar.ftl")}

    <div class="head1">${uiLabelMap.ManufacturingBillOfMaterials} <span class="head2"> <#if product?exists>${(product.internalName)?if_exists}</#if>[ID:${productId?if_exists}]</span></div>
    <#if productId?has_content>
        <a href="<@ofbizUrl>/findBom</@ofbizUrl>?productId=${productId}&partBomTypeId=${productAssocTypeId}" class="buttontext">[${uiLabelMap.ManufacturingBillOfMaterials}]</a>
    </#if>

    <br>
    <br>

    <form name="searchform" action="<@ofbizUrl>/UpdateProductBom</@ofbizUrl>" method=PUT >
    <input type=hidden name="UPDATE_MODE" value="">
    <table border="0" cellpadding="2" cellspacing="0">
        <tr>
            <td align=right><div class='tableheadtext'>${uiLabelMap.ManufacturingPartBOMType}:</div></td>
            <td>&nbsp;</td>
            <td>
            <select class="selectBox" name="productAssocTypeId" size=1>
            <#if productAssocTypeId?has_content>
                <#assign curAssocType = delegator.findByPrimaryKey("ProductAssocType", Static["org.ofbiz.base.util.UtilMisc"].toMap("productAssocTypeId", productAssocTypeId))>
                <#if curAssocType?exists>
                    <option selected value="${(curAssocType.productAssocTypeId)?if_exists}">${(curAssocType.description)?if_exists}</option>
                    <option value="${(curAssocType.productAssocTypeId)?if_exists}"></option>
                </#if>
            </#if>
            <#list assocTypes as assocType>
                <option value="${(assocType.productAssocTypeId)?if_exists}">${(assocType.description)?if_exists}</option>
            </#list>
            </select>
            </td>
            <td align=right><div class='tableheadtext'>${uiLabelMap.ProductProductId}:</div></td>
            <td>&nbsp;</td>
            <td>
            <input type="text" class="inputBox" name="productId" size="20" maxlength="40" value="${productId?if_exists}">
            <a href="javascript:call_fieldlookup(document.searchform.productId,'<@ofbizUrl>/LookupProduct</@ofbizUrl>', 'none',640,460);"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>
            <span class='tabletext'><a href="javascript:document.searchform.submit();" class="buttontext">${uiLabelMap.ManufacturingShowBOMAssocs}</a></span>
            </td>
        </tr>
        <tr>
            <td colspan="3">&nbsp;</td>
            <td align=right><div class='tableheadtext'>${uiLabelMap.ManufacturingCopyToProductId}:</div></td>
            <td>&nbsp;</td>
            <td>
            <input type="text" class="inputBox" name="copyToProductId" size="20" maxlength="40" value="">
            <a href="javascript:call_fieldlookup(document.searchform.copyToProductId,'<@ofbizUrl>/LookupProduct</@ofbizUrl>', 'none',640,460);"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>
            <span class='tabletext'><a href="javascript:document.searchform.UPDATE_MODE.value='COPY';document.searchform.submit();" class="buttontext">${uiLabelMap.ManufacturingCopyBOMAssocs}</a></span>
            </td>
        </tr>
    </table>
    </form>

    <hr class="sepbar">

    
    <form action="<@ofbizUrl>/UpdateProductBom</@ofbizUrl>" method=POST style="margin: 0;" name="editProductAssocForm">
    <table border="0" cellpadding="2" cellspacing="0">
    
    <#if !(productAssoc?exists)>
            <input type=hidden name="UPDATE_MODE" value="CREATE">
            <tr>
            <td align=right><div class='tableheadtext'>${uiLabelMap.ManufacturingPartBOMType}:</div></td>
            <td>&nbsp;</td>
            <td>
                <select class="selectBox" name="productAssocTypeId" size=1>
                <#if productAssocTypeId?has_content>
                    <#assign curAssocType = delegator.findByPrimaryKey("ProductAssocType", Static["org.ofbiz.base.util.UtilMisc"].toMap("productAssocTypeId", productAssocTypeId))>
                    <#if curAssocType?exists>
                        <option selected value="${(curAssocType.productAssocTypeId)?if_exists}">${(curAssocType.description)?if_exists}</option>
                        <option value="${(curAssocType.productAssocTypeId)?if_exists}"></option>
                    </#if>
                </#if>
                <#list assocTypes as assocType>
                    <option value="${(assocType.productAssocTypeId)?if_exists}">${(assocType.description)?if_exists}</option>
                </#list>
                </select>
            </td>
            </tr>
            <tr>
            <td align=right><div class='tableheadtext'>${uiLabelMap.ProductProductId}:</div></td>
            <td>&nbsp;</td>
            <td>
                <input type="text" class="inputBox" name="productId" size="20" maxlength="40" value="${productId?if_exists}">
                <a href="javascript:call_fieldlookup(document.editProductAssocForm.productId,'<@ofbizUrl>/LookupProduct</@ofbizUrl>', 'none',640,460);"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>
                
            </td>
            </tr>
            <tr>
            <td align=right><div class='tableheadtext'>${uiLabelMap.ManufacturingProductIdTo}:</div></td>
            <td>&nbsp;</td>
            <td>
                <input type="text" class="inputBox" name="productIdTo" size="20" maxlength="40" value="${productIdTo?if_exists}">
                <a href="javascript:call_fieldlookup(document.editProductAssocForm.productIdTo,'<@ofbizUrl>/LookupProduct</@ofbizUrl>', 'none',640,460);"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>
            </td>
            </tr>
            <tr>
            <td align=right><div class='tableheadtext'>${uiLabelMap.CommonFromDate}:</div></td>
            <td>&nbsp;</td>
            <td>
                <div class="tabletext">
                    <input type="text" class="inputBox" name="fromDate" size="25" maxlength="40" value="">
                    <a href="javascript:call_cal(document.editProductAssocForm.fromDate, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                    (Will be set to now if empty)
                </div>
            </td>
            </tr>
    <#else>
        <#assign curProductAssocType = productAssoc.getRelatedOneCache("ProductAssocType")>
        <input type=hidden name="UPDATE_MODE" value="UPDATE">
        <input type=hidden name="productId" value="${productId?if_exists}">
        <input type=hidden name="productIdTo" value="${productIdTo?if_exists}">
        <input type=hidden name="productAssocTypeId" value="${productAssocTypeId?if_exists}">
        <input type=hidden name="fromDate" value="${fromDate?if_exists}">
        <tr>
            <td align=right><div class='tableheadtext'>${uiLabelMap.ProductProductId}:</div></td>
            <td>&nbsp;</td>
            <td><b>${productId?if_exists}</b></td>
        </tr>
        <tr>
            <td align=right><div class='tableheadtext'>${uiLabelMap.ManufacturingProductIdTo}:</div></td>
            <td>&nbsp;</td>
            <td><b>${productIdTo?if_exists}</b></td>
        </tr>
        <tr>
            <td align=right><div class='tableheadtext'>${uiLabelMap.ManufacturingPartBOMType}:</div></td>
            <td>&nbsp;</td>
            <td><b><#if curProductAssocType?exists>${(curProductAssocType.description)?if_exists}<#else> ${productAssocTypeId?if_exists}</#if></b></td>
        </tr>
        <tr>
            <td align=right><div class='tableheadtext'>${uiLabelMap.CommonFromDate}:</div></td>
            <td>&nbsp;</td>
            <td><b>${fromDate?if_exists}</b></td>
        </tr>
    </#if>
    <tr>
        <td width="26%" align=right><div class='tableheadtext'>${uiLabelMap.CommonThruDate}:</div></td>
        <td>&nbsp;</td>
        <td width="74%">
        <div class="tabletext">
            <input type="text" class="inputBox" name="thruDate" <#if useValues> value="${productAssoc.thruDate?if_exists}"<#else>value="${(request.getParameter("thruDate"))?if_exists}"</#if> size="30" maxlength="30"> 
            <a href="javascript:call_cal(document.editProductAssocForm.thruDate, <#if useValues>'${productAssoc.thruDate?if_exists}'<#elseif (request.getParameter("thruDate"))?exists>'${request.getParameter("thruDate")}'<#else>'${nowTimestampString}'</#if>);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
        </div>
        </td>
    </tr>
    <tr>
        <td width="26%" align=right><div class='tableheadtext'>${uiLabelMap.CommonSequenceNum}:</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="sequenceNum" <#if useValues>value="${(productAssoc.sequenceNum)?if_exists}"<#else>value="${(request.getParameter("sequenceNum"))?if_exists}"</#if> size="5" maxlength="10"></td>
    </tr>
    <tr>
        <td width="26%" align=right><div class="tabletext"><div class='tableheadtext'>${uiLabelMap.ManufacturingReason}:</div></div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="reason" <#if useValues>value="${(productAssoc.reason)?if_exists}"<#else>value="${(request.getParameter("reason"))?if_exists}"</#if> size="60" maxlength="255"></td>
    </tr>
    <tr>
        <td width="26%" align=right><div class='tableheadtext'>${uiLabelMap.ManufacturingInstruction}:</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="instruction" <#if useValues>value="${(productAssoc.instruction)?if_exists}"<#else>value="${(request.getParameter("instruction"))?if_exists}"</#if> size="60" maxlength="255"></td>
    </tr>
    
    <tr>
        <td width="26%" align=right><div class='tableheadtext'>${uiLabelMap.ManufacturingQuantity}:</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="quantity" <#if useValues>value="${(productAssoc.quantity)?if_exists}"<#else>value="${(request.getParameter("quantity"))?if_exists}"</#if> size="10" maxlength="15"></td>
    </tr>

    <tr>
        <td width="26%" align=right><div class='tableheadtext'>${uiLabelMap.ManufacturingScrapFactor}:</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="scrapFactor" <#if useValues>value="${(productAssoc.scrapFactor)?if_exists}"<#else>value="${(request.getParameter("scrapFactor"))?if_exists}"</#if> size="10" maxlength="15"></td>
    </tr>
    
    <tr>
        <td colspan="2">&nbsp;</td>
        <td align=left><input type="submit" class="SmallSubmit" <#if !(productAssoc?exists)>value="${uiLabelMap.CommonAdd}"<#else>value="${uiLabelMap.CommonEdit}"</#if>></td>
    </tr>
    </table>
    </form>
    <br>
    <#if productId?exists && product?exists>
        <hr class="sepbar">
        <div class="head2">${uiLabelMap.ManufacturingProductComponents}</div>
        
        <table border="1" cellpadding="2" cellspacing="0">
            <tr>
            <td><div class="tabletext"><b>${uiLabelMap.ManufacturingPartId}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ManufacturingPartName}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonFromDate}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonThruDate}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonSequenceNum}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ManufacturingQuantity}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ManufacturingScrapFactor}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ManufacturingPartBOMType}</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
            </tr>
            <#list assocFromProducts as assocFromProduct>
            <#assign listToProduct = assocFromProduct.getRelatedOneCache("AssocProduct")>
            <#assign curProductAssocType = assocFromProduct.getRelatedOneCache("ProductAssocType")>
            <tr valign="middle">
                <td><a href="<@ofbizUrl>/EditProductBom?productId=${(assocFromProduct.productIdTo)?if_exists}&productAssocTypeId=${(assocFromProduct.productAssocTypeId)?if_exists}</@ofbizUrl>" class="buttontext">${(assocFromProduct.productIdTo)?if_exists}</a></td>
                <td><#if listToProduct?exists><a href="<@ofbizUrl>/EditProductBom?productId=${(assocFromProduct.productIdTo)?if_exists}&productAssocTypeId=${(assocFromProduct.productAssocTypeId)?if_exists}</@ofbizUrl>" class="buttontext">${(listToProduct.internalName)?if_exists}</a></#if>&nbsp;</td>
                <td><div class="tabletext" <#if (assocFromProduct.getTimestamp("fromDate"))?exists && nowDate.before(assocFromProduct.getTimestamp("fromDate"))> style="color: red;"</#if>>
                ${(assocFromProduct.fromDate)?if_exists}&nbsp;</div></td>
                <td><div class="tabletext" <#if (assocFromProduct.getTimestamp("thruDate"))?exists && nowDate.after(assocFromProduct.getTimestamp("thruDate"))> style="color: red;"</#if>>
                ${(assocFromProduct.thruDate)?if_exists}&nbsp;</div></td>
                <td><div class="tabletext">&nbsp;${(assocFromProduct.sequenceNum)?if_exists}</div></td>
                <td><div class="tabletext">&nbsp;${(assocFromProduct.quantity)?if_exists}</div></td>
                <td><div class="tabletext">&nbsp;${(assocFromProduct.scrapFactor)?if_exists}</div></td>
                <td><div class="tabletext"><#if curProductAssocType?exists> ${(curProductAssocType.description)?if_exists}<#else>${(assocFromProduct.productAssocTypeId)?if_exists}</#if></div></td>
                <td>
                <a href="<@ofbizUrl>/UpdateProductBom?UPDATE_MODE=DELETE&productId=${productId}&productIdTo=${(assocFromProduct.productIdTo)?if_exists}&productAssocTypeId=${(assocFromProduct.productAssocTypeId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(assocFromProduct.getTimestamp("fromDate").toString())}&useValues=true</@ofbizUrl>" class="buttontext">
                [${uiLabelMap.CommonDelete}]</a>
                </td>
                <td>
                <a href="<@ofbizUrl>/EditProductBom?productId=${productId}&productIdTo=${(assocFromProduct.productIdTo)?if_exists}&productAssocTypeId=${(assocFromProduct.productAssocTypeId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(assocFromProduct.getTimestamp("fromDate").toString())}&useValues=true</@ofbizUrl>" class="buttontext">
                [${uiLabelMap.CommonEdit}]</a>
                </td>
            </tr>
            </#list>
        </table>
        
        <hr class="sepbar">
        <div class="head2">${uiLabelMap.ManufacturingProductComponentOf}</div>
        <table border="1" cellpadding="2" cellspacing="0">
            <tr>
            <td><div class="tabletext"><b>${uiLabelMap.ManufacturingPartId}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ManufacturingPartName}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonFromDate}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.CommonThruDate}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ManufacturingQuantity}</b></div></td>
            <td><div class="tabletext"><b>${uiLabelMap.ManufacturingPartBOMType}</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
            </tr>
            <#list assocToProducts as assocToProduct>
            <#assign listToProduct = assocToProduct.getRelatedOneCache("MainProduct")>
            <#assign curProductAssocType = assocToProduct.getRelatedOneCache("ProductAssocType")>
            <tr valign="middle">
                <td><a href="<@ofbizUrl>/EditProductBom?productId=${(assocToProduct.productId)?if_exists}&productAssocTypeId=${(assocToProduct.productAssocTypeId)?if_exists}</@ofbizUrl>" class="buttontext">${(assocToProduct.productId)?if_exists}</a></td>
<!--                <td><#if listToProduct?exists><a href="<@ofbizUrl>/EditProduct?productId=${(assocToProduct.productId)?if_exists}</@ofbizUrl>" class="buttontext">${(listToProduct.internalName)?if_exists}</a></#if></td> -->
                <td><#if listToProduct?exists><a href="<@ofbizUrl>/EditProductBom?productId=${(assocToProduct.productId)?if_exists}&productAssocTypeId=${(assocToProduct.productAssocTypeId)?if_exists}</@ofbizUrl>" class="buttontext">${(listToProduct.internalName)?if_exists}</a></#if></td>
                <td><div class="tabletext">${(assocToProduct.getTimestamp("fromDate"))?if_exists}&nbsp;</div></td>
                <td><div class="tabletext">${(assocToProduct.getTimestamp("thruDate"))?if_exists}&nbsp;</div></td>
                <td><div class="tabletext">${(assocToProduct.quantity)?if_exists}&nbsp;</div></td>
                <td><div class="tabletext"><#if curProductAssocType?exists> ${(curProductAssocType.description)?if_exists}<#else> ${(assocToProduct.productAssocTypeId)?if_exists}</#if></div></td>
                <td>
                <a href="<@ofbizUrl>/UpdateProductBom?UPDATE_MODE=DELETE&productId=${(assocToProduct.productId)?if_exists}&productIdTo=${(assocToProduct.productIdTo)?if_exists}&productAssocTypeId=${(assocToProduct.productAssocTypeId)?if_exists}&fromDate=${Static["org.ofbiz.base.util.UtilFormatOut"].encodeQueryValue(assocToProduct.getTimestamp("fromDate").toString())}&useValues=true</@ofbizUrl>" class="buttontext">
                [${uiLabelMap.CommonDelete}]</a>
                </td>
            </tr>
            </#list>
        </table>

        <br>
        <div class="tabletext">NOTE: <b style="color: red;">Red</b> date/time entries denote that the current time is before the From Date or after the Thru Date. If the From Date is <b style="color: red;">red</b>, association has not started yet; if Thru Date is <b style="color: red;">red</b>, association has expired (<u>and should probably be deleted</u>).</div>
    </#if>
<#else>
  <h3>${uiLabelMap.ManufacturingViewPermissionError}</h3>
</#if>
