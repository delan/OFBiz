
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

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

<p class="head1">Find Party</p>

<table width="90%" border="0" cellpadding="2" cellspacing="0">
  <form method="post" action="<ofbiz:url>/viewprofile</ofbiz:url>" name="viewprofileform">
    <tr>
      <td width="25%" align=right><div class="tabletext">Party ID</div></td>
      <td width="40%">
        <input type="text" name="party_id" size="20">
      </td>
      <td width="35%"><a href="javascript:document.viewprofileform.submit()" class="buttontext">[Lookup]</a></td>
    </tr>
  </form>

  <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findnameform">
    <tr>
      <td width="25%" align=right><div class="tabletext">First Name</div></td>
      <td width="40%">
        <input type="text" name="first_name" size="30">
      </td>
      <td width="35%">&nbsp;</td>
    </tr>
    <tr>
      <td width="25%" align=right><div class="tabletext">Last Name</div></td>
      <td width="40%">
        <input type="text" name="last_name" size="30">
      </td>
      <td width="35%"><a href="javascript:document.findnameform.submit()" class="buttontext">[Lookup]</a></td>
    </tr>
  </form>
  
  <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findemailform">
    <tr>
      <td width="25%" align=right><div class="tabletext">E-Mail Address</div></td>
      <td width="40%">
        <input type="text" name="email" size="30">
      </td>
      <td width="35%"><a href="javascript:document.findemailform.submit()" class="buttontext">[Lookup]</a></td>
    </tr>
  </form>
  
  <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findloginform">
    <tr>
      <td width="25%" align=right><div class="tabletext">User Login ID</div></td>
      <td width="40%">
        <input type="text" name="userlogin_id" size="30">
      </td>
      <td width="35%"><a href="javascript:document.findloginform.submit()" class="buttontext">[Lookup]</a></td>
    </tr>
  </form>
</table>

<ofbiz:if name="parties">
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="25%"><div class="boxhead">PartyID</div></td>
          <td width="40%"><div class="boxhead">Name</div></td>
          <td width="25%"><div class="boxhead">Type</div></td>
          <td width="10%">&nbsp;</td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <ofbiz:iterator name="party" property="parties">
          <tr>
            <td width="25%">
              <div class="tabletext"><ofbiz:entityfield attribute="party" field="partyId"/></div>
            </td>
            <ofbiz:service name="getPerson">
              <ofbiz:param name="partyId" map="party" attribute="partyId"/>
            </ofbiz:service>
            <ofbiz:unless name="person">
              <td width="40%">
                <div class="tabletext">&nbsp;</div>
              </td>
            </ofbiz:unless>
            <ofbiz:if name="person">
              <td width="40%">
                <div class="tabletext"><ofbiz:entityfield attribute="person" field="firstName"/> <ofbiz:entityfield attribute="person" field="lastName"/></div>
              </td>
            </ofbiz:if>
            <td width="25%">
              <div class="tabletext"><ofbiz:entityfield attribute="party" field="partyTypeId"/></div>
            </td>
            <td width="10%" align="right">
              <a href="<ofbiz:url>/viewprofile?party_id=<ofbiz:entityfield attribute="party" field="partyId"/></ofbiz:url>" class="buttontext">[View]</a>&nbsp;&nbsp;
              <a href="/ordermgr/control/orderlist?partyId=<ofbiz:entityfield attribute="party" field="partyId"/>" class="buttontext">[Orders]</a>&nbsp;&nbsp;
            </td>
          </tr>
        </ofbiz:iterator>
      </table>
    </TD>
  </TR>
</TABLE>
</ofbiz:if>


