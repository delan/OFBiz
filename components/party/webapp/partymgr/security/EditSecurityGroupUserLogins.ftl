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
 *@author     Olivier Heintz (olivier.heintz@nereide.biz) 
 *@version    $Rev:$
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if security.hasEntityPermission("SECURITY", "_VIEW", session)>
  ${pages.get("/security/SecurityGroupTabBar.ftl")}
  <div class="head1">${uiLabelMap.PartyUserLoginsForSecurityGroup} "${groupId}"</div>
  <a href="<@ofbizUrl>/EditSecurityGroup</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyNewSecurityGroup}]</a>
  <br>
  <#if userLoginSecurityGroups?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align='right'>
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>/EditSecurityGroupUserLogins?groupId=${groupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} of ${listSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>/EditSecurityGroupUserLogins?groupId=${groupId}&VIEW_SIZE${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
            </#if>
          </b>
        </td>
      </tr>
    </table>
  <#else>
    <br>  
  </#if>  
  <table border="1" cellpadding='2' cellspacing='0' width='100%'>
    <tr>
      <td><div class="tabletext"><b>${uiLabelMap.PartyUserLoginId}</b></div></td>
      <td><div class="tabletext"><b>${uiLabelMap.CommonFromDate}</b></div></td>
      <td><div class="tabletext"><b>${uiLabelMap.CommonThruDate}</b></div></td>
      <td><div class="tabletext"><b>${uiLabelMap.PartyPartyId}</b></div></td>
      <td width='1%'><div class="tabletext">&nbsp;</div></td>
    </tr>
    
    <#assign idx = 0>
    <#if userLoginSecurityGroups?has_content>
      <#list userLoginSecurityGroups[lowIndex..highIndex-1] as userLoginSecurityGroup>
        <#assign userlogin = userLoginSecurityGroup.getRelatedOne("UserLogin")?if_exists>
        <#assign idx = idx + 1>
        <tr valign="middle">
          <td>
            &nbsp;<a href='<@ofbizUrl>/editlogin?userLoginId=${userLoginSecurityGroup.userLoginId}</@ofbizUrl>' class='buttontext'>${userLoginSecurityGroup.userLoginId}</a>
          </td>
          <td><div class='tabletext'>&nbsp;${(userLoginSecurityGroup.fromDate?string)?if_exists}</div></td>
          <td>
            <form name='editrec${idx}' action='<@ofbizUrl>/updateUserLoginToSecurityGroup</@ofbizUrl>' method='POST' style='margin: 0;'>
              <input type='hidden' name='userLoginId' value='${userLoginSecurityGroup.userLoginId}'>
              <input type='hidden' name='groupId' value='${groupId}'>
              <input type='hidden' name='fromDate' value='${userLoginSecurityGroup.fromDate?string}'>
              <input type='text' class='inputBox' name='thruDate' size="25" value='${(userLoginSecurityGroup.thruDate?string)?if_exists}'>
              <a href="javascript:call_cal(document.editrec${idx}.thruDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
              <input type='submit' class='smallSubmit' value='${uiLabelMap.CommonUpdate}'>
            </form>
          </td>
          <td><a href='<@ofbizUrl>/viewprofile?partyId=${userLogin.partyId}</@ofbizUrl>' class='buttontext'>${userLogin.partyId}</a></td>
          <td align="center">
            <a href='<@ofbizUrl>/removeUserLoginFromSecurityGroup?userLoginId=${userLoginSecurityGroup.userLoginId}&groupId=${groupId}&fromDate=${userLoginSecurityGroup.fromDate}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonDelete}]</a>
          </td>
        </tr>
      </#list>
    </#if>
  </table>    
  <#if userLoginSecurityGroups?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align='right'>
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>/EditSecurityGroupUserLogins?groupId=${groupId}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} of ${listSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>/EditSecurityGroupUserLogins?groupId=${groupId}&VIEW_SIZE${viewSize}&VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>
            </#if>
          </b>
        </td>
      </tr>
    </table>
  </#if>  
    
  <br>
  <form method="POST" action="<@ofbizUrl>/addUserLoginToSecurityGroup</@ofbizUrl>" style='margin: 0;' name='addUserLoginToSecurityGroupForm'>
    <input type="hidden" name="groupId" value="${groupId}">
    <input type="hidden" name="useValues" value="true">
  
    <div class='head2'>${uiLabelMap.PartyAddUserLoginToSecurityGroup}:</div>
    <table cellpadding="2">
      <tr>
        <td><span class="tableheadtext">${uiLabelMap.PartyUserLoginId}:</span></td>        
        <td><input type='text' class="inputBox" size='30' name='userLoginId'></td>
      </tr>
      <tr>
        <td><span class="tableheadtext">${uiLabelMap.CommonFromDate}:</span>
        <td>
          <input type='text' class="inputBox" size='25' name='fromDate'>
          <a href="javascript:call_cal(document.addUserLoginToSecurityGroupForm.fromDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td><input type="submit" class="smallSubmit" value="${uiLabelMap.CommonAdd}"></td>
      </tr>
    </table>
  </form>    
<#else>
  <h3>${uiLabelMap.PartySecurityViewPermissionError}</h3>
</#if>    
