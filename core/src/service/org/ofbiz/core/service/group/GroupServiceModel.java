/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.service.group;

import java.util.*;
import org.w3c.dom.*;

import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * GroupServiceModel.java
 * 
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Oct 18, 2002
 * @version    $Revision$
 */
public class GroupServiceModel {
    
    public static final String module = GroupServiceModel.class.getName();

    private String serviceName, serviceMode;
    
    /**
     * Constructor using DOM element     * @param service DOM element for the service     */
    public GroupServiceModel(Element service) {
        this.serviceName = service.getAttribute("name");
        this.serviceMode = service.getAttribute("mode");
    }  
    
    /**
     * Basic constructor     * @param serviceName name of the service     * @param serviceMode service invocation mode (sync|async)     */
    public GroupServiceModel(String serviceName, String serviceMode) {
        this.serviceName = serviceName;
        this.serviceMode = serviceMode;
    }  
    
    /**
     * Getter for the service mode     * @return String     */
    public String getMode() {
        return this.serviceMode;
    }
    
    /**
     * Getter for the service name     * @return String     */
    public String getName() {
        return this.serviceName;
    }  
    
    /**
     * Invoker method to invoke this service     * @param dispatcher ServiceDispatcher used for this invocation     * @param localName Name of the LocalDispatcher used     * @param context Context for this service (will use only valid parameters)     * @return Map result Map     * @throws GenericServiceException     */
    public Map invoke(ServiceDispatcher dispatcher, String localName, Map context) throws GenericServiceException {
        DispatchContext dctx = dispatcher.getLocalContext(localName);
        ModelService model = dctx.getModelService(getName());
        if (model == null)
            throw new GenericServiceException("Group defined service (" + getName() + ") is not a defined service.");
            
        Map thisContext = model.makeValid(context, ModelService.IN_PARAM);
        if (getMode().equals("async")) {
            List requiredOut = model.getParameterNames(ModelService.OUT_PARAM, false);
            if (requiredOut.size() > 0) {
                Debug.logWarning("Grouped service (" + getName() + ") requested 'async' invocation; running sync because of required OUT parameters.", module);
                return dispatcher.runSync(localName, model, thisContext);
            } else {
                dispatcher.runAsync(localName, model, thisContext, false);
                return new HashMap();
            }
        } else {
            return dispatcher.runSync(localName, model, thisContext);
        }
    }
           
    /**          * @see java.lang.Object#toString()     */           
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getName());
        str.append("::");
        str.append(getMode());
        str.append("::");
        return str.toString();
    }
}
