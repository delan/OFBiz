/*
 * $Id$
 */

package org.ofbiz.core.event;

import javax.servlet.*;
import javax.servlet.http.*;
import org.ofbiz.core.service.scheduler.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> TestEvent.java
 * <p><b>Description:</b> Test Event Class
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    July 14, 2001
 *@version    1.0
 */
public class TestEvent {

    
    public static String test (HttpServletRequest request, HttpServletResponse response ) {
        request.setAttribute("MESSAGE","Test Event Ran Fine.");
        Debug.log("Test Event Ran Fine.");
        return "success";
    }
        
    public static String httpClientTest(HttpServletRequest request, HttpServletResponse response ) {
        try {            
            HttpClient http = new HttpClient("http://www.ofbiz.org/cgi-bin/http_test.pl");
            http.setHeader("Cookie","name=value,value=name");
            http.setHeader("User-Agent", "Mozilla/4.0");
            http.setParameter("testId","testing");
            Debug.log(http.post());
        } 
        catch ( Exception e ) {
            Debug.log(e,"HttpClientException Caught.");
        }
        return "success";
    }
}
