<#--
 *  Copyright (c) 2002-2005 The Open For Business Project - www.ofbiz.org
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
 * @author     Al Byers (byersa@automationgroups.com)
 * @created    July 27 2005
 * @version    1.0
 */
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>
<#-- Main Heading -->
<table width="100%" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td align="left">
      <div class="head1">
      </div>
    </td>
    <td align="right">
      <div class="tabContainer">
        <a href="<@ofbizUrl>/EditPartyClassificationGroup?partyClassificationGroupId=<#if partyClassificationGroup?has_content>${partyClassificationGroup.partyClassificationGroupId}</#if></@ofbizUrl>" class="${selectedClassMap.EditPartyClassificationGroup?default(unselectedClassName)}">${uiLabelMap.PartyClassificationGroups}</a>
        <a href="<@ofbizUrl>/EditPartyClassificationGroupParties?partyClassificationGroupId=<#if partyClassificationGroup?has_content>${partyClassificationGroup.partyClassificationGroupId}</#if></@ofbizUrl>" class="${selectedClassMap.EditPartyClassificationGroupParties?default(unselectedClassName)}">${uiLabelMap.Parties}</a>
      </div>
    </td>
  </tr>
</table>

<#else>
  <div class="head2">${uiLabelMap.PartyMgrViewPermissionError}</div>
</#if>
