<%@ taglib uri='regions' prefix='region' %>

<TABLE border=0 width='100%' cellpadding='<%EntityField.run("layoutSettings", "boxBorderWidth", pageContext);%>' cellspacing=0 bgcolor='<%EntityField.run("layoutSettings", "boxBorderColor", pageContext);%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%EntityField.run("layoutSettings", "boxTopPadding", pageContext);%>' cellspacing='0' bgcolor='<%EntityField.run("layoutSettings", "boxTopColor", pageContext);%>'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;<region:render section='title'/></div>
          </td>
          <td valign="middle" align="right">
            <region:render section='buttons'/>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%EntityField.run("layoutSettings", "boxBottomPadding", pageContext);%>' cellspacing='0' bgcolor='<%EntityField.run("layoutSettings", "boxBottomColor", pageContext);%>'>
        <tr>
          <td>
            <region:render section='body'/>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
