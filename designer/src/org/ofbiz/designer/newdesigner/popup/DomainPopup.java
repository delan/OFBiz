package org.ofbiz.designer.newdesigner.popup;

import org.ofbiz.designer.util.*;

public class DomainPopup extends WFPopup {
    // {label, actionCommand}
    static String[][] menuItemInfo = {
        //{"Insert", SUBMENU + "InsertCompartmentMenu"}, 
        {"Delete", ActionEvents.DELETE}, 
        {"Insert", SUBMENU +  "DomainInsertPopup"}, 
        {"", ActionEvents.SEPERATOR},
        {"View up", ActionEvents.VIEW_UP},
        //{"Domain Editor", ActionEvents.DOMAIN_ED},
        {"Properties", ActionEvents.LOCAL_DOMAIN_ED},
        {"Domain Editor", ActionEvents.DOMAIN_ED},
        //{"Find", ActionEvents.FIND},
        //  {"Help", ActionEvents.HELP_CONTEXT}
    };

    public DomainPopup() {
        super(menuItemInfo, "org.ofbiz.designer.newdesigner.popup");
    }
}
