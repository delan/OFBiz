/*
 * $Id$
 *
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.webapp.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.log4j.Category;

/**
 * SOAPEventHandler - SOAP Event Handler implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @author     <a href="mailto:">Andy Chen</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.0
 */
public class SOAPEventHandler implements EventHandler {

    public static final String module = SOAPEventHandler.class.getName();
    public static Category category = Category.getInstance(SOAPEventHandler.class.getName());

    /** Invoke the web event
     *@param eventPath The path or location of this event
     *@param eventMethod The method to invoke
     *@param request The servlet request object
     *@param response The servlet response object
     *@return String Result code
     *@throws EventHandlerException
     */
    public String invoke(String eventPath, String eventMethod, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
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
            reqEnv = (SOAPEnvelope) msg.getSOAPPart().getEnvelope();                    
        } catch (SOAPException e) {
            throw new EventHandlerException("Cannot get the envelope", e);
        }
        
        List bodies = null;

        try {
            bodies = reqEnv.getBodyElements();
        } catch (AxisFault e) {
            sendError(response, e);
            throw new EventHandlerException(e.getMessage(), e);
        }

        Debug.logVerbose("[Processing]: SOAP Event", module);

        // each is a different service call
        Iterator i = bodies.iterator();

        while (i.hasNext()) {
            Object o = i.next();

            if (o instanceof RPCElement) {
                RPCElement body = (RPCElement) o;
                String serviceName = body.getMethodName();
                List params = null;

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

                    if (Debug.verboseOn()) Debug.logVerbose("[Reading Param]: " + param.getName(), module);
                    serviceContext.put(param.getName(), param.getValue());
                }
                try {
                    // verify the service is exported for remote execution and invoke it
                    ModelService model = dispatcher.getDispatchContext().getModelService(serviceName);

                    if (model != null && model.export) {
                        Map result = dispatcher.runSync(serviceName, serviceContext);

                        Debug.logVerbose("[EventHandler] : Service invoked", module);
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
                        resEnv.setEncodingStyle(Constants.URI_DEFAULT_SOAP_ENC);
                    } else {
                        sendError(response, "Request service not available");
                        throw new EventHandlerException("Service is not exported");
                    }
                } catch (GenericServiceException e) {
                    sendError(response, "Problem process the service");
                    throw new EventHandlerException(e.getMessage(), e);
                } catch (javax.xml.soap.SOAPException e) {
                    sendError(response, "Problem process the service");
                    throw new EventHandlerException(e.getMessage(), e);
                }
            }
        }

        // setup the response
        Debug.logVerbose("[EventHandler] : Setting up response message", module);
        msg = new Message(resEnv);
        mctx.setResponseMessage(msg);
        if (msg == null) {
            sendError(response, "No response message available");
            throw new EventHandlerException("No response message available");
        }

        try {            
            response.setContentType(msg.getContentType(Constants.DEFAULT_SOAP_VERSION));   
            response.setContentLength(Integer.parseInt(Long.toString(msg.getContentLength())));                                 
        } catch (AxisFault e) {
            sendError(response, e);
            throw new EventHandlerException(e.getMessage(), e);
        }

        try {
            msg.writeTo(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new EventHandlerException("Cannot write to the output stream");
        } catch (SOAPException e) {
            throw new EventHandlerException("Cannot write message to the output stream");
        }

        Debug.logVerbose("[EventHandler] : Message sent to requester", module);

        return "success";
    }

    private void sendError(HttpServletResponse res, Object obj) throws EventHandlerException {
        Message msg = new Message(obj);

        try {
            res.setContentType(msg.getContentType(Constants.DEFAULT_SOAP_VERSION));
            res.setContentLength(Integer.parseInt(Long.toString(msg.getContentLength())));
            msg.writeTo(res.getOutputStream());                        
            res.flushBuffer();
        } catch (Exception e) {
            throw new EventHandlerException(e.getMessage(), e);
        }
    }
}
