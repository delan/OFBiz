
<%
/**
 *  Title: Standard Time Period Entity
 *  Description: None
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
 *@created    Fri Jul 27 01:37:00 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.common.period.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewStandardTimePeriod"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("STANDARD_TIME_PERIOD", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("STANDARD_TIME_PERIOD", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("STANDARD_TIME_PERIOD", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("STANDARD_TIME_PERIOD", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String standardTimePeriodId = request.getParameter("STANDARD_TIME_PERIOD_STANDARD_TIME_PERIOD_ID");  


  StandardTimePeriod standardTimePeriod = StandardTimePeriodHelper.findByPrimaryKey(standardTimePeriodId);
%>

<br>
<SCRIPT language='JavaScript'>  
function ShowViewTab(lname) 
{
    document.all.viewtab.className = (lname == 'view') ? 'ontab' : 'offtab';
    document.all.viewlnk.className = (lname == 'view') ? 'onlnk' : 'offlnk';
    document.all.viewarea.style.visibility = (lname == 'view') ? 'visible' : 'hidden';

    document.all.edittab.className = (lname == 'edit') ? 'ontab' : 'offtab';
    document.all.editlnk.className = (lname == 'edit') ? 'onlnk' : 'offlnk';
    document.all.editarea.style.visibility = (lname == 'edit') ? 'visible' : 'hidden';
}
</SCRIPT>
<table cellpadding='0' cellspacing='0'><tr>  
  <td id=viewtab class=ontab>
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View StandardTimePeriod</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit StandardTimePeriod</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: StandardTimePeriod with (STANDARD_TIME_PERIOD_ID: <%=standardTimePeriodId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindStandardTimePeriod")%>" class="buttontext">[Find StandardTimePeriod]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewStandardTimePeriod")%>" class="buttontext">[Create New StandardTimePeriod]</a>
<%}%>
<%if(standardTimePeriod != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateStandardTimePeriod?UPDATE_MODE=DELETE&" + "STANDARD_TIME_PERIOD_STANDARD_TIME_PERIOD_ID=" + standardTimePeriodId)%>" class="buttontext">[Delete this StandardTimePeriod]</a>
  <%}%>
<%}%>

<%if(standardTimePeriod == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(standardTimePeriod == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified StandardTimePeriod was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>STANDARD_TIME_PERIOD_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(standardTimePeriod.getStandardTimePeriodId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PERIOD_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(standardTimePeriod.getPeriodTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(standardTimePeriod != null)
        {
          java.util.Date date = standardTimePeriod.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>THRU_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(standardTimePeriod != null)
        {
          java.util.Date date = standardTimePeriod.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
    </td>
  </tr>

<%} //end if standardTimePeriod == null %>
</table>
  </div>
<%StandardTimePeriod standardTimePeriodSave = standardTimePeriod;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(standardTimePeriod == null && (standardTimePeriodId != null)){%>
    StandardTimePeriod with (STANDARD_TIME_PERIOD_ID: <%=standardTimePeriodId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    standardTimePeriod = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateStandardTimePeriod")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(standardTimePeriod == null){%>
  <%if(hasCreatePermission){%>
    You may create a StandardTimePeriod by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>STANDARD_TIME_PERIOD_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="STANDARD_TIME_PERIOD_STANDARD_TIME_PERIOD_ID" value="<%=UtilFormatOut.checkNull(standardTimePeriodId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a StandardTimePeriod (STANDARD_TIME_PERIOD_ADMIN, or STANDARD_TIME_PERIOD_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="STANDARD_TIME_PERIOD_STANDARD_TIME_PERIOD_ID" value="<%=standardTimePeriodId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>STANDARD_TIME_PERIOD_ID</td>
      <td>
        <b><%=standardTimePeriodId%></b> (This cannot be changed without re-creating the standardTimePeriod.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a StandardTimePeriod (STANDARD_TIME_PERIOD_ADMIN, or STANDARD_TIME_PERIOD_UPDATE needed).
  <%}%>
<%} //end if standardTimePeriod == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PERIOD_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="STANDARD_TIME_PERIOD_PERIOD_TYPE_ID" value="<%if(standardTimePeriod!=null){%><%=UtilFormatOut.checkNull(standardTimePeriod.getPeriodTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("STANDARD_TIME_PERIOD_PERIOD_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FROM_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(standardTimePeriod != null)
        {
          java.util.Date date = standardTimePeriod.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("STANDARD_TIME_PERIOD_FROM_DATE_DATE");
          timeString = request.getParameter("STANDARD_TIME_PERIOD_FROM_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="STANDARD_TIME_PERIOD_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.STANDARD_TIME_PERIOD_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="STANDARD_TIME_PERIOD_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>THRU_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(standardTimePeriod != null)
        {
          java.util.Date date = standardTimePeriod.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("STANDARD_TIME_PERIOD_THRU_DATE_DATE");
          timeString = request.getParameter("STANDARD_TIME_PERIOD_THRU_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="STANDARD_TIME_PERIOD_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.STANDARD_TIME_PERIOD_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="STANDARD_TIME_PERIOD_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>
  </div>
<%}%>
</div>
<%if((hasUpdatePermission || hasCreatePermission) && standardTimePeriod == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the standardTimePeriod for cases when removed to retain passed form values --%>
<%standardTimePeriod = standardTimePeriodSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=0;
function ShowTab(lname) 
{
  for(inc=1; inc <= numTabs; inc++)
  {
    document.all['tab' + inc].className = (lname == 'tab' + inc) ? 'ontab' : 'offtab';
    document.all['lnk' + inc].className = (lname == 'tab' + inc) ? 'onlnk' : 'offlnk';
    document.all['area' + inc].style.visibility = (lname == 'tab' + inc) ? 'visible' : 'hidden';
  }
}
</SCRIPT>
<%if(standardTimePeriod != null){%>
<table cellpadding='0' cellspacing='0'><tr>
</tr></table>
<%}%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (STANDARD_TIME_PERIOD_ADMIN, or STANDARD_TIME_PERIOD_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
