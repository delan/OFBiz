<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Rev: 4791 $
 *@since      2.1
-->
<#assign includeHtmlArea=false/>
<#if "Y"=page.includeHtmlArea?if_exists><#assign includeHtmlArea=true/></#if>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<#-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> <html> -->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>${(productStore.storeName)?if_exists}: ${page.title?if_exists}</title>
    <script language="javascript" src="<@ofbizContentUrl>/images/selectall.js</@ofbizContentUrl>" type="text/javascript"></script>
    <script language="javascript" src="<@ofbizContentUrl>/images/calendar1.js</@ofbizContentUrl>" type="text/javascript"></script>
    <script language="javascript" src="<@ofbizContentUrl>/images/fieldlookup.js</@ofbizContentUrl>" type="text/javascript"></script>
    <link rel="stylesheet" href="/hotelfrontendimages/ecommain.css" type="text/css"/>
    <!--link rel="stylesheet" href="<@ofbizContentUrl>${(productStore.styleSheet)?default("/images/ecommain.css")}</@ofbizContentUrl>" type="text/css"/-->
    <link rel="stylesheet" href="/hotelfrontendimages/frontend.css" type="text/css"/>

    <#-- Append CSS for catalog -->
    <#if catalogStyleSheet?exists>
        <link rel="stylesheet" href="${catalogStyleSheet}" type="text/css"/>
    </#if>
    <#-- Append CSS for tracking codes -->
    <#if sessionAttributes.overrideCss?exists>
        <link rel="stylesheet" href="${sessionAttributes.overrideCss}" type="text/css"/>
    </#if>
    <#-- Meta tags if defined by the page action -->
    <#if metaDescription?exists>
        <meta name="description" content="${metaDescription}"/>
    </#if>
    <#if metaKeywords?exists>
        <meta name="keywords" content="${metaKeywords}"/>
    </#if>

    <#if includeHtmlArea>    
        <#assign contextPath=request.getContextPath()/>
        <link rel="stylesheet" href="<@ofbizContentUrl>${contextPath}/images/css/${(webSitePublishPoint.styleSheetFile)?if_exists}</@ofbizContentUrl>" type="text/css"/>
        
        <#assign primaryHTMLField=page.primaryHTMLField?if_exists/>
        <#if (dynamicPrimaryHTMLField?exists)>
          <#assign primaryHTMLField=dynamicPrimaryHTMLField/>
        </#if>
        <#assign secondaryHTMLField=page.secondaryHTMLField?if_exists/>
        <#if (primaryHTMLField?exists && (primaryHTMLField?length > 0))>
            <script type="text/javascript" language="javascript"> 
              _editor_url = "/content/images/htmlarea/"; // omit the final slash
            </script> 
        
            <script language="javascript" src="<@ofbizContentUrl>/content/images/htmlarea/htmlarea.js</@ofbizContentUrl>" type="text/javascript"></script>
            <script language="javascript" src="<@ofbizContentUrl>/content/images/htmlarea/lang/en.js</@ofbizContentUrl>" type="text/javascript"></script>
            <script language="javascript" src="<@ofbizContentUrl>/content/images/htmlarea/dialog.js</@ofbizContentUrl>" type="text/javascript"></script>
            <script language="javascript" src="<@ofbizContentUrl>/content/images/htmlarea/popupwin.js</@ofbizContentUrl>" type="text/javascript"></script>
            <style type="text/css">
                @import url(<@ofbizContentUrl>/content/images/htmlarea/htmlarea.css</@ofbizContentUrl>);
                textarea { background-color: #fff; border: 1px solid 00f; }
            </style>
            <script type="text/javascript">
                var editor = null;
                var summary = null;
                function init_all() {
                    primaryHTMLArea = new HTMLArea("${primaryHTMLField}"); primaryHTMLArea.generate();
                    <#if secondaryHTMLField?exists>
                        secondaryHTMLArea = new HTMLArea("${secondaryHTMLField}"); secondaryHTMLArea.generate();
                    </#if>
                }
            </script>
        </#if>
    </#if>
</head>

<body<#if includeHtmlArea> onLoad="init_all()"</#if>>
<#--
<div id="ecom-header">
    <div id="left">
        <#if sessionAttributes.overrideLogo?exists>
            <img src="${sessionAttributes.overrideLogo}"/>
        <#elseif catalogHeaderLogo?exists>
            <imh src="${catalogHeaderLogo}"/>
        <#elseif (productStore.headerLogo)?has_content>
            <img src="<@ofbizContentUrl>${productStore.headerLogo}</@ofbizContentUrl>"/>
        </#if>
    </div>
    <div id="right"<#if (productStore.headerRightBackground)?has_content> style="background-image: <@ofbizContentUrl>${productStore.headerRightBackground}</@ofbizContentUrl>;"</#if>>
        ${screens.render("component://ecommerce/widget/CartScreens.xml#microcart")}
    </div>
    <div id="middle"<#if (productStore.headerMiddleBackground)?has_content> style="background-image: <@ofbizContentUrl>${productStore.headerMiddleBackground}</@ofbizContentUrl>;"</#if>>
        <#if !productStore?exists>
            <div class="head2">There is no ProductStore for this WebSite; Check Settings.</div>
        </#if>
        <#if (productStore.title)?exists><div id="company-name">${productStore.title}</div></#if>
        <#if (productStore.subtitle)?exists><div id="company-subtitle">${productStore.subtitle}</div></#if>
        <div id="welcome-message">
            <#if sessionAttributes.autoName?has_content>
                ${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName}!
                (${uiLabelMap.CommonNotYou}?&nbsp;<a href="<@ofbizUrl>autoLogout</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonClickHere}</a>)
            <#else/>
                ${uiLabelMap.CommonWelcome}!
            </#if>
        </div>
    </div>
</div>

<div id="ecom-header-bar">
    <ul id="left-links">
        <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
            <li id="header-bar-logout"><a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a></li>
        <#else/>
            <li id="header-bar-login"><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a></li>
        </#if>
        <li id="header-bar-contactus"><a href="<@ofbizUrl>contactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a></li>
        <li id="header-bar-main"><a href="<@ofbizUrl>main</@ofbizUrl>">${uiLabelMap.CommonMain}</a></li>
    </ul>
    <ul id="right-links">
        <!-- NOTE: these are in reverse order because they are stacked right to left instead of left to right -->
<#--        <li id="header-bar-viewprofile"><a href="<@ofbizUrl>viewprofile</@ofbizUrl>">${uiLabelMap.CommonProfile}</a></li>
        <li id="header-bar-editShoppingList"><a href="<@ofbizUrl>editShoppingList</@ofbizUrl>">${uiLabelMap.EcommerceShoppingLists}</a></li>
        <li id="header-bar-orderhistory"><a href="<@ofbizUrl>orderhistory</@ofbizUrl>">${uiLabelMap.OrderHistory}</a></li>
        <#if catalogQuickaddUse>
            <li id="header-bar-quickadd"><a href="<@ofbizUrl>quickadd</@ofbizUrl>">${uiLabelMap.CommonQuickAdd}</a></li>
        </#if>
    </ul>
</div>
-->