/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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

import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.minilang.method.*;

/**
 * Adds the fail-message or fail-property value to the error-list.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class AddError extends MethodOperation {
    String message = null;
    String propertyResource = null;
    boolean isProperty = false;

    ContextAccessor errorListAcsr;

    public AddError(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        errorListAcsr = new ContextAccessor(element.getAttribute("error-list-name"), "error_list");

        Element failMessage = UtilXml.firstChildElement(element, "fail-message");
        Element failProperty = UtilXml.firstChildElement(element, "fail-property");

        if (failMessage != null) {
            this.message = failMessage.getAttribute("message");
            this.isProperty = false;
        } else if (failProperty != null) {
            this.propertyResource = failProperty.getAttribute("resource");
            this.message = failProperty.getAttribute("property");
            this.isProperty = true;
        }
    }

    public boolean exec(MethodContext methodContext) {
        boolean hasPermission = false;

        List messages = (List) errorListAcsr.get(methodContext);
        if (messages == null) {
            messages = new LinkedList();
            errorListAcsr.put(methodContext, messages);
        }

        this.addMessage(messages, methodContext.getLoader(), methodContext);
        return true;
    }

    public void addMessage(List messages, ClassLoader loader, MethodContext methodContext) {
        String message = methodContext.expandString(this.message);
        String propertyResource = methodContext.expandString(this.propertyResource);
        
        if (!isProperty && message != null) {
            messages.add(message);
            // if (Debug.infoOn()) Debug.logInfo("[SimpleMapOperation.addMessage] Adding message: " + message);
        } else if (isProperty && propertyResource != null && message != null) {
            String propMsg = UtilProperties.getPropertyValue(UtilURL.fromResource(propertyResource, loader), message);

            if (propMsg == null || propMsg.length() == 0) {
                messages.add("Simple Method error occurred, but no message was found, sorry.");
            } else {
                messages.add(propMsg);
            }
            // if (Debug.infoOn()) Debug.logInfo("[SimpleMapOperation.addMessage] Adding property message: " + propMsg);
        } else {
            messages.add("Simple Method error occurred, but no message was found, sorry.");
            // if (Debug.infoOn()) Debug.logInfo("[SimpleMapOperation.addMessage] ERROR: No message found");
        }
    }
}
