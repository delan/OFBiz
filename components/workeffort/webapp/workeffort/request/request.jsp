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
 *@version    $Revision: 1.4 $
 *@since      2.0
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.entity.util.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />

<%
    // check for a requestId and get the requst object
    String custRequestId = request.getParameter("custRequestId");
    GenericValue custRequest = null;
    if (custRequestId == null) custRequestId = (String) request.getAttribute("custRequestId");
	
    if (custRequestId != null) {
        custRequest = delegator.findByPrimaryKey("CustRequest", UtilMisc.toMap("custRequestId", custRequestId));
        if (custRequest != null) pageContext.setAttribute("custRequest", custRequest);
    }
    
   	// Get the request types
    Collection custRequestTypes = delegator.findAllCache("CustRequestType", UtilMisc.toList("description"));
    pageContext.setAttribute("custRequestTypes", custRequestTypes);   
    
    // get the current status item
    GenericValue currentStatusItem = null;
    if (custRequest != null) {
    	currentStatusItem = custRequest.getRelatedOne("StatusItem");
    	if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);
    }

    // get the communication event ID
    String communicationEventId = request.getParameter("communicationEventId");
    String partyId = request.getParameter("partyId");
    if (partyId == null) partyId = "";
    String custRequestName = request.getParameter("subject");
    if (custRequest != null) custRequestName = custRequest.getString("custRequestName");
    if (custRequestName == null) custRequestName = "";

	// status items
	if (custRequest != null && UtilValidate.isNotEmpty(custRequest.getString("statusId"))) {
		List statusChange = delegator.findByAnd("StatusValidChange", UtilMisc.toMap("statusId", custRequest.getString("statusId")));	
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
  <a href="<ofbiz:url>/request?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButtonSelected">Request</a>
  <a href="<ofbiz:url>/requestroles?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request Roles</a>
  <a href="<ofbiz:url>/requestitems?custRequestId=<%=custRequestId%></ofbiz:url>" class="tabButton">Request Items</a>
</div>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Request Detail</div>
          </TD>
          <td align="right">
            <a href="<ofbiz:url>/request</ofbiz:url>" class="submenutextright">Create New</a>
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
                    <ofbiz:unless name="custRequest">
                      <form method="post" action="<ofbiz:url>/createrequest</ofbiz:url>" name="custRequestForm">
                    </ofbiz:unless>
                    <ofbiz:if name="custRequest">
                      <form method="post" action="<ofbiz:url>/updaterequest</ofbiz:url>" name="custRequestForm">
                        <input type="hidden" name="custRequestId" value="<%=custRequestId%>">
                    </ofbiz:if>
                    <%if (communicationEventId != null) {%>
                      <input type="hidden" name="communicationEventId" value="<%=communicationEventId%>">
                    <%}%>
                    <table width="100%" cellpadding="2" cellspacing="0" border="0">
                      <tr>
                        <td align="right"><div class="tableheadtext">Request Date</div></td>
                        <td>
                          <input type="text" class="inputBox" size="23" <ofbiz:inputvalue entityAttr="custRequest" field="custRequestDate" fullattrs="true"/>>
                          <a href="javascript:call_cal(document.custRequestForm.custRequestDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                        </td>
                      </tr>
                      <tr>
                        <td align="right"><div class="tableheadtext">Response Required Date</div></td>
                        <td>
                          <input type="text" class="inputBox" size="23" <ofbiz:inputvalue entityAttr="custRequest" field="responseRequiredDate" fullattrs="true"/>>
                          <a href="javascript:call_cal(document.custRequestForm.responseRequiredDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                        </td>
                      </tr>
                      <tr>
                        <td align="right"><div class="tableheadtext">RequestType</div></td>
                        <td>
                          <select name="custRequestTypeId" class="selectBox">
                            <ofbiz:iterator name="custRequestType" property="custRequestTypes">
                              <%if (custRequest != null && custRequest.getString("custRequestTypeId").equals(custRequestType.getString("custRequestTypeId"))) {%>
                                <option SELECTED value="<%=custRequestType.getString("custRequestTypeId")%>"><%=custRequestType.getString("description")%></option>
                              <%} else {%>
                                <option value="<%=custRequestType.getString("custRequestTypeId")%>"><%=custRequestType.getString("description")%></option>
                              <%}%>
                            </ofbiz:iterator>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td align="right"><div class="tableheadtext">Status</div></td>
                        <td>
					      <select name="statusId" class="selectBox">
					        <ofbiz:if name="custRequest">					        
					        <option value='<%=custRequest.getString("statusId")%>'><%if (currentStatusItem != null) {%><%=currentStatusItem.getString("description")%><%} else {%><%=UtilFormatOut.ifNotEmpty(custRequest.getString("statusId"), "[", "]")%><%}%></option>
					        <option value='<%=custRequest.getString("statusId")%>'>----</option>
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
                        <td align="right"><div class="tableheadtext">Name</div></td>
                        <td><input type="text" class="inputBox" size="50" value="<%=custRequestName%>"</td>
                      </tr>
                      <tr>
                        <td align="right"><div class="tableheadtext">Description</div></td>
                        <td><input type="text" class="inputBox" size="50"  <ofbiz:inputvalue entityAttr="custRequest" field="description" fullattrs="true"/>></td>
                      </tr>

                      <ofbiz:unless name="custRequest">
                      <tr>
                        <td align="right"><div class="tableheadtext">Requesting Party</div></td>
                        <td><input type="text" name="requestPartyId" class="inputBox" size="20" value="<%=partyId%>"></td>
                      </tr>
                      </ofbiz:unless>

                      <tr>
                        <ofbiz:unless name="custRequest">
                          <td align="right"><input type="submit" class="smallSubmit" value="Create"></td>
                        </ofbiz:unless>
                        <ofbiz:if name="custRequest">
                          <td align="right"><input type="submit" class="smallSubmit" value="Update"></td>
                        </ofbiz:if>
                        <td>&nbsp</td>
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

