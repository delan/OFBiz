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
 *@author     Malin Nicolas (nicolas@ptimalin.net)
 *@version    $Rev:$
 *@since      3.0
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>

${pages.get("/mrp/MrpTabBar.ftl")}

<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
   <td align="center" width="100%">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
		<tr>
            <#if requestParameters.hideSearch?default("N") == "N">
    		<td><div class="boxhead">${uiLabelMap.CommonSelection}</div></td>
    		<#else>
        	<td align="right"><div class="tabletext">
                <a href="<@ofbizUrl>/FindProposedOrder?hideSearch=N${paramList?if_exists}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonNewSelection}</a>
              </#if>
           	  </div>
       		</td>
    	</tr>
	</table>
    <#if requestParameters.hideSearch?default("N") != "Y">
		${singleWrapper.renderFormString()} 
    </#if>
    </td>
  </tr>
</table>

<#if showList?default("Y") == "Y">
<br>
	${listWrapper.renderFormString()}
</#if>
<hr/>
