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

import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;

/**
 * GroupModel.java
 * 
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Oct 18, 2002
 * @version    $Revision$
 */
public class GroupModel {
    
    public static final String module = GroupModel.class.getName();
    
    private String groupName, sendMode;
    private List services;
    private int lastServiceRan;
    
    public GroupModel(Element group) {
        this.lastServiceRan = -1;
        this.services = new LinkedList();
        List serviceList = UtilXml.childElementList(group, "service");  
        Iterator i = serviceList.iterator();
        while (i.hasNext()) {
            Element service = (Element) i.next();
            services.add(new GroupServiceModel(service));
        }
        this.groupName = group.getAttribute("name");
        this.sendMode = group.getAttribute("send-mode"); 
        if (Debug.verboseOn()) Debug.logVerbose("Created Service Group Model --> " + this, module);       
    }
    
    public GroupModel(String groupName, String sendMode, List services) {
        this.lastServiceRan = -1;
        this.groupName = groupName;
        this.sendMode = sendMode;
        this.services = services;
    }
    
    public String getGroupName() {
        return this.groupName;
    }
    
    public String getSendMode() {
        return this.sendMode;
    }
    
    public List getServices() {
        return this.services;
    }
    
    public Map run(ServiceDispatcher dispatcher, String localName, Map context) throws GenericServiceException {
        if (this.getSendMode().equals("all")) {
            return runAll(dispatcher, localName, context);
        } else if (this.getSendMode().equals("round-robin")) {
            return runIndex(dispatcher, localName, context, (++lastServiceRan));   
        } else if (this.getSendMode().equals("random")) {
            int randomIndex = (int) (Math.random() * (double) (services.size())); 
            return runIndex(dispatcher, localName, context, randomIndex);
        } else if (this.getSendMode().equals("first-available")) {
            return runOne(dispatcher, localName, context);                            
        } else { 
            throw new GenericServiceException("This mode is not currently supported");
        }
    }
    
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getGroupName());
        str.append("::");
        str.append(getSendMode());
        str.append("::");        
        str.append(getServices());
        return str.toString();
    }
    
    private Map runAll(ServiceDispatcher dispatcher, String localName, Map context) throws GenericServiceException {
        Map result = new HashMap();
        Iterator i = services.iterator();
        while (i.hasNext()) {
            GroupServiceModel model = (GroupServiceModel) i.next();
            result.putAll(model.invoke(dispatcher, localName, context));
        }
        return result;
    }
    
    private Map runIndex(ServiceDispatcher dispatcher, String localName, Map context, int index) throws GenericServiceException {
        GroupServiceModel model = (GroupServiceModel) services.get(index);
        return model.invoke(dispatcher, localName, context);
    } 
    
    private Map runOne(ServiceDispatcher dispatcher, String localName, Map context) throws GenericServiceException {      
        Map result = null;        
        Iterator i = services.iterator();
        while (i.hasNext() && result != null) {
            GroupServiceModel model = (GroupServiceModel) i.next();
            try {
                result = model.invoke(dispatcher, localName, context);
            } catch (GenericServiceException e) {
                if (Debug.verboseOn()) Debug.logVerbose("Service: " + model + " failed.", module);
            }
        }
        if (result == null) 
            throw new GenericServiceException("All services failed to run; none availabel.");
        return result;
    }            
}
