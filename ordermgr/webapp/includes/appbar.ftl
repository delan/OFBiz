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
 *@version    $Revision$
 *@since      2.1
-->

<#assign security = requestAttributes.security>
<#if session.userLogin?has_content>
<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td height="15">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
        <tr>	       
          <#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>                          		   	      
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="javascript:document.partyform.submit()" title="Party Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="javascript:document.partyform.submit()" title="Party Manager" class="tablink">Party</a></td>
                <td class="tabdownright"><a href="javascript:document.partyform.submit()" title="Party Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
              </tr> 
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>                                          
            </table>
          </td>	 
          </#if>
          <#if security.hasEntityPermission("MARKETING", "_VIEW", session)>      
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="javascript:document.marketingform.submit()" title="Marketing Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="javascript:document.marketingform.submit()" title="Marketing Manager" class="tablink">Marketing</a></td>
                <td class="tabdownright"><a href="javascript:document.marketingform.submit()" title="Marketing Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>
          </#if>
          <#if security.hasEntityPermission("CATALOG", "_VIEW", session)>                                          
          <td class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="javascript:document.catalogform.submit()" title="Catalog Manager" class="tablinkselected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="javascript:document.catalogform.submit()" title="Catalog Manager" class="tablink">Catalog</a></td>
                <td class="tabdownright"><a href="javascript:document.catalogform.submit()" title="Catalog Manager" class="tablinkselected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
              </tr>  
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>                                           
            </table>
          </td>	     
          </#if>
          <#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="javascript:document.facilityform.submit()" title="Facility Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="javascript:document.facilityform.submit()" title="Facility Manager" class="tablink">Facility</a></td>
                <td class="tabdownright"><a href="javascript:document.facilityform.submit()" title="Facility Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>                
          </#if>
          <#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>       	        
          <td height="15" class="mainblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabupleft"><a href="javascript:document.orderform.submit()" title="Order Manager" class="tablinkselected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabupcenter"><a href="javascript:document.orderform.submit()" title="Order Manager" class="tablinkselected">Order</a></td>
                <td class="tabupright"><a href="javascript:document.orderform.submit()" title="Order Manager" class="tablinkselected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
              </tr>                          
            </table>
          </td>
          </#if>
          <#if security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="javascript:document.accountingform.submit()" title="Accounting" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="javascript:document.accountingform.submit()" title="Accounting" class="tablink">Accounting</a></td>
                <td class="tabdownright"><a href="javascript:document.accountingform.submit()" title="Accounting" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>     
          </#if>               
          <#if security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", session)>
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="javascript:document.workeffortform.submit()" title="WorkEffort" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="javascript:document.workeffortform.submit()" title="WorkEffort" class="tablink">WorkEffort</a></td>
                <td class="tabdownright"><a href="javascript:document.workeffortform.submit()" title="WorkEffort" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>                        
          </#if>
          <#if security.hasEntityPermission("CONTENTMGR", "_VIEW", session)>
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="javascript:document.contentform.submit()" title="Content Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="javascript:document.contentform.submit()" title="Content Manager" class="tablink">Content</a></td>
                <td class="tabdownright"><a href="javascript:document.contentform.submit()" title="Content Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>  
          </#if>          
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="javascript:document.webtoolsform.submit()" title="WebTools" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="javascript:document.webtoolsform.submit()" title="WebTools" class="tablink">WebTools</a></td>
                <td class="tabdownright"><a href="javascript:document.webtoolsform.submit()" title="WebTools" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="24" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>            
          <td width="100%" style="vertical-align: bottom;">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td>               
              </tr>               
            </table>
          </td>           
        </tr>        
      </table>
    </td>
  </tr>
</table>

<form method="POST" target="_self" action="<%=response.encodeURL("/webtools/control/login/main")%>" name="webtoolsform" style='margin: 0;'>
  <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
  <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
</form>
<form method="POST" target="_self" action="<%=response.encodeURL("/workeffort/control/login/main")%>" name="workeffortform" style='margin: 0;'>
  <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
  <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
</form>
<form method="POST" target="_self" action="<%=response.encodeURL("/catalog/control/login/main")%>" name="catalogform" style='margin: 0;'>
  <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
  <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
</form>
<form method="POST" target="_self" action="<%=response.encodeURL("/partymgr/control/login/main")%>" name="partyform" style='margin: 0;'>
  <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
  <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
</form>
<form method="POST" target="_self" action="<%=response.encodeURL("/ordermgr/control/login/main")%>" name="orderform" style='margin: 0;'>
  <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
  <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
</form>
<form method="POST" target="_self" action="<%=response.encodeURL("/accounting/control/login/main")%>" name="accountingform" style='margin: 0;'>
  <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
  <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
</form>
<form method="POST" target="_self" action="<%=response.encodeURL("/facility/control/login/main")%>" name="facilityform" style='margin: 0;'>
  <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
  <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
</form>
<form method="POST" target="_self" action="<%=response.encodeURL("/marketing/control/login/main")%>" name="marketingform" style='margin: 0;'>
  <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
  <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
</form>
<form method="POST" target="_self" action="<%=response.encodeURL("/content/control/login/main")%>" name="contentform" style='margin: 0;'>
  <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
  <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
</form>
  
</#if>