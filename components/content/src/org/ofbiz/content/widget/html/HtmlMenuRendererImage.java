/*
 * $Id: HtmlMenuRendererImage.java,v 1.4 2004/04/11 08:28:18 jonesde Exp $
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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.ContentManagementWorker;
import org.ofbiz.content.widget.menu.ModelMenuItem;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Widget Library - HTML Menu Renderer implementation
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.4 $
 * @since      2.2
 */

public class HtmlMenuRendererImage extends HtmlMenuRenderer {

    protected HtmlMenuRendererImage() {}

    public HtmlMenuRendererImage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }


    public String buildDivStr(ModelMenuItem menuItem, Map context) {

        String imgStr = "<img src=\"";
        String contentId = menuItem.getAssociatedContentId(context);
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        GenericValue webSitePublishPoint = null;
                //Debug.logInfo("in HtmlMenuRendererImage, contentId:" + contentId,"");
        try {
            webSitePublishPoint = ContentManagementWorker.getWebSitePublishPoint(delegator, contentId);
        } catch(GenericEntityException e) {
                //Debug.logInfo("in HtmlMenuRendererImage, GEException:" + e.getMessage(),"");
            throw new RuntimeException(e.getMessage());
        }
        String medallionLogoStr = webSitePublishPoint.getString("medallionLogo");
        StringBuffer buf = new StringBuffer();
        appendContentUrl(buf, medallionLogoStr);
        imgStr += buf.toString();
                //Debug.logInfo("in HtmlMenuRendererImage, imgStr:" + imgStr,"");
        String cellWidth = menuItem.getCellWidth();
        imgStr += "\"";
        String widthStr = "";
        if (UtilValidate.isNotEmpty(cellWidth)) 
            widthStr = " width=\"" + cellWidth + "\" ";
        
        imgStr += widthStr;
        imgStr += " border=\"0\" />";
        return imgStr;
    }

}
