/*
 * $Id: Output.java,v 1.2 2004/08/15 21:26:41 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.pos.component;

import net.xoetrope.swing.XEdit;
import net.xoetrope.xui.XPage;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class Output {

    public static final String module = Output.class.getName();
    public static final String TOTALD = "Total Due: ";
    public static final String CHANGE = "Change Due: ";
    public static final String ULOGIN = "Enter User ID:";
    public static final String UPASSW = "Enter Password:";
    public static final String CREDNO = "Enter Card Number:";
    public static final String CREDEX = "Enter Expiration Date (MMYY):";
    public static final String CREDCF = "Enter Last 4 Digits:";
    public static final String CREDZP = "Enter Billing ZipCode:";
    public static final String ISOPEN = "Register Is Open.";

    protected XEdit output = null;

    public Output(XPage page) {
        this.output = (XEdit) page.findComponent("pos_output");
        this.output.setFocusable(false);
        this.clear();
    }

    public void setLock(boolean lock) {
        if (lock) {
            this.print(ULOGIN);
        } else {
            this.print(ISOPEN);
        }
    }

    public void print(String message) {
        this.output.setText(message);
    }

    public void clear() {
        output.setText("");
    }
}
