package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;

public interface INetworkTaskRealizationSupportClass extends IDataSupportClass {
	public IDomain createDomain(String ID);
	public Vector getInputMappingNames();
	public void addInputMappingByName(String mappingStr);
	public void removeInputMappingByName(String mappingStr);
	public Vector getOutputMappingNames();
	public void addOutputMappingByName(String mappingStr);
	public void removeOutputMappingByName(String mappingStr);
}
