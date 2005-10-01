<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<div align="center"><img src="/HotelSimple/images/logo.GIF"></div>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td class="credits" style="color:#FFFFFF" height="35" bgcolor="#666666" align="center" valign="bottom"> 


                <#if sessionAttributes.autoName?has_content>
              					${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName}!
                (${uiLabelMap.CommonNotYou}?&nbsp;<a style="color:#FFFFFF;font:11px Verdana, Arial, Helvetica, sans-serif;text-decoration:none;" href="<@ofbizUrl>autoLogout</@ofbizUrl>">${uiLabelMap.CommonClickHere}</a>)
              
          <#else>
              ${uiLabelMap.CommonWelcome}!
          </#if>
          <br/>
           - <a style="color:#FFFFFF;font:11px Verdana, Arial, Helvetica, sans-serif;text-decoration:none;" href="<@ofbizUrl>showcart</@ofbizUrl>">CURRENT ORDER</a> - 
          <#if sessionAttributes.autoName?has_content><br/>
              <a style="color:#FFFFFF;font:11px Verdana, Arial, Helvetica, sans-serif;text-decoration:none;" href="<@ofbizUrl>viewprofile</@ofbizUrl>">PROFILE</a> - 
              <a style="color:#FFFFFF;font:11px Verdana, Arial, Helvetica, sans-serif;text-decoration:none;" href="<@ofbizUrl>orderhistory</@ofbizUrl>">ORDER HISTORY</a> - 
              <a style="color:#FFFFFF;font:11px Verdana, Arial, Helvetica, sans-serif;text-decoration:none;" href="<@ofbizUrl>contactus</@ofbizUrl>">CONTACT US</a> -
              <a style="color:#FFFFFF;font:11px Verdana, Arial, Helvetica, sans-serif;text-decoration:none;" href="<@ofbizUrl>autoLogout</@ofbizUrl>">LOGOUT(close-window)</a>
          <#--else>
            - <a style="color:#FFFFFF;font:11px Verdana, Arial, Helvetica, sans-serif;text-decoration:none;" href="<@ofbizUrl>checkLogin</@ofbizUrl>">LOGIN/REGISTER</a - -->
          </#if>

    </td>
  </tr>
</table>


