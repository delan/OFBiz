package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;

public interface ITypeSupportClass extends IDataSupportClass {
	public String toString();
	public void parseAndSet(String typeStr);
}
