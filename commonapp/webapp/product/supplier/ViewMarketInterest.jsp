
<%
/**
 *  Title: Market Interest Entity
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
 *@created    Fri Jul 27 01:37:24 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.supplier.*" %>

<%@ page import="org.ofbiz.commonapp.product.category.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewMarketInterest"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("MARKET_INTEREST", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("MARKET_INTEREST", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("MARKET_INTEREST", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("MARKET_INTEREST", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productCategoryId = request.getParameter("MARKET_INTEREST_PRODUCT_CATEGORY_ID");  
  String partyTypeId = request.getParameter("MARKET_INTEREST_PARTY_TYPE_ID");  


  MarketInterest marketInterest = MarketInterestHelper.findByPrimaryKey(productCategoryId, partyTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View MarketInterest</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit MarketInterest</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: MarketInterest with (PRODUCT_CATEGORY_ID, PARTY_TYPE_ID: <%=productCategoryId%>, <%=partyTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindMarketInterest")%>" class="buttontext">[Find MarketInterest]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewMarketInterest")%>" class="buttontext">[Create New MarketInterest]</a>
<%}%>
<%if(marketInterest != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateMarketInterest?UPDATE_MODE=DELETE&" + "MARKET_INTEREST_PRODUCT_CATEGORY_ID=" + productCategoryId + "&" + "MARKET_INTEREST_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this MarketInterest]</a>
  <%}%>
<%}%>

<%if(marketInterest == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(marketInterest == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified MarketInterest was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(marketInterest.getProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(marketInterest.getPartyTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_DATE</b></td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(marketInterest != null)
        {
          java.util.Date date = marketInterest.getFromDate();
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
        if(marketInterest != null)
        {
          java.util.Date date = marketInterest.getThruDate();
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

<%} //end if marketInterest == null %>
</table>
  </div>
<%MarketInterest marketInterestSave = marketInterest;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(marketInterest == null && (productCategoryId != null || partyTypeId != null)){%>
    MarketInterest with (PRODUCT_CATEGORY_ID, PARTY_TYPE_ID: <%=productCategoryId%>, <%=partyTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    marketInterest = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateMarketInterest")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(marketInterest == null){%>
  <%if(hasCreatePermission){%>
    You may create a MarketInterest by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_CATEGORY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="MARKET_INTEREST_PRODUCT_CATEGORY_ID" value="<%=UtilFormatOut.checkNull(productCategoryId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="MARKET_INTEREST_PARTY_TYPE_ID" value="<%=UtilFormatOut.checkNull(partyTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a MarketInterest (MARKET_INTEREST_ADMIN, or MARKET_INTEREST_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="MARKET_INTEREST_PRODUCT_CATEGORY_ID" value="<%=productCategoryId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_CATEGORY_ID</td>
      <td>
        <b><%=productCategoryId%></b> (This cannot be changed without re-creating the marketInterest.)
      </td>
    </tr>
      <input type="hidden" name="MARKET_INTEREST_PARTY_TYPE_ID" value="<%=partyTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARTY_TYPE_ID</td>
      <td>
        <b><%=partyTypeId%></b> (This cannot be changed without re-creating the marketInterest.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a MarketInterest (MARKET_INTEREST_ADMIN, or MARKET_INTEREST_UPDATE needed).
  <%}%>
<%} //end if marketInterest == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>FROM_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(marketInterest != null)
        {
          java.util.Date date = marketInterest.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("MARKET_INTEREST_FROM_DATE_DATE");
          timeString = request.getParameter("MARKET_INTEREST_FROM_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="MARKET_INTEREST_FROM_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.MARKET_INTEREST_FROM_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="MARKET_INTEREST_FROM_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}%>
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>THRU_DATE</td>
    <td>
      <%{
        String dateString = null;
        String timeString = null;
        if(marketInterest != null)
        {
          java.util.Date date = marketInterest.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("MARKET_INTEREST_THRU_DATE_DATE");
          timeString = request.getParameter("MARKET_INTEREST_THRU_DATE_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="MARKET_INTEREST_THRU_DATE_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.MARKET_INTEREST_THRU_DATE_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="MARKET_INTEREST_THRU_DATE_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && marketInterest == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the marketInterest for cases when removed to retain passed form values --%>
<%marketInterest = marketInterestSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=2;
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
<%if(marketInterest != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> ProductCategory</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> PartyType</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductCategory, type: one --%>
<%if(marketInterest != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
    <%-- ProductCategory productCategoryRelated = ProductCategoryHelper.findByPrimaryKey(marketInterest.getProductCategoryId()); --%>
    <%ProductCategory productCategoryRelated = marketInterest.getProductCategory();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductCategory</b> with (PRODUCT_CATEGORY_ID: <%=marketInterest.getProductCategoryId()%>)
    </div>
    <%if(marketInterest.getProductCategoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + marketInterest.getProductCategoryId())%>" class="buttontext">[View ProductCategory]</a>      
    <%if(productCategoryRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + marketInterest.getProductCategoryId())%>" class="buttontext">[Create ProductCategory]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productCategoryRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductCategory was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRelated.getProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productCategoryRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategory, type: one --%>
  

<%-- Start Relation for PartyType, type: one --%>
<%if(marketInterest != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <%-- PartyType partyTypeRelated = PartyTypeHelper.findByPrimaryKey(marketInterest.getPartyTypeId()); --%>
    <%PartyType partyTypeRelated = marketInterest.getPartyType();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>PartyType</b> with (PARTY_TYPE_ID: <%=marketInterest.getPartyTypeId()%>)
    </div>
    <%if(marketInterest.getPartyTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyType?" + "PARTY_TYPE_PARTY_TYPE_ID=" + marketInterest.getPartyTypeId())%>" class="buttontext">[View PartyType]</a>      
    <%if(partyTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyType?" + "PARTY_TYPE_PARTY_TYPE_ID=" + marketInterest.getPartyTypeId())%>" class="buttontext">[Create PartyType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified PartyType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getPartyTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(partyTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if partyTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyType, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (MARKET_INTEREST_ADMIN, or MARKET_INTEREST_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
