/*
 * $Id: EntityDataLoadContainer.java,v 1.2 2004/06/26 23:16:23 ajzeneski Exp $
 *
 * Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.entityext.data;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityDataLoader;
import org.ofbiz.service.ServiceDispatcher;


/**
 * Some utility routines for loading seed data.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class EntityDataLoadContainer implements Container {

    public static final String module = EntityDataLoadContainer.class.getName();
    protected int txTimeout = -1;

    public EntityDataLoadContainer() {
        super();
    }

    /**
     * @see org.ofbiz.base.container.Container#init(java.lang.String[])
     */
    public void init(String[] args) throws ContainerException {
        // disable job scheduler, JMS listener and startup services
        ServiceDispatcher.enableJM(false);
        ServiceDispatcher.enableJMS(false);
        ServiceDispatcher.enableSvcs(false);
        if (args != null && args.length > 0) {
            try {
                txTimeout = Integer.parseInt(args[0]);
            } catch (Exception e) {
            }
        }
    }

    /**
     * @see org.ofbiz.base.container.Container#start(java.lang.String)
     */
    public boolean start(String configFileLocation) throws ContainerException {
        ContainerConfig.Container cfg = ContainerConfig.getContainer("dataload-container", configFileLocation);
        ContainerConfig.Container.Property delegatorNameProp = cfg.getProperty("delegator-name");
        ContainerConfig.Container.Property entityGroupNameProp = cfg.getProperty("entity-group-name");

        String delegatorName = null;
        String entityGroupName = null;

        if (delegatorNameProp == null || delegatorNameProp.value == null || delegatorNameProp.value.length() == 0) {
            throw new ContainerException("Invalid delegator-name defined in container configuration");
        } else {
            delegatorName = delegatorNameProp.value;
        }

        if (entityGroupNameProp == null || entityGroupNameProp.value == null || entityGroupNameProp.value.length() == 0) {
            throw new ContainerException("Invalid entity-group-name defined in container configuration");
        } else {
            entityGroupName = entityGroupNameProp.value;
        }
        
        GenericDelegator delegator = GenericDelegator.getGenericDelegator(delegatorName);
        String helperName = delegator.getGroupHelperName(entityGroupName);
        //String paths = EntityDataLoader.getPathsString(helperName);
        List urlList = EntityDataLoader.getUrlList(helperName);

        NumberFormat changedFormat = NumberFormat.getIntegerInstance();
        changedFormat.setMinimumIntegerDigits(5);
        changedFormat.setGroupingUsed(false);
        
        List errorMessages = new LinkedList();
        List infoMessages = new LinkedList();
        int totalRowsChanged = 0;
        if (urlList.size() > 0) {
            Debug.logImportant("=-=-=-=-=-=-= Doing a data load with the following files:", module);
            Iterator urlIter = urlList.iterator();
            while (urlIter.hasNext()) {
                URL dataUrl = (URL) urlIter.next();
                Debug.logImportant(dataUrl.toExternalForm(), module);
            }

            Debug.logImportant("=-=-=-=-=-=-= Starting the data load...", module);

            urlIter = urlList.iterator();
            while (urlIter.hasNext()) {
                URL dataUrl = (URL) urlIter.next();
                try {
                    int rowsChanged = EntityDataLoader.loadData(dataUrl, helperName, delegator, errorMessages, txTimeout);
                    totalRowsChanged += rowsChanged;
                    infoMessages.add(changedFormat.format(rowsChanged) + " of " + changedFormat.format(totalRowsChanged) + " from " + dataUrl.toExternalForm());
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error loading data file: " + dataUrl.toExternalForm(), module);
                }
            }
        } else {
            Debug.logImportant("No XML Files found.", module);
        }

        if (infoMessages.size() > 0) {
            Debug.logImportant("=-=-=-=-=-=-= Here is a summary of the data load:", module);
            Iterator infoIter = infoMessages.iterator();
            while (infoIter.hasNext()){
              Debug.logImportant((String) infoIter.next(), module);
            }
        }
        
        if (errorMessages.size() > 0) {
            Debug.logImportant("The following errors occured in the data load:", module);
            Iterator errIter = errorMessages.iterator();
            while (errIter.hasNext()){
              Debug.logImportant((String) errIter.next(), module);
            }
        }

        Debug.logImportant("=-=-=-=-=-=-= Finished the data load with " + totalRowsChanged + " rows changed.", module);
        
        return true;
    }

    /**
     * @see org.ofbiz.base.container.Container#stop()
     */
    public void stop() throws ContainerException {
    }
}
