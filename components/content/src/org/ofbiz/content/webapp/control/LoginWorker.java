/*
 * $Id$
 *
 * Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.webapp.control;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import javax.transaction.InvalidTransactionException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionFactory;
import org.ofbiz.entity.transaction.TransactionUtil;

/**
 * Common Workers
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.0
 */
public class LoginWorker {
    
    public final static String module = LoginWorker.class.getName();

    public static final String EXTERNAL_LOGIN_KEY_ATTR = "externalLoginKey";

    /** This Map is keyed by the randomly generated externalLoginKey and the value is a UserLogin GenericValue object */
    public static Map externalLoginKeys = new HashMap();
    
    public static String makeLoginUrl(PageContext pageContext) {
        return makeLoginUrl(pageContext, "checkLogin");
    }

    public static String makeLoginUrl(ServletRequest request) {
        return makeLoginUrl(request, "checkLogin");
    }
	
    public static String makeLoginUrl(PageContext pageContext, String requestName) {
        return makeLoginUrl(pageContext.getRequest(), requestName);
    }
    public static String makeLoginUrl(ServletRequest request, String requestName) {
        String queryString = null;

        Enumeration parameterNames = request.getParameterNames();

        while (parameterNames != null && parameterNames.hasMoreElements()) {
            String paramName = (String) parameterNames.nextElement();

            if (paramName != null) {
                if (queryString == null) queryString = paramName + "=" + request.getParameter(paramName);
                else queryString = queryString + "&" + paramName + "=" + request.getParameter(paramName);
            }
        }

        String loginUrl = "/" + requestName + "/" + UtilFormatOut.checkNull((String) request.getAttribute("_CURRENT_VIEW_"));

        if (queryString != null) loginUrl = loginUrl + "?" + UtilFormatOut.checkNull(queryString);

        return loginUrl;
    }
    
    /**
     * Gets (and creates if necessary) a key to be used for an external login parameter
     */
    public static String getExternalLoginKey(HttpServletRequest request) {
        //Debug.logInfo("Running getExternalLoginKey, externalLoginKeys.size=" + externalLoginKeys.size(), module);
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");

        String externalKey = (String) request.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
        if (externalKey != null) return externalKey;

        HttpSession session = request.getSession();
        synchronized (session) {
            // if the session has a previous key in place, remove it from the master list
            String sesExtKey = (String) session.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
            if (sesExtKey != null) {
                externalLoginKeys.remove(sesExtKey);
            }

            //check the userLogin here, after the old session setting is set so that it will always be cleared
            if (userLogin == null) return "";

            //no key made yet for this request, create one
            while (externalKey == null || externalLoginKeys.containsKey(externalKey)) {
                externalKey = "EL" + Long.toString(Math.round(Math.random() * 1000000)) + Long.toString(Math.round(Math.random() * 1000000));
            }

            request.setAttribute(EXTERNAL_LOGIN_KEY_ATTR, externalKey);
            session.setAttribute(EXTERNAL_LOGIN_KEY_ATTR, externalKey);
            externalLoginKeys.put(externalKey, userLogin);
            return externalKey;
        }
    }

    public static void cleanupExternalLoginKey(HttpSession session) {
        String sesExtKey = (String) session.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
        if (sesExtKey != null) {
            externalLoginKeys.remove(sesExtKey);
        }
    }

    public static void setLoggedOut(GenericValue userLogin) {
        // set the logged out flag - need a mutable object first
        userLogin = new GenericValue(userLogin);
        userLogin.set("hasLoggedOut", "Y");

        TransactionManager txMgr = TransactionFactory.getTransactionManager();
        Transaction parentTx = null;
        boolean beganTransaction = false;

        try {
            if (txMgr != null) {
                try {
                    parentTx = txMgr.suspend();
                    beganTransaction = TransactionUtil.begin();
                } catch (SystemException se) {
                    Debug.logError(se, "Cannot suspend transaction: " + se.getMessage(), module);
                } catch (GenericTransactionException e) {
                    Debug.logError(e, "Cannot begin nested transaction: " + e.getMessage(), module);
                }
            }

            try {
                userLogin.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Unable to set logged out flag on UserLogin", module);
            }

            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                Debug.logError(e, "Could not commit nested transaction: " + e.getMessage(), module);
            }
        } finally {
            // resume/restore parent transaction
            if (parentTx != null) {
                try {
                    txMgr.resume(parentTx);
                    Debug.logVerbose("Resumed the parent transaction.", module);
                } catch (InvalidTransactionException ite) {
                    Debug.logError(ite, "Cannot resume transaction: " + ite.getMessage(), module);
                } catch (SystemException se) {
                    Debug.logError(se, "Unexpected transaction error: " + se.getMessage(), module);
                }
            }
        }
    }
}
