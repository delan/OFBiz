<#--
$Id$

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
<#assign includeHtmlArea=false/>
<#if "Y"=page.includeHtmlArea?if_exists><#assign includeHtmlArea=true/></#if>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<#-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> <html> -->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>${(productStore.storeName)?if_exists}: ${page.title?if_exists}</title>
    <script language="javascript" src="<@ofbizContentUrl>/images/selectall.js</@ofbizContentUrl>" type="text/javascript"></script>
    <script language="javascript" src="<@ofbizContentUrl>/images/calendar1.js</@ofbizContentUrl>" type="text/javascript"></script>
    <script language="javascript" src="<@ofbizContentUrl>/images/fieldlookup.js</@ofbizContentUrl>" type="text/javascript"></script>
    <link rel="stylesheet" href="/hds/images/ecommain.css" type="text/css"/>
    <link rel="stylesheet" href="/hds/images/frontend.css" type="text/css"/>

    <#-- Append CSS for catalog -->
    <#if catalogStyleSheet?exists>
        <link rel="stylesheet" href="${catalogStyleSheet}" type="text/css"/>
    </#if>
    <#-- Append CSS for tracking codes -->
    <#if sessionAttributes.overrideCss?exists>
        <link rel="stylesheet" href="${sessionAttributes.overrideCss}" type="text/css"/>
    </#if>
    <#-- Meta tags if defined by the page action -->
    <#if metaDescription?exists>
        <meta name="description" content="${metaDescription}"/>
    </#if>
    <#if metaKeywords?exists>
        <meta name="keywords" content="${metaKeywords}"/>
    </#if>


<body>
