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
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *@version    $Revision: 1.1 $
 *@since      3.0
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>

<script language="JavaScript">
<!-- //
function lookupParts() {
    document.lookuppart.submit();
}
// -->
</script>


<#if security.hasEntityPermission("MANUFACTURING", "_VIEW", session)>
<form method='post' name="lookuppart" action="<@ofbizUrl>/findParts</@ofbizUrl>">
<input type='hidden' name='lookupFlag' value='Y'>
<input type='hidden' name='hideFields' value='Y'>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td><div class='boxhead'>${uiLabelMap.ManufacturingFindParts}</div></td>
          <td align='right'>
            <div class="tabletext">
              <#if requestParameters.hideFields?default("N") == "Y">
                <a href="<@ofbizUrl>/findParts?hideFields=N${paramList}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonShowLookupFields}</a>
              <#else>
                <#if partList?exists>
                    <a href="<@ofbizUrl>/findParts?hideFields=Y${paramList}</@ofbizUrl>" class="submenutext">${uiLabelMap.CommonHideFields}</a>
                </#if>
                <a href="javascript:lookupParts();" class="submenutextright">${uiLabelMap.CommonLookup}</a>                
              </#if>
            </div>
          </td>
        </tr>
      </table>
      <#if requestParameters.hideFields?default("N") != "Y">
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td align='center' width='100%'>
            <table border='0' cellspacing='0' cellpadding='2'>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>${uiLabelMap.ManufacturingPartId}:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                    <input type='text' size='25' class='inputBox' name='partId' value='${requestParameters.partId?if_exists}'>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>${uiLabelMap.ManufacturingPartType}:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='partType' class='selectBox'> 
                    <option value="ANY">Any Part Type</option>                   
                    <#list partTypes as onePartType>
                      <option value="${onePartType.productTypeId}">${onePartType.description}</option>
                    </#list>
                  </select>
                </td>
              </tr>              
            </table>
          </td>
        </tr>
      </table>
      </#if>
    </td>
  </tr>
</table>
<input type="image" src="/images/spacer.gif" onClick="javascript:lookupParts();">
</form> 

<#if requestParameters.hideFields?default("N") != "Y">
<script language="JavaScript">
<!--//
document.lookuppart.partType.focus();
//-->
</script>
</#if>

<#if partList?exists>
<br>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="50%"><div class="boxhead">${uiLabelMap.CommonElementsFound}</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <#if 0 < partList?size>             
                <#if 0 < viewIndex>
                  <a href="<@ofbizUrl>/findParts?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutext">${uiLabelMap.CommonPrevious}</a>
                <#else>
                  <span class="submenutextdisabled">${uiLabelMap.CommonPrevious}</span>
                </#if>
                <#if 0 < listSize>
                  <span class="submenutextinfo">${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
                </#if>
                <#if highIndex < listSize>
                  <a href="<@ofbizUrl>/findParts?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonNext}</a>
                <#else>
                  <span class="submenutextrightdisabled">${uiLabelMap.CommonNext}</span>
                </#if>
              </#if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td width="20%" align="left"><div class="tableheadtext">${uiLabelMap.ManufacturingPartType}</div></td>
          <td width="30%" align="left"><div class="tableheadtext">${uiLabelMap.ManufacturingPartId}</div></td>
          <td width="50%" align="left"><div class="tableheadtext">${uiLabelMap.ManufacturingPartName}</div></td>
        </tr>
        <tr>
          <td colspan='3'><hr class='sepbar'></td>
        </tr>
        <#if partList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list partList[lowIndex..highIndex-1] as part>            
            <#assign partType = part.getRelatedOneCache("ProductType")>
            <tr class='${rowClass}'>
              <td><div class='tabletext'>${partType.description?default(partType.productTypeId?default(""))}</div></td>
              <td>
                  <a href="<@ofbizUrl>/findBom?partId=${part.productId}</@ofbizUrl>" class='buttontext'>${part.productId}</a>
              </td>
              <td>${part.productName?default("&nbsp;")}</td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "viewManyTR2">
              <#assign rowClass = "viewManyTR1">
            <#else>
              <#assign rowClass = "viewManyTR2">
            </#if>
          </#list>          
        <#else>
          <tr>
            <td colspan='4'><div class='head3'>${uiLabelMap.CommonNoElementFound}.</div></td>
          </tr>        
        </#if>
        <#if lookupErrorMessage?exists>
          <tr>
            <td colspan='4'><div class="head3">${lookupErrorMessage}</div></td>
          </tr>
        </#if>
      </table>
    </td>
  </tr>
</table>
        
</#if> 
<#else>
  <h3>${uiLabelMap.ManufacturingViewPermissionError}</h3>
</#if>
