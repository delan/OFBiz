<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<TABLE width='100%' border=0 cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">${uiLabelMap.ViewProductDetail}</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td align=center>
            <form name="viewproductdetailform" method="POST" action="<@ofbizUrl>/product/</@ofbizUrl>" style='margin: 0;'>
              <input type='hidden' name="VIEW_SIZE" value="10">
              <div class='tabletext'>
                <input type='text' class='inputBox' name="product_id" size="14" maxlength="50">
              </div><br>
              <div class='tabletext'><a href="javascript:document.viewproductdetailform.submit()" class="buttontext">${uiLabelMap.ViewProduct}</a></div>
            </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<br>
