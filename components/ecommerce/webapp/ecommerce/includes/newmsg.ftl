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
 *@author     Andy Zeneski
 *@version    $Revision: 1.1 $
 *@since      3.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${page.pageHeader}</div>
          </td>
          <#if page.showMessageLinks?default("false")?upper_case == "TRUE">
            <td valign="middle" align="right">
              <a href="<@ofbizUrl>/messagelist</@ofbizUrl>" class="submenutextright">View List</a>
            </td>
          </#if>
        </tr>
     </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <form name="contactus" method="post" action="<@ofbizUrl>${submitRequest}</@ofbizUrl>" style="margin: 0;">
        <input type="hidden" name="partyIdFrom" value="${userLogin.partyId}">
        <input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS">
        <input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI">
        <input type="hidden" name="note" value="${Static["org.ofbiz.base.util.UtilHttp"].getFullRequestUrl(request).toString()}">
        <#if message?has_content>
          <input type="hidden" name="parentCommEventId" value="${message.communicationEventId}">
          <#if (message.origCommEventId?exists && message.origCommEventId?length > 0)>
            <#assign orgComm = message.origCommEventId>
          <#else>
            <#assign orgComm = message.communicationEventId>
          </#if>
          <input type="hidden" name="origCommEventId" value="${orgComm}">
        </#if>
        <table width="100%" border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td colspan="3">&nbsp;</td>
          </tr>
          <tr>
            <td width="5">&nbsp;</td>
            <td align="right"><div class="tableheadtext">From:</div></td>
            <td><div class="tabletext">&nbsp;${sessionAttributes.autoName} [${userLogin.partyId}] (${uiLabelMap.CommonNotYou}?&nbsp;<a href="<@ofbizUrl>/autoLogout</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonClickHere}</a>)</div></td>
          </tr>
          <#if partyIdTo?has_content>
            <#assign partyToName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyIdTo, true)>
            <input type="hidden" name="partyIdTo" value="${partyIdTo}">
            <tr>
              <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
              <td width="5">&nbsp;</td>
              <td align="right"><div class="tableheadtext">To:</div></td>
              <td><div class="tabletext">&nbsp;${partyToName}</div></td>
            </tr>
          </#if>
          <tr>
            <td colspan="3">&nbsp;</td>
          </tr>
          <#assign defaultSubject = (message.subject)?default("")>
          <#if (defaultSubject?length == 0)>
            <#assign replyPrefix = "RE: ">
            <#if parentEvent?has_content>
              <#if !parentEvent.subject?default("")?upper_case?starts_with(replyPrefix)>
                <#assign defaultSubject = replyPrefix>
              </#if>
              <#assign defaultSubject = defaultSubject + parentEvent.subject?default("")>
            </#if>
          </#if>
          <tr>
            <td width="5">&nbsp;</td>
            <td align="right"><div class="tableheadtext">Subject:</div></td>
            <td><input type="input" class="inputBox" name="subject" size="20" value="${defaultSubject}">
          </tr>
          <tr>
            <td colspan="3">&nbsp;</td>
          </tr>
          <tr>
            <td width="5">&nbsp;</td>
            <td align="right"><div class="tableheadtext">Message:</div></td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td colspan="2">&nbsp;</td>
            <td colspan="2">
              <textarea name="content" class="textAreaBox" cols="40" rows="5" wrap="hard"></textarea>
            </td>
          </tr>
          <tr>
            <td colspan="3">&nbsp;</td>
          </tr>
          <tr>
            <td colspan="2">&nbsp;</td>
            <td><input type="submit" class="smallSubmit" value="Send"></td>
          </tr>
        </table>
      </form>
    </TD>
  </TR>
</TABLE>