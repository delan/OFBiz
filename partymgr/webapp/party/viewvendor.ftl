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
 * @author     David E. Jones
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
            ${lookupGroup.groupName?if_exists}
          <#else>
          "New User"
          </#if>
        </#if>
      </div>
    </td>
    <td align="right">
	  <div class="tabContainer">
      <a href="<@ofbizUrl>/viewprofile</@ofbizUrl>" class="tabButton">Profile</a>
      <a href="<@ofbizUrl>/viewvendor</@ofbizUrl>" class="tabButtonSelected">Vendor</a>
      <a href="<@ofbizUrl>/viewroles</@ofbizUrl>" class="tabButton">Roles</a>
      <a href="<@ofbizUrl>/viewrelationships</@ofbizUrl>" class="tabButton">Relationships</a>
      </div>
    </td>
  </tr>
</table>

<br>
<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Vendor Information</div>
          </td>
        </tr>
      </table>
    </td>
    </form>
  </tr>
  <tr>
    <td width="100%">
${editVendorWrapper.renderFormString()}
    </td>
  </tr>
</table>

<#else>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
</#if>
