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
 *@version    $Revision: 1.1 $
 *@since      3.1
-->

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Messages</div>
          </td>
          <#if profileMessages?exists>
            <td valign="middle" align="right">
              <a href="<@ofbizUrl>/messagelist</@ofbizUrl>" class="submenutextright">View All</a>
            </td>
          </#if>
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
              <#if !messages?has_content>
                <tr><td><div class="tabletext">No messages.</div></td></tr>
              <#else>
                <tr>
                  <td><div class="tableheadtext">From</div></td>
                  <td><div class="tableheadtext">Subject</div></td>
                  <td><div class="tableheadtext">Sent Date</div></td>
                  <td>&nbsp;</td>
                </tr>
                <tr><td colspan="4"><hr class="sepbar"></td></tr>
                <#list messages as message>
                  <#assign delegator = requestAttributes.delegator>
                  <#assign partyId = message.partyIdFrom>
                  <#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, true)>
                  <tr>
                    <td><div class="tabletext">${partyName}</div></td>
                    <td><div class="tabletext">${message.subject?default("")}</div></td>
                    <td><div class="tabletext">${message.entryDate}</div></td>
                    <td align="right"><a href="<@ofbizUrl>/readmessage?messageId=${message.communicationEventId}</@ofbizUrl>" class="buttontext">[Read]</a></td>
                  </tr>
                </#list>
              </#if>
            </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
