<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*, org.ofbiz.content.webapp.control.*" %>
<%@ page import="org.ofbiz.securityext.login.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<%String externalLoginKey = LoginWorker.getExternalLoginKey(request);%>
<%String externalKeyParam = externalLoginKey == null ? "" : "?externalLoginKey=" + externalLoginKey;%>

<ofbiz:if name="userLogin">
<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr bgcolor="#FFFFFF">
    <td height="15">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">                      
        <tr>	       
          <%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>                          		   	      
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="<%=response.encodeURL("/partymgr/control/main" + externalKeyParam)%>" title="Party Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="<%=response.encodeURL("/partymgr/control/main" + externalKeyParam)%>" title="Party Manager" class="tablink">Party</a></td>
                <td class="tabdownright"><a href="<%=response.encodeURL("/partymgr/control/main" + externalKeyParam)%>" title="Party Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>               
            </table>
          </td>	 
          <%}%>
          <%if(security.hasEntityPermission("MARKETING", "_VIEW", session)) {%>      
          <td height="15" class="mainblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabupleft"><a href="<%=response.encodeURL("/marketing/control/main" + externalKeyParam)%>" title="Marketing Manager" class="tablinkselected"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabupcenter"><a href="<%=response.encodeURL("/marketing/control/main" + externalKeyParam)%>" title="Marketing Manager" class="tablinkselected">Marketing</a></td>
                <td class="tabupright"><a href="<%=response.encodeURL("/marketing/control/main" + externalKeyParam)%>" title="Marketing Manager" class="tablinkselected"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>             
            </table>
          </td>
          <%}%>
          <%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>                                          
          <td height="15" class="tabdownleft">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="<%=response.encodeURL("/catalog/control/main" + externalKeyParam)%>" title="Catalog Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="<%=response.encodeURL("/catalog/control/main" + externalKeyParam)%>" title="Catalog Manager" class="tablink">Catalog</a></td>
                <td class="tabdownright"><a href="<%=response.encodeURL("/catalog/control/main" + externalKeyParam)%>" title="Catalog Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
               </tr>
              <tr><td colspan="3" class="blackarea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td>
              </tr>              
            </table>
          </td>	     
          <%}%>
          <%if(security.hasEntityPermission("FACILITY", "_VIEW", session)) {%>
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="<%=response.encodeURL("/facility/control/main" + externalKeyParam)%>" title="Facility Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="<%=response.encodeURL("/facility/control/main" + externalKeyParam)%>" title="Facility Manager" class="tablink">Facility</a></td>
                <td class="tabdownright"><a href="<%=response.encodeURL("/facility/control/main" + externalKeyParam)%>" title="Facility Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>               
            </table>
          </td>                
          <%}%>
          <%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>       	        
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="<%=response.encodeURL("/ordermgr/control/main" + externalKeyParam)%>" title="Order Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="<%=response.encodeURL("/ordermgr/control/main" + externalKeyParam)%>" title="Order Manager" class="tablink">Order</a></td>
                <td class="tabdownright"><a href="<%=response.encodeURL("/ordermgr/control/main" + externalKeyParam)%>" title="Order Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>               
            </table>
          </td>
          <%}%>
          <%if(security.hasEntityPermission("ACCOUNTING", "_VIEW", session)) {%>
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="<%=response.encodeURL("/accounting/control/main" + externalKeyParam)%>" title="Accounting" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="<%=response.encodeURL("/accounting/control/main" + externalKeyParam)%>" title="Accounting" class="tablink">Accounting</a></td>
                <td class="tabdownright"><a href="<%=response.encodeURL("/accounting/control/main" + externalKeyParam)%>" title="Accounting" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>               
            </table>
          </td>     
          <%}%>               
          <%if(security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", session)) {%>
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="<%=response.encodeURL("/workeffort/control/main" + externalKeyParam)%>" title="WorkEffort" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="<%=response.encodeURL("/workeffort/control/main" + externalKeyParam)%>" title="WorkEffort" class="tablink">WorkEffort</a></td>
                <td class="tabdownright"><a href="<%=response.encodeURL("/workeffort/control/main" + externalKeyParam)%>" title="WorkEffort" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>               
            </table>
          </td>                        
          <%}%>
          <%if(security.hasEntityPermission("CONTENTMGR", "_VIEW", session)) {%>
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="<%=response.encodeURL("/content/control/main" + externalKeyParam)%>" title="Content Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="<%=response.encodeURL("/content/control/main" + externalKeyParam)%>" title="Content Manager" class="tablink">Content</a></td>
                <td class="tabdownright"><a href="<%=response.encodeURL("/content/control/main" + externalKeyParam)%>" title="Content Manager" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>               
            </table>
          </td>  
          <%}%>          
          <td height="15" class="tabdownblock">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="tabdownleft"><a href="<%=response.encodeURL("/webtools/control/main" + externalKeyParam)%>" title="WebTools" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
                <td nowrap="nowrap" class="tabdowncenter"><a href="<%=response.encodeURL("/webtools/control/main" + externalKeyParam)%>" title="WebTools" class="tablink">WebTools</a></td>
                <td class="tabdownright"><a href="<%=response.encodeURL("/webtools/control/main" + externalKeyParam)%>" title="WebTools" class="tablink"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" width="10" height="15" border="0"></a></td>
              </tr>
              <tr><td colspan="3" class="blackarea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>
              <tr><td colspan="3" class="whitearea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td></tr>               
            </table>
          </td>            
          <td width="100%" style="vertical-align: bottom;">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">                
              <tr>
                <td class="whitearea"><img src="<ofbiz:contenturl>/images/spacer.gif</ofbiz:contenturl>" alt="" height="1"></td>               
              </tr>               
            </table>
          </td>           
        </tr>        
      </table>
    </td>
  </tr>
</table>
</ofbiz:if>
