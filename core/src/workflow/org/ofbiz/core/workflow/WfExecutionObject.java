/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

/**
 * <p><b>Title:</b> Workflow Interface
 * <p><b>Description:</b> Needs Description
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

public interface WfExecutionObject  {
  
  /**
   * @throws WfException
   * @return
   */
  public int workflowState() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public int whileOpen() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public int whyNotRunning() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public int howClosed() throws WfException;
  
  /**
   * @throws WfException
   * @return List of String objects.
   */
  public List validStates() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public String state() throws WfException;
  
  /**
   * @param newState
   * @throws WfException
   * @throws InvalidState
   * @throws TransitionNotAllowed
   */
  public void changeState(String newState) throws WfException, InvalidState, TransitionNotAllowed;
  
  /**
   * @throws WfException
   * @return
   */
  public String name() throws WfException;
  
  /**
   * @param newValue
   * @throws WfException
   */
  public void setName(String newValue) throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public String key() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public String description() throws WfException;
  
  /**
   * @param newValue
   * @throws WfException
   */
  public void setDescription(String newValue) throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public Map processContext() throws WfException;
  
  /**
   * @param newValue
   * @throws WfException
   * @throws InvalidData
   * @throws UpdateNotAllowed
   */
  public void setProcessContext(java.util.Map newValue) throws WfException, InvalidData, UpdateNotAllowed;
  
  /**
   * @throws WfException
   * @return
   */
  public int priority() throws WfException;
  
  /**
   * @param newValue
   * @throws WfException
   */
  public void setPriority(short newValue) throws WfException;
  
  /**
   * @throws WfException
   * @throws CannotResume
   * @throws NotRunning
   * @throws NotSuspended
   */
  public void resume() throws WfException, CannotResume, NotRunning, NotSuspended;
  
  /**
   * @throws WfException
   * @throws CannotSuspend
   * @throws NotRunning
   * @throws AlreadySuspended
   */
  public void suspend() throws WfException, CannotSuspend, NotRunning, AlreadySuspended;
  
  /**
   * @throws WfException
   * @throws CannotStop
   * @throws NotRunning
   */
  public void terminate() throws WfException, CannotStop, NotRunning;
  
  /**
   * @throws WfException
   * @throws CannotStop
   * @throws NotRunning
   */
  public void abort() throws WfException, CannotStop, NotRunning;
  
  /**
   * @throws WfException
   * @throws HistoryNotAvailable
   * @return
   */
  public int howManyHistory() throws WfException, HistoryNotAvailable;
  
  /**
   * @param query
   * @param namesInQuery
   * @throws WfException
   * @throws HistoryNotAvailable
   * @return
   */
  public Iterator getIteratorHistory(String query, java.util.Map namesInQuery) throws WfException, HistoryNotAvailable;
  
  /**
   * @param maxNumber
   * @throws WfException
   * @throws HistoryNotAvailable
   * @return List of WfEventAudit objects.
   */
  public List getSequenceHistory(int maxNumber) throws WfException, HistoryNotAvailable;
  
  /**
   * @param member
   * @throws WfException
   * @return
   */
  public boolean isMemberOfHistory(WfExecutionObject member) throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public Timestamp lastStateTime() throws WfException;
  
} // interface WfExecutionObjectOperations
