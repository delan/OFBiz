/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/07/19 20:50:22  azeneski
 * Added the job scheduler to 'core' module.
 *
 */

package org.ofbiz.core.scheduler;

import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> JobScheduler.java
 * <p><b>Description:</b> Thread For Job Scheduling.
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
 * Created on July 17, 2001, 8:46 PM
 */
public class JobScheduler implements Runnable {
    
    protected JobManager jm;
    protected Thread thread;
    private long sleep = -1;
    private boolean isRunning = true;
    
    public JobScheduler( JobManager jm ) {
        this.jm = jm;
        
        // start the thread
        thread = new Thread(this, this.toString());
        thread.setDaemon(false);
        thread.start();
    }
    
    public synchronized void updateDelay(long sleep) {
        this.sleep = sleep;
        notify();
    }
    
    public synchronized void run() {
        Debug.log("JobScheduler: (" + thread.getName() + ") Thread Running...");
        while( isRunning ) {
            try {
                if ( sleep <= 0 )
                    wait();
                else {
                    long timeout = sleep - System.currentTimeMillis();
                    if ( timeout > 0 )
                        wait(timeout);
                }
                if ( isRunning && sleep >= 0 && (sleep - System.currentTimeMillis() < 1000) ) {
                    sleep = -1;
                    jm.invokeJob();
                }
            }
            catch(InterruptedException e) {
                Debug.log(e);
                stop();
            }
        }
        Debug.log("JobScheduler: (" + thread.getName() + ") Thread ending...");
    }
    
    public synchronized void stop() {
        isRunning = false;
        Debug.log("JobScheduler: Shutting down...");
        notify();
    }
}

