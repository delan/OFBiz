<%--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones
 *@created    May 22 2001
 *@version    1.0
--%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*, org.ofbiz.commonapp.product.catalog.*" %>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">Search&nbsp;Catalog</div>
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
            <form name="keywordsearchform" method="POST" action="<ofbiz:url>/keywordsearch</ofbiz:url>" style='margin: 0;'>
              <input type='hidden' name="VIEW_SIZE" value="10">
              <input type='hidden' name="SEARCH_CATEGORY_ID" value="<%=UtilFormatOut.checkNull(CatalogWorker.getCatalogSearchCategoryId(pageContext, CatalogWorker.getCurrentCatalogId(pageContext)))%>">
              <input type='text' name="SEARCH_STRING" size="14" maxlength="50">
              <div class='tabletext'>
                Any<input type=RADIO name='SEARCH_OPERATOR' value='OR' checked>
                All<input type=RADIO name='SEARCH_OPERATOR' value='AND'>
                <a href="javascript:document.keywordsearchform.submit()" class="buttontext">&nbsp;Find</a>
              </div>
            </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
