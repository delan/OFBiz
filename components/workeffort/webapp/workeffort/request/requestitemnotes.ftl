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
  <a href="<@ofbizUrl>/requestitems?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortRequestItems}</a>
  <a href="<@ofbizUrl>/requestitem?custRequestId=${custRequestId}&custRequestItemSeqId=${custRequestItemSeqId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortItem}</a>
  <a href="<@ofbizUrl>/requestitemnotes?custRequestId=${custRequestId}&custRequestItemSeqId=${custRequestItemSeqId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.WorkEffortNotes}</a>
  <a href="<@ofbizUrl>/requestitemrequirements?custRequestId=${custRequestId}&custRequestItemSeqId=${custRequestItemSeqId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortRequirements}</a>    
  <a href="#" class="tabButton">${uiLabelMap.WorkEffortTasks}</a>    
</div>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD>
            <div class='boxhead'>${uiLabelMap.WorkEffortNotesForRequestItem}: ${custRequestItem.description?if_exists}</div>
          </TD>
          <td align="right">
            <#if showAll = "false">
            <a href="<@ofbizUrl>/requestitemnotes?custRequestId=${custRequestId}&custRequestItemSeqId=${custRequestItemSeqId}&showAll=true</@ofbizUrl>" class="lightbuttontext">[${uiLabelMap.WorkEffortShowAllNotes}]</a>
            <#else>
            <a href="<@ofbizUrl>/requestitemnotes?custRequestId=${custRequestId}&custRequestItemSeqId=${custRequestItemSeqId}&showAll=false</@ofbizUrl>" class="lightbuttontext">[${uiLabelMap.WorkEffortShowThisItemsNotes}]</a>
            </#if>
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
            <#if notes?has_content>
            <table width="100%" border="0" cellpadding="1">
              <#list notes as noteRef>
                <tr>
                  <td align="left" valign="top" width="35%">
                    <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonBy} : </b>${noteRef.firstName}&nbsp;${noteRef.lastName}</div>
                    <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonAt} : </b>${noteRef.noteDateTime}</div>
                    <#if showAll = "true">
                    <div class="tabletext">&nbsp;<b>${uiLabelMap.WorkEffortItem}: </b>${noteRef.custRequestItemSeqId}</div>
                    </#if>
                  </td>
                  <td align="left" valign="top" width="65%">
                    <div class="tabletext">${noteRef.noteInfo}</div>
                  </td>
                </tr>
                <#if noteRef_has_next>
                  <tr><td colspan="2"><hr class="sepbar"></td></tr>
                </#if>
              </#list>
            </table>
            <#else>
              <div class="tabletext">${uiLabelMap.WorkEffortNoNotesForThisRequestItem}.</div>
            </#if>  
          </td>
        </tr>
        <tr>
          <td><hr class="sepbar"></td>
        </tr>
        <tr>
          <td>
            <form method="post" action="<@ofbizUrl>/createrequestitemnote</@ofbizUrl>" name="createnoteform">
              <input type="hidden" name="custRequestId" value="${custRequestId}">
              <input type="hidden" name="custRequestItemSeqId" value="${custRequestItemSeqId}">
              <table width="90%" border="0" cellpadding="2" cellspacing="0">
                <tr>
                  <td width="26%" align='right'><div class="tableheadtext">${uiLabelMap.WorkEffortNewNote}</div></td>
                  <td width="74%">
                    <textarea class="textAreaBox" name="note" rows="5" cols="70"></textarea>
                  </td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                  <td align="right"> 
                    <input type="submit" style="font-size: small;" value="${uiLabelMap.CommonCreate}">  
                  </td>
                  <td>&nbsp;</td>
                </tr>                  
              </table>
            </form>            
                    
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
