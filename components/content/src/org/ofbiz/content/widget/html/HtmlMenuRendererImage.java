/*
 * $Id: HtmlMenuRendererImage.java,v 1.1 2004/03/15 14:53:58 byersa Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.content.widget.html;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.Debug;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.webapp.taglib.ContentUrlTag;
import org.ofbiz.content.widget.menu.MenuStringRenderer;
import org.ofbiz.content.widget.menu.ModelMenu;
import org.ofbiz.content.widget.menu.ModelMenuItem;
import org.ofbiz.content.widget.menu.ModelMenuItem.MenuTarget;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.content.ContentManagementWorker;

/**
 * Widget Library - HTML Menu Renderer implementation
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.1 $
 * @since      2.2
 */

public class HtmlMenuRendererImage extends HtmlMenuRenderer {

    protected HtmlMenuRendererImage() {}

    public HtmlMenuRendererImage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }


/*
    public void renderMenuItem(StringBuffer buffer, Map context, ModelMenuItem menuItem) {
        
        boolean hideThisItem = isHideIfSelected(menuItem);
            Debug.logInfo("in HtmlMenuRendererImage, hideThisItem:" + hideThisItem,"");
        if (hideThisItem)
            return;

        boolean bHasPermission = permissionCheck(menuItem);
        if (!bHasPermission) 
            return;
            Debug.logInfo("in HtmlMenuRendererImage, bHasPermission(2):" + bHasPermission,"");

        String cellWidth = menuItem.getCellWidth();
            Debug.logInfo("in HtmlMenuRendererImage, cellWidth:" + cellWidth,"");
        String widthStr = "";
        if (UtilValidate.isNotEmpty(cellWidth)) 
            widthStr = " width=\"" + cellWidth + "\" ";
            Debug.logInfo("in HtmlMenuRendererImage, widthStr:" + widthStr,"");
        
        String orientation = menuItem.getModelMenu().getOrientation();
        if (orientation.equalsIgnoreCase("vertical"))
            buffer.append("<tr>");
        buffer.append("<td align=left width='" + widthStr + "'>");
        MenuTarget target = menuItem.getCurrentMenuTarget();
            Debug.logInfo("in HtmlMenuRendererImage, target:" + target,"");
        String divStr = buildDivStr(menuItem, context);
            Debug.logInfo("in HtmlMenuRendererImage, divStr:" + divStr,"");
        String url = target.renderAsUrl( context);
            Debug.logInfo("in HtmlMenuRendererImage, url:" + url,"");
        buffer.append("<a href=\""); 
        appendOfbizUrl(buffer,  url);
        String imgStr = buildImgStr(menuItem);
            Debug.logInfo("in HtmlMenuRendererImage, imgStr:" + imgStr,"");
        buffer.append("\">" + imgStr + "</a>");
        buffer.append("</td> ");
        if (orientation.equalsIgnoreCase("vertical"))
            buffer.append("</tr>");
        this.appendWhitespace(buffer);
      
            Debug.logInfo("in HtmlMenuRendererImage, buffer:" + buffer.toString(),"");
        return;
    }
*/

    public String buildDivStr(ModelMenuItem menuItem, Map context) {

        String imgStr = "<img src=\"";
        String contentId = menuItem.getAssociatedContentId(context);
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        GenericValue webSitePublishPoint = null;
            Debug.logInfo("in HtmlMenuRendererImage, contentId:" + contentId,"");
        try {
            webSitePublishPoint = ContentManagementWorker.getWebSitePublishPoint(delegator, contentId);
        } catch(GenericEntityException e) {
            Debug.logInfo("in HtmlMenuRendererImage, GEException:" + e.getMessage(),"");
            throw new RuntimeException(e.getMessage());
        }
        String medallionLogoStr = webSitePublishPoint.getString("medallionLogo");
        StringBuffer buf = new StringBuffer();
        appendContentUrl(buf, medallionLogoStr);
        imgStr += buf.toString();
            Debug.logInfo("in HtmlMenuRendererImage, imgStr:" + imgStr,"");
        String cellWidth = menuItem.getCellWidth();
        imgStr += "\"";
        String widthStr = "";
        if (UtilValidate.isNotEmpty(cellWidth)) 
            widthStr = " width=\"" + cellWidth + "\" ";
        
        imgStr += widthStr;
        imgStr += " border=\"0\" \"/>";
        return imgStr;
    }

/*
    public void renderMenuOpen(StringBuffer buffer, Map context, ModelMenu modelMenu) {

        String menuWidth = modelMenu.getMenuWidth();
        String widthStr = "";
        if (UtilValidate.isNotEmpty(menuWidth)) 
            widthStr = " width=\"" + menuWidth + "\" ";
        
        buffer.append("<table " + widthStr + " border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>");
        this.appendWhitespace(buffer);
    }

    public void renderMenuClose(StringBuffer buffer, Map context, ModelMenu modelMenu) {
        buffer.append("</table> ");
        this.appendWhitespace(buffer);
    }

    public void renderFormatSimpleWrapperOpen(StringBuffer buffer, Map context, ModelMenu modelMenu) {

        this.appendWhitespace(buffer);
    }

    public void renderFormatSimpleWrapperClose(StringBuffer buffer, Map context, ModelMenu modelMenu) {

        this.appendWhitespace(buffer);
    }
*/

}
