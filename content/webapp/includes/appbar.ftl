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
<#assign externalKeyParam = requestAttributes.externalKeyParam>

<#if requestAttributes.userLogin?has_content>

<#assign unselectedClass = {"col" : "tabdownblock", "left" : "tabdownleft", "center" : "tabdowncenter", "right" : "tabdownright", "link" : "tablink"}>
<#assign selectedClass = {"col" : "mainblock", "left" : "tabupleft", "center" : "tabupcenter", "right" : "tabupright", "link" : "tablinkselected"}>
<#assign isActive = {requestAttributes.activeApp : selectedClass}>

<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td><div class="appbarleft"></div></td>
    <td height="15" width="100%">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
        <tr>	       
          <#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>          
          <td height="15" class="${isActive.partymgr?default(unselectedClass).col}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${isActive.partymgr?default(unselectedClass).left}"><a href="${response.encodeURL("/partymgr/control/main" + externalKeyParam)}" title="Party Manager" class="${isActive.partymgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${isActive.partymgr?default(unselectedClass).center}"><a href="${response.encodeURL("/partymgr/control/main" + externalKeyParam)}" title="Party Manager" class="${isActive.partymgr?default(unselectedClass).link}">Party</a></td>
                <td class="${isActive.partymgr?default(unselectedClass).right}"><a href="${response.encodeURL("/partymgr/control/main" + externalKeyParam)}" title="Party Manager" class="${isActive.partymgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr> 
              <#if !isActive.partymgr?has_content>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>                                          
              </#if>
            </table>
          </td>	 
          </#if>
          <#if security.hasEntityPermission("MARKETING", "_VIEW", session)>                         
          <td height="15" class="${isActive.marketingmgr?default(unselectedClass).col}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${isActive.marketingmgr?default(unselectedClass).left}"><a href="${response.encodeURL("/marketing/control/main" + externalKeyParam)}" title="Marketing Manager" class="${isActive.marketingmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${isActive.marketingmgr?default(unselectedClass).center}"><a href="${response.encodeURL("/marketing/control/main" + externalKeyParam)}" title="Marketing Manager" class="${isActive.marketingmgr?default(unselectedClass).link}">Marketing</a></td>
                <td class="${isActive.marketingmgr?default(unselectedClass).right}"><a href="${response.encodeURL("/marketing/control/main" + externalKeyParam)}" title="Marketing Manager" class="${isActive.marketingmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !isActive.marketingmgr?has_content>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>
          </#if>
          <#if security.hasEntityPermission("CATALOG", "_VIEW", session)>                                                            
          <td height="15" class="${isActive.catalogmgr?default(unselectedClass).col}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${isActive.catalogmgr?default(unselectedClass).left}"><a href="${response.encodeURL("/catalog/control/main" + externalKeyParam)}" title="Catalog Manager" class="${isActive.catalogmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${isActive.catalogmgr?default(unselectedClass).center}"><a href="${response.encodeURL("/catalog/control/main" + externalKeyParam)}" title="Catalog Manager" class="${isActive.catalogmgr?default(unselectedClass).link}">Catalog</a></td>
                <td class="${isActive.catalogmgr?default(unselectedClass).right}"><a href="${response.encodeURL("/catalog/control/main" + externalKeyParam)}" title="Catalog Manager" class="${isActive.catalogmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>  
              <#if !isActive.catalogmgr?has_content>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>                                           
              </#if>
            </table>
          </td>	     
          </#if>
          <#if security.hasEntityPermission("FACILITY", "_VIEW", session)>                   
          <td height="15" class="${isActive.facilitymgr?default(unselectedClass).col}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${isActive.facilitymgr?default(unselectedClass).left}"><a href="${response.encodeURL("/facility/control/main" + externalKeyParam)}" title="Facility Manager" class="${isActive.facilitymgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${isActive.facilitymgr?default(unselectedClass).center}"><a href="${response.encodeURL("/facility/control/main" + externalKeyParam)}" title="Facility Manager" class="${isActive.facilitymgr?default(unselectedClass).link}">Facility</a></td>
                <td class="${isActive.facilitymgr?default(unselectedClass).right}"><a href="${response.encodeURL("/facility/control/main" + externalKeyParam)}" title="Facility Manager" class="${isActive.facilitymgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !isActive.facilitymgr?has_content>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>                
          </#if>
          <#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>                          	        
          <td height="15" class="${isActive.ordermgr?default(unselectedClass).col}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${isActive.ordermgr?default(unselectedClass).left}"><a href="${response.encodeURL("/ordermgr/control/main" + externalKeyParam)}" title="Order Manager" class="${isActive.ordermgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${isActive.ordermgr?default(unselectedClass).center}"><a href="${response.encodeURL("/ordermgr/control/main" + externalKeyParam)}" title="Order Manager" class="${isActive.ordermgr?default(unselectedClass).link}">Order</a></td>
                <td class="${isActive.ordermgr?default(unselectedClass).right}"><a href="${response.encodeURL("/ordermgr/control/main" + externalKeyParam)}" title="Order Manager" class="${isActive.ordermgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr> 
              <#if !isActive.ordermgr?has_content>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>                                       
            </table>
          </td>
          </#if>
          <#if security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>                  
          <td height="15" class="${isActive.accountingmgr?default(unselectedClass).col}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${isActive.accountingmgr?default(unselectedClass).left}"><a href="${response.encodeURL("/accouting/control/main" + externalKeyParam)}" title="Accounting" class="${isActive.accountingmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${isActive.accountingmgr?default(unselectedClass).center}"><a href="${response.encodeURL("/accounting/control/main" + externalKeyParam)}" title="Accounting" class="${isActive.accountingmgr?default(unselectedClass).link}">Accounting</a></td>
                <td class="${isActive.accountingmgr?default(unselectedClass).right}"><a href="${response.encodeURL("/accounting/control/main" + externalKeyParam)}" title="Accounting" class="${isActive.accountingmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !isActive.accountingmgr?has_content>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>     
          </#if>               
          <#if security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", session)>                  
          <td height="15" class="${isActive.workeffortmgr?default(unselectedClass).col}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${isActive.workeffortmgr?default(unselectedClass).left}"><a href="${response.encodeURL("/workeffort/control/main" + externalKeyParam)}" title="WorkEffort" class="${isActive.workeffortmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${isActive.workeffortmgr?default(unselectedClass).center}"><a href="${response.encodeURL("/workeffort/control/main" + externalKeyParam)}" title="WorkEffort" class="${isActive.workeffortmgr?default(unselectedClass).link}">WorkEffort</a></td>
                <td class="${isActive.workeffortmgr?default(unselectedClass).right}"><a href="${response.encodeURL("/workeffort/control/main" + externalKeyParam)}" title="WorkEffort" class="${isActive.workeffortmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !isActive.workeffortmgr?has_content>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>                        
          </#if>
          <#if security.hasEntityPermission("CONTENTMGR", "_VIEW", session)>                  
          <td height="15" class="${isActive.contentmgr?default(unselectedClass).col}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${isActive.contentmgr?default(unselectedClass).left}"><a href="${response.encodeURL("/content/control/main" + externalKeyParam)}" title="Content Manager" class="${isActive.contentmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${isActive.contentmgr?default(unselectedClass).center}"><a href="${response.encodeURL("/content/control/main" + externalKeyParam)}" title="Content Manager" class="${isActive.contentmgr?default(unselectedClass).link}">Content</a></td>
                <td class="${isActive.contentmgr?default(unselectedClass).right}"><a href="${response.encodeURL("/content/control/main" + externalKeyParam)}" title="Content Manager" class="${isActive.contentmgr?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !isActive.contentmgr?has_content>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>  
          </#if>                  
          <td height="15" class="${isActive.webtools?default(unselectedClass).col}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${isActive.webtools?default(unselectedClass).left}"><a href="${response.encodeURL("/webtools/control/main" + externalKeyParam)}" title="WebTools" class="${isActive.webtools?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${isActive.webtools?default(unselectedClass).center}"><a href="${response.encodeURL("/webtools/control/main" + externalKeyParam)}" title="WebTools" class="${isActive.webtools?default(unselectedClass).link}">WebTools</a></td>
                <td class="${isActive.webtools?default(unselectedClass).right}"><a href="${response.encodeURL("/webtools/control/main" + externalKeyParam)}" title="WebTools" class="${isActive.webtools?default(unselectedClass).link}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !isActive.webtools?has_content>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>            
		  <td><div class="appbarright"></div></td>            
          <td width="100%" class="appbarresize">
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
</#if>