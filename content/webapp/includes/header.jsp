
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
          <td align="left" height="22" class="apptitle">&nbsp;Content Manager Application&nbsp;</td>
        </tr>                            
      </table>
    </td>
  </tr>
  <tr>  
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0'>
        <tr>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="headerbuttontext">Main</a></td>

          <td width="90%" align=center class='headerCenter'>&nbsp;</td>

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