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
 *@version    $Revision: 1.4 $
 *@since      2.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if 0 < catalogCol?size>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign=middle align=center>
              <div class="boxhead">${currentCatalogName}</div>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td align=center>
              <form name="choosecatalogform" method="POST" action="<@ofbizUrl>/orderentry</@ofbizUrl>" style='margin: 0;'>
                <SELECT name='CURRENT_CATALOG_ID' class='selectBox'>
                  <OPTION value='${currentCatalogId}'>${currentCatalogName}</OPTION>
                  <OPTION value='${currentCatalogId}'></OPTION>
                  <#list catalogCol as catalogId>
                    <#assign thisCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, catalogId)>
                    <OPTION value='${catalogId}'>${thisCatalogName}</OPTION>
                  </#list>
                </SELECT>
                <div><a href="javascript:document.choosecatalogform.submit()" class="buttontext">${uiLabelMap.ProductChooseCatalog}</a></div>
               </form>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</#if>

