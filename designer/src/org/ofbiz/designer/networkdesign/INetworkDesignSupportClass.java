package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;

public interface INetworkDesignSupportClass extends IDataSupportClass {
	public ITask createTask(String taskID);
	public IArc createArc(String arcID, String arcType, String sourceID, String destinationID);
}
