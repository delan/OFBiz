/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.workflow.client;

import java.util.*;

import org.ofbiz.core.service.*;
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.*;

/**
 * Workflow Client API - Start Activity Async-Job
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.0
 */
public class StartActivityJob extends AbstractJob {
    
    public static final String module = StartActivityJob.class.getName();

    protected WfActivity activity = null;
    protected GenericRequester requester = null;

    public StartActivityJob(WfActivity activity) {
        this(activity, null);
    }
    
    public StartActivityJob(WfActivity activity, GenericRequester requester) {
        super(activity.toString());        
        this.activity = activity;
        this.requester = requester;
        runtime = new Date().getTime();
    }

    protected void finish() {
        runtime = -1;
    }

    /**
     * @see org.ofbiz.core.service.job.Job#exec()
     */
    public void exec() {
        String activityIds = null;
        try {
            activityIds = activity.getDefinitionObject().getString("activityId") + " / " + 
                    activity.getRuntimeObject().getString("workEffortId");
            activity.activate();
            if (requester != null)
                requester.receiveResult(new HashMap());
        } catch (Exception e) {            
            Debug.logError(e, "Start Activity [" + activityIds + "] Failed", module);
            if (requester != null)
                requester.receiveException(e);
        }       
        finish();
    }
}
