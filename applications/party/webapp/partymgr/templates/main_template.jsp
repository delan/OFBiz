<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ include file="/includes/envsetup.jsp" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='regions' prefix='region' %>
<html>
<head>
    <%@page contentType='text/html; charset=UTF-8'%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><%EntityField.run("layoutSettings", "companyName", pageContext);%>: <region:render section='title'/></title>
    <script language='javascript' src='<ofbiz:contenturl>/images/calendar1.js</ofbiz:contenturl>' type='text/javascript'></script>
    <link rel='stylesheet' href='<ofbiz:contenturl>/images/maincss.css</ofbiz:contenturl>' type='text/css'>
    <link rel='stylesheet' href='<ofbiz:contenturl>/images/tabstyles.css</ofbiz:contenturl>' type='text/css'>    
</head>
<body>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
          <%if (UtilValidate.isNotEmpty((String) layoutSettings.get("headerImageUrl"))) {%>
            <TD align=left width='1%'><IMG alt="<%EntityField.run("layoutSettings", "companyName", pageContext);%>" src='<ofbiz:contenturl><%=(String) layoutSettings.get("headerImageUrl")%></ofbiz:contenturl>'></TD>
          <%}%>         
          <TD align=right width='1%' nowrap <%EntityField.run("layoutSettings", "headerRightBackgroundUrl", "background='", "'", pageContext);%>>
              <ofbiz:if name="person">
                <div class="insideHeaderText">Welcome<%EntityField.run("person", "firstName", "&nbsp;", "", pageContext);%><%EntityField.run("person", "lastName", "&nbsp;", "", pageContext);%>!</div>
              </ofbiz:if>
              <ofbiz:unless name="person">
                <ofbiz:if name="partyGroup">
                  <div class="insideHeaderText">Welcome<%EntityField.run("partyGroup", "groupName", "", "", pageContext);%>!</div>
                </ofbiz:if>
                <ofbiz:unless name="partyGroup">
                  <div class="insideHeaderText">Welcome!</div>
                </ofbiz:unless>
              </ofbiz:unless>
            <div class="insideHeaderText">&nbsp;<%=UtilDateTime.nowTimestamp().toString()%></div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<region:render section='appbar'/>

<div class="centerarea">
  <region:render section='header'/>
  <div class="contentarea">
    <div style='border: 0; margin: 0; padding: 0; width: 100%;'>
      <table style='border: 0; margin: 0; padding: 0; width: 100%;' cellpadding='0' cellspacing='0'>
        <tr>
          <region:render section='leftbar'/>
          <td width='100%' valign='top' align='left'>
            <region:render section='error'/>
            <region:render section='content'/>
          </td>
          <region:render section='rightbar'/>
        </tr>
      </table>       
    </div>
    <div class='spacer'></div>
  </div>
</div>

<region:render section='footer'/>


</body>
</html>