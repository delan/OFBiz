/*
 * $Id$
 * $Log$
 */

package org.ofbiz.core.scheduler;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.ServletContext;

import org.ofbiz.core.util.ConfigXMLReader;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> JobManager.java
 * <p><b>Description:</b> Manages scheduled events/jobs.
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
public class JobManager {
    
    protected ServletContext context;
    protected JobScheduler js;
    protected SortedSet queue;
    
    public JobManager() {
        init(null);
    }
    
    public JobManager(ServletContext context) {
        this.context = context;
        HashMap config = null;
        String configFileUrl = null;
        try {
            configFileUrl = context.getResource(context.getInitParameter(SiteDefs.SCHEDULER_CONFIG)).toString();
        }
        catch ( Exception e ) {
            Debug.log(e,"Error Reading Scheduler Config File: " + configFileUrl);
        }
        if ( configFileUrl != null )
            config = ConfigXMLReader.getSchedulerMap(configFileUrl);
        
        init(config);
    }
    
    public JobManager(HashMap config) {
        init(config);
    }
    
    public void init(HashMap config) {
        // Create the queue and start the thread.
        queue = (SortedSet) new TreeSet();
        js = new JobScheduler(this);
        
        if ( config != null ) {
            Set hashSet = config.keySet();
            Iterator i = hashSet.iterator();
            while ( i.hasNext() ) {
                Object o = i.next();
                // Job Name
                String name = (String) o;
                HashMap jobMap = (HashMap) config.get(o);                
                // Start/End Dates (must be parsed).
                String startStr = (String) jobMap.get(ConfigXMLReader.SCHEDULER_STARTDATE);
                String endStr = (String) jobMap.get(ConfigXMLReader.SCHEDULER_ENDDATE);
                // Interval type and value (must be parsed).
                String intervalStr = (String) jobMap.get(ConfigXMLReader.SCHEDULER_INTERVAL);
                String intervalTypeStr = (String) jobMap.get(ConfigXMLReader.SCHEDULER_INTERVAL_TYPE);
                // Event method/path/type strings.
                String eventType = (String) jobMap.get(ConfigXMLReader.SCHEDULER_EVENT_TYPE);
                String eventPath = (String) jobMap.get(ConfigXMLReader.SCHEDULER_EVENT_PATH);
                String eventMethod = (String) jobMap.get(ConfigXMLReader.SCHEDULER_EVENT_METHOD);
                // True/False does job repeat.
                String jobRepeats = (String) jobMap.get(ConfigXMLReader.SCHEDULER_REPEAT);
                // Parameters and headers to pass on to the invoker.
                HashMap params = (HashMap) jobMap.get(ConfigXMLReader.SCHEDULER_PARAMETERS);
                HashMap headers = (HashMap) jobMap.get(ConfigXMLReader.SCHEDULER_HEADERS);
                
                // Fix the interval integers.
                int intervalType = getIntervalTypeFromString(intervalTypeStr);
                int interval = -1;
                if ( intervalStr != null && !intervalStr.equals("") )
                    interval = Integer.parseInt(intervalStr);
                
                // Parse the date strings.
                Date startDate = null;
                Date endDate = null;
                if ( startStr != null && !startStr.equals("") ) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(SiteDefs.SCHEDULER_DATE_FORMAT);
                        ParsePosition pos = new ParsePosition(0);
                        startDate = sdf.parse(startStr,pos);                        
                    }
                    catch ( Exception e ) {
                        Debug.log(e,"Problems Parsing Start Date.");
                    }
                }
                if ( endStr != null && !endStr.equals("") ) {
                    try {                        
                        SimpleDateFormat sdf = new SimpleDateFormat(SiteDefs.SCHEDULER_DATE_FORMAT);
                        ParsePosition pos = new ParsePosition(0);
                        endDate = sdf.parse(endStr,pos);                      
                    }
                    catch ( Exception e ) {
                        Debug.log(e,"Problems Parsing End Date.");
                    }
                }
                
                // Get the boolean value.
                boolean repeat = jobRepeats.equalsIgnoreCase("true") ? true : false;
                
                // Add the job.
                addJob(name,startDate,endDate,interval,intervalType,repeat,eventType,eventPath,eventMethod,params,headers);
            }
        }
    }
    
    public synchronized Job addJob( String jobName, Date startDate, Date endDate, int interval, int intervalType,
    boolean isRepeated, String eventType, String eventPath, String eventMethod, HashMap parameters, HashMap headers ) {
        Job job = new Job(jobName,startDate,endDate,interval,intervalType,isRepeated,eventType,eventPath,eventMethod,parameters,headers);
        if (queueJob(job)) {
            Debug.log("JobManager: Added Job ("+jobName+").");
            Debug.log("Job String: " + job.toString());
        } 
        else {
            Debug.log("Job ("+jobName+") not queued.");
        }
        return job;
    }
    
    public synchronized Job addJob( String jobName, int interval, boolean isRepeated, String eventType,
    String eventPath, String eventMethod, HashMap parameters ) {
        Job job = addJob(jobName,null,null,interval,1,isRepeated,eventType,eventPath,eventMethod,parameters,null);
        return job;
    }
    
    /** Queues a job. **/
    public synchronized boolean queueJob(Job job) {
        while (containsJob(job))
            job.adjustRunTime();
        if ( job.getRunTime() != -1 ) {            
            if (queue.add(job)) {
                js.updateDelay( ((Job) queue.first()).getRunTime());
                return true;
            }
        }
        return false;
    }
    
    public synchronized boolean removeJob(Job job) {
        if (!queue.contains(job))
            return false;
        if (queue.remove(job)) {
            if (queue.size() > 0)
                js.updateDelay( ((Job) queue.first()).getRunTime());
        }
        return true;
    }
    
    /** Removes all jobs and stops the scheduler. */
    public synchronized void clearJobs() {
        js.stop();
        js = null;
        queue.clear();
    }
    
    /** Returns true if another job is scheduled for the same time. */
    public synchronized boolean containsJob(Job job) {
        return queue.contains(job);
    }
    
    /** Returns a list of Jobs. */
    public synchronized List getJobList() {
        LinkedList jobList = new LinkedList();
        Iterator iterator = queue.iterator();
        while (iterator.hasNext())
            jobList.add(iterator.next());
        return jobList;
    }
    
    /** Invoked by the Scheduler to run the job. */
    protected synchronized void invokeJob() {
        if (queue.isEmpty())
            return;
        Job firstJob = (Job) queue.first();
        queue.remove(firstJob);
        
        // Run the event here.
        Debug.log("***JobManager -- Invoking: " + firstJob.getJobName());
        new JobInvoker(firstJob);
        
        if (firstJob.isRepeated()) {
            firstJob.updateRunTime();            
            queueJob(firstJob);
        }
        if (!queue.isEmpty()) {
            Job nextJob = (Job) queue.first();
            long nextDelayTime = nextJob.getRunTime();
            if (nextDelayTime - System.currentTimeMillis() < 1000)
                invokeJob();
            else
                js.updateDelay(nextDelayTime);
        }
    }
    
    /** Returns the integer value of the Interval Type String */
    private int getIntervalTypeFromString(String type) {
        if ( type.equalsIgnoreCase("minute") )
            return Job.INTERVAL_TYPE_MINUTE;
        if ( type.equalsIgnoreCase("hour") )
            return Job.INTERVAL_TYPE_HOUR;
        if ( type.equalsIgnoreCase("day") )
            return Job.INTERVAL_TYPE_DAY;
        if ( type.equalsIgnoreCase("month") )
            return Job.INTERVAL_TYPE_MONTH;
        return -1;
    }
    
    public void finish() {
        if (js != null)
            js.stop();
    }
}
