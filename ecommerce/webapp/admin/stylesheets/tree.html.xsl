<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tree="http://www.einnovation.com/xmlns/WebUI/Tree"
    xmlns:page="http://www.einnovation.com/xmlns/WSP/Admin/Content"
    xmlns:html="http://www.w3.org/1999/xhtml">

  <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
  
  <xsl:template match="*" priority="-0.5">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
  
  <!-- FIXME: Un-hardcode "cvs_repository" from this file. -->
  
  <!--
      Transform a node with
          * @url = path
          * @icon-url is not present
      into a node with:
          * @url = /admin/edit.html?path=@url&repository=cvs_repository
          * @icon-url =
              - /admin/images/tree/page.gif (if it's a leaf)
              - /admin/images/tree/folder.gif (if it's not)
      If it is not a leaf, no URL is given.
  -->
  <xsl:template match="tree:node">
    <tree:node>
      <xsl:copy-of select="@*[not(name() = 'url' or name() = 'icon-url')]"/>

      <xsl:if test="@url and (@leaf = 'true')">
        <xsl:attribute name="path">
          <xsl:value-of select="@url"/>
        </xsl:attribute>
        <xsl:attribute name="url">
          <xsl:text>/admin/editors/edit.html?path=</xsl:text>
          <xsl:value-of select="@url"/>
        </xsl:attribute>
      </xsl:if>
      
      <xsl:attribute name="icon-url">
        <xsl:text>/admin/images/tree/</xsl:text>
        <xsl:choose>
          <xsl:when test="@leaf = 'true'">
            <xsl:text>page.gif</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>folder.gif</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      
      <!-- Copy all the child elements. -->
      
      <xsl:apply-templates select="*"/>
      
    </tree:node>
  </xsl:template>
  
</xsl:stylesheet>
