/*
 * $Id$
 */

package org.ofbiz.core.workflow;

/**
 * <p><b>Title:</b> TransitionNotAllowed Workflow Exception
 * <p><b>Description:</b> TransitionNotAllowed Workflow Exception
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
 *@author     Andy Zeneski (jaz@zsolv.com)
 *@created    October 29, 2001
 *@version    1.0
 */

public final class TransitionNotAllowed extends WfException {
    
    public TransitionNotAllowed() {
    }
    
    public TransitionNotAllowed(String msg) {
        super(msg);
    }
    
    public TransitionNotAllowed(String msg, Throwable nested) {
        super(msg,nested);
    }
    
} // class TransitionNotAllowed
