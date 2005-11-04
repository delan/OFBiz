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
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *@version    $Rev$
-->

<#if request?exists>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
        <#-- left side -->
        <td width="50%" valign="top" align="left">

            <div class="screenlet">
                <div class="screenlet-header">
                    <div class="boxhead">${uiLabelMap.OrderRequest}&nbsp;${custRequest.custRequestId}&nbsp;${uiLabelMap.CommonInformation}</div>
                </div>
                <div class="screenlet-body">
                    <table width="100%" border="0" cellpadding="1">
                        <#-- request header information -->
                        <tr>
                            <td align="right" valign="top" width="15%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonType}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                                <div class="tabletext">${(custRequestType.description)?default(custRequest.custRequestTypeId?if_exists)}</div>
                            </td>
                        </tr>
                        <tr><td colspan="7"><hr class="sepbar"/></td></tr>
                        <#-- request status information -->
                        <tr>
                            <td align="right" valign="top" width="15%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonStatus}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                                <div class="tabletext">${(statusItem.get("description", locale))?default(custRequest.statusId?if_exists)}</div>
                            </td>
                        </tr>
                        <#-- party -->
                        <tr><td colspan="7"><hr class="sepbar"/></td></tr>
                        <tr>
                            <td align="right" valign="top" width="15%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.PartyPartyId}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                                <div class="tabletext">${custRequest.fromPartyId?if_exists}</div>
                            </td>
                        </tr>
                        <#-- request name -->
                        <tr><td colspan="7"><hr class="sepbar"/></td></tr>
                        <tr>
                            <td align="right" valign="top" width="15%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonName}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                                <div class="tabletext">${custRequest.custRequestName?if_exists}</div>
                            </td>
                        </tr>
                        <#-- request description -->
                        <tr><td colspan="7"><hr class="sepbar"/></td></tr>
                        <tr>
                            <td align="right" valign="top" width="15%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonDescription}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                                <div class="tabletext">${custRequest.description?if_exists}</div>
                            </td>
                        </tr>
                        <#-- request currency -->
                        <tr><td colspan="7"><hr class="sepbar"/></td></tr>
                        <tr>
                            <td align="right" valign="top" width="15%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonCurrencyUom}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                                <div class="tabletext"><#if currency?exists>${currency.description?default(custRequest.maximumAmountUomId?if_exists)}</#if></div>
                            </td>
                        </tr>
                        <#-- request currency -->
                        <tr><td colspan="7"><hr class="sepbar"/></td></tr>
                        <tr>
                            <td align="right" valign="top" width="15%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.ProductProductStore}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                                <div class="tabletext"><#if store?exists>${store.storeName?default(custRequest.productStoreId?if_exists)}</#if></div>
                            </td>
                        </tr>
                        
                    </table>
                </div>
            </div>
        </td>

        <td bgcolor="white" width="1">&nbsp;&nbsp;</td>
        <#-- right side -->

        <td width="50%" valign="top" align="left">
            <div class="screenlet">
                <div class="screenlet-header">
                    <div class="boxhead">&nbsp;${uiLabelMap.CommonDate}</div>
                </div>
                <div class="screenlet-body">
                    <table width="100%" border="0" cellpadding="1">
                        <tr>
                            <td align="right" valign="top" width="25%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderRequestDate}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="70%">
                                <div class="tabletext">${(custRequest.custRequestDate.toString())?if_exists}</div>
                            </td>
                        </tr>
                        <tr><td colspan="7"><hr class="sepbar"/></td></tr>
                        <tr>
                            <td align="right" valign="top" width="25%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderRequestCreatedDate}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="70%">
                                <div class="tabletext">${(custRequest.createdDate.toString())?if_exists}</div>
                            </td>
                        </tr>
                        <tr><td colspan="7"><hr class="sepbar"/></td></tr>
                        <tr>
                            <td align="right" valign="top" width="25%">
                                <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderRequestLastModifiedDate}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="70%">
                                <div class="tabletext">${(custRequest.lastModifiedDate.toString())?if_exists}</div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div class="screenlet">
                <div class="screenlet-header">
                    <div class="boxhead">&nbsp;${uiLabelMap.OrderRequestRoles}</div>
                </div>
                <div class="screenlet-body">
                    <table width="100%" border="0" cellpadding="1">
                        <#list requestRoles as requestRole>
                            <#assign roleType = requestRole.getRelatedOne("RoleType")>
                            <#assign party = requestRole.getRelatedOne("Party")>
                            <tr>
                                <td align="right" valign="top" width="15%">
                                    <div class="tabletext">&nbsp;<b>${roleType.description?if_exists}</b></div>
                                </td>
                                <td width="5">&nbsp;</td>
                                <td align="left" valign="top" width="80%">
                                    <div class="tabletext">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(party)}</div>
                                </td>
                            </tr>
                            <tr><td colspan="7"><hr class="sepbar"/></td></tr>
                        </#list>
                    </table>
                </div>
            </div>
        </td>
    </tr>
</table>
</#if>