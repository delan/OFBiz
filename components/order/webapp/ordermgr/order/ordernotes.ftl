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
 *@version    $Revision: 1.2 $
 *@since      2.2
-->

<#assign delegator = requestAttributes.delegator>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Notes</div>
          </td>
          <td valign="middle" align="right">
            <#if security.hasEntityPermission("ORDERMGR", "_NOTE", session)>  
              <a href="<@ofbizUrl>/createnewnote?${paramString}</@ofbizUrl>" class="submenutextright">Create New</a>
            </#if>
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
            <#if orderNotes?has_content>
            <table width="100%" border="0" cellpadding="1">
              <#list orderNotes as note>
                <tr>
                  <td align="left" valign="top" width="35%">
                    <div class="tabletext">&nbsp;<b>By: </b>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, note.noteParty, true)}</div>
                    <div class="tabletext">&nbsp;<b>At: </b>${note.noteDateTime?string?if_exists}</div>
                  </td>
                  <td align="left" valign="top" width="65%">
                    <div class="tabletext">${note.noteInfo?if_exists}</div>
                  </td>
                </tr>
                <#if note_has_next>          
                  <tr><td colspan="2"><hr class="sepbar"></td></tr>
                </#if>
              </#list>
            </table>
            <#else>            
              <div class="tabletext">&nbsp;No notes for this order.</div>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
