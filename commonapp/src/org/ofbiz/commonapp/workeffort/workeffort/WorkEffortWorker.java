/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.commonapp.workeffort.workeffort;

import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.UtilMisc;

/**
 * WorkEffortWorker - Worker class to reduce code in JSPs & make it more reusable
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class WorkEffortWorker {
    public static void getMonthWorkEffortEvents(PageContext pageContext, String attributeName) {}

    public static void getActivityContext(PageContext pageContext, String workEffortId) {
        getActivityContext(pageContext, workEffortId, "activityContext");
    }

    public static void getActivityContext(PageContext pageContext, String workEffortId, String attribute) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) pageContext.getRequest().getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);
        Map svcCtx = UtilMisc.toMap("workEffortId", workEffortId, "userLogin", userLogin);
        Map result = null;

        try {
            result = dispatcher.runSync("wfGetActivityContext", svcCtx);
        } catch (GenericServiceException e) {
            Debug.logError(e);
        }
        if (result != null && result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))
            Debug.logError((String) result.get(ModelService.ERROR_MESSAGE));
        if (result != null && result.containsKey("activityContext")) {
            Map aC = (Map) result.get("activityContext");

            pageContext.setAttribute(attribute, aC);
        }
    }
}
