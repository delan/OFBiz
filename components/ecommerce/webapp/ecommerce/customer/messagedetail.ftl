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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Rev:$
 *@since      3.1
-->

<#assign delegator = requestAttributes.delegator>
<#assign partyIdFrom = message.partyIdFrom>
<#assign partyIdTo = message.partyIdTo>
<#assign fromName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyIdFrom, true)>
<#assign toName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyIdTo, true)>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${(message.subject)?default("[No subject]")}</div>
          </td>
          <td valign="middle" align="right">
            <#if (useSentTo)?default("false")?lower_case == "false">
              <a href="<@ofbizUrl>/newmessage?messageId=${message.communicationEventId}</@ofbizUrl>" class="submenutext">Reply</a>
            </#if>
            <a href="<@ofbizUrl>/sentmessages</@ofbizUrl>" class="submenutext">View Sent</a>
            <a href="<@ofbizUrl>/messagelist</@ofbizUrl>" class="submenutextright">View List</a>
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
            <table width="100%" border="0" cellpadding="1">
              <tr><td>&nbsp;</td></tr>
              <tr>
                <td align="right"><div class="tableheadtext">From:</div></td>
                <td><div class="tabletext">${fromName}</div></td>
              </tr>
              <tr>
                <td align="right"><div class="tableheadtext">To:</div></td>
                <td><div class="tabletext">${toName}</div></td>
              </tr>
              <tr>
                <td align="right"><div class="tableheadtext">Date:</div></td>
                <td><div class="tabletext">${message.entryDate}</div></td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr><td>&nbsp;</td></tr>
              <tr>
                <td>&nbsp;</td>
                <td>
                  <div class="tabletext">${message.content?default("[Empty Body]")}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>