<#--
 *    Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a 
 *    copy of this software and associated documentation files (the "Software"), 
 *    to deal in the Software without restriction, including without limitation 
 *    the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *    and/or sell copies of the Software, and to permit persons to whom the 
 *    Software is furnished to do so, subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included 
 *    in all copies or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *    OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *    IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *    CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *    OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *    THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author         David E. Jones (jonesde@ofbiz.org)
 *@version        $Revision: 1.1 $
 *@since          3.0
-->

<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>

${pages.get("/facility/FacilityTabBar.ftl")}

<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
    <tr>
        <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
                <tr>
                    <td><div class="boxhead">Stock Moves To Do</div></td>
                    <td align="right">&nbsp;</td>
                </tr>
            </table>
            <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">
                <tr>
                    <td align="left" width="100%">
                        <table border="1" cellspacing="0" cellpadding="2">
                            <tr>
                                <td><div class="tableheadtext">Header One</div></td>
                            </tr>
                            <#if xxx?has_content>
                                <#list xxx as yyy>
                                    <tr>
                                        <td><div class="tabletext">&nbsp;</div></td>
                                    </tr>
                                </#list>
                            <#else>
                                <tr><td colspan="7"><div class="head3">No picks are started right now.</div></td></tr>
                            </#if>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<#else>
    <h3>You do not have permission to view this page. ("FACILITY_VIEW" or "FACILITY_ADMIN" needed)</h3>
</#if>
