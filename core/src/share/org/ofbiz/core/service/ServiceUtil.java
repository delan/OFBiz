/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.util.*;
import javax.servlet.http.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Service Utility Class
 * <p><b>Description:</b> None
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
 *@created    October 20, 2001
 *@version    1.0
 */
public class ServiceUtil {
    public static void getHtmlMessages(HttpServletRequest request, Map result) {
        String errorMessage = ServiceUtil.makeHtmlErrorMessage(result);
        if (errorMessage != null && errorMessage.length() > 0)
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errorMessage);
            
        String successMessage = ServiceUtil.makeHtmlSuccessMessage(result);
        if (successMessage != null && successMessage.length() > 0)
            request.setAttribute(SiteDefs.EVENT_MESSAGE, successMessage);
    }
    
    public static String makeHtmlErrorMessage(Map result) {
        String errorMsg = (String) result.get(ModelService.ERROR_MESSAGE);
        List errorMsgList = (List) result.get(ModelService.ERROR_MESSAGE_LIST);
        StringBuffer outMsg = new StringBuffer();
        
        outMsg.append(makeHtmlMessageList(errorMsgList));
        
        if (errorMsg != null) {
            outMsg.append("<li>");
            outMsg.append(errorMsg);
            outMsg.append("</li>");
        }
        
        if (outMsg.length() > 0) {
            return "<b>The following errors occured:</b><br><ul>" + outMsg + "</ul>";
        } else {
            return null;
        }
    }

    public static String makeHtmlSuccessMessage(Map result) {
        String successMsg = (String) result.get(ModelService.SUCCESS_MESSAGE);
        List successMsgList = (List) result.get(ModelService.SUCCESS_MESSAGE_LIST);
        StringBuffer outMsg = new StringBuffer();
        
        outMsg.append(makeHtmlMessageList(successMsgList));
        
        if (successMsg != null) {
            outMsg.append("<li>");
            outMsg.append(successMsg);
            outMsg.append("</li>");
        }
        
        if (outMsg.length() > 0) {
            return "<b>The following occured:</b><br><ul>" + outMsg + "</ul>";
        } else {
            return null;
        }
    }

    public static String makeHtmlMessageList(List msgList) {
        StringBuffer outMsg = new StringBuffer();
        if (msgList != null && msgList.size() > 0) {
            Iterator iter = msgList.iterator();
            while (iter.hasNext()) {
                String curMsg = (String) iter.next();
                outMsg.append("<li>");
                outMsg.append(curMsg);
                outMsg.append("</li>");
            }
        }
        return outMsg.toString();
    }
}
