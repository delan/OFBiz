
<#-- get these for the promoText -->
<#assign productPromos = Static["org.ofbiz.commonapp.product.promo.ProductPromoWorker"].getCatalogProductPromos(delegator, request)>

<#-- Make sure that at least one promo has non-empty promoText -->
<#assign showPromoText = false>
<#foreach productPromo in productPromos>
    <#if productPromo.promoText?has_content><#assign showPromoText = true></#if>
</#foreach>

<#if showPromoText>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="center">
              <div class="boxhead">&nbsp;Special&nbsp;Offers</div>
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
                <table width='100%' cellspacing="0" cellpadding="1" border="0">
                  <#-- show promotions text -->
                  <#foreach productPromo in productPromos>
                    <#if productPromo.promoText?has_content>
                        <tr>
                          <td>
                            <div class='tabletext'>${productPromo.promoText}</div>
                          </td>
                        </tr>
                        <#if productPromo_has_next>
                          <tr><td><hr class='sepbar'></td></tr>
                        </#if>
                    </#if>
                  </#foreach>
                </table>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</#if>

