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

import javax.naming.*;
import javax.jms.*;
import javax.transaction.xa.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.engine.*;
import org.ofbiz.core.util.*;

import org.w3c.dom.Element;

/**
 * JMSXAQueueSender - Publish a service message
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Jul 16, 2002
 * @version    1.0
 */
public class JMSXAQueueSender extends AbstractJMSEngine {

    private QueueConnection connect = null;
    private QueueSession session = null;
    private QueueSender sender = null;

    public static final String module = JMSXAQueueSender.class.getName();

    public JMSXAQueueSender(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    protected Map run(ModelService modelService, Map context) throws GenericServiceException {
        Element serviceElement = getServiceElement(modelService);
        String broker = serviceElement.getAttribute("broker");
        String userName = serviceElement.getAttribute("userName");
        String password = serviceElement.getAttribute("password");
        String queueName = serviceElement.getAttribute("topic-queue");
        String jndiName = serviceElement.getAttribute("jndi-name");

        try {
            InitialContext jndi = JNDIContextFactory.getInitialContext(jndiName);
            XAQueueConnectionFactory factory = (XAQueueConnectionFactory) jndi.lookup(broker);
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

}
