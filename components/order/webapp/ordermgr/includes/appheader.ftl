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
 *@version    $Rev$
 *@since      2.1
-->

<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.security)?exists><#assign security = requestAttributes.security></#if>
<#if (requestAttributes.userLogin)?exists><#assign userLogin = requestAttributes.userLogin></#if>
<#if (requestAttributes.checkLoginUrl)?exists><#assign checkLoginUrl = requestAttributes.checkLoginUrl></#if>

<#assign unselectedLeftClassName = "headerButtonLeft">
<#assign unselectedRightClassName = "headerButtonRight">
<#assign selectedLeftClassMap = {page.headerItem?default("void") : "headerButtonLeftSelected"}>
<#assign selectedRightClassMap = {page.headerItem?default("void") : "headerButtonRightSelected"}>

<div class="apptitle">&nbsp;Order Manager Application&nbsp;</div>
<div class="row">
  <div class="col"><a href="<@ofbizUrl>/main</@ofbizUrl>" class="${selectedLeftClassMap.main?default(unselectedLeftClassName)}">${uiLabelMap.CommonMain}</a></div>
  <#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session) || security.hasRolePermission("ORDERMGR_ROLE", "_VIEW", "", "", session)>
  <div class="col"><a href="<@ofbizUrl>/tasklist</@ofbizUrl>" class="${selectedLeftClassMap.orderlist?default(unselectedLeftClassName)}">${uiLabelMap.OrderOrderList}</a></div>
  </#if>
  <#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>
  <div class="col"><a href="<@ofbizUrl>/findorders</@ofbizUrl>" class="${selectedLeftClassMap.findorders?default(unselectedLeftClassName)}">${uiLabelMap.CommonFind}</a></div>  
  </#if>
  <#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
  <div class="col"><a href="<@ofbizUrl>/orderentry</@ofbizUrl>" class="${selectedLeftClassMap.orderentry?default(unselectedLeftClassName)}">${uiLabelMap.OrderOrderEntry}</a></div>  
  </#if>
  <#if security.hasEntityPermission("ORDERMGR", "_RETURN", session)>
  <div class="col"><a href="<@ofbizUrl>/findreturn</@ofbizUrl>" class="${selectedLeftClassMap.return?default(unselectedLeftClassName)}">${uiLabelMap.OrderOrderReturns}</a></div>
  </#if>

  <#if requestAttributes.userLogin?has_content>
    <div class="col-right"><a href="<@ofbizUrl>/logout</@ofbizUrl>" class="${selectedRightClassMap.logout?default(unselectedRightClassName)}">${uiLabelMap.CommonLogout}</a></div>
  <#else>
    <div class="col-right"><a href='<@ofbizUrl>${requestAttributes.checkLoginUrl?if_exists}</@ofbizUrl>' class='${selectedRightClassMap.login?default(unselectedRightClassName)}'>Login</a></div>
  </#if>  
  <div class="col-right"><a href='<@ofbizUrl>/orderreportlist</@ofbizUrl>' class="${selectedRightClassMap.reports?default(unselectedRightClassName)}">${uiLabelMap.CommonReports}</a></div>
  <div class="col-right"><a href='<@ofbizUrl>/orderstats</@ofbizUrl>' class="${selectedRightClassMap.stats?default(unselectedRightClassName)}">${uiLabelMap.CommonStats}</a></div>
  <div class="col-fill">&nbsp;</div>
</div>
