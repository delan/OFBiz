package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.util.AbstractMenuBar;
import org.ofbiz.designer.util.WFPopup;
import org.ofbiz.designer.newdesigner.popup.*;

public class BasicMenuBar extends AbstractMenuBar {
    // {label, actionCommand}
    // submenu identifiers must not contain any spaces.
    //String[] menuInfo = {"File", "Edit", "Tools", "Help"};
    String[] menuInfo = {"File"};
    String[][][] menuItemInfo =
    {
        {
            {"Save & Exit", ActionEvents.SAVE_EXIT},
            {"Discard & Quit", ActionEvents.DISCARD_QUIT}
        }
    };


    public BasicMenuBar() {
        super();
        setData(menuInfo, menuItemInfo);
    }
}


