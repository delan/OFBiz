<#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="100%"><div class="boxhead">Find Parties</div></td>
          <#--<td width="50%" align='right'><a href="<@ofbizUrl>/editperson?create_new=Y</@ofbizUrl>" class="lightbuttontext">[Create Person]</a></td>-->
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
          <form method="post" action="<@ofbizUrl>/viewprofile</@ofbizUrl>" name="viewprofileform">
            <tr>
              <td width="25%" align=right><div class="tabletext">Party ID</div></td>
              <td width="40%">
                <input type="text" name="party_id" size="20" class="inputBox" value='${requestParameters.party_id?if_exists}'>
              </td>
              <td width="35%">
                <a href="javascript:document.viewprofileform.submit()" class="buttontext">[Lookup]</a>
                &nbsp;
                <a href="<@ofbizUrl>/findparty?findAll=true</@ofbizUrl>" class="buttontext">[Find All]</a>
              </td>
            </tr>
          </form>

          <form method="post" action="<@ofbizUrl>/findparty</@ofbizUrl>" name="findnameform">
            <tr>
              <td width="25%" align=right><div class="tabletext">First Name</div></td>
              <td width="40%">
                <input type="text" name="first_name" size="30" class="inputBox" value='${requestParameters.first_name?if_exists}'>
              </td>
              <td width="35%">&nbsp;</td>
            </tr>
            <tr>
              <td width="25%" align=right><div class="tabletext">Last Name</div></td>
              <td width="40%">
                <input type="text" name="last_name" size="30" class="inputBox" value='${requestParameters.last_name?if_exists}'>
              </td>
              <td width="35%"><a href="javascript:document.findnameform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<@ofbizUrl>/findparty</@ofbizUrl>" name="findgroupnameform">
            <tr>
              <td width="25%" align=right><div class="tabletext">Party Group Name</div></td>
              <td width="40%">
                <input type="text" name="group_name" size="30" class="inputBox" value='${requestParameters.group_name?if_exists}'>
              </td>
              <td width="35%"><a href="javascript:document.findgroupnameform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<@ofbizUrl>/findparty</@ofbizUrl>" name="findemailform">
            <tr>
              <td width="25%" align=right><div class="tabletext">E-Mail Address</div></td>
              <td width="40%">
                <input type="text" name="email" size="30" class="inputBox" value='${requestParameters.email?if_exists}'>
              </td>
              <td width="35%"><a href="javascript:document.findemailform.submit()" class="buttontext">[Lookup]</a></td>
            </tr>
          </form>

          <form method="post" action="<@ofbizUrl>/viewprofile</@ofbizUrl>" name="findloginform">
            <tr>
              <td width="25%" align=right><div class="tabletext">User Login ID</div></td>
              <td width="40%">
                <input type="text" name="userlogin_id" size="30" class="inputBox" value='${requestParameters.userlogin_id?if_exists}'>
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
              <#if parties?has_content>
                <#if (viewIndex > 0)>
                  <a href="<@ofbizUrl>/findparty?${searchString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)}</@ofbizUrl>" class="lightbuttontext">[Previous]</a> |
                </#if>
                <#if (parties?size > 0)>
                  ${lowIndex+1} - ${highIndex} of ${parties?size}
                </#if>
                <#if (parties?size > highIndex)>
                  | <a href="<@ofbizUrl>/findparty?${searchString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)}</@ofbizUrl>" class="lightbuttontext">[Next]</a>
                </#if>
              </#if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td width="10%"><div class="head3">PartyID</div></td>
          <td width="20%"><div class="head3">User Login</div></td>
          <#if group_name?has_content>
            <td colspan="2" width="40%"><div class="head3">Party Group Name</div></td>
          <#else>
            <td width="20%"><div class="head3">Last Name</div></td>
            <td width="20%"><div class="head3">First Name</div></td>
          </#if>
          <td width="15%"><div class="head3">Type</div></td>
          <td width="15%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan='6'><hr class='sepbar'></td>
        </tr>
        <#if parties?exists>        	
            <#list parties as partyMap> <#--" property="parties" expandMap="true" type="java.util.Map" offset="<%=lowIndex%>" limit="<%=viewSize%>">-->
              <#if partyMap_index % 2 = 0>
              	<#assign rowClass = "viewManyTR1">
              <#else>
                <#assign rowClass = "viewManyTR2">
              </#if>
              <tr class="${rowClass}">
                <td><a href='<@ofbizUrl>/viewprofile?party_id=${partyMap.party.partyId}</@ofbizUrl>' class="buttontext">${partyMap.party.partyId}</a></td>
                <td>
                    <div class="tabletext">${partyMap.userLogins?if_exists}</div>
                </td>
                <#if partyMap.person?has_content>
                    <td><div class="tabletext">${partyMap.person.lastName?if_exists}</div></td>
                    <td><div class="tabletext">${partyMap.person.firstName?if_exists}</div></td>
                <#elseif partyMap.group?has_content>
                	<td colspan='2'><div class="tabletext">${partyMap.group.groupName}</div></td>
                <#else>
                	<td><div class="tabletext">&nbsp;</div></td>
                     <td><div class="tabletext">&nbsp;</div></td>
                </#if>
                <td><div class="tabletext">${partyMap.party.partyTypeId?if_exists}</div></td>
                <td align="right">
                  <!-- this is all on one line so that no break will be inserted -->
                  <div class="tabletext"><nobr>
                    <a href='<@ofbizUrl>/viewprofile?party_id=${partyMap.party.partyId}</@ofbizUrl>' class="buttontext">[View&nbsp;Profile]</a>&nbsp;
                    <#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
                      <a href='/ordermgr/control/findorders?lookupFlag=Y&partyId=${partyMap.party.partyId + externalKeyParam}' class="buttontext">[Orders]</a>&nbsp;
                    </#if>
                    <#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
                      <a href='/ordermgr/control/orderentry?mode=SALES_ORDER&partyId=${partyMap.party.partyId + externalKeyParam}' class="buttontext">[New Order]</a>&nbsp;
                    </#if>
                  </nobr></div>
                </td>
              </tr>
            </#list>
        </#if>
        <#if errorMessage?has_content>
          <tr>
            <td colspan='4'><div class="head3"><ofbiz:print attribute="errorMessage"/></div></td>
          </tr>
        </#if>
        <#if !parties?has_content>
          <tr>
            <td colspan='4'><div class='head3'>No parties found.</div></td>
          </tr>
        </#if>
      </table>
    </TD>
  </TR>
</TABLE>
<#else>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
</#if>
