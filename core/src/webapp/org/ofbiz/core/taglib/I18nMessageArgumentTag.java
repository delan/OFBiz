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

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * I18nMessageArgumentTag - JSP tag that used inside a MessageTag to create an
 * ordered list of arguments to use with java.text.MessageFormat.
 *
 * @author  <a href="mailto:k3ysss@yahoo.com">Jian He</a>
 * @version 1.0
 * @created April 16, 2002
 */
public class I18nMessageArgumentTag extends TagSupport {
    public void setValue (Object argumentValue) throws JspException {
        try {
            I18nMessageTag messageTag = (I18nMessageTag) this.getParent();
            messageTag.addArgument (argumentValue);
        } catch (Exception e) {
            throw new JspException (e.getMessage());
        }
    }
}
