<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *@version    $Rev:$
-->
<?xml version="1.0" encoding="UTF-8" ?>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
<#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, quote.partyId, false)}-->

<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
            margin-top="0.5in" margin-bottom="1in" margin-left="1in" margin-right="1in">
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>

<#if quote?exists>
        <fo:page-sequence master-reference="main">
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <fo:block font-size="12pt">Quote Report</fo:block>
            <fo:block font-size="10pt">${uiLabelMap.OrderOrderQuoteId} ${quote.quoteId}</fo:block>
            <fo:block font-size="10pt">Customer: </fo:block>
            <fo:block font-size="10pt">Order Status:</fo:block>
            <fo:block space-after.optimum="10pt" font-size="8pt">
            <fo:table>
                <fo:table-column column-width="40pt"/>
                <fo:table-column column-width="200pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold">
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.ProductItem}</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block>${uiLabelMap.EcommerceProduct}</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block text-align="right">${uiLabelMap.ProductQuantity}</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block text-align="right">${uiLabelMap.OrderAmount}</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block text-align="right">${uiLabelMap.OrderOrderQuoteUnitPrice}</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block text-align="right">${uiLabelMap.OrderAdjustments}</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom="thin solid grey"><fo:block text-align="right">${uiLabelMap.CommonSubtotal}</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#assign totalQuoteAmount = 0.0>
                    <#list quoteItems as quoteItem>
                        <#if quoteItem.productId?exists>
                            <#assign product = quoteItem.getRelatedOne("Product")>
                        </#if>
                        <#assign quoteItemAmount = quoteItem.quoteUnitPrice?default(0) * quoteItem.quantity?default(0)>
                        <#assign quoteItemAdjustments = quoteItem.getRelated("QuoteAdjustment")>
                        <#assign totalQuoteItemAdjustmentAmount = 0.0>
                        <#list quoteItemAdjustments as quoteItemAdjustment>
                            <#assign totalQuoteItemAdjustmentAmount = quoteItemAdjustment.amount?default(0) + totalQuoteItemAdjustmentAmount>
                        </#list>
                        <#assign totalQuoteItemAmount = quoteItemAmount + totalQuoteItemAdjustmentAmount>
                        <#assign totalQuoteAmount = totalQuoteAmount + totalQuoteItemAmount>

                        <fo:table-row>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${quoteItem.quoteItemSeqId}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block>${(product.internalName)?xml?if_exists} [${quoteItem.productId?if_exists}]</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right">${quoteItem.quantity?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right">${quoteItem.selectedAmount?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right"><@ofbizCurrency amount=quoteItem.quoteUnitPrice isoCode=quote.currencyUomId/></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right"><@ofbizCurrency amount=totalQuoteItemAdjustmentAmount isoCode=quote.currencyUomId/></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block text-align="right"><@ofbizCurrency amount=totalQuoteItemAmount isoCode=quote.currencyUomId/></fo:block>
                            </fo:table-cell>

                        </fo:table-row>
                        <#list quoteItemAdjustments as quoteItemAdjustment>
                            <#assign adjustmentType = quoteItemAdjustment.getRelatedOne("OrderAdjustmentType")>
                            <fo:table-row>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                    <fo:block font-size="7pt" text-align="right">${adjustmentType.description?if_exists}</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                    <fo:block font-size="7pt" text-align="right"><@ofbizCurrency amount=quoteItemAdjustment.amount isoCode=quote.currencyUomId/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="2pt" background-color="${rowColor}">
                                </fo:table-cell>
                            </fo:table-row>
                        </#list>

                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>        
                    </#list>          
                </fo:table-body>
            </fo:table>
            </fo:block>
        </fo:flow>
        </fo:page-sequence>
<#else>
    <fo:page-sequence master-reference="main">
    <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
        <fo:block font-size="14pt">
            Quote Not Found.
        </fo:block>
    </fo:flow>
    </fo:page-sequence>
</#if>
</fo:root>

