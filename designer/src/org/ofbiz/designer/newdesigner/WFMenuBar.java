package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.util.AbstractMenuBar;
import org.ofbiz.designer.util.WFPopup;
import org.ofbiz.designer.newdesigner.popup.*;

public class WFMenuBar
extends AbstractMenuBar {
    // {label, actionCommand}
    // submenu identifiers must not contain any spaces.
    String[] menuInfo = {"File", "Tools"};
    String[][][] menuItemInfo =
    {
        {
            {"New", ActionEvents.NEW}, 
            //{"Open", WFPopup.DYNAMICSUBMENU + "FileOpenSubMenu"}, 
            {"Open", ActionEvents.OPEN}, 
            //{"Revert to Saved", ActionEvents.REVERT},
            {"Save", ActionEvents.SAVE}, 
            {"", ActionEvents.SEPERATOR}, 
            {"Save & Exit", ActionEvents.SAVE_EXIT},
            {"Discard & Quit", ActionEvents.DISCARD_QUIT}
        },
        {
            {"Task Editor", ActionEvents.TASK_ED},
            {"Domain Editor", ActionEvents.DOMAIN_ED},
            {"Data Editor", ActionEvents.DATA_ED},
            {"RoleDomain Editor", ActionEvents.ROLE_ED},
            {"Split Design", ActionEvents.SPLIT_DESIGN},
            {"", ActionEvents.SEPERATOR}, 
            {"Level Mapping Inputs", ActionEvents.LEVEL_MAPPING_INPUTS},
            {"Level Mapping Outputs", ActionEvents.LEVEL_MAPPING_OUTPUTS},
            {"", ActionEvents.SEPERATOR}, 
            {"Generate OrbWork Runtime", ActionEvents.GENERATE_ORBWORK_RUNTIME},
            {"Generate NRL Runtime", ActionEvents.GENERATE_NRL_RUNTIME},
            {"", ActionEvents.SEPERATOR}, 
            {"Automap Parameters", ActionEvents.AUTOMAP_PARAMETERS},
        },
    };


    public WFMenuBar() {
        super();
        setData(menuInfo, menuItemInfo);
    }
}


