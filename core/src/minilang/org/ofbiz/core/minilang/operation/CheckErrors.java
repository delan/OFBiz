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

package org.ofbiz.core.minilang.operation;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;

import org.ofbiz.core.minilang.*;

/**
 * An event operation that checks a message list and may introduce a return code and stop the event
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class CheckErrors extends EventOperation {
    String errorListName;
    String errorCode;

    FlexibleMessage errorPrefix;
    FlexibleMessage errorSuffix;
    FlexibleMessage messagePrefix;
    FlexibleMessage messageSuffix;

    public CheckErrors(Element element, SimpleEvent simpleEvent) {
        super(element, simpleEvent);
        errorCode = element.getAttribute("error-code");
        if (errorCode == null || errorCode.length() == 0)
            errorCode = "error";
        errorListName = element.getAttribute("error-list-name");
        if (errorListName == null || errorListName.length() == 0)
            errorListName = "_error_list_";

        errorPrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "error-prefix"), "check.error.prefix");
        errorSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "error-suffix"), "check.error.suffix");
        messagePrefix = new FlexibleMessage(UtilXml.firstChildElement(element, "message-prefix"), "check.message.prefix");
        messageSuffix = new FlexibleMessage(UtilXml.firstChildElement(element, "message-suffix"), "check.message.suffix");
    }

    public boolean exec(Map env, HttpServletRequest request, ClassLoader loader) {
        List messages = (List) env.get(errorListName);
        if (messages != null && messages.size() > 0) {
            String errMsg = errorPrefix.getMessage(loader) +
                    ServiceUtil.makeMessageList(messages, messagePrefix.getMessage(loader), messageSuffix.getMessage(loader)) +
                    errorSuffix.getMessage(loader);
            env.put(simpleEvent.errorMessageName, errMsg);

            env.put(simpleEvent.responseCodeName, errorCode);
            return false;
        }

        return true;
    }
}
