${pages.get("/includes/header.ftl")}
  <div class="contentarea">
    <div style='border: 0; margin: 0; padding: 0; width: 100%;'>
      <table style='border: 0; margin: 0; padding: 0; width: 100%;' cellpadding='0' cellspacing='0'>
        <tr>
          
          <td width='100%' valign='top' align='left'>
            ${pages.get("/includes/errormsg.ftl")}
            ${pages.get(page.path)}
          </td>
          
        </tr>
      </table>       
    </div>
    <div class='spacer'></div>
  </div>
${pages.get("/includes/footer.ftl")}

