/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.*;
import org.ofbiz.core.workflow.impl.*;

/**
 * <p><b>Title:</b> WfFactory.java
 * <p><b>Description:</b> Workflow Factory Class
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
 *@created    October 31, 2001
 *@version    1.0
 */

public class WfFactory {
  
  
  /** Creates a new {@link WfActivity} instance.
   * @throws WfException
   * @return An instance of the WfActivify Interface
   */
  public static WfActivity newWfActivity(String name, String description) throws WfException {
      return new WfActivityImpl(name,description);      
  }
  
  
  /** Creates a new {@link WfAssignment} instance.
   * @throws WfException
   * @return An instance of the WfAssignment Interface
   */
  public static WfAssignment newWfAssignment(WfActivity activity, WfResource resource) throws WfException {
      return new WfAssignmentImpl(activity,resource);
  }
  
  
  /** Creates a new {@link WfProcess} instance.
   * @throws WfException
   * @return An instance of the WfProcess Interface.
   */
  public static WfProcess newWfProcess(String name, String description) throws WfException {
      return new WfProcessImpl(name,description);      
  }
  
  
  /** Creates a new {@link WfProcessMgr} instance.
   * @throws WfException
   * @return An instance of the WfProcessMgr Interface.
   */
  public static WfProcessMgr newWfProcessMgr() throws WfException {
      return null;
  }
  
  /** Creates a new {@link WfRequester} instance.
   * @throws WfException
   * @return An instance of the WfRequester Interface.
   */
  public static WfRequester newWfRequester(String name) throws WfException {
      return new WfRequesterImpl(name);
  }
  
  /** Creates a new {@link WfResource} instance.
   * @throws WfException
   * @return An instance of the WfResource Interface.
   */
  public static WfResource newWfResource(String name, String key, List items) throws WfException {
      return new WfResourceImpl(name,key,items);
  }
}
