
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>
  <%String searchString = "";%>

<ofbiz:if name="first_name">
  <%searchString = "first_name=" + request.getParameter("first_name") + "&last_name=" + request.getParameter("last_name");%>
  <ofbiz:service name="getPartyFromName">
    <ofbiz:param name="firstName" attribute="first_name"/>
    <ofbiz:param name="lastName" attribute="last_name"/>
  </ofbiz:service> 
</ofbiz:if>
<ofbiz:unless name="first_name">
  <ofbiz:if name="last_name">
    <%searchString = "first_name=" + request.getParameter("first_name") + "&last_name=" + request.getParameter("last_name");%>
    <ofbiz:service name="getPartyFromName">
      <ofbiz:param name="firstName" attribute="first_name"/>
      <ofbiz:param name="lastName" attribute="last_name"/>
    </ofbiz:service>
  </ofbiz:if>
</ofbiz:unless>

<ofbiz:if name="email">
  <%searchString = "email=" + request.getParameter("email");%>
  <ofbiz:service name="getPartyFromEmail">
    <ofbiz:param name="email" attribute="email"/>
  </ofbiz:service>
</ofbiz:if>

<ofbiz:if name="userlogin_id">
  <%searchString = "userlogin_id=" + request.getParameter("userlogin_id");%>
  <ofbiz:service name="getPartyFromUserLogin">
    <ofbiz:param name="userLoginId" attribute="userlogin_id"/>
  </ofbiz:service>
</ofbiz:if>

<%
    Collection parties = (Collection) pageContext.getAttribute("parties");

    int viewIndex = 0;
    int viewSize = 10;
    int highIndex = 0;
    int lowIndex = 0;
    int listSize = 0;

    try {
        viewIndex = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_INDEX")).intValue();
    } catch (Exception e) {
        viewIndex = 0;
    }
    try {
        viewSize = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_SIZE")).intValue();
    } catch (Exception e) {
        viewSize = 10;
    }
    if (parties != null) {
        listSize = parties.size();
    }
    lowIndex = viewIndex * viewSize;
    highIndex = (viewIndex + 1) * viewSize;
    if (listSize < highIndex) {
        highIndex = listSize;
    }
%>

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="50%"><div class="boxhead">Find Parties</div></td>
          <td width="50%" align='right'><a href="<ofbiz:url>/editperson?create_new=Y</ofbiz:url>" class="lightbuttontext">[Create Person]</a></td>
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
          <td width="50%"><div class="boxhead">Parties Found</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <ofbiz:if name="parties" size="0">
                <%if (viewIndex > 0) {%>
                  <a href="<ofbiz:url><%="/findparty?" + searchString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="lightbuttontext">[Previous]</a> |
                <%}%>
                <%if (listSize > 0) {%>
                  <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
                <%}%>
                <%if (listSize > highIndex) {%>
                  | <a href="<ofbiz:url><%="/findparty?" + searchString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="lightbuttontext">[Next]</a>
                <%}%>
              </ofbiz:if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td width="10%"><div class="head3">PartyID</div></td>
          <td width="20%"><div class="head3">Last Name</div></td>
          <td width="20%"><div class="head3">First Name</div></td>
          <td width="20%"><div class="head3">User Login</div></td>
          <td width="15%"><div class="head3">Type</div></td>
          <td width="15%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan='6'><hr class='sepbar'></td>
        </tr>
        <ofbiz:if name="parties">
            <ofbiz:iterator name="party" property="parties" offset="<%=lowIndex%>" limit="<%=viewSize%>">
              <tr>
                <td><a href='<ofbiz:url>/viewprofile?party_id=<ofbiz:entityfield attribute="party" field="partyId"/></ofbiz:url>' class="buttontext"><ofbiz:entityfield attribute="party" field="partyId"/></a></td>
                <ofbiz:service name="getPerson">
                  <ofbiz:param name="partyId" map="party" attribute="partyId"/>
                </ofbiz:service>
                <%
                    List userLogins = (List) delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", party.getString("partyId")));
                    StringBuffer sb = new StringBuffer();
                    Iterator uli = userLogins.iterator();
                    while (uli.hasNext()) {
                        GenericValue v = (GenericValue) uli.next();
                        sb.append(v.getString("userLoginId"));
                        if (uli.hasNext())
                            sb.append(", ");
                    }
                    String userLoginString = sb.toString();
                %>
                <ofbiz:unless name="person">
                  <td><div class="tabletext">&nbsp;</div></td>
                  <td><div class="tabletext">&nbsp;</div></td>
                </ofbiz:unless>
                <ofbiz:if name="person">
                  <td><div class="tabletext"><ofbiz:entityfield attribute="person" field="lastName"/></div></td>
                  <td><div class="tabletext"><ofbiz:entityfield attribute="person" field="firstName"/></div></td>
                </ofbiz:if>
                <td><div class="tabletext"><%=userLoginString%></div></td>
                <td><div class="tabletext"><ofbiz:entityfield attribute="party" field="partyTypeId"/></div></td>
                <td align="right">
                  <a href='<ofbiz:url>/viewprofile?party_id=<ofbiz:entityfield attribute="party" field="partyId"/></ofbiz:url>' class="buttontext">[View Profile]</a>&nbsp;&nbsp;
                  <a href='/ordermgr/control/orderlist?partyId=<ofbiz:entityfield attribute="party" field="partyId"/>' class="buttontext">[Orders]</a>&nbsp;&nbsp;
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
