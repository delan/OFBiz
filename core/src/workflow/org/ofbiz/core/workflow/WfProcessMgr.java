/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.Map;
import java.util.Iterator;
import java.util.List;

/**
 * <p><b>Title:</b> WfProcessMgr.java
 * <p><b>Description:</b> Workflow Process Manager Interface
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

public interface WfProcessMgr  {
  
  /**
   * @throws WfException
   * @return
   */
  public int howManyProcess() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public Iterator getIteratorProcess() throws WfException;
  
  /**
   * @param maxNumber
   * @throws WfException
   * @return List of WfProcess objects.
   */
  public List getSequenceProcess(int maxNumber) throws WfException;
  
  /**
   * @param member
   * @throws WfException
   * @return
   */
  public boolean isMemberOfProcess(WfProcess member) throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public int processMgrState() throws WfException;
  
  /**
   * @param newState
   * @throws WfException
   * @throws TransitionNotAllowed
   */
  public void setProcessMgrState(int newState) throws WfException, TransitionNotAllowed;
  
  /**
   * @throws WfException
   * @return
   */
  public String name() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public String description() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public String category() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public String version() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public Map contextSignature() throws WfException;
  
  /**
   * @throws WfException
   * @return
   */
  public Map resultSignature() throws WfException;
  
  /**
   * Create a WfProcess object
   * @param requester
   * @throws WfException
   * @throws NotEnabled
   * @throws InvalidRequester
   * @throws RequesterRequired
   * @return WfProcess created
   */
  public WfProcess createProcess(WfRequester requester) throws WfException, NotEnabled, InvalidRequester, RequesterRequired;
  
} // interface WfProcessMgr
