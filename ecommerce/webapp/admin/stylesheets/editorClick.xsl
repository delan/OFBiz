<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

 	<xsl:output method = "html" indent = "yes" omit-xml-declaration = "yes"/> 

	<xsl:template match="/">
    <xsl:param name="indent-level" select="0"/>
	

<STYLE type="text/css">
        BODY {font:x-small 'Verdana'; margin-right:1.5em}
      
        .c  {cursor:hand}
      
        .b  {color:red; font-family:'Courier New'; font-weight:bold; text-decoration:none}
      
        .e  {margin-left:1em; text-indent:-1em; margin-right:1em}
      
        .k  {margin-left:1em; text-indent:-1em; margin-right:1em}
      
        .t  {color:#990000}
      
        .xt {color:#990099}
      
        .ns {color:red}
      
        .m  {color:blue}
      
        .tx {font-weight:bold}
      
        .db {text-indent:0px; margin-left:1em; margin-top:0px; margin-bottom:0px;
             padding-left:.3em; border-left:1px solid #CCCCCC; font:small Courier}
      
        .di {font:small Courier}
      
        .d  {color:blue}
      
        .pi {color:blue}
      
        .cb {text-indent:0px; margin-left:1em; margin-top:0px; margin-bottom:0px;
             padding-left:.3em; font:small Courier; color:#888888}
      
        .ci {font:small Courier; color:#888888}
        PRE {margin:0px; display:inline}
</STYLE>


<SCRIPT TYPE="TEXT/JAVASCRIPT" LANGUAGE="JAVASCRIPT">
	 
		
		var stdBrowser = (document.getElementById) ? true : false

		function popToggle(evt,currElem,intd) {
			//this is getting called too much
			//alert( document.getElementById(intd.id).item(0) );
			alert( currElem );
			
			//var popUpWin = document.getElementById(currElem).style;
			//alert( popUpWin );
			var popUpWin = (stdBrowser) ? document.getElementById(currElem).style : eval("document." + currElem)
			if (popUpWin.visibility == "visible" || popUpWin.visibility == "show")
				popDown(currElem);
			else
				popUp(evt, currElem,intd);
		}
				
		function popUp(evt,currElem,intd) {

			var popnum;	
			<!--
			for (popnum=1;popnum<1;popnum++) {
				var elem = 'popUp' + popnum;
				var test = (stdBrowser) ? document.getElementById(elem).style : eval("document." + elem);
				if (test.visibility=="visible" || test.visibility=="show")
					popDown(elem);
			}
			-->

			
			var popUpWin = (stdBrowser) ? document.getElementById(currElem).style : eval("document." + currElem);
			
			myClickBounds = getBounds( intd );
			//alert( document.all );
			/*
			if (document.all) {
				popUpWin.pixelTop = parseInt(evt.y)-15;
				popUpWin.pixelLeft = intd.offsetLeft ;  //Math.max(2,parseInt(evt.x)+10);
			}
			
			else {
			*/
				if (stdBrowser) {
					//popUpWin.top = parseInt(evt.pageY)-15 + "px";
					//popUpWin.left = Math.max(2,parseInt(evt.pageX)+10) + "px";
					popUpWin.top = myClickBounds.y + "px";
					popUpWin.left = myClickBounds.x + "px";
				}
				else {
					popUpWin.top = parseInt(evt.pageY)-15;
					popUpWin.left = Math.max(2,parseInt(evt.pageX)+10);
				}
			//}
			//We should just create a SPAN using the DOM API
			
			document.getElementById('SiteText').value = intd.innerHTML;
			editingcell = intd;
			popUpWin.visibility = "visible";
			document.getElementById('SiteText').focus();
		}

		function popDown(currElem) {
			var popUpWin = (stdBrowser) ? document.getElementById(currElem).style : eval("document." + currElem);

			if (document.layers) { //if Netscape:
				popUpWin.visibility = "hide";
			}
			else {
				popUpWin.visibility = "hidden";
			}
			editingcell.innerHTML = document.getElementById('SiteText').value;
		}
		
		function getBounds(el){
			for (var lx=0,ly=0;el!=null;
				lx+=el.offsetLeft,ly+=el.offsetTop,el=el.offsetParent);
			return {x:lx,y:ly}
		}
		var editingcell;
		var colours = new Array();
		colours[1] = "#f0f0ff";
		colours[2] = "#e0e0ff";
		colours[3] = "#d0d0ff";
		colours[4] = "#c0c0ff";
		colours[5] = "#b0b0ff";
		colours[6] = "#a0a0ff";
		colours[7] = "#9090ff";
		colours[8] = "#8080ff";
		colours[9] = "#7070ff";
	</SCRIPT>
	<STYLE TYPE="TEXT/CSS">

		.popUpStyle {background-color: transparent;	border: #000000 0px solid;
					 layer-background-color: transparent;
					 font: 10px verdana, arial, helvetica, sans-serif; padding: 0px;
					 position: absolute; visibility: hidden}
	</STYLE>	

<SPAN CLASS="popUpStyle" ID="popUp1">
	<form name="popUp1">
		<textarea name="SITE" id="SiteText" cols="50" rows="5">site.</textarea>
		<input type="button" value="ok" onClick="popDown('popUp1');"/>
	</form>
</SPAN>

<!--    <xsl:apply-templates select="tree:children/tree:node">
      <xsl:with-param name="indent-level" select="$indent-level + 1"/>
    </xsl:apply-templates>
-->
	<!-- Copy each element found -->
	<xsl:apply-templates select="*"/>
</xsl:template>	

<!--
When we go to save we have to grab all the attributes, text, comments and nodes and save them?


-->
<xsl:template match="@*">
	attrib
	<SPAN class="t">
	 <xsl:text />
	 <xsl:value-of select="name(.)" />
	 </SPAN>
	 <SPAN class="m">="</SPAN>
	<B>
	 <xsl:value-of select="." />
	 </B>
	 <SPAN class="m">"</SPAN>
</xsl:template>

<!-- Template for text nodes 
 
<xsl:template match="text()">
Hey
	<xsl:choose>
	<xsl:when test="name(.) = '#cdata-section'">
	 <xsl:call-template name="cdata" />
	 </xsl:when>
	<xsl:otherwise>
	<DIV class="e">
	 <SPAN class="b"> </SPAN>
	<SPAN class="tx">
	 <xsl:value-of select="." />
	 </SPAN>
	 </DIV>
	 </xsl:otherwise>
	 </xsl:choose>
</xsl:template>
-->

<xsl:template match="*">
		<!-- Do not put tables inside of tables or they will get double mouse clicks -->
		<table width="100%" cellspacing="10">
		<tr>
		<td bgcolor="#f0f0ff">

			<font face="Arial, Helvetica" size="+2"><b><xsl:value-of select="name(.)" /> </b></font>
			
			</td>
			</tr>
			<tr><td> <SPAN onClick="popUp(event,'popUp1',this);"><xsl:attribute name="ID">myText<xsl:number value="position()" format="1"/></xsl:attribute>
				<xsl:value-of select="text()" />
			</SPAN>
			</td>
			</tr>
			<tr>
			<td>
			<xsl:apply-templates select="child::*" /></td></tr>
		</table>
</xsl:template>

<xsl:template match="processing-instruction()|comment()"> 
		Some comment
		<xsl:apply-templates/>
</xsl:template>

<!--
	<xsl:template match="p">
	</xsl:template>
	
		
	<xsl:template match="hr|p|ul|li|a|b|u|i|font|table|tr|td|th|form|input|div">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
-->	
<!--

<xsl:call-template name="for_loop">
	<xsl:with-param name="i">1</xsl:with-param>
	<xsl:with-param name="count">10</xsl:with-param>
</xsl:call-template>

<xsl:template name="for_loop">
	<xsl:param name="i"/>
	<xsl:param name="count"/>
	<xsl:if test="$i &lt;= $count">
		body of loop goes here 
	</xsl:if>
	<xsl:if test="$i &lt;= $count">
	<xsl:call-template name="for_loop">
	
		<xsl:with-param name="i">
		Increment index
		<xsl:value-of select="$i + 1"/>
	</xsl:with-param>
	<xsl:with-param name="count">
	<xsl:value-of select="$count"/>
	</xsl:with-param>
	</xsl:call-template>
	</xsl:if>
</xsl:template>

-->
</xsl:stylesheet>
