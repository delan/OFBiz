/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.service.jms;


import java.util.*;
import javax.jms.*;
import javax.naming.*;
import javax.transaction.xa.*;

import org.ofbiz.core.config.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.config.*;
import org.ofbiz.core.service.engine.*;
import org.ofbiz.core.util.*;

import org.w3c.dom.*;


/**
 * AbstractJMSEngine
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Sep 26, 2002
 * @version    1.0
 */
public class JmsServiceEngine implements GenericEngine {

    public static final String module = JmsServiceEngine.class.getName();

    protected ServiceDispatcher dispatcher;

    public JmsServiceEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

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
            if (Debug.verboseOn()) Debug.logVerbose("Serializing Context --> " + context, module);
            xmlContext = XmlSerializer.serialize(context);
        } catch (Exception e) {
            throw new GenericServiceException("Cannot serialize context.", e);
        }
        MapMessage message = session.createMapMessage();

        message.setString("serviceName", modelService.invoke);
        message.setString("serviceContext", xmlContext);
        return message;
    }

    protected List serverList(Element serviceElement) throws GenericServiceException {
        String sendMode = serviceElement.getAttribute("send-mode");
        List serverList = UtilXml.childElementList(serviceElement, "server");

        if (sendMode.equals("none")) {
            return new ArrayList();
        } else if (sendMode.equals("all")) {
            return serverList;
        } else {
            throw new GenericServiceException("Requested send mode not supported.");
        }
    }

    protected Map runTopic(ModelService modelService, Map context, Element server) throws GenericServiceException {
        String serverName = server.getAttribute("jndi-server-name");
        String jndiName = server.getAttribute("jndi-name");
        String topicName = server.getAttribute("topic-queue");
        String userName = server.getAttribute("username");
        String password = server.getAttribute("password");

        try {
            InitialContext jndi = JNDIContextFactory.getInitialContext(serverName);
            TopicConnectionFactory factory = (TopicConnectionFactory) jndi.lookup(jndiName);
            TopicConnection con = factory.createTopicConnection(userName, password);

            //con.setClientID("0123456789ABCDEF");
            con.start();

            TopicSession session = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) jndi.lookup(topicName);
            TopicPublisher publisher = session.createPublisher(topic);

            // create/send the message
            Message message = makeMessage(session, modelService, context);

            publisher.publish(message);
            if (Debug.verboseOn()) Debug.logVerbose("Sent JMS Message to " + topicName, module);

            // close the connections
            publisher.close();
            session.close();
            con.close();

        } catch (GeneralException ge) {
            throw new GenericServiceException("Problems getting JNDI InitialContext.", ge.getNested());
        } catch (NamingException ne) {
            throw new GenericServiceException("JNDI Lookup problems.", ne);
        } catch (JMSException je) {
            throw new GenericServiceException("JMS Internal Error.", je);
        }
        return ServiceUtil.returnSuccess();

    }

    protected Map runQueue(ModelService modelService, Map context, Element server) throws GenericServiceException {
        String serverName = server.getAttribute("jndi-server-name");
        String jndiName = server.getAttribute("jndi-name");
        String queueName = server.getAttribute("topic-queue");
        String userName = server.getAttribute("username");
        String password = server.getAttribute("password");

        try {
            InitialContext jndi = JNDIContextFactory.getInitialContext(serverName);
            QueueConnectionFactory factory = (QueueConnectionFactory) jndi.lookup(jndiName);
            QueueConnection con = factory.createQueueConnection(userName, password);

            con.setClientID(userName);
            con.start();

            QueueSession session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) jndi.lookup(queueName);
            QueueSender sender = session.createSender(queue);

            // create/send the message
            Message message = makeMessage(session, modelService, context);

            sender.send(message);
            if (Debug.verboseOn()) Debug.logVerbose("Sent JMS Message to " + queueName, module);

            // close the connections
            sender.close();
            session.close();
            con.close();

        } catch (GeneralException ge) {
            throw new GenericServiceException("Problems getting JNDI InitialContext.", ge.getNested());
        } catch (NamingException ne) {
            throw new GenericServiceException("JNDI Lookup problems.", ne);
        } catch (JMSException je) {
            throw new GenericServiceException("JMS Internal Error.", je);
        }
        return ServiceUtil.returnSuccess();
    }

    protected Map runXaQueue(ModelService modelService, Map context, Element server) throws GenericServiceException {
        String serverName = server.getAttribute("jndi-server-name");
        String jndiName = server.getAttribute("jndi-name");
        String queueName = server.getAttribute("topic-queue");
        String userName = server.getAttribute("username");
        String password = server.getAttribute("password");

        try {
            InitialContext jndi = JNDIContextFactory.getInitialContext(serverName);
            XAQueueConnectionFactory factory = (XAQueueConnectionFactory) jndi.lookup(jndiName);
            XAQueueConnection con = factory.createXAQueueConnection(userName, password);

            con.setClientID(userName);
            con.start();

            // enlist the XAResource
            XAQueueSession session = con.createXAQueueSession();
            XAResource resource = session.getXAResource();

            if (TransactionUtil.getStatus() == TransactionUtil.STATUS_ACTIVE)
                TransactionUtil.enlistResource(resource);

            Queue queue = (Queue) jndi.lookup(queueName);
            QueueSession qSession = session.getQueueSession();
            QueueSender sender = qSession.createSender(queue);

            // create/send the message
            Message message = makeMessage(session, modelService, context);

            sender.send(message);

            if (TransactionUtil.getStatus() != TransactionUtil.STATUS_ACTIVE)
                session.commit();

            Debug.logInfo("Message sent.", module);

            // close the connections
            sender.close();
            session.close();
            con.close();

        } catch (GenericTransactionException gte) {
            Debug.logError(gte);
            throw new GenericServiceException("Problems enlisting resource w/ transaction manager.", gte.getNested());
        } catch (GeneralException ge) {
            Debug.logError(ge);
            throw new GenericServiceException("Problems getting JNDI InitialContext.", ge.getNested());
        } catch (NamingException ne) {
            Debug.logError(ne);
            throw new GenericServiceException("JNDI Lookup problems.", ne);
        } catch (JMSException je) {
            Debug.logError(je);
            throw new GenericServiceException("JMS Internal Error.", je);
        }
        return ServiceUtil.returnSuccess();
    }

    protected Map run(ModelService modelService, Map context) throws GenericServiceException {
        Element serviceElement = getServiceElement(modelService);
        List serverList = serverList(serviceElement);

        Map result = new HashMap();
        Iterator i = serverList.iterator();

        while (i.hasNext()) {
            Element server = (Element) i.next();
            String serverType = server.getAttribute("type");

            if (serverType.equals("topic"))
                result.putAll(runTopic(modelService, context, server));
            else if (serverType.equals("queue"))
                result.putAll(runQueue(modelService, context, server));
            else
                throw new GenericServiceException("Illegal server messaging type.");
        }
        return result;
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
