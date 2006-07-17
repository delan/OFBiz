<#--

Copyright 2001-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->

<div class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead">&nbsp;${uiLabelMap.EcommerceSpecialOffers}</div>
    </div>
    <div class="screenlet-body">
        <#-- show promotions text -->
        <#list productPromosAllShowable as productPromo>
            <div class="tabletext"><a href="<@ofbizUrl>showPromotionDetails?productPromoId=${productPromo.productPromoId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonDetails}</a> ${productPromo.promoText}</div>
            <#if productPromo_has_next>
                <hr class="sepbar"/>
            </#if>
        </#list>
    </div>
</div>

<#if (shoppingCartSize > 0)>
  ${screens.render(promoUseDetailsInlineScreen)}
</#if>
