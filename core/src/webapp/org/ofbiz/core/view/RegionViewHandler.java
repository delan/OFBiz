/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 *
 */
package org.ofbiz.core.view;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.region.*;

/**
 * Handles Region type view rendering
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class RegionViewHandler implements ViewHandler {

    protected ServletContext context;
    protected URL regionFile = null;

    public void init(ServletContext context) throws ViewHandlerException {
        this.context = context;

        try {
            regionFile = context.getResource(SiteDefs.REGIONS_CONFIG_LOCATION);
        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException("regions.xml file URL invalid: " + e.getMessage());
        }

        if (regionFile == null) {
            Debug.logWarning("No " + SiteDefs.REGIONS_CONFIG_LOCATION + " file found in this webapp");
        } else {
            Debug.logVerbose("Loading regions from XML file in: " + regionFile);
            RegionManager.getRegions(regionFile);
        }
    }

    public void render(String name, String page, String info, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        // some containers call filters on EVERY request, even forwarded ones,
        // so let it know that it came from the control servlet

        if (request == null) {
            throw new ViewHandlerException("The HttpServletRequest object was null, how did that happen?");
        }
        if (page == null || page.length() == 0) {
            throw new ViewHandlerException("Page name was null or empty, but must be specified");
        }

        request.setAttribute(SiteDefs.FORWARDED_FROM_CONTROL_SERVLET, new Boolean(true));

        Region region = RegionManager.getRegion(regionFile, page);

        if (region == null) {
            throw new ViewHandlerException("Error: could not find region with name " + page);
        }

        try {
            // this render method does not come from a page tag so some setup needs to happen here
            RegionStack.push(request, region);

            region.render(request, response);
        } catch (IOException ie) {
            throw new ViewHandlerException("IO Error in region", ie);
        } catch (ServletException e) {
            Throwable throwable = e.getRootCause() != null ? e.getRootCause() : e;

            if (throwable instanceof JspException) {
                JspException jspe = (JspException) throwable;

                throwable = jspe.getRootCause() != null ? jspe.getRootCause() : jspe;
            }
            Debug.logError(throwable, "ServletException rendering JSP view");
            throw new ViewHandlerException(e.getMessage(), throwable);
        }
        RegionStack.pop(request);
    }
}
