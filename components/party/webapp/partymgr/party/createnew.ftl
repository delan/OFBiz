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
 *@version    $Revision: 1.1 $
 *@since      2.2
-->

<#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)> 
<table width='300' border='0' cellpadding='0' cellspacing='0' align='center'>
  <tr>    
    <td width='100%' valign='top'>
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign='middle' align='center'>
                  <div class="boxhead">Create New Party Detail</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td align="center" valign="center" width='100%'>                  
                  <table width='100%' border='0' cellpadding='0' cellspacing='2'>
                    <tr>        
                      <td><a href="<@ofbizUrl>/editpartygroup?create_new=Y</@ofbizUrl>" class="buttontextbig">[*] Create New Party Group</a></td>
                    </tr>
                    <tr>        
                      <td><a href="<@ofbizUrl>/editperson?create_new=Y</@ofbizUrl>" class="buttontextbig">[*] Create New Person</a></td>
                    </tr>                    
                    <tr>        
                      <td><a href="<@ofbizUrl>/newcustomer</@ofbizUrl>" class="buttontextbig">[*] Create Customer</a></td>
                    </tr>
                    <tr>        
                      <td><a href="#" class="buttontextbigdisabled">[*] Create Employee</a>&nbsp;&nbsp;<span class="tabletext">Coming Soon!</span></td>
                    </tr>           
                  </table>  
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>  
<#else>
  <h3>You do not have permission to view this page. ("PARTYMGR_CREATE" or "PARTYMGR_ADMIN" needed)</h3>
</#if>