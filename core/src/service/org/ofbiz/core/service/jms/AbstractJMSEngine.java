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

import org.ofbiz.core.config.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.config.*;
import org.ofbiz.core.service.engine.*;
import org.ofbiz.core.util.*;

import org.w3c.dom.Element;

/**
 * AbstractJMSEngine
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Jul 16, 2002
 * @version    1.0
 */
public abstract class AbstractJMSEngine implements GenericEngine {

    protected ServiceDispatcher dispatcher;

    public AbstractJMSEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    protected abstract Map run(ModelService modelService, Map context) throws GenericServiceException;

    protected Element getServiceElement(ModelService modelService) throws GenericServiceException {
       Element rootElement = null;
        try {
            rootElement = ServiceConfigUtil.getXmlRootElement();
        } catch (GenericConfigException e) {
            throw new GenericServiceException("Error getting JMS Service element", e);
        }
        Element serviceElement = UtilXml.firstChildElement(rootElement, "jms-service", "name", modelService.location);
        if (serviceElement == null) {
            throw new GenericServiceException("Cannot find an JMS service definition for the name [" + modelService.location + "] in the serviceengine.xml file");
        }
        return serviceElement;
    }

    protected Message makeMessage(Session session, ModelService modelService, Map context)
            throws GenericServiceException, JMSException {
        List outParams = modelService.getParameterNames(ModelService.OUT_PARAM, false);
        if (outParams != null && outParams.size() > 0)
            throw new GenericServiceException("JMS service cannot have required OUT parameters; no parameters will be returned.");
        String xmlContext = null;
        try {
            xmlContext = XmlSerializer.serialize(context);
        } catch (Exception e) {
            throw new GenericServiceException("Cannot serialize context.", e);
        }
        MapMessage message = session.createMapMessage();
        message.setString("serviceName", modelService.invoke);
        message.setString("serviceContext", xmlContext);
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
