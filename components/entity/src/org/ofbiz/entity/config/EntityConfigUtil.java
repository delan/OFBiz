/*
 * $Id: EntityConfigUtil.java,v 1.10 2004/06/01 06:13:24 jonesde Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entity.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.config.GenericConfigException;
import org.ofbiz.base.config.ResourceLoader;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericEntityConfException;
import org.ofbiz.entity.GenericEntityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Misc. utility method for dealing with the entityengine.xml file
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision: 1.10 $
 * @since      2.0
 */
public class EntityConfigUtil {
    
    public static final String module = EntityConfigUtil.class.getName();
    public static final String ENTITY_ENGINE_XML_FILENAME = "entityengine.xml";

    // ========== engine info fields ==========
    protected static String txFactoryClass;
    protected static String txFactoryUserTxJndiName;
    protected static String txFactoryUserTxJndiServerName;
    protected static String txFactoryTxMgrJndiName;
    protected static String txFactoryTxMgrJndiServerName;

    protected static Map resourceLoaderInfos = new HashMap();
    protected static Map delegatorInfos = new HashMap();
    protected static Map entityModelReaderInfos = new HashMap();
    protected static Map entityGroupReaderInfos = new HashMap();
    protected static Map entityEcaReaderInfos = new HashMap();
    protected static Map entityDataReaderInfos = new HashMap();
    protected static Map fieldTypeInfos = new HashMap();
    protected static Map datasourceInfos = new HashMap();

    protected static Element getXmlRootElement() throws GenericEntityConfException {
        try {
            return ResourceLoader.getXmlRootElement(EntityConfigUtil.ENTITY_ENGINE_XML_FILENAME);
        } catch (GenericConfigException e) {
            throw new GenericEntityConfException("Could not get entity engine XML root element", e);
        }
    }

    protected static Document getXmlDocument() throws GenericEntityConfException {
        try {
            return ResourceLoader.getXmlDocument(EntityConfigUtil.ENTITY_ENGINE_XML_FILENAME);
        } catch (GenericConfigException e) {
            throw new GenericEntityConfException("Could not get entity engine XML document", e);
        }
    }

    static {
        try {
            initialize(getXmlRootElement());
        } catch (Exception e) {
            Debug.logError(e, "Error loading entity config XML file " + ENTITY_ENGINE_XML_FILENAME, module);
        }
    }

    public static synchronized void reinitialize() throws GenericEntityException {
        try {
            ResourceLoader.invalidateDocument(ENTITY_ENGINE_XML_FILENAME);
            initialize(getXmlRootElement());
        } catch (Exception e) {
            throw new GenericEntityException("Error reloading entity config XML file " + ENTITY_ENGINE_XML_FILENAME, e);
        }
    }

    public static void initialize(Element rootElement) throws GenericEntityException {
        Element transactionFactoryElement = UtilXml.firstChildElement(rootElement, "transaction-factory");
        if (transactionFactoryElement == null) {
            throw new GenericEntityConfException("ERROR: no transaction-factory definition was found in " + ENTITY_ENGINE_XML_FILENAME);
        }

        EntityConfigUtil.txFactoryClass = transactionFactoryElement.getAttribute("class");

        Element userTxJndiElement = UtilXml.firstChildElement(transactionFactoryElement, "user-transaction-jndi");
        if (userTxJndiElement != null) {
            EntityConfigUtil.txFactoryUserTxJndiName = userTxJndiElement.getAttribute("jndi-name");
            EntityConfigUtil.txFactoryUserTxJndiServerName = userTxJndiElement.getAttribute("jndi-server-name");
        } else {
            EntityConfigUtil.txFactoryUserTxJndiName = null;
            EntityConfigUtil.txFactoryUserTxJndiServerName = null;
        }

        Element txMgrJndiElement = UtilXml.firstChildElement(transactionFactoryElement, "transaction-manager-jndi");
        if (txMgrJndiElement != null) {
            EntityConfigUtil.txFactoryTxMgrJndiName = txMgrJndiElement.getAttribute("jndi-name");
            EntityConfigUtil.txFactoryTxMgrJndiServerName = txMgrJndiElement.getAttribute("jndi-server-name");
        } else {
            EntityConfigUtil.txFactoryTxMgrJndiName = null;
            EntityConfigUtil.txFactoryTxMgrJndiServerName = null;
        }

        // not load all of the maps...
        List childElements = null;
        Iterator elementIter = null;

        // resource-loader - resourceLoaderInfos
        childElements = UtilXml.childElementList(rootElement, "resource-loader");
        elementIter = childElements.iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            EntityConfigUtil.ResourceLoaderInfo resourceLoaderInfo = new EntityConfigUtil.ResourceLoaderInfo(curElement);
            EntityConfigUtil.resourceLoaderInfos.put(resourceLoaderInfo.name, resourceLoaderInfo);
        }

        // delegator - delegatorInfos
        childElements = UtilXml.childElementList(rootElement, "delegator");
        elementIter = childElements.iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            EntityConfigUtil.DelegatorInfo delegatorInfo = new EntityConfigUtil.DelegatorInfo(curElement);
            EntityConfigUtil.delegatorInfos.put(delegatorInfo.name, delegatorInfo);
        }

        // entity-model-reader - entityModelReaderInfos
        childElements = UtilXml.childElementList(rootElement, "entity-model-reader");
        elementIter = childElements.iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            EntityConfigUtil.EntityModelReaderInfo entityModelReaderInfo = new EntityConfigUtil.EntityModelReaderInfo(curElement);
            EntityConfigUtil.entityModelReaderInfos.put(entityModelReaderInfo.name, entityModelReaderInfo);
        }

        // entity-group-reader - entityGroupReaderInfos
        childElements = UtilXml.childElementList(rootElement, "entity-group-reader");
        elementIter = childElements.iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            EntityConfigUtil.EntityGroupReaderInfo entityGroupReaderInfo = new EntityConfigUtil.EntityGroupReaderInfo(curElement);
            EntityConfigUtil.entityGroupReaderInfos.put(entityGroupReaderInfo.name, entityGroupReaderInfo);
        }

        // entity-eca-reader - entityEcaReaderInfos
        childElements = UtilXml.childElementList(rootElement, "entity-eca-reader");
        elementIter = childElements.iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            EntityConfigUtil.EntityEcaReaderInfo entityEcaReaderInfo = new EntityConfigUtil.EntityEcaReaderInfo(curElement);
            EntityConfigUtil.entityEcaReaderInfos.put(entityEcaReaderInfo.name, entityEcaReaderInfo);
        }

        // entity-data-reader - entityDataReaderInfos
        childElements = UtilXml.childElementList(rootElement, "entity-data-reader");
        elementIter = childElements.iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            EntityConfigUtil.EntityDataReaderInfo entityDataReaderInfo = new EntityConfigUtil.EntityDataReaderInfo(curElement);
            EntityConfigUtil.entityDataReaderInfos.put(entityDataReaderInfo.name, entityDataReaderInfo);
        }

        // field-type - fieldTypeInfos
        childElements = UtilXml.childElementList(rootElement, "field-type");
        elementIter = childElements.iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            EntityConfigUtil.FieldTypeInfo fieldTypeInfo = new EntityConfigUtil.FieldTypeInfo(curElement);
            EntityConfigUtil.fieldTypeInfos.put(fieldTypeInfo.name, fieldTypeInfo);
        }

        // datasource - datasourceInfos
        childElements = UtilXml.childElementList(rootElement, "datasource");
        elementIter = childElements.iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            EntityConfigUtil.DatasourceInfo datasourceInfo = new EntityConfigUtil.DatasourceInfo(curElement);
            EntityConfigUtil.datasourceInfos.put(datasourceInfo.name, datasourceInfo);
        }
    }

    public static String getTxFactoryClass() {
        return txFactoryClass;
    }

    public static String getTxFactoryUserTxJndiName() {
        return txFactoryUserTxJndiName;
    }

    public static String getTxFactoryUserTxJndiServerName() {
        return txFactoryUserTxJndiServerName;
    }

    public static String getTxFactoryTxMgrJndiName() {
        return txFactoryTxMgrJndiName;
    }

    public static String getTxFactoryTxMgrJndiServerName() {
        return txFactoryTxMgrJndiServerName;
    }

    public static EntityConfigUtil.ResourceLoaderInfo getResourceLoaderInfo(String name) {
        return (EntityConfigUtil.ResourceLoaderInfo) resourceLoaderInfos.get(name);
    }

    public static EntityConfigUtil.DelegatorInfo getDelegatorInfo(String name) {
        return (EntityConfigUtil.DelegatorInfo) delegatorInfos.get(name);
    }

    public static EntityConfigUtil.EntityModelReaderInfo getEntityModelReaderInfo(String name) {
        return (EntityConfigUtil.EntityModelReaderInfo) entityModelReaderInfos.get(name);
    }

    public static EntityConfigUtil.EntityGroupReaderInfo getEntityGroupReaderInfo(String name) {
        return (EntityConfigUtil.EntityGroupReaderInfo) entityGroupReaderInfos.get(name);
    }

    public static EntityConfigUtil.EntityEcaReaderInfo getEntityEcaReaderInfo(String name) {
        return (EntityConfigUtil.EntityEcaReaderInfo) entityEcaReaderInfos.get(name);
    }

    public static EntityConfigUtil.EntityDataReaderInfo getEntityDataReaderInfo(String name) {
        return (EntityConfigUtil.EntityDataReaderInfo) entityDataReaderInfos.get(name);
    }

    public static EntityConfigUtil.FieldTypeInfo getFieldTypeInfo(String name) {
        return (EntityConfigUtil.FieldTypeInfo) fieldTypeInfos.get(name);
    }

    public static EntityConfigUtil.DatasourceInfo getDatasourceInfo(String name) {
        return (EntityConfigUtil.DatasourceInfo) datasourceInfos.get(name);
    }
    
    public static Map getDatasourceInfos() {
        return datasourceInfos;
    }

    public static class ResourceLoaderInfo {
        public String name;
        public String className;
        public String prependEnv;
        public String prefix;

        public ResourceLoaderInfo(Element element) {
            this.name = element.getAttribute("name");
            this.className = element.getAttribute("class");
            this.prependEnv = element.getAttribute("prepend-env");
            this.prefix = element.getAttribute("prefix");
        }
    }


    public static class DelegatorInfo {
        public String name;
        public String entityModelReader;
        public String entityGroupReader;
        public String entityEcaReader;
        public boolean useDistributedCacheClear;
        public String distributedCacheClearClassName;
        public String distributedCacheClearUserLoginId;
        public Map groupMap = new HashMap();

        public DelegatorInfo(Element element) {
            this.name = element.getAttribute("name");
            this.entityModelReader = element.getAttribute("entity-model-reader");
            this.entityGroupReader = element.getAttribute("entity-group-reader");
            this.entityEcaReader = element.getAttribute("entity-eca-reader");
            // this defaults to false, ie anything but true is false
            this.useDistributedCacheClear = "true".equals(element.getAttribute("distributed-cache-clear-enabled"));
            this.distributedCacheClearClassName = element.getAttribute("distributed-cache-clear-class-name");
            if (UtilValidate.isEmpty(this.distributedCacheClearClassName)) this.distributedCacheClearClassName = "org.ofbiz.entityext.cache.EntityCacheServices";
            
            this.distributedCacheClearUserLoginId = element.getAttribute("distributed-cache-clear-user-login-id");
            if (UtilValidate.isEmpty(this.distributedCacheClearUserLoginId)) this.distributedCacheClearUserLoginId= "admin";

            List groupMapList = UtilXml.childElementList(element, "group-map");
            Iterator groupMapIter = groupMapList.iterator();

            while (groupMapIter.hasNext()) {
                Element groupMapElement = (Element) groupMapIter.next();

                groupMap.put(groupMapElement.getAttribute("group-name"), groupMapElement.getAttribute("datasource-name"));
            }
        }
    }


    public static class EntityModelReaderInfo {
        public String name;
        public List resourceElements;

        public EntityModelReaderInfo(Element element) {
            this.name = element.getAttribute("name");
            resourceElements = UtilXml.childElementList(element, "resource");
        }
    }

    public static class EntityGroupReaderInfo {
        public String name;
        public List resourceElements;

        public EntityGroupReaderInfo(Element element) {
            this.name = element.getAttribute("name");
            String loader = element.getAttribute("loader");
            String location = element.getAttribute("location");
            
            resourceElements = new LinkedList();
            if (loader != null && loader.length() > 0 && location != null && location.length() > 0) {            
                resourceElements.add(element);
            }
            resourceElements.addAll(UtilXml.childElementList(element, "resource"));
        }
    }

    public static class EntityEcaReaderInfo {
        public String name;
        public List resourceElements;

        public EntityEcaReaderInfo(Element element) {
            this.name = element.getAttribute("name");
            resourceElements = UtilXml.childElementList(element, "resource");
        }
    }

    public static class EntityDataReaderInfo {
        public String name;
        public List resourceElements;

        public EntityDataReaderInfo(Element element) {
            this.name = element.getAttribute("name");
            resourceElements = UtilXml.childElementList(element, "resource");
        }
    }


    public static class FieldTypeInfo {
        public String name;
        public Element resourceElement;

        public FieldTypeInfo(Element element) {
            this.name = element.getAttribute("name");
            resourceElement = element;
        }
    }


    public static class DatasourceInfo {
        public String name;
        public String helperClass;
        public String fieldTypeName;
        public List sqlLoadPaths = new LinkedList();
        public List readDatas = new LinkedList();
        public Element datasourceElement;
        
        public static final int TYPE_JNDI_JDBC = 1;        
        public static final int TYPE_INLINE_JDBC = 2;
        public static final int TYPE_TYREX_DATA_SOURCE = 3;
        public static final int TYPE_OTHER = 4;
                
        public Element jndiJdbcElement;
        public Element tyrexDataSourceElement;
        public Element inlineJdbcElement;

        public String schemaName = null;
        public boolean useSchemas = true;
        public boolean checkOnStart = true;
        public boolean addMissingOnStart = false;
        public boolean useFks = true;
        public boolean useFkIndices = true;
        public boolean checkForeignKeysOnStart = false;
        public boolean checkFkIndicesOnStart = false;
        public boolean usePkConstraintNames = true;
        public int constraintNameClipLength = 30;
        public boolean useProxyCursor = false;
        public String cursorName = "p_cursor";
        public int resultFetchSize = -1;
        public String fkStyle = null;
        public boolean useFkInitiallyDeferred = true;
        public boolean useIndices = true;
        public boolean checkIndicesOnStart = false;
        public String joinStyle = null;
        public boolean aliasViews = true;
        public boolean alwaysUseConstraintKeyword = false;

        public DatasourceInfo(Element element) {
            this.name = element.getAttribute("name");
            this.helperClass = element.getAttribute("helper-class");
            this.fieldTypeName = element.getAttribute("field-type-name");

            sqlLoadPaths = UtilXml.childElementList(element, "sql-load-path");
            readDatas = UtilXml.childElementList(element, "read-data");
            datasourceElement = element;

            if (datasourceElement == null) {
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for schema-name (none)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for use-schemas (true)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for check-on-start (true)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for add-missing-on-start (false)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for use-foreign-keys (true)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default use-foreign-key-indices (true)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for check-fks-on-start (false)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for check-fk-indices-on-start (false)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for use-pk-constraint-names (true)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for constraint-name-clip-length (30)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for fk-style (name_constraint)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for use-fk-initially-deferred (true)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for use-indices (true)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for check-indices-on-start (false)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for join-style (ansi)", module);
                Debug.logWarning("datasource def not found with name " + this.name + ", using default for always-use-constraint-keyword (false)", module);
            } else {
                schemaName = datasourceElement.getAttribute("schema-name");
                // anything but false is true
                useSchemas = !"false".equals(datasourceElement.getAttribute("use-schemas"));
                // anything but false is true
                checkOnStart = !"false".equals(datasourceElement.getAttribute("check-on-start"));
                // anything but true is false
                addMissingOnStart = "true".equals(datasourceElement.getAttribute("add-missing-on-start"));
                // anything but false is true
                useFks = !"false".equals(datasourceElement.getAttribute("use-foreign-keys"));
                // anything but false is true
                useFkIndices = !"false".equals(datasourceElement.getAttribute("use-foreign-key-indices"));
                // anything but true is false
                checkForeignKeysOnStart = "true".equals(datasourceElement.getAttribute("check-fks-on-start"));
                // anything but true is false
                checkFkIndicesOnStart = "true".equals(datasourceElement.getAttribute("check-fk-indices-on-start"));
                // anything but false is true
                usePkConstraintNames = !"false".equals(datasourceElement.getAttribute("use-pk-constraint-names"));
                try {
                    constraintNameClipLength = Integer.parseInt(datasourceElement.getAttribute("constraint-name-clip-length"));
                } catch (Exception e) {
                    Debug.logError("Could not parse constraint-name-clip-length value for datasource with name " + this.name + ", using default value of 30", module);
                }
                useProxyCursor = "true".equalsIgnoreCase(datasourceElement.getAttribute("use-proxy-cursor"));
                cursorName = datasourceElement.getAttribute("proxy-cursor-name");
                try {
                    resultFetchSize = Integer.parseInt(datasourceElement.getAttribute("result-fetch-size"));
                } catch (Exception e) {
                    Debug.logWarning("Could not parse result-fetch-size value for datasource with name " + this.name + ", using JDBC driver default value", module);
                }
                fkStyle = datasourceElement.getAttribute("fk-style");
                // anything but true is false
                useFkInitiallyDeferred = "true".equals(datasourceElement.getAttribute("use-fk-initially-deferred"));
                // anything but false is true
                useIndices = !"false".equals(datasourceElement.getAttribute("use-indices"));
                // anything but true is false
                checkIndicesOnStart = "true".equals(datasourceElement.getAttribute("check-indices-on-start"));
                joinStyle = datasourceElement.getAttribute("join-style");
                aliasViews = !"false".equals(datasourceElement.getAttribute("alias-view-columns"));
                // anything but true is false
                alwaysUseConstraintKeyword = "true".equals(datasourceElement.getAttribute("always-use-constraint-keyword"));
            }
            if (fkStyle == null || fkStyle.length() == 0) fkStyle = "name_constraint";
            if (joinStyle == null || joinStyle.length() == 0) joinStyle = "ansi";

            jndiJdbcElement = UtilXml.firstChildElement(datasourceElement, "jndi-jdbc");
            tyrexDataSourceElement = UtilXml.firstChildElement(datasourceElement, "tyrex-dataSource");
            inlineJdbcElement = UtilXml.firstChildElement(datasourceElement, "inline-jdbc");
        }
    }
}
