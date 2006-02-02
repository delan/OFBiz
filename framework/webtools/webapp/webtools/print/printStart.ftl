<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org -->
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Rev$
 *@since      3.5
-->
<#assign serverRoot = Static["org.ofbiz.webapp.control.RequestHandler"].getDefaultServerRootUrl(request, false)>
<#assign resetCookie = request.getParameter("reset-cookies")?default("false")>
<#assign serverHost = request.getServerName()>
<#assign screenMap = (requestAttributes.screenPrinterMap)?if_exists>
<#assign sessionId = (requestAttributes.sessionId)?if_exists>
<#assign screens = (screenMap.keySet())?if_exists>
<#assign auto = "true">
<html>
    <body>
      <center>
       <#if screens?has_content>
        <object align="center" height="50" width="350" classid="java:org.ofbiz.webtools.print.applet.FopPrintApplet"
                type="application/x-java-applet" mayscript="true" archive="/webtools/applet/ofbiz-webtools-print.jar"
                codebase="/webtools/applet" server-url="${serverRoot}" session-id="${sessionId}" reset-cookies="${resetCookie}"
                <#assign count = 1>
                <#list screens as screen>
                  <#assign printer = screenMap.get(screen)?if_exists>
                  <#if printer?has_content>
                    printer.${count}="${printer}"
                  <#else>
                    <#assign auto = "false">
                  </#if>
                  screen.${count}="${screen}"
                  <#assign count = count + 1>
                </#list>>

            <object align="center" height="200" width="600" classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93">
	            <param name="archive" value="/webtools/applet/ofbiz-webtools-print.jar">

                <param name="codebase" value="/webtools/applet">
                <param name="code" value="org.ofbiz.webtools.print.applet.FopPrintApplet">
                <param name="mayscript" value="true">
                <param name="reset-cookies" value="${resetCookie}">
                <param name="session-id" value="${sessionId}">
                <param name="server-url" value="${serverRoot}">                
                <#assign count = 1>
                <#list screens as screen>
                  <#assign printer = screenMap.get(screen)?if_exists>
                  <#if printer?has_content>
                    <param name="printer.${count}" value="${printer}">
                  <#else>
                    <#assign auto = "false">
                  </#if>
                  <param name="screen.${count}" value="${screen}">
                  <#assign count = count + 1>
                </#list>
            </object>
        </object>
      <#else>
        <p>Nothing to print.</p>
      </#if>
      <#if auto == "true">
        <script language="javascript">
            window.opener.focus();
        </script>
      </#if>
      </center>
    </body>
</html>
