/*
 * $Id$
 */

package org.ofbiz.core.service.scheduler;

import java.io.*;
import java.util.*;
import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Job.java
 * <p><b>Description:</b> An individual Job object.
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 15, 2001
 *@version    1.0
 */

public class Job implements Comparable, Serializable {
    
    private RecurrenceInfo recurrence;
    private GenericValue job;
    private GenericRequester requester;
    private Map context;   
    private long runtime;
    private long seqNum;
    
    /** Creates a new Job object. */
    public Job(GenericValue job, Map context, GenericRequester requester) {
        this.job = job;
        this.context = context;
        this.requester = requester;       
        this.runtime = -1;
        this.seqNum = 0;
        init();
        try {
            job.store();
        }
        catch ( GenericEntityException e ) {
            e.printStackTrace();
        }
    }
    
    // Initialize the job.
    private void init() {
        try {
            recurrence = new RecurrenceInfo(job.getRelatedOne("RecurrenceInfo"));
        }
        catch ( GenericEntityException gee ) {
            recurrence = null;
        }
        catch ( RecurrenceInfoException rie ) {
            recurrence = null;
        }
        updateRuntime();
    }
    
    /** Updates the runtime based on the interval (minutes). */
    public void updateRuntime() {
        if ( recurrence == null )
            return;
        runtime = recurrence.next();
        Debug.logInfo("[Job.updateRuntime] : " + runtime);
    }
    
    /** Returns the name of the service associated with this job. */
    public String getService() {
        return job.getString("serviceName");
    }
    
    /** Returns the context of this job. */
    public Map getContext() {
        return this.context;
    }
    
    /** Returns the name of the dispatcher used to initialize this job */
    public String getLoader() {
        return job.getString("loaderName");
    }
    
    /** Checks to see if this Job is scheduled to run within the next second. */
    private boolean checkRuntime() {
        long delayTime = runtime - System.currentTimeMillis();
        if (delayTime <= 1000)
            return false;
        return true;
    }
    
    /** Adjusts the run time to not conflict with other Jobs. */
    public void adjustSeqNum() {
        this.seqNum++;
    }
    
    /** Receives notification when this Job is running. */
    public void receiveNotice() {
        receiveNotice(null);
    }
    
    /** Receives notification when this Job is running. */
    public void receiveNotice(Map result) {        
        if ( result != null && requester != null ) {
            requester.receiveResult(result);
        }
        Debug.logInfo("[Job.receiveNotice] : Next Runtime: " + runtime);
        // This would be a good place to log async transactions.
    }
    
    /** Re-Schedules the job for the next recurrence */
    public void rescheduleJob() {
        long runCount = 1;
        if ( job.get("runCount") != null )
            runCount = job.getLong("runCount").longValue() + 1;        
        job.set("lastRuntime",new java.sql.Timestamp(RecurrenceUtil.now()));
        job.set("runCount", new Long(runCount));
        try {            
            recurrence.incrementCurrentCount();
            job.store();
        }
        catch ( GenericEntityException gee ) {            
            gee.printStackTrace();
        }    
        updateRuntime();
    }
    
    /** Returns the name of this Job. */
    public String getJobName() {
        return job.getString("jobName");
    }
    
    /** Returns the time to run in milliseconds. */
    public long getRuntime() {
        return runtime;
    }
    
    /** Retuns the sequence number of this job. */
    public long getSeqNum() {
        return seqNum;
    }
    
    /** Retuns the last time the Job ran or 0 if never ran. */
    public long lastRuntime() {
        if ( job.getDate("lastRuntime") == null )
            return 0;
        else
            return job.getDate("lastRuntime").getTime();
    }
    
    /** Returns the number of times this Job has run. */
    public int getRunCount() {
        return job.getInteger("runCount").intValue();
    }
    
    /** Returns true if this Job repeats. */
    public boolean isRepeated() {
        if ( job.getInteger("interval").intValue() == -1 )
            return false;
        else
            return job.getBoolean("isRepeated").booleanValue();
    }
    
    /** Returns true if this job is still valid. */
    public boolean isValid() {
        if ( runtime > 0 )
            return true;
        return false;
    }
    
    /** Evaluates if this Job is equal to another Job. */
    public boolean equals(Object obj) {
        Job testJob = (Job) obj;
        if (this.runtime == testJob.getRuntime() && this.seqNum == testJob.getSeqNum())
            return true;
        return false;
    }
    
    /** Used by the comparable interface. */
    public int compareTo(Object obj) {
        Job testJob = (Job) obj;
        if ( this.runtime == testJob.getRuntime()) {
            if ( this.seqNum < testJob.getSeqNum())
                return -1;
            if ( this.seqNum > testJob.getSeqNum())
                return 1;
            return 0;
        }
        else {
            if (this.runtime < testJob.getRuntime())
                return -1;
            if (this.runtime > testJob.getRuntime())
                return 1;
        }
        return 0;
    }
    
    /** Returns a string description of this Job. */
    public String toString() {
        return "Not implemented yet.";
    }
}







