/*
 * $Id$
 */

package org.ofbiz.core.event;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.minilang.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> SimpleEventHandler.java
 * <p><b>Description:</b> Simple Event Mini-Lang Handler
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    January 8, 2002
 *@version    1.0
 */
public class SimpleEventHandler implements EventHandler {

    private String xmlResource = null;
    private String eventName = null;

    /** Initialize the required parameters
     *@param eventPath The path or location of this event
     *@param eventMethod The method to invoke
     */
    public void initialize(String eventPath, String eventMethod) {
        this.xmlResource = eventPath;
        this.eventName = eventMethod;
    }

    /** Invoke the web event
     *@param request The servlet request object
     *@param response The servlet response object
     *@return String Result code
     *@throws EventHandlerException
     */
    public String invoke(HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        if (xmlResource == null)
            throw new EventHandlerException("XML Resource (eventPath) cannot be null");
        if (eventName == null)
            throw new EventHandlerException("Event Name (eventMethod) cannot be null");

        try {
            return SimpleEvent.runSimpleEvent(xmlResource, eventName, request);
        } catch (MiniLangException e) {
            Debug.logError(e);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not complete event: " + e.getMessage());
            return "error";
        }
    }
}
