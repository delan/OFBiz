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
 *@author	  Hans Bakker (hb@opentravelsystem.org) Modified to create webapp application bar.
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.4-OTS1 $
 *@since      3.0
-->
<#if (requestAttributes.configMap)?exists>
	<#assign configMap = requestAttributes.configMap>
	<#assign productStoreId = configMap.productStoreId>
<#else>
	<#assign productStoreId = "HotelDemoStore">
</#if>
<#if (requestAttributes.uiLabelMap)?exists>	<#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.catalogId)?exists>	<#assign catalogId = requestAttributes.catalogId>
	<#else><#assign catalogId = "">
	</#if>
<#if requestAttributes.security?exists><#assign security = requestAttributes.security></#if>
<#if requestAttributes.externalLoginKey?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey></#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?if_exists>
<#assign contextPath = request.getContextPath()>

<#if requestAttributes.userLogin?has_content>
	<#assign displayApps = [
		 {"title":"Main", 				"url":"main"}
 		,{"title":"Product",				"url":"mainCatalog"}
<#-- 		,{"title":"Assets",				"url":"ListFixedAssets?parentFixedAssetId=${productStoreId}"}-->
		,{"title":"Reservations",		"url":"findorders?productStoreId=${productStoreId}"}
		,{"title":"Customers",			"url":"findparty?productStoreId=${productStoreId}&productStoreRole=ADMIN"}
		,{"title":"Settings",				"url":"mainSettings"}
<#--		,{"title":"Profile",				"url":"EditProductStore?productStoreId=${productStoreId}"}-->
		,{"title":"Website", 			"url":"http://127.0.0.1:8080/frontend?productStoreId=${productStoreId}"}
		,{"title":"Logout", 				"url":"logout"}
		]>
<#else>
	<#assign displayApps = [
		{"title":"Website", 			"url":"http://127.0.0.1:8080/frontend?productStoreId=${productStoreId}"}
		,{"title":"Login",				"url":"checkLogin/main"}
		]>
</#if>
<#assign unselectedClass = {"col" : "tabdownblock", "left" : "tabdownleft", "center" : "tabdowncenter", "right" : "tabdownright", "link" : "tablink"}>
<#assign selectedClass = {"col" : "mainblock", "left" : "tabupleft", "center" : "tabupcenter", "right" : "tabupright", "link" : "tablinkselected"}>
<#assign thisAppOpt = page.appTabButtonItem?default("void")>

<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">    
    <td><div class="appbarleft"></div></td>
    <td height="15" width="100%">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
        <tr>
          <#list displayApps as display>
	          <#if thisAppOpt == display.title>
	            <#assign class = selectedClass>
	          <#else>
	            <#assign class = unselectedClass>
	          </#if>
	          <td height="15" class="${class.col}">
	            <table width="100%" border="0" cellspacing="0" cellpadding="0">
	              <tr>
	                <td class="${class.left}"><a href="<@ofbizUrl>${display.url}</@ofbizUrl>" title="" class="${class.link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
	                <td nowrap="nowrap" class="${class.center}"><a href=<#if display.title == "Website">"${display.url}" target="newwindow"<#else>"<@ofbizUrl>${display.url}</@ofbizUrl>"</#if> title="" class="${class.link}">${"uiLabelMap.opentravelsystem${display.title}"?eval}</a></td>
	                <td class="${class.right}"><a href="<@ofbizUrl>${display.url}</@ofbizUrl>" title="" class="${class.link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>"alt="" width="10" height="15" border="0"></a></td></tr>
				  <#if thisAppOpt != display.title>
	                <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
	                <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
	              </#if>
	            </table>
	          </td>
          </#list>
		  <td><div class="appbarright"></div></td>            
          <td width="100%" class="appbarresize">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr><td class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>           
        </tr>        
      </table>
    </td>
  </tr>
</table>

