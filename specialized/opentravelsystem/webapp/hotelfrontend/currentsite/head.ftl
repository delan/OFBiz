<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.layoutSettings)?exists><#assign layoutSettings = requestAttributes.layoutSettings></#if>
<#if (requestAttributes.locale)?exists><#assign locale = requestAttributes.locale></#if>
<#if (requestAttributes.availableLocales)?exists><#assign availableLocales = requestAttributes.availableLocales></#if>
<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#if (requestAttributes.partyGroup)?exists><#assign partyGroup = requestAttributes.partyGroup></#if>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<#-- <title>${layoutSettings.companyName}: <#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)?if_exists}</#if></title> -->
	<script language='javascript' src='<@ofbizContentUrl>/images/calendar1.js</@ofbizContentUrl>' type='text/javascript'></script>
	<script language='javascript' src='<@ofbizContentUrl>/images/selectall.js</@ofbizContentUrl>' type='text/javascript'></script>
	<link rel='stylesheet' href='<@ofbizContentUrl>/hotelfrontend/images/frontend.css</@ofbizContentUrl>' type='text/css'>
	<link rel='stylesheet' href='<@ofbizContentUrl>/images/tabstyles.css</@ofbizContentUrl>' type='text/css'>
	<script language="JavaScript" type="text/JavaScript">
		var ie=document.all;var gc=document.getElementById&&!document.all;var op=(window.navigator.userAgent.toLowerCase().indexOf('opera/7')!=-1)?1:0;var mac=(window.navigator.platform.toLowerCase().indexOf('mac')!=-1)?1:0;var sf=(window.navigator.userAgent.toLowerCase().indexOf('safari')!=-1)?1:0;var ns=document.layers;
		if(document.compatMode=="CSS1Compat"){document.getElementsByTagName("HTML")[0].style.overflowX="visible";document.getElementsByTagName("HTML")[0].style.overflowY="auto";}
		if(!gc){document.write('<style>body{overflow:auto}div.rydges-column-main{width:100%;}<\/style>');}if(op){document.write('<style>body{padding:0px}<\/style>');}if(!gc && !op){document.write('<style>div.barmiddle{float:left;}<\/style>');}
		function MM_openBrWindow(theURL,winName,features){window.open(theURL,winName,features);}
	</script>
</head>
<body>
