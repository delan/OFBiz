<#--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     Johan Isacsson (conversion of jsp created by David E. Jones)
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap) 
 *@created    May 13 2003
 *@version    1.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>${uiLabelMap.WorkEffortMyRequestList}</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<@ofbizUrl>/requestlist</@ofbizUrl>' class='submenutextdisabled'>${uiLabelMap.WorkEffortRequestList}</A><A href='<@ofbizUrl>/request</@ofbizUrl>' class='submenutextright'>${uiLabelMap.WorkEffortNewRequest}</A>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
        <#if custRequestAndRoles?has_content> 
        <TR>
          <TD><DIV class='tabletext'><b>${uiLabelMap.WorkEffortPriority}</b></DIV></TD>
          <TD><DIV class='tabletext'><b>${uiLabelMap.WorkEffortRequest}</b></DIV></TD>
          <TD><DIV class='tabletext'><b>${uiLabelMap.WorkEffortResponseRequiredBy}</b></DIV></TD>
          <TD><DIV class='tabletext'><b>${uiLabelMap.WorkEffortStatus}</b></DIV></TD>
          <#-- <TD><DIV class='tabletext'><b>${uiLabelMap.PartyPartyId}</b></DIV></TD> -->
          <TD><DIV class='tabletext'><b>${uiLabelMap.PartyRoleId}</b></DIV></TD>
          <TD><DIV class='tabletext'><b>${uiLabelMap.WorkEffortRequestName}</b></DIV></TD>
          <TD align=right><DIV class='tabletext'><b>${uiLabelMap.CommonEdit}</b></DIV></TD>
        </TR>
        <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
        <#list custRequestAndRoles as custRequestAndRole>          
          <TR>
            <TD><DIV class='tabletext'>${custRequestAndRole.priority?if_exists}</DIV></TD>
            <TD><DIV class='tabletext'>${custRequestAndRole.custRequestDate?if_exists}</DIV></TD>
            <TD><DIV class='tabletext'>${custRequestAndRole.responseRequiredDate?if_exists}</DIV></TD>
            <TD><DIV class='tabletext'>${delegator.findByPrimaryKeyCache("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", custRequestAndRole.getString("statusId"))).getString("description")?if_exists}</DIV></TD>
            <#-- <TD><DIV class='tabletext'>${custRequestAndRole.partyId}</DIV></TD> -->
            <TD><DIV class='tabletext'>${delegator.findByPrimaryKeyCache("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", custRequestAndRole.getString("roleTypeId"))).getString("description")?if_exists}</DIV></TD>
            <TD><A class='buttontext' href='<@ofbizUrl>/request?custRequestId=${custRequestAndRole.custRequestId?if_exists}</@ofbizUrl>'>
            ${custRequestAndRole.custRequestName}</a></DIV></TD>
            <TD align=right><A class='buttontext' href='<@ofbizUrl>/request?custRequestId=${custRequestAndRole.custRequestId?if_exists}</@ofbizUrl>'>
              ${uiLabelMap.CommonEdit};[${custRequestAndRole.custRequestId?if_exists}]</a></DIV></TD>
          </TR>
        </#list>        
        <#else>
        <TR>
          <TD><div class="tabletext">&nbsp;<b>${uiLabelMap.WorkEffortNoRequestFound}.</b></div></TD>
        </TR>
        </#if>
      </TABLE>
    </TD>
  </TR>
</TABLE>
