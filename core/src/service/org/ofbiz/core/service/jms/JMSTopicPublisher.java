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

import javax.naming.*;
import javax.jms.*;

import org.ofbiz.core.service.*;
import org.ofbiz.core.service.engine.*;
import org.ofbiz.core.util.*;

import org.w3c.dom.Element;

/**
 * JMSTopicPublisher - Publish a message (Pub/Sub) to a topic.
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Jul 18, 2002
 * @version    1.0
 */
public class JMSTopicPublisher extends AbstractJMSEngine {

    public static final String module = JMSTopicPublisher.class.getName();

    private TopicConnection connect = null;
    private TopicSession session = null;
    private TopicPublisher publisher = null;

    public JMSTopicPublisher(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    protected Map run(ModelService modelService, Map context) throws GenericServiceException {
        // get the config
        Element serviceElement = getServiceElement(modelService);
        String broker = serviceElement.getAttribute("broker");
        String userName = serviceElement.getAttribute("username");
        String password = serviceElement.getAttribute("password");
        String topicName = serviceElement.getAttribute("topic-queue");
        String jndiName = serviceElement.getAttribute("jndi-name");

        try {
            InitialContext jndi = JNDIContextFactory.getInitialContext(jndiName);
            TopicConnectionFactory factory = (TopicConnectionFactory) jndi.lookup(broker);
            TopicConnection con = factory.createTopicConnection(userName, password);
            con.setClientID(userName);
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

}
