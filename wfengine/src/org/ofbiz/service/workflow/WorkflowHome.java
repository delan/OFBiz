/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Aug 11, 2001
 * Time: 1:11:52 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.service.workflow;

import javax.ejb.EJBHome;
import javax.ejb.CreateException;
import java.rmi.RemoteException;

public interface WorkflowHome extends EJBHome {

    public Workflow create() throws RemoteException, CreateException;
}
