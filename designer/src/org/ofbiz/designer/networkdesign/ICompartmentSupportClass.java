package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;

public interface ICompartmentSupportClass extends IDataSupportClass {
    public ICompartment createCompartment(String ID);
    public void setUrlAttribute(String modelURL);
}
