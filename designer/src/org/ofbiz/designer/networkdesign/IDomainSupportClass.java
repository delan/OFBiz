package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;

public interface IDomainSupportClass extends IDataSupportClass {
    public ICompartment createCompartment(String ID);
    public Vector getAllTaskIDs();
    public void setUrlAttribute(String modelURL);
}
