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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Rev:$
 *@since      3.0
-->

<html>
<head>
  <title>Tell-A-Friend</title>
</head>
<body class="ecbody">
  <center>
    <form name="tellafriend" action="<@ofbizUrl>/emailFriend</@ofbizUrl>" method="post">
      <#if requestParameters.productId?exists>
        <input type="hidden" name="pageUrl" value="<@ofbizUrl fullPath="true" encode="false" secure="false">/product?product_id=${requestParameters.productId}</@ofbizUrl>">
      <#elseif requestParameters.categoryId?exists>
        <input type="hidden" name="pageUrl" value="<@ofbizUrl fullPath="true" encode="false" secure="false">/category?category_id=${requestParameters.categoryId}</@ofbizUrl>">
      <#else>
        <#assign cancel = "Y">
      </#if>
      <#if !cancel?exists>
        <table>
          <tr>
            <td>Your email:</td>
            <td><input type="text" name="sendFrom" size="30"></td>
          </tr>
          <tr>
            <td>Email To:</td>
            <td><input type="text" name="sendTo" size="30"></td>
          </tr>
          <tr>
            <td colspan="2" align="center">Message</td>
          </tr>
          <tr>
            <td colspan="2" align="center">
              <textarea cols="40"  rows="5" name="message"></textarea>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="center">
              <input type="submit" value="Send">
            </td>
          </tr>
        </table>
      <#else>
        <script language="javascript">
        <!-- //
        window.close();
        // -->
        </script>
        <div class="tabletext">Sorry, you cannot send this page to a friend. Please select from either a category or product.</div>
      </#if>
    </form>
  </center>
</body>
</html>