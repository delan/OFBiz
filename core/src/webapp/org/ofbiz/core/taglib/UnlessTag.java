/*
 * $Id$
 */

package org.ofbiz.core.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * <p><b>Title:</b> UnlessTag.java
 * <p><b>Description:</b> Custom JSP tag to test page context attributes.
 * <p>Copyright (c) 2002 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @version    1.0
 * @created    August 31, 2001
 */
public class UnlessTag extends IfTag {

    public int doStartTag() throws JspTagException {
        //do the opposite of what IfTag does. EVAL_BODY_AGAIN -> SKIP_BODY, vice-versa
        return (EVAL_BODY_AGAIN + SKIP_BODY) - super.doStartTag();
    }
}

