<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	version='1.0'>
 <xsl:output
	method="xml"
	indent="yes"
 />
 <xsl:param name="reader">seed</xsl:param>
 <xsl:param name="fromemail">no-reply@127.0.0.1</xsl:param>
 <xsl:param name="password">47ca69ebb4bdc9ae0adec130880165d2cc05db1a</xsl:param>
 <xsl:param name="ofbizhome"/>

 <xsl:template match="/">
<!--
  <xsl:message>OFBIZHOME(<xsl:value-of select="$ofbizhome"/>)</xsl:message>
-->
  <entity-engine-xml>
   <xsl:comment>READER <xsl:value-of select="$reader"/></xsl:comment>
   <xsl:apply-templates mode="components">
    <xsl:with-param name="basedir"><xsl:value-of select="$ofbizhome"/></xsl:with-param>
   </xsl:apply-templates>
  </entity-engine-xml>
 </xsl:template>

 <xsl:template mode="components" match="load-components">
  <xsl:param name="basedir"/>
  <xsl:variable name="dir"><xsl:value-of select="$basedir"/>/<xsl:value-of select="@parent-directory"/></xsl:variable>
  <xsl:variable name="file"><xsl:value-of select="$dir"/>/component-load.xml</xsl:variable>
<!--
  <xsl:message>LOAD COMPONENT(dir: <xsl:value-of select="$dir"/>)</xsl:message>
-->
  <xsl:apply-templates mode="components" select="document($file)">
   <xsl:with-param name="basedir"><xsl:value-of select="$dir"/></xsl:with-param>
  </xsl:apply-templates>
 </xsl:template>

 <xsl:template mode="components" match="load-components[@parent-directory='hot-deploy']">
 </xsl:template>

 <xsl:template mode="components" match="load-component">
  <xsl:param name="basedir"/>
  <xsl:variable name="dir"><xsl:value-of select="$basedir"/>/<xsl:value-of select="@component-location"/></xsl:variable>
  <xsl:variable name="file"><xsl:value-of select="$dir"/>/ofbiz-component.xml</xsl:variable>
  <xsl:apply-templates mode="component" select="document($file)">
   <xsl:with-param name="basedir"><xsl:value-of select="$dir"/></xsl:with-param>
  </xsl:apply-templates>
 </xsl:template>

 <xsl:template mode="component" match="entity-resource[@type='data' and @reader-name=$reader]">
  <xsl:param name="basedir"/>
<!--
  <xsl:message>ENTITY-RESOURCE(<xsl:value-of select="name()"/>)(basedir: <xsl:value-of select="$basedir"/>)</xsl:message>
-->
  <xsl:variable name="file"><xsl:value-of select="$basedir"/>/<xsl:value-of select="@location"/></xsl:variable>
  <xsl:apply-templates mode="data" select="document($file)">
   <xsl:with-param name="basedir"><xsl:value-of select="$basedir"/></xsl:with-param>
  </xsl:apply-templates>
 </xsl:template>

 <xsl:template mode="components" match="/|*">
  <xsl:param name="basedir"/>
<!--
  <xsl:message>DEFAULT COMPONENTS RULE(<xsl:value-of select="name()"/>)(basedir: <xsl:value-of select="$basedir"/>)</xsl:message>
-->
  <xsl:apply-templates mode="components">
   <xsl:with-param name="basedir"><xsl:value-of select="$basedir"/></xsl:with-param>
  </xsl:apply-templates>
 </xsl:template>

 <xsl:template mode="component" match="/|*">
  <xsl:param name="basedir"/>
<!--
  <xsl:message>DEFAULT COMPONENT RULE(<xsl:value-of select="name()"/>)(basedir: <xsl:value-of select="$basedir"/>)</xsl:message>
-->
  <xsl:apply-templates mode="component">
   <xsl:with-param name="basedir"><xsl:value-of select="$basedir"/></xsl:with-param>
  </xsl:apply-templates>
 </xsl:template>

 <xsl:template mode="data" match="/|*">
  <xsl:param name="basedir"/>
<!--
  <xsl:message>ENTITY DATA(<xsl:value-of select="name()"/>)(basedir: <xsl:value-of select="$basedir"/>)</xsl:message>
-->
  <xsl:apply-templates mode="data">
   <xsl:with-param name="basedir"><xsl:value-of select="$basedir"/></xsl:with-param>
  </xsl:apply-templates>
 </xsl:template>

 <xsl:template mode="data" match="
  ProductStoreEmailSetting[
   @bccAddress='ofbiztest@yahoo.com' or
   @fromAddress='ofbiztest@yahoo.com']|ContactList[
   @verifyEmailFrom='ofbiztest@yahoo.com'
  ]|ContactMech[
   @infoString='ofbiztest@yahoo.com'
  ]|WorkflowDataField[
   @initialValue='ofbiztest@yahoo.com'
  ]|UserLogin[
   @currentPassword='47ca69ebb4bdc9ae0adec130880165d2cc05db1a'
  ]">
  <xsl:element name="{name()}">
   <xsl:apply-templates mode="match" select="*|@*|text()|comment()"/>
  </xsl:element>
 </xsl:template>

 <xsl:template mode="match" match="ProductStoreEmailSetting/@bccAddress|ProductStoreEmailSetting/@fromAddress|ProductStoreEmailSetting/@verifyEmailFrom|ContactMech/@infoString|ContactList/@verifyEmailFrom|WorkflowDataField/@initialValue">
  <xsl:attribute name="{name()}">
   <xsl:choose>
    <xsl:when test=". = 'ofbiztest@yahoo.com'"><xsl:value-of select="$fromemail"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
   </xsl:choose>
  </xsl:attribute>
 </xsl:template>

 <xsl:template mode="match" match="UserLogin/@currentPassword">
  <xsl:attribute name="{name()}">
   <xsl:choose>
    <xsl:when test=". = '47ca69ebb4bdc9ae0adec130880165d2cc05db1a'"><xsl:value-of select="$password"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
   </xsl:choose>
  </xsl:attribute>
 </xsl:template>

 <xsl:template match="comment()|@*" mode="data">
  <xsl:apply-templates mode="data"/>
 </xsl:template>

 <xsl:template mode="match" match="*">
  <xsl:element name="{name()}">
   <xsl:apply-templates mode="match" select="*|@*|text()|comment()"/>
  </xsl:element>
 </xsl:template>

 <xsl:template mode="data" match="text()"/>
 <xsl:template mode="match" match="text()"/>
 <xsl:template mode="components" match="text()"/>
 <xsl:template mode="component" match="text()"/>

 <xsl:template mode="match" match="@*">
  <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
 </xsl:template>

 <xsl:template mode="match" match="comment()">
  <xsl:copy-of select="."/>
 </xsl:template>
</xsl:stylesheet>
