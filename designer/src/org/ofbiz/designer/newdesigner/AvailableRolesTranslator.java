/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 4:24:52 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.pattern.BaseTranslator;
import org.ofbiz.designer.pattern.XmlWrapper;
import org.ofbiz.designer.generic.IListWrapper;
import org.ofbiz.designer.generic.IListModel;
import org.ofbiz.designer.networkdesign.IRolesWrapper;
import org.ofbiz.designer.networkdesign.IRoles;
import org.ofbiz.designer.util.WARNING;

import java.util.Hashtable;
import java.util.HashSet;

class AvailableRolesTranslator extends BaseTranslator {
    Hashtable allRoledomainXmls;

    public AvailableRolesTranslator(IListWrapper modelIn, IRolesWrapper wrapperIn, Hashtable allRoledomainXmlsIn, String mode) {
        super(modelIn, wrapperIn);
        allRoledomainXmls = allRoledomainXmlsIn;
        synchronize(mode);
    }

    public void updateModelImpl() {
        IListModel model = (IListModel)getGuiModel();

        while(model.getSize() > 0)
            model.remove(0);

        IRoles wrapper = (IRoles)getDataObject();
        String href = wrapper.getRoledomainAttribute();
        XmlWrapper roledomainXml = (XmlWrapper)allRoledomainXmls.get(href);
        org.ofbiz.designer.roledomain.IRoleDomainWrapper roleDomain = (org.ofbiz.designer.roledomain.IRoleDomainWrapper)roledomainXml.getRoot();

        HashSet currentRoles = new HashSet();
        for(int i = 0; i < wrapper.getRoleCount(); i++) {
            String roleHref = wrapper.getRoleAt(i).getUrlAttribute();
            int index = roleHref.indexOf("#");
            String name = roleHref.substring(index+1, roleHref.length());
            currentRoles.add(name);
        }

        int index = 0;
        for(int i = 0; i < roleDomain.getRoleCount(); i++) {
            String roleName = roleDomain.getRoleAt(i).getName();
            if(!currentRoles.contains(roleName)) {
                model.insertElementAt(roleName, index++);
            }
        }
    }

    public void updateDataImpl() {
        IListModel model = (IListModel)getGuiModel();
        WARNING.println("NOT IMPLEMENTED");
    }
}
