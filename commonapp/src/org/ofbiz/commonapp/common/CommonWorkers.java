/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.commonapp.common;

import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Common Workers
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    January 06, 2002
 *@version    1.0
 */
public class CommonWorkers {

    public static String makeLoginUrl(PageContext pageContext) {
        return makeLoginUrl(pageContext, "checkLogin");
    }

    public static String makeLoginUrl(PageContext pageContext, String requestName) {
        String queryString = null;
        ServletRequest request = pageContext.getRequest();
        
        Enumeration parameterNames = request.getParameterNames();
        while(parameterNames != null && parameterNames.hasMoreElements()) {
            String paramName = (String)parameterNames.nextElement();
            if(paramName != null) {
                if(queryString == null) queryString = paramName + "=" + request.getParameter(paramName);
                else queryString = queryString + "&" + paramName + "=" + request.getParameter(paramName);
            }
        }
        
        String loginUrl = "/" + requestName + "/" + UtilFormatOut.checkNull((String)request.getAttribute(SiteDefs.CURRENT_VIEW));
        if(queryString != null) loginUrl = loginUrl  + "?" + UtilFormatOut.checkNull(queryString);
        
        return loginUrl;
    }
}
