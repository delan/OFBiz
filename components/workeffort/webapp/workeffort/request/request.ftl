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
-->

<#if (requestAttributes.uiLabelMap)?exists>
    <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>
                    <#if custRequest?exists>
                      <form method="post" action="<@ofbizUrl>/updaterequest</@ofbizUrl>" name="custRequestForm">
                        <input type="hidden" name="custRequestId" value="${custRequestId}">
                    <#else>
                      <form method="post" action="<@ofbizUrl>/createrequest</@ofbizUrl>" name="custRequestForm">
                    </#if>
                    <#if communicationEventId?exists>
                      <input type="hidden" name="communicationEventId" value="${communicationEventId}">
                    </#if>
                    <table cellpadding="2" cellspacing="0" border="0">
                      <tr>
                        <td align="right"><div class="tableheadtext">Request Date</div></td>
                        <td>
                          <#--<input type="text" class="inputBox" size="23" <ofbiz:inputvalue entityAttr="custRequest" field="custRequestDate" fullattrs="true"/>>-->
                          <input type="text" class="inputBox" size="23" name="custRequestDate" value="<#if custRequest?exists>${custRequest.custRequestDate?if_exists}</#if>">
                          <a href="javascript:call_cal(document.custRequestForm.custRequestDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                        </td>
                      </tr>
                      <tr>
                        <td align="right"><div class="tableheadtext">Response Required Date</div></td>
                        <td>
                          <#--<input type="text" class="inputBox" size="23" <ofbiz:inputvalue entityAttr="custRequest" field="responseRequiredDate" fullattrs="true"/>>-->
                          <input type="text" class="inputBox" size="23" name="responseRequiredDate" value="<#if custRequest?exists>${custRequest.responseRequiredDate?if_exists}</#if>">
                          <a href="javascript:call_cal(document.custRequestForm.responseRequiredDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                        </td>
                      </tr>
                      <tr>
                        <td align="right"><div class="tableheadtext">RequestType</div></td>
                        <td>
                          <select name="custRequestTypeId" class="selectBox">
                            <#list custRequestTypes as custRequestType>
                              <option <#if custRequest?exists><#if (custRequest.custRequestTypeId = custRequestType.custRequestTypeId)>selected</#if></#if> value="${custRequestType.custRequestTypeId}">${custRequestType.description}</option>
                            </#list>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td align="right"><div class="tableheadtext">Status</div></td>
                        <td>
                        <select name="statusId" class="selectBox">
                        <#if custRequest?exists>
                            <option value='${custRequest.getString("statusId")}'>
                                <#if currentStatusItem?exists>
                                    ${currentStatusItem.description}
                                <#else>
                                    [${custRequest.statusId?default("")}]
                                </#if>
                            </option>
                            <option value='${custRequest.statusId}'>----</option>
                        </#if>
                        <#list statusItems as statusItem>
                            <option value='${statusItem.statusId}'>
                                ${statusItem.description}
                            </option>
			</#list>
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
                        <td><input type="text" class="inputBox" size="50" name="custRequestName" value="<#if custRequest?exists>${custRequest.custRequestName?if_exists}</#if>"></td>
                      </tr>
                      <tr>
                        <td align="right"><div class="tableheadtext">Description</div></td>
                        <td><input type="text" class="inputBox" size="50" name="description" value="<#if custRequest?exists>${custRequest.description?if_exists}</#if>"></td>
                      </tr>

                      <#if custRequest?exists>
                      <#else>
                      <tr>
                        <td align="right"><div class="tableheadtext">Requesting Party</div></td>
                        <td><input type="text" name="requestPartyId" class="inputBox" size="20" value="${partyId?if_exists}"></td>
                      </tr>
                      </#if>

                      <tr>
                        <#if custRequest?exists>
                          <td align="right"><input type="submit" class="smallSubmit" value="Update"></td>
                        <#else>
                          <td align="right"><input type="submit" class="smallSubmit" value="Create"></td>
                        </#if>
                        <td>&nbsp</td>
                      </tr>
                    </table>
                    </form>
