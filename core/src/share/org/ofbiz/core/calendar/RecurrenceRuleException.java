/*
 * $Id$
 */

package org.ofbiz.core.calendar;

/**
 *
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on November 6, 2001, 12:24 PM
 */
public class RecurrenceRuleException extends org.ofbiz.core.util.GeneralException {

    public RecurrenceRuleException() {
        super();
    }

    public RecurrenceRuleException(String msg) {
        super(msg);
    }

    public RecurrenceRuleException(String msg, Throwable nested) {
        super(msg, nested);
    }

}
