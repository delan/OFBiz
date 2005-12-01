<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="top-row1">
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="180" height="70" align="center" valign="middle"><a href="/frontend"><img src="/hotelfrontend/hotelfrontendimages/logo.gif" width="134" height="49" border="0"></a></td>
					<td height="70" align="center">
						<table width="99%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center"><a href="http://www.rydges-chiangmai.com/golf_special.shtml" target="_self"><img src="/hotelfrontend/hotelfrontendimages/banner.gif" width="468" height="60" border="0"></a></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="top-row2"><img src="/hotelfrontend/hotelfrontendimages/spacer.gif" width="1" height="5" border="0"></td>
	</tr>
	<tr>
		<td class="top-row3">
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="180" height="40" align="center"><a href="http://chat.boldchat.com/chat/visitor.jsp?cdid=54916494" target="_blank" onclick="this.newWindow = window.open('http://chat.boldchat.com/chat/visitor.jsp?cdid=54916494&url=' + document.location, 'Chat', 'toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=640,height=480'); this.newWindow.focus(); this.newWindow.opener = window;return false;"><img alt="Live chat by Boldchat" src="/hotelfrontend/hotelfrontendimages/boldchat.gif" width="133" height="34" border="0"></a></td>
					<td height="40" align="center">
						<table  border="0" cellpadding="0" cellspacing="0">
			          	<#if sessionAttributes.autoName?has_content>
              					<tr><td align="center" class="link">${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName}!
                (${uiLabelMap.CommonNotYou}?&nbsp;<a href="<@ofbizUrl>autoLogout</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonClickHere}</a>)
              </td></tr>
          <#else>
              <tr><td width="90%" align="center" class="link">${uiLabelMap.CommonWelcome}!</TD></tr>
          </#if>
						
							<tr>
								<td align="center" class="link">
									<a href="<@ofbizUrl>main</@ofbizUrl>" target="_self" class="link">Home</a> | 
									<a href="<@ofbizUrl>book</@ofbizUrl>" target="_self" class="link">Book Online</a> | 
									<a href="<@ofbizUrl>orderhistory</@ofbizUrl>" class="link">Order History</a> | 
									<a href="<@ofbizUrl>viewprofile</@ofbizUrl>" class="link">Profile</a> | 
			          	<#if sessionAttributes.autoName?has_content>
									<a href="<@ofbizUrl>contactus</@ofbizUrl>" class="link">Contact Us</a> | 
				          <#else>
									<a href="http://www.rydges-chiangmai.com/contactus.shtml" target="_self" class="link">Contact Us</a>
				          </#if>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>


