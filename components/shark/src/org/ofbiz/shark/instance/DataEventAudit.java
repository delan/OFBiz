/*
 * $Id: DataEventAudit.java,v 1.1 2004/04/22 15:41:01 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.shark.instance;

import org.enhydra.shark.api.internal.instancepersistence.*;
import org.enhydra.shark.SharkUtilities;
import org.enhydra.shark.SharkEngineManager;
import org.xml.sax.SAXException;

import java.util.Map;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;

/**
 * Persistance Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class DataEventAudit extends EventAudit implements DataEventAuditPersistenceInterface {

    public static final String module = AssignmentEventAudit.class.getName();
    protected GenericValue dataEventAudit = null;
    private boolean newValue = false;

    protected DataEventAudit() {}

    public DataEventAudit(GenericDelegator delegator, String eventAuditId) {
        super(delegator, eventAuditId);
        if (this.delegator != null) {
            try {
                this.dataEventAudit = delegator.findByPrimaryKey("WfDataEventAudit", UtilMisc.toMap("eventAuditId", eventAuditId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    public DataEventAudit(GenericDelegator delegator) {
        super(delegator);
        this.newValue = true;
        this.dataEventAudit = delegator.makeValue("WfDataEventAudit", UtilMisc.toMap("eventAuditId", this.eventAuditId));
    }

    public DataEventAudit(GenericValue dataEventAudit) {
        super(dataEventAudit.getDelegator(), dataEventAudit.getString("eventAuditId"));
        this.dataEventAudit = dataEventAudit;
    }

    public void setOldData(Map od) {
        byte[] value = serialize(od);
        dataEventAudit.setBytes("oldData", (value != null ? value : null));
    }

    public Map getOldData() {
        byte[] value = dataEventAudit.getBytes("oldData");
        if (value != null) {
            return deserialize(value);
        }
        return null;
    }

    public void setNewData(Map nd) {
        byte[] value = serialize(nd);
        dataEventAudit.setBytes("newData", (value != null ? value : null));
    }

    public Map getNewData() {
        byte[] value = dataEventAudit.getBytes("newData");
        if (value != null) {
            return deserialize(value);
        }
        return null;
    }

    public void store() throws GenericEntityException {
        super.store();
        if (newValue) {
            newValue = false;
            delegator.createOrStore(dataEventAudit);
        } else {
            delegator.store(dataEventAudit);
        }
    }

    public void reload() throws GenericEntityException {
        super.reload();
        if (!newValue) {
            dataEventAudit.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        super.remove();
        if (!newValue) {
            delegator.removeValue(dataEventAudit);
        }
    }

    private Map deserialize(byte[] bytes) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        Map map = null;

        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            map = (Map) ois.readObject();
        } catch (IOException e) {
            Debug.logError(e, module);
        } catch (ClassCastException e) {
            Debug.logError(e, module);
        } catch (ClassNotFoundException e) {
            Debug.logError(e, module);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
        }
        return map;
    }

    private byte[] serialize(Map map) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        byte[] bytes = null;

        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(map);
            oos.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            Debug.logError(e, module);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
        }
        return bytes;
    }
}
