<%--
 *  Description: None
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
 *@author     Andy Zeneski 
 *@version    $Revision$
 *@since      2.0
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
    // check for the requestItemSeqId and get the item object
    String custRequestId = request.getParameter("custRequestId");
    String custRequestItemSeqId = request.getParameter("custRequestItemSeqId");
    GenericValue custRequestItem = null;
    if (custRequestItemSeqId == null) custRequestItemSeqId = (String) request.getAttribute("custRequestItemSeqId");
   	if (custRequestId != null && custRequestItemSeqId != null) {
   		custRequestItem = delegator.findByPrimaryKey("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId));
   		if (custRequestItem != null) pageContext.setAttribute("custRequestItem", custRequestItem);
   	}
   	
    // get all the request items.
    int nextSeqId = 1;
    if (custRequestId != null) {
        List requestItems = delegator.findByAnd("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId));
        if (requestItems != null && requestItems.size() > 0) {
            pageContext.setAttribute("custRequestItems", requestItems);
            nextSeqId = requestItems.size() + 1;
        }
    }   	   	
%>

<div class='tabContainer'>
  <a href="<ofbiz:url>/request?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request</a>
  <a href="<ofbiz:url>/requestroles?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request Roles</a>
  <a href="<ofbiz:url>/requestitems?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButtonSelected">Request Items</a>
</div>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD>
            <div class='boxhead'>&nbsp;Request Items</div>
          </TD>
          <td align='right'>
            <a href="<ofbiz:url>/requestitem?custRequestId=<%=custRequestId%></ofbiz:url>" class="lightbuttontext">[Create&nbsp;New]</a>
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
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <ofbiz:unless name="custRequestItems">
                  <TR>
                    <TD><div class="tabletext">&nbsp;<b>No items created.</b></div></TD>
                  </TR>
                </ofbiz:unless>
                <ofbiz:if name="custRequestItems">
                <TR>
                  <TD>
                    <table width="100%" cellpadding="2" cellspacing="0" border="0">
                      <tr>
                        <td align="right"><div class="tableheadtext">#</div></td>
                        <td>&nbsp;</td>
                        <td><div class="tableheadtext">Description</div></td>
                        <td><div class="tableheadtext">Priority</div></td>
                        <td><div class="tableheadtext">Status</div></td>
                        <td><div class="tableheadtext">Product</div></td>
                        <td align='right'><div class="tableheadtext">Quantity</div></td>
                        <td>&nbsp;&nbsp;</td>
                        <td align='right'><div class="tableheadtext">Max Amount</div></td>
                        <td>&nbsp;&nbsp;</td>
                        <td><div class="tableheadtext">Required&nbsp;Date</div></td>
                        <td>&nbsp;</td>                        
                      </tr>
                      <tr>
                        <td colspan='12'><hr class="sepbar"></td>
                      </tr>
                      <ofbiz:iterator name="item" property="custRequestItems">
                          <% GenericValue statusItem = item.getRelatedOne("StatusItem"); %>
                          <tr>
                            <td align="right"><div class="tabletext"><%=item.getString("custRequestItemSeqId")%></div></td>
                            <td>&nbsp;</td>
                            <td><a href="<ofbiz:url>/requestitem?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=item.getString("custRequestItemSeqId")%></ofbiz:url>" class="buttontext"><%=UtilFormatOut.checkNull(item.getString("description"))%></a></td>
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(item.getString("priority"))%></div></td>
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(statusItem.getString("description"))%></div></td>
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(item.getString("productId"))%></div></td>
                            <td align='right'><div class="tabletext"><%=UtilFormatOut.formatQuantity(item.getDouble("quantity"))%></div></td>
                            <td>&nbsp;&nbsp;</td>
                            <td align='right'><div class="tabletext"><%=UtilFormatOut.formatPrice(item.getDouble("maximumAmount"))%></div></td>
                            <td>&nbsp;&nbsp;</td>
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(item.getString("requiredByDate"))%></div></td>
                            <td align="right"><div class="tabletext"><a href="<ofbiz:url>/requestitem?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=item.getString("custRequestItemSeqId")%></ofbiz:url>" class="buttontext">[View]</a></td>
                          </tr>
                      </ofbiz:iterator>
                    </table>
                  </TD>
                </TR>
                </ofbiz:if>              
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>


