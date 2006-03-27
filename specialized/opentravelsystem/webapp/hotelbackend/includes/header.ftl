<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org -->
<#--
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
 *@version    $Rev$
 *@since      2.1
-->

<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.layoutSettings)?exists><#assign layoutSettings = requestAttributes.layoutSettings></#if>
<#if (requestAttributes.locale)?exists><#assign locale = requestAttributes.locale></#if>
<#if (requestAttributes.availableLocales)?exists><#assign availableLocales = requestAttributes.availableLocales></#if>
<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#if (requestAttributes.partyGroup)?exists><#assign partyGroup = requestAttributes.partyGroup></#if>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${layoutSettings.companyName?if_exists}: <#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)?if_exists}</#if></title>
    <script language='javascript' src='<@ofbizContentUrl>/images/calendar1.js</@ofbizContentUrl>' type='text/javascript'></script>
    <script language='javascript' src='<@ofbizContentUrl>/images/selectall.js</@ofbizContentUrl>' type='text/javascript'></script>
    <script language="javascript" src="<@ofbizContentUrl>/images/fieldlookup.js</@ofbizContentUrl>" type="text/javascript"></script>
    <link rel='stylesheet' href='<@ofbizContentUrl>/images/maincss.css</@ofbizContentUrl>' type='text/css'>
    <link rel='stylesheet' href='<@ofbizContentUrl>/images/tabstyles.css</@ofbizContentUrl>' type='text/css'>    
	${layoutSettings.extraHead?if_exists}
    <#if htmlEdit?exists>
		<script language="Javascript" type="text/javascript" src="/${activeApp}/html/whizzywig.js"></script>
		<script language="Javascript" type="text/javascript" src="/${activeApp}/html/xhtml.js"></script>
	<script language="JavaScript">
		// wizzywig variables
		buttonPath = "/${activeApp}/html/images/";
		cssFile = "/${activeApp}/html/images/simple.css";
		gentleClean = "true";
	</script>
	</#if>
	<script language="JavaScript">
		function getHelpWindow(url) {
	    win=window.open(url,"helpwin","height=400,width=800,toolbar=no,location=no,scrollbars=yes,directories=no,status=no,menubar=no,resizable=yes");
	    win.focus();
			}
	</script>
</head>

<body>
<#if !parameters.popup?exists>
<table border="0" width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
         <#if userLogin?has_content && productStoreId?exists>
          <td align="left" width='1%'><img alt="${layoutSettings.companyName}" src="<@ofbizContentUrl>${layoutSettings.headerImageUrl}</@ofbizContentUrl>"/></td>
          <#else>
          <td align="left" width='1%'><img alt="Backend system" src="/${activeApp}/html/images/system.jpg"/></td>
          </#if>  
            <#if userLogin?has_content && productStoreId?exists>
				<td class="head2"><center><u>Store Name: ${productStoreId}<br/>Org.Party: ${organizationPartyId?if_exists}</u></center></td/>
			</#if>
          <td align='right' width='1%' nowrap <#if layoutSettings.headerRightBackgroundUrl?has_content>background='${layoutSettings.headerRightBackgroundUrl}'</#if>>
            <#if person?has_content>
              <div class="insideHeaderText">${uiLabelMap.CommonWelcome}&nbsp;${person.firstName?if_exists}&nbsp;${person.lastName?if_exists}!</div>
            <#elseif partyGroup?has_content>
              <div class="insideHeaderText">${uiLabelMap.CommonWelcome}&nbsp;${partyGroup.groupName?if_exists}!</div>
            <#else>
              <div class="insideHeaderText">${uiLabelMap.CommonWelcome}!</div>
            </#if>
            <div class="insideHeaderText">&nbsp;${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()}</div>
            <div class="insideHeaderText">
                <form method="post" action="<@ofbizUrl>setSessionLocale</@ofbizUrl>" style="margin: 0;">
                  <select name="locale" class="selectBox">
                    <option value="${locale}">${locale.getDisplayName(locale)}</option>
                    <option value="${locale}">----</option>
                    <#list availableLocales as availableLocale>
                        <option value="${availableLocale.toString()}">${availableLocale.getDisplayName(locale)}</option>
                    </#list>
                  </select>
                  <input type="submit" value="${uiLabelMap.CommonSet}" class="smallSubmit"/>
                </form>
            </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</#if>

