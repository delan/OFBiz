package org.ofbiz.designer.newdesigner.model;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public interface INetworkEditorComponentModel extends IContainerModel {
	public static final String TASKTYPE = "TaskType";
	public static final String DOMAINTYPE = "DomainType";
	public static final String COMPARTMENTTYPE = "CompartmentType";
	public static final String WORKFLOWTYPE = "WorkflowType";

	public static final String COLLABORATIONREAL = "CollaborationRealization";
	public static final String HUMANREAL = "HumanRealization";
	public static final String NONTRANSACTIONALREAL = "NonTransactionalTaskRealization";
	public static final String TRANSACTIONALREAL = "TransactionalTaskRealization";
	public static final String NONTRANSACTIONALNETWORK = "NonTransactionalNetwork";
	public static final String TRANSACTIONALNETWORK = "TransactionalNetwork";
	public static final String OPEN2PCNETWORK = "Open2PCNetwork";
	public static final String COMPOSITENETWORK = "CompositeNetwork";
	public static final String RESULTBADTASK = "ResultBadTask";
	public static final String RESULTGOODTASK = "ResultGoodTask";
	public static final String STARTTASK = "StartTask";
	public static final String SYNCHRONIZATIONTASKIN = "SynchronizationTaskIn";
	public static final String SYNCHRONIZATIONTASKOUT = "SynchronizationTaskOut";

	public static final Dimension taskDimension = new Dimension(40, 40);
	public static final Dimension specialTaskDimension = new Dimension(30, 30);

	public ImageIcon getIcon();
	public Color getColor();
	//public String getID();
	public String getDomainURL();
	public void setDomainURL(String url);
	public String getTaskReference();
	public void setTaskReference(String taskReference);
	public String getDisplayName();
	public void setDisplayName(String displayName);
	public String getStartTask();
	public void setStartTask(String startTask);
	public String getEndTask();
	public void setEndTask(String endTask);

	public IArcModel createIncomingArc(String name, String arcID, String arcType);
	public IArcModel createOutgoingArc(String name, String arcID, String arcType);

	public String getHref();
	public void setHref(String href);
	public void setColor(Color color);
	public void setTaskType(String taskTypeIn);
	public String getTaskType();
	public String getModelType();
	public void setModelType(String modelTypeIn);
	public IContainerModel createChildContainer(String name, String taskType);
	public IArcModel createChildArc(String ID);

	public INetworkEditorComponentModel getChildContainerByID(String ID);
	public INetworkEditorComponentModel getChildContainerByIDRecursive(String ID);
	public IArcModel getChildArcByID(String ID);
	public IArcModel getChildArcByIDRecursive(String ID);

	public boolean isSimpleTask();
	public boolean isSpecialTask();
	public HashSet getDeletedChildren();
	public boolean isPrimaryDomain();
}