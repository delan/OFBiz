/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project and repected authors.
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

package org.ofbiz.core.service.jms;

import java.util.*;
import javax.jms.*;

import org.ofbiz.core.service.engine.*;
import org.ofbiz.core.service.*;

/**
 * JMSAbstractEngine
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Jul 16, 2002
 * @version    1.0
 */
public abstract class JMSAbstractEngine implements GenericEngine {

    protected ServiceDispatcher dispatcher;

    public JMSAbstractEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    protected abstract Map run(ModelService modelService, Map context) throws GenericServiceException;

    protected Message makeMessage(Session session, Map context) throws JMSException {
        Message message = session.createTextMessage("Test");
        return message;
    }

    /**
     * Run the service synchronously and return the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @return Map of name, value pairs composing the result.
     * @throws GenericServiceException
     */
    public Map runSync(ModelService modelService, Map context) throws GenericServiceException {
        return run(modelService, context);
    }

    /**
     * Run the service synchronously and IGNORE the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @throws GenericServiceException
     */
    public void runSyncIgnore(ModelService modelService, Map context) throws GenericServiceException {
        run(modelService, context);
    }

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param requester Object implementing GenericRequester interface which will receive the result.
     * @param persist True for store/run; False for run - Ignored.
     * @throws GenericServiceException
     */
    public void runAsync(ModelService modelService, Map context, GenericRequester requester, boolean persist)
            throws GenericServiceException {
        Map result = run(modelService, context);
        requester.receiveResult(result);
    }

    /**
     * Run the service asynchronously and IGNORE the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param persist True for store/run; False for run - Ignored.
     * @throws GenericServiceException
     */
    public void runAsync(ModelService modelService, Map context, boolean persist) throws GenericServiceException {
        run(modelService, context);
    }

    /**
     * Set the name of the local dispatcher - Ignored.
     * @param loader name of the local dispatcher.
     */
    public void setLoader(String loader) {
        return;
    }
}
