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
        rDateList = RecurrenceUtil.split(info.getString("recurrenceDateTimes"),",");
        // Get the exception date list
        eDateList = RecurrenceUtil.split(info.getString("exceptionDateTimes"),",");
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
    
    /** Returns a recurrence date iterator */
    public Iterator getRecurrenceDateIterator() {
        return rDateList.iterator();
    }
    
    /** Returns a exception recurrence iterator */
    public Iterator getExceptionRuleIterator() {
        return eRulesList.iterator();
    }
    
    /** Returns a exception date iterator */
    public Iterator getExceptionDateIterator() {
        return eDateList.iterator();
    }

    /** Returns the first recurrence. */
    public long first() throws RecurrenceRuleException {
        return startDate.getTime();
        // TODO: Get the recurrence of a special byXXX case.
    }
    
    /** Returns the last recurrence. */
    public long last() throws RecurrenceRuleException {
        return 0;
    }
    
    /** Returns the next recurrence from now. */
    public long next() throws RecurrenceRuleException {
        return next(RecurrenceUtil.now());
    }
    
    /** Returns the next recurrence from the specified time. */
    public long next(long previous) throws RecurrenceRuleException {        
        // Get the first rule.
        Iterator rulesIt = getRecurrenceRuleIterator();
        RecurrenceRule rule = null;
        if ( rulesIt.hasNext() )
            rule = (RecurrenceRule) rulesIt.next();
        
        int currentCount = info.getInteger("recurrenceCount").intValue();
        int maxCount = rule.getCount();
        
        // Test the number of repeats
        if ( maxCount > 0 && ++currentCount > maxCount )
            return 0;
        
        // Test the end date/time
        if ( rule.getEndTime() > RecurrenceUtil.now() )
            return 0;
                                
        return 0;
    }    
}
