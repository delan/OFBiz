/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.workflow.impl;


import java.sql.Timestamp;
import java.util.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;


/**
 * WfEventAuditImpl - Workflow Event Audit implementation
 *
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@created    December 3, 2001
 *@version    1.0
 */

public class WfEventAuditImpl implements WfEventAudit {

    private WfExecutionObject object;
    private String eventType;
    private Timestamp timeStamp;

    public WfEventAuditImpl(WfExecutionObject object, String eventType) {
        this.object = object;
        this.eventType = eventType;
        this.timeStamp = new Timestamp(new Date().getTime());
    }

    /**
     * @throws WfException
     * @throws SourceNotAvailable
     * @return
     */
    public WfExecutionObject source() throws WfException, SourceNotAvailable {
        return object;
    }

    /**
     * @throws WfException
     * @return
     */
    public Timestamp timeStamp() throws WfException {
        return timeStamp;
    }

    /**
     * @throws WfException
     * @return
     */
    public String eventType() throws WfException {
        return eventType;
    }

    /**
     * @throws WfException
     * @return
     */
    public String activityKey() throws WfException {
        try {
            if (ObjectType.instanceOf(object, "org.ofbiz.core.workflow.WfActivity"))
                return object.key();
        } catch (Exception e) {
            throw new WfException("Source is not a WfActivity object");
        }
        throw new WfException("Source is not a WfActivity object");
    }

    /**
     * @throws WfException
     * @return
     */
    public String activityName() throws WfException {
        try {
            if (ObjectType.instanceOf(object, "org.ofbiz.core.workflow.WfActivity"))
                return object.name();
        } catch (Exception e) {}
        throw new WfException("Source is not a WfActivity object");

    }

    /**
     * @throws WfException
     * @return
     */
    public String processKey() throws WfException {
        try {
            if (ObjectType.instanceOf(object, "org.ofbiz.core.workflow.WfProcess"))
                return object.key();
        } catch (Exception e) {}
        throw new WfException("Source is not a WfProcess object");

    }

    /**
     * @throws WfException
     * @return
     */
    public String processName() throws WfException {
        try {
            if (ObjectType.instanceOf(object, "org.ofbiz.core.workflow.WfProcess"))
                return object.name();
        } catch (Exception e) {}
        throw new WfException("Source is not a WfProcess object");

    }

    /**
     * @throws WfException
     * @return
     */
    public String processMgrName() throws WfException {
        try {
            if (ObjectType.instanceOf(object, "org.ofbiz.core.workflow.WfProcess"))
                return ((WfProcess) object).manager().name();
            else if (ObjectType.instanceOf(object, "org.ofbiz.core.workflow.WfActivity"))
                return ((WfActivity) object).container().manager().name();
        } catch (Exception e) {}
        throw new WfException("Illegal source object");
    }

    /**
     * @throws WfException
     * @return
     */
    public String processMgrVersion() throws WfException {
        try {
            if (ObjectType.instanceOf(object, "org.ofbiz.core.workflow.WfProcess"))
                return ((WfProcess) object).manager().version();
            else if (ObjectType.instanceOf(object, "org.ofbiz.core.workflow.WfActivity"))
                return ((WfActivity) object).container().manager().version();
        } catch (Exception e) {}
        throw new WfException("Illegal source object");
    }

}

