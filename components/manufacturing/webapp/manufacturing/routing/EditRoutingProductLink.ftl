<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     Olivier.Heintz@nereide.biz
 *@version    $Rev$
 *@since      3.0
-->
<#if (requestAttributes.uiLabelMap)?exists>
    <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>


	<div class="head1">${uiLabelMap.ManufacturingEditRoutingProductLink}&nbsp;
	</div>
        <#if byProduct?has_content>
            <div class='tabletext'>
                <a href="<@ofbizUrl>/EditProductBom?productId=${byProduct}&amp;productAssocTypeId=MANUF_COMPONENT</@ofbizUrl>" class="buttontext">[${uiLabelMap.ManufacturingProductBom}]</a>
            </div>
        </#if>
        
        ${addRoutingProductLinkWrapper.renderFormString()}
		<hr class="sepbar">
	<#if routingProductLink?has_content>
			${updateRoutingProductLinkWrapper.renderFormString()}
			<br>
	</#if>
	<#if allRoutingProductLinks?has_content>
		${listRoutingProductLinkWrapper.renderFormString()}
	</#if>


	