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
 *@created    May 13 2003
 *@version    1.0
-->
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;My Request List</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<@ofbizUrl>/requestlist</@ofbizUrl>' class='lightbuttontextdisabled'>[Request&nbsp;List]</A>
            <A href='<@ofbizUrl>/request</@ofbizUrl>' class='lightbuttontext'>[New&nbsp;Request]</A>
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
          <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
          <TD><DIV class='tabletext'><b>Request</b></DIV></TD>
          <TD><DIV class='tabletext'><b>Response Required By</b></DIV></TD>
          <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
          <#-- <TD><DIV class='tabletext'><b>Party&nbsp;ID</b></DIV></TD> -->
          <TD><DIV class='tabletext'><b>Role&nbsp;ID</b></DIV></TD>
          <TD><DIV class='tabletext'><b>Request&nbsp;Name</b></DIV></TD>
          <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
        </TR>
        <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
        <#list custRequestAndRoles as custRequestAndRole>          
          <TR>
            <TD><DIV class='tabletext'>${custRequestAndRole.priority?if_exists}</DIV></TD>
            <TD><DIV class='tabletext'>${custRequestAndRole.custRequestDate?if_exists}</DIV></TD>
            <TD><DIV class='tabletext'>${custRequestAndRole.responseRequiredDate?if_exists}</DIV></TD>
            <TD><DIV class='tabletext'>${delegator.findByPrimaryKeyCache("StatusItem", Static["org.ofbiz.core.util.UtilMisc"].toMap("statusId", custRequestAndRole.getString("statusId"))).getString("description")?if_exists}</DIV></TD>
            <#-- <TD><DIV class='tabletext'>${custRequestAndRole.partyId}</DIV></TD> -->
            <TD><DIV class='tabletext'>${delegator.findByPrimaryKeyCache("RoleType", Static["org.ofbiz.core.util.UtilMisc"].toMap("roleTypeId", custRequestAndRole.getString("roleTypeId"))).getString("description")?if_exists}</DIV></TD>
            <TD><A class='buttontext' href='<@ofbizUrl>/request?custRequestId=${custRequestAndRole.custRequestId?if_exists}</@ofbizUrl>'>
            ${custRequestAndRole.custRequestName}</a></DIV></TD>
            <TD align=right><A class='buttontext' href='<@ofbizUrl>/request?custRequestId=${custRequestAndRole.custRequestId?if_exists}</@ofbizUrl>'>
              Edit&nbsp;[${custRequestAndRole.custRequestId?if_exists}]</a></DIV></TD>
          </TR>
        </#list>        
        <#else>
        <TR>
          <TD><div class="tabletext">&nbsp;<b>No requests found.</b></div></TD>
        </TR>
        </#if>
      </TABLE>
    </TD>
  </TR>
</TABLE>
