<!-- Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org -->
<#--
 *  Copyright (c) 2003-2004 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Rev: 4047 $
 *@since      2.1
-->

${pages.get("/currentsite/head.ftl")}

<div class="rydges-main">
	${pages.get("/currentsite/top.ftl")}

	<div class="rydges-column-left">
	    ${pages.get("/currentsite/left.ftl")}
	</div>

	<div class="rydges-column-main">
    	<div class="barwidth">
		  ${pages.get("/templates/rightbar.ftl")}
		</div>
		<div class="barmiddle">
    	${common.get("/includes/messages.ftl")}
    	${pages.get(page.path)}
    	</div>
  	</div>

	${pages.get("/currentsite/footer.ftl")}
</div>
<#if screens?exists>${screens.render("component://opentravelsystem/includes/footer.ftl")}</#if>

<#--
${pages.get("/includes/header.ftl")}

<div class="ecom-mainarea">
  ${pages.get("/templates/leftbar.ftl")}
  ${pages.get("/templates/rightbar.ftl")}
  <div class="ecom-column-main">
    ${common.get("/includes/messages.ftl")}
    ${pages.get(page.path)}
  </div>
</div>

${pages.get("/includes/footer.ftl")}
-->