package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;

public interface IArcSupportClass extends IDataSupportClass {
    Vector getMappingNames();
    void addMappingByName(String mappingStr);
    public void addMappingByParameterID(String sourceID, String destinationID) ;
    void removeMappingByName(String mappingStr);
    public Vector getSourceExceptionNames();
    public Vector getAllTaskNames();
    public String getAlternativeTransitionByName();
    public void setAlternativeTransitionByName(String name);
    public void removeAlternativeTransitionByName();
    public String getSourceTaskAttribute();
}
