/*
 * $Id$
 * $Log$
 * Revision 1.3  2001/08/25 01:42:01  azeneski
 * Seperated event processing, now is found totally in EventHandler.java
 * Updated all classes which deal with events to use to new handler.
 *
 * Revision 1.2  2001/07/23 18:05:00  azeneski
 * Fixed runaway thread in the job scheduler.
 *
 * Revision 1.1  2001/07/19 20:51:37  azeneski
 * Added a test event for the job scheduler.
 *;
 */

package org.ofbiz.core.event;

import java.util.Map;

import org.ofbiz.core.util.HttpClient;
import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> SchedulerTestEvent.java
 * <p><b>Description:</b> Scheduler Test Event Class
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
 * Created on July 14, 2001, 6:41 PM
 */
public class SchedulerTestEvent {
    
    public static String testJob(Map request, Map response) {
        Debug.log("Scheduler Test Event - parameter hash size: " + request.size());
        Debug.log("Scheduler Test Event - Has completed.");
        return "success";
    }
    
}