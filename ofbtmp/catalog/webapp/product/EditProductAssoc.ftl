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
 *@version    $Revision$
 *@since      2.2
-->

<#if hasPermission>

${pages.get("/product/ProductTabBar.ftl")}
    
    <div class="head1">Associations <span class="head2">for <#if product?exists>${(product.productName)?if_exists}</#if>[ID:${productId?if_exists}]</span></div>
    
    <a href="<@ofbizUrl>/EditProduct</@ofbizUrl>" class="buttontext">[New Product]</a>
    <#if productId?has_content>
        <a href="/ecommerce/control/product?product_id=${productId}" class="buttontext" target="_blank">[Product Page]</a>
    </#if>
    <br>
    <br>
    
    <form action="<@ofbizUrl>/UpdateProductAssoc</@ofbizUrl>" method=POST style="margin: 0;" name="editProductAssocForm">
    <table border="0" cellpadding="2" cellspacing="0">
    
    <#if !(productAssoc?exists)>
        <#if productId?exists && productIdTo?exists && productAssocTypeId?exists && fromDate?exists>
            <div class="tabletext"><b>Association not found: Product Id=${productId?if_exists}, Product Id To=${productIdTo?if_exists}, Association Type Id=${productAssocTypeId?if_exists}, From Date=${fromDate?if_exists}.</b></div>
            <input type=hidden name="UPDATE_MODE" value="CREATE">
            <tr>
            <td align=right><div class="tabletext">Product ID</div></td>
            <td>&nbsp;</td>
            <td><input type="text" class="inputBox" name="PRODUCT_ID" size="20" maxlength="40" value="${productId?if_exists}"></td>
            </tr>
            <tr>
            <td align=right><div class="tabletext">Product ID To</div></td>
            <td>&nbsp;</td>
            <td><input type="text" class="inputBox" name="PRODUCT_ID_TO" size="20" maxlength="40" value="${productIdTo?if_exists}"></td>
            </tr>
            <tr>
            <td align=right><div class="tabletext">Association Type ID</div></td>
            <td>&nbsp;</td>
            <td>
                <select class="selectBox" name="PRODUCT_ASSOC_TYPE_ID" size=1>
                <#if productAssocTypeId?has_content>
                    <#assign curAssocType = delegator.findByPrimaryKey("ProductAssocType", Static["org.ofbiz.core.util.UtilMisc"].toMap("productAssocTypeId", productAssocTypeId))>
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
            <td align=right><div class="tabletext">From Date</div></td>
            <td>&nbsp;</td>
            <td>
                <div class="tabletext">
                    <input type="text" class="inputBox" name="FROM_DATE" size="25" maxlength="40" value="${fromDate?if_exists}">
                    <a href="javascript:call_cal(document.editProductAssocForm.FROM_DATE, '${fromDate?default(nowTimestampString)}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                    (Will be set to now if empty)
                </div>
            </td>
            </tr>
        <#else>
            <input type=hidden name="UPDATE_MODE" value="CREATE">
            <tr>
            <td align=right><div class="tabletext">Product ID</div></td>
            <td>&nbsp;</td>
            <td><input type="text" class="inputBox" name="PRODUCT_ID" size="20" maxlength="40" value="${productId?if_exists}"></td>
            </tr>
            <tr>
            <td align=right><div class="tabletext">Product ID To</div></td>
            <td>&nbsp;</td>
            <td><input type="text" class="inputBox" name="PRODUCT_ID_TO" size="20" maxlength="40" value="${productIdTo?if_exists}"></td>
            </tr>
            <tr>
            <td align=right><div class="tabletext">Association Type ID</div></td>
            <td>&nbsp;</td>
            <td>
                <select class="selectBox" name="PRODUCT_ASSOC_TYPE_ID" size=1>
                <-- <option value="">&nbsp;</option> -->
                <#list assocTypes as assocType>
                    <option value="${(assocType.productAssocTypeId)?if_exists}">${(assocType.description)?if_exists}</option>
                </#list>
                </select>
            </td>
            </tr>
            <tr>
            <td align=right><div class="tabletext">From Date</div></td>
            <td>&nbsp;</td>
            <td>
                <div class="tabletext">
                    <input type="text" class="inputBox" name="FROM_DATE" size="25" maxlength="40" value="">
                    <a href="javascript:call_cal(document.editProductAssocForm.FROM_DATE, '${nowTimestampString}');"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
                    (Will be set to now if empty)
                </div>
            </td>
            </tr>
        </#if>
    <#else>
        <#assign isCreate = false>
        <#assign curProductAssocType = productAssoc.getRelatedOneCache("ProductAssocType")>
        <input type=hidden name="UPDATE_MODE" value="UPDATE">
        <input type=hidden name="PRODUCT_ID" value="${productId?if_exists}">
        <input type=hidden name="PRODUCT_ID_TO" value="${productIdTo?if_exists}">
        <input type=hidden name="PRODUCT_ASSOC_TYPE_ID" value="${productAssocTypeId?if_exists}">
        <input type=hidden name="FROM_DATE" value="${fromDate?if_exists}">
        <tr>
            <td align=right><div class="tabletext">Product ID</div></td>
            <td>&nbsp;</td>
            <td><b>${productId?if_exists}</b> (You must re-create the association to change this.)</td>
        </tr>
        <tr>
            <td align=right><div class="tabletext">Product ID To</div></td>
            <td>&nbsp;</td>
            <td><b>${productIdTo?if_exists}</b> (You must re-create the association to change this.)</td>
        </tr>
        <tr>
            <td align=right><div class="tabletext">Association Type</div></td>
            <td>&nbsp;</td>
            <td><b><#if curProductAssocType?exists>${(curProductAssocType.description)?if_exists}<#else> ${productAssocTypeId?if_exists}</#if></b> (You must re-create the association to change this.)</td>
        </tr>
        <tr>
            <td align=right><div class="tabletext">From Date</div></td>
            <td>&nbsp;</td>
            <td><b>${fromDate?if_exists}</b> (You must re-create the association to change this.)</td>
        </tr>
    </#if>
    <tr>
        <td width="26%" align=right><div class="tabletext">Thru Date</div></td>
        <td>&nbsp;</td>
        <td width="74%">
        <div class="tabletext">
            <input type="text" class="inputBox" name="THRU_DATE" <#if useValues> value="${productAssoc.getTimestamp("thruDate").toString()}"<#else>value="${(request.getParameter("THRU_DATE"))?if_exists}"</#if> size="30" maxlength="30">
            <a href="javascript:call_cal(document.editProductAssocForm.THRU_DATE, <#if useValues>'${productAssoc.getTimestamp("thruDate").toString()}'<#elseif (request.getParameter("THRU_DATE"))?exists>'${request.getParameter("THRU_DATE")}'<#else>'${nowTimestampString}'</#if>);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"></a>
        </div>
        </td>
    </tr>
    <tr>
        <td width="26%" align=right><div class="tabletext">SequenceNum</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="SEQUENCE_NUM" <#if useValues>value="${(productAssoc.sequenceNum)?if_exists}"<#else>value="${(request.getParameter("SEQUENCE_NUM"))?if_exists}"</#if> size="5" maxlength="10"></td>
    </tr>
    <tr>
        <td width="26%" align=right><div class="tabletext">Reason</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="REASON" <#if useValues>value="${(productAssoc.reason)?if_exists}"<#else>value="${(request.getParameter("REASON"))?if_exists}"</#if> size="60" maxlength="255"></td>
    </tr>
    <tr>
        <td width="26%" align=right><div class="tabletext">Instruction</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="INSTRUCTION" <#if useValues>value="${(productAssoc.instruction)?if_exists}"<#else>value="${(request.getParameter("INSTRUCTION"))?if_exists}"</#if> size="60" maxlength="255"></td>
    </tr>
    
    <tr>
        <td width="26%" align=right><div class="tabletext">Quantity</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" class="inputBox" name="QUANTITY" <#if useValues>value="${(productAssoc.quantity)?if_exists}"<#else>value="${(request.getParameter("QUANTITY"))?if_exists}"</#if> size="10" maxlength="15"></td>
    </tr>
    
    <tr>
        <td colspan="2">&nbsp;</td>
        <td align=left><input type="submit" <#if isCreate>value="Create"<#else>value="Update"</#if>></td>
    </tr>
    </table>
    </form>
    <br>
    <#if productId?exists && product?exists>
        <hr class="sepbar">
        <div class="head2">Associations FROM this Product to...</div>
        
        <table border="1" cellpadding="2" cellspacing="0">
            <tr>
            <td><div class="tabletext"><b>To Product ID</b></div></td>
            <td><div class="tabletext"><b>Name</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td><div class="tabletext"><b>SeqNum</b></div></td>
            <td><div class="tabletext"><b>Association&nbsp;Type</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
            </tr>
            <#list assocFromProducts as assocFromProduct>
            <#assign listToProduct = assocFromProduct.getRelatedOneCache("AssocProduct")>
            <#assign curProductAssocType = assocFromProduct.getRelatedOneCache("ProductAssocType")>
            <tr valign="middle">
                <td><a href="<@ofbizUrl>/EditProduct?productId=${(assocFromProduct.productIdTo)?if_exists}</@ofbizUrl>" class="buttontext">${(assocFromProduct.productIdTo)?if_exists}</a></td>
                <td><#if listToProduct?exists><a href="<@ofbizUrl>/EditProduct?productId=${(assocFromProduct.productIdTo)?if_exists}</@ofbizUrl>" class="buttontext">${(listToProduct.productName)?if_exists}</a></#if>&nbsp;</td>
                <td><div class="tabletext" <#if (assocFromProduct.getTimestamp("fromDate"))?exists && nowDate.before(assocFromProduct.getTimestamp("fromDate"))> style="color: red;"</#if>>
                ${(assocFromProduct.getTimestamp("fromDate"))?if_exists}&nbsp;</div></td>
                <td><div class="tabletext" <#if (assocFromProduct.getTimestamp("thruDate"))?exists && nowDate.after(assocFromProduct.getTimestamp("thruDate"))> style="color: red;"</#if>>
                ${(assocFromProduct.getTimestamp("thruDate"))?if_exists}&nbsp;</div></td>
                <td><div class="tabletext">&nbsp;${(assocFromProduct.sequenceNum)?if_exists}</div></td>
                <td><div class="tabletext"><#if curProductAssocType?exists> ${(curProductAssocType.description)?if_exists}<#else>${(assocFromProduct.productAssocTypeId)?if_exists}</#if></div></td>
                <td>
                <a href="<@ofbizUrl>/UpdateProductAssoc?UPDATE_MODE=DELETE&PRODUCT_ID=${productId}&PRODUCT_ID_TO=${(assocFromProduct.productIdTo)?if_exists}&PRODUCT_ASSOC_TYPE_ID=${(assocFromProduct.productAssocTypeId)?if_exists}&FROM_DATE=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(assocFromProduct.getTimestamp("fromDate").toString())}&useValues=true</@ofbizUrl>" class="buttontext">
                [Delete]</a>
                </td>
                <td>
                <a href="<@ofbizUrl>/EditProductAssoc?PRODUCT_ID=${productId}&PRODUCT_ID_TO=${(assocFromProduct.productIdTo)?if_exists}&PRODUCT_ASSOC_TYPE_ID=${(assocFromProduct.productAssocTypeId)?if_exists}&FROM_DATE=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(assocFromProduct.getTimestamp("fromDate").toString())}&useValues=true</@ofbizUrl>" class="buttontext">
                [Edit]</a>
                </td>
            </tr>
            </#list>
        </table>
        
        <hr class="sepbar">
        <div class="head2">Associations TO this Product from...</div>
        <table border="1" cellpadding="2" cellspacing="0">
            <tr>
            <td><div class="tabletext"><b>Product ID</b></div></td>
            <td><div class="tabletext"><b>Name</b></div></td>
            <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
            <td><div class="tabletext"><b>Association&nbsp;Type</b></div></td>
            <td><div class="tabletext"><b>&nbsp;</b></div></td>
            </tr>
            <#list assocToProducts as assocToProduct>
            <#assign listToProduct = assocToProduct.getRelatedOneCache("MainProduct")>
            <#assign curProductAssocType = assocToProduct.getRelatedOneCache("ProductAssocType")>
            <tr valign="middle">
                <td><a href="<@ofbizUrl>/EditProduct?productId=${(assocToProduct.productId)?if_exists}</@ofbizUrl>" class="buttontext">${(assocToProduct.productId)?if_exists}</a></td>
                <td><#if listToProduct?exists><a href="<@ofbizUrl>/EditProduct?productId=${(assocToProduct.productId)?if_exists}</@ofbizUrl>" class="buttontext">${(listToProduct.productName)?if_exists}</a></#if></td>
                <td><div class="tabletext">${(assocToProduct.getTimestamp("fromDate"))?if_exists}&nbsp;</div></td>
                <td><div class="tabletext">${(assocToProduct.getTimestamp("thruDate"))?if_exists}&nbsp;</div></td>
                <td><div class="tabletext"><#if curProductAssocType?exists> ${(curProductAssocType.description)?if_exists}<#else> ${(assocToProduct.productAssocTypeId)?if_exists}</#if></div></td>
                <td>
                <a href="<@ofbizUrl>/UpdateProductAssoc?UPDATE_MODE=DELETE&PRODUCT_ID=${(assocToProduct.productId)?if_exists}&PRODUCT_ID_TO=${(assocToProduct.productIdTo)?if_exists}&PRODUCT_ASSOC_TYPE_ID=${(assocToProduct.productAssocTypeId)?if_exists}&FROM_DATE=${Static["org.ofbiz.core.util.UtilFormatOut"].encodeQueryValue(assocToProduct.getTimestamp("fromDate").toString())}&useValues=true</@ofbizUrl>" class="buttontext">
                [Delete]</a>
                </td>
            </tr>
            </#list>
        </table>

        <br>
        <div class="tabletext">NOTE: <b style="color: red;">Red</b> date/time entries denote that the current time is before the From Date or after the Thru Date. If the From Date is <b style="color: red;">red</b>, association has not started yet; if Thru Date is <b style="color: red;">red</b>, association has expired (<u>and should probably be deleted</u>).</div>
    </#if>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
