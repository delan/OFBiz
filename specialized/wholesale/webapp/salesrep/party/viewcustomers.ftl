<#--
 *  Copyright (c) 2005 The Open For Business Project - www.ofbiz.org
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
 *@author     Si Chen (sichen@sinfoniasolutions.com)
-->
<TABLE border="0" width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Current&nbsp;Customer</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <form method="post" action="<@ofbizUrl>setcustselect/${donePage}</@ofbizUrl>" style="margin: 0;">
        <select name="party_id" class="selectBox">
          <#if parties?has_content>
            <option value="">Select Customer</option>
            <#list parties as partyMap>
              <option value="${partyMap.party.partyId}"
                <#if partyMap.party.partyId.equals(rep_cust)>
                  SELECTED
                </#if>
              >
              <#if partyMap.person?has_content>
                ${partyMap.person.lastName?if_exists}
                <#if partyMap.person.firstName?has_content>, ${partyMap.person.firstName}</#if>
              <#elseif partyMap.group?has_content>
                ${partyMap.group.groupName}
              </#if>
              </option>
            </#list>
          </#if>
        </select>
        <input type="submit" value="Select" class="smallSubmit"/>
      </form>
    </TD>
  </TR>
</TABLE>