/*
 * $Id$
 */

package org.ofbiz.core.scheduler;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 *
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on November 6, 2001, 3:37 PM
 */
public class RecurrenceInfo {
    
    protected GenericValue info;
    protected Date startDate;
    protected List rRulesList;
    protected List eRulesList;
    protected List rDateList;
    protected List eDateList;;
    
    /** Creates new RecurrenceInfo */
    public RecurrenceInfo(GenericValue info) throws RecurrenceInfoException {
        this.info = info;
        init();
    }
    
    /** Initializes the rules for this RecurrenceInfo object. */
    public void init() throws RecurrenceInfoException {
        
        // Get start date
        long startTime = info.getTimestamp("startDateTime").getTime();
        if ( startTime > 0 ) {
            int nanos = info.getTimestamp("startDateTime").getNanos();
            startTime += (nanos/1000000);
        }
        else
            throw new RecurrenceInfoException("Recurrence's must have a start date.");
        startDate = new Date(startTime);
        
        // Get the recurrence rules objects
        try {
            Collection c = info.getRelated("RecurrenceRules");
            Iterator i = c.iterator();
            rRulesList = new ArrayList();
            while ( i.hasNext() )
                rRulesList.add(new RecurrenceRule((GenericValue)i.next()));
        }
        catch ( GenericEntityException gee ) {
            rRulesList = null;
        }
        catch ( RecurrenceRuleException rre ) {
            throw new RecurrenceInfoException("Illegal rule format.");
        }
        
        // Get the exception rules objects
        try {
            Collection c = info.getRelated("ExceptionRecurrenceRules");
            Iterator i = c.iterator();
            eRulesList = new ArrayList();
            while ( i.hasNext() )
                eRulesList.add(new RecurrenceRule((GenericValue)i.next()));
        }
        catch ( GenericEntityException gee ) {
            eRulesList = null;
        }
        catch ( RecurrenceRuleException rre ) {
            throw new RecurrenceInfoException("Illegal rule format.");
        }
        
        // Get the recurrence date list
        rDateList = RecurrenceUtil.parseDateList(RecurrenceUtil.split(info.getString("recurrenceDateTimes"),","));
        // Get the exception date list
        eDateList = RecurrenceUtil.parseDateList(RecurrenceUtil.split(info.getString("exceptionDateTimes"),","));
        
        // Sort the lists.
        Collections.sort(rDateList);
        Collections.sort(eDateList);
    }
    
    /** Returns the startDate Date object. */
    public Date getStartDate() {
        return this.startDate;
    }
    
    /** Returns the long value of the startDate. */
    public long getStartTime() {
        return this.startDate.getTime();
    }
    
    /** Returns a recurrence rule iterator */
    public Iterator getRecurrenceRuleIterator() {
        return rRulesList.iterator();
    }
    
    /** Returns a sorted recurrence date iterator */
    public Iterator getRecurrenceDateIterator() {
        return rDateList.iterator();
    }
    
    /** Returns a exception recurrence iterator */
    public Iterator getExceptionRuleIterator() {
        return eRulesList.iterator();
    }
    
    /** Returns a sorted exception date iterator */
    public Iterator getExceptionDateIterator() {
        return eDateList.iterator();
    }
    
    /** Returns the current count of this recurrence. */
    public int getCurrentCount() {
        int count = 0;
        count = info.getInteger("recurrenceCount").intValue();
        return count;
    }
    
    /** Increments the current count of this recurrence. */
    public void incrementCurrentCount() throws GenericEntityException {
        int count = getCurrentCount();
        count++;
        Integer countInt = new Integer(count);
        info.set("recurrenceCount",countInt);
        info.store();
    }
    
    /** Returns the first recurrence. */
    public long first()  {
        return startDate.getTime();
        // TODO: Get the recurrence of a special byXXX case.
    }
    
    /** Returns the last recurrence. */
    public long last()  {
        return 0;
    }
    
    /** Returns the next recurrence from now. */
    public long next()  {
        return next(RecurrenceUtil.now());
    }
    
    /** Returns the next recurrence from the specified time. */
    public long next(long fromTime)  {
        // Check for the first recurrence
        if ( fromTime == 0 || fromTime == startDate.getTime() )
            return first();
        
        // Check the rules and date list
        if ( rDateList == null && rRulesList == null )
            return 0;
        
        Date fromDate = new Date(fromTime);
        Date now = new Date();
        Date next = null;
        
        // Get the next recurrence from the rule(s).
        long nextTime = 0;
        Iterator rulesIterator = getRecurrenceRuleIterator();
        while ( rulesIterator.hasNext() ) {
            RecurrenceRule rule = (RecurrenceRule) rulesIterator.next();
            if ( nextTime == 0 ) {
                nextTime = rule.next(getStartTime(), fromTime, getCurrentCount());
                // Loop through the date list to find an earlier time
                Iterator dateIterator = getRecurrenceDateIterator();
                while ( dateIterator.hasNext() ) {
                    Date thisDate = (Date) dateIterator.next();
                    if ( thisDate.getTime() < nextTime && thisDate.getTime() > fromTime )
                        nextTime = thisDate.getTime();
                }
                // Check the exception rule(s) and dates
                Iterator exceptRulesIterator = getExceptionRuleIterator();
                while ( exceptRulesIterator.hasNext() ) {
                    RecurrenceRule except = (RecurrenceRule) exceptRulesIterator.next();
                    if ( except.isValid(getStartTime(),nextTime) || eDateList.contains(new Date(nextTime)) )
                        nextTime = 0;
                }
            }
        }
        
        return nextTime;
    }
}
