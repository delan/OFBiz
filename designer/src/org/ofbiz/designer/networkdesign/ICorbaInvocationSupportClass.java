package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;

public interface ICorbaInvocationSupportClass extends IDataSupportClass {
    public void addParameterByName(String name);
    public void removeParameterByName(String name);
    public Vector getParametersNames();
    public Vector getForwardMappings();
    public String getReturnValueMapping();
    public void addForwardMapping(String mappingStr);
    public void addReverseMapping(String mappingStr);
    public void removeForwardMapping(String mappingStr);
    public void removeReverseMapping(String mappingStr);
    public void setReturnValueMapping(String mappingStr);
}
    
