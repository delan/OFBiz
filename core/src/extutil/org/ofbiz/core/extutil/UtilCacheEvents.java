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

package org.ofbiz.core.extutil;


import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.security.*;

/**
 * Contains events for the UtilCache class; must be external to access security resources
 * 
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    May 16, 2001
 *@version    1.0
 */
public class UtilCacheEvents {

    /** An HTTP WebEvent handler the specified element from the specified cache
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return
     */
    public static String removeElementEvent(HttpServletRequest request, HttpServletResponse response) {
        Security security = (Security) request.getAttribute("security");
        if (!security.hasPermission("UTIL_CACHE_EDIT", request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have permission to perform this operation, UTIL_CACHE_EDIT required.");
            return "error";
        }

        String name = request.getParameter("UTIL_CACHE_NAME");
        if (name == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not remove cache line/element, no cache name specified.");
            return "error";
        }
        String numString = request.getParameter("UTIL_CACHE_ELEMENT_NUMBER");

        if (numString == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not remove cache line/element, no element number specified.");
            return "error";
        }
        int number;

        try {
            number = Integer.parseInt(numString);
        } catch (Exception e) {
            return "error";
        }

        UtilCache utilCache = (UtilCache) UtilCache.utilCacheTable.get(name);

        if (utilCache != null) {
            Object key = null;

            if (utilCache.getMaxSize() > 0) {
                try {
                    key = utilCache.keyLRUList.get(number);
                } catch (Exception e) {}
            } else {
                // no LRU, try looping through the keySet to see if we find the specified index...
                Iterator ksIter = utilCache.cacheLineTable.keySet().iterator();
                int curNum = 0;

                while (ksIter.hasNext()) {
                    if (number == curNum) {
                        key = ksIter.next();
                        break;
                    } else {
                        ksIter.next();
                    }
                    curNum++;
                }
            }

            if (key != null) {
                utilCache.remove(key);
                request.setAttribute(SiteDefs.EVENT_MESSAGE, "Removed element from cache with key: " + key.toString());
            } else {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not remove cache element, element not found with cache name: " + name + ", element number: " + numString);
                return "error";
            }
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not remove cache element, cache not found with name: " + name);
            return "error";
        }
        return "success";
    }

    /** An HTTP WebEvent handler that clears the named cache
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return
     */
    public static String clearEvent(HttpServletRequest request, HttpServletResponse response) {
        Security security = (Security) request.getAttribute("security");
        if (!security.hasPermission("UTIL_CACHE_EDIT", request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have permission to perform this operation, UTIL_CACHE_EDIT required.");
            return "error";
        }

        String name = request.getParameter("UTIL_CACHE_NAME");

        if (name == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not clear cache, no name specified.");
            return "error";
        }
        UtilCache utilCache = (UtilCache) UtilCache.utilCacheTable.get(name);

        if (utilCache != null) {
            utilCache.clear();
            request.setAttribute(SiteDefs.EVENT_MESSAGE, "Cleared cache with name: " + name);
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not clear cache, cache not found with name: " + name);
            return "error";
        }
        return "success";
    }

    /** An HTTP WebEvent handler that clears all caches
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return
     */
    public static String clearAllEvent(HttpServletRequest request, HttpServletResponse response) {
        Security security = (Security) request.getAttribute("security");
        if (!security.hasPermission("UTIL_CACHE_EDIT", request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have permission to perform this operation, UTIL_CACHE_EDIT required.");
            return "error";
        }

        UtilCache.clearAllCaches();
        request.setAttribute(SiteDefs.EVENT_MESSAGE, "Cleared all caches.");
        return "success";
    }

    /** An HTTP WebEvent handler that clears all caches
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return
     */
    public static String clearAllExpiredEvent(HttpServletRequest request, HttpServletResponse response) {
        Security security = (Security) request.getAttribute("security");
        if (!security.hasPermission("UTIL_CACHE_EDIT", request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have permission to perform this operation, UTIL_CACHE_EDIT required.");
            return "error";
        }

        UtilCache.clearExpiredFromAllCaches();
        request.setAttribute(SiteDefs.EVENT_MESSAGE, "Cleared all expried elements from all caches.");
        return "success";
    }

    /** An HTTP WebEvent handler that updates the named cache
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return
     */
    public static String updateEvent(HttpServletRequest request, HttpServletResponse response) {
        Security security = (Security) request.getAttribute("security");
        if (!security.hasPermission("UTIL_CACHE_EDIT", request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have permission to perform this operation, UTIL_CACHE_EDIT required.");
            return "error";
        }

        String name = request.getParameter("UTIL_CACHE_NAME");

        if (name == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not update cache settings, no name specified");
            return "error";
        }
        String maxSizeStr = request.getParameter("UTIL_CACHE_MAX_SIZE");
        String expireTimeStr = request.getParameter("UTIL_CACHE_EXPIRE_TIME");
        String useSoftReferenceStr = request.getParameter("UTIL_CACHE_USE_SOFT_REFERENCE");

        Long maxSize = null, expireTime = null;

        try {
            maxSize = Long.valueOf(maxSizeStr);
        } catch (Exception e) {}
        try {
            expireTime = Long.valueOf(expireTimeStr);
        } catch (Exception e) {}

        UtilCache utilCache = (UtilCache) UtilCache.utilCacheTable.get(name);

        if (utilCache != null) {
            if (maxSize != null)
                utilCache.setMaxSize(maxSize.longValue());
            if (expireTime != null)
                utilCache.setExpireTime(expireTime.longValue());
            if (useSoftReferenceStr != null) {
                utilCache.setUseSoftReference("true".equals(useSoftReferenceStr));
            }
        }
        return "success";
    }
}
