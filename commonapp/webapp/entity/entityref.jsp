<!--
 *  Copyright (c) 2001 The Open For Business Project and respected authors.
 
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
-->

<%@ page import="org.ofbiz.core.util.*" %>

<HTML>
<HEAD>
<TITLE>Entity Reference Chart</TITLE>
</HEAD>
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%
	String search = null;
	String list = controlPath + "/view/entityref_list";
	String main = controlPath + "/view/entityref_main";
	search = (String) request.getParameter("search");
	if ( search != null ) {
		list = list + "?search=" + search;
		main = main + "?search=" + search;
	}
%>
<FRAMESET cols="30%,70%">
<FRAME src="<%= list %>" name="entityListFrame">
<FRAME src="<%= main %>" name="entityFrame">
</FRAMESET>
<NOFRAMES>
<H2>
Frame Alert</H2>

<P>
This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client.
<BR>
</HTML>
