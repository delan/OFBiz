
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*, org.ofbiz.core.service.ModelService" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>
<%
    String searchString = "";
    if (request.getParameter("first_name") != null || request.getParameter("last_name") != null) {
        searchString += "first_name=" + request.getParameter("first_name") + "&last_name=" + request.getParameter("last_name");
    }
    if (request.getParameter("group_name") != null) {
        if (searchString.length() > 0) searchString += "&";
        searchString += "group_name=" + request.getParameter("group_name");
    }
    if (request.getParameter("email") != null) {
        if (searchString.length() > 0) searchString += "&";
        searchString += "email=" + request.getParameter("email");
    }
    if (request.getParameter("userlogin_id") != null) {
        if (searchString.length() > 0) searchString += "&";
        searchString += "userlogin_id=" + request.getParameter("userlogin_id");
    }

    Collection parties = null;

    //cache by the search string

    String lastSearchString = (String) session.getAttribute("LAST_SEARCH_STRING");
    Collection lastParties = (Collection) session.getAttribute("LAST_SEARCH_VALUES");
    if (lastParties != null && lastSearchString != null && lastSearchString.equals(searchString)) {
        parties = lastParties;
        pageContext.setAttribute("parties", parties);
        Debug.logInfo("Got parties from cache, size is " + parties.size());
    } else {
%>
        <ofbiz:if name="first_name">
          <ofbiz:service name="getPartyFromName">
            <ofbiz:param name="firstName" attribute="first_name"/>
            <ofbiz:param name="lastName" attribute="last_name"/>
          </ofbiz:service>
        </ofbiz:if>
        <ofbiz:unless name="first_name">
          <ofbiz:if name="last_name">
            <ofbiz:service name="getPartyFromName">
              <ofbiz:param name="firstName" attribute="first_name"/>
              <ofbiz:param name="lastName" attribute="last_name"/>
            </ofbiz:service>
          </ofbiz:if>
        </ofbiz:unless>

        <ofbiz:if name="group_name">
          <ofbiz:service name="getPartyFromGroupName">
            <ofbiz:param name="groupName" attribute="group_name"/>
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
<%
        parties = (Collection) pageContext.getAttribute("parties");
        if (parties != null) {
            session.setAttribute("LAST_SEARCH_STRING", searchString);
            session.setAttribute("LAST_SEARCH_VALUES", parties);
            Debug.logInfo("Got parties from QUERY, size is " + parties.size());
        }
    }
%>

<%
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
          <td width="100%"><div class="boxhead">Find Parties</div></td>
          <%--<td width="50%" align='right'><a href="<ofbiz:url>/editperson?create_new=Y</ofbiz:url>" class="lightbuttontext">[Create Person]</a></td>--%>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
          <form method="post" action="<ofbiz:url>/viewprofile</ofbiz:url>" name="viewprofileform">
            <tr>
              <td width="25%" align=right><div class="tabletext">Party ID</div></td>
              <td width="40%">
                <input type="text" name="party_id" size="20" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("party_id"))%>'>
              </td>
              <td width="35%"><a href="javascript:document.viewprofileform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findnameform">
            <tr>
              <td width="25%" align=right><div class="tabletext">First Name</div></td>
              <td width="40%">
                <input type="text" name="first_name" size="30" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("first_name"))%>'>
              </td>
              <td width="35%">&nbsp;</td>
            </tr>
            <tr>
              <td width="25%" align=right><div class="tabletext">Last Name</div></td>
              <td width="40%">
                <input type="text" name="last_name" size="30" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("last_name"))%>'>
              </td>
              <td width="35%"><a href="javascript:document.findnameform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findgroupnameform">
            <tr>
              <td width="25%" align=right><div class="tabletext">Party Group Name</div></td>
              <td width="40%">
                <input type="text" name="group_name" size="30" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("group_name"))%>'>
              </td>
              <td width="35%"><a href="javascript:document.findgroupnameform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findemailform">
            <tr>
              <td width="25%" align=right><div class="tabletext">E-Mail Address</div></td>
              <td width="40%">
                <input type="text" name="email" size="30" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("email"))%>'>
              </td>
              <td width="35%"><a href="javascript:document.findemailform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<ofbiz:url>/findparty</ofbiz:url>" name="findloginform">
            <tr>
              <td width="25%" align=right><div class="tabletext">User Login ID</div></td>
              <td width="40%">
                <input type="text" name="userlogin_id" size="30" style="font-size: small;" value='<%=UtilFormatOut.checkNull(request.getParameter("userlogin_id"))%>'>
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
          <td width="20%"><div class="head3">User Login</div></td>
          <ofbiz:if name="group_name">
            <td colspan="2" width="40%"><div class="head3">Party Group Name</div></td>
          </ofbiz:if>
          <ofbiz:unless name="group_name">
            <td width="20%"><div class="head3">Last Name</div></td>
            <td width="20%"><div class="head3">First Name</div></td>
          </ofbiz:unless>
          <td width="15%"><div class="head3">Type</div></td>
          <td width="15%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan='6'><hr class='sepbar'></td>
        </tr>
        <ofbiz:if name="parties">
            <% String rowClass = "viewManyTR2"; %>
            <ofbiz:iterator name="partyMap" property="parties" expandMap="true" type="java.util.Map" offset="<%=lowIndex%>" limit="<%=viewSize%>">
              <%
                  rowClass = rowClass.equals("viewManyTR2") ? "viewManyTR1" : "viewManyTR2";
                  GenericValue party = (GenericValue) pageContext.getAttribute("party");
                  if (party != null && pageContext.getAttribute("lookupPerson") == null && pageContext.getAttribute("lookupGroup") == null) {
                      //this is a bit complicated, many inherited types, so use special method
                      GenericValue curPartyType = party.getRelatedOneCache("PartyType");
                      GenericValue partyPersonType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", "PERSON"));
                      if (EntityTypeUtil.isType(curPartyType, partyPersonType)) {
                          GenericValue person = party.getRelatedOne("Person");
                          if (person != null) {
                              partyMap.put("person", person);
                              pageContext.setAttribute("lookupPerson", person);
                          }
                      } else {
                          GenericValue partyGroupType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", "PARTY_GROUP"));
                          if (EntityTypeUtil.isType(curPartyType, partyGroupType)) {
                              GenericValue partyGroup = party.getRelatedOne("PartyGroup");
                              if (partyGroup != null) {
                                  partyMap.put("partyGroup", partyGroup);
                                  pageContext.setAttribute("lookupGroup", partyGroup);
                              }
                          }
                      }
                  }
              %>
              <tr class="<%=rowClass%>">
                <td><a href='<ofbiz:url>/viewprofile?party_id=<ofbiz:entityfield attribute="party" field="partyId"/></ofbiz:url>' class="buttontext"><ofbiz:entityfield attribute="party" field="partyId"/></a></td>
                <td>
                    <%
                        List userLogins = null;
                        if (party != null) {
                            userLogins = (List) delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", party.getString("partyId")));
                        }
                        StringBuffer sb = new StringBuffer();
                        Iterator uli = UtilMisc.toIterator(userLogins);
                        while (uli != null && uli.hasNext()) {
                            GenericValue curUserLogin = (GenericValue) uli.next();
                            sb.append(curUserLogin.getString("userLoginId"));
                            if (uli.hasNext()) {
                                sb.append(", ");
                            }
                        }
                        String userLoginString = sb.toString();
                    %>
                    <div class="tabletext"><%=userLoginString%></div>
                </td>
                <ofbiz:if name="lookupPerson">
                    <td><div class="tabletext"><ofbiz:entityfield attribute="lookupPerson" field="lastName"/></div></td>
                    <td><div class="tabletext"><ofbiz:entityfield attribute="lookupPerson" field="firstName"/></div></td>
                </ofbiz:if>
                <ofbiz:unless name="lookupPerson">
                    <ofbiz:if name="lookupGroup">
                        <td colspan='2'><div class="tabletext"><ofbiz:entityfield attribute="lookupGroup" field="groupName"/></div></td>
                    </ofbiz:if>
                    <ofbiz:unless name="lookupGroup">
                        <td><div class="tabletext">&nbsp;</div></td>
                        <td><div class="tabletext">&nbsp;</div></td>
                    </ofbiz:unless>
                </ofbiz:unless>
                <td><div class="tabletext"><ofbiz:entityfield attribute="party" field="partyTypeId"/></div></td>
                <td align="right">
                  <!-- this is all on one line so that no break will be inserted -->
                  <div class="tabletext"><nobr>
                    <a href='<ofbiz:url>/viewprofile?party_id=<ofbiz:entityfield attribute="party" field="partyId"/></ofbiz:url>' class="buttontext">[View&nbsp;Profile]</a>&nbsp;
                    <a href='/ordermgr/control/orderlist?partyId=<ofbiz:entityfield attribute="party" field="partyId"/>' class="buttontext">[Orders]</a>&nbsp;
                    <a href='/ordermgr/control/salesentry?partyId=<ofbiz:entityfield attribute="party" field="partyId"/>' class="buttontext">[New Order]</a>&nbsp;
                  </nobr></div>
                </td>
              </tr>
              <%pageContext.removeAttribute("lookupPerson");%>
              <%pageContext.removeAttribute("lookupGroup");%>
            </ofbiz:iterator>
        </ofbiz:if>
        <ofbiz:if name="errorMessage">
          <tr>
            <td colspan='4'><div class="head3"><ofbiz:print attribute="errorMessage"/></div></td>
          </tr>
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
