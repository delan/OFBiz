/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.Iterator;
import java.util.List;

/**
 * <p><b>Title:</b> WfResource.java
 * <p><b>Description:</b> Workflow Resource Interface
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

public interface WfResource  {
    
    /**
     * @throws WfException
     * @return
     */
    public int howManyWorkItem() throws WfException;
    
    /**
     * @throws WfException
     * @return
     */
    public Iterator getIteratorWorkItem() throws WfException;
    
    /**
     * @param maxNumber
     * @throws WfException
     * @return List of WfAssignment objects.
     */
    public List getSequenceWorkItem(int maxNumber) throws WfException;
    
    /**
     * @param member
     * @throws WfException
     * @return
     */
    public boolean isMemberOfWorkItems(WfAssignment member) throws WfException;
    
    /**
     * @throws WfException
     * @return
     */
    public String resourceKey() throws WfException;
    
    /**
     * @throws WfException
     * @return
     */
    public String resourceName() throws WfException;
    
    /**
     * @param fromAssigment
     * @param releaseInfo
     * @throws WfException
     * @throws NotAssigned
     */
    public void release(WfAssignment fromAssignment, String releaseInfo) throws WfException, NotAssigned;
    
} // interface WfResourceOperations
