package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;

public interface IRealizationSupportClass extends IDataSupportClass {
	public void createSimpleRealization(String realizationType);
	public void createNetworkRealization(String realizationType);
}
