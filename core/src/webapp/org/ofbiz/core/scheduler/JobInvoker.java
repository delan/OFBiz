/*
 * $Id$
 * $Log$
 * Revision 1.2  2001/11/02 23:11:14  azeneski
 * Some non-functional services implementation.
 *
 */

package org.ofbiz.core.scheduler;

import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> JobInvoker.java
 * <p><b>Description:</b> Thread which runs the event for the job.
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
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on July 18, 2001, 2:24 PM
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
        job.receiveNotice();
        // invoke the service job.getString("serviceName");
        // dispatcher.runSync(...);
    }
}
