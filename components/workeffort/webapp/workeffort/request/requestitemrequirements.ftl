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
 *@version    $Revision: 1.3 $
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<div class='tabContainer'>
  <a href="<@ofbizUrl>/request?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortRequest}</a>
  <a href="<@ofbizUrl>/requestroles?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortRequestRoles}</a>
  <a href="<@ofbizUrl>/requestitems?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortRequestItems}</a>
  <a href="<@ofbizUrl>/requestitem?custRequestId=${custRequestId}&custRequestItemSeqId=${custRequestItemSeqId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortItem}</a>
  <a href="<@ofbizUrl>/requestitemnotes?custRequestId=${custRequestId}&custRequestItemSeqId=${custRequestItemSeqId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortNotes}</a>
  <a href="<@ofbizUrl>/requestitemrequirements?custRequestId=${custRequestId}&custRequestItemSeqId=${custRequestItemSeqId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.WorkEffortRequirements}</a>    
  <a href="#" class="tabButton">${uiLabelMap.WorkEffortTasks}</a>  
</div>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD>
            <div class='boxhead'>${uiLabelMap.WorkEffortRequirementsForRequestItem}: ${custRequestItem.description?if_exists}</div>
          </TD>
          <td align="right" valign="middle">
            <a href="<@ofbizUrl>/requirement?custRequestId=${custRequestId}&custRequestItemSeqId=${custRequestItemSeqId}&productId=${custRequestItem.productId?if_exists}&quantity=${custRequestItem.quantity?if_exists}&donePage=requestitemrequirements</@ofbizUrl>" class="lightbuttontext">[${uiLabelMap.WorkEffortNewRequirement}]</a>
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
                <#if requirements?has_content>
                <TR>
                  <TD>
                    <table width="100%" cellpadding="2" cellspacing="0" border="0">
                      <tr>
                        <td><div class="tableheadtext">${uiLabelMap.WorkEffortRequirementId}</div></td>
                        <td><div class="tableheadtext">${uiLabelMap.CommonDescription}</div></td>
                        <td><div class="tableheadtext">${uiLabelMap.WorkEffortProductId}</div></td>
                        <td align='right'><div class="tableheadtext">${uiLabelMap.WorkEffortQuantity}</div></td>
                        <td>&nbsp;&nbsp;</td>
                        <td align='right'><div class="tableheadtext">${uiLabelMap.WorkEffortEstBudget}</div></td>
                        <td>&nbsp;&nbsp;</td>
                        <td><div class="tableheadtext">${uiLabelMap.WorkEffortRequiredByDate}</div></td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr>
                        <td colspan="9"><hr class="sepbar"></td>
                      </tr>                      	
					  <#setting number_format="#.##"/>
                      <#list requirements as requirement>
                          <tr>
                            <td><a href="<@ofbizUrl>/requirement?requirementId=${requirement.requirementId}&custRequestId=${custRequestId}&custRequestItemSeqId=${requirement.custRequestItemSeqId}&donePage=request</@ofbizUrl>" class="buttontext">${requirement.requirementId}</a></td>
                            <td><div class="tabletext">${requirement.description?if_exists}</div></td>
                            <td><div class="tabletext">${requirement.productId?if_exists}</div></td>
                            <td align='right'><div class="tabletext">${requirement.quantity?default(0)?string.number}></div></td>
                            <td>&nbsp;&nbsp;</td>
                            <td align='right'><div class="tabletext">${requirement.estimatedBudget?default(0)?string.currency}</div></td>
                            <td>&nbsp;&nbsp;</td>
                            <td><div class="tabletext">${requirement.requiredByDate?if_exists}</div></td>
                            <td align="right"><div class="tabletext"><a href="<@ofbizUrl>/requirement?requirementId=${requirement.requirementId}&custRequestId=${custRequestId}&custRequestItemSeqId=${requirement.custRequestItemSeqId}&donePage=request</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonEdit}]</a></td>
                          </tr>
                      </#list>
                    </table>
                  </TD>
                </TR>
                <#else>
                <TR>
                    <TD><div class="tabletext">&nbsp;<b>${uiLabelMap.WorkEffortNoRequirementsCreated}.</b></div></TD>
                  </TR>
                </#if>                                 
              </TABLE>       
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>          

