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

package org.ofbiz.core.service.scheduler;

import java.util.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * JobInvoker
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @created    November 15, 2001
 * @version    1.0
 */
public class JobInvoker implements Runnable {
    
    private Job job;
    private ServiceDispatcher dispatcher;
    private Thread thread;
    
    /** Creates new JobInvoker */
    public JobInvoker(Job job, ServiceDispatcher dispatcher) {
        this.job = job;
        this.dispatcher = dispatcher;
        long runtime = job.getRuntime();
        String threadName = (String) (new Long(runtime)).toString();
        // Start the invoker thread.
        thread = new Thread(this, threadName);
        thread.setDaemon(false);
        thread.start();
    }
    
    public void run() {
        Debug.logInfo("JobInvoker: Thread (" + thread.getName() + ") Running...");
        Map result = null;
        try {
            DispatchContext ctx = dispatcher.getLocalContext(job.getLoader());
            ModelService service = ctx.getModelService(job.getService());
            result = dispatcher.runSync(job.getLoader(),service,job.getContext());
        }
        catch ( GenericServiceException e ) {
            e.printStackTrace();
        }
        job.receiveNotice(result);
    }
}
