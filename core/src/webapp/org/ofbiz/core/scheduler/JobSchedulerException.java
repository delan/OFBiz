/*
 * JobSchedulerException.java
 *
 * Created on November 2, 2001, 9:53 AM
 */

package org.ofbiz.core.scheduler;

import java.io.*;

/**
 *
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version
 */
public class JobSchedulerException extends Exception {
    
    Throwable nested = null;
    
    /**
     * Creates new <code>JobSchedulerException</code> without detail message.
     */
    public JobSchedulerException() {
    }
        
    /**
     * Constructs an <code>JobSchedulerException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public JobSchedulerException(String msg) {
        super(msg);        
    }
    
    /**
     * Constructs an <code>JobSchedulerException</code> with the specified detail message and nested Exception.
     * @param msg the detail message.
     */
    public JobSchedulerException(String msg, Throwable nested) {
        super(msg);
        this.nested = nested;
    }    
    
    /** Returns the detail message, including the message from the nested exception if there is one. */
    public String getMessage() {
        if(nested != null) return super.getMessage() + " (" + nested.getMessage() + ")";
        else return super.getMessage();
    }
    
    /** Prints the composite message to System.err. */
    public void printStackTrace() {
        super.printStackTrace();
        if(nested != null) nested.printStackTrace();
    }
    
    /** Prints the composite message and the embedded stack trace to the specified stream ps. */
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if(nested != null) nested.printStackTrace(ps);
    }
    
    /** Prints the composite message and the embedded stack trace to the specified print writer pw. */
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if(nested != null) nested.printStackTrace(pw);
    }
}


