/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * <p><b>Title:</b> WfActivity.java
 * <p><b>Description:</b> Workflow Activity Interface
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
 *@created    October 29, 2001
 *@version    1.0
 */

public interface WfActivity extends WfExecutionObject {
    
    /**
     * @throws WfException
     * @return
     */
    public int howManyAssignment() throws WfException;
    
    /**
     * @throws WfException
     * @return
     */
    public Iterator getIteratorAssignment() throws WfException;
    
    /**
     * @param maxNumber
     * @throws WfException
     * @return  List of WfAssignment objects.
     */
    public List getSequenceAssignment(int maxNumber) throws WfException;
    
    /**
     * @param member
     * @throws WfException
     * @return
     */
    public boolean isMemberOfAssignment(WfAssignment member) throws WfException;
    
    /**
     * @throws WfException
     * @return
     */
    public WfProcess container() throws WfException;
    
    /**
     * @throws WfException
     * @throws ResultNotAvailable
     * @return
     */
    public Map result() throws WfException, ResultNotAvailable;
    
    /**
     * @param result
     * @throws WfException
     * @throws InvalidData
     */
    public void setResult(Map result) throws WfException, InvalidData;
    
    /**
     * @throws WfException
     * @throws CannotComplete
     */
    public void complete() throws WfException, CannotComplete;
    
    /**
     * Activates this activity.
     * @param manual flag to specify this is a manual attempt
     * @throws WfException
     * @throws CannotStart
     * @throws AlreadyRunning
     */
    public void activate(boolean manual) throws WfException, CannotStart, AlreadyRunning;
    
    /** 
     * Assign this activity to a resource
     * @param WfResource to assign this activity to
     * @param append Append to end if existing list (true) or replace existing (false)
     * @throws WfException
     */
    public void assign(WfResource resource, boolean append) throws WfException;    
    
} // interface WfActivity
