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
	String custRequestId = request.getParameter("custRequestId");
	String custRequestItemSeqId = request.getParameter("custRequestItemSeqId");
	String showAll = request.getParameter("showAll");
	if (showAll == null) showAll = "false";
	GenericValue custRequestItem = null;
   	if (custRequestId != null && custRequestItemSeqId != null) {
   		custRequestItem = delegator.findByPrimaryKey("CustRequestItem", UtilMisc.toMap("custRequestId", custRequestId, "custRequestItemSeqId", custRequestItemSeqId));
   		if (custRequestItem != null) pageContext.setAttribute("custRequestItem", custRequestItem);
   	}	
   	Map fields = UtilMisc.toMap("custRequestId", custRequestId);
   	if (showAll.equals("false")) fields.put("custRequestItemSeqId", custRequestItemSeqId);
	List notes = delegator.findByAnd("CustRequestItemNoteView", fields, UtilMisc.toList("-noteDateTime"));
    if (notes != null && notes.size() > 0) pageContext.setAttribute("notes", notes);	
%>

<div class='tabContainer'>
  <a href="<ofbiz:url>/request?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request</a>
  <a href="<ofbiz:url>/requestroles?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request Roles</a>
  <a href="<ofbiz:url>/requestitems?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request Items</a>
  <a href="<ofbiz:url>/requestitem?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=custRequestItemSeqId%></ofbiz:url>" class="tabButton">Item</a>
  <a href="<ofbiz:url>/requestitemnotes?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=custRequestItemSeqId%></ofbiz:url>" class="tabButtonSelected">Notes</a>
  <a href="<ofbiz:url>/requestitemrequirements?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=custRequestItemSeqId%></ofbiz:url>" class="tabButton">Requirements</a>    
  <a href="#" class="tabButton">Tasks</a>    
</div>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD>
            <div class='boxhead'>&nbsp;Notes For Request Item: <%=UtilFormatOut.checkNull(custRequestItem.getString("description"))%></div>
          </TD>
          <td align="right">
            <% if (showAll.equals("false")) { %>
            <a href="<ofbiz:url>/requestitemnotes?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=custRequestItemSeqId%>&showAll=true</ofbiz:url>" class="lightbuttontext">[Show All Notes]</a>
            <% } else { %>
            <a href="<ofbiz:url>/requestitemnotes?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=custRequestItemSeqId%>&showAll=true</ofbiz:url>" class="lightbuttontext">[Show This Item's Notes]</a>
            <% } %>
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
                    <% if (showAll.equals("true")) { %>
                    <div class="tabletext">&nbsp;<b>Item: </b><ofbiz:entityfield attribute="noteRef" field="custRequestItemSeqId"/></div>
                    <% } %>
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
              <div class="tabletext">&nbsp;No notes for this request item.</div>
            </ofbiz:unless>  
          </td>
        </tr>
        <tr>
          <td><hr class="sepbar"></td>
        </tr>
        <tr>
          <td>
            <form method="post" action="<ofbiz:url>/createrequestitemnote</ofbiz:url>" name="createnoteform">
              <input type="hidden" name="custRequestId" value="<%=custRequestId%>">
              <input type="hidden" name="custRequestItemSeqId" value="<%=custRequestItemSeqId%>">
              <table width="90%" border="0" cellpadding="2" cellspacing="0">
                <tr>
                  <td width="26%" align='right'><div class="tableheadtext">New Note</div></td>
                  <td width="74%">
                    <textarea class="textAreaBox" name="note" rows="5" cols="70"></textarea>
                  </td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                  <td align="right"> 
                    <input type="submit" style="font-size: small;" value="Create">  
                  </td>
                  <td>&nbsp;</td>
                </tr>                  
              </table>
            </form>            
                    
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
