<%
    /**
     *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
     * @author     Andy Zeneski
     * @version    $Revision$
     * @since      2.0
     */
%>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Notes</div>
          </td>
          <td valign="middle" align="right">&nbsp;
            <%if(security.hasEntityPermission("ORDERMGR", "_NOTE", session)) {%>  
              <a href="<ofbiz:url>/createnewnote?<%=qString%></ofbiz:url>" class="lightbuttontext">[Create New]</a>&nbsp;&nbsp;
            <%}%>
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
            <ofbiz:if name="notes">
            <table width="100%" border="0" cellpadding="1">
              <ofbiz:iterator name="noteRef" property="notes">
                <tr>
                  <td align="left" valign="top" width="35%">
                    <div class="tabletext">&nbsp;<b>By: </b><ofbiz:entityfield attribute="noteRef" field="firstName"/>&nbsp;<ofbiz:entityfield attribute="noteRef" field="lastName"/></div>
                    <div class="tabletext">&nbsp;<b>At: </b><ofbiz:entityfield attribute="noteRef" field="noteDateTime"/></div>
                  </td>
                  <td align="left" valign="top" width="65%">
                    <div class="tabletext"><ofbiz:entityfield attribute="noteRef" field="noteInfo"/></div>
                  </td>
                </tr>
                <ofbiz:iteratorHasNext>
                  <tr><td colspan="2"><hr class="sepbar"></td></tr>
                </ofbiz:iteratorHasNext>
              </ofbiz:iterator>
            </table>
            </ofbiz:if>
            <ofbiz:unless name="notes">
              <div class="tabletext">&nbsp;No notes for this order.</div>
            </ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
