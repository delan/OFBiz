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
package org.ofbiz.core.ftl;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.control.*;
import org.ofbiz.core.util.*;

import freemarker.ext.beans.BeanModel;
import freemarker.template.Environment;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;

/**
 * OfbizUrlTransform - Freemarker Transform for URLs (links)
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class OfbizUrlTransform implements TemplateTransformModel {
    
    public Writer getWriter(final Writer out, Map args) {              
        final StringBuffer buf = new StringBuffer();
        return new Writer(out) {
            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {  
                try {                              
                    Environment env = Environment.getCurrentEnvironment();
                    BeanModel req = (BeanModel)env.getVariable("requestBean");
                    BeanModel res = (BeanModel) env.getVariable("responseBean");
                    HttpServletRequest request = (HttpServletRequest) req.getWrappedObject();
                    HttpServletResponse response = (HttpServletResponse) res.getWrappedObject();
                    ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
                    RequestHandler rh = (RequestHandler) ctx.getAttribute(SiteDefs.REQUEST_HANDLER);                                        
                    out.write(rh.makeLink(request, response, buf.toString()));
                } catch (TemplateModelException e) {
                    throw new IOException(e.getMessage());
                }
            }
        };
    }
}
