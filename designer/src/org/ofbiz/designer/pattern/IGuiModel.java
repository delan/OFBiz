package org.ofbiz.designer.pattern;

import java.util.*;

public interface IGuiModel {
    // should return names of methods that modify model data
    public HashSet getModifyMethods();
    public void dataGone();
    //public void setGui(IView view);
    //public void synchronizeGui();
    //public void die();	
    //public void synchronizeModel(Object changeSource);
    //public void fireSynchronizeModel();
}
