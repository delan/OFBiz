<!--
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
 *@author     Johan Isacsson
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap)
 *@version    $Rev:$
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<script language="JavaScript">


var re_id = new RegExp('id=(\\d+)');
var num_id = (re_id.exec(String(window.location))
	? new Number(RegExp.$1) : 0);
var obj_caller = (window.opener ? window.opener.lookups[num_id] : null);


// function passing selected value to calling window
function set_value(value) {
	if (!obj_caller) return;
	window.close();
	obj_caller.target.value = value;
}
</script>
<table width="100%" cellpadding="5" cellspacing="0" border="0" bgcolor="#FFFFFF">
<tr><td>
${lookupFieldWrapper.renderFormString()}
</td></tr></table>
<table width="100%" cellpadding="5" cellspacing="0" border="0">
<tr bgcolor="#000000"><td colspan="2"><font color="#FFFFFF"><strong>${uiLabelMap.WorkEffortResultOfLookup}</strong></font></td></tr>
<#if resultList?has_content>
<#list resultList as result>
<tr><td width="25%"><a href="javascript:set_value('${result.value}');">${result.value?if_exists}</a></td><td nowrap width="75%"><a href="javascript:set_value('${result.value}');">${result.label?if_exists}</a></td></tr>
</#list>
<#else>
<tr><td width="100%" colspan="2"></td></tr>
</#if>
</table>

<script language="JavaScript">
document.forms[0].elements[3].focus();
</script>