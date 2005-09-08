<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
<center><img src="/HotelSimple/images/logo.GIF"></center>
</tr>
  <tr>
    <td height="35" bgcolor="#666666"> 
      <div align="center" valign="bottom">
        <p span class="credits">
                <#if sessionAttributes.autoName?has_content><font color="#FFFFFF">
              					${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName}!
                (${uiLabelMap.CommonNotYou}?&nbsp;</font><a href="<@ofbizUrl>/autoLogout</@ofbizUrl>" class="buttontext"><font color="#FFFFFF">${uiLabelMap.CommonClickHere}</font></a>)
              
          <#else>
              <font color="#FFFFFF">${uiLabelMap.CommonWelcome}!</font>
          </#if>
          <br/>
          <font color="#FFFFFF">  - </font><a href="<@ofbizUrl>/showcart</@ofbizUrl>"><font color="#FFFFFF">CURRENT ORDER</font><font color="#FFFFFF">  - </font></a>
          <#if sessionAttributes.autoName?has_content><br/>
              <a href="<@ofbizUrl>viewprofile</@ofbizUrl>"><font color="#FFFFFF">PROFILE</font></a><font color="#FFFFFF">  - </font>
              <a href="<@ofbizUrl>orderhistory</@ofbizUrl>"><font color="#FFFFFF">ORDER HISTORY</font></a><font color="#FFFFFF">  - </font>
              <a href="<@ofbizUrl>contactus</@ofbizUrl>"><font color="#FFFFFF">CONTACT US</font><font color="#FFFFFF">  - </font></a>
              <a href="<@ofbizUrl>autoLogout</@ofbizUrl>"><font color="#FFFFFF">LOGOUT(close-window)</font></a>
          <#--else>
            <font color="#FFFFFF">  - </font><a href="<@ofbizUrl>/checkLogin</@ofbizUrl>"><font color="#FFFFFF">LOGIN/REGISTER</font></a-->
          </#if>
          </p>
      </div>
    </td>
  </tr>
</table>


