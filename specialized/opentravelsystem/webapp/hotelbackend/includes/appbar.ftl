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

<#if requestAttributes.userLogin?has_content>
	<#assign displayApps = [
 		{"title":"ProductManagement",				"url":"mainCatalog"}
		,{"title":"OrdersReservationsManagement",	"url":"findorders"}
		,{"title":"PartiesManagement",			"url":"findParties?statusId=PARTYREL-ACTIVE"}
		,{"title":"AccountingManagement",			"url":"mainAccounting"}
		,{"title":"WebsiteManagement", 			"url":"findElectronicTexts"}
		,{"title":"Logout", 			"url":"logout"}
		]>
<#else>
	<#assign displayApps = [
		{"title":"Login",				"url":"checkLogin/main"}
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
"tab" : "tabdowncenter", 
"link" : "tablink"}>

<#assign selectedClass = { 
"tab" : "tabupcenter",
"link" : "tablinkselected"}>

<#if !appTabButtonItem?has_content><#assign appTabButtonItem = page.appTabButtonItem?default("void")></#if>
<#assign thisAppOpt = page.appTabButtonItem?default("void")>

<table class="appbar" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF"> 
   <td width="5px" class="appbarleft">&nbsp;</td>   
    <td height="21px" width="100%">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
        <tr>
          <#list displayApps as display>
          <#if thisAppOpt == display.title>
            <#assign class = selectedClass>
          <#else>
            <#assign class = unselectedClass>
          </#if>
            <#--DEZE SYNTAX MOET BETER KUNNEN< LIJKT ME--> 
        	<#if requestAttributes.userLogin?has_content>
        	
        	<#else>
        		<#assign class = selectedClass>
        	</#if>
          <td width="107px">
            <table height="21px" width="107px" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                 <td width="107px" height="21px" class="${class.tab}" id="${"uiLabelMap.opentravelsystem${display.title}"?eval}" onMouseOver="ChangeClass('${"uiLabelMap.opentravelsystem${display.title}"?eval}', '${class.tab}');" onMouseOut="ChangeClass('${"uiLabelMap.opentravelsystem${display.title}"?eval}', '${class.tab}');"><a href=<#if display.title == "Website">"${display.url}" target="newwindow"<#else>"<@ofbizUrl>${display.url}</@ofbizUrl>"</#if> title="" class="${class.link}">${"uiLabelMap.opentravelsystem${display.title}"?eval}</a></td>
              	 <td height="1px" class="betweenstabs">&nbsp;</td>  
              </tr>
                            
            </table>
          </td>
     	  </#list> 
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

