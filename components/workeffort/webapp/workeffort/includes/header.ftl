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
 *@version    $Revision: 1.1 $
 *@since      2.1
-->

<#assign security = requestAttributes.security>
<#assign unselectedLeftClassName = "headerButtonLeft">
<#assign unselectedRightClassName = "headerButtonRight">
<#assign selectedLeftClassMap = {page.headerItem?default("void") : "headerButtonLeftSelected"}>
<#assign selectedRightClassMap = {page.headerItem?default("void") : "headerButtonRightSelected"}>

<div class="apptitle">&nbsp;Work Effort Manager Application&nbsp;</div>
<div class="row">
  <div class="col"><a href="<@ofbizUrl>/main</@ofbizUrl>" class="${selectedLeftClassMap.main?default(unselectedLeftClassName)}">Main</a></div>   
  <div class="col"><a href="<@ofbizUrl>/mytasks</@ofbizUrl>" class="${selectedLeftClassMap.task?default(unselectedLeftClassName)}">Task&nbsp;List</a></div>
  <div class="col"><a href="<@ofbizUrl>/day</@ofbizUrl>" class="${selectedLeftClassMap.calendar?default(unselectedLeftClassName)}">Calendar</a></div>
  <div class="col"><a href="<@ofbizUrl>/projectlist</@ofbizUrl>" class="${selectedLeftClassMap.project?default(unselectedLeftClassName)}">Projects</a></div>
  <div class="col"><a href="<@ofbizUrl>/requestlist</@ofbizUrl>" class="${selectedLeftClassMap.request?default(unselectedLeftClassName)}">Requests</a></div>  
  <#if userLogin?has_content>
  <div class="col-right"><a href="<@ofbizUrl>/logout</@ofbizUrl>" class="${selectedRightClassMap.logout?default(unselectedRightClassName)}">Logout</a></div>
  <#else>
    <div class="col-right"><a href='<@ofbizUrl>${requestAttributes.checkLoginUrl?if_exists}</@ofbizUrl>' class='${selectedRightClassMap.login?default(unselectedRightClassName)}'>Login</a></div>
  </#if>
  <div class="col-fill">&nbsp;</div>
</div>
