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

package org.ofbiz.core.minilang.method;


import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.ofbiz.core.minilang.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;


/**
 * A single operation, does the specified operation on the given field
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@created    February 15, 2002
 *@version    1.0
 */
public class MethodContext {
    public static final int EVENT = 1;
    public static final int SERVICE = 2;

    int methodType;

    Map env = new HashMap();
    Map parameters;
    ClassLoader loader;
    LocalDispatcher dispatcher;
    GenericDelegator delegator;
    Security security;
    GenericValue userLogin;

    HttpServletRequest request = null;
    HttpServletResponse response = null;

    Map results = null;
    DispatchContext ctx;

    public MethodContext(HttpServletRequest request, HttpServletResponse response, ClassLoader loader) {
        this.methodType = MethodContext.EVENT;
        this.parameters = UtilMisc.getParameterMap(request);
        this.loader = loader;
        this.request = request;
        this.response = response;
        this.dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        this.delegator = (GenericDelegator) request.getAttribute("delegator");
        this.security = (Security) request.getAttribute("security");
        this.userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);

        if (this.loader == null)
            this.loader = Thread.currentThread().getContextClassLoader();
    }

    public MethodContext(DispatchContext ctx, Map context, ClassLoader loader) {
        this.methodType = MethodContext.SERVICE;
        this.parameters = context;
        this.loader = loader;
        this.dispatcher = ctx.getDispatcher();
        this.delegator = ctx.getDelegator();
        this.security = ctx.getSecurity();
        this.results = new HashMap();
        this.userLogin = (GenericValue) this.getParameter("userLogin");

        if (this.loader == null)
            this.loader = Thread.currentThread().getContextClassLoader();
    }
    
    public void setErrorReturn(String errMsg, SimpleMethod simpleMethod) {
        if (getMethodType() == MethodContext.EVENT) {
            putEnv(simpleMethod.getEventErrorMessageName(), errMsg);
            putEnv(simpleMethod.getEventResponseCodeName(), simpleMethod.getDefaultErrorCode());
        } else if (getMethodType() == MethodContext.SERVICE) {
            putEnv(simpleMethod.getServiceErrorMessageName(), errMsg);
            putEnv(simpleMethod.getServiceResponseMessageName(), simpleMethod.getDefaultErrorCode());
        }
    }

    public int getMethodType() {
        return this.methodType;
    }

    public Object getEnv(String key) {
        return this.env.get(key);
    }

    public void putEnv(String key, Object value) {
        this.env.put(key, value);
    }

    public void putAllEnv(Map values) {
        this.env.putAll(values);
    }

    public Object removeEnv(String key) {
        return this.env.remove(key);
    }

    public Iterator getEnvEntryIterator() {
        return this.env.entrySet().iterator();
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }

    public void putParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public Map getParameters() {
        return this.parameters;
    }

    public ClassLoader getLoader() {
        return this.loader;
    }

    public LocalDispatcher getDispatcher() {
        return this.dispatcher;
    }

    public GenericDelegator getDelegator() {
        return this.delegator;
    }

    public Security getSecurity() {
        return this.security;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public GenericValue getUserLogin() {
        return this.userLogin;
    }

    public void setUserLogin(GenericValue userLogin) {
        this.userLogin = userLogin;
    }

    public Object getResult(String key) {
        return this.results.get(key);
    }

    public void putResult(String key, Object value) {
        this.results.put(key, value);
    }

    public Map getResults() {
        return this.results;
    }
}
