
<%if(userLogin != null) {%>
<TABLE border=0 width='100%' cellpadding=1 cellspacing=0 bgcolor='black'>
<%--
  <TR>
    <TD width='100%'>
      <table width="100%" border="0" cellpadding="4" cellspacing="0" bgcolor="#678475">
        <tr>
          <TD align=left width='99%' >
            <div  style="margin: 0; font-size: 12pt; font-weight: bold; color: white;">OFBIZ - Application QuickLinks</div>
          </TD>
          <TD align=right width='1%'>&nbsp;</TD>
        </tr>
      </table>
    </TD>
  </TR>
--%>  
  <TR>
    <TD width='100%'>
      <table width='100%' border=0 cellpadding='<%=headerBoxBottomPadding%>' cellspacing=0 bgcolor='<%=headerBoxBottomColor%>'>
        <tr>
          <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonLeft"><a href="javascript:document.commonappform.submit()" class="buttontext">CommonApp</a></td>
          <TD bgcolor="<%=headerBoxBottomColor%>" width="90%" align=center class='headerCenter'>App Links</TD>
          <%if(security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", session)) {%>
            <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="javascript:document.workeffortform.submit()" class="buttontext">WorkEffort</a></td>
          <%}%>
          <%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
            <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="javascript:document.catalogform.submit()" class="buttontext">Catalog</a></td>
          <%}%>
          <%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>
            <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="javascript:document.partyform.submit()" class="buttontext">Party</a></td>
          <%}%>
          <%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>
            <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="javascript:document.orderform.submit()" class="buttontext">Order</a></td>
          <%}%>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

  <form method="POST" action="<%=response.encodeURL("/commonapp/control/login/main")%>" name="commonappform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%=userLogin.getString("userLoginId")%>">
    <input type="hidden" name="PASSWORD" value="<%=userLogin.getString("currentPassword")%>">
  </form>
  <form method="POST" action="<%=response.encodeURL("/workeffort/control/login/main")%>" name="workeffortform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%=userLogin.getString("userLoginId")%>">
    <input type="hidden" name="PASSWORD" value="<%=userLogin.getString("currentPassword")%>">
  </form>
  <form method="POST" action="<%=response.encodeURL("/catalog/control/login/main")%>" name="catalogform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%=userLogin.getString("userLoginId")%>">
    <input type="hidden" name="PASSWORD" value="<%=userLogin.getString("currentPassword")%>">
  </form>
  <form method="POST" action="<%=response.encodeURL("/partymgr/control/login/main")%>" name="partyform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%=userLogin.getString("userLoginId")%>">
    <input type="hidden" name="PASSWORD" value="<%=userLogin.getString("currentPassword")%>">
  </form>
  <form method="POST" action="<%=response.encodeURL("/ordermgr/control/login/main")%>" name="orderform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%=userLogin.getString("userLoginId")%>">
    <input type="hidden" name="PASSWORD" value="<%=userLogin.getString("currentPassword")%>">
  </form>
<%}%>
