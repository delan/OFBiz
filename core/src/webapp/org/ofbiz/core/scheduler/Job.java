/*
 * $Id$
 * $Log$
 */

package org.ofbiz.core.scheduler;

import java.io.*;
import java.util.*;
import org.ofbiz.core.entity.*;
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on July 17, 2001
 */

public class Job implements Comparable, Serializable {
    
    public static final int RUNTIME_ADJUSTMENT = 3000;  // Default adjustment is 3 seconds.
    public static final int INTERVAL_MINUTE = 1;
    public static final int INTERVAL_HOUR = 2;
    public static final int INTERVAL_DAY = 3;
    public static final int INTERVAL_MONTH = 4;
    
    private GenericValue job;
    private Map context;
    private long runTime;
    
    /* Entity Fields:
     * String jobName
     * String serviceName
     * Date startDate
     * Date endDate
     * Date lastRun
     * int interval
     * int intervalType
     * int runCount
     * boolean isRepeated
     * boolean isPersistant
     */
    
    /** Creates a new Job object. */
    public Job(GenericValue job, Map context) {
        this.job = job;
        this.context = context;
        this.runTime = -1;
        updateRunTime();
        try {
            job.store();
        }
        catch ( GenericEntityException e ) {
            e.printStackTrace();
        }
    }
    
    /** Updates the runTime based on the interval (minutes). */
    public void updateRunTime() {
        if ( job.getDate("startDate") == null )
            job.set("startDate", new Date());
        if ( job.getInteger("interval").intValue() != -1 )
            runTime = getNextStartTime();
    }
    
    /** Checks to see if this Job is scheduled to run within the next second. */
    private boolean checkRunTime() {
        long delayTime = runTime - System.currentTimeMillis();
        if (delayTime <= 1000)
            return false;
        return true;
    }
    
    /** Adjusts the run time to not conflict with other Jobs. */
    public void adjustRunTime() {
        runTime += RUNTIME_ADJUSTMENT;
    }
    
    /** Receives notification when this Job is running. */
    public void receiveNotice() {
        Date stamp = new Date();
        int runCount = job.getInteger("runCount").intValue();
        runCount++;
        job.set("lastRunTime",stamp);
        job.set("runCount", new Integer(runCount));
        try {
            job.store();
        }
        catch ( GenericEntityException e ) {
            e.printStackTrace();
        }
    }
    
    /** Returns the name of this Job. */
    public String getJobName() {
        return job.getString("jobName");
    }
    
    /** Returns the time to run in milliseconds. */
    public long getRunTime() {
        return runTime;
    }
    
    /** Retuns the last time the Job ran or 0 if never ran. */
    public long lastRunTime() {
        if ( job.getDate("lastRunTime") == null )
            return 0;
        else
            return job.getDate("lastRunTime").getTime();
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
    
    /** Evaluates if this Job is equal to another Job. */
    public boolean equals(Object obj) {
        Job testJob = (Job) obj;
        if (this.runTime == testJob.getRunTime())
            return true;
        return false;
    }
    
    /** Used by the comparable interface. */
    public int compareTo(Object obj) {
        Job testJob = (Job) obj;
        if (this.runTime < testJob.getRunTime())
            return -1;
        if (this.runTime > testJob.getRunTime())
            return 1;
        return 0;
    }
    
    private long getNextStartTime() {
        // Build the calendar object.
        Calendar eventCal = Calendar.getInstance();
        eventCal.setTime(job.getDate("startDate"));
        
        // Get the current times.
        long startTime = job.getDate("startDate").getTime();
        long currentTime = System.currentTimeMillis();
        long endTime = -1;
        if ( job.getDate("endDate") != null )
            endTime = job.getDate("endDate").getTime();
        
        // If Job end time has past, run no more.
        if ( (endTime != -1) && (endTime < currentTime) )
            return -1;
        
        // If Job has run and does not repeat, run no more.
        if ( !job.getBoolean("isRepeated").booleanValue() && getRunCount() > 0 )
            return -1;
        
        // If startTime has not yet arrived use it.
        if ( startTime > currentTime )
            return startTime;
        
        // The end time has not yet arrived, get the next start time.
        long nextStartTime = startTime;
        if ( job.getInteger("intervalType").intValue() > 0 ) {
            while ( nextStartTime < currentTime ) {
                switch(job.getInteger("intervalType").intValue()) {
                    case INTERVAL_MINUTE:
                        eventCal.add(Calendar.MINUTE, job.getInteger("interval").intValue());
                        break;
                    case INTERVAL_HOUR:
                        eventCal.add(Calendar.HOUR, job.getInteger("interval").intValue());
                        break;
                    case INTERVAL_DAY:
                        eventCal.add(Calendar.DAY_OF_MONTH, job.getInteger("interval").intValue());
                        break;
                    case INTERVAL_MONTH:
                        eventCal.add(Calendar.MONTH, job.getInteger("interval").intValue());
                        break;
                    default:
                        break;
                }
                nextStartTime = eventCal.getTime().getTime();
            }
            return nextStartTime;
        }
        return -1;
    }
    
    /** Returns a string description of this Job. */
    public String toString() {
        StringBuffer sb = new StringBuffer("Job");
        sb.append(" Name="); sb.append(job.getString("jobName"));
        sb.append(" Start="); sb.append(job.getDate("startDate"));
        sb.append(" End="); sb.append(job.getDate("endDate"));
        sb.append(" Next-Run="); sb.append(new Date(runTime));
        sb.append(" Interval="); sb.append(job.getInteger("interval"));
        sb.append(" Interval-Type="); sb.append(job.getInteger("intervalType"));
        sb.append(" Repeats="); sb.append(job.getBoolean("isRepeated"));
        sb.append(" Service="); sb.append(job.getString("serviceName"));
        return sb.toString();
    }
}







