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
 *@version    $Rev:$
 *@since      2.0
--%>


<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.entity.util.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />

<%
	String custRequestId = request.getParameter("custRequestId");
	String custRequestItemSeqId = request.getParameter("custRequestItemSeqId");
	GenericValue custRequestItem = null;
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
    
    // get the current status item
    GenericValue currentStatusItem = null;
    if (custRequestItem != null) {
    	currentStatusItem = custRequestItem.getRelatedOne("StatusItem");
    	if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);
    }
    
	// status items
	if (custRequestItem != null && UtilValidate.isNotEmpty(custRequestItem.getString("statusId"))) {
		List statusChange = delegator.findByAnd("StatusValidChange", UtilMisc.toMap("statusId", custRequestItem.getString("statusId")));	
        if (statusChange != null) {
        	List statusItems = new ArrayList();
        	Iterator statusChangeIter = statusChange.iterator();
        	while (statusChangeIter.hasNext()) {
        		GenericValue curStatusChange = (GenericValue) statusChangeIter.next();
        		GenericValue curStatusItem = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", curStatusChange.get("statusIdTo")));
        		if (curStatusItem != null) statusItems.add(curStatusItem);
        	}
			List statusItem = EntityUtil.orderBy(statusItems, UtilMisc.toList("sequenceId"));
        	pageContext.setAttribute("statusItems", statusItems);
        }
    } else {
    	List statusItems = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "CUSTREQ_STTS"), UtilMisc.toList("sequenceId"));
        if (statusItems != null) pageContext.setAttribute("statusItems", statusItems);
    }	       	   	
%>

<div class='tabContainer'>
  <a href="<ofbiz:url>/request?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request</a>
  <a href="<ofbiz:url>/requestroles?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request Roles</a>
  <a href="<ofbiz:url>/requestitems?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request Items</a>
  <% if (custRequestItemSeqId != null && custRequestItemSeqId.length() > 0) { %>
  <a href="<ofbiz:url>/requestitem?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=custRequestItemSeqId%></ofbiz:url>" class="tabButtonSelected">Item</a>
  <a href="<ofbiz:url>/requestitemnotes?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=custRequestItemSeqId%></ofbiz:url>" class="tabButton">Notes</a>
  <a href="<ofbiz:url>/requestitemrequirements?custRequestId=<%=custRequestId%>&custRequestItemSeqId=<%=custRequestItemSeqId%></ofbiz:url>" class="tabButton">Requirements</a>    
  <a href="#" class="tabButton">Tasks</a>  
  <% } %>
</div>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD>
            <div class='boxhead'>&nbsp;<%=custRequestItemSeqId != null ? "Request Item #" + custRequestItemSeqId : "New Request Item"%></div>
          </TD>  
          <td align="right">
            <a href="<ofbiz:url>/requestitem?custRequestId=<%=custRequestId%></ofbiz:url>" class="lightbuttontext">[Create New]</a>        
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
                <TR>
                  <TD>
                    <% if (custRequestItem == null) { %>
                    <form method="post" name="requestItemForm" action="<ofbiz:url>/createrequestitem</ofbiz:url>">
                    <% } else { %>
                    <form method="post" name="requestItemForm" action="<ofbiz:url>/updaterequestitem</ofbiz:url>">
                    <% } %>
                      <input type="hidden" name="custRequestId" value="<%=custRequestId%>">
                      <input type="hidden" name="custRequestItemSeqId" value="<%=custRequestItem != null ? custRequestItem.getString("custRequestItemSeqId") : (new Integer(nextSeqId)).toString()%>">
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <tr>
                          <td align="right"><div class="tableheadtext">Response Required Date</div></td>
                          <td>
                            <input type="text" name="requiredByDate" class="inputBox" size="30" value='<ofbiz:inputvalue field="requiredByDate" entityAttr="custRequestItem"/>'>
                            <a href="javascript:call_cal(document.requestItemForm.requiredByDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                          </td>
                        </tr>
                        <tr>
                          <td align="right"><div class="tableheadtext">Product</div></td>
                          <td><input type="text" name="productId" class="inputBox" size="20" value='<ofbiz:inputvalue field="productId" entityAttr="custRequestItem"/>'></td>
                        </tr>
                        <tr>
                          <td align="right"><div class="tableheadtext">Max Amount</div></td>
                          <td><input type="text" name="maximumAmount" class="inputBox" size="10" value='<ofbiz:inputvalue field="maximumAmount" entityAttr="custRequestItem"/>'></td>
                        </tr>                                                
                        <tr>
                          <td align="right"><div class="tableheadtext">Quantity</div></td>
                          <td><input type="text" name="quantity" class="inputBox" size="6" value='<ofbiz:inputvalue field="quantity" entityAttr="custRequestItem"/>'></td>
                        </tr>
                        <tr>
                          <td align="right"><div class="tableheadtext">Status</div></td>
                          <td>
					        <select name="statusId" class='selectBox'>
					          <ofbiz:if name="custRequestItem">					        
					          <option value='<%=custRequestItem.getString("statusId")%>'><%if (currentStatusItem != null) {%><%=currentStatusItem.getString("description")%><%} else {%><%=UtilFormatOut.ifNotEmpty(custRequestItem.getString("statusId"), "[", "]")%><%}%></option>
					          <option value='<%=custRequestItem.getString("statusId")%>'>----</option>
					          </ofbiz:if>
					          <ofbiz:iterator name="statusItem" property="statusItems">
					            <option value='<ofbiz:inputvalue entityAttr="statusItem" field="statusId"/>'><ofbiz:inputvalue entityAttr="statusItem" field="description"/></option>
					          </ofbiz:iterator>
							</select>                                                                          
                          </td>
                        </tr>
                        <tr>
                          <td align="right"><div class="tableheadtext">Priority</div></td>
                          <td>
                            <select name="priority" class="selectBox">
                              <option>9</option>
                              <option>8</option>
                              <option>7</option>
                              <option>6</option>
                              <option>5</option>
                              <option>4</option>
                              <option>3</option>
                              <option>2</option>
                              <option>1</option>
                            </select>
                          </td>
                        </tr>
                        <tr>
                          <td align="right"><div class="tableheadtext">Description</div></td>
                          <td><input type="text" name="description" class="inputBox" size="50" value='<ofbiz:inputvalue field="description" entityAttr="custRequestItem"/>'></td>
                        </tr>
                        <tr>
                          <td align="right"><div class="tableheadtext">Story</div></td>
                          <td><textarea name="story" class="textAreaBox" cols="60" rows="20"><%=custRequestItem != null ? UtilFormatOut.checkNull(custRequestItem.getString("story")) : ""%></textarea></td>
                        </tr>
                        <tr>
                          <td align="right">
                            <% if (custRequestItem == null) { %>
                            <input type="submit" style="font-size: small;" value="Create">
                            <% } else { %>
                            <input type="submit" style="font-size: small;" value="Update">
                            <% } %>
                          </td>
                          <td>&nbsp;</td>
                        </tr>
                      </table>
                    </form>
                  </TD>
                </TR>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>          

