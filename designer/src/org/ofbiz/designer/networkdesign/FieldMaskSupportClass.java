package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class FieldMaskSupportClass extends AbstractDataSupportClass implements IFieldMaskSupportClass {
    private static Vector pureInputAccessTypes = new Vector();
    private static Vector accessTypes = new Vector();
    static {
        pureInputAccessTypes.addElement("ReadOnly");
        pureInputAccessTypes.addElement("NoAccess");

        accessTypes.addAll(pureInputAccessTypes);
        accessTypes.addElement("FullControl");
    }

    public Vector getPureInputAccessTypes() {
        //return pureInputAccessTypes;
        return accessTypes;
    }

    public Vector getAccessTypes() {
        return accessTypes;
    }

    public String getAccessType() {
        return fieldMask().getAccesstypeAttribute();
    }

    public void setAccessType(String accessType) {
        fieldMask().setAccesstypeAttribute(accessType);
        notifyDataModified(fieldMask());
    }

    private IFieldMask fieldMask() {
        return(IFieldMask)getDtdObject();
    }
}
