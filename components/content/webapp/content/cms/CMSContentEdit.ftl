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
 *@version    $Revision: 1.8 $
 *@since      2.1
-->


<#include "publishlib.ftl" />
<#--
<#import "publishlib.ftl" as publish/>
-->
${menuWrapper.renderMenuString()}
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


<#if currentValue?has_content>
    <@renderTextData content=currentValue textData=textData />
</#if>
<#list textList as map>
    <@renderTextData content=map.entity textData=map.text />
</#list>
<#-- ============================================================= -->

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp; Links </div>
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
                    <#assign rootForumId2=page.getProperty("rootForumId2") />
                    <@publishContent forumId=rootForumId2 contentId=contentId />
                    <tr>
                      <td colspan="1">
                          <input type="submit" name="submitBtn" value="Publish"/>
                      </td>
                    </tr>
              </table>
              <input type="hidden" name="_rowCount" value="${rowCount}"/>
            </form>
          </td>
        </tr>

      </table>
    </TD>
  </TR>
</TABLE>

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp; Features </div>
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
            <form mode="POST" name="updatefeatures" action="<@ofbizUrl>/updateFeatures</@ofbizUrl>">
              <input type="hidden" name="contentId" value="${contentId}"/>
              <table width="100%" border="0" cellpadding="1">
                       <tr>
                          <td class="">Product Feature</td>
                          <td class="">Has Feature</td>
                    <#assign rowCount = 0 />
                    <#list featureList as feature>
                       <#assign checked=""/>
                       <#if feature.action?has_content && feature.action == "Y">
                           <#assign checked="checked"/>
                       </#if>
                       <tr>
                          <td class="">[${feature.productFeatureId}] - ${feature.description}</td>
                          <td class=""><input type="checkbox" name="action_o_${rowCount}" value="Y" ${checked}/></td>
                          <input type="hidden" name="fieldName0_o_${rowCount}" value="productFeatureId"/>
                          <input type="hidden" name="fieldValue0_o_${rowCount}" value="${feature.productFeatureId}"/>
                          <input type="hidden" name="fieldName1_o_${rowCount}" value="dataResourceId"/>
                          <input type="hidden" name="fieldValue1_o_${rowCount}" value="${feature.dataResourceId}"/>
                          <input type="hidden" name="entityName_o_${rowCount}" value="ProductFeatureDataResource"/>
                          <input type="hidden" name="pkFieldCount_o_${rowCount}" value="2"/>
                       </tr>
                       <#assign rowCount=rowCount + 1/>
                    </#list>
                    <tr>
                      <td valign="middle" align="left">
                        <div class="boxhead"><input type="text" name="fieldValue0_o_${rowCount}" value=""/>
                          <a href="javascript:call_fieldlookup3('<@ofbizUrl>/LookupFeature</@ofbizUrl>')">
                            <img src="<@ofbizContentUrl>/content/images/fieldlookup.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Lookup">
                          </a>
                        </div>
                      </td>
                          <input type="hidden" name="fieldName0_o_${rowCount}" value="productFeatureId"/>
                          <input type="hidden" name="fieldValue0_o_${rowCount}" value=""/>
                          <input type="hidden" name="fieldName1_o_${rowCount}" value="dataResourceId"/>
                          <input type="hidden" name="fieldValue1_o_${rowCount}" value="${dataResourceId}"/>
                          <input type="hidden" name="entityName_o_${rowCount}" value="ProductFeatureDataResource"/>
                          <input type="hidden" name="pkFieldCount_o_${rowCount}" value="2"/>
                          <#assign rowCount=rowCount + 1/>
                    </tr>
                    <tr>
                      <td colspan="1">
                          <input type="submit" name="submitBtn" value="Update"/>
                      </td>
                    </tr>
              </table>
              <input type="hidden" name="_rowCount" value="${rowCount}"/>
            </form>
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


<#--
<#macro contentTree currentValue >

    <#assign contentId = currentValue.contentId/>
    <#assign dataResourceId = currentValue.dataResourceId/>
    <#assign currentTextData = "" />
    <#if dataResourceId?has_content>
        <#assign currentTextData=Static["org.ofbiz.content.data.DataResourceWorker"].renderDataResourceAsTextCache(delegator, dataResourceId, (Map)null, (GenericValue)null, (Locale)null, (String)null) />
        <#if currentTextData?has_content>
            <@renderTextData contentId=contentId textData=currentTextData />
        </#if>
    </#if>
    <#assign contentAssocViewList =Static["org.ofbiz.content.content.ContentWorker"].getContentAssocViewList(delegator, contentId, null, "SUB_CONTENT", null, null)?if_exists />
    <#list contentAssocViewList as contentAssocDataResourceView>
        <#assign contentId2 = contentAssocDataResourceView.contentId/>
        <#assign mapKey = contentAssocDataResourceView.mapKey/>
        <#assign dataResourceId2 = contentAssocDataResourceView.dataResourceId/>
        <#assign currentTextData=Static["org.ofbiz.content.data.DataResourceWorker"].renderDataResourceAsTextCache(delegator, dataResourceId2, null, null, null, null) />
        <#if currentTextData?has_content>
            <@renderTextData contentId=contentId2 mapKey=mapKey textData=currentTextData />
        </#if>
    </#list>
</#macro>
-->

<#macro renderTextData content textData >
    <#assign contentId=content.contentId?if_exists/>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/EditAddContent?contentId=${content.contentId?if_exists}&contentIdTo=${content.caContentIdTo?if_exists}&contentAssocTypeId=${content.caContentAssocTypeId?if_exists}&fromDate=${content.caFromDate?if_exists}&mapKey=${content.caMapKey?if_exists}</@ofbizUrl>" class="submenutextright">Update</a>
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
    <tr><td align=right nowrap><div class='tabletext'><b>Content Name</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${content.contentName?if_exists}</div></td></tr>
    <tr><td align=right nowrap><div class='tabletext'><b>Description</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${content.description?if_exists}<div></td></tr>
  </table>
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
    <tr><td align=right nowrap><div class='tabletext'><b></b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>
${textData?if_exists}
<div></td></tr>
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</#macro>
