/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.minilang.method;


import java.net.*;
import java.text.*;
import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;


/**
 * A type of MethodString that represents a String constant value
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    October 4, 2002
 *@version    1.0
 */
public class StringString extends MethodString {
    String value;
    String cdataValue;

    public StringString(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        value = element.getAttribute("value");
        cdataValue = UtilXml.elementValue(element);
    }

    public String getString(MethodContext methodContext) {
        boolean valueExists = UtilValidate.isNotEmpty(value);
        boolean cdataValueExists = UtilValidate.isNotEmpty(cdataValue);
        
        if (valueExists && cdataValueExists) {
            return value + cdataValue;
        } else {
            if (valueExists) {
                return value;
            } else {
                return cdataValue;
            }
        }
    }
}
