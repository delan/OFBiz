/*
 * $Id$
 *
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

package org.ofbiz.core.taglib;


import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;


/**
 * IteratorHasNextTag - Conditional Tag.
 *
 * @author     <a href='mailto:jonesde@ofbiz.org'>David E. Jones</a>
 * @version    1.0
 * @created    August 4, 2001
 */
public class IteratorHasNextTag extends BodyTagSupport {

    public int doStartTag() throws JspTagException {
        IteratorTag iteratorTag =
            (IteratorTag) findAncestorWithClass(this, IteratorTag.class);

        if (iteratorTag == null)
            throw new JspTagException("IterateNextTag not inside IteratorTag.");

        Iterator iterator = iteratorTag.getIterator();

        if (iterator == null || !iterator.hasNext())
            return SKIP_BODY;

        return EVAL_BODY_AGAIN;
    }

    public int doAfterBody() {
        return SKIP_BODY;
    }

    public int doEndTag() {
        try {
            BodyContent body = getBodyContent();

            if (body != null) {
                JspWriter out = body.getEnclosingWriter();
                String bodyString = body.getString();
                body.clearBody();
                out.print(bodyString);
            }
        } catch (IOException e) {
            System.out.println("IterateNext Tag error: " + e);
        }
        return EVAL_PAGE;
    }
}

