
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>

<ofbiz:if name="first_name">
  <ofbiz:service name="getPartyFromName">
    <ofbiz:param name="firstName" attribute="first_name"/>
    <ofbiz:param name="lastName" attribute="last_name"/>
  </ofbiz:service> 
</ofbiz:if>

<ofbiz:if name="last_name">
  <ofbiz:service name="getPartyFromName">
    <ofbiz:param name="firstName" attribute="first_name"/>
    <ofbiz:param name="lastName" attribute="last_name"/>
  </ofbiz:service>
</ofbiz:if>

<ofbiz:if name="email">
  <ofbiz:service name="getPartyFromEmail">
    <ofbiz:param name="email" attribute="email"/>
  </ofbiz:service>
</ofbiz:if>

<ofbiz:if name="userlogin_id">
  <ofbiz:service name="getPartyFromUserLogin">
    <ofbiz:param name="userLoginId" attribute="userlogin_id"/>
  </ofbiz:service>
</ofbiz:if>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="100%"><div class="boxhead">Find Parties</div></td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
          <form method="post" action="<ofbiz:url>/viewprofile</ofbiz:url>" name="viewprofileform">
            <tr>
              <td width="25%" align=right><div class="tabletext">Party ID</div></td>
              <td width="40%">
                <input type="text" name="party_id" size="20" value='<%=UtilFormatOut.checkNull(request.getParameter("party_id"))%>'>
              </td>
              <td width="35%"><a href="javascript:document.viewprofileform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findnameform">
            <tr>
              <td width="25%" align=right><div class="tabletext">First Name</div></td>
              <td width="40%">
                <input type="text" name="first_name" size="30" value='<%=UtilFormatOut.checkNull(request.getParameter("first_name"))%>'>
              </td>
              <td width="35%">&nbsp;</td>
            </tr>
            <tr>
              <td width="25%" align=right><div class="tabletext">Last Name</div></td>
              <td width="40%">
                <input type="text" name="last_name" size="30" value='<%=UtilFormatOut.checkNull(request.getParameter("last_name"))%>'>
              </td>
              <td width="35%"><a href="javascript:document.findnameform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findemailform">
            <tr>
              <td width="25%" align=right><div class="tabletext">E-Mail Address</div></td>
              <td width="40%">
                <input type="text" name="email" size="30" value='<%=UtilFormatOut.checkNull(request.getParameter("email"))%>'>
              </td>
              <td width="35%"><a href="javascript:document.findemailform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findloginform">
            <tr>
              <td width="25%" align=right><div class="tabletext">User Login ID</div></td>
              <td width="40%">
                <input type="text" name="userlogin_id" size="30" value='<%=UtilFormatOut.checkNull(request.getParameter("userlogin_id"))%>'>
              </td>
              <td width="35%"><a href="javascript:document.findloginform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>
    </table>
</table>

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="100%"><div class="boxhead">Parties Found</div></td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td width="15%"><div class="head3">PartyID</div></td>
          <td width="40%"><div class="head3">Name</div></td>
          <td width="15%"><div class="head3">Type</div></td>
          <td width="30%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan='4'><hr class='sepbar'></td>
        </tr>
        <ofbiz:if name="parties">
            <ofbiz:iterator name="party" property="parties">
              <tr>
                <td><div class="tabletext"><ofbiz:entityfield attribute="party" field="partyId"/></div></td>
                <ofbiz:service name="getPerson">
                  <ofbiz:param name="partyId" map="party" attribute="partyId"/>
                </ofbiz:service>
                <ofbiz:unless name="person">
                  <td><div class="tabletext">&nbsp;</div></td>
                </ofbiz:unless>
                <ofbiz:if name="person">
                  <td><div class="tabletext"><ofbiz:entityfield attribute="person" field="firstName"/> <ofbiz:entityfield attribute="person" field="lastName"/></div></td>
                </ofbiz:if>
                <td><div class="tabletext"><ofbiz:entityfield attribute="party" field="partyTypeId"/></div></td>
                <td align="right">
                  <a href="<ofbiz:url>/viewprofile?party_id=<ofbiz:entityfield attribute="party" field="partyId"/></ofbiz:url>" class="buttontext">[View Profile]</a>&nbsp;&nbsp;
                  <a href="/ordermgr/control/orderlist?partyId=<ofbiz:entityfield attribute="party" field="partyId"/>" class="buttontext">[Orders]</a>&nbsp;&nbsp;
                </td>
              </tr>
            </ofbiz:iterator>
        </ofbiz:if>
        <ofbiz:unless name="parties">
          <tr>
            <td colspan='4'><div class='head3'>No parties found.</div></td>
          </tr>
        </ofbiz:unless>
      </table>
    </TD>
  </TR>
</TABLE>
<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
