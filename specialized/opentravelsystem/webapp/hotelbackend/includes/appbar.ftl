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
 *@version    $Revision$
 *@since      3.0
-->
<#if (requestAttributes.uiLabelMap)?exists>	<#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.catalogId)?exists>	<#assign catalogId = requestAttributes.catalogId>
	<#else><#assign catalogId = "">
	</#if>
<#if requestAttributes.security?exists><#assign security = requestAttributes.security></#if>
<#if requestAttributes.externalLoginKey?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey></#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?if_exists>
<#assign contextPath = request.getContextPath()>

<#if requestAttributes.userLogin?has_content && userLogin.partyId != "admin">
	<#assign displayApps = [
 		{"title":"Product",				"url":"mainCatalog"}
		,{"title":"Reservations",	"url":"findorders"}
		,{"title":"Parties",			"url":"findParties?statusId=PARTYREL-ACTIVE"}
		,{"title":"Accounting",			"url":"mainAccounting"}
		,{"title":"Website", 			"url":"findWebsiteContent"}
		,{"title":"Logout", 			"url":"logout"}
		]>
<#elseif requestAttributes.userLogin?has_content && userLogin.partyId == "admin">
	<#assign displayApps = [
 		{"title":"Product",				"url":"mainCatalog"}
		,{"title":"Reservations",	"url":"findorders"}
		,{"title":"Parties",			"url":"findParties?statusId=PARTYREL-ACTIVE"}
		,{"title":"Accounting",			"url":"mainAccounting"}
		,{"title":"Website", 			"url":"findWebsiteContent"}
		,{"title":"SysAdmin", 			"url":"sysAdminMain"}
		,{"title":"Logout", 			"url":"logout"}
		]>
</#if>

<script language="JavaScript">
function ChangeClass(tab,toclass) {
document.getElementById(tab).className = toclass;
}
function ReturnClass(tab,toclass) {
document.getElementById(tab).className= toclass;
}

</script>

<#assign unselectedClass = { 
"tab" : "tabdown", 
"tab_mouseover" : "tabdown_mouseover",
"link" : "tablink"}>

<#assign selectedClass = { 
"tab" : "tabup",
"tab_mouseover" : "tabup_mouseover", 
"link" : "tablinkselected"}>

<#if !appTabButtonItem?has_content><#assign appTabButtonItem = page.appTabButtonItem?default("void")></#if>
<#assign thisAppOpt = page.appTabButtonItem?default("void")>

<table class="appbar" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF"> 
   <td class="appbarleft">&nbsp;</td>   
    <td height="21px" width="100%">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
        <tr>
          <#if displayApps?has_content>	
          <#list displayApps as display>
          <#if thisAppOpt == display.title>
            <#assign class = selectedClass>
          <#else>
            <#assign class = unselectedClass>
          </#if>  
          <td>
            <table height="21px" width="120px" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                 <td height="21px" onclick="window.location='${display.url}'"  style="cursor:pointer; cursor:hand" class="${class.tab}" id="${"uiLabelMap.${display.title}"?eval}" onMouseOver="ChangeClass('${"uiLabelMap.${display.title}"?eval}', '${class.tab_mouseover}');" onMouseOut="ChangeClass('${"uiLabelMap.${display.title}"?eval}', '${class.tab}');">${"uiLabelMap.${display.title}"?eval}</td>              
              </tr>                          
            </table>
          </td>
     	  </#list> 
     	  </#if>
      	   <td width="100%">	
      		<table width="100% border="0" cellspacing="0" cellpadding="0">  
          		<tr>             
         			<td width="100%" class="appbarright" height="21px">&nbsp;</td>
         		</tr>
     		</table>
     		</td>		
        </tr>        
      </table>
    </td>
  </tr>
</table>

