/*
 * $Id$
 */

package org.ofbiz.core.event;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.scheduler.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b>CoreEvents
 * <p><b>Description:</b> WebApp Events Related To CORE components
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    January 8, 2002
 *@version    1.0
 */

public class CoreEvents {
    
    public static String changeDelegator(HttpServletRequest request, HttpServletResponse response) {
        ServletContext application = request.getSession().getServletContext();
        String delegatorName = request.getParameter("delegator");
        if ( delegatorName == null ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"<li>Required parameter 'delegator' not passed.");
            return "error";
        }
        
        GenericDelegator delegator = GenericDelegator.getGenericDelegator(delegatorName);
        if ( delegator == null ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"<li>Invalid delegator.");
            return "error";
        }
        application.setAttribute("delegator",delegator);
        return "success";
    }
    
}


