package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;

public interface IFieldListSupportClass extends IDataSupportClass {
	public IField createField();
	public IField getField(String name);
}
