package org.ofbiz.designer.newdesigner.model;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.util.*;

public abstract class BaseModel extends AbstractRelationshipNode implements IBaseModel {
    AbstractModel abstractModel = new AbstractModel(this);

    public void handleChanged() {
        synchronizeGui();
    }

    // DO NOT MODIFY THE ORDER OF ACTIVITIES IN THE FOLLOWING METHOD !!!
    final public void die() {
        beginTransaction();
        {
            IModelProxySupportClass proxy = (IModelProxySupportClass)GuiModelProxy.getGuiModelProxy(this);
            fireDying();
            try {
                proxy.getTranslator().close();
                proxy.setTranslator(null);
            } catch(Exception e) {
            }
            Enumeration keys = relationships.keys();
            while(keys.hasMoreElements())
                removeAllRelationshipElements((String)keys.nextElement());
            modifiedSet.remove(this);
        }
        commitTransaction();
        GuiModelProxy.removeGuiModelProxy(this);        
    }

    final public IView getGui() {
        return abstractModel.getGui();
    }

    final public void synchronizeGui() {
        abstractModel.synchronizeGui();
    }

    final public void setGui(IView view) {
        abstractModel.setGui(view);
    }

    protected static HashSet modifyMethods = new HashSet();
    static {
        modifyMethods.add("setDataElement");        
        modifyMethods.add("addRelationship");       
        modifyMethods.add("removeRelationship");
    }

    public HashSet getModifyMethods() {
        new Throwable("WARNING : method NOT overridden").printStackTrace();
        return modifyMethods;
    }

    public void dataGone(){
        WARNING.println("DATA HAS BEEN REMOVED !!");
    }
}

