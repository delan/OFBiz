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
 *@version    $Revision$
 *@since      2.1
-->

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <#-- this needs to be fully qualified to appear in email; the server must also be available -->
    <link rel='stylesheet' href='<@ofbizContentUrl>/images/maincss.css</@ofbizContentUrl>' type='text/css'>      
</head>

<body> 

<#-- custom logo or text can be inserted here -->

<p class="head1">Order Confirmation</p>
<p>NOTE: This is a DEMO store-front.  Orders placed here will NOT be billed, and will NOT be fulfilled.</p>

<#-- display the standard order status page header and items -->
<#include "orderheader.ftl">
<br>
<#include "orderitems.ftl">
<div align='right'>  
  <#-- change this to not display when no account was created -->
  <a href="<@ofbizUrl encode="true" fullPath="true">/orderstatus?order_id=${orderHeader.orderId}</@ofbizUrl>" class="buttontextbig">[View&nbsp;Order]&nbsp;</a>
  <a href="<@ofbizUrl encode="true" fullPath="true">/main</@ofbizUrl>" class="buttontextbig">[Continue&nbsp;Shopping]</a>
</div>
</body>   