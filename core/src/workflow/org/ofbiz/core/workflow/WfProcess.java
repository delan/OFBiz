/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.Iterator;
import java.util.Map;
import java.util.List;

/**
 * <p><b>Title:</b> WfProcess.java
 * <p><b>Description:</b> Workflow Process Interface
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

public interface WfProcess extends WfExecutionObject {
  
  /**
   * @throws WfException
   * @return
   */
  public WfRequester requester() throws WfException;
  
  /**
   * @param newValue
   * @throws WfException
   * @throws CannotChangeRequester
   */
  public void setRequester(WfRequester newValue) throws WfException, CannotChangeRequester;
  
  /**
   * @throws WfException
   * @return
   */
  public int howManyStep() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public Iterator getIteratorStep() throws WfException;
  
  /**
   * @param maxNumber
   * @throws WfException
   * @return List of WfActivity objects.
   */
  public List getSequenceStep(int maxNumber) throws WfException;
  
  /**
   * @param member
   * @throws WfException
   * @return
   */
  public boolean isMemberOfStep(WfActivity member) throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public WfProcessMgr manager() throws WfException;
  
  /**
   * @throws WfException
   * @throws ResultNotAvailable
   * @return
   */
  public Map result() throws WfException, ResultNotAvailable;
  
  /**
   * @throws WfException
   * @throws CannotStart
   * @throws AlreadyRunning
   */
  public void start() throws WfException, CannotStart, AlreadyRunning;
  
  /**
   * @param state
   * @throws WfException
   * @throws InvalidState
   * @return
   */
  public Iterator getActivitiesInState(String state) throws WfException, InvalidState;
  
} // interface WfProcessOperations
