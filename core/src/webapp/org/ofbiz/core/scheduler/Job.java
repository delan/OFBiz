/*
 * $Id$
 * $Log$
 * Revision 1.5  2001/08/25 17:29:11  azeneski
 * Started migrating Debug.log to Debug.logInfo and Debug.logError
 *
 * Revision 1.4  2001/08/25 01:42:01  azeneski
 * Seperated event processing, now is found totally in EventHandler.java
 * Updated all classes which deal with events to use to new handler.
 *
 * Revision 1.3  2001/07/23 21:20:51  azeneski
 * Added support for HTTP GET/POST events in job scheduler.
 * Fixed a bug in the XML parser which caused the parser to die
 * when a empty element was found.
 *
 * Revision 1.2  2001/07/23 18:05:00  azeneski
 * Fixed runaway thread in the job scheduler.
 *
 * Revision 1.1  2001/07/19 20:50:22  azeneski
 * Added the job scheduler to 'core' module.
 *
 */

package org.ofbiz.core.scheduler;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;

import org.ofbiz.core.event.EventHandler;
import org.ofbiz.core.event.EventHandlerException;
import org.ofbiz.core.util.Debug;

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
 * Created on July 17, 2001, 8:46 PM
 */
public class Job implements Comparable, Serializable {
    
    public static final int INTERVAL_TYPE_MINUTE = 1;
    public static final int INTERVAL_TYPE_HOUR = 2;
    public static final int INTERVAL_TYPE_DAY = 3;
    public static final int INTERVAL_TYPE_MONTH = 4;
    
    private String jobName = null;
    private Date startDate = null;
    private Date endDate = null;
    private int interval = -1;
    private int intervalType = 0;
    private long runTime = -1;
    private int runCount = 0;
    private boolean isRepeated;
    private boolean fromConfig;
    private String eventType;
    private String eventPath;
    private String eventMethod;
    private Map parameters;
    private Map headers;
    
    /** Creates a new Job object. */
    public Job( String jobName, Date startDate, Date endDate, int interval, int intervalType, boolean isRepeated, String eventType, String eventPath, String eventMethod, Map parameters, Map headers ) {
        this.jobName =jobName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.interval = interval;
        this.intervalType = intervalType;
        this.isRepeated = isRepeated;        
        this.eventType = eventType;
        this.eventPath = eventPath;
        this.eventMethod = eventMethod;
        this.parameters = parameters;
        this.headers = headers;
        updateRunTime();
    }
        
    /** Updates the runTime based on the interval (minutes). */
    public void updateRunTime() {
        if ( startDate == null )          
            startDate = new Date();
        if ( interval != -1 )         
                runTime = getNextStartTime();        
    }
    
    /** Checks to see if this job is scheduled to run within the next second. */
    private boolean checkRunTime() {
        long delayTime = runTime - System.currentTimeMillis();
        if (delayTime <= 1000)
            return false;
        return true;
    }
    
    /** Adjusts the runTime by 5 seconds. */
    public void adjustRunTime() {
        runTime += 5000;
    }
    
    /** Returns the name of this job. */
    public String getJobName() {
        return jobName;
    }
    
    /** Returns the time to run in milliseconds. */
    public long getRunTime() {
        return runTime;
    }
    
    /** Returns true if this job is repeats. */
    public boolean isRepeated() {
        if ( interval == -1 )
            isRepeated = false;
        return isRepeated;
    }
    
    /** Notifies the job manager this job was scheduled from the config file. */
    public void setFromConfig(boolean fromConfig) {
        this.fromConfig = fromConfig;
    }
    
    /** Returns true if this job was scheduled from the configuration file. */
    public boolean isFromConfig() {
        return fromConfig;
    }
    
    /** Evaluates if this job is equal to another job. */
    public boolean equals(Object obj) {
        Job job = (Job) obj;
        if (runTime == job.runTime)
            return true;
        return false;
    }
    
    /** Used by the comparable interface. */
    public int compareTo(Object obj) {
        Job job = (Job) obj;
        if (runTime < job.runTime)
            return -1;
        if (runTime > job.runTime)
            return 1;
        return 0;
    }
    
    /** Returns the number of times this job has run. */
    public int getRunCount() {
        return runCount;
    }
    
    /** Invokes the event associated with this job. */
    public void invoke() {
        Debug.logInfo("Job (" + jobName + ") invoking.");
        String eventResult = null;
        runCount++;
        if ( eventType != null && eventPath != null && eventMethod != null ) {
            try {
                EventHandler eh = new EventHandler(eventType,eventPath,eventMethod);
                eventResult = eh.invoke(parameters,parameters);
            }
            catch ( EventHandlerException e ) {
                Debug.logError(e,"Event Error - ");
            }
        }
    }
    
    private long getNextStartTime() {
        // Build the calendar object.        
        Calendar eventCal = Calendar.getInstance();  
        eventCal.setTime(startDate);
        
        // Get the current times.
        long startTime = startDate.getTime();
        long currentTime = System.currentTimeMillis();
        long endTime = -1;
        if ( endDate != null )
            endTime = endDate.getTime();
                
        // If event end time has past, run no more.
        if ( (endTime != -1) && (endTime < currentTime) )
            return -1;
        
        // If job has run and does not repeat, run no more.
        if ( !isRepeated && runCount > 0 )
            return -1;
        
        // If startTime has not yet arrived use it.
        if ( startTime > currentTime )
            return startTime;
        
        // The end time has not yet arrived, get the next start time.
        long nextStartTime = startTime;
        if ( intervalType > 0 ) {
            while ( nextStartTime < currentTime ) {
                switch(intervalType) {
                    case INTERVAL_TYPE_MINUTE:
                        eventCal.add(Calendar.MINUTE, interval);
                        break;
                    case INTERVAL_TYPE_HOUR:
                        eventCal.add(Calendar.HOUR, interval);
                        break;
                    case INTERVAL_TYPE_DAY:
                        eventCal.add(Calendar.DAY_OF_MONTH, interval);
                        break;
                    case INTERVAL_TYPE_MONTH:
                        eventCal.add(Calendar.MONTH, interval);
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
    
    /** Returns a string description of this job. */
    public String toString() {
        StringBuffer sb = new StringBuffer("Job");
        sb.append(" Name="); sb.append(jobName);
        sb.append(" Start="); sb.append(startDate);
        sb.append(" End="); sb.append(endDate);
        sb.append(" Next-Run="); sb.append(new Date(runTime));
        sb.append(" Interval="); sb.append(interval);
        sb.append(" Interval-Type="); sb.append(intervalType);
        sb.append(" Repeats="); sb.append(isRepeated);
        sb.append(" Event-Type="); sb.append(eventType);
        sb.append(" Event-Path="); sb.append(eventPath);
        sb.append(" Event-Invoke="); sb.append(eventMethod);
        sb.append(" Parameters="); sb.append(parameters.size());
        sb.append(" Headers="); sb.append(headers.size());
        return sb.toString();
    }
}







