/*
 * $Id$
 */

package org.ofbiz.core.event;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.axis.*;
import org.apache.axis.message.*;
import org.apache.axis.server.*;
import org.apache.log4j.Category;
import org.w3c.dom.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> SOAPEventHandler.java
 * <p><b>Description:</b> SOAP Event Handler implementation
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    December 7, 2001
 *@version    1.0
 */
public class SOAPEventHandler implements EventHandler {

    static Category category = Category.getInstance(SOAPEventHandler.class.getName());

    private String eventPath = null;
    private String eventMethod = null;

    /** Initialize the required parameters
     *@param eventPath The path or location of this event
     *@param eventMethod The method to invoke
     */
    public void initialize(String eventPath, String eventMethod) {
        this.eventPath = null;
        this.eventMethod = null;
    }

    /** Invoke the web event
     *@param request The servlet request object
     *@param response The servlet response object
     *@return String Result code
     *@throws EventHandlerException
     */
    public String invoke(HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        AxisServer axisServer;
        try {
            axisServer = AxisServer.getServer(UtilMisc.toMap("name", "OFBiz/Axis Server", "provider", null));
        } catch (AxisFault e) {
            sendError(response, e);
            throw new EventHandlerException("Problems with the AXIS server", e);
        }
        MessageContext mctx = new MessageContext(axisServer);

        // get the SOAP message
        Message msg = null;
        try {
            msg = new Message(request.getInputStream(), false,
                              request.getHeader("Content-Type"), request.getHeader("Content-Location"));
        } catch (IOException ioe) {
            throw new EventHandlerException("Cannot read the input stream", ioe);
        }

        if (msg == null) {
            sendError(response, "No message");
            throw new EventHandlerException("SOAP Message is null");
        }

        mctx.setRequestMessage(msg);

        // new envelopes
        SOAPEnvelope resEnv = new SOAPEnvelope();
        SOAPEnvelope reqEnv = null;

        // get the service name and parameters
        try {
            reqEnv = msg.getSOAPPart().getAsSOAPEnvelope();
        } catch (AxisFault e) {
            sendError(response, e);
            throw new EventHandlerException("Cannot get the envelope", e);
        }
        Vector bodies = null;
        try {
            bodies = reqEnv.getBodyElements();
        } catch (AxisFault e) {
            sendError(response, e);
            throw new EventHandlerException(e.getMessage(), e);
        }

        Debug.logInfo("[EventHandler] : Processing SOAP event");

        // each is a different service call
        Iterator i = bodies.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof RPCElement) {
                RPCElement body = (RPCElement) o;
                String serviceName = body.getMethodName();
                Vector params = null;
                try {
                    params = body.getParams();
                } catch (Exception e) {
                    sendError(response, e);
                    throw new EventHandlerException(e.getMessage(), e);
                }
                Map serviceContext = new HashMap();
                Iterator p = params.iterator();
                while (p.hasNext()) {
                    RPCParam param = (RPCParam) p.next();
                    Debug.logInfo("[EventHandler] : Reading param - " + param.getName());
                    serviceContext.put(param.getName(), param.getValue());
                }
                try {
                    // verify the service is exported for remote execution and invoke it
                    ModelService model = dispatcher.getDispatchContext().getModelService(serviceName);
                    if (model != null && model.export) {
                        Map result = dispatcher.runSync(serviceName, serviceContext);
                        Debug.logInfo("[EventHandler] : Service invoked");
                        RPCElement resBody = new RPCElement(serviceName + "Response");
                        resBody.setPrefix(body.getPrefix());
                        resBody.setNamespaceURI(body.getNamespaceURI());
                        Set keySet = result.keySet();
                        Iterator ri = keySet.iterator();
                        while (ri.hasNext()) {
                            Object key = ri.next();
                            RPCParam par = new RPCParam(((String) key), result.get(key));
                            resBody.addParam(par);
                        }
                        resEnv.addBodyElement(resBody);
                        resEnv.setEncodingStyleURI(Constants.URI_SOAP_ENC);
                    } else {
                        sendError(response, "Request service not available");
                        throw new EventHandlerException("Service is not exported");
                    }
                } catch (GenericServiceException e) {
                    sendError(response, "Problem process the service");
                    throw new EventHandlerException(e.getMessage(), e);
                }
            }
        }

        // setup the response
        Debug.logInfo("[EventHandler] : Setting up response message");
        msg = new Message(resEnv);
        mctx.setResponseMessage(msg);
        if (msg == null) {
            sendError(response, "No response message available");
            throw new EventHandlerException("No response message available");
        }

        try {
            response.setContentType(msg.getContentType());
            response.setContentLength(msg.getContentLength());
        } catch (AxisFault e) {
            sendError(response, e);
            throw new EventHandlerException(e.getMessage(), e);
        }

        try {
            msg.writeContentToStream(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new EventHandlerException("Cannot write to the output stream");
        }

        Debug.logInfo("[EventHandler] : Message sent to requester");

        return "success";
    }

    private void sendError(HttpServletResponse res, Object obj) throws EventHandlerException {
        Message msg = new Message(obj);
        try {
            res.setContentType(msg.getContentType());
            res.setContentLength(msg.getContentLength());
            msg.writeContentToStream(res.getOutputStream());
            res.flushBuffer();
        } catch (Exception e) {
            throw new EventHandlerException(e.getMessage(), e);
        }
    }

}
