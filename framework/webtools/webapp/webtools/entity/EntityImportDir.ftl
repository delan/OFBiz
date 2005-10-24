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
 * @author Brian Johnson (bmj@camfour.com)
 * @author Ray Barlow (ray.barlow@makeyour-point.com)
 * @author David E. Jones (jonesde@ofbiz.org)
 * @author Jacopo Cappellato (tiz@sastau.it)
-->

<div class="head1">XML Import to DataSource(s)</div>
<div class="tabletext">This page can be used to import exported Entity Engine XML documents. These documents all have a root tag of "&lt;entity-engine-xml&gt;".</div>
<hr>
  <div class="head2">Import:</div>

  <form method="post" action="<@ofbizUrl>EntityImportDir</@ofbizUrl>">
    <div class="tabletext">Absolute directory path:</div>
    <div><input type="text" class="inputBox" size="60" name="path" value="${path?if_exists}"/></div>
    <div class="tabletext"><input type="checkbox" name="mostlyInserts" <#if mostlyInserts?exists>"checked"</#if>/>Mostly Inserts?</div>
    <div class="tabletext"><input type="checkbox" name="maintainTimeStamps" <#if keepStamps?exists>"checked"</#if>/>Maintain Timestamps?</div>
    <div class="tabletext"><input type="checkbox" name="createDummyFks" <#if createDummyFks?exists>"checked"</#if>/>Create "Dummy" FKs?</div>
    <div class="tabletext"><input type="checkbox" name="deleteFiles" <#if (deleteFiles?exists || !path?has_content)>"checked"</#if>/>Delete Files Afterwards?</div>
    <div class="tabletext">TX Timeout Seconds:<input type="text" size="6" value="${txTimeoutStr?default("7200")}" name="txTimeout"/></div>
    <div class="tabletext">Pause (secs) between files:<input type="text" size="6" value="${filePauseStr?default("0")}" name="filePause"/></div>
    <div><input type="submit" value="Import Files"/></div>
  </form>
  <hr>
    <div class="head1">Results:</div>
    <#list messages as message>
        <div class="tabletext">${message}</div>
    </#list>
