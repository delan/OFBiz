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

/**
 * GroupServiceModel.java
 * 
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Oct 18, 2002
 * @version    $Revision$
 */
public class GroupServiceModel {

    private String serviceName, serviceMode;
    
    public GroupServiceModel(Element service) {
        this.serviceName = service.getAttribute("name");
        this.serviceMode = service.getAttribute("mode");
    }  
    
    public GroupServiceModel(String serviceName, String serviceMode) {
        this.serviceName = serviceName;
        this.serviceMode = serviceMode;
    }  
    
    public String getMode() {
        return this.serviceMode;
    }
    
    public String getName() {
        return this.serviceName;
    }  
    
    public Map invoke(ServiceDispatcher dispatcher, String localName, Map context) throws GenericServiceException {
        DispatchContext dctx = dispatcher.getLocalContext(localName);
        ModelService model = dctx.getModelService(getName());
        Map thisContext = model.makeValid(context, ModelService.IN_PARAM);
        if (getMode().equals("async")) {
            dispatcher.runAsync(localName, model, thisContext, false);
            return new HashMap();
        } else {
            return dispatcher.runSync(localName, model, thisContext);
        }
    }
           
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getName());
        str.append("::");
        str.append(getMode());
        str.append("::");
        return str.toString();
    }
}
