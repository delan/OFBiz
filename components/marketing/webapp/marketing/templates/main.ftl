<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- Copyright (c) 2003 The Open For Business Project - www.ofbiz.org -->
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
 *@version    $Revision: 1.3 $
 *@since      2.1
-->

<#assign layoutSettings = requestAttributes.layoutSettings>
<html>
<head>
    <#assign layoutSettings = requestAttributes.layoutSettings>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${layoutSettings.companyName}: ${page.title}</title>
    <script language='javascript' src='<@ofbizContentUrl>/images/calendar1.js</@ofbizContentUrl>' type='text/javascript'></script>
    <link rel='stylesheet' href='<@ofbizContentUrl>/images/maincss.css</@ofbizContentUrl>' type='text/css'>
    <link rel='stylesheet' href='<@ofbizContentUrl>/images/tabstyles.css</@ofbizContentUrl>' type='text/css'>    
</head>

<body>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
          <#if layoutSettings.headerImageUrl?exists>
          <td align=left width='1%'><img alt="${layoutSettings.companyName}" src='<@ofbizContentUrl>${layoutSettings.headerImageUrl}</@ofbizContentUrl>'></td>
          </#if>       
          <td align='right' width='1%' nowrap <#if layoutSettings.headerRightBackgroundUrl?has_content>background='${layoutSettings.headerRightBackgroundUrl}'</#if>>
            <#if requestAttributes.person?has_content>
              <div class="insideHeaderText">Welcome&nbsp;${requestAttributes.person.firstName?if_exists}&nbsp;${requestAttributes.person.lastName?if_exists}!</div>
            <#elseif requestAttributes.partyGroup?has_content>
              <div class="insideHeaderText">Welcome&nbsp;${requestAttributes.partyGroup.groupName?if_exists}!</div>
            <#else>
              <div class="insideHeaderText">Welcome!</div>
            </#if>
            <div class="insideHeaderText">&nbsp;${Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()}</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

${pages.get("/includes/appbar.ftl")}

<div class="centerarea">
  ${pages.get("/includes/header.ftl")}
  <div class="contentarea">
    <div style='border: 0; margin: 0; padding: 0; width: 100%;'>
      <table style='border: 0; margin: 0; padding: 0; width: 100%;' cellpadding='0' cellspacing='0'>
        <tr>
          <#if page.leftbar?exists>${pages.get(page.leftbar)}</#if>
          <td width='100%' valign='top' align='left'>
            ${common.get("/includes/errormsg.ftl")}
            ${pages.get(page.path)}
          </td>
          <#if page.rightbar?exists>${pages.get(page.rightbar)}</#if>
        </tr>
      </table>       
    </div>
    <div class='spacer'></div>
  </div>
</div>

${pages.get("/includes/footer.ftl")}

</body>
</html>
