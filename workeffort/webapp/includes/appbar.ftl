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
<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td><div class="appbarleft"></div></td>
    <td height="15" width="100%">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
        <tr>	       
          <#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)> 
          <#if requestAttributes.isPartyMgrActive?default(false)> 
            <#assign colClass = "mainblock">
            <#assign leftClass = "tabupleft">
            <#assign centerClass = "tabupcenter">
            <#assign rightClass = "tabupright">
            <#assign linkClass = "tablinkselected">
          <#else>
            <#assign colClass = "tabdownblock">
            <#assign leftClass = "tabdownleft">
            <#assign centerClass = "tabdowncenter">
            <#assign rightClass = "tabdownright">
            <#assign linkClass = "tablink">
          </#if>          
          <td height="15" class="${colClass}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="${response.encodeURL("/partymgr/control/main" + externalKeyParam)}" title="Party Manager" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="${response.encodeURL("/partymgr/control/main" + externalKeyParam)}" title="Party Manager" class="${linkClass}">Party</a></td>
                <td class="tabdownright"><a href="${response.encodeURL("/partymgr/control/main" + externalKeyParam)}" title="Party Manager" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr> 
              <#if !requestAttributes.isPartyMgrActive?default(false)>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>                                          
              </#if>
            </table>
          </td>	 
          </#if>
          <#if security.hasEntityPermission("MARKETING", "_VIEW", session)>   
          <#if requestAttributes.isMarketingMgrActive?default(false)> 
            <#assign colClass = "mainblock">
            <#assign leftClass = "tabupleft">
            <#assign centerClass = "tabupcenter">
            <#assign rightClass = "tabupright">
            <#assign linkClass = "tablinkselected">
          <#else>
            <#assign colClass = "tabdownblock">
            <#assign leftClass = "tabdownleft">
            <#assign centerClass = "tabdowncenter">
            <#assign rightClass = "tabdownright">
            <#assign linkClass = "tablink">
          </#if>                       
          <td height="15" class="${colClass}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${leftClass}"><a href="${response.encodeURL("/marketing/control/main" + externalKeyParam)}" title="Marketing Manager" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${centerClass}"><a href="${response.encodeURL("/marketing/control/main" + externalKeyParam)}" title="Marketing Manager" class="${linkClass}">Marketing</a></td>
                <td class="${rightClass}"><a href="${response.encodeURL("/marketing/control/main" + externalKeyParam)}" title="Marketing Manager" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !requestAttributes.isMarketingMgrActive?default(false)>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>
          </#if>
          <#if security.hasEntityPermission("CATALOG", "_VIEW", session)>    
          <#if requestAttributes.isCatalogMgrActive?default(false)> 
            <#assign colClass = "mainblock">
            <#assign leftClass = "tabupleft">
            <#assign centerClass = "tabupcenter">
            <#assign rightClass = "tabupright">
            <#assign linkClass = "tablinkselected">
          <#else>
            <#assign colClass = "tabdownblock">
            <#assign leftClass = "tabdownleft">
            <#assign centerClass = "tabdowncenter">
            <#assign rightClass = "tabdownright">
            <#assign linkClass = "tablink">
          </#if>                                                          
          <td height="15" class="${colClass}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${leftClass}"><a href="${response.encodeURL("/catalog/control/main" + externalKeyParam)}" title="Catalog Manager" class="${linkClass}selected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${centerClass}"><a href="${response.encodeURL("/catalog/control/main" + externalKeyParam)}" title="Catalog Manager" class="${linkClass}">Catalog</a></td>
                <td class="${rightClass}"><a href="${response.encodeURL("/catalog/control/main" + externalKeyParam)}" title="Catalog Manager" class="${linkClass}selected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>  
              <#if !requestAttributes.isCatalogMgrActive?default(false)>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>                                           
              </#if>
            </table>
          </td>	     
          </#if>
          <#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
          <#if requestAttributes.isFacilityMgrActive?default(false)> 
            <#assign colClass = "mainblock">
            <#assign leftClass = "tabupleft">
            <#assign centerClass = "tabupcenter">
            <#assign rightClass = "tabupright">
            <#assign linkClass = "tablinkselected">
          <#else>
            <#assign colClass = "tabdownblock">
            <#assign leftClass = "tabdownleft">
            <#assign centerClass = "tabdowncenter">
            <#assign rightClass = "tabdownright">
            <#assign linkClass = "tablink">
          </#if>                    
          <td height="15" class="${colClass}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${leftClass}"><a href="${response.encodeURL("/facility/control/main" + externalKeyParam)}" title="Facility Manager" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${centerClass}"><a href="${response.encodeURL("/facility/control/main" + externalKeyParam)}" title="Facility Manager" class="${linkClass}">Facility</a></td>
                <td class="${rightClass}"><a href="${response.encodeURL("/facility/control/main" + externalKeyParam)}" title="Facility Manager" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !requestAttributes.isFacilityMgrActive?default(false)>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>                
          </#if>
          <#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>  
          <#if requestAttributes.isOrderMgrActive?default(false)> 
            <#assign colClass = "mainblock">
            <#assign leftClass = "tabupleft">
            <#assign centerClass = "tabupcenter">
            <#assign rightClass = "tabupright">
            <#assign linkClass = "tablinkselected">
          <#else>
            <#assign colClass = "tabdownblock">
            <#assign leftClass = "tabdownleft">
            <#assign centerClass = "tabdowncenter">
            <#assign rightClass = "tabdownright">
            <#assign linkClass = "tablink">
          </#if>                         	        
          <td height="15" class="${colClass}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${leftClass}"><a href="${response.encodeURL("/ordermgr/control/main" + externalKeyParam)}" title="Order Manager" class="${linkClass}selected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${centerClass}"><a href="${response.encodeURL("/ordermgr/control/main" + externalKeyParam)}" title="Order Manager" class="${linkClass}">Order</a></td>
                <td class="${rightClass}"><a href="${response.encodeURL("/ordermgr/control/main" + externalKeyParam)}" title="Order Manager" class="${linkClass}selected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr> 
              <#if !requestAttributes.isOrderMgrActive?default(false)>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>                                       
            </table>
          </td>
          </#if>
          <#if security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
          <#if requestAttributes.isAccountingMgrActive?default(false)> 
            <#assign colClass = "mainblock">
            <#assign leftClass = "tabupleft">
            <#assign centerClass = "tabupcenter">
            <#assign rightClass = "tabupright">
            <#assign linkClass = "tablinkselected">
          <#else>
            <#assign colClass = "tabdownblock">
            <#assign leftClass = "tabdownleft">
            <#assign centerClass = "tabdowncenter">
            <#assign rightClass = "tabdownright">
            <#assign linkClass = "tablink">
          </#if>                    
          <td height="15" class="${colClass}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${leftClass}"><a href="${response.encodeURL("/accouting/control/main" + externalKeyParam)}" title="Accounting" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${centerClass}"><a href="${response.encodeURL("/accounting/control/main" + externalKeyParam)}" title="Accounting" class="${linkClass}">Accounting</a></td>
                <td class="${rightClass}"><a href="${response.encodeURL("/accounting/control/main" + externalKeyParam)}" title="Accounting" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !requestAttributes.isAccountingMgrActive?default(false)>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>     
          </#if>               
          <#if security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", session)>
          <#if requestAttributes.isWorkEffortMgrActive?default(false)> 
            <#assign colClass = "mainblock">
            <#assign leftClass = "tabupleft">
            <#assign centerClass = "tabupcenter">
            <#assign rightClass = "tabupright">
            <#assign linkClass = "tablinkselected">
          <#else>
            <#assign colClass = "tabdownblock">
            <#assign leftClass = "tabdownleft">
            <#assign centerClass = "tabdowncenter">
            <#assign rightClass = "tabdownright">
            <#assign linkClass = "tablink">
          </#if>                    
          <td height="15" class="${colClass}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${leftClass}"><a href="j${response.encodeURL("/workeffort/control/main" + externalKeyParam)}" title="WorkEffort" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${centerClass}"><a href="${response.encodeURL("/workeffort/control/main" + externalKeyParam)}" title="WorkEffort" class="${linkClass}">WorkEffort</a></td>
                <td class="${rightClass}"><a href="${response.encodeURL("/workeffort/control/main" + externalKeyParam)}" title="WorkEffort" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !requestAttributes.isWorkEffortMgrActive?default(false)>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>                        
          </#if>
          <#if security.hasEntityPermission("CONTENTMGR", "_VIEW", session)>
          <#if requestAttributes.isContentMgrActive?default(false)> 
            <#assign colClass = "mainblock">
            <#assign leftClass = "tabupleft">
            <#assign centerClass = "tabupcenter">
            <#assign rightClass = "tabupright">
            <#assign linkClass = "tablinkselected">
          <#else>
            <#assign colClass = "tabdownblock">
            <#assign leftClass = "tabdownleft">
            <#assign centerClass = "tabdowncenter">
            <#assign rightClass = "tabdownright">
            <#assign linkClass = "tablink">
          </#if>                    
          <td height="15" class="${colClass}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${leftClass}"><a href="${response.encodeURL("/content/control/main" + externalKeyParam)}" title="Content Manager" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${centerClass}"><a href="${response.encodeURL("/content/control/main" + externalKeyParam)}" title="Content Manager" class="${linkClass}">Content</a></td>
                <td class="${rightClass}"><a href="${response.encodeURL("/content/control/main" + externalKeyParam)}" title="Content Manager" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !requestAttributes.isContentMgrActive?default(false)>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
              </#if>
            </table>
          </td>  
          </#if>          
          <#if requestAttributes.isWebToolsActive?default(false)> 
            <#assign colClass = "mainblock">
            <#assign leftClass = "tabupleft">
            <#assign centerClass = "tabupcenter">
            <#assign rightClass = "tabupright">
            <#assign linkClass = "tablinkselected">
          <#else>
            <#assign colClass = "tabdownblock">
            <#assign leftClass = "tabdownleft">
            <#assign centerClass = "tabdowncenter">
            <#assign rightClass = "tabdownright">
            <#assign linkClass = "tablink">
          </#if>          
          <td height="15" class="${colClass}">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="${leftClass}"><a href="${response.encodeURL("/webtools/control/main" + externalKeyParam)}" title="WebTools" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="${centerClass}"><a href="${response.encodeURL("/webtools/control/main" + externalKeyParam)}" title="WebTools" class="${linkClass}">WebTools</a></td>
                <td class="${rightClass}"><a href="${response.encodeURL("/webtools/control/main" + externalKeyParam)}" title="WebTools" class="${linkClass}"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <#if !requestAttributes.isWebToolsActive?default(false)>
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