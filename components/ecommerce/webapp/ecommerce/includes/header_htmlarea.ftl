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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.1 $
 *@since      2.1
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${(productStore.storeName)?if_exists}: ${page.title?if_exists}</title>
    <script language="javascript" src="<@ofbizContentUrl>/images/selectall.js</@ofbizContentUrl>" type="text/javascript"></script>
    <link rel="stylesheet" href="<@ofbizContentUrl>${(productStore.styleSheet)?default("/images/maincss.css")}</@ofbizContentUrl>" type="text/css">

    <#-- Append CSS for catalog -->
    <#if catalogStyleSheet?exists>
    <link rel="stylesheet" href="${catalogStyleSheet}" type="text/css">
    </#if>
    <#-- Append CSS for tracking codes -->
    <#if sessionAttributes.overrideCss?exists>
        <link rel="stylesheet" href="${sessionAttributes.overrideCss}" type="text/css">
    </#if>
    <#-- Meta tags if defined by the page action -->
    <#if metaDescription?exists>
    <meta name="description" content="${metaDescription}">
    </#if>
    <#if metaKeywords?exists>
    <meta name="keywords" content="${metaKeywords}">
    </#if>
</head>

<body class="ecbody" onLoad="init_all()">

<table border="0" width="100%" cellspacing="0" cellpadding="0" class="headerboxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="headerboxtop">
        <tr>
          <#if !productStore?exists>
            <TD align="center" width="100%"><div class="head2">There is no ProductStore for this WebSite; Check Settings.</div></td>
          </#if>
          <#if sessionAttributes.overrideLogo?exists>
            <TD align=left width="1%"><IMG src="${sessionAttributes.overrideLogo}"></TD>
          <#elseif catalogHeaderLogo?exists>
            <TD align=left width="1%"><IMG src="${catalogHeaderLogo}"></TD>
          <#elseif (productStore.headerLogo)?has_content>
            <td align=left width="1%"><IMG src="<@ofbizContentUrl>${productStore.headerLogo}</@ofbizContentUrl>"></TD>
          </#if>
          <td align=center width="98%" <#if (productStore.headerMiddleBackground)?has_content>background="<@ofbizContentUrl>${productStore.headerMiddleBackground}</@ofbizContentUrl>"</#if> >
              <#if (productStore.title)?exists><span class="headerCompanyName">${productStore.title}</span></#if>
              <#if (productStore.subtitle)?exists><br><span class="headerCompanySubtitle">${productStore.subtitle}</span></#if>
          </td>
          <td align=right width="1%" nowrap <#if (productStore.headerRightBackground)?has_content>background="<@ofbizContentUrl>${productStore.headerRightBackground}</@ofbizContentUrl>"</#if> >
            ${pages.get("/cart/microcart.ftl")}
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="headerboxbottom">
        <tr>
          <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
              <td class="headerButtonLeft" nowrap><a href="<@ofbizUrl>/logout</@ofbizUrl>" class="headerbuttontext">${uiLabelMap.CommonLogout}</a></td>
          <#else>
              <td class="headerButtonLeft" nowrap><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>" class="headerbuttontext">${uiLabelMap.CommonLogin}</a></td>
          </#if>
          <td class="headerButtonLeft" nowrap><a href="<@ofbizUrl>/contactus</@ofbizUrl>" class="headerbuttontext">Contact Us</a></td>
          <td class="headerButtonLeft" nowrap><a href="<@ofbizUrl>/main</@ofbizUrl>" class="headerbuttontext">${uiLabelMap.CommonMain}</a></td>

          <#if sessionAttributes.autoName?has_content>
              <td width="90%" align="center" class="headerCenter">
                ${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName}!
                (${uiLabelMap.CommonNotYou}?&nbsp;<a href="<@ofbizUrl>/autoLogout</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonClickHere}</a>)
              </td>
          <#else>
              <td width="90%" align=center class="headerCenter">${uiLabelMap.CommonWelcome}!</TD>
          </#if>

          <#if catalogQuickaddUse>
            <td class="headerButtonRight" nowrap><a href="<@ofbizUrl>/quickadd</@ofbizUrl>" class="headerbuttontext">${uiLabelMap.CommonQuickAdd}</a></td>
          </#if>
          <td class="headerButtonRight" nowrap><a href="<@ofbizUrl>/orderhistory</@ofbizUrl>" class="headerbuttontext">${uiLabelMap.OrderHistory}</a></td>
          <td class="headerButtonRight" nowrap><a href="<@ofbizUrl>/editShoppingList</@ofbizUrl>" class="headerbuttontext">${uiLabelMap.EcommerceShoppingLists}</a></td>
          <td class="headerButtonRight" nowrap><a href="<@ofbizUrl>/viewprofile</@ofbizUrl>" class="headerbuttontext">${uiLabelMap.CommonProfile}</a></td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<br>

