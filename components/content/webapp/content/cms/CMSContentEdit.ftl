<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 *@author     Al Byers (byersa@automationgroups.com)
 *@version    $Revision: 1.1 $
 *@since      2.1
-->
<SCRIPT language="javascript">
    function submitRows(rowCount) {
        var rowCountElement = document.createElement("input");
        rowCountElement.setAttribute("name", "_rowCount");
        rowCountElement.setAttribute("type", "hidden");
        rowCountElement.setAttribute("value", rowCount);
        document.forms.publishsite.appendChild(rowCountElement);
        document.forms.publishsite.submit();
    }
</SCRIPT>

<#include "publishlib.ftl" />
<#--
<#import "publishlib.ftl" as publish/>
${menuAuxWrapper.renderMenuString()}
-->
<#-- Main Heading -->
<table width='100%' cellpadding='0' cellspacing='0' border='0'>
  <tr>
    <td align=left>
      <div class="head1">${contentId?if_exists}
      </div>
    </td>
    <td align=right>
    </td>
  </tr>
</table>
<br>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Content Information</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/EditContentInfo?contentId=${contentId?if_exists}</@ofbizUrl>" class="submenutextright">Update</a>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="0" cellspacing='0'>
    <tr><td align=right nowrap><div class='tabletext'><b>Content Name</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${contentName?if_exists}</div></td></tr>
    <tr><td align=right nowrap><div class='tabletext'><b>Description</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${description?if_exists}<div></td></tr>
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Summary Information</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/EditAddContent?contentId=${sumContentId?if_exists}&dataResourceId=${subDataResourceId?if_exists}</@ofbizUrl>" class="submenutextright">Update</a>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="0" cellspacing='0'>
    <tr><td align=right nowrap><div class='tabletext'><b>Summary</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>
${summaryData?if_exists}
<div></td></tr>
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Article Information</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/EditAddContent?contentId=${txtContentId?if_exists}&dataResourceId=${txtDataResourceId?if_exists}</@ofbizUrl>" class="submenutextright">Update</a>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="0" cellspacing='0'>
    <tr><td align=right nowrap><div class='tabletext'><b>Article</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>
${textData?if_exists}
<div></td></tr>
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<#-- ============================================================= -->

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp; Roles </div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/EditPublishLinks?contentId=${contentId?if_exists}</@ofbizUrl>" class="submenutextright">Publish</a>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <form mode="POST" name="publishsite" action="<@ofbizUrl>/linkContentToPubPt</@ofbizUrl>">
              <input type="hidden" name="contentId" value="${contentId}"/>
              <table width="100%" border="0" cellpadding="1">
                    <#assign rowCount = 0 />
                    <#assign rootForumId=page.getProperty("rootForumId") />
                    <@publishContent forumId=rootForumId contentId=contentId />
              </table>
            </form>
          </td>
        </tr>
        <tr>
          <td colspan="1">
<div class="standardSubmit" ><a href="javascript:submitRows('${rowCount?default(0)}')">Publish</a></div>
          </td>
        </tr>

      </table>
    </TD>
  </TR>
</TABLE>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Image Information</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/EditAddImage?contentId=${imgContentId?if_exists}dataResourceId=${imgDataResourceId?if_exists}</@ofbizUrl>" class="submenutextright">Update</a>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="0" cellspacing='0'>
    <tr><td align=right nowrap><div class='tabletext'><b>Image</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>
        <img src="<@ofbizUrl>/img?imgId=${imgDataResourceId?if_exists}</@ofbizUrl>" />
<div></td></tr>
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

