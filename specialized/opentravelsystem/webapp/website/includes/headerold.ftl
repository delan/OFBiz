<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org -->
<#--
 *  Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Rev: 4140 $
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
    <title>${layoutSettings.companyName}: <#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)?if_exists}</#if></title>
    <script language='javascript' src='<@ofbizContentUrl>/images/calendar1.js</@ofbizContentUrl>' type='text/javascript'></script>
    <script language='javascript' src='<@ofbizContentUrl>/images/selectall.js</@ofbizContentUrl>' type='text/javascript'></script>
    <link rel='stylesheet' href='<@ofbizContentUrl>/websiteImages/main.css</@ofbizContentUrl>' type='text/css'>
    <link rel='stylesheet' href='<@ofbizContentUrl>/images/tabstyles.css</@ofbizContentUrl>' type='text/css'>    
	${layoutSettings.extraHead?if_exists}
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
    
#        html, body {
#          font-family: Verdana,sans-serif;
#          background-color: #fea;
#          color: #000;
#        }
#        a:link, a:visited { color: #00f; }
#        a:hover { color: #048; }
#        a:active { color: #f00; }
        
        textarea { background-color: #fff; border: 1px solid 00f; }
    </style>

    <script type="text/javascript">
        var editor = null;
        var summary = null;
        function initEditor() {
        primaryHTMLArea = new HTMLArea("${primaryHTMLField}"); primaryHTMLArea.generate();
        <#if secondaryHTMLField?exists>
        secondaryHTMLArea = new HTMLArea("${secondaryHTMLField}"); secondaryHTMLArea.generate();
        </#if>
        }
    </script>
</#if>

    <script language="JavaScript">
        // This code inserts the value lookedup by a popup window back into the associated form element
        var re_id = new RegExp('id=(\\d+)');
        var num_id = (re_id.exec(String(window.location))
                ? new Number(RegExp.$1) : 0);
        var obj_caller = (window.opener ? window.opener.lookups[num_id] : null);
        
        
        // function passing selected value to calling window
        function set_value(value) {
                if (!obj_caller) return;
                window.close();
                obj_caller.target.value = value;
        }
        // function refreshes caller after posting new entry
        function refresh_caller(value) {
            var str = "/postSubContent";
            <#assign separator="?"/>
            <#if requestAttributes.contentId?exists>
                str += '${separator}';
                str += "contentId=" + "${requestAttributes.contentId}";
                <#assign separator="&"/>
            </#if>
            <#if requestAttributes.mapKey?exists>
                str += '${separator}';
                str += "mapKey=" + "${requestAttributes.mapKey}";
                <#assign separator="&"/>
            </#if>
                str += '${separator}';
                str += value;
            var requestStr = '"<@ofbizUrl>"' + escape(str) + '</@ofbizUrl>' + '"'; 

            window.opener.replace(requestStr);
        }
    </script>

    <script language="JavaScript">
        function lookupSubContent (viewName, contentId, mapKey, subDataResourceTypeId, subMimeTypeId) {
	    var viewStr = viewName;
            var my=20;
            var mx=20;
            var separator = "?";
            if (contentId != null && (contentId.length > 0)) {
                viewStr += separator + "contentIdTo=" + contentId;
                separator = "&";
            }
            if (mapKey != null && mapKey.length > 0) {
                viewStr += separator + "mapKey=" + mapKey;
                separator = "&";
            }
            if (subDataResourceTypeId != null && subDataResourceTypeId.length > 0) {
                viewStr += separator + "drDataResourceTypeId=" + subDataResourceTypeId;
                separator = "&";
            }
            if (subMimeTypeId != null && subMimeTypeId.length > 0) {
                viewStr += separator + "drMimeTypeId=" + subMimeTypeId;
            }
	    var obj_lookupwindow = window.open(viewStr, 'FieldLookup', 
                'width=700,height=550,scrollbars=yes,status=no,top='
                  +my+',left='+mx+',dependent=yes,alwaysRaised=yes');
	    obj_lookupwindow.opener = window;
            obj_lookupwindow.focus();
        }
    </script>
	
</head>

<body <#if primaryHTMLField?exists && (primaryHTMLField?length >0)>onLoad="initEditor()"</#if> >
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
          <#if layoutSettings.headerImageUrl?exists>
          <td align=left width='1%'><img alt="${layoutSettings.companyName}" src='<@ofbizContentUrl>${layoutSettings.headerImageUrl}</@ofbizContentUrl>'></td>
          </#if>  
            <#if userLogin?has_content && productStoreId?exists>
				<td class="head2"><center><u>Store Name: ${productStoreId}</u></center></td/>
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
                <form method="POST" action="/backend/control/setSessionLocale" style="margin: 0;">
                <select name="locale" class="selectBox">
                    <option value="en_US">English (United States)</option>
                    <option value="en">----</option>
                        <option value="nl">Dutch</option>
                        <option value="en_US">English (United States)</option>
                        <option value="th_TH">Thai (Thailand)</option>
                </select>
                <input type="submit" value="Set" class="smallSubmit"/>
                </form>
            </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

