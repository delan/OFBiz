<!-- Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org -->
<#--
 *  Copyright (c) 2003-2004 The Open For Business Project - www.ofbiz.org
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
 *@author     Al Byers (byersa@automationgroups.com)
 *@version    $Revision: 1.3 $
 *@since      3.1
-->
    <#assign contextPath=request.getContextPath()/>
    <script language='javascript' src='<@ofbizContentUrl>/content/images/calendar1.js</@ofbizContentUrl>' type='text/javascript'></script>

    <link rel="stylesheet" href="<@ofbizContentUrl>${contextPath}/images/css/${webSitePublishPoint?if_exists.styleSheetFile?if_exists}</@ofbizContentUrl>" type="text/css"/>

<#assign primaryHTMLField= page.getProperty("primaryHTMLField")?if_exists />
<#if (dynamicPrimaryHTMLField?exists)>
<#assign primaryHTMLField= dynamicPrimaryHTMLField />
</#if>
<#assign secondaryHTMLField= page.getProperty("secondaryHTMLField")?if_exists />
<#if (primaryHTMLField?exists && (primaryHTMLField?length >0))>
    <script type="text/javascript" language="javascript"> 
      _editor_url = "/content/images/htmlarea/"; // omit the final slash 
    </script> 

    <script language='javascript' src='<@ofbizContentUrl>/content/images/htmlarea/htmlarea.js</@ofbizContentUrl>' 
                                                       type='text/javascript'></script>
    <script language='javascript' src='<@ofbizContentUrl>/content/images/htmlarea/lang/en.js</@ofbizContentUrl>' 
                                                       type='text/javascript'></script>
    <script language='javascript' src='<@ofbizContentUrl>/content/images/htmlarea/dialog.js</@ofbizContentUrl>' 
                                                       type='text/javascript'></script>
    <script language='javascript' src='<@ofbizContentUrl>/content/images/htmlarea/popupwin.js</@ofbizContentUrl>' 
                                                       type='text/javascript'></script>
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


${pages.get("/includes/header_htmlarea.ftl")}

<table width='100%' border='0' cellpadding='0' cellspacing='0'>
 <tr>
  ${pages.get("/templates/leftbar.ftl")}
  <td width='100%' valign='top' align='left'>
    ${common.get("/includes/messages.ftl")}
    ${pages.get(page.path)}
  </td>
  ${pages.get("/templates/rightbar.ftl")}
 </tr>
</table>

${pages.get("/includes/footer.ftl")}
