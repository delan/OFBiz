<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.security)?exists><#assign security = requestAttributes.security></#if>
<#if (requestAttributes.userLogin)?exists><#assign userLogin = requestAttributes.userLogin></#if>
<#if (requestAttributes.checkLoginUrl)?exists><#assign checkLoginUrl = requestAttributes.checkLoginUrl></#if>



	<#assign displayApps = [
 		{"title":"${uiLabelMap.opentravelsystemMain}",		"tabname":"main",		"url":"mainCatalog"}
		,{"title":"${uiLabelMap.opentravelsystemCategories}","tabname":"categories",	"url":"EditProdCatalogCategories"}
		,{"title":"${uiLabelMap.opentravelsystemProducts}",		"tabname":"products",	"url":"EditProducts"}
		,{"title":"${uiLabelMap.opentravelsystemFixedAssets}","tabname":"fixedassets",			"url":"EditFixedAssets"}
		,{"title":"${uiLabelMap.opentravelsystemStore}", 	"tabname":"store",		"url":"EditProductStore"}
		,{"title":"Produkt&nbsp;Upload", 	"tabname":"importProduct",		"url":"importProduct"}
		]>

<#assign unselectedClass = {"appheader_col" : "appheader_tabdownblock", "appheader_center" : "appheader_tabdowncenter", "appheader_link" : "appheader_tablink"}>
<#assign selectedClass = {"appheader_col" : "appheader_mainblock", "appheader_center" : "appheader_tabupcenter", "appheader_link" : "appheader_tablinkselected"}>
<#if !appTabButtonItem?has_content><#assign appTabButtonItem = page.appTabButtonItem?default("void")></#if>
<#assign thisAppOpt = page.headerItem?default("void")>

<#if requestAttributes.userLogin?has_content>
<div class="apptitle">&nbsp;${uiLabelMap.opentravelsystemCatalog}</div>
<table class="appheader_appbar" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#f9f4a2">    
    <td width="5px" class="appheader_appbarleft">&nbsp;</td>  
    <td height="19px" width="100%">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
        <tr>
          <#list displayApps as display>
	          <#if thisAppOpt == display.tabname>
	            <#assign class = selectedClass>
	          <#else>
	            <#assign class = unselectedClass>
	          </#if>
	          <td valign="bottom" height="19px" class="${class.appheader_col}">
	            <table width="100%" border="0" cellspacing="0" cellpadding="0">
	              <tr>
	                <td class="${class.appheader_center}"><a href=<#if display.title == "Website">"${display.url}" target="newwindow"<#else>"<@ofbizUrl>${display.url}</@ofbizUrl>"</#if> title="" class="${class.appheader_link}">${display.title}</a></td>
	                <td class="appheader_betweenstabs" width="1px">&nbsp;</td>					
					</tr>
	            </table>
	          </td>
          </#list>
      	   <td width="100%">	
      		<table width="100% border="0" cellspacing="0" cellpadding="0">  
          		<tr>             
         			<td width="100%" class="appheader_appbarright" height="21px">&nbsp;</td>
         		</tr>
     		</table>
     		</td>           
        </tr>        
      </table>
    </td>
  </tr>
</table>
</#if>