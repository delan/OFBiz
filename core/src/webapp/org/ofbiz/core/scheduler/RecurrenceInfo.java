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
        
}
