/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/08/25 01:42:01  azeneski
 * Seperated event processing, now is found totally in EventHandler.java
 * Updated all classes which deal with events to use to new handler.
 *
 */

package org.ofbiz.core.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.lang.reflect.*;

import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> EventHandler.java
 * <p><b>Description:</b> Generic event invoker.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on August 24, 2001, 8:28 PM
 */
public class EventHandler {
    
    private String eventType, eventPath, eventMethod;
    
    /**
     * Creates a new EventHandler.
     *@param eventType The type of event to be processed.
     *@param eventPath The physical path to the event.
     *@param eventMethod The method/function found in the eventPath to invoke.
     */
    public EventHandler(String eventType, String eventPath, String eventMethod) {
        this.eventType = eventType;
        this.eventPath = eventPath;
        this.eventMethod = eventMethod;
    }
    
    /** 
     * Invoke the event.     
     */
    public String invoke(HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        Class[] paramTypes = new Class[] {HttpServletRequest.class, HttpServletResponse.class};
        Object[] params = new Object[] {request,response};
        return invoke(paramTypes, params);
    }
    
    /**
     * Invoke the event.     
     */
    public String invoke(Map request, Map response) throws EventHandlerException {
        Class[] paramTypes = new Class[] {Map.class, Map.class};
        Object[] params = new Object[] {request,response};
        return invoke(paramTypes, params);
    }
    
    private String invoke(Class[] paramTypes, Object[] params) throws EventHandlerException {
        String eventReturnString = null;
        if ( eventType == null || eventPath == null || eventMethod == null )
            throw new EventHandlerException("Invalid event type, method or path.");
        
        if ( eventType.compareToIgnoreCase("java") == 0 ) {
            Debug.logInfo("[EventHandler] : Processing JAVA event.");
            try {
                Class c = Class.forName(eventPath);
                Method m = c.getMethod(eventMethod,paramTypes);
                eventReturnString = (String) m.invoke(null,params);
                Debug.logInfo("[EventHandler] : Returned -  " + eventReturnString);
            }
            catch ( Exception e ) {
                Debug.logError(e,"[EventHandler] : Problems Processing Event.");
                throw new EventHandlerException("Problems processing event: " + e.getMessage());
            }
        }
        // else if ( eventType.compareToIgnoreCase("javascript") == 0 ) { }
        // to be implemented later.
        else {
            throw new EventHandlerException("Unknown event type");
        }
        return eventReturnString;
    }                    
}
