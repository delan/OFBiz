<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method = "html" indent = "yes" omit-xml-declaration = "yes"/>  

	<xsl:template match="/">
	
<script src="xml.js" />
<script src="edit.js" />

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

 <input type="button" value="Process" onClick="processXML()" />


<!--    <xsl:apply-templates select="tree:children/tree:node">
      <xsl:with-param name="indent-level" select="$indent-level + 1"/>
    </xsl:apply-templates>
-->
	<!-- Copy each element found -->
 <textarea name="in" id="inputarea" rows="10" cols="80">
	<xsl:copy-of select="/" />
 </textarea>

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
		<xsl:copy-of select="child::*" />
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
