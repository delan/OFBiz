/*
 * $Id$
 *
 * Copyright (c) 2001 Sun Microsystems Inc., published in "Advanced Java Server Pages" by Prentice Hall PTR
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.region;

import java.net.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.view.*;

/**
 * A section is content with a name that implements Content.render. 
 * <p>That method renders content either by including
 * it or by printing it directly, depending upon the direct
 * value passed to the Section constructor.</p>
 *
 * <p>Note that a section's content can also be a region;if so,
 * Region.render is called from Section.Render().</p>
 *
 *@author     David M. Geary in the book "Advanced Java Server Pages"
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 26, 2002
 *@version    1.0
 */
public class Section extends Content {
    protected final String name;
    protected final String info;
    protected URL regionFile;
    
    public Section(String name, String info, String content, String type, URL regionFile) {
        super(content, type);
        this.name = name;
        this.info = info;
        this.regionFile = regionFile;
    }
    
    public String getName() {
        return name;
    }
    
    public void render(PageContext pageContext) throws JspException {
        try {
            render((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
        } catch (java.io.IOException e) {
            Debug.logError(e, "Error rendering section: ");
            throw new JspException(e);
        } catch (ServletException e) {
            Throwable throwable = e.getRootCause() != null ? e.getRootCause() : e;
            Debug.logError(throwable, "Error rendering section: ");
            throw new JspException(throwable);
        }
    }
    
    public void render(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException, ServletException {
        boolean verboseOn = Debug.verboseOn();
        if (verboseOn) Debug.logVerbose("Rendering " + this.toString());
        
        //long viewStartTime = System.currentTimeMillis();
        if (content != null) {
            if ("direct".equals(type)) {
                response.getWriter().print(content);
            } else if ("default".equals(type) || "region".equals(type) || "resource".equals(type)) {
                //if type is resource then we won't even look up the region
                
                //if this is default or region, check to see if the content points to a valid region name
                Region region = null;
                if ("default".equals(type) || "region".equals(type)) {
                    region = RegionManager.getRegion(regionFile, content);
                }
                
                if ("region".equals(type) || region != null) {
                    if (region == null) {
                        throw new IllegalArgumentException("No region definition found with name: " + content);
                    }
                    // render the content as a region
                    RegionStack.push(request, region);
                    region.render(request, response);
                    RegionStack.pop(request);
                } else {
                    //default is the string that the ViewFactory expects for webapp resources
                    viewHandlerRender("default", request, response);
                }
            } else {
                viewHandlerRender(type, request, response);
            }
        }
        if (verboseOn) Debug.logVerbose("DONE Rendering " + this.toString());
    }
    
    protected void viewHandlerRender(String typeToUse, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServletContext context = (ServletContext) request.getAttribute("servletContext");
        //see if the type is defined in the controller.xml file
        try {
            if (Debug.verboseOn()) Debug.logVerbose("Rendering view [" + content + "] of type [" + typeToUse + "]");
            ViewHandler vh = ViewFactory.getViewHandler(context, typeToUse);
            vh.render(name, content, info, request, response);
        } catch (ViewHandlerException e) {
            throw new ServletException(e.getNonNestedMessage(), e.getNested());
        }
    }
    
    public String toString() {
        return "Section: " + name + ", info=" + info + ", content=" + content + ", type=" + type;
    }
}
