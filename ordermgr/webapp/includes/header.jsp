
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<ofbiz:if name="userLogin">
<table width="100%" border="0" align="center" cellspacing="0" cellpadding="0">                                                    
  <tr> 
    <td> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">                                                              
        <tr>                                     
          <td align="left" height="22" class="apptitle">&nbsp;Order Manager Application&nbsp;</td>
        </tr>                            
      </table>
    </td>
  </tr>
  <tr>  
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0'>
        <tr>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="headerbuttontext">Main</a></td>

		  <%if(security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)) {%>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/tasklist</ofbiz:url>" class="headerbuttontext">Order&nbsp;List</a></td>
          <%}%>

		  <%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/orderlist</ofbiz:url>" class="headerbuttontext">Find&nbsp;Orders</a></td>
          <%}%>

		  <%if(security.hasEntityPermission("ORDERMGR", "_CREATE", session)) {%>
		  <td class="headerButtonLeft"><a href="<ofbiz:url>/salesentry</ofbiz:url>" class="headerbuttontext">Sale&nbsp;Entry</a></td>
		  <!--<td class="headerButtonLeft"><a href="#" class="headerbuttontext">Purchase&nbsp;Entry</a></td>-->
          <%}%>

          <td class="headerButtonLeft"><a href="<ofbiz:url>/orderreportlist</ofbiz:url>" class="headerbuttontext">Order&nbsp;Reports</a></td>
         
          <td width="90%" align='center' class='headerCenter'>&nbsp;</td>

          <td class="headerButtonRight"><a href='<ofbiz:url>/shipsetup</ofbiz:url>' class="headerbuttontext">Setup</a></td>
          <ofbiz:unless name="userLogin">
            <td class="headerButtonRight"><a href='<ofbiz:url><%=CommonWorkers.makeLoginUrl(pageContext)%></ofbiz:url>' class='headerbuttontext'>Login</a></td>
          </ofbiz:unless>
          <ofbiz:if name="userLogin">
            <td class="headerButtonRight"><a href="<ofbiz:url>/logout</ofbiz:url>" class="headerbuttontext">Logout</a></td>
          </ofbiz:if>
        </TR>
      </table>
    </td>
  </tr>               
</table>
</ofbiz:if>