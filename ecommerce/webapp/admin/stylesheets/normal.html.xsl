<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:page="http://www.einnovation.com/xmlns/WSP/Admin/Content"
    xmlns:list="http://www.einnovation.com/xmlns/WebUI/List"
    xmlns:tree="http://www.einnovation.com/xmlns/WebUI/Tree"
    xmlns:user="http://www.wspublisher.com/xmlns/User"
    xmlns:group="http://www.wspublisher.com/xmlns/Group"
    xmlns:html="http://www.w3.org/1999/xhtml">
  
  <xsl:output method="html" indent="yes"/>
  
  <!--
      This should be filled in by WSP when
      <use-request-parameters>true</use-request-parameters> is specified in a
      page's config file.
  -->
  <xsl:param name="requestURI" select="''"/>
  
  <xsl:template match="page:page">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="/admin/admin.css"/>
        <title><xsl:value-of select="page:title"/></title>
        <script type="text/javascript">
			function gotoPage( url, target )
			{
			  // Should perform any validation that we need to do here
			  document.wizard.action = url;
			  if ( target )
			  {
			      document.wizard.target = target;
			  }
			  else
			  {
			  	document.wizard.target = "_self";
			  }
			  document.wizard.submit();
			}
        </script>
        <xsl:apply-templates select="page:script"/>
        
      </head>
      
      <body bgcolor="#c6d3de">
       	<xsl:copy-of select="@onload"/>        	
        <xsl:apply-templates select="page:content"/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="page:content">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page:wizard|page:dialog|page:dialog2">
    <form name="wizard" method="post">
      <xsl:copy-of select="@enctype"/>
      <table align="center" border="0" cellspacing="0" cellpadding="0">
        <xsl:if test="@fill = 'yes'">
          <xsl:attribute name="width">
            <xsl:text>100%</xsl:text>
          </xsl:attribute>
        </xsl:if>
        <!--
            FIXME: Make this prettier.  The dialog currently looks like:
                 ______________________________________
                /                                      \
                |              Title                   |
                \______________________________________/
                /                                      \
                | Contents                             |
                |                                      |
                |                                      |
                \______________________________________/
                 ______   ______
                /Button\ /Button\
                \______/ \______/
            
            which is kinda ugly.  It would be nicer if it looked like this:
                 ______________________________________
                /                                      \
                |              Title                   |
                |______________________________________|
                |                                      |
                | Contents                             |
                |                                      |
                |                                      |
                \______________________________________/
                 ______   ______
                /Button\ /Button\
                \______/ \______/
        -->
        <xsl:if test="page:title">
          <tr>
            <td align="center"><xsl:apply-templates select="page:title"/></td>
          </tr>
        </xsl:if>
        <tr>
          <td align="center"><xsl:apply-templates select="page:buttons"/></td>
        </tr>
        <tr>
          <td align="center"><xsl:apply-templates select="page:content"/></td>
        </tr>
      </table>
    </form>
  </xsl:template>
  
  
  <xsl:template match="page:wizard/page:title|page:dialog/page:title">
    <table align="center" border="0" cellspacing="0" cellpadding="0">
      <xsl:if test="(ancestor::page:wizard[1]/@fill = 'yes') or (ancestor::page:dialog[1]/@fill = 'yes')">
        <xsl:attribute name="width">
          <xsl:text>100%</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/ulcorner.gif"></td>
        <td height="4" background="/admin/images/border/uborder.gif"></td>
        <td width="4" height="4" background="/admin/images/border/urcorner.gif"></td>
      </tr>
      <tr>
        <td width="4" background="/admin/images/border/lborder.gif"></td>
        <td bgcolor="#d7e3ed" style="padding: 5px; font-weight: bold; text-align: center">
          <xsl:apply-templates/>
        </td>
        <td width="4" background="/admin/images/border/rborder.gif"></td>
      </tr>
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/blcorner.gif"></td>
        <td height="4" background="/admin/images/border/bborder.gif"></td>
        <td width="4" height="4" background="/admin/images/border/brcorner.gif"></td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template match="page:divided-box">
    <table align="center" border="0" cellspacing="0" cellpadding="0">
      <xsl:if test="(ancestor::page:wizard[1]/@fill = 'yes') or (ancestor::page:dialog[1]/@fill = 'yes') or @fill='yes'">
        <xsl:attribute name="width">
          <xsl:text>100%</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/ulcorner.gif"></td>
        <td height="4" background="/admin/images/border/uborder.gif"></td>
        <td width="7" height="4" background="/admin/images/border/uvdivider-mixed.gif"></td>
        <td height="4" background="/admin/images/border/uborder-white.gif"></td>
        <td width="4" height="4" background="/admin/images/border/urcorner-white.gif"></td>
      </tr>
      <xsl:apply-templates/>
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/blcorner.gif"></td>
        <td height="4" background="/admin/images/border/bborder.gif"></td>
        <td width="7" height="4" background="/admin/images/border/bvdivider-mixed.gif"></td>
        <td height="4" background="/admin/images/border/bborder-white.gif"></td>
        <td width="4" height="4" background="/admin/images/border/brcorner-white.gif"></td>
      </tr>
    </table>
  </xsl:template>
  
 <xsl:template match="page:box">
    <table border="0" cellspacing="0" cellpadding="0">
      <xsl:if test="(ancestor::page:wizard[1]/@fill = 'yes') or (ancestor::page:dialog[1]/@fill = 'yes') or @fill = 'yes'">
        <xsl:attribute name="width">
          <xsl:text>100%</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/ulcorner-white.gif"></td>
        <td height="4" background="/admin/images/border/uborder-white.gif"></td>
        <td width="4" height="4" background="/admin/images/border/urcorner-white.gif"></td>
      </tr>
      <tr>
        <td width="4" background="/admin/images/border/lborder-white.gif"></td>
        <td bgcolor="#ffffff" style="padding: 5px">
          <xsl:apply-templates/>
        </td>
        <td width="4" background="/admin/images/border/rborder-white.gif"></td>
      </tr>
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/blcorner-white.gif"></td>
        <td height="4" background="/admin/images/border/bborder-white.gif"></td>
        <td width="4" height="4" background="/admin/images/border/brcorner-white.gif"></td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="page:border">
 
     <table border="0" cellspacing="0" cellpadding="0" style="margin-top: 5px; margin-left: 2px; margin-right: 2px; margin-bottom: 5px">
      <xsl:if test="@fill = 'yes'">
        <xsl:attribute name="width">
          <xsl:text>100%</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/ulcorner.gif"></td>
        <td height="4" background="/admin/images/border/uborder.gif"></td>
        <td width="4" height="4" background="/admin/images/border/urcorner.gif"></td>
      </tr>
      <tr>
        <td width="4" background="/admin/images/border/lborder.gif"></td>
        <td bgcolor="#d7e3ed" style="padding: 2px">
 	      <xsl:apply-templates/>
        </td>
        <td width="4" background="/admin/images/border/rborder.gif"></td>
      </tr>
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/blcorner.gif"></td>
        <td height="4" background="/admin/images/border/bborder.gif"></td>
        <td width="4" height="4" background="/admin/images/border/brcorner.gif"></td>
      </tr>
    </table>
 
  </xsl:template>


  <xsl:template match="page:divided-box/page:item">
    <tr>
      <td width="4" background="/admin/images/border/lborder.gif"></td>
      <td bgcolor="#d7e3ed" align="right" valign="top" style="padding: 5px"><xsl:apply-templates select="page:header"/></td>
      <td width="7" background="/admin/images/border/vdivider-mixed.gif"></td>
      <td bgcolor="#ffffff" align="left" valign="top" style="padding: 5px"><xsl:apply-templates select="page:detail"/></td>
      <td width="4" background="/admin/images/border/rborder-white.gif"></td>
    </tr>
  </xsl:template>
  
  <xsl:template match="page:divided-box/page:spanning-text">
    <xsl:if test="preceding-sibling::*[1][self::page:item]">
      <tr>
        <td colspan="5" bgcolor="#a4aab4" height="1"></td>
      </tr>
    </xsl:if>
    <tr>
      <td width="4" background="/admin/images/border/lborder-white.gif"></td>
      <td colspan="3" bgcolor="#ffffff" style="padding: 5px"><xsl:apply-templates/></td>
      <td width="4" background="/admin/images/border/rborder-white.gif"></td>
    </tr>
    <xsl:if test="following-sibling::*">
      <tr>
        <td colspan="5" bgcolor="#a4aab4" height="1"></td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="page:buttons">
    <table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <xsl:for-each select="*">
          <td>
            <xsl:apply-templates select="."/>
          </td>
        </xsl:for-each>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template match="page:back">
    <xsl:call-template name="print-button">
      <xsl:with-param name="name" select="'&lt; Back'"/>
      <xsl:with-param name="button" select="."/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="page:next">
    <xsl:call-template name="print-button">
      <xsl:with-param name="name" select="'Next &gt;'"/>
      <xsl:with-param name="button" select="."/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="page:finish">
    <xsl:call-template name="print-button">
      <xsl:with-param name="name" select="'Finish'"/>
      <xsl:with-param name="button" select="."/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="page:cancel">
    <xsl:call-template name="print-button">
      <xsl:with-param name="name" select="'Cancel'"/>
      <xsl:with-param name="button" select="."/>
      <xsl:with-param name="submit" select="false()"/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="page:button">
    <xsl:call-template name="print-button">
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="icon" select="@icon"/>
      <xsl:with-param name="title" select="@title"/>
      <xsl:with-param name="button" select="."/>
      <xsl:with-param name="submit" select="false()"/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="page:submit-button">
    <xsl:call-template name="print-button">
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="icon" select="@icon"/>
      <xsl:with-param name="title" select="@title"/>
      <xsl:with-param name="button" select="."/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="page:tab-pane">
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
      <tr align="left">
        <td class="tab-bar" width="100%" height="22">
          <table height="22" border="0" cellpadding="0" cellspacing="0">
            <tr height="22">
              <xsl:for-each select="page:tabs/page:tab">
                <xsl:variable name="image-suffix">
                  <xsl:choose>
                    <xsl:when test="@selected = 'yes'">
                      <xsl:text>selected</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>unselected</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                <td height="22">
                  <img src="/admin/images/tab/lhs-{$image-suffix}.gif" alt="" border="0"/>
                </td>
                <td height="22" background="/admin/images/tab/bg-{$image-suffix}.gif">
                  <span class="tab">
                    <xsl:choose>
                      <xsl:when test="@url">
                        <a href="{@url}"><xsl:value-of select="page:name"/></a>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:value-of select="page:name"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </span>
                </td>
                <td height="22">
                  <img src="/admin/images/tab/rhs-{$image-suffix}.gif" alt="" border="0"/>
                </td>
              </xsl:for-each>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td class="tab-pane" width="100%">
          <xsl:apply-templates select="page:content"/>
        </td>
      </tr>
    </table>
  </xsl:template>
  
  <!-- Copy all HTML verbatim. -->
  
  <xsl:template match="html:*|page:hr|page:p|page:ul|page:li|page:a|page:b|page:u|page:i|page:font|page:table|page:tr|page:td|page:form|page:input|page:option|page:select|page:div|page:br|page:script|page:textarea">
    <xsl:element name="{local-name()}">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  
  <!-- List templates -->
  
  <xsl:template match="list:list">
    <div class="list">
      <xsl:for-each select="*">
        <div class="list-item">
          <xsl:apply-templates select="."/>
        </div>
      </xsl:for-each>
    </div>
  </xsl:template>
  
  <!-- User templates -->
  
  <xsl:template match="user:user">
  	<!-- FIXME: It would be kinda nice to put a little user icon here... -->
    <a href="/admin/usermanager/modifyuser.html?username={user:userName}" target="edit"><xsl:value-of select="user:userName"/></a>
  </xsl:template>
  
  <xsl:template match="list:selectable-item[user:user]">
    <input type="checkbox" name="{ancestor::list:list/@name}"
        value="{user:user/user:userName}"/>
    <xsl:apply-templates/>
  </xsl:template>
  
  <!-- Group templates -->
  
  <xsl:template match="group:group">
  	<!-- FIXME: It would be kinda nice to put some sort of group icon here... -->
    <a href="/admin/usermanager/modifygroup.html?groupname={group:name}" target="edit"><xsl:value-of select="group:name"/></a>
  </xsl:template>
  
  <xsl:template match="list:selectable-item[group:group]">
    <input type="checkbox" name="{ancestor::list:list/@name}"
        value="{group:group/group:name}"/>
    <xsl:apply-templates/>
  </xsl:template>
  
  <!-- Tree templates -->
  
  <xsl:template match="tree:tree">
    <script type="text/javascript" lang="JavaScript" src="/admin/tree.js"></script>
    <script type="text/javascript" lang="JavaScript">
      var tree = new Tree(
<xsl:apply-templates select="tree:node"/>,
      <xsl:choose>
        <xsl:when test="@name">"<xsl:value-of select="@name"/>"</xsl:when>
        <xsl:otherwise>null</xsl:otherwise>
      </xsl:choose>
      );
    </script>
    <div class="tree">
      <script type="text/javascript">
        tree.draw();
      </script>
    </div>
  </xsl:template>
  
  <!-- Print out the given number of levels of indentation. -->
  <xsl:template name="print-indent">
    <xsl:param name="indent-level" select="0"/>
    
    <xsl:if test="$indent-level &gt; 0">
      <xsl:text>    </xsl:text>
      <xsl:call-template name="print-indent">
        <xsl:with-param name="indent-level" select="$indent-level - 1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  
  <!--
      Print a string of the form
          new Node( "My Node", "/admin/edit.html?path=whatever&repository=xfs_repository", "/admin/images/tree/page.gif" )
      or
          new Node( "My Node", "/admin/edit.html?path=whatever&repository=xfs_repository", "/admin/images/tree/folder.gif", new Array(
              /* children here */
          ) )
      depending on the type of the node.
  -->
  <xsl:template match="tree:node">
    <xsl:param name="indent-level" select="0"/>
    
    <!-- First, write out enough spaces. -->
    
    <xsl:text>          </xsl:text>
    <xsl:call-template name="print-indent">
      <xsl:with-param name="indent-level" select="$indent-level"/>
    </xsl:call-template>
    
    <!-- Then, write out the beginning of the constructor. -->
    
    <xsl:text>new Node( "</xsl:text>
    <xsl:value-of select="tree:name"/>
    <xsl:text>", </xsl:text>
    <xsl:choose>
      <xsl:when test="@url">
        <xsl:text>"</xsl:text>
        <xsl:value-of select="@url"/>
        <xsl:text>"</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>null</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>, </xsl:text>
    <xsl:choose>
      <xsl:when test="@icon-url">
        <xsl:text>"</xsl:text>
        <xsl:value-of select="@icon-url"/>
        <xsl:text>"</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>null</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    
    <!--
        If there are children, write them out underneath this node.
        Otherwise, if this is an unexpanded node, write out a server expansion
        URL.
    -->

    <xsl:text>, </xsl:text>

    <xsl:choose>
      <xsl:when test="@path">
        <xsl:text>"</xsl:text>
        <xsl:value-of select="@path"/>
        <xsl:text>"</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>null</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    
    <xsl:choose>
      <xsl:when test="tree:children/tree:node">
        <xsl:text>, null, new Array(&#10;</xsl:text>
        <xsl:apply-templates select="tree:children/tree:node">
          <xsl:with-param name="indent-level" select="$indent-level + 1"/>
        </xsl:apply-templates>
        <xsl:text>          </xsl:text>
        <xsl:call-template name="print-indent">
          <xsl:with-param name="indent-level" select="$indent-level"/>
        </xsl:call-template>
        <xsl:text>)</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="not(@leaf = 'true') and not(@has-been-expanded = 'true')">
          <xsl:text>, "</xsl:text><xsl:value-of select="$requestURI"/><xsl:text>?WebTreeName=</xsl:text><xsl:value-of select="ancestor::tree:tree[1]/@name"/><xsl:text>&amp;wsp-action=expandTreeNode&amp;nodeID=</xsl:text>
          <xsl:value-of select="@id"/>
          <xsl:text>"</xsl:text>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
    
    <!-- Finish up the constructor call. -->
    
    <xsl:text> )</xsl:text>
    <xsl:if test="position() != last()">
      <xsl:text>,</xsl:text>
    </xsl:if>
    <xsl:text>&#10;</xsl:text> <!-- newline -->
    
  </xsl:template>
  
  <!--
      Named Templates
  -->
  
  <xsl:template name="print-button">
    <xsl:param name="name" select="''"/>       <!-- name or icon required -->
    <xsl:param name="icon" select="''"/>       <!-- name or icon required -->
    <xsl:param name="title" select="''"/>      <!-- optional -->
    <xsl:param name="button" select="/.."/>    <!-- required -->
    <xsl:param name="submit" select="true()"/> <!-- optional -->
    
    <table border="0" cellspacing="0" cellpadding="0" style="margin-top: 5px; margin-left: 2px; margin-right: 2px; margin-bottom: 5px">
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/ulcorner.gif"></td>
        <td height="4" background="/admin/images/border/uborder.gif"></td>
        <td width="4" height="4" background="/admin/images/border/urcorner.gif"></td>
      </tr>
      <tr>
        <td width="4" background="/admin/images/border/lborder.gif"></td>
        <td bgcolor="#d7e3ed" style="padding: 2px">
          <xsl:choose>
            <xsl:when test="$button/@href">
              <xsl:choose>
                <xsl:when test="$submit">
                  <a href="javascript:{$button/@onclick};gotoPage( '{$button/@href}', '{$button/@target}' );">
                    <xsl:call-template name="print-button-text">
                      <xsl:with-param name="name" select="$name"/>
                      <xsl:with-param name="icon" select="$icon"/>
                      <xsl:with-param name="title" select="$title"/>
                    </xsl:call-template>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:choose>
                    <xsl:when test="$button/@target">
                      <a href="javascript:{$button/@onclick};top.frames['{$button/@target}'].location.href = '{$button/@href}'">
                        <xsl:call-template name="print-button-text">
                          <xsl:with-param name="name" select="$name"/>
                          <xsl:with-param name="icon" select="$icon"/>
                          <xsl:with-param name="title" select="$title"/>
                        </xsl:call-template>
                      </a>
                    </xsl:when>
                    <xsl:otherwise>
                      <a href="javascript:{$button/@onclick};document.location.href = '{$button/@href}'">
                        <xsl:call-template name="print-button-text">
                          <xsl:with-param name="name" select="$name"/>
                          <xsl:with-param name="icon" select="$icon"/>
                          <xsl:with-param name="title" select="$title"/>
                        </xsl:call-template>
                      </a>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
              <xsl:choose>
                <xsl:when test="$button/@onclick">
                  <a href="javascript:{$button/@onclick}">
                    <xsl:call-template name="print-button-text">
                      <xsl:with-param name="name" select="$name"/>
                      <xsl:with-param name="icon" select="$icon"/>
                      <xsl:with-param name="title" select="$title"/>
                    </xsl:call-template>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="print-button-text">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="icon" select="$icon"/>
                    <xsl:with-param name="title" select="$title"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </td>
        <td width="4" background="/admin/images/border/rborder.gif"></td>
      </tr>
      <tr height="4">
        <td width="4" height="4" background="/admin/images/border/blcorner.gif"></td>
        <td height="4" background="/admin/images/border/bborder.gif"></td>
        <td width="4" height="4" background="/admin/images/border/brcorner.gif"></td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template name="print-button-text">
    <xsl:param name="name" select="''"/> <!-- name or icon required -->
    <xsl:param name="icon" select="''"/>
    <xsl:param name="title" select="''"/>
    
    <xsl:if test="$icon">
      <img border="0" src="{$icon}" alt="{$title}" title="{$title}"/>
    </xsl:if>
    <xsl:if test="$icon and $name">
      <xsl:text>&#160;</xsl:text>
    </xsl:if>
    <xsl:if test="$name">
      <xsl:value-of select="$name"/>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
