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
        ServletContext context = session.getServletContext();
        LocalDispatcher dispatcher = (LocalDispatcher) context.getAttribute("dispatcher");
        AxisServer axisServer;
        try {
            axisServer = AxisServerFactory.getServer("OFBiz/Axis Server",null);
        }
        catch ( AxisFault e ) {
            // TODO create a soap message w/ the error(s).
            throw new EventHandlerException("Problems with the AXIS server",e);
        }
        MessageContext mctx = new MessageContext(axisServer);
                        
        // get the SOAP message
        Message msg = null;                       
        try {                        
            msg = new Message(request.getInputStream(), false, 
              request.getHeader("Content-Type"), request.getHeader("Content-Location"));
        }
        catch ( IOException ioe ) {
            throw new EventHandlerException(ioe.getMessage(),ioe);            
        }
        
        if ( msg == null ) {
            // TODO create a soap message w/ the error(s).
            throw new EventHandlerException("SOAP Message is null");            
        }
        
        mctx.setRequestMessage(msg);
                                                                                                            
        // new envelopes
        SOAPEnvelope resEnv = new SOAPEnvelope();
        SOAPEnvelope reqEnv = null;
                                        
        // get the service name and parameters                    
        try {
            reqEnv = msg.getSOAPPart().getAsSOAPEnvelope();            
        }
        catch ( AxisFault e ) {
            // TODO create a soap message w/ the error(s).
            Debug.logError(e,"[SOAP EVENT] : Error w/ SOAPEnvelope : ");            
        }
        Vector bodies = null;
        try {
            bodies = reqEnv.getBodyElements();
        }
        catch ( AxisFault e ) {
            Debug.logError(e,"[SOAP EVENT] : Error w/ BodyElements : ");            
        }
        
        // each is a different service call
        Iterator i = bodies.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof RPCElement ) {
                RPCElement body = (RPCElement) o;
                String serviceName = body.getMethodName();
                Debug.logInfo("[SOAP EVENT] : Service Name - " + serviceName);
                Vector params = null; 
                try {
                    params = body.getParams();
                }
                catch ( Exception e ) {
                    Debug.logError(e,"[SOAP EVENT] : Error w/ Parameters : ");
                }
                Map serviceContext = new HashMap();
                Iterator p = params.iterator();
                while ( p.hasNext() ) {
                    RPCParam param = (RPCParam) p.next();
                    serviceContext.put(param.getName(),param.getValue());
                    Debug.logInfo("[SOAP EVENT] : Name - " + param.getName());
                    Debug.logInfo("[SOAP EVENT] : Value - " + param.getValue());
                }                
                try {
                    // verify the service is exported for remote execution and invoke it
                    ModelService model = dispatcher.getDispatchContext().getModelService(serviceName);
                    if ( model != null && model.export ) {                                                    
                        Map result = dispatcher.runSync(serviceName,serviceContext); 
                        RPCElement resBody = new RPCElement(serviceName + "Response");
                        resBody.setPrefix(body.getPrefix());
                        resBody.setNamespaceURI(body.getNamespaceURI());
                        Set keySet = result.keySet();
                        Iterator ri = keySet.iterator();
                        while ( ri.hasNext() ) {
                            Object key = ri.next();                                              
                            RPCParam par = new RPCParam(((String)key), result.get(key));
                            Debug.logInfo("[SOAP EVENT] : Response Name: " + par.getName());
                            Debug.logInfo("[SOAP EVENT] : Response Value: " + par.getValue());
                            resBody.addParam(par);
                        }
                        Debug.logInfo("[SOAP EVENT] : Setting the body element and encoding style");
                        resEnv.addBodyElement(resBody);
                        resEnv.setEncodingStyleURI(Constants.URI_SOAP_ENC);
                        Debug.logInfo("[SOAP EVENT] : Set.");
                    }
                    else {
                        Debug.logInfo("[SOAP EVENT] : Service is not exported");
                        // set messages
                    }
                }
                catch ( GenericServiceException e ) { 
                    e.printStackTrace();
                }
            }
        }
                                                
        // setup the response
        msg = new Message(resEnv);    
        mctx.setResponseMessage(msg);
        if ( msg == null ) 
            Debug.logInfo("[SOAP EVENT] : Response message is null");
        else
            Debug.logInfo("[SOAP EVENT] : Response XML\n" + msg.getSOAPPart().getAsString());
        
        try {
            response.setContentType( msg.getContentType() );
            response.setContentLength(msg.getContentLength() );
            Debug.logInfo("[SOAP EVENT] : Content-type: " + msg.getContentType() + " - Content-length: " + msg.getContentLength());
        }
        catch ( AxisFault e ) {
            Debug.logError(e,"[SOAP EVENT] : GenResponse : ");
            return "error";
        }
         
        try {
            msg.writeContentToStream(response.getOutputStream());
            response.flushBuffer();
        }
        catch ( IOException e ) {
            Debug.logError(e,"[SOAP EVENT] : WriteResponse : ");
            return "error";
        }
                                
        return "";
    }    
}
