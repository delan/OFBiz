/*
 * $Id$ 
 */

package org.ofbiz.core.service.scheduler;

import java.io.*;

/**
 * <p><b>Title:</b> Job Scheduler Exception
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
public class JobSchedulerException extends org.ofbiz.core.util.GeneralException {
    
    Throwable nested = null;
    
    /**
     * Creates new <code>JobSchedulerException</code> without detail message.
     */
    public JobSchedulerException() {
        super();
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
        super(msg,nested);        
    }            
}


