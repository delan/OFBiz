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
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap)
 *@version    $Rev$
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>


<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <#-- left side -->
    <td width='50%' valign='top' align='left'>
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <#-- general invoice info -->
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;${uiLabelMap.AccountingInvoice}&nbsp;<#if invoice?has_content>#${invoice.invoiceId}&nbsp;</#if>${uiLabelMap.AccountingInformation}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>        
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>              
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1">
                    <#-- billing party information -->
                    <#if billingParty?has_content>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.AccountingName}</b></div>
                        </td>   
                        <td width="5">&nbsp;</td>
                        <#if billingPerson?has_content>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            <#if billingPerson.partyId != "_NA_">
                              ${billingPerson.firstName?if_exists}&nbsp;
                              <#if billingPerson.middleName?exists>${billingPerson.middleName}&nbsp;</#if>
                              ${billingPerson.lastName?if_exists}
                            <#else>
                              [Anonymous Shopper]
                            </#if>
                          </div>
                        </td>
                        </#if>
                        <#if billingGroup?has_content>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            ${billingGroup.groupName?if_exists}
                          </div>
                        </td>
                        </#if>
                      </tr>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    </#if>
                    <#-- invoice status information -->
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${uiLabelMap.AccountingStatus}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">${invoiceStatus.description}</div> 
                      </td>
                      <#if payments?has_content>
                      <td align="right" width="5%" nowrap>
                        <a href="<@ofbizUrl>/invoicePayments?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="buttontext">${uiLabelMap.AccountingPayments}</a>&nbsp;
                      </td>
                      </#if>
                    </tr>
                    <#-- invoiced date -->                    
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${uiLabelMap.AccountingDate}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">${invoice.invoiceDate?default("N/A").toString()}</div>
                      </td>
                    </tr>
                    <#-- invoiced orders -->
                    <#if orders?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.AccountingOrders}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <#list orders as order>
                            <div class="tabletext">#<a href="/ordermgr/control/orderview?order_id=${order}${requestAttributes.externalKeyParam}" class="buttontext">${order}</a></div>
                          </#list>
                        </td>
                      </tr>
                    </#if>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      <#-- add <br> and continue for more -->
    </td>
    <td bgcolor="white" width="1">&nbsp;&nbsp;</td>
    <#-- right side -->
    <td width='50%' valign='top' align='left'>
      <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>        
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;${uiLabelMap.AccountingBillingInformation}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <#-- billing address -->
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1">
                    <#if billingAddress?has_content>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.AccountingBilling}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            <#if billingAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b> ${billingAddress.toName}<br></#if>
                            <#if billingAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b> ${billingAddress.attnName}<br></#if>
                            ${billingAddress.address1}<br>
                            <#if billingAddress.address2?has_content>${billingAddress.address2}<br></#if>                            
                            ${billingAddress.city}<#if billingAddress.stateProvinceGeoId?has_content>, ${billingAddress.stateProvinceGeoId} </#if>
                            ${billingAddress.postalCode}<br>
                            ${billingAddress.countryGeoId}
                          </div>
                        </td>
                      </tr>
                    <#else>
                      <tr>
                        <td><div class="tabletext">${uiLabelMap.AccountingNocontactinformationset}.</div></td>
                      </tr>
                    </#if>                                                                                           
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <#-- terms -->
        <#if terms?has_content>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;${uiLabelMap.AccountingInvoiceTerms}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1">
                    <#list terms as term>
                      <#assign termType = term.getRelatedOne("TermType")>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.AccountingTerm}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            ${termType.description} <#if term.termValue?has_content> - ${term.termValue}</#if>
                          </div>
                        </td>
                      </tr>
                    </#list>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        </#if>

      </table>
    </td>
  </tr>
</table>

<br>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.AccountingInvoiceItems}</div>
          </td>         
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <table width="100%" border="0" cellpadding="0">
              <tr align='left' valign='bottom'>
                <td width="20%" align="left"><span class="tableheadtext">${uiLabelMap.AccountingType}</span></td>
                <td width="15%" aligh="left"><span class="tableheadtext">${uiLabelMap.AccountingProduct}</span></td>
                <td width="40%" align="left"><span class="tableheadtext">${uiLabelMap.AccountingDescription}</span></td>
                <td width="5%" align="right"><span class="tableheadtext">${uiLabelMap.AccountingQuantity}</span></td>
                <td width="10%" align="right"><span class="tableheadtext">${uiLabelMap.AccountingAmount}</span></td>            
                <td width="10%" align="right"><span class="tableheadtext">${uiLabelMap.AccountingLineTotal}</span></td>
              </tr>
              <#assign seqId = 1>
              <#list invoiceItems as invoiceItem>
                <#assign itemType = invoiceItem.getRelatedOne("InvoiceItemType")>
                <tr><td colspan="7"><hr class='sepbar'></td></tr>                
                <tr>     
                  <td>
                    <div class="tabletext">${itemType.description?if_exists}</div>
                  </td>
                  <td valign="top">                  
                    <div class="tabletext">${invoiceItem.productId?if_exists}</div>
                  </td>
                  <td valign="top">
                    <div class="tabletext">${invoiceItem.description?if_exists}</div>
                  </td>             
                  <td align="right" valign="top">
                    <div class="tabletext" nowrap>${invoiceItem.quantity?string.number}</div>
                  </td>
                  <td align="right" valign="top">
                    <div class="tabletext" nowrap><@ofbizCurrency amount=invoiceItem.amount isoCode=invoice.currencyUomId?if_exists/></div>
                  </td>                 
                  <td align="right" valign="top" nowrap>
                    <div class="tabletext"><@ofbizCurrency amount=(invoiceItem.quantity?double * invoiceItem.amount?double) isoCode=invoice.currencyUomId?if_exists/></div>
                  </td>                
                </tr>
                <#assign seqId = seqId + 1>               
              </#list>
              <#if !invoiceItems?has_content || invoiceItems?size == 0>
                <tr><td><font color="red">${uiLabelMap.AccountingNocurrentinvoicelineitems}.</font></td></tr>
              </#if>
              <#if editInvoice?default(false)>
                <form name="additem" method="post" action="<@ofbizUrl>/createInvoiceItem</@ofbizUrl>">
                <input type="hidden" name="editInvoice" value="true">
                <input type="hidden" name="invoiceId" value="${invoice.invoiceId}">  
                <input type="hidden" name="invoiceItemSeqId" value="${seqId}">  
                <input type="hidden" name="taxableFlag" value="false">           
                <tr><td colspan="8"><hr class='sepbar'></td></tr>
                <tr>
                  <td>
                    <select name="invoiceItemTypeId" class="selectBox">
                      <#list invoiceItemTypes as itemType>
                      <option value="${itemType.invoiceItemTypeId}">${itemType.description?if_exists}</option>
                      </#list>
                    </select>
                  </td>
                  <td>
                    <input type="text" name="productId" class="inputBox" size="8">                                                 
                  </td>
                  <td>
                    <input type="text" name="description" class="inputBox" size="40">
                  </td>
                  <td align="right">
                    <input type="text" name="quantity" class="inputBox" size="3">
                  </td>
                  <td align="right">
                    <input type="text" name="amount" class="inputBox" size="6">
                  </td>
                  <td align="right">
                    <input type="submit" value="Add" class="smallSubmit">
                  </td>
                </tr>
                </form>
              </#if>
              <tr><td colspan="8"><hr class='sepbar'></td></tr>                                                                     
              <tr>
                <td align="right" colspan="4"><div class="tabletext"><b>${uiLabelMap.AccountingInvoiceTotal}</b></div></td>
                <td align="right" nowrap>
                  <div class="tabletext"><@ofbizCurrency amount=invoiceTotal isoCode=invoice.currencyUomId?if_exists/></div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

