
<#assign associatedProducts = Static["org.ofbiz.commonapp.product.catalog.CatalogWorker"].getRandomCartProductAssoc(request)>

<#if associatedProducts?exists>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="center">
              <div class="boxhead">You&nbsp;Might&nbsp;Like...</div>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td>
    <table width='100%' CELLSPACING="0" CELLPADDING="0" BORDER="0">
      <#-- random complementary products -->
      <#list associatedProducts as miniProduct> 
        <tr>
          <td>
            <#assign miniProdQuantity = 1>
            <#assign miniProdFormName = "theminiassocprod" + associatedProduct_index + "form">
            <#include "/catalog/miniproductsummary.jsp">
          </td>
        </tr>
        <#if associatedProduct_has_next>
          <tr><td><hr class='sepbar'></td></tr>
        </#if>
      </#list>
    </table>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</#if>

