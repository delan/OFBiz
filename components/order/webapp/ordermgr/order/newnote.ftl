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
 *@since      2.2
-->

<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>  
  <p class="head1">Add Note</p>

  &nbsp;<a href="<@ofbizUrl>/authview/${donePage}</@ofbizUrl>" class="buttontext">[Go&nbsp;Back]</a>
  &nbsp;<a href="javascript:document.createnoteform.submit()" class="buttontext">[Save]</a>

  <form method="post" action="<@ofbizUrl>/createordernote/${donePage}</@ofbizUrl>" name="createnoteform">
    <input type="hidden" name="orderId" value="${orderId?if_exists}">
    <table width="90%" border="0" cellpadding="2" cellspacing="0">
      <tr>
        <td width="26%" align=right><div class="tabletext">Note</div></td>
        <td width="54%">
          <textarea name="note" class="textAreaBox" rows="5" cols="70"></textarea>
        </td>
        <td>*</td>
      </tr>
    </table>
  </form>

  &nbsp;<a href="<@ofbizUrl>/authview/${donePage}</@ofbizUrl>" class="buttontext">[Go&nbsp;Back]</a>
  &nbsp;<a href="javascript:document.createnoteform.submit()" class="buttontext">[Save]</a>
  
<#else>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
</#if>
