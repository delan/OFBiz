/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.workflow.impl.*;

/**
 * <p><b>Title:</b> WfFactory.java
 * <p><b>Description:</b> Workflow Factory Class
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
 *@created    October 31, 2001
 *@version    1.0
 */
public class WfFactory {
    
    // A cache of WfProcessMgr implementations
    private static Map managers = new HashMap();

    /** Creates a new {@link WfActivity} instance. 
     * @param value GenericValue object defining this activity.     
     * @param proc The WfProcess which this activity belongs.    
     * @return An instance of the WfActivify Interface
     * @throws WfException
     */
    public static WfActivity getWfActivity(GenericValue value, WfProcess proc) throws WfException {
        return getWfActivity(value,null,proc);
    }
    
    /** Creates a new {@link WfActivity} instance. 
     * @param value GenericValue object defining this activity.
     * @param data GenericValue object for runtime data.
     * @param proc The WfProcess which this activity belongs.    
     * @return An instance of the WfActivify Interface
     * @throws WfException
     */
    public static WfActivity getWfActivity(GenericValue value, GenericValue data, WfProcess proc) throws WfException {
        if ( value == null )
            throw new WfException("GenericValue object cannot be null");
        if ( proc == null )
            throw new WfException("WfProcess cannot be null");
        return new WfActivityImpl(value,data,proc);
    }
    
    
    /** Creates a new {@link WfAssignment} instance.
     * @throws WfException
     * @return An instance of the WfAssignment Interface
     */
    public static WfAssignment getWfAssignment(WfActivity activity, WfResource resource) throws WfException {
        if ( activity == null )
            throw new WfException("WfActivity cannot be null");
        if ( resource == null )
            throw new WfException("WfResource cannot be null");
        return new WfAssignmentImpl(activity,resource);
    }
    
    
    /** Creates a new {@link WfProcess} instance.
     * @param value The GenericValue object for the process definition.
     * @param mgr The WfProcessMgr which is managing this process.
     * @return An instance of the WfProcess Interface.
     * @throws WfException     
     */
    public static WfProcess getWfProcess(GenericValue value, WfProcessMgr mgr) throws WfException {
       return getWfProcess(value,null,mgr);
    }
    
    /** Creates a new {@link WfProcess} instance.
     * @param value The GenericValue object for the process definition.
     * @param data The GenericValue object for the runtime data.
     * @param mgr The WfProcessMgr which is managing this process.
     * @return An instance of the WfProcess Interface.
     * @throws WfException     
     */
    public static WfProcess getWfProcess(GenericValue value, GenericValue data, WfProcessMgr mgr) throws WfException {
        if ( value == null )
            throw new WfException("Process definition value object cannot be null");
        if ( mgr == null )
            throw new WfException("WfProcessMgr cannot be null");
        return new WfProcessImpl(value,data,mgr);
    }    
    
    /** Creates a new {@link WfProcessMgr} instance.
     * @param del The GenericDelegator to use for this manager.
     * @param pkg The Workflow Package ID.
     * @param pid The Workflow Process ID.
     * @throws WfException
     * @return An instance of the WfProcessMgr Interface.
     */
    public static WfProcessMgr getWfProcessMgr(GenericDelegator del, String pkg, String pid) throws WfException {
        if ( del == null )
            throw new WfException("Delegator cannot be null");
        if ( pkg == null )
            throw new WfException("Workflow package id cannot be null.");
        if ( pid == null )
            throw new WfException("Workflow process id cannot be null");
        
        StringBuffer buf = new StringBuffer();
        buf.append(pkg);
        buf.append(":");
        buf.append(pid);
        buf.append(":");
        String name = buf.toString();
        buf.append(del.getDelegatorName());
        WfProcessMgr mgr = null;
        if ( managers.containsKey(name) )
            mgr = (WfProcessMgr) managers.get(name);
        else
            mgr = new WfProcessMgrImpl(del,pkg,pid);
        managers.put(name,mgr);
        return mgr;
    }
    
    /** Creates a new {@link WfRequester} instance.
     * @throws WfException
     * @return An instance of the WfRequester Interface.
     */
    public static WfRequester getWfRequester() throws WfException {
        return new WfRequesterImpl();
    }
    
    /** Creates a new {@link WfResource} instance.
     * @param value The GenericValue object of the WorkflowParticipant     
     * @throws WfException
     * @return An instance of the WfResource Interface.
     */
    public static WfResource getWfResource(GenericValue value) throws WfException {
        if ( value == null )
            throw new WfException("Value object for WfResource definition cannot be null");
        return new WfResourceImpl(value);
    }

    /** Creates a new {@link WfResource} instance.
     * @param delegator The GenericDelegator for this instance
     * @param key The key for the resource
     * @param name The name of the resource
     * @param party The partyId of the resource
     * @param role The roleTypeId of the resource
     * @throws WfException
     * @return An instance of the WfResource Interface.
     */
    public static WfResource getWfResource(GenericDelegator delegator, String key, String name, String party, String role) throws WfException {        
        if ( party == null )
            party = "_NA_";
        if ( role == null )
            role = "_NA_";          
        return new WfResourceImpl(delegator,key,name,party,role);
    }    
    
    /** Creates a new {@link WfEventAudit} instance.
     * @throws WfException
     * @return An instance of the WfEventAudit Interface.
     */
    public static WfEventAudit getWfEventAudit(WfExecutionObject object, String type) throws WfException {                
        return new WfEventAuditImpl(object,type);
    }    
}
