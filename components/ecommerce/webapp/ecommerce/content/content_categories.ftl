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
 *@author     Al Byers (byersa@automationgroups.com)
 *@version    $Rev:$
 *@since      3.1
-->
<#assign forumRootId = "WebStoreCONTENT" />

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#-- variable setup and worker calls -->
<#assign curCategoryId = requestAttributes.curCategoryId?if_exists>
<#assign forumTrailCsv=requestParameters.forumTrailCsv?if_exists/>
<#assign forumTrail=[]/>
<#assign firstContentId=""/>
<#if forumTrailCsv?has_content>
  <#assign forumTrail=Static["org.ofbiz.base.util.StringUtil"].split(forumTrailCsv, ",") />
  <#if 0 < forumTrail?size>
    <#assign firstContentId=forumTrail[0]?string/>
  </#if>
</#if>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">${uiLabelMap.ProductBrowseContent}</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <div style='margin-left: 10px;'>

                <#assign count_1=0 />
                <@loopSubContentCache subContentId=forumRootId
                    viewIndex=0
                    viewSize=9999
                    orderBy="contentName"
                    contentAssocTypeId="SUBSITE"
                >
                       <tr>
                         <td >
                           <div class="browsecategorytext" style="margin-left: 10px">
                             -&nbsp;<a href="<@ofbizUrl>/showcontenttree?contentId=${subContentId}&nodeTrailCsv=${subContentId}</@ofbizUrl>" class="browsecategorybutton">${content.contentName}</a>
                           </div>
                         </td >
                       </tr>
                    <#assign count_1=count_1 + 1 />
                </@loopSubContentCache >
            </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>


