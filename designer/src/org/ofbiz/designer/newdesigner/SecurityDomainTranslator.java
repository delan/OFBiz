/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 4:25:45 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.networkdesign.ITask;
import org.ofbiz.designer.networkdesign.ITaskWrapper;
import org.ofbiz.designer.generic.IComboBoxModel;
import org.ofbiz.designer.generic.IComboBoxWrapper;
import org.ofbiz.designer.pattern.BaseTranslator;

import java.util.Enumeration;
import java.util.Hashtable;

class SecurityDomainTranslator extends BaseTranslator {
    Hashtable allSecurityDomains;

    public SecurityDomainTranslator(IComboBoxWrapper modelIn, ITaskWrapper taskWrapperIn, Hashtable allSecurityDomainsIn, String mode) {
        super(modelIn, taskWrapperIn);
        allSecurityDomains = allSecurityDomainsIn;
        synchronize(mode);
    }

    public void updateModelImpl() {
        IComboBoxModel model = (IComboBoxModel)getGuiModel();
        ITaskWrapper taskWrapper = (ITaskWrapper)getDataObject();

        model.removeAllElements();
        String domain = taskWrapper.getSecuritydomainurlAttribute();
        int poundIndex = domain.indexOf("#");
        domain = domain.substring(poundIndex+1, domain.length());

        Enumeration keys = allSecurityDomains.keys();
        while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            model.addElement(key);
        }
        if(model.getIndexOf(domain) < 0)
            throw new RuntimeException("invalid domain " + domain);

        model.setSelectedItem(domain);
    }

    public void updateDataImpl() {
        IComboBoxModel model = (IComboBoxModel)getGuiModel();
        ITaskWrapper taskWrapper = (ITaskWrapper)getDataObject();

        String domain = (String)model.getSelectedItem();
        String href = taskWrapper.getSecuritydomainurlAttribute();
        int poundIndex = href.indexOf("#");
        domain = href.substring(0, poundIndex+1) + domain;

        if(!domain.equals(href))
            ((ITask)taskWrapper).setSecuritydomainurlAttribute(domain);
            //taskWrapper.setSecuritydomainurlAttribute(domain);
    }
}
