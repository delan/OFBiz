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
        
        // Get the recurrence rules objects
        try {
            Collection c = info.getRelated("RecurrenceRules");
            Iterator i = c.iterator();
            rRulesList = new ArrayList();
            while ( i.hasNext() )
                rRulesList.add(new RecurrenceRule((GenericValue)i.next()));
        }
        catch ( GenericEntityException gee ) {
            throw new RecurrenceInfoException("No recurrence rule associated with this entity.");
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
        
        Date fromDate = new Date(fromTime);
        Date now = new Date();
        Date next = null;
        
        // Loop through the date list.
        Iterator dateIterator = getRecurrenceDateIterator();
        while ( dateIterator.hasNext() && next == null ) {
            Date thisDate = (Date) dateIterator.next();
            // Test if this date is valid and not in the exception list.
            if ( thisDate.getTime() > fromTime && !eDateList.contains(thisDate))
                next = thisDate;
        }
        if ( next != null )
            return next.getTime();
        
        // Test the rules.
        long nextTime = 0;
        Iterator rulesIterator = getRecurrenceRuleIterator();
        while ( rulesIterator.hasNext() ) {
            RecurrenceRule rule = (RecurrenceRule) rulesIterator.next();
            if ( nextTime == 0 )
                nextTime = rule.next(getStartTime(), fromTime, getCurrentCount());
        }
                
        return nextTime;
    }
}
