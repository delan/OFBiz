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

    public static final String module = JobInvoker.class.getName();

    private Thread thread;
    private JobScheduler js;
    private Job job;
    private Date created;
    private int count;
    private String name;

    public JobInvoker() {
        this.created = new Date();
        this.count = 0;

        // get a new thread
        thread = new Thread(this);
        name = thread.getName();
        thread.setDaemon(false);
    }

    /**
     * Invoke a job.
     */
    public void invoke(JobScheduler js, Job job) {
        if (job == null)
            throw new IllegalArgumentException("Cannot invoke null job");
        if (js == null)
            throw new IllegalArgumentException("Invalid scheduler");
        this.job = job;
        this.js = js;
        long runtime = job.getRuntime();
        Debug.logVerbose("JobInvoker: Invoking -- " + runtime, module);
        thread.start();
    }

    /**
     * Set this thread up for future use.
     * @return This thread.
     */
    public JobInvoker release() {
        Debug.logVerbose("JobInvoker: Releasing thread back to the pool.", module);
        // reset the reused variables
        job = null;
        js = null;
        count++;

        // get a fresh thread
        thread = new Thread(this, name);
        thread.setDaemon(false);
        return this;
    }

    /**
     * Gets the number of times this thread was used.
     * @return The number of times used.
     */
    public int getUsage() {
        return count;
    }

    /**
     * Gets the Date object when this thread was created.
     * @returns Date when this was created.
     */
    public long getTime() {
        return created.getTime();
    }

    public void run() {
        Debug.logInfo("JobInvoker: Thread (" + thread.getName() + ") Running...", module);
        ServiceDispatcher dispatcher = js.getManager().getDispatcher();
        Map result = null;
        try {
            DispatchContext ctx = dispatcher.getLocalContext(job.getLoader());
            ModelService service = ctx.getModelService(job.getService());
            result = dispatcher.runSync(job.getLoader(),service,job.getContext());
        }
        catch ( GenericServiceException e ) {
            e.printStackTrace();
        }
        Debug.logInfo("JobInvoker: Finished invocation.", module);
        job.receiveNotice(result);
        js.putInvoker(release());
    }

}
