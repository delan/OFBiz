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

package org.ofbiz.core.minilang.method.callops;


import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;
import org.ofbiz.core.minilang.operation.*;


/**
 * An operation that calls a simple method in the same, or from another, file
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class CallSimpleMethod extends MethodOperation {
    
    public static final String module = CallSimpleMethod.class.getName();
    
    String xmlResource;
    String methodName;

    public CallSimpleMethod(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.xmlResource = element.getAttribute("xml-resource");
        this.methodName = element.getAttribute("method-name");
    }

    public boolean exec(MethodContext methodContext) {
        if (xmlResource != null && xmlResource.length() > 0 &&
            methodName != null && methodName.length() > 0) {

            Map simpleMethods = null;

            try {
                simpleMethods = SimpleMethod.getSimpleMethods(xmlResource, methodName, methodContext.getLoader());
            } catch (MiniLangException e) {
                Debug.logError(e);
                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [error getting methods from resource: " + e.getMessage() + "]";

                if (methodContext.getMethodType() == MethodContext.EVENT) {
                    methodContext.putEnv(simpleMethod.getEventErrorMessageName(), errMsg);
                    methodContext.putEnv(simpleMethod.getEventResponseCodeName(), simpleMethod.getDefaultErrorCode());
                } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                    methodContext.putEnv(simpleMethod.getServiceErrorMessageName(), errMsg);
                    methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), simpleMethod.getDefaultErrorCode());
                }
                return false;
            }

            SimpleMethod simpleMethodToCall = (SimpleMethod) simpleMethods.get(methodName);

            if (simpleMethodToCall == null) {
                String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process, could not find SimpleMethod " + methodName + " in XML document in resource: " + xmlResource;

                if (methodContext.getMethodType() == MethodContext.EVENT) {
                    methodContext.putEnv(simpleMethod.getEventErrorMessageName(), errMsg);
                    methodContext.putEnv(simpleMethod.getEventResponseCodeName(), simpleMethod.getDefaultErrorCode());
                } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                    methodContext.putEnv(simpleMethod.getServiceErrorMessageName(), errMsg);
                    methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), simpleMethod.getDefaultErrorCode());
                }
                return false;
            }
            String returnVal = simpleMethodToCall.exec(methodContext);

            if (returnVal != null && returnVal.equals(simpleMethodToCall.getDefaultErrorCode())) {
                // in this case just set the error code, the error messages will already be in place...
                if (methodContext.getMethodType() == MethodContext.EVENT) {
                    methodContext.putEnv(simpleMethod.getEventResponseCodeName(), simpleMethod.getDefaultErrorCode());
                } else if (methodContext.getMethodType() == MethodContext.SERVICE) {
                    methodContext.putEnv(simpleMethod.getServiceResponseMessageName(), simpleMethod.getDefaultErrorCode());
                }
                return false;
            }
        }

        return true;
    }
}
