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
<%
    String state = request.getParameter("SearchProductsState");
    boolean isOpen = true;
    if (state != null) {
        session.setAttribute("SearchProductsState", state);
        isOpen = "open".equals(state);
    } else {
        state = (String) session.getAttribute("SearchProductsState");
        if (state != null) {
            isOpen = "open".equals(state);
        }
    }
%>
<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">Search&nbsp;Products</div>
          </td>
          <td valign=middle align=right>
            <%if (isOpen) {%>
                <a href='<ofbiz:url>/main?SearchProductsState=close</ofbiz:url>' class='lightbuttontext'>&nbsp;_&nbsp;</a>
            <%} else {%>
                <a href='<ofbiz:url>/main?SearchProductsState=open</ofbiz:url>' class='lightbuttontext'>&nbsp;[]&nbsp;</a>
            <%}%>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
<%if (isOpen) {%>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <form name="keywordsearchform" method="POST" action="<ofbiz:url>/keywordsearch?VIEW_SIZE=25</ofbiz:url>" style='margin: 0;'>
              <div class='tabletext'>Keywords: <input type="text" name="SEARCH_STRING" size="20" maxlength="50"></div>
              <div class='tabletext'>CategoryId: <input type="text" name="SEARCH_CATEGORY_ID" size="20" maxlength="20"></div>
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
<%}%>
</TABLE>
