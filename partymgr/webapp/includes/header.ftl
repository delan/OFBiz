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
 *@version    $Revision$
 *@since      2.1
-->

<#assign security = requestAttributes.security>
<#assign unselectedLeftClassName = "headerButtonLeft">
<#assign unselectedRightClassName = "headerButtonRight">
<#assign selectedLeftClassMap = {page.headerItem?default("void") : "headerButtonLeftSelected"}>
<#assign selectedRightClassMap = {page.headerItem?default("void") : "headerButtonRightSelected"}>

<div class="apptitle">&nbsp;Party Manager Application&nbsp;</div>
<div class="row">
  <div class="col"><a href="<@ofbizUrl>/main</@ofbizUrl>" class="${selectedLeftClassMap.main?default(unselectedLeftClassName)}">Main</a></div>   
  <div class="col"><a href="<@ofbizUrl>/findparty</@ofbizUrl>" class="${selectedLeftClassMap.find?default(unselectedLeftClassName)}">Find&nbsp;Party</a></div>
  <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
  <div class="col"><a href="<@ofbizUrl>/editpartygroup?create_new=Y</@ofbizUrl>" class="${selectedLeftClassMap.newGroup?default(unselectedLeftClassName)}">New&nbsp;Group</a></div>
  </#if>
  <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
  <div class="col"><a href="<@ofbizUrl>/editperson?create_new=Y</@ofbizUrl>" class="${selectedLeftClassMap.newPerson?default(unselectedLeftClassName)}">New&nbsp;Person</a></div>
  </#if>
  <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
  <div class="col"><a href="<@ofbizUrl>/newcustomer</@ofbizUrl>" class="${selectedLeftClassMap.newCustomer?default(unselectedLeftClassName)}">New&nbsp;Customer</a></div>
  </#if>
  <div class="col"><a href="<@ofbizUrl>/showvisits</@ofbizUrl>" class="${selectedLeftClassMap.visits?default(unselectedLeftClassName)}">Visits</a></div>
  <#if security.hasEntityPermission("SECURITY", "_VIEW", session)>
  <div class="col"><a href="<@ofbizUrl>/FindSecurityGroup</@ofbizUrl>" class="${selectedLeftClassMap.security?default(unselectedLeftClassName)}">Security</a></div>
  </#if>    
  <#if userLogin?has_content>
  	<div class="col-right"><a href="<@ofbizUrl>/logout</@ofbizUrl>" class="${unselectedRightClassName}">Logout</a></div>
  <#else>
    <div class="col-right"><a href='<@ofbizUrl>${requestAttributes.checkLoginUrl?if_exists}</@ofbizUrl>' class='${selectedRightClassMap.login?default(unselectedRightClassName)}'>Login</a></div>
  </#if>  
  <div class="col-fill">&nbsp;</div>
</div>
