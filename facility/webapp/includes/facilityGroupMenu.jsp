<%--
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

<%if(facilityGroupId != null && facilityGroupId.length() > 0) {%>
  <div class='tabContainer'>
    <a href="<ofbiz:url>/EditFacilityGroup?facilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButtonSelected">Facility Group</a>
    <a href="<ofbiz:url>/EditFacilityGroupRollup?showFacilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButtonSelected">Rollups</a>
    <a href="<ofbiz:url>/EditFacilityGroupMembers?facilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButtonSelected">Facilities</a>
	<a href="<ofbiz:url>/EditFacilityGroupRoles?facilityGroupId=<%=facilityGroupId%></ofbiz:url>" class="tabButtonSelected">Roles</a>
  </div>
<%}%>
