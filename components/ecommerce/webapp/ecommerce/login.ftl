<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Rev:$
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>

<div class="head1">${uiLabelMap.CommonLogin}</div>
<br>

<table width='100%' border='0' cellpadding='0' cellspacing='0'>
  <tr>
    <td width='50%' valign='top'>
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign='middle' align='center'>
                  <div class="boxhead">${uiLabelMap.CommonRegistred}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <form method="POST" action="<@ofbizUrl>/login${previousParams}</@ofbizUrl>" name="loginform" style='margin: 0;'>
                    <table width='100%' border='0' cellpadding='0' cellspacing='2'>
                      <tr align="center">
                        <td align='right'><span class="tabletext">${uiLabelMap.CommonUsername}:&nbsp;</span></td>
                        <#-- another possible way...
                        <#if autoUserLogin?exists>
                          <input type="hidden" name="USERNAME" value='${autoUserLogin.userLoginId}'>
                          <td>
                            <span class="head2">${autoUserLogin.userLoginId}</span>
                            <span class="tabletext">
                              (Not&nbsp;${autoUserLogin.userLoginId}?&nbsp;
                              <a href="<@ofbizUrl>${autoLogoutUrl}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonClickHere}</a>)
                            </span>
                          </td>
                        </#if>
                        -->
                        <td align=left>
                          <input type="text" class='inputBox' name="USERNAME" value='<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>' size="20">
                        </td>
                      </tr>
                      <#if autoUserLogin?has_content>
                        <tr align="center">
                          <td align=right>&nbsp;</td>
                          <td align=left>
                            <span class="tabletext">
                              (${uiLabelMap.CommonNot}&nbsp;${autoUserLogin.userLoginId}?&nbsp;<a href="<@ofbizUrl>${autoLogoutUrl}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonClickHere}</a>)
                            </span>
                          </td>
                        </tr>
                      </#if>
                      <tr align="center">
                        <td align='right'><span class="tabletext">${uiLabelMap.CommonPassword}:&nbsp;</span></td>
                          <td align='left'><input type="password" class='inputBox' name="PASSWORD" value="" size="20"></td>
                      </tr>
                      <tr>
                        <td colspan="2" align="center"><input type="submit" class="smallSubmit" value="${uiLabelMap.CommonLogin}"></td>
                      </tr>
                    </table>
                  </form>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      <br>
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign='middle' align='center'>
                  <div class="boxhead">${uiLabelMap.CommonForgotYourPassword}?</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td valign='middle' align='center'>
                  <form method="POST" action="<@ofbizUrl>/forgotpassword${previousParams}</@ofbizUrl>" name="forgotpassword" style='margin: 0;'>
                    <span class="tabletext">${uiLabelMap.CommonUsername}:&nbsp;</span><input type="text" size="20" class='inputBox' name="USERNAME" value='<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>'>
                    <div><input type="submit" class="smallSubmit" value="${uiLabelMap.CommonGetPasswordHint}" name="GET_PASSWORD_HINT">&nbsp;<input type="submit" class="smallSubmit" value="${uiLabelMap.CommonEmailPassword}" name="EMAIL_PASSWORD"></div>
                  </form>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
    <td>&nbsp;&nbsp;&nbsp;</td>
    <td width='50%' valign=top>
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign='middle' align='center'>
                  <div class="boxhead">${uiLabelMap.CommonNewUser}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <form method="POST" action="<@ofbizUrl>/newcustomer${previousParams}</@ofbizUrl>" style='margin: 0;'>
                    <div class="tabletext" align=center>${uiLabelMap.CommonMayCreateNewAccountHere}:</div>
                    <div align='center'><input type="submit" class="smallSubmit" value="${uiLabelMap.CommonMayCreate}"></div>
                  </form>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      <#if (shoppingCartSize > 0)>
      <br>
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign='middle' align='center'>
                  <div class="boxhead">${uiLabelMap.CommonCheckoutAnonymous}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <form method="POST" action="<@ofbizUrl>/finalizeOrder</@ofbizUrl>" style='margin: 0;'>
                    <div class="tabletext" align=center>${uiLabelMap.CommonCheckoutAnonymousMsg}:</div>
                    <div align='center'><input type="submit" class="smallSubmit" value="Checkout"></div>
                  </form>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      </#if>
    </td>
  </tr>
</table>

<script language="JavaScript">
  <#if autoUserLogin?has_content>document.loginform.PASSWORD.focus();</#if>
  <#if !autoUserLogin?has_content>document.loginform.USERNAME.focus();</#if>
</script>

