/* $Id$
 * @(#)Workflow.java   Sun Aug 12 13:22:41 GMT+02:00 2001
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
 * @created Sun Aug 12 13:22:41 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */
/*
 * (#)Workflow.java 
 */
package org.ofbiz.wfengine;


import javax.ejb.EJBObject;
import java.util.Collection;
import java.util.Date;



import java.util.Collection;
import java.io.Serializable;

/**
 * Schnittstelle der Workflow-Engine. Sie stellt Methode für das Erzeugen und Ausführen von Workflowprozessen zur Verfügung.<p> Für die Benutzer- und Prozesszuordnung gibt es jeweils ein Sessionobjekt, welche beim Aufruf der Methoden

übergeben werden müssen.<p>
 * @author Oliver Wieland
 * @version 1.0
 */
 
public interface Workflow  extends EJBObject  {

	/**	 
	 * Cancel a running process on user request
	 * @param pId Wert für Id
	 * @param pReason Wert für Reason
	 */
	public void abortProcess(WFProcessID pId, String pReason)  throws WFException;

	/**	 
	 * Adds a listener
	 * @param pListener Wert für Listener
	 */
	public void addListener(WFListener pListener)  throws WFException;

	/**	 
	 * Creates a new workflow process. The invoker must have the right to do that.
	 * @param pInitiator Wert für Initiator
	 * @param pName Wert für Name
	 * @return WFProcessID
	 */
	public WFProcessID createProcess(WFPrincipal pInitiator, String pName)  throws WFException;

	/**	 
	 * Methode getActiveProcesses
	 * @param pUser Wert für User
	 * @return Collection
	 */
	public Collection getActiveProcesses(WFPrincipal pUser)  throws WFException;

	/**	 
	 * Methode getProcesses
	 * @param pUser Wert für User
	 * @return Collection
	 */
	public Collection getProcesses(WFPrincipal pUser)  throws WFException;

	/**	 
	 * Returns the worklist of the user
	 * @param pUser Wert für User
	 * @return Collection
	 */
	public Collection getWorklist(WFPrincipal pUser)  throws WFException;

	/**	 
	 * Returns the worklist of the user
	 * @param pUser Wert für User
	 * @param pFilter Wert für Filter
	 * @return Collection
	 */
	public Collection getWorklist(WFPrincipal pUser, WFActivityFilter pFilter)  throws WFException;

	/**	 
	 * Logs in a user and returns a user session object. 
	 * @param pUser User ID
	 * @param pPassword Wert für Password
	 * @return WFPrincipal
	 */
	public WFPrincipal login(String pUser, String pPassword) ;

	/**	 
	 * Removes an existing listener
	 * @param pListener Wert für Listener
	 */
	public void removeListener(WFListener pListener)  throws WFException;

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
	 * @param pId Wert für Id
	 * @param pContext Wert für Context
	 * @return WFActivity
	 */
	public WFActivity updateProcess(WFProcessID pId, WFContext pContext)  throws WFException;

	/**	 
	 * Marks the actual activity as completed and schedules the next activities to run. This method returns the next activity, if it is a) unique (no branch) b) is executable by the current user and c) the process session is still valid. Otherwise null is returned.
	 * @param pId Wert für Id
	 * @param pContext Wert für Context
	 * @param pActivity Wert für Activity
	 * @return WFActivity
	 */
	public WFActivity updateProcess(WFProcessID pId, WFContext pContext, WFActivity pActivity)  throws WFException;

	/**	 
	 * Returns the real username
	 * @param pUser Wert für User
	 * @return String
	 */
	public String getRealName(WFPrincipal pUser)  throws WFException;

	/**	 
	 * Returns the history of an activity (who worked on it, etc.)
	 * @param pUser Wert für User
	 * @param pActivity Wert für Activity
	 * @return Collection
	 */
	public Collection getHistory(WFPrincipal pUser, WFActivity pActivity)  throws WFException;

	/**	 
	 * Retuns the group of a user
	 * @param pUser Wert für User
	 * @return Collection
	 */
	public Collection getGroups(WFPrincipal pUser) ;


}