<table width='100%' border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td bgcolor='#678475' align=center valign=center width='100%'>
      <div class="boxhead">Product&nbsp;Search</div>
    </td>
  </tr>
  <tr>
    <td align="center" valign="center" bgcolor='white' width='100%'>
      <form name="keywordsearchform" method="POST" action="<%=response.encodeURL(controlPath + "/keywordsearch")%>" style='margin: 0;'>
        <input type=hidden name="VIEW_SIZE" value="10">
        <p><input type="text" name="SEARCH_STRING" size="14" maxlength="50"><a href="javascript:document.keywordsearchform.submit()" class="buttontext">&nbsp;Find</a></p>
      </form>
    </td>
  </tr>
</table>