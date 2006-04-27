<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.layoutSettings)?exists><#assign layoutSettings = requestAttributes.layoutSettings></#if>
<#if (requestAttributes.locale)?exists><#assign locale = requestAttributes.locale></#if>
<#if (requestAttributes.availableLocales)?exists><#assign availableLocales = requestAttributes.availableLocales></#if>
<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#if (requestAttributes.partyGroup)?exists><#assign partyGroup = requestAttributes.partyGroup></#if>

<html>
<head>
	<title>"Opentravelsystem: Hotel Demo"</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<#-- <title>${layoutSettings.companyName}: <#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)?if_exists}</#if></title> -->
	<script language='javascript' src='<@ofbizContentUrl>/images/calendar1.js</@ofbizContentUrl>' type='text/javascript'></script>
	<script language='javascript' src='<@ofbizContentUrl>/images/selectall.js</@ofbizContentUrl>' type='text/javascript'></script>
    <script language="javascript" src="<@ofbizContentUrl>/images/fieldlookup.js</@ofbizContentUrl>" type="text/javascript"></script>
	<link rel='stylesheet' href='<@ofbizContentUrl>/images/ecommain.css</@ofbizContentUrl>' type='text/css'>
	<link rel='stylesheet' href='<@ofbizContentUrl>/hds/frontend.css</@ofbizContentUrl>' type='text/css'>
	<link rel='stylesheet' href='<@ofbizContentUrl>/images/tabstyles.css</@ofbizContentUrl>' type='text/css'>
	<script language="JavaScript" type="text/JavaScript">
		var ie=document.all;var gc=document.getElementById&&!document.all;var op=(window.navigator.userAgent.toLowerCase().indexOf('opera/7')!=-1)?1:0;var mac=(window.navigator.platform.toLowerCase().indexOf('mac')!=-1)?1:0;var sf=(window.navigator.userAgent.toLowerCase().indexOf('safari')!=-1)?1:0;var ns=document.layers;
		if(document.compatMode=="CSS1Compat"){document.getElementsByTagName("HTML")[0].style.overflowX="visible";document.getElementsByTagName("HTML")[0].style.overflowY="auto";}
		if(!gc){document.write('<style>body{overflow:auto}div.rydges-column-main{width:100%;}<\/style>');}if(op){document.write('<style>body{padding:0px}<\/style>');}if(!gc && !op){document.write('<style>div.barmiddle{float:left;}<\/style>');}
		function MM_openBrWindow(theURL,winName,features){window.open(theURL,winName,features);}
	</script>
</head>
<body>
