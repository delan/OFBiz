<#--
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
 * @author     Andy Zeneski
 * @created    July 12, 2002
 * @version    1.0
-->

<#if hasViewPermission>

<#-- Main Heading -->
<table width="100%" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td align="left">
      <div class="head1">The Profile of
        <#if lookupPerson?exists>
          ${lookupPerson.personalTitle?if_exists}
          ${lookupPerson.firstName?if_exists}
          ${lookupPerson.middleName?if_exists}
          ${lookupPerson.lastName?if_exists}
          ${lookupPerson.suffix?if_exists}
        <#else>
          <#if lookupGroup?exists>
            ${lookupGroup.groupName?default("No name (group)")}
          <#else>
          "New User"
          </#if>
        </#if>
      </div>
    </td>
    <td align="right">
	  <div class="tabContainer">
        <a href="<@ofbizUrl>/viewprofile?party_id=${partyId}</@ofbizUrl>" class="tabButton">Profile</a>
        <a href="<@ofbizUrl>/viewvendor?party_id=${partyId}</@ofbizUrl>" class="tabButton">Vendor</a>
        <a href="<@ofbizUrl>/viewroles?party_id=${partyId}</@ofbizUrl>" class="tabButtonSelected">Roles</a>
        <a href="<@ofbizUrl>/viewrelationships?party_id=${partyId}</@ofbizUrl>" class="tabButton">Relationships</a>
        <a href="<@ofbizUrl>/viewcommunications?partyId=${partyId}</@ofbizUrl>" class="tabButton">Communications</a>
      </div>
    </td>
  </tr>
</table>

<#-- Party Roles -->
<br>
<TABLE border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Member Roles</div>
          </td>
        </tr>
      </table>
    </TD>
    </form>
  </TR>
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td>
            <#if partyRoles?has_content>
            <table width="100%" border="0" cellpadding="1">
              <#list partyRoles as userRole>
              <tr>
                <td align="right" valign="top" width="10%" nowrap><div class="tabletext"><b>Role</b></div></td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="70%"><div class="tabletext">${userRole.description} [${userRole.roleTypeId}]</div></td>
                <#if hasDeletePermission>
                <td align="right" valign="top" width="20%">
                  <a href="<@ofbizUrl>/deleterole?partyId=${partyId}&roleTypeId=${userRole.roleTypeId}</@ofbizUrl>" class="buttontext">[Remove]</a>&nbsp;
                </td>
                </#if>
              </tr>
              </#list>
            </table>
            <#else>
              <div class="tabletext">No party roles found.</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <#if hasUpdatePermission>
  <TR>
    <TD width="100%"><hr class="sepbar"></TD>
  </TR>
  <TR>
    <TD width="100%" >
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <form name="addPartyRole" method="post" action="<@ofbizUrl>/addrole/viewroles</@ofbizUrl>">
        <input type="hidden" name="partyId" value="${partyId}">
        <tr>
          <td align="right" width="75%"><span class="tabletext">&nbsp;Add To Role:&nbsp;</span></td>
          <td>
            <select name="roleTypeId" class="selectBox">
              <#list roles as role>
                <option value="${role.roleTypeId}">${role.description}</option>
              </#list>
            </select>
          </td>
          <td>
            <a href="javascript:document.addPartyRole.submit()" class="buttontext">[Add]</a>&nbsp;&nbsp;
          </td>
        </tr>
        </form>
      </table>
    </TD>
  </TR>
  </#if>
</TABLE>

<#-- Add role type -->
<#if hasCreatePermission>
<br>
<TABLE border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;New Role Type</div>
          </td>
        </tr>
      </table>
    </TD>
    </form>
  </TR>
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="1" cellpadding="1" class="boxbottom">
        <form method="post" action="<@ofbizUrl>/createroletype/viewroles</@ofbizUrl>" name="createroleform">
        <tr>
          <td width="16%"><div class="tabletext">Role Type ID</div></td>
          <td width="84%">
            <input type="text" name="roleTypeId" size="20" class="inputBox">*
          </td>
        <tr>
          <td width="16%"><div class="tabletext">Description</div></td>
          <td width="84%">
            <input type="text" name="description" size="30" class="inputBox">*
            &nbsp;&nbsp;<a href="javascript:document.createroleform.submit()" class="buttontext">[Save]</a>
          </td>
        </tr>
        </form>
      </table>
    </TD>
  </TR>
</TABLE>
</#if>

<#else>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
</#if>
