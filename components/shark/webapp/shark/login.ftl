<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.1 $
 *@since      3.1
-->

<#assign previousParams = sessionAttributes._PREVIOUS_PARAMS_?if_exists>
<#if previousParams?has_content>
  <#assign previousParams = "?" + previousParams>
</#if>
<table width="300" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr>    
    <td width="100%" valign="top">
      <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
        <tr>
          <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
              <tr>
                <td valign="middle" align="center">
                  <div class="boxhead">Registered User Login</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
              <tr>
                <td align="center" valign="middle" width="100%">
                  <form method="POST" action="<@ofbizUrl>/login${previousParams?if_exists}</@ofbizUrl>" name="loginform" style="margin: 0;">
                    <table width="100%" border="0" cellpadding="0" cellspacing="2">
                      <tr>
                        <td align="right">
                          <span class="tabletext">Username:&nbsp;</span>
                        </td>
                        <td>
                          <input type="text" class="inputBox" name="USERNAME" value="${requestParameters.USERNAME?if_exists}" size="20">
                        </td>
                      </tr>
                      <tr>
                        <td align="right">
                          <span class="tabletext">Password:&nbsp;</span>
                        </td>
                        <td align="left">
                          <input type="password" class="inputBox" name="PASSWORD" value="" size="20">
                        </td>
                      </tr>
                      <tr>
                        <td colspan="2" align="center">
                          <!--<a href="javascript:document.loginform.submit()" class="buttontext">[Login]</a>-->
                          <input type="submit" value="Login" class="loginButton">
                        </td>
                      </tr>
                    </table>
                  </form>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<script language="JavaScript" type="text/javascript">
<!--
  document.loginform.USERNAME.focus();
//-->
</script>
