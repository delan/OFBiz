/*
 * $Id: RenderWrappedTextTransform.java,v 1.9 2004/04/30 23:08:27 ajzeneski Exp $
 *
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.webapp.ftl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

import freemarker.core.Environment;
import freemarker.template.TemplateTransformModel;

/**
 * RenderWrappedTextTransform - Freemarker Transform for URLs (links)
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.9 $
 * @since      3.0
 */
public class RenderWrappedTextTransform implements  TemplateTransformModel {

    public static final String module = RenderWrappedTextTransform.class.getName();
   
    public Writer getWriter(final Writer out, Map args) {                      
        final Environment env = Environment.getCurrentEnvironment();
        Map ctx = (Map)FreeMarkerWorker.getWrappedObject("context", env);
        final String wrappedFTL = FreeMarkerWorker.getArg(args, "wrappedFTL", ctx);

        return new Writer(out) {

            public void write(char cbuf[], int off, int len) {
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {  
                if (UtilValidate.isNotEmpty(wrappedFTL)) {
                        out.write(wrappedFTL);
                } else {
                    Debug.logInfo("wrappedFTL was empty. skipping write.", module);
                }
            }
        };
    }
}
