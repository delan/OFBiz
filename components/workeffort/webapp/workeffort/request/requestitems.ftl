<#--
 *  Description: None
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Johan Isacsson (conversion of jsp created by Andy Zeneski) 
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap) 
 *@version    $Rev:$
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<div class='tabContainer'>
  <a href="<@ofbizUrl>/request?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortRequest}</a>
  <a href="<@ofbizUrl>/requestroles?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortRequestRoles}</a>
  <a href="<@ofbizUrl>/requestitems?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.WorkEffortRequestItems}</a>
</div>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD>
            <div class='boxhead'>${uiLabelMap.WorkEffortRequestItems}</div>
          </TD>
          <td align='right'>
            <a href="<@ofbizUrl>/requestitem?custRequestId=${custRequestId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                
                <#if custRequestItems?has_content>
                <TR>
                  <TD>
                    <table width="100%" cellpadding="2" cellspacing="0" border="0">
                      <tr>
                        <td align="right"><div class="tableheadtext">#</div></td>
                        <td>&nbsp;</td>
                        <td><div class="tableheadtext">${uiLabelMap.CommonDescription}</div></td>
                        <td><div class="tableheadtext">${uiLabelMap.WorkEffortPriority}</div></td>
                        <td><div class="tableheadtext">${uiLabelMap.WorkEffortStatus}</div></td>
                        <td><div class="tableheadtext">${uiLabelMap.WorkEffortProduct}</div></td>
                        <td align='right'><div class="tableheadtext">${uiLabelMap.WorkEffortQuantity}</div></td>
                        <td>&nbsp;&nbsp;</td>
                        <td align='right'><div class="tableheadtext">${uiLabelMap.WorkEffortMaxAmount}</div></td>
                        <td>&nbsp;&nbsp;</td>
                        <td><div class="tableheadtext">${uiLabelMap.WorkEffortRequiredDate}</div></td>
                        <td>&nbsp;</td>                        
                      </tr>
                      <tr>
                        <td colspan='12'><hr class="sepbar"></td>
                      </tr>
                      <#setting number_format="#.##"/>
                      <#list custRequestItems as item>
                          <#assign statusItem = item.getRelatedOne("StatusItem")>
                          <tr>
                            <td align="right"><div class="tabletext">${item.custRequestItemSeqId}</div></td>
                            <td>&nbsp;</td>
                            <td><a href="<@ofbizUrl>/requestitem?custRequestId=${custRequestId}&custRequestItemSeqId=${item.custRequestItemSeqId}</@ofbizUrl>" class="buttontext">${item.description?if_exists}</a></td>
                            <td><div class="tabletext">${item.priority?if_exists}</div></td>
                            <td><div class="tabletext">${statusItem.description?if_exists}</div></td>
                            <td><div class="tabletext">${item.productId?if_exists}</div></td>
                            <td align='right'><div class="tabletext">
                            ${item.quantity?default(0)?string.number}</div></td>
                            <td>&nbsp;&nbsp;</td>
                            <td align='right'><div class="tabletext">${item.maximumAmount?default(0)?string.currency}</div></td>
                            <td>&nbsp;&nbsp;</td>
                            <td><div class="tabletext">${item.requiredByDate?if_exists}</div></td>
                            <td align="right"><div class="tabletext"><a href="<@ofbizUrl>/requestitem?custRequestId=${custRequestId}&custRequestItemSeqId=${item.custRequestItemSeqId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonView}]</a></td>
                          </tr>
                      </#list>
                    </table>
                  </TD>
                </TR>
                <#else>
                  <TR>
                    <TD><div class="tabletext">&nbsp;<b>${uiLabelMap.WorkEffortNoItemsCreated}.</b></div></TD>
                  </TR>
                </#if>            
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>


