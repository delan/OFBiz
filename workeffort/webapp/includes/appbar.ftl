<#if userLogin?has_content>
<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td height="15">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
        <tr>
        <#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>	                           		   	      
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="/partymgr/control/main${externalKeyParam}" title="Party Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="/partymgr/control/main${externalKeyParam}" title="Party Manager" class="tablink">Party</a></td>
                <td class="tabdownright"><a href="/partymgr/control/main${externalKeyParam}" title="Party Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
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
                <td class="tabdownleft"><a href="/marketing/control/main${externalKeyParam}" title="Marketing Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="/marketing/control/main${externalKeyParam}" title="Marketing Manager" class="tablink">Marketing</a></td>
                <td class="tabdownright"><a href="/marketing/control/main${externalKeyParam}" title="Marketing Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>
          </#if>          
          <#if security.hasEntityPermission("CATALOG", "_VIEW", session)>                                          
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="/catalog/control/main${externalKeyParam}" title="Catalog Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="/catalog/control/main${externalKeyParam}" title="Catalog Manager" class="tablink">Catalog</a></td>
                <td class="tabdownright"><a href="/catalog/control/main${externalKeyParam}" title="Catalog Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
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
                <td class="tabdownleft"><a href="/facility/control/main${externalKeyParam}" title="Facility Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="/facility/control/main${externalKeyParam}" title="Facility Manager" class="tablink">Facility</a></td>
                <td class="tabdownright"><a href="/facility/control/main${externalKeyParam}" title="Facility Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>                
          </#if>          
          <#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>        	        
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="/ordermgr/control/main${externalKeyParam}" title="Order Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="/ordermgr/control/main${externalKeyParam}" title="Order Manager" class="tablink">Order</a></td>
                <td class="tabdownright"><a href="/ordermgr/control/main${externalKeyParam}" title="Order Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>
          </#if>          
          <#if security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>  
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="/accounting/control/main${externalKeyParam}" title="Accounting" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="/accounting/control/main${externalKeyParam}" title="Accounting" class="tablink">Accounting</a></td>
                <td class="tabdownright"><a href="/accounting/control/main${externalKeyParam}" title="Accounting" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>     
          </#if>          
          <#if security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", session)>  
          <td height="15" class="mainblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabupleft"><a href="/workeffort/control/main${externalKeyParam}" title="WorkEffort" class="tablinkselected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabupcenter"><a href="/workeffort/control/main${externalKeyParam}" title="WorkEffort" class="tablinkselected">WorkEffort</a></td>
                <td class="tabupright"><a href="/workeffort/control/main${externalKeyParam}" title="WorkEffort" class="tablinkselected"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>                             
            </table>
          </td>                        
          </#if>          
          <#if security.hasEntityPermission("CONTENTMGR", "_VIEW", session)>  
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="/content/control/main${externalKeyParam}" title="Content Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="/content/control/main${externalKeyParam}"  title="Content Manager" class="tablink">Content</a></td>
                <td class="tabdownright"><a href="/content/control/main${externalKeyParam}" title="Content Manager" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" height="1"></td></tr>               
            </table>
          </td>  
          </#if>         
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="/webtools/control/main${externalKeyParam}" title="WebTools" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="/webtools/control/main${externalKeyParam}" title="WebTools" class="tablink">WebTools</a></td>
                <td class="tabdownright"><a href="/webtools/control/main${externalKeyParam}" title="WebTools" class="tablink"><img src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" alt="" width="10" height="15" border="0"></a></td>
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
</#if>
