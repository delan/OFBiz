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
package org.ofbiz.core.taglib;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.ofbiz.core.control.*;
import org.ofbiz.core.util.*;

/**
 * UrlTag - Creates a URL string prepending the current control path.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class UrlTag extends BodyTagSupport {

    public static final String module = UrlTag.class.getName();

    public int doEndTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
       
        ServletContext context = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) context.getAttribute(SiteDefs.REQUEST_HANDLER);        

        BodyContent body = getBodyContent();

        String baseURL = body.getString();
        String newURL = rh.makeLink(request, response, baseURL);        
     
        body.clearBody();

        try {            
            getPreviousOut().print(newURL);
        } catch (IOException e) {
            if (UtilJ2eeCompat.useNestedJspException(pageContext.getServletContext())) {
                throw new JspException(e.getMessage(), e);
            } else {
                Debug.logError(e, "Server does not support nested exceptions, here is the exception", module);
                throw new JspException(e.toString());
            }
        }
        return SKIP_BODY;
    }
}
