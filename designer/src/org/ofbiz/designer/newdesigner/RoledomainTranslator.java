/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 4:26:51 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.BaseTranslator;
import org.ofbiz.designer.generic.IComboBoxWrapper;
import org.ofbiz.designer.generic.IComboBoxModel;
import org.ofbiz.designer.networkdesign.ITaskWrapper;
import org.ofbiz.designer.util.WARNING;

class RoledomainTranslator extends BaseTranslator {
    public RoledomainTranslator(IComboBoxWrapper modelIn, ITaskWrapper taskWrapperIn, String mode) {
        super(modelIn, taskWrapperIn);
        synchronize(mode);
    }

    public void updateModelImpl() {
        IComboBoxModel model = (IComboBoxModel)getGuiModel();
        ITaskWrapper taskWrapper = (ITaskWrapper)getDataObject();

        model.removeAllElements();
        int count = 0;
        try {
            count = taskWrapper.getRolesCount();
        } catch(NullPointerException e) {
            return;
        }
        for(int i=0; i<count; i++) {
            String roleDomain = taskWrapper.getRolesAt(i).getRoledomainAttribute();
            model.insertElementAt(roleDomain, i);
        }
    }

    public void updateDataImpl() {
        WARNING.println("NOT IMPLEMENTED");
    }
}
