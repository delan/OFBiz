/* $Id$
 * @(#)Workflow.java   Fri Aug 17 12:18:06 GMT+02:00 2001
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Oliver Wieland (wieland.oliver@t-online.de)
 * @created Fri Aug 17 12:18:06 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 * Revision 1.1.1.1  2001/08/10 10:33:41  owieland
 * Initial import
 *
 */
/*
 * (#)Workflow.java 
 */
package org.ofbiz.service.workflow;


import java.util.Collection;
import java.util.Date;



import java.util.Collection;
import java.io.Serializable;

/**
 * Interface (facade) for the workflow engine. Currently it is not j2ee aware.
 * @author Oliver Wieland
 * @version 1.0
 */
 
public interface Workflow  extends Serializable  {
	

	/**	 
	 * Cancel a running process on user request
	 * @param pId Value for  Id
	 * @param pReason Value for  Reason
	 */
	public void abortProcess(WFProcessID pId, String pReason)  throws WFException;

	/**	 
	 * Adds a listener
	 * @param pListener Value for  Listener
	 */
	public void addListener(WFListener pListener) ;

	/**	 
	 * Creates a new workflow process. The invoker must have the right to do that.
	 * @param pInitiator Value for  Initiator
	 * @param pName Value for  Name
	 * @return WFProcessID
	 */
	public WFProcessID createProcess(WFPrincipal pInitiator, String pName)  throws WFException;

	/**	 
	 * Method getActiveProcesses
	 * @param pUser Value for  User
	 * @return Collection
	 */
	public Collection getActiveProcesses(WFPrincipal pUser) ;

	/**	 
	 * Method getProcesses
	 * @param pUser Value for  User
	 * @return Collection
	 */
	public Collection getProcesses(WFPrincipal pUser) ;

	/**	 
	 * Returns the worklist of the user
	 * @param pUser Value for  User
	 * @return Collection
	 */
	public Collection getWorklist(WFPrincipal pUser) ;

	/**	 
	 * Returns the worklist of the user
	 * @param pUser Value for  User
	 * @param pFilter Value for  Filter
	 * @return Collection
	 */
	public Collection getWorklist(WFPrincipal pUser, WFActivityFilter pFilter) ;

	/**	 
	 * Logs in a user and returns a user session object
	 * @param pUser Value for  User
	 * @param pPassword Value for  Password
	 * @return WFPrincipal
	 */
	public WFPrincipal login(String pUser, String pPassword) ;

	/**	 
	 * Removes an existing listener
	 * @param pListener Value for  Listener
	 */
	public void removeListener(WFListener pListener) ;

	/**	 
	 * Resumes a previous suspended process. If worng state, an exception is thrown
	 * @param pUser User session object
	 * @param pId Id of suspended activity
	 */
	public void resumeProcess(WFPrincipal pUser, String pId)  throws WFException;

	/**	 
	 * Suspends a process.
	 * @param pActivity Aktivität (bzw. Prozess), der suspendiert werden soll
	 * @param pResumeDate Date, when the engine should resume the activity. If null, the activity must be manually resumed
	 */
	public void suspendProcess(WFActivity pActivity, Date pResumeDate)  throws WFException;

	/**	 
	 * Marks the given activity as 'completed' and returns the next activity, if unique and the same user can perform this activity. Otherwise null
	 * @param pId Value for  Id
	 * @param pContext Value for  Context
	 * @return WFActivity
	 */
	public WFActivity updateProcess(WFProcessID pId, WFContext pContext)  throws WFException;

	/**	 
	 * Markiert die aktuelle Aktivität als beendet und liefert die nächste Aktivität. Wenn es keine weiteren Aktivitäten mehr gibt oder der Prozess sich aufteilt, wird null zurückgegeben.

Zusätzlich kann hier die Aktivität vorgegeben werden (notwendig bei Verzweigungen)
	 * @param pId Value for  Id
	 * @param pContext Value for  Context
	 * @param pActivity Value for  Activity
	 * @return WFActivity
	 */
	public WFActivity updateProcess(WFProcessID pId, WFContext pContext, WFActivity pActivity) ;

	/**	 
	 * Method getRealName
	 * @param pUser Value for  User
	 * @return String
	 */
	public String getRealName(WFPrincipal pUser) ;

	/**	 
	 * Method getHistory
	 * @param pUser Value for  User
	 * @param pActivity Value for  Activity
	 * @return Collection
	 */
	public Collection getHistory(WFPrincipal pUser, WFActivity pActivity) ;

	/**	 
	 * Method getGroupName
	 * @param pUser Value for  User
	 * @return String
	 */
	public String getGroupName(WFPrincipal pUser) ;


}