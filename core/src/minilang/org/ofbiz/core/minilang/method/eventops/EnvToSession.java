/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.minilang.method.eventops;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Copies a map field to a Servlet session attribute
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class EnvToSession extends MethodOperation {
    String envName;
    String sessionName;

    public EnvToSession(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        envName = element.getAttribute("env-name");
        sessionName = element.getAttribute("session-name");

        //set sessionName to their defualt value of envName if empty
        if (sessionName == null || sessionName.length() == 0) {
            sessionName = envName;
        }
    }

    public boolean exec(MethodContext methodContext) {
        //only run this if it is in an EVENT context
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            Object fieldVal = methodContext.getEnv(envName);
            if (fieldVal == null) {
                Debug.logWarning("Env field value not found with name " + envName + ", not copying");
                return true;
            }

            methodContext.getRequest().getSession().setAttribute(sessionName, fieldVal);
        }
        return true;
    }
}
