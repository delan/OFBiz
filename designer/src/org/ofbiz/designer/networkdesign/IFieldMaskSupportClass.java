package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;

public interface IFieldMaskSupportClass extends IDataSupportClass {
	public Vector getPureInputAccessTypes();
	public Vector getAccessTypes();
	public String getAccessType();
	public void setAccessType(String accessType);
}
